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
public class DataProperty extends Property {

	
	
	public DataProperty(String iri) {
		super(iri);
	}

	@Override
	public boolean isDataProperty() {
		return true;
	}

	@Override
	public boolean isObjectProperty() {		
		return false;
	}

	@Override
	public DataProperty asDataProperty() {		
		return this;
	}

	@Override
	public ObjectProperty asObjectProperty() {		
		throw new OWLRuntimeException("Not an Object Property");
	}

	@Override
	public JSONObject toJSON() {
		return super.toJSON();
	}
	
	

}
