/*******************************************************************************
 * Copyright 2017 by the Department of Informatics (University of Oslo)
 * 
 *    This file is part of the Ontology Services Toolkit 
 *
 *******************************************************************************/
package uio.ifi.ontology.toolkit.projection.model;

import org.json.JSONObject;

/**
 *
 * @author ernesto
 * Created on 5 Jan 2017
 *
 */
public class SliderFacet extends Facet{
	
	private String min_value;
	private String max_value;
	
	//Step value: //1, 0.1, 0.01, ...., 0.000001,....
	private String step_value="1";

	
	
	public SliderFacet(){
		
	}
	
	
	public SliderFacet(String minValue, String maxValue){
		min_value = minValue;
		max_value = maxValue;
		setStepValue();
		
	}
	
	
	@Override
	public String getInputType() {
		//return "range-slider";
		return "rangeSlider";
	}

	/**
	 * @return the min_value
	 */
	public String getMinValue() {
		return min_value;
	}

	/**
	 * @param min_value the min_value to set
	 */
	public void setMinValue(String min_value) {
		this.min_value = min_value;
	}

	/**
	 * @return the max_value
	 */
	public String getMaxValue() {
		return max_value;
	}

	/**
	 * @param max_value the max_value to set
	 */
	public void setMaxValue(String max_value) {
		this.max_value = max_value;
	}

	/**
	 * @return the step_value
	 */
	public String getStepValue() {
		return step_value;
	}

	/**
	 * @param step_value the step_value to set
	 */
	public void setStepValue(String step_value) {
		this.step_value = step_value;
	}
	
	
	public void setStepValue() {
		setStepValue(getMinValue(), getMaxValue());
	}
	
	public void setStepValue(String minFacet, String maxFacet) {
		
		if (minFacet==null || maxFacet==null){
			//this.step_value="1";
			return;
		}
					
		this.step_value = getStepValueForFacet(minFacet);
		String aux = getStepValueForFacet(maxFacet);		
		
		//We keep the smallest stepvalue, that is, the longest string with decimals 
		if (aux.length()>this.step_value.length())
			this.step_value = aux;
	}
	
	private String getStepValueForFacet(String restriction){
		
		int num_dec;
				
		//remove unnecessary xxxx.0
		if (restriction.endsWith(".0")){
			restriction = restriction.split("\\.")[0];
		}
		
		if (restriction.contains(".")){
			num_dec = restriction.split("\\.")[1].length();
			//obj.put("stepValue", getStepvalue(num_dec)); //default
			return createDecimalStepvalue(num_dec);  //Decimal step value: 0.1, 0.01, ...., 0.000001,....
		}
		else{
			return "1"; //default
		}
	}
	
	private String createDecimalStepvalue(int num_dec){
		
		String step_value="0.";
		
		for (int i=0; i<num_dec-1; i++){
			step_value+="0";
		}
		
		step_value+="1";
		
		return step_value;
		
	}
	
	
	
	public JSONObject toJSON(){
		//Uses parent jason as baseline 
		JSONObject obj = super.toJSON();
		
		
		JSONObject min_max_avlues = new JSONObject();
		min_max_avlues.put("minInclusive", getMinValue());
		min_max_avlues.put("maxInclusive", getMaxValue());
						
		obj.put("option", min_max_avlues);
		
		//Decimal step value						
		obj.put("stepValue", getStepValue());
		
		return obj;
		
	}
	
	

}
