package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.TreeSet;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.tdb.TDBFactory;

import data.GlobalData;

public class CalValueSimilarity
{
	static public void calConf(String filename1, String filename2, String targetfile)
			throws IOException
	{
		System.out.println(filename1 + " " + filename2);
		FileOutputStream fos = new FileOutputStream(targetfile);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter bw = new BufferedWriter(osw);

		File file1 = new File(filename1);
		BufferedReader reader1 = null;

		File file2 = new File(filename2);
		BufferedReader reader2 = null;

		String p1 = "";
		String p2 = "";
		TreeSet<String> value1 = new TreeSet<String>();
		TreeSet<String> value2 = new TreeSet<String>();

		try
		{
			reader1 = new BufferedReader(new FileReader(file1));
			String temp1 = null;
			while ((temp1 = reader1.readLine()) != null)
			{
				if (temp1.length() == 0)
				{
					continue;
				}
				int index = temp1.indexOf(" ");
				p1 = temp1.substring(0, index);
				value1 = dataConverter(temp1.substring(index));
				reader2 = new BufferedReader(new FileReader(file2));
				String temp2 = null;
				while ((temp2 = reader2.readLine()) != null)
				{
					if (temp2.length() == 0)
					{
						continue;
					}
					System.out.println(p2);
					index = temp2.indexOf(" ");

					p2 = temp2.substring(0, index);
					value2 = dataConverter(temp2.substring(index));
					float conf = 0;
					int[] size = Utilities.unionAndIntersectionSize(value1, value2);
					conf = 2 * (Float.valueOf(size[1])) / Float.valueOf(size[0]);

					String[] ss = p1.split("/|#");
					String[] ts = p2.split("/|#");
					String[] w1 = ss[ss.length - 1]
							.split("([-_ ]|(?<=[^-_ A-Z])(?=[A-Z]))");
					String[] w2 = ts[ts.length - 1]
							.split("([-_ ]|(?<=[^-_ A-Z])(?=[A-Z]))");

					float editDis = 0;
					float semanticSim = 0;
					int times = 0;
					for (int i = 0; i < w1.length; i++)
					{
						for (int j = 0; j < w2.length; j++)
						{
							semanticSim += CalSemanticSimmilarity.run2(w1[i], w2[j]);
							editDis += CalEditDistance.editDistance(w1[i], w2[j]);
							times++;
						}
					}
					editDis = editDis / Float.valueOf(times);
					semanticSim = semanticSim / Float.valueOf(times);
					float sim = editDis*0.33f+semanticSim*0.33f+conf*0.34f;
					bw.write(p1 + " " + p2 + " " + conf + " " + editDis + " "
							+ semanticSim + " "+sim);
					bw.newLine();
				}
			}
		}
		finally
		{
			reader1.close();
			reader2.close();
			bw.close();
		}
	}

	static private TreeSet<String> dataConverter(String record)
	{
		TreeSet<String> result = new TreeSet<String>();
		int index = record.indexOf("[");
		String values = record.substring(index, record.length() - 1);

		String[] vs = values.split(", ");
		for (int i = 0; i < vs.length; i++)
		{
			// 去掉头尾的符号
			int begin = 0;
			int end = vs[i].length() - 1;
			boolean rec = false;
			while (vs[i].length() > begin
					&& (vs[i].charAt(begin) < '1' || vs[i].charAt(begin) > '9')
					&& (vs[i].charAt(begin) < 'A' || vs[i].charAt(begin) > 'Z')
					&& (vs[i].charAt(begin) < 'a' || vs[i].charAt(begin) > 'z'))
			{
				begin++;
			}
			while (end >= begin && (vs[i].charAt(end) < '1' || vs[i].charAt(end) > '9')
					&& (vs[i].charAt(end) < 'A' || vs[i].charAt(end) > 'Z')
					&& (vs[i].charAt(end) < 'a' || vs[i].charAt(end) > 'z'))
			{
				end--;
			}
			if (end >= begin)
			{
				rec = true;
			}
			if (rec)
			{
				String r = vs[i].substring(begin, end + 1);
				result.add(r);
			}
		}
		return result;
	}

