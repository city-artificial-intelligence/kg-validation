package uk.city.kg.validation.constraint.controller.rdfox;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import uk.city.kg.validation.constraint.controller.ConstraintValidator;
import uk.city.kg.validation.constraint.controller.iris.ConstraintCreatorForIRIS;
import uk.city.kg.validation.constraint.utils.Utility;
import uk.city.kg.validation.constraint.utils.pagoda_hermit.RuleHelperExtended;

/**
 * Test class to see the generated datalog clauses for the selected constraints
 */
public class TestConstraintValidator extends ConstraintValidator{

	public TestConstraintValidator(OWLOntology ontology) throws OWLOntologyCreationException {		
		// TODO Auto-generated constructor stub
		super(ontology);
	}

	@Override
	public void performDataConstraintValidation() throws Exception {
				
		String text_constraint_rules = RuleHelperExtended.getTextAndPrefixes(constraint_creator.getDataConstraintsClauses());
		Utility.println(text_constraint_rules);
		
		//constraint_creator.
		
		
	}

	@Override
	protected void setConstraintCreator() {
		//constraint_creator = new ConstraintCreatorForRDFox();
		constraint_creator = new ConstraintCreatorForIRIS();
		
	}

	@Override
	public void printStatistics() {
		// TODO Auto-generated method stub
		
	}

}
