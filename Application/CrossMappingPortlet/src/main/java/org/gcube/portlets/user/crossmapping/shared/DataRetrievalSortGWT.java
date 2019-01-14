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


public class DataRetrievalSortGWT implements Serializable {

	private final static long serialVersionUID = 1L;
	
    protected String column;
    
    protected DataRetrievalSortingDirectionGWT direction;
	
    public String getColumn() {
		return column;
	}
    
	public void setColumn(String column) {
		this.column = column;
	}
	
	public DataRetrievalSortingDirectionGWT getDirection() {
		return direction;
	}
	
	public void setDirection(DataRetrievalSortingDirectionGWT direction) {
		this.direction = direction;
	}
	
	public DataRetrievalSortGWT(){
		
	}
	
	public DataRetrievalSortGWT(String column,
			DataRetrievalSortingDirectionGWT direction) {
		super();
		this.column = column;
		this.direction = direction;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result
				+ ((direction == null) ? 0 : direction.hashCode());
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
		DataRetrievalSortGWT other = (DataRetrievalSortGWT) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (direction != other.direction)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "DataRetrievalSortGWT [column=" + column + ", direction="
				+ direction + "]";
	}	

    
    
}
