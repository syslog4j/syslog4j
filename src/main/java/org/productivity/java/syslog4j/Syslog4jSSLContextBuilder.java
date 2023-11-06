package org.productivity.java.syslog4j;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

public interface Syslog4jSSLContextBuilder {

	SSLSocketFactory newClientSSLSocketFactory(KeyManager[] keyManagers, String protocol) throws Exception;
	
	SSLServerSocketFactory newSSLServerSocketFactory() throws Exception;

}
