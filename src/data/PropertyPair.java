package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * 源数据中的属性 + 属性类型 + 候选匹配属性列表
 * 
 * @author Emma Liu
 * 
 */
public class PropertyPair
{
	static public ArrayList<PropertyPair> property_pair = new ArrayList<PropertyPair>();
	// property type
	static public TreeMap<String, Integer> source_property_type = new TreeMap<String, Integer>();
	static public TreeMap<String, Integer> target_property_type = new TreeMap<String, Integer>();
	// source property sumconf
	static public TreeMap<String, Float> source_property_sumconf = new TreeMap<String, Float>();
	static public TreeMap<String, Float> target_property_sumconf = new TreeMap<String, Float>();

	static public void calSumConf()
	{
		Iterator<PropertyPair> it = property_pair.iterator();
		while (it.hasNext())
		{
			PropertyPair cur = it.next();
			String s = cur.p_from_source;
			float f = cur.sim;
			String t = cur.p_from_target;

			if (source_property_sumconf.containsKey(s))
			{
				f += source_property_sumconf.get(s);
				source_property_sumconf.put(s, f);
			}
			else
			{
				source_property_sumconf.put(s, f);
			}
			f = cur.sim;
			if (target_property_sumconf.containsKey(t))
			{
				f += target_property_sumconf.get(t);
				target_property_sumconf.put(t, f);
			}
			else
			{
				target_property_sumconf.put(t, f);
			}
		}
	}

	static public void init(String filename) throws IOException
	{
		File file = new File(filename);
		BufferedReader reader = null;

		reader = new BufferedReader(new FileReader(file));
		String temp = null;
		while ((temp = reader.readLine()) != null)
		{
			String[] content = temp.split(" ");
			String ps = content[0];
			int type = Integer.valueOf(content[2]);
			float sim = Float.valueOf(content[3]);

			source_property_type.put(ps, type);
			target_property_type.put(content[1], type);
			property_pair.add(new PropertyPair(ps, type, content[1], sim));

		}
		System.out.println(property_pair.size()
				+ " property pairs have been established.");
	}

	String p_from_source = "";
	int type;
	float sim = 0;
	String p_from_target = "";

	public PropertyPair(String ps, int t, String pt, float s)
	{
		p_from_source = ps;
		type = t;
		sim = s;
		p_from_target = pt;
	}
}
