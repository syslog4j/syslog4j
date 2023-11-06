package org.productivity.java.syslog4j.test.message.processor;

import org.productivity.java.syslog4j.SyslogMessageProcessorIF;
import org.productivity.java.syslog4j.impl.message.processor.SyslogMessageProcessor;
import org.productivity.java.syslog4j.test.base.AbstractBaseTest;

public class SyslogMessageProcessorTest extends AbstractBaseTest {
	protected static final SyslogMessageProcessorIF syslogMessageProcessor = new SyslogMessageProcessor();

	public void testCreatingDefault() {
		SyslogMessageProcessor origMessageProcessor = SyslogMessageProcessor.getDefault();
		
		SyslogMessageProcessor newMessageProcessor = new SyslogMessageProcessor();
		
		SyslogMessageProcessor.setDefault(newMessageProcessor);
		assertEquals(newMessageProcessor,SyslogMessageProcessor.getDefault());
		
		SyslogMessageProcessor.setDefault(origMessageProcessor);
		assertEquals(origMessageProcessor,SyslogMessageProcessor.getDefault());
	}
	
	public void testCreatePacketData1() {
		byte[] h = "<15> Oct  5 00:00:00 ".getBytes();
		byte[] m = "[TEST] Test 123".getBytes();
		
		int s = 0;
		int l = m.length;
		
		byte[] d = syslogMessageProcessor.createPacketData(h,m,s,l);
		
		System.out.println(new String(d));
	}

	public void testCreatePacketData2() {
		byte[] h = "<15> Oct  5 00:00:00 ".getBytes();
		byte[] m = "For now is the time".getBytes();
		
		byte[] d = null;
		
		d = syslogMessageProcessor.createPacketData(h,m,0,8,null,"..".getBytes());
		System.out.println(new String(d));

		d = syslogMessageProcessor.createPacketData(h,m,8,6,"..".getBytes(),"..".getBytes());
		System.out.println(new String(d));
	}
}
