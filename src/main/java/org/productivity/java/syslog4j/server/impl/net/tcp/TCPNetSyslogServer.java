package org.productivity.java.syslog4j.server.impl.net.tcp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.net.ServerSocketFactory;

import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.impl.AbstractSyslogServer;
import org.productivity.java.syslog4j.util.SyslogUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* TCPNetSyslogServer provides a simple threaded TCP/IP server implementation.
* 
* <p>Syslog4j is licensed under the Lesser GNU Public License v2.1.  A copy
* of the LGPL license is available in the META-INF folder in all
* distributions of Syslog4j and in the base directory of the "doc" ZIP.</p>
* 
* @author &lt;syslog4j@productivity.org&gt;
* @version $Id: TCPNetSyslogServer.java,v 1.23 2010/11/28 22:07:57 cvs Exp $
* 
* History
* =======
* 14.09.2017 WLI At 03.03.2017 Marjan Stankovic added ServerSocket.setNeedClientAuth(true).
*                Therefore the client sent a certificate which then caused an Authentication failure.
* 02.07.2020 WLI ORC-3824 removed synchronization on sessions object.
*/
public class TCPNetSyslogServer extends AbstractSyslogServer {
	
	protected static Logger logger = LoggerFactory.getLogger( "com.soffxt.syslog" );

	public static class TCPNetSyslogSocketHandler implements Runnable {
		protected SyslogServerIF server = null;
		protected Socket socket = null;
		protected Sessions sessions = null;
//		protected BufferedReader br;
		protected BufferedInputStream bis;
		
		public TCPNetSyslogSocketHandler(Sessions sessions, SyslogServerIF server, Socket socket) {
			this.sessions = sessions;
			this.server = server;
			this.socket = socket;
			
			this.sessions.addSocket(socket); // ORC-3824
		}
				
		public String readLine() throws IOException {
			int first = bis.read();
			if (first < 0)
				return null;
			
			if (Character.isDigit(first)) {
				int digit = first - '0';
				int buflen = digit;
				int ch = bis.read();
				while (Character.isDigit(ch)) {
					digit = ch - '0';
					buflen = buflen * 10 + digit;
					ch = bis.read();
				}
				byte [] buf = new byte[buflen];
				int rest = buflen;
				int off = 0;
				while (rest > 0) {
					int rcnt = bis.read(buf, off, rest);
					if (rcnt < 0)
						break;
					off += rcnt;
					rest -= rcnt;
				}
				String result = new String(buf, 0, off, "UTF-8");
				if (result.endsWith("\n")) {
					if (result.endsWith("\r\n")) {
						result = result.substring(0, result.length() - 2);
					}
					else result = result.substring(0, result.length() - 1);
				}
				return result;
			}
			else {
				ByteArrayOutputStream bout = new ByteArrayOutputStream(500);
				bout.write(first);
//				buf[0] = (byte)(first & 0xFF);
				int cnt = 1;
				int b = bis.read();
				int last = -1;
				while (b > 0 && b != 10) {
//					buf[cnt++] = (byte)(b & 0xFF);
					bout.write(b);
					cnt++;
					last = b;
					b = bis.read();
				}
				if (last == 13) {
					cnt--;
				}
				
				byte [] buf = bout.toByteArray();
				
				String rest = new String(buf, 0, cnt, "UTF-8");
				return rest;
			}
		}
		
		public void run() {
			boolean timeout = false;
			
			try {
//				br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				bis = new BufferedInputStream(this.socket.getInputStream());
				String line = readLine(); // WL: br.readLine();
				
				if (line != null) {
					AbstractSyslogServer.handleSessionOpen(this.sessions,this.server,this.socket);
				}
				
				while (line != null && line.length() != 0) {
					logger.debug("Read line: {}", line);
					SyslogServerEventIF event = createEvent(this.server.getConfig(),line,this.socket.getInetAddress());
					
					AbstractSyslogServer.handleEvent(this.sessions,this.server,this.socket,event);

					line = readLine(); // WL: br.readLine();
				}
				
			} catch (SocketTimeoutException ste) {
				logger.warn("timeout when reading syslog socket", ste); // WL ORC-8935 (spelling correction).
				timeout = true;
				
			} catch (SocketException se) {
				AbstractSyslogServer.handleException(this.sessions,this.server,this.socket.getRemoteSocketAddress(),se);
				
				if ("socket closed".equalsIgnoreCase(se.getMessage())) {
					// System.out.println(se.getMessage());
					
				} else {
					logger.warn("reading syslog socket failed", se); // WL
				}
				
			} catch (IOException ioe) {
				logger.warn("reading syslog failed", ioe); // WL
				AbstractSyslogServer.handleException(this.sessions,this.server,this.socket.getRemoteSocketAddress(),ioe);
			}
			
			try {
				AbstractSyslogServer.handleSessionClosed(this.sessions,this.server,this.socket,timeout);
				
				this.sessions.removeSocket(this.socket);

				this.socket.close();
				
			} catch (IOException ioe) {
				AbstractSyslogServer.handleException(this.sessions,this.server,this.socket.getRemoteSocketAddress(),ioe);
			}
		}
	}

	protected ServerSocket serverSocket = null;
	
