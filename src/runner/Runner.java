package runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.xerces.util.URI.MalformedURIException;

import steps.PropertySelection;
import steps.PropertySelection2;
import data.DataImporter;

public class Runner
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String schemeDir = "file:///F:/本体库数据及软件/iimb-2011-20072011/IIMB/000/onto.owl";
		DataImporter.importData(schemeDir);

		File file = new File("dbpedia-books");
		BufferedReader reader = null;
		try
		{
			PropertySelection2.fos = new FileOutputStream("data//selection-result");
			PropertySelection2.osw = new OutputStreamWriter(PropertySelection2.fos);
			PropertySelection2.bw = new BufferedWriter(PropertySelection2.osw);
			
			PropertySelection2.fos2 = new FileOutputStream("data//property-selected");
			PropertySelection2.osw2 = new OutputStreamWriter(PropertySelection2.fos2);
			PropertySelection2.bw2 = new BufferedWriter(PropertySelection2.osw2);
			
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null)
			{
				PropertySelection2.getPropertiesViaNet(tempString, 200);
				
				// PropertySelection.getPropertiesViaNet(
				// "http://dbpedia.org/resource/Black_Humor_(film)", 2000);
				// 显示行号
				line++;
				if(line == 5)
				{
					break;
				}
			}
			PropertySelection2.bw.close();
			PropertySelection2.bw2.close();
			reader.close();

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
//		try
//		{
//			PropertyAlignment.propertyAlignment();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}

		// try
		// {
		// PropertyLabel.labelProperty();
		// }
		// catch (IOException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// try
		// {
		// PackageConstructor.packageBuilder(2,
		// "http://oaei.ontologymatching.org/2010/IIMBDATA/m/item4884402347953804557");
		// }
		// catch (IOException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// String s= "1988";
		// System.out.println(Utilities.isInteger(s));
		// System.out.println(Utilities.isDouble(s));
		// System.out.println(Utilities.isDate(s));
		// System.out.println(Utilities.isURI(s));
	}

}
