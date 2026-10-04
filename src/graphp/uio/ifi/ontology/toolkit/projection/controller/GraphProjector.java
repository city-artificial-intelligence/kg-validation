/*******************************************************************************
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.controller;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitor;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataRangeVisitor;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNaryBooleanClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.OWLQuantifiedDataRestriction;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import uio.ifi.ontology.toolkit.projection.model.GraphProjection;
import uio.ifi.ontology.toolkit.projection.utils.URIUtils;

/**
 * 
 * This class will implement the graph projection using the OWL API 
 * built in OWL axiom visitor. The projection will be store in an RDF model.
 * 
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public class GraphProjector extends OWLAxiomVisitorAdapter {
	
	GraphProjection graph;
	OWLOntology ontology; //we get the source ontology as an additional input
	
	private FillerClassExpressionVisitor visitor_2level = new FillerClassExpressionVisitor();
	private ClassExpressionVisitor visitor_1level = new ClassExpressionVisitor();
	
	private AnnotationValueVisitor ann_value_visitor = new AnnotationValueVisitor();
	private AnnotationSubjectVisitor ann_subject_visitor = new AnnotationSubjectVisitor();
	
	private DataRangeVisitor data_visitor = new DataRangeVisitor();
	
	
	
	//In principle will be one, with some exceptions
	private Set<OWLClass> source_classes = new HashSet<OWLClass>();
		
	//For links	
	private Set<OWLClass> target_classes = new HashSet<OWLClass>();
	private OWLObjectProperty OP;
			
	//For facets	
	private OWLDataRange data_range;			
	private OWLDataProperty DP;
	private boolean isEnumeration = false;
	private boolean isDatatypeRestriction = false;
	private boolean hasRangeType = false;
	private Set<String> values = new HashSet<String>();
	private String min_value;
	private String max_value;
	private OWLDatatype datatype;
	
	private boolean create_facets;
	
	
	
	/**
	 * 
	 * @param graph The RDF model to populate.
	 * @param ontology The OWL ontology to poulate
	 */
	public GraphProjector(GraphProjection graph, OWLOntology ontology, boolean create_facets){
		
		//It modifies/extends this object
		this.graph = graph;
		
		this.ontology = ontology;
		
		this.create_facets=create_facets;
		
		
		//Visit axioms ontology to project as RDF graphs
		for (OWLAxiom ax : ontology.getAxioms(Imports.INCLUDED)){
			ax.accept(this);
		}
		
		
		//OPTIQUE ANNOTATIONS compatibility
		//Visit signature and annotations  and project as RDF graphs
		for (OWLObjectProperty op : ontology.getObjectPropertiesInSignature(Imports.INCLUDED)){			
			createTriplesForOptiqueAnnotations(op);
		}
		for (OWLDataProperty dp : ontology.getDataPropertiesInSignature(Imports.INCLUDED)){			
			createTriplesForOptiqueAnnotations(dp);
		}
		//END Optique annotations compatibility
		
		
	}
	
	
	//Call when analysing the axioms
	protected void resetStructures(){
		source_classes.clear();
		target_classes.clear();
		values.clear();
		visitor_1level.isObjectRestriction=false;
		visitor_1level.isDataRestriction=false;		
		isEnumeration=false;
		isDatatypeRestriction=false;
		hasRangeType=false;
	
	}
	
	
	
	protected void createLinks(){
		
		//Exploit inverses here? Done at query level!
		
		
		//Avoid these triples
		if (OP.isOWLTopObjectProperty())
			return;
			
		graph.addObjectPropertyTypeTriple(OP.toStringID());
		
		
		for (OWLClass source: source_classes){
			for (OWLClass target: target_classes){
				graph.addTriple(source.toStringID(), OP.toStringID(), target.toStringID());
			}
		}
		
	}
	
	int facet_id = 0;
	
	protected void createFacet(){
		
		
		//Use data range visitor results
		//We use DP and Source classes and data range	
		
		//Avoid these triples
		if (DP.isOWLTopDataProperty())
			return;
		
		
		graph.addDataPropertyTypeTriple(DP.toStringID());
		
		
		
		
		//TODO We do not add facet nodes in the projection for embeddings
		if (!create_facets)
			return;
		
		
		
		for (OWLClass cls : source_classes){
			
			//There should be at least the TopDataype as range
			if (hasRangeType){
				
				String new_facet_uri = URIUtils.facet_uri+Calendar.getInstance().getTimeInMillis() + (facet_id++);
											
				//add triple pointing to facet
				graph.addTriple(cls.toStringID(), DP.toStringID(), new_facet_uri);
				
				//add triple with Facet type
				graph.addFacetTypeTriple(new_facet_uri);
				
				
				graph.addRangeTriple(new_facet_uri, datatype.toStringID());
				
				
				if (isDatatypeRestriction || isEnumeration){
					///This field indicated the scope for which a facet was defined. This is relevant for enumerations and ranges
					//to avoid bottom-up propagation and give priority to the focus-related facets. 					
					graph.addFacetScope(new_facet_uri, cls.toStringID());
					
				}
				
				if (isDatatypeRestriction){
					
					//add triple with SliderFacet type
					graph.addTypeTriple(new_facet_uri, URIUtils.slider_facet_uri);
					
					//min max values
					graph.addLiteralTriple(new_facet_uri, URIUtils.min_value_uri, min_value);
					graph.addLiteralTriple(new_facet_uri, URIUtils.max_value_uri, max_value);
					
					
					
				}
				else if (isEnumeration){
					
					//add triple with SelectFacet type
					graph.addTypeTriple(new_facet_uri, URIUtils.select_facet_uri);
					
					
					//values
					for (String value : values){
						graph.addValueTriple(new_facet_uri, value);
					}
					
				}
									
				
				
			}
			
			
		}
		
	}
	
	protected void createHierarchyLink(OWLClass sub, OWLClass sup){
		
		//Avoid Top sub A, A sub Bottom, and Bottom sub A
		if (sub.isTopEntity() || sub.isBottomEntity() || sup.isBottomEntity())
			return;
		
		
		graph.addClassTypeTriple(sub.toStringID()); //Only subclass: the taxonomy will contain A sub Top, B sub A, etc.
		//We do not add Top as a class (we do not add the type triple). This will disallow to perform top and down propagation using top. 
		
		graph.addSubClassTriple(sub.toStringID(), sup.toStringID());
		
		
		
	}
	
	
	
	protected void createTriplesForOptiqueAnnotations(OWLObjectProperty oprop){
		
		resetStructures();
		
		OP = oprop;
		
		for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(oprop.getIRI())){
			//TODO Fill source_classes and target_classes
			
			if (ax.getProperty().isOWLAnnotationProperty()){
				
				String annotation_prop = ax.getAnnotation().getProperty().asOWLAnnotationProperty().getIRI().toString();
				
				if (annotation_prop.equals(URIUtils.domain_class_optique_uri)){
					
					ax.getValue().accept(ann_value_visitor);
					if (ann_value_visitor.isIRI()){
						source_classes.add(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(IRI.create(ann_value_visitor.getAnnotationValue())));
					}
					
					
				}					
				else if (annotation_prop.equals(URIUtils.range_class_optique_uri)){
					
					ax.getValue().accept(ann_value_visitor);
					if (ann_value_visitor.isIRI()){
						target_classes.add(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(IRI.create(ann_value_visitor.getAnnotationValue())));
					}
				
				}
			}
			
		}
		
		//if either source or target are empty then add Top
		if (source_classes.size()==0 && target_classes.size()==0)
			return; //nothing to add
		else if (source_classes.size()==0)
			source_classes.add(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
		else if (target_classes.size()==0)
			target_classes.add(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
		
		createLinks();
		
	}
	
	
	protected void createTriplesForOptiqueAnnotations(OWLDataProperty dprop){
		resetStructures();
		
		DP = dprop;
		datatype = ontology.getOWLOntologyManager().getOWLDataFactory().getTopDatatype(); //default
		
		for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(dprop.getIRI())){
			
				//TODO Fill source_classes, hasRangeType, rangeType flags, values, min_value, max_value, datatype
				if (ax.getProperty().isOWLAnnotationProperty()){
				
					String annotation_prop = ax.getAnnotation().getProperty().asOWLAnnotationProperty().getIRI().toString();
				
					if (annotation_prop.equals(URIUtils.min_value_optique_uri)){
						
						ax.getValue().accept(ann_value_visitor);
						
						if (!ann_value_visitor.getAnnotationValue().equals("")){
							min_value = ann_value_visitor.getAnnotationValue();
							isDatatypeRestriction = true;
						}
						
					}							
					else if (annotation_prop.equals(URIUtils.max_value_optique_uri)){
						
						ax.getValue().accept(ann_value_visitor);
						
						if (!ann_value_visitor.getAnnotationValue().equals("")){
							max_value = ann_value_visitor.getAnnotationValue();
							isDatatypeRestriction = true;
						}
						
						
					}
					else if (annotation_prop.equals(URIUtils.domain_class_optique_uri)){
						ax.getValue().accept(ann_value_visitor);
						if (ann_value_visitor.isIRI()){
							source_classes.add(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(IRI.create(ann_value_visitor.getAnnotationValue())));
						}
						
						
					}					
					else if (annotation_prop.equals(URIUtils.range_class_optique_uri)){
						
						ax.getValue().accept(ann_value_visitor);
						if (ann_value_visitor.isIRI()){
							datatype = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLDatatype(IRI.create(ann_value_visitor.getAnnotationValue()));
							hasRangeType=true;
						}
						
					}
					else if (annotation_prop.equals(URIUtils.data_values_optique_uri)){
						
						ax.getValue().accept(ann_value_visitor);
						
						if (!ann_value_visitor.getAnnotationValue().equals("")){
							//updated values set
							splitValues(ann_value_visitor.getAnnotationValue());
							isEnumeration = true;
						}
						
					}
				}
			
			
		}
		
		if (source_classes.size()==0 && !hasRangeType && !isEnumeration && !isDatatypeRestriction){
			return; //nothing to add
		}
		//if sources or target datatype are empty then add Tops if required...
		else if (source_classes.size()==0){
			source_classes.add(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
		}
		//else: datatype is already set to top property as default
		hasRangeType=true;
		
		createFacet();
		
	}
	

	/**
	 * Values are given separated by ","
	 * @param values_str
	 */
	private void splitValues(String values_str){
		
		
		String[] split_values = values_str.split(",");
		
			
		try {
		
			//There were some codification issues with Norwegian characteres
			for (String value : split_values){
				
				byte[] utf8;
				
				utf8 = new String(value.getBytes(), "ISO-8859-1").getBytes("UTF-8");
				
	
				byte[] latin1 = new String(utf8, "UTF-8").getBytes("ISO-8859-1");
						
				String iso_string = new String(latin1, "ISO-8859-1");
								
				values.add(iso_string);
				
			}				
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void visit(OWLAnnotationAssertionAxiom axiom) {
		
		resetStructures();
		
		//Create triple for the annotation		
	
		axiom.getSubject().accept(ann_subject_visitor);
		
		
		String ann_subject_iri = ann_subject_visitor.getSubjectIRIStr();
		
		String ann_property_iri = axiom.getProperty().toStringID();
		
		
		//Label-synomys
		if (URIUtils.accepted_annotation_properties.contains(ann_property_iri)){
			
			axiom.getValue().accept(ann_value_visitor);
			
			if (!ann_value_visitor.getAnnotationValue().equals("")){
				graph.addLabelTriple(ann_subject_iri, ann_value_visitor.getAnnotationValue());
			}
			
		}
		else if (ann_property_iri.equals(URIUtils.rdf_comment_uri)){ //comment-description
			
			axiom.getValue().accept(ann_value_visitor);
			
			if (!ann_value_visitor.getAnnotationValue().equals("")){
				graph.addCommentTriple(ann_subject_iri, ann_value_visitor.getAnnotationValue());
			}
		}
		else if (ann_property_iri.equals(URIUtils.hidden_uri) || ann_property_iri.equals(URIUtils.hidden_optique_uri)){ //hidden URI
			
			axiom.getValue().accept(ann_value_visitor);
			
			if (!ann_value_visitor.getAnnotationValue().equals("") && !ann_value_visitor.getAnnotationValue().toLowerCase().equals("false")){
				graph.addHiddenTriple(ann_subject_iri);//, ann_value_visitor.getAnnotationValue());
			}
		}
		
		//Main artifact entity (e.g. geological image)
		else if (ann_property_iri.equals(URIUtils.is_main_artifact_gic_uri)){ 
			
			axiom.getValue().accept(ann_value_visitor);
			
			if (!ann_value_visitor.getAnnotationValue().equals("") && !ann_value_visitor.getAnnotationValue().toLowerCase().equals("false")){
				graph.addMainArtifactTriple(ann_subject_iri);
			}
		}
		
		
		//TODO Add other type of annotations: e.g. optique annotations: domain, range, ...
		//Create suitable triples: some of them are meant to be interpreted in same facet!
		//Group by object property?
		//data_values_optique_uri
		//domain_class_optique_uri
		//range_class_optique_uri
		//min_value_optique_uri
		//max_value_optique_uri
		
		
	}
	
	
	
	@Override
	public void visit(OWLSubClassOfAxiom ax) {
		
		resetStructures();
		
		//A sub B: subclassOf		
		if (!ax.getSubClass().isAnonymous() && !ax.getSuperClass().isAnonymous()){
			
			createHierarchyLink(ax.getSubClass().asOWLClass(), ax.getSuperClass().asOWLClass());
			
		}
			
		//A sub CLS_EXP
		else if (!ax.getSubClass().isAnonymous() && ax.getSuperClass().isAnonymous()){
			
			source_classes.add(ax.getSubClass().asOWLClass());
			
			ax.getSuperClass().accept(visitor_1level); //populates target_classes or datarange and property
			
											
		}
		
		//CLS_EXP sub A
		else if (ax.getSubClass().isAnonymous() && !ax.getSuperClass().isAnonymous()){				
			
			//We still consider A as the potential source class
			source_classes.add(ax.getSuperClass().asOWLClass());
			
			ax.getSubClass().accept(visitor_1level); //populates target_classes or datarange and property			
			
			
		}
		
		
		if (visitor_1level.isDataRestriction)
			createFacet();
		else if (visitor_1level.isObjectRestriction)
			createLinks();
		
		
		
			
		
	}
	
	@Override
	public void visit(OWLDataPropertyDomainAxiom ax) {
		
		resetStructures();
		
		if (!ax.getProperty().isAnonymous()){
			DP = ax.getProperty().asOWLDataProperty();
		
			ax.getDomain().accept(visitor_2level);;
			
			source_classes.addAll(visitor_2level.getClasses());
			
			//We add Top datatype as data range, a more informative facet will be retrieved if it exists in subsequent step			
			data_range = ontology.getOWLOntologyManager().getOWLDataFactory().getTopDatatype();
			
			data_range.accept(data_visitor);
			
			
			
			//Facets in graph 
			createFacet();
			
		}
		
		
		
	}
	

	@Override
	public void visit(OWLObjectPropertyDomainAxiom ax) {
		
		resetStructures();
		
		if (!ax.getProperty().isAnonymous()){
			OP = ax.getProperty().asOWLObjectProperty();
		
			ax.getDomain().accept(visitor_2level);
			
			source_classes.addAll(visitor_2level.getClasses());
			
			//We add Top as target of the link (when retrieving neighbours, a more informative target node(s) will be retrieved if it exists)
			target_classes.add(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
			
			//Links in graph 
			createLinks();
			
		}		
		
	}
	
	
	@Override
	public void visit(OWLObjectPropertyRangeAxiom ax) {
		
		resetStructures();
		
		if (!ax.getProperty().isAnonymous()){
			OP = ax.getProperty().asOWLObjectProperty();
		
			ax.getRange().accept(visitor_2level);
			
			target_classes.addAll(visitor_2level.getClasses());
			
			//We add Top as source of the link (when retrieving neighbours, a more informative source node(s) will be retrieved if it exists)
			source_classes.add(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
			
			//Links in graph 
			createLinks();
		}		
		
		
		
	}
	
	
	
	@Override
	public void visit(OWLDataPropertyRangeAxiom ax) {
		
		resetStructures();
		
		if (!ax.getProperty().isAnonymous()){
			DP = ax.getProperty().asOWLDataProperty();
		
			data_range = ax.getRange();
			
			data_range.accept(data_visitor);
			
			//We add Top as source of the facet
			source_classes.add(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing());
			
			//Facets in graph 
			createFacet();
			
		}		
		
		
		
	}
	
	
	@Override
	public void visit(OWLEquivalentClassesAxiom ax) {
		
		
		for (OWLSubClassOfAxiom subax : ax.asOWLSubClassOfAxioms()){
			subax.accept(this);
		}
		
		
		/*
		int size = ax.getClassExpressionsAsList().size();
		
		for (int i=0; i<size-1; i++){
			for (int j=i+1; j<size; j++){
				
				//We visit the respective subclassof expressions
				ontology.getOWLOntologyManager().getOWLDataFactory().getOWLSubClassOfAxiom(ax.getClassExpressionsAsList().get(i), ax.getClassExpressionsAsList().get(j)).accept(this);
				ontology.getOWLOntologyManager().getOWLDataFactory().getOWLSubClassOfAxiom(ax.getClassExpressionsAsList().get(j), ax.getClassExpressionsAsList().get(i)).accept(this);
				
			}			
		}
		*/
		
		
		
		
	}
	
	
	

	
	

	@Override
	public void visit(OWLObjectPropertyAssertionAxiom ax) {
		
		resetStructures();
		
		if (!ax.getProperty().isAnonymous()){
			
			OP = ax.getProperty().asOWLObjectProperty(); 
			
			//We also add link for the assertion
			graph.addTriple(ax.getSubject().toStringID(), OP.toStringID(), ax.getObject().toStringID());
			
			
			//Target: Get types of object if any
			for (OWLClassAssertionAxiom ass_ax : ontology.getClassAssertionAxioms(ax.getObject())){				
				ass_ax.getClassExpression().accept(visitor_2level);
				target_classes.addAll(visitor_2level.getClasses());				
			}
			
			//Source: Get types of subject if any
			for (OWLClassAssertionAxiom ass_ax : ontology.getClassAssertionAxioms(ax.getSubject())){				
				ass_ax.getClassExpression().accept(visitor_2level);
				source_classes.addAll(visitor_2level.getClasses());
				
			}
			
			//Links in graph 
			createLinks();
		}
		
		
		
	}
	
	
	
	@Override
	public void visit(OWLClassAssertionAxiom ax) {
		
		//TODO Add a rdf:type A  and direct type triples. Necessary to include in the projection possible values for an object range
		
		if (ax.getIndividual().isNamed() && !ax.getClassExpression().isAnonymous()) {
			graph.addTypeTriple(ax.getIndividual().asOWLNamedIndividual().toStringID(), ax.getClassExpression().asOWLClass().toStringID());
			graph.addDirectTypeTriple(ax.getIndividual().asOWLNamedIndividual().toStringID(), ax.getClassExpression().asOWLClass().toStringID());
		}
		
	}
	
	
	
	@Override
	public void visit(OWLDataPropertyAssertionAxiom ax) {
		
		resetStructures();
		
		if (!ax.getProperty().isAnonymous()){
			
			DP = ax.getProperty().asOWLDataProperty();
			
			//We also add link for the assertion
			graph.addLiteralTriple(ax.getSubject().toStringID(), DP.toStringID(), ax.getObject().getLiteral());
			
			
			//Source: Get types of subject if any
			for (OWLClassAssertionAxiom ass_ax : ontology.getClassAssertionAxioms(ax.getSubject())){				
				ass_ax.getClassExpression().accept(visitor_2level);
				source_classes.addAll(visitor_2level.getClasses());
				
			}
			
			data_range = ax.getObject().getDatatype();
						
			//Facets in graph 
			createFacet();
		}
		
		
	}
	
	
	@Override
	public void visit(OWLInverseObjectPropertiesAxiom ax) {
		
		if (ax.getFirstProperty().isAnonymous() || ax.getSecondProperty().isAnonymous()  || ax.getSecondProperty().isOWLTopObjectProperty() || ax.getFirstProperty().isOWLTopObjectProperty())
			return;
		
		
		graph.addObjectPropertyTypeTriple(ax.getFirstProperty().asOWLObjectProperty().toStringID());
		graph.addObjectPropertyTypeTriple(ax.getSecondProperty().asOWLObjectProperty().toStringID());
		
		graph.addInverseTriple(
				ax.getFirstProperty().asOWLObjectProperty().toStringID(), 
				ax.getSecondProperty().asOWLObjectProperty().toStringID());
		
		graph.addInverseTriple(
				ax.getSecondProperty().asOWLObjectProperty().toStringID(),
				ax.getFirstProperty().asOWLObjectProperty().toStringID() 
				);
		
	}
	
	

	@Override
	public void visit(OWLEquivalentObjectPropertiesAxiom ax) {
		for (OWLSubObjectPropertyOfAxiom subax : ax.asSubObjectPropertyOfAxioms()){
			subax.accept(this);
		}
		
	}



	@Override
	public void visit(OWLSubObjectPropertyOfAxiom ax) {
		
		
		//TODO we avoid links to top object property
		if (ax.getSubProperty().isAnonymous() || ax.getSuperProperty().isAnonymous() || ax.getSuperProperty().isOWLTopObjectProperty() ||  ax.getSubProperty().isOWLTopObjectProperty())
			return;
		
		graph.addSubPropertyTriple(
				ax.getSubProperty().asOWLObjectProperty().toStringID(), 
				ax.getSuperProperty().asOWLObjectProperty().toStringID());
		
		
	}

	
	

	@Override
	public void visit(OWLEquivalentDataPropertiesAxiom ax) {
		
		for (OWLSubDataPropertyOfAxiom subax : ax.asSubDataPropertyOfAxioms()){
			subax.accept(this);
		}
				
	}





	@Override
	public void visit(OWLSubDataPropertyOfAxiom ax) {
		
		//TODO we avoid links to top data property
		if (ax.getSubProperty().isAnonymous() || ax.getSuperProperty().isAnonymous() || ax.getSuperProperty().isOWLTopDataProperty() || ax.getSubProperty().isOWLTopDataProperty())
			return;
		
		graph.addSubPropertyTriple(
				ax.getSubProperty().asOWLDataProperty().toStringID(), 
				ax.getSuperProperty().asOWLDataProperty().toStringID());
	}


	@Override
	public void visit(OWLSubPropertyChainOfAxiom ax) {
		// TODO Auto-generated method stub
		
	}

	



	@Override
	public void visit(SWRLRule ax) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	
	
	/**
	 * Class expression visitor for the required axioms
	 * First level expressions
	 *
	 * @author ernesto
	 * Created on 5 Jan 2017
	 *
	 */
	private class ClassExpressionVisitor extends OWLClassExpressionVisitorAdapter {
		
		private boolean isObjectRestriction;
		private boolean isDataRestriction;
		 
		
		private void visitObjectRestriction(OWLQuantifiedObjectRestriction cls_exp){
			
			if(!cls_exp.getProperty().isAnonymous()){
				isObjectRestriction = true;
				OP = cls_exp.getProperty().asOWLObjectProperty();			
				cls_exp.getFiller().accept(visitor_2level);
				
				target_classes.addAll(visitor_2level.getClasses());
			}		
		}
		
		
		@Override
		public void visit(OWLObjectSomeValuesFrom cls_exp) {			
			visitObjectRestriction(cls_exp);			
		}

		@Override
		public void visit(OWLObjectAllValuesFrom cls_exp) {
			visitObjectRestriction(cls_exp);			
		}
		
		
		@Override
		public void visit(OWLObjectMinCardinality cls_exp) {
			
			visitObjectRestriction(cls_exp);
			
		}

		@Override
		public void visit(OWLObjectExactCardinality cls_exp) {
			
			visitObjectRestriction(cls_exp);
			
		}

		@Override
		public void visit(OWLObjectMaxCardinality cls_exp) {
			
			visitObjectRestriction(cls_exp);
			
		}

		@Override
		public void visit(OWLObjectHasValue cls_exp) {
			
			if(!cls_exp.getProperty().isAnonymous()){
				
				isObjectRestriction = true;
				OP = cls_exp.getProperty().asOWLObjectProperty();
					
				//Get types of individual if any
				for (OWLClassAssertionAxiom ass_ax : ontology.getClassAssertionAxioms(cls_exp.getFiller())){
						
					ass_ax.getClassExpression().accept(visitor_2level);
					
					target_classes.addAll(visitor_2level.getClasses());
				}
			}			
			
		}

			
		
		private void visitDataRestriction(OWLQuantifiedDataRestriction cls_exp){
						
			if(!cls_exp.getProperty().isAnonymous()){
				isDataRestriction = true;
				DP = cls_exp.getProperty().asOWLDataProperty();
					
				data_range = cls_exp.getFiller();
				
				data_range.accept(data_visitor);
				
			}			
		}
		

		@Override
		public void visit(OWLDataSomeValuesFrom cls_exp) {
			
			visitDataRestriction(cls_exp);
			
		}

		@Override
		public void visit(OWLDataAllValuesFrom cls_exp) {
			
			visitDataRestriction(cls_exp);
			
		}
		

		@Override
		public void visit(OWLDataMinCardinality cls_exp) {
			
			visitDataRestriction(cls_exp);
			
		}

		@Override
		public void visit(OWLDataExactCardinality cls_exp) {
			
			visitDataRestriction(cls_exp);
			
		}

		@Override
		public void visit(OWLDataMaxCardinality cls_exp) {
			
			visitDataRestriction(cls_exp);
			
		}
		
		
		@Override
		public void visit(OWLDataHasValue cls_exp) {
			if(!cls_exp.getProperty().isAnonymous()){
				
				isDataRestriction = true;
				DP = cls_exp.getProperty().asOWLDataProperty();
								
				data_range = cls_exp.getFiller().getDatatype(); //datatype of value
				
				data_range.accept(data_visitor);
				
				//We treat it as a list of allowed values. In this case only one. 
				
				
				
				values.add(cls_exp.getFiller().getLiteral());
				
				
				isEnumeration = true;
				isDatatypeRestriction = false;
				hasRangeType = true;
				
				
			}			
		}
		
	}
	
	/**
	 * Visitor for filler class expressions from OWL Class restrictions
	 *
	 * @author ernesto
	 * Created on 24 Jan 2017
	 *
	 */
	private class FillerClassExpressionVisitor extends OWLClassExpressionVisitorAdapter {
		
		private Set<OWLClass> classes = new HashSet<OWLClass>();
		
		public Set<OWLClass> getClasses(){
			return classes;
		}
	
		@Override
		public void visit(OWLClass cls) {
			
			classes.clear();
			
			classes.add(cls);
			
		}
		
		private void visitExpressionWithListOfOperands(OWLNaryBooleanClassExpression cls_exp){
			
			classes.clear();
			
			for (OWLClassExpression cls : cls_exp.getOperands()){
				if (!cls.isAnonymous())
					classes.add(cls.asOWLClass());
			}
			
		}
		

		@Override
		public void visit(OWLObjectIntersectionOf cls_exp) {
			
			visitExpressionWithListOfOperands(cls_exp);
			
		}

		@Override
		public void visit(OWLObjectUnionOf cls_exp) {
			
			visitExpressionWithListOfOperands(cls_exp);
			
		}
		
	}
	
	
	
	
	/**
	 * Visitor to create facets
	 *
	 * @author ernesto
	 * Created on 5 Jan 2017
	 *
	 */
	public class DataRangeVisitor implements OWLDataRangeVisitor{

		public void visit(OWLDataOneOf range) {
		
			isEnumeration = true;
			isDatatypeRestriction = false;
			hasRangeType = true;
			
			values.clear();
			
			for (OWLLiteral lit : range.getValues()){
				values.add(lit.getLiteral().toString());
				datatype = lit.getDatatype(); //we keep only one datatype
			}
			
		}
		
		
		@Override
		public void visit(OWLDatatypeRestriction range) {
			
			isEnumeration = false;
			isDatatypeRestriction = true;
			hasRangeType = true;
			
			try {
				
				String value_facet;
				
				datatype = range.getDatatype();
				
				
				for (OWLFacetRestriction facet : range.getFacetRestrictions()){
					
					value_facet = facet.getFacetValue().getLiteral().toString();
					
					if (facet.getFacet().getShortForm().equals("minInclusive") || facet.getFacet().getShortForm().equals("minExclusive")){
						
						min_value =  value_facet;
					}
					else{
						max_value =  value_facet;
					}				
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		@Override
		public void visit(OWLDatatype rangeType) {
			
			isEnumeration = false;
			isDatatypeRestriction = false;
			hasRangeType = true;
			
			
			try {
				
				datatype = rangeType;
				
				
				
			} catch (Exception e) {

				e.printStackTrace();
			}
			
		}
			
		

		@Override
		public void visit(OWLDataComplementOf arg0) {
			isEnumeration = false;
			isDatatypeRestriction = false;
			hasRangeType = false;			
		}

		@Override
		public void visit(OWLDataIntersectionOf arg0) {
			isEnumeration = false;
			isDatatypeRestriction = false;
			hasRangeType = false;
		}

		@Override
		public void visit(OWLDataUnionOf arg0) {
			isEnumeration = false;
			isDatatypeRestriction = false;
			hasRangeType = false;
		}


		


		

	}
	
	
	
	
	/**
	 * Exploit annotations if given
	 *
	 * @author ernesto
	 * Created on 5 Jan 2017
	 *
	 */
	private class AnnotationValueVisitor implements OWLAnnotationValueVisitor {

		
		boolean isDataValue=false;
		boolean isIRI=false;
		
		String value="";
		
		
		public String getAnnotationValue(){
			return value;
		}
		
		
		/**
		 * @return
		 */
		public boolean isIRI() {
			return isIRI;
		}


		@Override
		public void visit(IRI IRI) {
			
			isDataValue=false;
			isIRI=true;
			
			value = IRI.toString();
			
		}

		@Override
		public void visit(OWLAnonymousIndividual geneid_value) {
		
			isDataValue=false;
			isIRI=false;
			
			
			value = "";
			
			
		}

		@Override
		public void visit(OWLLiteral literal) {
			
			
			try	{
				//LogOutput.print(((OWLLiteral)annAx.getAnnotation().getValue()).getLiteral());
				
				String label = literal.getLiteral();//.toLowerCase();
				
				//System.err.println(entityAnnAx + " " + label);
				
				if (label==null || label.equals("null") || label.equals("")){
					//System.err.println("NULL LABEL: " + entityAnnAx);
					value = "";
				}
				
						
				value = label;
				
				isDataValue=true;
				
				
			}
			catch (Exception e){
				//In case of error. Accessing an object in an expected way				
				isIRI=false;
				isDataValue=false;
				value = "";
			}
			
		}
		
	}
	
	
	
	private class AnnotationSubjectVisitor implements OWLAnnotationSubjectVisitor{

		
		String iri_str="";
		
		
		public String getSubjectIRIStr(){
			return iri_str;
		}
		
		
		@Override
		public void visit(IRI iri) {
			iri_str = iri.toString();
			
		}

		@Override
		public void visit(OWLAnonymousIndividual individual) {
			iri_str = "";
			
		}
		
	}
	
	
	
	
	/**
	 * Probably not necessary in the end
	 *
	 * @author ernesto
	 * Created on 24 Jan 2017
	 *
	 */
	private class PropertyExpressionVisitor implements OWLPropertyExpressionVisitor{

		@Override
		public void visit(OWLObjectProperty arg0) {
			
			
		}

		@Override
		public void visit(OWLObjectInverseOf arg0) {
			
			
		}

		@Override
		public void visit(OWLDataProperty arg0) {
			
			
		}

		@Override
		public void visit(OWLAnnotationProperty arg0) {
			
			
		}
		
	}

}
