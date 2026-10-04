/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;

/**
 *
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public class SelectFacet extends Facet{

	private List<String> allowed_values = new ArrayList<String>();
	
	
	public SelectFacet(){
		
	}
	
	public SelectFacet(TreeSet<String> values){
		allowed_values.addAll(values);
	}
	
	
	@Override
	public String getInputType() {		
		return "select";
	}

	/**
	 * @return the allowed_values
	 */
	public List<String> getAllowedValues() {
		return allowed_values;
	}

	/**
	 * @param allowed_values the allowed_values to set
	 */
	public void setAllowedValues(List<String> allowed_values) {
		this.allowed_values = allowed_values;
	}

	/**
	 * @param allowed_values the allowed_value to add
	 */
	public void addAllowedValue(String allowed_value) {
		this.allowed_values.add(allowed_value);
	}
	
	
	
	public JSONObject toJSON(){
		//Uses parent jason as baseline 
		JSONObject obj = super.toJSON();
		
		
		JSONObject values = new JSONObject();
		
		//We assume allowed_values has been (alphabetically) ordered
		for (int i=0; i<allowed_values.size(); i++){
			values.put(String.valueOf(i), allowed_values.get(i));
		}
		
		obj.put("option", values);
		
		return obj;
		
	}
	
	
	
}
