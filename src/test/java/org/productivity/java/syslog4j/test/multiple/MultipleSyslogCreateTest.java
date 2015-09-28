package org.productivity.java.syslog4j.test.multiple;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogBackLogHandlerIF;
import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogMessageIF;
import org.productivity.java.syslog4j.SyslogMessageModifierIF;
import org.productivity.java.syslog4j.SyslogMessageProcessorIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.message.processor.SyslogMessageProcessor;
import org.productivity.java.syslog4j.impl.message.processor.structured.StructuredSyslogMessageProcessor;
import org.productivity.java.syslog4j.impl.multiple.MultipleSyslogConfig;

public class MultipleSyslogCreateTest extends TestCase {
	public static class FakeSyslog implements SyslogIF {
		private static final long serialVersionUID = 7519273907420813675L;
		
		public String protocol = null;
		public SyslogConfigIF config = null;
		
		public int total = 0;

		public void debug(String message) { this.total += 1; }
		public void debug(SyslogMessageIF message) { this.total += 2; }

		public void info(String message) { this.total += 4; }
		public void info(SyslogMessageIF message) { this.total += 8; }

		public void notice(String message) { this.total += 16; }
		public void notice(SyslogMessageIF message) { this.total += 32; }

		public void warn(String message) { this.total += 64; }
		public void warn(SyslogMessageIF message) { this.total += 128; }

		public void error(String message) { this.total += 256; }
		public void error(SyslogMessageIF message) { this.total += 512; }

		public void critical(String message) { this.total += 1024; }
		public void critical(SyslogMessageIF message) { this.total += 2048; }

		public void alert(String message) { this.total += 4096; }
		public void alert(SyslogMessageIF message) { this.total += 8192; }

		public void emergency(String message) { this.total += 16384; }
		public void emergency(SyslogMessageIF message) { this.total += 32768; }

		public void backLog(int level, String message, Throwable reasonThrowable) { }
		public void backLog(int level, String message, String reason) { }

		public void flush() throws SyslogRuntimeException { }
		
		public SyslogConfigIF getConfig() { return this.config; }

		public SyslogMessageProcessorIF getMessageProcessor() { return SyslogMessageProcessor.getDefault(); }
		public void setMessageProcessor(SyslogMessageProcessorIF messageProcessor) { }

		public SyslogMessageProcessorIF getStructuredMessageProcessor() { return StructuredSyslogMessageProcessor.getDefault(); }
		public void setStructuredMessageProcessor(SyslogMessageProcessorIF messageProcessor) { }

		public String getProtocol() { return this.protocol; }

		public void initialize(String syslogProtocol, SyslogConfigIF syslogConfig) throws SyslogRuntimeException {
			this.protocol = syslogProtocol;
			this.config = syslogConfig;
		}

		public void log(int level, String message) {
			if (SyslogConstants.LEVEL_DEBUG == level) { debug(message); }
			if (SyslogConstants.LEVEL_INFO == level) { info(message); }
			if (SyslogConstants.LEVEL_NOTICE == level) { notice(message); }
			if (SyslogConstants.LEVEL_WARN == level) { warn(message); }
			if (SyslogConstants.LEVEL_ERROR == level) { error(message); }
			if (SyslogConstants.LEVEL_CRITICAL == level) { critical(message); }
			if (SyslogConstants.LEVEL_ALERT == level) { alert(message); }
			if (SyslogConstants.LEVEL_EMERGENCY == level) { emergency(message); }
		}

		public void log(int level, SyslogMessageIF message) {
			if (SyslogConstants.LEVEL_DEBUG == level) { debug(message); }
			if (SyslogConstants.LEVEL_INFO == level) { info(message); }
			if (SyslogConstants.LEVEL_NOTICE == level) { notice(message); }
			if (SyslogConstants.LEVEL_WARN == level) { warn(message); }
			if (SyslogConstants.LEVEL_ERROR == level) { error(message); }
			if (SyslogConstants.LEVEL_CRITICAL == level) { critical(message); }
			if (SyslogConstants.LEVEL_ALERT == level) { alert(message); }
			if (SyslogConstants.LEVEL_EMERGENCY == level) { emergency(message); }
		}

		public void shutdown() throws SyslogRuntimeException { }
	}
	
	public static class FakeSyslogConfig implements SyslogConfigIF {
		private static final long serialVersionUID = -5349124688260481740L;

		public void addBackLogHandler(SyslogBackLogHandlerIF backLogHandler) { }

		public void addMessageModifier(SyslogMessageModifierIF messageModifier) { }

		public String getCharSet() { return SyslogConstants.CHAR_SET_DEFAULT; }

		public int getFacility() { return SyslogConstants.SYSLOG_FACILITY_DEFAULT; }

		public String getHost() { return SyslogConstants.SYSLOG_HOST_DEFAULT; }

		public String getIdent() { return ""; }

		public String getLocalName() { return null; }

		public int getPort() { return SyslogConstants.SYSLOG_PORT_DEFAULT; }

		public boolean isTruncateMessage() { return SyslogConstants.TRUNCATE_MESSAGE_DEFAULT; }
		
		public int getMaxMessageLength() { return SyslogConstants.MAX_MESSAGE_LENGTH_DEFAULT; }

