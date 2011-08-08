/*
 * File:                RandomFoldCreator.java
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

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.learning.data.PartitionedDataset;
import gov.sandia.cognition.learning.data.RandomizedDataPartitioner;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The {@code RandomFoldCreator} class makes use of a randomized data 
 * partitioner to create a set number of folds for a set of data by passing
 * the data to the data partitioner multiple times.
 *
 * @param  <DataType> The type of data to create the folds for.
 * @author Justin Basilico
 * @since  2.0
 */
public class RandomFoldCreator<DataType>
    extends Object
    implements ValidationFoldCreator<DataType, DataType>
{
    /** The default number of folds is 10. */
    public static final int DEFAULT_NUM_FOLDS = 10;
    
    /** The number of folds to create. */
    protected int numFolds;
    
    /** The partitioner used for each fold. */
    protected RandomizedDataPartitioner<DataType> partitioner;
    
    /**
     * Creates a new instance of RandomFoldCreator.
     */
    public RandomFoldCreator()
    {
        this(DEFAULT_NUM_FOLDS, null);
    }
    
    /**
     * Creates a new instance of RandomFoldCreator.
     *
     * @param  numFolds The number of folds to create.
     * @param  partitioner The partitioner to use to create the folds.
     */
    public RandomFoldCreator(
        final int numFolds,
        final RandomizedDataPartitioner<DataType> partitioner)
    {
        super();
        
        this.setNumFolds(numFolds);
        this.setPartitioner(partitioner);
    }

    /**
     * Creates the folds from the given data by passing the data into the
     * set data partitioner multiple times.
     *
     * @param  data The data to partition into multiple folds.
     * @return The folds of partitioned data.
     */
    public ArrayList<PartitionedDataset<DataType>> createFolds(
        final Collection<? extends DataType> data)
    {
        // Create the result folds.
        ArrayList<PartitionedDataset<DataType>> folds = 
            new ArrayList<PartitionedDataset<DataType>>(this.getNumFolds());
        
        // Partition the data randomly to get each fold.
        for (int i = 0; i < this.getNumFolds(); i++)
        {
            folds.add(partitioner.createPartition(data));
        }
        
        // Return the created folds.
        return folds;
    }

    /**
     * Gets the number of folds to create.
     *
     * @return The number of folds to create.
     */
    public int getNumFolds()
    {
        return this.numFolds;
    }

    /**
     * Sets the number of folds to create. Must be greater than zero.
     *
     * @param  numFolds The number of folds to create.
     */
    public void setNumFolds(
        final int numFolds)
    {
        if ( numFolds <= 0 )
        {
            throw new IllegalArgumentException(
                "numFolds must be greater than zero.");
        }
        
        this.numFolds = numFolds;
    }

    /**
     * Gets the randomized partitioner to use.
     *
     * @return The randomized partitioner.
     */
    public RandomizedDataPartitioner<DataType> getPartitioner()
    {
        return this.partitioner;
    }
    
    /**
     * Sets the randomized partitioner to use.
     *
     * @param  partitioner The randomized partitioner.
     */
    public void setPartitioner(
        final RandomizedDataPartitioner<DataType> partitioner)
    {
        this.partitioner = partitioner;
    }
}
