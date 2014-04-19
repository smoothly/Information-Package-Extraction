package data;

import java.util.TreeMap;

public class XVpair
{
	// <x, r.value>
	public TreeMap<String, Float> xv_source = new TreeMap<String, Float>();
	public TreeMap<String, Float> xv_target = new TreeMap<String, Float>();
	
	public void addXVpair(String s, float f, boolean source)
	{
		if (source)
		{
			xv_source.put(s, f);
		}
		else
		{
			xv_target.put(s, f);
		}
	}
//
//	public void addValue(String s, float f)
//	{
//
//		if (!xv.containsKey(s))
//		{
//			xv.put(s, f);
//		}
//		else
//		{
//			f += xv.get(s);
//			xv.put(s, f);
//		}
//	}

	// public class AXVpair implements Comparable<AXVpair>
	// {
	// public String x;
	// public float value;
	//
	// AXVpair(String s, float f)
	// {
	// x = s;
	// value = f;
	// }
	//
	// public int compareTo(AXVpair o)
	// {
	// if (x.compareTo(o.x) > 0)
	// return 1;
	// if (x.compareTo(o.x) < 0)
	// return -1;
	// return 0;
	// }
	// }
}
