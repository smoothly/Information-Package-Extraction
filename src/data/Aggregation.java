package data;

import java.util.ArrayList;

public class Aggregation
{
	static private final int AGGREGATION_FUNCTION_MAX = 0;
	//static private final int AGGREGATION_FUNCTION_MIN = 2;
	static private final int AGGREGATION_FUNCTION_AVE = 1;

	private float aggregation_value = 0;

	public void setAggregatin_type(int aggregatin_type)
	{
		this.aggregatin_type = aggregatin_type;
	}

	private int aggregatin_type = 0;

	public int getAggregatin_type()
	{
		return aggregatin_type;
	}

	public ArrayList<Aggregation> aggregations = new ArrayList<Aggregation>();
	public ArrayList<Comparison> comparisons = new ArrayList<Comparison>();

	private float[] weight = {0, 0};

	public float[] getWeight()
	{
		return weight;
	}

	public void setWeight(float[] weight)
	{
		this.weight = weight;
	}

	private float threshold = 0;

	public Aggregation()
	{
		comparisons.add(new Comparison());
		comparisons.add(new Comparison());
		weight[0] = 1 - (float) Math.random();
		weight[1] = 1 - weight[0];
		aggregatin_type = (int) (Math.random() * 2);
		setThreshold(0);
		// System.out.println("创建aggregation threshold:" + this.getThreshold() +
		// " weight:"
		// + weight[0] + " " + weight[1]);
	}

	/**
	 * @return the aggregation_value
	 */
	public float get_aggregation_value(String can1, String can2)
	{
		float[] num = {0, 0};
		int index = 0;

		if (comparisons.size() > 0)
		{
			num[index++] = comparisons.get(0).getComparison_value(can1, can2);
		}
		if (comparisons.size() > 1)
		{
			num[index++] = comparisons.get(1).getComparison_value(can1, can2);
		}
		if (aggregations.size() > 0)
		{
			num[index++] = aggregations.get(0).get_aggregation_value(can1, can2);
		}
		if (aggregations.size() > 1)
		{
			num[index++] = aggregations.get(1).get_aggregation_value(can1, can2);
		}
		if (aggregatin_type == AGGREGATION_FUNCTION_MAX)
		{
			aggregation_value = Math.max(num[0] * weight[0], num[1] * weight[1]);
		}
		else if (aggregatin_type == AGGREGATION_FUNCTION_AVE)
		{
			aggregation_value = ((num[0] * weight[0] + num[1] * weight[1]) / 2.0f);
		}
		// if (this.getThreshold() == -1 || this.getThreshold() == 0)
		// {
		this.setThreshold((aggregation_value + threshold) / 2.0f);
		// }
		// if(aggregation_value!=0)
		// {
		// System.out.println("有值");
		// }
		return aggregation_value;
	}

	/**
	 * @return the threshold
	 */
	public float getThreshold()
	{
		return threshold;
	}

	/**
	 * @param threshold
	 *            the threshold to set
	 */
	public void setThreshold(float threshold)
	{
		this.threshold = threshold;
	}

	public String toString()
	{
		String s = "[Aggeration: Threshold=" + this.getThreshold() + " Aggregation type="
				+ this.getAggregatin_type();

		if (comparisons.size() > 0)
		{
			s += comparisons.get(0).toString();
		}
		if (comparisons.size() > 1)
		{
			s += comparisons.get(1).toString();
		}
		if (aggregations.size() > 0)
		{
			s += aggregations.get(0).toString();
		}
		if (aggregations.size() > 1)
		{
			s += aggregations.get(1).toString();
		}

		return s;
	}

