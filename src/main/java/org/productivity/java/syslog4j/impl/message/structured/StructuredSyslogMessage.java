package org.productivity.java.syslog4j.impl.message.structured;

import java.util.HashMap;
import java.util.Map;

import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.impl.message.AbstractSyslogMessage;

/**
 * SyslogStructuredMessage extends AbstractSyslogMessage's ability to provide
 * support for turning POJO (Plain Ol' Java Objects) into Syslog messages. It
 * adds support for structured syslog messages as specified by
 * draft-ietf-syslog-protocol-23. More information here:
 * 
 * <p>
 * http://tools.ietf.org/html/draft-ietf-syslog-protocol-23#section-6
 * </p>
 * 
 * <p>
 * Syslog4j is licensed under the Lesser GNU Public License v2.1. A copy of the
 * LGPL license is available in the META-INF folder in all distributions of
 * Syslog4j and in the base directory of the "doc" ZIP.
 * </p>
 * 
 * @author Manish Motwani
 * @version $Id: StructuredSyslogMessage.java,v 1.5 2010/09/11 16:49:24 cvs Exp $
 */
public class StructuredSyslogMessage extends AbstractSyslogMessage implements StructuredSyslogMessageIF {
	
	private static final long serialVersionUID = 3669887659567965965L;

	public static final String BOM = "\uFEFF"; // WL: results in the byte sequence EF BB BF
	
	private String processId = null; // WL moved processId to StructuredSyslogMessage
	private String messageId;
	private Map<String,Map<String,String>> structuredData;
	private String message;

	private StructuredSyslogMessage() {
		this.processId = null; // WL
		this.messageId = null;
		this.message = null;
		this.structuredData = null;
	}

	/**
	 * Constructs the {@link StructuredSyslogMessage} using MSGID,
	 * STRUCTURED-DATA and MSG fields, as described in:
	 * 
	 * <p>
	 * http://tools.ietf.org/html/draft-ietf-syslog-protocol-23#section-6
	 * </p>
	 * 
	 * The Map must be a String -> (Map of String -> String), which encompasses
	 * the STRUCTURED-DATA field described in above document.
	 * 
	 * @param messageId
	 * @param structuredData
	 * @param message
	 */
	public StructuredSyslogMessage(final String processId, final String messageId,
			final Map<String,Map<String,String>> structuredData, final String message) {
		super();
		this.processId = processId; // WL
		this.messageId = messageId;
		if (structuredData != null && !structuredData.isEmpty())
			this.structuredData = structuredData;
		setMessage(message);

		ensureCorrectMapType();
	}

	
	private void ensureCorrectMapType() {
		if (!(getStructuredData() == null)) {
			
			for (Map.Entry<String,Map<String,String>> sdEntry: getStructuredData().entrySet()) {
				
//			Set sdEntrySet = getStructuredData().entrySet();
//			for (Iterator it = sdEntrySet.iterator(); it.hasNext();) {
//				Map.Entry sdEntry = (Map.Entry) it.next();
				if (!(sdEntry.getKey() instanceof String)) {
					throw new IllegalArgumentException(
							"Structured data map must be a map of String -> (Map of String,String)");
				}
				if (!(sdEntry.getValue() instanceof Map)) {
					throw new IllegalArgumentException(
							"Structured data map must be a map of String -> (Map of String,String)");
				}

				for (Map.Entry<String, String> entry: sdEntry.getValue().entrySet()) {
//				Set entrySet = sdEntry.getValue().entrySet();
//				for (Iterator it2 = entrySet.iterator(); it2.hasNext();) {
//					Map.Entry entry = (Map.Entry) it2.next();

					if (!(entry.getKey() instanceof String)) {
						throw new IllegalArgumentException(
								"Structured data map must be a map of String -> (Map of String,String)");
					}
					if (!(entry.getValue() instanceof String)) {
						throw new IllegalArgumentException(
								"Structured data map must be a map of String -> (Map of String,String)");
					}
				}
			}

		}
	}

