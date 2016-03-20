package org.apache.nutch.indexer.length;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.indexer.geoip.GeoIPIndexingFilter;
import org.apache.nutch.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LengthIndexingFilter implements IndexingFilter {

	private static final Logger LOG = LoggerFactory.getLogger(GeoIPIndexingFilter.class);

	private Configuration conf;

	/**
	 * @see org.apache.hadoop.conf.Configurable#setConf(org.apache.hadoop.conf.Configuration)
	 */
	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;

	}

	/**
	 * @see org.apache.hadoop.conf.Configurable#getConf()
	 */
	@Override
	public Configuration getConf() {
		return this.conf;
	}

	@Override
	public NutchDocument filter(NutchDocument doc, Parse parse, Text url, CrawlDatum datum, Inlinks inlinks)
			throws IndexingException {
		String text = parse.getText();
		doc.add("length", text.length());		
		return doc;
	}

}
