package org.productivity.java.syslog4j.test.misc;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.Syslog4jVersion;
import org.productivity.java.syslog4j.server.SyslogServer;

public class SyslogVersionTest extends TestCase {
	public void testVersion() {
		assertEquals(Syslog4jVersion.VERSION,Syslog.getVersion());
		assertEquals(Syslog4jVersion.VERSION,SyslogServer.getVersion());
	}
}
