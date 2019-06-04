package org.magic.api.dav;

import java.nio.file.FileSystem;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.magic.api.fs.MTGPath;
import org.magic.services.MTGLogger;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.http11.auth.DigestGenerator;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.CollectionResource;
import io.milton.resource.CopyableResource;
import io.milton.resource.DigestResource;
import io.milton.resource.MakeCollectionableResource;
import io.milton.resource.MoveableResource;
import io.milton.resource.Resource;

public class MTGDavResource implements Resource, MoveableResource, CopyableResource, DigestResource, MakeCollectionableResource {
	protected Logger log = MTGLogger.getLogger(this.getClass());

	
	public MTGDavResource(MTGPath path, FileSystem fs, boolean root) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Object authenticate(DigestResponse digestRequest) {
		if (digestRequest.getUser().equals("user")) {
            DigestGenerator gen = new DigestGenerator();
            String actual = gen.generateDigest(digestRequest, "password");
            if (actual.equals(digestRequest.getResponseDigest())) {
                return digestRequest.getUser();
            } else {
                log.warn("that password is incorrect. Try 'password'");
            }
        } else {
            log.warn("user not found: " + digestRequest.getUser() + " - try 'user'");
        }
        return null;
	}

	@Override
	public boolean isDigestAllowed() {
		return false;
	}


	@Override
	public void copyTo(CollectionResource toCollection, String name)throws NotAuthorizedException, BadRequestException, ConflictException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveTo(CollectionResource rDest, String name)throws ConflictException, NotAuthorizedException, BadRequestException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUniqueId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object authenticate(String user, String passw) {
		 if( user.equals("user") && passw.equals("password")) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getModifiedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkRedirect(Request request) throws NotAuthorizedException, BadRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource child(String childName) throws NotAuthorizedException, BadRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends Resource> getChildren() throws NotAuthorizedException, BadRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollectionResource createCollection(String newName)
			throws NotAuthorizedException, ConflictException, BadRequestException {
		// TODO Auto-generated method stub
		return null;
	}

}
