package org.productivity.java.syslog4j.test.message.processor.structured;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.impl.message.processor.structured.StructuredSyslogMessageProcessor;

public class StructuredSyslogMessageProcessorTest extends TestCase {
	public void testCreatingDefaultAndParameters() {
		StructuredSyslogMessageProcessor origMessageProcessor = StructuredSyslogMessageProcessor.getDefault();
		
		StructuredSyslogMessageProcessor newMessageProcessor = new StructuredSyslogMessageProcessor();
		
		newMessageProcessor.setApplicationName("app1");
		assertEquals("app1",newMessageProcessor.getApplicationName());
		
//		newMessageProcessor.setProcessId("proc1");
//		assertEquals("proc1",newMessageProcessor.getProcessId());
		
		StructuredSyslogMessageProcessor.setDefault(newMessageProcessor);
		assertEquals(newMessageProcessor,StructuredSyslogMessageProcessor.getDefault());
		
		StructuredSyslogMessageProcessor.setDefault(origMessageProcessor);
		assertEquals(origMessageProcessor,StructuredSyslogMessageProcessor.getDefault());
	}
}
