package data;

import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DataImporter
{
	static public void importData(String schemeDir)
	{
		System.out.println("Start to load data...");
		GlobalData.scheme = ModelFactory.createDefaultModel();
		GlobalData.scheme.read(schemeDir);
		System.out.println("Data loaded");
	}
}
