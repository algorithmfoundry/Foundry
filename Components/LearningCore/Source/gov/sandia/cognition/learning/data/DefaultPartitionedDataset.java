/*
 * File:                DefaultPartitionedDataset.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 26, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.data;

import java.util.Collection;

/**
 * The PartitionedDataset class provides a simple container for the training
 * and testing datasets to be held together.
 *
 * @param  <DataType>
 *      The type of the data in the dataset.
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  3.0
 */
public class DefaultPartitionedDataset<DataType>
    extends Object
    implements PartitionedDataset<DataType>
{

    /** The training dataset. */
    private Collection<DataType> trainingSet = null;

    /** The testing dataset. */
    private Collection<DataType> testingSet = null;

    /**
     * Creates a new instance of PartitionedDataset.
     *
     * @param trainingSet The training set.
     * @param testingSet The testing set.
     */
    public DefaultPartitionedDataset(
        final Collection<DataType> trainingSet,
        final Collection<DataType> testingSet)
    {
        super();

        this.setTrainingSet(trainingSet);
        this.setTestingSet(testingSet);
    }

    public Collection<DataType> getTrainingSet()
    {
        return this.trainingSet;
    }

    public Collection<DataType> getTestingSet()
    {
        return this.testingSet;
    }

    /**
     * Sets the training set.
     *
     * @param trainingSet The new training set.
     */
    protected void setTrainingSet(
        final Collection<DataType> trainingSet)
    {
        this.trainingSet = trainingSet;
    }

    /**
     * Sets the testing set.
     *
     * @param testingSet The new testing set.
     */
    protected void setTestingSet(
        final Collection<DataType> testingSet)
    {
        this.testingSet = testingSet;
    }

    /**
     * Convenience method to create a new {@code DefaultPartitionedDataset}
     * from the two given collections.
     *
     * @param   <DataType>
     *      The type of the data in the dataset.
     * @param   trainingSet
     *      The training set.
     * @param   testingSet
     *      The testing set.
     * @return
     *      A new default partitioned dataset with the given training and
     *      testing sets.
     */
    public static <DataType> DefaultPartitionedDataset<DataType> create(
        final Collection<DataType> trainingSet,
        final Collection<DataType> testingSet)
    {
        return new DefaultPartitionedDataset<DataType>(trainingSet, testingSet);
    }
}
