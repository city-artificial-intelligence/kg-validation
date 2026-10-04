/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.constraint.controller.rdfox;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;

import tech.oxfordsemantic.jrdfox.Prefixes;
import tech.oxfordsemantic.jrdfox.client.ConnectionFactory;
import tech.oxfordsemantic.jrdfox.client.Cursor;
import tech.oxfordsemantic.jrdfox.client.DataStoreConnection;
//import tech.oxfordsemantic.jrdfox.client.QueryDomain;
import tech.oxfordsemantic.jrdfox.client.ResourceValue;
import tech.oxfordsemantic.jrdfox.client.ServerConnection;
import tech.oxfordsemantic.jrdfox.client.UpdateType;
import tech.oxfordsemantic.jrdfox.exceptions.JRDFoxException;

import uio.ifi.ontology.toolkit.constraint.controller.ConstraintValidator;
import uio.ifi.ontology.toolkit.constraint.utils.Namespace;
import uio.ifi.ontology.toolkit.constraint.utils.Utility;
import uio.ifi.ontology.toolkit.constraint.utils.pagoda_hermit.RuleHelperExtended;
import uio.ifi.ontology.toolkit.constraint.utils.pagoda_hermit.Timer;



/**
 * Constraint validator using the RDFox datalog reasoner
 * @author ernesto
 * Created on 11 Jan 2017
 *
 */
public class RDFoxBasedConstraintValidator extends ConstraintValidator{
	
	//RDFox datastore
	DataStoreConnection dataStoreConnection;
	
	//In RDFox we can clearly differentiate between materialization of axioms and then materialization of constraints.
	protected double axiom_materialization_time;		
	protected long number_triples_materialization_axioms;

	
	
	
	
	/**
	 * The input is one ontology containing both regular TBox axioms, constraints and data.
	 * RDFox accepts the data as an input file so it has to be saved into a file.
	 * @param ontology
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 */
	public RDFoxBasedConstraintValidator(OWLOntology ontology) throws OWLOntologyStorageException, OWLOntologyCreationException{		
		this(ontology, createDataFile(ontology));		
	}
	
	
	/**
	 * The input is an ontology with regular Tbox axioms and constraints/requirements and the data comes in an independent object.
	 * RDFox accepts the data as an input file so it has to be saved into a file.
	 * @param ontology
	 * @param data
	 * @throws OWLOntologyStorageException 
	 * @throws OWLOntologyCreationException 
	 */
	public RDFoxBasedConstraintValidator(OWLOntology ontology, OWLOntology data) throws OWLOntologyCreationException, OWLOntologyStorageException{		
		this(ontology, createDataFile(data));
				
		
	}
	
	/**
	 * Input is given in 3 independent files: requirements ontology, ontology with TBox axioms, and data
	 * RDFox accepts the data as an input file so it has to be saved into a file.
	 * @param req_ontology
	 * @param ontology
	 * @param data
	 * @throws OWLOntologyStorageException 
	 * @throws OWLOntologyCreationException 
	 */
	public RDFoxBasedConstraintValidator(OWLOntology req_ontology, OWLOntology ontology, OWLOntology data) throws OWLOntologyCreationException, OWLOntologyStorageException{		
		this(req_ontology, ontology, createDataFile(data));
		
		
	}
	
	
	/**
	 * 
	 * The input is an ontology with regular Tbox axioms and constraints/requirements. The data comes in an independent file (typically turtle format).
	 * When data is large this is he preferred input for some systems like RDFox, which does not rely on the OWLAPI to parse the data
	 * 
	 * @param ontology
	 * @param file_data
	 * @throws OWLOntologyCreationException 
	 */
	public RDFoxBasedConstraintValidator(OWLOntology ontology, File file_data) throws OWLOntologyCreationException{
		super(ontology, file_data);
		
		//At this point we already have the tbox_ontology, the constraints in visitor and the file_data
		
		
	}
	
