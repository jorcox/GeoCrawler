package org.unizar.nutch.indexer.ogc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.indexer.length.LengthIndexingFilter;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.NutchConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unizar.nutch.parse.ogc.OgcParseFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class OgcIndexingFilterTest {
	
    private IndexingFilter filter;
    private String text;
    private NutchDocument nutchDocument;
    private Parse parse;
    private Text urlText;
    private CrawlDatum datum;
    private Inlinks inlinks;
    private String url;
	
	
    @Before
    public void setup() {
        filter = new LengthIndexingFilter();
        filter.setConf(NutchConfiguration.create());
        text = "Hola que tal";
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
		String contentValue = new Scanner(f).useDelimiter("\\Z").next();		
		ParseResult testParseResult = createParseResultWithMetadata(new Metadata(), url);
		Content testContent = createContent(url, contentValue);

		OgcIndexingFilter indexingFilter = new OgcIndexingFilter();
		OgcParseFilter parseFilter = new OgcParseFilter();

		
		ParseResult res = parseFilter.filter(testContent, testParseResult, null, null);
		parse = res.get(url);
		
		NutchDocument doc = indexingFilter.filter(nutchDocument, parse, urlText, datum, inlinks);

		Assert.assertTrue("Comprobación de que el campo ogc_version esta indexado", doc.getFieldNames().contains("ogc_version"));
		Assert.assertTrue("Comprobación de que el campo ogc_service esta indexado", doc.getFieldNames().contains("ogc_service"));
		Assert.assertTrue("Comprobación de que el campo raw_content esta indexado", doc.getFieldNames().contains("raw_content"));
	}
	
	private ParseResult createParseResultWithMetadata(Metadata metadata, String url) {
		ParseData parseData = new ParseData();

		if (metadata != null) {
			parseData.setParseMeta(metadata);
		}

		Parse parse = new ParseImpl("Texto extraido", parseData);
		ParseResult testParseResult = ParseResult.createParseResult(url, parse);
		return testParseResult;
	}

	private Content createContent(final String url, final String content) {
		byte[] contentByteArray = {};
		if (content != null) {
			contentByteArray = content.getBytes();
		}
		Content cont = new Content(url, "", contentByteArray, null, new Metadata(), new Configuration());
		return cont;
	}
}
