package de.geektank.bitcoin.supporttr.tools;

import org.junit.Test;
import static org.junit.Assert.*;

public class SupporttrToolsTest {

	@Test
	public void getTimestampOneMonthAheadTest() {
		
		// TODO: Test with sample dates from the net
		assertEquals(0,SupporttrTools.getTimestampOneMonthAhead(0));
		
	}
	
}
