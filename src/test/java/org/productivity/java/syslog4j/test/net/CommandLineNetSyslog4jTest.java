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
			fail(exception.getMessage());
		}

		public void sessionClosed(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, boolean timeout) {
			//
		}

		public void initialize(SyslogServerIF syslogServer) {
			// TODO Auto-generated method stub
			
		}
	}

	public void testUDP() {
		testSendReceive("udp",false);
	}
	
	public void testTCP() {
		testSendReceive("tcp",false);
	}
	
	public void testSendReceive(String protocol, boolean useSyslogClass) {
		SyslogServer.getInstance(protocol).getConfig().setPort(1514);
		SyslogServerIF syslogServer = SyslogServer.getThreadedInstance(protocol);
		
		SyslogUtility.sleep(100);
		
		CaptureHandler captureHandler = new CaptureHandler();
		syslogServer.getConfig().addEventHandler(captureHandler);
		
		String message = "test message";
		
		try {
			if (useSyslogClass) {
				Syslog.main(new String[] { "-p", "1514", protocol, message });
				
			} else {
				SyslogMain.main(new String[] { "-p", "1514", protocol, message }, false);
			}
			
		} catch (Exception e) {
			//
		}
		
		SyslogUtility.sleep(250);
		
		assertTrue(captureHandler.capturedEvent.getMessage().endsWith(message));
		
		syslogServer.shutdown();

		SyslogUtility.sleep(100);
	}
}
