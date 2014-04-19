package data;

import java.util.HashMap;

public class TfIdf implements Comparable<TfIdf>
{
	public String word;
	// hashmap<文件名(subject), 词频>
	public HashMap<String, Float> fileList = new HashMap<String, Float>();

	public TfIdf(String w)
	{
		word = w;
	}

	/**
	 * 获取tfidf值
	 * 
	 * @param s
	 *            主语
	 * @param file_number
	 *            文件总数
	 * @return
	 */
	public float getValue(String sp, int file_number)
	{
		float tf = fileList.get(sp);
		float idf = (float) Math.log((double) (Float.valueOf(file_number) / Float
				.valueOf(fileList.size())));
		return tf * idf;
	}

	// public void update(String s, float frequency)
	// {
	// fileList.put(s, frequency);
	// }

	public int compareTo(TfIdf o)
	{
		if (word.equals(o.word))
			return 0;
		if (word.compareTo(o.word) > 0)
			return 1;
		else
			return -1;
	}
}
