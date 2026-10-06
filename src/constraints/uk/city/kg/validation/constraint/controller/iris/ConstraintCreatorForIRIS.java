/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.city.kg.validation.constraint.controller.iris;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.HermiT.model.Atom;
import org.semanticweb.HermiT.model.AtomicConcept;
import org.semanticweb.HermiT.model.Constant;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.Term;
import org.semanticweb.HermiT.model.Variable;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

import uk.city.kg.validation.constraint.controller.ConstraintCreator;
import uk.city.kg.validation.constraint.model.clause.DatatypeAtom;
import uk.city.kg.validation.constraint.utils.Namespace;
import uk.city.kg.validation.constraint.utils.Utility;
import uk.city.kg.validation.constraint.utils.pagoda_hermit.MyPrefixes;

/**
 *
 * @author ernesto
 * Created on 11 Jan 2017
 *
 */
public class ConstraintCreatorForIRIS extends ConstraintCreator {
	
	//TODO: TBC missing methods
	//TODO: it will also require some additional rules that Pagoda/HermiT fail to produce. Especially when dealing with nominals and datatype restrictions.
	//TODO: create OWL 2 RL parser so that only RL axioms are given to pagoda + detect conflicting axioms that are not transformed as expected: Reuse from More or RDFox?
	
	
	
	//IRIS does not deal very well with equality so we need to add custom clauses to mimic equality as in RDfox
	protected Collection<DLClause> equalityRules = new HashSet<DLClause>();
	
	//Fresh symbols	
	private Set<String> freshPredicateSymbols = new HashSet<String>();		
	private Set<String> freshDatatypeSymbols = new HashSet<String>();
	
	//Facts to be given to IRIS as rules
	protected Collection<DLClause> facts = new HashSet<DLClause>();
	
	
	public Collection<DLClause> getEqualityRules(){
		return equalityRules;
	}
	
	public Collection<DLClause> getFacts(){
		return facts;
	}
	
	public void clearFacts(){
		facts.clear();
	}
	
	
	protected Atom getNegatedSameAsAtom(Term x, Term y){
		//return getRoleAtom(Constants.IRIS_NOT_EQUAL_IRI, x, y);  //Not working as expected
		return getNegatedRoleAtom(Namespace.sameAS_iri, x, y); //We use the same as in RDFox
		
	}
	
	
	//TODO
	//protected Atom getDistinctValueAtom(Term x, String value){
		
	//
	
	
	protected String getNewFreshPredicateURI(String p_iri, String filler_iri, int cardinality){
		
		String fresh_uri = super.getNewFreshPredicateURI(p_iri, filler_iri, cardinality);
				
		freshPredicateSymbols.add(fresh_uri);
		
		return fresh_uri;
		
		
	}
	
	protected String getNewFreshPredicateURI(String p_iri, int cardinality){
		
		String fresh_uri = super.getNewFreshPredicateURI(p_iri, cardinality);
		
		freshPredicateSymbols.add(fresh_uri);
		
		return fresh_uri;
				
	}

	
	protected String getFreshPredicateForDataRangeConstraint(String p_iri, String filler_iri, List<String> range_values){
		
		String fresh_uri = super.getFreshPredicateForDataRangeConstraint(p_iri, filler_iri, range_values);
		
		freshPredicateSymbols.add(fresh_uri);
		
		return fresh_uri;
		
	}
	
	
	protected String getFreshPredicateForNominalConstraint(String prop_iri, String ax_id) {
		String fresh_uri = super.getFreshPredicateForNominalConstraint(prop_iri, ax_id);
		
		freshPredicateSymbols.add(fresh_uri);
		
		return fresh_uri;
	}
	

	
	
	protected Atom getDatatypeAtom(String datatype_iri, Term x) {
		Term[] variables = new Term[1];
		variables[0] = x;		
		
		String abbrevaited_datatype_iri = MyPrefixes.PAGOdAPrefixes.abbreviateIRI("<"+datatype_iri+">");
		
		String lexical_form="";
		
		freshDatatypeSymbols.add(datatype_iri);
 		lexical_form=getIRISDatatype(abbrevaited_datatype_iri) + "(" + "?" + ((Variable) x).getName()+ ")";
	 	
		
		return new DatatypeAtom(AtomicConcept.create(datatype_iri), variables, lexical_form);
	}
	
