package org.unizar.nutch.indexer.ogc;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unizar.nutch.parse.ogc.OgcParseFilter;
import org.unizar.nutch.test.utils.Utils;

public class OgcIndexingFilterTest {

	private NutchDocument nutchDocument;
	private Parse parse;
	private Text urlText;
	private CrawlDatum datum;
	private Inlinks inlinks;
	private String url;

	@Before
	public void setup() {
		String text = "Hola que tal";
		nutchDocument = new NutchDocument();
		parse = new ParseImpl(text, new ParseData());
		url = "http://wms.magrama.es/sig/Agricultura/TurcSecano/wms.aspx?request=GetCapabilities&service=WMS";
		urlText = new Text(url);
		datum = new CrawlDatum();
		inlinks = new Inlinks();

	}

	@Test
	public void testOgcIndexingFilter() throws FileNotFoundException, URISyntaxException, IndexingException {
		File f = new File(getClass().getResource("testWMS.xml").toURI());
		@SuppressWarnings("resource")
		String contentValue = new Scanner(f).useDelimiter("\\Z").next();
		ParseResult testParseResult = Utils.createParseResultWithMetadata(new Metadata(), url);
		Content testContent = Utils.createContent(url, contentValue);

		OgcIndexingFilter indexingFilter = new OgcIndexingFilter();
		OgcParseFilter parseFilter = new OgcParseFilter();

		ParseResult res = parseFilter.filter(testContent, testParseResult, null, null);
		parse = res.get(url);

		NutchDocument doc = indexingFilter.filter(nutchDocument, parse, urlText, datum, inlinks);

		Assert.assertTrue("Comprobación de que el campo ogc_version esta indexado",
				doc.getFieldNames().contains("ogc_version"));
		Assert.assertTrue("Comprobación de que el campo ogc_service esta indexado",
				doc.getFieldNames().contains("ogc_service"));
		Assert.assertTrue("Comprobación de que el campo raw_content esta indexado",
				doc.getFieldNames().contains("raw_content"));
	}
}
