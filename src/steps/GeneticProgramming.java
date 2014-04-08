package steps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import tools.Utilities;
import data.Aggregation;
import data.Comparison;
import data.PropertyPair;
import data.SubjectPredicate;

public class GeneticProgramming
{
	static public int max_iteration = 30;
	static public int seed_number = 300;
	static public float possibility_crossover = 0.75f;
	static public float possibility_mutation = 0.25f;
	static public float elite_portion = 0.02f;

	static public boolean stop = false;

	static public TreeSet<String> candidates_source = new TreeSet<String>();
	static public TreeSet<String> candidates_target = new TreeSet<String>();

	// 保存精英种子的index
	static public int[] elite_index = null;
	static public float[] elite_mcc = null;
	static public int min_elite_index = 0;
	static public float min_elite_mcc = 0;
	static public int elite_number = 0;

	static public TreeMap<String, String> positive_pair = new TreeMap<String, String>();

	static public Aggregation[] aggs = null;
	static public float[] mcc = null;

	static private FileOutputStream fos = null;
	static private OutputStreamWriter osw = null;
	static private BufferedWriter bw = null;

	static public void initial(String filename1, String filename2, String positive_file)
			throws IOException
	{
		fos = new FileOutputStream("generation_average_mcc.data");
		osw = new OutputStreamWriter(fos);
		bw = new BufferedWriter(osw);
		aggs = new Aggregation[seed_number];
		mcc = new float[seed_number];
		for (int i = 0; i < seed_number; i++)
		{
			aggs[i] = new Aggregation();
		}
		// 读取candidates
		File file1 = new File(filename1);
		BufferedReader reader1 = null;

		File file2 = new File(filename2);
		BufferedReader reader2 = null;

		File file3 = new File(positive_file);
		BufferedReader reader3 = null;

		try
		{
			reader1 = new BufferedReader(new FileReader(file1));
			String temp1 = null;
			while ((temp1 = reader1.readLine()) != null)
			{
				candidates_source.add(temp1);
			}
			reader2 = new BufferedReader(new FileReader(file2));
			String temp2 = null;
			while ((temp2 = reader2.readLine()) != null)
			{
				candidates_target.add(temp2);
			}
			reader3 = new BufferedReader(new FileReader(file3));
			String temp3 = null;
			while ((temp3 = reader3.readLine()) != null)
			{
				String[] temp = temp3.split("\t");
				positive_pair.put(temp[0], temp[1]);
			}
		}
		catch (IOException ioe)
		{

		}
		// 初始化elite mcc
		elite_number = (int) (seed_number * elite_portion);
		System.out.println("精英数量：" + elite_number);
		elite_mcc = new float[elite_number];
		elite_index = new int[elite_number];
		for (int i = 0; i < elite_number; i++)
		{
			elite_mcc[i] = 0;
			elite_index[i] = 0;
		}
		// 初始化dbpedia数据库
		TreeMap<SubjectPredicate, String> source = SubjectPredicate
				.introduceTriples("data//dbpedia-film-subject.data");
		TreeMap<SubjectPredicate, String> target = SubjectPredicate
				.introduceTriples("data//linkedmdb-film-spo.data");
		Comparison.triples.putAll(source);
		Comparison.triples.putAll(target);
	}

	static public float calculate_mcc() throws IOException
	{
		float value = 0;
		float maxMCC = 0;
		float average_mcc = 0;
		for (int i = 0; i < aggs.length; i++)
		{
			Iterator<String> it1 = candidates_source.iterator();
			Iterator<String> it2 = candidates_target.iterator();
			// System.out.println(i + "th aggregation calculating...");
			int TP = 0;
			int TN = 0;
			int FP = 0;
			int FN = 0;
			while (it1.hasNext())
			{
				String candidate1 = it1.next();
				while (it2.hasNext())
				{
					String candidate2 = it2.next();
					value = aggs[i].get_aggregation_value(candidate1, candidate2);
					// if (value != 0)
					// {
					// System.out.println(candidate1 + " " + candidate2 + " " +
					// value
					// + " " + aggs[i].getThreshold());
					// }
					// System.out.println("value: "+value+" threshold:"+aggs[i].getThreshold());
					// 将其判断为true
					if (value > aggs[i].getThreshold())
					{
						if (positive_pair.get(candidate1).equals(candidate2))
						{
							// true positive
							TP++;
						}
						else
						{
							// false positive
							FP++;
						}
					}
					// 判断为false
					else
					{
						if (positive_pair.get(candidate1).equals(candidate2))
						{
							// false negative
							FN++;
						}
						else
						{
							// true negative
							TN++;
						}
					}
				}
			}

			float den = 1;
			if ((TP + FP) * (TP + FN) * (TN + FP) * (TN + FN) == 0)
				den = 1;
			else
				den = (TP + FP) * (TP + FN) * (TN + FP) * (TN + FN);
			mcc[i] = Float.valueOf(TP * TN - FP * FN) / (float) Math.pow(den, 0.5);
			// System.out.println(TP + " " + TN + " " + FP + " " + FN + " " +
			// mcc[i]);

			if (mcc[i] >= min_elite_mcc)
			{
				elite_index[min_elite_index] = i;
				elite_mcc[min_elite_index] = mcc[i];
				min_elite_index = Utilities.getMinIndex(elite_mcc);
				min_elite_mcc = elite_mcc[min_elite_index];
			}

		}
		for (int j = 0; j < elite_index.length; j++)
		{
			if (mcc[elite_index[j]] > maxMCC)
			{
				maxMCC = mcc[elite_index[j]];
			}
		}
		average_mcc = Utilities.getAverage(elite_mcc);
		bw.write(average_mcc + " ");

		return maxMCC;
	}

