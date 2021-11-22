package org.magic.api.dav;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.Map;

import org.magic.services.MTGConstants;
import org.magic.services.network.URLTools;

import io.milton.http.Auth;
import io.milton.http.FileItem;
import io.milton.http.Range;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.CollectionResource;
import io.milton.resource.FileResource;

public class MTGDavFileResource extends AbstractMTGDavResource implements FileResource
{
	
	public MTGDavFileResource(MTGPath path, MTGFileSystem fs, String log, String pass) {
		super(path, fs, log, pass);
	
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
