/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.model;

import org.json.JSONObject;

import uio.ifi.ontology.toolkit.projection.model.entities.Concept;
import uio.ifi.ontology.toolkit.projection.model.entities.ObjectProperty;

/**
 *
 * A NeighbourLink represents the connection in the graph of two concepts via an object property.
 * 
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public class NeighbourLink extends ComparableNeighbourLink implements Cloneable{
	
	private ObjectProperty property;
	private Concept target;
	
	//For cases with anonymous inverse properties
	//If false: "source P target"
	//It true: "source P- target"
	private boolean isInversePropertyLink;
	
	
	
	/**
	 * @return the property
	 */
	public ObjectProperty getProperty() {
		return property;
	}
	/**
	 * @param property the property to set
	 */
	public void setProperty(ObjectProperty property) {
		this.property = property;
	}
	
	/**
	 * @return the target
	 */
	public Concept getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(Concept target) {
		this.target = target;
	}
	/**
	 * @return the isInversePropertyLink
	 */
	public boolean isInversePropertyLink() {
		return isInversePropertyLink;
	}
	/**
	 * @param isInversePropertyLink the isInversePropertyLink to set
	 */
	public void setInversePropertyLink(boolean isInversePropertyLink) {
		this.isInversePropertyLink = isInversePropertyLink;
	}
	
	
	
	public boolean equals(Object o){
		
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof NeighbourLink))
			return false;
		
		NeighbourLink i =  (NeighbourLink)o;
		
		return equals(i);
		
	}
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + property.getIri().hashCode();
		  code = 50 * code + target.getIri().hashCode();
		  return code;
	}
	
	
	public int compareTo(NeighbourLink l){
		if (property.getLabel().compareTo(l.getProperty().getLabel())>0)
			return -1;
		
		else if (property.getLabel().compareTo(l.getProperty().getLabel())==0){
			
			if (target.getLabel().compareTo(l.getTarget().getLabel())>0)
				return -1;
			else 
				return 1;
		}
		else
			return 1;
	}
	
	
	public boolean equals(NeighbourLink l){
		
		if (!property.getIri().equals(l.getProperty().getIri()) || 
			!target.getIri().equals(l.getTarget().getIri())){
			return false;
		}
		return true;
	}
	
	
	public NeighbourLink clone() {// throws CloneNotSupportedException {
        try {
			return (NeighbourLink) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this;
		}
	}
	
	 
	public JSONObject toJSON(){
		
		//Uses the target class json as baseline 
		JSONObject obj = getTarget().toJSON();
		
		if (!isInversePropertyLink){
			obj.put("prop", getProperty().toJSON());
		}
		else { 
			//Special link for the implicit inverse			
			JSONObject inv_p = new JSONObject();
			
			inv_p.put("id", getProperty().getIri() + "_inverseProp");  
			inv_p.put("name", "^"+getProperty().getName());			
			inv_p.put("ns", getProperty().getNamespace());			
			inv_p.put("short_ns", getProperty().getShortIRI() + "_inverseProp");			
			inv_p.put("label", "(inv) "+ getProperty().getLabel());			
			inv_p.put("desc",  "(inv) "+ getProperty().getDescription());			
			
			obj.put("prop", inv_p);					
		}
		
		return obj;
	}
	
	
}
