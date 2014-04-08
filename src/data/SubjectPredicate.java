package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

import tools.Utilities;

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

public class SubjectPredicate implements Comparable<SubjectPredicate>
{
	String subject;
	String predicate;

	public SubjectPredicate(String subject, String predicate)
	{
		super();
		this.subject = subject;
		this.predicate = predicate;
	}

	public boolean equalTo(SubjectPredicate o)
	{
		if (this.compareTo(o) == 0)
			return true;
		return false;
	}

	public int compareTo(SubjectPredicate o)
	{
		if (this.subject.compareTo(o.subject) > 0)
			return 1;
		else if (this.subject.compareTo(o.subject) < 0)
			return -1;
		else if (this.predicate.compareTo(o.predicate) > 0)
			return 1;
		else if (this.predicate.compareTo(o.predicate) < 0)
			return -1;
		return 0;
	}

	public static TreeMap<SubjectPredicate, String> introduceTriples(String filename)
	{

		TreeMap<SubjectPredicate, String> triples = new TreeMap<SubjectPredicate, String>();
		File file1 = new File(filename);
		BufferedReader reader1 = null;

		try
		{
			reader1 = new BufferedReader(new FileReader(file1));
			String temp1 = null;
			while ((temp1 = reader1.readLine()) != null)
			{
				String[] triple = temp1.split(" ");
				if (triple.length < 3)
					continue;
				String s = triple[0];
				String p = triple[1];
				String oo = triple[2];
				String object = "";
				if (triple.length == 3
						&& (oo.startsWith("http://") || oo.startsWith("https://")
								|| oo.startsWith("ftp://") || oo.startsWith("http:/")))
				{
					String[] ss = oo.split("/|#");
					String[] ww = ss[ss.length - 1]
							.split("([-_ .]|(?<=[^-_ A-Z])(?=[A-Z]))");
					for (int i = 0; i < ww.length; i++)
					{
						if (ww[i].length() != 0)
						{
							object = object + ww[i] + " ";
						}
					}
				}
				else
				{
					for (int i = 2; i < triple.length; i++)
					{
						object = object + triple[i] + " ";
					}
				}
				triples.put(new SubjectPredicate(s, p), object);
				// System.out.println(s+" "+p+" "+object);
			}
			reader1.close();
		}
		catch (IOException ie)
		{

		}

		return triples;

	}

	/**
	 * 如果这个主语和谓语的取值没有被查询过，则通过该函数进行查询
	 * 
	 * @param s
	 *            　主语
	 * @param p
	 *            　谓语
	 * @param dir
	 *            tdb文件所在目录
	 * @return 将取值整理为一个字符串，单词之间用空格连接
	 */
	public static String createOneTriple(String s, String p, String dir, String service)
	{
		String object = "";
		String prefix;
		String pc;
		if (p.contains("#"))
		{
			prefix = p.substring(0, p.lastIndexOf("#") + 1);
			pc = p.substring(p.lastIndexOf("#") + 1, p.length());
		}
		else
		{
			prefix = p.substring(0, p.lastIndexOf("/") + 1);
			pc = p.substring(p.lastIndexOf("/") + 1, p.length());
		}
		s = s.replace("\"", "%22");
		s = s.replace("|", "%7C");
		s = s.replace("`", "%60");
		String sparqlQueryString = "PREFIX pre: <" + prefix
				+ ">\nselect distinct ?o where { <" + s + "> pre:" + pc + " ?o.}";
		ARQ.getContext().setTrue(ARQ.useSAX);
		// System.out.println(sparqlQueryString);
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = null;
		if (service.equals(""))
		{
			Dataset dataset = TDBFactory.createDataset(dir);
			Model model = dataset.getDefaultModel();
			qexec = QueryExecutionFactory.create(sparqlQueryString, model);
		}
		else
		{
			qexec = QueryExecutionFactory.sparqlService(service, query);
		}
		ResultSet results = qexec.execSelect();

		while (results.hasNext())
		{
			QuerySolution soln = results.nextSolution();
			RDFNode rdfn = soln.get("?o");
			if (Utilities.isURI(rdfn.toString()))
			{
				String[] ss = rdfn.toString().split("/|#");
				String[] ww = ss[ss.length - 1].split("([-_ ]|(?<=[^-_ A-Z])(?=[A-Z]))");
				for (int i = 0; i < ww.length; i++)
				{
					if (ww[i].length() != 0)
					{
						object = object + ww[i] + " ";
					}
				}
			}
			else
			{
				String value = rdfn.asLiteral().getLexicalForm();
				if (Utilities.isDate(rdfn.toString())
						|| Utilities.isDouble(rdfn.toString())
						|| Utilities.isInteger(rdfn.toString()))
				{
					object = object + value + " ";
				}
				else
				{
					String temp = rdfn.toString().replace("/n", " ");
					String[] ss = temp.split(" ");
					for (int i = 0; i < ss.length; i++)
					{
						if (ss[i].length() != 0)
						{
							object = object + ss[i];
							object = object + " ";
						}
					}

				}
			}
		}
		return object;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
