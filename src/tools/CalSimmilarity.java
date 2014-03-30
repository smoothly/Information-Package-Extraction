package tools;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
/**
 * 返回两个词的语义相似度
 * rcs：选择相似度计算方法
 * run()：根据相似度计算方法，输出对应的值
 * run2()：只计算一种相似度，rcs[0]，返回该相似度
 * @author Emma Liu
 *
 */
public class CalSimmilarity
{
	private static ILexicalDatabase db = new NictWordNet();
	private static RelatednessCalculator[] rcs = {new Lin(db)};

	// new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db), new
	// WuPalmer(db),
	// new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)

	private static void run(String word1, String word2)
	{
		WS4JConfiguration.getInstance().setMFS(true);
		for (RelatednessCalculator rc : rcs)
		{
			double s = rc.calcRelatednessOfWords(word1, word2);
			System.out.println(rc.getClass().getName() + "\t" + s);
		}
	}

	public static float run2(String word1, String word2)
	{
		WS4JConfiguration.getInstance().setMFS(true);
		RelatednessCalculator rc = rcs[0];
		double s = rc.calcRelatednessOfWords(word1, word2);
		return (float) s;
	}
	
	/**
	 * unit test
	 * @param args
	 */
	public static void main(String[] args)
	{
		long t0 = System.currentTimeMillis();
		run("low", "low");
		long t1 = System.currentTimeMillis();
		System.out.println("Done in " + (t1 - t0) + " msec.");
	}
}
