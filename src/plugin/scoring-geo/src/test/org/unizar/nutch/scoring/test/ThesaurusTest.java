package org.unizar.nutch.scoring.test;

import org.apache.nutch.indexer.IndexingException;
import org.junit.Before;
import org.junit.Test;
import org.unizar.nutch.scoring.geo.thesaurus.Thesaurus;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class ThesaurusTest {

	private Thesaurus th;

	@Before
	public void setup() {
		th = new Thesaurus();
	}

	@Test
	public void testOgcIndexingFilter() throws FileNotFoundException, URISyntaxException, IndexingException {
		int results = th.execQuery("agua");
		assertEquals(results, 1);
		results = th.execQuery("вода");
		assertEquals(results, 1);
		results = th.execQuery("Mar");
		assertEquals(results, 30);

	}

}
