/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 *
 * Simple session manager. We may need to use a smarter option e.g. HttpSession
 * So far we deal with the ontology session only
 *
 * @author ernesto
 * Created on 19 Nov 2017
 *
 */
public abstract class SessionManager {
	
	
	private  Map<String, GraphProjectionManager> sessions = new HashMap<String, GraphProjectionManager>();
	
	
	
	public synchronized  Map<String, GraphProjectionManager> getSessions(){
		return sessions;
	}
	
	
	public synchronized Set<String> getLoadedOntologies(){
		return sessions.keySet();
	}
	
	
	public synchronized boolean isOntologyLoaded(String iri){
		//System.out.println(sessions.size());
		//System.out.println(sessions.keySet());
		return sessions.containsKey(iri);
	}
	
	
	public synchronized void clearOntologySession(String iri){
		if (!sessions.containsKey(iri))
			return;
		
		disposeOntologySession(iri);
		
		sessions.remove(iri);
			
	}
	
	
	protected synchronized void disposeOntologySession(String iri){
		//if (!sessions.containsKey(iri))
		//	return;
		
		sessions.get(iri).dispose();
			
	}
	
	
	
	public synchronized void clearAllOntologySessions(){
		
		for (String session : sessions.keySet())
			disposeOntologySession(session);
		
		sessions.clear();
			
	}
	
	public abstract boolean createNewSession(String iri);
	
	public abstract boolean createNewSession(String iri, String data_fileS);
	
	
	public abstract boolean createNewSession(String Session_id, Set<String> iris);
	
	public abstract boolean createNewSession(String Session_id, Set<String> iris, String data_fileS);
	
	
	public synchronized GraphProjectionManager getSession(String iri){
		return sessions.get(iri);
	}


	public boolean createNewSessionForEmbeddings(String ontology_iri) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean createNewSessionForEmbeddings(String Session_id, Set<String> iris) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	

	
	
	

}
