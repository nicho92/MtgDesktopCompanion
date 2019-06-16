package org.magic.api.dav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.magic.api.fs.MTGFileSystem;
import org.magic.api.fs.MTGPath;
import org.magic.servers.impl.WebDAVServer;
import org.magic.services.MTGLogger;

import io.milton.http.Auth;
import io.milton.http.Range;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.XmlWriter;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.http.http11.auth.DigestGenerator;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.CollectionResource;
import io.milton.resource.DigestResource;
import io.milton.resource.FolderResource;
import io.milton.resource.Resource;

public class MTGDavFolderResource implements FolderResource, DigestResource
{
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected MTGPath mtgpath;
	protected MTGFileSystem fs;
	protected boolean root;
	protected String user;
	protected String pass;
	private List<Resource> children;
	
	public MTGDavFolderResource(MTGPath path, MTGFileSystem fs, boolean root) {
		this.mtgpath=path;
		this.fs=fs;
		this.root=root;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public Object authenticate(DigestResponse digestRequest) {
		if (digestRequest.getUser().equals(WebDAVServer.LOG)) {
            DigestGenerator gen = new DigestGenerator();
            String actual = gen.generateDigest(digestRequest, WebDAVServer.PAS);
            if (actual.equals(digestRequest.getResponseDigest())) {
                return digestRequest.getUser();
            } else {
            	logger.warn("that password is incorrect");
            }
        } else {
        	logger.warn("user not found: " + digestRequest.getUser());
        }
        return null;
	}

	@Override
	public boolean isDigestAllowed() {
		return true;
	}

	@Override
	public String getUniqueId() {
		return Md5Crypt.md5Crypt(mtgpath.toString().getBytes());
	}

	@Override
	public String getName() {
		return mtgpath.getStringFileName();
	}

	@Override
	public Object authenticate(String user, String passw) {
		if( user.equals(WebDAVServer.LOG) && passw.equals(WebDAVServer.PAS)) {
	            return user;
	    }
	    return null;
	}

	@Override
	public boolean authorise(Request request, Method method, Auth auth) {
		return auth != null;
	}

	@Override
	public String getRealm() {
		return "mtg";
	}

	@Override
	public Date getModifiedDate() {
		return null;
	}

	@Override
	public String checkRedirect(Request request) throws NotAuthorizedException, BadRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource child(String childName) throws NotAuthorizedException, BadRequestException {
		if(children==null)
			return null;
		
		
		Optional<? extends Resource>  opt = getChildren().stream().filter(r->r.getName().equals(childName)).findFirst();
		
		if(opt.isPresent())
			return opt.get();
		
		return null;
		
	}

	@Override
	public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
		if(children==null)
		{
				children = new ArrayList<>();
				
				if(root)
				{
					fs.getRootDirectories().forEach(p->children.add(new MTGDavFolderResource((MTGPath)p, fs, false)));
					return children;
				}
				else
				{
					try (Stream<Path> s = Files.list(mtgpath))
					{
						s.forEach(p->{
							if(((MTGPath)p).isCard())
								children.add(new MTGDavFileResource((MTGPath)p, fs, false));
							else
								children.add(new MTGDavFolderResource((MTGPath)p, fs, false));
						});
					} catch (IOException e) {
						logger.error(e);
					}
						
				}
		}
		return children;
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
       
		
		XmlWriter w = new XmlWriter(out);
	        w.open("html");
	        w.open("body");
	       	w.begin("h1").open().writeText(this.getName()).close();
	        w.open("table");
	        
	        if(!root)
	        	w.begin("tr").open().begin("a").writeAtt("href", "../").open().writeText("../").close();
	
	        for (Resource r : getChildren()) {
	            w.open("tr");
	            	w.open("td");
			            if(root)
			            {
			            	w.begin("a").writeAtt("href", "/"+ r.getName()).open().writeText(r.getName()).close();	
			            }
			            else 
			            {
			           		w.begin("a").writeAtt("href", this.getName() +"/"+ r.getName()).open().writeText(r.getName()).close();
			            }
	            w.close("td");
	            w.close("tr");
	        }
	        w.close("table");
	        w.close("body");
	        w.close("html");
        w.flush();
		
	}

	
	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return null;
	}

	@Override
	public String getContentType(String accepts) {
		return "text/html";
	}

	@Override
	public Long getContentLength() {
		return null;
	}
	
	@Override
	public Date getCreateDate() {
		return new Date(mtgpath.readAttributes().creationTime().toMillis());
	}

	@Override
	public void delete() throws NotAuthorizedException, ConflictException, BadRequestException {
		try {
			Files.deleteIfExists(mtgpath);
		} catch (IOException e) {
			Log.error("error delete()",e);
			throw new BadRequestException(this);
		}
	}

	@Override
	public Resource createNew(String newName, InputStream inputStream, Long length, String contentType)throws IOException, ConflictException, NotAuthorizedException, BadRequestException {
		logger.debug("create new " + newName + " " + length + " " + contentType);
		return null;
	}

	@Override
	public CollectionResource createCollection(String newName)throws NotAuthorizedException, ConflictException, BadRequestException {
		logger.debug("create new " + newName);
		
		return null;
	}

	
	@Override
	public void copyTo(CollectionResource toCollection, String name)throws NotAuthorizedException, BadRequestException, ConflictException {
		logger.debug("copyTo " + toCollection + " " + name);
		
	}

	@Override
	public void moveTo(CollectionResource toCollection, String name)throws ConflictException, NotAuthorizedException, BadRequestException {
		logger.debug("moveTo " + toCollection + " " + name);
		
		
	}

}
