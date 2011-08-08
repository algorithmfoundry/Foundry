/*
 * File:                LeaveOneOutCreator.java
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
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.collection.RangeExcludedArrayList;
import gov.sandia.cognition.learning.data.DefaultPartitionedDataset;
import gov.sandia.cognition.learning.data.PartitionedDataset;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The {@code LeaveOneOutFoldCreator} class implements the leave-one-out method
 * for creating training-testing folds for a cross-validation experiment. The
 * leave-one-out method takes a data set containing n items and creates n folds
 * where each fold contains n - 1 training items and 1 testing item.  
 *
 * @param  <DataType> The type of data to create the folds for.
 * @author Justin Basilico
 * @since  2.0
 */
public class LeaveOneOutFoldCreator<DataType>
    extends Object
    implements ValidationFoldCreator<DataType, DataType>, Serializable
{
    /**
     * Creates a new instance of LeaveOneOutCreator
     */
    public LeaveOneOutFoldCreator()
    {
        super();
    }

    /**
     * Creates a list of folds that is the same size as the given data. Each
     * fold contains n - 1 training examples and 1 testing example. Thus, there
     * is one fold for each example, which is the one where it appears as a
     * testing example.
     *
     * @param  data The data to create the folds for.
     * @return The created validation folds.
     */
    @SuppressWarnings("unchecked")
    public List<PartitionedDataset<DataType>> createFolds(
        final Collection<? extends DataType> data)
    {
        final int count = data.size();
        if ( count < 2 )
        {
            throw new IllegalArgumentException(
                "data must have at least 2 items");
        }

        // Create the data array that will be underlying each fold.
        final ArrayList<DataType> dataArray;
        if( data instanceof ArrayList )
        {
            dataArray = (ArrayList<DataType>) data;
        }
        else
        {
            dataArray = new ArrayList<DataType>( data );
        }
        final ArrayList<PartitionedDataset<DataType>> datasets = 
            new ArrayList<PartitionedDataset<DataType>>(count);
        
        // For each item in the list we create a training set with all the items 
        // except that one plus a singleton testing set with that item.
        for (int i = 0; i < count; i++)
        {
            // We make a light-weight list that excludes the single item for
            // the training set so that we do not to copy the whole dataset
            // into a new list.
            final List<DataType> training = 
                new RangeExcludedArrayList<DataType>(dataArray, i, i);
            
            // The testing set is a singleton containing the one item we
            // excluded from the training set.
            final List<DataType> testing = 
                Collections.singletonList(dataArray.get(i));
            
            datasets.add(new DefaultPartitionedDataset<DataType>(training, testing));
        }
        
        return datasets;
    }
    
}
