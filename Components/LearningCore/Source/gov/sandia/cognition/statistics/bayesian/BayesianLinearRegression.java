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
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.IncrementalLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.AbstractSufficientStatistic;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Collection;

/**
 * Computes a Bayesian linear estimator for a given feature function
 * and a set of observed data.
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
public class BayesianLinearRegression
    extends AbstractCloneableSerializable
    implements BayesianRegression<Double,MultivariateGaussian>
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
    protected double outputVariance;

    /**
     * Prior distribution of the weights, typically a zero-mean,
     * diagonal-variance distribution.
     */
    protected MultivariateGaussian weightPrior;

    /** 
     * Creates a new instance of BayesianLinearRegression 
     * @param dimensionality
     * Sets up the parameters (except featureMap) for the given dimensionality
     * of objects in feature space.
     */
    public BayesianLinearRegression(
        int dimensionality )
    {
        this( DEFAULT_OUTPUT_VARIANCE,
            new MultivariateGaussian( VectorFactory.getDefault().createVector(dimensionality),
                MatrixFactory.getDefault().createIdentity(dimensionality,dimensionality).scale( DEFAULT_WEIGHT_VARIANCE ) ) );
    }

    /**
     * Creates a new instance of BayesianLinearRegression
     * @param outputVariance
     * Assumed known variance of the outputs (measurements),
     * must be greater than zero.
     * @param weightPrior
     * Prior distribution of the weights, typically a zero-mean,
     * diagonal-variance distribution.
     */
    public BayesianLinearRegression(
        double outputVariance,
        MultivariateGaussian weightPrior)
    {
        this.setOutputVariance(outputVariance);
        this.setWeightPrior(weightPrior);
    }

    @Override
    public BayesianLinearRegression clone()
    {
        @SuppressWarnings("unchecked")
        BayesianLinearRegression clone = (BayesianLinearRegression) super.clone();
        clone.setWeightPrior( ObjectUtil.cloneSafe( this.getWeightPrior() ) );
        return clone;
    }

    @Override
    public MultivariateGaussian.PDF learn(
        Collection<? extends InputOutputPair<? extends Vectorizable, Double>> data)
    {
        MultivariateGaussian prior = this.getWeightPrior();

        RingAccumulator<Matrix> Cin = new RingAccumulator<Matrix>();
        Matrix Ci = prior.getCovarianceInverse().clone();
        Cin.accumulate( Ci );
        RingAccumulator<Vector> zn = new RingAccumulator<Vector>();
        Vector z = Ci.times( prior.getMean() );
        zn.accumulate( z );

        for (InputOutputPair<? extends Vectorizable, Double> pair : data)
        {
            Vector x1 = pair.getInput().convertToVector();
            Vector x2 = x1.clone();
            final double beta = DatasetUtil.getWeight(pair) / this.outputVariance;
            if( beta != 1.0 )
            {
                x2.scaleEquals(beta);
            }
            Cin.accumulate( x1.outerProduct(x2) );

            final double y = pair.getOutput();
            if( y != 1.0 )
            {
                x2.scaleEquals(y);
            }
            zn.accumulate( x2 );
        }

        Ci = Cin.getSum();
        Matrix C = Ci.inverse();
        z = zn.getSum();
        Vector mean = C.times( z );

        return new MultivariateGaussian.PDF( mean, C );
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
    @Override
    public UnivariateGaussian createConditionalDistribution(
        Vectorizable input,
        Vector weights )
    {
        double mean = input.convertToVector().dotProduct(weights);
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
    @Override
    public BayesianLinearRegression.PredictiveDistribution createPredictiveDistribution(
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
        implements Evaluator<Vectorizable,UnivariateGaussian.PDF>
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

        @Override
        public UnivariateGaussian.PDF evaluate(
            Vectorizable input)
        {
            // Bishop's equations 3.58-3.59
            Vector x = input.convertToVector();
            double mean = x.dotProduct( this.posterior.getMean() );
            double variance = x.times( this.posterior.getCovariance() ).dotProduct(x) + outputVariance;
            return new UnivariateGaussian.PDF( mean, variance );
        }

    }

    /**
     * Incremental estimator for BayesianLinearRegression
     */
    public static class IncrementalEstimator
        extends BayesianLinearRegression
        implements IncrementalLearner<InputOutputPair<? extends Vectorizable,Double>, IncrementalEstimator.SufficientStatistic>
    {

        /**
         * Creates a new instance of IncrementalEstimator
         * @param dimensionality
         * Sets up the parameters (except featureMap) for the given dimensionality
         * of objects in feature space.
         */
        public IncrementalEstimator(
            int dimensionality )
        {
            super( dimensionality );
        }

        /**
         * Creates a new instance of IncrementalEstimator
         * @param outputVariance
         * Assumed known variance of the outputs (measurements),
         * must be greater than zero.
         * @param weightPrior
         * Prior distribution of the weights, typically a zero-mean,
         * diagonal-variance distribution.
         */
        public IncrementalEstimator(
            double outputVariance,
            MultivariateGaussian weightPrior)
        {
            super( outputVariance, weightPrior);
        }

        @Override
        public IncrementalEstimator.SufficientStatistic createInitialLearnedObject()
        {
            return new SufficientStatistic(this.getWeightPrior());
        }

        @Override
        public MultivariateGaussian.PDF learn(
            Collection<? extends InputOutputPair<? extends Vectorizable, Double>> data)
        {
            IncrementalEstimator.SufficientStatistic target =
                this.createInitialLearnedObject();
            this.update(target, data);
            return target.create();
        }

        @Override
        public void update(
            IncrementalEstimator.SufficientStatistic target,
            InputOutputPair<? extends Vectorizable, Double> data)
        {
            target.update(data);
        }

        @Override
        public void update(
            SufficientStatistic target,
            Iterable<? extends InputOutputPair<? extends Vectorizable, Double>> data)
        {
            target.update(data);
        }

        /**
         * SufficientStatistic for incremental Bayesian linear regression
         */
        public class SufficientStatistic
            extends AbstractSufficientStatistic<InputOutputPair<? extends Vectorizable, Double>, MultivariateGaussian>
        {

            /**
             * "z" statistic, proportional to the mean
             */
            private Vector z;

            /**
             * Covariance inverse, sometimes called "precision"
             */
            private Matrix covarianceInverse;

            /**
             * Creates a new instance of SufficientStatistic
             * @param prior
             * Prior on the weights
             */
            public SufficientStatistic(
                MultivariateGaussian prior )
            {
                super();

                if( prior != null )
                {
                    this.covarianceInverse = prior.getCovarianceInverse().clone();
                    this.z = this.covarianceInverse.times( prior.getMean() );
                    this.count = 1;
                }
                else
                {
                    this.covarianceInverse = null;
                    this.z = null;
                    this.count = 0;
                }
            }

            @Override
            public void update(
                InputOutputPair<? extends Vectorizable, Double> value)
            {
                this.count++;
                Vector v = value.getInput().convertToVector();
                Vector x1 = v;
                Vector x2 = v.clone();
                final double y = value.getOutput();
                final double beta = DatasetUtil.getWeight(value) / outputVariance;
                if( beta != 1.0 )
                {
                    x2.scaleEquals(beta);
                }

                if( this.covarianceInverse == null )
                {
                    this.covarianceInverse = x1.outerProduct(x2);
                }
                else
                {
                    this.covarianceInverse.plusEquals( x1.outerProduct(x2) );
                }

                if( y != 1.0 )
                {
                    x2.scaleEquals( y );
                }

                if( this.z == null )
                {
                    this.z = x2;
                }
                else
                {
                    this.z.plusEquals( x2 );
                }
            }

            @Override
            public MultivariateGaussian.PDF create()
            {
                MultivariateGaussian.PDF g =
                    new MultivariateGaussian.PDF(this.getDimensionality());
                this.create(g);
                return g;
            }

            @Override
            public void create(
                MultivariateGaussian distribution)
            {
                distribution.setMean(this.getMean());
                distribution.setCovarianceInverse(this.getCovarianceInverse());
            }

            /**
             * Getter for covarianceInverse
             * @return
             * Covariance inverse, sometimes called "precision"
             */
            public Matrix getCovarianceInverse()
            {
                return this.covarianceInverse;
            }

            /**
             * Getter for z
             * @return
             * "z" statistic, proportional to the mean
             */
            public Vector getZ()
            {
                return this.z;
            }
            
            /**
             * Computes the mean of the Gaussian, but involves a matrix
             * inversion and multiplication, so it's expensive.
             * @return
             * Mean of the Gaussian.
             */
            public Vector getMean()
            {
                return this.covarianceInverse.inverse().times( this.z );
            }

            /**
             * Gets the dimensionality of the underlying Gaussian
             * @return
             * Dimensionality of the underlying Gaussian
             */
            public int getDimensionality()
            {
                return this.getZ().getDimensionality();
            }

        }

    }

}
