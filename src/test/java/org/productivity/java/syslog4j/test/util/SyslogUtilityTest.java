package org.productivity.java.syslog4j.test.util;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.SyslogCharSetIF;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class SyslogUtilityTest extends TestCase {
	public static class CharSet implements SyslogCharSetIF {
		private static final long serialVersionUID = 130052445223551802L;
		
		protected String charSet = null;

		public String getCharSet() {
			return this.charSet;
		}

		public void setCharSet(String charSet) {
			this.charSet = charSet;
		}
		
	}
	
	public void testSyslogUtility() {
		assertTrue(SyslogUtility.isClassExists("java.lang.String"));
		assertFalse(SyslogUtility.isClassExists("java.lang.NonexistentClass"));
		
		assertEquals(SyslogConstants.FACILITY_AUTH,SyslogUtility.getFacility("auth"));
		assertEquals(SyslogConstants.FACILITY_AUTHPRIV,SyslogUtility.getFacility("authpriv"));
		assertEquals(SyslogConstants.FACILITY_CRON,SyslogUtility.getFacility("cron"));
		assertEquals(SyslogConstants.FACILITY_DAEMON,SyslogUtility.getFacility("daemon"));
		assertEquals(SyslogConstants.FACILITY_FTP,SyslogUtility.getFacility("ftp"));
		assertEquals(SyslogConstants.FACILITY_KERN,SyslogUtility.getFacility("kern"));
		assertEquals(SyslogConstants.FACILITY_LOCAL0,SyslogUtility.getFacility("local0"));
		assertEquals(SyslogConstants.FACILITY_LOCAL1,SyslogUtility.getFacility("local1"));
		assertEquals(SyslogConstants.FACILITY_LOCAL2,SyslogUtility.getFacility("local2"));
		assertEquals(SyslogConstants.FACILITY_LOCAL3,SyslogUtility.getFacility("local3"));
		assertEquals(SyslogConstants.FACILITY_LOCAL4,SyslogUtility.getFacility("local4"));
		assertEquals(SyslogConstants.FACILITY_LOCAL5,SyslogUtility.getFacility("local5"));
		assertEquals(SyslogConstants.FACILITY_LOCAL6,SyslogUtility.getFacility("local6"));
		assertEquals(SyslogConstants.FACILITY_LOCAL7,SyslogUtility.getFacility("local7"));
		assertEquals(SyslogConstants.FACILITY_LPR,SyslogUtility.getFacility("lpr"));
		assertEquals(SyslogConstants.FACILITY_MAIL,SyslogUtility.getFacility("mail"));
		assertEquals(SyslogConstants.FACILITY_NEWS,SyslogUtility.getFacility("news"));
		assertEquals(SyslogConstants.FACILITY_SYSLOG,SyslogUtility.getFacility("syslog"));
		assertEquals(SyslogConstants.FACILITY_USER,SyslogUtility.getFacility("user"));
		assertEquals(SyslogConstants.FACILITY_UUCP,SyslogUtility.getFacility("uucp"));
		assertEquals(-1,SyslogUtility.getFacility(null));
		assertEquals(-1,SyslogUtility.getFacility(""));

		assertEquals("auth",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_AUTH));
		assertEquals("authpriv",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_AUTHPRIV));
		assertEquals("cron",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_CRON));
		assertEquals("daemon",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_DAEMON));
		assertEquals("ftp",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_FTP));
		assertEquals("kern",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_KERN));
		assertEquals("local0",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_LOCAL0));
		assertEquals("local1",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_LOCAL1));
		assertEquals("local2",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_LOCAL2));
		assertEquals("local3",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_LOCAL3));
		assertEquals("local4",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_LOCAL4));
		assertEquals("local5",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_LOCAL5));
		assertEquals("local6",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_LOCAL6));
		assertEquals("local7",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_LOCAL7));
		assertEquals("lpr",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_LPR));
		assertEquals("mail",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_MAIL));
		assertEquals("news",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_NEWS));
		assertEquals("syslog",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_SYSLOG));
		assertEquals("user",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_USER));
		assertEquals("uucp",SyslogUtility.getFacilityString(SyslogConstants.FACILITY_UUCP));
		assertEquals("UNKNOWN",SyslogUtility.getFacilityString(-1));
		
		assertEquals("DEBUG",SyslogUtility.getLevelString(SyslogConstants.LEVEL_DEBUG));
		assertEquals("INFO",SyslogUtility.getLevelString(SyslogConstants.LEVEL_INFO));
		assertEquals("NOTICE",SyslogUtility.getLevelString(SyslogConstants.LEVEL_NOTICE));
		assertEquals("WARN",SyslogUtility.getLevelString(SyslogConstants.LEVEL_WARN));
		assertEquals("ERROR",SyslogUtility.getLevelString(SyslogConstants.LEVEL_ERROR));
		assertEquals("CRITICAL",SyslogUtility.getLevelString(SyslogConstants.LEVEL_CRITICAL));
		assertEquals("ALERT",SyslogUtility.getLevelString(SyslogConstants.LEVEL_ALERT));
		assertEquals("EMERGENCY",SyslogUtility.getLevelString(SyslogConstants.LEVEL_EMERGENCY));
		assertEquals("UNKNOWN",SyslogUtility.getLevelString(-1));

		assertEquals(SyslogConstants.LEVEL_DEBUG,SyslogUtility.getLevel("DEBUG"));
		assertEquals(SyslogConstants.LEVEL_INFO,SyslogUtility.getLevel("INFO"));
		assertEquals(SyslogConstants.LEVEL_NOTICE,SyslogUtility.getLevel("NOTICE"));
		assertEquals(SyslogConstants.LEVEL_WARN,SyslogUtility.getLevel("WARN"));
		assertEquals(SyslogConstants.LEVEL_ERROR,SyslogUtility.getLevel("ERROR"));
		assertEquals(SyslogConstants.LEVEL_CRITICAL,SyslogUtility.getLevel("CRITICAL"));
		assertEquals(SyslogConstants.LEVEL_ALERT,SyslogUtility.getLevel("ALERT"));
		assertEquals(SyslogConstants.LEVEL_EMERGENCY,SyslogUtility.getLevel("EMERGENCY"));
		assertEquals("UNKNOWN",SyslogUtility.getLevelString(-1));

		String message = "foo";
		
		CharSet cs = new CharSet();
		cs.setCharSet("FakeCharSet");
		
		assertEquals(message,SyslogUtility.newString(cs,message.getBytes()));
		assertEquals(message,new String(SyslogUtility.getBytes(cs,message)));
		
		try {
			SyslogUtility.getInetAddress("fake-host-name");
//			fail("Should not return an address on a fake host name: " + address.toString());
			
		} catch (SyslogRuntimeException sre) {
			assertTrue(true);
		}

		try {
			SyslogUtility.getLocalName();
			
		} catch (SyslogRuntimeException sre) {
			assertTrue(true);
		}
	}
}
