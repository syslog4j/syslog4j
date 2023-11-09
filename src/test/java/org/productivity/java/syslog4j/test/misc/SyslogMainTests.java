package org.productivity.java.syslog4j.test.misc;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.SyslogMain;
import org.productivity.java.syslog4j.server.SyslogServerMain;

public class SyslogMainTests extends TestCase {
	public void testSyslogMainSuccesses() {
		SyslogMain.Options options = null;
		
		options = SyslogMain.parseOptions(new String[] { "udp" });
		assertNull(options.usage);
		assertEquals("udp",options.protocol);
		assertNull(options.message);
		assertFalse(options.quiet);

		options = SyslogMain.parseOptions(new String[] { "udp", "message" });
		assertNull(options.usage);
		assertEquals("udp",options.protocol);
		assertEquals("message",options.message);
		assertFalse(options.quiet);

		options = SyslogMain.parseOptions(new String[] { "-h", "host", "tcp", "message" });
		assertNull(options.usage);
		assertEquals("tcp",options.protocol);
		assertEquals("host",options.host);
		assertEquals("message",options.message);

		options = SyslogMain.parseOptions(new String[] { "-p", "1234", "udp", "message" });
		assertNull(options.usage);
		assertEquals("udp",options.protocol);
		assertEquals("1234",options.port);
		assertEquals("message",options.message);

		options = SyslogMain.parseOptions(new String[] { "-q", "udp", "message" });
		assertEquals("udp",options.protocol);
		assertNull(options.usage);
		assertTrue(options.quiet);
		assertEquals("message",options.message);

		options = SyslogMain.parseOptions(new String[] { "udp", "-l", "WARN", "message" });
		assertEquals("udp",options.protocol);
		assertEquals("WARN",options.level);
		assertNull(options.usage);
		assertEquals("message",options.message);

		options = SyslogMain.parseOptions(new String[] { "udp", "-f", "LOCAL0" });
		assertEquals("udp",options.protocol);
		assertEquals("LOCAL0",options.facility);
		assertNull(options.usage);

		options = SyslogMain.parseOptions(new String[] { "udp", "-i", "file.txt" });
		assertEquals("udp",options.protocol);
		assertEquals("file.txt",options.fileName);
		assertNull(options.usage);

		options = SyslogMain.parseOptions(new String[] { "-q", "udp", "message word 1", "message word 2" });
		assertEquals("udp",options.protocol);
		assertEquals("message word 1 message word 2",options.message);
		assertNull(options.usage);
		assertTrue(options.quiet);
	}

	public void testSyslogMainFailures() {
		SyslogMain.Options options = null;

		options = SyslogMain.parseOptions(new String[] { });
		assertEquals("Must specify protocol",options.usage);

		options = SyslogMain.parseOptions(new String[] { "udp", "-h" });
		assertEquals("Must specify host with -h",options.usage);

		options = SyslogMain.parseOptions(new String[] { "udp", "-p" });
		assertEquals("Must specify port with -p",options.usage);

		options = SyslogMain.parseOptions(new String[] { "udp", "-l" });
		assertEquals("Must specify level with -l",options.usage);
	
		options = SyslogMain.parseOptions(new String[] { "udp", "-f" });
		assertEquals("Must specify facility with -f",options.usage);

		options = SyslogMain.parseOptions(new String[] { "udp", "-i" });
		assertEquals("Must specify file with -i",options.usage);

		options = SyslogMain.parseOptions(new String[] { "udp", "-i", "file.txt", "message" });
		assertEquals("Must specify either -i <file> or <message>, not both",options.usage);
	}

	public void testSyslogMainUsage() {
		SyslogMain.usage(null);
		SyslogMain.usage("Problem (Ignore)");
	}
	
	public void testSyslogServerMainSuccesses() {
		SyslogServerMain.Options options = null;
		
		options = SyslogServerMain.parseOptions(new String[] { "udp" });
		assertNull(options.usage);
		assertEquals("udp",options.protocol);
		assertFalse(options.quiet);

		options = SyslogServerMain.parseOptions(new String[] { "-h", "host", "tcp" });
		assertNull(options.usage);
		assertEquals("tcp",options.protocol);
		assertEquals("host",options.host);

		options = SyslogServerMain.parseOptions(new String[] { "-p", "1234", "udp" });
		assertNull(options.usage);
		assertEquals("udp",options.protocol);
		assertEquals("1234",options.port);

		options = SyslogServerMain.parseOptions(new String[] { "-q", "udp" });
		assertEquals("udp",options.protocol);
		assertNull(options.usage);
		assertTrue(options.quiet);

		options = SyslogServerMain.parseOptions(new String[] { "udp", "-o", "file.txt" });
		assertEquals("udp",options.protocol);
		assertEquals("file.txt",options.fileName);
		assertNull(options.usage);
		assertFalse(options.append);

		options = SyslogServerMain.parseOptions(new String[] { "udp", "-o", "file.txt", "-a" });
		assertEquals("udp",options.protocol);
		assertEquals("file.txt",options.fileName);
		assertNull(options.usage);
		assertTrue(options.append);
	}

	public void testSyslogServerMainFailures() {
		SyslogServerMain.Options options = null;

		options = SyslogServerMain.parseOptions(new String[] { });
		assertEquals("Must specify protocol",options.usage);

		options = SyslogServerMain.parseOptions(new String[] { "udp", "-h" });
		assertEquals("Must specify host with -h",options.usage);

		options = SyslogServerMain.parseOptions(new String[] { "udp", "-p" });
		assertEquals("Must specify port with -p",options.usage);

		options = SyslogServerMain.parseOptions(new String[] { "udp", "-o" });
		assertEquals("Must specify file with -o",options.usage);

		options = SyslogServerMain.parseOptions(new String[] { "udp", "-a" });
		assertEquals("Cannot specify -a without specifying -f <file>",options.usage);
	}
	
	public void testSyslogServerMainUsage() {
		SyslogServerMain.usage(null);
		SyslogServerMain.usage("Problem (Ignore)");
	}

	public void testSyslogMain() throws Exception {
		SyslogMain.CALL_SYSTEM_EXIT_ON_FAILURE = false;
		
		SyslogMain.main(new String[] { });

		SyslogMain.main(new String[] { "nonExistentProtocol" });
	}

	public void testSyslogServerMain() throws Exception {
		SyslogServerMain.CALL_SYSTEM_EXIT_ON_FAILURE = false;
		
		SyslogServerMain.main(new String[] { });

		SyslogServerMain.main(new String[] { "nonExistentProtocol" });
	}
}
