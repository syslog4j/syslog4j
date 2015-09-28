package org.productivity.java.syslog4j.test.message.modifier;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;

import javax.crypto.spec.SecretKeySpec;

import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogMessageModifierIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.AbstractSyslogConfig;
import org.productivity.java.syslog4j.impl.message.modifier.checksum.ChecksumSyslogMessageModifier;
import org.productivity.java.syslog4j.impl.message.modifier.checksum.ChecksumSyslogMessageModifierConfig;
import org.productivity.java.syslog4j.impl.message.modifier.escape.HTMLEntityEscapeSyslogMessageModifier;
import org.productivity.java.syslog4j.impl.message.modifier.hash.HashSyslogMessageModifier;
import org.productivity.java.syslog4j.impl.message.modifier.hash.HashSyslogMessageModifierConfig;
import org.productivity.java.syslog4j.impl.message.modifier.mac.MacSyslogMessageModifier;
import org.productivity.java.syslog4j.impl.message.modifier.sequential.SequentialSyslogMessageModifier;
import org.productivity.java.syslog4j.impl.message.modifier.sequential.SequentialSyslogMessageModifierConfig;
import org.productivity.java.syslog4j.impl.message.modifier.text.PrefixSyslogMessageModifier;
import org.productivity.java.syslog4j.impl.message.modifier.text.StringCaseSyslogMessageModifier;
import org.productivity.java.syslog4j.impl.message.modifier.text.SuffixSyslogMessageModifier;
import org.productivity.java.syslog4j.test.net.base.AbstractNetSyslog4jTest;
import org.productivity.java.syslog4j.util.Base64;
import org.productivity.java.syslog4j.util.SyslogUtility;

public class SyslogMessageModifierTest extends AbstractNetSyslog4jTest {
	protected static int pause = 100;
	
	protected int getMessageCount() {
		return -1;
	}

	protected String getClientProtocol() {
		return "udp";
	}
	
	protected String getServerProtocol() {
		return "udp";
	}
	
	public void testStringCase() {
		// CHECK OUT OF BOUNDS
		
		try {
			new StringCaseSyslogMessageModifier((byte) 3);
			fail("Should not be able to construct with an invalid byte value");
			
		} catch (SyslogRuntimeException sre) {
			assertTrue(true);
		}
		
		// PREPARE
		
		List events = new ArrayList();
		String message = null;
		
		String protocol = getClientProtocol();
		SyslogIF syslog = getSyslog(protocol);
		syslog.getConfig().removeAllMessageModifiers();
		
		ArrayList list = new ArrayList();
		((AbstractSyslogConfig) syslog.getConfig()).setMessageModifiers(list);
		
		// UPPER SET UP

		list.add(StringCaseSyslogMessageModifier.UPPER);
		
		// UPPER

		message = "[TEST] abcDEF Abc deF eFg";
		syslog.debug(message);
		events.add(message.toUpperCase());

		// LOWER SET UP
		
		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(StringCaseSyslogMessageModifier.LOWER);

		// LOWER

		message = "[TEST] ABCdef aBC DEf EfG";
		syslog.warn(message);
		events.add(message.toLowerCase());

		// VERIFY
		
		SyslogUtility.sleep(pause);
		syslog.flush();
		verifySendReceive(events,false,false);
	}