	/**
	 * 统计属性的取值(dbpedia)
	 * 
	 * @throws IOException
	 */
	static public void valuesOfPropertyViaNet(String service, String dir,
			String filename, String propertyFile) throws IOException
	{
		// 读属性
		File file = new File(propertyFile);
		BufferedReader reader = null;
		int line = 0;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = reader.readLine()) != null)
			{
				GlobalData.pSource.put(tempString, 0);
				// if (line++ > 100)
				// {
				// break;
				// }
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e1)
				{
				}
			}
		}

		FileOutputStream[] fos = {new FileOutputStream(filename + "0" + ".data"),
				new FileOutputStream(filename + "1" + ".data"),
				new FileOutputStream(filename + "2" + ".data"),
				new FileOutputStream(filename + "3" + ".data"),
				new FileOutputStream(filename + "4" + ".data")};
		OutputStreamWriter[] osw = {new OutputStreamWriter(fos[0]),
				new OutputStreamWriter(fos[1]), new OutputStreamWriter(fos[2]),
				new OutputStreamWriter(fos[3]), new OutputStreamWriter(fos[4])};
		BufferedWriter[] bw = {new BufferedWriter(osw[0]), new BufferedWriter(osw[1]),
				new BufferedWriter(osw[2]), new BufferedWriter(osw[3]),
				new BufferedWriter(osw[4])};

		System.out.println("start to get values of properties in " + service + dir);
		// source property
		Iterator<String> it = GlobalData.pSource.keySet().iterator();
		int iterNum = 0;
		int exceptionNum = 0;

		// while (alreadyDone != 0 && it.hasNext())
		// {
		// it.next();
		// iterNum++;
		// if (alreadyDone == iterNum)
		// {
		// break;
		// }
		// }
		while (it.hasNext())
		{
			int typeNum[] = new int[5];
			for (int i = 0; i < 5; i++)
			{
				typeNum[i] = 0;
			}
			String cur = it.next();
			System.out.println((iterNum++) + " " + cur);
			TreeSet<String> values = new TreeSet<String>();
			// int type = GlobalData.pSource.get(cur);
			String sparqlQueryString = "select distinct ?o where {?s <" + cur + "> ?o .}";
			Query query = QueryFactory.create(sparqlQueryString);
			ARQ.getContext().setTrue(ARQ.useSAX);
			QueryExecution qexec = null;

			String content = " " + cur;
			if (service.length() > 0)
			{
				qexec = QueryExecutionFactory.sparqlService(service, query);
			}
			else
			{
				Dataset dataset = TDBFactory.createDataset(dir);
				Model model = dataset.getDefaultModel();
				qexec = QueryExecutionFactory.create(sparqlQueryString, model);
			}
			try
			{
				ResultSet results = qexec.execSelect();
				if (!results.hasNext())
				{
					continue;
				}
				for (; results.hasNext();)
				{
					QuerySolution soln = results.nextSolution();
					RDFNode rdfn = soln.get("?o");

					// 如果取值是URI，则将前缀去掉，进行tokenize，再存入values中。
					if (Utilities.isURI(rdfn.toString()))
					{
						typeNum[GlobalData.P_TYPE_URI]++;
						String[] ss = rdfn.toString().split("/|#");
						String[] ww = ss[ss.length - 1]
								.split("([-_ ]|(?<=[^-_ A-Z])(?=[A-Z]))");
						for (int i = 0; i < ww.length; i++)
						{
							if (ww[i].length() != 0)
							{
								values.add(ww[i]);
							}
						}
					}
					else
					{
						try
						{
							String value = rdfn.asLiteral().getLexicalForm();
							if (Utilities.isInteger(value))
							{
								typeNum[GlobalData.P_TYPE_int]++;
								values.add(value);
							}
							else if (Utilities.isDouble(value))
							{
								typeNum[GlobalData.P_TYPE_decimal]++;
								values.add(value);
							}
							else if (Utilities.isDate(value))
							{
								typeNum[GlobalData.P_TYPE_date]++;
								values.add(value);
							}
							else
							{
								String temp = rdfn.toString().replace("/n", " ");
								String[] ss = temp.split(" ");
								typeNum[GlobalData.P_TYPE_string]++;
								for (int i = 0; i < ss.length; i++)
								{
									if (ss[i].length() != 0)
									{
										values.add(ss[i]);
									}
								}
							}
						}
						catch (DatatypeFormatException dfe)
						{
							exceptionNum++;
						}
					}
				}
			}
			finally
			{
				qexec.close();
			}

			int type = Utilities.getMaxIndex(typeNum);
			content = cur + " " + values.toString();

			bw[type].write(content);
			bw[type].newLine();
		}
		System.out.println("properties in " + service + dir + " values are stored");
		System.out.println(exceptionNum + " exceptions occured.");
		for (int i = 0; i < 5; i++)
		{
			bw[i].close();
		}
	}

	public static void main(String[] args) throws IOException
	{
		// http://www.linkedmdb.org/sparql
		// valuesOfPropertyViaNet("",
		// "F://本体库数据及软件//linkedmdb-latest-dump//linkedmdb-tdb",
		// "data//linkedmdb-property-value", "data//linkedmdb-property.data");
		// valuesOfPropertyViaNet("http://dbpedia.org/sparql/", "",
		// "data//dbpedia-property-value-movie", "data//property-movie.data");
		calConf("data//linkedmdb-property-value0.data",
				"data//dbpedia-property-value-movie0.data", "data//compare0.data");
		calConf("data//linkedmdb-property-value1.data",
				"data//dbpedia-property-value-movie1.data", "data//compare1.data");
		calConf("data//linkedmdb-property-value2.data",
				"data//dbpedia-property-value-movie2.data", "data//compare2.data");
		calConf("data//linkedmdb-property-value3.data",
				"data//dbpedia-property-value-movie3.data", "data//compare3.data");
		calConf("data//linkedmdb-property-value4.data",
				"data//dbpedia-property-value-movie4.data", "data//compare4.data");
	}
}
