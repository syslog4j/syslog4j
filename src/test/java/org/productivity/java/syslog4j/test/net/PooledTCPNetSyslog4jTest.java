package org.productivity.java.syslog4j.test.net;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.impl.net.tcp.pool.PooledTCPNetSyslogConfig;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.impl.net.tcp.TCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;

public class PooledTCPNetSyslog4jTest extends AbstractNetSyslog4jTest {
	protected void setupPoolConfig(boolean threaded, int maxActive, int maxWait) {
		PooledTCPNetSyslogConfig config = new PooledTCPNetSyslogConfig();
		
		config.setThreaded(threaded);
		config.setThrowExceptionOnWrite(true);
		config.setThrowExceptionOnInitialize(true);
		
		if (maxWait > 0) {
			config.setMaxWait(maxWait);
		}
		
		if (maxActive > 0) {
			config.setMaxActive(maxActive);
		}
			
		Syslog.createInstance("pooledTcp",config);
	}
	
	protected int getMessageCount() {
		return 250;
	}

	protected String getClientProtocol() {
		return "pooledTcp";
	}

	protected String getServerProtocol() {
		TCPNetSyslogServerConfig serverConfig = new TCPNetSyslogServerConfig();
		
		SyslogServer.createThreadedInstance("pooledTcp", serverConfig);
		
		return "pooledTcp";
	}
	
	public void _testOne() {
		setupPoolConfig(false,0,0);
		
		getSyslog("pooledTcp").info("[TEST] test");
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
