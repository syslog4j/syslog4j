package org.productivity.java.syslog4j.test.log4j;

import org.productivity.java.syslog4j.test.log4j.base.AbstractLog4jSyslog4jTest;

public class Log4jSyslog4jTest extends AbstractLog4jSyslog4jTest {
	protected int getMessageCount() {
		return 100;
	}

	protected String getServerProtocol() {
		return "udp";
	}
	
	public void testSendReceive() {
		super._testSendReceive();
	}
}
