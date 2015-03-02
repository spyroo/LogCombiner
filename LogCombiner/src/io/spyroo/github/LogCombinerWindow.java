package io.spyroo.github;

import java.awt.EventQueue;

import javax.swing.JFrame;

import net.lingala.zip4j.exception.ZipException;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class LogCombinerWindow {

	private JFrame frmLogFileCombiner;
	private JTextField logFileLink1;
	private JTextField logFileLink2;
	private JTextField destFile;
	private JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		frmLogFileCombiner.setBounds(100, 100, 457, 140);
		frmLogFileCombiner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLogFileCombiner.getContentPane().setLayout(new MigLayout("", "[][grow][63.00]", "[][][][]"));
		
		JLabel lblLogFileLink = new JLabel("Log File Link 1");
		frmLogFileCombiner.getContentPane().add(lblLogFileLink, "cell 0 0,alignx trailing");
		
		logFileLink1 = new JTextField();
		frmLogFileCombiner.getContentPane().add(logFileLink1, "cell 1 0,growx");
		logFileLink1.setColumns(10);
		
		JLabel lblLogFileLink_1 = new JLabel("Log File Link 2");
		frmLogFileCombiner.getContentPane().add(lblLogFileLink_1, "cell 0 1,alignx trailing");
		
		logFileLink2 = new JTextField();
		frmLogFileCombiner.getContentPane().add(logFileLink2, "cell 1 1,growx");
		logFileLink2.setColumns(10);
		
		JLabel lblDestinationFile = new JLabel("Destination File");
		frmLogFileCombiner.getContentPane().add(lblDestinationFile, "cell 0 2,alignx trailing");
		
		destFile = new JTextField();
		frmLogFileCombiner.getContentPane().add(destFile, "cell 1 2,growx");
		destFile.setColumns(10);
		
		JButton btnCombine = new JButton("Combine");
		btnCombine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				LogCombiner lc = new LogCombiner();
				
				String clean = lc.cleanLogsUrl(logFileLink1.getText());
				String clean2 = lc.cleanLogsUrl(logFileLink2.getText());
				String url = lc.getLogDownload(clean);
				String url2 = lc.getLogDownload(clean2);
				try {
					progressBar.setMaximum(100);
					File log1 = lc.getLogFile(url, clean);
					progressBar.setValue(33);
					File log2 = lc.getLogFile(url2, clean2);
					progressBar.setValue(66);
					File combined = lc.getCombinedFiles(log1, log2, destFile.getText());
					progressBar.setValue(100);
					
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frmLogFileCombiner, "IO Excpetion when reading file.\n" + e.getMessage());
				} catch (ZipException e) {
					JOptionPane.showMessageDialog(frmLogFileCombiner, "ZIP Excpetion when reading file.\n" + e.getMessage());
				}
				
			}
		});
		frmLogFileCombiner.getContentPane().add(btnCombine, "cell 0 3");
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		frmLogFileCombiner.getContentPane().add(progressBar, "cell 1 3");
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
				
			    int returnVal = jfc.showOpenDialog(frmLogFileCombiner);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       destFile.setText(jfc.getSelectedFile().getPath() + "\\combinedlog.log");
			    }
				
			}
		});
		frmLogFileCombiner.getContentPane().add(btnBrowse, "cell 2 2");
		
	}

}
