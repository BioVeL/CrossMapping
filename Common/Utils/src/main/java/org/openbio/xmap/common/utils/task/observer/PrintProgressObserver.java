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
package org.openbio.xmap.common.utils.task.observer;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import org.openbio.xmap.common.utils.task.Progress;
import org.openbio.xmap.common.utils.task.ProgressReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that implements Observer and it will be in charge of print the changes in the progress of the task  
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class PrintProgressObserver implements Observer {
	
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * slf4j logger for this class
	 */
	private static Logger logger = LoggerFactory.getLogger(PrintProgressObserver.class);
	
	/**
	 * start time of the task
	 */
	private final long startTime;
	
	/**
	 * time formatter
	 */
    private final DecimalFormat timeFormatter = new DecimalFormat("0.00s: ");
    
    /**
     * percentage formatter
     */
    private final DecimalFormat percentFormatter = new DecimalFormat("##0.0'%' ");
  
    /**
     * object ProgressReader being observed
     */
    private final ProgressReader progressReader;


	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
    
    /**
     * Constructor of PrintProgressObserver that receive the object ProgressReader to observe
     * @param progressReader object ProgressReader to be observed
     */
    public PrintProgressObserver(ProgressReader progressReader) {
        this.progressReader = progressReader;
        startTime = System.currentTimeMillis();
        print(startTime);
    }

	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
    /**
     * Method responsible print information of the progress when its state has been updated
     * Note: This method is called whenever the observed object is changed.
     * @param observable the observable object.
     * @param arg an argument passed to the notifyObservers method.
     */
    @Override
    public void update(Observable observable, Object arg) {
        assert observable == progressReader;
        print(System.currentTimeMillis());
    }
    
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	
    
    /**
     * Method responsible to print the message with the current progress of the task 
     * @param now current time
     */
    private void print(long now) {
        Progress progress = progressReader.getProgress();
        String timestamp = timeFormatter.format((now - startTime) / 1000.0d);
        String phase = progress.getPhase();
        String message = timestamp + (phase == null ? "" : phase + ": ") +
                percentFormatter.format(progress.getPercentProgress()) + progress;
       
        logger.debug(message);
    }    
}
