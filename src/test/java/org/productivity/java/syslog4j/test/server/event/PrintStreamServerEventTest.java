package org.productivity.java.syslog4j.test.server.event;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.SyslogServerEvent;
import org.productivity.java.syslog4j.server.impl.event.printstream.FileSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.event.printstream.PrintStreamSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.event.printstream.SystemErrSyslogServerEventHandler;
import org.productivity.java.syslog4j.server.impl.event.printstream.SystemOutSyslogServerEventHandler;

public class PrintStreamServerEventTest extends TestCase {
	public void testPrintStreamEvent() {
		SyslogServerIF server = SyslogServer.getInstance("udp");
		
		String message = "test message";
		
		InetAddress inetAddress = null;
		
		try { inetAddress = InetAddress.getLocalHost(); } catch (UnknownHostException uhe) { }
		
		SyslogServerEventIF event = new SyslogServerEvent(message.getBytes(),message.length(),inetAddress);

		assertEquals(SyslogConstants.CHAR_SET_DEFAULT,event.getCharSet());
		event.setCharSet("xxyyzz");
		assertEquals("xxyyzz",event.getCharSet());
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		
		SyslogServerSessionEventHandlerIF eventHandler = new PrintStreamSyslogServerEventHandler(ps);
		eventHandler.sessionOpened(server,null);
		eventHandler.event(null,server,null,event);
		eventHandler.exception(null,server,null,null);
		eventHandler.sessionClosed(null,server,null,false);
		assertEquals(event.getMessage(),new String("test message"));

		Date date = new Date();
		event.setDate(date);
		assertTrue(date == event.getDate());
		
		event.setFacility(SyslogConstants.FACILITY_LOCAL0);
		assertEquals(SyslogConstants.FACILITY_LOCAL0,event.getFacility());
		
		event.setHost("foo");
		assertEquals("foo",event.getHost());
		
		event.setLevel(SyslogConstants.LEVEL_DEBUG);
		assertEquals(SyslogConstants.LEVEL_DEBUG,event.getLevel());
		
		event.setMessage(message);
		assertEquals(message,event.getMessage());
		
		eventHandler = SystemOutSyslogServerEventHandler.create();
		eventHandler.event(null,server,null,event);

		eventHandler = new SystemErrSyslogServerEventHandler();
		eventHandler.event(null,server,null,event);
		
		try {
			File f = File.createTempFile("syslog4j-test",".txt");
			
			long fileLen = 10 * 1024; // 10 KB
			eventHandler = new FileSyslogServerEventHandler(f.getPath(), false, fileLen, 2);
			eventHandler.event(null,server,null,event);

			eventHandler = new FileSyslogServerEventHandler(f.getPath(), true, fileLen, 2);
			eventHandler.event(null,server,null,event);

		} catch (Exception e) {
			fail(e.toString());
		}
	}
}
