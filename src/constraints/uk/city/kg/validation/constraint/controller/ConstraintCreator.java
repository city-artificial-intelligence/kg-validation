package uk.city.kg.validation.constraint.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


import org.semanticweb.HermiT.model.Atom;
import org.semanticweb.HermiT.model.AtomicConcept;
import org.semanticweb.HermiT.model.AtomicRole;
import org.semanticweb.HermiT.model.Constant;
import org.semanticweb.HermiT.model.Term;
import org.semanticweb.HermiT.model.DLClause;

import org.semanticweb.HermiT.model.Individual;

import org.semanticweb.HermiT.model.Variable;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

import uk.city.kg.validation.constraint.model.clause.NegatedAtom;
import uk.city.kg.validation.constraint.utils.Namespace;
import uk.city.kg.validation.constraint.utils.Utility;





/**
 * This visitor will filter data constraints in the TBox and create datalog rules for them.
 * For class/role assertions it will also create corresponding facts 
 * @author ernesto
 *
 */
public abstract class ConstraintCreator implements OWLAxiomVisitor {

	
	protected boolean isDataConstraint;
	protected int numberOfDataConstraint=0;
	
	protected Collection<DLClause> data_constraints = new HashSet<DLClause>();
	
	
	//public static final AtomicConcept THING=create("http://www.w3.org/2002/07/owl#Thing");
    //public static final AtomicConcept NOTHING=create("http://www.w3.org/2002/07/owl#Nothing");
    
	//We index axioms and reference them from clauses
	private Map<String, OWLAxiom> AxiomMap = new HashMap<String, OWLAxiom>();
	
	
	
	
	protected ConstraintTypeVisitor visitor = new ConstraintTypeVisitor();
	protected DataRangeVisitor range_visitor = new DataRangeVisitor();
	
	
	private int ax_index;
		
	public ConstraintCreator(){
		
		//Default constraints	
		data_constraints.add(getClauseForMaxCardViolation2Bottom());
		data_constraints.add(getClauseForMinCardViolation2Bottom());
		data_constraints.add(getClauseForRangeViolation2Bottom());
		
		ax_index=0;
	}
	
	
	public boolean isDataConstraint(){
		return isDataConstraint;
	}
	
	public int getNumberOfDataConstraints(){
		return numberOfDataConstraint;
	}
	
	
	
	public Collection<DLClause> getDataConstraintsClauses(){
		return data_constraints;
	}
	
	

	
	public Map<String, OWLAxiom> getDataConstraintAxiomsMap(){
		return AxiomMap;
	}
	
	
	
	
	
	
	
	
	protected Atom getBottomAtom(Variable x) {
		return Atom.create(AtomicConcept.NOTHING, x);
	}
	
	protected Atom getTopAtom(Variable x) {
		return Atom.create(AtomicConcept.THING, x);
	}
	
	
	
	protected Atom getRoleAtom(OWLObjectProperty oprop, Term x, Term y){
		return getRoleAtom(oprop.toStringID(), x, y);
	}
	
	protected Atom getRoleAtom(String oprop_iri, Term x, Term y){
		return Atom.create(AtomicRole.create(oprop_iri), x, y);
	}
	
	protected Atom getSameAsAtom( Term x, Term y){
		return getRoleAtom(Namespace.sameAS_iri, x, y);
	}
	
	
	protected Atom getNegatedRoleAtom(OWLObjectProperty oprop, Term x, Term y){
		return getNegatedRoleAtom(oprop.toStringID(), x, y);
	}
	
	protected Atom getNegatedRoleAtom(String oprop_iri, Term x, Term y){
		
		Term[] variables = new Term[2];
		variables[0] = x;
		variables[1] = y;
		
		return new NegatedAtom(AtomicRole.create(oprop_iri), variables);
		
		
		//return NegatedAtom.create(				
		//		AtomicRole.create(oprop_iri)
		//		, x, y);
	}
	
		
	protected Atom getClassAtom(OWLClass c, Term x) {
		return getClassAtom(c.toStringID(), x);
	}
	
	protected Atom getClassAtom(String cls_iri, Term x) {
		return Atom.create(AtomicConcept.create(cls_iri), x);
	}
	
	
	//TODO Abstracts methods
	protected abstract Atom getNegatedSameAsAtom(Term x, Term y);
	
	protected abstract Atom getDatatypeAtom(String datatype_iri, Term x);
	
	protected abstract Atom getDatatypeRangeConstraintAtom(String datatype_iri, Term x, List<String> constraints, boolean reverse_facet);
	
	protected abstract Atom getDatatypeDistinctValueAtom(String datatype_iri, Term x, String value);
	
	protected abstract Atom getNegatedDatatypeAtom(String filler_iri, Term y);


	
	
	protected String getFacetSymbol(String facet_symbol, boolean reverse_facet){
		
		if (reverse_facet)
			return Utility.getReversedFacetSymbol(facet_symbol);
				
		return facet_symbol;
		
		
	}
	
	
	
	
	
	protected Atom getNegatedClassAtom(OWLClass c, Term x) {
		return getNegatedClassAtom(c.toStringID(), x);
	}
	
	protected Atom getNegatedClassAtom(String cls_iri, Term x) {
		//return NegatedAtom.create(AtomicConcept.create(cls_iri), x);
		
		Term[] variables = new Term[1];
		variables[0] = x;		

		return new NegatedAtom(AtomicConcept.create(cls_iri), variables);
		
	}
	
	
	/**
	 * Fresh predicate/symbol URIS
	 * 
	 * For example: has_1_R_C
	 * @param p
	 * @param filler
	 * @param cardinality
	 * @return
	 */
	private String getNewFreshPredicateURI(OWLObjectProperty p, OWLClass filler, int cardinality){
		
		return getNewFreshPredicateURI(p.toStringID(), filler.toStringID(), cardinality);
		
		
	}
	
	/**
	 * Fresh predicate/symbol URIS for cardinalities
	 * @param p_iri
	 * @param filler_iri
	 * @param cardinality
	 * @return
	 */
	protected String getNewFreshPredicateURI(String p_iri, String filler_iri, int cardinality){
		
		String fresh_uri = Namespace.namespace_newatoms + "has_"+ cardinality + "_" + Utility.getEntityLabelFromURI(p_iri) + "_" + Utility.getEntityLabelFromURI(filler_iri);
		
		return fresh_uri;
		
		
	}
	
	
	/**
	 * For example: has_R_int_geq_1_leq_100
	 * @param p_iri
	 * @param filler_iri
	 * @param range_values
	 * @return
	 */
	protected String getFreshPredicateForDataRangeConstraint(String p_iri, String filler_iri, List<String> range_values){
		
				
		String fresh_uri = Namespace.namespace_newatoms + "has_"+ Utility.getEntityLabelFromURI(p_iri) + "_" + Utility.getEntityLabelFromURI(filler_iri) + getTextForm(range_values);
		
		return fresh_uri;		
		
	}
	
	
	
