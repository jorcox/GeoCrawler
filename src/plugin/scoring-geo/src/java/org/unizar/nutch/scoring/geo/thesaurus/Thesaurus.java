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

	public static int execQuery(String word) {
		String themeGeo = "<http://www.eionet.europa.eu/gemet/theme/16>";
		String uriGEMET = "http://www.eionet.europa.eu/gemet/2004/06/gemet-schema.rdf#";

		String queryString = "PREFIX rdf: <" + RDF.getURI() + "> PREFIX rdfs: <" + RDFS.getURI() + "> PREFIX skos: <"
				+ SKOS.getURI() + "> PREFIX gemet: <" + uriGEMET + "> "
				+ "SELECT distinct ?x WHERE { ?y rdf:type skos:Concept . ?y skos:prefLabel ?x . FILTER regex(?x, '"
				+ word + "', 'i') ." + " ?y gemet:theme " + themeGeo + "}";

		Model model = FileManager.get().loadModel("gemetThesaurus.rdf");
		Model model2 = FileManager.get().loadModel("gemet-backbone.rdf");
		Model model3 = FileManager.get().loadModel("gemet-definitions.rdf");
		Model model4 = FileManager.get().loadModel("gemet-groups.rdf");
		Model model5 = FileManager.get().loadModel("gemet-skoscore.rdf");
		model.add(model2);
		model.add(model3);
		model.add(model4);
		model.add(model5);

		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		int c = 0;
		try {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				String sentencia = "";
				QuerySolution soln = results.nextSolution();
				RDFNode x = soln.get("x");
				c++;
			}
		} finally {
			qexec.close();
		}
		return c;
	}

	public static void main(String[] args) throws FileNotFoundException {
		execQuery("agua");
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
