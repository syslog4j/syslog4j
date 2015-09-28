package org.productivity.java.syslog4j.test.net;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.multiple.MultipleSyslogConfig;
import org.productivity.java.syslog4j.impl.net.AbstractNetSyslogConfig;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;

public class MultipleSyslog4jTest extends AbstractNetSyslog4jTest {
	protected void setupMultipleConfig() {
		((AbstractNetSyslogConfig) Syslog.getInstance("tcp").getConfig()).setPort(TEST_PORT);
		
		MultipleSyslogConfig config = new MultipleSyslogConfig();
		
		config.addProtocol("tcp");
		
		Syslog.createInstance("multipleTcp",config);
	}

	protected int getMessageCount() {
		return 100;
	}

	protected String getClientProtocol() {
		return "multipleTcp";
	}

	protected String getServerProtocol() {
		return "tcp";
	}
	
	public void _testExceptionThrows() {
		SyslogIF s = Syslog.getInstance("multipleTcp");

		// NO-OPs
		s.backLog(0,null,"");
		s.backLog(0,null,new Exception());

		// Exceptions for methods that shouldn't be called
		try { s.setMessageProcessor(null); fail(); } catch (SyslogRuntimeException e) { }
		try { s.getMessageProcessor(); fail(); } catch (SyslogRuntimeException e) { }

		// Exceptions for methods that shouldn't be called
		try { s.setStructuredMessageProcessor(null); fail(); } catch (SyslogRuntimeException e) { }
		try { s.getStructuredMessageProcessor(); fail(); } catch (SyslogRuntimeException e) { }

		// Initialize Exceptions
		try {
			s.initialize(null,new TCPNetSyslogConfig());
			fail();
			
		} catch (SyslogRuntimeException e) {
			//
		}
	}

	public void testSendReceive() {
		setupMultipleConfig();
		
		_testExceptionThrows();
		
		super._testSendReceive(true,true);
	}
}
