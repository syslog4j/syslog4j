package org.productivity.java.syslog4j.test.server.event;

import java.net.InetAddress;

import org.productivity.java.syslog4j.server.impl.event.SyslogServerEvent;

import junit.framework.TestCase;

public class ServerEventTest extends TestCase {
	public void testServerEvent() throws Exception {
		InetAddress inetAddress = InetAddress.getLocalHost();
		String hostName = inetAddress.getHostName();
		
		int i = hostName.indexOf('.');
		if (i > -1) {
			hostName = hostName.substring(0,i);
		}
		
		String baseMessage = "<1>Jan  1 00:00:00 ";
		
		byte[] message = (baseMessage + "test").getBytes();		
		SyslogServerEvent event = new SyslogServerEvent(message,message.length,inetAddress);
		assertNull(event.getHost());

		message = (baseMessage + hostName + " test").getBytes();
		event = new SyslogServerEvent(message,message.length,inetAddress);
		assertEquals(hostName,event.getHost());
		assertEquals("test",event.getMessage());
		assertTrue(event.isHostStrippedFromMessage());

		InetAddress mirrorInetAddress = InetAddress.getByName("mirror.productivity.org");
		String mirrorHostName = "mirror";
		
		message = (baseMessage + mirrorHostName + " test").getBytes();
		event = new SyslogServerEvent(message,message.length,mirrorInetAddress);
		assertEquals(mirrorHostName,event.getHost());
		assertEquals("test",event.getMessage());
		assertTrue(event.isHostStrippedFromMessage());
		
		String alteredHostName = hostName + "1";

		message = (baseMessage + alteredHostName + " test").getBytes();
		event = new SyslogServerEvent(message,message.length,inetAddress);
		assertEquals(hostName,event.getHost());
		assertEquals(alteredHostName + " test",event.getMessage());
		assertFalse(event.isHostStrippedFromMessage());
		
		String hostAddress = InetAddress.getLocalHost().getHostAddress();

		message = (baseMessage + hostAddress + " test").getBytes();
		event = new SyslogServerEvent(message,message.length,inetAddress);
		assertEquals(hostAddress,event.getHost());
		assertEquals("test",event.getMessage());
		assertTrue(event.isHostStrippedFromMessage());

		baseMessage = "<1>Xan  1 00:00:00 ";
		
		message = (baseMessage + "test").getBytes();		
		new SyslogServerEvent(message,message.length,inetAddress);

		baseMessage = "<x>Jan  1 00:00:00 ";
		
		message = (baseMessage + "test").getBytes();		
		new SyslogServerEvent(message,message.length,inetAddress);
	}
}
