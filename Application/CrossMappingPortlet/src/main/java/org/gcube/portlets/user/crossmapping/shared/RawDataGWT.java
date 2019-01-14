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

public class RawDataGWT implements Serializable {

	private final static long serialVersionUID = 1L;
	
	protected List<RawDataRegisterGWT> registers;

	public List<RawDataRegisterGWT> getRegisters() {
		return registers;
	}

	public void setRegisters(List<RawDataRegisterGWT> registers) {
		this.registers = registers;
	}
	
	public RawDataGWT(){
		
	}

	public RawDataGWT(List<RawDataRegisterGWT> registers) {
		super();
		this.registers = registers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((registers == null) ? 0 : registers.hashCode());
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
		RawDataGWT other = (RawDataGWT) obj;
		if (registers == null) {
			if (other.registers != null)
				return false;
		} else if (!registers.equals(other.registers))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RawDataGWT [registers=" + registers + "]";
	}
	
	
	
	
}