	/**
	 * Input is given in 3 independent files: requirements ontology, ontology with TBox axioms and data in an independent file (typically turtle format).
	 * When data is large this is he preferred input for some systems like RDFox, which does not rely on the OWLAPI to parse the data 
	 * 
	 * @param req_ontology
	 * @param ontology
	 * @param file_data
	 * @throws OWLOntologyCreationException 
	 */
	public RDFoxBasedConstraintValidator(OWLOntology req_ontology, OWLOntology ontology, File file_data) throws OWLOntologyCreationException{
		super(req_ontology, ontology, file_data);		
		
		//At this point we already have the tbox_ontology or ontology, the constraints in visitor and the file_data		
		
	}
	
	
	
	
	protected static File createDataFile(OWLOntology o) throws OWLOntologyStorageException, OWLOntologyCreationException{
		//we save data in turtle format so that RDFox can read it. SO far is the ony way RDFox can read data
		File file_data = new File(Utility.tmp_directory + "Abox_data.ttl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology aboxOntology = manager.createOntology(o.getABoxAxioms(Imports.INCLUDED), IRI.create("http://somm.cs.ox.ac.uk/ontology/ABoxAxioms.owl"));
		manager.saveOntology(aboxOntology, new TurtleDocumentFormat(), IRI.create(file_data));
		return file_data;
	}
	
	
	@Override
	public void performDataConstraintValidation() throws JRDFoxException, URISyntaxException{
		
		//First stage
		performMaterialization(tbox_ontology, file_data);		
		
					
		//Second stage (same store)
		String text_constraint_rules = RuleHelperExtended.getTextAndPrefixes(constraint_creator.getDataConstraintsClauses());
		Utility.println(text_constraint_rules);
		checkDataConstraints(text_constraint_rules);
		
		//Utility.print(RuleHelperExtended.getTextAndPrefixes(constraint_creator.getDataConstraintsClauses()));
		
		
				
		dataStoreConnection.clear();
		dataStoreConnection.close();
		
	}
	
	
	@Override
	protected void setConstraintCreator() {		
		constraint_creator = new ConstraintCreatorForRDFox();
	}
	
	

	
	
	
	
	
	protected String getTextForEqualityRules(){
		
		StringBuilder builder = new StringBuilder(); 
		builder.append("PREFIX owl: <http://www.w3.org/2002/07/owl#>");
		builder.append("\n");
		builder.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		builder.append("\n");
		builder.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
		builder.append("\n\n");
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
		
		//Utility.print(builder.toString());
		return builder.toString();
	}
	
	
	
	protected void initializeRDFox() throws JRDFoxException {
		
		//Local server
		ServerConnection serverConnection = ConnectionFactory.newServerConnection("rdfox:local", "", "");
		
		// We create a data store of type "par-complex-nn".
		//Used ParallelSimpleNN before
		//https://oxfordsemtech.github.io/RDFox/#/04-using?id=data-store-type
        serverConnection.createDataStore("ontology-projection", "par-complex-nn", Collections.emptyMap());
		
        // We connect to the data store.
		dataStoreConnection = serverConnection.newDataStoreConnection("ontology-projection");
		
		//Setting the number of threads...TODO: Not available anymore?
        //dataStoreConnection.setNumberOfThreads(8); //This could be a parameter
		
		
	}
	
	
	
	
	
	protected static long getTriplesCount(DataStoreConnection dataStoreConnection) throws JRDFoxException {
        HashMap<String, String> parameters = new HashMap<String, String>();
        //parameters.put("domain", queryDomain.toString()); //TODO: Change to string "IDB"??
        try (Cursor cursor = dataStoreConnection.createCursor(Prefixes.s_emptyPrefixes, "SELECT ?X ?Y ?Z WHERE{ ?X ?Y ?Z }", parameters)) {
            long result = 0;
            for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance())
                result += multiplicity;
            // Since the iterator is exhausted, it does not need to be closed.
            return result;
        }
    }
	
	
	/**
	 * First stage
	 * @param o
	 * @param tuples_file
	 * @throws JRDFoxException
	 * @throws URISyntaxException
	 */
	protected void performMaterialization(OWLOntology o, File tuples_file) throws JRDFoxException, URISyntaxException{
		
		//Initialize connection to RDFox
		initializeRDFox();
		
		try{			
			
			
			Timer t = new Timer();
			Utility.println("Importing Ontology...");
			//store.importOntology(o);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			o.saveOntology(new TurtleDocumentFormat(), out);
			dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, out.toString()); //TODO: Old parameters?? true, true, false, false);
			
						
			Utility.println("Importing RDF data...");
			//store.importFiles(new File[] {tuples_file});
			InputStream fis = new  FileInputStream(tuples_file);
			InputStream inputStream = new BufferedInputStream(fis);
            dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, inputStream);
            
