package steps;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import tools.CalSimmilarity;
import tools.Utilities;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.LiteralRequiredException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

import data.GlobalData;
import data.PropertySimilarity;

/**
 * 计算conf值 根据属性的取值判断属性的相似度，相似度指标存入conf矩阵中
 * 
 * @author Emma Liu
 * 
 */
public class PropertyAlignment
{
	static private FileOutputStream fos = null;
	static private OutputStreamWriter osw = null;
	static private BufferedWriter bw = null;
	static private int sindex = 0;

	static public void propertyAlignment() throws IOException
	{
		fos = new FileOutputStream("conf-result");
		osw = new OutputStreamWriter(fos);
		bw = new BufferedWriter(osw);
		valuesOfPropertyViaNet();
		valuesOfProperty();
		int alignmentSize = GlobalData.value_scheme_date.size()
				* GlobalData.value_source_date.size()
				+ GlobalData.value_scheme_double.size()
				* GlobalData.value_source_double.size()
				+ GlobalData.value_scheme_int.size() * GlobalData.value_source_int.size()
				+ GlobalData.value_scheme_string.size()
				* GlobalData.value_source_string.size()
				+ GlobalData.value_scheme_uri.size() * GlobalData.value_source_uri.size();
		GlobalData.pss = new PropertySimilarity[alignmentSize];
		calConf(GlobalData.value_scheme_date, GlobalData.value_source_date);
		calConf(GlobalData.value_scheme_string, GlobalData.value_source_string);
		calConf(GlobalData.value_scheme_uri, GlobalData.value_source_uri);
		calConf(GlobalData.value_scheme_int, GlobalData.value_source_int);
		calConf(GlobalData.value_scheme_double, GlobalData.value_source_double);
		bw.close();
		System.out.println("conf calculated.");
	}

	/**
	 * 计算value1和value2两个数据集的属性的conf值
	 * 
	 * @param values1
	 * @param values2
	 * @throws IOException
	 */
	static private <T> void calConf(TreeMap<String, TreeSet<T>> values1,
			TreeMap<String, TreeSet<T>> values2) throws IOException
	{

		Iterator<String> is = values1.keySet().iterator();
		String s = "";
		String t = "";

		while (is.hasNext())
		{
			int tindex = 0;
			s = is.next();
			Iterator<String> it = values2.keySet().iterator();

			GlobalData.pss[sindex] = new PropertySimilarity(s, values2.size());
			while (it.hasNext())
			{
				t = it.next();
				float ros = values1.get(s).size();
				float rot = values2.get(t).size();
				float union = Utilities.intersectionSize(values1.get(s), values2.get(t));
				float conf = 2 * union / (ros + rot);
				String[] ss = s.split("/|#");
				String[] ts = t.split("/|#");
				String[] w1 = ss[ss.length - 1].split("([-_ ]|(?<=[^-_ A-Z])(?=[A-Z]))");
				String[] w2 = ts[ts.length - 1].split("([-_ ]|(?<=[^-_ A-Z])(?=[A-Z]))");

				float similarity = 0.0f;
				for (int i = 0; i < w1.length; i++)
				{
					for (int j = 0; j < w2.length; j++)
					{
						similarity += CalSimmilarity.run2(w1[i], w2[j]);
					}
				}
				similarity = similarity / (w1.length * w2.length);
				if (Float.isInfinite(similarity))
				{
					similarity = 1;
				}
				float sim = 0.5f * conf + 0.5f * similarity;
				GlobalData.pss[sindex].tps[tindex].setValue(t, sim);
				tindex++;
				bw.write(s + "\t" + t + "\t" + conf + " " + similarity + " " + sim);
				bw.newLine();
			}
			System.out.println(s);
			String selected = GlobalData.pss[sindex].getMaxSimilarityString();
			System.out.println(s + " max similarity " + selected);
			bw.write(s + " max similarity " + selected);
			bw.newLine();
			sindex++;
		}
	}