	/**
	 * exchange this's and a's comparisons
	 * 
	 * @param a
	 * @return
	 */
	public Aggregation exchange_comparison(Aggregation a)
	{
		double rc = Math.random() * 2;
		double rc1 = Math.random();
		double rc2 = Math.random();
		Comparison temp = new Comparison();
		Comparison temp2 = new Comparison();
		Aggregation temp3 = new Aggregation();
		Aggregation temp4 = new Aggregation();
		if (this.comparisons.size() == 2 && a.comparisons.size() == 2)
		{
			int i1 = 0;
			if (rc1 < 0.5)
			{
				i1 = 1;
			}
			int i2 = 0;
			if (rc2 < 0.5)
			{
				i2 = 1;
			}
			temp = this.comparisons.get(i1);
			temp2 = a.comparisons.get(i2);
			this.comparisons.add(temp2);
			a.comparisons.add(temp);
			this.comparisons.remove(i1);
			a.comparisons.remove(i2);
		}
		else if (this.comparisons.size() == 2 && a.comparisons.size() == 1)
		{
			int i = 0;
			if (rc1 < 0.5)
			{
				i = 1;
			}
			temp = this.comparisons.get(i);
			this.comparisons.remove(i);
			if (rc < 1)
			{
				temp2 = a.comparisons.get(0);
				this.comparisons.add(temp2);
				a.comparisons.remove(0);
			}
			else
			{
				temp3 = a.aggregations.get(0);
				this.aggregations.add(temp3);
				a.aggregations.remove(0);
			}
			a.comparisons.add(temp);
		}
		else if (this.comparisons.size() == 2 && a.comparisons.size() == 0)
		{
			// 交换1， 3
			int i1 = 0;
			if (rc1 < 0.5)
			{
				i1 = 1;
			}
			int i2 = 0;
			if (rc2 < 0.5)
			{
				i2 = 1;
			}
			temp = this.comparisons.get(i1);
			this.comparisons.remove(i1);
			temp3 = a.aggregations.get(i2);
			this.aggregations.add(temp3);
			a.aggregations.remove(i2);
			a.comparisons.add(temp);
		}
		else if (this.comparisons.size() == 1 && a.comparisons.size() == 2)
		{
			int i = 0;
			if (rc1 < 0.5)
			{
				i = 1;
			}
			if (rc < 1)
			{
				temp = this.comparisons.get(0);
				this.comparisons.remove(0);
				temp2 = a.comparisons.get(i);
				this.comparisons.add(temp2);
				a.comparisons.remove(i);
				a.comparisons.add(temp);
			}
			else
			{
				temp3 = this.aggregations.get(0);
				this.aggregations.remove(0);
				temp2 = a.comparisons.get(i);
				this.comparisons.add(temp2);
				a.comparisons.remove(i);
				a.aggregations.add(temp3);
			}
		}
		else if (this.comparisons.size() == 1 && a.comparisons.size() == 1)
		{
			if (rc < 1)
			{
				temp = this.comparisons.get(0);

				if (rc1 < 0.5)
				{
					temp2 = a.comparisons.get(0);
					this.comparisons.add(temp2);
					a.comparisons.add(temp);
					a.comparisons.remove(0);
				}
				else
				{
					temp3 = a.aggregations.get(0);
					this.aggregations.add(temp3);
					a.comparisons.add(temp);
					a.aggregations.remove(0);
				}
				this.comparisons.remove(0);
			}
			else
			{
				temp3 = this.aggregations.get(0);
				if (Math.random() < 0.5)
				{
					temp2 = a.comparisons.get(0);
					this.comparisons.add(temp2);
					a.aggregations.add(temp3);
					a.comparisons.remove(0);
				}
				else
				{
					temp4 = a.aggregations.get(0);
					this.aggregations.add(temp4);
					a.aggregations.add(temp3);
					a.aggregations.remove(0);
				}
				this.aggregations.remove(0);
			}
		}
		else if (this.comparisons.size() == 1 && a.comparisons.size() == 0)
		{
			if (rc < 1)
			{
				temp = this.comparisons.get(0);

				int i = 0;
				if (rc1 < 0.5)
				{
					i = 1;
				}
				temp3 = a.aggregations.get(i);
				this.aggregations.add(temp3);
				a.comparisons.add(temp);
				a.aggregations.remove(i);
				this.comparisons.remove(0);
			}
			else
			{
				temp3 = this.aggregations.get(0);
				int i = 0;
				if (rc1 < 0.5)
				{
					i = 1;
				}
				temp4 = a.aggregations.get(i);
				this.aggregations.add(temp4);
				a.aggregations.add(temp3);
				a.aggregations.remove(i);
				this.aggregations.remove(0);

			}
		}
		else if (this.comparisons.size() == 0 && a.comparisons.size() == 2)
		{
			int i1 = 0;
			if (rc1 < 0.5)
			{
				i1 = 1;
			}
			int i2 = 0;
			if (rc2 < 0.5)
			{
				i2 = 1;
			}
			temp3 = this.aggregations.get(i1);

			temp2 = a.comparisons.get(i2);
			this.comparisons.add(temp2);
			a.aggregations.add(temp3);
			a.comparisons.remove(i2);
			this.aggregations.remove(i1);
		}
		else if (this.comparisons.size() == 0 && a.comparisons.size() == 1)
		{
			int i = 0;
			if (rc1 < 0.5)
			{
				i = 1;
			}
			temp3 = this.aggregations.get(i);
			if (rc < 1)
			{
				this.aggregations.add(a.aggregations.get(0));
				a.aggregations.add(temp3);
				a.aggregations.remove(0);
			}
			else
			{
				this.comparisons.add(a.comparisons.get(0));
				a.aggregations.add(temp3);
				a.comparisons.remove(0);
			}
			this.aggregations.remove(i);
		}
		else if (this.comparisons.size() == 0 && a.comparisons.size() == 0)
		{
			int i1 = 0;
			if (rc1 < 0.5)
			{
				i1 = 1;
			}
			int i2 = 0;
			if (rc2 < 0.5)
			{
				i2 = 1;
			}
			temp3 = this.aggregations.get(i1);
			a.aggregations.add(temp3);
			this.aggregations.add(a.aggregations.get(i2));
			this.aggregations.remove(i1);
			a.aggregations.remove(i2);
		}
		return a;
	}

