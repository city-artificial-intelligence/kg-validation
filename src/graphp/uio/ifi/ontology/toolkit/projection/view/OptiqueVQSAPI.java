/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.view;

import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import uio.ifi.ontology.toolkit.projection.controller.triplestore.RDFoxSessionManager;
import uio.ifi.ontology.toolkit.projection.model.Facet;
import uio.ifi.ontology.toolkit.projection.model.NeighbourLink;
import uio.ifi.ontology.toolkit.projection.model.entities.Concept;
import uk.city.kg.validation.constraint.utils.Utility;

/**
 * 
 * 
 * 
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public class OptiqueVQSAPI extends OntologyProjectionAPI {
	
	
	//Include serialization methods to JSON
	
	public OptiqueVQSAPI(RDFoxSessionManager session){
		sessionManager = session;
	}
	
	
	
	public JSONObject getOntologies(){

		//An array of jsonobjects representing each class
		JSONArray jarray = new JSONArray();
		
		for (String uri : super.getLoadedOntologies()){
			jarray.put(uri);
		}
		
		JSONObject object = new JSONObject();
		object.put("result", jarray);

		return object;
		
	}
	

	public JSONObject getCoreConcepts(String ontologyURIStr) {
		
		if (!sessionManager.isOntologyLoaded(ontologyURIStr)){
			System.err.println("The ontology '" + ontologyURIStr + "' has not been loaded yet. Please invoke 'loadOntology()' first");
			return getEmptySetOptions();//new JSONObject();
		}
		
		//An array of jsonobjects representing each class
		JSONArray jarray = new JSONArray();
		
		
		//Add JSonObjects
		TreeSet<Concept> concepts = sessionManager.getSession(ontologyURIStr).getCoreConcepts();
		for (Concept c : concepts.descendingSet()){
			//Avoid hidden
			if (!c.isHidden())
				jarray.put(c.toJSON());
		}
		
		//Return object
		JSONObject object = new JSONObject();
		object.put("options", jarray);
		
		//Utility.print(object.toString(2));
		
		return wrapObject(object);
		
	}

	public JSONObject getNeighbourConcepts(String ontologyURIStr, String conceptURI) {
		
		if (!sessionManager.isOntologyLoaded(ontologyURIStr)){
			System.err.println("The ontology '" + ontologyURIStr + "' has not been loaded yet. Please invoke 'loadOntology()' first");
			return getEmptySetOptions(); //new JSONObject();
		}
		//Utility.println("Getting neigbours for concept: " + conceptURI);
		
		//TreeSet<Concept> concepts = sessionManager.getSession(ontologyURIStr).getCoreConcepts();
		//
		//for (Concept c : concepts.descendingSet()){			
		//	TreeSet<NeighbourLink> links = sessionManager.getSession(ontologyURIStr).getNeighbourConcepts(c.getIri());
		//}
		
		//An array of jsonobjects representing each neighbour class
		JSONArray jarray = new JSONArray();
		
		
		TreeSet<NeighbourLink> links = sessionManager.getSession(ontologyURIStr).getNeighbourConcepts(conceptURI);
		
		Utility.println("Extracted '"+ links.size()+"' neighbours for concept: " + conceptURI);
		
		
		for (NeighbourLink link : links.descendingSet()){
			if (!link.getTarget().isHidden() && !link.getProperty().isHidden())
				jarray.put(link.toJSON());
			
		}
		
		JSONObject object = new JSONObject();
		object.put("options", jarray);
		
		return wrapObject(object);
	}

	public JSONObject getConceptFacets(String ontologyURIStr, String conceptURI) {
		
		if (!sessionManager.isOntologyLoaded(ontologyURIStr)){
			System.err.println("The ontology '" + ontologyURIStr + "' has not been loaded yet. Please invoke 'loadOntology()' first");
			return getEmptySetFields();
		}
			
		//Given by QFI for root node
		if (conceptURI.equals("0")){			
			return getEmptySetFields();
		}
		//Utility.println("Getting facets for concept: " + conceptURI);
		
			
		//An array of jsonobjects representing each facet
		JSONArray jarray = new JSONArray();
				
				
		TreeSet<Facet> facets = sessionManager.getSession(ontologyURIStr).getConceptFacets(conceptURI);
		
		Utility.println("Extracted '"+ facets.size()+"' facets for concept: " + conceptURI);
				
		for (Facet facet : facets.descendingSet()){
			if (!facet.getProperty().isHidden())
				jarray.put(facet.toJSON());
		}
		
		
		JSONObject object = new JSONObject();
		//Return object
		object.put("fields", jarray);
		
		
		Utility.printlnQueryInfo(object.toString(2));
		
		
		return wrapObject(object);
	}
	
	
	
	private JSONObject getEmptySetFields() {
		JSONArray jarray = new JSONArray();
		JSONObject object = new JSONObject();
		object.put("fields", jarray);
		
		return wrapObject(object);
	}
	
	private JSONObject getEmptySetOptions() {
		JSONArray jarray = new JSONArray();
		JSONObject object = new JSONObject();
		object.put("options", jarray);
		
		return wrapObject(object);
	}
	
	
	
	
	
	public JSONObject getDirectSuperClasses(String ontologyURIStr, String conceptURI) {

		if (!sessionManager.isOntologyLoaded(ontologyURIStr)){
			System.err.println("The ontology '" + ontologyURIStr + "' has not been loaded yet. Please invoke 'loadOntology()' first");
			return new JSONObject();
		}
		
		JSONArray jarray = new JSONArray();
		
		
		TreeSet<Concept> superclassess = sessionManager.getSession(ontologyURIStr).getDirectSuperClasses(conceptURI);
		for (Concept superclass : superclassess.descendingSet()){
			jarray.put(superclass.toJSON());
		}
		
		
		JSONObject object = new JSONObject();
		object.put("options", jarray);
		
		return wrapObject(object);
	}
	
	
	public JSONObject getDirectSubClasses(String ontologyURIStr, String conceptURI) {

		if (!sessionManager.isOntologyLoaded(ontologyURIStr)){
			System.err.println("The ontology '" + ontologyURIStr + "' has not been loaded yet. Please invoke 'loadOntology()' first");
			return new JSONObject();
		}
		
		JSONArray jarray = new JSONArray();
		
		TreeSet<Concept> subclassess = sessionManager.getSession(ontologyURIStr).getDirectSubClasses(conceptURI);
		for (Concept subclass : subclassess.descendingSet()){
			jarray.put(subclass.toJSON());
		}
		
		
		JSONObject object = new JSONObject();
		object.put("options", jarray);
		
		return wrapObject(object);
	}
	
	
	
	
	public JSONObject getQuery(String ontologyURIStr, String sparql_query) {
		
		JSONObject object = new JSONObject();
		
		//TODO Array of JSON objects (i.e. Tuples)
		//Interesting facility to provide flexibility
		
		return object;
		
	}
	
	
	/**
	 * To add expected format by OptiqueVQS
	 * @param object
	 * @return
	 */
	protected JSONObject wrapObject(JSONObject object){
			                      
		JSONObject results_object = new JSONObject();
		
		results_object.put("result", object);
		
		results_object.put("id", "1");
		results_object.put("jsonrpc", "2.0");
		
		return results_object;
		
	}
	
	
	
	
	
	
}