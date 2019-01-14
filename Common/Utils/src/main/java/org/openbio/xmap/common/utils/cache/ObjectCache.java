/*
 * #%L
 * Xmap Utils
 * %%
 * Copyright (C) 2012 - 2013 Cardiff University
 * %%
 * Use of this software is governed by the attached licence file. If no licence 
 * file is present the software must not be used.
 * 
 * The use of this software, including reverse engineering, for any other purpose 
 * is prohibited without the express written permission of the software owner, 
 * Cardiff University.
 * #L%
 */
package org.openbio.xmap.common.utils.cache;

import java.util.HashMap;
import java.util.Random;

/**
 * Generic class to provide methods for cached objects in a hash map that will be keep in memory
 * <p>
 * OpenBio XMap
 * <p> 
 * Date: 15/05/12
 * @author scmjpg
 * @param <T> Type of object to chache in the hash map
 */
public class ObjectCache<T> extends HashMap<Integer, T> {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * hash map in which the object to be cached will be saved
	 */
    private final HashMap<Integer, T> map = new HashMap<Integer, T>();

    
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
    /**
     * Method responsible to add a new object to the cache
     * @param value object to be cached
     * @return int with the key in the hash map in which the object has been cached
     */
    public int add(T value) {
        Random random = new Random(value.hashCode());
        int key = random.nextInt();
        synchronized (map) {
            while (map.containsKey(key)) {
                key = random.nextInt();
            }
            put(key, value);
        }
        return key;
    }
    
    /**
     * Method responsible to obtain a cached object from the hash map usign the key received as parameter
     * @param key int with the key in the hash map in which the object was cached
     * @return object obtained from the cache
     */
    public T get(int key) {
        synchronized (map) {
            return map.get(key);
        }
    }

    /**
     * Method responsible to delete an object from the cache
     * @param key int with the key in the hash map in which the object was cached
     * @return object removed from the cache
     */
    public T remove(int key) {
        synchronized (map) {
            return map.remove(key);
        }
    }
}
