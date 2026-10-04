package uio.ifi.ontology.toolkit.constraint.model.clause;

import org.semanticweb.HermiT.model.Atom;
import org.semanticweb.HermiT.model.DLPredicate;
import org.semanticweb.HermiT.model.Term;

/**
 * RDFox does not consider atoms like xsd:int(?X). Instead one needs to provide:
 * FILTER(DATATYPE(?X) = xsd:integer). Similar problem with restrictions where one needs to use
 * FILTER(?X > 1). In IRRIS it is not used the FILTER constructs
 * @author ernesto
 *
 */
public class DatatypeAtom  extends Atom {

	String lexicalFormForURI;
	
	public DatatypeAtom(DLPredicate dlPredicate, Term[] arguments, String lexicalFormForURI) {
		super(dlPredicate, arguments);
		this.lexicalFormForURI = lexicalFormForURI;
		// TODO Auto-generated constructor stub
	}
	
	
	public String toString(){
		return lexicalFormForURI;
	}
	

}
