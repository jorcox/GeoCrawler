package org.unizar.nutch.indexer.ogc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extract fields from Parse metadata and add them to the final Nutch document
 * to be indexed in the database.
 * 
 * @author Jorge Cancer
 */

public class OgcIndexingFilter implements IndexingFilter {

	private static final Logger LOG = LoggerFactory.getLogger(OgcIndexingFilter.class);

	private Configuration conf;

	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public Configuration getConf() {
		return this.conf;
	}

	@Override
	public NutchDocument filter(NutchDocument doc, Parse parse, Text url, CrawlDatum datum, Inlinks inlinks)
			throws IndexingException {
		ParseData dataP = parse.getData();
		Metadata meta = dataP.getParseMeta();
		boolean index = false;
		
		for (String key : meta.names()) {
			if(key.equals("ogc_service"))
				index = true;
			String value = meta.get(key);
			LOG.info("Adding " + key + " to NutchDocument");
			doc.add(key, value);
		}
		/* Return the document if it is an ogc service, otherwise return null */
		return index ? doc : null;
	}

}
