package org.magic.services.tools;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.logging.log4j.Logger;
import org.magic.api.beans.MagicEdition;
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
		 Files.touch(f);
		 try (var fileOuputStream = new FileOutputStream(f))
		 {
	            fileOuputStream.write(content);
		 }
	}

	public static void appendLine(File f,String line) throws IOException
	{
		String correctFilename= f.getName().replaceAll(CORRECT_REGEX, "_");
		f=new File(f.getParentFile(),correctFilename);
		FileUtils.write(f, line,MTGConstants.DEFAULT_ENCODING,true);
	}

	public static int linesCount(File f)
	{
		try {
			return Files.readLines(f,MTGConstants.DEFAULT_ENCODING).size();
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
		String correctFilename= f.getName().replaceAll(CORRECT_REGEX, "_");
		f=new File(f.getParentFile(),correctFilename);
		logger.debug("saving file {}",f);
		FileUtils.write(f, data,enc);
	}


	public static void saveLargeFile(File f, String data, Charset enc) throws IOException {
		logger.debug("saving file {}", f);
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

	}



	public static void saveProperties(File f,Properties props) throws IOException
	{
		try (var fos = new FileOutputStream(f)){
			props.store(fos, "");
		}
	}



	public static void loadProperties(File f,Properties props) throws IOException
	{
		props.clear();
		try (var fis = new FileInputStream(f)){
			props.load(fis);
		}
	}

	public static void saveFile(File f,Properties props) throws IOException
	{
		try (var fos = new FileOutputStream(f)){
			props.store(fos, "");
		}
	}


	public static void deleteFile(File f) throws IOException
	{
			String correctFilename= f.getName().replaceAll(CORRECT_REGEX, "_");
			f=new File(f.getParentFile(),correctFilename);
			logger.debug("deleting file {}",f);
			FileUtils.forceDelete(f);
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
		if(f==null || !f.exists())
		{
			logger.warn("{} doesn't exist",f);
			return "";
		}
		else
		{
			var fileName=f.getAbsolutePath().replaceAll("[\n\r\t]", "_");
			logger.debug("opening file {}", fileName);
			return FileUtils.readFileToString(f,charset);
		}
	}


	public static JsonElement readJson(File f) throws IOException
	{
		return URLTools.toJson(readFile(f));
	}


	private FileTools() {	}

	public static void extractConfig(File fzip) throws IOException
	{
		IOFileFilter fileFilter1 =   FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("logs", null));
		IOFileFilter fileFilter2 =   FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("data", null));
		IOFileFilter exceptFilter =   FileFilterUtils.and(fileFilter1, fileFilter2 );


		try (var out = new ZipOutputStream(new FileOutputStream(fzip))) {
			for(File f : FileUtils.listFilesAndDirs(MTGConstants.CONF_DIR, FileFileFilter.INSTANCE, exceptFilter))
				addFile(f,out);
		}

	}

	public static void decompressGzipFile(File fileZip,File dest) {

		if(dest.isDirectory())
			dest=new File(dest,FilenameUtils.removeExtension(fileZip.getName()));

        try (
        		var fis = new FileInputStream(fileZip);
        		var gis = new GZIPInputStream(fis);
        		var fos = new FileOutputStream(dest);
        	)
        	{
        	var buffer = new byte[512];
            int len;
            while((len = gis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            logger.error(e);
        }

    }


	public static synchronized void writeSetRecognition(File f,MagicEdition ed,int sizeOfSet, List<DescContainer> desc) throws IOException
	{

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

	}

	public static ByteBuffer getBuffer(File f) throws IOException
	{
		try(var aFile = new RandomAccessFile(f.getAbsolutePath(),"r"))
		{
			var inChannel = aFile.getChannel();
			long fileSize = inChannel.size();
			var buffer = ByteBuffer.allocate((int) fileSize);
			inChannel.read(buffer);
			buffer.flip();
			return buffer;
		}

	}

	public static void unzip(File fileZip,File dest) throws IOException
	{

		if(!dest.isDirectory())
			throw new IOException(dest + " is not a directory");

		try(var zipFile = new ZipFile(fileZip)){
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
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
		       	}

		    }
		}
	}


	private static void addFile(File f, ZipOutputStream out) throws IOException
	{
		if(f.isDirectory())
			return;

		try (var in = new FileInputStream(f)) {
			out.putNextEntry(new ZipEntry(MTGConstants.CONF_DIR.toPath().relativize(f.toPath()).toString()));
			IOUtils.write(in.readAllBytes(), out);
			out.closeEntry();
		}
	}


	public static void zip(File dir,File dest) throws IOException {
		try (var out = new ZipOutputStream(new FileOutputStream(dest))) {
			for (File doc : dir.listFiles()) {
				if (!doc.getName().endsWith(".tmp")) {
					addFile(doc,out);
				}
			}
		}
	}


	public static void unZipIt(File src,File dst) {
		var buffer = new byte[1024];
 		try (var zis = new ZipInputStream(new FileInputStream(src))) {
 			var ze = zis.getNextEntry();
 			while (ze != null) {
				logger.info("unzip : {}",src.getAbsoluteFile());
 				try (var fos = new FileOutputStream(dst)) {
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					ze = zis.getNextEntry();
				}
			}
		} catch (IOException ex) {
			logger.error(ex);
		}
 		boolean del = FileUtils.deleteQuietly(src);
		logger.debug("removing {}={}",src,del);
 	}

	public static void copyDirJarToDirectory(String path, File writeDirectory) throws IOException {

		try(var jarFile = new JarFile(FileTools.class.getProtectionDomain().getCodeSource().getLocation().getPath()))
		{
		    final Enumeration<JarEntry> entries = jarFile.entries(); //gives ALL entries in jar
		    while(entries.hasMoreElements()) {
		        final JarEntry entry = entries.nextElement();
		        final String name = entry.getName();
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
		return Files.readLines(f, MTGConstants.DEFAULT_ENCODING);
	}



	public static int daysBetween(File temp) throws IOException {
		return UITools.daysBetween(java.nio.file.Files.getLastModifiedTime(temp.toPath()).toInstant(), new Date().toInstant());
	}



	public static List<File> listFiles(File dir)
	{
			try(Stream<Path> s = java.nio.file.Files.list(dir.toPath())){
				return s.map(Path::toFile).toList();
			} catch (IOException e) {
				logger.error(e);
				return new ArrayList<>();
			}
	}




}
