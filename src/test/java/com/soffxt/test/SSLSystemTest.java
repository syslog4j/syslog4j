package com.soffxt.test;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.net.tcp.ssl.SSLTCPNetSyslogConfig;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.impl.net.tcp.ssl.SSLTCPNetSyslogServerConfig;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class SSLSystemTest {
	
	private SyslogIF client;
	private SyslogServerIF server;
	
	private String host = "127.0.0.1";
	private String protocol = "ssl";
	private int port = 10514;
	
	private RecorderHandler recorder;
	
	@Before
	public void setUp() {

		// client
		SSLTCPNetSyslogConfig clientConfig = new SSLTCPNetSyslogConfig(host, port);
		clientConfig.setThreaded(false); // WL: originally the test uses the default (threaded = true)
		
		client = Syslog.createInstance(protocol, clientConfig);
		
		// server
		SSLTCPNetSyslogServerConfig serverConfig = new SSLTCPNetSyslogServerConfig();
		serverConfig.setPort(port);
		recorder = new RecorderHandler ();
		serverConfig.addEventHandler(recorder);
		
		this.server = SyslogServer.createInstance(protocol, serverConfig);		

		if (this.server.getThread() == null) {
			Thread t = new Thread(this.server);
			t.setName("SyslogServer: " + protocol);
			t.start();
			
			this.server.setThread(t);
		}

		SyslogUtility.sleep(300); // Wait until the server is ready to accept connections
	}
	
	@Test
	public void testSendReceive() {
		int mcnt = 0;
		while (mcnt < 10) {
			client.info("message " + (++mcnt));
		}

		int i = 0;
		while (i < 100 && recorder.getRecordedEvents().size() < mcnt) {
			SyslogUtility.sleep(500);
			i++;
		}
		assertEquals(mcnt, recorder.getRecordedEvents().size());
		System.out.println("test needed " + i / 2 + " seconds.");
	}

	@After
	public void shutdown () {
		server.shutdown();
		client.shutdown();
	}
}
