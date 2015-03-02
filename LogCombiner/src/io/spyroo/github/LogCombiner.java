package io.spyroo.github;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class LogCombiner {
	
	public LogCombiner(){

	}
	/*
	public void test(){
		String clean = cleanLogsUrl("http://logs.tf/697809?highlight=76561198045043478");
		String clean2 = cleanLogsUrl("http://logs.tf/697767?highlight=76561198045043478");
		String url = getLogDownload(clean);
		String url2 = getLogDownload(clean2);
		try {
			
			File log1 = getLogFile(url, clean);
			File log2 = getLogFile(url2, clean2);
			
			File combined = getCombinedFiles(log1, log2, "temp/newLogFile.log");
			
			System.out.println("done");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}*/
	
	public File getLogFile(String logFileUrl, String cleanLogsLink) throws ZipException, IOException{
		ZipFile zf = getZipFile(logFileUrl);
		
        Path tmp_2 = Files.createTempDirectory("Combiner_");
		zf.extractAll(tmp_2.toAbsolutePath().toString());
		
		System.out.println(tmp_2.toString());
		System.out.println(getName(cleanLogsLink));
		return new File(tmp_2.toString() + "/" + getName(cleanLogsLink));
	}
	
	public File getCombinedFiles(File file1, File file2, String newLogFileName) throws IOException{
		PrintWriter pw = new PrintWriter(newLogFileName);
		BufferedReader br = new BufferedReader(new FileReader(file1));
		String line;
		System.out.println("adding file 1");
		while((line = br.readLine()) != null){
			pw.println(line);
		}
		br.close();
		BufferedReader br2 = new BufferedReader(new FileReader(file2));
		System.out.println("adding file 2");
		while((line = br2.readLine()) != null){
			pw.println(line);
		}
		br2.close();
		pw.close();
		return new File(newLogFileName);
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
