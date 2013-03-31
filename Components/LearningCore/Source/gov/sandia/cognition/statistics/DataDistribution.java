/*
 * File:                DataDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 18, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.matrix.InfiniteVector;
import gov.sandia.cognition.collection.ScalarMap;

/**
 * A distribution of data from which we can sample and perform Ring operations.
 * @param <DataType>
 *      The type of data stored at the indices, the hash keys.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   3.3.1
 */
public interface DataDistribution<DataType>
    extends DiscreteDistribution<DataType>,
        EstimableDistribution<DataType, DataDistribution<DataType>>,
        ScalarMap<DataType>
{

    @Override
    public DataDistribution<DataType> clone();

    /**
     * Converts this data distribution to an infinite vector.
     *
     * @return
     *      A new {@code InfiniteVector} with values from this data
     *      distribution.
     */
    public InfiniteVector<DataType> toInfiniteVector();

    /**
     * Replaces the entries in this data distribution with the entries in the
     * given infinite vector.
     *
     * @param   vector
     *      The infinite vector to use to populate this data distribution.
     */
    public void fromInfiniteVector(
        final InfiniteVector<? extends DataType> vector);

    /**
     * Computes the information-theoretic entropy of the vector in bits.
     *
     * @return
     *      Entropy in bits of the distribution.
     */
    public double getEntropy();

    /**
     * Gets the fraction of the counts represented by the given key.
     *
     * @param   key
     *      The key.
     * @return
     *      The fraction of the total count represented by the key, if it
     *      exists. Otherwise, 0.0.
     */
    public double getFraction(
        final DataType key);

    /**
     * Gets the natural logarithm of the fraction of the counts represented
     * by the given key.
     *
     * @param key
     * Key to consider
     * @return
     * Natural logarithm of the fraction of the counts represented by the key
     */
    public double getLogFraction(
        final DataType key);

    /**
     * Gets the total (sum) of the values in the distribution.
     *
     * @return
     *      The sum of the values in the distribution.
     */
    public double getTotal();

    @Override
    public DistributionEstimator<DataType, ? extends DataDistribution<DataType>> getEstimator();

    @Override
    public DataDistribution.PMF<DataType> getProbabilityFunction();

    /**
     * Interface for the probability mass function (PMF) of a data distribution.
     *
     * @param <KeyType>
     *      Type of data stored at the indices, the hash keys.
     */
    public static interface PMF<KeyType>
        extends DataDistribution<KeyType>,
        ProbabilityMassFunction<KeyType>
    {
    }

}
