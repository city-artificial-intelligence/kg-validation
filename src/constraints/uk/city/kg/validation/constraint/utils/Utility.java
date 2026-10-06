/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uk.city.kg.validation.constraint.utils;

import java.util.Calendar;

import org.semanticweb.HermiT.Prefixes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author ernesto
 * Created on 10 Jan 2017
 *
 */
public class Utility {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Utility.class);
	

	public static final String JAVA_FILE_SEPARATOR = "/";
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	//Temporal directory
	public static String tmp_directory = System.getProperty("java.io.tmpdir") + FILE_SEPARATOR;//"/tmp/";
	
	
	public enum DATALOG_ENGINE { RDFox, IRIS };
	
	//public static DATALOG_ENGINE datalog_engine = DATALOG_ENGINE.IRIS;
	public static DATALOG_ENGINE datalog_engine = DATALOG_ENGINE.RDFox;
	
	//RDFox double store
	public static boolean use_double_store = false;	
	
	
	/**
     * Checks whether the given IRI can be expanded
     * Copied from HermiT. Does not seem to be available in latest/maven version
     */
    public static boolean canBeExpanded(String iri, Prefixes hermit_prefixes) {
        if (iri.length()>0 && iri.charAt(0)=='<')
            return false;
        else {
            int pos=iri.indexOf(':');
            if (pos!=-1) {
                String prefix=iri.substring(0,pos+1);
                return hermit_prefixes.getPrefixIRIsByPrefixName().get(prefix)!=null;
            }
            else
                return false;
        }
    }
    
    
    public static String removeAngles(String quotedString) {
		if (quotedString.startsWith("<") && quotedString.endsWith(">"))
			return quotedString.substring(1, quotedString.length() - 1);
		else if (quotedString.contains(":"))
			return quotedString; 
		 
		return quotedString; 		
	}
	
	public static String addAngles(String name) {
		StringBuilder sb = new StringBuilder(); 
		sb.append("<").append(name).append(">");
		return sb.toString();
	}
	
	
	public static String getEntityLabelFromURI(String uriStr){
		
		if (uriStr.indexOf("#")>=0){// && uriStr.indexOf("#")<uriStr.length()-1){
			if (uriStr.split("#").length>1){
				return uriStr.split("#")[1];
			}
			else{				
				return "empty"+ Calendar.getInstance().getTimeInMillis();
			}
		}
		
		//For URIS like http://ontology.dumontierlab.com/hasReference
		int index = uriStr.lastIndexOf("/");
		if (index>=0){
		
			return uriStr.substring(index+1);
		}
				
		return uriStr; //For other schemas
	}
	
	public static String getReversedFacetSymbol(String symbol){
		switch (symbol){
			case ">":
				return "<=";
			case ">=":
				return "<";
			case "<":
				return ">=";
			case "<=":
				return ">";
			default:
				return symbol;
		}		
						
	}
	
	
	public static boolean print_allowed = true;
	public static boolean print_query_info_allowed = false;
	
	public static void printlnQueryInfo(String str){
		if (print_query_info_allowed)
			//System.out.println(str);
			LOGGER.info(str);
	}
	
	public static void printQueryInfo(String str){
		if (print_query_info_allowed)
			//System.out.print(str);
			LOGGER.info(str);
	}
	
	public static void println(String str){
		if (print_allowed)
			//System.out.println(str);
			LOGGER.info(str);
	}
	
	public static void print(String str){
		if (print_allowed)
			//System.out.print(str);
			LOGGER.info(str);
	}
	
	public static void setPrintAllowed(boolean allowed){
		print_allowed = allowed;
	}
	
	
	
	
}
