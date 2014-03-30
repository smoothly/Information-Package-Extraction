package data;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public class GlobalData
{
	static public Dataset dsScheme = null;// TDBFactory.createDataset(directory)
	static public Model scheme = null;
	
	static public String ci = "";
	static public int sourceScale = 0;
	// string:property's uri, integer:property's type
	static public TreeMap<String, Integer> pScheme = new TreeMap<String, Integer>();
	static public TreeMap<String, Integer> pSource = new TreeMap<String, Integer>();

	static public int schemePropertyNumber = 0;
	static public int sourcePropertyNumber = 0;

	static public TreeMap<String, TreeSet<String>> value_scheme_string = new TreeMap<String, TreeSet<String>>();
	static public TreeMap<String, TreeSet<String>> value_scheme_uri = new TreeMap<String, TreeSet<String>>();
	static public TreeMap<String, TreeSet<Double>> value_scheme_double = new TreeMap<String, TreeSet<Double>>();
	static public TreeMap<String, TreeSet<String>> value_scheme_date = new TreeMap<String, TreeSet<String>>();
	static public TreeMap<String, TreeSet<Integer>> value_scheme_int = new TreeMap<String, TreeSet<Integer>>();

	static public TreeMap<String, TreeSet<String>> value_source_string = new TreeMap<String, TreeSet<String>>();
	static public TreeMap<String, TreeSet<String>> value_source_uri = new TreeMap<String, TreeSet<String>>();
	static public TreeMap<String, TreeSet<Double>> value_source_double = new TreeMap<String, TreeSet<Double>>();
	static public TreeMap<String, TreeSet<String>> value_source_date = new TreeMap<String, TreeSet<String>>();
	static public TreeMap<String, TreeSet<Integer>> value_source_int = new TreeMap<String, TreeSet<Integer>>();

	static public final int P_TYPE_URI = 0;
	static public final int P_TYPE_string = 1;
	static public final int P_TYPE_decimal = 2;
	static public final int P_TYPE_date = 3;
	static public final int P_TYPE_int = 4;

	//属性相似性矩阵
	static public PropertySimilarity[] pss = null;

	static public final int SELECTED = 2;
	static public final int FREE = 1;
	static public final int DESELECTED = 0;

	//属性标签
	static public TreeMap<String, Integer> labels = new TreeMap<String, Integer>();

	//结果集的个体集合
	static public TreeSet<String> individualChosen = new TreeSet<String>();
	//结果集的三元组集合
	static public ArrayList<Statement> informationPackage = new ArrayList<Statement>();
}
