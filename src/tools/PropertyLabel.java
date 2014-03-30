package tools;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

import data.GlobalData;

public class PropertyLabel
{
	static private FileOutputStream fos = null;
	static private OutputStreamWriter osw = null;
	static private BufferedWriter bw = null;

	static public void labelProperty() throws IOException
	{
		fos = new FileOutputStream("label-result");
		osw = new OutputStreamWriter(fos);
		bw = new BufferedWriter(osw);

		for (int i = 0; i < GlobalData.pss.length; i++)
		{
			ArrayList<String> selected = GlobalData.pss[i].getMaxSimilarity();
			int maxSize = selected.size();
			/**
			 * label selected
			 */
			for (int j = 0; j < selected.size(); j++)
			{
				GlobalData.labels.put(selected.get(j), GlobalData.SELECTED);
			}
			/**
			 * label free
			 */
			for (int j = maxSize; j < GlobalData.pss[i].tps.length; j++)
			{
				if (GlobalData.pss[i].tps[j].getSimilarity() < 0.5)
				{
					break;
				}
				if (GlobalData.labels.containsKey(GlobalData.pss[i].tps[j].getP2()))
				{
					continue;
				}
				else
				{
					GlobalData.labels.put(GlobalData.pss[i].tps[j].getP2(), 1);
				}
			}
		}
		/**
		 * label deselected
		 */
		Iterator<String> it = GlobalData.pt.keySet().iterator();
		while (it.hasNext())
		{
			String cur = it.next();
			if (!GlobalData.labels.containsKey(cur))
			{
				GlobalData.labels.put(cur, GlobalData.DESELECTED);
			}
		}

		it = GlobalData.labels.keySet().iterator();
		while (it.hasNext())
		{
			String cur = it.next();
			bw.write(cur + " " + GlobalData.labels.get(cur));
			bw.newLine();
		}
		bw.close();
	}
}
