package org.productivity.java.syslog4j.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class DummyTrustManager implements X509TrustManager {

	private final X509Certificate[] EMPTY_ISSUERS = new X509Certificate[0];

	public DummyTrustManager() {
	}

	@Override
	public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		// we trust all issuers
		return EMPTY_ISSUERS;
	}

}
