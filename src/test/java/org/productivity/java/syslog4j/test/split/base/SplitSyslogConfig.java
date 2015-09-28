package org.productivity.java.syslog4j.test.split.base;

import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.AbstractSyslogConfig;

public class SplitSyslogConfig extends AbstractSyslogConfig {
	private static final long serialVersionUID = 6192648434706811381L;

	public Class getSyslogClass() {
		return SplitSyslog.class;
	}

	public String getHost() {
		return null;
	}

	public String getSocketPath() {
		return null;
	}

	public int getPort() {
		return 0;
	}

	public void setHost(String host) throws SyslogRuntimeException {
		//
	}

	public void setSocketPath(String path) throws SyslogRuntimeException {
		//
	}

	public void setPort(int port) throws SyslogRuntimeException {
		//
	}

	public int getMaxQueueSize() {
		return 0;
	}

	public void setMaxQueueSize(int maxQueueSize) {
		//
	}
	
	
}