		public Class getSyslogClass() { return FakeSyslog.class; }

		public void insertBackLogHandler(int index, SyslogBackLogHandlerIF backLogHandler) { }
		
		public void insertMessageModifier(int index, SyslogMessageModifierIF messageModifier) { }
		
		public boolean isIncludeIdentInMessageModifier() { return SyslogConstants.INCLUDE_IDENT_IN_MESSAGE_MODIFIER_DEFAULT; }

		public boolean isSendLocalName() { return SyslogConstants.SEND_LOCAL_NAME_DEFAULT; }

		public boolean isSendLocalTimestamp() { return SyslogConstants.SEND_LOCAL_TIMESTAMP_DEFAULT; }

		public boolean isThrowExceptionOnInitialize() { return SyslogConstants.THROW_EXCEPTION_ON_INITIALIZE_DEFAULT; }

		public boolean isThrowExceptionOnWrite() { return SyslogConstants.THROW_EXCEPTION_ON_WRITE_DEFAULT; }

		public void removeAllBackLogHandlers() { }

		public void removeAllMessageModifiers() { }

		public void removeBackLogHandler(SyslogBackLogHandlerIF backLogHandler) { }

		public void removeMessageModifier(SyslogMessageModifierIF messageModifier) { }
		
		public void setCharSet(String charSet) { }

		public void setFacility(int facility) { }

		public void setFacility(String facilityName) { }

		public void setHost(String host) throws SyslogRuntimeException { }

		public void setIdent(String ident) { }

		public void setLocalName(String localName) { }
		
		public void setIncludeIdentInMessageModifier(boolean throwExceptionOnInitialize) { }

		public void setPort(int port) throws SyslogRuntimeException { }

		public void setSendLocalName(boolean sendLocalName) { }

		public void setSendLocalTimestamp(boolean sendLocalTimestamp) { }
		
		public void setThrowExceptionOnInitialize(boolean throwExceptionOnInitialize) { }

		public void setThrowExceptionOnWrite(boolean throwExceptionOnWrite) { }

		public void setTruncateMessage(boolean truncateMessage) { }
		
		public void setMaxMessageLength(int maxMessageLength) { }

		public boolean isUseStructuredData() { return USE_STRUCTURED_DATA_DEFAULT; }

		public void setUseStructuredData(boolean useStructuredData) { }			
	}
	
	public static class FakeSyslogMessage implements SyslogMessageIF {
		private static final long serialVersionUID = 7448036571948286738L;

		public String createMessage() {
			return "fake message";
		}
	}
	
	public void testMultipleSyslog() {
		FakeSyslogConfig config1 = new FakeSyslogConfig();
		FakeSyslog fake1 = (FakeSyslog) Syslog.createInstance("fake1", config1);

		FakeSyslogConfig config2 = new FakeSyslogConfig();
		FakeSyslog fake2 = (FakeSyslog) Syslog.createInstance("fake2", config2);

		MultipleSyslogConfig config = new MultipleSyslogConfig();
		config.addProtocol("fake1");
		config.addProtocol("fake2");
		
		SyslogIF syslog = Syslog.createInstance("multiple",config);
		
		assertEquals(0,fake1.total);
		assertEquals(0,fake2.total);
		
		syslog.debug("test");
		assertEquals(1,fake1.total);
		assertEquals(1,fake2.total);

		syslog.debug(new FakeSyslogMessage());
		assertEquals(1 + 2,fake1.total);
		assertEquals(1 + 2,fake2.total);

		syslog.info("test");
		assertEquals(1 + 2 + 4,fake1.total);
		assertEquals(1 + 2 + 4,fake2.total);
		
		syslog.info(new FakeSyslogMessage());
		assertEquals(1 + 2 + 4 + 8,fake1.total);
		assertEquals(1 + 2 + 4 + 8,fake2.total);
		
		syslog.notice("test");
		assertEquals(1 + 2 + 4 + 8 + 16,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16,fake2.total);
		
		syslog.notice(new FakeSyslogMessage());
		assertEquals(1 + 2 + 4 + 8 + 16 + 32,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32,fake2.total);

		syslog.warn("test");
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64,fake2.total);
		
		syslog.warn(new FakeSyslogMessage());
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128,fake2.total);

		syslog.error("test");
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256,fake2.total);
		
		syslog.error(new FakeSyslogMessage());
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512,fake2.total);

		syslog.critical("test");
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024,fake2.total);
		
		syslog.critical(new FakeSyslogMessage());
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024 + 2048,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024 + 2048,fake2.total);

		syslog.alert("test");
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024 + 2048 + 4096,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024 + 2048 + 4096,fake2.total);
		
		syslog.alert(new FakeSyslogMessage());
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024 + 2048 + 4096 + 8192,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024 + 2048 + 4096 + 8192,fake2.total);

		syslog.emergency("test");
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024 + 2048 + 4096 + 8192 + 16384,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024 + 2048 + 4096 + 8192 + 16384,fake2.total);
		
		syslog.emergency(new FakeSyslogMessage());
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024 + 2048 + 4096 + 8192 + 16384 + 32768,fake1.total);
		assertEquals(1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024 + 2048 + 4096 + 8192 + 16384 + 32768,fake2.total);
	}
}
