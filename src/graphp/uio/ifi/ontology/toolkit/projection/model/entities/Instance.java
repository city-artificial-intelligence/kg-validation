package uio.ifi.ontology.toolkit.projection.model.entities;


import org.json.JSONObject;

public class Instance extends Entity implements Cloneable{

	protected String cls_type;
	
	public Instance() {
	}

	
	public Instance(String iri) {
		super(iri);		
	}

	
	public Instance clone() {// throws CloneNotSupportedException {
        try {
			return (Instance) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this;
		}
	}
	
	@Override
	public JSONObject toJSON() {
		
		JSONObject obj = super.toJSON();
		
		obj.put("type", getClassType());

		return obj;
	}

	public String getClassType() {
		return cls_type;
	}

	public void setClassType(String type) {
		this.cls_type = type;
	}
	
	
	//public boolean equals(Object o) {
	//	return super.equals(o);
	//}
	
	
	
}
