package org.magic.services.tools;

import java.io.BufferedInputStream;
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
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
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

		final var info = new FileAccessInfo(f);
		Files.touch(f);
		try (var fileOuputStream = new FileOutputStream(f)) {
			fileOuputStream.write(content);
		}

		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.CREATE);

		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static int linesCount(File f) {
		final var info = new FileAccessInfo(f);
		try {
			final var ret = Files.readLines(f, MTGConstants.DEFAULT_ENCODING).size();
			info.setEnd(Instant.now());
			info.setAccesstype(ACCESSTYPE.READ);
			AbstractTechnicalServiceManager.inst().store(info);

			return ret;

		} catch (final IOException e) {
			logger.error(e);
			return -1;
		}
	}

	public static void saveFile(File f, String data) throws IOException {
		saveFile(f, data, MTGConstants.DEFAULT_ENCODING);
	}

	public static void saveFile(File f, String data, Charset enc) throws IOException {
		final var info = new FileAccessInfo(f);
		final var correctFilename = f.getName().replaceAll(CORRECT_REGEX, "_");
		f = new File(f.getParentFile(), correctFilename);
		logger.debug("saving file {}", f);
		FileUtils.write(f, data, enc);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static void saveLargeFile(File f, String data, Charset enc) throws IOException {
		logger.debug("saving file {}", f);
		final var info = new FileAccessInfo(f);
		try (final OutputStream os = new FileOutputStream(f, false)) {
			final InputStream inputStream = IOUtils.toInputStream(data, enc);
			if (inputStream != null) {
				final byte[] buffer = new byte[1024];
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

	public static void saveProperties(File f, Properties props) throws IOException {
		final var info = new FileAccessInfo(f);
		try (var fos = new FileOutputStream(f)) {
			props.store(fos, "");
		}
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static void loadProperties(File f, Properties props) throws IOException {
		final var info = new FileAccessInfo(f);
		props.clear();
		try (var fis = new FileInputStream(f)) {
			props.load(fis);
		}
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.READ);
		AbstractTechnicalServiceManager.inst().store(info);

	}

	public static void deleteFile(File f) throws IOException {
		final var info = new FileAccessInfo(f);
		final var correctFilename = f.getName().replaceAll(CORRECT_REGEX, "_");
		f = new File(f.getParentFile(), correctFilename);
		logger.debug("deleting file {}", f);
		FileUtils.forceDelete(f);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.DELETE);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static byte[] readFileAsBinary(File f) throws IOException {
		return Files.toByteArray(f);
	}

	public static String readUTF8(ByteBuffer buf) {
		final var len = buf.getShort();
		final var str = new byte[len];
		buf.get(str);
		String res = null;
		res = new String(str, StandardCharsets.UTF_8);
		return res;
	}

	public static String readFile(File f) throws IOException {
		return readFile(f, MTGConstants.DEFAULT_ENCODING);
	}

	public static String readFile(File f, Charset charset) throws IOException {

		final var info = new FileAccessInfo(f);
		if (f == null || !f.exists()) {
			logger.warn("{} doesn't exist", f);
			return "";
		} else {
			final var fileName = f.getAbsolutePath().replaceAll("[\n\r\t]", "_");
			logger.debug("opening file {}", fileName);
			final var ret = FileUtils.readFileToString(f, charset);
			info.setEnd(Instant.now());
			info.setAccesstype(ACCESSTYPE.READ);
			AbstractTechnicalServiceManager.inst().store(info);

			return ret;
		}
	}

	public static JsonElement readJson(File f) throws IOException {
		return URLTools.toJson(readFile(f));
	}

	private FileTools() {
	}

	public static void extractConfig(File fzip) throws IOException {
		final var fileFilter1 = FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("logs", null));
		final var fileFilter2 = FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("data", null));
		final var exceptFilter = FileFilterUtils.and(fileFilter1, fileFilter2);

		try (var out = new ZipOutputStream(new FileOutputStream(fzip))) {
			for (final File f : FileUtils.listFilesAndDirs(MTGConstants.CONF_DIR, FileFileFilter.INSTANCE, exceptFilter)) {
			    addFile(f, out);
			}
		}

	}

	public static synchronized void writeSetRecognition(File f, MTGEdition ed, int sizeOfSet, List<DescContainer> desc)
			throws IOException {
		final var info = new FileAccessInfo(f);
		if (!f.getParentFile().exists()) {
			FileUtils.forceMkdir(f.getParentFile());
		}

		try (var out = new DataOutputStream(new FileOutputStream(f))) {
			out.writeUTF(ed.getSet());
			out.writeInt(sizeOfSet);
			out.writeInt(desc.size());

			for (final DescContainer element : desc) {
				out.writeUTF(element.getStringData());
				element.getDescData().writeOut(out);
			}
		}
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.READ);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static ByteBuffer getBuffer(File f) throws IOException {
		final var info = new FileAccessInfo(f);
		try (var aFile = new RandomAccessFile(f.getAbsolutePath(), "r")) {
			final var inChannel = aFile.getChannel();
			final long fileSize = inChannel.size();
			final var buffer = ByteBuffer.allocate((int) fileSize);
			inChannel.read(buffer);
			buffer.flip();
			info.setEnd(Instant.now());
			info.setAccesstype(ACCESSTYPE.READ);
			AbstractTechnicalServiceManager.inst().store(info);
			return buffer;
		}

	}

	public static void unZipFileAndRemove(File src, File dst) {

		final var infoR = new FileAccessInfo(src);
		final var buffer = new byte[1024];
		try (var zis = new ZipInputStream(new FileInputStream(src))) {
			var ze = zis.getNextEntry();
			while (ze != null) {
				logger.debug("unzip : {}", src.getAbsoluteFile());
				try (var fos = new FileOutputStream(dst)) {
					final var info = new FileAccessInfo(dst);
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
		} catch (final IOException ex) {
			logger.error(ex);
		}

		infoR.setEnd(Instant.now());
		infoR.setAccesstype(ACCESSTYPE.READ);
		AbstractTechnicalServiceManager.inst().store(infoR);

		final var dinfo = new FileAccessInfo(src);
		final boolean del = FileUtils.deleteQuietly(src);
		dinfo.setEnd(Instant.now());
		dinfo.setAccesstype(ACCESSTYPE.DELETE);
		AbstractTechnicalServiceManager.inst().store(dinfo);
		logger.debug("removing {}={}", src, del);

	}

	public static void unzipInDirectory(File input, File destDirectory) throws IOException {
	    final var infoR = new FileAccessInfo(input);
	    final var destPath = destDirectory.toPath().toAbsolutePath().normalize();
	    java.nio.file.Files.createDirectories(destPath);

	    try (var zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(input)))) {
	        ZipEntry entry;
	        while ((entry = zis.getNextEntry()) != null) {
	            final var target = destPath.resolve(entry.getName()).normalize();

	            final var infoW = new FileAccessInfo(target.toFile());

	            if (!target.startsWith(destPath)) {
	                throw new IOException("Entrée zip invalide (Zip Slip) : " + entry.getName());
	            }
	            if (entry.isDirectory()) {
	        	  java.nio.file.Files.createDirectories(target);
	            } else {
	        	  java.nio.file.Files.createDirectories(target.getParent());
	        	  java.nio.file.Files.copy(zis, target, StandardCopyOption.REPLACE_EXISTING);
	            }
			infoW.setEnd(Instant.now());
			infoW.setAccesstype(ACCESSTYPE.WRITE);
			AbstractTechnicalServiceManager.inst().store(infoW);
	        }
	    }

	    infoR.setEnd(Instant.now());
	    infoR.setAccesstype(ACCESSTYPE.READ);
	    AbstractTechnicalServiceManager.inst().store(infoR);
	}


	private static void addFile(File f, ZipOutputStream out) throws IOException {
		final var info = new FileAccessInfo(f);
		if (f.isDirectory()) {
		    return;
		}

		try (var in = new FileInputStream(f)) {
			out.putNextEntry(new ZipEntry(MTGConstants.CONF_DIR.toPath().relativize(f.toPath()).toString()));
			IOUtils.write(in.readAllBytes(), out);
			out.closeEntry();
		}
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
	}



	public static void copyDirJarToDirectory(String path, File writeDirectory) throws IOException {

		try (var jarFile = new JarFile(FileTools.class.getProtectionDomain().getCodeSource().getLocation().getPath())) {
			final var entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				final var entry = entries.nextElement();
				final var name = entry.getName();
				if (name.startsWith(path + "/")) { // filter according to the path
					final var f = new File(writeDirectory, name);
					logger.debug("writing {}", f);

					if (entry.isDirectory()) {
					    FileUtils.forceMkdir(f);
					} else {
					    FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), f);
					}
				}
			}
		}

	}

	public static List<String> readAllLines(File f) throws IOException {
		logger.debug("opening file {}", f);
		final var info = new FileAccessInfo(f);
		final var ret = Files.readLines(f, MTGConstants.DEFAULT_ENCODING);

		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.READ);
		AbstractTechnicalServiceManager.inst().store(info);
		return ret;
	}

	public static int daysBetween(File temp) throws IOException {
		return UITools.daysBetween(java.nio.file.Files.getLastModifiedTime(temp.toPath()).toInstant(),
				new Date().toInstant());
	}

	public static void copyURLToFile(URL resource, File conf) throws IOException {
		final var info = new FileAccessInfo(conf);
		FileUtils.copyURLToFile(resource, conf);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static void forceMkdir(File dataDir) throws IOException {
		final var info = new FileAccessInfo(dataDir);
		FileUtils.forceMkdir(dataDir);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.CREATE);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static void cleanDirectory(File file) throws IOException {
		final var info = new FileAccessInfo(file);
		FileUtils.cleanDirectory(file);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.DELETE);
		AbstractTechnicalServiceManager.inst().store(info);
	}

	public static long sizeOfDirectory(File file) {
		return FileUtils.sizeOfDirectory(file);
	}

	public static void deleteDirectory(File file) throws IOException {
		final var info = new FileAccessInfo(file);
		FileUtils.deleteDirectory(file);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.DELETE);
		AbstractTechnicalServiceManager.inst().store(info);

	}

	public static void copyInputStreamToFile(InputStream content, File dest) throws IOException {
		final var info = new FileAccessInfo(dest);
		FileUtils.copyInputStreamToFile(content, dest);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);

	}

	public static Long sizeOf(File file) {
		final var info = new FileAccessInfo(file);
		final var ret = FileUtils.sizeOf(file);
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

		final var tmp = new File(MTGConstants.DATA_DIR, "tmp");

		if (!tmp.exists()) {
		    forceMkdir(tmp);
		}

		final var p = File.createTempFile(string, fileExtension, tmp);
		final var info = new FileAccessInfo(p);
		info.setEnd(Instant.now());
		info.setAccesstype(ACCESSTYPE.WRITE);
		AbstractTechnicalServiceManager.inst().store(info);
		return p;

	}

	public static File toFile(String url) {

	   return new File(url);

	}

}
