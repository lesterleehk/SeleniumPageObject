package com.java.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class CriteriaParser {
	
	
	/**
	 * Usage example: Appraisal.java>runCheckStatus> loadExpectedResult
	 * Status1:Started
	 * Status2:MID.label
	 * Status3:Completed
	 * 
	 * @param keySpliter is :
	 * @param strToParse
	 * @return
	 */
	
	public static HashMap<String, ArrayList<String>> parseKeyValueList(String keySpliter, String strToParse) {
		return parseKeyValueList (keySpliter, null, "\n", strToParse);
	}
	
	/**
	 * Usage example: runAutoEnroll case in LearningModule.java
	 * enrollment: uma_sara;bfeng
	 * 
	 * @param keySpliter is :
	 * @param valueSpliter is ;
	 * @param strToParse
	 * @return
	 */
	
	public static HashMap<String, ArrayList<String>> parseKeyValueList(String keySpliter,String valueSpliter, String strToParse) {
		return parseKeyValueList (keySpliter, valueSpliter, "\n", strToParse);
	}
	/**
	 * Parse expected results w.r.t. a list
	 * Example: JobProfile sheet runAutoEnroll case
	 * enrollment:uma_rob;uma_qa1;
	 * non-enrollment:bsam;"
	 * for auto-enroll into
	 * ":" is the keySpliter 
	 * ";" is the valueSpliter
	 * 
	 * @param strToParse
	 * @return: A Map of key=ArrayList(values)
	 * 1. key=enrollment values=ArrayList(1.uma_rob,2.uma_qa1)
	 * 2. key=non-enrollment values=ArrayList(1.bsam)
	 */
	private static HashMap<String, ArrayList<String>> parseKeyValueList(String keySpliter,String valueSpliter,String listSeperator, String strToParse) {
		HashMap<String, ArrayList<String>> key_ValuesList_Map = new HashMap<String, ArrayList<String>>();
		if (listSeperator== null)
			listSeperator="\n"; // new line
			
		if (!strToParse.equals("")) {
			String[] strs = strToParse.split(listSeperator);//default listSeperator="\n" is new line

			ArrayList<String> local_values = null;
			String criteria = "";
			for (String str_tmp : strs) { // str_tmp = enrollment:
											// uma_rob;uma_qa1;
				String[] strs_tmp = str_tmp.split(keySpliter);//keySpliter=":"
				criteria = strs_tmp[0].trim();// enrollment or non-enrollment
				strs_tmp[1]=strs_tmp[1].trim();
				if (key_ValuesList_Map.containsKey(criteria)) {
					local_values = key_ValuesList_Map.get(criteria);
				} else {
					local_values = new ArrayList<String>();
				}
				
				
				if (Validate.isBlank(valueSpliter)){ // can also be blank, then pass null or ""
					local_values.add(strs_tmp[1]);
				}else{
					String[] values_tmp = strs_tmp[1].split(valueSpliter);//valueSpliter=";"
					for (String value : values_tmp) {
						local_values.add(value.trim());
					}
				}
				key_ValuesList_Map.put(criteria, local_values);
			}
		}

		return key_ValuesList_Map;
	}
}
