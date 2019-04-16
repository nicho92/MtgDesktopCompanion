package org.magic.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;
import org.magic.services.MTGConstants;
import org.magic.services.MTGLogger;

public class FileTools {

	private static Logger logger = MTGLogger.getLogger(FileTools.class);

	private FileTools() {	}

	public static void main(String[] args) throws IOException {
		importConfig(new File("d:/test.zip"));
	}
	
	public static void extractConfig(File dir) throws IOException 
	{
		IOFileFilter fileFilter1 =   FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("logs", null));
		IOFileFilter fileFilter2 =   FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("data", null));
		IOFileFilter exceptFilter =   FileFilterUtils.and(fileFilter1, fileFilter2 );
		
		
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dir))) {
			for(File f : FileUtils.listFilesAndDirs(MTGConstants.CONF_DIR, FileFileFilter.FILE, exceptFilter))
				addFile(f,out);
		}
	
	}
	
	public static void importConfig(File fileZip) throws IOException 
	{
//		ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
//		ZipEntry zipEntry = zis.getNextEntry();
//        while (zipEntry != null) {
//            File newFile = newFile(new File("d:/"), zipEntry);
//            FileOutputStream fos = new FileOutputStream(newFile);
//            IOUtils.write(zis.readAllBytes(), fos);
//            fos.close();
//            zipEntry = zis.getNextEntry();
//        }
//        zis.closeEntry();
//        zis.close();
	}
	
	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
         
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
         
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
         
        return destFile;
    }
	
	
	private static void addFile(File f, ZipOutputStream out) throws IOException
	{
		if(f.isDirectory())
			return;
		
		try (FileInputStream in = new FileInputStream(f)) {
			out.putNextEntry(new ZipEntry(MTGConstants.CONF_DIR.toPath().relativize(f.toPath()).toString()));
			IOUtils.write(in.readAllBytes(), out);
			out.closeEntry();
		}
	}
	
	
	public static void zip(File dir,File dest) throws IOException {
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dest))) {
			for (File doc : dir.listFiles()) {
				if (!doc.getName().endsWith(".tmp")) {
					addFile(doc,out);
				}
			}
		}
	}
	
	
	public static void unZipIt(File src,File dst) {
 		byte[] buffer = new byte[1024];
 		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(src))) {
 			ZipEntry ze = zis.getNextEntry();
 			while (ze != null) {
				logger.info("unzip : " + src.getAbsoluteFile());
 				try (FileOutputStream fos = new FileOutputStream(dst)) {
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
		logger.debug("removing " + src + "=" + del);
 	}
}
