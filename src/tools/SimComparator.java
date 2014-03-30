package tools;

import java.util.Comparator;

import data.PropertySimilarity.TargetProperty;

public class SimComparator implements Comparator<TargetProperty>
{
	public int compare(TargetProperty t1, TargetProperty t2)
	{
		if (t1.similarity > t2.similarity)
			return -1;
		else if (t1.similarity < t2.similarity)
			return 1;
		else
			return 0;
	}
}
