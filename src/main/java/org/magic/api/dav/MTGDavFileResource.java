package org.magic.api.dav;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.log4j.Logger;
import org.magic.api.fs.MTGFileSystem;
import org.magic.api.fs.MTGPath;
import org.magic.servers.impl.WebDAVServer;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;
import org.magic.tools.URLTools;

import io.milton.http.Auth;
import io.milton.http.FileItem;
import io.milton.http.Range;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.http.http11.auth.DigestGenerator;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.CollectionResource;
import io.milton.resource.DigestResource;
import io.milton.resource.FileResource;

public class MTGDavFileResource implements FileResource, DigestResource
{
	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected MTGPath mtgpath;
	protected MTGFileSystem fs;
	protected boolean root;
	protected String user;
	protected String pass;
	
	public MTGDavFileResource(MTGPath path, MTGFileSystem fs, boolean root) {
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
		return null;
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException, NotAuthorizedException, BadRequestException, NotFoundException {
		out.write(fs.getSerializer().toJsonElement(new String(Files.readAllBytes(mtgpath),MTGConstants.DEFAULT_ENCODING)).toString().getBytes());
	}

	
	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return null;
	}

	@Override
	public String getContentType(String accepts) {
		return URLTools.HEADER_JSON;
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
			throw new BadRequestException(this);
		}
	}
	
	@Override
	public void copyTo(CollectionResource toCollection, String name)throws NotAuthorizedException, BadRequestException, ConflictException {
		logger.debug("copyTo " + toCollection + " " + name);
		
	}

	@Override
	public void moveTo(CollectionResource toCollection, String name)throws ConflictException, NotAuthorizedException, BadRequestException {
		logger.debug("moveTo " + toCollection + " " + name);
		
		
	}

	@Override
	public String processForm(Map<String, String> parameters, Map<String, FileItem> files) throws BadRequestException, NotAuthorizedException, ConflictException {
		logger.debug("processForm " + parameters + " " + files);
		
		return null;
	}

}
