package org.productivity.java.syslog4j.test.net;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Ignore;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.impl.net.tcp.ssl.SSLTCPNetSyslogConfig;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.impl.net.tcp.ssl.SSLTCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.server.impl.net.tcp.ssl.SSLTCPNetSyslogServerConfigIF;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;

public class SSLTCPNetSyslog4jTest extends AbstractNetSyslog4jTest {
	protected int getMessageCount() {
		return 100;
	}

	protected String getClientProtocol() {
		return "sslTcp";
	}

	protected String getServerProtocol() {
		return "sslTcp";
	}
	
	public void setUp() throws Exception {
		setupSslClient();
		setupSslServer();

		super.setUp();
	}
	
	protected void setupSslClient() throws Exception {
		SSLTCPNetSyslogConfig config = new SSLTCPNetSyslogConfig("127.0.0.1",10514);
		SSLConfigUtil.configure(config);
		Syslog.createInstance("sslTcp",config);
	}

	protected void setupSslServer() throws Exception {
		SSLTCPNetSyslogServerConfigIF config = new SSLTCPNetSyslogServerConfig();
		SSLConfigUtil.configure(config);
		SyslogServer.createInstance("sslTcp", config);
	}

	protected boolean isSyslogServerTcpBacklog() {
		return true;
	}

	public void testSendReceive() {
		super._testSendReceive(true,true);
	}

	public void testThreadedSendReceive() {
		super._testThreadedSendReceive(50,true,true);
	}
}
