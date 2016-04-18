package main;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

public class Sparql {
	public static final String resource_URL = "http://home.eps.hw.ac.uk/~qz1/ontologies/wirelessnetwork_networkResource.owl";
	
	public static final String resource_NS = resource_URL + "#";
	
	public static final String prefix = 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
			"PREFIX netRes: <http://home.eps.hw.ac.uk/~qz1/ontologies/wirelessnetwork_networkResource.owl#>";

	public static ResultSet runQuery(String queryStr) {
		Model model = FileManager.get().loadModel(resource_URL);

		String queryString = prefix + queryStr;
		
		Query query = QueryFactory.create(queryString, Syntax.syntaxARQ ) ;

		try(QueryExecution qExec = QueryExecutionFactory.create(query, model)) { 
			ResultSet results = qExec.execSelect();
			results = ResultSetFactory.copyResults(results);

			return results;
		}
	}

}