	public void testPrefixSuffix() {
		// PREPARE
		
		List events = new ArrayList();
		String message = null;
		
		String protocol = getClientProtocol();
		SyslogIF syslog = getSyslog(protocol);
		syslog.getConfig().removeAllMessageModifiers();
		
		// PREFIX SET UP

		PrefixSyslogMessageModifier prefixModifier = new PrefixSyslogMessageModifier("[TEST]");
		assertEquals("[TEST]",prefixModifier.getPrefix());
		syslog.getConfig().addMessageModifier(prefixModifier);
		
		// PREFIX

		message = "abcDEF Abc deF eFg";
		syslog.error(message);
		events.add("[TEST] abcDEF Abc deF eFg");

		// PREFIX SET UP

		prefixModifier = new PrefixSyslogMessageModifier("[TEST]","|");
		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(prefixModifier);
		
		// PREFIX

		message = "abcDEF Abc deF eFg";
		syslog.notice(message);
		events.add("[TEST]|abcDEF Abc deF eFg");

		// PREFIX SET UP

		prefixModifier = new PrefixSyslogMessageModifier();
		prefixModifier.setPrefix("[TEST] xx");
		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(prefixModifier);
		
		// PREFIX

		message = "abcDEF Abc deF eFg hIj";
		syslog.error(message);
		events.add("[TEST] xx abcDEF Abc deF eFg hIj");

		// PREFIX SET UP

		prefixModifier = new PrefixSyslogMessageModifier();
		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(prefixModifier);
		
		// PREFIX

		message = "[TEST] abcDEF Abc deF eFg hIj";
		syslog.error(message);
		events.add(message);

		// SUFFIX SET UP
		
		SuffixSyslogMessageModifier suffixModifier = new SuffixSyslogMessageModifier("[END]");
		assertEquals("[END]",suffixModifier.getSuffix());
		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(suffixModifier);

		// SUFFIX

		message = "[TEST] ABCdef aBC DEf EfG";
		syslog.emergency(message);
		events.add(message + " [END]");

		// SUFFIX SET UP
		
		suffixModifier = new SuffixSyslogMessageModifier("[END]","|");
		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(suffixModifier);

		// SUFFIX

		message = "[TEST] ABCdef aBC DEf EfG";
		syslog.critical(message);
		events.add(message + "|[END]");

		// SUFFIX SET UP
		
		syslog.getConfig().removeAllMessageModifiers();
		suffixModifier = new SuffixSyslogMessageModifier();
		suffixModifier.setSuffix("yy [END]");
		syslog.getConfig().addMessageModifier(suffixModifier);

		// SUFFIX

		message = "[TEST] ABCdef aBC DEf EfG HiJ";
		syslog.alert(message);
		events.add(message + " yy [END]");

		// SUFFIX SET UP
		
		suffixModifier = new SuffixSyslogMessageModifier();
		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(suffixModifier);

		// SUFFIX

		message = "[TEST] ABCdef aBC DEf EfG HiJ";
		syslog.emergency(message);
		events.add(message);

		// VERIFY
		
		SyslogUtility.sleep(pause);
		syslog.flush();
		verifySendReceive(events,false,false);
	}

	public void testSequential() {
		// PREPARE
		
		List events = new ArrayList();
		String message = null;
		
		String protocol = getClientProtocol();
		SyslogIF syslog = getSyslog(protocol);
		syslog.getConfig().removeAllMessageModifiers();
		
		// SET UP

		SequentialSyslogMessageModifier sequentialModifier = SequentialSyslogMessageModifier.createDefault();
		syslog.getConfig().addMessageModifier(sequentialModifier);
		assertEquals(sequentialModifier.getConfig().getFirstNumber(),SequentialSyslogMessageModifierConfig.createDefault().getFirstNumber());
		
		// ZERO

		message = "[TEST] Sequence Test";
		syslog.info(message);
		events.add(message + " #0000");

		// ONE

		message = "[TEST] Sequence Test";
		syslog.info(message);
		events.add(message + " #0001");

		// NINE THOUSAND NINE HUNDRED NIGHTY EIGHT
		
		sequentialModifier.setNextSequence(SyslogConstants.LEVEL_INFO,9998);
		
		message = "[TEST] Sequence Test";
		syslog.info(message);
		events.add(message + " #9998");

		// NINE THOUSAND NINE HUNDRED NIGHTY NINE
		
		message = "[TEST] Sequence Test";
		syslog.info(message);
		events.add(message + " #9999");

		// ZERO
		
		message = "[TEST] Sequence Test";
		syslog.info(message);
		events.add(message + " #0000");

		// VERIFY
		
		SyslogUtility.sleep(pause);
		syslog.flush();
		verifySendReceive(events,false,false);
	}

