package org.beta;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.log4j.Logger;
import org.magic.services.MTGLogger;
import org.magic.tools.URLTools;
import org.magic.tools.URLToolsClient;

public class CloudFlareParser {

	protected Logger log = MTGLogger.getLogger(this.getClass());
    private URLToolsClient client;
    private Pattern jsChallenge = Pattern.compile("name=\"jschl_vc\" value=\"(.+?)\"");
    private Pattern password = Pattern.compile("name=\"pass\" value=\"(.+?)\"");
    private Pattern jsScript = Pattern.compile("setTimeout\\(function\\(\\)\\{\\s+(var s,t,o,p,b,r,e,a,k,i,n,g,f.+?\\r?\\n[\\s\\S]+?a\\.value =.+?)\\r?\\n");
    private ScriptEngineManager engineManager = new ScriptEngineManager();
    private ScriptEngine engine = engineManager.getEngineByName("nashorn");
    private static class Response{
        private int httpStatus;
        private String responseText;

        Response(int httpStatus, String responseText) {
            this.httpStatus = httpStatus;
            this.responseText = responseText;
        }
    }

    
    
    
    public static void main(String[] args) throws IOException, ScriptException {
		
    	CloudFlareParser parse = new CloudFlareParser(URLTools.newClient());
    	parse.getAuthorizationResult("https://deckmaster.info/card.php?multiverseid=456360");
    	
	}
    
    
    public CloudFlareParser(URLToolsClient client) {
        this.client = client;
    }

    public boolean getAuthorizationResult(String url) throws IOException, ScriptException {

        URL cloudFlareUrl = new URL(url);

        try {

            int retries = 5;
            int timer = 4500;
            Response response = getResponse(url,url);

            while (response.httpStatus == HttpStatus.SC_SERVICE_UNAVAILABLE && retries-- > 0) {
                log.trace(response.responseText);
                log.debug(retries);

                String answer = getJsAnswer(cloudFlareUrl,response.responseText);
                String jschlvc = new PatternStreamer(jsChallenge).results(response.responseText).findFirst().orElse("");
                String pass =  new PatternStreamer(password).results(response.responseText).findFirst().orElse("");

                String authUrl = String.format("https://%s/cdn-cgi/l/chk_jschl?jschl_vc=%s&pass=%s&jschl_answer=%s",
                        cloudFlareUrl.getHost(),
                        URLEncoder.encode(jschlvc,"UTF-8"),
                        URLEncoder.encode(pass,"UTF-8"),
                        answer);

                log.debug(String.format("CloudFlare auth URL: %s",authUrl));

                Thread.sleep(timer);
                response = getResponse(authUrl, url);
            }

            if (response.httpStatus != HttpStatus.SC_OK) {
                if(response.httpStatus == HttpStatus.SC_FORBIDDEN && response.responseText.contains("cf-captcha-container")){
                    log.warn("Getting CAPTCHA request from bittrex, throttling retries");
                    Thread.sleep(15000);
                }
                log.error("Failure HTML: " + response.httpStatus);
                return false;
            }

        }catch(InterruptedException ie){
            log.error("Interrupted whilst waiting to perform CloudFlare authorization",ie);
            Thread.currentThread().interrupt();
            return false;
        }

        Optional<Cookie> cfClearanceCookie = client.getCookies()
                .stream()
                .filter(cookie -> cookie.getName().equals("cf_clearance"))
                .findFirst();

        if(cfClearanceCookie.isPresent()) {
            log.info("Cloudflare DDos authorization success, cf_clearance: {}"+ cfClearanceCookie.get().getValue());
        }else{
            log.info("Cloudflare DDoS is not currently active");
        }

        return true;
    }

    private Response getResponse(String url, String referer) throws IOException {

        HttpGet getRequest = new HttpGet(url);

        if(referer != null)
            getRequest.setHeader(HttpHeaders.REFERER,referer);

        int hardTimeout = 30; // seconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                getRequest.abort();
            }
        };
        new Timer(true).schedule(task, hardTimeout * 1000l);

        HttpResponse httpResponse = client.execute(getRequest);

        String responseText = client.extractAndClose(httpResponse);
        int httpStatus = httpResponse.getStatusLine().getStatusCode();

        task.cancel();
        httpResponse.getEntity().getContent().close();
        ((CloseableHttpResponse)httpResponse).close();
        return new Response(httpStatus,responseText);
    }
   
	private String getJsAnswer(URL url, String responseHtml) throws ScriptException {

        //Credit to Anarov to the improved Regex JS parsing here from https://github.com/Anorov/cloudflare-scrape

        Matcher result = jsScript.matcher(responseHtml);

        if(result.find()){
            String jsCode = result.group(1);
            jsCode = jsCode.replaceAll("a\\.value = (.+ \\+ t\\.length).+","$1");
            jsCode = jsCode.replaceAll("\\s{3,}[a-z](?: = |\\.).+","").replace("t.length",String.valueOf(url.getHost().length()));
            jsCode = jsCode.replaceAll("[\\n\\\\']","");

            if(!jsCode.contains("toFixed")){
                throw new IllegalStateException("BUG: could not find toFixed inside CF JS challenge code");
            }

            log.debug(String.format("CloudFlare JS challenge code: %s", jsCode));
            return new BigDecimal(engine.eval(jsCode).toString()).setScale(10, RoundingMode.HALF_UP).toString();
        }
        throw new IllegalStateException("BUG: could not find initial CF JS challenge code");
    }
}

class PatternStreamer {
    private final Pattern pattern;
    public PatternStreamer(String regex) {
        this.pattern = Pattern.compile(regex);
    }
    public PatternStreamer(Pattern regex){
        this.pattern=regex;
    }
    public Stream<String> results(CharSequence input) {
        List<String> list = new ArrayList<>();
        for (Matcher m = this.pattern.matcher(input); m.find(); )
            for(int idx = 1; idx<=m.groupCount(); ++idx){
                list.add(m.group(idx));
            }
        return list.stream();
    }
}
