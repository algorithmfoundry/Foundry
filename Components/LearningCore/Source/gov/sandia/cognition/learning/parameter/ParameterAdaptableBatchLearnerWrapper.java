/*
 * File:                ParameterAdaptableBatchLearnerWrapper.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 29, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.parameter;

import gov.sandia.cognition.learning.algorithm.AbstractBatchLearnerContainer;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.LinkedList;

/**
 * A wrapper for adding parameter adapters to a batch learner. It simply
 * passes the learner and the data to the parameter adapters attached to the
 * wrapper before calling the learner.
 * 
 * @param <DataType> 
 *      Type of data to use.
 * @param <ResultType> 
 *      Type of result to return.
 * @param <LearnerType> 
 *      Type of BatchLearner to use.
 * @author  Justin Basilico
 * @since   3.0
 */
public class ParameterAdaptableBatchLearnerWrapper<DataType, ResultType, LearnerType extends BatchLearner<? super DataType, ? extends ResultType>>
    extends AbstractBatchLearnerContainer<LearnerType>
    implements BatchLearner<DataType, ResultType>,
        ParameterAdaptable<LearnerType, DataType>
{
    /** The list of parameter adapters for the learner. It should be null if
     *  there are no adapters. */
    protected LinkedList<ParameterAdapter<? super LearnerType, ? super DataType>>
        parameterAdapters;
    
    /**
     * Creates a new {@code ParameterAdaptableBatchLearnerWrapper}.
     */
    public ParameterAdaptableBatchLearnerWrapper()
    {
        this(null);
    }
    
    /**
     * Creates a new {@code ParameterAdaptableBatchLearnerWrapper}.
     * 
     * @param   learner
     *      The learner whose parameters are to be adapted.
     */
    public ParameterAdaptableBatchLearnerWrapper(
        final LearnerType learner)
    {
        super(learner);
        
        this.setParameterAdapters(null);
    }

    @Override
    public ParameterAdaptableBatchLearnerWrapper<DataType, ResultType, LearnerType> clone()
    {
        @SuppressWarnings("unchecked")
        final ParameterAdaptableBatchLearnerWrapper<DataType, ResultType, LearnerType>
            result = (ParameterAdaptableBatchLearnerWrapper<DataType, ResultType, LearnerType>)
                super.clone();
        result.parameterAdapters = 
            ObjectUtil.cloneSmartElementsAsLinkedList(this.parameterAdapters);
        return result;
    }
    
    public ResultType learn(
        final DataType data)
    {
        if (this.parameterAdapters != null)
        {
            // Adapt the parameters of the learner.
            for (ParameterAdapter<? super LearnerType, ? super DataType> adapter 
                : this.parameterAdapters)
            {
                adapter.adapt(this.getLearner(), data);
            }
        }
        
        // Now use the learner.
        return this.getLearner().learn(data);
    }

    public void addParameterAdapter(
        final ParameterAdapter<? super LearnerType, ? super DataType> parameterAdapter)
    {
        if (this.parameterAdapters == null)
        {
            // Initialize the list since it does not exist yet.
            this.parameterAdapters = 
                new LinkedList<ParameterAdapter<? super LearnerType, ? super DataType>>();
        }
        
        // Add the adapter.
        this.parameterAdapters.add(parameterAdapter);
    }

    public void removeParameterAdapter(
        final ParameterAdapter<? super LearnerType, ? super DataType> parameterAdapter)
    {
        if (this.parameterAdapters != null)
        {
            // Remove the adapter.
            this.parameterAdapters.remove(parameterAdapter);
            
            if (this.parameterAdapters.isEmpty())
            {
                // There are no more adapters, so null out the list.
                this.parameterAdapters = null;
            }
        }
        // else - No adapters to remove from.
    }
    
    public LinkedList<ParameterAdapter<? super LearnerType, ? super DataType>> 
    getParameterAdapters()
    {
        return this.parameterAdapters;
    }
    
    /**
     * Sets the list of parameter adapters for the learning algorithm.
     * 
     * @param   parameterAdapters
     *      The list of parameter adapters for the learning algorithm.
     */
    public void setParameterAdapters(
        final LinkedList<ParameterAdapter<? super LearnerType, ? super DataType>> parameterAdapters)
    {
        this.parameterAdapters = parameterAdapters;
    }

    /**
     * A convenience method for creating the wrapper for a learner.
     *
     * @param <DataType>
     *      Type of data to use.
     * @param <ResultType>
     *      Type of result to return.
     * @param <LearnerType>
     *      Type of BatchLearner to use.
     * @param   learner
     *      The learner whose parameters are to be adapted.
     * @return
     *      A new ParameterAdaptableBatchLearnerWrapper.
     */
    public static <DataType, ResultType, LearnerType extends BatchLearner<? super DataType, ? extends ResultType>> ParameterAdaptableBatchLearnerWrapper<DataType, ResultType, LearnerType>
    create(
        final LearnerType learner)
    {
        return new ParameterAdaptableBatchLearnerWrapper<DataType, ResultType, LearnerType>(
            learner);
    }
    
}
