package org.unizar.nutch.scoring.geo.thesaurus;

import java.io.FileNotFoundException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

public class Thesaurus {

	private static final String themeGeo = "<http://www.eionet.europa.eu/gemet/theme/16>";
	private static final String themeAir = "<http://www.eionet.europa.eu/gemet/theme/3>";
	private static final String themeClimate = "<http://www.eionet.europa.eu/gemet/theme/7>";
	private static final String themeAgricultute = "<http://www.eionet.europa.eu/gemet/theme/2>";
	private static final String themeFishery = "<http://www.eionet.europa.eu/gemet/theme/12>";
	private static final String themeNatural = "<http://www.eionet.europa.eu/gemet/theme/23>";
	private static final String themePollution = "<http://www.eionet.europa.eu/gemet/theme/26>";
	private static final String themeWater = "<http://www.eionet.europa.eu/gemet/theme/40>";
	private static final String themeUrban = "<http://www.eionet.europa.eu/gemet/theme/38>";
	private static final String uriGEMET = "http://www.eionet.europa.eu/gemet/2004/06/gemet-schema.rdf#";
	private Model model;
	private static String[] langs = { "ar", "bg", "cs", "da", "de", "el", "en-US", "en", "es", "et", "eu", "fi", "fr",
			"hu", "it", "lt", "lv", "mt", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sv", "tr" };

	public Thesaurus() {
		model = FileManager.get().loadModel("gemetThesaurus.rdf");
		Model modelBackbone = FileManager.get().loadModel("gemet-backbone.rdf");
		Model modelCore = FileManager.get().loadModel("gemet-skoscore.rdf");
		model.add(modelBackbone);
		model.add(modelCore);
		for (String lang : langs) {
			Model groupLang = FileManager.get().loadModel("gemet-groups-" + lang + ".rdf");
			Model definitionLang = FileManager.get().loadModel("gemet-definitions-" + lang + ".rdf");
			model.add(groupLang);
			model.add(definitionLang);
		}
	}

	public int execQuery(String word) {
		// System.out.println(word);
		String queryString = "";

		queryString = "PREFIX rdf: <" + RDF.getURI() + "> PREFIX rdfs: <" + RDFS.getURI() + "> PREFIX skos: <"
				+ SKOS.getURI() + "> PREFIX gemet: <" + uriGEMET + "> "
				+ "SELECT distinct ?x WHERE { ?y rdf:type skos:Concept . ?y skos:prefLabel ?x . FILTER regex(?x, '"
				+ word + "', 'i') ." + "{ ?y gemet:theme " + themeGeo + "} UNION {" + "?y gemet:theme " + themeAir
				+ "} UNION {" +
				// "?y gemet:theme " + themeClimate + "} UNION {" +
				// "?y gemet:theme " + themeAgricultute + "} UNION {" +
				// "?y gemet:theme " + themeFishery + "} UNION {" +
				// "?y gemet:theme " + themeNatural + "} UNION {" +
				// "?y gemet:theme " + themePollution + "} UNION {" +
				// "?y gemet:theme " + themeWater + "} UNION {" +
		"?y gemet:theme " + themeUrban + "}}";

		Query query = QueryFactory.create(queryString);
		int c = 0;
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				c++;
			}
		}
		return c;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Thesaurus th = new Thesaurus();
		th.execQuery("agua");
		/*
		 * Model model1 = FileManager.get().loadModel("gemetThesaurus.rdf");
		 * Model model2 = FileManager.get().loadModel("gemet-backbone.rdf");
		 * Model model3 = FileManager.get().loadModel("gemet-definitions.rdf");
		 * Model model4 = FileManager.get().loadModel("gemet-groups.rdf"); Model
		 * model5 = FileManager.get().loadModel("gemet-skoscore.rdf");
		 * model1.add(model2); model1.add(model3); model1.add(model4);
		 * model1.add(model5);
		 * 
		 * String a = model1.toString();
		 * 
		 * FileOutputStream fop = null; File file; String content =
		 * "This is the text content";
		 * 
		 * try {
		 * 
		 * file = new File(
		 * "/media/jorge/a890aa75-9c1b-4a9d-aad8-ec9bf8b240bd/jorge/Almacen/prueba.txt"
		 * ); fop = new FileOutputStream(file);
		 * 
		 * // if file doesnt exists, then create it if (!file.exists()) {
		 * file.createNewFile(); }
		 * 
		 * // get the content in bytes byte[] contentInBytes = a.getBytes();
		 * 
		 * fop.write(contentInBytes); fop.flush(); fop.close();
		 * 
		 * System.out.println("Done");
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 */
	}
}
