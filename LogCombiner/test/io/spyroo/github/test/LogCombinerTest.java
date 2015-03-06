package io.spyroo.github.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import io.spyroo.github.LogCombiner;
import net.lingala.zip4j.exception.ZipException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LogCombinerTest {
	
	private LogCombiner lc;
	private String clean = "http://logs.tf/686299";
	private String download = "http://logs.tf/static/logs/log_686299.log.zip";
	private String clean2 = "http://logs.tf/686330";
	private String download2 = "http://logs.tf/static/logs/log_686330.log.zip";
	
	@Before
	public void before() {
		lc = new LogCombiner("Spyros Combiner (Unit tests)",
				"65d7341d52f5260db135567f650422af");
	}

	@Test
	public void testGetCleanLogsLink() {
		assertTrue("Logs link must be http://logs.tf/686299", "http://logs.tf/686299".equals(lc.getCleanLogsLink("http://logs.tf/686299?highlight=76561198045043478")));
	}

	@Test
	public void testGetLogsDownloadLink() {
		assertTrue("Logs download link must be http://logs.tf/static/logs/log_686299.log.zip", "http://logs.tf/static/logs/log_686299.log.zip".equals(lc.getLogsDownloadLink(clean)));
	}
	
	@Test
	public void testGetLogFile(){
		try {
			assertNotNull("getLogFile is not null", lc.getLogFile(download, clean));
		} catch (ZipException e) {
			fail("getLogFile must not throw ZipExcpetion");
		} catch (IOException e) {
			fail("getLogFile must not throw IOException");
		}
	}
	
	@Test
	public void testGetCombinedFiles() throws ZipException, IOException{
		File log1 = lc.getLogFile(download, clean);
		File log2 = lc.getLogFile(download2, clean2);
		File combined = lc.getCombinedFiles(log1, log2, "combinedLogFile");
		assertNotNull("Combined file must not be null", combined);
	}
	
	@Test
	public void testSendLog() throws ZipException, IOException{
		File log1 = lc.getLogFile(download, clean);
		File log2 = lc.getLogFile(download2, clean2);
		File combined = lc.getCombinedFiles(log1, log2, "combinedLogFile");
		
		String response = lc.sendLog("Unit test log", "cp_steel", combined);
		assertFalse("Resposne must not have fail", response.contains("fail"));
		
	}
	
}
