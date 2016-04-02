package org.unizar.nutch.parse.ogc;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.OutlinkExtractor;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseStatus;
import org.apache.nutch.parse.xml.XMLUtils;
import org.apache.nutch.protocol.Content;
import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;

/**
 * Parse the content from XML in order to check if it corresponds to an OGC
 * service. In addition, makes full raw content of XML (and HTML) documents
 * available.
 * 
 * @author Jorge Cancer
 */
public class OgcParseFilter implements HtmlParseFilter {

	public final static String RAW_CONTENT = "raw_content";

	public final static String OGC_SERVICE = "ogc_service";

	public final static String OGC_VERSION = "ogc_version";

	public static final Logger LOG = LoggerFactory.getLogger(OgcParseFilter.class);

	private Configuration conf;

	private Namespace nsc;

	@Override
	public Configuration getConf() {
		return conf;
	}

	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public ParseResult filter(Content content, ParseResult parseResult, HTMLMetaTags metaTags, DocumentFragment doc) {

		String url = content.getUrl();
		LOG.info("Getting rawxml content for " + url);

		Metadata metadata = parseResult.get(url).getData().getParseMeta();

		//Metadata metadata = new Metadata();

		byte[] raw = content.getContent();

		metadata.add(RAW_CONTENT, new String(raw));

		Document dom = XMLUtils.parseXml(new ByteArrayInputStream(raw));

		nsc = dom.getRootElement().getNamespace();

		String text = "";
		try {
			detectOGC(dom, metadata);
		} catch (JaxenException e) {
			return new ParseStatus(ParseStatus.FAILED, "XML Parser Jaxen error : " + e.getMessage())
					.getEmptyParseResult(content.getUrl(), getConf());
		}

		// TODO Possible improve 
		Outlink[] outlinks = OutlinkExtractor.getOutlinks(text, getConf());

		return parseResult;
	}

	private void detectOGC(Document dom, Metadata metadata) throws JaxenException {
		JDOMXPath xp = new JDOMXPath("//*");
		xp.addNamespace(nsc.getPrefix(), nsc.getURI());
		List<?> ls = xp.selectNodes(dom);
		Element root = (Element) ls.get(0);
		String name = root.getName();
		String version = root.getAttributeValue("version");
		if (version != null){
			metadata.add(OGC_VERSION, version);
		}
		
		if (containsOGC(name, "WMS")) {
			metadata.add(OGC_SERVICE, "wms");
		} else if (containsOGC(name, "WMT_MS")) {
			metadata.add(OGC_SERVICE, "wms-c");
		} else if (containsOGC(name, "CSW")) {
			metadata.add(OGC_SERVICE, "csw");
		} else if (containsOGC(name, "WFS")) {
			metadata.add(OGC_SERVICE, "wfs");
		} else if (containsOGC(name, "WPS")) {
			metadata.add(OGC_SERVICE, "wps");
		} else if (containsOGC(name, "WCS")) {
			metadata.add(OGC_SERVICE, "wcs");
		} else if (containsOGC(name, "WCTS")) {
			metadata.add(OGC_SERVICE, "wcts");
		} else if(checkAtom(ls)){	
			metadata.add(OGC_VERSION, "1.0");
			metadata.add(OGC_SERVICE, "atom");
		} else if(checkWMTS(ls)){
			metadata.add(OGC_SERVICE, "wmts");
		} else {
			LOG.info("OGC service not detected");
		}
	}

	private boolean checkWMTS(List<?> ls) {
		Element root = (Element) ls.get(0);		
		return containsOGC(root.getNamespace().getURI(), "WMTS");
	}

	private boolean checkAtom(List<?> ls) {
		Element root = (Element) ls.get(0);
		return root.getNamespace().getURI().equals("http://www.w3.org/2005/Atom");
	}

	private boolean containsOGC(String f, String service) {
		return org.apache.commons.lang3.StringUtils.containsIgnoreCase(f, service);
	}
}
