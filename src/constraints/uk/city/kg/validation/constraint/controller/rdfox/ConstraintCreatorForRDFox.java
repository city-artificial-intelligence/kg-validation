/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.city.kg.validation.constraint.controller.rdfox;

import java.util.List;

import org.semanticweb.HermiT.model.Atom;
import org.semanticweb.HermiT.model.AtomicConcept;
import org.semanticweb.HermiT.model.Constant;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.Term;
import org.semanticweb.HermiT.model.Variable;

import uk.city.kg.validation.constraint.controller.ConstraintCreator;
import uk.city.kg.validation.constraint.model.clause.DatatypeAtom;
import uk.city.kg.validation.constraint.utils.Namespace;
import uk.city.kg.validation.constraint.utils.pagoda_hermit.MyPrefixes;

/**
 *
 * @author ernesto
 * Created on 11 Jan 2017
 *
 */
public class ConstraintCreatorForRDFox extends ConstraintCreator{
	
	protected Atom getNegatedSameAsAtom(Term x, Term y){
		
		return getNegatedRoleAtom(Namespace.sameAS_iri, x, y);					
		
	}
	
	
	protected Atom getDatatypeAtom(String datatype_iri, Term x) {
		Term[] variables = new Term[1];
		variables[0] = x;		
		
		String abbrevaited_datatype_iri = MyPrefixes.PAGOdAPrefixes.abbreviateIRI("<"+datatype_iri+">");
		
		String lexical_form="";
		
		//lexical_form = 
		///	"FILTER(DATATYPE("+ "?" + ((Variable) x).getName() +") = " + 
		//			"xsd:integer" + ")";
			
		//lexical_form = 
		//		"FILTER(DATATYPE("+ "?" + ((Variable) x).getName() +") = <" + 
		//				MyPrefixes.PAGOdAPrefixes.abbreviateIRI(datatype_iri) + ">)";
	
		
		//RDFox does not seem to like xsd:int
		if (abbrevaited_datatype_iri.equals("xsd:int"))
			abbrevaited_datatype_iri = "xsd:integer";
		
		lexical_form=
					"FILTER(DATATYPE("+ "?" + ((Variable) x).getName() +") = " + 
						abbrevaited_datatype_iri + ")";
		
		//lexical_form=
		//		"FILTER("+ "?" + ((Variable) x).getName() +" >= " + 
		//			"1" + ")";
		
						
		
		return new DatatypeAtom(AtomicConcept.create(datatype_iri), variables, lexical_form);
	}
	
	
	
	protected Atom getNegatedDatatypeAtom(String datatype_iri, Term x) {
		Term[] variables = new Term[1];
		variables[0] = x;		
		
		String abbrevaited_datatype_iri = MyPrefixes.PAGOdAPrefixes.abbreviateIRI("<"+datatype_iri+">");
		
		String lexical_form="";
		
		//RDFox does not seem to like xsd:int
		if (abbrevaited_datatype_iri.equals("xsd:int"))
			abbrevaited_datatype_iri = "xsd:integer";
		
		lexical_form=
					"not FILTER(DATATYPE("+ "?" + ((Variable) x).getName() +") = " + 
						abbrevaited_datatype_iri + ")";
		
		
		//We reuse the same type of atom since we only use the lexical form
		return new DatatypeAtom(AtomicConcept.create(datatype_iri), variables, lexical_form);
	}
	
	
	
	
	protected Atom getDatatypeRangeConstraintAtom(String datatype_iri, Term x, List<String> constraints, boolean reverse_facet) {
		Term[] variables = new Term[1];
		variables[0] = x;		
		
		String abbrevaited_datatype_iri = MyPrefixes.PAGOdAPrefixes.abbreviateIRI("<"+datatype_iri+">");
		
		String lexical_form="";
					
		//RDFox does not seem to like xsd:int
		if (abbrevaited_datatype_iri.equals("xsd:int"))
			abbrevaited_datatype_iri = "xsd:integer";
		
		lexical_form=
					"FILTER(DATATYPE("+ "?" + ((Variable) x).getName() +") = " + 
						abbrevaited_datatype_iri + ")";
		
		//Format constraints: groups of  (i+0: symbol, i+1:text facet, i+2:value)
		//e.g. 0: >, min, 1	
		for (int i=0; i<constraints.size()-2; i+=3)
			lexical_form += ", FILTER("+ "?" + ((Variable) x).getName() + " " + getFacetSymbol(constraints.get(i), reverse_facet) + " " + constraints.get(i+2) +")";
		
						
		//We reuse the same type of atom since we only use the lexical form
		return new DatatypeAtom(AtomicConcept.create(datatype_iri), variables, lexical_form);
	}
	
	
	protected Atom getDatatypeDistinctValueAtom(String datatype_iri, Term x, String value){
		
		Term[] variables = new Term[1];
		variables[0] = x;	
		
		String lexical_form = "FILTER("+ "?" + ((Variable) x).getName() + " != " + value +")";
		
		//We reuse the same type of atom since we only use the lexical form
		return new DatatypeAtom(AtomicConcept.create(datatype_iri), variables, lexical_form);
		
		
	}
	
	
	protected void getClausesForOnlyDatatypeRestriction(String cls, String prop_iri, String filler_iri, String ax_id) {
		
		
		//RULE 1: define violation: RangeViolation(?X,"Ax_i") :- A(?X), R(?x, ?Y),  not int(?Y)		
		//Does not work for RDFox: not FILTER... or FILTER NOT EXIST, perhaps with and SPARQL workaround
		//ALTERNATIVE: RangeViolation(?X,"Ax_i") :- A(?X), R(?x, ?y),  not has_R_int(?x, ?y)
		//has_R_int :- R(x, y), int(y)  
		data_constraints.add(getHasDataRSuccessorClause(prop_iri, filler_iri));
				
		
				
		Variable X = Variable.create("X");
		Variable Y = Variable.create("Y");
		
		Atom[] bodyAtoms = new Atom[3];
		Atom[] headAtoms = new Atom[1];
				
		Constant c = getAxiomsIDConstant(ax_id);
		
		headAtoms[0] = getRoleAtom(Namespace.range_violation_iri, X, c);
		
		
		//Class
		bodyAtoms[0] = getClassAtom(cls, X);
				
		//R successor
		bodyAtoms[1] = getRoleAtom(prop_iri, X, Y); //has R

		//TODO
		//Violation
		//we need to create new fresh binary predicate
		bodyAtoms[2] = getNegatedRoleAtom(getNewFreshPredicateURI(prop_iri, filler_iri, 1), X, Y); //but has 

		//RULE 2	
		DLClause clause = DLClause.create(headAtoms, bodyAtoms);
		data_constraints.add(clause);		

		
	
	}

	
	
}