	/**
	 * For example has_R_nominal_ax1
	 * @param prop_iri
	 * @param value
	 * @param ax_id
	 * @return
	 */
	protected String getFreshPredicateForNominalConstraint(String prop_iri, String ax_id) {
		String fresh_uri = Namespace.namespace_newatoms + "has_"+ Utility.getEntityLabelFromURI(prop_iri) + "_Nominal_" + ax_id;
		
		return fresh_uri;		
	}
	
	
	
	
	
	/**
	 * Format: groups of  (i+0: symbol, i+1:text facet, i+2:value)
	 * @param range_values
	 * @return
	 */
	protected String getTextForm(List<String> range_values){
		
		String text="";
		
		for (int i=0; i<range_values.size()-2; i+=3){
			text+="_"+range_values.get(i+1)+"_"+range_values.get(i+2);	
		}
		
		return text;
	}
	
	
	/**
	 * for example: has_1_R
	 * @param p
	 * @param cardinality
	 * @return
	 */
	private String getNewFreshPredicateURI(OWLDataProperty p, int cardinality){
		
		return getNewFreshPredicateURI(p.toStringID(), cardinality);
				
	}
	
	/**
	 * for example: has_1_R
	 * @param p
	 * @param cardinality
	 * @return
	 */
	private String getNewFreshPredicateURI(OWLObjectProperty p, int cardinality){
		
		return getNewFreshPredicateURI(p.toStringID(), cardinality);
				
	}
	
	protected String getNewFreshPredicateURI(String p_iri, int cardinality){
		
		String fresh_uri = Namespace.namespace_newatoms + "has_"+ cardinality + "_" + Utility.getEntityLabelFromURI(p_iri);
					
		return fresh_uri;
				
	}
	
	

	
	
	
	
	private DLClause getHasNRSuccessorsClause(String prop_iri, int card){
		
		Variable X = Variable.create("X");
		Variable[] YVariables = new Variable[card];
		
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newClassAtom = getClassAtom(getNewFreshPredicateURI(prop_iri, card), X);		
		headAtoms[0] = newClassAtom;	
		
		
		//BODY OF THE RULE
		//In the body we will need: card number of atoms  +  (card * (card - 1))/2 combinations of Yi not_equal Yj
		int size = card + (card * (card - 1))/2;
		Atom[] bodyAtoms = new Atom[size];
				
		//R successors
		for (int i=0; i<card; i++){
			YVariables[i] = Variable.create("Y"+(i+1));		
			bodyAtoms[i] = getRoleAtom(prop_iri, X, YVariables[i]);
		}
		
		//Different R successors
		int index = card;
		for (int i=0; i<card-1; i++){
			for (int j=i+1; j<card; j++){
				bodyAtoms[index] = getNegatedSameAsAtom(YVariables[i], YVariables[j]);
				index++;
			}
			
		}
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	}
	
	
	protected DLClause getHasNRSuccessorsClause(String prop_iri, String filler_iri, int card, boolean datatypeProp){
		
		Variable X = Variable.create("X");
		Variable[] YVariables = new Variable[card];
		
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newClassAtom = getClassAtom(getNewFreshPredicateURI(prop_iri, filler_iri, card), X);		
		headAtoms[0] = newClassAtom;	
		
		
		//BODY OF THE RULE
		//In the body we will need: card number of atoms X 2  +  (card * (card - 1))/2 combinations of Yi not_equal Yj
		int size = card*2 + (card * (card - 1))/2;
		Atom[] bodyAtoms = new Atom[size];
				
		//R successors
		for (int i=0; i<card; i++){
			YVariables[i] = Variable.create("Y"+(i+1));		
			bodyAtoms[i] = getRoleAtom(prop_iri, X, YVariables[i]);
		}
		
		//Type of successors
		int index = card;
		for (int i=0; i<card; i++){					
			 
			//FILTER(DATATYPE(?Z) = xsd:integer)
			if (datatypeProp)
				bodyAtoms[index] = getDatatypeAtom(filler_iri, YVariables[i]); 
			else
				bodyAtoms[index] = getClassAtom(filler_iri, YVariables[i]);
			
			index++;
		}
		
		//Different R successors
		index = card*2;
		for (int i=0; i<card-1; i++){
			for (int j=i+1; j<card; j++){
				bodyAtoms[index] = getNegatedSameAsAtom(YVariables[i], YVariables[j]);
				index++;
			}
			
		}
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	}
	
	
	/**
	 * @param prop_iri
	 * @param filler_iri
	 * @param rangeValues
	 * @return
	 */
	private DLClause getHasMinMaxValueClause(String prop_iri, String filler_iri, List<String> rangeValues) {
		
		
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newClassAtom = getClassAtom(getFreshPredicateForDataRangeConstraint(prop_iri, filler_iri, rangeValues), X);			
		headAtoms[0] = newClassAtom;
		
		
		//BODY
		Atom[] bodyAtoms = new Atom[2];
		
		//R successor
		bodyAtoms[0] = getRoleAtom(prop_iri, X, Y);
		
		
		//Type of successor + constraints			
		bodyAtoms[1] = getDatatypeRangeConstraintAtom(filler_iri, Y, rangeValues, false); //we do not reverse the facet symbols
		
		
		return DLClause.create(headAtoms, bodyAtoms);
	}
	
	
	
	/**
	 * has_R_int(X, Y)
	 * 
	 * @param prop_iri
	 * @param filler_iri
	 * @param rangeValues
	 * @return
	 */
	protected DLClause getHasDataRSuccessorClause(String prop_iri, String filler_iri) {
		
		
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newClassAtom = getRoleAtom(getNewFreshPredicateURI(prop_iri, filler_iri, 1), X, Y);			
		headAtoms[0] = newClassAtom;
		
		
		//BODY
		Atom[] bodyAtoms = new Atom[2];
		
		//R successor
		bodyAtoms[0] = getRoleAtom(prop_iri, X, Y);
		
		
		//Type of successor
		bodyAtoms[1] = getDatatypeAtom(filler_iri, Y);
		
		
		return DLClause.create(headAtoms, bodyAtoms);
	}
	
	
	
	/**
	 * @param prop_iri
	 * @param filler_iri
	 * @param value
	 * @return
	 */
	private DLClause getHasDataValueNominalClause(String prop_iri, String value,  String datatype_iri, String ax_id) {
		
		
		Variable X = Variable.create("X");
		
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newClassAtom = getClassAtom(getFreshPredicateForNominalConstraint(prop_iri, ax_id), X);			
		headAtoms[0] = newClassAtom;
		
		
		//BODY
		Atom[] bodyAtoms = new Atom[1];
		
		//R successor
		Constant cvalue = getConstantTerm(value, datatype_iri);
		bodyAtoms[0] = getRoleAtom(prop_iri, X, cvalue);
				
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	
		
		
	}
	
	
	/**
	 * @param prop_iri
	 * @param value
	 * @param ax_id
	 * @return
	 */
	private DLClause getHasObjectValueNominalClause(String prop_iri, String indiv_iri, String ax_id) {
		
		Variable X = Variable.create("X");
		
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newClassAtom = getClassAtom(getFreshPredicateForNominalConstraint(prop_iri, ax_id), X);			
		headAtoms[0] = newClassAtom;
		
		
		//BODY
		Atom[] bodyAtoms = new Atom[1];
		
		//R successor
		Individual indiv = getIndividualConstant(indiv_iri);
		bodyAtoms[0] = getRoleAtom(prop_iri, X, indiv);
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	}
	
	
	
	
	
	
	


