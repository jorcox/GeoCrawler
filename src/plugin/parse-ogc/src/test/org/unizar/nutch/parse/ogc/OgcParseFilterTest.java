package org.unizar.nutch.parse.ogc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;
import org.junit.Test;

public class OgcParseFilterTest {

	@Test
	public void testWMS() throws FileNotFoundException {
		// Preparacion
		File f = new File("res/testOGC/testWMS.xml");
		String contentValue = new Scanner(f).useDelimiter("\\Z").next();
		String url = "http://wms.magrama.es/sig/Agricultura/TurcSecano/wms.aspx?request=GetCapabilities&service=WMS";
		ParseResult testParseResult = createParseResultWithMetadata(new Metadata(), url);
		Content testContent = createContent(url, contentValue);

		OgcParseFilter parseFilter = new OgcParseFilter();

		// Filtrar
		ParseResult res = parseFilter.filter(testContent, testParseResult, null, null);

		// Comprobaciones
		Metadata metadata = res.get(url).getData().getParseMeta();
		assertEquals("1.3.0", metadata.get("ogc_version"));
	    assertEquals("wms", metadata.get("ogc_service"));

	}
	
	@Test
	public void testATOM() throws FileNotFoundException {
		// Preparacion
		File f = new File("res/testOGC/testATOM.xml");
		String contentValue = new Scanner(f).useDelimiter("\\Z").next();
		String url = "http://www.magrama.gob.es/ide/inspire/atom/CategCalidadEvalAmbiental/downloadservice.xml";
		ParseResult testParseResult = createParseResultWithMetadata(new Metadata(), url);
		Content testContent = createContent(url, contentValue);

		OgcParseFilter parseFilter = new OgcParseFilter();

		// Filtrar
		ParseResult res = parseFilter.filter(testContent, testParseResult, null, null);

		// Comprobaciones
		Metadata metadata = res.get(url).getData().getParseMeta();
		assertEquals("1.0", metadata.get("ogc_version"));
	    assertEquals("atom", metadata.get("ogc_service"));

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

	private ParseResult createMockParseResult() {
		return createParseResultWithMetadata(new Metadata(), null);
	}

	private Content createContent(final String url) {
		return createContent(url, null);
	}

	private Content createContent(final String url, final String content) {
		byte[] contentByteArray = {};
		if (content != null) {
			contentByteArray = content.getBytes();
		}
		Content cont = new Content(url, "", contentByteArray, null, new Metadata(), new Configuration());
		return cont;
	}
	
	public static void main(String[] args) {
		try {
			new OgcParseFilterTest().testATOM();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
