/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.model;

/**
 *
 * @author ernesto
 * Created on 31 Jan 2018
 *
 */
public class BooleanFacet extends SelectFacet {
	
	public BooleanFacet(){
		addAllowedValue("false"); //position 0
		addAllowedValue("true");  //position 1
		
		setDatatype("boolean");		
	}

}
