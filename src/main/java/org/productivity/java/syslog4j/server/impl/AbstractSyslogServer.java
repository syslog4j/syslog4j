package org.productivity.java.syslog4j.server.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.productivity.java.syslog4j.SyslogCharSetIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerEventHandlerIF;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionEventHandlerIF;
import org.productivity.java.syslog4j.server.SyslogServerSessionlessEventHandlerIF;
import org.productivity.java.syslog4j.server.impl.event.SyslogServerEvent;
import org.productivity.java.syslog4j.server.impl.event.structured.StructuredSyslogServerEvent;
import org.productivity.java.syslog4j.util.SyslogUtility;

/**
* AbstractSyslogServer provides a base abstract implementation of the SyslogServerIF.
* 
* <p>Syslog4j is licensed under the Lesser GNU Public License v2.1.  A copy
* of the LGPL license is available in the META-INF folder in all
* distributions of Syslog4j and in the base directory of the "doc" ZIP.</p>
* 
* @author &lt;syslog4j@productivity.org&gt;
* @version $Id: AbstractSyslogServer.java,v 1.12 2011/01/11 05:11:13 cvs Exp $
* 
* History
* =======
* 02.07.2020 WLI ORC-3824 the method Sessions.removeSocket() didn't remove the Socket from the map 
*                resulting in an OutOfMemoryError after the syslog server is running for some time.
*/
public abstract class AbstractSyslogServer implements SyslogServerIF {
	
	public static class Sessions {
		
		private ConcurrentHashMap<Socket, Map<SyslogServerEventHandlerIF, Object>> sessions = new ConcurrentHashMap<>();

		public int size() {
			return sessions.size();
		}

		public void addSocket(Socket socket) {
			sessions.put(socket,new HashMap<SyslogServerEventHandlerIF, Object>());
		}

		public void removeSocket(Socket socket) {
			sessions.remove(socket); // ORC-3824
		}
		
		public void closeAll() { // ORC-3824
			for (Socket socket: sessions.keySet()) {
				try {
					socket.close();
				} catch (IOException e) {
					// intentionally left blank because closAll is called on shutdown only
				}
			}
			sessions.clear();
		}
		
		public void addSession(Socket socket, SyslogServerEventHandlerIF eventHandler, Object session) {
			Map<SyslogServerEventHandlerIF, Object> handlerMap = sessions.get(socket);
			
			if (handlerMap == null) {
				handlerMap = new HashMap<SyslogServerEventHandlerIF, Object>();
				sessions.put(socket,handlerMap);
			}
			
			handlerMap.put(eventHandler,session);
		}

		public Object getSession(Socket socket, SyslogServerEventHandlerIF eventHandler) {
			Map<SyslogServerEventHandlerIF, Object> handlerMap = sessions.get(socket);
			return handlerMap.get(eventHandler);
		}
	}
	
	protected String syslogProtocol = null;
	protected AbstractSyslogServerConfig syslogServerConfig = null;
	protected Thread thread = null;
	
	protected boolean shutdown = false;
	
	public void initialize(String protocol, SyslogServerConfigIF config) throws SyslogRuntimeException {
		this.syslogProtocol = protocol;
		
		try {
			this.syslogServerConfig = (AbstractSyslogServerConfig) config;
			
		} catch (ClassCastException cce) {
			throw new SyslogRuntimeException(cce);
		}
		
		initialize();
	}
	
	public String getProtocol() {
		return this.syslogProtocol;
	}

	public SyslogServerConfigIF getConfig() {
		return this.syslogServerConfig;
	}
	
	protected abstract void initialize() throws SyslogRuntimeException;
	
	public void shutdown() throws SyslogRuntimeException {
		this.shutdown = true;
	}

	public Thread getThread() {
		return this.thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}
	
	protected static boolean isStructuredMessage(SyslogCharSetIF syslogCharSet, byte[] receiveData) {
		String receiveDataString = SyslogUtility.newString(syslogCharSet, receiveData);

		boolean isStructuredMessage = isStructuredMessage(syslogCharSet,receiveDataString);
		
		return isStructuredMessage;
	}

	protected static boolean isStructuredMessage(SyslogCharSetIF syslogCharSet, String receiveData) {
		int idx = receiveData.indexOf('>');

		if (idx != -1) {
			// If there's a numerical VERSION field after the <priority>, return true.
			if (receiveData.length() > idx + 1 && Character.isDigit(receiveData.charAt(idx + 1))) {
				return true;
			}
		}

		return false;
	}

	protected static SyslogServerEventIF createEvent(SyslogServerConfigIF serverConfig, byte[] lineBytes, int lineBytesLength, InetAddress inetAddr) {
		SyslogServerEventIF event = null;
		
		if (serverConfig.isUseStructuredData() && AbstractSyslogServer.isStructuredMessage(serverConfig,lineBytes)) {
			event = new StructuredSyslogServerEvent(lineBytes,lineBytesLength,inetAddr);
			
			if (serverConfig.getDateTimeFormatter() != null) {
				((StructuredSyslogServerEvent) event).setDateTimeFormatter(serverConfig.getDateTimeFormatter());
			}
			
		} else {
			event = new SyslogServerEvent(lineBytes,lineBytesLength,inetAddr);
		}		
		
		return event;
	}
	
	protected static SyslogServerEventIF createEvent(SyslogServerConfigIF serverConfig, String line, InetAddress inetAddr) {
		SyslogServerEventIF event = null;
		
		if (serverConfig.isUseStructuredData() && AbstractSyslogServer.isStructuredMessage(serverConfig,line)) {
			event = new StructuredSyslogServerEvent(line,inetAddr);
			
		} else {
			event = new SyslogServerEvent(line,inetAddr);
		}		
		
		return event;
	}
	
