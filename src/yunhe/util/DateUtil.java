package yunhe.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtil {
	public final static int AROUNDDAY = 3; 
	private final static SimpleDateFormat sdf = new SimpleDateFormat("ddMMMEEEyyyyMM",Locale.ENGLISH);
	private final static SimpleDateFormat sdf_yyyy_MM_dd = new SimpleDateFormat("yyyy-M-d");
	public static List<String[]> returnRoundMonth(int dateRate) {
		List<String[]> dateList = new ArrayList<String[]>();
		Date today = getAfterDateByDays(getZeroOfTheDay(new Date()),dateRate);
		for(int i=AROUNDDAY;i>0;i--){
			dateList.add(makeDateFormat(sdf.format(getBeforeDateByDays(today,i))));
		}
		dateList.add(makeDateFormat(sdf.format(today)));
		for(int i=1;i<=AROUNDDAY;i++){
			dateList.add(makeDateFormat(sdf.format(getAfterDateByDays(today,i))));
		}
		return dateList;
	}
	public static String returnDateTo_yyyy_MM_dd(Date date){
		return sdf_yyyy_MM_dd.format(date);
	}
	public static String returnDDMMMEEETo_yyyy_MM_dd(String[] date){
		String _date = date[2].replaceAll("\n", "");
		Date d = null;
		try {
			d = sdf.parse(_date.substring(3,5)+_date.substring(5,8)+_date.substring(0,3)
					+date[3]+date[4]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sdf_yyyy_MM_dd.format(d);
	}
	public static boolean checkTodayByYyyy_MM_dd(String date){
		return sdf_yyyy_MM_dd.format(new Date()).equals(date);
	}
	private static String[] makeDateFormat(String date){
		String[] dateFormat = new String[5];
		dateFormat[0] = date.substring(0, 2);
		dateFormat[1] = date.substring(5, 8);
		
		StringBuffer sb = new StringBuffer();
		sb.append(dateFormat[1]).append("\n");
		sb.append(dateFormat[0]).append("\n");
		sb.append(date.substring(2, 5));
		dateFormat[2] = sb.toString().toUpperCase();
		dateFormat[3] = date.substring(8, 12);
		dateFormat[4] = date.substring(12, 14);
		return dateFormat;
	}
	/**输出某天的0:00点钟的Date */
	private static Date getZeroOfTheDay(Date date) {
		if (date == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		Calendar newcal = Calendar.getInstance();
		newcal.set(Calendar.YEAR, year);
		newcal.set(Calendar.MONTH, month);
		newcal.set(Calendar.DAY_OF_MONTH, day);

		newcal.set(Calendar.HOUR_OF_DAY, 0);
		newcal.set(Calendar.MINUTE, 0);
		newcal.set(Calendar.SECOND, 0);
		newcal.set(Calendar.MILLISECOND, 0);
		return newcal.getTime();
	}
	/** 返回给定日期前几天在某小时某分钟某秒的时间 */
	private static Date getBeforeDateByDays(Date date, int beforedays) {
		if (date == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		cal.set(Calendar.DAY_OF_YEAR, day - beforedays);
		return cal.getTime();
	}
	/** 返回给定日期前几天在某小时某分钟某秒的时间 */
	private static Date getAfterDateByDays(Date date, int afterdays) {
		return getBeforeDateByDays(date, -afterdays);
	}
}
