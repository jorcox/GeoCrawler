package org.apache.nutch.indexer.length;

import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.util.NutchConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestLengthIndexingFilter {

    private IndexingFilter filter;
    private String text;
    private NutchDocument nutchDocument;
    private Parse parse;
    private Text urlText;
    private CrawlDatum datum;
    private Inlinks inlinks;

    @Before
    public void setup() {
        filter = new LengthIndexingFilter();
        filter.setConf(NutchConfiguration.create());
        text = "Hola que tal";
        nutchDocument = new NutchDocument();
        parse = new ParseImpl(text, new ParseData());
        urlText = new Text("http://nutch.apache.org/index.html");
        datum = new CrawlDatum();
        inlinks = new Inlinks();
    }

	@Test
	public void test() throws Exception {
        filter.filter(nutchDocument, parse, urlText, datum,  inlinks);

		Assert.assertTrue("Comprobación de que el campo esta indexado", nutchDocument.getFieldNames().contains("length"));
		Assert.assertEquals("Comprobar si se realiza bien la medicion", text.length(),
                nutchDocument.getField("length").getValues().get(0));
	}
}