	public static void handleInitialize(SyslogServerIF syslogServer) {
		List<?> eventHandlers = syslogServer.getConfig().getEventHandlers();
		
		for(int i=0; i<eventHandlers.size(); i++) {
			SyslogServerEventHandlerIF eventHandler = (SyslogServerEventHandlerIF) eventHandlers.get(i);

			try {
				eventHandler.initialize(syslogServer);
				
			} catch (Exception exception) {
				//
			}
		}
	}

	public static void handleDestroy(SyslogServerIF syslogServer) {
		List<?> eventHandlers = syslogServer.getConfig().getEventHandlers();
		
		for(int i=0; i<eventHandlers.size(); i++) {
			SyslogServerEventHandlerIF eventHandler = (SyslogServerEventHandlerIF) eventHandlers.get(i);

			try {
				eventHandler.destroy(syslogServer);
					
			} catch (Exception exception) {
				//
			}
		}
	}

	public static void handleSessionOpen(Sessions sessions, SyslogServerIF syslogServer, Socket socket) {
		List<?> eventHandlers = syslogServer.getConfig().getEventHandlers();
		
		for(int i=0; i<eventHandlers.size(); i++) {
			SyslogServerEventHandlerIF eventHandler = (SyslogServerEventHandlerIF) eventHandlers.get(i);
			
			if (eventHandler instanceof SyslogServerSessionEventHandlerIF) {
				try {
					Object session = ((SyslogServerSessionEventHandlerIF) eventHandler).sessionOpened(syslogServer,socket.getRemoteSocketAddress());
					
					if (session != null) {
						sessions.addSession(socket,eventHandler,session);
					}
					
				} catch (Exception exception) {
					try {
						((SyslogServerSessionEventHandlerIF) eventHandler).exception(null,syslogServer,socket.getRemoteSocketAddress(),exception);
						
					} catch (Exception e) {
						//
					}
				}
			}
		}
	}

	public static void handleSessionClosed(Sessions sessions, SyslogServerIF syslogServer, Socket socket, boolean timeout) {
		List<?> eventHandlers = syslogServer.getConfig().getEventHandlers();
		
		for(int i=0; i<eventHandlers.size(); i++) {
			SyslogServerEventHandlerIF eventHandler = (SyslogServerEventHandlerIF) eventHandlers.get(i);

			if (eventHandler instanceof SyslogServerSessionEventHandlerIF) {
				Object session = sessions.getSession(socket,eventHandler);
				
				try {
					((SyslogServerSessionEventHandlerIF) eventHandler).sessionClosed(session,syslogServer,socket.getRemoteSocketAddress(),timeout);
					
				} catch (Exception exception) {
					try {
						((SyslogServerSessionEventHandlerIF) eventHandler).exception(session,syslogServer,socket.getRemoteSocketAddress(),exception);
						
					} catch (Exception e) {
						//
					}
				}
			}
		}
	}

	public static void handleEvent(Sessions sessions, SyslogServerIF syslogServer, DatagramPacket packet, SyslogServerEventIF event) {
		handleEvent(sessions,syslogServer,null,packet.getSocketAddress(),event);
	}

	public static void handleEvent(Sessions sessions, SyslogServerIF syslogServer, Socket socket, SyslogServerEventIF event) {
		handleEvent(sessions,syslogServer,socket,socket.getRemoteSocketAddress(),event);
	}

	protected static void handleEvent(Sessions sessions, SyslogServerIF syslogServer, Socket socket, SocketAddress socketAddress, SyslogServerEventIF event) {
		List<?> eventHandlers = syslogServer.getConfig().getEventHandlers();
		
		for(int i=0; i<eventHandlers.size(); i++) {
			SyslogServerEventHandlerIF eventHandler = (SyslogServerEventHandlerIF) eventHandlers.get(i);
			
			Object session = (sessions != null && socket != null) ? sessions.getSession(socket,eventHandler) : null;
			
			if (eventHandler instanceof SyslogServerSessionEventHandlerIF) {
				try {
					((SyslogServerSessionEventHandlerIF) eventHandler).event(session,syslogServer,socketAddress,event);
					
				} catch (Exception exception) {
					try {
						((SyslogServerSessionEventHandlerIF) eventHandler).exception(session,syslogServer,socketAddress,exception);
					
					} catch (Exception e) {
						//
					}
				}
				
			} else if (eventHandler instanceof SyslogServerSessionlessEventHandlerIF) {
				try {
					((SyslogServerSessionlessEventHandlerIF) eventHandler).event(syslogServer,socketAddress,event);
					
				} catch (Exception exception) {
					try {
						((SyslogServerSessionlessEventHandlerIF) eventHandler).exception(syslogServer,socketAddress,exception);
						
					} catch (Exception e) {
						//
					}
				}
			}
		}
	}

	public static void handleException(Object session, SyslogServerIF syslogServer, SocketAddress socketAddress, Exception exception) {
		List<?> eventHandlers = syslogServer.getConfig().getEventHandlers();
		
		for(int i=0; i<eventHandlers.size(); i++) {
			SyslogServerEventHandlerIF eventHandler = (SyslogServerEventHandlerIF) eventHandlers.get(i);

			if (eventHandler instanceof SyslogServerSessionEventHandlerIF) {
				try {
					((SyslogServerSessionEventHandlerIF) eventHandler).exception(session,syslogServer,socketAddress,exception);
					
				} catch (Exception e) {
					//
				}
				
			} else if (eventHandler instanceof SyslogServerSessionlessEventHandlerIF) {
				try {
					((SyslogServerSessionlessEventHandlerIF) eventHandler).exception(syslogServer,socketAddress,exception);
					
				} catch (Exception e) {
					//					
				}
			}
		}
	}
}
