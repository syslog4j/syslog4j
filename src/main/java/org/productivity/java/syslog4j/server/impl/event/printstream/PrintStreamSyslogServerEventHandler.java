package org.productivity.java.syslog4j.server.impl.event.printstream;

import java.io.PrintStream;
import java.net.SocketAddress;
import java.util.Date;

import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.structured.StructuredSyslogServerEvent;
import org.productivity.java.syslog4j.util.SyslogUtility;

/**
* SystemOutSyslogServerEventHandler provides a simple example implementation
* of the SyslogServerEventHandlerIF which writes the events to System.out.
* 
* <p>Syslog4j is licensed under the Lesser GNU Public License v2.1.  A copy
* of the LGPL license is available in the META-INF folder in all
* distributions of Syslog4j and in the base directory of the "doc" ZIP.</p>
* 
* @author &lt;syslog4j@productivity.org&gt;
* @version $Id: PrintStreamSyslogServerEventHandler.java,v 1.7 2010/11/28 22:07:57 cvs Exp $
*
* History
* =======
* 13.09.2017 WLI ORC-228 Create rolling log files for Syslog Server
*/
public class PrintStreamSyslogServerEventHandler implements SyslogServerSessionEventHandlerIF {
	
	private static final long serialVersionUID = 6036415838696050746L;
	
	protected PrintStream stream = null;
	
	protected PrintStreamSyslogServerEventHandler () {
		
	}
	
	public PrintStreamSyslogServerEventHandler(PrintStream stream) {
		this.stream = stream;
	}

	public void initialize(SyslogServerIF syslogServer) {
		return;
	}

	public Object sessionOpened(SyslogServerIF syslogServer, SocketAddress socketAddress) {
		return null;
	}

	public void event(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, SyslogServerEventIF event) {
		String date = (event.getDate() == null ? new Date() : event.getDate()).toString();
		String facility = SyslogUtility.getFacilityString(event.getFacility());
		String level = SyslogUtility.getLevelString(event.getLevel());
		String msg = event.getMessage();
//		System.out.println("handle event: " + msg); // WL

		if (event instanceof StructuredSyslogServerEvent) {
			try {
				StructuredSyslogMessage structuredMsg = StructuredSyslogMessage.fromString(msg);
				StringBuilder builder = new StringBuilder(msg.length() + 20);
				builder.append(date);
				builder.append(' ');
				builder.append(level);
				String processId = structuredMsg.getProcessId();
				if (processId != null) {
					builder.append(' ');
					builder.append(processId);
				}
				String messageId = structuredMsg.getMessageId();
				if (messageId != null) {
					builder.append(' ');
					builder.append(messageId);
				}
				
				builder.append(' ');
				builder.append(structuredMsg.getMessage());

				println(builder.toString());
				return;
			} 
			catch (Exception e) {
			}
		}
		
		println("{" + facility + "} " + date + " " + level + " " + msg);
	}

	protected void println(String text) {
		this.stream.println(text);
	}

	public void exception(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, Exception exception) {
		//
	}

	public void sessionClosed(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, boolean timeout) {
		//
	}

	public void destroy(SyslogServerIF syslogServer) {
		if (stream != null) {
			stream.close();
			stream = null;
		}
	}
}
