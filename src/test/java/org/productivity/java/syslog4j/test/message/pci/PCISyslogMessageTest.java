package org.productivity.java.syslog4j.test.message.pci;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.impl.message.pci.PCISyslogMessage;
import org.productivity.java.syslog4j.impl.message.pci.PCISyslogMessageIF;

public class PCISyslogMessageTest extends TestCase {
	public class TestSyslogMessage implements PCISyslogMessageIF {
		public String getUserId() {
			return "a2";
		}

		public String getEventType() {
			return "b2";
		}

		public String getDate() {
			return "c2";
		}

		public String getTime() {
			return "d2";
		}

		public String getStatus() {
			return "e2";
		}

		public String getOrigination() {
			return "f2";
		}

		public String getAffectedResource() {
			return "g2";
		}
	}
	
	public void testPCISyslogMessageDefaultConstructor() throws Exception {
		PCISyslogMessage event = new PCISyslogMessage();
		
		String date = event.getDate();
		String time = event.getTime();
		event.setTime(time);
		String localHostName = InetAddress.getLocalHost().getHostName();
		
		assertEquals("undefined undefined " + date + " " + time + " undefined " + localHostName + " undefined",event.createMessage());
	}

	public void testPCISyslogMessage() throws Exception {
		PCISyslogMessage event1 = new PCISyslogMessage("admin123","password_reset","11/01/2008","00:30:00","success","database_server","user123");
		
		assertEquals("admin123 password_reset 11/01/2008 00:30:00 success database_server user123",event1.createMessage());

		//
		
		PCISyslogMessage event2 = new PCISyslogMessage("admin123","password_reset","11/01/2008","00:30:00","success","user123");

		String localHostName = InetAddress.getLocalHost().getHostName();
		assertEquals("admin123 password_reset 11/01/2008 00:30:00 success " + localHostName + " user123",event2.createMessage());

		//
		
		PCISyslogMessage event3 = new PCISyslogMessage("admin123","password_reset","success","user123");
		String time3 = event3.getTime();
		event3.setTime(time3);

		assertEquals("admin123 password_reset " + event3.getDate() + " " + event3.getTime() + " success " + localHostName + " user123",event3.createMessage());

		//
		
		PCISyslogMessage event4 = new PCISyslogMessage("admin123","password_reset","success","database_server","user123");
		String time4 = event4.getTime();
		event4.setTime(time4);

		assertEquals("admin123 password_reset " + event4.getDate() + " " + event4.getTime() + " success database_server user123",event4.createMessage());

		//
		
		PCISyslogMessage event5 = new PCISyslogMessage("admin123","password_reset",new Date(),"success","user123");
		String time5 = event5.getTime();
		event5.setTime(time5);

		assertEquals("admin123 password_reset " + event3.getDate() + " " + event3.getTime() + " success " + localHostName + " user123",event3.createMessage());

		//
		
		PCISyslogMessage event6 = new PCISyslogMessage("admin123","password_reset",new Date(),"success","database_server","user123");
		String time6 = event6.getTime();
		event6.setTime(time6);

		assertEquals("admin123 password_reset " + event4.getDate() + " " + event4.getTime() + " success database_server user123",event4.createMessage());
	}

	public void testPCISyslogMessageNulls() throws Exception {
		String localHostName = InetAddress.getLocalHost().getHostName();
		
		PCISyslogMessage event = null;
		
		event = new PCISyslogMessage(null,null,null,null);
		event.setTime(event.getTime());
		assertEquals("undefined undefined " + event.getDate() + " " + event.getTime() + " undefined " + localHostName + " undefined",event.createMessage());

		event = new PCISyslogMessage(null,null,new Date(),null,null);
		event.setTime(event.getTime());
		assertEquals("undefined undefined " + event.getDate() + " " + event.getTime() + " undefined " + localHostName + " undefined",event.createMessage());
		
		event = new PCISyslogMessage(null,null,new Date(),null,null,null);
		event.setTime(event.getTime());
		assertEquals("undefined undefined " + event.getDate() + " " + event.getTime() + " undefined " + localHostName + " undefined",event.createMessage());

		event = new PCISyslogMessage(null,null,null,null,null,null,null);
		event.setTime(event.getTime());
		assertEquals("undefined undefined " + event.getDate() + " " + event.getTime() + " undefined " + localHostName + " undefined",event.createMessage());
	}

	public void testPCISyslogMessageReplaceDelimiters() throws Exception {
		String localHostName = InetAddress.getLocalHost().getHostName();
		
		PCISyslogMessage event = null;
		
		event = new PCISyslogMessage("a b c"," d e f","g h i "," j k l ");
		event.setTime(event.getTime());
		assertEquals("a_b_c _d_e_f " + event.getDate() + " " + event.getTime() + " g_h_i_ " + localHostName + " _j_k_l_",event.createMessage());
	}

	public void testPCISyslogMessageMap() throws Exception {
		Map map = new HashMap();
		
		map.put(PCISyslogMessage.USER_ID,"b");
		map.put(PCISyslogMessage.EVENT_TYPE,"c");
		map.put(PCISyslogMessage.DATE,"d");
		map.put(PCISyslogMessage.TIME,"e");
		map.put(PCISyslogMessage.STATUS,"f");
		map.put(PCISyslogMessage.ORIGINATION,"g");
		map.put(PCISyslogMessage.AFFECTED_RESOURCE,"h");
		
		PCISyslogMessage message = new PCISyslogMessage(map);
		assertEquals("b c d e f g h",message.createMessage());

		map = new HashMap();
		
		map.put(PCISyslogMessage.USER_ID,"b");
		map.put(PCISyslogMessage.EVENT_TYPE,"c");
		map.put(PCISyslogMessage.DATE,new Date());
		map.put(PCISyslogMessage.STATUS,"f");
		map.put(PCISyslogMessage.ORIGINATION,"g");
		map.put(PCISyslogMessage.AFFECTED_RESOURCE,"h");
		
		message = new PCISyslogMessage(map);
		message.setTime(message.getTime());
		assertEquals("b c " + message.getDate() + " " + message.getTime() + " f g h",message.createMessage());
	}

	public void testPCISyslogMessageIF() throws Exception {
		PCISyslogMessage message = new PCISyslogMessage(new TestSyslogMessage());
		assertEquals("a2 b2 c2 d2 e2 f2 g2",message.createMessage());
	}
}