	public void testChecksum() {
		try {
			new ChecksumSyslogMessageModifier(null);
			fail("Should not allow an empty config into a modifier");
			
		} catch (SyslogRuntimeException sre) {
			assertTrue(true);
		}
		
		// PREPARE
		
		List events = new ArrayList();
		String message = null;
		
		String protocol = getClientProtocol();
		SyslogIF syslog = getSyslog(protocol);
		syslog.getConfig().removeAllMessageModifiers();
		
		// CRC32 SET UP

		syslog.getConfig().addMessageModifier(ChecksumSyslogMessageModifier.createCRC32());

		// CRC32
		
		message = "[TEST] This Line Will Have a CRC32 Checksum";
		syslog.info(message);
		events.add(message + " {F8E7A4E4}");

		// ADLER32 SET UP
		
		syslog.getConfig().removeAllMessageModifiers();
		ChecksumSyslogMessageModifier adler32Modifier = ChecksumSyslogMessageModifier.createADLER32();
		syslog.getConfig().addMessageModifier(adler32Modifier);

		// ADLER32
		
		message = "[TEST] This Line Will Have an ADLER32 Checksum";
		syslog.info(message);
		events.add(message + " {5AD70EE4}");

		// ADLER32 SET UP with LOWER FIRST
		
		syslog.getConfig().insertMessageModifier(0,StringCaseSyslogMessageModifier.LOWER);

		// ADLER32 with LOWER FIRST
		
		message = "[TEST] This Line Will Have an ADLER32 Checksum";
		syslog.info(message);
		events.add(message.toLowerCase() + " {8A1710A4}");

		// ADLER32
		
		adler32Modifier.getConfig().setChecksum(new Adler32());
		message = "[TEST] This Line Will Have an ADLER32 Checksum 2";
		syslog.info(message);
		events.add(message.toLowerCase() + " {ABD110F6}");

		// VERIFY
		
		SyslogUtility.sleep(pause);
		syslog.flush();
		verifySendReceive(events,false,false);
	}

	public void testContinousChecksum() {
		try {
			new ChecksumSyslogMessageModifier(null);
			fail("Should not allow an empty config into a modifier");
			
		} catch (SyslogRuntimeException sre) {
			assertTrue(true);
		}
		
		// PREPARE
		
		List events = new ArrayList();
		String message = null;
		
		String protocol = getClientProtocol();
		SyslogIF syslog = getSyslog(protocol);
		syslog.getConfig().removeAllMessageModifiers();
		
		// CRC32 SET UP

		ChecksumSyslogMessageModifierConfig config = ChecksumSyslogMessageModifierConfig.createCRC32();
		assertFalse(config.isContinuous());
		config.setContinuous(true);
		assertTrue(config.isContinuous());
		
		ChecksumSyslogMessageModifier modifier = new ChecksumSyslogMessageModifier(config);
		syslog.getConfig().addMessageModifier(modifier);

		// CRC32 Message #1
		
		message = "[TEST] This Line Will Have a CRC32 Checksum 1";
		syslog.info(message);
		events.add(message + " {6E2A9F99}");

		// CRC32 Message #2
		
		message = "[TEST] This Line Will Have a CRC32 Checksum 2";
		syslog.info(message);
		events.add(message + " {7E736783}");

		// VERIFY
		
		SyslogUtility.sleep(pause);
		syslog.flush();
		verifySendReceive(events,false,false);
	}

