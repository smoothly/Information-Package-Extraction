package tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TreeSet;

public class Utilities
{
	static public boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		}
		catch (NumberFormatException ne)
		{
			return false;
		}
	}

	static public boolean isDouble(String s)
	{
		if (isInteger(s))
		{
			return false;
		}
		try
		{
			Double.parseDouble(s);
			return true;
		}
		catch (NumberFormatException ne)
		{
			return false;
		}
	}

	static public boolean isURI(String s)
	{

		if (s.startsWith("http://") || s.startsWith("https://") || s.startsWith("ftp://")|| s.startsWith("http:/"))
		{
			return true;
		}
		return false;
	}

	/**
	 * identify yyyy-mm-dd format date
	 * 
	 * @param s
	 * @return
	 */
	static public boolean isDate(String s)
	{
		boolean result = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss hh:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-mm-dd");
		try
		{
			sdf.parse(s);
			result = true;
		}
		catch (ParseException pe)
		{
		}
		try
		{
			sdf2.parse(s);
			result = true;
		}
		catch (ParseException pe)
		{
		}
		// String[] ymd = s.split("-");
		// if (ymd.length != 3)
		// {
		// return false;
		// }
		// else if (!isInteger(ymd[0]) || !isInteger(ymd[1]) ||
		// !isInteger(ymd[2]))
		// {
		// return false;
		// }
		// else if (Integer.parseInt(ymd[1]) > 12 || Integer.parseInt(ymd[1]) <
		// 0
		// || Integer.parseInt(ymd[2]) > 31 || Integer.parseInt(ymd[2]) < 0)
		// {
		// return false;
		// }
		return result;
	}

	static public int getMaxIndex(int[] a)
	{
		int index = 0;
		int max = a[0];
		for (int i = 1; i < a.length; i++)
		{
			if (a[i] > max)
			{
				index = i;
				max = a[i];
			}
		}
		return index;
	}
	static public int getMinIndex(float[] a)
	{
		int index = 0;
		float min = a[0];
		for (int i = 1; i < a.length; i++)
		{
			if (a[i] < min)
			{
				index = i;
				min = a[i];
			}
		}
		return index;
	}
	
	static public float getAverage(float[] a)
	{
		float ave = 0;
		for (int i = 1; i < a.length; i++)
		{
			ave+=a[i];
		}
		return ave/a.length;
	}
	

	public static <T> int intersectionSize(TreeSet<T> t1, TreeSet<T> t2)
	{
		int size = 0;
		for (T o : t1)
		{
			if (t2.contains(o))
			{
				size++;
			}
		}
		return size;
	}

	public static <T> int[] unionAndIntersectionSize(TreeSet<T> t1, TreeSet<T> t2)
	{
		int size1 = 0;
		int size2 = 0;
		for (T o : t1)
		{
			if (t2.contains(o))
			{
				size2++;
			}
			else
			{
				size1++;
			}
		}
		size1 += t2.size();
		//result[0]为并集大小，size2为交集大小
		int[] result = {size1, size2};
		return result;
	}

}
