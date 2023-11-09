package org.productivity.java.syslog4j.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.Ignore;
//import org.productivity.java.syslog4j.test.log4j.Log4jSyslog4jTest;
import org.productivity.java.syslog4j.test.message.UDPPCISyslogMessageTest;
import org.productivity.java.syslog4j.test.message.modifier.SyslogMessageModifierTest;
import org.productivity.java.syslog4j.test.message.modifier.SyslogMessageModifierVerifyTest;
import org.productivity.java.syslog4j.test.message.pci.PCISyslogMessageTest;
import org.productivity.java.syslog4j.test.message.processor.SyslogMessageProcessorTest;
import org.productivity.java.syslog4j.test.message.processor.structured.StructuredSyslogMessageProcessorTest;
import org.productivity.java.syslog4j.test.message.structured.StructuredSyslogMessageTest;
import org.productivity.java.syslog4j.test.misc.NonDefinedSyslogInstanceTest;
import org.productivity.java.syslog4j.test.misc.SyslogCreateAndDestroyTest;
import org.productivity.java.syslog4j.test.misc.SyslogMainTests;
import org.productivity.java.syslog4j.test.misc.SyslogParameterTest;
import org.productivity.java.syslog4j.test.misc.SyslogVersionTest;
import org.productivity.java.syslog4j.test.multiple.MultipleSyslogCreateTest;
import org.productivity.java.syslog4j.test.net.CommandLineNetSyslog4jTest;
import org.productivity.java.syslog4j.test.net.FreshConnectionIntervalTest;
import org.productivity.java.syslog4j.test.net.MaxActiveSocketsTest;
import org.productivity.java.syslog4j.test.net.MaxQueueSizeTest;
import org.productivity.java.syslog4j.test.net.MultipleSyslog4jTest;
import org.productivity.java.syslog4j.test.net.NonPersistentTCPNetSyslog4jTest;
import org.productivity.java.syslog4j.test.net.PooledSSLTCPNetSyslog4jTest;
import org.productivity.java.syslog4j.test.net.PooledTCPNetSyslog4jTest;
import org.productivity.java.syslog4j.test.net.SSLTCPNetSyslog4jTest;
import org.productivity.java.syslog4j.test.net.SyslogServerSessionTest;
import org.productivity.java.syslog4j.test.net.TCPNetSyslog4jTest;
import org.productivity.java.syslog4j.test.net.UDPNetSyslog4jTest;
import org.productivity.java.syslog4j.test.server.event.PrintStreamServerEventTest;
import org.productivity.java.syslog4j.test.server.event.ServerEventTest;
import org.productivity.java.syslog4j.test.split.SplitSyslogTest;
//import org.productivity.java.syslog4j.test.unix.UnixSyslogTest;
//import org.productivity.java.syslog4j.test.unix.socket.UnixSocketSyslogTest;
import org.productivity.java.syslog4j.test.util.OSDetectUtilityTest;
import org.productivity.java.syslog4j.test.util.SyslogUtilityTest;
//import org.productivity.java.syslog4j.util.OSDetectUtility;


/**
 * Ignore Parent-Testsuite, otherwise you also have to comment tests which should not 
 * be executed here out
 *
 */
@Ignore @Deprecated
public class Syslog4jTest {
	public static Test suite() {
		TestSuite testSuite = new TestSuite("Syslog4j TestSuite");
		
		testSuite.addTestSuite(SyslogMainTests.class);
		testSuite.addTestSuite(SyslogCreateAndDestroyTest.class);
		testSuite.addTestSuite(SyslogParameterTest.class);
		testSuite.addTestSuite(MultipleSyslogCreateTest.class);
		testSuite.addTestSuite(SyslogVersionTest.class);
		testSuite.addTestSuite(SyslogMessageProcessorTest.class);
		testSuite.addTestSuite(StructuredSyslogMessageProcessorTest.class);
		testSuite.addTestSuite(StructuredSyslogMessageTest.class);
		testSuite.addTestSuite(NonDefinedSyslogInstanceTest.class);
		testSuite.addTestSuite(SplitSyslogTest.class);
		testSuite.addTestSuite(PCISyslogMessageTest.class);
		testSuite.addTestSuite(SyslogUtilityTest.class);
		testSuite.addTestSuite(OSDetectUtilityTest.class);
		testSuite.addTestSuite(PrintStreamServerEventTest.class);
				
		testSuite.addTestSuite(ServerEventTest.class);
		testSuite.addTestSuite(UDPNetSyslog4jTest.class);
		testSuite.addTestSuite(TCPNetSyslog4jTest.class);
		testSuite.addTestSuite(SSLTCPNetSyslog4jTest.class);
		testSuite.addTestSuite(PooledTCPNetSyslog4jTest.class);
		testSuite.addTestSuite(PooledSSLTCPNetSyslog4jTest.class);
		testSuite.addTestSuite(MultipleSyslog4jTest.class);
		testSuite.addTestSuite(NonPersistentTCPNetSyslog4jTest.class);
		testSuite.addTestSuite(UDPPCISyslogMessageTest.class);
//		testSuite.addTestSuite(Log4jSyslog4jTest.class);
//		testSuite.addTestSuite(BackLogTCPNetSyslog4jTest.class);
		testSuite.addTestSuite(SyslogServerSessionTest.class);

		testSuite.addTestSuite(SyslogMessageModifierTest.class);
		testSuite.addTestSuite(SyslogMessageModifierVerifyTest.class);

//		if (OSDetectUtility.isUnix()) {
//			testSuite.addTestSuite(UnixSyslogTest.class);
//			testSuite.addTestSuite(UnixSocketSyslogTest.class); 
//		}
		
		testSuite.addTestSuite(MaxQueueSizeTest.class);
		testSuite.addTestSuite(MaxActiveSocketsTest.class);
		testSuite.addTestSuite(FreshConnectionIntervalTest.class);
		
		testSuite.addTestSuite(CommandLineNetSyslog4jTest.class);
		
		return testSuite;
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(Syslog4jTest.suite());
	}
}