	public void testHash() {
		try {
			new HashSyslogMessageModifier(null);
			fail("Should not allow an empty config into a modifier");
			
		} catch (SyslogRuntimeException sre) {
			assertTrue(true);
		}
		// PREPARE

		try {
			HashSyslogMessageModifierConfig config = new HashSyslogMessageModifierConfig("FakeAlgorithm");
			new HashSyslogMessageModifier(config);
			fail("Should not allow an unsupported algorithm");
			
		} catch (SyslogRuntimeException sre) {
			assertTrue(true);
		}

		List events = new ArrayList();
		String message = null;
		
		String protocol = getClientProtocol();
		SyslogIF syslog = getSyslog(protocol);
		syslog.getConfig().removeAllMessageModifiers();
		
		// SHA1 (SHA-160) SET UP

		syslog.getConfig().addMessageModifier(HashSyslogMessageModifier.createSHA1());

		// SHA1 (SHA-160)
		
		message = "[TEST] This Line Will Have a SHA1 Hash";
		syslog.info(message);
		events.add(message + " {fb7Jl0VGnzY5ehJCpmkf7bSZ5Vk=}");

		// SHA1 (SHA-160) SET UP

		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(HashSyslogMessageModifier.createSHA160());

		// SHA1 (SHA-160)
		
		message = "[TEST] This Line Will Have a SHA1 Hash";
		syslog.info(message);
		events.add(message + " {fb7Jl0VGnzY5ehJCpmkf7bSZ5Vk=}");

		// SHA1 (SHA-160) SET UP

		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(new HashSyslogMessageModifier(HashSyslogMessageModifierConfig.createSHA160()));

		// SHA1 (SHA-160)
		
		message = "[TEST] This Line Will Have a SHA1 Hash";
		syslog.info(message);
		events.add(message + " {fb7Jl0VGnzY5ehJCpmkf7bSZ5Vk=}");

		// SHA256 SET UP
		
		HashSyslogMessageModifierConfig hashConfig = HashSyslogMessageModifierConfig.createSHA256();
		HashSyslogMessageModifier hash = new HashSyslogMessageModifier(hashConfig);
		assertTrue(hashConfig == hash.getConfig());

		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(hash);

		// SHA256

		message = "[TEST] This Line Will Have a SHA256 Hash";
		syslog.info(message);
		events.add(message + " {aWcyqL9rCPpKzAsQ89msFUmKxDWM3Pk7gUi4vWfJ35I=}");

		// SHA256 SET UP
		
		hash = HashSyslogMessageModifier.createSHA256();

		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(hash);

		// SHA256

		message = "[TEST] This Line Will Have a SHA256 Hash";
		syslog.info(message);
		events.add(message + " {aWcyqL9rCPpKzAsQ89msFUmKxDWM3Pk7gUi4vWfJ35I=}");

		// SHA384 SET UP
		
		syslog.getConfig().removeAllMessageModifiers();
		syslog.getConfig().addMessageModifier(HashSyslogMessageModifier.createSHA384());

		// SHA384
		
		message = "[TEST] This Line Will Have a SHA384 Hash";
		syslog.info(message);
		events.add(message + " {HTsuBfjU2efWCVUzy7isUirJRQIjoJu4CVsUMIEcH0EDbItt3nPZ07d2Y5tZfw/S}");

		// SHA512 SET UP
		
		syslog.getConfig().removeAllMessageModifiers();
		HashSyslogMessageModifier sha512 = HashSyslogMessageModifier.createSHA512();
		syslog.getConfig().addMessageModifier(sha512);

		// SHA512
		
		message = "[TEST] This Line Will Have a SHA512 Hash";
		syslog.info(message);
		events.add(message + " {YS5eWX0TKbMK74t8aduMdhiOAjo1j9L49+AzVBEyRSMn7xlSjlQ0nx69LkheZgU+I+8r4PuIehr8aux4Y0oIPg==}");

		// MD5 SET UP
		
		syslog.getConfig().removeMessageModifier(sha512);
		HashSyslogMessageModifier md5 = HashSyslogMessageModifier.createMD5();
		syslog.getConfig().addMessageModifier(md5);

		// MD5
		
		message = "[TEST] This Line Will Have an MD5 Hash";
		syslog.info(message);
		events.add(message + " {/ZswXan2FLE6OQHl2yrYEA==}");

		// MD5 SET UP with Custom Prefix/Suffix
		
		syslog.getConfig().removeMessageModifier(md5);
		
		HashSyslogMessageModifierConfig messageModifierConfig = new HashSyslogMessageModifierConfig("MD5");
		messageModifierConfig.setPrefix(null);
		messageModifierConfig.setPrefix(" [");
		messageModifierConfig.setSuffix(null);
		messageModifierConfig.setSuffix("]");
		
		SyslogMessageModifierIF messageModifier = new HashSyslogMessageModifier(messageModifierConfig);
		
		syslog.getConfig().addMessageModifier(messageModifier);

		// MD5
		
		message = "[TEST] This Line Will Have an MD5 Hash with Custom Prefix";
		syslog.info(message);
		events.add(message + " [+SuV7QIkt2jWZgoFnVkcZg==]");

		// VERIFY
		
		SyslogUtility.sleep(pause);
		syslog.flush();
		verifySendReceive(events,false,false);
	}
	
