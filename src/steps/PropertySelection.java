package steps;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.xerces.util.URI.MalformedURIException;

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

/**
 * 在dbpedia中获取与ci相关的属性
 * 
 * @author Emma Liu
 * 
 */

public class PropertySelection
{
	static private FileOutputStream fos = null;
	static private OutputStreamWriter osw = null;
	static private BufferedWriter bw = null;

	/**
	 * 通过SPARQL终端获取dbpedia数据
	 * 
	 * @param ci
	 *            中心节点
	 * @param maxNodes
	 *            最大节点规模
	 * @throws IOException
	 */
	static public void getPropertiesViaNet(String ci, int maxNodes) throws IOException
	{
		fos = new FileOutputStream("selection-result");
		osw = new OutputStreamWriter(fos);
		bw = new BufferedWriter(osw);

		System.out.println("start to search properties in dbpedia...");
		Queue<String> ns = new LinkedBlockingDeque<String>();
		TreeSet<String> os = new TreeSet<String>();

		int nodes = 0;
		ns.add(ci);
		os.add(ci);
		while (!ns.isEmpty())
		{

			bw.write("" + os.size());
			bw.write(" ");
			String cur = ns.poll();
			String sparqlQueryString = "select distinct ?p ?o where {<" + cur
					+ "> ?p ?o .}";
			Query query = QueryFactory.create(sparqlQueryString);
			ARQ.getContext().setTrue(ARQ.useSAX);
			String service = "http://dbpedia.org/sparql/";
			QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query);
			// System.out.println(sparqlQueryString);
			try
			{
				ResultSet results = qexec.execSelect();
				for (; results.hasNext();)
				{
					QuerySolution soln = results.nextSolution();
					String p = soln.get("?p").toString();
					if (p.equals("http://www.w3.org/2000/01/rdf-schema#subClassOf")
							|| p.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
							|| p.equals("http://www.w3.org/2000/01/rdf-schema#hasSubClass"))
					{
						continue;
					}
					RDFNode o = soln.get("?o");
					if (o.isURIResource())
					{
						GlobalData.pSource.put(p, GlobalData.P_TYPE_URI);
						GlobalData.value_source_uri.put(p, new TreeSet<String>());
						if (!os.contains(o.toString()))
						{
							os.add(o.toString());
							ns.add(o.toString());

							nodes++;
							if (nodes > maxNodes)
							{
								ns.clear();
							}
						}
					}
					else
					{
						String oo = o.asLiteral().getValue().toString();
						System.out.println(oo);
						if (Utilities.isDate(oo))
						{
							GlobalData.pSource.put(p, GlobalData.P_TYPE_date);
							GlobalData.value_source_date.put(p, new TreeSet<String>());
						}
						else if (Utilities.isDouble(oo))
						{
							GlobalData.pSource.put(p, GlobalData.P_TYPE_decimal);
							GlobalData.value_source_double.put(p, new TreeSet<Double>());
						}
						else if (Utilities.isInteger(oo))
						{
							GlobalData.pSource.put(p, GlobalData.P_TYPE_int);
							GlobalData.value_source_int.put(p, new TreeSet<Integer>());
						}
						else
						{
							GlobalData.pSource.put(p, GlobalData.P_TYPE_string);
							GlobalData.value_source_string.put(p, new TreeSet<String>());
						}
					}
				}

			}
			finally
			{
				qexec.close();
			}

			bw.write("" + GlobalData.pSource.size());
			bw.newLine();

		}

		bw.close();

		System.out.println("get " + GlobalData.pSource.size() + " properties in dbpedia");
		Iterator<String> it = GlobalData.pSource.keySet().iterator();
		while (it.hasNext())
		{
			String cur = it.next();
			System.out.println(cur + " type:" + GlobalData.pSource.get(cur));
		}
		getProperties(GlobalData.scheme);

	}

	static private void getProperties(Model m)
	{
		System.out.println("start to search properties in local data");
		try
		{
			QueryExecution qExec = QueryExecutionFactory.create(
					"SELECT distinct ?p {?s ?p ?o}", m);
			ResultSet rs = qExec.execSelect();
			try
			{
				// ResultSetFormatter.out(rs);
				while (rs.hasNext())
				{
					String uri = rs.next().getResource("?p").getURI();
					int type = ptype(m, m.getProperty(uri));
					GlobalData.pScheme.put(uri, type);
					if (type == GlobalData.P_TYPE_date)
					{
						GlobalData.value_scheme_date.put(uri, new TreeSet<String>());
					}
					else if (type == GlobalData.P_TYPE_decimal)
					{
						GlobalData.value_scheme_double.put(uri, new TreeSet<Double>());
					}
					else if (type == GlobalData.P_TYPE_int)
					{
						GlobalData.value_scheme_int.put(uri, new TreeSet<Integer>());
					}
					if (type == GlobalData.P_TYPE_URI)
					{
						GlobalData.value_scheme_uri.put(uri, new TreeSet<String>());
					}
					if (type == GlobalData.P_TYPE_string)
					{
						GlobalData.value_scheme_string.put(uri, new TreeSet<String>());
					}
				}
			}
			finally
			{
				qExec.close();
			}

		}
		finally
		{
		}
		System.out.println("get " + GlobalData.pScheme.size()
				+ " properties in local data");
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
		return typeNumber;
	}

	/**
	 * unit test
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			getPropertiesViaNet("http://dbpedia.org/resource/Black_Humor_(film)", 200);
			System.out.println(GlobalData.pSource.size());
			PropertyAlignment.valuesOfPropertyViaNet();

		}
		catch (MalformedURIException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
