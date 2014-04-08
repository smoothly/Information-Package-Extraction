package tools;


public class StringFloatComparison implements Comparable<StringFloatComparison>
{
	String s = "";
	float fre = 0;

	public StringFloatComparison(String str, float frequency)
	{
		s = str;
		fre = frequency;
	}
//升序排列
	public int compareTo(StringFloatComparison o)
	{
		if (this.fre > o.fre)
			return -1;
		else if (this.fre < o.fre)
			return 1;
		else
			return 0;
	}

}