	/**
	 * @deprecated
	 * @param prop_iri
	 * @return
	 */
	private DLClause getHas2RSuccessorsClause(String prop_iri){
		
		Variable X = Variable.create("X"), Y1 = Variable.create("Y1"), Y2 = Variable.create("Y2"); 
		Atom[] bodyAtoms = new Atom[3];
		Atom[] headAtoms = new Atom[1];
		
		Atom newClassAtom = getClassAtom(getNewFreshPredicateURI(prop_iri, 2), X);
		
		headAtoms[0] = newClassAtom;	
		
		bodyAtoms[0] = getRoleAtom(prop_iri, X, Y1);
		bodyAtoms[1] = getRoleAtom(prop_iri, X, Y2);
		bodyAtoms[2] = getNegatedSameAsAtom(Y1, Y2);
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	}
	
	
	
	private DLClause getClauseForRangeViolation2Bottom(){		
		return getClauseForViolation2Bottom(Namespace.range_violation_iri);
	}
	
	
	
	private DLClause getClauseForMinCardViolation2Bottom(){		
		return getClauseForViolation2Bottom(Namespace.min_violation_iri);
	}
	
	
	private DLClause getClauseForMaxCardViolation2Bottom(){
		return getClauseForViolation2Bottom(Namespace.max_violation_iri);
	}
	
	
	private DLClause getClauseForViolation2Bottom(String violation_iri){
		
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		Atom[] bodyAtoms = new Atom[1];
		Atom[] headAtoms = new Atom[1];
				
		bodyAtoms[0] = getRoleAtom(violation_iri, X, Y);
		headAtoms[0] = getBottomAtom(X);
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	}
	
	
	protected Constant getAxiomsIDConstant(String ax_id){
		return Constant.create(ax_id, "http://www.w3.org/2001/XMLSchema#string");
	}
	
	private Constant getConstantTerm(String value, String datatype_iri){
		
		return Constant.create(value, datatype_iri);
	}
	
	
	private Individual getIndividualConstant(String indiv_iri){
		return Individual.create(indiv_iri);	
	}
	
	
	
