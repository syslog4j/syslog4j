package org.productivity.java.syslog4j.test.net.base;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.message.pci.PCISyslogMessage;
import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;
import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessageIF;
import org.productivity.java.syslog4j.impl.multiple.MultipleSyslog;
import org.productivity.java.syslog4j.impl.multiple.MultipleSyslogConfig;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.structured.StructuredSyslogServerEvent;
import org.productivity.java.syslog4j.server.impl.net.AbstractNetSyslogServerConfig;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.test.base.AbstractBaseTest;
import org.productivity.java.syslog4j.util.SyslogUtility;

/**
 * History
 * =======
 * 03.05.2017 WLI LC-001 In case of UDP some messages might get lost.
 */
public abstract class AbstractNetSyslog4jTest extends AbstractBaseTest {
	protected static String APP_ID = "Syslog4jTest";
	
	public static class ClientThread implements Runnable {
		protected SyslogIF syslog = null;
		protected List<String> messages = null;
		public static int active = 0;
		
		protected synchronized void incrementActive() {
			active++;
		}

		protected synchronized void decrementActive() {
			active--;
		}
		
		public ClientThread(SyslogIF syslog, List<String> messages) {
			this.syslog = syslog;
			this.messages = messages;
		}
		
		public void run() {
			incrementActive();
			for(int i=0; i<this.messages.size(); i++) {
				String message = this.messages.get(i);
				try {
					this.syslog.info(message);
					
				} catch (SyslogRuntimeException sre) {
					System.err.println(sre);
				}
			}
			decrementActive();
		}
	}

	protected class RecorderHandler implements SyslogServerSessionEventHandlerIF {
		private static final long serialVersionUID = 7364480542656523264L;
		
		protected List<String> recordedEvents = new LinkedList<String>();
		
		public List<String> getRecordedEvents() {
			return this.recordedEvents;
		}

		public void initialize(SyslogServerIF syslogServer) {
			//
		}

		public Object sessionOpened(SyslogServerIF syslogServer, SocketAddress socketAddress) {
			return null;
		}