	/**
	 * Parses and loads a {@link StructuredSyslogMessage} from string.
	 * 
	 * @param syslogMessageStr
	 * @return Returns an instance of StructuredSyslogMessage.
	 */
	public static StructuredSyslogMessage fromString(
			final String syslogMessageStr) {
		final StructuredSyslogMessage syslogMessage = new StructuredSyslogMessage();
		syslogMessage.deserialize(syslogMessageStr);
		return syslogMessage;
	}

	private int getBlankIndex(final int fromIndex, final String stringMessage) {
		int blankIndex = stringMessage.indexOf(' ', fromIndex);
		if (blankIndex < 0) {
			throw new IllegalArgumentException("Invalid Syslog string format: " + stringMessage);
		}
		return blankIndex;
	}
	
	private String getValue(final int beginIndex, final int endIndex, final String stringMessage) {
		String value = stringMessage.substring(beginIndex, endIndex);
		if (SyslogConstants.STRUCTURED_DATA_NILVALUE.equals(value))
			return null;
		
		if (!StructuredSyslogMessage.checkIsPrintable(value)) {
					throw new IllegalArgumentException("Invalid character in Syslog string: " + value);
		}
		
		return value;
	}
	
	// procid msgId struct
	private void deserialize(final String stringMessage) {
		int blank1Index = getBlankIndex(0, stringMessage);
		this.processId = getValue(0, blank1Index, stringMessage);
		int blank2Index = getBlankIndex(blank1Index + 1, stringMessage);
		this.messageId = getValue(blank1Index + 1, blank2Index, stringMessage);
		char schar = stringMessage.charAt(blank2Index + 1);
		int blank3Index = blank2Index + 2;
		if (schar == '-') {
			this.structuredData = null;
		}
		else {
			if (schar != '[')
				throw new IllegalArgumentException("Invalid Syslog string format: " + stringMessage);
			
			this.structuredData = new HashMap<String,Map<String,String>>();
			int openIndex = blank2Index + 1;

			while (schar == '[') {
				int closeIndex = stringMessage.indexOf(']', openIndex + 1);
				if (closeIndex < 0) {
					throw new IllegalArgumentException("Invalid Syslog string format: " + stringMessage);
				}
				blank3Index = closeIndex + 1;
				String structuredDataIteration = stringMessage.substring(openIndex + 1, closeIndex);
				if (!parseStructuredData(structuredDataIteration))
					throw new IllegalArgumentException ("Invalid structured data format in Syslog message: " + stringMessage);
				if (blank3Index < stringMessage.length()) {
					schar = stringMessage.charAt(blank3Index);
					openIndex = blank3Index;
				}
				else schar = 0;
			}
		}
		
		if (blank3Index < stringMessage.length()) {
			char bch = stringMessage.charAt(blank3Index);
			if (bch != ' ')
				throw new IllegalArgumentException("Invalid Syslog string format: " + stringMessage);

			setMessage(stringMessage.substring(blank3Index + 1));
		}
		else this.message = "";
	}

