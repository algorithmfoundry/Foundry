/*
 * File:                BayesianLinearRegression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 11, 2010, Sandia Corporation.
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
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Computes a Bayesian linear estimator for a given feature function
 * and a set of observed data.
 * @author Kevin R. Dixon
 * @since 3.0
 * @param <InputType>
 * Type of inputs to map through the feature map onto a Vector.
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
public class BayesianLinearRegression<InputType>
    extends AbstractBayesianRegression<InputType,Double,MultivariateGaussian>
{

    /**
     * Default output variance, {@value}.
     */
    public static final double DEFAULT_OUTPUT_VARIANCE = 1.0;

    /**
     * Default weight variance, {@value}.
     */
    public static final double DEFAULT_WEIGHT_VARIANCE = 1.0;

    /**
     * Assumed known variance of the outputs (measurements),
     * must be greater than zero.
     */
    private double outputVariance;

    /**
     * Prior distribution of the weights, typically a zero-mean,
     * diagonal-variance distribution.
     */
    private MultivariateGaussian weightPrior;

    /** 
     * Creates a new instance of BayesianLinearRegression 
     * @param dimensionality
     * Sets up the parameters (except featureMap) for the given dimensionality
     * of objects in feature space.
     */
    public BayesianLinearRegression(
        int dimensionality )
    {
        this( null, DEFAULT_OUTPUT_VARIANCE,
            new MultivariateGaussian( VectorFactory.getDefault().createVector(dimensionality),
                MatrixFactory.getDefault().createIdentity(dimensionality,dimensionality).scale( DEFAULT_WEIGHT_VARIANCE ) ) );
    }

    /**
     * Creates a new instance of BayesianLinearRegression
     * @param featureMap
     * Function that maps the input space onto a Vector.
     * @param outputVariance
     * Assumed known variance of the outputs (measurements),
     * must be greater than zero.
     * @param weightPrior
     * Prior distribution of the weights, typically a zero-mean,
     * diagonal-variance distribution.
     */
    public BayesianLinearRegression(
        Evaluator<? super InputType, Vector> featureMap,
        double outputVariance,
        MultivariateGaussian weightPrior)
    {
        super( featureMap );
        this.setOutputVariance(outputVariance);
        this.setWeightPrior(weightPrior);
    }

    @Override
    public BayesianLinearRegression<InputType> clone()
    {
        @SuppressWarnings("unchecked")
        BayesianLinearRegression<InputType> clone =
            (BayesianLinearRegression<InputType>) super.clone();
        clone.setWeightPrior( ObjectUtil.cloneSafe( this.getWeightPrior() ) );
        return clone;
    }

    public MultivariateGaussian learn(
        Iterable<? extends InputOutputPair<? extends InputType, Double>> data)
    {
        
        int featureDim;
        final int N = CollectionUtil.size(data);
        Vector y = VectorFactory.getDefault().createVector( N );

        Matrix X = null;
        int n = 0;
        for (InputOutputPair<? extends InputType, Double> pair : data)
        {
            final double weight = DatasetUtil.getWeight(pair);

            Vector x = this.getFeatureMap().evaluate(pair.getInput());
            if( X == null )
            {
                featureDim = x.getDimensionality();
                X = MatrixFactory.getDefault().createMatrix(featureDim, N);
            }
            double output = pair.getOutput();

            if( weight != 1.0 )
            {
                output *= weight;
                x.scaleEquals(weight);
            }

            X.setColumn( n, x );
            y.setElement( n, output );
            n++;
        }

        Vector m0 = this.getWeightPrior().getMean();
        Matrix C0i = this.getWeightPrior().getCovarianceInverse();

        // Bishop's Equations 3.49 - 3.51
        Matrix C = C0i.plus( X.times( X.transpose() ).scale( 1.0/this.getOutputVariance() ) ).inverse();
        Vector mean = C.times( C0i.times( m0 ).plus(
            X.times( y ).scale( 1.0/this.getOutputVariance() ) ) );

        return new MultivariateGaussian( mean, C );

    }

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
    public UnivariateGaussian createConditionalDistribution(
        InputType input,
        Vector weights )
    {
        double mean = this.featureMap.evaluate(input).dotProduct(weights);
        return new UnivariateGaussian( mean, this.getOutputVariance() );
    }

    /**
     * Getter for weightPrior
     * @return
     * Prior distribution of the weights, typically a zero-mean,
     * diagonal-variance distribution.
     */
    public MultivariateGaussian getWeightPrior()
    {
        return this.weightPrior;
    }

    /**
     * Setter for weightPrior
     * @param weightPrior
     * Prior distribution of the weights, typically a zero-mean,
     * diagonal-variance distribution.
     */
    public void setWeightPrior(
        MultivariateGaussian weightPrior)
    {
        this.weightPrior = weightPrior;
    }

    /**
     * Getter for outputVariance
     * @return
     * Assumed known variance of the outputs (measurements),
     * must be greater than zero.
     */
    public double getOutputVariance()
    {
        return this.outputVariance;
    }

    /**
     * Setter for outputVariance
     * @param outputVariance
     * Assumed known variance of the outputs (measurements),
     * must be greater than zero.
     */
    public void setOutputVariance(
        double outputVariance)
    {
        if( outputVariance <= 0.0 )
        {
            throw new IllegalArgumentException(
                "outputVariance must be > 0.0" );
        }
        this.outputVariance = outputVariance;
    }

    /**
     * Creates the predictive distribution of outputs given the weight posterior
     * @param posterior
     * Posterior distribution of weights.
     * @return
     * Predictive distribution of outputs given the posterior.
     */
    public BayesianLinearRegression<InputType>.PredictiveDistribution createPredictiveDistribution(
        MultivariateGaussian posterior )
    {
        return new PredictiveDistribution( posterior );
    }

    /**
     * Creates the predictive distribution for the likelihood of a given point.
     */
    @PublicationReference(
        author="Christopher M. Bishop",
        title="Pattern Recognition and Machine Learning",
        type=PublicationType.Book,
        year=2006,
        pages=156
    )
    public class PredictiveDistribution
        extends AbstractCloneableSerializable
        implements Evaluator<InputType,UnivariateGaussian.PDF>
    {

        /**
         * Posterior distribution of the weights given the data.
         */
        private MultivariateGaussian posterior;

        /**
         * Creates a new instance of PredictiveDistribution
         * @param posterior
         * Posterior distribution of the weights given the data.
         */
        public PredictiveDistribution(
            MultivariateGaussian posterior )
        {
            this.posterior = posterior;
        }

        public UnivariateGaussian.PDF evaluate(
            InputType input)
        {
            // Bishop's equations 3.58-3.59
            Vector x = featureMap.evaluate(input);
            double mean = x.dotProduct( this.posterior.getMean() );
            double variance = x.times( this.posterior.getCovariance() ).dotProduct(x) + outputVariance;
            return new UnivariateGaussian.PDF( mean, variance );
        }

    }
    
}
