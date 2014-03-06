package de.geektank.bitcoin.supporttr.tools;

import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.*;

public class BitcoinToolsTest {

	@Test
	public void validateBitcoinAddress_Production() {
		
		// addresses positives
		assertTrue(BitcoinTools.validateBitcoinAddress("15zehi7Bz2CATYKSJW1o6FfJDpjkn1htFD", BitcoinTools.PRODUCTION));
		
		// negatives 
		assertFalse(BitcoinTools.validateBitcoinAddress(null, BitcoinTools.PRODUCTION));
		assertFalse(BitcoinTools.validateBitcoinAddress("", BitcoinTools.PRODUCTION));
		assertFalse(BitcoinTools.validateBitcoinAddress("abc", BitcoinTools.PRODUCTION));
		assertFalse(BitcoinTools.validateBitcoinAddress("mhw7FQcUgwMq3nui6BWDWrqvR5Mvxj1Aio", BitcoinTools.PRODUCTION));
		
	}
	
	@Test
	public void validateBitcoinAddress_Testnet()  {
				
		// testnet3 addresses positives
		assertTrue(BitcoinTools.validateBitcoinAddress("mhw7FQcUgwMq3nui6BWDWrqvR5Mvxj1Aio", BitcoinTools.TESTNET3));
		assertTrue(BitcoinTools.validateBitcoinAddress("mimoZNLcP2rrMRgdeX5PSnR7AjCqQveZZ4", BitcoinTools.TESTNET3));
		
		// testnet3 negatives 
		assertFalse(BitcoinTools.validateBitcoinAddress("", BitcoinTools.TESTNET3));
		assertFalse(BitcoinTools.validateBitcoinAddress("abc", BitcoinTools.TESTNET3));
		assertFalse(BitcoinTools.validateBitcoinAddress("15zehi7Bz2CATYKSJW1o6FfJDpjkn1htFD", BitcoinTools.TESTNET3));
		
	}
	
	@Test
	public void stripBitcoinAddressTest() {
		
		assertEquals(null, BitcoinTools.stripBitcoinAddress(null));
		assertEquals("15zehi7Bz2CATYKSJW1o6FfJDpjkn1htFD", BitcoinTools.stripBitcoinAddress("15zehi7Bz2CATYKSJW1o6FfJDpjkn1htFD"));
		assertEquals("15zehi7Bz2CATYKSJW1o6FfJDpjkn1htFD", BitcoinTools.stripBitcoinAddress("bitcoin:15zehi7Bz2CATYKSJW1o6FfJDpjkn1htFD"));
		assertEquals("15zehi7Bz2CATYKSJW1o6FfJDpjkn1htFD", BitcoinTools.stripBitcoinAddress("bitcoin:15zehi7Bz2CATYKSJW1o6FfJDpjkn1htFD?param1=43545"));
		
	}
	
	@Test
	public void double2NanoLongTest() {
		assertEquals(Long.parseLong("100000000"), BitcoinTools.double2NanoLong(1.0));
		assertEquals(Long.parseLong("10000000"), BitcoinTools.double2NanoLong(0.1));
	} 

	@Test
	public void double2mBTCTest() {
		assertEquals("1000.00", BitcoinTools.double2mBTC(Locale.US, Double.parseDouble("1")));
		assertEquals("1.00", BitcoinTools.double2mBTC(Locale.US, Double.parseDouble("0.001")));
		assertEquals("3.21", BitcoinTools.double2mBTC(Locale.US, Double.parseDouble("0.00321")));
	}
	
}
