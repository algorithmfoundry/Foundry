/*
 * File:                CrossFoldCreator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.collection.RangeExcludedArrayList;
import gov.sandia.cognition.learning.data.DefaultPartitionedDataset;
import gov.sandia.cognition.learning.data.PartitionedDataset;
import gov.sandia.cognition.math.Permutation;
import gov.sandia.cognition.util.AbstractRandomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * The {@code CrossFoldCreator} implements a validation fold creator that 
 * creates folds for a typical k-fold cross-validation experiment. That is, it
 * splits the data into k folds where each item appears in the testing set in
 * exactly 1 fold and in the training set in the remaining k - 1 folds. At the
 * limit where k is equal to the size of the data, this becomes leave-one-out
 * cross-validation, but is typically used in the case where leave-one-out
 * cross-validation is too costly to run and k is set to a much smaller value.
 *
 * @param  <DataType> The type of data to create the folds for.
 * @author Justin Basilico
 * @since  2.0
 */
public class CrossFoldCreator<DataType>
    extends AbstractRandomized
    implements ValidationFoldCreator<DataType, DataType>
{
    /** The default number of folds is 10. */
    public static final int DEFAULT_NUM_FOLDS = 10;
    
    /** The number of folds to create. */
    protected int numFolds;
    
    /**
     * Creates a new instance of CrossFoldCreator with a default number of folds
     * (10) and a default Random number generator.
     */
    public CrossFoldCreator()
    {
        this(DEFAULT_NUM_FOLDS, new Random());
    }
    
    /**
     * Creates a new CrossFoldCreator.
     *
     * @param  numFolds The number of folds to create.
     * @param  random The random number generator to use.
     */
    public CrossFoldCreator(
        final int numFolds,
        final Random random)
    {
        super(random);
        
        this.setNumFolds(numFolds);
    }
    
    /**
     * Creates the requested number of cross-validation folds from the given 
     * data. The number of folds returned will be the minimum of the number
     * of requested folds and the size of the data because it cannot create 
     * more folds than elements of the data.
     *
     * @param  data The data to create the folds for.
     * @return The created cross-validation folds.
     */
    public List<PartitionedDataset<DataType>> createFolds(
        final Collection<? extends DataType> data)
    {
        return CrossFoldCreator.createFolds(data, this.getNumFolds(), 
            this.getRandom());
    }

    /**
     * Creates the requested number of cross-validation folds from the given 
     * data. The number of folds returned will be the minimum of the number
     * of requested folds and the size of the data because it cannot create 
     * more folds than elements of the data.
     *
     * @param   <DataType> The type of data to create folds over.
     * @param   data The data to create the folds for.
     * @param   numFolds The number of folds to create.
     * @param   random The random number generator to use.
     * @return  The created cross-validation folds.
     */
    public static <DataType> List<PartitionedDataset<DataType>> createFolds(
        final Collection<? extends DataType> data,
        final int numFolds,
        final Random random)
    {
        final int total = data.size();
        if (total  < 2)
        {
            throw new IllegalArgumentException(
                "data must have at least 2 items");
        }
        CrossFoldCreator.checkNumFolds(numFolds);
        
        // Randomize the data before splitting it.
        final ArrayList<DataType> reordering = 
            Permutation.createReordering(data, random);
        
        // If there is less data than folds, we need a smaller number of
        // actual folds. This means that the algorithm defaults to a
        // leave-one-out type of validation.
        final int numActualFolds = Math.min(total, numFolds);
        final ArrayList<PartitionedDataset<DataType>> datasets = 
            new ArrayList<PartitionedDataset<DataType>>(numActualFolds);
        
        // We will create partitions with the same backing list of data.
        int fromIndex = 0;
        int toIndex = 0;
        for (int i = 0; i < numActualFolds; i++)
        {
            // Figure out the chunk that will be witheld as training data.
            fromIndex = toIndex;
            final int foldSize = (total - fromIndex) / (numActualFolds - i);
            toIndex = fromIndex + foldSize;
            
            // Create the training set by excluding the testing set indices
            // from the larger set of data.
            final List<DataType> training = 
                new RangeExcludedArrayList<DataType>(reordering, 
                    fromIndex, toIndex - 1);
            
            // Create the testing set by taking the sub list of the set of
            // all the data.
            final List<DataType> testing = 
                reordering.subList(fromIndex, toIndex);
            
            datasets.add(new DefaultPartitionedDataset<DataType>(training, testing));
        }
        
        return datasets;
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
     * Sets the number of folds to create. The number of folds must be greater
     * than one.
     *
     * @param  numFolds The number of folds to create.
     */
    public void setNumFolds(
        final int numFolds)
    {
        CrossFoldCreator.checkNumFolds(numFolds);
        this.numFolds = numFolds;
    }
    
    /**
     * Checks the given number of folds to make sure that it is greater than
     * 1.
     *
     * @param  numFolds The number of folds.
     */
    protected static void checkNumFolds(
        final int numFolds)
    {
        if (numFolds <= 1)
        {
            throw new IllegalArgumentException(
                "numFolds must be greater than 1.");
        }
    }
}
