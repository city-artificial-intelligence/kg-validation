/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.model;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.SESAME;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import uio.ifi.ontology.toolkit.projection.utils.URIUtils;
import uk.city.kg.validation.constraint.utils.Utility;


/**
*
* We project the ontology into an RDF4J (Sesame) RDF model
* 
* @author ernesto
* Created on 5 Jan 2017
*
*/
public class GraphProjection {
	
	//Will contains links, facets for a concepts
	//Subclass as special type of link
	//Range and domains as "incomplete" links
	//We index by URI of source concept
	/*Map<String, Link> links = new HashMap<String, Link>();
	//We index by URI of subclass	
	Map<String, Link> hierarchy_links = new HashMap<String, Link>();
	//We index by URI of superclass: keep this index?
	Map<String, Link> inverse_hierarchy_links = new HashMap<String, Link>();
	Map<String, Facet> facets = new HashMap<String, Facet>();
	Map<String, Concept> concepts = new HashMap<String, Concept>();
	Map<String, Property> properties = new HashMap<String, Property>();*/
	
	
	
	//Factory to create RDF model/triples
	ValueFactory vf;
	
	//RDF Model
	Model model;
	
	
	
	public GraphProjection(){
		// Create a new, empty Model object.
		model = new TreeModel();
		
		vf = SimpleValueFactory.getInstance();
	
		
	}
	
	
	public void dispose(){
		model.clear();
	}
	
	
	
	public Model getRDFModel(){		
		return model;
	}
	
	
	public void saveModel() throws IOException{
		
		saveModel(Utility.tmp_directory + "tmp_file.ttl");
	}

	public void saveModel(String file) throws IOException{
		
		//for (Statement st: model){
		//	System.out.println(st.toString());			
		//}
		
		
		FileOutputStream output_stream = new FileOutputStream(new File(file));
		Rio.write(model, output_stream, RDFFormat.TURTLE); //System.out
		output_stream.close();
		
	}
	
	
	
	
	
	public void addTriple(String subject, String predicate, String object){
		model.add(vf.createIRI(subject), vf.createIRI(predicate), vf.createIRI(object));
	}
	
	
	public void addTriple(Resource subject, IRI predicate, Value object){
		model.add(subject, predicate, object);
	}
	
	public void addLiteralTriple(String subject, String predicate, String object){
		model.add(vf.createIRI(subject), vf.createIRI(predicate), vf.createLiteral(object));
	}
	
	
	public void addLiteralTriple(Resource subject, IRI predicate, Value object){
		model.add(subject, predicate, object);
	}
	
	public void addTypeTriple(String subject, String object){
		model.add(vf.createIRI(subject), RDF.TYPE, vf.createIRI(object));
	}
	
	public void addTypeTriple(Resource subject, Value object){
		model.add(subject, RDF.TYPE, object);
	}
	
	
	public void addDirectTypeTriple(String subject, String object){
		model.add(vf.createIRI(subject), vf.createIRI(URIUtils.DIRECT_TYPE), vf.createIRI(object));
	}
	
	public void addDirectTypeTriple(Resource subject, Value object){
		model.add(subject, vf.createIRI(URIUtils.DIRECT_TYPE), object);
	}
	
	public void addObjectPropertyTypeTriple(String subject){
		model.add(vf.createIRI(subject), RDF.TYPE, OWL.OBJECTPROPERTY);
	}
	
	public void addObjectPropertyTypeTriple(Resource subject){
		model.add(subject, RDF.TYPE, OWL.OBJECTPROPERTY);
	}
	
	
	public void addDataPropertyTypeTriple(String subject){
		model.add(vf.createIRI(subject), RDF.TYPE, OWL.DATATYPEPROPERTY);
	}
	
	public void addDataPropertyTypeTriple(Resource subject){
		model.add(subject, RDF.TYPE, OWL.DATATYPEPROPERTY);
	}
	
	public void addClassTypeTriple(String subject){
		model.add(vf.createIRI(subject), RDF.TYPE, OWL.CLASS);
	}
	
	public void addClassTypeTriple(Resource subject){
		model.add(subject, RDF.TYPE, OWL.CLASS);
	}
	
	
	public void addFacetTypeTriple(String subject){
		model.add(vf.createIRI(subject), RDF.TYPE, vf.createIRI(URIUtils.facet_uri));
	}
	
	public void addFacetTypeTriple(Resource subject){
		model.add(subject, RDF.TYPE, vf.createIRI(URIUtils.facet_uri));
	}
	
	
	
	
	public void addSubClassTriple(String subject, String object){
		model.add(vf.createIRI(subject), RDFS.SUBCLASSOF, vf.createIRI(object));
	}
	
	public void addSubClassTriple(Resource subject, Value object){
		model.add(subject, RDFS.SUBCLASSOF, object);
	}
	
	
	public void addSubPropertyTriple(String subject, String object){
		model.add(vf.createIRI(subject), RDFS.SUBPROPERTYOF, vf.createIRI(object));
	}
	
	public void addSubPropertyTriple(Resource subject, Value object){
		model.add(subject, RDFS.SUBPROPERTYOF, object);
	}
	
	
	public void addLabelTriple(Resource subject, Value object){
		model.add(subject, RDFS.LABEL, object);
	}
	
	public void addLabelTriple(String subject, String object){
		model.add(vf.createIRI(subject), RDFS.LABEL, vf.createLiteral(object));
	}
	
	public void addCommentTriple(Resource subject, Value object){
		model.add(subject, RDFS.COMMENT, object);
	}
	
	public void addCommentTriple(String subject, String object){
		model.add(vf.createIRI(subject), RDFS.COMMENT, vf.createLiteral(object));
	}
	
	
	public void addHiddenTriple(Resource subject){
		model.add(subject, vf.createIRI(URIUtils.hidden_uri), vf.createLiteral(true));
	}
	
	public void addHiddenTriple(String subject){
		addHiddenTriple(vf.createIRI(subject));
	}
	
	public void addMainArtifactTriple(Resource subject){
		model.add(subject, vf.createIRI(URIUtils.is_main_artifact_gic_uri), vf.createLiteral(true));
	}
	
	public void addMainArtifactTriple(String subject){
		addMainArtifactTriple(vf.createIRI(subject));
	}
	
	
	public void addInverseTriple(Resource subject, Value object){
		model.add(subject, OWL.INVERSEOF, object);
	}
	
	public void addInverseTriple(String subject, String object){
		model.add(vf.createIRI(subject), OWL.INVERSEOF, vf.createIRI(object));
		
	}
	
	public void addRangeTriple(Resource subject, Value object){
		model.add(subject, RDFS.RANGE, object);
	}
	
	public void addRangeTriple(String subject, String object){
		model.add(vf.createIRI(subject), RDFS.RANGE, vf.createIRI(object));
		//alternative to RANGE: URIUtils.data_range_uri
	}
	
	
	public void addValueTriple(Resource subject, Value object){
		model.add(subject, RDF.VALUE, object);
	}
	
	public void addValueTriple(String subject, String object){
		model.add(vf.createIRI(subject), RDF.VALUE, vf.createLiteral(object));
	}


	/**
	 * @param new_facet_uri
	 * @param stringID
	 */
	public void addFacetScope(String subject, String object) {
		addFacetScope(vf.createIRI(subject), vf.createLiteral(object));
		
	}
	
	
	public void addFacetScope(Resource subject, Value object) {
		model.add(subject, vf.createIRI(URIUtils.scope_facet_uri), object);
	}
	
	
	
	
	// 
	
	
	
	

	
	
	
	
	
	

}
