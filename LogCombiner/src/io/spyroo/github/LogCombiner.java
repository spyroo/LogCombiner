package io.spyroo.github;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.apache.commons.*;
import org.apache.commons.io.FileUtils;

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
		
		return new File(tempDir.toString() + "/" + getName(cleanLogsLink));
	}
	
	public File getCombinedFiles(File file1, File file2, String newLogFileName) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(Files.readAllBytes(file1.toPath()));
		out.write(Files.readAllBytes(file2.toPath()));
		File f = new File(tempDir.toString() + "/" + newLogFileName);
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
	
	public String sendLog(String title, String map, File logfile){
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost("http://logs.tf/upload");

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("title", title, ContentType.TEXT_PLAIN);
		builder.addTextBody("map", map, ContentType.TEXT_PLAIN);
		builder.addTextBody("key", "65d7341d52f5260db135567f650422af", ContentType.TEXT_PLAIN);
		builder.addBinaryBody("logfile", logfile, ContentType.APPLICATION_OCTET_STREAM, logfile.getName());
		builder.addTextBody("key", "65d7341d52f5260db135567f650422af", ContentType.TEXT_PLAIN);
		builder.addTextBody("uploader", "Spyro's Combiner", ContentType.TEXT_PLAIN);

		HttpEntity multipart = builder.build();

		uploadFile.setEntity(multipart);
		
		try{
			HttpResponse response = httpClient.execute(uploadFile); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
			    sb.append(line).append("\n");
			}
			
			String responseString = sb.toString();
			JSONObject obj = new JSONObject(responseString);
			boolean success = obj.getBoolean("success");
			if(success){
				String url = "logs.tf" + obj.getString("url");
				return url;
			}else{
				return obj.getString("error");
			}
					
		}catch(Exception e){
			e.printStackTrace();
		}
		return "false";
	}

	public void delDir() {
		try {
			FileUtils.deleteDirectory(tempDir.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
