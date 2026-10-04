/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.model.entities;

import java.util.Random;
import java.util.Set;

import org.json.JSONObject;

/**
*
* Nodes in the projection graph
* 
* @author ernesto
* Created on 5 Jan 2017
*
*/
public class Concept extends Entity implements Cloneable{
	
	//Indicates if concepts instances can be plotted in a map
	private boolean geoLocation;
	//Indicates if the instances of the class store temporal information
	private boolean temporal;
	//Indicates if the concept should be hidden in visualizations of the graph projection
	private boolean hidden;
	
	
	
	public Concept(String iri) {
		super(iri);
		
	}

	
	
	/**
	 * @return the geoLocation
	 */
	public boolean isGeoLocation() {
		return geoLocation;
	}
	/**
	 * @param geoLocation the geoLocation to set
	 */
	public void setGeoLocation(boolean geoLocation) {
		this.geoLocation = geoLocation;
	}
	/**
	 * @return the temporal
	 */
	public boolean isTemporal() {
		return temporal;
	}
	/**
	 * @param temporal the temporal to set
	 */
	public void setTemporal(boolean temporal) {
		this.temporal = temporal;
	}
	
	public Concept clone() {// throws CloneNotSupportedException {
        try {
			return (Concept) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this;
		}
	}
	
	@Override
	public JSONObject toJSON() {
		
		JSONObject obj = super.toJSON();
		
		//TODO Estimation of the enumber of answers (now it is just a dummy number)
		Random random = new Random();
		obj.put("cnt", random.nextInt(1000));

		return obj;
	}
	
	
	
	
	

}
