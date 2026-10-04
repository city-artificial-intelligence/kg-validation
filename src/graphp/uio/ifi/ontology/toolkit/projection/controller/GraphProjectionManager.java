/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.rdf4j.model.Model;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;

import tech.oxfordsemantic.jrdfox.exceptions.JRDFoxException;
import uio.ifi.ontology.toolkit.constraint.utils.Utility;
import uio.ifi.ontology.toolkit.constraint.utils.pagoda_hermit.Timer;
import uio.ifi.ontology.toolkit.projection.controller.reasoner.ReasonerManager;
import uio.ifi.ontology.toolkit.projection.controller.reasoner.ReasonerManager.OWL2Reasoner;
import uio.ifi.ontology.toolkit.projection.model.Facet;
import uio.ifi.ontology.toolkit.projection.model.GraphProjection;
import uio.ifi.ontology.toolkit.projection.model.NeighbourLink;
import uio.ifi.ontology.toolkit.projection.model.entities.Concept;
import uio.ifi.ontology.toolkit.projection.model.entities.DataProperty;
import uio.ifi.ontology.toolkit.projection.model.entities.GenericValue;
import uio.ifi.ontology.toolkit.projection.model.entities.Instance;
import uio.ifi.ontology.toolkit.projection.model.entities.ObjectProperty;
import uio.ifi.ontology.toolkit.projection.model.triples.DataPropertyTriple;
import uio.ifi.ontology.toolkit.projection.model.triples.ObjectPropertyTriple;
import uio.ifi.ontology.toolkit.projection.model.triples.Triple;
import uio.ifi.ontology.toolkit.projection.utils.URIUtils;



/**
 * 
 * This class will manage the creation of the projection and reply to the 
 * required questions about the projection.
 * 
 *
 * @author ernesto
 * Created on 5 Jan 2017
 * @param <T>
 *
 */
public abstract class GraphProjectionManager<T, S> {
	
	//Methods to manipulate the graph: e.g.: extract neighbours, facets, classes etc.
	
	//Gives to Graph Projector the classification (told and inferred), inverses, etc.
	
	private OWLOntology ontology;
	
	private OWLOntology classified_ontology;	
	
	//private Reasoner hermit;
	private OWLReasoner reasoner;
	
	//private GraphProjector projector;
	
	private GraphProjection graph;
	
	private OWLDataFactory dataFactory;
	
	private OWL2Reasoner reasonerID;
	
	public GraphProjectionManager(String ontology_iri, boolean create_facets) throws OWLOntologyCreationException{		
		this(OWLManager.createConcurrentOWLOntologyManager().loadOntology(IRI.create(ontology_iri)), OWL2Reasoner.HERMIT, create_facets);
	}
	
	public GraphProjectionManager(String ontology_iri, OWL2Reasoner reasonerID, boolean create_facets) throws OWLOntologyCreationException{		
		this(OWLManager.createConcurrentOWLOntologyManager().loadOntology(IRI.create(ontology_iri)), reasonerID, create_facets);
	}
	
	
	public GraphProjectionManager(Set<String> ontology_iris, boolean create_facets) throws OWLOntologyCreationException{		
		this(mergeOntologies(ontology_iris), OWL2Reasoner.HERMIT, create_facets);
	}
	
	public GraphProjectionManager(Set<String> ontology_iris, OWL2Reasoner reasonerID, boolean create_facets) throws OWLOntologyCreationException{		
		this(mergeOntologies(ontology_iris), reasonerID, create_facets);
	}
	
	
	public GraphProjectionManager(OWLOntology ontology, OWL2Reasoner reasonerID, boolean create_facets){
	
		this.dataFactory = OWLManager.createConcurrentOWLOntologyManager().getOWLDataFactory();
		
		this.ontology = ontology;
		
		graph = new GraphProjection();
		
		//We project ontology
		new GraphProjector(graph, ontology, create_facets);
		
		this.reasonerID = reasonerID;
		
		
		if (performClassification()) {
			
			
			Timer t = new Timer();
			
			//This may avoid the exception due to datatypes outside OWL 2 specification
			
			reasoner = ReasonerManager.getReasoner(ontology, reasonerID);
			
			
			createClasssifiedOntology();
			Utility.println("Done in " + t.durationMilisecons() + " (ms)");
			
			//We optionally project the classification
			new GraphProjector(graph, classified_ontology, create_facets);
		
		}
		
		
		
	}
	
	
	
