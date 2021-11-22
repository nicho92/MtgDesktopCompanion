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

import org.jfree.util.Log;
import org.magic.services.network.URLTools;

import io.milton.http.Auth;
import io.milton.http.Range;
import io.milton.http.XmlWriter;
import io.milton.http.exceptions.BadRequestException;
import io.milton.http.exceptions.ConflictException;
import io.milton.http.exceptions.NotAuthorizedException;
import io.milton.http.exceptions.NotFoundException;
import io.milton.resource.CollectionResource;
import io.milton.resource.FolderResource;
import io.milton.resource.Resource;

public class MTGDavFolderResource extends AbstractMTGDavResource implements FolderResource
{
	private List<AbstractMTGDavResource> children;
	private boolean root;

	
	public MTGDavFolderResource(MTGPath path, MTGFileSystem fs, boolean root, String log, String pass) {
		super(path, fs, log, pass);
		this.root=root;
	
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
					fs.getRootDirectories().forEach(p->children.add(new MTGDavFolderResource((MTGPath)p, fs, false,user,pass)));
					return children;
				}
				else
				{
					try (Stream<Path> s = Files.list(mtgpath))
					{
						s.forEach(p->{
							if(((MTGPath)p).isCard())
								children.add(new MTGDavFileResource((MTGPath)p, fs, user,pass));
							else
								children.add(new MTGDavFolderResource((MTGPath)p, fs, false,user,pass));
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
       
		
		var w = new XmlWriter(out);
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
		return URLTools.HEADER_HTML;
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
		
		logger.debug("createCollection " + mtgpath + "/" + newName);
		
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
