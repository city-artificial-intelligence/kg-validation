package uk.city.kg.validation.constraint.utils.pagoda_hermit;

import java.util.Collection;

import org.semanticweb.HermiT.model.AnnotatedEquality;
import org.semanticweb.HermiT.model.Atom;
import org.semanticweb.HermiT.model.AtomicRole;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.HermiT.model.DLPredicate;

import org.semanticweb.HermiT.model.Equality;
import org.semanticweb.HermiT.model.Inequality;

import org.semanticweb.HermiT.model.NodeIDLessEqualThan;
import org.semanticweb.HermiT.model.NodeIDsAscendingOrEqual;
import org.semanticweb.HermiT.model.Term;
import org.semanticweb.HermiT.model.Variable;

import uk.city.kg.validation.constraint.model.clause.DatatypeAtom;
import uk.city.kg.validation.constraint.model.clause.NegatedAtom;
import uk.city.kg.validation.constraint.utils.Namespace;
import uk.city.kg.validation.constraint.utils.Utility;


/**
 * Class addapted/reused from Pagoda to avoid dependency with the whole system and RDFox
 *
 */
public class RuleHelperExtended {	
	
	public enum BRACKETS {NORMAL, SQUARE};
	
	
	public static String getTextAndPrefixes(Collection<DLClause> clauses) {
		return getTextAndPrefixes(clauses, BRACKETS.NORMAL);
	}
	
	public static String getTextAndPrefixes(Collection<DLClause> clauses, BRACKETS brackets_type) {
		
		StringBuilder sb = new StringBuilder(getText(clauses, brackets_type)); 
		sb.insert(0, Utility.LINE_SEPARATOR); 
		sb.insert(0, MyPrefixes.PAGOdAPrefixes.prefixesText());
		return sb.toString(); 
	}
	
	
	public static String getText(Collection<DLClause> clauses) {
		return getText(clauses, BRACKETS.NORMAL);
	}
	
	
	public static String getText(Collection<DLClause> clauses, BRACKETS brackets_type) {		
		StringBuilder buf = new StringBuilder();
		for (DLClause cls: clauses) {
			buf.append(getText(cls, brackets_type));
			buf.append(Utility.LINE_SEPARATOR); 
		}
		return buf.toString(); 
	}
	
	
	
	
	public static String getText(DLClause clause) {
		return getText(clause, BRACKETS.NORMAL);
	}
	
	public static String getText(DLClause clause, BRACKETS brackets_type) {
		StringBuffer buf = new StringBuffer();
		String atomText; 
		
		//TODO In disjoint axioms. The head is missing (no owl:Nothing)
		//if (clause.getHeadAtoms().length==0) {
		//	return "";
		//}
		
		boolean lastSpace = true;
		for (Atom headAtom: clause.getHeadAtoms()) {
			atomText = getText(headAtom, brackets_type);
			if (atomText == null || atomText.isEmpty()) continue; 
			if (!lastSpace)	buf.append(" v "); 
			buf.append(atomText);
			lastSpace = false;
		}
		//TODO if not body then do not use :-    (added by ernesto, so that they can be printed as facts) 
		if (clause.getBodyAtoms().length>0)
			buf.append(" :- ");
		
		lastSpace = true;
		for (Atom bodyAtom: clause.getBodyAtoms()) {
//		for (String str: strs[1].split(", ")) {
			atomText = getText(bodyAtom, brackets_type);
			if (atomText == null || atomText.isEmpty()) continue; 
			if (!lastSpace) buf.append(", ");
			buf.append(atomText);
			lastSpace = false;
		}
		buf.append(" .");
		return buf.toString();
	}

	
	private static String getEqualityPredicate(){
		switch (Utility.datalog_engine) {
	 		case RDFox:
	 			return Namespace.EQUALITY_ABBR; 			
	 		case IRIS:
	 			// return "sameAs";
	 			//return Constants.IRIS_EQUAL;
	 			return Namespace.EQUALITY_NOPREFIX;
	     	default:
	     		return Namespace.EQUALITY_ABBR;
 			
		}
	}
	
	
	private static String getInequalityPredicate(){
		switch (Utility.datalog_engine) {
	 		case RDFox:
	 			return Namespace.INEQUALITY_ABBR; 			
	 		case IRIS:
	 			// return "differentFrom";
	 			//return Constants.IRIS_NOT_EQUAL;
	 			return Namespace.INEQUALITY_NOPREFIX;
	     	default:
	     		return Namespace.INEQUALITY_ABBR;
 			
		}
	}
	
