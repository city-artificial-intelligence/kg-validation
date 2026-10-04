/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.controller.triplestore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlSchema;

import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semarglproject.vocab.XSD;

import uio.ifi.ontology.toolkit.constraint.utils.Utility;
import uio.ifi.ontology.toolkit.constraint.utils.pagoda_hermit.Timer;
import uio.ifi.ontology.toolkit.projection.controller.GraphProjectionManager;
import uio.ifi.ontology.toolkit.projection.controller.reasoner.ReasonerManager.OWL2Reasoner;
import uio.ifi.ontology.toolkit.projection.model.BooleanFacet;
import uio.ifi.ontology.toolkit.projection.model.DateFacet;
import uio.ifi.ontology.toolkit.projection.model.DefaultFacet;
import uio.ifi.ontology.toolkit.projection.model.Facet;
import uio.ifi.ontology.toolkit.projection.model.Facet.FacetType;
import uio.ifi.ontology.toolkit.projection.model.NeighbourLink;
import uio.ifi.ontology.toolkit.projection.model.SelectFacet;
import uio.ifi.ontology.toolkit.projection.model.SliderFacet;
import uio.ifi.ontology.toolkit.projection.model.TextFacet;
import uio.ifi.ontology.toolkit.projection.model.entities.Concept;
import uio.ifi.ontology.toolkit.projection.model.entities.DataProperty;
import uio.ifi.ontology.toolkit.projection.model.entities.Entity;
import uio.ifi.ontology.toolkit.projection.model.entities.GenericValue;
import uio.ifi.ontology.toolkit.projection.model.entities.Instance;
import uio.ifi.ontology.toolkit.projection.model.entities.ObjectProperty;
import uio.ifi.ontology.toolkit.projection.model.triples.DataPropertyTriple;
import uio.ifi.ontology.toolkit.projection.model.triples.ObjectPropertyTriple;
import uio.ifi.ontology.toolkit.projection.utils.URIUtils;

import tech.oxfordsemantic.jrdfox.Prefixes;
import tech.oxfordsemantic.jrdfox.client.ConnectionFactory;
import tech.oxfordsemantic.jrdfox.client.Cursor;
import tech.oxfordsemantic.jrdfox.client.DataStoreConnection;
//import tech.oxfordsemantic.jrdfox.client.QueryDomain;
import tech.oxfordsemantic.jrdfox.client.ResourceValue;
import tech.oxfordsemantic.jrdfox.client.ServerConnection;
import tech.oxfordsemantic.jrdfox.client.UpdateType;
import tech.oxfordsemantic.jrdfox.exceptions.JRDFoxException;





/**
 * Uses RDFox as manager of the projection
 * Uses RDFox 2019 (downloaded from OST)
 * Documentation: https://oxfordsemtech.github.io/RDFox/#/
 * @author ernesto
 * Created on 5 Jan 2017
 * Modified on May 3 2019
 *
 */
public class RDFoxProjectionManager extends GraphProjectionManager<Cursor, UpdateType> {

	//Requires serialization of objects into triples
	//requires addition of rules to enhance automatic reasoning
	
	//RDFox datastore connection
	ServerConnection serverConnection;
	DataStoreConnection dataStoreConnection;
	
	long number_initial_triples;
	double axiom_materialization_time;
	double data_materialization_time;
	long number_triples_materialization_axioms;
	long number_triples_materialization_data;
	
	
	String tmp_file_projection = Utility.tmp_directory + "tmp_file_projection.ttl";
	String tmp_file_materialization = Utility.tmp_directory + "tmp_file_materialization_snapshot.ttl";
	
	String data_file = "";
	
	
	boolean topBottomPropagation;
	boolean extendedPropagation;
	 
	//protected Collection<DLClause> datalog_rules = new HashSet<DLClause>();
	
	
	/**
	 * 
	 * @param ontology_iris Set of ontologies to project
	 * @param data_file
	 * @param topBottomPropagation
	 * @param bottomUpPropagation
	 * @param performClassification
	 * @throws JRDFoxException
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 */
	public RDFoxProjectionManager(Set<String> ontology_iris, String data_file, boolean topBottomPropagation, boolean extendedPropagation, OWL2Reasoner reasonerID, boolean create_facets) throws JRDFoxException, OWLOntologyCreationException, IOException {
		//Classifies ontology and creates model projection		
		super(ontology_iris, reasonerID, create_facets);
		
		this.data_file = data_file; 
		
		setUp(topBottomPropagation, extendedPropagation);
	}
	
	/**
	 * 
	 * @param ontology_iri Ontology path
	 * @param data_file (Optional) file with data associated to ontology
	 * @param topBottomPropagation 
	 * @param bottomUpPropagation
	 * @param performClassification If OWL 2 classification is performed
	 * @throws JRDFoxException
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 */
	public RDFoxProjectionManager(String ontology_iri, String data_file, boolean topBottomPropagation, boolean extendedPropagation, OWL2Reasoner reasonerID, boolean create_facets) throws JRDFoxException, OWLOntologyCreationException, IOException {
		//Classifies ontology and creates model projection		
		super(ontology_iri, reasonerID, create_facets);
		
		this.data_file = data_file; 
		
		setUp(topBottomPropagation, extendedPropagation);
	}
	

	/**
	 * 
	 * @param ontology Ontology object
	 * @param data_file (Optional) file with data associated to ontology
	 * @param topBottomPropagation
	 * @param bottomUpPropagation
	 * @param performClassification f OWL 2 classification is performed
	 * @throws JRDFoxException
	 * @throws IOException
	 */
	public RDFoxProjectionManager(OWLOntology ontology, String data_file, boolean topBottomPropagation, boolean extendedPropagation, OWL2Reasoner performClassification, boolean create_facets) throws JRDFoxException, IOException {
		//Classifiess ontology and creates model projection	
		super(ontology, performClassification, create_facets);
		
		this.data_file = data_file;
		
		setUp(topBottomPropagation, extendedPropagation);
	
	}
	
	
	protected void setUp(boolean topBottomPropagation, boolean extendedPropagation) throws JRDFoxException, IOException {
		this.topBottomPropagation=topBottomPropagation;
		this.extendedPropagation=extendedPropagation;
		
		
		Utility.println("Saving ontology RDF model");
		//Stores model projection as turtle file (required by RDFox)
		getGraph().saveModel(tmp_file_projection);
				
		Utility.println("Performing RDFox materialization...");
		initializeRDFox();
		perfromMaterialization();
		
		//getCoreConcepts();
	}

	
	
	
	protected void initializeRDFox() throws JRDFoxException {
		
		//Local server
		serverConnection = ConnectionFactory.newServerConnection("rdfox:local", "", "");
		
		// We create a data store of type "par-complex-nn".
		//Used ParallelSimpleNN before
		//https://oxfordsemtech.github.io/RDFox/#/04-using?id=data-store-type
		HashMap<String, String> options = new HashMap<String, String>(); //Collections.emptyMap()
		
		//TODO Check UNA and equaliaty rules.... 
		options.put("equality", "off"); //off, UNA, noUNA, etc.
	
		if(serverConnection.containsDataStore("ontology-projection"))
			serverConnection.deleteDataStore("ontology-projection"); 
	
        serverConnection.createDataStore("ontology-projection", "par-complex-nn", options); //par-complex-nn //par-simple-nn
		
        // We connect to the data store.
		dataStoreConnection = serverConnection.newDataStoreConnection("ontology-projection");
		
		//Setting the number of threads is not anymore supported
        //dataStoreConnection.setNumberOfThreads(8); //This could be a parameter
		
		
	}
	
	
	
	
	