	public void testMac() {
		// PREPARE
		
		List events = new ArrayList();
		String message = null;
		
		String protocol = getClientProtocol();
		SyslogIF syslog = getSyslog(protocol);
		syslog.getConfig().removeAllMessageModifiers();
		
		// HmacSHA1 SET UP
		
		String base64Key = "fb7Jl0VGnzY5ehJCabcf7bSZ5Vk=";
		SyslogMessageModifierIF messageModifier = MacSyslogMessageModifier.createHmacSHA1(base64Key);
		syslog.getConfig().addMessageModifier(messageModifier);

		// HmacSHA1
		
		message = "[TEST] This Line Will Have an HmacSHA1 Hash";
		syslog.info(message);
		events.add(message + " {6CIz39WE8wgxwpsqPievrtDWaXM=}");

		// HmacSHA256 SET UP
		
		base64Key = "+v2mHoOx6QGLqYFa/Tx0J7BkXSK4HEVMtGHtG66vQ54=";
		syslog.getConfig().removeAllMessageModifiers();
		messageModifier = MacSyslogMessageModifier.createHmacSHA256(base64Key);
		syslog.getConfig().addMessageModifier(messageModifier);

		// HmacSHA256
		
		message = "[TEST] This Line Will Have an HmacSHA256 Hash";
		syslog.info(message);
		events.add(message + " {sEpaXO6fvnO7szaJSjcqoIVl0C180/oWSP0rs6RFfB8=}");

		// HmacSHA512 SET UP
		
		base64Key = "w5sn5tOHpk/jBTWAQ4doTlSbtE1GQZC2RCe2/ayTy67zscXFEdlT/Zwsm5GFrjOwxlZITrAaq+s2KFCNpBTDig==";
		syslog.getConfig().removeAllMessageModifiers();
		messageModifier = MacSyslogMessageModifier.createHmacSHA512(base64Key);
		syslog.getConfig().addMessageModifier(messageModifier);

		// HmacSHA512
		
		message = "[TEST] This Line Will Have an HmacSHA512 Hash";
		syslog.info(message);
		events.add(message + " {Ddx+6JegzcifzT5H82BISGeGQ9FCA5biW51qItH9y95tJvFnFv+0+Tx/Kv0HAms2jv6iq08tlL7IFI1gMUWBtA==}");

		// HmacMD5 SET UP
		
		syslog.getConfig().removeAllMessageModifiers();

		base64Key = "fb7Jl0VGnzY5ehJCdeff7bSZ5Vk=";
		messageModifier = MacSyslogMessageModifier.createHmacMD5(base64Key);
		syslog.getConfig().addMessageModifier(messageModifier);

		// HmacMD5
		
		message = "[TEST] This Line Will Have an HmacMD5 Hash";
		syslog.info(message);
		events.add(message + " {Z+BBv07/AlQ55a6d88OuGg==}");

		// VERIFY
		
		SyslogUtility.sleep(pause);
		syslog.flush();
		verifySendReceive(events,false,false);
	}
	
	protected Key createKey(String base64,String algorithm) {
		byte[] keyBytes = Base64.decode(base64);
		
		Key key = new SecretKeySpec(keyBytes,algorithm);
		
		return key;
	}

