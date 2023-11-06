package org.productivity.java.syslog4j.impl.net.tcp.ssl;

import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;

import javax.net.ssl.KeyManager;

/**
* SSLTCPNetSyslogConfig is an extension of TCPNetSyslogConfig that provides
* configuration support for TCP/IP-based (over SSL/TLS) syslog clients.
* 
* <p>Syslog4j is licensed under the Lesser GNU Public License v2.1.  A copy
* of the LGPL license is available in the META-INF folder in all
* distributions of Syslog4j and in the base directory of the "doc" ZIP.</p>
 *
 * <p>
 * <b>History</b>
 * <pre>
 *  26.02.2020  AKN  OFIND-448  Add {@link #keyManagers} attribute holding the keystore information for the client certificate
 *                              authentication.
 * </pre>
 * </p>
* 
* @author &lt;syslog4j@productivity.org&gt;
* @version $Id: SSLTCPNetSyslogConfig.java,v 1.2 2009/03/29 17:38:58 cvs Exp $
*/
public class SSLTCPNetSyslogConfig extends TCPNetSyslogConfig implements SSLTCPNetSyslogConfigIF {
	private static final long serialVersionUID = 5569086213824510834L;
	private KeyManager[] keyManagers = null;

	public SSLTCPNetSyslogConfig() {
	}

	public SSLTCPNetSyslogConfig(int facility, String host, int port) {
		super(facility, host, port);
	}

	public SSLTCPNetSyslogConfig(int facility, String host) {
		super(facility, host);
	}

	public SSLTCPNetSyslogConfig(int facility) {
		super(facility);
	}

	public SSLTCPNetSyslogConfig(String host, int port) {
		super(host, port);
	}

	public SSLTCPNetSyslogConfig(String host) {
		super(host);
	}

	public Class getSyslogClass() {
		return SSLTCPNetSyslog.class;
	}

	public Class getSyslogWriterClass() {
		return SSLTCPNetSyslogWriter.class;
	}

	public void setKeystore(KeyManager[] keyManagers) {
		this.keyManagers = keyManagers;
	}

	public KeyManager[] getKeystore() {
		return this.keyManagers;
	}

}
