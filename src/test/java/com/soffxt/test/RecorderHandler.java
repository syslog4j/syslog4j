package com.soffxt.test;

import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.structured.StructuredSyslogServerEvent;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class RecorderHandler implements SyslogServerSessionEventHandlerIF {
	private static final long serialVersionUID = 7364480542656523264L;
	
	protected List recordedEvents = new LinkedList();
	
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
		if (event instanceof StructuredSyslogServerEvent) {
			String msg = event.getMessage();
			System.out.println("record: " + msg); // WL
			int prefixLen = "Syslog4jTest: ".length();
			msg = msg.substring(prefixLen);
			this.recordedEvents.add(msg);
			
		} else {
			String recordedEvent = SyslogUtility.newString(syslogServer.getConfig(),event.getRaw());
			System.out.println("recorded: " + recordedEvent); // WL
			synchronized(this.recordedEvents) {
				this.recordedEvents.add(recordedEvent);
			}
		}
	}

	public void exception(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, Exception exception) {
		
	}

	public void sessionClosed(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, boolean timeout) {
		//
	}

	public void destroy(SyslogServerIF syslogServer) {
		//
	}
}
