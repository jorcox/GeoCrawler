package org.unizar.nutch.scoring.geo.thesaurus;

import java.io.FileNotFoundException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

public class Thesaurus {

	private String queryString;
	private final String themeGeo = "<http://www.eionet.europa.eu/gemet/theme/16>";
	private final String uriGEMET = "http://www.eionet.europa.eu/gemet/2004/06/gemet-schema.rdf#";
	private Model model;
	private String[] langs = { "ar", "bg", "cs", "da", "de", "el", "en-US", "en", "es", "et", "eu", "fi", "fr", "hu",
			"it", "lt", "lv", "mt", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sv", "tr" };

	public Thesaurus(){
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
		queryString = "PREFIX rdf: <" + RDF.getURI() + "> PREFIX rdfs: <" + RDFS.getURI() + "> PREFIX skos: <"
				+ SKOS.getURI() + "> PREFIX gemet: <" + uriGEMET + "> "
				+ "SELECT distinct ?x WHERE { ?y rdf:type skos:Concept . ?y skos:prefLabel ?x . FILTER regex(?x, '"
				+ word + "', 'i') ." + " ?y gemet:theme " + themeGeo + "}";

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		int c = 0;
		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				//String sentencia = "";
				QuerySolution soln = results.nextSolution();
				RDFNode x = soln.get("x");
				System.out.println(x.toString());
				c++;
			}
		} finally {
			qexec.close();
		}
		return c;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Thesaurus th = new Thesaurus();
		th.execQuery("Mar");
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
