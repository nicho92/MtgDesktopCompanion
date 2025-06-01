package org.magic.services.tools;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MTGEdition;
import org.magic.api.beans.technical.audit.FileAccessInfo;
import org.magic.api.beans.technical.audit.FileAccessInfo.ACCESSTYPE;
import org.magic.api.interfaces.abstracts.AbstractTechnicalServiceManager;
import org.magic.services.MTGConstants;
import org.magic.services.logging.MTGLogger;
import org.magic.services.network.URLTools;
import org.magic.services.recognition.DescContainer;

import com.google.common.io.Files;
import com.google.gson.JsonElement;

public class FileTools {

	private static final String CORRECT_REGEX = "[^a-zA-Z0-9\\._-]+";
	private static Logger logger = MTGLogger.getLogger(FileTools.class);


	public static void saveFile(File f, byte[] content) throws IOException {
		
		var info = new FileAccessInfo(f);
		 Files.touch(f);
		 try (var fileOuputStream = new FileOutputStream(f))
		 {
	            fileOuputStream.write(content);
		 }
		
		 	info.setEnd(Instant.now());
			info.setAccesstype(ACCESSTYPE.CREATE);
		 
		 AbstractTechnicalServiceManager.inst().store(info);
	}

	public static int linesCount(File f)
	{
		var info = new FileAccessInfo(f);
		try {
			var ret =  Files.readLines(f,MTGConstants.DEFAULT_ENCODING).size();
		 	info.setEnd(Instant.now());
			info.setAccesstype(ACCESSTYPE.READ);
			AbstractTechnicalServiceManager.inst().store(info);
			
			return ret;
			
		} catch (IOException e) {
			logger.error(e);
			return -1;
		}
	}



	public static void saveFile(File f,String data) throws IOException
	{
		saveFile(f,data,MTGConstants.DEFAULT_ENCODING);
	}

	public static void saveFile(File f, String data, Charset enc) throws IOException {
		var info = new FileAccessInfo(f);
		String correctFilename= f.getName().replaceAll(CORRECT_REGEX, "_");
		f=new File(f.getParentFile(),correctFilename);
		logger.debug("saving file {}",f);
		FileUtils.write(f, data,enc);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
	}


