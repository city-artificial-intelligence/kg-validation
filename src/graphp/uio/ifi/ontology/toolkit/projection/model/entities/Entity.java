/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.model.entities;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.semanticweb.owlapi.model.IRI;

import uio.ifi.ontology.toolkit.projection.utils.URIUtils;


/**
 *
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public abstract class Entity implements Comparable<Entity> {
	
	protected String iri;
	protected String label="";
	protected String name;	
	protected String description="";
	protected Set<String> synonyms = new HashSet<String>();
	private boolean hidden;
	
	public Entity() {
	}
	
	public Entity(String iri) {
		setIri(iri);
	}
	
	
	/**
	 * @return the iri
	 */
	public String getIri() {
		return iri;
	}
	/**
	 * @param iri the iri to set
	 */
	public void setIri(String iri) {
		this.iri = iri;
		this.name = URIUtils.getEntityLabelFromURI(iri);
		this.label = name;
	}
	
	
	public String getNamespace(){
		return URIUtils.getNameSpaceFromURI(iri);
	}
	
	
	public String getShortIRI(){
		//TODO ns:name
		return URIUtils.getShortNamespace(IRI.create(iri)) + ":" + name;
	}
	
	/**
	 * @return the label
	 */
	public String getName() {
		return name;
	}
	
	
	
	public String getVisualRepresentation() {
		return getLabel();
	}
	
	
	/**
	 * @return the label
	 */
	public String getLabel() {
		if (label==null || label.equals(""))
			return name;
		
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		if (description==null || description.equals(""))
			return getLabel();
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the synonyms
	 */
	public Set<String> getSynonyms() {
		return synonyms;
	}
	/**
	 * @param synonyms the synonyms to set
	 */
	public void setSynonyms(Set<String> synonyms) {
		this.synonyms = synonyms;
	}
	
	/**
	 * @param synonym the synonyms to add
	 */
	public void addSynonym(String synonym) {
		this.synonyms.add(synonym);
	}
	
	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}
	/**
	 * @param hidden the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	
	public boolean equals(Object o){
		
		//System.out.println("Comparing :" + this + "  with  " + o);
		if  (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof Entity)) {
		//if (!(o.getClass() == this.getClass())) {
			return false;
		}
			
		Entity i =  (Entity)o;
		
		return equalsEntity(i);
		
	}
	
	public  int hashCode() {
		 int code = 10;
		 code = 40 * code + iri.hashCode();
		 code = 50 * code + name.hashCode();
		 return code;
		 //return iri.hashCode();
	}
	
	
	
	
	public int compareTo(Entity e){
		
		if (equals(e))
			return 0;
		
		if (label.compareTo(e.getLabel())>0)
			return -1;
		else
			return 1;
	}
	
	
	public boolean equalsEntity(Entity m){
		
		if (!iri.equals(m.getIri())){
			return false;
		}
		return true;
	}
	
	
	
	public JSONObject toJSON(){
		
		JSONObject obj = new JSONObject();
		
		obj.put("id", iri);  
		obj.put("name", name);
		obj.put("ns", getNamespace());
		obj.put("short_ns", getShortIRI());
		
		obj.put("label", getLabel());
		
		obj.put("desc", getDescription());
		
		//obj.put("icon", icon);
		
		JSONObject object_labels = new JSONObject();
		
		int i=0;
		for (String syn : getSynonyms()){
			object_labels.put(String.valueOf(i), syn);
			i++;
		}
		
		if (object_labels.length()==0)
			object_labels.put(String.valueOf(i), label);
		
		obj.put("alt_labels", object_labels);

		return obj;
		
	}
	
	public String toString(){
		
		//return iri + " - " + label;
		return "<" + iri + ">";
		
	}
	
	
	
}
