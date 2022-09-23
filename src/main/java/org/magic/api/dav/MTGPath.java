package org.magic.api.dav;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.magic.api.beans.MagicCollection;

public class MTGPath implements Path {

	private FileSystem fs;
	private List<String> parts;


	@Override
	public String toString()
	{
		return String.join(fs.getSeparator(), parts);
	}

	public MTGPath(FileSystem fs,String first, String... more)
	{
		init(fs,first,more);
	}

	private void init(FileSystem fs, String first, String... more)
	{
		this.fs=fs;
		parts = new ArrayList<>();

		if(first!=null)
			parts.addAll(Arrays.asList(StringUtils.split(first,fs.getSeparator(),-1)));

		if(more!=null)
			parts.addAll(Arrays.asList(more));
	}

	public List<String> getParts() {
		return parts;
	}

	@Override
	public FileSystem getFileSystem() {
		return fs;
	}

	@Override
	public boolean isAbsolute() {
		return parts.get(0).equals(fs.getSeparator());
	}

	@Override
	public Path getRoot() {
		if (parts ==  null) {
            return this;
        }
		return new MTGPath(fs,parts.get(0));
	}

	@Override
	public Path getFileName() {
		return this;
	}

	@Override
	public Path getParent() {
		if (parts == null) {
	        return null;
        }
		return new MTGPath(fs, parts.get(getNameCount()-1));
	}

	@Override
	public File toFile() {
		return new File(toString());
	}

	@Override
	public int getNameCount() {
		return parts.size();
	}

	public String getStringFileName()
	{
		try {
			return parts.get(parts.size()-1);
		}
		catch(Exception e)
		{
			return "/";
		}
	}

	@Override
	public Path getName(int index) {
		return new MTGPath(fs,parts.get(index));
	}

	@Override
	public Path subpath(int beginIndex, int endIndex) {
		List<String> l = parts.subList(beginIndex, beginIndex+endIndex);
		return new MTGPath(fs,beginIndex == 0 ? parts.get(0) : null, l.toArray(new String[l.size()]) );
	}

	@Override
	public boolean startsWith(Path other) {

		var ret = false;

		if(other instanceof MTGPath p)
		{
			List<String> l = p.getParts();
			for(var i=0;i<=l.size()-1;i++)
			{
				ret = l.get(i).equalsIgnoreCase(parts.get(i));
			}

			return ret;

		}
		return ret;
	}

	@Override
	public boolean endsWith(Path other) {
		var ret = false;

		if(other instanceof MTGPath p)
		{
			List<String> l = p.getParts();
			for(int i=l.size()-1;i>=0;i--)
			{
				ret = l.get(i).equalsIgnoreCase(parts.get(i));
			}
			return ret;
		}
		return ret;
	}

	@Override
	public Path normalize() {
		try {
			return new MTGPath(fs, new URI(toString()).normalize().toString());
		} catch (URISyntaxException e) {
			return null;
		}

	}

	@Override
	public Path resolve(Path other) {
		return new MTGPath(fs, toString(), ((MTGPath)other).toString());
	}

	@Override
	public Path relativize(Path other) {
		return null;
	}

	@Override
	public URI toUri() {
		try {
			return new URI(toString());
		} catch (URISyntaxException e) {
			return null;
		}
	}

	@Override
	public Path toAbsolutePath() {
		return null;
	}

	@Override
	public Path toRealPath(LinkOption... options) throws IOException {
		return null;
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException {
		return null;
	}

	@Override
	public int compareTo(Path other) {
		if(other==null)
			return -1;

		return other.compareTo(this);
	}

	public boolean isMtgPathRoot()
	{
		return parts.size()<=1;
	}

	public boolean isCollection()
	{
		return parts.size()==2;
	}

	public boolean isEdition()
	{
		return parts.size()==3;
	}

	public boolean isCard()
	{
		return parts.size()==4;
	}

	public MagicCollection getCollection()
	{
		return new MagicCollection(getParts().get(1));
	}

	public String getIDEdition()
	{
		return getParts().get(2);
	}

	public String getCardName()
	{
		return getParts().get(3);
	}

	public BasicFileAttributes readAttributes() {
		return new BasicFileAttributes() {

			@Override
			public long size() {
				return 0;
			}

			@Override
			public FileTime lastModifiedTime() {
				return FileTime.fromMillis(new Date().getTime());
			}

			@Override
			public FileTime lastAccessTime() {
				return FileTime.fromMillis(new Date().getTime());
			}

			@Override
			public boolean isSymbolicLink() {
				return false;
			}

			@Override
			public boolean isRegularFile() {
				return isCard();
			}

			@Override
			public boolean isOther() {
				return false;
			}

			@Override
			public boolean isDirectory() {
				return !isRegularFile();
			}

			@Override
			public Object fileKey() {
				return null;
			}

			@Override
			public FileTime creationTime() {
				return FileTime.fromMillis(new Date().getTime());
			}
		};
	}

}
