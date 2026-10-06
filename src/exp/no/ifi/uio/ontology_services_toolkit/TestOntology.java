package no.ifi.uio.ontology_services_toolkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;
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

import uk.city.kg.validation.constraint.utils.Utility;
import uk.city.kg.validation.constraint.utils.pagoda_hermit.Timer;

/**
 * This class tests and modifies the Geological Ontology
 * @author ejimenez-ruiz
 *
 */
public class TestOntology {
	
	private OWLOntology classified_ontology;
	protected OWLOntology ontology;
	protected OWLDataFactory factory;
	protected Reasoner hermit;
	
	
	
	
	
	protected void loadOntology(String ontology_iri) {
		try {
		ontology = OWLManager.createConcurrentOWLOntologyManager().loadOntology(IRI.create(ontology_iri));
		factory = OWLManager.createConcurrentOWLOntologyManager().getOWLDataFactory();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void classifyOntology() {
	
		//This may avoid the exception due to datatypes outside OWL 2 specification
		Configuration conf = new Configuration();
		conf.ignoreUnsupportedDatatypes=true;
		
		Utility.print("Performing reasoning with HermiT...");
		hermit = new Reasoner(conf, ontology);
		hermit.classifyClasses();
		hermit.classifyObjectProperties();
		
		createClasssifiedOntology();
		Utility.println("Done");
		
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
        		   hermit, new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>());
           //InferredOntologyGenerator ontGen = new InferredOntologyGenerator(reasoner);
           
           
           ontGen.addGenerator(new InferredEquivalentClassAxiomGenerator());
           ontGen.addGenerator(new InferredSubClassAxiomGenerator());
           ontGen.addGenerator(new InferredInverseObjectPropertiesAxiomGenerator());
           //TODO Also add property characteristics and hierarchy?
           //Include parameter?
           ontGen.addGenerator(new InferredDataPropertyCharacteristicAxiomGenerator());	           
           ontGen.addGenerator(new InferredEquivalentDataPropertiesAxiomGenerator());
           ontGen.addGenerator(new InferredSubDataPropertyAxiomGenerator());
       
           ontGen.addGenerator(new InferredEquivalentObjectPropertyAxiomGenerator());
           ontGen.addGenerator(new InferredObjectPropertyCharacteristicAxiomGenerator());
           ontGen.addGenerator(new InferredSubObjectPropertyAxiomGenerator());
           
           
           
           
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
	
	
	protected void modifyAxiom() {
		
	}
	
	
	
	
	protected void removeUnnecessaryEquivalences() {
		
		String hasGeologicalAge = "http://no.sirius.ontology/geological-ontology#hasGeologicalAge";
		
		String hasDirectNext="http://no.sirius.ontology/geological-ontology#hasDirectNext";
		
		Set<OWLAxiom> toRemove = new HashSet<OWLAxiom>();
		Set<OWLAxiom> toAdd = new HashSet<OWLAxiom>();
		
		for (OWLClass cls : ontology.getClassesInSignature(Imports.INCLUDED)) {
			
			//System.out.println(cls);
			
			for (OWLEquivalentClassesAxiom ax : ontology.getEquivalentClassesAxioms(cls)){
				
				for (OWLClassExpression clsexp: ax.getClassExpressions()) {
					
					if (clsexp.isAnonymous()) {
											
					
						if (clsexp instanceof OWLDataAllValuesFrom) {
														
							//if (!((OWLDataAllValuesFrom)clsexp).getProperty().asOWLDataProperty().toStringID().equals(hasGeologicalAge)) {								
							toRemove.add(ax);
							toAdd.add(factory.getOWLSubClassOfAxiom(cls, clsexp));								
							//}
							
						}
						else if (clsexp instanceof OWLObjectAllValuesFrom) {
							
							//if (!((OWLObjectAllValuesFrom)clsexp).getProperty().asOWLObjectProperty().toStringID().equals(hasDirectNext)){
							toRemove.add(ax);
							toAdd.add(factory.getOWLSubClassOfAxiom(cls, clsexp));
							//}
							
						}
						else if (clsexp instanceof OWLDataSomeValuesFrom) {
							
							//if (!((OWLDataSomeValuesFrom)clsexp).getProperty().asOWLDataProperty().toStringID().equals(hasGeologicalAge)) {
							toRemove.add(ax);
							toAdd.add(factory.getOWLSubClassOfAxiom(cls, clsexp));
							//}
						}
						else if (clsexp instanceof OWLObjectSomeValuesFrom) {
							
							//if (!((OWLObjectSomeValuesFrom)clsexp).getProperty().asOWLObjectProperty().toStringID().equals(hasDirectNext)){
							toRemove.add(ax);
							toAdd.add(factory.getOWLSubClassOfAxiom(cls, clsexp));
							//}
						}
						
						//System.out.println("\t"+clsexp);
					}
				}
				
				
			}
				
			
			
			
		}
		
		
		ontology.getOWLOntologyManager().removeAxioms(ontology, toRemove);		
		
		ontology.getOWLOntologyManager().addAxioms(ontology, toAdd);
		
		
				
	}
	
	
	public void saveOntology(String phy_iri_onto) {
		
		try {
			ontology.getOWLOntologyManager().saveOntology(ontology, new RDFXMLDocumentFormat(), IRI.create(phy_iri_onto));
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void main (String args[]){
	
		
		String iri_onto= "file:/home/ejimenez-ruiz/Documents/SIRIUS/Image-Annotation/ontology/geological-ontology.owl";
		

		String iri_onto_out= "file:/home/ejimenez-ruiz/Documents/SIRIUS/Image-Annotation/ontology/geological-annotation-ontology.owl";
		
		
		
		TestOntology test = new TestOntology();
		test.loadOntology(iri_onto);
		test.removeUnnecessaryEquivalences();
		
		
		Timer t = new Timer();
		test.classifyOntology();
		Utility.println("Reasoning time: " + t.durationMilisecons()  + " (ms)");
		
		
		
		test.saveOntology(iri_onto_out);
		
		
	}
	
	
	

}
