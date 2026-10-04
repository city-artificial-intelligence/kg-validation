/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.utils;

import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;


/**
 *
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public class URIUtils {

	public static String rdf_label_uri = "http://www.w3.org/2000/01/rdf-schema#label";
	public static String rdf_comment_uri = "http://www.w3.org/2000/01/rdf-schema#comment";
	
	private static String skos_label_uri =    "http://www.w3.org/2004/02/skos/core#prefLabel";	                                        
	private static String skos_altlabel_uri = "http://www.w3.org/2004/02/skos/core#altLabel";
	
	private static String SYN_synonym = "http://purl.bioontology.org/ontology/SYN#synonym";											   
	
	private static String EFO_synonym = "http://www.ebi.ac.uk/efo/alternative_term";
	
	private static String foaf_name_uri = "http://xmlns.com/foaf/0.1/name";
	
	private static String NCI_synonym = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN";
	private static String OBO_synonym = "http://purl.obolibrary.org/obo/synonym";
	private static String CSEO_synonym = "http://scai.fraunhofer.de/CSEO#Synonym";
	
	
	private static String BIRNLEX_prefLabel = "http://bioontology.org/projects/ontologies/birnlex#preferred_label";
	private static String BIRNLEX_synonym = "http://bioontology.org/projects/ontologies/birnlex#synonyms";
	
	
	
	private static String hasRelatedSynonym_uri = "http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym";
	private static String hasExactSynonym_uri   = "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym";
	
	private static String hasRelatedSynonym_uri2 = "http://www.geneontology.org/formats/oboInOWL#hasRelatedSynonym";
	private static String hasExactSynonym_uri2   = "http://www.geneontology.org/formats/oboInOWL#hasExactSynonym";
	
	private static String nci_synonym_uri = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Synonym";
	private static String fma_synonym_uri="http://bioontology.org/projects/ontologies/fma/fmaOwlDlComponent_2_0#Synonym";
	private static String hasDefinition_uri="http://www.geneontology.org/formats/oboInOwl#hasDefinition";
	private static String xbrl_label_uri="http://www.xbrl.org/2003/role/label";
	
	public static final String OWL_THING= "http://www.w3.org/2002/07/owl#Thing";
	public static final String OWLTopObjectProperty= "http://www.w3.org/2002/07/owl#topObjectProperty";
	public static final String OWLTopDataProperty= "http://www.w3.org/2002/07/owl#topDataProperty";
	public static final String OWL_SAMEAS= "http://www.w3.org/2002/07/owl#sameAs";
	
	
	public static final String DC_CONTRIBUTOR = "http://purl.org/dc/terms/contributor";
	public static final String DC_DATESUBMITTED = "http://purl.org/dc/terms/dateSubmitted";
	public static final String DC_CREATOR = "http://purl.org/dc/terms/creator";
	
	
	public static String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
	public static String XSD_DOUBLE = "http://www.w3.org/2001/XMLSchema#double";
	public static String XSD_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
	public static String XSD_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";
	//public static String XSD_INTEGER = "http://www.w3.org/2001/XMLSchema#int";
	public static String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";
	
	
	public static String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	//For convenience
	public static String DIRECT_TYPE = "http://no.sirius.ontology/annotations#direct_type";
		
	
	
	public static String XSD_DATETIMESTAMP = "http://www.w3.org/2001/XMLSchema#dateTimeStamp";
	
	
	
	//Geological image annotations (SIRIUS)
	public static String is_main_artifact_gic_uri = "http://no.sirius.ontology/annotations#isMainArtefactClass";
	
	
	
	//Optique annotations
	public static String geolocation_optique_uri = "http://eu.optique.ontology/annotations#geoLocation";
	public static String temporal_optique_uri = "http://eu.optique.ontology/annotations#temporal";
	public static String data_values_optique_uri = "http://eu.optique.ontology/annotations#data_values";
	public static String domain_class_optique_uri = "http://eu.optique.ontology/annotations#domain_class";
	public static String range_class_optique_uri = "http://eu.optique.ontology/annotations#range_class";
	public static String min_value_optique_uri = "http://eu.optique.ontology/annotations#min_value";
	public static String max_value_optique_uri = "http://eu.optique.ontology/annotations#max_value";
	
	public static String hidden_optique_uri = "http://eu.optique.ontology/annotations#hidden";
	
	
	// Inverse label and comment.
	public static String inv_label_uri = "http://eu.optique.ontology/annotations#inverse_label";
	public static String inv_comment_uri = "http://eu.optique.ontology/annotations#inverse_comment";
	//New one for hidden properties
	public static String hidden_uri = "http://no.sirius.ontology/annotations#hidden";			
	//End Optique annotations
	
	
	public static String min_value_uri = "http://no.sirius.ontology/graphp#min_value";
	public static String max_value_uri = "http://no.sirius.ontology/graphp#max_value";
	public static String select_facet_uri = "http://no.sirius.ontology/graphp#SelectFacet";
	public static String slider_facet_uri = "http://no.sirius.ontology/graphp#SliderFacet";
	public static String facet_uri = "http://no.sirius.ontology/graphp#Facet";
	public static String data_range_uri = "http://no.sirius.ontology/graphp#data_range";
	
	
	public static String scope_facet_uri = "http://no.sirius.ontology/graphp#scope";
	
	
	public static final String GEOLOCATION = "geoLocation";
	public static final String TEMPORAL = "temporal";
	
								  
	
	
	
	public static Set<String> accepted_annotation_properties = new HashSet<String>();
	
	
	public static OntologyIRIShortFormProvider xutils = new OntologyIRIShortFormProvider();
	
	
	static {
		//accepted_annotation_URIs = new HashSet<String>();
		accepted_annotation_properties.add(rdf_label_uri);
		accepted_annotation_properties.add(hasExactSynonym_uri);
		accepted_annotation_properties.add(hasRelatedSynonym_uri);
		
		accepted_annotation_properties.add(hasExactSynonym_uri2);
		//TODO
		//accepted_annotation_URIs_for_classes.add(hasRelatedSynonym_uri2);
		
		accepted_annotation_properties.add(nci_synonym_uri);
		accepted_annotation_properties.add(fma_synonym_uri);
		accepted_annotation_properties.add(hasDefinition_uri);
		accepted_annotation_properties.add(xbrl_label_uri);
		
		accepted_annotation_properties.add(skos_label_uri);
		accepted_annotation_properties.add(skos_altlabel_uri);
		accepted_annotation_properties.add(foaf_name_uri);
		
		accepted_annotation_properties.add(SYN_synonym);
		accepted_annotation_properties.add(EFO_synonym);
		
		accepted_annotation_properties.add(NCI_synonym);
		accepted_annotation_properties.add(OBO_synonym);
		accepted_annotation_properties.add(CSEO_synonym);
		
		accepted_annotation_properties.add(BIRNLEX_synonym);
		accepted_annotation_properties.add(BIRNLEX_prefLabel);
	}
	
	
	
	public static String getEntityLabelFromURI(String uriStr){
		
		
		if (uriStr.indexOf("#")>=0){// && uriStr.indexOf("#")<uriStr.length()-1){
			if (uriStr.split("#").length>1){
				return uriStr.split("#")[1];
			}
			else{ //No name in URI
				return "empty"+ Calendar.getInstance().getTimeInMillis();
			}
		}
	
		//For URIS like http://ontology.dumontierlab.com/hasReference
		int index = uriStr.lastIndexOf("/");
		if (index>=0){
			return uriStr.substring(index+1);
		}
		
		return uriStr;
	}
	
	
	public static String getNameSpaceFromURI(String uriStr){
		if (uriStr.startsWith("http")){
			if (uriStr.indexOf("#")>=0){
				return uriStr.split("#")[0]+"#";
			}
			else {
				//For URIS like http://ontology.dumontierlab.com/hasReference
				int index = uriStr.lastIndexOf("/");
				if (index>=0){
					return uriStr.substring(0, index+1);
				}
			}
			return uriStr;
		}
		else
			return "";
		
	}
	
	
	public static String getShortNamespace(IRI iri){
		return xutils.getShortForm(iri);
	}
	
	public static boolean isValidURI(String urlString)
	{
	    try
	    {
	        URL url = new URL(urlString);
	        url.toURI();
	        return true;
	    } catch (Exception exception)
	    {
	        return false;
	    }
	}
}


