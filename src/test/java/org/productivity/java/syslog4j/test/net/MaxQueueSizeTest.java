package org.productivity.java.syslog4j.test.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogBackLogHandlerIF;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.backlog.NullSyslogBackLogHandler;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfigIF;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class MaxQueueSizeTest extends TestCase {
	public static class BackLogCounter implements SyslogBackLogHandlerIF {
		public int count = 0;
		
		public void down(SyslogIF syslog, String reason) {
			//
		}

		public void initialize() throws SyslogRuntimeException {
			count = 0;
		}

		public void log(SyslogIF syslog, int level, String message, String reason) throws SyslogRuntimeException {
			System.out.println(message + " " + reason);
			count++;
		}

		public void up(SyslogIF syslog) {
			//
		}
	}
	
	public static class FakeSyslogServer implements Runnable {
		public int port = 0;
		public int catchCount = 0;
		
		public boolean shutdown = false;
		public int count = 0;
		
		public FakeSyslogServer(int port, int catchCount) {
			this.port = port;
			this.catchCount = catchCount;
		}
		
		public void run() {
			ServerSocket serverSocket = null;
			
			ServerSocketFactory factory = ServerSocketFactory.getDefault();
			
			try {
				serverSocket = factory.createServerSocket(this.port);
				
				Socket socket = serverSocket.accept();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				for(int i=0; i<catchCount; i++) {
					String line = br.readLine();
					
					System.out.println("Received: " + line);
					count++;
				}
				
				while(!shutdown) {
					SyslogUtility.sleep(10);
				}
				
				String line = br.readLine();
				while(line != null) {
					System.out.println("Received: " + line);
					count++;
				
					line = br.readLine();
				}
				
				socket.close();
				
				serverSocket.close();
				
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}		
	}
	
	public void testMaxQueueSize() {
		int catchCount = 5;
		int maxQueueSize = 5;
		int messagesToSend = 15;
		int port = 7777;
		
		FakeSyslogServer server = new FakeSyslogServer(port,catchCount);
		Thread thread = new Thread(server);
		thread.start();
		
		BackLogCounter counter = new BackLogCounter();
		
		TCPNetSyslogConfigIF syslogConfig = new TCPNetSyslogConfig();
		syslogConfig.setPort(port);
		assertEquals(syslogConfig.getMaxQueueSize(),SyslogConstants.MAX_QUEUE_SIZE_DEFAULT);
		syslogConfig.setMaxQueueSize(maxQueueSize);
		syslogConfig.addBackLogHandler(counter);
		syslogConfig.addBackLogHandler(NullSyslogBackLogHandler.INSTANCE);
		
		SyslogIF syslog = Syslog.createInstance("maxQueueSizeTest",syslogConfig);
		
		for(int i=1; i<=messagesToSend; i++) {
			syslog.log(SyslogConstants.LEVEL_INFO,"test line " + i);
		}
		
		SyslogUtility.sleep(500);
		
		server.shutdown = true;

		SyslogUtility.sleep(500);

		System.out.println("Sent Messages:       " + messagesToSend);	
		System.out.println("Received Messages:   " + server.count);
		System.out.println("Backlogged Messages: " + counter.count);
		
		assertEquals(messagesToSend,(server.count+counter.count));
	}
}
