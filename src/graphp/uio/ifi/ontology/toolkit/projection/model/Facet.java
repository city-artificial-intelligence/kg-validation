/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.model;

import org.json.JSONObject;

import uio.ifi.ontology.toolkit.projection.model.entities.DataProperty;
import uio.ifi.ontology.toolkit.projection.utils.URIUtils;

/**
 * 
 * A facet will encode information associated to a data property in the scope of a source concept 
 * (i.e. range, list of values, min/max values, etc.)
 *
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public abstract class Facet extends ComparableFacet implements Cloneable{

	protected DataProperty property;
		
	//xml datatype
	protected String datatype;
	
	public enum FacetType {
	    SLIDER, SELECT, BOOLEAN, DATE, TEXT, DEFAULT 
	}
	
	/**
	 * @return the property
	 */
	public DataProperty getProperty() {
		return property;
	}

	/**
	 * @param property the property to set
	 */
	public void setProperty(DataProperty property) {
		this.property = property;
	}

	
	

	/**
	 * Returns full datatype URI
	 * @return the datatype
	 */
	public String getDatatype() {
		return datatype;
	}
	
	/**
	 * Returns name of the datatype
	 * @return
	 */
	public String getDatatypeName(){
		return URIUtils.getEntityLabelFromURI(getDatatype());
	}

	/**
	 * @param datatype the datatype to set
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	
	
	
	public abstract String getInputType();
	
	
	
	
	public boolean equals(Object o){
		
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof Facet))
			return false;
		
		Facet i =  (Facet)o;
		
		return equals(i);
		
	}
	
	public  int hashCode() {
		  int code = 10;
		  code = 40 * code + property.getIri().hashCode();		  
		  return code;
	}
	
	
	public int compareTo(Facet l){
		if (property.getLabel().compareTo(l.getProperty().getLabel())>0)
			return -1;		
		else
			return 1;
	}
	
	
	public boolean equals(Facet l){
		
		if (!property.getIri().equals(l.getProperty().getIri())){
			return false;
		}
		return true;
	}
	
	
	public Facet clone() {// throws CloneNotSupportedException {
        try {
			return (Facet) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this;
		}
	}
	
	
	
	
	
	
	public JSONObject toJSON(){
		//Uses the data property json as baseline 
		JSONObject obj = getProperty().toJSON();
		
		obj.put("inputType", getInputType());
		
		obj.put("type", getDatatypeName()); //the json object only includes short datatype name
		
		return obj;
		
	}

}
