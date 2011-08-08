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

    public double getFraction(
        final DataType value)
    {
        final int tc = this.getTotalCount();
        if (tc <= 0)
        {
            // This prevents a divide-by-zero.
            return 0.0;
        }
        else
        {
            return (double) this.getCount(value) / tc;
        }
    }

    public void addAll(
        final Iterable<? extends DataType> values)
    {
        for (DataType value : values)
        {
            this.add(value);
        }
    }
    
    public <OtherDataType extends DataType> void addAll(
        final DataHistogram<OtherDataType> other)
    {
        for (OtherDataType value : other.getDomain())
        {
            this.add(value, other.getCount(value));
        }
    }
    
    /**
     * Partial implementation of a PMF for AbstractDataHistogram
     * @param <DataType>
     * Type of data in the domain
     */
//    public static abstract class PMF<DataType>
//        extends AbstractDataHistogram<DataType>
//        implements DataHistogram.PMF<DataType>
//    {
//
//        /**
//         * Default constructor
//         */
//        public PMF()
//        {
//            super();
//        }
//
//        @Override
//        public AbstractDataHistogram.PMF<DataType> getProbabilityFunction()
//        {
//            return this;
//        }
//
//        @Override
//        public Double evaluate(
//            DataType input)
//        {
//            return this.getFraction(input);
//        }
//
//        @Override
//        public double logEvaluate(
//            DataType input)
//        {
//            return Math.log( this.evaluate(input) );
//        }
//
//        @Override
//        public double getEntropy()
//        {
//            return ProbabilityMassFunctionUtil.getEntropy(this);
//        }
//
//        @Override
//        public ArrayList<? extends DataType> sample(
//            Random random,
//            int numSamples)
//        {
//            return ProbabilityMassFunctionUtil.sample(this, random, numSamples);
//        }
//
//    }

}