	protected String getIRISDatatype(String abbrevaited_datatype_iri){
		
		//TODO We use the xmls datatypes. IRIS has bultin datatypes, but unless one needs the for arithmetic operations does not seem to be worth using them
		if (abbrevaited_datatype_iri.indexOf(":")>0){
			return abbrevaited_datatype_iri.substring(abbrevaited_datatype_iri.indexOf(":")+1, abbrevaited_datatype_iri.length());
		}
		return abbrevaited_datatype_iri;
		
	}
	
	
	
	
	protected void getClausesForOnlyDatatypeRestriction(String cls, String prop_iri, String filler_iri, String ax_id) {
		
		
		//RULE 1: define violation: RangeViolation(?X,"Ax_i") :- A(?X), R(?x, ?Y),  not int(?Y)		
		//Does not work for RDFox: not FILTER... or FILTER NOT EXIST, perhaps with and SPARQL workaround
				
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		
		Atom[] bodyAtoms1 = new Atom[3];
		Atom[] headAtoms = new Atom[1];
				
		Constant c = getAxiomsIDConstant(ax_id);
		
		headAtoms[0] = getRoleAtom(Namespace.range_violation_iri, X, c);
		
		
		//Class
		bodyAtoms1[0] = getClassAtom(cls, X);
				
		//R successor
		bodyAtoms1[1] = getRoleAtom(prop_iri, X, Y);

		//value violation 1
		bodyAtoms1[2] = getNegatedDatatypeAtom(filler_iri, Y);

		//RULE 1	
		DLClause clause = DLClause.create(headAtoms, bodyAtoms1);
		data_constraints.add(clause);		

		
	
	}
	
	
	
	public void createEqualityRulesForSameAS(){
		
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		Variable Z = Variable.create("Z");
		
		Atom[] sameas_xx_atom = new Atom[1];
		Atom[] sameas_xy_atom = new Atom[1];
		Atom[] sameas_yx_atom = new Atom[1];
		
		Atom[] body_transtitivity = new Atom[2];
		
				
		sameas_xx_atom[0] = getSameAsAtom(X, X);
		sameas_xy_atom[0] = getSameAsAtom(X, Y);
		sameas_yx_atom[0] = getSameAsAtom(Y, X);
		
		body_transtitivity[0] = getSameAsAtom(X, Z);
		body_transtitivity[1] = getSameAsAtom(Z, Y);
		
		
		//Reflexivity
		//sameAs(?x,?x) :- sameAs(?x, ?y).
		equalityRules.add(DLClause.create(sameas_xx_atom, sameas_xy_atom));
		//sameAs(?x,?x) :- sameAs(?y, ?x).
		equalityRules.add(DLClause.create(sameas_xx_atom, sameas_yx_atom));
		
		//Symmetry
		//sameAs(?x,?y) :- sameAs(?y,?x).
		equalityRules.add(DLClause.create(sameas_xy_atom, sameas_yx_atom));
		
		//Transitivity
		//sameAs(?x,?y) :- sameAs(?x,?z), sameAs(?z,?y).
		equalityRules.add(DLClause.create(sameas_xy_atom, body_transtitivity));

		
	}
	
	
	//Reflexivity
	//sameAs(?x,?x) :- R(?x, ?y).
	//sameAs(?x,?x) :- R(?y, ?x).
	//sameAs(?x,?x) :- Dprop(?x, ?y).
	//sameAs(?x,?x) :- C(?x).
	
	//Equality propagation
	//R(?x1, ?y) :- sameAs(?x, ?x1), R(?x, ?y).
	//R(?x, ?y1) :- sameAs(?y, ?y1), R(?x, ?y).
	
	//Dprop(?x1, ?y) :- sameAs(?x, ?x1), Dprop(?x, ?y).
	
	//C(?x1) :- sameAs(?x, ?x1), C(?x).
	
	public void createEqualityRulesForObjectProperty(OWLObjectProperty oprop){
		createEqualityRulesForObjectProperty(oprop.toStringID());
	}
	
	
	public void createEqualityRulesForObjectProperty(String oprop_iri){
		createEqualityRulesForObjectProperty(oprop_iri, true);
	}
	
