package tools;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import steps.GeneticProgramming;
import data.CandidatePair;
import data.GlobalData;
import data.PropertyPair;
import data.SubjectPredicate;
import data.TfIdf;
import data.XVpair;

public class Blocking
{

	// <r.lable, treeMap<x, r.value>>
	static TreeMap<String, XVpair> H = new TreeMap<String, XVpair>();

	static TreeMap<String, TfIdf> tfidf_source = new TreeMap<String, TfIdf>();
	static TreeMap<String, TfIdf> tfidf_target = new TreeMap<String, TfIdf>();
	static int file_length_source = 0;
	static int file_length_target = 0;

	static public TreeSet<CandidatePair> blocking() throws IOException
	{
		System.out.println("start to block...");
		TreeSet<CandidatePair> result = new TreeSet<CandidatePair>();
		TreeSet<CandidatePair> middle_result = new TreeSet<CandidatePair>();
		tfidf_source = readFile(true);
		tfidf_target = readFile(false);
		PropertyPair.calSumConf();

		Iterator<SubjectPredicate> it = GeneticProgramming.source.keySet().iterator();
		while (it.hasNext())
		{
			SubjectPredicate sp = it.next();
			String p = sp.predicate;
			String s = sp.subject;
			String o = GeneticProgramming.source.get(sp);
			float sumconf = PropertyPair.source_property_sumconf.get(sp.predicate);
			String[] os = o.split(" ");
			float r_value = 1;

			for (int i = 0; i < os.length; i++)
			{
				if (PropertyPair.source_property_type.get(p) == GlobalData.P_TYPE_string
						|| PropertyPair.source_property_type.get(p) == GlobalData.P_TYPE_URI)
				{
					r_value = tfidf_source.get(os[i]).getValue(s + " " + p,
							file_length_source);
					// System.out.println(p + " " + os[i]+" "+r_value);
				}
				// System.out.println(os[i] + " " + sumconf * r_value);
				if (!H.containsKey(os[i]))
				{
					H.put(os[i], new XVpair());
				}
				H.get(os[i]).addXVpair(s, sumconf * r_value, true);
			}

		}

		it = GeneticProgramming.target.keySet().iterator();
		while (it.hasNext())
		{
			SubjectPredicate sp = it.next();
			String p = sp.predicate;
			String s = sp.subject;
			String o = GeneticProgramming.target.get(sp);
			float sumconf = PropertyPair.target_property_sumconf.get(sp.predicate);
			String[] os = o.split(" ");
			float r_value = 1;

			for (int i = 0; i < os.length; i++)
			{
				if (PropertyPair.target_property_type.get(p) == GlobalData.P_TYPE_string
						|| PropertyPair.target_property_type.get(p) == GlobalData.P_TYPE_URI)
				{
					r_value = tfidf_target.get(os[i]).getValue(s + " " + p,
							file_length_target);
				}
				if (!H.containsKey(os[i]))
				{
					H.put(os[i], new XVpair());
				}
				H.get(os[i]).addXVpair(s, sumconf * r_value, false);
			}
		}

		TreeMap<String, Float> maxx = new TreeMap<String, Float>();
		TreeMap<String, Float> maxt = new TreeMap<String, Float>();
		Iterator<String> its = H.keySet().iterator();
		while (its.hasNext())
		{
			String cur = its.next();
			if (H.get(cur).xv_source.size() == 0 || H.get(cur).xv_target.size() == 0)
			{
				continue;
			}
			Iterator<String> its1 = H.get(cur).xv_source.keySet().iterator();
			while (its1.hasNext())
			{
				String xs = its1.next();
				float vs = H.get(cur).xv_source.get(xs);
				Iterator<String> its2 = H.get(cur).xv_target.keySet().iterator();
				while (its2.hasNext())
				{
					String xt = its2.next();
					float vt = H.get(cur).xv_target.get(xt);
					middle_result.add(new CandidatePair(xs, xt, vs * vt));
					if (maxx.containsKey(xs))
					{
						if (vs * vt > maxx.get(xs))
						{
							maxx.put(xs, vs * vt);
						}
					}
					else
					{
						maxx.put(xs, vs * vt);
					}
					if (maxt.containsKey(xt))
					{
						if (vs * vt > maxt.get(xt))
						{
							maxt.put(xt, vs * vt);
						}
					}
					else
					{
						maxt.put(xt, vs * vt);
					}
				}
			}
		}
		Iterator<CandidatePair> itc = middle_result.iterator();
		while (itc.hasNext())
		{
			CandidatePair cur = itc.next();
			String c1 = cur.e1;
			String c2 = cur.e2;
			float v = cur.vs_vt;
			float max = Math.max(maxx.get(c1), maxt.get(c2));
			//System.out.println(v + " " + v / max);
			if (v >= 0.5 && v / max >= 0.1)
			{
				result.add(cur);
			}
		}
		System.out.println("blocking is done. " + result.size()
				+ " pairs candidates created.");
		return result;
	}

	// 读文件，将取值存成列表
	static public TreeMap<String, TfIdf> readFile(boolean source) throws IOException
	{
		TreeMap<String, TfIdf> tfidf = new TreeMap<String, TfIdf>();
		TreeMap<SubjectPredicate, String> spo = new TreeMap<SubjectPredicate, String>();

		if (source)
		{
			spo = GeneticProgramming.source;
		}
		else
		{
			spo = GeneticProgramming.target;
		}
		Iterator<SubjectPredicate> itsp = spo.keySet().iterator();
		while (itsp.hasNext())
		{
			SubjectPredicate sp = itsp.next();
			String s = sp.subject;
			String p = sp.predicate;

			String o = spo.get(sp);

			// System.out.println(p);
			if ((source && PropertyPair.source_property_type.containsKey(p) && (PropertyPair.source_property_type
					.get(p) == GlobalData.P_TYPE_string || PropertyPair.source_property_type
					.get(p) == GlobalData.P_TYPE_URI))
					|| (!source && PropertyPair.target_property_type.containsKey(p) && (PropertyPair.target_property_type
							.get(p) == GlobalData.P_TYPE_string || PropertyPair.target_property_type
							.get(p) == GlobalData.P_TYPE_URI)))
			{

				// System.out.println(p + " " + o);
				String[] w = o.split(" ");
				TreeMap<String, Integer> table = new TreeMap<String, Integer>();
				int i = 0;
				for (i = 0; i < w.length; i++)
				{
					if (!table.containsKey(w[i]))
						table.put(w[i], 1);
					else
					{
						int time = table.get(w[i]);
						table.put(w[i], time + 1);
					}
					
				}
				int file_length = i;
				// 对于table中的所有词，更新其tfidf值
				Iterator<String> it = table.keySet().iterator();
				while (it.hasNext())
				{
					String cur = it.next();
					if (!tfidf.containsKey(cur))
					{
						tfidf.put(cur, new TfIdf(cur));
					}

					tfidf.get(cur).fileList.put(s + " " + p,
							Float.valueOf(table.get(cur)) / Float.valueOf(file_length));

				}
			}
		}
		// 假设先存源
		if (file_length_source == 0)
			file_length_source = spo.size();
		else
			file_length_target = spo.size();
		return tfidf;
	}

}