	protected final Sessions sessions = new Sessions();
	
	protected TCPNetSyslogServerConfigIF tcpNetSyslogServerConfig = null;
	
	public void initialize() throws SyslogRuntimeException {
		this.tcpNetSyslogServerConfig = null;
		
		try {
			this.tcpNetSyslogServerConfig = (TCPNetSyslogServerConfigIF) this.syslogServerConfig;
			
		} catch (ClassCastException cce) {
			throw new SyslogRuntimeException("config must be of type TCPNetSyslogServerConfig");
		}
		
		if (this.syslogServerConfig == null) {
			throw new SyslogRuntimeException("config cannot be null");
		}

		if (this.tcpNetSyslogServerConfig.getBacklog() < 1) {
			this.tcpNetSyslogServerConfig.setBacklog(SyslogConstants.SERVER_SOCKET_BACKLOG_DEFAULT);
		}
	}
	
	public Sessions getSessions() {
		return this.sessions;
	}
	
	public synchronized void shutdown() {
		super.shutdown();
		
		try {
			if (this.serverSocket != null) {
				if (this.syslogServerConfig.getShutdownWait() > 0) {
					SyslogUtility.sleep(this.syslogServerConfig.getShutdownWait());
				}
				
				this.serverSocket.close();
			}
			
			sessions.closeAll(); // ORC-3824
			
		} catch (IOException ioe) {
			//
		}
		
		if (this.thread != null) {
			this.thread.interrupt();
			this.thread = null;
		}
	}

	protected ServerSocketFactory getServerSocketFactory() throws Exception {
		ServerSocketFactory serverSocketFactory = ServerSocketFactory.getDefault();
		
		return serverSocketFactory;
	}
	
	protected ServerSocket createServerSocket() throws Exception {
		ServerSocket newServerSocket = null;
		
		ServerSocketFactory factory = getServerSocketFactory();
		
		if (this.syslogServerConfig.getHost() != null) {
			InetAddress inetAddress = InetAddress.getByName(this.syslogServerConfig.getHost());
				
			newServerSocket = factory.createServerSocket(this.syslogServerConfig.getPort(),this.tcpNetSyslogServerConfig.getBacklog(),inetAddress); 
				
		} else {
			if (this.tcpNetSyslogServerConfig.getBacklog() < 1) {
				newServerSocket = factory.createServerSocket(this.syslogServerConfig.getPort());
				
			} else {
				newServerSocket = factory.createServerSocket(this.syslogServerConfig.getPort(),this.tcpNetSyslogServerConfig.getBacklog());				
			}
		}
/*	WLI: Marjan Stankovic added the following 4 lines. Therefore the client sent a certificate which then caused an Authentication failure.
		// mutual authentication is required
		if (newServerSocket instanceof SSLServerSocket) {
			((SSLServerSocket) newServerSocket).setNeedClientAuth( true );
		}
*/		
		return newServerSocket;
	}

	public void run() {
		try {
			this.serverSocket = createServerSocket();
			this.shutdown = false;
			
		} catch (SocketException se) {
			throw new SyslogRuntimeException(se);

		} catch (IOException ioe) {
			throw new SyslogRuntimeException(ioe);

		} catch (Exception e) {
			e.printStackTrace();
			throw new SyslogRuntimeException(e);
		}

		handleInitialize(this);
		
		while(!this.shutdown) {
			try {
				Socket socket = this.serverSocket.accept();
				
				if (this.tcpNetSyslogServerConfig.getTimeout() > 0) {
					socket.setSoTimeout(this.tcpNetSyslogServerConfig.getTimeout());
				}
				
				if (this.tcpNetSyslogServerConfig.getMaxActiveSockets() > 0 && this.sessions.size() >= this.tcpNetSyslogServerConfig.getMaxActiveSockets()) {
					if (this.tcpNetSyslogServerConfig.getMaxActiveSocketsBehavior() == TCPNetSyslogServerConfigIF.MAX_ACTIVE_SOCKETS_BEHAVIOR_REJECT) {
						try {
//							logger.info("Server: reject socket"); // WL
							socket.close();
							
						} catch (Exception e) {
							//
						}
						
						socket = null;
						
					} else if (this.tcpNetSyslogServerConfig.getMaxActiveSocketsBehavior() == TCPNetSyslogServerConfigIF.MAX_ACTIVE_SOCKETS_BEHAVIOR_BLOCK) {
						while (!this.shutdown && this.sessions.size() >= this.tcpNetSyslogServerConfig.getMaxActiveSockets() && socket.isConnected() && !socket.isClosed()) {
							SyslogUtility.sleep(SyslogConstants.THREAD_LOOP_INTERVAL_DEFAULT);
						}
					}
				}

				if (socket != null) {
					TCPNetSyslogSocketHandler handler = new TCPNetSyslogSocketHandler(this.sessions,this,socket);
					
					Thread t = new Thread(handler);
					
					t.start();
				}
				
			} catch (SocketException se) {
				if ("Socket closed".equals(se.getMessage())) {
					this.shutdown = true;
					
				} else {
					//
				}
				
			} catch (IOException ioe) {
				//
			}
		}
		
		handleDestroy(this);
	}
}