	private boolean parseStructuredData(String structuredDataIteration) {
		final Map<String,String> iterMap = new HashMap<String,String>();

		final String[] params = structuredDataIteration.split(" ");

		for (int i = 1; i < params.length; i++) {
			final String[] paramIter = params[i].split("=");
			if (paramIter.length != 2) {
				return false;
			}

			if (!paramIter[1].startsWith("\"")
					|| !paramIter[1].endsWith("\"")) {
				return false;
			}

			iterMap.put(paramIter[0], paramIter[1].substring(1,
					paramIter[1].length() - 1));
		}

		this.structuredData.put(params[0], iterMap);
		return true;
	}
/*
	private void deserialize(final String stringMessage) {
		// Check correct format
		if (stringMessage.indexOf('[') <= 0)
			throw new IllegalArgumentException("Invalid Syslog string format: " + stringMessage);

		// Divide the string in 2 sections
		final String syslogHeader = stringMessage.substring(0, stringMessage.indexOf('['));
		String structuredDataString = stringMessage.substring(stringMessage.indexOf('['), stringMessage.lastIndexOf(']') + 1);
		
		if ((stringMessage.lastIndexOf(']') + 2) <= stringMessage.length())
			setMessage(stringMessage.substring(stringMessage.lastIndexOf(']') + 2));
		
		else {
			this.message = "";
		}
		
		// Split into tokens
		final String[] tokens = syslogHeader.split(" ");

		// Check number of tokens must be 1 -- rest of the header should already
		// be stripped
		if (tokens.length != 2) { // WL now not only the messageId but also the processId is part of the StructuredSyslogMessage
			throw new IllegalArgumentException("Invalid Syslog string format: " + stringMessage);
		}

//		this.messageId = SyslogConstants.STRUCTURED_DATA_NILVALUE.equals(tokens[0]) ? null : tokens[0];
		this.processId = SyslogConstants.STRUCTURED_DATA_NILVALUE.equals(tokens[0]) ? null : tokens[0];
		this.messageId = SyslogConstants.STRUCTURED_DATA_NILVALUE.equals(tokens[1]) ? null : tokens[1];

		this.structuredData = new HashMap();
		if (!SyslogConstants.STRUCTURED_DATA_EMPTY_VALUE.equals(structuredDataString)) {
			while (!"".equals(structuredDataString)) {
				if (!structuredDataString.startsWith("[")
						|| structuredDataString.indexOf(']') == -1) {
					throw new IllegalArgumentException(
							"Invalid structured data format in Syslog message: " + stringMessage);
				}

				final String structuredDataIteration = structuredDataString.substring(1, structuredDataString.indexOf(']'));
				if (!parseStructuredData(structuredDataIteration))
					throw new IllegalArgumentException ("Invalid structured data format in Syslog message: " + stringMessage);

				if (structuredDataString.indexOf(']') != structuredDataString.lastIndexOf(']')) {
					structuredDataString = structuredDataString.substring(structuredDataString.indexOf(']') + 1);
				} else {
					structuredDataString = "";
				}
			}
		}
	}
*/

	public String getProcessId() {
		return this.processId;
	}

	/**
	 * Returns the MSGID field of the structured message format, as described
	 * in:
	 * 
	 * <p>
	 * http://tools.ietf.org/html/draft-ietf-syslog-protocol-23#section-6
	 * </p>
	 * 
	 * @return Returns the MSG ID field.
	 */
	public String getMessageId() {
		return this.messageId;
	}

	/**
	 * Returns the structured data map. The Map is a String -> (Map of String ->
	 * String), which encompasses the STRUCTURED-DATA field, as described in:
	 * 
	 * <p>
	 * http://tools.ietf.org/html/draft-ietf-syslog-protocol-23#section-6
	 * </p>
	 * 
	 * @return Returns a Map object containing structured data.
	 */
	public Map<String,Map<String,String>> getStructuredData() {
		return this.structuredData;
	}

	/**
	 * Returns the MSG field of the structured message format, as described in:
	 * 
	 * <p>
	 * http://tools.ietf.org/html/draft-ietf-syslog-protocol-23#section-6
	 * </p>
	 * 
	 * @return Returns the MSG field.
	 */
	public String getMessage() {
		return this.message;
	}
	
