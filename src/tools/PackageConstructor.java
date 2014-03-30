package tools;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;

import data.GlobalData;

public class PackageConstructor
{
	static private FileOutputStream fos = null;
	static private OutputStreamWriter osw = null;
	static private BufferedWriter bw = null;

	static public void packageBuilder(int maxDepth, String CI) throws IOException
	{
		fos = new FileOutputStream("information-package");
		osw = new OutputStreamWriter(fos);
		bw = new BufferedWriter(osw);
		GlobalData.individualChosen.add(CI);

		TreeSet<String> ts = statementsFounder(maxDepth);
		while (ts.size() != 0)
		{
			GlobalData.individualChosen.addAll(ts);
			ts = statementsFounder(maxDepth);
		}
		Iterator<Statement> its = GlobalData.informationPackage.iterator();
		while (its.hasNext())
		{
			bw.write(its.next().toString());
			bw.newLine();
		}
		bw.close();
	}

	static public TreeSet<String> statementsFounder(int maxDepth)
	{
		TreeSet<String> result = new TreeSet<String>();

		Iterator<String> it = GlobalData.individualChosen.iterator();
		// 对每个individualChosen集合中的个体进行深度为maxDepth的遍历
		while (it.hasNext())
		{
			String cur = it.next();
			String vars = "?p1 ?o1";
			String ques = "<" + cur + ">" + " ?p1 ?o1.\n";
			for (int i = 2; i <= maxDepth; i++)
			{
				vars = vars + " ?p" + i + " ?o" + i;
				ques = ques + " ?o" + (i - 1) + " ?p" + i + " ?o" + i + " .\n";
			}
			String sparql = "select " + vars + " where {\n" + ques + "}";
			System.out.println(sparql);
			Query q = QueryFactory.create(sparql);
			QueryExecution qe = QueryExecutionFactory.create(q, GlobalData.target);
			ResultSet results = qe.execSelect();

			

			for (; results.hasNext();)
			{
				// 对每个查询结果进行扫描，查看其是否包含selected属性
				ArrayList<Statement> st = new ArrayList<Statement>();
				TreeSet<String> tempResult = new TreeSet<String>();
				boolean ifSelected = false;
				QuerySolution curQS = results.next();
				System.out.println(curQS);
				for (int j = 1; j <= maxDepth; j++)
				{
					RDFNode rn = curQS.get("?p" + j);
					int selectedLevel = GlobalData.labels.get(rn.asResource().getURI());
					if (selectedLevel == 2)
					{
						ifSelected = true;
					}
					else if(selectedLevel == 0)
					{
						ifSelected = false;
						break;
					}
					if (j == 1)
					{
						st.add(new StatementImpl(GlobalData.target.getResource(cur),
								GlobalData.target.getProperty(rn.asResource().getURI()),
								GlobalData.target
										.getRDFNode(curQS.get("?o" + j).asNode())));
					}
					else
					{
						st.add(new StatementImpl(curQS.get("?o" + (j - 1)).asResource(),
								GlobalData.target.getProperty(rn.asResource().getURI()),
								GlobalData.target
										.getRDFNode(curQS.get("?o" + j).asNode())));

					}
					if (curQS.get("?o" + j).isURIResource())
					{
						tempResult.add(curQS.get("?o" + j).asResource().getURI());
					}
				}
				// 如果这条结果包含selected属性，则将这条结果中的statement均加入到informationPackage里面
				if (ifSelected)
				{
					GlobalData.informationPackage.addAll(st);
					result.addAll(tempResult);
				}
			}
		}
		// 对所有的结果扫描完之后，查看是否有新增的individualChosen元素，若有则返回，若没有返回空集
		if (GlobalData.individualChosen.containsAll(result))
		{
			result.clear();
		}
		return result;
	}
	/**
	 * Unit Test
	 * 
	 * @throws IOException
	 */
	// public static void main(String[] args) throws IOException
	// {
	// packageBuilder(1,
	// "http://oaei.ontologymatching.org/2010/IIMBDATA/en/item438746705886466210");
	// // packageBuilder(2,
	// //
	// "http://oaei.ontologymatching.org/2010/IIMBDATA/en/item438746705886466210");
	// // packageBuilder(3,
	// //
	// "http://oaei.ontologymatching.org/2010/IIMBDATA/en/item438746705886466210");
	// }
}
