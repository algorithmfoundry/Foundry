/*
 * File:                MapBasedSortedDataHistogram.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 17, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.statistics.DataHistogram;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import java.util.Collection;
import java.util.TreeMap;

/**
 * A map-based DataHistogram, where the domain is sorted by its natural order.
 * @param <DataType>
 * Type of data in the domain
 * @author Kevin R. Dixon
 * @since 3.1
 */
public class MapBasedSortedDataHistogram<DataType extends Comparable<? super DataType>>
    extends MapBasedDataHistogram<DataType>
{

    /**
     * Creates a new instance of DataCountMapHistogram.
     */
    public MapBasedSortedDataHistogram()
    {
        super( new TreeMap<DataType, Entry>() );
    }

    /**
     * Creates a new instance of DataCountMapHistogram.
     * @param data Data to add
     */
    public MapBasedSortedDataHistogram(
        Collection<DataType> data )
    {
        this();
        if( data != null )
        {
            for( DataType x : data )
            {
                this.add( x );
            }
        }
    }

    /**
     * Copy constructor
     * @param other
     * MapBasedDataHistogram to copy
     */
    public MapBasedSortedDataHistogram(
        DataHistogram<DataType> other )
    {
        this();
        for( DataType input : other.getDomain() )
        {
            this.add( input, other.getCount( input ) );
        }
    }

    @Override
    public MapBasedSortedDataHistogram<DataType> clone()
    {
        MapBasedSortedDataHistogram<DataType> clone =
            (MapBasedSortedDataHistogram<DataType>) super.clone();
        clone.setCountMap( new TreeMap<DataType, Entry>( clone.getCountMap() ) );
        return clone;
    }

    @Override
    public MapBasedSortedDataHistogram.PMF<DataType> getProbabilityFunction()
    {
        return new MapBasedSortedDataHistogram.PMF<DataType>(this);
    }

    /**
     * PMF of MapBasedSortedDataHistogram
     * @param <DataType>
     */
    public static class PMF<DataType extends Comparable<? super DataType>>
        extends MapBasedSortedDataHistogram<DataType>
        implements DataHistogram.PMF<DataType>
    {
        
        /**
         * Creates a new instance of DataCountMapHistogram.
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of DataCountMapHistogram.
         * @param data Data to add
         */
        public PMF(
            Collection<DataType> data )
        {
            super( data );
        }

        /**
         * Copy constructor
         * @param other
         * MapBasedDataHistogram to copy
         */
        public PMF(
            DataHistogram<DataType> other )
        {
            super( other );
        }

        @Override
        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

        @Override
        public double logEvaluate(
            DataType input)
        {
            return Math.log( this.evaluate(input) );
        }

        @Override
        public Double evaluate(
            DataType input)
        {
            return this.getFraction(input);
        }

        @Override
        public MapBasedSortedDataHistogram.PMF<DataType> getProbabilityFunction()
        {
            return this;
        }

    }

}
