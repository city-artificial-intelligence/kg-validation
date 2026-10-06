/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.city.kg.validation.constraint.controller;

import java.nio.file.FileSystems;
import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.city.kg.validation.constraint.controller.rdfox.ConstraintCreatorForRDFox;
import uk.city.kg.validation.constraint.controller.rdfox.RDFoxBasedConstraintValidator;
import uk.city.kg.validation.constraint.controller.rdfox.TestConstraintValidator;
import uk.city.kg.validation.constraint.utils.Utility;
import uk.city.kg.validation.constraint.utils.pagoda_hermit.RuleHelperExtended;

/**
 *
 * @author ernesto
 * Created on 11 Jan 2017
 *
 */
public class ConstraintValidatorManager {

	
	
	
	public static void test(){
		
		
		 String userDirectory = FileSystems.getDefault()
 	            .getPath("")
 	           .toAbsolutePath()
 	            .toString();	        
		 System.out.println(userDirectory);
		
		
		//String path = "/home/ernesto/Documents/Models_Siemens/ontologies/";
		//Utility.tmp_directory = path;
		
		String file;
		//file = path + "isa8895_all.owl";
		//file = path + "processes.owl";
		//file = path + "TurboMachineryConcepts.owl";
		//file = path + "test.owl";
		//file = path + "test3.owl";
		
		file =  userDirectory + "/data/witnesses/w1/o1.ttl"; 
		
		
		System.out.println(file);
		
		
		try {
			OWLOntologyManager managerOnto = OWLManager.createOWLOntologyManager();
			OWLOntology o;
			o = managerOnto.loadOntologyFromOntologyDocument(new File(file));
			
			TestConstraintValidator testCosntraintsCreation = new TestConstraintValidator(o);
			testCosntraintsCreation.performDataConstraintValidation();
			
			//RDFoxBasedConstraintValidator dataLogTest = new RDFoxBasedConstraintValidator(o);
			//dataLogTest.performDataConstraintValidation();
			//dataLogTest.printStatistics();
						
			
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
