/*
 * #%L
 * XMap GUI CrossMapping Portlet
 * %%
 * Copyright (C) 2012 - 2013 Cardiff University
 * %%
 * Use of this software is governed by the attached licence file. If no licence 
 * file is present the software must not be used.
 * 
 * The use of this software, including reverse engineering, for any other purpose 
 * is prohibited without the express written permission of the software owners, 
 * Cardiff University and Italy National Research Council.
 * #L%
 */
package org.gcube.portlets.user.crossmapping.shared;

import java.io.Serializable;
import java.util.List;

public class RawDataRegisterGWT implements Serializable {

	private final static long serialVersionUID = 1L;    
	
	private int count = 0;
	private int numberOfFields;
	
	protected List<RawDataFieldGWT> rawDataFields;

	public List<RawDataFieldGWT> getRawDataFields() {
		return rawDataFields;
	}

	public void setRawDataFields(List<RawDataFieldGWT> rawDataFields) {
		this.rawDataFields = rawDataFields;
		this.numberOfFields = rawDataFields.size();
	}
	
	public RawDataRegisterGWT(){
		
	}

	public RawDataRegisterGWT(List<RawDataFieldGWT> rawDataFields) {
		super();
		this.setRawDataFields(rawDataFields);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((rawDataFields == null) ? 0 : rawDataFields.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RawDataRegisterGWT other = (RawDataRegisterGWT) obj;
		if (rawDataFields == null) {
			if (other.rawDataFields != null)
				return false;
		} else if (!rawDataFields.equals(other.rawDataFields))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RawDataRegisterGWT [rawDataFields=" + rawDataFields + "]";
	}
	
	
	public String getFieldValue(){
		String value = this.getRawDataFields().get(count).getValue();
		this.count = this.count + 1;
		if (this.count == this.numberOfFields){
			this.count = 0;
		}
		return value;
	}
	
	
	public String getId(){
		return Long.toString(serialVersionUID);
	}
	
}
