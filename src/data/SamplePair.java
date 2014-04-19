package data;

public class SamplePair implements Comparable<SamplePair>
{
	public String e1;
	public String e2;
	public float value;
	public boolean positive;

	public SamplePair(String s1, String s2, float v, boolean p)
	{
		e1 = s1;
		e2 = s2;
		value = v;
		positive = p;
	}

	public int compareTo(SamplePair o)
	{
		if (value > o.value)
			return -1;
		else if (value < o.value)
			return 1;
		return 0;
	}

}
