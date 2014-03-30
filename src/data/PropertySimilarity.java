package data;

import java.util.ArrayList;
import java.util.Arrays;

import tools.SimComparator;

public class PropertySimilarity
{

	public PropertySimilarity(String p1, int tpsize)
	{
		super();
		this.p1 = p1;
		this.tps = new TargetProperty[tpsize];
		for (int i = 0; i < tpsize; i++)
		{
			this.tps[i] = new TargetProperty();
		}
	}

	public String p1;
	public TargetProperty[] tps;

	public class TargetProperty
	{

		public TargetProperty()
		{
			super();
		}

		public String getP2()
		{
			return p2;
		}

		public void setValue(String p2, float similarity)
		{
			this.p2 = p2;
			this.similarity = similarity;
		}

		public float getSimilarity()
		{
			return similarity;
		}

		public String p2 = "";
		public float similarity = 0;
	}

	public ArrayList<String> getMaxSimilarity()
	{
		ArrayList<String> result = new ArrayList<String>();
		Arrays.sort(this.tps, new SimComparator());

		float max = this.tps[0].getSimilarity();
		if (max > 0.5)
		{
			result.add(this.tps[0].getP2());
		}
		int i = 1;
		while (this.tps.length > i && this.tps[i].getSimilarity() == max && max > 0.5)
		{
			result.add(this.tps[i].getP2());
		}
		return result;
	}

	public String getMaxSimilarityString()
	{
		String result = "";
		if(this.tps.length==0)
		{
			return "";
		}
		Arrays.sort(this.tps, new SimComparator());
		
		float max = this.tps[0].getSimilarity();
		if (max > 0.5)
		{
			result = this.tps[0].getP2();
		}
		int i = 1;
		while (this.tps.length > i && this.tps[i].getSimilarity() == max )//&& max > 0.5)
		{
			result = result + " " + this.tps[i].getP2();
			i++;
		}
		return result;
	}
}
