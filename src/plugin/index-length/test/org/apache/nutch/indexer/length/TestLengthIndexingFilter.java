package org.apache.nutch.indexer.length;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlink;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.util.NutchConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class TestLengthIndexingFilter {

	@Test
	public void test() throws Exception {
		Configuration conf = NutchConfiguration.create();
		LengthIndexingFilter filter = new LengthIndexingFilter();
		filter.setConf(conf);
		Assert.assertNotNull(filter);
		NutchDocument doc = new NutchDocument();
		String exText = "Hola que tal";
		ParseImpl parse = new ParseImpl(exText, new ParseData());
		Inlinks inlinks = new Inlinks();
		inlinks.add(new Inlink("http://test1.com/", "text1"));
		inlinks.add(new Inlink("http://test2.com/", "text2"));
		inlinks.add(new Inlink("http://test3.com/", "text3"));
		try {
			filter.filter(doc, parse, new Text("http://nutch.apache.org/index.html"), new CrawlDatum(), inlinks);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(doc);
		Assert.assertTrue("Comprobación de que el campo esta indexado", doc.getFieldNames().contains("length"));
		Assert.assertEquals("Comprobar si se realiza bien la medicion", exText.length(),
				doc.getField("length").getValues().get(0));
	}
}
