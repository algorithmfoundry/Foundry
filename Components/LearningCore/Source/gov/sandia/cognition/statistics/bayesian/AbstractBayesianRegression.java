/*
 * File:                AbstractBayesianRegression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 1, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Partial implementation of BayesianRegression
 * @param <InputType>
 * Type of inputs to map onto a Vector
 * @param <OutputType>
 * Type of outputs to consider, typically a Double
 * @param <PosteriorType>
 * Posterior distribution of the weights given the observed InputOutputPairs
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractBayesianRegression<InputType,OutputType,PosteriorType extends Distribution<? extends Vector>>
    extends AbstractCloneableSerializable
    implements BayesianRegression<InputType,OutputType,PosteriorType>
{

    /**
     * Function that maps the input space onto a Vector.
     */
    protected Evaluator<? super InputType,Vector> featureMap;

    /**
     * Creates a new instance of AbstractBayesianRegression
     * @param featureMap
     * Function that maps the input space onto a Vector.
     */
    public AbstractBayesianRegression(
        Evaluator<? super InputType, Vector> featureMap)
    {
        this.featureMap = featureMap;
    }

    @Override
    public AbstractBayesianRegression<InputType,OutputType,PosteriorType> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractBayesianRegression<InputType,OutputType,PosteriorType> clone =
            (AbstractBayesianRegression<InputType,OutputType,PosteriorType>) super.clone();
        clone.setFeatureMap( ObjectUtil.cloneSmart( this.getFeatureMap() ) );
        return clone;
    }

    @Override
    public Evaluator<? super InputType, Vector> getFeatureMap()
    {
        return this.featureMap;
    }

    @Override
    public void setFeatureMap(
        Evaluator<? super InputType, Vector> featureMap)
    {
        this.featureMap = featureMap;
    }
    
}
