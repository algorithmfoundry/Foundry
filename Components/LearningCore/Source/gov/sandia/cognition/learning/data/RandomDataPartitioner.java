/*
 * File:                RandomDataPartitioner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright August 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.math.Permutation;
import gov.sandia.cognition.util.AbstractRandomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * The {@code RandomDataPartitioner} class implements a randomized data
 * partitioner that takes a collection of data and randomly splits it into
 * training and testing sets based on a fixed percentage of training data.
 *
 * @param  <DataType> The type of data to partition.
 * @author Justin Basilico
 * @since  2.0
 */
public class RandomDataPartitioner<DataType>
    extends AbstractRandomized
    implements RandomizedDataPartitioner<DataType>
{
    /** The default percentage of training data is 50%. */
    public static final double DEFAULT_TRAINING_PERCENT = 0.5;
    
    /** The percentage of training data. */
    protected double trainingPercent;
    
    /**
     * Creates a new instance of RandomDataPartitioner.
     */
    public RandomDataPartitioner()
    {
        super(new Random());
        
        this.setTrainingPercent(DEFAULT_TRAINING_PERCENT);
    }
    
    /**
     * Creates a new instance of RandomDataPartitioner.
     *
     * @param  trainingPercent The percentage of training data.
     * @param  random The Random object to use.
     */
    public RandomDataPartitioner(
        final double trainingPercent,
        final Random random)
    {
        super(random);
        
        this.setTrainingPercent(trainingPercent);
    }

    /**
     * Randomly partitions the given data into a training and testing set.
     *
     * @param  data The data to partition.
     * @return The data partitioned according to the training percentage.
     */
    public PartitionedDataset<DataType> createPartition(
        final Collection<? extends DataType> data)
    {
        return RandomDataPartitioner.createPartition(data, 
            this.getTrainingPercent(), this.getRandom());
    }
    
    /**
     * Randomly partitions the given data into a training and testing set.
     *
     * @param   <DataType> The type of data to partition.
     * @param   data The data to partition.
     * @param   trainingPercent the percentage of data to put in the training 
     *      partition. Must be greater than 0.0 and less than 1.0.
     * @param   random The random number generator to use.
     * @return  The data partitioned according to the training percentage.
     */
    public static <DataType> PartitionedDataset<DataType> createPartition(
        final Collection<? extends DataType> data,
        final double trainingPercent,
        final Random random)
    {
        final int numTotal = data.size();
        if ( numTotal < 2 )
        {
            throw new IllegalArgumentException(
                "data must have at least 2 items");
        }
        
        // Make sure the training percent is within range.
        RandomDataPartitioner.checkTrainingPercent(trainingPercent);
        
        // Compute the number of total data and training data.
        final int numTrain = Math.max(1, (int) (trainingPercent * numTotal));
        
        // Create the reordering of the data.
        final ArrayList<DataType> reordering = 
            Permutation.createReordering(data, random);
        
        // Get the sub lists for the two sets.
        final List<DataType> trainingSet = reordering.subList(0, numTrain);
        final List<DataType> testingSet = 
            reordering.subList(numTrain, numTotal);
        
        // Return the partitioned dataset.
        return new DefaultPartitionedDataset<DataType>(trainingSet, testingSet);
    }

    /**
     * Gets the percentage of data to put in the training partition.
     *
     * @return The percentage of data to put in the training partition.
     */
    public double getTrainingPercent()
    {
        return trainingPercent;
    }
    
    /**
     * Sets the percentage of data to put in the training partition. Must be
     * greater than 0.0 and less than 1.0.
     *
     * @param  trainingPercent The percentage of data to put in the training 
     *         partition.
     */
    public void setTrainingPercent(
        final double trainingPercent)
    {
        RandomDataPartitioner.checkTrainingPercent(trainingPercent);
        this.trainingPercent = trainingPercent;
    }
    
    /**
     * Checks to make sure the training percent greater than 0.0 and less than
     * 1.0.
     *
     * @param  trainingPercent The percentage of data to put in the training 
     *         partition.
     */
    protected static final void checkTrainingPercent(
        final double trainingPercent)
    {
        if ( trainingPercent <= 0.0 || trainingPercent >= 1.0 )
        {
            throw new IllegalArgumentException(
                "trainingPercent must be greater than 0.0 and less than 1.0");
        }
    }
}
