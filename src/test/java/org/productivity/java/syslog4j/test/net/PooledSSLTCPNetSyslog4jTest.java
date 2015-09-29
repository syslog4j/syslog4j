package org.productivity.java.syslog4j.test.net;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.impl.net.tcp.ssl.pool.PooledSSLTCPNetSyslogConfig;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.impl.net.tcp.ssl.SSLTCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.server.impl.net.tcp.ssl.SSLTCPNetSyslogServerConfigIF;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;

public class PooledSSLTCPNetSyslog4jTest extends AbstractNetSyslog4jTest {
	protected void setupPoolConfig(boolean threaded, int maxActive, int maxWait) {
		PooledSSLTCPNetSyslogConfig config = new PooledSSLTCPNetSyslogConfig();

		config.setThreaded(threaded);
		config.setThrowExceptionOnWrite(true);
		config.setThrowExceptionOnInitialize(true);
		
		if (maxWait > 0) {
			config.setMaxWait(maxWait);
		}
		
		if (maxActive > 0) {
			config.setMaxActive(maxActive);
		}
			
		Syslog.createInstance("pooledSslTcp",config);
	}
	
	protected int getMessageCount() {
		return 250;
	}

	protected String getClientProtocol() {
		return "pooledSslTcp";
	}

	protected String getServerProtocol() throws Exception {
		SSLTCPNetSyslogServerConfigIF config = new SSLTCPNetSyslogServerConfig();
		SSLConfigUtil.configure(config);
		SyslogServer.createThreadedInstance("pooledSslTcp", config);
		
		return "pooledSslTcp";
	}
	
	public void _testOne() {
		setupPoolConfig(false,0,0);
		
		getSyslog("pooledSslTcp").info("[TEST] test");
	}
	
	public void testThreadedSendReceive_50_threaded() {
		setupPoolConfig(true,0,8000);
		
		super._testThreadedSendReceive(50,true,true);
	}

	public void testThreadedSendReceive_50() {
		setupPoolConfig(false,0,8000);
		
		super._testThreadedSendReceive(50,true,true);
	}
}
