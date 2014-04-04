package steps;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingDeque;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import data.GlobalData;

/**
 * 在dbpedia中获取与ci相关的属性
 * 
 * @author Emma Liu
 * 
 */

public class PropertySelection2
{
	static public FileOutputStream fos = null;
	static public OutputStreamWriter osw = null;
	static public BufferedWriter bw = null;

	static public FileOutputStream fos2 = null;
	static public OutputStreamWriter osw2 = null;
	static public BufferedWriter bw2 = null;

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
		boolean reachMaxNodes = false;
		GlobalData.pSource.clear();
		System.out.println("start to search properties in dbpedia...");
		// ns存待遍历的个体
		Queue<String> ns = new LinkedBlockingDeque<String>();
		// os存每个遍历过的个体
		TreeSet<String> os = new TreeSet<String>();
		// ps存每个遍历过的属性
		TreeSet<String> ps = new TreeSet<String>();

		ns.add(ci);
		os.add(ci);
		while (!ns.isEmpty())
		{
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

				System.out.println(Float.valueOf(os.size()) / Float.valueOf(maxNodes) + "%");

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
					if (ps.contains(p))
					{
						continue;
					}
					ps.add(p);
					bw2.write(p);
					bw2.newLine();
					if (o.isURIResource())
					{
						GlobalData.pSource.put(p, GlobalData.P_TYPE_URI);
						if (!os.contains(o.toString()) && !reachMaxNodes)
						{
							os.add(o.toString());
							ns.add(o.toString());
							if (os.size() > maxNodes)
							{
								reachMaxNodes = true;
							}
						}
					}
					else
					{
						GlobalData.pSource.put(p, GlobalData.P_TYPE_string);
						GlobalData.value_source_string.put(p, new TreeSet<String>());
					}
				}
				bw.write(GlobalData.pSource.size() + " ");
			}
			finally
			{
				qexec.close();
			}
		}

		bw.newLine();

		System.out.println("get " + GlobalData.pSource.size() + " properties in dbpedia");
		// Iterator<String> it = GlobalData.pSource.keySet().iterator();
		// while (it.hasNext())
		// {
		// String cur = it.next();
		// System.out.println(cur + " type:" + GlobalData.pSource.get(cur));
		// }
		// getProperties(GlobalData.scheme);

	}
}
