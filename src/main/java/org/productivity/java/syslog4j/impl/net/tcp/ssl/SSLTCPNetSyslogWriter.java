package org.productivity.java.syslog4j.impl.net.tcp.ssl;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;

import org.productivity.java.syslog4j.Syslog4jSSLContextBuilder;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogWriter;
import org.productivity.java.syslog4j.util.SyslogUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* SSLTCPNetSyslogWriter is an implementation of Runnable that supports sending
* TCP/IP-based (over SSL/TLS) messages within a separate Thread.
* 
* <p>When used in "threaded" mode (see TCPNetSyslogConfig for the option),
* a queuing mechanism is used (via LinkedList).</p>
* 
* <p>Syslog4j is licensed under the Lesser GNU Public License v2.1.  A copy
* of the LGPL license is available in the META-INF folder in all
* distributions of Syslog4j and in the base directory of the "doc" ZIP.</p>
 *
 * <p>
 * <b>History</b>
 * <pre>
 *  26.02.2020  AKN  OFIND-448  Add logging to {@link #obtainSocketFactory()} method
 *  26.02.2020  AKN  OFIND-448  Refactor {@link #obtainSocketFactory()} to load {@link KeyManager}s from {@link SSLTCPNetSyslogConfig}
 * </pre>
 * </p>
* 
* @author &lt;syslog4j@productivity.org&gt;
* @version $Id: SSLTCPNetSyslogWriter.java,v 1.4 2009/03/29 17:38:58 cvs Exp $
*/
public class SSLTCPNetSyslogWriter extends TCPNetSyslogWriter {

	static private Logger logger = LoggerFactory.getLogger("com.soffxt.syslog");

	private static final long serialVersionUID = 8944446235285662244L;
	private String             protocol                      = "TLS";

	@Override
	protected SocketFactory obtainSocketFactory() throws Exception {

		logger.info("SSLTCPNetSyslogWriter.obtainSocketFactory()" );
		Syslog4jSSLContextBuilder builder = SyslogUtility.getSSLContextBuilder();
		if (this.syslogConfig instanceof SSLTCPNetSyslogConfig) {
			logger.info("SSLTCPNetSyslogConfig found" );

			SSLTCPNetSyslogConfig sslConfig = (SSLTCPNetSyslogConfig) this.syslogConfig;
			KeyManager[] keyManagers = sslConfig.getKeystore();
			return builder.newClientSSLSocketFactory(keyManagers, protocol);
		}
		else {
			return builder.newClientSSLSocketFactory(null, protocol);
		}
	}
}
