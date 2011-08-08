/*
 * File:                BayesianRegression.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.Distribution;

/**
 * A type of regression algorithm maps inputs onto a Vector space, and the
 * weights of this Vector space are represented as a posterior distribution
 * given the observed InputOutputPairs.  The system can also estimate
 * the predictive distribution of future data given the weight posterior
 * for a desired input value.
 * @param <InputType>
 * Type of inputs to map onto a Vector
 * @param <OutputType>
 * Type of outputs to consider, typically a Double
 * @param <PosteriorType>
 * Posterior distribution of the weights given the observed InputOutputPairs
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Christopher M. Bishop",
            title="Pattern Recognition and Machine Learning",
            type=PublicationType.Book,
            year=2006,
            pages={152,159}
        )
        ,
        @PublicationReference(
            author="Hanna M. Wallach",
            title="Introduction to Gaussian Process Regression",
            type=PublicationType.Misc,
            year=2005,
            url="http://www.cs.umass.edu/~wallach/talks/gp_intro.pdf"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Bayesian linear regression",
            type=PublicationType.WebPage,
            year=2010,
            url="http://en.wikipedia.org/wiki/Bayesian_linear_regression"
        )
    }
)
public interface BayesianRegression<InputType,OutputType,PosteriorType extends Distribution<? extends Vector>>
    extends BayesianEstimator<InputOutputPair<? extends InputType, OutputType>, Vector, PosteriorType>
//    IncrementalLearner<InputOutputPair<? extends InputType, OutputType>, PosteriorType>
{

    /**
     * Getter for featureMap
     * @return the featureMap
     * Function that maps the input space onto a Vector.
     */
    public Evaluator<? super InputType, Vector> getFeatureMap();

    /**
     * Setter for featureMap
     * @param featureMap
     * Function that maps the input space onto a Vector.
     */
    public void setFeatureMap(
        Evaluator<? super InputType, Vector> featureMap);

    /**
     * Creates the distribution from which the outputs are generated, given
     * the weights and the input to consider.
     * @param input
     * Input to condition on
     * @param weights
     * Weights that determine the mean
     * @return
     * Conditional distribution from which outputs are generated.
     */
    public Distribution<OutputType> createConditionalDistribution(
        InputType input,
        Vector weights );

    /**
     * Creates the predictive distribution of outputs given the weight posterior
     * @param posterior
     * Posterior distribution of weights.
     * @return
     * Predictive distribution of outputs given the posterior.
     */
    public Evaluator<? super InputType,? extends Distribution<OutputType>> createPredictiveDistribution(
        PosteriorType posterior );
    
}