	public static void saveLargeFile(File f, String data, Charset enc) throws IOException {
		logger.debug("saving file {}", f);
		var info = new FileAccessInfo(f);
		try (final OutputStream os = new FileOutputStream(f, false)) {
	        final InputStream inputStream = IOUtils.toInputStream(data,enc);
	        if (inputStream != null) {
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            while ((bytesRead = inputStream.read(buffer)) != -1) {
	                os.write(buffer, 0, bytesRead);
	            }
	            inputStream.close();
	        }
		}
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);

	}



	public static void saveProperties(File f,Properties props) throws IOException
	{
		var info = new FileAccessInfo(f);
		try (var fos = new FileOutputStream(f)){
			props.store(fos, "");
		}
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
	}



	public static void loadProperties(File f,Properties props) throws IOException
	{
		var info = new FileAccessInfo(f);
		props.clear();
		try (var fis = new FileInputStream(f)){
			props.load(fis);
		}
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.READ);
		AbstractTechnicalServiceManager.inst().store(info);
		
		
	}


	public static void deleteFile(File f) throws IOException
	{
			var info = new FileAccessInfo(f);
			String correctFilename= f.getName().replaceAll(CORRECT_REGEX, "_");
			f=new File(f.getParentFile(),correctFilename);
			logger.debug("deleting file {}",f);
			FileUtils.forceDelete(f);
			info.setEnd(Instant.now());
			info.setAccesstype(ACCESSTYPE.DELETE);
			AbstractTechnicalServiceManager.inst().store(info);
	}


	public static byte[] readFileAsBinary(File f) throws IOException
	{
		return Files.toByteArray(f);
	}


	public static String readUTF8(ByteBuffer buf)
	{
		var len = buf.getShort();
		var str = new byte[len];
		buf.get(str);
		String res = null;
		res = new String(str,StandardCharsets.UTF_8);
		return res;
	}

	public static String readFile(File f) throws IOException
	{
		return readFile(f,MTGConstants.DEFAULT_ENCODING);
	}

	public static String readFile(File f,Charset charset) throws IOException
	{
		var info = new FileAccessInfo(f);
		if(f==null || !f.exists())
		{
			logger.warn("{} doesn't exist",f);
			return "";
		}
		else
		{
			var fileName=f.getAbsolutePath().replaceAll("[\n\r\t]", "_");
			logger.debug("opening file {}", fileName);
			var ret=FileUtils.readFileToString(f,charset);
			info.setEnd(Instant.now());
			info.setAccesstype(ACCESSTYPE.READ);
			AbstractTechnicalServiceManager.inst().store(info);
			
			return ret;
		}
	}


	public static JsonElement readJson(File f) throws IOException
	{
		return URLTools.toJson(readFile(f));
	}


	private FileTools() {	}

	public static void extractConfig(File fzip) throws IOException
	{
		var fileFilter1 =   FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("logs", null));
		var fileFilter2 =   FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("data", null));
		var exceptFilter =   FileFilterUtils.and(fileFilter1, fileFilter2 );


		try (var out = new ZipOutputStream(new FileOutputStream(fzip))) {
			for(File f : FileUtils.listFilesAndDirs(MTGConstants.CONF_DIR, FileFileFilter.INSTANCE, exceptFilter))
				addFile(f,out);
		}

	}

	public static synchronized void writeSetRecognition(File f,MTGEdition ed,int sizeOfSet, List<DescContainer> desc) throws IOException
	{
		var info = new FileAccessInfo(f);
		if(!f.getParentFile().exists())
		{
			FileUtils.forceMkdir(f.getParentFile());
		}

		try(var out = new DataOutputStream(new FileOutputStream(f)))
		{
			out.writeUTF(ed.getSet());
			out.writeInt(sizeOfSet);
			out.writeInt(desc.size());

				for (DescContainer element : desc) {
					out.writeUTF(element.getStringData());
					element.getDescData().writeOut(out);
				}
		}
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.READ);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static ByteBuffer getBuffer(File f) throws IOException
	{
		var info = new FileAccessInfo(f);
		try(var aFile = new RandomAccessFile(f.getAbsolutePath(),"r"))
		{
			var inChannel = aFile.getChannel();
			long fileSize = inChannel.size();
			var buffer = ByteBuffer.allocate((int) fileSize);
			inChannel.read(buffer);
			buffer.flip();
			info.setEnd(Instant.now());
			info.setAccesstype(ACCESSTYPE.READ);
			AbstractTechnicalServiceManager.inst().store(info);
			return buffer;
		}

	}

	public static void unzip(File fileZip,File dest) throws IOException
	{
		var infoR = new FileAccessInfo(fileZip);
		if(!dest.isDirectory())
			throw new IOException(dest + " is not a directory");

		try(var zipFile = new ZipFile(fileZip)){
			var entries = zipFile.entries();
		
			while (entries.hasMoreElements())
			{
				var zipEntry = entries.nextElement();
				var f = new File(dest, zipEntry.getName());

	        	if(zipEntry.isDirectory())
	        	{
	        		FileUtils.forceMkdir(f);
	        	}
	        	else
	        	{
	        		FileUtils.forceMkdirParent(f);
	        		FileUtils.touch(f);
	        	}


		       	try(var fos = new FileOutputStream(f))
		       	{
		       		IOUtils.write(zipFile.getInputStream(zipEntry).readAllBytes(), fos);
		    		var infoW = new FileAccessInfo(f);
		       		infoW.setEnd(Instant.now());
		       		infoW.setAccesstype(ACCESSTYPE.WRITE);
		       		AbstractTechnicalServiceManager.inst().store(infoW);
		       	}
		    }
		}
		infoR.setEnd(Instant.now());
   		infoR.setAccesstype(ACCESSTYPE.READ);
   		AbstractTechnicalServiceManager.inst().store(infoR);
	}


	private static void addFile(File f, ZipOutputStream out) throws IOException
	{	
		var info = new FileAccessInfo(f);
		if(f.isDirectory())
			return;

		try (var in = new FileInputStream(f)) {
			out.putNextEntry(new ZipEntry(MTGConstants.CONF_DIR.toPath().relativize(f.toPath()).toString()));
			IOUtils.write(in.readAllBytes(), out);
			out.closeEntry();
		}
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
	}


	public static void unZipIt(File src,File dst) {
		
		var infoR = new FileAccessInfo(src);
		var buffer = new byte[1024];
 		try (var zis = new ZipInputStream(new FileInputStream(src))) {
 			var ze = zis.getNextEntry();
 			while (ze != null) {
				logger.info("unzip : {}",src.getAbsoluteFile());
 				try (var fos = new FileOutputStream(dst)) {
 					var info = new FileAccessInfo(dst);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					ze = zis.getNextEntry();
					info.setEnd(Instant.now());
					info.setAccesstype(ACCESSTYPE.WRITE);
					AbstractTechnicalServiceManager.inst().store(info);
				}
			}
 			AbstractTechnicalServiceManager.inst().store(infoR);
		} catch (IOException ex) {
			logger.error(ex);
		}
		
		infoR.setEnd(Instant.now());
		infoR.setAccesstype(ACCESSTYPE.READ);
		AbstractTechnicalServiceManager.inst().store(infoR);
 		
 		var dinfo = new FileAccessInfo(src);
 		boolean del = FileUtils.deleteQuietly(src);
 		dinfo.setEnd(Instant.now());
 		dinfo.setAccesstype(ACCESSTYPE.DELETE);
		AbstractTechnicalServiceManager.inst().store(dinfo);
		logger.debug("removing {}={}",src,del);
		
 	}

	public static void copyDirJarToDirectory(String path, File writeDirectory) throws IOException {

		try(var jarFile = new JarFile(FileTools.class.getProtectionDomain().getCodeSource().getLocation().getPath()))
		{
		    final var entries = jarFile.entries(); 
		    while(entries.hasMoreElements()) {
		        final var entry = entries.nextElement();
		        final var name = entry.getName();
		        if (name.startsWith(path + "/")) { //filter according to the path
		        	var f = new File(writeDirectory,name);
		        	logger.debug("writing {}", f);

		        	if(entry.isDirectory())
		        		FileUtils.forceMkdir(f);
		        	else
		        		FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry),f );
		        }
		    }
		}


	}



	public static List<String> readAllLines(File f) throws IOException {
		logger.debug("opening file {}",f);
		var info = new FileAccessInfo(f);
		var ret = Files.readLines(f, MTGConstants.DEFAULT_ENCODING);
		
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.READ);
		AbstractTechnicalServiceManager.inst().store(info);
		return ret;
	}



	public static int daysBetween(File temp) throws IOException {
		return UITools.daysBetween(java.nio.file.Files.getLastModifiedTime(temp.toPath()).toInstant(), new Date().toInstant());
	}

	public static void copyURLToFile(URL resource, File conf) throws IOException {
		var info = new FileAccessInfo(conf);
		FileUtils.copyURLToFile(resource,conf);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static void forceMkdir(File dataDir) throws IOException {
		var info = new FileAccessInfo(dataDir);
		FileUtils.forceMkdir(dataDir);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.CREATE);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static void cleanDirectory(File file) throws IOException {
		var info = new FileAccessInfo(file);
		FileUtils.cleanDirectory(file);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.DELETE);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static long sizeOfDirectory(File file) {
		return FileUtils.sizeOfDirectory(file);
	}

	public static void deleteDirectory(File file) throws IOException {
		var info = new FileAccessInfo(file);
		FileUtils.deleteDirectory(file);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.DELETE);
		AbstractTechnicalServiceManager.inst().store(info);
		
	}

	public static void copyInputStreamToFile(InputStream content, File dest) throws IOException {
		var info = new FileAccessInfo(dest);
		FileUtils.copyInputStreamToFile(content, dest);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
		
	}

	public static Long sizeOf(File file) {
		var info = new FileAccessInfo(file);
		var ret = FileUtils.sizeOf(file);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.READ);
		AbstractTechnicalServiceManager.inst().store(info);
		return ret;
	}

	public static void moveFileToDirectory(File f, File dest, boolean b) throws IOException {
		FileUtils.moveFileToDirectory(f, dest, b);
		
	}

	public static Collection<File> listFiles(File edDir, WildcardFileFilter wildcardFileFilter, IOFileFilter instance) {
		return FileUtils.listFiles(edDir, wildcardFileFilter, instance);
	}

	public static File createTempFile(String string, String fileExtension) throws IOException {
		
		var tmp = new File(MTGConstants.DATA_DIR, "tmp");
		
		if(!tmp.exists())
			forceMkdir(tmp);
		
		var p = File.createTempFile(string,fileExtension,tmp);
		var info = new FileAccessInfo(p);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
		return p;
		
		
	}




}