	private static OWLOntology mergeOntologies(Set<String> ontology_iris) throws OWLOntologyCreationException {
		
		Set<OWLAxiom> axioms =  new HashSet<OWLAxiom>();
		
		for (String ontology_iri: ontology_iris) {	
			axioms.addAll(OWLManager.createConcurrentOWLOntologyManager().loadOntology(IRI.create(ontology_iri)).getAxioms());
		}
		
		return OWLManager.createConcurrentOWLOntologyManager().createOntology(axioms, IRI.create("http://no.sirius.ontology/ontology"));
		
	}
	
	
	
	public OWLOntology getOntology(){
		return ontology;
	}
	
	
	public abstract String getDataFilePath();
	
	
	public OWLOntology getClassifiedOntology(){
		
		//for (OWLAxiom ax: classified_ontology.getAxioms()){
		//	Utility.println(ax.toString());
		//}
		Utility.println("Classified ontology size: " + classified_ontology.getAxiomCount(Imports.INCLUDED));
		Utility.println("Original ontology size: " + getOntology().getAxiomCount(Imports.INCLUDED));
		if (performClassification())
			return classified_ontology;
		else
			return getOntology();
	}
	
	/**
	 * Returns the classified ontology in Turtle format. 
	 * Needed by RDFox after version 2, since OWLAPI is not supported anymore
	 * @return
	 * @throws OWLOntologyStorageException 
	
	public String getClassifiedOntologyString() throws OWLOntologyStorageException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		getClassifiedOntology().saveOntology(new TurtleDocumentFormat(), out);
		String turtle = new String(out.toByteArray());
		return turtle;
	}
	*/
	
	/**
	 * Returns the classified ontology in Turtle format. 
	 * Needed by RDFox after version 2, since OWLAPI is not supported anymore
	 * @return
	 * @throws OWLOntologyStorageException 
	 */
	public InputStream getClassifiedOntologyStream() throws OWLOntologyStorageException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		getClassifiedOntology().saveOntology(new TurtleDocumentFormat(), out);
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	/**
	 * Used by getOntologyPipe to transfer an ontology from the OWLApi into RDFox
	 * @author dag
	 *
	 */
	protected class OntologyPiper extends Thread {
		OWLOntology ont;
		PipedInputStream in;
		PipedOutputStream out;

		public OntologyPiper(OWLOntology _ont, PipedInputStream _in) throws IOException {
			this.in = _in;
			this.ont = _ont;
			out = new PipedOutputStream();
			in.connect(out);
		}
		public void run() {
			try {
				ont.saveOntology(new TurtleDocumentFormat(), out);
			} catch (OWLOntologyStorageException e) {
				System.err.println("Error while saving ontology to pipe by OntologyPiper");
				e.printStackTrace();
			} 
			finally {
				try {
					if(out != null)
						out.close();
					in.close();
				} catch (IOException e) {
					System.err.println("Error while closing threads used by OntologyPiper");
					e.printStackTrace();
				}
			}
		}
	}
		/**
		 * From an OWLOntology, creates a InputStream that can be used to read
		 * that ontology, using pipes
		 * @throws IOException 
		 * @throws JRDFoxException
		 */
	public InputStream getClassifiedOntologyPipe() throws IOException {
		PipedInputStream in = new PipedInputStream();
		Thread t = new OntologyPiper(getClassifiedOntology(), in);
		t.start();
		return in;
	}
	
	
	public OWLReasoner getReasoner(){
		return reasoner;
	}
	
	
	public boolean performClassification() {
		return reasonerID!=OWL2Reasoner.NONE;
	}
	
	
	
	public boolean isSubClassOf(String sub_cls, String super_cls){
		return isSubClassOf(dataFactory.getOWLClass(IRI.create(sub_cls)), dataFactory.getOWLClass(IRI.create(super_cls)));
	}
	
