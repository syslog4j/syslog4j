package org.productivity.java.syslog4j.test.util;

import org.productivity.java.syslog4j.util.OSDetectUtility;

import junit.framework.TestCase;

public class OSDetectUtilityTest extends TestCase {
	public void testOSDetectUtility() {
		boolean unix = OSDetectUtility.isUnix();
		boolean windows = OSDetectUtility.isWindows();
		
		assertTrue(unix ^ windows);
	}
}
