/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package no.ifi.uio.ontology_services_toolkit;

import java.io.FileNotFoundException;
import java.util.TreeSet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uio.ifi.ontology.toolkit.projection.controller.triplestore.RDFoxSessionManager;
import uio.ifi.ontology.toolkit.projection.model.entities.Concept;
import uio.ifi.ontology.toolkit.projection.view.OptiqueVQSAPI;


/**
 *
 * @author ernesto
 * Created on 14 Nov 2017
 *
 */
public class TestProjection {
	
	public static void main (String args[]){
		
		
		try {
			
			
			
			RDFoxSessionManager session_rdf = new RDFoxSessionManager();
			
			//session_rdf.createNewSessionForEmbeddings("file:/home/ejimenez-ruiz/Documents/ontologies/go.owl");
			
			session_rdf.createNewSession("file:/home/ejimenez-ruiz/Downloads/subsurface-exploration.ttl");
			
			
			if (true)
				return;
			
			
			
			String onto_url = "file:/home/ernesto/Desktop/example.owl";
			
			OWLOntologyManager manager = OWLManager.createConcurrentOWLOntologyManager();
			
			//new RDFoxProjectionManager(manager.loadOntology(IRI.create(onto_url)), true, true);	
			
			RDFoxSessionManager session = new RDFoxSessionManager();
			
			OptiqueVQSAPI vqs = new OptiqueVQSAPI(session);
			
			vqs.loadOntologySession(onto_url);
			
			System.out.println("Loaded ontos: \n "+ vqs.getOntologies());
			
			
			
			//System.out.println("Core: \n "+ vqs.getCoreConcepts(onto_url).toString(1));
			System.out.println("Core: \n "+ vqs.getCoreConcepts(onto_url).toString(1));
			
			
			TreeSet<Concept> concepts = vqs.getSessionManager().getSession(onto_url).getCoreConcepts();
			
			for (Concept c: concepts){
				System.out.println("Links for " + c.getName() + ": \n "+ vqs.getNeighbourConcepts(onto_url, c.getIri()).toString(1));
			}
			
			
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}
