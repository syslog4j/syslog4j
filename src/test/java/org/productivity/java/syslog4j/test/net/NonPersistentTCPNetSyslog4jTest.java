package org.productivity.java.syslog4j.test.net;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;

public class NonPersistentTCPNetSyslog4jTest extends AbstractNetSyslog4jTest {
	public static String instanceName = "tcp-non-persistent";
	
	public void setUp() throws Exception {
		TCPNetSyslogConfig config = new TCPNetSyslogConfig();
		config.setPersistentConnection(false);
		
		Syslog.createInstance(instanceName,config);

		super.setUp();
	}
	
	protected int getMessageCount() {
		return 100;
	}

	protected String getClientProtocol() {
		return instanceName;
	}

	protected String getServerProtocol() {
		return "tcp";
	}
	
	public void testSendReceive() {
		super._testSendReceive(true,true);
	}

	public void testThreadedSendReceive() {
		super._testThreadedSendReceive(10,true,true);
	}
}
