/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.constraint.controller;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;



/**
 * 
 * Abstract class for the constraint validation
 * 
 * @author ernesto
 * Created on 11 Jan 2017
 *
 */
public abstract class ConstraintValidator {
	
	protected long number_initial_triples;
	protected long number_triples_after_validation;	
	protected long number_violations; //one individual may have more than 1 violation
	protected long number_invalid_individuals;	
	protected double validation_time;
	
	protected ConstraintCreator constraint_creator;
	
	//No constraints Tbox axioms
	protected OWLOntology tbox_ontology;
	
	protected File file_data;
	
	//protected OWLOntology data_ontology;
	
	
	public long gentNumberInitialTriples(){
		return number_initial_triples;
	}
	
	public long gentNumberTriplesAfterValidation(){
		return number_triples_after_validation;
	}
	
	public long getNumberOfViolations(){
		return number_violations;
	}
	
	public long getNumberInvalidIndividuals(){
		return number_invalid_individuals;
	}
	
	public double getValidationTime(){
		return validation_time;
	}
	
	
	
	//Input cases
	//1. Only one ontology with data and constraints
	//2. Ontology (axioms and constraints) + data: case of RDFox data should come in a special file
	//3.Three independent file requirements ontology + ontology + data	
	
	
	/**
	 * The input is one ontology containing both regular TBox axioms, constraints and data
	 * @param ontology
	 * @throws OWLOntologyCreationException 
	 */
	public ConstraintValidator(OWLOntology ontology) throws OWLOntologyCreationException{
		
		setConstraintCreator();
		
		//No constraints Tbox axioms
		tbox_ontology = filterConstraintsAndData(ontology);
				


		
	}
	
	
	/**
	 * The input is an ontology with regular Tbox axioms and constraints/requirements and the data comes in an independent object
	 * @param ontology
	 * @param data
	 * @throws OWLOntologyCreationException 
	 */
	public ConstraintValidator(OWLOntology ontology, OWLOntology data) throws OWLOntologyCreationException{
		
		setConstraintCreator();
		
		//No constraints Tbox axioms
		tbox_ontology = filterConstraintsAndData(ontology);
				
		
	}
	
	/**
	 * Input is given in 3 independent files: requirements ontology, ontology with TBox axioms, and data
	 * @param req_ontology
	 * @param ontology
	 * @param data
	 * @throws OWLOntologyCreationException 
	 */
	public ConstraintValidator(OWLOntology req_ontology, OWLOntology ontology, OWLOntology data) throws OWLOntologyCreationException{
		
		setConstraintCreator();
		
		//Creates (only) accepted constraints from requirements ontology 
		filterConstraintsAndData(req_ontology);
		
		tbox_ontology = ontology;
		
		
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
	public ConstraintValidator(OWLOntology ontology, File file_data) throws OWLOntologyCreationException{
		
		setConstraintCreator();
		
		//No constraints Tbox axioms
		tbox_ontology = filterConstraintsAndData(ontology);
		
		this.file_data = file_data;
				
		
		

		
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
	public ConstraintValidator(OWLOntology req_ontology, OWLOntology ontology, File file_data) throws OWLOntologyCreationException{
		
		setConstraintCreator();
		
		//Creates (only) accepted constraints from requirements ontology 
		filterConstraintsAndData(req_ontology);
		
		tbox_ontology = ontology;
		
		this.file_data = file_data;
		
	}
	
	
	
	
	
	
	
	/**
	 * This method returns TBox axioms that are not constraints. The visitor will also create the datalog representations of the constraints
	 * and (optionally) of the data 
	 * @param ontology
	 * @return
	 * @throws OWLOntologyCreationException 
	 */
	protected OWLOntology filterConstraintsAndData(OWLOntology ontology) throws OWLOntologyCreationException{
		
		//Tbox axioms that are not constraints
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		
		//We filter axioms to extract data constraints
		for (OWLAxiom ax : ontology.getTBoxAxioms(Imports.INCLUDED)){
			ax.accept(constraint_creator);
			if (!constraint_creator.isDataConstraint()){
				//TODO Filter axioms
				axioms.add(ax);
			}
		}
		
		
		for (OWLAxiom ax : ontology.getRBoxAxioms(Imports.INCLUDED)){
			ax.accept(constraint_creator);
			if (!constraint_creator.isDataConstraint()){
				//TODO Filter axioms
				axioms.add(ax);
			}
		}
		
		for (OWLAxiom ax : ontology.getABoxAxioms(Imports.INCLUDED)){
			//IF data is required to be transformed to DL clases (e.g. in IRIS)
			ax.accept(constraint_creator);			
		}
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology tbox_ontology = manager.createOntology(axioms, IRI.create("http://toolkit/constraints/ontology/TBoxAxioms.owl"));		
		
		
		return tbox_ontology;
		
	}
	
	
	public abstract void performDataConstraintValidation() throws Exception;
	
	protected abstract void setConstraintCreator();
	
	public abstract void printStatistics();
	
	

}
