package io.spyroo.github;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class LogCombiner {
	
	private Path tempDir;
	
	public LogCombiner(){
		try {
			tempDir = Files.createTempDirectory("Combiner_");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public File getLogFile(String logFileUrl, String cleanLogsLink) throws ZipException, IOException{
		ZipFile zf = getZipFile(logFileUrl);
		
		zf.extractAll(tempDir.toAbsolutePath().toString());
		
		System.out.println(tempDir.toString());
		System.out.println(getName(cleanLogsLink));
		return new File(tempDir.toString() + "/" + getName(cleanLogsLink));
	}
	
	public File getCombinedFiles(File file1, File file2, String newLogFileName) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(Files.readAllBytes(file1.toPath()));
		out.write(Files.readAllBytes(file2.toPath()));
		File f = new File(newLogFileName);
		Files.write(f.toPath(), out.toByteArray());
		return f;
	}
	
	public ZipFile getZipFile(String fileUrl) throws IOException, ZipException{
		URL website = new URL(fileUrl);
		Path tmp_2 = Files.createTempDirectory("Combiner_");
		File f = new File(tmp_2.toString() + "/templog");
		f.createNewFile();
		Files.copy(website.openStream(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		ZipFile za = new ZipFile(f);
		return za;
	}
	
	public String cleanLogsUrl(String dirtyLogsUrl){
		StringBuilder sb = new StringBuilder();
		for(char c : dirtyLogsUrl.toCharArray()){
			if(c == '?'){
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	public String getLogDownload(String url){
		return "http://logs.tf/static/logs/" + getName(url) + ".zip";
	}
	
	public String getName(String url){
		String id = url.replaceAll("[^0-9]*", "");
		return "log_" + id + ".log";
	}
	
}
