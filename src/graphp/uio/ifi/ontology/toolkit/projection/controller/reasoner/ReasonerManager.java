package uio.ifi.ontology.toolkit.projection.controller.reasoner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uio.ifi.ontology.toolkit.constraint.utils.Utility;


public class ReasonerManager {
	
	
	public enum OWL2Reasoner {
		  NONE,
		  HERMIT,
		  ELK,
		  STRUCTURAL
		} ;

	/*public static int NONE = -1;
	public static int HERMIT = 0;
	public static int ELK = 1;
	public static int STRUCTURAL = 2;*/
	
	
	
	public static OWLReasoner getReasoner(OWLOntology ontology, OWL2Reasoner reasonerID) {
		if (reasonerID==OWL2Reasoner.HERMIT) {
			return getHermiTReasoner(ontology);
		}
		else if (reasonerID==OWL2Reasoner.ELK)
			return getELKReasoner(ontology);
		else
			return getStructuralReasoner(ontology);
	}
	
	
	
	public static OWLReasoner getELKReasoner(OWLOntology ontology) {
		
		Utility.print("Performing reasoning with ELK...");
		
		Logger.getLogger("org.semanticweb.elk").setLevel(Level.OFF);
		
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		
		
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		
		
		return reasoner;
		
		
	}
	
	
	
	public static OWLReasoner getHermiTReasoner(OWLOntology ontology) {
		
		Reasoner hermit;
		
		Configuration conf = new Configuration();
		conf.ignoreUnsupportedDatatypes=true;
		
		Utility.print("Performing reasoning with HermiT...");
		hermit = new Reasoner(conf, ontology);
		hermit.classifyClasses();
		hermit.classifyObjectProperties();
		
		
		return hermit;
		
	}
	
	
	public static OWLReasoner getStructuralReasoner(OWLOntology ontology) {
		
		Utility.print("Performing reasoning with OWL API Structural reasoner...");
		
		OWLReasoner reasoner = new StructuralReasonerExtended(ontology);
		
		return reasoner;
	}
	
	
	
	
	

}
