/*
 * File:                AbstractDataHistogram.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

/**
 * Partial implementation of DataHistogram.
 *
 * @param  <DataType>
 *      Type of data to use.
 * @author  Kevin R. Dixon
 * @since   3.0
 */
public abstract class AbstractDataHistogram<DataType>
    extends AbstractDistribution<DataType>
    implements DataHistogram<DataType>
{

    /**
     * Creates a new {@code AbstractDataHistogram}.
     */
    public AbstractDataHistogram()
    {
        super();
    }

    public void add(
        DataType value)
    {
        this.add(value, 1);
    }

    public void remove(
        DataType value )
    {
        this.remove(value, 1);
    }
    
}
