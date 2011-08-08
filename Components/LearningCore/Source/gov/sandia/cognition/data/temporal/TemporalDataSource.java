/*
 * File:                TemporalDataSource.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 28, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.temporal;

import gov.sandia.cognition.util.Temporal;

/**
 * The {@code TemporalDataSource} interface defines the functionality of a
 * class that represents a data source. It must be in ascending order.
 * 
 * @param   <DataType> The data that is being read in.
 * @author  Justin Basilico
 * @since   2.1
 */
public interface TemporalDataSource<DataType extends Temporal>
{
    /**
     * Gets the data channel for the data source.
     * 
     * @return  The data channel for the data source.
     */
    TemporalDataReadChannel<DataType> readChannel();
    
    
}
