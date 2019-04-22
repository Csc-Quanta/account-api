package org.csc.account.util;

import java.util.Calendar;

public class DateTimeUtil {
//	public static boolean isToday(long timestamp) {
//		Calendar c = Calendar.getInstance();
//
//		return isToday(c.getTimeInMillis(), timestamp);
//	}

	public static boolean isToday(long compareWith, long timestamp) {
		Calendar d = Calendar.getInstance();
		d.setTimeInMillis(timestamp);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(compareWith);

		if (d.get(Calendar.DATE) != c.get(Calendar.DATE)) {
			return false;
		} else if (d.get(Calendar.MONTH) != c.get(Calendar.MONTH)) {
			return false;
		} else if (d.get(Calendar.YEAR) != c.get(Calendar.YEAR)) {
			return false;
		}
		return true;
	}

}