	public boolean isSubClassOf(OWLClass sub_cls, OWLClass super_cls){
		return getReasoner().isEntailed(dataFactory.getOWLSubClassOfAxiom(sub_cls, super_cls));
	}
	
	
	/**
	 * Gets more specific/direct type
	 * Greedy and simple method. Try to find a better solution to consider multiple direct types
	 * @param types
	 * @return
	 */
	protected String getMostSpecifictType(Set<String> types) {
	
		//System.out.println("Types: " + types);
		
		
		boolean good_type;
		
		for (String type1 : types) {
			
			good_type=true;
			
			for (String type2 : types) {
				if (type1.equals(type2)) 
					continue;
				
				if (isSubClassOf(type2, type1)) {
					good_type=false;
					break;
				}
				
			}
			if (good_type) {
				//System.out.println("Good type: "+ type1);
				return type1;
			}
			
		}
		
		
		return "";
		
	}
	
	/*
	 * Incomplete method
	 * @param types
	 * @return
	 *
	public Set<String> getMostSpecifictTypes(Set<String> types) {
		//TODO
		return null;
	}*/
	
	
	
	
	public GraphProjection getGraph(){
		return graph;
	}
	
	public Model getGraphModel(){
		return graph.getRDFModel();
	}
	
	
	
	/**
	 * This closure will involve subclass axioms
	 */
	private void createClasssifiedOntology(){
				
		try {
			
			
           OWLOntologyManager classifiedOntoMan = OWLManager.createConcurrentOWLOntologyManager(); //ontology.getOWLOntologyManager();
		   //OWLOntologyManager classifiedOntoMan = SynchronizedOWLManager.createOWLOntologyManager();
		   																 
           classified_ontology = classifiedOntoMan.createOntology(IRI.create(ontology.getOntologyID().getOntologyIRI().toString()+"classsified"));
           InferredOntologyGenerator ontGen = new InferredOntologyGenerator(
        		   getReasoner(), new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>());
           //InferredOntologyGenerator ontGen = new InferredOntologyGenerator(reasoner);
           
           
           ontGen.addGenerator(new InferredEquivalentClassAxiomGenerator());
           ontGen.addGenerator(new InferredSubClassAxiomGenerator());
           
           //TODO Also add property characteristics and hierarchy?
           //Include parameter?
           if (reasonerID != OWL2Reasoner.ELK) {  //Do not seem to be supported by ELK
	           
        	   ontGen.addGenerator(new InferredInverseObjectPropertiesAxiomGenerator());
        	   
        	   ontGen.addGenerator(new InferredDataPropertyCharacteristicAxiomGenerator());	           
	           ontGen.addGenerator(new InferredEquivalentDataPropertiesAxiomGenerator());
	           ontGen.addGenerator(new InferredSubDataPropertyAxiomGenerator());
	       
	           ontGen.addGenerator(new InferredEquivalentObjectPropertyAxiomGenerator());
	           ontGen.addGenerator(new InferredObjectPropertyCharacteristicAxiomGenerator());
	           ontGen.addGenerator(new InferredSubObjectPropertyAxiomGenerator());
           }
           
           
           
           //Fills inferred onto
           ontGen.fillOntology(classifiedOntoMan.getOWLDataFactory(), classified_ontology);
         
           
           //OTHER GENERATORS
   		   //ontGen.addGenerator(new InferredClassAssertionAxiomGenerator());
   		   //ontGen.addGenerator(new InferredPropertyAssertionGenerator());
   		   //Original computational cost is really high! With extension we can extract only eplicit disjointness	   		   
           //ontGen.addGenerator(new InferredDisjointClassesAxiomGenerator());
           
           //ontGen.addGenerator(new InferredDataPropertyCharacteristicAxiomGenerator());	           
           //ontGen.addGenerator(new InferredEquivalentDataPropertiesAxiomGenerator());
           //ontGen.addGenerator(new InferredSubDataPropertyAxiomGenerator());
       
           //ontGen.addGenerator(new InferredEquivalentObjectPropertyAxiomGenerator());
           //ontGen.addGenerator(new InferredInverseObjectPropertiesAxiomGenerator());
           //ontGen.addGenerator(new InferredObjectPropertyCharacteristicAxiomGenerator());
           //ontGen.addGenerator(new InferredSubObjectPropertyAxiomGenerator());
           
           
           
           //ADD property chains from original ontology
           classifiedOntoMan.addAxioms(classified_ontology, ontology.getAxioms(AxiomType.SUB_PROPERTY_CHAIN_OF));
           
           
           
           
           
	           
	       }
	       catch (Exception e) {
	           e.printStackTrace();
	           //return new ArrayList<OWLAxiom>();
	       }
		
		
		
	}
	
	
	public String getSPARQLQueryCoreConcepts(){
		
		return "SELECT DISTINCT ?uri WHERE{ ?uri rdf:type owl:Class }";
		
		//return "SELECT DISTINCT ?x ?y ?z WHERE{ ?x rdf:type owl:Class . OPTIONAL { ?x rdfs:label ?y } . OPTIONAL { ?x rdfs:comment ?z } }";
		//getQueryResults("SELECT DISTINCT ?x ?y WHERE{ ?x rdf:type owl:Class . OPTIONAL { ?x rdfs:label ?y } }");
		//getQueryResults("SELECT DISTINCT ?x ?y ?z WHERE{ ?x ?y ?z }");
	}
	
