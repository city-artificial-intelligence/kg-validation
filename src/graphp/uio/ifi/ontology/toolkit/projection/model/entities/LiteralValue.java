package uio.ifi.ontology.toolkit.projection.model.entities;

import org.eclipse.rdf4j.model.impl.SimpleLiteral;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.json.JSONObject;


public class LiteralValue extends SimpleLiteral{

	private static final long serialVersionUID = 1L;

	
	public LiteralValue(String value, String datatype) {
		super(value, SimpleValueFactory.getInstance().createIRI(datatype));
	}
	
	public LiteralValue(String value) {
		super(value);
	}

	public String getVisualRepresentation() {
		return getLabel();
	}
	
	
	public String getValue() {
		return getLabel();
	}
	
	
	public String getDatatypeString() {
		return getDatatype().toString();
	}
		
	
	public JSONObject toJSON(){
		
		JSONObject obj = new JSONObject();
		
		obj.put("value", getLabel());  
		obj.put("datatype", getDatatype().stringValue());

		return obj;
		
	}
	
	/*public String toString(){
		return getLabel() + " - " + getDatatype();
	}*/
}
