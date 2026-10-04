/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.constraint.controller;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uio.ifi.ontology.toolkit.constraint.controller.rdfox.RDFoxBasedConstraintValidator;
import uio.ifi.ontology.toolkit.constraint.utils.Utility;

/**
 *
 * @author ernesto
 * Created on 11 Jan 2017
 *
 */
public class ConstraintValidatorManager {

	
	public static void test(){
		String path = "/home/ernesto/Documents/Models_Siemens/ontologies/";
		//Utility.tmp_directory = path;
		
		String file;
		//file = path + "isa8895_all.owl";
		//file = path + "processes.owl";
		//file = path + "TurboMachineryConcepts.owl";
		//file = path + "test.owl";
		file = path + "test3.owl";
		
		System.out.println(file);
		
		
		try {
			OWLOntologyManager managerOnto = OWLManager.createOWLOntologyManager();
			OWLOntology o;
			o = managerOnto.loadOntology(IRI.create("file:"+file));
			
			RDFoxBasedConstraintValidator dataLogTest = new RDFoxBasedConstraintValidator(o);
			dataLogTest.performDataConstraintValidation();
			dataLogTest.printStatistics();
						
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	
	public static void main(String[] args){
		
		test();
		
		//testManufacturing();
			
		//testTurbine();
		
		
		
	}
}
