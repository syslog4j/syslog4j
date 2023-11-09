package org.productivity.java.syslog4j.util;

import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;

import org.productivity.java.syslog4j.Syslog4jSSLContextBuilder;

public class Syslog4jSSLContextBuilderImpl implements Syslog4jSSLContextBuilder {

	private String keystoreType = "pkcs12";
	private String keystorePassword = "syslog4j";
	private String keyPassword = "syslog4j";
	private String keystorePath = "/config/security/syslog4jtest.pkcs12";
	private String sslKeyManagerFactoryAlgorithm = "SunX509";

	@Override
	public SSLSocketFactory newClientSSLSocketFactory(KeyManager[] keyManagers, String protocol) throws Exception {
		SSLContext sslContext = createSSLContext(keyManagers, protocol);
		return sslContext.getSocketFactory();
	}

	@Override
	public SSLServerSocketFactory newSSLServerSocketFactory() throws Exception {
		SSLContext sslContext = createSSLContext(null, "TLS");
		return sslContext.getServerSocketFactory();
	}

	private SSLContext createSSLContext(KeyManager[] keyManagers, String protocol) throws Exception {
		
		SSLContext context = SSLContext.getInstance(protocol);
		
		if (keyManagers == null) {
			KeyStore keyStoreContainer = loadDefaultKeyStore();
			keyManagers = createKeyManagersFromKeyStore(keyStoreContainer);
		}
		
		TrustManager[] trustManagers = new TrustManager[1];
		TrustManager myTrustManager = new DummyTrustManager();
		trustManagers[0] = myTrustManager;

		context.init(keyManagers, trustManagers, null);
		
		return context;
	}

	private KeyManager[] createKeyManagersFromKeyStore(KeyStore keyStoreContainer) throws Exception {
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(sslKeyManagerFactoryAlgorithm);
		keyManagerFactory.init(keyStoreContainer, keyPassword == null ? null : keyPassword.toCharArray());
		KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

		if (keyManagers[0] instanceof X509KeyManager) {
			keyManagers[0] = new AbstractWrapper((X509KeyManager) keyManagers[0]);
		}
		return keyManagers;
	}

	private KeyStore loadDefaultKeyStore() throws Exception {
		InputStream keystoreInputStream = this.getClass().getResourceAsStream(keystorePath);
		KeyStore keyStoreContainer = KeyStore.getInstance(keystoreType);
		char[] localKeyStorePassword = (keystorePassword == null) ? null : keystorePassword.toCharArray();
		keyStoreContainer.load(keystoreInputStream, localKeyStorePassword);
		return keyStoreContainer;
	}
	
	private static final class AbstractWrapper extends X509ExtendedKeyManager {

		private final X509KeyManager km;

		AbstractWrapper(X509KeyManager km) {
			this.km = km;
		}

		public String[] getClientAliases(String keyType, Principal[] issuers) {
			return km.getClientAliases(keyType, issuers);
		}

		public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
			return km.chooseClientAlias(keyType, issuers, socket);
		}

		public String[] getServerAliases(String keyType, Principal[] issuers) {
			return km.getServerAliases(keyType, issuers);
		}

		public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
			return km.chooseServerAlias(keyType, issuers, socket);
		}

		public X509Certificate[] getCertificateChain(String alias) {
			return km.getCertificateChain(alias);
		}

		public PrivateKey getPrivateKey(String alias) {
			return km.getPrivateKey(alias);
		}

		public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
			return km.chooseClientAlias(keyType, issuers, null);
		}

		public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
			return km.chooseServerAlias(keyType, issuers, null);
		}
	}

}
