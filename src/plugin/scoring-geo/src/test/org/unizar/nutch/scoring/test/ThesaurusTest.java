package org.unizar.nutch.scoring.test;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.apache.nutch.indexer.IndexingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unizar.nutch.scoring.geo.thesaurus.Thesaurus;

public class ThesaurusTest {

	private Thesaurus th;

	@Before
	public void setup() {
		th = new Thesaurus();
	}

	@Test
	public void testOgcIndexingFilter() throws FileNotFoundException, URISyntaxException, IndexingException {
		int results = th.execQuery("agua");
		Assert.assertEquals(results, 1);
		results = th.execQuery("вода");
		Assert.assertEquals(results, 1);
		results = th.execQuery("Mar");
		Assert.assertEquals(results, 30);

	}

}
