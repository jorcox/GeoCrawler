package org.unizar.nutch.parse.ogc;

import static org.junit.Assert.assertEquals;
import static org.unizar.nutch.test.utils.Utils.createContent;
import static org.unizar.nutch.test.utils.Utils.createParseResultWithMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;
import org.junit.Test;

public class OgcParseFilterTest {

	@Test
	public void testWMS() throws FileNotFoundException, URISyntaxException {
		// Preparaci
		File f = new File(getClass().getResource("testWMS.xml").toURI());
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
	public void testATOM() throws FileNotFoundException, URISyntaxException {
		// Preparacion
		File f = new File(getClass().getResource("testATOM.xml").toURI());
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

}
