package com.soffxt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;

public class ClientTest {

	@Test
	public void testBOM () throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		builder.append(StructuredSyslogMessage.BOM);
		builder.append("Ärger");
		String msgText = builder.toString();
		byte[] bomBytes = msgText.getBytes("UTF-8");
		// expect the byte sequence EF BB BF
		assertEquals(0xEF, bomBytes[0] & 0xFF);
		assertEquals(0xBB, bomBytes[1] & 0xFF);
		assertEquals(0xBF, bomBytes[2] & 0xFF);
		StructuredSyslogMessage msg = new StructuredSyslogMessage ("prcId", "mgId", null, msgText);
		assertEquals("Ärger", msg.getMessage());
		String serialized = msg.createMessage();
		byte[] bytes = serialized.getBytes("UTF-8");
		boolean found = false;
		for (int idx = 0; idx < bytes.length; idx++) {
			int b = bytes[idx] & 0xFF;
			if (b == 0xEF) {
				found = true;
			}
		}
		assertTrue("no BOM in serialized message", found);
	}
}
