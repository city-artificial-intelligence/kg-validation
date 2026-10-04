package uio.ifi.ontology.toolkit.projection.controller.triplestore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uio.ifi.ontology.toolkit.constraint.utils.Utility;
import uio.ifi.ontology.toolkit.constraint.utils.pagoda_hermit.Timer;
import uio.ifi.ontology.toolkit.projection.controller.reasoner.ReasonerManager.OWL2Reasoner;

import tech.oxfordsemantic.jrdfox.Prefixes;
//import tech.oxfordsemantic.jrdfox.client.QueryDomain;
import tech.oxfordsemantic.jrdfox.client.UpdateType;
import tech.oxfordsemantic.jrdfox.exceptions.JRDFoxException;

public class RDFoxProjectionManagerForEmbeddings extends RDFoxProjectionManager{

	
	
	public RDFoxProjectionManagerForEmbeddings(OWLOntology ontology, OWL2Reasoner reasonerID) throws JRDFoxException, IOException {
		super(ontology, "", false, false, reasonerID, false); //no propagation and no facets

	}
	
	
	public RDFoxProjectionManagerForEmbeddings(String ontology_iri, OWL2Reasoner reasonerID) throws JRDFoxException, OWLOntologyCreationException, IOException {
		super(ontology_iri, "", false, false, reasonerID, false); //no propagation and no facets
	}
	
	
	
	public RDFoxProjectionManagerForEmbeddings(Set<String> ontology_iris, OWL2Reasoner reasonerID) throws JRDFoxException, OWLOntologyCreationException, IOException {
		super(ontology_iris, "", false, false, reasonerID, false); //no propagation and no facets
	}
	
	
	
	
	protected void perfromMaterialization() throws JRDFoxException{
		
		try{			
			
			//1. Import rules from classification and inverse axioms
			Timer t = new Timer();
			
			//We import the classified ontology rules (inverses are the required ones specially) and also the
			//projection of the classification (see GraphProjectionManager)
			//TODO Not necessary for embeddings
			//Utility.println("Importing classified ontology...");
			//store.importOntology(getClassifiedOntology());
						
			//2. Import projection data
			Utility.println("Importing RDF data (projection)...");
			//store.importFiles(new File[] {new File(tmp_file_projection)});//, new File(tmp_file)});
			try (InputStream inputStream = new BufferedInputStream(RDFoxProjectionManager.class.getResourceAsStream(tmp_file_projection))) {
                dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, inputStream);
            }
			
			//EDB are the explicitly stated facts.
            //IDB facts are the EDB facts plus all of their consequences.
			number_initial_triples = getTriplesCount(dataStoreConnection);
			Utility.println("Number of tuples after initial data import: " + number_initial_triples);
			
			
			
			StringBuilder rule_builder = new StringBuilder(); 
			
			//3a. Import equality rules			
			Utility.println("Importing rules...");			
			//File equality_file = new File(Constants.working_directory + "equality.dlog");
			//store.importFiles(new File[] {equality_file});
			getTextForPrefixes(rule_builder);
			
			//TODO DO we need them here?
			getTextForEqualityRules(rule_builder);
			
			//
			
			
			//3b. Merge Domain and Range axiom triples
			getTextForRangeDomainPropagationRules(rule_builder);
			
			//4. Add rules to saturate graph: top-bottom
			if (topBottomPropagation)
				getTextForTopBottomPropagationRules(rule_builder);
			
			//store.importText(rule_builder.toString());
			dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, rule_builder.toString());
			Utility.println("Importing time RDFox: " + t.durationMilisecons()  + " (ms)");
			
			
			//5. Materialization
			Utility.println("Standard materialization + Top-down propagation...");
			t = new Timer();
			//store.applyReasoning();
			dataStoreConnection.updateMaterialization();
			//Utility.println("Materialization time RDFox: " + t.duration()  + " (s)");
			axiom_materialization_time = t.durationMilisecons();
			Utility.println("Materialization time RDFox 1: " + axiom_materialization_time  + " (ms)");

			number_triples_materialization_axioms = getTriplesCount(dataStoreConnection);
			Utility.println("Number of tuples after materialization 1: " + number_triples_materialization_axioms);
			
			
			
			//6. Make facts explicit
			//Necessary to avoid the application of other rules like bottom-up 
			//store.clearRulesAndMakeFactsExplicit();//Important to clear rules
			//store.makeFactsExplicit();
			dataStoreConnection.clearRulesAxiomsExplicateFacts();
			
			
				
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}

	
	
	
}
