package org.productivity.java.syslog4j.test.net;

import java.net.SocketAddress;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.message.processor.SyslogMessageProcessor;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfigIF;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.impl.event.printstream.SystemOutSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfigIF;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;
import org.productivity.java.syslog4j.util.SyslogUtility;

/**
 * 
 * @author wli
 *
 * History
 * =======
 * 03.05.2017 WLI LC-001 Other UnitTests call Syslog.shutdown().
 *                Because shutdown is a static method this Unit test failed if other tests where invoked!
 *                Actually this is a bad pattern in the Syslog4j library.
 *                To cope with this i call Syslog.shutdown(); and Syslog.initialize(); in setUp
 */
public class TCPNetSyslog4jTest extends AbstractNetSyslog4jTest {
	protected static boolean ONCE = true;
	
	public static class TimeoutHandler extends SystemOutSyslogServerEventHandler {
		private static final long serialVersionUID = 2634851821468142915L;
		public SyslogServerEventIF lastEvent = null;

		public Object sessionOpened(SyslogServerIF syslogServer, SocketAddress socketAddress) {
			System.out.println("Open");
			
			return null;
		}
		
		public void exception(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, Exception exception) {
			System.out.println("Exception after " + lastEvent.getMessage());
		}

		public void event(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, SyslogServerEventIF event) {
			super.event(session, syslogServer, socketAddress, event);
			
			this.lastEvent = event;
		}

		public void sessionClosed(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, boolean timeout) {
			if (timeout) {
				System.out.println("Timeout after event: " + lastEvent.getMessage());
				
				assertTrue(lastEvent.getMessage().endsWith("yes"));
				
			} else {
				assertTrue(lastEvent.getMessage().endsWith("no"));				
			}
			
			System.out.println("Closed");
		}
	}
	
	public void setUp() {
		
//		LC-001: Because the order of execution is not defined
//			    this test run into an error if other tests called Syslog.shutdown(); 
		Syslog.shutdown();
		Syslog.initialize();

		if (ONCE) {
			ONCE = false;
			
		} else {
			SyslogServer.getInstance(getServerProtocol()).getConfig().setHost("127.0.0.1");
		}
	
		super.setUp();	
	}
	
	protected int getMessageCount() {
		return 100;
	}

	protected String getClientProtocol() {
		return "tcp";
	}

	protected String getServerProtocol() {
		return "tcp";
	}

	public void testSendReceive() {
		super._testSendReceive(true,true);
	}
	
	public void testThreadedSendReceive() {
		Syslog.getInstance("tcp").setMessageProcessor(SyslogMessageProcessor.getDefault());
		
		super._testThreadedSendReceive(50,true,true);
	}
	
	public void testStructuredMessage() {
		_testSendReceiveStructuredMessages(true,true);
	}
	
	public void testTimeout() {
		TCPNetSyslogServerConfigIF serverConfig = new TCPNetSyslogServerConfig();
		serverConfig.setTimeout(150);
		serverConfig.setPort(7777);
		serverConfig.addEventHandler(new TimeoutHandler());

		TCPNetSyslogConfigIF clientConfig = new TCPNetSyslogConfig();
		clientConfig.setPort(7777);
		clientConfig.setThreaded(false);

		SyslogServer.createThreadedInstance("tcp_to",serverConfig);
		SyslogUtility.sleep(250);
		
		SyslogIF syslog = Syslog.createInstance("tcp_to",clientConfig);
		SyslogUtility.sleep(250);
		
		int mark = 0;
		
		for(int i=1; i<=10; i++) {
			if (i > 5) {
				mark--;
				
			} else {
				mark++;
			}
			
			int timeout = 35 * mark + 25;
			
			syslog.info("Test " + i + " (" + timeout + "ms) Should Timeout: " + (timeout > serverConfig.getTimeout() ? "yes" : "no"));
			SyslogUtility.sleep(timeout);
		}
		
		syslog.flush();
		SyslogUtility.sleep(200);
		
		SyslogServer.destroyInstance("tcp_to");
		
		SyslogUtility.sleep(200);
		
		Syslog.destroyInstance("tcp_to");
	}
}
