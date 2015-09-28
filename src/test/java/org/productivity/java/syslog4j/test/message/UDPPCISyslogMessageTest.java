package org.productivity.java.syslog4j.test.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.message.pci.PCISyslogMessage;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class UDPPCISyslogMessageTest extends AbstractNetSyslog4jTest {
	protected static int pause = 100;
	
	protected int getMessageCount() {
		return -1;
	}

	protected String getClientProtocol() {
		return "udp";
	}
	
	protected String getServerProtocol() {
		return "udp";
	}

	public void testPCISyslogMessage() {
		// PREPARE
		
		List events = new ArrayList();
		String message = null;
		
		String protocol = getClientProtocol();
		SyslogIF syslog = getSyslog(protocol);
		
		message = new PCISyslogMessage("a","b",new Date(),"c","d","e").createMessage();
		syslog.info("[TEST] " + message);
		events.add("[TEST] " + message);

		// VERIFY
		
		SyslogUtility.sleep(pause);
		syslog.flush();
		verifySendReceive(events,false,false);
	}
}
