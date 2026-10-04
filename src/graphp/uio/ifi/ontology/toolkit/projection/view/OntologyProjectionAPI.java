/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.view;

import java.util.Set;

import uio.ifi.ontology.toolkit.projection.controller.SessionManager;
import uio.ifi.ontology.toolkit.projection.controller.triplestore.RDFoxSessionManager;

/**
 * The idea in the "view" module is to implement the different entry points for 
 * different applications like OptiqueVQS 
 * 
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public abstract class OntologyProjectionAPI {
	
	
	//TODO Not initialised here. It will be given by the context 
	//protected RDFoxSessionManager sessionManager = new RDFoxSessionManager();
	protected SessionManager sessionManager;
	
	
	/**
	 * For tests only
	 * @return
	 */
	public SessionManager getSessionManager(){
		return sessionManager;
	}

	
	protected Set<String> getLoadedOntologies(){
		return sessionManager.getLoadedOntologies();
	}
	
	

	public void loadOntologySession(String ontologyURI) {
		
		if (!sessionManager.isOntologyLoaded(ontologyURI)){
			sessionManager.createNewSession(ontologyURI);
		}

		
	}

	public void clearOntologySession(String ontologyURI) {
		sessionManager.clearOntologySession(ontologyURI);
		
	}
	
	public void clearAllSessions() {
		sessionManager.clearAllOntologySessions();
		
	}
	
	
	
	
	
}
