package uio.ifi.ontology.toolkit.projection.model.entities;

/**
 * It could be and URI or a literal value
 * RDFOx gives a special type for URIs -> internal:iri-reference
 * @author ejimenez-ruiz
 *
 */
public class GenericValue {
	
	private String value;
	private String type;
	
	public GenericValue(String value, String type){
		setType(type);
		setValue(value);
	}
	
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