	private void setMessage(String message) { // WL
		if (message != null && message.startsWith(BOM))
			 this.message = message.substring(BOM.length());
		else this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.productivity.java.syslog4j.impl.message.AbstractSyslogMessage#
	 * createMessage()
	 */
	public String createMessage() {
		return serialize();
	}

	private String serialize() {
		
		if (!StructuredSyslogMessage.checkIsPrintable(getProcessId())) // WL
			throw new IllegalArgumentException("Invalid process id: " + getProcessId());
		
		if (!StructuredSyslogMessage.checkIsPrintable(getMessageId()))
			throw new IllegalArgumentException("Invalid message id: " + getMessageId());

		final StringBuffer sb = new StringBuffer();

		sb.append(StructuredSyslogMessage.nilProtect(getProcessId())); // WL moved processId to StructuredSyslogMessage
		sb.append(' ');

		sb.append(StructuredSyslogMessage.nilProtect(getMessageId()));
		sb.append(' ');

		if (getStructuredData() == null || getStructuredData().size() == 0) {
		/*
			// This is not desired, but rsyslogd does not store version 1 syslog
			// message correctly if
			// there is no
			// structured data present
			sb.append(SyslogConstants.STRUCTURED_DATA_EMPTY_VALUE);
		 */
//			WL: according to RFC 5424 we should write - if no structured data is present 
			sb.append(SyslogConstants.STRUCTURED_DATA_NILVALUE);
		} 
		else {
			
			for (Map.Entry<String,Map<String,String>> sdElement: getStructuredData().entrySet()) {
				
//			Set sdEntrySet = getStructuredData().entrySet();
//			for (Iterator it = sdEntrySet.iterator(); it.hasNext();) {
//				final Map.Entry sdElement = (Map.Entry) it.next();
				final String sdId = (String) sdElement.getKey();

				if (sdId == null || sdId.length() == 0
						|| !StructuredSyslogMessage.checkIsPrintable(sdId)) {
					throw new IllegalArgumentException("Illegal structured data id: " + sdId);
				}

				sb.append('[').append(sdId);

				final Map<String,String> sdParams = sdElement.getValue();

				if (sdParams != null) {
					
					for (Map.Entry<String,String> entry: sdParams.entrySet()) {
//					Set entrySet = sdParams.entrySet();
//					for (Iterator it2 = entrySet.iterator(); it2.hasNext();) {
//						Map.Entry entry = (Map.Entry) it2.next();
						final String paramName = entry.getKey();
						final String paramValue = entry.getValue();

						if (paramName == null
								|| paramName.length() == 0
								|| !StructuredSyslogMessage
										.checkIsPrintable(paramName))
							throw new IllegalArgumentException(
									"Illegal structured data parameter name: " + paramName);

						if (paramValue == null)
							throw new IllegalArgumentException(
									"Null structured data parameter value for parameter name: " + paramName);

						sb.append(' ');
						sb.append(paramName);
						sb.append('=').append('"');
						StructuredSyslogMessage.sdEscape(sb, paramValue);
						sb.append('"');
					}
				}

				sb.append(']');
			}
		}

		if (getMessage() != null && getMessage().length() != 0) {
			sb.append(' ');
			sb.append(BOM); // WL
			sb.append(getMessage());
		}

		return sb.toString();

	}

	public static void sdEscape(final StringBuffer sb, final String value) {
		for (int i = 0; i < value.length(); i++) {
			final char c = value.charAt(i);

			if (c == '"' || c == '\\' || c == ']') {
				sb.append('\\');
			}

			sb.append(c);
		}
	}

	public static boolean checkIsPrintable(final String value) {
		if (value == null)
			return true;

		for (int i = 0; i < value.length(); i++) {
			final char c = value.charAt(i);

			if (c < 33 || c > 126)
				return false;
		}

		return true;
	}

	public static String nilProtect(final String value) {
		if (value == null || value.trim().length() == 0) {
			return SyslogConstants.STRUCTURED_DATA_NILVALUE;
		}

		return value;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result
				+ ((processId == null) ? 0 : processId.hashCode()); // WL
		result = prime * result
				+ ((messageId == null) ? 0 : messageId.hashCode());
		result = prime * result
				+ ((structuredData == null) ? 0 : structuredData.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		StructuredSyslogMessage other = (StructuredSyslogMessage) obj;
		
		if (message == null) {
			if (other.message != null) return false;
			
		} else if (!message.equals(other.message)) return false;
		
		if (processId == null) { // WL
			if (other.processId != null) return false;
			
		} else if (!processId.equals(other.processId)) return false;

		if (messageId == null) {
			if (other.messageId != null) return false;
			
		} else if (!messageId.equals(other.messageId)) return false;
		
		if (structuredData == null) {
			if (other.structuredData != null) return false;
			
		} else if (!structuredData.equals(other.structuredData)) return false;
		
		return true;
	}

	public String toString() {
		return serialize();
	}
}
