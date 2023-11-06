package org.productivity.java.syslog4j.server.impl.event.printstream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 
 * @author wli
 *
 * History
 * =======
 * 13.09.2017 WLI ORC-228 Create rolling log files for Syslog Server
 */
public class FileSyslogServerEventHandler extends PrintStreamSyslogServerEventHandler {
	
	private static final long serialVersionUID = -755824686809731430L;

	private final String baseName;
	private final long maxLen;
	private final int fileCnt;
	
	private int currentFileIdx;
	private long currentLen;
	
	private PrintStream createPrintStream(boolean append) throws IOException {
		
		if (fileCnt == 1) {
			File file = new File(baseName);
			OutputStream os = new FileOutputStream(file,append);
			return new PrintStream(os);

		}
		
		int nextFileIndex = currentFileIdx + 1;
		
		int pIdx = baseName.lastIndexOf('.');
		String fileName;
		if (pIdx > 0) {
			String prefix = baseName.substring(0, pIdx);
			String suffix = baseName.substring(pIdx);
			fileName = prefix + nextFileIndex + suffix;
		}
		else {
			fileName = baseName + nextFileIndex;
		}
		
		File file = new File(fileName);
		currentLen = file.length();
		OutputStream os = new FileOutputStream(file,append);
		
		PrintStream printStream = new PrintStream(os);
		
		currentFileIdx = (nextFileIndex == fileCnt) ? 0 : nextFileIndex;
		
		return printStream;
	}

	@Override
	protected void println(String text) {
		
		if (fileCnt > 1) {
			int tlen = text.length() + 1;
			
			if (currentLen + tlen > maxLen) { // ORC-228
				try {
					PrintStream ps = createPrintStream(false);
					super.stream.close();
					super.stream = ps;
					currentLen = 0;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			currentLen += tlen;
		}
		
		super.println(text);
	}
	
	public FileSyslogServerEventHandler(String fileName, boolean append, long maxLen, int fileCnt) throws IOException {
		this.baseName = fileName;
		this.maxLen = maxLen;
		this.fileCnt = fileCnt;
		
		super.stream = createPrintStream(append);
	}
}