			number_initial_triples = getTriplesCount(dataStoreConnection);
			Utility.println("Number of tuples after import: " + number_initial_triples);
			
			
			Utility.println("Importing equality rules...");			
			//File equality_file = new File(Constants.working_directory + "equality.dlog");
			//store.importFiles(new File[] {equality_file});
			//store.importText(getTextForEqualityRules());
			dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, getTextForEqualityRules());
			Utility.println("Importing time RDFox: " + t.durationMilisecons()  + " (ms)");
			
			
			
			Utility.println("Materialization...");
			t = new Timer();
			//store.applyReasoning();
			dataStoreConnection.updateMaterialization();
			
			//Utility.print("Materialization time RDFox: " + t.duration()  + " (s)");
			axiom_materialization_time = t.durationMilisecons();
			Utility.println("Materialization time RDFox: " + axiom_materialization_time  + " (ms)");

			number_triples_materialization_axioms = getTriplesCount(dataStoreConnection);
			Utility.println("Number of tuples after materialization: " + number_triples_materialization_axioms);
			
				
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	
	/**
	 * Second stage
	 * @param text_rules
	 * @throws URISyntaxException 
	 * @throws JRDFoxException 
	 */
	protected void checkDataConstraints(String text_rules) throws JRDFoxException, URISyntaxException{
		
		//Use same store
		
		Timer t = new Timer();
		
		
		//store.clearRulesAndMakeFactsExplicit();
		dataStoreConnection.clearRulesAxiomsExplicateFacts();//Important to clear rules
		
		Utility.println("Adding rules from code...");
		//store.importText(text_rules);
		dataStoreConnection.importData(UpdateType.ADDITION, Prefixes.s_emptyPrefixes, text_rules);
		
		
		
		//store.applyReasoning();
		dataStoreConnection.updateMaterialization();
		
		number_triples_after_validation = getTriplesCount(dataStoreConnection);
		Utility.println("Number of tuples after constraint checking: " + number_triples_after_validation);
		
		//Query
		//getViolations();
		boolean show_results=true;
		if (show_results){
			Utility.println("Checking data constraints...");
						
			extractViolations();
			
			//Utility.print("\n\n\n");
			
			extractInvalidIndividuals();
		
			//Utility.print("Constraint Validation time RDFox: " + t.duration() + " (s)");
			validation_time = t.durationMilisecons();
			Utility.println("Constraint Validation time RDFox: " + validation_time  + " (ms)");
			
			
		}
		
		
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	protected void extractViolations() throws JRDFoxException{			
		number_violations = extractMinCardinalilyViolations();
		number_violations+= extractMaxCardinalilyViolations();
		number_violations+= extractRangeViolations();
	}
		
		
	protected int extractMinCardinalilyViolations() throws JRDFoxException{
		 //return getQueryResults("SELECT DISTINCT ?x ?y WHERE{ ?x <http://somm.cs.ox.ac.uk/constraints/newatoms#MinCardViolation> ?y }");
		return getQueryResults("SELECT DISTINCT ?x ?y WHERE{ ?x <" + Namespace.min_violation_iri + "> ?y }");
	}
		
	protected int extractMaxCardinalilyViolations() throws JRDFoxException{
		//return getQueryResults("SELECT DISTINCT ?x ?y WHERE{ ?x <http://somm.cs.ox.ac.uk/constraints/newatoms#MaxCardViolation> ?y }");
		return getQueryResults("SELECT DISTINCT ?x ?y WHERE{ ?x <" + Namespace.max_violation_iri + "> ?y }");
		
	}
	
	protected int extractRangeViolations() throws JRDFoxException{		
		return getQueryResults("SELECT DISTINCT ?x ?y WHERE{ ?x <" + Namespace.range_violation_iri + "> ?y }");
		
	}
			
	
	protected void extractInvalidIndividuals() throws JRDFoxException{
		number_invalid_individuals = getQueryResults("SELECT DISTINCT ?x WHERE{ ?x rdf:type owl:Nothing }");
	}

	
	
	
	
	
	
	
	protected Cursor createQueryCursor(String query) throws JRDFoxException {
		Utility.printlnQueryInfo(query);
		
		//TupleIterator tupleIterator;
		//Prefixes prefixes = Prefixes.DEFAULT_IMMUTABLE_INSTANCE;
		
		HashMap<String, String> parameters = new HashMap<String, String>();
	    //parameters.put("domain", queryDomain.toString());
		
		//We use default IDB
		//tupleIterator = store.compileQuery(query, prefixes);
	   return dataStoreConnection.createCursor(Prefixes.s_defaultPrefixes, query, parameters);
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
	
	
	
	
	
	
	protected int getQueryResults(String query, boolean is_user_query, boolean print_minimal_rsults) throws JRDFoxException{
	
		Cursor cursor = createQueryCursor(query);

		int numberOfRows;
		
		if (print_minimal_rsults)
			numberOfRows = evaluateAndPrintMinimalResults(Prefixes.s_defaultPrefixes, cursor);
		else
			numberOfRows = evaluateAndPrintResults(Prefixes.s_defaultPrefixes, cursor);
		
		
		cursor.close();
		
		return numberOfRows;
	}
	
	
	/**
	 * From RDFox example
	 * @param prefixes
	 * @param tupleIterator
	 * @throws JRDFoxException
	 */
	protected int evaluateAndPrintResults(Prefixes prefixes, Cursor cursor) throws JRDFoxException {
		int numberOfRows = 0;
		Utility.println("");
		Utility.println("=======================================================================================");
		int arity = cursor.getArity();
		// We iterate trough the result tuples
		for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance()) { //.getNext()
			// We iterate trough the terms of each tuple
			for (int termIndex = 0; termIndex < arity; ++termIndex) {
				if (termIndex != 0)
					System.out.print("  ");
				// For each term we get a Resource object that contains the lexical form and the data type of the term.
				// One can also access terms as GroundTerm objects from the tech.oxfordsemantic.jrdfox.model package using the 
				// method TupleIterator.getGroundTerm(int termIndex). Using objects form the tech.oxfordsemantic.jrdfox.model 
				// package has the benefit of ensuring that at any point each term is represented by at most one Java 
				// object. This benefit, however, comes at a price, since, unlike in the case of Resource objects, the 
				// creation of GroundTerm objects involves a hashtable lookup, which in some cases can lead to a 
				// significant overhead. 
				ResourceValue resource = cursor.getResourceValue(termIndex);
				System.out.print(resource.toString(prefixes));
			}
			System.out.print(" * ");
			System.out.print(multiplicity);
			Utility.println("");
			++numberOfRows;
		}
		Utility.println("---------------------------------------------------------------------------------------");
		Utility.println("  The number of rows returned: " + numberOfRows);
		Utility.println("=======================================================================================");
		Utility.println("");
		
		return numberOfRows;
	}
	
	
	protected int  evaluateAndPrintMinimalResults(Prefixes prefixes, Cursor cursor) throws JRDFoxException {
		int numberOfRows = 0;
		Utility.println("");
		Utility.println("=======================================================================================");
		int arity = cursor.getArity();
		// We iterate trough the result tuples
		for (long multiplicity = cursor.open(); multiplicity != 0; multiplicity = cursor.advance()) { //.getNext()
			// We iterate trough the terms of each tuple
			for (int termIndex = 0; termIndex < arity; ++termIndex) {
				//if (termIndex != 0)
					//System.out.print("  ");
				// For each term we get a Resource object that contains the lexical form and the data type of the term.
				// One can also access terms as GroundTerm objects from the tech.oxfordsemantic.jrdfox.model package using the 
				// method TupleIterator.getGroundTerm(int termIndex). Using objects form the tech.oxfordsemantic.jrdfox.model 
				// package has the benefit of ensuring that at any point each term is represented by at most one Java 
				// object. This benefit, however, comes at a price, since, unlike in the case of Resource objects, the 
				// creation of GroundTerm objects involves a hashtable lookup, which in some cases can lead to a 
				// significant overhead. 
				ResourceValue resource = cursor.getResourceValue(termIndex);
			///	System.out.print(resource.toString(prefixes));
			}
			//System.out.print(" * ");
			//System.out.print(multiplicity);
			//print();
			++numberOfRows;
		}
		Utility.println("---------------------------------------------------------------------------------------");
		Utility.println("  The number of rows returned: " + numberOfRows);
		Utility.println("=======================================================================================");
		Utility.println("");
		
		return numberOfRows;
	}


	@Override
	public void printStatistics() {
		System.out.println(
				"Tripels" + "\t" +
				"Mat Trpls Axioms" + "\t" +
				"Mat Trpls Validation" + "\t" +
				"Mat. (ms)" + "\t" +
				"Validation (ms)" + "\t" +
				"Viol." + "\t" +
				 "Invalid"
		);
		
		String results = 
				number_initial_triples + "\t" +
				number_triples_materialization_axioms + "\t" +
				number_triples_after_validation + "\t" +
				axiom_materialization_time + "\t" +
				validation_time + "\t" +				
				number_violations + "\t" +
				number_invalid_individuals + "\n";
		
		System.out.println(results);
		
	}
	
	
	
	
	
}