	public String getSPARQLQueryDataPredicates(){
		
		return "SELECT DISTINCT ?uri WHERE{ ?uri rdf:type owl:DatatypeProperty }";
	}
	
	public String getSPARQLQueryObjectPredicates(){
		
		return "SELECT DISTINCT ?uri WHERE{ ?uri rdf:type owl:ObjectProperty }";
	}
	
	
	/**
	 * 
	 * @param c_uri
	 * @return
	 * @see getSPARQLQueryEntityFacets
	 */
	public String getSPARQLQueryDataPredicatesForConcept(String c_uri){
		
		return "SELECT DISTINCT ?p_uri WHERE{ <"+ c_uri + "> rdf:type owl:Class . <"+ c_uri + "> ?p_uri ?o . ?o rdf:type <"+ URIUtils.facet_uri + "> . ?p_uri rdf:type owl:DatatypeProperty }";
	}
	
	/**
	 * 
	 * @param c_uri
	 * @return
	 * @see getSPARQLQueryEntityLinks
	 */
	public String getSPARQLQueryObjectPredicatesForConcept(String c_uri){
		
		return "SELECT DISTINCT ?p_uri WHERE{ <"+ c_uri + "> rdf:type owl:Class . <"+ c_uri + "> ?p_uri ?o . ?o rdf:type owl:Class . ?p_uri rdf:type owl:ObjectProperty }";
	}
	
	
	/**
	 * 
	 * @param c_uri
	 * @return
	 * @see getSPARQLQueryEntityLinks
	 */
	public String getSPARQLQueryTargetConceptsForConceptPredicate(String c_uri, String p_uri){
		
		return "SELECT DISTINCT ?o_uri WHERE{ <"+ c_uri + "> rdf:type owl:Class . <"+ c_uri + "> <" + p_uri+ "> ?o_uri . ?o_uri rdf:type owl:Class . <" + p_uri+ "> rdf:type owl:ObjectProperty }";
	}
	
	//TODO Get target facets....
	
	
	
	public String getSPARQLQueryInstances(){
		
		return "SELECT DISTINCT ?uri WHERE{ ?uri rdf:type owl:Thing }";
	}
	
	
	public String getSPARQLQueryEntityLabels(String e_uri){
		
		return "SELECT DISTINCT ?label WHERE{ <"+ e_uri + "> rdfs:label ?label }";
		
	}
	
		
	public String getSPARQLQueryEntityForLabel(){
		
		return "SELECT DISTINCT ?x ?label WHERE{ ?x rdfs:label ?label . ?x rdf:type owl:Class }";
		
	}
	
	public String getSPARQLQueryEntityComments(String e_uri){
		
		return "SELECT DISTINCT ?comment WHERE{ <"+ e_uri + "> rdfs:comment ?comment }";
		
	}
	
	public String getSPARQLQueryEntityHidden(String e_uri){
		
		return "SELECT DISTINCT ?x WHERE{ <"+ e_uri + "> <" + URIUtils.hidden_uri + "> ?x }";
		
	}
	
