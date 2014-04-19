package steps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import tools.Blocking;
import tools.Utilities;
import data.Aggregation;
import data.CandidatePair;
import data.Comparison;
import data.PropertyPair;
import data.SamplePair;
import data.SubjectPredicate;

public class GeneticProgramming
{
	static public int max_iteration = 30;
	static public int seed_number = 300;
	static public float possibility_crossover = 0.75f;
	static public float possibility_mutation = 0.25f;
	static public float elite_portion = 0.018f;

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
	static public TreeMap<String, String> negative_pair = new TreeMap<String, String>();

	// triples
	static public TreeMap<SubjectPredicate, String> source = new TreeMap<SubjectPredicate, String>();
	static public TreeMap<SubjectPredicate, String> target = new TreeMap<SubjectPredicate, String>();

	static public Aggregation[] aggs = null;
	static public float[] mcc = null;

	static private FileOutputStream fos = null;
	static private OutputStreamWriter osw = null;
	static private BufferedWriter bw = null;

	static boolean[] FPFN = null;

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
		FPFN = new boolean[seed_number];
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
			System.out.println(candidates_source.size() + " source candidates added");
			reader1.close();

			reader2 = new BufferedReader(new FileReader(file2));
			String temp2 = null;
			while ((temp2 = reader2.readLine()) != null)
			{
				candidates_target.add(temp2);
			}
			reader2.close();

