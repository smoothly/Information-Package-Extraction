package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 源数据中的属性 + 属性类型 + 候选匹配属性列表
 * 
 * @author Emma Liu
 * 
 */
public class PropertyPair
{
	static public ArrayList<PropertyPair> property_pair = new ArrayList<PropertyPair>();

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

			property_pair.add(new PropertyPair(ps, type, content[1]));

		}
		System.out.println(property_pair.size()
				+ " property pairs have been established.");
	}

	String p_from_source = "";
	int type;
	String p_from_target = "";

	public PropertyPair(String ps, int t, String pt)
	{
		p_from_source = ps;
		type = t;
		p_from_target = pt;
	}
}