	protected static long getTriplesCount(DataStoreConnection dataStoreConnection) throws JRDFoxException {
        HashMap<String, String> parameters = new HashMap<String, String>();
        //parameters.put("domain", queryDomain.toString());
        try (Cursor cursor = dataStoreConnection.createCursor(Prefixes.s_emptyPrefixes, "SELECT ?X ?Y ?Z WHERE{ ?X ?Y ?Z }", parameters)) {
            long result = 0;
            for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance())
                result += multiplicity;
            // Since the iterator is exhausted, it does not need to be closed.
            return result;
        }
    }
	
	
	
	
	protected void perfromMaterialization() throws JRDFoxException{
		
		try{			
			
			//1. Import rules from classification and inverse axioms
			Timer t = new Timer();
			Utility.println("Importing classified ontology...");
			
			//We import the classified ontology rules (inverses are the required ones specially) and also the
			//projection of the classification (see GraphProjectionManager)
			//store.importOntology(getClassifiedOntology());
			//dataStoreConnection.importData(getClassifiedOntology(), UpdateType.ADDITION, true, true, false, false);
			
			// TODO: This is probably not correct, I dont know what toString does
			//InputStream turtle_stream = getOntologyPipe(getClassifiedOntology());
			dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, getClassifiedOntologyStream());
			
			
			//2. Import projection data
			Utility.println("Importing RDF data (projection)...");
			//store.importFiles(new File[] {new File(tmp_file_projection)});//, new File(tmp_file)});
			
			InputStream fis = new FileInputStream(new File(tmp_file_projection));
			try (InputStream inputStream = new BufferedInputStream(fis)) {
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
			axiom_materialization_time = t.durationMilisecons();
			Utility.println("Materialization time RDFox 1: " + axiom_materialization_time  + " (ms)");

			number_triples_materialization_axioms = getTriplesCount(dataStoreConnection);
			Utility.println("Number of tuples after materialization 1: " + number_triples_materialization_axioms);
			
			
			//DONE
			//Potential problem with the propagation: links are propagated up and down. In multiple inheritance cases eg: AA sub A and AA sub B. The links from A will be propagated to AA and from AA to B.
			//This may be desired depending on the case... but it will also increase the number of suggestions.  
			//Necessary to do a 2 step approach as for the constraint validation. 
			//1st Bottom-Up propagation, 2nd clearRulesAndFactsExplicit, 3rd Top-Bottom propagation ->Leads to undesired propagation
			//1st Top-down propagation, 2nd clearRulesAndFactsExplicit, 3rd Bottom-up propagation -> Desired behaviour.It may over propagate with multiple inheritance
			
			
			//6. Make facts explicit
			//Necessary to avoid the application of other rules like bottom-up 
			//store.clearRulesAndMakeFactsExplicit();
			dataStoreConnection.clearRulesAxiomsExplicateFacts();//Important to clear rules
			
			
			//7. Add rules to saturate graph: bottom-up rules
			//Add rules to saturate graph: bottom-up rules		
			if (extendedPropagation) {
				
				rule_builder.setLength(0);
				getTextForPrefixes(rule_builder);

				
				getTextForBottomUpPropagationRules(rule_builder);
			
			
				//store.importText(rule_builder.toString());
				dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, rule_builder.toString());
				
				
				//8. Materialization
				Utility.println("Materialization Bottom-Up propagation...");
				t = new Timer();
				//store.applyReasoning();
				dataStoreConnection.updateMaterialization();
				//Utility.println("Materialization time RDFox: " + t.duration()  + " (s)");
				axiom_materialization_time = t.durationMilisecons();
				Utility.println("Materialization time RDFox 2: " + axiom_materialization_time  + " (ms)");
	
				number_triples_materialization_axioms = getTriplesCount(dataStoreConnection);
				Utility.println("Number of tuples after materialization 2: " + number_triples_materialization_axioms);
				
				//9. Make facts explicit
				//Necessary to avoid the application of other rules like bottom-up 
				//store.clearRulesAndMakeFactsExplicit();
				dataStoreConnection.clearRulesAxiomsExplicateFacts();//Important to clear rules
				
			}
			
			

			
			
			//10. Add rules to saturate graph: top-bottom in the range
			if (extendedPropagation) { //extended propagation on the range...
				
				rule_builder.setLength(0);
				getTextForPrefixes(rule_builder);
				
				getTextForTopBottomPropagationRulesInRange(rule_builder);
			
				//store.importText(rule_builder.toString());
				dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, rule_builder.toString());
				
				
				
				//11. Materialization
				Utility.println("Materialization Top-Bottom propagation in range...");
				t = new Timer();
				//store.applyReasoning();
				dataStoreConnection.updateMaterialization();
				//Utility.println("Materialization time RDFox: " + t.duration()  + " (s)");
				axiom_materialization_time = t.durationMilisecons();
				Utility.println("Materialization time RDFox 3: " + axiom_materialization_time  + " (ms)");
	
				number_triples_materialization_axioms = getTriplesCount(dataStoreConnection);
				Utility.println("Number of tuples after materialization 3: " + number_triples_materialization_axioms);
			}
			
			
			
			//12. Load ontology and rules again and optional data
			Utility.println("Last step: importing ontology, rules and data");
			//store.importOntology(getClassifiedOntology());
			//dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, getClassifiedOntology().toString(), true, true, false, false);
			dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, getClassifiedOntologyStream());
						
			rule_builder.setLength(0);
			getTextForPrefixes(rule_builder);
			
			//TODO DO we need them here?
			getTextForEqualityRules(rule_builder);
			
			//To infer Top as type
			getTextForThingPropagationRules(rule_builder);
			//store.importText(rule_builder.toString());
			dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, rule_builder.toString());
			
			performMaterializationAdditionalData(getDataFilePath(), true, UpdateType.ADDITION);
			
			//TODO TESTS: Export/save materialization snapshot (turtle)
			exportMaterielizationSnapshot();
			
				
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
	public void performMaterializationAdditionalData(String file_data, boolean incremental, UpdateType updateType) throws JRDFoxException, IOException{
	
		//TODO be called when data changes (e.g., new annotations)
		
		Utility.println("Importing RDF data (e.g. image annotations) to " + updateType + "...");
		
		File file = new File(file_data);
		
		if (!file.exists()) {
			Utility.println("...(Optional) Data file not given or it does not exist.");
			return;
		}
		
		//store.importFiles(new File[] {new File(file_data)});//, new File(tmp_file)});
		//https://oxfordsemtech.github.io/RDFox/#/05-shell?id=import
		//store.importFiles(new File[] {new File(file_data)}, updateType);  //if updateType not indicated by default is addition 
		InputStream fis = new FileInputStream(new File(file_data));
		try (InputStream inputStream = new BufferedInputStream(fis)) {
            dataStoreConnection.importData(updateType, Prefixes.s_emptyPrefixes, inputStream);
        }
		
		
		
		//TODO Update type important if removing triples
		//store.importFiles(new File[] {new File(file_data)}, UpdateType.ScheduleForDeletion);
		
		
		Timer t = new Timer();
		/*if (!updateType.equals(UpdateType.Add))
			store.applyReasoning(incremental);//incremental reasoning=true only works only with ScheduleFor... and viceversa
		else
			store.applyReasoning(); //With default updateType.ADD incremental does not not work 
		*/
		dataStoreConnection.updateMaterialization();
		
		data_materialization_time = t.durationMilisecons();
		Utility.println("Data materialization time RDFox: " + data_materialization_time  + " (ms)");
		
		number_triples_materialization_data = getTriplesCount(dataStoreConnection);
		Utility.println("Number of tuples after data import (on the fly additions or deletions): " + number_triples_materialization_data);
		
		//TODO TESTS: Export/save materialization snapshot (turtle)
		//exportMaterielizationSnapshot();
		
	}
	
	
	/**
	 * Exports/saves materialization snapshot into turtle
	 * @throws JRDFoxException
	 */
	public void exportMaterielizationSnapshot(String file_out_materialization) throws JRDFoxException {
		
		try {
			//store.export(new File(file_out_materialization), Format.Turtle);
			File exportFacts = new File(file_out_materialization);
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(exportFacts))) {
            	Map<String, String> preferences = new HashMap<String, String>();
                 dataStoreConnection.exportData(Prefixes.s_defaultPrefixes, outputStream , "application/n-triples", preferences);
            }
		}
		catch (Exception e) {
			System.out.println("Error storing materizalition. " + e.getMessage());
		}
		
		
		//Show/print all triples:
		getQueryResults(getSPARQLQueryAll());
	
	}
	
	/**
	 * Exports/saves materialization snapshot into turtle
	 * @throws JRDFoxException
	 */
	protected void exportMaterielizationSnapshot() throws JRDFoxException {
		
		exportMaterielizationSnapshot(tmp_file_materialization);
		
		//store.export(new File(tmp_file_materialization), Format.Turtle);
		
		
		//Show/print all triples:
		//getQueryResults(getSPARQLQueryAll());
	
	}
	
	
	
	protected void getTextForThingPropagationRules(StringBuilder builder){
		
		builder.append("\n");
		builder.append("[ ?X, rdf:type, owl:Thing ] :- [ ?X, rdf:type, ?Y ], [ ?Y,  rdfs:subClassOf, owl:Thing ], [ ?Y, rdf:type, owl:Class ] .");

		
	}
	
	
	protected void getTextForTopBottomPropagationRules(StringBuilder builder){
		
		
		//We also restrict by type to avoid the application of the rule for other type of properties like subclassof, inverseof or annotations. 
		//We also restrict the type of the subject and object, otherwise the rule was generating many other undesired cases. 
		builder.append("\n");
		builder.append("[ ?X1, ?R, ?Y ] :- [ ?X, ?R, ?Y ], [ ?X1,  rdfs:subClassOf, ?X ], [ ?R, rdf:type, owl:ObjectProperty ], [ ?X, rdf:type, owl:Class ], [ ?X1, rdf:type, owl:Class ], [ ?Y, rdf:type, owl:Class ] .");
		builder.append("\n");
		builder.append("[ ?X1, ?R, ?Y ] :- [ ?X, ?R, ?Y ], [ ?X1,  rdfs:subClassOf, ?X ], [ ?R, rdf:type, owl:DatatypeProperty ], [ ?X, rdf:type, owl:Class ], [ ?X1, rdf:type, owl:Class ], [ ?Y, rdf:type, sirius:Facet ] .");
		builder.append("\n");
		builder.append("[ ?X1, ann:hidden, ?Y ] :- [ ?X, ann:hidden, ?Y ], [ ?X1,  rdfs:subClassOf, ?X ], [ ?X, rdf:type, owl:Class ], [ ?X1, rdf:type, owl:Class ] .");
		//Hidden propagation		
	}
	
	
	protected void getTextForBottomUpPropagationRules(StringBuilder builder){
						
		//NOTE that ?R must not be a subclassof, an inverse or an annotation. It must be an object property or data type property only
		builder.append("\n");
		builder.append("[ ?X1, ?R, ?Y ] :- [ ?X, ?R, ?Y ], [ ?X,  rdfs:subClassOf, ?X1 ], [ ?R, rdf:type, owl:ObjectProperty ], [ ?X, rdf:type, owl:Class ], [ ?X1, rdf:type, owl:Class ], [ ?Y, rdf:type, owl:Class ] .");
		builder.append("\n");
		builder.append("[ ?X1, ?R, ?Y ] :- [ ?X, ?R, ?Y ], [ ?X,  rdfs:subClassOf, ?X1 ], [ ?R, rdf:type, owl:DatatypeProperty ], [ ?X, rdf:type, owl:Class ], [ ?X1, rdf:type, owl:Class ], [ ?Y, rdf:type, sirius:Facet ] .");
		//builder.append("[ ?X1, ?R, ?Y ] :- [ ?X, ?R, ?Y ], [ ?X,  rdfs:subClassOf, ?X1 ] .");
				
		
	}
	
	
	
	protected void getTextForTopBottomPropagationRulesInRange(StringBuilder builder){		
		
		//We also restrict by type to avoid the application of the rule for other type of properties like subclassof, inverseof or annotations. 
		//We also restrict the type of the subject and object, otherwise the rule was generating many other undesired cases. 
		builder.append("\n");
		builder.append("[ ?X, ?R, ?Y1 ] :- [ ?X, ?R, ?Y ], [ ?Y1,  rdfs:subClassOf, ?Y ], [ ?R, rdf:type, owl:ObjectProperty ], [ ?X, rdf:type, owl:Class ], [ ?Y, rdf:type, owl:Class ], [ ?Y1, rdf:type, owl:Class ] .");
			
	}
	
	
	
	protected void getTextForRangeDomainPropagationRules(StringBuilder builder){	
		
		//When properties only contain domain and range, or appear in restriction with Top as filler. the following triples will be created "A R Top" and "Top R B". In VQS one would expect to see suggest "A R B"  
		//Same for data properties with facets: "A R rdfs:Literal" and "T R string" -> "A R string" 
		builder.append("\n");
		builder.append("[ ?X, ?R, ?Y ] :- [ ?X, ?R, owl:Thing ], [ owl:Thing, ?R, ?Y ], [ ?R, rdf:type, owl:ObjectProperty ], [ ?X, rdf:type, owl:Class ], [ ?Y, rdf:type, owl:Class ] .");
		builder.append("\n");
		
		
		builder.append("[ ?X, ?R, ?F2 ] :- [ ?X, ?R, ?F1 ], [ owl:Thing, ?R, ?F2 ], [ ?R, rdf:type, owl:DatatypeProperty ], [ ?X, rdf:type, owl:Class ], [ ?F1, rdf:type, sirius:Facet ], [ ?F2, rdf:type, sirius:Facet ] .");
		//We propagate always even though the datatype of the facet F1 is not Top datatype literal: [ ?F1, rdfs:range, rdfs:Literal ]
		//The range may bring list of values and other relevant general restrictions.
		
	}
	
	
	protected void getTextForPrefixes(StringBuilder builder){
		builder.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>");
		builder.append("\n");
		builder.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		builder.append("\n");
		builder.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
		builder.append("\n");
		builder.append("PREFIX ann: <http://no.sirius.ontology/annotations#>");
		builder.append("\n");
		builder.append("PREFIX sirius: <http://no.sirius.ontology/graphp#>");
		builder.append("\n\n");
	}
	
	
	
	
	protected void getTextForEqualityRules(StringBuilder builder){
		
		//StringBuilder builder = new StringBuilder(); 
		
		builder.append("[ ?X, owl:sameAs, ?X ] :- [ ?X, ?Y, ?Z ] .");
		builder.append("\n");
		builder.append("[ ?Y, owl:sameAs, ?Y ] :- [ ?X, ?Y, ?Z ] .");
		builder.append("\n");
		builder.append("[ ?Z, owl:sameAs, ?Z ] :- [ ?X, ?Y, ?Z ] .");
		builder.append("\n");
		builder.append("[ ?X, ?Y, ?Z ] :- [ ?X1, ?Y, ?Z ], [ ?X1, owl:sameAs, ?X ] .");
		builder.append("\n");
		builder.append("[ ?X, ?Y, ?Z ] :- [ ?X, ?Y1, ?Z ], [ ?Y1, owl:sameAs, ?Y ] .");
		builder.append("\n");
		builder.append("[ ?X, ?Y, ?Z ] :- [ ?X, ?Y, ?Z1 ], [ ?Z1, owl:sameAs, ?Z ] .");
		builder.append("\n");
		builder.append("[ ?X, rdf:type, owl:Nothing ] :- [ ?X, owl:differentFrom, ?X] .");
		
		
		//TODO Do we need equality rules for new symbols like sirius:Facet?
		
		//return builder.toString();
	}
	
	
	protected Concept createNothing() {
		return createConcept("http://www.w3.org/2002/07/owl#Nothing");
	}
	
	
	public Concept createConcept(String e_uri){
		Concept c = new Concept(e_uri);
		
		updateStandardEntityAttributes(c, e_uri);		
		
		return c;
	}
	
	
	public Instance createInstance(String e_uri){
		return createInstance(e_uri, "");
	}
	/**
	 * 
	 * @param e_uri
	 * @param type General type (optional)
	 * @return
	 */
	public Instance createInstance(String e_uri, String type){
		Instance i = new Instance(e_uri);
				
		//Get most concrete type
		String concrete_type = getMostConcreteTypeForInstance(e_uri);
		if (!concrete_type.equals(""))
			i.setClassType(concrete_type);
		else if(!type.equals(""))
			i.setClassType(type);
		else
			i.setClassType(URIUtils.OWL_THING);
				
		
		updateStandardEntityAttributes(i, e_uri);		
		
		return i;
	}
	
	
	public ObjectProperty createObjectPropery(String e_uri) {
		
		ObjectProperty op = new ObjectProperty(e_uri);
				
		updateStandardEntityAttributes(op, e_uri);		
		
		return op;
	}
	
	
	public DataProperty createDataPropery(String e_uri) {
		
		DataProperty dp = new DataProperty(e_uri);
				
		updateStandardEntityAttributes(dp, e_uri);		
		
		return dp;
	}
	
	
	protected void updateStandardEntityAttributes(Entity en, String e_uri) {
		
		try {
		
			Set<String> labels = getQueryResultsArityOne(getSPARQLQueryEntityLabels(e_uri));
			for (String label : labels){
				en.addSynonym(label);
				en.setLabel(label); //any of them
			}
			
			Set<String> comments = getQueryResultsArityOne(getSPARQLQueryEntityComments(e_uri));
			for (String desc : comments){
				//We keep only one
				if (!desc.equals("")){
					en.setDescription(desc);
					break;
				}
			}
			//If there is a hidden entry
			if (getNumberOfTuplesQueryResults(getSPARQLQueryEntityHidden(e_uri))>0){
				en.setHidden(true);
			}
		}
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public String getMostConcreteTypeForInstance(String e_uri) {
		
		try {
		
			//TODO Get explicit type if stored: in principle at most one in the use cases
			Set<String> types = getQueryResultsArityOne(getSPARQLQueryDirectTypesForInstance(e_uri));
			for (String type: types)
				return type;
			
			
			//In case no direct type defined
			return getMostSpecifictType(getQueryResultsArityOne(getSPARQLQueryTypesForInstance(e_uri)));
			
		}
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		return "";
		
	}
	
	
	
	@Override
	public TreeSet<Concept> getCoreConcepts() {
		return getCoreConcepts("");
	}
	
	
	@Override
	public TreeSet<Concept> getCoreConcepts(String filterByNameSpace) {
		
		//Alphabetical
		TreeSet<Concept> ordered_concepts = new TreeSet<Concept>();
		
		//Query all concepts
		try {
			
			Set<String> concepts = getQueryResultsArityOne(getSPARQLQueryCoreConcepts());
			
			for (String e_uri : concepts){
				
				if (filterByNameSpace.equals("") || !e_uri.startsWith(filterByNameSpace)) {
					
					Concept c = createConcept(e_uri);
					
					//Filter hidden concepts that we do not want to show to the user
					if (!c.isHidden())
						ordered_concepts.add(c);
					
					//ordered_concepts.add(createConcept(e_uri));
				}
				
				
			}
							
			//for (Concept c : ordered_concepts.descendingSet()){
			//	Utility.println(c.toString());
			//}
			
			return ordered_concepts;
			
			
			
			
		} catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		return ordered_concepts;
	}
	
	@Override
	public TreeSet<DataProperty> getDataPredicates() {
		
		//Alphabetical
		TreeSet<DataProperty> ordered_predicates = new TreeSet<DataProperty>();
		
		//Query all data properties
		try {
			
			Set<String> predicates = getQueryResultsArityOne(getSPARQLQueryDataPredicates());
			
			for (String e_uri : predicates){
				
				ordered_predicates.add(createDataPropery(e_uri));
				
			}
			
			return ordered_predicates;
			
			
			
			
		} catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		return ordered_predicates;
	}
	
	
	
	
	@Override
	public TreeSet<ObjectProperty> getObjectPredicates() {
		
		//Alphabetical
		TreeSet<ObjectProperty> ordered_predicates = new TreeSet<ObjectProperty>();
		
		//Query all data properties
		try {
			
			Set<String> predicates = getQueryResultsArityOne(getSPARQLQueryObjectPredicates());
			
			for (String e_uri : predicates){
				
				ordered_predicates.add(createObjectPropery(e_uri));
				
			}
			
			return ordered_predicates;
			
			
			
			
		} catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		return ordered_predicates;
	}
	
	
	
	
	@Override
	public TreeSet<Instance> getInstances() {
		
		return getInstancesForType(URIUtils.OWL_THING);
	}
	
	
	
	
	
	@Override
	public Map<String, Set<String>> getObjectRelationhipsForSubject(String subject) {
		
		
		try {
		
			return getQueryResultsArityTwo(getSPARQLQueryObjectPropertyTriplesForSubject(subject));
			
		}
		
		catch (JRDFoxException e) {			
			e.printStackTrace();
		}
		
		return new HashMap<String, Set<String>>();
		
		
	}

	@Override
	public Map<String, Set<String>> getDataRelationhipsForSubject(String subject) {
		try {
			
			return getQueryResultsArityTwo(getSPARQLQueryDataPropertyTriplesForSubject(subject));
			
		}
		
		catch (JRDFoxException e) {			
			e.printStackTrace();
		}
		
		return new HashMap<String, Set<String>>();

	}
	
	
	
	@Override
	public Map<String, Set<GenericValue>> getAllRelationhipsForSubject(String subject) {
		try {
			
			return getQueryResultsArityTwoWithType(getSPARQLQueryAllRelationshipsForSubject(subject));
			
		}
		
		catch (JRDFoxException e) {			
			e.printStackTrace();
		}
		
		return new HashMap<String, Set<GenericValue>>();

	}
	
	
	
	@Override
	public Map<String, Set<String>> getAllRelationhipsForObject(String object) {
		try {
			
			return getQueryResultsArityTwo(getSPARQLQueryAllRelationshipsForObject(object));
			
		}
		
		catch (JRDFoxException e) {			
			e.printStackTrace();
		}
		
		return new HashMap<String, Set<String>>();

	}
	
	
	
	
	
	@Override
	public TreeSet<DataProperty> getAllowedDataPredicatesForSubject(String instance_uri) {
		
		//Alphabetical
		TreeSet<DataProperty> ordered_predicates = new TreeSet<DataProperty>();
		
		try {
		
			//Query all data properties associated to instance (i.e. associated to its type)		
			Set<String> types = getQueryResultsArityOne(getSPARQLQueryTypesForInstance(instance_uri));
			
			for (String concept_uri: types) {//only one expected
			
				if (concept_uri.equals(URIUtils.OWL_THING))
					continue;
			
					
				Set<String> predicates = getQueryResultsArityOne(getSPARQLQueryDataPredicatesForConcept(concept_uri));
					
				for (String p_uri : predicates){
						
					ordered_predicates.add(createDataPropery(p_uri));
						
				}
			}
			
			return ordered_predicates;
			
		
		} catch (JRDFoxException e) {
			e.printStackTrace();
		}
			
		return ordered_predicates;
	}
	
	
	@Override
	public TreeSet<ObjectProperty> getAllowedObjectPredicatesForSubject(String instance_uri) {
		
		//Alphabetical
		TreeSet<ObjectProperty> ordered_predicates = new TreeSet<ObjectProperty>();
		
		
		try {
			//Query all data properties associated to instance (i.e. associated to its type)
			Set<String> types = getQueryResultsArityOne(getSPARQLQueryTypesForInstance(instance_uri));
					
			for (String concept_uri: types) {//only one expected
					
				if (concept_uri.equals(URIUtils.OWL_THING))
					continue;
					
				Set<String> predicates = getQueryResultsArityOne(getSPARQLQueryObjectPredicatesForConcept(concept_uri));
				
				for (String p_uri : predicates){
					
					ordered_predicates.add(createObjectPropery(p_uri));
					
				}
			}
			
			System.out.println(ordered_predicates);
			
			return ordered_predicates;
			
		} catch (JRDFoxException e) {
				e.printStackTrace();
			}
		
		
		return ordered_predicates;
	}
	
	
	
	@Override
	public TreeSet<Instance> getAllowedObjectInstancesForSubjectPredicate(String instance_uri, String predicate_iri) {
		
		//Alphabetical
		TreeSet<Instance> ordered_instances = new TreeSet<Instance>();
		
		
		try {
			//Query all data properties associated to instance (i.e. associated to its type)
			Set<String> types = getQueryResultsArityOne(getSPARQLQueryTypesForInstance(instance_uri));
					
			for (String concept_uri: types) {//only one expected
					
				if (concept_uri.equals(URIUtils.OWL_THING))
					continue;
					
				
				Set<String> concepts = getQueryResultsArityOne(getSPARQLQueryTargetConceptsForConceptPredicate(concept_uri, predicate_iri));
					
				
				for (String target_concept : concepts) {
				
					//if (target_concept.equals(URIUtils.OWLTHING))
					//	continue;
					
					Set<String> instances = getQueryResultsArityOne(getSPARQLQueryInstancesFortype(target_concept));
					
					for (String i_uri : instances) {
						ordered_instances.add(createInstance(i_uri));
					}
					
				}
			}
			
			return ordered_instances;
			
		} catch (JRDFoxException e) {
				e.printStackTrace();
			}
		
		
		return ordered_instances;
	}
	
	
	
	
	
	

	@Override
	public TreeSet<NeighbourLink> getNeighbourConcepts(String conceptURI) {
		
		//Take into account implicit inverses in an additional SPARQL query 
				
		TreeSet<NeighbourLink> ordered_links = new TreeSet<NeighbourLink>();
		
		try {
			
			//DONE Filter if entries with Top. If R.B and R.T then keep only R.B. 
			//1. Top is not shown as it is not stored as owl:class in the tripelstore
			//2. We have now a rule to merge < A R T> (e.g. triple result of a property domain axiom) with <T R B> (e.g. triple result of a property range axiom) into <A R B>
			
			Map<String, Set<String>> map_links = getQueryResultsArityTwo(getSPARQLQueryEntityLinks(conceptURI));
						
			for (String p : map_links.keySet()){
				
				//Utility.println(p);
				ObjectProperty op = createObjectPropery(p);
				
				for (String o : map_links.get(p)){
					//Utility.println("\t" + o);
										
					NeighbourLink l = new NeighbourLink();
					//We do not need to clone. The links can use the same ObjectProperty reference.
					//l.setProperty(op.clone().asObjectProperty());
					l.setProperty(op);
					l.setTarget(createConcept(o));
					l.setInversePropertyLink(false);
					
					ordered_links.add(l.clone());
					
				}
			}
			
			
			//Create special neighbour link with inverse flag to true
			Map<String, Set<String>> map_inverse_links = getQueryResultsArityTwo(getSPARQLQueryInverseEntityLinks(conceptURI));
			
			Set<String> prop_with_invserse = getQueryResultsArityOne(getSPARQLQueryPropertiesWithInverse());
			
			for (String p : map_inverse_links.keySet()){
				// TODO: Dag does not understand why this check is necessary
				if (prop_with_invserse.contains(p)){
					
					//Utility.println(p + "-inverse");
					ObjectProperty opinv = createObjectPropery(p);
					
					for (String o : map_inverse_links.get(p)){
						//Utility.println("\t" + o);
						
						NeighbourLink l = new NeighbourLink();
						
						l.setProperty(opinv);
						l.setTarget(createConcept(o));
						l.setInversePropertyLink(true);
						
						ordered_links.add(l.clone());
						
					}
				} // end "if (prop_with_invserse.contains(p)){"
			}
			
			
		
			
		} 
		catch (JRDFoxException e) {			
			e.printStackTrace();
		}
		
		return ordered_links;
	}

	@Override
	public TreeSet<Facet> getConceptFacets(String conceptURI) {
				
		TreeSet<Facet> ordered_facets = new TreeSet<Facet>();
		
		
		try {
		
			Map<String, Set<String>> map_facets = getQueryResultsArityTwo(getSPARQLQueryEntityFacets(conceptURI));
			
			
			
			//Pairs data property - facet
			for (String p : map_facets.keySet()){
				
				//Utility.println(p);			
				DataProperty op = createDataPropery(p);
				Facet facet;
				
				
				double min_value=0;
				double max_value=-1;
				TreeSet<String> ordered_values = new TreeSet<String>();
				FacetType type_facet = FacetType.DEFAULT;
				String datatype = RDFS.LITERAL.toString();
				
				
				//0. Top datatype is rdfs:Literal
				//1. If more than one facet: keep only one. Merge values. Merge min and max. As long as datatype is compatible. Otherwise keep only one.
				//2. There may be some  issues if several non compatible facets. Otherwise they are merged (e.g. merged )
				
				for (String facet_uri : map_facets.get(p)){
					
					//SPARQL queries
					//Get datatype (in principle only one)
					Set<String> datatypes = getQueryResultsArityOne(getSPARQLQueryFacetDataype(facet_uri));
					
					String aux_datatype=RDFS.LITERAL.toString();
					if (datatypes.size()>0)
						aux_datatype = datatypes.iterator().next();
					
					//We only update the datatype if different from top datatype. Important when many facets, we do not want to overwrite more specific datatypes
					if (!aux_datatype.equals(RDFS.LITERAL.toString()))
						datatype = aux_datatype;
						
					
					//If not defined type yet in the other associated facets!
					//if (type_facet==FacetType.DEFAULT){
						
						//if top datatype then continue
						if (datatype.equals(RDFS.LITERAL.toString())){
							type_facet= FacetType.DEFAULT;
						}
						else if (datatype.equals(XMLSchema.BOOLEAN.toString())){
							type_facet= FacetType.BOOLEAN;
						}
						else if (datatype.equals(XMLSchema.DATE.toString()) || datatype.equals(XMLSchema.DATETIME.toString())  || datatype.equals(URIUtils.XSD_DATETIMESTAMP)){
							type_facet= FacetType.DATE;
						}
						else { //others
							type_facet= FacetType.TEXT;
						}
					//}					
					
					
					//Get values assocaited to facet if any
					Set<String> values = getQueryResultsArityOne(getSPARQLQueryFacetValues(facet_uri));					
					Set<String> minValues = getQueryResultsArityOne(getSPARQLQueryFacetMinValue(facet_uri));
					Set<String> maxValues = getQueryResultsArityOne(getSPARQLQueryFacetMaxValue(facet_uri));
					
					//if no associated values then we continue to next facet
					if (values.isEmpty() && minValues.isEmpty() && maxValues.isEmpty()){
						continue;
					}
					
					
					
					
					//-------------------------------------------
					//Scope of facet
					//For values (enum and restrictions) we only propagate top-bottom and keep more specific set of values. The scope is key for that.)
					
					Set<String> scopes = getQueryResultsArityOne(getSPARQLQueryFacetScopes(facet_uri));
					
					if (!scopes.contains(conceptURI)){//If it is contained then, we do as usual
						
						boolean isScopeSuperClass=true;
						
						for (String scope : scopes){
							//Check compatibility with focus: "conceptURI"
							//If conceptURI is "strict" super class of scope(s) then continue
							if (!isSubClassOf(conceptURI, scope)){//only propagate if scope is a superclass
								isScopeSuperClass=false;								
								//Utility.println("SUBCLASSOF " + conceptURI + "  " + scope + "  " + isScopeSuperClass);
								break;//do not check other scopes								
							}
							//else: If conceptURI is a subclass of scope(s) then use values: enum and restrictions							
						}
						if (!isScopeSuperClass) //only propagate if scope is a superclasss
							continue;
					}
					//end scopes
					//-------------------------------------------
					
					
					
					
					
					//Get min max values (in principle only one)
					double aux_min;
					double aux_max;
					
					
					//We keep more restrictive range of values. This is alright since only top-down propagation has effect
					if (minValues.size()>0){
						aux_min = Double.valueOf(minValues.iterator().next());
						if (aux_min > min_value || min_value>max_value) // min_value>max_value: initial values
							min_value = aux_min;
					}
					
					
					if (maxValues.size()>0){
						aux_max = Double.valueOf(maxValues.iterator().next());
						if (aux_max < max_value  || min_value>max_value) // min_value>max_value: initial values
							max_value = aux_max;
					}
					
					//Utility.println(scopes + "  " + conceptURI + "  " + min_value + "  " + max_value);
					
					
					
					//Enumerate values
					if (!values.isEmpty()){
						if (ordered_values.size()>values.size() || ordered_values.isEmpty()){
							//We keep smaller set of allowed values. In line with a top-down propagation
							ordered_values.clear();
							ordered_values.addAll(values);
						}
					}
					
					
					
					
					
															
				}//end iteration over facets
				//---------------------------------------------
				
				//Utility.println("VALUES");
				//Utility.println("\t"+ordered_values);
				//Utility.println("\t"+min_value + " " + max_value);
				
				
				//Priority to list of values: especially when only one value is accepted!
				if (!ordered_values.isEmpty()){
					type_facet= FacetType.SELECT;
				}
				else if (min_value<=max_value){
					type_facet= FacetType.SLIDER;
				}
				
				
				
				
				//Create type of Facet
				//Add (still) missing attributed depending on type of facet
				switch (type_facet) {
	            case DEFAULT:
	                facet = new DefaultFacet();
	                break;
	                    
	            case TEXT:
	            	facet = new TextFacet();
	            	facet.setDatatype(datatype);
	            	break;
	                         
	            case BOOLEAN:
	            	facet = new BooleanFacet();
	                break;
	                       
	            case DATE:
	            	facet = new DateFacet();
	            	facet.setDatatype(datatype);
	            	break;
	            case SLIDER:
	            	facet = new SliderFacet(String.valueOf(min_value), String.valueOf(max_value));
	            	facet.setDatatype(datatype);
	            	break;
	            case SELECT:
	            	facet = new SelectFacet(ordered_values);
	            	facet.setDatatype(datatype);
	            	break;
	            default:
	            	facet = new DefaultFacet();
	                break;
				}				
				
				facet.setProperty(op);
				
			
				ordered_facets.add(facet);
				
			}	
		
		} 
		catch (JRDFoxException e) {			
			e.printStackTrace();
		}
		
		return ordered_facets;
	}
	
	
	@Override
	public TreeSet<Concept> getAllSubClasses(String conceptURI){
		
		TreeSet<Concept> ordered_concepts = new TreeSet<Concept>();
		
		
		try {
			Set<String> subclasses = getQueryResultsArityOne(getSPARQLQueryDirectSubclasses(conceptURI));
			
			for (String subclass : subclasses){
				ordered_concepts.add(createConcept(subclass));
				ordered_concepts.addAll(getAllSubClasses(subclass));
				
			}
		} 
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		
		
		return ordered_concepts;

	}
	
	
	@Override
	public TreeSet<Concept> getDirectSubClasses(String conceptURI){
		
		TreeSet<Concept> ordered_concepts = new TreeSet<Concept>();
		
		
		try {
			Set<String> subclasses = getQueryResultsArityOne(getSPARQLQueryDirectSubclasses(conceptURI));
			
			for (String subclass : subclasses){
				ordered_concepts.add(createConcept(subclass));
			}
		} 
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		
		
		return ordered_concepts;
	}
	
	
	@Override
	public TreeSet<Concept> getDirectSuperClasses(String conceptURI){
		
		TreeSet<Concept> ordered_concepts = new TreeSet<Concept>();
		
		try {
			Set<String> superclasses = getQueryResultsArityOne(getSPARQLQueryDirectSuperclasses(conceptURI));
			
			for (String superclass : superclasses){
				ordered_concepts.add(createConcept(superclass));
			}
		} 
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		
		
		return ordered_concepts;
	}
	
	
	
	@Override
	public TreeSet<Concept> getAllSuperClasses(String conceptURI){
		
		TreeSet<Concept> ordered_concepts = new TreeSet<Concept>();
		
		try {
			Set<String> superclasses = getQueryResultsArityOne(getSPARQLQueryDirectSuperclasses(conceptURI));
			
			for (String superclass : superclasses){
				ordered_concepts.add(createConcept(superclass));
				ordered_concepts.addAll(getAllSuperClasses(superclass));
			}
		} 
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		
		
		return ordered_concepts;
	}
	
	
	
	
	@Override
	public TreeSet<Instance> getInstancesForType(String type){ //Uses reasoning to include subtypes
		
		TreeSet<Instance> ordered_instances = new TreeSet<Instance>();
		
		//TODO Does it work if type = OWL:THING?
		
		try {
			Set<String> instances = getQueryResultsArityOne(getSPARQLQueryInstancesFortype(type));
			
			for (String instance_uri : instances){
				ordered_instances.add(createInstance(instance_uri, type));				
			}
		} 
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		
		
		return ordered_instances;
	}
	

	
	
	@Override
	public Set<String> getObjectsForSubjectPredicate(String subject, String predicate){
		
		try {
			return getQueryResultsArityOne(getSPARQLQueryObjectsForSubjectPredicate(subject, predicate));
			
		} 
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		
		
		return new HashSet<String>();
	}
	
	
	
	@Override
	public Set<String> getObjectsForSubjectPredicate(String subject, String predicate, String type_object){
		
		try {
			return getQueryResultsArityOne(getSPARQLQueryObjectsForSubjectPredicate(subject, predicate, type_object));
			
		} 
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		
		
		return new HashSet<String>();
	}
	
	
	
	@Override
	public Set<String> getSubjectsForObjectPredicate(String predicate, String object){
		try {
			return getQueryResultsArityOne(getSPARQLQuerySubjectsForObjectPredicate(predicate, object));
			
		} 
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		
		
		return new HashSet<String>();
	}
	
	
	
	@Override
	public Set<String> getObjectsForPredicate(String predicate){
		try {
			return getQueryResultsArityOne(getSPARQLQueryObjectsForPredicate(predicate));
			
		} 
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		
		
		return new HashSet<String>();
	}
	
	
	
	@Override
	public Set<String> getSubjectsForObjectPredicate(String predicate, String object, String type_object){
		try {
			return getQueryResultsArityOne(getSPARQLQuerySubjectsForObjectPredicate(predicate, object, type_object));
			
		} 
		catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		
		
		return new HashSet<String>();
	}
	
	
	
	
	
	
	/**
	 * For example: return geological image concept
	 * @return
	 */
	public Concept getMainArtefactConcept(){
		
		try {
			
			//Expected to be only one in ontology
			for (String artefact : getQueryResultsArityOne(getSPARQLQueryMainArtefact())){
				
				return createConcept(artefact);
				
			}
			
		} catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		return createNothing();
		
		
	}
	
	
	
	
	

	
	
	
	@Override
	public Concept getConceptForLabel(String label) {
		
		//Default return
		Concept c = new Concept(label);				
		//c.setIri(label);
		
		try {
			
			Map<String, Set<String>> concept2labels = getQueryResultsArityTwo(getSPARQLQueryEntityForLabel());
			
			for (String uri : concept2labels.keySet()){
				for (String c_label : concept2labels.get(uri)){
					if (c_label.equals(label)) {
						c.setIri(uri);
						c.setLabel(label);
						return c;
					}
				}
				
			}
			
		} catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		return c;
		
	}
	
	
	
	
	
	/**
	 * For example: return geological image concept
	 * @return
	 */
	public boolean isPredicateAnObjectProperty(String p_uri){
		
		try {
					
			return (getNumberOfTuplesQueryResults(getSPARQLQueryIsObjectProperty(p_uri))>0);
			
			
		} catch (JRDFoxException e) {
			e.printStackTrace();
		}
		
		return false;
		
		
	}
	
	
	
	
	
	@Override
	public Cursor runSPARQLQuery(String query){
		//TODO Auto-generated method stub
		//General sparql query. We return TreSet of tuples? or TupleIterator? 
		return null;
	}
	
	
	
	
	
	protected Cursor createQueryCursor(String query) throws JRDFoxException {
		Utility.printlnQueryInfo(query);
		
		//TupleIterator tupleIterator;
		//Prefixes prefixes = Prefixes.DEFAULT_IMMUTABLE_INSTANCE;
		
		HashMap<String, String> parameters = new HashMap<String, String>();
	    //parameters.put("domain", queryDomain.toString());
		
		//We use default IDB
		//tupleIterator = store.compileQuery(query, prefixes);
	   return dataStoreConnection.createCursor(Prefixes.s_defaultPrefixes, query, parameters); //s_emptyPrefixes
	}
	
	
	

	
	
	/**
	 * We retrieve set of results for ?x (arity 1) and return them as a Set. Default QueryDomain: IDB (over inferred facts)
	 * @param query
	 * @return Set of values
	 * @throws JRDFoxException
	 
	protected Set<String> getQueryResultsArityOne(String query) throws JRDFoxException{
		
		return getQueryResultsArityOne(query);
	}*/
	
	
	
	
	/**
	 * We retrieve set of results for ?x (arity 1) and return them as a Set
	 * @param query
	 * @param queryDomain IDB (inferred facts) or EDB (explict facts)
	 * @return Set of values
	 * @throws JRDFoxException
	 */
	protected Set<String> getQueryResultsArityOne(String query) throws JRDFoxException{
		
		Set<String> query_results = new HashSet<String>();
		
		Cursor cursor = createQueryCursor(query);
		
		
		int arity = cursor.getArity();
					
		// We iterate trough the result tuples
		for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance()) {
			// We iterate trough the terms of each tuple
					
			
			//for (int termIndex = 0; termIndex < arity; ++termIndex) {
			if (arity==1){
				//System.out.println("Resorce results: " + cursor.getResource(0));
				ResourceValue resource = cursor.getResourceValue(0);				
				query_results.add(resource.m_lexicalForm);
			}				
			//}
			
		}
		
		cursor.close();
		
		Utility.printlnQueryInfo("\t Number of rows: " + query_results.size());
		
		return query_results;
		
	}
	
	
	
	/**
	 * We retrieve set of results for ?x ?y (arity 2) and return them as a HasMap. Default QueryDomain: IDB (over inferred facts)
	 * @param query
	 * @return
	 * @throws JRDFoxException
	 
	protected Map<String, Set<String>> getQueryResultsArityTwo(String query) throws JRDFoxException{
		return getQueryResultsArityTwo(query);
	}*/
	
	
	/**
	 * We retrieve set of results for ?x ?y (arity 2) and return them as a HasMap.
	 * @param query
	 * @param queryDomain IDB (inferred facts) or EDB (explict facts)
	 * @return
	 * @throws JRDFoxException
	 */
	protected Map<String, Set<String>> getQueryResultsArityTwo(String query) throws JRDFoxException{
		
		Map<String, Set<String>> query_results = new HashMap<String, Set<String>>();
		
		Cursor cursor = createQueryCursor(query);
		
		
		int arity = cursor.getArity();
		
		String key;
		
		// We iterate trough the result tuples
		for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance()) {
			// We iterate trough the terms of each tuple
									
			//for (int termIndex = 0; termIndex < arity; ++termIndex) {
			if (arity==2){
				
				//Key
				ResourceValue resource = cursor.getResourceValue(0);
					
				key = resource.m_lexicalForm;
				
				//System.out.println(key);
					
				if (!query_results.containsKey(key)){
					query_results.put(key, new HashSet<String>());
				}
									
				//value
				resource = cursor.getResourceValue(1);
				query_results.get(key).add(resource.m_lexicalForm);
				
				//System.out.println("\t" + resource.m_lexicalForm);
				//System.out.println("\t" + resource.m_datatype.getIRI());
				
				
				
			}
			//}
			
		}
		
		cursor.close();
		
		Utility.printlnQueryInfo("\t Number of rows: " + query_results.size());
		
		return query_results;
		
	}
	
	
	
	
	/**
	 * We retrieve set of results for ?x ?y (arity 2) and return them as a HasMap. We consider the datatype. Default QueryDomain: IDB (over inferred facts) 
	 * @param query
	 * @return
	 * @throws JRDFoxException
	 
	protected Map<String, Set<GenericValue>> getQueryResultsArityTwoWithType(String query) throws JRDFoxException{
		
		return getQueryResultsArityTwoWithType(query;
	}*/
	
	
	/**
	 * We retrieve set of results for ?x ?y (arity 2) and return them as a HasMap. We consider the datatype. 
	 * @param query
	 * @param queryDomain IDB (inferred facts) or EDB (explict facts)
	 * @return
	 * @throws JRDFoxException
	 */
	protected Map<String, Set<GenericValue>> getQueryResultsArityTwoWithType(String query) throws JRDFoxException{
		
		
		Map<String, Set<GenericValue>> query_results = new HashMap<String, Set<GenericValue>>();
		
		Cursor cursor = createQueryCursor(query);
		
		
		int arity = cursor.getArity();
		
		String key;
		
		// We iterate trough the result tuples
		for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance()) {
			// We iterate trough the terms of each tuple
									
			//for (int termIndex = 0; termIndex < arity; ++termIndex) {
			if (arity==2){
				
				//Key
				ResourceValue resource = cursor.getResourceValue(0);
					
				key = resource.m_lexicalForm;
				
				//System.out.println(key);
					
				if (!query_results.containsKey(key)){
					query_results.put(key, new HashSet<GenericValue>());
				}
									
				//value
				resource = cursor.getResourceValue(1);
				query_results.get(key).add(new GenericValue(resource.m_lexicalForm, resource.m_datatype.getIRI().toString()));
				
				//System.out.println("\t" + resource.m_lexicalForm);
				//System.out.println("\t" + resource.m_datatype.getIRI());
				
				
				
			}
			//}
			
		}
		
		cursor.close();
		
		Utility.printlnQueryInfo("\t Number of rows: " + query_results.size());
		
		return query_results;
		
	}
	
	
	
	/**
	 * Retrieves number of tuples. Default QueryDomain: IDB (over inferred facts) 
	 * @param query
	 *  @param queryDomain IDB (inferred facts) or EDB (explict facts)
	 * @return Numbe rof tuples
	 * @throws JRDFoxException
	 
	protected int getNumberOfTuplesQueryResults(String query) throws JRDFoxException{
		return getNumberOfTuplesQueryResults(query);
	}*/
	
	/**
	 * Retrieves number of tuples
	 * @param query
	 * @param queryDomain IDB (inferred facts) or EDB (explict facts)
	 * @return Numbe rof tuples
	 * @throws JRDFoxException
	 */
	protected int getNumberOfTuplesQueryResults(String query) throws JRDFoxException{
		
		int numberOfRows = 0;
		
		Cursor cursor = createQueryCursor(query);
		
		// We iterate trough the result tuples
		for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance()) {
			++numberOfRows;
		}
		
		cursor.close();
		
		return numberOfRows;
		
	}
	
	
	
	
	
	
	
	/**
	 * General method to get results.  Returns number of tuples and prints them. Default QueryDomain: IDB (over inferred facts) 
	 * @param query
	 * @return
	 * @throws JRDFoxException
	 */
	protected int getQueryResults(String query) throws JRDFoxException{
		
		//return getQueryResults(query, store, false, true); //minimal results
		return getQueryResults(query, true, false); //
		
	}
	
	
	
	/**
	 * Returns number of returned tuples. Useful if only intereste din number of violations for example.
	 * @param query
	 * @param queryDomain IDB (inferred facts) or EDB (explict facts)
	 * @param after_materialization
	 * @param print_minimal_rsults
	 * @return
	 * @throws JRDFoxException
	 */
	protected int getQueryResults(String query, boolean after_materialization, boolean print_minimal_rsults) throws JRDFoxException{
	
		Cursor cursor = createQueryCursor(query);

		int numberOfRows;
		
		if (print_minimal_rsults)
			numberOfRows = evaluateAndPrintMinimalResults(cursor);
		else
			numberOfRows = evaluateAndPrintResults(cursor);
		
		
		cursor.close();
		
		return numberOfRows;
	}
	
	
	/**
	 * From RDFox example (for tests)
	 * @param prefixes
	 * @param tupleIterator
	 * @throws JRDFoxException
	 */
	protected int evaluateAndPrintResults(Cursor cursor) throws JRDFoxException {
		int numberOfRows = 0;
		Utility.printlnQueryInfo("");
		Utility.printlnQueryInfo("=======================================================================================");
		int arity = cursor.getArity();
					
		// We iterate trough the result tuples
		for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance()) {
			// We iterate trough the terms of each tuple
									
			for (int termIndex = 0; termIndex < arity; ++termIndex) {
				if (termIndex != 0)
					Utility.printQueryInfo("  ");
				// For each term we get a Resource object that contains the lexical form and the data type of the term.
				// One can also access terms as GroundTerm objects from the tech.oxfordsemantic.jrdfox.model package using the 
				// method TupleIterator.getGroundTerm(int termIndex). Using objects form the tech.oxfordsemantic.jrdfox.model 
				// package has the benefit of ensuring that at any point each term is represented by at most one Java 
				// object. This benefit, however, comes at a price, since, unlike in the case of Resource objects, the 
				// creation of GroundTerm objects involves a hashtable lookup, which in some cases can lead to a 
				// significant overhead. 
				ResourceValue resource = cursor.getResourceValue(termIndex);
				//Utility.print(resource.toString(prefixes));
				//Utility.print(resource.toString());
				try{
				Utility.printQueryInfo(resource.m_lexicalForm.split("#")[1]);
				}
				catch (Exception e){
					Utility.printQueryInfo(resource.m_lexicalForm);
				}
			}
			Utility.printQueryInfo(" * ");
			Utility.printQueryInfo(String.valueOf(multiplicity));
			Utility.printlnQueryInfo("");
			++numberOfRows;
		}
		Utility.printlnQueryInfo("---------------------------------------------------------------------------------------");
		Utility.printlnQueryInfo("  The number of rows returned: " + numberOfRows);
		Utility.printlnQueryInfo("=======================================================================================");
		Utility.printlnQueryInfo("");
		
		return numberOfRows;
	}
	
	
	/**
	 * Reduced print. From RDFox example (for tests)
	 * @param prefixes
	 * @param tupleIterator
	 * @return
	 * @throws JRDFoxException
	 */
	protected int  evaluateAndPrintMinimalResults(Cursor cursor) throws JRDFoxException {
		int numberOfRows = 0;
		Utility.printlnQueryInfo("");
		Utility.printlnQueryInfo("=======================================================================================");
		int arity = cursor.getArity();
		// We iterate trough the result tuples
		for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance()) {
			// We iterate trough the terms of each tuple
			for (int termIndex = 0; termIndex < arity; ++termIndex) {
				//if (termIndex != 0)
					//Utility.print("  ");
				// For each term we get a Resource object that contains the lexical form and the data type of the term.
				// One can also access terms as GroundTerm objects from the tech.oxfordsemantic.jrdfox.model package using the 
				// method TupleIterator.getGroundTerm(int termIndex). Using objects form the tech.oxfordsemantic.jrdfox.model 
				// package has the benefit of ensuring that at any point each term is represented by at most one Java 
				// object. This benefit, however, comes at a price, since, unlike in the case of Resource objects, the 
				// creation of GroundTerm objects involves a hashtable lookup, which in some cases can lead to a 
				// significant overhead. 
				ResourceValue resource = cursor.getResourceValue(termIndex);
			///	Utility.print(resource.toString(prefixes));
			}
			//Utility.print(" * ");
			//Utility.print(multiplicity);
			//print();
			++numberOfRows;
		}
		Utility.printlnQueryInfo("---------------------------------------------------------------------------------------");
		Utility.printlnQueryInfo("  The number of rows returned: " + numberOfRows);
		Utility.printlnQueryInfo("=======================================================================================");
		Utility.printlnQueryInfo("");
		
		return numberOfRows;
	}
	
	
	
	public void dispose(){
		super.dispose();
		
		//store.dispose();
		try {
			
			//ServerConnection serverConnection = ConnectionFactory.newServerConnection("rdfox:local", "", "");
			if(serverConnection.containsDataStore("ontology-projection"))
				serverConnection.deleteDataStore("ontology-projection"); 
			
			dataStoreConnection.clear();
		} catch (JRDFoxException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		dataStoreConnection.close();
		
	}

	@Override
	public String getDataFilePath() {
		return data_file;
	}

	@Override
	public UpdateType getAdditionUpdateType() {
		return UpdateType.ADDITION;
		
	}

	@Override
	public UpdateType getDeletionUpdateType() {
		return UpdateType.DELETION;
		
	}

	
	
	
	
	
	
	
	
}