	static public void crossover()
	{
		System.out.println("crossovering...");
		boolean[] mark = new boolean[seed_number];
		for (int i = 0; i < seed_number; i++)
		{
			mark[i] = false;
		}
		for (int i = 0; i < elite_index.length; i++)
		{
			mark[elite_index[i]] = true;
		}
		for (int i = 0; i < aggs.length; i++)
		{
			if (mark[i])
			{
				continue;
			}
			double c = Math.random();
			// crossover
			if (c < possibility_crossover)
			{
				// choose another individual to crossover
				int index = (int) ((float) Math.random() * seed_number);
				while (mark[index])
				{
					index = (int) ((float) Math.random() * seed_number);
				}
				mark[index] = true;
				c = Math.random() * 5.0;
				if (c < 1)
				{
					// aggregation function crossover
					int temp = aggs[i].getAggregatin_type();
					aggs[i].setAggregatin_type(aggs[index].getAggregatin_type());
					aggs[index].setAggregatin_type(temp);
				}
				else if (c < 2)
				{
					// comparison crossover
					// System.out.println(aggs[index] + " " + aggs[i]);
					aggs[index] = aggs[i].exchange_comparison(aggs[index]);
				}
				else if (c < 3)
				{
					// crossover threshold
					float temp = aggs[i].getThreshold();
					aggs[i].setThreshold(aggs[index].getThreshold());
					aggs[index].setThreshold(temp);
				}
				else if (c < 4)
				{
					// crossover weight
					float[] temp = aggs[i].getWeight();
					aggs[i].setWeight(aggs[index].getWeight());
					aggs[index].setWeight(temp);
				}
				else
				{
					// crossover aggregation
					double rc = Math.random();
					if (rc < 0.5)
					{
						// System.out.println("exchange will happen:\n"+aggs[i].toString()+"\n"+aggs[index].toString());
						aggs[index] = aggs[i].exchange_aggregation(aggs[index]);
						// System.out.println("exchange happened:\n"+aggs[i].toString()+"\n"+aggs[index].toString());
					}
					else
					{// 将aggs[i]加入aggs[index]
						// System.out.println("exchange will happen:\n"+aggs[i].toString()+"\n"+aggs[index].toString());
						aggs[i] = aggs[index].exchange_aggregation(aggs[i]);
						// System.out.println("exchange happened:\n"+aggs[i].toString()+"\n"+aggs[index].toString());
					}
				}
			}
		}
	}

	static public void genetic_process() throws IOException
	{
		PropertyPair.init("data//property-pair.data");
		initial("data//linkedmdb-training-candidates.data",
				"data//dbpedia-training-candidates.data",
				"data//linkedmdb-dbpedia-training-positive.data");
		int gen = 0;
		while (gen < max_iteration)
		{
			bw.write(gen + " ");
			System.out.println(gen++ + "th generation");
			float maxmcc = calculate_mcc();
			System.out.println(maxmcc);
			if (maxmcc == 1)
			{
				break;
			}
			for (int i = 0; i < elite_number; i++)
			{
				System.out.print(elite_mcc[i] + " ");
			}
			if (gen == max_iteration)
			{
				break;
			}
			System.out.println();
			bw.newLine();
			crossover();
		}
		for (int i = 0; i < elite_number; i++)
		{
			bw.write(aggs[elite_index[i]] + " " + elite_mcc[i] + "\n");
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException
	{
		genetic_process();
		System.out.println();
	}
}
