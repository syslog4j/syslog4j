package org.productivity.java.syslog4j.test.misc;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogMessageProcessorIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.message.processor.SyslogMessageProcessor;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.impl.net.udp.UDPNetSyslogServerConfig;

public class SyslogCreateAndDestroyTest extends TestCase {
	public static class FakeMessageProcessor implements SyslogMessageProcessorIF {
		private static final long serialVersionUID = -2118511880575127720L;

		public byte[] createPacketData(byte[] header, byte[] message, int start, int length) {
			return null;
		}

		public byte[] createPacketData(byte[] header, byte[] message, int start, int length, byte[] splitBeginText, byte[] splitEndText) {
			return null;
		}

		public String createSyslogHeader(int facility, int level, String localName, boolean sendLocalTimestamp, boolean sendLocalName) {
			return null;
		}
	}
	
	public void testCreateAndDestroyByProtocol() {
		UDPNetSyslogConfig config = new UDPNetSyslogConfig();
		config.setPort(999);
		
		SyslogIF syslog = Syslog.createInstance("udpToDestroy",config);

		assertEquals(SyslogMessageProcessor.getDefault(),syslog.getMessageProcessor());		
		SyslogMessageProcessorIF messageProcessor = new FakeMessageProcessor();
		syslog.setMessageProcessor(messageProcessor);
		assertEquals(messageProcessor,syslog.getMessageProcessor());	
		
		SyslogIF matchSyslog = Syslog.getInstance("udpToDestroy");
		
		assertEquals(syslog,matchSyslog);
		
		Syslog.destroyInstance("");
		Syslog.destroyInstance("udpToDestroy");
		
		try {
			Syslog.getInstance("udpToDestroy");
			fail("udpToDestroy should not exist");
			
		} catch (SyslogRuntimeException sre) {
			//
		}
	}

	public void testCreateAndDestroyByInstance() {
		UDPNetSyslogConfig config = new UDPNetSyslogConfig();
		config.setPort(999);
		
		SyslogIF syslog = Syslog.createInstance("udpToDestroy",config);
		
		SyslogIF matchSyslog = Syslog.getInstance("udpToDestroy");
		
		assertEquals(syslog,matchSyslog);
		
		Syslog.destroyInstance((SyslogIF) null);
		Syslog.destroyInstance(syslog);
		
		try {
			Syslog.getInstance("udpToDestroy");
			fail("udpToDestroy should not exist");
			
		} catch (SyslogRuntimeException sre) {
			//
		}

		try {
			Syslog.destroyInstance(syslog);
			fail("udpToDestroy should not exist");
			
		} catch (SyslogRuntimeException sre) {
			//
		}
		
		assertFalse(Syslog.getSuppressRuntimeExceptions());
		Syslog.setSuppressRuntimeExceptions(true);
		assertTrue(Syslog.getSuppressRuntimeExceptions());

		assertNull(Syslog.getInstance("udpToDestroy"));

		Syslog.destroyInstance(syslog);

		Syslog.setSuppressRuntimeExceptions(false);
	}

	public void testCreateAndDestroyServerByProtocol() {
		UDPNetSyslogServerConfig config = new UDPNetSyslogServerConfig();
		config.setPort(999);
		
		SyslogServerIF server = SyslogServer.createInstance("udpToDestroy",config);
		
		SyslogServerIF matchServer = SyslogServer.getInstance("udpToDestroy");
		
		assertEquals(server,matchServer);
		
		SyslogServer.destroyInstance("");
		SyslogServer.destroyInstance("udpToDestroy");
		
		try {
			SyslogServer.getInstance("udpToDestroy");
			fail("udpToDestroy should not exist");
			
		} catch (SyslogRuntimeException sre) {
			//
		}
		
		assertFalse(SyslogServer.getSuppressRuntimeExceptions());
		SyslogServer.setSuppressRuntimeExceptions(true);
		assertTrue(SyslogServer.getSuppressRuntimeExceptions());

		assertNull(SyslogServer.getInstance("udpToDestroy"));

		SyslogServer.setSuppressRuntimeExceptions(false);
	}

	public void testCreateAndDestroyServerByInstance() {
		UDPNetSyslogServerConfig config = new UDPNetSyslogServerConfig();
		config.setPort(999);
		
		SyslogServerIF server = SyslogServer.createInstance("udpToDestroy",config);
		
		SyslogServerIF matchServer = SyslogServer.getInstance("udpToDestroy");
		
		assertEquals(server,matchServer);
		
		SyslogServer.destroyInstance((SyslogServerIF) null);
		SyslogServer.destroyInstance(server);
		
		try {
			SyslogServer.getInstance("udpToDestroy");
			fail("udpToDestroy should not exist");
			
		} catch (SyslogRuntimeException sre) {
			//
		}

		try {
			SyslogServer.destroyInstance(server);
			fail("udpToDestroy should not exist");
			
		} catch (SyslogRuntimeException sre) {
			//
		}
		
		SyslogServer.setSuppressRuntimeExceptions(true);

		assertNull(SyslogServer.getInstance("udpToDestroy"));
		
		SyslogServer.destroyInstance(server);

		SyslogServer.setSuppressRuntimeExceptions(false);
}	
}
