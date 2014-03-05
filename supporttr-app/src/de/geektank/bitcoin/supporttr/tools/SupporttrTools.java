package de.geektank.bitcoin.supporttr.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SupporttrTools {

	public static long getTimestampOneMonthAhead(long base) {
		
		if (base<=0) return 0;
		
		SimpleDateFormat parserInput=new SimpleDateFormat("d-M-y-HH-mm");
		String dateStr = parserInput.format(new Date(base));
	
		long resultDateLong = 0;
		try {
		
			String[] parts = dateStr.split("-");
		
			int day = Integer.parseInt(parts[0]);
			int month = Integer.parseInt(parts[1]);
			int year = Integer.parseInt(parts[2]);
			
			// simplified upcounting
			month++;
			if (month>12) {
				month = 1;
				year++;
			}	
			if (day>30) day = 30;
			if ((day>28) && (month==2)) day=28;
		
			String nextMonth = day+"."+month+"."+year+" "+parts[3]+":"+parts[4];
	
			SimpleDateFormat parserOutput=new SimpleDateFormat("d.M.y HH:mm");
			Date resultDate = parserOutput.parse(nextMonth);
			resultDateLong = resultDate.getTime();
		} catch (Exception e) {
			resultDateLong = base + Long.parseLong("2592000000");
		}

		return resultDateLong;
	}
	
}
