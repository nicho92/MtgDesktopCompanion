package org.magic.services.network;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;


public class CloudFlareAuthorizer {

    private Logger logger = MTGLogger.getLogger(CloudFlareAuthorizer.class);

    private MTGHttpClient httpClient;
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

    
    public CloudFlareAuthorizer() {
    	httpClient = URLTools.newClient();
	}
    
    public String getAuthorizationResult(String url) throws IOException, ScriptException {

        URL cloudFlareUrl = new URL(url);
        Response response =null;
        try {

            int retries = 5;
            int timer = 4500;
            response = getResponse(url,url);

            while (response.httpStatus == HttpStatus.SC_SERVICE_UNAVAILABLE && retries-- > 0) {

                logger.debug("CloudFlare response HTML:");
                logger.debug(response.responseText);

                String answer = getJsAnswer(cloudFlareUrl,response.responseText);
                String jschlVc = new PatternStreamer(jsChallenge).results(response.responseText).findFirst().orElse("");
                String pass =  new PatternStreamer(password).results(response.responseText).findFirst().orElse("");

                String authUrl = String.format("https://%s/cdn-cgi/l/chk_jschl?jschl_vc=%s&pass=%s&jschl_answer=%s",
                        cloudFlareUrl.getHost(),
                        URLEncoder.encode(jschlVc,"UTF-8"),
                        URLEncoder.encode(pass,"UTF-8"),
                        answer);

                logger.debug(String.format("CloudFlare auth URL: %s",authUrl));

                Thread.sleep(timer);
                response = getResponse(authUrl, url);
            }

            if (response.httpStatus != HttpStatus.SC_OK) {
                if(response.httpStatus == HttpStatus.SC_FORBIDDEN && response.responseText.contains("cf-captcha-container")){
                    logger.warn("Getting CAPTCHA request from bittrex, throttling retries");
                    Thread.sleep(15000);
                }
                logger.trace("Failure HTML: {}",response.responseText);
                return response.responseText;
            }

        }catch(InterruptedException ie){
        	Thread.currentThread().interrupt();
            logger.error("Interrupted whilst waiting to perform CloudFlare authorization",ie);
            return "interrupted";
        }

        Optional<Cookie> cfClearanceCookie = httpClient.getCookies()
                .stream()
                .filter(cookie -> cookie.getName().equals("cf_clearance"))
                .findFirst();

        if(cfClearanceCookie.isPresent()) {
            logger.debug("Cloudflare DDos authorization success, cf_clearance: {}",cfClearanceCookie.get().getValue());
        }else{
            logger.debug("Cloudflare DDoS is not currently active");
        }

        return response.responseText;
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
        new Timer(true).schedule(task, hardTimeout * 1000L);

        HttpResponse httpResponse = httpClient.execute(getRequest);

        String responseText = PatternStreamer.convertStreamToString(httpResponse.getEntity().getContent());
        int httpStatus = httpResponse.getStatusLine().getStatusCode();
        logger.trace(httpResponse.getStatusLine());
        task.cancel();
        httpResponse.getEntity().getContent().close();
        ((CloseableHttpResponse)httpResponse).close();
        return new Response(httpStatus,responseText);
    }
    private String getJsAnswer(URL url, String responseHtml) throws ScriptException{

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

            logger.debug("CloudFlare JS challenge code: {}", jsCode);
            return new BigDecimal(engine.eval(jsCode).toString()).setScale(10, RoundingMode.HALF_UP).toString();
        }
        throw new IllegalStateException("BUG: could not find initial CF JS challenge code in: "+responseHtml);
    }
}

final class PatternStreamer {
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
    
    public static String convertStreamToString(InputStream is) {
        try(var s = new java.util.Scanner(is).useDelimiter("\\A"))
        {
        	return s.hasNext() ? s.next() : "";
        }
        		
    }
    
}