			System.out.println(candidates_target.size() + " target candidates added");
			reader3 = new BufferedReader(new FileReader(file3));
			String temp3 = null;
			while ((temp3 = reader3.readLine()) != null)
			{
				String[] temp = temp3.split("\t");
				positive_pair.put(temp[0], temp[1]);
			}
			reader3.close();
			System.out.println(positive_pair.size() + " positive pair added");
		}
		catch (IOException ioe)
		{

		}
		// 初始化negative pair
		Iterator<String> key = positive_pair.keySet().iterator();
		String temp = "";
		while (key.hasNext())
		{
			String cur = key.next();
			negative_pair.put(cur, temp);
			temp = positive_pair.get(cur);
		}
		negative_pair.put(negative_pair.firstKey(), temp);
		System.out.println(negative_pair.size() + " negative pair added");
		// 初始化elite mcc
		elite_number = (int) (seed_number * elite_portion);
		System.out.println("精英数量：" + elite_number);
		elite_mcc = new float[elite_number];
		elite_index = new int[elite_number];
		for (int i = 0; i < elite_number; i++)
		{
			elite_mcc[i] = -100000;
			elite_index[i] = 0;
		}
		// 初始化dbpedia数据库
		source = SubjectPredicate.introduceTriples("data//linkedmdb-film-spo.data", true);
		target = SubjectPredicate.introduceTriples("data//dbpedia-film-subject.data",
				false);
		Comparison.triples.putAll(source);
		Comparison.triples.putAll(target);

		// 初始化candidates_pair
		CandidatePair.candidate_pair = Blocking.blocking();
	}

	static public float calculate_mcc() throws IOException
	{
		min_elite_index = 0;
		min_elite_mcc = 0;
		float value = 0;
		float maxMCC = 0;
		float average_mcc = 0;
		for (int i = 0; i < elite_number; i++)
		{
			elite_mcc[i] = -100000;
			elite_index[i] = 0;
		}
		for (int i = 0; i < aggs.length; i++)
		{

			// System.out.print(i + " ");
			// System.out.println(i + "th aggregation calculating...");
			int TP = 0;
			int TN = 0;
			int FP = 0;
			int FN = 0;
			float[] agg_values = new float[CandidatePair.candidate_pair.size()];
			// System.out.println(agg_values.length + " aggeration values");
			// float thre = 0;
			int index = 0;
			// int thren = 1;
			// 计算阈值
			SamplePair[] samples = new SamplePair[positive_pair.size()
					+ negative_pair.size()];
			Iterator<String> sit = positive_pair.keySet().iterator();
			while (sit.hasNext())
			{
				String candidate1 = sit.next();
				String candidate2 = positive_pair.get(candidate1);
				value = aggs[i].get_aggregation_value(candidate1, candidate2);
				samples[index++] = new SamplePair(candidate1, candidate2, value, true);
			}
			sit = negative_pair.keySet().iterator();
			while (sit.hasNext())
			{
				String candidate1 = sit.next();
				String candidate2 = positive_pair.get(candidate1);
				value = aggs[i].get_aggregation_value(candidate1, candidate2);
				samples[index++] = new SamplePair(candidate1, candidate2, value, false);
			}

			Arrays.sort(samples);
			// for (int j = 0; j < samples.length; j++)
			// {
			// System.out.print(samples[j].value+" ");
			// }
			// System.out.println();
			float threshold = samples[0].value;
			float maxMcc = 0;
			if (samples[0].positive)
			{
				TP++;
			}
			else
			{
				FP++;
			}
			for (int j = 1; j < samples.length; j++)
			{
				if (samples[j].positive)
				{
					FN++;
				}
				else
				{
					TN++;
				}
			}
			float den = 1;
			if ((TP + FP) * (TP + FN) * (TN + FP) * (TN + FN) == 0)
				den = 1;
			else
				den = (TP + FP) * (TP + FN) * (TN + FP) * (TN + FN);
			maxMcc = Float.valueOf(TP * TN - FP * FN) / (float) Math.pow(den, 0.5);

			for (int j = 1; j < samples.length; j++)
			{
				if (samples[j].value == 0)
					break;
				if (samples[j].positive)
				{
					TP++;
					FN--;
				}
				else
				{
					FP++;
					TN--;
				}
				if ((TP + FP) * (TP + FN) * (TN + FP) * (TN + FN) == 0)
					den = 1;
				else
					den = (TP + FP) * (TP + FN) * (TN + FP) * (TN + FN);
				float temp = Float.valueOf(TP * TN - FP * FN)
						/ (float) Math.pow(den, 0.5);
				if (temp > maxMcc)
				{
					maxMcc = temp;
					threshold = samples[j].value;
				}
			}

			Iterator<CandidatePair> it = CandidatePair.candidate_pair.iterator();

			index = 0;
			aggs[i].setThreshold(threshold);
			TP = 0;
			TN = 0;
			FN = 0;
			FP = 0;
			while (it.hasNext())
			{
				CandidatePair cur = it.next();
				String candidate1 = cur.e1;
				// System.out.println(candidate1);

				String candidate2 = cur.e2;
				value = aggs[i].get_aggregation_value(candidate1, candidate2);
				agg_values[index] = value;
				// System.out.println(index + " comparisons");
				// System.out.println(aggs[i].toString());

				// if (agg_values[index] != 0)
				// {
				// System.out.println(candidate1 + " " + candidate2 + " "
				// + agg_values[index] + " " + aggs[i].getThreshold());
				// }
				// System.out.println("value: " + value + " threshold:"
				// + aggs[i].getThreshold());
				// 将其判断为true
				if (agg_values[index] >= aggs[i].getThreshold())
				{
					if (positive_pair.get(candidate1).equals(candidate2))
					{
						// true positive
						TP++;
					}
					else if (negative_pair.get(candidate1).equals(candidate2))
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
					else if (negative_pair.get(candidate1).equals(candidate2))
					{
						// true negative
						TN++;
					}
				}
				index++;
			}
			if (FP > FN)
			{
				FPFN[i] = true;
			}
			else
			{
				FPFN[i] = false;
			}
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
		bw.write(average_mcc + " " + maxMCC);
		System.out.println();
		return average_mcc;
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
				// mark[i] = true;
				// choose another individual to crossover
				int index = (int) ((float) Math.random() * seed_number);
				while (mark[index])
				{
					index = (int) ((float) Math.random() * seed_number);
				}
				// mark[index] = true;
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
					float temp = (aggs[i].getThreshold() + aggs[index].getThreshold()) / 2.0f;

					aggs[i].setThreshold(temp);
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

	static public void mutation()
	{
		System.out.println("mutation...");
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
				continue;

			if (Math.random() < possibility_mutation)
			{
				aggs[i].comparisons.add(new Comparison());

				if (aggs[i].comparisons.size() >= 2)
					aggs[i].comparisons.remove(0);
				else
				{
					aggs[i].aggregations.remove(0);
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

		float maxmcc = 0;
		while (gen < max_iteration)
		{
			bw.write(gen + " ");
			System.out.println(gen++ + "th generation");
			maxmcc = calculate_mcc();
			System.out.println(maxmcc);
			if (maxmcc == 1)
			{
				break;
			}
			for (int i = 0; i < elite_number; i++)
			{
				System.out.print(elite_mcc[i] + " ");
			}
			System.out.println();
			bw.newLine();
			crossover();
			mutation();
		}
		for (int i = 0; i < elite_number; i++)
		{
			bw.newLine();
			bw.write(aggs[elite_index[i]] + " " + elite_mcc[i]);
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException
	{
		genetic_process();
		System.out.println();
	}
}