	public void testMacWithKeys() {
		// PREPARE
		
		List events = new ArrayList();
		String message = null;
		
		String protocol = getClientProtocol();
		SyslogIF syslog = getSyslog(protocol);
		syslog.getConfig().removeAllMessageModifiers();
		
		// HmacSHA1 SET UP
		
		Key key = createKey("fb7Jl0VGnzY5ehJCabcf7bSZ5Vk=","SHA1");
		SyslogMessageModifierIF messageModifier = MacSyslogMessageModifier.createHmacSHA1(key);
		syslog.getConfig().addMessageModifier(messageModifier);

		// HmacSHA1
		
		message = "[TEST] This Line Will Have an HmacSHA1 Hash";
		syslog.info(message);
		events.add(message + " {6CIz39WE8wgxwpsqPievrtDWaXM=}");

		// HmacSHA256 SET UP
		
		key = createKey("+v2mHoOx6QGLqYFa/Tx0J7BkXSK4HEVMtGHtG66vQ54=","SHA256");
		syslog.getConfig().removeAllMessageModifiers();
		messageModifier = MacSyslogMessageModifier.createHmacSHA256(key);
		syslog.getConfig().addMessageModifier(messageModifier);

		// HmacSHA256
		
		message = "[TEST] This Line Will Have an HmacSHA256 Hash";
		syslog.info(message);
		events.add(message + " {sEpaXO6fvnO7szaJSjcqoIVl0C180/oWSP0rs6RFfB8=}");

		// HmacSHA512 SET UP
		
		key = createKey("w5sn5tOHpk/jBTWAQ4doTlSbtE1GQZC2RCe2/ayTy67zscXFEdlT/Zwsm5GFrjOwxlZITrAaq+s2KFCNpBTDig==","SHA512");
		syslog.getConfig().removeAllMessageModifiers();
		messageModifier = MacSyslogMessageModifier.createHmacSHA512(key);
		syslog.getConfig().addMessageModifier(messageModifier);

		// HmacSHA512
		
		message = "[TEST] This Line Will Have an HmacSHA512 Hash";
		syslog.info(message);
		events.add(message + " {Ddx+6JegzcifzT5H82BISGeGQ9FCA5biW51qItH9y95tJvFnFv+0+Tx/Kv0HAms2jv6iq08tlL7IFI1gMUWBtA==}");

		// HmacMD5 SET UP
		
		syslog.getConfig().removeAllMessageModifiers();

		key = createKey("fb7Jl0VGnzY5ehJCdeff7bSZ5Vk=","MD5");
		messageModifier = MacSyslogMessageModifier.createHmacMD5(key);
		syslog.getConfig().addMessageModifier(messageModifier);

		// HmacMD5
		
		message = "[TEST] This Line Will Have an HmacMD5 Hash";
		syslog.info(message);
		events.add(message + " {Z+BBv07/AlQ55a6d88OuGg==}");

		// VERIFY
		
		SyslogUtility.sleep(pause);
		syslog.flush();
		verifySendReceive(events,false,false);
	}
	
	public void testHTMLEntityEscapeMessageModifier() {
		SyslogMessageModifierIF messageModifier = new HTMLEntityEscapeSyslogMessageModifier();

		String nullHtml = messageModifier.modify(null,0,0,null);
		assertNull(nullHtml);
		
		String emptyHtml = messageModifier.modify(null,0,0,"");
		assertEquals("",emptyHtml);

		// PREPARE
		
		List events = new ArrayList();
		String message = null;
		
		String protocol = getClientProtocol();
		SyslogIF syslog = getSyslog(protocol);
		syslog.getConfig().removeAllMessageModifiers();		

		syslog.getConfig().addMessageModifier(HTMLEntityEscapeSyslogMessageModifier.createDefault());
		
		// SEND HTML

		message = "[TEST] <html>&\"test\"&'" + "\t" + "</html>" + (char) 255;
		syslog.info(message);
		events.add("[TEST] &lt;html&gt;&amp;&quot;test&quot;&amp;&#39;&#9;&lt;/html&gt;&#255;");

		// VERIFY
		
		SyslogUtility.sleep(pause);
		syslog.flush();
		verifySendReceive(events,false,false);
	}
}
