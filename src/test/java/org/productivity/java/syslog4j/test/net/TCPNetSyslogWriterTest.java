package org.productivity.java.syslog4j.test.net;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogWriter;

public class TCPNetSyslogWriterTest {

	private static void test (int len) {
		byte[] prefix = TCPNetSyslogWriter.getPacketLengthPrefix (len);
		String sprefix = new String(prefix);
		final String expected = Integer.toString(len) + ' ';
		assertEquals(expected, sprefix);
	}
	
	@Test
	public void testCreationOf () {
		byte[] prefix = TCPNetSyslogWriter.getPacketLengthPrefix (142);
		String sprefix = new String(prefix);
		assertEquals("142 ", sprefix);
		
		test(13);
		test(204);
		test(1782);
		test(1);
		test(10000);
	}
}