	/**
	 * replace a1's comparison with a2
	 * 
	 * @param a1
	 * @param a2
	 */
	public Aggregation exchange_aggregation(Aggregation a)
	{
		Aggregation[] result = new Aggregation[2];
		result[0] = this;
		result[1] = a;
		if (result[0].comparisons.size() == 2)
		{
			double rrc = Math.random();
			if (rrc < 0.5)
			{
				// 用aags[index]替换aggs[i].0
				result[0].comparisons.remove(0);
				result[0].aggregations.add(result[1]);
				result[1] = new Aggregation();
				return result[1];
			}
			else
			{
				// 将aags[index]替换aggs[i].1
				result[0].comparisons.remove(1);
				result[0].aggregations.add(result[1]);
				result[1] = new Aggregation();
				return result[1];
			}
		}
		else if (result[0].comparisons.size() == 1)
		{
			// 将这个comparison替换或将另一个aggregation替换
			double rrc = Math.random();
			if (rrc < 0.5)
			{
				result[0].comparisons.clear();
				result[0].aggregations.add(result[1]);
				result[1] = new Aggregation();
				return result[1];
			}
			else
			{
				result[0].aggregations.clear();
				result[0].aggregations.add(result[1]);
				result[1] = new Aggregation();
				return result[1];
			}
		}
		else
		{
			// 将一个aggregation替换
			double rrc = Math.random();
			if (rrc < 0.5)
			{
				result[0].aggregations.remove(0);
				result[0].aggregations.add(result[1]);
				result[1] = new Aggregation();
				return result[1];
			}
			else
			{
				result[0].aggregations.remove(1);
				result[0].aggregations.add(result[1]);
				result[1] = new Aggregation();
				return result[1];
			}

		}
	}
}
