package org.productivity.java.syslog4j.test.unix;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.AbstractSyslog;
import org.productivity.java.syslog4j.impl.unix.UnixSyslogConfig;

public class UnixSyslogTest extends TestCase {
	public void testUnixSyslogConfig() {
		if (Boolean.getBoolean(SyslogConstants.DISABLE_UNIX_PROPERTY)) {
			assertTrue(true);
			return;
		}

		UnixSyslogConfig config = new UnixSyslogConfig();
		
		assertNull(config.getHost());
		
		assertEquals(0,config.getPort());
		
		try {
			config.setHost("abcdef");
			fail();
			
		} catch (Exception e) {
			//
		}

		try {
			config.setPort(999);
			fail();
			
		} catch (Exception e) {
			//
		}

		try {
			config.getMaxQueueSize();
			fail();
			
		} catch (Exception e) {
			//
		}

		try {
			config.setMaxQueueSize(888);
			fail();
			
		} catch (Exception e) {
			//
		}
		
		assertEquals(SyslogConstants.SYSLOG_LIBRARY_DEFAULT,config.getLibrary());		
		config.setLibrary("d");
		assertEquals("d",config.getLibrary());
		
		assertEquals(SyslogConstants.OPTION_NONE,config.getOption());
		config.setOption(1);
		assertEquals(1,config.getOption());
	}
	
	public void testUnixSyslog() {
		SyslogIF syslog = Syslog.getInstance(SyslogConstants.UNIX_SYSLOG);
		
		syslog.getConfig().setFacility(SyslogIF.FACILITY_KERN);
		
		syslog.error(this.getClass().getName() + ": unix_syslog " + System.currentTimeMillis());
		
		syslog.flush();
		
		syslog.shutdown();
		
		AbstractSyslog abstractSyslog = (AbstractSyslog) syslog;
		
		assertNull(abstractSyslog.getWriter());
		abstractSyslog.returnWriter(null);
	}
}
