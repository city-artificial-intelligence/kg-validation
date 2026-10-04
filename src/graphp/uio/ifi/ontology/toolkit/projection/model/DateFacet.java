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
 * Created on 5 Jan 2017
 *
 */
public class DateFacet extends Facet{

		
	@Override
	public String getInputType() {
		return "date-range";
	}

}
