package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class PropertyConvert
{
	public static void convert(String[] filename, String targetfile, float threshold)
			throws IOException
	{
		FileOutputStream fos = new FileOutputStream(targetfile);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter bw = new BufferedWriter(osw);

		File[] file = new File[filename.length];
		BufferedReader[] reader = new BufferedReader[filename.length];
		for (int i = 0; i < filename.length; i++)
		{
			file[i] = new File(filename[i]);
			reader[i] = new BufferedReader(new FileReader(file[i]));
			try
			{
				String temp = null;
				while ((temp = reader[i].readLine()) != null)
				{
					String[] content = temp.split(" ");
					String psource = content[0];
					if (Float.valueOf(content[5]) >= threshold)
					{
						bw.write(psource + " " + content[1] + " " + i + " " + content[5]);
						bw.newLine();
					}
				}
			}
			finally
			{
				reader[i].close();
			}
		}
		bw.close();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		String[] filenames = {"data//compare0.data", "data//compare1.data",
				"data//compare2.data", "data//compare3.data", "data//compare4.data"};
		// TODO Auto-generated method stub
		convert(filenames, "data//property-pair.data", 0.1f);

	}
}