	public String getSPARQLQueryMainArtefact(){
		
		return "SELECT DISTINCT ?x WHERE{ ?x <" + URIUtils.is_main_artifact_gic_uri + "> ?y }";
		
	}
	
	
	public String getSPARQLQueryEntityLinks(String e_uri){
		
		return "SELECT DISTINCT ?p ?o WHERE{ <"+ e_uri + "> rdf:type owl:Class . <"+ e_uri + "> ?p ?o . ?o rdf:type owl:Class . ?p rdf:type owl:ObjectProperty }";
		
	}
	
	
	
	/**
	 * Get ranges given a subject class and datatype property as predicate
	 * @param e_uri
	 * @param p_uri
	 * @return
	 */
	public String getSPARQLQueryRangeClasses(String e_uri, String p_uri){		
		return "SELECT DISTINCT ?range WHERE{ <"+ e_uri + "> rdf:type owl:Class . <"+ e_uri + "> <" + p_uri + "> ?range . <" + p_uri + "> rdf:type owl:ObjectProperty . ?range rdf:type owl:Class}";
		
	}
	
	
	
	
	/**
	 * Inverse links
	 * @param e_uri
	 * @return
	 */
	public String getSPARQLQueryInverseEntityLinks(String e_uri){
		
		return "SELECT DISTINCT ?p ?s WHERE{ <"+ e_uri + "> rdf:type owl:Class . ?s ?p <"+ e_uri + "> . ?s rdf:type owl:Class . ?p rdf:type owl:ObjectProperty }";
		
	}
	
	
	/**
	 * Properties with inverse
	 * @param e_uri
	 * @return
	 */
	public String getSPARQLQueryPropertiesWithInverse(){
		
		return "SELECT DISTINCT ?p WHERE{ ?p rdf:type owl:ObjectProperty . ?p  owl:inverseOf ?q }";
		
	}
	
	
	
	/**
	 * Retrieves facets (DP - Facet) associated to a concept. Several facets may be associated to the same property. 
	 * In that cases one will need to chose only one facet representation.
	 * @param e_uri
	 * @return
	 */
	public String getSPARQLQueryEntityFacets(String e_uri){
		
		return "SELECT DISTINCT ?p ?o WHERE{ <"+ e_uri + "> rdf:type owl:Class . <"+ e_uri + "> ?p ?o . ?o rdf:type <"+ URIUtils.facet_uri + "> . ?p rdf:type owl:DatatypeProperty }";
		
	}
	
	
	public String getSPARQLQueryFacetDataype(String f_uri){
		
		return "SELECT DISTINCT ?dt WHERE{ <"+ f_uri + "> rdf:type <" + URIUtils.facet_uri + "> . <" + f_uri + "> rdfs:range ?dt }";
		
	}
	
	
	public String getSPARQLQueryFacetValues(String f_uri){
		
		return "SELECT DISTINCT ?value WHERE{ <"+ f_uri + "> rdf:type <" + URIUtils.facet_uri + "> . <" + f_uri + "> rdf:value ?value }";
		
	}
	
	
	public String getSPARQLQueryFacetMinValue(String f_uri){
		
		//If several min values keep minimun
		return "SELECT DISTINCT ?min WHERE{ <"+ f_uri + "> rdf:type <" + URIUtils.facet_uri + "> . <" + f_uri + "> <"+  URIUtils.min_value_uri + "> ?min }";
		
	}
	
	public String getSPARQLQueryFacetMaxValue(String f_uri){
		
		//If several min values keep maximum
		return "SELECT DISTINCT ?max WHERE{ <"+ f_uri + "> rdf:type <" + URIUtils.facet_uri + "> . <" + f_uri + "> <"+  URIUtils.max_value_uri + "> ?max }";
		
	}
	
	
	public String getSPARQLQueryFacetScopes(String f_uri){
		
		return "SELECT DISTINCT ?s WHERE{ <"+ f_uri + "> rdf:type <" + URIUtils.facet_uri + "> . <" + f_uri + "> <"+  URIUtils.scope_facet_uri + "> ?s }"; //. ?s rdf:type owl:Class 
		
	}
	
	
	
	
	public String getSPARQLQueryDirectSuperclasses(String e_uri){
		
		return "SELECT DISTINCT ?c WHERE{ <"+ e_uri + "> rdf:type owl:Class . <"+ e_uri + "> rdfs:subClassOf ?c . ?c rdf:type owl:Class }";
		
	}
	
	
	
