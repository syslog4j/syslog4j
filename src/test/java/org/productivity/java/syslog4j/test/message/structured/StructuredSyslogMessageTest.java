package org.productivity.java.syslog4j.test.message.structured;

//
// Cleversafe open-source code header - Version 1.2 - February 15, 2008
//
// Cleversafe Dispersed Storage(TM) is software for secure, private and
// reliable storage of the world's data using information dispersal.
//
// Copyright (C) 2005-2008 Cleversafe, Inc.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
// USA.
//
// Contact Information: Cleversafe, 224 North Desplaines Street, Suite 500
// Chicago IL 60661
// email licensing@cleversafe.org
//
// END-OF-HEADER
// -----------------------
// @author: mmotwani
//
// Date: Jul 15, 2009
// ---------------------

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;

public class StructuredSyslogMessageTest extends TestCase
{
   public void testFromString1()
   {
      final String messageStr = "procId msgId1 - " + StructuredSyslogMessage.BOM + "my message!!";

      final StructuredSyslogMessage message = StructuredSyslogMessage.fromString(messageStr);
      
      assertEquals(messageStr, message.toString());
//      assertEquals(-108931075,message.hashCode());
      assertEquals(-1735213078,message.hashCode());
      
      assertEquals("my message!!", message.getMessage());
      assertEquals("msgId1", message.getMessageId());
//      assertTrue(message.getStructuredData().size() == 0);
      assertNull(message.getStructuredData());
   }

   public void testFromString2()
   {
      final String messageStr = "procId msgId1 [invalid SD] my message!!";

      try {
    	  StructuredSyslogMessage.fromString(messageStr);
    	  fail();
    	  
      } catch (IllegalArgumentException iae) {
    	  //
      }
   }

   public void testFromString3()
   {
      final String messageStr = "procId msgId1 [data1 a=b] my message!!";

      try {
	      StructuredSyslogMessage.fromString(messageStr);
		  fail();
		  
	   } catch (IllegalArgumentException iae) {
	 	  //
	   }
   }

   public void testFromString4()
   {
      final String messageStr = "procId msgId1 [data1 a=\"b] my message!!";

      try {
	      StructuredSyslogMessage.fromString(messageStr);
		  fail();
		  
	   } catch (IllegalArgumentException iae) {
	 	  //
	   }
   }

   public void testFromString5()
   {
      final String messageStr = "procId msgId1 [data1 a=b\"] my message!!";

      try {
    	  StructuredSyslogMessage.fromString(messageStr);
    	  fail();
    	  
      } catch (IllegalArgumentException iae) {
    	  //
      }

   }

   public void testFromString6()
   {
      final String messageStr = "procId msgId1 [data1 a=\"b\"] my message!!";

      final StructuredSyslogMessage message = StructuredSyslogMessage.fromString(messageStr);

      assertEquals("my message!!", message.getMessage());
      assertEquals("msgId1", message.getMessageId());
      assertTrue(message.getStructuredData().size() == 1);
      assertTrue(((Map) message.getStructuredData().get("data1")).size() == 1);
      assertEquals("b", ((Map) message.getStructuredData().get("data1")).get("a"));
   }

   public void testFromString7()
   {
      final String messageStr =
            "procId msgId1 [data1 a=\"b\"][data2 a=\"b\" x1=\"c1\" n2=\"f5\"] my message!!";

      final StructuredSyslogMessage message = StructuredSyslogMessage.fromString(messageStr);

      assertEquals("my message!!", message.getMessage());
      assertEquals("msgId1", message.getMessageId());
      assertTrue(message.getStructuredData().size() == 2);
      assertTrue(((Map) message.getStructuredData().get("data1")).size() == 1);
      assertTrue(((Map) message.getStructuredData().get("data2")).size() == 3);
      assertEquals("b", ((Map) message.getStructuredData().get("data1")).get("a"));
      assertEquals("b", ((Map) message.getStructuredData().get("data2")).get("a"));
      assertEquals("c1", ((Map) message.getStructuredData().get("data2")).get("x1"));
      assertEquals("f5", ((Map) message.getStructuredData().get("data2")).get("n2"));
   }

   public void testCreateMessage1()
   {
      final StructuredSyslogMessage message = new StructuredSyslogMessage(null, "msgId", null, null);
      assertEquals("- msgId -", message.createMessage());
   }

   public void testCreateMessage2()
   {
      final StructuredSyslogMessage message =
            new StructuredSyslogMessage("procId", "msgId", null, "my message");
      assertEquals("procId msgId - " + StructuredSyslogMessage.BOM + "my message", message.createMessage());
   }

   public void testCreateMessage3()
   {
      final StructuredSyslogMessage message =
            new StructuredSyslogMessage("procId", "msgId", new HashMap(), "my message");
      assertEquals("procId msgId - " + StructuredSyslogMessage.BOM + "my message", message.createMessage());
   }

   public void testCreateMessage4()
   {
      final Map map = new HashMap();
      final StructuredSyslogMessage message =
            new StructuredSyslogMessage("procId", "msgId", map, "my message");
      assertEquals("procId msgId - " + StructuredSyslogMessage.BOM + "my message", message.createMessage());
   }
}
