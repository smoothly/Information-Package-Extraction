package tools;

import java.util.TreeSet;

import org.apache.xerces.util.URI;
import org.apache.xerces.util.URI.MalformedURIException;

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

		if (s.startsWith("http://"))
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
		String[] ymd = s.split("-");
		if (ymd.length != 3)
		{
			return false;
		}
		else if (!isInteger(ymd[0]) || !isInteger(ymd[1]) || !isInteger(ymd[2]))
		{
			return false;
		}
		else if (Integer.parseInt(ymd[1]) > 12 || Integer.parseInt(ymd[1]) < 0
				|| Integer.parseInt(ymd[2]) > 31 || Integer.parseInt(ymd[2]) < 0)
		{
			return false;
		}
		return true;
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

}
