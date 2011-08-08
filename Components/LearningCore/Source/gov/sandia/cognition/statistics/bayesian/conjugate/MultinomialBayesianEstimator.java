/*
 * File:                MultinomialBayesianEstimator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 17, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.bayesian.AbstractBayesianParameter;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.statistics.distribution.DirichletDistribution;
import gov.sandia.cognition.statistics.distribution.MultinomialDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariatePolyaDistribution;

/**
 * A Bayesian estimator for the parameters of a MultinomialDistribution using
 * its conjugate prior distribution, the DirichletDistribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Conjugate Prior",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Conjugate_prior"
)
public class MultinomialBayesianEstimator 
    extends AbstractConjugatePriorBayesianEstimator<Vector,Vector,MultinomialDistribution,DirichletDistribution>
    implements ConjugatePriorBayesianEstimatorPredictor<Vector,Vector,MultinomialDistribution,DirichletDistribution>
{

    /**
     * Default number of trials, {@value}.
     */
    public static final int DEFAULT_NUM_TRIALS = 2;

    /**
     * Default number of classes/labels, {@value}.
     */
    public static final int DEFAULT_NUM_CLASSES = 2;

    /** 
     * Creates a new instance of MultinomialBayesianEstimator 
     */
    public MultinomialBayesianEstimator()
    {
        this( DEFAULT_NUM_TRIALS );
    }

    /**
     * Creates a new instance of MultinomialBayesianEstimator
     * @param numTrials
     * Number of trials in the distribution, must be greater than 0.
     */
    public MultinomialBayesianEstimator(
        int numTrials )
    {
        this( DEFAULT_NUM_CLASSES, numTrials );
    }

    /**
     * Creates a new instance of MultinomialBayesianEstimator 
     * @param numClasses
     * Number of classes/labels/parameters
     * @param numTrials
     * Number of trials in the distribution, must be greater than 0.
     */
    public MultinomialBayesianEstimator(
        int numClasses,
        int numTrials )
    {
        this( new DirichletDistribution( VectorFactory.getDefault().createVector(numClasses,1.0) ), numTrials );
    }

    /**
     * Creates a new instance of MultinomialBayesianEstimator
     * @param initialBelief
     * Initial belief of the prior.
     * @param numTrials
     * Number of trials in the distribution, must be greater than 0.
     */
    public MultinomialBayesianEstimator(
        DirichletDistribution initialBelief,
        int numTrials )
    {
        this( new MultinomialDistribution( initialBelief.getParameters().getDimensionality(), numTrials), initialBelief );
    }

    /**
     * Creates a new instance of PoissonBayesianEstimator
     * @param prior
     * Default conjugate prior.
     * @param conditional
     * Conditional distribution of the conjugate prior.
     */
    public MultinomialBayesianEstimator(
        MultinomialDistribution conditional,
        DirichletDistribution prior )
    {
        this( new MultinomialBayesianEstimator.Parameter(conditional, prior) );
    }

    /**
     * Creates a new instance
     * @param parameter
     * Bayesian hyperparameter relationship between the conditional
     * distribution and the conjugate prior distribution.
     */
    protected MultinomialBayesianEstimator(
        BayesianParameter<Vector,MultinomialDistribution,DirichletDistribution> parameter )
    {
        super( parameter );
    }

    public MultinomialBayesianEstimator.Parameter createParameter(
        MultinomialDistribution conditional,
        DirichletDistribution prior)
    {
        return new MultinomialBayesianEstimator.Parameter( conditional, prior );
    }

    public double computeEquivalentSampleSize(
        DirichletDistribution belief)
    {
        Vector a = belief.getParameters();
        return a.norm1() / this.getNumTrials();
    }

    public void update(
        DirichletDistribution belief,
        Vector value)
    {
        Vector a = belief.getParameters();
        Vector anext = a.plus( value );
        belief.setParameters(anext);
    }

    /**
     * Getter for numTrials
     * @return
     * Number of trials in the distribution, must be greater than 0.
     */
    public int getNumTrials()
    {
        return this.parameter.getConditionalDistribution().getNumTrials();
    }

    /**
     * Setter for numTrials
     * @param numTrials
     * Number of trials in the distribution, must be greater than 0.
     */
    public void setNumTrials(
        int numTrials)
    {
        if( numTrials <= 0 )
        {
            throw new IllegalArgumentException( "numTrials must be > 0" );
        }
        this.parameter.getConditionalDistribution().setNumTrials(numTrials);
    }

    @Override
    public MultinomialDistribution createConditionalDistribution(
        Vector parameter)
    {
        parameter.assertSameDimensionality(
            this.parameter.getConditionalDistribution().getParameters() );
        return super.createConditionalDistribution(parameter);
    }

    public MultivariatePolyaDistribution createPredictiveDistribution(
        DirichletDistribution posterior)
    {
        return new MultivariatePolyaDistribution.PMF(
            posterior.getParameters(), this.getNumTrials() );
    }

    /**
     * Parameter of this conjugate prior relationship.
     */
    public static class Parameter
        extends AbstractBayesianParameter<Vector,MultinomialDistribution,DirichletDistribution>
    {

        /**
         * Name of the parameter, {@value}.
         */
        public static final String NAME = "parameters";

        /**
         * Creates a new instance
         * @param prior
         * Default conjugate prior.
         * @param conditional
         * Conditional distribution of the conjugate prior.
         */
        public Parameter(
            MultinomialDistribution conditional,
            DirichletDistribution prior )
        {
            super( conditional, NAME, prior );
        }

        public void setValue(
            Vector value)
        {
            value.assertSameDimensionality(
                this.conditionalDistribution.getParameters() );
            this.conditionalDistribution.setParameters(value);
        }

        public Vector getValue()
        {
            return this.conditionalDistribution.getParameters();
        }

    }

}
