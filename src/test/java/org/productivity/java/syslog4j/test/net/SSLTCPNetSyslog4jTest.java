package org.productivity.java.syslog4j.test.net;

import org.junit.Test;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.impl.net.tcp.ssl.SSLTCPNetSyslogConfig;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.impl.net.tcp.ssl.SSLTCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.server.impl.net.tcp.ssl.SSLTCPNetSyslogServerConfigIF;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;

/**
 * Test sending and receiving of syslog messages over a SSL connection.
 * 
 * History
 * =======
 * 23.05.2017 WLI setUp of class SSLTCPNetSyslog4jTest initializes the ServiceRegistry
 *            because SSLTCPNetSyslogServer.getServerSocketFactory() uses SSLHelper 
 *            and SSLHelper calls ServiceRegistry.getServiceInstance.
 * 15.09.2017 WLI setUp of class SSLTCPNetSyslog4jTest also creates an registers
 *            a dummy EnvironmentSettingsService which delivers the keystore to the SSLHelper 
 */
public class SSLTCPNetSyslog4jTest extends AbstractNetSyslog4jTest {
	protected int getMessageCount() {
		return 10;
	}

	protected String getClientProtocol() {
		return "sslTcp";
	}

	protected String getServerProtocol() {
		return "sslTcp";
	}

	public void setUp() {
		
		setupSslClient();
		setupSslServer();

		super.setUp();
	}
	
	protected void setupSslClient() {
		SSLTCPNetSyslogConfig config = new SSLTCPNetSyslogConfig("127.0.0.1",10514);
/*	WL: SSL	
		// These next two lines aren't needed, but put here for code coverage
		config.setKeyStore("certs/ssltest.jks");
		config.setKeyStorePassword("ssltest");

		config.setTrustStore("certs/ssltest.jks");
		config.setTrustStorePassword("ssltest");
 */		
//		config.setThreaded(false); // WL: originally the test uses the default (threaded = true)
		
		Syslog.createInstance("sslTcp",config);
	}

	protected void setupSslServer() {
		SSLTCPNetSyslogServerConfigIF config = new SSLTCPNetSyslogServerConfig();
	/*	WL: SSL	
		config.setKeyStore("certs/ssltest.jks");
		config.setKeyStorePassword("ssltest");

		config.setTrustStore("certs/ssltest.jks");
		config.setTrustStorePassword("ssltest");
	*/
		SyslogServer.createInstance("sslTcp", config);
	}
	
	protected boolean isSyslogServerTcpBacklog() {
		return true;
	}

	@Test
	public void testSendReceive() {
		super._testSendReceive(true,true);
	}
	
	@Test
	public void testThreadedSendReceive() {
		super._testThreadedSendReceive(50,true,true);
	}
}
