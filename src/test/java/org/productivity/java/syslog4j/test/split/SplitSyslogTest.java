package org.productivity.java.syslog4j.test.split;

import java.util.List;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.test.split.base.SplitSyslog;
import org.productivity.java.syslog4j.test.split.base.SplitSyslogConfig;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class SplitSyslogTest extends TestCase {
	protected int localNameLength = 0;
	
	public void setUp() {
		
//	WLI: Because the order of execution is not defined
//		this test run into an error when testTruncate was executed before testSplit!
		
//		if (!Syslog.exists("split")) {
//			SplitSyslogConfig config = new SplitSyslogConfig();
//			
//			Syslog.createInstance("split", config);
//		}
		Syslog.shutdown();
		Syslog.initialize();
		SplitSyslogConfig config = new SplitSyslogConfig();
		Syslog.createInstance("split", config);
	}
	
	protected void setMessageLength(int length) {
		SyslogConfigIF config = Syslog.getInstance("split").getConfig();
		
		String localName = SyslogUtility.getLocalName();
		this.localNameLength = localName.length();
		
		config.setMaxMessageLength(21 + this.localNameLength + length);
	}
	
	protected void assertSyslog(SyslogIF syslog, String[] expectedMessages) {
		SplitSyslog splitSyslog = (SplitSyslog) syslog;
		
		List lastMessages = splitSyslog.getLastMessages();
		
		if (lastMessages.size() < 1) {
			fail("No messages received");
		}
		
		for(int i=0; i<lastMessages.size(); i++) {
			String lastMessage = ((String) lastMessages.get(i)).substring(21 + this.localNameLength);
			
			assertEquals(expectedMessages[i],lastMessage);
		}
		
		syslog.flush();
	}
	
	public void testSplit() {
		setMessageLength(18);
		
		SyslogIF syslog = Syslog.getInstance("split");
		
		syslog.info("For now is the tim");
		assertSyslog(syslog,new String[] { "For now is the tim" });
		
		syslog.info("For now is the time");
		assertSyslog(syslog,new String[] {"For now is the ...", "...time" });
		
		syslog.info("For now is the time ");
		assertSyslog(syslog,new String[] {"For now is the ...", "...time " });
		
		syslog.info("------------------");
		syslog.flush();
		
		syslog.info("For now is the time for all g");
		assertSyslog(syslog,new String[] {"For now is the ...", "...time for all g" });
		
		syslog.info("For now is the time for all go");
		assertSyslog(syslog,new String[] {"For now is the ...", "...time for all go" });
		
		syslog.info("For now is the time for all goo");
		assertSyslog(syslog,new String[] {"For now is the ...", "...time for all...", "... goo" });
		
		syslog.flush();
	}
	
	public void testTruncate() {
		setMessageLength(10);
		
		SyslogIF syslog = Syslog.getInstance("split");
		syslog.getConfig().setTruncateMessage(true);
		
		syslog.info("T");
		syslog.info("Test 1234");
		syslog.info("Test 12345");
		syslog.info("Test 654321");
		syslog.info("Test 12345 For now is the time for all good men to come to the aid of their country");
		
		assertSyslog(syslog,new String[] {"T", "Test 1234", "Test 12345", "Test 65432", "Test 12345" } );
		
		syslog.flush();
	}
}