	public String getSPARQLQueryDirectSubclasses(String e_uri){
		
		return "SELECT DISTINCT ?c WHERE{ <"+ e_uri + "> rdf:type owl:Class . ?c rdfs:subClassOf <"+ e_uri + "> . ?c rdf:type owl:Class }";
		
	}
	
	
	
	public String getSPARQLQueryAll(){
		
		return "SELECT DISTINCT ?s ?p ?o WHERE{ ?s ?p ?o }";
		
	}
	
	
	public String getSPARQLQueryInstancesFortype(String type_uri){
		
		return "SELECT DISTINCT ?i WHERE{ ?i rdf:type <" + type_uri + ">  }";
		
	}
	
	public String getSPARQLQueryTypesForInstance(String i_uri) {
		return "SELECT DISTINCT ?t WHERE{ <"+ i_uri +"> rdf:type ?t }";
	}
	
	
	
	public String getSPARQLQueryDirectTypesForInstance(String i_uri) {
		return "SELECT DISTINCT ?t WHERE{ <"+ i_uri +"> <" + URIUtils.DIRECT_TYPE + "> ?t }";
	}
	
	
	
	public String getSPARQLQueryObjectsForSubjectPredicate(String subject, String predicate){
		
		return "SELECT DISTINCT ?o WHERE{ <" + subject + "> <" + predicate + "> ?o }";
		
	}
	
	
	public String getSPARQLQueryObjectsForSubjectPredicate(String subject, String predicate, String type_object){
		
		return "SELECT DISTINCT ?o WHERE{ <" + subject + "> <" + predicate + "> ?o . ?o rdf:type <" + type_object + "> }";
		
	}
	
	
	public String getSPARQLQuerySubjectsForObjectPredicate(String predicate, String object){
		
		return "SELECT DISTINCT ?s WHERE{ ?s <" + predicate + "> <" + object + "> }";
		
	}
	
	
	/**
	 * We filter by the type of the object too
	 * @param predicate
	 * @param object
	 * @param type_object
	 * @return
	 */
	public String getSPARQLQuerySubjectsForObjectPredicate(String predicate, String object, String type_object){
		
		return "SELECT DISTINCT ?s WHERE{ ?s <" + predicate + "> <" + object + "> . <" + object + "> rdf:type <" + type_object + "> }";
		
	}
	
	
	
	public String getSPARQLQueryObjectsForPredicate(String predicate) {
		return "SELECT DISTINCT ?o WHERE{ ?s <" + predicate + "> ?o }";
	}
	
	
	
	
	
	
	/**
	 * Get ranges (for all the available facets) given a subject class and datatype property as predicate.
	 * It will be useful for the case or "Top R range_facet"
	 * @param e_uri
	 * @param p_uri
	 * @return
	 */
	public String getSPARQLQueryRangeFacets(String e_uri, String p_uri){		
		return "SELECT DISTINCT ?range WHERE{ <"+ e_uri + "> rdf:type owl:Class . <" + p_uri + "> rdf:type owl:DatatypeProperty . <"+ e_uri + "> <" + p_uri + "> ?facet . ?facet rdfs:range ?range }";
		
	}
	
	
	
	public String getSPARQLQueryDataPropertyTriplesForSubject(String i_uri){		
		return "SELECT DISTINCT ?p ?o WHERE{ <"+ i_uri + "> ?p ?o . ?p rdf:type owl:DatatypeProperty }";
		
	}
	
	public String getSPARQLQueryObjectPropertyTriplesForSubject(String i_uri){		
		return "SELECT DISTINCT ?p ?o WHERE{ <"+ i_uri + "> ?p ?o . ?p rdf:type owl:ObjectProperty }";
		
	}
	
	
	public String getSPARQLQueryAllRelationshipsForSubject(String i_uri){		
		return "SELECT DISTINCT ?p ?o WHERE{ <"+ i_uri + "> ?p ?o }";
		
	}
	
	
	public String getSPARQLQueryAllRelationshipsForObject(String i_uri){		
		return "SELECT DISTINCT ?p ?s WHERE{ ?s ?p <" + i_uri + "> }";
		
	}
	
	
	
	
	public String getSPARQLQueryIsObjectProperty(String p_uri){		
		//return "ASK { <"+ p_uri + "> rdf:type owl:ObjectProperty }";
		return "SELECT DISTINCT ?p_type WHERE{ <"+ p_uri + "> ?p_type owl:ObjectProperty }";
	}
	
	
	
	
	//TODO SPARQL to query for facets....
	//Query for data properties (facets)
	//Query for type of facets, etc.
	
	
	public abstract Concept createConcept(String e_uri) ;
	