	/**
	 * 统计属性的取值(dbpedia)
	 */
	static public void valuesOfPropertyViaNet()
	{
		System.out.println("start to get values of properties in dbpedia");
		// source property
		Iterator<String> it = GlobalData.pSource.keySet().iterator();
		while (it.hasNext())
		{
			String cur = it.next();
			int type = GlobalData.pSource.get(cur);
			String sparqlQueryString = "select distinct ?o where {?s <" + cur
					+ "> ?o .} LIMIT 100";
			Query query = QueryFactory.create(sparqlQueryString);
			ARQ.getContext().setTrue(ARQ.useSAX);
			String service = "http://dbpedia.org/sparql/";
			QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query);
			try
			{
				ResultSet results = qexec.execSelect();
				for (; results.hasNext();)
				{
					QuerySolution soln = results.nextSolution();
					RDFNode rdfn = soln.get("?o");
					if (type == GlobalData.P_TYPE_date)
					{
						GlobalData.value_source_date.get(cur).add(rdfn.toString());
					}
					else if (type == GlobalData.P_TYPE_int)
					{
						try
						{
							int value = Integer.valueOf(rdfn.asLiteral().getValue()
									.toString());
							GlobalData.value_source_int.get(cur).add(value);
						}
						catch (NumberFormatException nfe)
						{
							try
							{
								Double value = Double.valueOf(rdfn.asLiteral().getValue()
										.toString());
								GlobalData.pSource.put(cur, GlobalData.P_TYPE_decimal);
								GlobalData.value_source_double.put(cur,
										new TreeSet<Double>());
								Iterator<Integer> iit = GlobalData.value_source_int.get(
										cur).iterator();
								while (iit.hasNext())
								{
									GlobalData.value_source_double.get(cur).add(
											Double.parseDouble(iit.next().toString()));
								}
								GlobalData.value_source_double.get(cur).add(value);
								type = GlobalData.P_TYPE_decimal;
								GlobalData.value_source_int.remove(cur);
							}
							catch (NumberFormatException nfe2)
							{
								GlobalData.pSource.put(cur, GlobalData.P_TYPE_string);
								GlobalData.value_source_string.put(cur,
										new TreeSet<String>());
								Iterator<Integer> iit = GlobalData.value_source_int.get(
										cur).iterator();
								while (iit.hasNext())
								{
									GlobalData.value_source_string.get(cur).add(
											iit.next().toString());
								}
								GlobalData.value_source_string.get(cur).add(
										rdfn.asLiteral().getValue().toString());
								type = GlobalData.P_TYPE_string;
								GlobalData.value_source_int.remove(cur);
							}

						}

					}
					else if (type == GlobalData.P_TYPE_decimal)
					{
						GlobalData.value_source_double.get(cur).add(
								Double.valueOf(rdfn.asLiteral().getValue().toString()));
					}
					else if (type == GlobalData.P_TYPE_string)
					{
						String[] ss = rdfn.toString().split(" ");
						for (int i = 0; i < ss.length; i++)
						{
							if (ss[i].length() != 0)
							{
								GlobalData.value_source_string.get(cur).add(ss[i]);
							}
						}
					}
					else if (type == GlobalData.P_TYPE_URI)
					{
						String[] ss = rdfn.toString().split("/|#");
						String[] ww = ss[ss.length - 1]
								.split("([-_ ]|(?<=[^-_ A-Z])(?=[A-Z]))");
						for (int i = 0; i < ww.length; i++)
						{
							if (ww[i].length() != 0)
							{
								GlobalData.value_source_uri.get(cur).add(ww[i]);
							}
						}
					}
				}
			}
			finally
			{
				qexec.close();
			}
		}
		System.out.println("properties in dbpedia's values are stored");
	}

	/**
	 * 统计属性取值(local)
	 */
	static public void valuesOfProperty()
	{
		System.out.println("start to get values of Properties in local data");
		// scheme property
		Iterator<String> it = GlobalData.pScheme.keySet().iterator();
		while (it.hasNext())
		{
			String cur = it.next();
			ptype(GlobalData.scheme, GlobalData.scheme.getProperty(cur));
		}
		System.out.println("properties in local data's values are stored");
	}

	static private int ptype(Model m, Property p)
	{

		int invalidValue = 0;
		TreeSet<String> string_result = new TreeSet<String>();
		TreeSet<Integer> int_result = new TreeSet<Integer>();
		TreeSet<Double> double_result = new TreeSet<Double>();
		TreeSet<String> date_result = new TreeSet<String>();
		TreeSet<String> uri_result = new TreeSet<String>();

		int[] typeStat = new int[5];
		for (int i = 0; i < 5; i++)
		{
			typeStat[i] = 0;
		}
		NodeIterator ni = m.listObjectsOfProperty(p);
		while (ni.hasNext())
		{
			RDFNode n = ni.next();
			if (n.isURIResource())
			{
				typeStat[GlobalData.P_TYPE_URI]++;
				String[] ss = n.toString().split("/");
				for (int i = 0; i < ss.length; i++)
				{
					if (ss[i].length() > 0 && !ss[i].equals("http:"))
					{
						uri_result.add(ss[i]);
					}
				}
			}
			else
			{
				Literal l = null;
				try
				{
					l = n.asLiteral();
				}
				catch (LiteralRequiredException lre)
				{
					invalidValue++;
					continue;
				}
				String cur = l.getValue().toString();
				if (Utilities.isInteger(cur))
				{
					typeStat[GlobalData.P_TYPE_int]++;
					int_result.add(Integer.parseInt(cur));
				}
				else if (Utilities.isDouble(cur))
				{
					typeStat[GlobalData.P_TYPE_decimal]++;
					double_result.add(Double.parseDouble(String.format(cur, "%.2f")));
				}

				else if (Utilities.isDate(cur))
				{
					typeStat[GlobalData.P_TYPE_date]++;
					date_result.add(cur);
				}
				else
				{
					typeStat[GlobalData.P_TYPE_string]++;
					String[] ss = n.toString().split(" ");
					for (int i = 0; i < ss.length; i++)
					{
						if (ss[i].length() > 0)
						{
							string_result.add(ss[i]);
						}
					}
				}
			}
		}
		if (invalidValue > 0)
		{
			System.out.println(p.getURI() + " has " + invalidValue + " invalid nodes.");
		}
		int typeNumber = Utilities.getMaxIndex(typeStat);
		if (typeNumber == GlobalData.P_TYPE_date)
		{
			GlobalData.value_scheme_date.put(p.getURI(), date_result);
		}
		else if (typeNumber == GlobalData.P_TYPE_decimal)
		{
			GlobalData.value_scheme_double.put(p.getURI(), double_result);
		}
		else if (typeNumber == GlobalData.P_TYPE_int)
		{
			GlobalData.value_scheme_int.put(p.getURI(), int_result);
		}
		else if (typeNumber == GlobalData.P_TYPE_string)
		{
			GlobalData.value_scheme_string.put(p.getURI(), string_result);
		}
		else if (typeNumber == GlobalData.P_TYPE_URI)
		{
			GlobalData.value_scheme_uri.put(p.getURI(), uri_result);
		}
		return typeNumber;
	}
}
