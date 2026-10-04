package uio.ifi.ontology.toolkit.constraint.model.clause;

import org.semanticweb.HermiT.Prefixes;
import org.semanticweb.HermiT.model.AnnotatedEquality;
import org.semanticweb.HermiT.model.Atom;
import org.semanticweb.HermiT.model.DLPredicate;
import org.semanticweb.HermiT.model.Term;

/**
 * Especial datalog negation atom
 * We just extend the text representation
 *
 * @author ernesto
 * Created on 10 Jan 2017
 *
 */
public class NegatedAtom extends Atom {

	public NegatedAtom(DLPredicate dlPredicate, Term[] arguments) {
		super(dlPredicate, arguments);
	}	
	

	
	
	
	public String toString(Prefixes prefixes) {
        StringBuffer buffer=new StringBuffer();
        if (s_infixPredicates.contains(m_dlPredicate)) {
            buffer.append(m_arguments[0].toString(prefixes));
            buffer.append(' ');
            buffer.append(m_dlPredicate.toString(prefixes));
            buffer.append(' ');
            buffer.append(m_arguments[1].toString(prefixes));
        }
        else if (m_dlPredicate instanceof AnnotatedEquality) {
            AnnotatedEquality annotatedEquality=(AnnotatedEquality)m_dlPredicate;
            buffer.append('[');
            buffer.append(m_arguments[0].toString(prefixes));
            buffer.append(' ');
            buffer.append("==");
            buffer.append(' ');
            buffer.append(m_arguments[1].toString(prefixes));
            buffer.append("]@atMost(");
            buffer.append(annotatedEquality.getCaridnality());
            buffer.append(' ');
            buffer.append(annotatedEquality.getOnRole().toString(prefixes));
            buffer.append(' ');
            buffer.append(annotatedEquality.getToConcept().toString(prefixes));
            buffer.append(")(");
            buffer.append(m_arguments[2].toString(prefixes));
            buffer.append(')');
        }
        else {
        	buffer.append("not ");
            buffer.append(m_dlPredicate.toString(prefixes));
            buffer.append('(');
            for (int i=0;i<m_arguments.length;i++) {
                if (i!=0)
                    buffer.append(',');
                buffer.append(m_arguments[i].toString(prefixes));
            }
            buffer.append(')');
        }
        return buffer.toString();
    }
	
	

}
