package org.productivity.java.syslog4j.test.net;

import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;

public class UDPNetSyslog4jTest extends AbstractNetSyslog4jTest {
	protected static boolean ONCE = true;
	
	public void setUp() throws Exception {
		if (ONCE) {
			ONCE = false;
			
		} else {
			SyslogServer.getInstance(getServerProtocol()).getConfig().setHost("127.0.0.1");
		}
	
		super.setUp();	
	}
	
	protected int getMessageCount() {
		return 100;
	}

	protected String getClientProtocol() {
		return "udp";
	}
	
	protected String getServerProtocol() {
		return "udp";
	}
	
	public void testSendReceive() {
		super._testSendReceive(true,true);
	}
	
	public void xtestThreadedSendReceive() {
		super._testThreadedSendReceive(10,true,true);
	}
	
	public void testPCIMessages() {
		super._testSendReceivePCIMessages(true,true);
	}

	public void testStructuredMessages() {
		super._testSendReceiveStructuredMessages(true,true);
	}
}
