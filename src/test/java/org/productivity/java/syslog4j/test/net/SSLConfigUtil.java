package org.productivity.java.syslog4j.test.net;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.productivity.java.syslog4j.impl.net.tcp.ssl.SSLTCPNetSyslogConfigIF;
import org.productivity.java.syslog4j.server.impl.net.tcp.ssl.SSLTCPNetSyslogServerConfigIF;

public class SSLConfigUtil {

  public static final String KEYSTORE = "certs/ssltest.jks";

  public static final String PASSWORD = "ssltest";

  public static void configure(SSLTCPNetSyslogServerConfigIF config)
       throws Exception {
    final String keyStorePath = keyStorePath();

    config.setKeyStore(keyStorePath);
    config.setKeyStorePassword(PASSWORD);

    config.setTrustStore(keyStorePath);
    config.setTrustStorePassword(PASSWORD);
  }

  public static void configure(SSLTCPNetSyslogConfigIF config)
      throws Exception {
    final String keyStorePath = keyStorePath();

    config.setKeyStore(keyStorePath);
    config.setKeyStorePassword(PASSWORD);

    config.setTrustStore(keyStorePath);
    config.setTrustStorePassword(PASSWORD);
  }

  private static String keyStorePath() throws IOException, URISyntaxException {
    final URL url = SSLConfigUtil.class.getClassLoader().getResource(KEYSTORE);
    return new File(url.toURI()).getAbsolutePath();
  }

}
