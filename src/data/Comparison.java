package data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import tools.CalEditDistance;
import tools.CalSemanticSimmilarity;
import tools.Utilities;

public class Comparison
{
	static public final int COMPARISON_EDIT_DIS = 0;
	static public final int COMPARISON_JACCARD_DIS = 1;
	static public final int COMPARISON_NUMERIC_DIS = 2;
	static public final int COMPARISON_SEMANTIC_SIM = 3;
	static public final int COMPARISON_DATE_DIS = 4;

	static public TreeMap<SubjectPredicate, String> triples = new TreeMap<SubjectPredicate, String>();

	String property_from_source = "";
	String property_from_target = "";

	private int comparison_type = 0;
	private float comparison_value = 0;
	private int property_type = 0;

	public String toString()
	{
		return "[Comparison: p1=" + property_from_source + " p2=" + property_from_target
				+ " comparison type=" + comparison_type + "]";
	}

	/**
	 * 创建一个初始comparison, 随机选择comparison method，对于日期类和数字类属性，确定其comparison
	 * method，字符串类和uri类随机选择
	 * 
	 */
	public Comparison()
	{
		// 随机选择属性对
		int choose_pair = (int) (Math.random() * PropertyPair.property_pair.size());

		property_from_source = PropertyPair.property_pair.get(choose_pair).p_from_source;
		property_from_target = PropertyPair.property_pair.get(choose_pair).p_from_target;
		property_type = PropertyPair.property_pair.get(choose_pair).type;
		// System.out.println("选择属性对：" + choose_pair + " " +
		// property_from_source + " "
		// + property_from_target + " type:" + this.property_type);

		if (property_type == GlobalData.P_TYPE_date)
		{
			comparison_type = COMPARISON_DATE_DIS;
		}
		else if (property_type == GlobalData.P_TYPE_decimal
				|| property_type == GlobalData.P_TYPE_int)
		{
			comparison_type = COMPARISON_NUMERIC_DIS;
		}
		else if (property_type == GlobalData.P_TYPE_string
				|| property_type == GlobalData.P_TYPE_URI)
		{
			double random = Math.random();
			if (random <= 1.0 / 3.0)
			{
				comparison_type = COMPARISON_SEMANTIC_SIM;
			}
			else if (random <= 1.0 / 6.0)
			{
				comparison_type = COMPARISON_JACCARD_DIS;
			}
			else
			{
				comparison_type = COMPARISON_EDIT_DIS;
			}
		}
		// System.out.println("comparison type:" + comparison_type);
	}

	public int getComparison_type()
	{
		return comparison_type;
	}

	public void setComparison_type(int comparison_type)
	{
		this.comparison_type = comparison_type;
	}

	public String getObject(String s, String p, String dir, String service,
			int property_type)
	{
		// if (!triples.containsKey(new SubjectPredicate(s, p)))
		// {
		// triples.put(new SubjectPredicate(s, p),
		// SubjectPredicate.createOneTriple(s, p, dir, service));
		// }
		//
		String result = triples.get(new SubjectPredicate(s, p));
//		if (result != null && !result.equals(""))
//		{
//			System.out.println(result);
//		}
		// s = "<" + s + ">";
		// p = "<" + p + ">";
		// String prefix;
		// String pc;
		// if (p.contains("#"))
		// {
		// prefix = p.substring(0, p.lastIndexOf("#") + 1);
		// pc = p.substring(p.lastIndexOf("#") + 1, p.length());
		// }
		// else
		// {
		// prefix = p.substring(0, p.lastIndexOf("/") + 1);
		// pc = p.substring(p.lastIndexOf("/") + 1, p.length());
		// }
		// s = s.replace("\"", "%22");
		// s = s.replace("|", "%7C");
		// s = s.replace("`", "%60");
		// String sparqlQueryString = "PREFIX pre: <" + prefix
		// + ">\nselect distinct ?o where { <" + s + "> pre:" + pc + " ?o.}";
		// ARQ.getContext().setTrue(ARQ.useSAX);
		// // System.out.println(sparqlQueryString);
		// Query query = QueryFactory.create(sparqlQueryString);
		// QueryExecution qexec = null;
		// String result = "";
		// if (service.length() > 0)
		// {
		// qexec = QueryExecutionFactory.sparqlService(service, query);
		// }
		// else
		// {
		// Dataset dataset = TDBFactory.createDataset(dir);
		// Model model = dataset.getDefaultModel();
		// qexec = QueryExecutionFactory.create(sparqlQueryString, model);
		// }
		//
		// ResultSet results = qexec.execSelect();
		// while (results.hasNext())
		// {
		// QuerySolution soln = results.nextSolution();
		// RDFNode rdfn = soln.get("?o");
		// // System.out.println("property:" + p + " type:" +
		// // property_type);
		// // System.out.println("有结果了："+rdfn.toString());
		// if (property_type == GlobalData.P_TYPE_URI
		// || Utilities.isURI(rdfn.toString()))
		// {
		// String[] ss = rdfn.toString().split("/|#");
		// String[] ww = ss[ss.length -
		// 1].split("([-_ ]|(?<=[^-_ A-Z])(?=[A-Z]))");
		// for (int i = 0; i < ww.length; i++)
		// {
		// if (ww[i].length() != 0)
		// {
		// result = result + ww[i];
		// result = result + " ";
		// }
		// }
		// }
		// else
		// {
		//
		// String value = rdfn.asLiteral().getLexicalForm();
		// if (property_type == GlobalData.P_TYPE_string)
		// {
		//
		// String temp = rdfn.toString().replace("/n", " ");
		// String[] ss = temp.split(" ");
		// for (int i = 0; i < ss.length; i++)
		// {
		// if (ss[i].length() != 0)
		// {
		// result = result + ss[i];
		// result = result + " ";
		// }
		// }
		//
		// }
		// else
		// {
		// result = result + value;
		// result = result + " ";
		// }
		// }
		// }
		//
		// qexec.close();

		return result;
	}

