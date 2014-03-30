package runner;

import java.io.IOException;

import org.apache.xerces.util.URI.MalformedURIException;

import steps.PropertyAlignment;
import steps.PropertySelection;
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
		try
		{
			PropertySelection.getPropertiesViaNet("http://dbpedia.org/resource/Black_Humor_(film)",2000);
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
		try
		{
			PropertyAlignment.propertyAlignment();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
//		try
//		{
//			PropertyLabel.labelProperty();
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try
//		{
//			PackageConstructor.packageBuilder(2,
//					 "http://oaei.ontologymatching.org/2010/IIMBDATA/m/item4884402347953804557");
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String s= "1988";
//		System.out.println(Utilities.isInteger(s));
//		System.out.println(Utilities.isDouble(s));
//		System.out.println(Utilities.isDate(s));
//		System.out.println(Utilities.isURI(s));
	}

}