	public void createEqualityRulesForObjectProperty(String oprop_iri, boolean add_reflexibity_rules){	
		
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		Variable X1 = Variable.create("X1");
		Variable Y1 = Variable.create("Y1");
		
		Atom[] sameas_xx_atom = new Atom[1];
		Atom[] sameas_xx1_atom = new Atom[1];
		Atom[] sameas_yy1_atom = new Atom[1];
				
		sameas_xx_atom[0] = getSameAsAtom(X, X);
		sameas_xx1_atom[0] = getSameAsAtom(X, X1);
		sameas_yy1_atom[0] = getSameAsAtom(Y, Y1);
		
		Atom[] role_xy_atom = new Atom[1];
		Atom[] role_yx_atom = new Atom[1];
		Atom[] role_x1y_atom = new Atom[1];
		Atom[] role_xy1_atom = new Atom[1];
		
		role_xy_atom[0]=getRoleAtom(oprop_iri, X, Y);
		role_yx_atom[0]=getRoleAtom(oprop_iri, Y, X);
		role_x1y_atom[0]=getRoleAtom(oprop_iri, X1, Y);
		role_xy1_atom[0]=getRoleAtom(oprop_iri, X, Y1);
		
		Atom[] body_role_x1y_atom = new Atom[2];
		Atom[] body_role_xy1_atom = new Atom[2];
		
		body_role_x1y_atom[0]=sameas_xx1_atom[0];
		body_role_x1y_atom[1]=role_xy_atom[0];
		
		body_role_xy1_atom[0]=sameas_yy1_atom[0];
		body_role_xy1_atom[1]=role_xy_atom[0];
		
		
		//Reflexivity
		//sameAs(?x,?x) :- R(?x, ?y).
		//sameAs(?x,?x) :- R(?y, ?x).
		if (add_reflexibity_rules){
			equalityRules.add(DLClause.create(sameas_xx_atom, role_xy_atom));
			equalityRules.add(DLClause.create(sameas_xx_atom, role_yx_atom));
		}
		
		//Equality propagation
		//R(?x1, ?y) :- sameAs(?x, ?x1), R(?x, ?y).
		//R(?x, ?y1) :- sameAs(?y, ?y1), R(?x, ?y).
		equalityRules.add(DLClause.create(role_x1y_atom, body_role_x1y_atom));
		equalityRules.add(DLClause.create(role_xy1_atom, body_role_xy1_atom));
		
		
		
	}
	
	public void createEqualityRulesForDataProperty(OWLDataProperty dprop){
		createEqualityRulesForDataProperty(dprop.toStringID());
	}
	
	public void createEqualityRulesForDataProperty(String dprop_iri){
		createEqualityRulesForDataProperty(dprop_iri, true);
	}
	
	public void createEqualityRulesForDataProperty(String dprop_iri, boolean add_reflexibity_rules){
		
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		Variable X1 = Variable.create("X1");
		
		Atom[] sameas_xx_atom = new Atom[1];
		Atom[] sameas_xx1_atom = new Atom[1];
				
		sameas_xx_atom[0] = getSameAsAtom(X, X);
		sameas_xx1_atom[0] = getSameAsAtom(X, X1);
		
		Atom[] role_xy_atom = new Atom[1];
		Atom[] role_x1y_atom = new Atom[1];
		
		
		role_xy_atom[0]=getRoleAtom(dprop_iri, X, Y);
		role_x1y_atom[0]=getRoleAtom(dprop_iri, X1, Y);
		
		Atom[] body_role_x1y_atom = new Atom[2];
		
		body_role_x1y_atom[0]=sameas_xx1_atom[0];
		body_role_x1y_atom[1]=role_xy_atom[0];
		
		
		
		//Reflexivity
		//sameAs(?x,?x) :- Dprop(?x, ?y).
		if (add_reflexibity_rules)
			equalityRules.add(DLClause.create(sameas_xx_atom, role_xy_atom));
		
		//Equality propagation
		//Dprop(?x1, ?y) :- sameAs(?x, ?x1), Dprop(?x, ?y).
		equalityRules.add(DLClause.create(role_x1y_atom, body_role_x1y_atom));
		
	}

	public void createEqualityRulesForClass(OWLClass cls){
		createEqualityRulesForClass(cls.toStringID());
	}
	
	public void createEqualityRulesForClass(String cls_iri){
		createEqualityRulesForClass(cls_iri, true);
	}
	
