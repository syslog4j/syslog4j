package org.productivity.java.syslog4j.test.net;

import java.net.SocketAddress;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogMain;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class CommandLineNetSyslog4jTest extends TestCase {
	public static class CaptureHandler implements SyslogServerSessionEventHandlerIF {
		private static final long serialVersionUID = -432500986007750320L;
		
		public SyslogServerEventIF capturedEvent = null;

		public void destroy(SyslogServerIF syslogServer) {
			// TODO Auto-generated method stub
			
		}

		public Object sessionOpened(SyslogServerIF syslogServer, SocketAddress socketAddress) {
			return null;
		}

		public void event(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, SyslogServerEventIF event) {
			this.capturedEvent = event;
		}

		public void exception(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, Exception exception) {
			exception.printStackTrace();
			fail(exception.getMessage());
		}

		public void sessionClosed(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, boolean timeout) {
			//
		}

		public void initialize(SyslogServerIF syslogServer) {
			// TODO Auto-generated method stub
			
		}
	}

/*
WL: Originally testUDP called testSendReceive("udp",false)
	and testTCP called testSendReceive("tcp",true);
	If testUDP was executed after testTCP it failed because in testTCP Syslog.shutdown was called.
	Now we call both, testTCP and testUDP with Parameter true causing a Syslog.shutdown in the test.
	Therefore we call Syslog.initialize(); in setUp.
	Because actually Syslog.initialize() is called in the initialization code of the class Syslog
	We first have to call shutdown; otherwise a RuntimeException is thrown. 
	Alternatively we can check if the Syslog is shut down and reinitialize it.
 */
	
	public void setUp() {
//		Syslog.shutdown();
		if (Syslog.isShutdown())
			Syslog.initialize();
	}

	public void testUDP() throws Exception {
		// WL: Originally this test set the second parameter (useSyslogClass) to false.
		testSendReceive("udp",true);
	}
	
	public void testTCP() throws Exception {
		testSendReceive("tcp",true);
	}
	
	public void testSendReceive(String protocol, boolean useSyslogClass) throws Exception {
		SyslogServer.getInstance(protocol).getConfig().setPort(1514);
		SyslogServerIF syslogServer = SyslogServer.getThreadedInstance(protocol);
		
		SyslogUtility.sleep(100);
		
		CaptureHandler captureHandler = new CaptureHandler();
		syslogServer.getConfig().addEventHandler(captureHandler);
		
		String message = "test message";
		
		if (useSyslogClass) {
			Syslog.main(new String[] { "-p", "1514", protocol, message });
			
		} else {
			SyslogMain.main(new String[] { "-p", "1514", protocol, message }, false);
		}
		
		SyslogUtility.sleep(250);
		assertNotNull(captureHandler.capturedEvent);
		assertTrue(captureHandler.capturedEvent.getMessage().endsWith(message));
		
		syslogServer.shutdown();

		SyslogUtility.sleep(100);
	}
}
