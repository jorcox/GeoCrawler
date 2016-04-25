package org.unizar.nutch.scoring.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.scoring.ScoringFilterException;
import org.junit.Before;
import org.junit.Test;
import org.unizar.nutch.scoring.geo.SharkScoringFilter;

public class SharkScoringTest {

	private SharkScoringFilter scoringFilter;
	private Parse parse;
	private Text urlText;
	private CrawlDatum datum;
	private Inlinks inlinks;
	private String url;
	private Content content;

	@Before
	public void setup() {
		String text = "Hola que tal";
		scoringFilter = new SharkScoringFilter();
		parse = new ParseImpl(text, new ParseData());
		url = "http://wms.magrama.es/sig/Agricultura/TurcSecano/wms.aspx?request=GetCapabilities&service=WMS";
		urlText = new Text(url);
		datum = new CrawlDatum();
		inlinks = new Inlinks();
		content = new Content();
	}

	@Test
	public void testInjectedScore() throws ScoringFilterException {
		float preScore = datum.getScore();
		scoringFilter.injectedScore(urlText, datum);
		assertEquals(preScore, datum.getScore(), 0.001f);
	}

	@Test
	public void testGeneratorSortValue() throws ScoringFilterException {
		float sortValue = scoringFilter.generatorSortValue(urlText, datum, 1.0f);
		assertEquals(0.0f, sortValue, 0.001f);
	}

	@Test
	public void testInitialScore() throws ScoringFilterException {
		scoringFilter.initialScore(urlText, datum);
		assertEquals(0.0f, datum.getScore(), 0.001f);
	}

	@Test
	public void testPassScoreBeforeParsing() throws ScoringFilterException {
		scoringFilter.passScoreBeforeParsing(urlText, datum, content);
		// TODO
		assertEquals(0.0f, datum.getScore(), 0.001f);
	}

	@Test
	public void testPassScoreAfterParsing() throws ScoringFilterException {
		scoringFilter.passScoreAfterParsing(urlText, content, parse);
		// TODO
		assertEquals(0.0f, datum.getScore(), 0.001f);
	}

	@Test
	public void testDistributeScoreToOutlinks() throws ScoringFilterException {
		ArrayList<Entry<Text, CrawlDatum>> targets = new ArrayList<>();
		targets.add(new AbstractMap.SimpleEntry<Text, CrawlDatum>(urlText, datum));
		ParseData parseData = new ParseData();
		CrawlDatum out = scoringFilter.distributeScoreToOutlinks(urlText, parseData, targets, datum, 1);
		assertNull(out);
	}

}