	public void createEqualityRulesForClass(String cls_iri, boolean add_reflexibity_rules){
		
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		Variable X1 = Variable.create("X1");
		
		Atom[] sameas_xx_atom = new Atom[1];
		Atom[] sameas_xx1_atom = new Atom[1];
				
		sameas_xx_atom[0] = getSameAsAtom(X, X);
		sameas_xx1_atom[0] = getSameAsAtom(X, X1);
		
		Atom[] class_x_atom = new Atom[1];
		Atom[] class_x1_atom = new Atom[1];
		
		
		class_x_atom[0]=getClassAtom(cls_iri, X);
		class_x1_atom[0]=getClassAtom(cls_iri, X1);
		
		Atom[] body_class_x1_atom = new Atom[2];
		
		body_class_x1_atom[0]=sameas_xx1_atom[0];
		body_class_x1_atom[1]=class_x_atom[0];
		
		
		
		//Reflexivity
		//sameAs(?x,?x) :- C(?x).
		if (add_reflexibity_rules)
			equalityRules.add(DLClause.create(sameas_xx_atom, class_x_atom));
		
		//Equality propagation
		//C(?x1) :- sameAs(?x, ?x1), C(?x).
		equalityRules.add(DLClause.create(class_x1_atom, body_class_x1_atom));
		
	
	}
	
	
	public void createEqualityRulesForDatatypes(){
		
		//So far only for int and integer, but one could extend it for other datatypes e.g. : double(?x) :- int(?x)
		
		Variable X = Variable.create("X");
		
		Atom[] int_atom = new Atom[1];
		Atom[] integer_atom = new Atom[1];
				
		int_atom[0] = getClassAtom(Namespace.xsd_int, X);
		integer_atom[0] = getClassAtom(Namespace.xsd_integer, X);
		
		//int(?x) :- integer(?x)
		equalityRules.add(DLClause.create(int_atom, integer_atom));
		
		//integer(?x) :- int(?x)
		equalityRules.add(DLClause.create(integer_atom, int_atom));
		
		
		
		Atom[] literal_atom = new Atom[1];
		Atom[] plainliteral_atom = new Atom[1];
				
		literal_atom[0] = getClassAtom(Namespace.rdfs_literal, X);
		plainliteral_atom[0] = getClassAtom(Namespace.rdf_plainliteral, X);
		
		//Literal(?x) :- PlainLiteral(?x)
		equalityRules.add(DLClause.create(literal_atom, plainliteral_atom));
				
		//PlainLiteral(?x) :- Literal(?x)
		equalityRules.add(DLClause.create(plainliteral_atom, literal_atom));
		
		
		//Included in list of fresh symbols
		//createEqualityRulesForClass(xsd_integer);
		//createEqualityRulesForClass(xsd_int);
		
	}
	
	
	public void createEqualityRulesForNewSymbols(){
		//sameAs, minCar, MaxCard, Nothing, differentFrom, int, double...
		//has_.... successors, etc, one can reuse the methods above
		
		createEqualityRulesForObjectProperty(Namespace.differentFrom_iri);
	
		
		for (String fresh_uri : freshDatatypeSymbols){
			createEqualityRulesForClass(fresh_uri);
		}
		
		
		//For datatypes int and integer
		createEqualityRulesForDatatypes();
		
		
		//If added any of the ones below with reflexivity = true, then the program is not stratified
		//It is ok since we do not infer new equality from them
		
		createEqualityRulesForDataProperty(Namespace.min_violation_iri, false);
		createEqualityRulesForDataProperty(Namespace.max_violation_iri, false);
		
		createEqualityRulesForClass(Namespace.nothing_iri, false);
		
		
		for (String fresh_uri : freshPredicateSymbols){
			createEqualityRulesForClass(fresh_uri, false);
		}
		
	
	}
	
	
	
	@Override
	public void visit(OWLObjectPropertyAssertionAxiom ax) {
		
		isDataConstraint=false;
		
		if (!ax.getProperty().isAnonymous() && ax.getSubject().isNamed() && ax.getObject().isNamed()){
			
			facts.add(getFactForObjectRoleAssertion(ax.getProperty().asOWLObjectProperty(), 
					ax.getSubject().asOWLNamedIndividual(), 
					ax.getObject().asOWLNamedIndividual())
					);
			
			
			
		}
		
	}
	
	
	@Override
	public void visit(OWLDataPropertyAssertionAxiom ax) {
		isDataConstraint=false;
		
		//ax.getObject().getLiteral().getDatatype();
		
		if (!ax.getProperty().isAnonymous() && ax.getSubject().isNamed()){
			
			facts.add(getFactForDataRoleAssertion(ax.getProperty().asOWLDataProperty(), 
					ax.getSubject().asOWLNamedIndividual(), 
					ax.getObject())
					);
			
			facts.add(getFactForDatatypeMembership(ax.getObject()));
		}
		
	}
	
	
	@Override
	public void visit(OWLClassAssertionAxiom ax) {
		isDataConstraint=false;
		
		if (!ax.getClassExpression().isAnonymous() && ax.getIndividual().isNamed()){
			
			facts.add(getFactForClassAssertion(ax.getClassExpression().asOWLClass(), 
					ax.getIndividual().asOWLNamedIndividual())
					);
		}
		
	}
	
	@Override
	public void visit(OWLSameIndividualAxiom ax) {
		isDataConstraint=false;
		
		for (int i=0; i<ax.getIndividualsAsList().size()-1; i++){
			for (int j=i+1; j<ax.getIndividualsAsList().size(); j++){
				
				if (ax.getIndividualsAsList().get(i).isNamed() && ax.getIndividualsAsList().get(j).isNamed()){
					
					facts.add(getFactForSameAs(
							ax.getIndividualsAsList().get(i).asOWLNamedIndividual(),
							ax.getIndividualsAsList().get(j).asOWLNamedIndividual())
							);
				}
				
			}
		}
			
		
	}

	@Override
	protected Atom getDatatypeRangeConstraintAtom(String datatype_iri, Term x, List<String> constraints,
			boolean reverse_facet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Atom getDatatypeDistinctValueAtom(String datatype_iri, Term x, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Atom getNegatedDatatypeAtom(String filler_iri, Term y) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
