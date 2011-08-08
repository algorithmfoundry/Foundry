/*
 * File:                AbstractBatchAndIncrementalLearner.java
 * Authors:             Kevin R. Dixon and Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract class that has both batch learning ability as well as online
 * learning ability by taking a Collection of input data.  For the batch
 * learning case, we iterate over the Collection of input data and pass this
 * through the online learning function to arrive at the batch result.
 *
 * @param   <DataType>
 *      Type of the input data.  Typically an iterable or a collection.
 * @param   <ResultType>
 *      Result type from both the online- and batch-learning interfaces.
 * @author  Kevin R. Dixon
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractBatchAndIncrementalLearner<DataType, ResultType>
    extends AbstractCloneableSerializable
    implements BatchLearner<Iterable<? extends DataType>, ResultType>,
        IncrementalLearner<DataType, ResultType>
{

    /** 
     * Creates a new instance of {@code AbstractBatchAndIncrementalLearner}.
     */
    public AbstractBatchAndIncrementalLearner()
    {
        super();
    }

    @Override
    public AbstractBatchAndIncrementalLearner<DataType,ResultType> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractBatchAndIncrementalLearner<DataType,ResultType> clone =
            (AbstractBatchAndIncrementalLearner<DataType,ResultType>) super.clone();
        return clone;
    }

    public ResultType learn(
        final Iterable<? extends DataType> data )
    {
        // Create the initial learned object.
        final ResultType result = this.createInitialLearnedObject();

        // Update the result.
        this.update(result, data);
        
        // Return the result.
        return result;
    }

    public void update(
        ResultType target,
        Iterable<? extends DataType> data)
    {

        for( DataType value : data )
        {
            this.update(target, value);
        }
        
    }
    
}
