/*
 * File:                ConfusionMatrixPerformanceEvaluator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 11, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.factory.DefaultFactory;
import gov.sandia.cognition.factory.Factory;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.learning.performance.AbstractSupervisedPerformanceEvaluator;
import java.util.Collection;

/**
 * A performance evaluator that builds a confusion matrix.
 *
 * @param   <InputType>
 *      The input type of the object to evaluate the performance of.
 * @param   <CategoryType>
 *      The category type (output type) to build the confusion matrix over,
 *      which is the also the output type of the object to evaluate the
 *      performance of.
 * @author  Justin Basilico
 * @since   3.1
 */
public class ConfusionMatrixPerformanceEvaluator<InputType, CategoryType>
    extends AbstractSupervisedPerformanceEvaluator<InputType, CategoryType, CategoryType, ConfusionMatrix<CategoryType>>
{

    /** The factory used to create the confusion matrix of the evaluator. */
    protected Factory<? extends ConfusionMatrix<CategoryType>> factory;

    /**
     * Creates a new {@code ConfusionMatrixPerformanceEvaluator} with a
     * default factory behind it.
     */
    @SuppressWarnings("unchecked")
    public ConfusionMatrixPerformanceEvaluator()
    {
        this(new DefaultFactory(DefaultConfusionMatrix.class));
    }

    /**
     * Creates a new {@code ConfusionMatrixPerformanceEvaluator} using the given
     * factory.
     *
     * @param   factory
     *      The factory used to create the matrix from the evaluator.
     */
    public ConfusionMatrixPerformanceEvaluator(
        final Factory<? extends ConfusionMatrix<CategoryType>> factory)
    {
        super();
        
        this.setFactory(factory);
    }

    @Override
    public ConfusionMatrix<CategoryType> evaluatePerformance(
        final Collection<? extends TargetEstimatePair<CategoryType, CategoryType>> data)
    {
        // Create the confusion matrix and add each target-estimate pair to it.
        final ConfusionMatrix<CategoryType> result = this.getFactory().create();
        for (TargetEstimatePair<CategoryType, CategoryType> pair : data)
        {
            result.add(pair.getTarget(), pair.getEstimate());
        }
        return result;
    }

    /**
     * Gets the factory for the confusion matrix the evaluator creates.
     *
     * @return
     *      The factory for the confusion matrix.
     */
    public Factory<? extends ConfusionMatrix<CategoryType>> getFactory()
    {
        return this.factory;
    }

    /**
     * Sets the factory for the confusion matrix the evaluator creates.
     *
     * @param   factory
     *      The factory for the confusion matrix.
     */
    public void setFactory(
        final Factory<? extends ConfusionMatrix<CategoryType>> factory)
    {
        this.factory = factory;
    }
    
}
