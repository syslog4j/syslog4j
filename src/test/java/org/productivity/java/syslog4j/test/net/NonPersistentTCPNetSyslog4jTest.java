package org.productivity.java.syslog4j.test.net;

import org.junit.Ignore;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;


/**
 * Ignore test - this one fails randomly, project reached (unofficially) end-of-life in 2015 and is not
 * maintained anymore. Tests on Github also fails  
 * see https://github.com/syslog4j/syslog4j
 */
@Ignore @Deprecated
public class NonPersistentTCPNetSyslog4jTest extends AbstractNetSyslog4jTest {
	public static String instanceName = "tcp-non-persistent";
	
	public void setUp() {
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
