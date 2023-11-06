package com.soffxt.test;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;

public class MessageParseTest {

	private static Map<String,Map<String,String>> makeStructData() {
		Map<String,Map<String,String>> structData = null;
		structData = new HashMap<String,Map<String,String>> ();
		Map<String,String> params = new HashMap<String,String> ();
		params.put("software", "Orchestra");
		params.put("swVersion", "4.5.8.0");
		structData.put("origin", params);
		return structData;
	}
	
	@Test
	public void testStructuredMessageParsing1 () throws UnsupportedEncodingException {
		
		StructuredSyslogMessage msg1 = new StructuredSyslogMessage ("prcId", "mgId", makeStructData(), "Ärger");
		
		String syslogMessageStr = "prcId mgId [origin software=\"Orchestra\" swVersion=\"4.5.8.0\"] " + StructuredSyslogMessage.BOM + "Ärger";
		StructuredSyslogMessage msg2 = StructuredSyslogMessage.fromString(syslogMessageStr);
		assertEquals(msg1,msg2);
	}
	
	@Test
	public void testStructuredMessageParsing2 () throws UnsupportedEncodingException {
		
		StructuredSyslogMessage msg1 = new StructuredSyslogMessage ("prcId", "mgId", makeStructData(), "Ärger");
		
		String syslogMessageStr = "prcId mgId [origin software=\"Orchestra\" swVersion=\"4.5.8.0\"] Ärger";
		StructuredSyslogMessage msg2 = StructuredSyslogMessage.fromString(syslogMessageStr);
		assertEquals(msg1,msg2);
	}
	
	@Test
	public void testStructuredMessageParsing3 () throws UnsupportedEncodingException {
		
		Map<String,Map<String,String>> structData = null;
		
		StructuredSyslogMessage msg1 = new StructuredSyslogMessage ("prcId", "mgId", structData, "Ärger");
		String syslogMessageStr = msg1.createMessage();

		StructuredSyslogMessage msg2 = StructuredSyslogMessage.fromString(syslogMessageStr);
		assertEquals(msg1,msg2);
	}

	@Test
	public void testStructuredMessageParsing4 () throws UnsupportedEncodingException {
		
		Map<String,Map<String,String>> structData = new HashMap<String,Map<String,String>> ();
		
		StructuredSyslogMessage msg1 = new StructuredSyslogMessage ("prcId", "mgId", structData, "Ärger");
		String syslogMessageStr = msg1.createMessage();

		StructuredSyslogMessage msg2 = StructuredSyslogMessage.fromString(syslogMessageStr);
		assertEquals(msg1,msg2);
	}

	@Test
	public void testStructuredMessageParsing5 () throws UnsupportedEncodingException {
		
		Map<String,Map<String,String>> structData = makeStructData();
		
		StructuredSyslogMessage msg1 = new StructuredSyslogMessage ("prcId", "mgId", structData, "Ärger");
		String syslogMessageStr = msg1.createMessage();

		StructuredSyslogMessage msg2 = StructuredSyslogMessage.fromString(syslogMessageStr);
		assertEquals(msg1,msg2);
	}
	
	@Test
	public void testStructuredMessageParsing6 () throws UnsupportedEncodingException {
		
		StructuredSyslogMessage msg1 = new StructuredSyslogMessage ("prcId", "mgId", null, "Ärger");
		
		String syslogMessageStr = "prcId mgId - " + StructuredSyslogMessage.BOM + "Ärger";
		StructuredSyslogMessage msg2 = StructuredSyslogMessage.fromString(syslogMessageStr);
		assertEquals(msg1,msg2);
	}
	
	@Test
	public void testStructuredMessageParsing7 () throws UnsupportedEncodingException {
		
		StructuredSyslogMessage msg1 = new StructuredSyslogMessage (null, null, null, "Ärger");
		
		String syslogMessageStr = "- - - " + StructuredSyslogMessage.BOM + "Ärger";
		StructuredSyslogMessage msg2 = StructuredSyslogMessage.fromString(syslogMessageStr);
		assertEquals(msg1,msg2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStructuredMessageParsing10 () throws UnsupportedEncodingException {
		String syslogMessageStr = "prcId mgId [origin software=\"Orchestra\" swVersion=\"4.5.8.0\"]Ärger";
		StructuredSyslogMessage.fromString(syslogMessageStr);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStructuredMessageParsing11 () throws UnsupportedEncodingException {
		String syslogMessageStr = "test [origin software=\"Orchestra\" swVersion=\"4.5.8.0\"] Ärger";
		StructuredSyslogMessage.fromString(syslogMessageStr);
	}
}
