/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.model.entities;

/**
 *
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public abstract class Property extends Entity implements Cloneable{

	public Property(String iri) {
		super(iri);
	}

	public abstract boolean isDataProperty();
	
	public abstract boolean isObjectProperty();
	
	public abstract DataProperty asDataProperty();
	
	public abstract ObjectProperty asObjectProperty();
	
	public Property clone() {//throws CloneNotSupportedException {
        try {
			return (Property) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
			return this;
		}
	}
	
	
	
	
	
	
	
	
}
