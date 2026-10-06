package uk.city.kg.validation.constraint.utils;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;

import uk.city.kg.validation.constraint.controller.ConstraintValidator;
import uk.city.kg.validation.constraint.controller.rdfox.ConstraintCreatorForRDFox;
import uk.city.kg.validation.constraint.utils.pagoda_hermit.RuleHelperExtended;

public class OntologyToDatalogRules {

	Reasoner hermit;

	ConstraintCreatorForRDFox constraint_creator = new ConstraintCreatorForRDFox();
	
	public OntologyToDatalogRules(OWLOntology onto) throws OWLOntologyCreationException {
		

		//Ontology mostly in RL
		OWLOntology filtered_onto = filterConstraintsAndData(onto);
		
		
		Configuration conf = new Configuration();
		conf.ignoreUnsupportedDatatypes=true;
		
		hermit= new Reasoner(conf, filtered_onto);
		
		System.out.println(RuleHelperExtended.getTextAndPrefixes(hermit.getDLOntology().getDLClauses(),RuleHelperExtended.BRACKETS.SQUARE));
		
	}
	
	
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
	
	
	public static void main (String[] args) {
		
		OWLOntology onto;
		
		String onto_uri = "https://zenodo.org/record/3925544/files/SiriusGeoOnto_v1.0.owl";
		onto_uri = "https://zenodo.org/record/3925544/files/SiriusGeoAnnotator_InternalOntology_v1.0.owl";
		//onto_uri = "file:/home/ernesto/Desktop/test-hermit.owl";
		
		//TODO --------------------------------------------------
		//The output needs to be cleaned
		//Role chains seems to be ignored (must be added manually)
		//TODO --------------------------------------------------
		
		try {
			onto = OWLManager.createConcurrentOWLOntologyManager()
				.loadOntology(IRI.create(onto_uri));
			
			new OntologyToDatalogRules(onto);
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		
	
	
}