	public float getComparison_value(String candidate1, String candidate2)
	{

		String dir1 = "F://lab//workspace//DBpediaAnalyzer//tdb";
		String dir = "F://本体库数据及软件//linkedmdb-latest-dump//linkedmdb-tdb";
		String source_property_value = getObject(candidate1, property_from_source, dir,
				"", property_type);
		String target_property_value = getObject(candidate2, property_from_target, dir1,
				"", property_type);

		if (source_property_value == null || target_property_value == null
				|| source_property_value.length() == 0
				|| target_property_value.length() == 0)
		{
			return 0;
		}
		// System.out.println("可以对比");
		String[] sourceValues = source_property_value.split(" ");
		String[] targetValues = target_property_value.split(" ");
		int times = 0;
		for (int i = 0; i < sourceValues.length; i++)
		{
			for (int j = 0; j < targetValues.length; j++)
			{
				times++;
				if (comparison_type == COMPARISON_DATE_DIS)
				{
					comparison_value += comparison_date(sourceValues[i], targetValues[j]);
				}
				else if (comparison_type == COMPARISON_NUMERIC_DIS)
				{
					try
					{
						if (Float.valueOf(sourceValues[i]) == Float
								.valueOf(targetValues[j]))
							comparison_value += 1;
						else
							comparison_value += 0;
					}
					catch (NumberFormatException nfe)
					{
						System.out.println(sourceValues[i] + " or " + targetValues[j]
								+ " can't be converted to number.");
					}
				}
				else
				{
					if (property_type == GlobalData.P_TYPE_URI)
					{
						comparison_value += comparison_uri(sourceValues[i],
								targetValues[j], comparison_type);
					}
					else
					{
						comparison_value += comparison_string(sourceValues[i],
								targetValues[j], comparison_type);
					}
				}
			}
		}

		return comparison_value / times;
	}

	private float comparison_string(String source_property_value,
			String target_property_value, int comparison_type)
	{
		TreeSet<String> value1 = new TreeSet<String>();
		TreeSet<String> value2 = new TreeSet<String>();
		String temp = source_property_value.toString().replace("/n", " ");
		String[] ss1 = temp.split(" ");
		for (int i = 0; i < ss1.length; i++)
		{
			if (ss1[i].length() != 0)
			{
				value1.add(ss1[i]);
			}
		}
		temp = target_property_value.toString().replace("/n", " ");
		String[] ss2 = temp.split(" ");
		for (int i = 0; i < ss2.length; i++)
		{
			if (ss2[i].length() != 0)
			{
				value2.add(ss2[i]);
			}
		}
		return comparison_string_set(value1, value2, comparison_type);
	}

	private float comparison_uri(String source_property_value,
			String target_property_value, int comparison_type)
	{
		TreeSet<String> value1 = new TreeSet<String>();
		TreeSet<String> value2 = new TreeSet<String>();
		// 如果取值是URI，则将前缀去掉，进行tokenize，再存入values中。
		String[] ss1 = source_property_value.toString().split("/|#");
		String[] ww1 = ss1[ss1.length - 1].split("([-_ ]|(?<=[^-_ A-Z])(?=[A-Z]))");
		for (int i = 0; i < ww1.length; i++)
		{
			if (ww1[i].length() != 0)
			{
				value1.add(ww1[i]);
			}
		}
		String[] ss2 = source_property_value.toString().split("/|#");
		String[] ww2 = ss2[ss2.length - 1].split("([-_ ]|(?<=[^-_ A-Z])(?=[A-Z]))");
		for (int i = 0; i < ww2.length; i++)
		{
			if (ww2[i].length() != 0)
			{
				value2.add(ww2[i].toLowerCase());
			}
		}
		return comparison_string_set(value1, value2, comparison_type);
	}

	// TODO add other comparison method
	// the value represents similarity, which means, if the value is small, the
	// two treeset are different
	private float comparison_string_set(TreeSet<String> s1, TreeSet<String> s2,
			int comparison_type)
	{
		float result = 0;

		if (comparison_type == COMPARISON_JACCARD_DIS)
		{
			int[] sizes = Utilities.unionAndIntersectionSize(s1, s2);
			result = 2 * Float.valueOf(sizes[1]) / Float.valueOf(sizes[2]);
		}
		else
		{
			int times = 0;
			Iterator<String> i1 = s1.iterator();
			Iterator<String> i2 = s2.iterator();
			while (i1.hasNext())
			{
				String ss1 = i1.next();
				while (i2.hasNext())
				{
					times++;
					String ss2 = i2.next();
					if (comparison_type == COMPARISON_EDIT_DIS)
						result += CalEditDistance.editDistance(ss1.toLowerCase(),
								ss2.toLowerCase());
					else
						result += CalSemanticSimmilarity.run2(ss1, ss2);
				}
			}
			result = result / times;
		}
		return result;
	}

	private float comparison_date(String source_property_value,
			String target_property_value)
	{
		Date date1 = new Date();
		Date date2 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss hh:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-mm-dd");
		try
		{
			date1 = sdf.parse(source_property_value);
		}
		catch (ParseException pe)
		{
			try
			{
				date1 = sdf2.parse(source_property_value);
			}
			catch (ParseException e)
			{
			}
		}
		try
		{
			date2 = sdf.parse(target_property_value);
		}
		catch (ParseException pe)
		{
			try
			{
				date2 = sdf2.parse(target_property_value);
			}
			catch (ParseException e)
			{
			}
		}
		if (date1.compareTo(date2) == 0)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
