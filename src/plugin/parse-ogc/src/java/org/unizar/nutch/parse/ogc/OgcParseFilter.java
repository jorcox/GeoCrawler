package org.unizar.nutch.parse.ogc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.ParseData;
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

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parse the content from XML in order to check if it corresponds to an OGC
 * service. In addition, makes full raw content of XML (and HTML) documents
 * available.
 * 
 * @author Jorge Cancer
 */
public class OgcParseFilter implements HtmlParseFilter {

	private final static String RAW_CONTENT = "raw_content";

    private final static String OGC_SERVICE = "ogc_service";

    private final static String OGC_VERSION = "ogc_version";
    
    private final static String ANCHOR_CONTEXT = "anchor_context";
    
    private final static String ANCHOR = "anchor";

    private static final Logger LOG = LoggerFactory.getLogger(OgcParseFilter.class);
    
    private int boundary;

    private Configuration conf;

	private Namespace nsc;
    private static final Map<String, String> CONTAINS_MAP = new HashMap<>();
    static {
        CONTAINS_MAP.put("wms","wms");
        CONTAINS_MAP.put("wms_ms","wms");
        CONTAINS_MAP.put("csw","csw");
        CONTAINS_MAP.put("wfs","wfs");
        CONTAINS_MAP.put("wcs","wcs");
        CONTAINS_MAP.put("wcts","wcts");
        CONTAINS_MAP.put("wps","wps");
    }

    @Override
	public Configuration getConf() {
		return conf;
	}

	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
		boundary = conf.getInt("ogc.outlink.anchor.context", 30);
	}

	@Override
	public ParseResult filter(Content content, ParseResult parseResult, HTMLMetaTags metaTags, DocumentFragment doc) {

		String url = content.getUrl();
		
		LOG.info("Getting ogc content for " + url);

		Metadata metadata = parseResult.get(url).getData().getParseMeta();
		
		ParseData parseData = parseResult.get(url).getData();
		

		byte[] raw = content.getContent();
		
		// Text with HTML tags
		String h = new String(raw);
		
		// Text without HTML tags
		String he = parseResult.get(url).getText();
		
		Outlink[] outLinks = parseData.getOutlinks();
		

		// For each link read anchor and and around text with a boundary definded in conf
		for (Outlink outlink : outLinks) {
			String anchor = outlink.getAnchor();
			if(!anchor.equals("")){
				int index = he.indexOf(anchor);  		// Anchor's index (Search around)
				MapWritable metadataOutlink = new MapWritable();
				String context = extractContext(he, index);
				metadataOutlink.put(new Text(ANCHOR_CONTEXT), new Text(context));
				metadataOutlink.put(new Text(ANCHOR), new Text(anchor));
				outlink.setMetadata(metadataOutlink);
			} else{
				// TODO ¿What if the outlink doesn't have anchor?
			}			
		}
		
		// Save changes in parse data
		parseData.setOutlinks(outLinks);
		
		// If the content is xml check if it's an ogc service		
		String contentType = content.getContentType();		
		if(contentType.equals("application/xml") || contentType.equals("text/xml")){
				
			metadata.add(RAW_CONTENT, new String(raw));
	
			Document dom = XMLUtils.parseXml(new ByteArrayInputStream(raw));
	
			nsc = dom.getRootElement().getNamespace();
	
			try {
				detectOGC(dom, metadata);
			} catch (JaxenException e) {
				return new ParseStatus(ParseStatus.FAILED, "XML Parser Jaxen error : " + e.getMessage())
						.getEmptyParseResult(content.getUrl(), getConf());
			}
		}
	
			// TODO Possible improve 
			// Outlink[] outlinks = OutlinkExtractor.getOutlinks(text, getConf());

		return parseResult;
	}

	private String extractContext(String he, int index) {
		String res = "";
		try{
			res = he.substring(index-boundary, index+boundary);
		} catch (IndexOutOfBoundsException e){
			if(index-boundary < 0 && index+boundary > he.length() ) {
				res = he.substring(0, he.length());
			} else if(index+boundary > he.length()){
				// index + boundary is larger than string length
				res = he.substring(index-boundary, he.length());
			} else if(index-boundary < 0) {
				res = he.substring(0, he.length());
			}
		}		
		return res;
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


		for(Map.Entry<String,String> e: CONTAINS_MAP.entrySet()) {
			if (containsOGC(name, e.getKey())) {
				metadata.add(OGC_SERVICE, e.getValue());
				return;
			}
		}

		if(checkAtom(ls)){
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
