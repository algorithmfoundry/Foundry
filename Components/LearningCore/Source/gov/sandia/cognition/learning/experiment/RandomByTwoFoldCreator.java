/*
 * File:                RandomByTwoFoldCreator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright January 20, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.data.DefaultPartitionedDataset;
import gov.sandia.cognition.learning.data.PartitionedDataset;
import gov.sandia.cognition.math.Permutation;
import gov.sandia.cognition.util.AbstractRandomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * A validation fold creator that takes a given collection of data and randomly
 * splits it in half a given number of times, returning two folds for each
 * split, using one half as training and the other half as testing. The number
 * of folds is thus twice the parameterized number of splits. The data is
 * reordered as a result of each split, so this should not be used for data
 * whose sequence order matters. The default setup is a 5x2 cross-fold
 * creation, which is a common validation technique.
 *
 * @param   <DataType>
 *      The type of data to create folds over.
 * @author  Justin Basilico
 * @since   3.0
 */
public class RandomByTwoFoldCreator<DataType>
    extends AbstractRandomized
    implements ValidationFoldCreator<DataType, DataType>
{
    /** The default number of splits is {@value}. */
    public static final int DEFAULT_NUM_SPLITS = 5;
    
    /** The number of splits. The number of folds is twice this number. */
    protected int numSplits;

    /**
     * Creates a new {@code RandomByTwoFoldCreator} with a default number of
     * splits.
     */
    public RandomByTwoFoldCreator()
    {
        this(DEFAULT_NUM_SPLITS);
    }

    /**
     * Creates a new {@code RandomByTwoFoldCreator} with a given number of
     * splits.
     *
     * @param   numSplits
     *      The number of splits to create. The number of folds created is
     *      twice this number. It must be positive.
     */
    public RandomByTwoFoldCreator(
        final int numSplits)
    {
        this(numSplits, new Random());
    }

    /**
     * Creates a new {@code RandomByTwoFoldCreator} with a given number of
     * splits.
     *
     * @param   numSplits
     *      The number of splits to create. The number of folds created is
     *      twice this number. It must be positive.
     * @param   random
     *      The random number generator to use.
     */
    public RandomByTwoFoldCreator(
        final int numSplits,
        final Random random)
    {
        super(random);

        this.setNumSplits(numSplits);
    }

    public List<PartitionedDataset<DataType>> createFolds(
        final Collection<? extends DataType> data)
    {
        final int size = CollectionUtil.size(data);
        if (size < 2)
        {
            // Need at least two elements.
            throw new IllegalArgumentException(
                "data must have at least 2 elements.");
        }

        // Figure out the actual number of splits and folds
        final int actualNumSplits = Math.min(size, this.getNumSplits());
        final int actualNumFolds = 2 * actualNumSplits;

        // We are going to have twice as many partitions as number of splits.
        final ArrayList<PartitionedDataset<DataType>> result =
            new ArrayList<PartitionedDataset<DataType>>(actualNumFolds);

        final int halfSize = Math.max(size / 2, 1);

        // Create the splits.
        for (int i = 0; i < actualNumSplits; i++)
        {
            // Create a random ordering.
            final ArrayList<DataType> reordering =
                Permutation.createReordering(data, this.getRandom());

            // Get the two halves.
            final List<DataType> firstHalf = reordering.subList(0, halfSize);
            final List<DataType> secondHalf = reordering.subList(halfSize, size);

            // Add the two datasets.
            result.add(DefaultPartitionedDataset.create(firstHalf, secondHalf));
            result.add(DefaultPartitionedDataset.create(secondHalf, firstHalf));
        }

        // Return the resulting partitions.
        return result;
    }

    /**
     * Gets the number of splits to perform. When a dataset is given, two times
     * this number of partitions is returned. Must be positive.
     *
     * @return
     *      The number of splits to perform. Must be positive.
     */
    public int getNumSplits()
    {
        return this.numSplits;
    }

    /**
     * Sets the number of splits to perform. When a dataset is given, two times
     * this number of partitions is returned. Must be positive.
     *
     * @param   numSplits
     *      The number of splits to perform. Must be positive.
     */
    public void setNumSplits(
        final int numSplits)
    {
        if (numSplits <= 0)
        {
            throw new IllegalArgumentException(
                "numSplits must be positive");
        }
        this.numSplits = numSplits;
    }

}
