package org.productivity.java.syslog4j.test.net;

import java.net.SocketAddress;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfigIF;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfigIF;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class FreshConnectionIntervalTest extends TestCase {
	public class SocketCounter implements SyslogServerSessionEventHandlerIF {
		private static final long serialVersionUID = 7166226890012336710L;
		
		public int openCounter = 0;
		public int eventCounter = 0;
		public int closeCounter = 0;

		public void initialize(SyslogServerIF syslogServer) {
			//
		}
		
		public Object sessionOpened(SyslogServerIF syslogServer, SocketAddress socketAddress) {
			openCounter++;
			return null;
		}
		
		public void event(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, SyslogServerEventIF event) {
			eventCounter++;
			System.out.println(openCounter + "/" + eventCounter + "/" + closeCounter + " " + event.getMessage() + " " + (event.isHostStrippedFromMessage() ? "host_stripped" : "host_not_stripped"));
		}

		public void exception(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, Exception exception) {
			//
		}

		public void sessionClosed(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, boolean timeout) {
			closeCounter++;
		}

		public void destroy(SyslogServerIF syslogServer) {
			//			
		}
	}
	
	public void testFreshConnectionInterval() {
		TCPNetSyslogServerConfigIF serverConfig = new TCPNetSyslogServerConfig();
		serverConfig.setPort(8888);
		
		SocketCounter counter = new SocketCounter();
		serverConfig.addEventHandler(counter);
		
		SyslogServerIF server = SyslogServer.createThreadedInstance("tcp_8888",serverConfig);

		SyslogUtility.sleep(100);

		TCPNetSyslogConfigIF config = new TCPNetSyslogConfig();
		config.setPort(8888);
		config.setFreshConnectionInterval(300);
		
		SyslogIF syslog = Syslog.createInstance("tcp_8888",config);

		for(int i=0; i<10; i++) {
			syslog.info("message " + i);
			SyslogUtility.sleep(100);
		}
		
		SyslogUtility.sleep(100);
		
		Syslog.destroyInstance(syslog);
		SyslogUtility.sleep(100);
		SyslogServer.destroyInstance(server);


		assertEquals("OpenCounter",3,counter.openCounter);
		assertEquals("EventCounter",10,counter.eventCounter);
		assertEquals("CloseCounter",3,counter.closeCounter);
	}
}
