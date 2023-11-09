package org.productivity.java.syslog4j.test.misc;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.test.base.AbstractBaseTest;

public class NonDefinedSyslogInstanceTest extends AbstractBaseTest {
	public void testNonDefinedSyslogInstance() {
		try {
			Syslog.getInstance("not_defined");
			fail("not_defined shouldn't exist");
			
		} catch (SyslogRuntimeException sre) {
			assertTrue(sre.getMessage().startsWith("Syslog protocol \"not_defined\" not defined; call Syslogger.createSyslogInstance(protocol,config) first or use one of the following instances: "));
		}
	}

	public void testNonDefinedSyslogServerInstance() {
		try {
			SyslogServer.getInstance("not_defined");
			fail("not_defined shouldn't exist");
			
		} catch (SyslogRuntimeException sre) {
			assertTrue(sre.getMessage().startsWith("SyslogServer instance \"not_defined\" not defined; use \"tcp\" or \"udp\" or call SyslogServer.createInstance(protocol,config) first"));
		}
	}
}
