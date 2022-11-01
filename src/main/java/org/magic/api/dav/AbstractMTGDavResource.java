package org.magic.api.dav;

import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.magic.services.logging.MTGLogger;
import org.magic.services.tools.IDGenerator;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.http11.auth.DigestGenerator;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.DigestResource;

public class AbstractMTGDavResource implements DigestResource  {


	protected Logger logger = MTGLogger.getLogger(this.getClass());
	protected MTGPath mtgpath;
	protected MTGFileSystem fs;
	protected String user;
	protected String pass;


	public AbstractMTGDavResource(MTGPath path, MTGFileSystem fs, String log, String pass) {
		this.mtgpath=path;
		this.fs=fs;
		this.user=log;
		this.pass=pass;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Object authenticate(DigestResponse digestRequest) {
		if (digestRequest.getUser().equals(user)) {
			var gen = new DigestGenerator();
            String actual = gen.generateDigest(digestRequest, pass);
            if (actual.equals(digestRequest.getResponseDigest())) {
                return digestRequest.getUser();
            } else {
            	logger.warn("that password is incorrect");
            }
        } else {
        	logger.warn("user not found: {}",digestRequest.getUser());
        }
        return null;
	}

	@Override
	public boolean isDigestAllowed() {
		return true;
	}

	@Override
	public String getUniqueId() {
		return IDGenerator.generateMD5(mtgpath.toString());
	}

	@Override
	public String getName() {
		return mtgpath.getStringFileName();
	}

	@Override
	public Object authenticate(String u, String passw) {
		if( user.equals(u) && passw.equals(pass)) {
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
}