		public void event(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, SyslogServerEventIF event) {
			if (event instanceof StructuredSyslogServerEvent) {
				String msg = event.getMessage();
//				System.out.println("record: " + msg); // WL
				int prefixLen = "Syslog4jTest: ".length();
				msg = msg.substring(prefixLen);
				this.recordedEvents.add(msg);
				
			} else {
				String recordedEvent = SyslogUtility.newString(syslogServer.getConfig(),event.getRaw());
//				System.out.println("recorded: " + recordedEvent); // WL
				
				recordedEvent = recordedEvent.substring(recordedEvent.toUpperCase().indexOf("[TEST]"));

				synchronized(this.recordedEvents) {
//					System.out.println("record: " + recordedEvent); // WL
					this.recordedEvents.add(recordedEvent);
				}
			}
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
	
	protected abstract String getClientProtocol();
	protected abstract String getServerProtocol();
	
	protected abstract int getMessageCount();

	protected RecorderHandler recorderEventHandler = new RecorderHandler();
	
	protected SyslogIF getSyslog(String protocol) {
		if (!Syslog.exists(protocol)) {
			fail("Protocol \"" + protocol + "\" does not exist");
		}
		
		SyslogIF syslog = Syslog.getInstance(protocol);
		
		if (!(syslog instanceof MultipleSyslog)) {
			syslog.getConfig().setIdent(APP_ID);
		}
		
		if (!(syslog.getConfig() instanceof MultipleSyslogConfig)) {
			syslog.getConfig().setPort(TEST_PORT);
		}
		
		return syslog;
	}
	
	protected boolean isSyslogServerTcpBacklog() {
		return false;
	}

	protected void startServerThread(String protocol) {
		assertTrue(SyslogServer.exists(protocol));
		
		this.server = SyslogServer.getInstance(protocol);
		
		if (isSyslogServerTcpBacklog() && this.server.getConfig() instanceof TCPNetSyslogServerConfig) {
			((TCPNetSyslogServerConfig) this.server.getConfig()).setBacklog(0);
		}
		
		AbstractNetSyslogServerConfig config = (AbstractNetSyslogServerConfig) this.server.getConfig();
		config.setPort(TEST_PORT);
		config.addEventHandler(this.recorderEventHandler);

		if (this.server.getThread() == null) {
			Thread t = new Thread(this.server);
			t.setName("SyslogServer: " + protocol);
			t.start();
			
			this.server.setThread(t);
			
			assertEquals(t,this.server.getThread());
		}
	}

	public void setUp() {
		
		String protocol = getServerProtocol();
		
		startServerThread(protocol);
		SyslogUtility.sleep(100);
	}

	protected void verifySendReceive(List<String> events, boolean sortEvents, boolean sortRecordedEvents) {
		verifySendReceive(events,sortEvents,sortRecordedEvents,100);
	}

	protected void verifySendReceive(List<String> events, boolean sortEvents, boolean sortRecordedEvents, int threads) {
		boolean done = false;
		
		long start = System.currentTimeMillis();
		
		while(!done) {
			int eventCount = events.size();
			int recordedEventCount = this.recorderEventHandler.recordedEvents.size(); 
			
			int perc = (int) (((double) recordedEventCount / (double) eventCount) * 100000) / 1000;
			
			String detail; // = "Count: " + perc + "% " + recordedEventCount + "/" + eventCount + " : " + ClientThread.active;
			
			if (eventCount > recordedEventCount) {
				detail = "count: " + perc + "% " + recordedEventCount + "/" + eventCount + " : " + ClientThread.active;
				System.out.println(detail);

			} else if (eventCount < recordedEventCount) {
				detail = "Problem - Sent Events: " + eventCount + " Recorded Events: " + recordedEventCount;
				
				System.err.println(detail);

				fail(detail);
				
			} else {
				detail = "Count: " + perc + "% " + recordedEventCount + "/" + eventCount + " : " + ClientThread.active;
				System.out.println(detail);
				
				done = true;
			}
			
			if (!done) {
				long now = System.currentTimeMillis();
				
				if (now - start > 100 * threads) { // LC-001 100 statt 2000
					
					done = true;
					
					if (!"udp".equals(getClientProtocol()) || perc < 10) { // LC-001
						detail = "Problem: " + eventCount + " " + recordedEventCount;
						
						System.err.println(detail);
						
						fail(detail);
					}
				}
			}
			
			SyslogUtility.sleep(200);
		}
		
		if (sortEvents) {
			Collections.sort(events);
		}
		
		List<String> recordedEvents = this.recorderEventHandler.getRecordedEvents();
		if ("udp".equals(getClientProtocol())) { // LC-001
			int eventCount = events.size();
			int recordedEventCount = recordedEvents.size(); 
			double ratio = ((double)recordedEventCount) / ((double)eventCount);
			assertTrue(ratio > 0.1);
		}
		else {
			if (sortRecordedEvents) {
				Collections.sort(recordedEvents);
			}
			
			for(int i=0; i < events.size(); i++) {
				String sentEvent = events.get(i);
				
				String recordedEvent = recordedEvents.get(i);
				
				if (!sentEvent.equals(recordedEvent)) {
					System.out.println("SENT: " + sentEvent);
					System.out.println("RCVD: " + recordedEvent);
					
					fail("Sent and recorded events do not match");
				}
			}
		}
	}

	public void _testSendReceive(boolean sortEvents, boolean sortRecordedEvents){
		List<String> events = new ArrayList<String>();
		
		String protocol = getClientProtocol();
		
		SyslogIF syslog = getSyslog(protocol);
		
		for(int i=0; i<getMessageCount(); i++) {
			String message = "[TEST] " + i + " / " + System.currentTimeMillis();
			
			syslog.info(message);
			events.add(message);
		}
		
		SyslogUtility.sleep(200);
		
		syslog.flush();
		
		verifySendReceive(events,sortEvents,sortRecordedEvents);
	}

	public void _testSendReceivePCIMessages(boolean sortEvents, boolean sortRecordedEvents){
		List<String> events = new ArrayList<String>();
		
		String protocol = getClientProtocol();
		
		SyslogIF syslog = getSyslog(protocol);

		PCISyslogMessage message = new PCISyslogMessage();
		message.setUserId("[TEST]");
		
		syslog.debug(message);
		events.add(message.createMessage());
		
		syslog.info(message);
		events.add(message.createMessage());
		
		syslog.notice(message);
		events.add(message.createMessage());
		
		syslog.warn(message);
		events.add(message.createMessage());
		
		syslog.error(message);
		events.add(message.createMessage());
		
		syslog.critical(message);
		events.add(message.createMessage());

		syslog.alert(message);
		events.add(message.createMessage());

		syslog.emergency(message);
		events.add(message.createMessage());

		syslog.log(SyslogConstants.LEVEL_INFO,message);
		events.add(message.createMessage());

		SyslogUtility.sleep(100);
		
		syslog.flush();
		
		verifySendReceive(events,sortEvents,sortRecordedEvents);
	}

	public void _testSendReceiveStructuredMessages(boolean sortEvents, boolean sortRecordedEvents){
		List<String> events = new ArrayList<String>();
		
		String protocol = getClientProtocol();
		
		SyslogIF syslog = getSyslog(protocol); 
		syslog.setMessageProcessor(syslog.getStructuredMessageProcessor()); // WL
		
		this.server.getConfig().setUseStructuredData(true);

		Map<String, String> m2 = new HashMap<String, String>();
		m2.put("foo","bar");

		Map<String, Map<String, String>> m1 = new HashMap<String, Map<String, String>>();
		m1.put("testa",m2);
		
		StructuredSyslogMessageIF message = new StructuredSyslogMessage("procid", "[TEST]",m1,"testb");
		
		syslog.debug(message);
		events.add(message.createMessage());
		
		syslog.info(message);
		events.add(message.createMessage());
		
		syslog.notice(message);
		events.add(message.createMessage());
		
		syslog.warn(message);
		events.add(message.createMessage());
		
		syslog.error(message);
		events.add(message.createMessage());
		
		syslog.critical(message);
		events.add(message.createMessage());

		syslog.alert(message);
		events.add(message.createMessage());

		syslog.emergency(message);
		events.add(message.createMessage());

		syslog.log(SyslogConstants.LEVEL_INFO,message);
		events.add(message.createMessage());

		syslog.log(SyslogConstants.LEVEL_INFO,message.createMessage());
		events.add(message.createMessage());

		SyslogUtility.sleep(100);
		
		syslog.flush();
		
		verifySendReceive(events,sortEvents,sortRecordedEvents);

		this.server.getConfig().setUseStructuredData(false);
	}

	public void _testThreadedSendReceive(int threads, boolean sortEvents, boolean sortRecordedEvents){
		_testThreadedSendReceive(threads,sortEvents,sortRecordedEvents,null);
	}
	
	public void _testThreadedSendReceive(int threads, boolean sortEvents, boolean sortRecordedEvents, List<String> backLogEvents){
		List<String> events = new ArrayList<String>();
		
		String protocol = getClientProtocol();
		
		SyslogIF syslog = getSyslog(protocol);

		for(int t=0; t<threads; t++) {
			List<String> messageList = new ArrayList<String>();
			
			for(int i=0; i<getMessageCount(); i++) {
				String message = "[TEST] " + t + " / " + i + " / " + System.currentTimeMillis();
				
				messageList.add(message);
				events.add(message);
			}
			
			Runnable r = new ClientThread(syslog,messageList);
			
			Thread thread = new Thread(r);
			thread.setName("Syslog: " + protocol + " [" + t + "]");
			
			thread.start();
		}

		SyslogUtility.sleep(1000);
		
		syslog.flush();
		
		if (backLogEvents != null) {
			List<String> recordedEvents = this.recorderEventHandler.getRecordedEvents();
			
			int haveCount = recordedEvents.size() + backLogEvents.size();
			
			long startTime = System.currentTimeMillis();
			
			while(haveCount < events.size()) {
				System.out.println("Count: " + haveCount + "/" + events.size());
				SyslogUtility.sleep(250);
				
				haveCount = recordedEvents.size() + backLogEvents.size();
				
				long currentTime = System.currentTimeMillis();
				
				if ((currentTime - startTime) > 5000) {
					break;
				}
			}
			
			System.out.println("Sent Events:     " + events.size());
			System.out.println("BackLog Events:  " + backLogEvents.size());
			System.out.println("Recorded Events: " + recordedEvents.size());
			
			if (backLogEvents.size() < 1) {
				fail("No backLog events received");
			}

			if (recordedEvents.size() < 1) {
				fail("No recorded events received");
			}

			if ((recordedEvents.size() + backLogEvents.size()) != events.size()) {
				fail("Lost some events");
			}
			
			recordedEvents.addAll(backLogEvents);
		}
		
		verifySendReceive(events,sortEvents,sortRecordedEvents);
	}

	public void tearDown() {
		System.out.print("Shutting down Syslog...");
		Syslog.shutdown();
		System.out.println("done.");

		SyslogUtility.sleep(100);
		
		System.out.print("Shutting down SyslogServer...");
		SyslogServer.shutdown();
		System.out.println("done.");
		
		SyslogUtility.sleep(100);

		Syslog.initialize();
		SyslogServer.initialize();
	}
}
