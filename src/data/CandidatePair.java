package data;

import java.util.TreeSet;

/**
 * 候选集对
 * 
 * @author Emma Liu
 * 
 */
public class CandidatePair implements Comparable<CandidatePair>
{
	static public TreeSet<CandidatePair> candidate_pair = new TreeSet<CandidatePair>();
	
	public String e1;
	public String e2;
	public float vs_vt = 0;

	public CandidatePair(String s1, String s2, float vv)
	{
		e1 = s1;
		e2 = s2;
		vs_vt = vv;
	}

	public int compareTo(CandidatePair o)
	{
		if (this.vs_vt > o.vs_vt)
			return 1;
		else if (this.vs_vt < o.vs_vt)
			return -1;
		else
		{
			if (this.e1.compareTo(o.e1) > 0)
			{
				return 1;
			}
			else if (this.e1.compareTo(o.e1) < 0)
			{
				return -1;
			}
			else
			{
				if (this.e2.compareTo(o.e2) > 0)
					return 1;
				else if (this.e2.compareTo(o.e2) < 0)
					return -1;
				else
					return 0;
			}
		}
	}
}