	public static String getText(Atom atom) {
		return getText(atom, BRACKETS.NORMAL);
	}
	
	private static String getText(Atom atom, BRACKETS brackets_type) {
		if (atom.getDLPredicate() instanceof NodeIDsAscendingOrEqual ||
				atom.getDLPredicate() instanceof NodeIDLessEqualThan) 
			return null;
		
		String left_b;
		String right_b;
		if (brackets_type.equals(BRACKETS.NORMAL)) {
			left_b="(";
			right_b=")";
		}
		else{ //square  (used in dlog rules by latest rdfox command line)
			left_b="[";
			right_b="]";
		}
		
		StringBuilder builder = new StringBuilder(); 
		if (atom.getArity() == 1) {
			
			if (atom instanceof DatatypeAtom){ // && Parameters.RDFox_rules){ //adhoc addition by ernesto
				builder.append(atom.toString());
			}
			else{				
				if (atom instanceof NegatedAtom) //adhoc addition by ernesto
					builder.append("not ");
				builder.append(getText(atom.getDLPredicate())); 
				builder.append(left_b); 
				builder.append(getText(atom.getArgument(0)));
				builder.append(right_b);
			}
		}
		else { //arity>2
			if (atom instanceof NegatedAtom)
				builder.append("not ");  //adhoc addition by ernesto.
			DLPredicate p = atom.getDLPredicate();
			if (p instanceof Equality || p instanceof AnnotatedEquality){
				
				builder.append(getEqualityPredicate());
				
			}
			else if (p instanceof Inequality){
				
				builder.append(getInequalityPredicate());
				
			}
			else builder.append(getText(p));
			builder.append(left_b); 
			builder.append(getText(atom.getArgument(0))); 
			builder.append(","); 
			builder.append(getText(atom.getArgument(1))); 
			builder.append(right_b); 
		}
		return builder.toString(); 
	}
	
	public static String getText(DLPredicate p) {
		if (p instanceof Equality || p instanceof AnnotatedEquality) {
			
			return getEqualityPredicate();
			
		}
		if (p instanceof Inequality){
			
			return getInequalityPredicate();
			
		}
		if (p instanceof AtomicRole && ((AtomicRole) p).getIRI().startsWith("?")){					
			return ((AtomicRole) p).getIRI(); 
		}
		
		
		String abbreviated_string = MyPrefixes.PAGOdAPrefixes.abbreviateIRI(p.toString());
		
		
		//Predicate names		
		switch (Utility.datalog_engine) {
	 		case RDFox:
	 			return abbreviated_string;
	 		case IRIS:
	 			if (!abbreviated_string.contains("http://") && abbreviated_string.indexOf(":")>0){
					String abbreviated_string_iris = abbreviated_string.substring(abbreviated_string.indexOf(":")+1, abbreviated_string.length());
					//System.out.println(p.toString() + " -> " + abbreviated_string_iris);
					return abbreviated_string_iris;
				}
				else{
					return abbreviated_string;
				}	 			     	
	     	default:
	     		return abbreviated_string;
				
		}
		
		
	}

	public static String getText(Term t) {
		if (t instanceof Variable)
			return "?" + ((Variable) t).getName(); 
		
						
		String abbreviated_string = MyPrefixes.PAGOdAPrefixes.abbreviateIRI(t.toString());
		//System.out.println(t.toString() + " -> " + abbreviated_string);
				
		
		//Variables, constants and URIS
		switch (Utility.datalog_engine) {
	 		case RDFox:
	 			return abbreviated_string;
	 		case IRIS:
	 			//For Individuals
				if (t.toString().startsWith("<http://")){
					//keep URI and quote				
					return "'" + Utility.removeAngles(t.toString()) + "'";
				}
				else if (t.toString().indexOf("^^")>0){
					//remove datatype from constants
					return t.toString().substring(0, t.toString().indexOf("^^"));
				}
				else{
					return abbreviated_string;
				}
	     	default:
	     		return abbreviated_string;
				
		}
		
	}

}
