/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.controller.triplestore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uio.ifi.ontology.toolkit.projection.controller.SessionManager;
import uio.ifi.ontology.toolkit.projection.controller.reasoner.ReasonerManager.OWL2Reasoner;
import uk.city.kg.validation.constraint.utils.Utility;
//import tech.oxfordsemantic.jrdfox.JRDFStoreException;
import tech.oxfordsemantic.jrdfox.exceptions.JRDFoxException;

/**
 *
 * @author ernesto
 * Created on 19 Nov 2017
 *
 */
public class RDFoxSessionManager extends SessionManager{

	
	
	
	
	
	/**
	 * BAsic projection. Propagations are no required
	 */
	@Override
	public synchronized boolean createNewSessionForEmbeddings(String ontology_iri) {
		if (isOntologyLoaded(ontology_iri))
			return true;
		
		try {
			
			Utility.println("Loading RDFox projection session (for embeddings): " +  ontology_iri);
			
			//TODO OWL 2 classification with HermiT can now be switched off
			OWL2Reasoner owl2classification = OWL2Reasoner.ELK;
			getSessions().put(ontology_iri, new RDFoxProjectionManagerForEmbeddings(ontology_iri, owl2classification));
			
			Utility.println("Session loaded for : "+  ontology_iri);
			Utility.println("\tNumber of sessions: " + getSessions().size());
			Utility.println("\tActive sessions: " + getSessions().keySet());
			
		} 
		catch (OWLOntologyCreationException | JRDFoxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();//errors
			return false;
		}
		
		return true;
	}
	

	
	
	/**
	 * Basic projection. Propagations are no required
	 */
	@Override
	public synchronized boolean createNewSessionForEmbeddings(String sessionId, Set<String> ontology_iris) {
		if (isOntologyLoaded(sessionId))
			return true;
		
		try {
			
			Utility.println("Loading RDFox projection session (for embeddings): " +  ontology_iris);
			
			//TODO OWL 2 classification with HermiT can now be switched off
			OWL2Reasoner owl2classification = OWL2Reasoner.ELK;
			getSessions().put(sessionId, new RDFoxProjectionManagerForEmbeddings(ontology_iris, owl2classification));
			
			Utility.println("Session loaded for : "+  sessionId);
			Utility.println("\tNumber of sessions: " + getSessions().size());
			Utility.println("\tActive sessions: " + getSessions().keySet());
			
		} 
		catch (OWLOntologyCreationException | JRDFoxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();//errors
			return false;
		}
		
		return true;
	}
	
	
	
	
	@Override
	public synchronized boolean createNewSession(String ontology_iri) {
		return createNewSession(ontology_iri, ""); //empty (optional) data file 
	}
	
	
	/**
	 * Single ontology iri to project. Used in OptiqueVQS
	 */
	public synchronized boolean createNewSession(String ontology_iri, String data_file) {
		if (isOntologyLoaded(ontology_iri))
			return true;
		
		try {
			
			Utility.println("Loading RDFox session: " +  ontology_iri);
			
			//TODO OWL 2 classification with HermiT can now be switched off
			OWL2Reasoner owl2classification = OWL2Reasoner.HERMIT;
			boolean top_bottom_propagation = true;
			boolean extended_propagation = true; //false: no propagation in ranges nor top-bottom
			boolean create_facets=true;
			
			getSessions().put(ontology_iri, new RDFoxProjectionManager(ontology_iri, data_file, top_bottom_propagation, extended_propagation, owl2classification, create_facets));
			
			Utility.println("Session loaded for : "+  ontology_iri);
			Utility.println("\tNumber of sessions: " + getSessions().size());
			Utility.println("\tActive sessions: " + getSessions().keySet());
			
		} 
		catch (OWLOntologyCreationException | JRDFoxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();//errors
			return false;
		}
		
		return true;
	}
	
	
	@Override
	public synchronized boolean createNewSession(String session_id, Set<String> ontology_iris) {
		return createNewSession(session_id, ontology_iris, ""); //empty data file
	}
	

	/**
	 * Give a set of iris to project. Used in Image annotation systems
	 */
	public synchronized boolean createNewSession(String session_id, Set<String> ontology_iris, String data_file) {
		
		if (isOntologyLoaded(session_id))
			return true;
		
		try {
			
			Utility.println("Loading RDFox session: " +  session_id);
			
			//TODO OWL 2 classification with HermiT can now be switched off
			OWL2Reasoner owl2classification = OWL2Reasoner.HERMIT;
			boolean top_bottom_propagation = true;
			boolean extended_propagation = false;
			boolean create_facets=true;
			getSessions().put(session_id, new RDFoxProjectionManager(ontology_iris, data_file, top_bottom_propagation, extended_propagation, owl2classification, create_facets));
			
			Utility.println("Session loaded for : "+  session_id);
			Utility.println("\tNumber of sessions: " + getSessions().size());
			Utility.println("\tActive sessions: " + getSessions().keySet());
			
		} 
		catch (OWLOntologyCreationException | JRDFoxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();//errors
			return false;
		}
		
		return true;
	}
	

}
