package io.spyroo.github;

import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;

import net.lingala.zip4j.exception.ZipException;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class LogCombinerWindow {

	private JFrame frmLogFileCombiner;
	private JTextField logFileLink1;
	private JTextField logFileLink2;
	private JTextField combinedName;
	private JProgressBar progressBar;
	private JTextField finalLogLink;
	private JLabel lblLink;
	private JButton btnCopy;
	private JLabel lblMapName;
	private JTextField mapNameText;
	private static LogCombiner lc;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	lc.delDir();
		    }
		});
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogCombinerWindow window = new LogCombinerWindow();
					window.frmLogFileCombiner.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LogCombinerWindow() {
		lc = new LogCombiner("Spyros Combiner", "65d7341d52f5260db135567f650422af");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLogFileCombiner = new JFrame();
		frmLogFileCombiner.setResizable(false);
		frmLogFileCombiner.setTitle("Log File Combiner By Spyro");
		frmLogFileCombiner.setBounds(100, 100, 457, 190);
		frmLogFileCombiner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLogFileCombiner.getContentPane().setLayout(new MigLayout("", "[][grow][63.00]", "[][][][][][]"));
		
		JLabel lblLogFileLink = new JLabel("Log File Link 1");
		frmLogFileCombiner.getContentPane().add(lblLogFileLink, "cell 0 0,alignx trailing");
		
		logFileLink1 = new JTextField();
		frmLogFileCombiner.getContentPane().add(logFileLink1, "cell 1 0 2 1,growx");
		logFileLink1.setColumns(10);
		
		JLabel lblLogFileLink_1 = new JLabel("Log File Link 2");
		frmLogFileCombiner.getContentPane().add(lblLogFileLink_1, "cell 0 1,alignx trailing");
		
		logFileLink2 = new JTextField();
		frmLogFileCombiner.getContentPane().add(logFileLink2, "cell 1 1 2 1,growx");
		logFileLink2.setColumns(10);
		
		JLabel lblDestinationFile = new JLabel("Combined Name");
		frmLogFileCombiner.getContentPane().add(lblDestinationFile, "cell 0 2,alignx trailing");
		
		combinedName = new JTextField();
		frmLogFileCombiner.getContentPane().add(combinedName, "cell 1 2 2 1,growx");
		combinedName.setColumns(10);
		
		JButton btnCombine = new JButton("Combine");
		btnCombine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				progressBar.setMaximum(100);
				if(!logFileLink1.getText().isEmpty() && !logFileLink2.getText().isEmpty() && !combinedName.getText().isEmpty()){
					
					progressBar.setValue(15);
					String clean = lc.getCleanLogsLink(logFileLink1.getText());
					String clean2 = lc.getCleanLogsLink(logFileLink2.getText());
					String url = lc.getLogsDownloadLink(clean);
					String url2 = lc.getLogsDownloadLink(clean2);
					try {
						File log1 = lc.getLogFile(url, clean);
						progressBar.setValue(25);
						File log2 = lc.getLogFile(url2, clean2);
						progressBar.setValue(50);
						File combined = lc.getCombinedFiles(log1, log2, combinedName.getText());
						progressBar.setValue(75);
						String response = lc.sendLog(combinedName.getText(), mapNameText.getText(), combined);
						if(response.contains("false")){
							finalLogLink.setText("Error uploading log");
						}else{
							finalLogLink.setText(response);
						}
						progressBar.setValue(100);
						
					} catch (IOException e) {
						JOptionPane.showMessageDialog(frmLogFileCombiner, "IO Excpetion when reading file.\n" + e.getMessage());
					} catch (ZipException e) {
						JOptionPane.showMessageDialog(frmLogFileCombiner, "ZIP Excpetion when reading file.\n" + e.getMessage());
					}
					
				}else{
					finalLogLink.setText("Can not send logs, Fill out all fields");
				}
			}
		});
		
		lblMapName = new JLabel("Map name");
		frmLogFileCombiner.getContentPane().add(lblMapName, "cell 0 3,alignx trailing");
		
		mapNameText = new JTextField();
		frmLogFileCombiner.getContentPane().add(mapNameText, "cell 1 3 2 1,growx");
		mapNameText.setColumns(10);
		frmLogFileCombiner.getContentPane().add(btnCombine, "cell 0 4");
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		frmLogFileCombiner.getContentPane().add(progressBar, "cell 1 4");
		
		lblLink = new JLabel("Link to log");
		frmLogFileCombiner.getContentPane().add(lblLink, "cell 0 5,alignx trailing");
		
		finalLogLink = new JTextField();
		finalLogLink.setEditable(false);
		frmLogFileCombiner.getContentPane().add(finalLogLink, "cell 1 5,growx");
		finalLogLink.setColumns(10);
		
		btnCopy = new JButton("Copy");
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				StringSelection stringSelection = new StringSelection(finalLogLink.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
			}
		});
		frmLogFileCombiner.getContentPane().add(btnCopy, "cell 2 5");
		
	}

}