	private void getClausesForFunctionality(String prop_iri, String ax_id){
		
		int card = 2;			
		
		//RULE 1
		data_constraints.add(getHasNRSuccessorsClause(prop_iri, card));
		
		
		//RULE 2: define violation: MaxCardViolation(?X,"Ax_i") :- has_N_R(?X)
		Variable X = Variable.create("X");
		Atom[] bodyAtoms = new Atom[1];
		Atom[] headAtoms = new Atom[1];
		
		Constant c = getAxiomsIDConstant(ax_id);
		
		bodyAtoms[0] = getClassAtom(getNewFreshPredicateURI(prop_iri, card), X);
		headAtoms[0] = getRoleAtom(Namespace.max_violation_iri, X, c);
		
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);
				
						
		/*System.out.println(clause.toString());			
		System.out.println(RuleHelper.getText(clause));
		System.out.println(RuleHelperExtended.getText(clause));
		*/
		
	}
	
	
	
		
	
	private void getClausesForMinCardinality(
			String cls, String prop_iri, String filler_iri, int card, boolean datatypeProp, String ax_id){
	
		//RULE 1		
		//if(datatypeProp) //in case we would like to avoid to mention the datatype
		//	data_constraints.add(getHasNRSuccessorsClause(prop_iri, card));
		//else
			data_constraints.add(getHasNRSuccessorsClause(prop_iri, filler_iri, card, datatypeProp));
				
				
		//RULE 2: define violation: MinCardViolation(?X,"Ax_i") :- A(?X), not has_N_R(?X)
		Variable X = Variable.create("X");
		Atom[] bodyAtoms = new Atom[2];
		Atom[] headAtoms = new Atom[1];
		
		Constant c = getAxiomsIDConstant(ax_id);
		
		bodyAtoms[0] = getClassAtom(cls, X);
		//if(datatypeProp)
		//	bodyAtoms[1] = getNegatedClassAtom(getNewClassURI(prop_iri, card), X);
		//else
			bodyAtoms[1] = getNegatedClassAtom(getNewFreshPredicateURI(prop_iri, filler_iri, card), X);
		
		headAtoms[0] = getRoleAtom(Namespace.min_violation_iri, X, c);
		
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);		
	}
	
	
	
	private void getClausesForMaxCardinality(
			String cls, String prop_iri, String filler_iri, int card, boolean datatypeProp, String ax_id){
	
		//RULE 1: note that we add card+1
		//if(datatypeProp)
		//	data_constraints.add(getHasNRSuccessorsClause(prop_iri, card+1));
		//else
			data_constraints.add(getHasNRSuccessorsClause(prop_iri, filler_iri, card+1, datatypeProp));
				
				
		//RULE 2: define violation: MaxCardViolation(?X,"Ax_i") :- A(?X), has_N_R(?X)
		Variable X = Variable.create("X");
		Atom[] bodyAtoms = new Atom[2];
		Atom[] headAtoms = new Atom[1];
		
		Constant c = getAxiomsIDConstant(ax_id);
		
		bodyAtoms[0] = getClassAtom(cls, X);
		//if(datatypeProp)
		//	bodyAtoms[1] = getClassAtom(getNewClassURI(prop_iri, card+1), X);
		//else
			bodyAtoms[1] = getClassAtom(getNewFreshPredicateURI(prop_iri, filler_iri,  card+1), X);
		headAtoms[0] = getRoleAtom(Namespace.max_violation_iri, X, c);
		
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);
	}
	
	
	private void getClausesForObjectValueRestriction(
			String cls, String prop_iri, String indiv_iri, String ax_id){
	
		//Only one rule required
		//RULE 1: define violation: MinCardViolation(?X,"Ax_i") :- A(?X), not R(?X, indiv)
		Variable X = Variable.create("X");
		Atom[] bodyAtoms = new Atom[2];
		Atom[] headAtoms = new Atom[1];
		
		Constant c = getAxiomsIDConstant(ax_id);
		Individual indiv = getIndividualConstant(indiv_iri);
		
		bodyAtoms[0] = getClassAtom(cls, X);
		bodyAtoms[1] = getNegatedRoleAtom(prop_iri, X, indiv);
		headAtoms[0] = getRoleAtom(Namespace.min_violation_iri, X, c);
		
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);
	}
	
	
	private void getClausesForDataValueRestriction(
			String cls, String prop_iri, String value, String datatype_iri, String ax_id){
	
		//Only one rule required
		//RULE 1: define violation: MinCardViolation(?X,"Ax_i") :- A(?X), not R(?X, value)
		Variable X = Variable.create("X");
		Atom[] bodyAtoms = new Atom[2];
		Atom[] headAtoms = new Atom[1];
		
		Constant c = getAxiomsIDConstant(ax_id);
		Constant cvalue = getConstantTerm(value, datatype_iri);
		
		bodyAtoms[0] = getClassAtom(cls, X);
		bodyAtoms[1] = getNegatedRoleAtom(prop_iri, X, cvalue);
		headAtoms[0] = getRoleAtom(Namespace.min_violation_iri, X, c);
		
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);
	}
	
	
	
	/**
	 * @param sub_cls
	 * @param propertyIRI
	 * @param rangeValues
	 * @param ax_id
	 */
	private void getClausesForOnlyObjectNominals(String cls, String prop_iri, List<String> rangeValues,
			String ax_id) {
		
		
		//RULE 1: define violation: RangeViolation(?X,"Ax_i") :- A(?X), R(?x, ?Y),  not sameas(y, a1), ..., not sameas(y, an)  
			
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		
		Atom[] headAtoms = new Atom[1];
		
		Constant c = getAxiomsIDConstant(ax_id);
		
		headAtoms[0] = getRoleAtom(Namespace.range_violation_iri, X, c);
				
		Atom[] bodyAtoms = new Atom[rangeValues.size()+2];
		
		//Class
		bodyAtoms[0] = getClassAtom(cls, X);
				
		//R successor
		bodyAtoms[1] = getRoleAtom(prop_iri, X, Y);
		
		
		//Check with values in object nominals
		for (int i=0; i<rangeValues.size(); i++){			
			bodyAtoms[i+2] = getNegatedSameAsAtom(Y, getIndividualConstant(rangeValues.get(i)));
		}		
		
		//RULE 1	
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);		
		
		
	}


	/**
	 * @param sub_cls
	 * @param propertyIRI
	 * @param fillerIRI
	 * @param rangeValues
	 * @param ax_id
	 */
	private void getClausesForOnlyDataNominals(String cls, String prop_iri, String filler_iri,
			List<String> rangeValues, String ax_id) {
		
		
		//RULE 0: range type as well. Otherwise wrong types will always be valid. (not necessary here)
		//getClausesForOnlyDatatypeRestriction(cls, prop_iri, filler_iri, ax_id);
				
		
				
		//RULE 1: define violation: RangeViolation(?X,"Ax_i") :- A(?X), R(?x, ?Y),  Y != a1, Y != a2, ..., Y != an  
						
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		
		Atom[] headAtoms = new Atom[1];
		
		Constant c = getAxiomsIDConstant(ax_id);
		
		headAtoms[0] = getRoleAtom(Namespace.range_violation_iri, X, c);
				
		Atom[] bodyAtoms = new Atom[rangeValues.size()+2];
		
		//Class
		bodyAtoms[0] = getClassAtom(cls, X);
				
		//R successor
		bodyAtoms[1] = getRoleAtom(prop_iri, X, Y);
		
		
		//Check with values in data nominals
		for (int i=0; i<rangeValues.size(); i++){			
			bodyAtoms[i+2] = getDatatypeDistinctValueAtom(filler_iri, Y, rangeValues.get(i)); // Y != ai
			//System.out.println(bodyAtoms[i+2]);
		}		
		
		
		
		//RULE 1	
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);		
		
	}
	
	
	/**
	 * @param sub_cls
	 * @param propertyIRI
	 * @param fillerIRI
	 * @param ax_id
	 */
	protected abstract void getClausesForOnlyDatatypeRestriction(String cls, String prop_iri, String filler_iri, String ax_id);

	
	



	/**
	 * @param sub_cls
	 * @param propertyIRI
	 * @param fillerIRI
	 * @param rangeValues
	 * @param ax_id
	 */
	private void getClausesForOnlyDatatypeFacet(String cls, String prop_iri, String filler_iri,
			List<String> rangeValues, String ax_id) {
		
		
		//RULE 0: range type as well. Otherwise wrong types will always be valid. Inside or ourside interval
		getClausesForOnlyDatatypeRestriction(cls, prop_iri, filler_iri, ax_id);
		
		
		//RULE 1: define violation: RangeViolation(?X,"Ax_i") :- A(?X), R(?x, ?Y),  Y < 1 (min value violation)
		//RULE 2: define violation: RangeViolation(?X,"Ax_i") :- A(?X), R(?x, ?Y),  Y > 100 (max value violation)  (Optional)
		
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		
		Atom[] bodyAtoms1 = new Atom[3];
		Atom[] headAtoms = new Atom[1];
				
		Constant c = getAxiomsIDConstant(ax_id);
		
		headAtoms[0] = getRoleAtom(Namespace.range_violation_iri, X, c);
		
		
		List<List<String>> rangeValuesList = splitRangeValues(rangeValues);
		
		//Class
		bodyAtoms1[0] = getClassAtom(cls, X);
				
		//R successor
		bodyAtoms1[1] = getRoleAtom(prop_iri, X, Y);

		//value violation 1
		bodyAtoms1[2] = getDatatypeRangeConstraintAtom(filler_iri, Y, rangeValuesList.get(0), true); //reverse constraint

		//RULE 1	
		DLClause clause1 = DLClause.create(headAtoms, bodyAtoms1);
		data_constraints.add(clause1);		

		
		//In case the restriction facet included two (lower and upper) limit values
		if (rangeValuesList.size()>1){
		
			Atom[] bodyAtoms2 = new Atom[3];
			//class
			bodyAtoms2[0] = getClassAtom(cls, X);
			//R
			bodyAtoms2[1] = getRoleAtom(prop_iri, X, Y);
			//value violation 2
			bodyAtoms2[2] = getDatatypeRangeConstraintAtom(filler_iri, Y, rangeValuesList.get(1), true);  //reverse constraint					
			
			//RULE 2
			DLClause clause2 = DLClause.create(headAtoms, bodyAtoms2);
			data_constraints.add(clause2);		
		}
		
	}
	
	
	private List<List<String>> splitRangeValues(List<String> constraints){
		
		List<List<String>> rangeValuesList = new ArrayList<List<String>>();  
		
		//Format constraints: groups of  (i+0: symbol, i+1:text facet, i+2:value)
		int j=0;
		for (int i=0; i<constraints.size()-2; i+=3){
			rangeValuesList.add(new ArrayList<String>());
			rangeValuesList.get(j).add(constraints.get(i));
			rangeValuesList.get(j).add(constraints.get(i+1));
			rangeValuesList.get(j).add(constraints.get(i+2));
			j++;
		}
		
		return rangeValuesList;
	}


	/**
	 * @param sub_cls
	 * @param propertyIRI
	 * @param rangeValues
	 * @param ax_id
	 */
	private void getClausesForSomeObjectNominals(String cls, String prop_iri, List<String> rangeValues,
			String ax_id) {
		
		
		//Object nominals: {indiv1, indiv2, indiv3, etc.} . 
		//RULE 1: has_R_nominal_axID(?X):- R(?X, indiv1)
		//RULE 2: has_R_nominal_axID((?X):- R(?X, indiv2)
		//RULE n: has_R_nominal_axID((?X):- R(?X, indivn)
		for (String value : rangeValues)
			data_constraints.add(getHasObjectValueNominalClause(prop_iri, value, ax_id));
		
		
		//RULE n+1: define violation: MinCardViolation(?X,"Ax_i") :- A(?X), not has_R_Nominal_Ax_i(?X)
		Variable X = Variable.create("X");
		Atom[] bodyAtoms = new Atom[2];
		Atom[] headAtoms = new Atom[1];
		
		Constant c = getAxiomsIDConstant(ax_id);
		
		bodyAtoms[0] = getClassAtom(cls, X);
		
		bodyAtoms[1] = getNegatedClassAtom(getFreshPredicateForNominalConstraint(prop_iri, ax_id), X);
		
		headAtoms[0] = getRoleAtom(Namespace.min_violation_iri, X, c);
		
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);		

		
		
	}


	


	/**
	 * @param sub_cls
	 * @param propertyIRI
	 * @param fillerIRI
	 * @param rangeValues
	 * @param ax_id
	 */
	private void getClausesForSomeDataNominals(String cls, String prop_iri, String filler_iri,
			List<String> rangeValues, String ax_id) {
		
		
		//Data nominals: {s1, s2, s3, etc.} . 
		//RULE 1: has_R_nominal_axID(?X):- R(?X, s1)
		//RULE 2: has_R_nominal_axID((?X):- R(?X, s2)
		//RULE n: has_R_nominal_axID((?X):- R(?X, sn)
		for (String value : rangeValues)
			data_constraints.add(getHasDataValueNominalClause(prop_iri, value, filler_iri, ax_id));
		
		
		//RULE n+1: define violation: MinCardViolation(?X,"Ax_i") :- A(?X), not has_R_Nominal_Ax_i(?X)
		Variable X = Variable.create("X");
		Atom[] bodyAtoms = new Atom[2];
		Atom[] headAtoms = new Atom[1];
		
		Constant c = getAxiomsIDConstant(ax_id);
		
		bodyAtoms[0] = getClassAtom(cls, X);
		
		bodyAtoms[1] = getNegatedClassAtom(getFreshPredicateForNominalConstraint(prop_iri, ax_id), X);
		
		headAtoms[0] = getRoleAtom(Namespace.min_violation_iri, X, c);
		
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);		

			
		
	}


	


	/**
	 * @param sub_cls
	 * @param propertyIRI
	 * @param fillerIRI
	 * @param rangeValues
	 * @param datatypeProp
	 * @param ax_id
	 */
	private void getClausesForSomeDatatypeFacet(String cls, String prop_iri, String filler_iri,
			List<String> rangeValues, String ax_id) {
		// 
		
		//RULE 1: has_R_int_1_100(?X):- R(?X, ?Y), int(?Y), Y > 1, Y < 100
		data_constraints.add(getHasMinMaxValueClause(prop_iri, filler_iri, rangeValues));
				
				
		//RULE 2: define violation: MinCardViolation(?X,"Ax_i") :- A(?X), not has_R_int_1_100(?X)
		Variable X = Variable.create("X");
		Atom[] bodyAtoms = new Atom[2];
		Atom[] headAtoms = new Atom[1];
		
		Constant c = getAxiomsIDConstant(ax_id);
		
		bodyAtoms[0] = getClassAtom(cls, X);
		
		bodyAtoms[1] = getNegatedClassAtom(getFreshPredicateForDataRangeConstraint(prop_iri, filler_iri, rangeValues), X);
		
		headAtoms[0] = getRoleAtom(Namespace.min_violation_iri, X, c);
		
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);		

		
		
	}

	
	
	
	
	

	
	


	protected DLClause getFactForClassAssertion(OWLClass cls, OWLNamedIndividual ind){
		
		
		Constant indivConstant = Constant.create(ind.toStringID(), "http://www.w3.org/2001/XMLSchema#string");
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newClassAtom = getClassAtom(cls, indivConstant);		
		headAtoms[0] = newClassAtom;	
		
		
		//BODY OF THE RULE: empty
		Atom[] bodyAtoms = new Atom[0];
				
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	}
	
	protected DLClause getFactForDatatypeMembership(OWLLiteral literal){
		
		Constant constant = Constant.create(literal.getLiteral(), "http://www.w3.org/2001/XMLSchema#string");  //literal.getDatatype().toStringID()
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newClassAtom = getClassAtom(literal.getDatatype().toStringID(), constant);		
		headAtoms[0] = newClassAtom;	
		
		
		//BODY OF THE RULE: empty
		Atom[] bodyAtoms = new Atom[0];
				
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	}
	
	
	protected DLClause getFactForObjectRoleAssertion(OWLObjectProperty prop, OWLNamedIndividual ind_s, OWLNamedIndividual ind_t){
		
		Constant indivSourceConstant = Constant.create(ind_s.toStringID(), "http://www.w3.org/2001/XMLSchema#string");
		Constant indivTargetConstant = Constant.create(ind_t.toStringID(), "http://www.w3.org/2001/XMLSchema#string");
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newRoleAtom = getRoleAtom(prop.toStringID(), indivSourceConstant, indivTargetConstant);		
		headAtoms[0] = newRoleAtom;	
		
		
		
		//BODY OF THE RULE: empty
		Atom[] bodyAtoms = new Atom[0];
				
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	}
	
	
	
	protected DLClause getFactForSameAs(OWLNamedIndividual ind_s, OWLNamedIndividual ind_t){
		
		Constant indivSourceConstant = Constant.create(ind_s.toStringID(), "http://www.w3.org/2001/XMLSchema#string");
		Constant indivTargetConstant = Constant.create(ind_t.toStringID(), "http://www.w3.org/2001/XMLSchema#string");
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newRoleAtom = getSameAsAtom(indivSourceConstant, indivTargetConstant);		
		headAtoms[0] = newRoleAtom;	
		
		
		
		//BODY OF THE RULE: empty
		Atom[] bodyAtoms = new Atom[0];
				
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	}
	
	
	
	
	protected DLClause getFactForDataRoleAssertion(OWLDataProperty prop, OWLNamedIndividual ind_s, OWLLiteral literal){
		
		Constant indivSourceConstant = Constant.create(ind_s.toStringID(), "http://www.w3.org/2001/XMLSchema#string");
		Constant targetConstant = Constant.create(literal.getLiteral(), "http://www.w3.org/2001/XMLSchema#string");  //literal.getDatatype().toStringID()
		
		
		//getDatatypeAtom(filler_iri, YVariables[i])
		
		//HEAD of the RULE
		Atom[] headAtoms = new Atom[1];		
		Atom newClassAtom = getRoleAtom(prop.toStringID(), indivSourceConstant, targetConstant);		
		headAtoms[0] = newClassAtom;	
		
		
		
		//BODY OF THE RULE: empty
		Atom[] bodyAtoms = new Atom[0];
				
		
		return DLClause.create(headAtoms, bodyAtoms);
		
	}
	
	
	
	
	
	@Override
	public void visit(OWLFunctionalObjectPropertyAxiom ax) {
				
		isDataConstraint=false;
		
		
		if (!ax.getProperty().isAnonymous()){
			
			isDataConstraint=true;
			numberOfDataConstraint++;
			String ax_id = "Ax"+ax_index++;
			AxiomMap.put(ax_id, ax);
						
			getClausesForFunctionality(ax.getProperty().asOWLObjectProperty().toStringID(), ax_id);
			
		}
		
	}
	
	
	
	@Override
	public void visit(OWLFunctionalDataPropertyAxiom ax) {
		
		isDataConstraint=false;		
		
		
		if (!ax.getProperty().isAnonymous()){
			
			isDataConstraint=true;
			numberOfDataConstraint++;
			String ax_id = "Ax"+ax_index++;
			AxiomMap.put(ax_id, ax);
			
			getClausesForFunctionality(ax.getProperty().asOWLDataProperty().toStringID(), ax_id);
			
		}			
	}
	
	
	@Override
	public void visit(OWLSubClassOfAxiom ax) {
		isDataConstraint=false;		
		
		if (!ax.getSubClass().isAnonymous() && ax.getSuperClass().isAnonymous()){
			String sub_cls = ax.getSubClass().asOWLClass().toStringID();			
			ax.getSuperClass().accept(visitor);			
			
			//System.out.println(ax);
			if (isDataConstraint){
				numberOfDataConstraint++;
				String ax_id = "Ax"+ax_index++;
				AxiomMap.put(ax_id, ax);
				
				switch (visitor.getConstraintType()) {
	            	case MIN:
	            		getClausesForMinCardinality(
	            				sub_cls, 
	            				visitor.getPropertyIRI(),
	            				visitor.getFillerIRI(),
	            				visitor.getCardinality(),
	            				visitor.isDatatypeProp(),
	            				ax_id);
	            		break;
	            		
	            	case MAX:
	            		getClausesForMaxCardinality(
	            				sub_cls, 
	            				visitor.getPropertyIRI(),
	            				visitor.getFillerIRI(),
	            				visitor.getCardinality(),
	            				visitor.isDatatypeProp(),
	            				ax_id);
	            		break;
	            		
	            	case EXACT: //we add both restrictions
	            		getClausesForMinCardinality(
	            				sub_cls, 
	            				visitor.getPropertyIRI(),
	            				visitor.getFillerIRI(),
	            				visitor.getCardinality(),
	            				visitor.isDatatypeProp(),
	            				ax_id);
	            		getClausesForMaxCardinality(
	            				sub_cls, 
	            				visitor.getPropertyIRI(),
	            				visitor.getFillerIRI(),
	            				visitor.getCardinality(),
	            				visitor.isDatatypeProp(),
	            				ax_id);
	            		break;
	            		
	            	case DATAVALUE:
	            		getClausesForDataValueRestriction(
	            				sub_cls, 
	            				visitor.getPropertyIRI(),
	            				visitor.getValue(),
	            				visitor.getDatatypeIRI(),
	            				ax_id);
	            		break;
	            		
	            	case OBJECTVALUE:
	            		getClausesForObjectValueRestriction(
	            				sub_cls, 
	            				visitor.getPropertyIRI(),
	            				visitor.getFillerIRI(),
	            				ax_id);
	            		break;
	            	
	            		
	            	case SOME_DATATYPE_FACET:
	            		
	            		getClausesForSomeDatatypeFacet(
	            				sub_cls,
	            				visitor.getPropertyIRI(),
	            				visitor.getFillerIRI(), //datatype
	            				visitor.getRangeValues(),	            				
	            				ax_id);
	            		
	            		break;
	            		
	            	case SOME_DATA_NOMINALS:
	            		            		
	            		getClausesForSomeDataNominals(
	            				sub_cls,
	            				visitor.getPropertyIRI(),
	            				visitor.getFillerIRI(), //datatype
	            				visitor.getRangeValues(),	            			
	            				ax_id);	            	
	            		
	            		break;
	            		
	            	case SOME_OBJECT_NOMINALS:
	            		
            			getClausesForSomeObjectNominals(
            				sub_cls,
            				visitor.getPropertyIRI(),            				
            				visitor.getRangeValues(),
            				ax_id);
            	
            		
            			break;
            			
	            	case ONLY_DATA_REST:
	            		getClausesForOnlyDatatypeRestriction(
	            				sub_cls, 
	            				visitor.getPropertyIRI(),
	            				visitor.getFillerIRI(),	//datatype			
	            				ax_id);
	            		break;
	            		
	            	case ONLY_DATATYPE_FACET:
	            		
	            		getClausesForOnlyDatatypeFacet(
	            				sub_cls,
	            				visitor.getPropertyIRI(),
	            				visitor.getFillerIRI(), //datatype
	            				visitor.getRangeValues(),	            				
	            				ax_id);
	            		
	            		break;
	            		
	            	case ONLY_DATA_NOMINALS:
	            		
            			getClausesForOnlyDataNominals(
            				sub_cls,
            				visitor.getPropertyIRI(),
            				visitor.getFillerIRI(), //datatype
            				visitor.getRangeValues(),	            			
            				ax_id);	            	
            		
            			break;
            		
	            	case ONLY_OBJECT_NOMINALS:
            		
	        			getClausesForOnlyObjectNominals(
	        				sub_cls,
	        				visitor.getPropertyIRI(),            				
	        				visitor.getRangeValues(),
	        				ax_id);
	        	
	        		
	        			break;
	            		
	            	
				}			
			}
			
		}				
	}
	
	


	@Override
	public void visit(OWLEquivalentClassesAxiom ax) {
		isDataConstraint=false;
		//TODO Split equivalences?
		//TODO Better to split/normalise them outside the checker? Also intersection on the right and union on the left
		//Currently we do not consider them in SOMM
		
	}
	
	
	
	@Override
	public void visit(OWLObjectPropertyAssertionAxiom ax) {
		
		isDataConstraint=false;
		
	}
	
	
	@Override
	public void visit(OWLDataPropertyAssertionAxiom ax) {
		isDataConstraint=false;
		
		
	}
	
	
	@Override
	public void visit(OWLClassAssertionAxiom ax) {
		isDataConstraint=false;
		
		
	}
	
	@Override
	public void visit(OWLSameIndividualAxiom ax) {
		isDataConstraint=false;
			
		
	}

	
	


	
	
	public enum CONSTRAINT_TYPE {		
		//Extend with Nominals: dataoneof and objectoneof
		//Also with datatype restrictions
		//Additionally with range restrictions not supported in RL: for_all + dataype_rest/oneof  
		//We also add as a range constraint data only restrictions A sub R only int -> which may be useful to validate data using different datatypes from the ontology (common case in practise) 
	    MIN, MAX, EXACT, DATAVALUE, OBJECTVALUE, ONLY_DATA_REST, 
	    SOME_DATATYPE_FACET, SOME_DATA_NOMINALS, SOME_OBJECT_NOMINALS, ONLY_DATATYPE_FACET, ONLY_DATA_NOMINALS, ONLY_OBJECT_NOMINALS
	}
	
	
	protected class ConstraintTypeVisitor implements OWLClassExpressionVisitor {
		
		
		private String filler_iri;
		private String prop_iri;		
		private int card;		
		private CONSTRAINT_TYPE type;
		private boolean datatypeProp;
		//List of literals, list of individuals (IRIS) or min/max constraints
		private List<String> range_values = new ArrayList<String>();
				
		private String value;
		private String datatype_iri;
		
		public String getPropertyIRI(){
			return prop_iri;
		}
		
		public String getFillerIRI(){
			return filler_iri;
		}
		
		public int getCardinality(){
			return card;
		}
		
		public CONSTRAINT_TYPE getConstraintType(){
			return type;
		}
		
		public String getValue(){
			return value;
		}
		
		public List<String> getRangeValues(){
			return range_values;
		}
		
		public String getDatatypeIRI(){
			return datatype_iri;
		}
		
		public boolean isDatatypeProp(){
			return datatypeProp;
		}
		
	
		
		@Override
		public void visit(OWLDataSomeValuesFrom cls_exp) {
			
			isDataConstraint=false;		
			
			if (!cls_exp.getProperty().isAnonymous()){
				
				prop_iri = cls_exp.getProperty().asOWLDataProperty().toStringID();				
				datatypeProp=true;
				card = 1;
				
				if (cls_exp.getFiller().isDatatype()){				
					
					isDataConstraint=true;
										
					filler_iri = cls_exp.getFiller().asOWLDatatype().toStringID();					
					type = CONSTRAINT_TYPE.MIN;
					
				}
				else {
					cls_exp.getFiller().accept(range_visitor);
					if (range_visitor.isDatatypeRestrictionRange()){						
					
						isDataConstraint=true;
						
						filler_iri = range_visitor.getDatatypeString();						
						range_values = range_visitor.getRangeValues();
						
						type = CONSTRAINT_TYPE.SOME_DATATYPE_FACET;
						
						
					}
					else if (range_visitor.isEnumerationRange()){
						isDataConstraint=true;
						
						filler_iri = range_visitor.getDatatypeString();
						//System.out.println(filler_iri);
						range_values = range_visitor.getRangeValues();
						
						type = CONSTRAINT_TYPE.SOME_DATA_NOMINALS;
						
					}
					
					
				}
				
			}
			
			
		}
	
		@Override
		public void visit(OWLDataAllValuesFrom cls_exp) {
			
			isDataConstraint=false;
			
			//We only take into account the cases restricting the range with list of values or a datatype range
			//Since they are not included in RL and they represent a typical and useful constraint
			
			
			if (!cls_exp.getProperty().isAnonymous()){
			
				prop_iri = cls_exp.getProperty().asOWLDataProperty().toStringID();				
				datatypeProp=true;
				
				
				//Useful to control the validation of data
				if (cls_exp.getFiller().isDatatype()){				
					
					isDataConstraint=true;
										
					filler_iri = cls_exp.getFiller().asOWLDatatype().toStringID();					
					type = CONSTRAINT_TYPE.ONLY_DATA_REST;
					
				}
				else{
					cls_exp.getFiller().accept(range_visitor);
					if (range_visitor.isDatatypeRestrictionRange()){						
					
						isDataConstraint=true;
						
						filler_iri = range_visitor.getDatatypeString();						
						range_values = range_visitor.getRangeValues();
						
						type = CONSTRAINT_TYPE.ONLY_DATATYPE_FACET;
						
						
					}
					else if (range_visitor.isEnumerationRange()){
						
						isDataConstraint=true;
						
						filler_iri = range_visitor.getDatatypeString();
						range_values = range_visitor.getRangeValues();
						
						type = CONSTRAINT_TYPE.ONLY_DATA_NOMINALS;
						
					}
				}
			}				
			
		}
	
		@Override
		public void visit(OWLDataHasValue cls_exp) {
			
			isDataConstraint=false;
					
			if (!cls_exp.getProperty().isAnonymous()){
				
				isDataConstraint=true;
				
				prop_iri = cls_exp.getProperty().asOWLDataProperty().toStringID();
				value = cls_exp.getFiller().getLiteral(); //.getValue().getLiteral();
				datatype_iri = cls_exp.getFiller().getDatatype().toStringID();//.getValue().getDatatype().toStringID();
				type = CONSTRAINT_TYPE.DATAVALUE;
				datatypeProp=true;
				
			}
			
					
		}
	
		@Override
		public void visit(OWLDataMinCardinality cls_exp) {
			
			isDataConstraint=false;
			
			
			if (!cls_exp.getProperty().isAnonymous() && cls_exp.getFiller().isDatatype()){
				
				isDataConstraint=true;
				
				prop_iri = cls_exp.getProperty().asOWLDataProperty().toStringID();
				filler_iri = cls_exp.getFiller().asOWLDatatype().toStringID();
				card = cls_exp.getCardinality();
				type = CONSTRAINT_TYPE.MIN;
				datatypeProp=true;
				
			}
			
			
		}
	
		@Override
		public void visit(OWLDataExactCardinality cls_exp) {
			
			isDataConstraint=false;
					
			if (!cls_exp.getProperty().isAnonymous() && cls_exp.getFiller().isDatatype()){
				
				isDataConstraint=true;
				
				prop_iri = cls_exp.getProperty().asOWLDataProperty().toStringID();
				filler_iri = cls_exp.getFiller().asOWLDatatype().toStringID();
				card = cls_exp.getCardinality();
				type = CONSTRAINT_TYPE.EXACT;
				datatypeProp=true;
			}
			
		}
	
		@Override
		public void visit(OWLDataMaxCardinality cls_exp) {
			
			isDataConstraint=false;
			
			if (!cls_exp.getProperty().isAnonymous() && cls_exp.getFiller().isDatatype()){
				
				isDataConstraint=true;
				
				prop_iri = cls_exp.getProperty().asOWLDataProperty().toStringID();
				filler_iri = cls_exp.getFiller().asOWLDatatype().toStringID();
				card = cls_exp.getCardinality();
				type = CONSTRAINT_TYPE.MAX;
				datatypeProp=true;
				
			}
			
			
		}
		
		
		protected boolean containsListOfNominals(OWLClassExpression cls_exp){
			
			range_values.clear();
			
			if (!(cls_exp instanceof OWLObjectOneOf))
				return false;
														
			OWLObjectOneOf nominals = (OWLObjectOneOf)cls_exp;
				
			for (OWLIndividual indiv : nominals.getIndividuals()){
					
				if (indiv.isNamed())
					range_values.add(indiv.asOWLNamedIndividual().toStringID());
					
			}
			
			return range_values.size()>0; 
			
			
		}
		
		
		
		
		@Override
		public void visit(OWLObjectSomeValuesFrom cls_exp) {
			
			isDataConstraint=false;
			
			
			if (!cls_exp.getProperty().isAnonymous()){
				
				prop_iri = cls_exp.getProperty().asOWLObjectProperty().toStringID();
				datatypeProp=false;
				card = 1;
				
				if (!cls_exp.getFiller().isAnonymous()){
			
					isDataConstraint=true;					
					
					filler_iri = cls_exp.getFiller().asOWLClass().toStringID();
					
					type = CONSTRAINT_TYPE.MIN;
					
				}
				else if (containsListOfNominals(cls_exp.getFiller())){										
					
					isDataConstraint=true;
					
					//range_values filled inside the method
					
					//No filler_iri required
															
					type = CONSTRAINT_TYPE.SOME_OBJECT_NOMINALS;
					
				}
			}		
			
			
			
		}
	
		@Override
		public void visit(OWLObjectAllValuesFrom cls_exp) {
			
			isDataConstraint=false;
			
			//We only take into account the cases restricting the range with list of values or a datatype range
			//Since they are not included in RL and they represent a typical and useful constraint
		
			
			if (!cls_exp.getProperty().isAnonymous()){
				
				prop_iri = cls_exp.getProperty().asOWLObjectProperty().toStringID();
				datatypeProp=false;
						
				if (containsListOfNominals(cls_exp.getFiller())){										
				
					isDataConstraint=true;
					
					//range_values filled inside the method
					
					//No filler_iri required
															
					type = CONSTRAINT_TYPE.ONLY_OBJECT_NOMINALS;
				}	
			}
			
			
			
					
		}
	
		@Override
		public void visit(OWLObjectHasValue cls_exp) {
			
			isDataConstraint=false;
					
			if (!cls_exp.getProperty().isAnonymous() && cls_exp.getFiller().isNamed()){ //also accessed as getFiller?
						
				isDataConstraint=true;
				
				prop_iri = cls_exp.getProperty().asOWLObjectProperty().toStringID();
				filler_iri = cls_exp.getFiller().asOWLNamedIndividual().toStringID();
				type = CONSTRAINT_TYPE.OBJECTVALUE;
				datatypeProp=false;
				
			}
			
			
			
		}
	
		@Override
		public void visit(OWLObjectMinCardinality cls_exp) {
			
			isDataConstraint=false;
			
			
			if (!cls_exp.getProperty().isAnonymous() && !cls_exp.getFiller().isAnonymous()){
				
				isDataConstraint=true;
				
				prop_iri = cls_exp.getProperty().asOWLObjectProperty().toStringID();
				filler_iri = cls_exp.getFiller().asOWLClass().toStringID();
				card = cls_exp.getCardinality();
				type = CONSTRAINT_TYPE.MIN;
				datatypeProp=false;
				
			}
			
		}
	
		@Override
		public void visit(OWLObjectExactCardinality cls_exp) {
			
			isDataConstraint=false;
			
			if (!cls_exp.getProperty().isAnonymous() && !cls_exp.getFiller().isAnonymous()){
				
				isDataConstraint=true;
				
				prop_iri = cls_exp.getProperty().asOWLObjectProperty().toStringID();
				filler_iri = cls_exp.getFiller().asOWLClass().toStringID();
				card = cls_exp.getCardinality();
				type = CONSTRAINT_TYPE.EXACT;
				datatypeProp=false;
				
			}
			
		}
	
		@Override
		public void visit(OWLObjectMaxCardinality cls_exp) {
			
			isDataConstraint=false;
			
			if (!cls_exp.getProperty().isAnonymous() && !cls_exp.getFiller().isAnonymous()){
				
				isDataConstraint=true;
				
				prop_iri = cls_exp.getProperty().asOWLObjectProperty().toStringID();
				filler_iri = cls_exp.getFiller().asOWLClass().toStringID();
				card = cls_exp.getCardinality();
				type = CONSTRAINT_TYPE.MAX;
				datatypeProp=false;
				
			}
			
		}
	
		
		
		
		
		@Override
		public void visit(OWLObjectHasSelf cls_exp) {		
			
			isDataConstraint=false;
			
			
		}
		
		
		
		
		@Override
		public void visit(OWLClass cls_exp) {	
			isDataConstraint=false;
		}
	
		@Override
		public void visit(OWLObjectIntersectionOf cls_exp) {
			isDataConstraint=false;
			
		}
	
		@Override
		public void visit(OWLObjectUnionOf cls_exp) {
			isDataConstraint=false;
			
		}
	
		@Override
		public void visit(OWLObjectComplementOf cls_exp) {
			isDataConstraint=false;
			
		}
	
	
	
		@Override
		public void visit(OWLObjectOneOf cls_exp) {
			isDataConstraint=false;
			
		}
	}
	//end visitor class expression: cardinalities/range visitor
	



	
	
	
	//OTHER AXIOMS
	

	@Override
	public void visit(OWLAnnotationAssertionAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLSubAnnotationPropertyOfAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLAnnotationPropertyDomainAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLAnnotationPropertyRangeAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLDeclarationAxiom arg0) {
		isDataConstraint=false;
		
	}


	


	@Override
	public void visit(OWLNegativeObjectPropertyAssertionAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLAsymmetricObjectPropertyAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLReflexiveObjectPropertyAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLDisjointClassesAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLDataPropertyDomainAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLObjectPropertyDomainAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLEquivalentObjectPropertiesAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLNegativeDataPropertyAssertionAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLDifferentIndividualsAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLDisjointDataPropertiesAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLDisjointObjectPropertiesAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLObjectPropertyRangeAxiom arg0) {
		isDataConstraint=false;
		
	}



	@Override
	public void visit(OWLSubObjectPropertyOfAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLDisjointUnionAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLSymmetricObjectPropertyAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLDataPropertyRangeAxiom arg0) {
		isDataConstraint=false;
		
	}





	@Override
	public void visit(OWLEquivalentDataPropertiesAxiom arg0) {
		isDataConstraint=false;
		
	}




	@Override
	public void visit(OWLTransitiveObjectPropertyAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLSubDataPropertyOfAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLInverseFunctionalObjectPropertyAxiom arg0) {
		isDataConstraint=false;
		
	}


	


	@Override
	public void visit(OWLSubPropertyChainOfAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLInverseObjectPropertiesAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLHasKeyAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(OWLDatatypeDefinitionAxiom arg0) {
		isDataConstraint=false;
		
	}


	@Override
	public void visit(SWRLRule arg0) {
		isDataConstraint=false;
		
	}



}
