package uk.city.kg.validation.constraint.controller;

import java.util.ArrayList;

import java.util.List;


import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRangeVisitor;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;

import uk.city.kg.validation.constraint.utils.Namespace;

/**
 * Visitor to capture enumerations of values and min-max datatype restrictions
 * @author ernesto
 *
 */
public class DataRangeVisitor implements OWLDataRangeVisitor{

	private boolean isEnumeration = false;
	private boolean isDatatypeRestriction = false;
	
	
	private List<String> range_values = new ArrayList<String>();
	
	//private OWLDatatype datatype;
	
	private String datatypeString;
	
	
	public List<String> getRangeValues(){
		return range_values;
	}
	
	//public OWLDatatype getDatatype(){
	//	return datatype;
	//}
	
	public String getDatatypeString(){
		return datatypeString;
	}
	
	
	
	public void visit(OWLDataOneOf range) {
	
		setEnumerationRange(true);
		setDatatypeRestrictionRange(false);
		
		range_values.clear();
		
		boolean same_type = true;
		datatypeString = "";
		
		for (OWLLiteral lit : range.getValues()){
			range_values.add(lit.getLiteral().toString());
			if (!datatypeString.equals("") && !datatypeString.equals(lit.getDatatype().toStringID()))
				 same_type = false;
			
			datatypeString = lit.getDatatype().toStringID();						
			//
			
		}
		
		//Problems with Hermit creating Constant terms if datatype is outside OWL 2
		if (!same_type || datatypeString.equals(Namespace.rdf_plainliteral) || datatypeString.equals(Namespace.rdfs_literal))
			datatypeString = Namespace.XSD_STRING;
		
	}
	
	
	@Override
	public void visit(OWLDatatypeRestriction range) {
		
		setEnumerationRange(false);
		setDatatypeRestrictionRange(true);
		
		try {
			
			String value_facet;
			String symbol_facet;
			String text_facet;
			
			datatypeString = range.getDatatype().toStringID();
			
			range_values.clear();
			
			
			
			for (OWLFacetRestriction facet : range.getFacetRestrictions()){
				
				value_facet = facet.getFacetValue().getLiteral().toString();
				symbol_facet = facet.getFacet().getSymbolicForm();
				text_facet = facet.getFacet().getShortForm();
				
				//For convenience we store symbol, text_form and number in different positions
				//e.g.: ">" and "1"
				range_values.add(symbol_facet);
				range_values.add(text_facet);
				range_values.add(value_facet);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void visit(OWLDatatype arg0) {
		setEnumerationRange(false);
		setDatatypeRestrictionRange(false);
	}
	

	@Override
	public void visit(OWLDataComplementOf arg0) {
		setEnumerationRange(false);
		setDatatypeRestrictionRange(false);
		
	}

	@Override
	public void visit(OWLDataIntersectionOf arg0) {
		setEnumerationRange(false);
		setDatatypeRestrictionRange(false);
	}

	@Override
	public void visit(OWLDataUnionOf arg0) {
		setEnumerationRange(false);
		setDatatypeRestrictionRange(false);
	}


	public boolean isEnumerationRange() {
		return isEnumeration;
	}


	private void setEnumerationRange(boolean isEnumeration) {
		this.isEnumeration = isEnumeration;
	}


	public boolean isDatatypeRestrictionRange() {
		return isDatatypeRestriction;
	}


	private void setDatatypeRestrictionRange(boolean isDatatypeRestriction) {
		this.isDatatypeRestriction = isDatatypeRestriction;
	}



}
