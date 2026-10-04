/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.model.entities;

import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLRuntimeException;

/**
 *
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public class ObjectProperty extends Property{
	
	public ObjectProperty(String iri) {
		super(iri);
	}

	//Optional
	protected ObjectProperty inverse_property;
	

	@Override
	public boolean isDataProperty() {
		return false;
	}

	@Override
	public boolean isObjectProperty() {		
		return true;
	}

	@Override
	public DataProperty asDataProperty() {		
		throw new OWLRuntimeException("Not a Data Property");
	}

	@Override
	public ObjectProperty asObjectProperty() {		
		return this;
	}

	/**
	 * @return the inverse_property
	 */
	public ObjectProperty getInverseProperty() {
		return inverse_property;
	}

	/**
	 * @return the inverse_property
	 */
	public boolean hasInverseProperty() {
		return inverse_property!=null;
	}

	
	/**
	 * @param inverse_property the inverse_property to set
	 */
	public void setInverseProperty(ObjectProperty inverse_property) {
		this.inverse_property = inverse_property;
	}

	@Override
	public JSONObject toJSON() {
		return super.toJSON();
	}
	
	
	

	

}
