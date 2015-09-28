package org.productivity.java.syslog4j.test.log4j.base;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.net.AbstractNetSyslogServerConfig;
import org.productivity.java.syslog4j.test.base.AbstractBaseTest;
import org.productivity.java.syslog4j.util.SyslogUtility;

public abstract class AbstractLog4jSyslog4jTest extends AbstractBaseTest {
	protected class RecorderHandler implements SyslogServerSessionEventHandlerIF {
		private static final long serialVersionUID = 8040266564168724L;
		
		protected List recordedEvents = new ArrayList();
		
		public List getRecordedEvents() {
			return this.recordedEvents;
		}
		
		public void initialize(SyslogServerIF syslogServer) {
			//
		}

		public Object sessionOpened(SyslogServerIF syslogServer, SocketAddress socketAddress) {
			return null;
		}

		public void event(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, SyslogServerEventIF event) {
			String recordedEvent = SyslogUtility.newString(syslogServer.getConfig(),event.getRaw());
			
			recordedEvent = recordedEvent.substring(recordedEvent.toUpperCase().indexOf("[TEST] "));

			this.recordedEvents.add(recordedEvent);
		}

		public void exception(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, Exception exception) {
			fail(exception.getMessage());
		}

		public void sessionClosed(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, boolean timeout) {
			//
		}

		public void destroy(SyslogServerIF syslogServer) {
			//
		}
	}
	
	public static final int TEST_PORT = 10514;

	protected SyslogServerIF server = null;
	
	protected abstract String getServerProtocol();
	
	protected abstract int getMessageCount();

	protected RecorderHandler recorderEventHandler = new RecorderHandler();
	
	protected void startServerThread(String protocol) {
		this.server = SyslogServer.getInstance(protocol);
		
		AbstractNetSyslogServerConfig config = (AbstractNetSyslogServerConfig) this.server.getConfig();
		config.setPort(TEST_PORT);
		config.addEventHandler(this.recorderEventHandler);

		this.server = SyslogServer.getThreadedInstance(protocol);
	}

	public void setUp() {
		UDPNetSyslogConfig config = new UDPNetSyslogConfig();
		
		assertTrue(config.isCacheHostAddress());
		config.setCacheHostAddress(false);
		assertFalse(config.isCacheHostAddress());
		
		assertTrue(config.isThrowExceptionOnInitialize());
		config.setThrowExceptionOnInitialize(false);
		assertFalse(config.isThrowExceptionOnInitialize());
		
		assertFalse(config.isThrowExceptionOnWrite());
		config.setThrowExceptionOnWrite(true);
		assertTrue(config.isThrowExceptionOnWrite());
		
		Syslog.createInstance("log4jUdp",config);
		
		String protocol = getServerProtocol();
		
		startServerThread(protocol);
		SyslogUtility.sleep(100);
	}
	
	protected void verifySendReceive(List events, boolean sort) {
		if (sort) {
			Collections.sort(events);
		}
		
		List recordedEvents = this.recorderEventHandler.getRecordedEvents();
		
		if (sort) {
			Collections.sort(recordedEvents);
		}
		
		for(int i=0; i < events.size(); i++) {
			String sentEvent = (String) events.get(i);
			
			String recordedEvent = (String) recordedEvents.get(i);
			
			if (!sentEvent.equals(recordedEvent)) {
				System.out.println("SENT: " + sentEvent);
				System.out.println("RCVD: " + recordedEvent);
				
				fail("Sent and recorded events do not match");
			}
		}
	}
	
	public void _testSendReceive(){
		Logger logger = Logger.getLogger(this.getClass());
		
		List events = new ArrayList();
		
		for(int i=0; i<getMessageCount(); i++) {
			String message = "[TEST] " + i + " / " + System.currentTimeMillis();
			
			logger.info(message);
			events.add(message);
		}
		
		SyslogUtility.sleep(100);
		
		verifySendReceive(events,true);
	}
	
	public void tearDown() {
		Syslog.shutdown();

		SyslogUtility.sleep(100);
		
		SyslogServer.shutdown();
		
		SyslogUtility.sleep(100);

		Syslog.initialize();
		SyslogServer.initialize();
	}
}