	public abstract ObjectProperty createObjectPropery(String e_uri);
	
	public abstract DataProperty createDataPropery(String e_uri);
	
	public abstract Instance createInstance(String e_uri);
	
	public abstract Instance createInstance(String e_uri, String type);
	
	
	
	
	
	public abstract Concept getMainArtefactConcept();
	
	
	public abstract TreeSet<Concept> getCoreConcepts();
	
	public abstract TreeSet<Concept> getCoreConcepts(String filterByNamespace);
	
	
	public abstract TreeSet<DataProperty> getDataPredicates();
	
	public abstract TreeSet<ObjectProperty> getObjectPredicates();
	
	
	public abstract TreeSet<DataProperty> getAllowedDataPredicatesForSubject(String i_uri);
	
	
	public abstract TreeSet<ObjectProperty> getAllowedObjectPredicatesForSubject(String i_uri);
	
	
	public abstract TreeSet<Instance> getAllowedObjectInstancesForSubjectPredicate(String c_uri, String p_uri);

		
	
	public abstract TreeSet<NeighbourLink> getNeighbourConcepts(String conceptURI);

	public abstract TreeSet<Facet> getConceptFacets(String conceptURI);
	
	
	public abstract TreeSet<Concept> getDirectSubClasses(String conceptURI);
	
	public abstract TreeSet<Concept> getDirectSuperClasses(String conceptURI);
	
	
	public abstract TreeSet<Concept> getAllSubClasses(String conceptURI);
	
	public abstract TreeSet<Concept> getAllSuperClasses(String conceptURI);

	
	public abstract Concept getConceptForLabel(String label);
	
	public abstract TreeSet<Instance> getInstancesForType(String type);
	
	public abstract TreeSet<Instance> getInstances();
	
	public abstract Set<String> getObjectsForSubjectPredicate(String subject, String predicate);
	
	public abstract Set<String> getObjectsForSubjectPredicate(String subject, String predicate, String type_object);
	
	public abstract Set<String> getSubjectsForObjectPredicate(String predicate, String object);
	
	public abstract Set<String> getSubjectsForObjectPredicate(String predicate, String object, String type_object);
	
	public abstract Set<String> getObjectsForPredicate(String predicate);
	
	//TODO: get 
	public abstract Map<String, Set<String>> getObjectRelationhipsForSubject(String subject);
	
	public abstract Map<String, Set<String>> getDataRelationhipsForSubject(String subject);
	
	public abstract Map<String, Set<GenericValue>> getAllRelationhipsForSubject(String subject);
		
	public abstract Map<String, Set<String>> getAllRelationhipsForObject(String subject);
	
	
	
	public abstract void performMaterializationAdditionalData(String file_data, boolean incremental, S updateType) throws Exception;
	
	
	public abstract void exportMaterielizationSnapshot(String file_out_materialization) throws Exception;
	
	
	
	public abstract S getAdditionUpdateType();
	
	public abstract S getDeletionUpdateType();
	
	
	
	public abstract String getMostConcreteTypeForInstance(String i_uri);
	
	public abstract boolean isPredicateAnObjectProperty(String p_uri);
	
	
	
	
	
	/**
	 * @param query
	 */
	public abstract T runSPARQLQuery(String query);
	

	/**
	 * 
	 */
	public void dispose() {
		
		//ontology.getOWLOntologyManager().removeOntology(ontology); //closed inside hermit already
		if (performClassification()) {
			classified_ontology.getOWLOntologyManager().removeOntology(classified_ontology);
			getReasoner().dispose();
		}
		
		ontology=null;
		classified_ontology=null;
		
		graph.dispose();
		
	}


	
	
	
	
	
	
}
