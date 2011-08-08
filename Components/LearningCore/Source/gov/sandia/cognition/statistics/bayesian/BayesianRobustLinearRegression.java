/*
 * File:                BayesianRobustLinearRegression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Mar 18, 2010, Sandia Corporation.
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
import gov.sandia.cognition.statistics.AbstractSufficientStatistic;
import gov.sandia.cognition.statistics.distribution.InverseGammaDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussianInverseGammaDistribution;
import gov.sandia.cognition.statistics.distribution.StudentTDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Collection;

/**
 * Computes a Bayesian linear estimator for a given feature function given
 * a set of InputOutputPair observed values.  This regression algorithm
 * assumes that the conditional distribution of observations is a zero-mean
 * Gaussian with unknown variance, distributed according to the standard
 * (conjugate prior) inverse-Gamma distribution.  The prior distribution of
 * weights is a multivariate Gaussian.  Given a training set, the algorithm
 * computes the posterior of the weights given the training set.  The system
 * perform robust regression because the predictive distribution of future
 * data is a Student-t distribution, resulting from the assumption that
 * the variance of the outputs is unknown.  Consequently, the algorithm is
 * more robust against outliers than fix-variance regression.
 * @param <InputType>
 * Type of inputs to map through the feature map onto a Vector.
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
            author="Jan Drugowitsch",
            title="Bayesian Linear Regression",
            type=PublicationType.Misc,
            year=2009,
            url="http://www.bcs.rochester.edu/people/jdrugowitsch/code/bayes_linear_notes_0.1.1.pdf"
        )
    }
)
public class BayesianRobustLinearRegression<InputType>
    extends AbstractBayesianRegression<InputType,Double,MultivariateGaussianInverseGammaDistribution>
{

    /**
     * Default weight variance, {@value}.
     */
    public static final double DEFAULT_WEIGHT_VARIANCE = 1.0;

    /**
     * Prior distribution of the weights, typically a zero-mean,
     * diagonal-variance distribution.
     */
    private MultivariateGaussian weightPrior;

    /**
     * Distribution of the output (measurement) variance
     */
    private InverseGammaDistribution outputVariance;

    /**
     * Creates a new instance of BayesianLinearRegression
     * @param dimensionality
     * Sets up the parameters (except featureMap) for the given dimensionality
     * of objects in feature space.
     */
    public BayesianRobustLinearRegression(
        int dimensionality )
    {
        this( null, new InverseGammaDistribution(),
            new MultivariateGaussian( VectorFactory.getDefault().createVector(dimensionality),
                MatrixFactory.getDefault().createIdentity(dimensionality,dimensionality).scale( DEFAULT_WEIGHT_VARIANCE ) ) );
    }

    /**
     * Creates a new instance of BayesianRobustLinearRegression
     * @param featureMap
     * Function that maps the input space onto a Vector.
     * @param outputVariance
     * Distribution of the output (measurement) variance
     * @param weightPrior
     * Prior distribution of the weights, typically a zero-mean,
     * diagonal-variance distribution.
     */
    public BayesianRobustLinearRegression(
        Evaluator<? super InputType, Vector> featureMap,
        InverseGammaDistribution outputVariance,
        MultivariateGaussian weightPrior )
    {
        super( featureMap );
        this.setWeightPrior(weightPrior);
        this.setOutputVariance(outputVariance);
    }

    @Override
    public BayesianRobustLinearRegression<InputType> clone()
    {
        @SuppressWarnings("unchecked")
        BayesianRobustLinearRegression<InputType> clone =
            (BayesianRobustLinearRegression<InputType>) super.clone();
        clone.setWeightPrior( ObjectUtil.cloneSafe( this.getWeightPrior() ) );
        clone.setOutputVariance( ObjectUtil.cloneSafe( this.getOutputVariance() ) );
        return clone;
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

    @Override
    public MultivariateGaussianInverseGammaDistribution learn(
        Collection<? extends InputOutputPair<? extends InputType, Double>> data)
    {
        MultivariateGaussian g = this.weightPrior;
        RingAccumulator<Matrix> Cin = new RingAccumulator<Matrix>();
        Matrix Ci = g.getCovarianceInverse();
        Cin.accumulate( Ci );
        RingAccumulator<Vector> zn = new RingAccumulator<Vector>();
        Vector z = Ci.times( g.getMean() );
        zn.accumulate( z );

        InverseGammaDistribution ig = this.outputVariance;
        double an = ig.getShape();
        double bn = ig.getScale();
        double sy2 = 0.0;
        for (InputOutputPair<? extends InputType, Double> pair : data)
        {
            Vector x1 = this.featureMap.evaluate(pair.getInput());
            Vector x2 = x1.clone();
            final double beta = DatasetUtil.getWeight(pair);
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

            sy2 += y*y;
            an += 0.5;
        }

        Ci = Cin.getSum();
        Matrix C = Ci.inverse();
        z = zn.getSum();
        Vector mean = C.times( z );
        bn += 0.5*(sy2 - mean.times( Ci ).dotProduct(mean));

        return new MultivariateGaussianInverseGammaDistribution(
            new MultivariateGaussian( mean, C ),
            new InverseGammaDistribution( an, bn ) );

    }

    /**
     * Getter for outputVariance
     * @return
     * Distribution of the output (measurement) variance
     */
    public InverseGammaDistribution getOutputVariance()
    {
        return this.outputVariance;
    }

    /**
     * Setter for outputVariance
     * @param outputVariance
     * Distribution of the output (measurement) variance
     */
    public void setOutputVariance(
        InverseGammaDistribution outputVariance)
    {
        this.outputVariance = outputVariance;
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
        InputType input,
        Vector weights )
    {
        double mean = this.featureMap.evaluate(input).dotProduct(weights);
        double variance = this.getOutputVariance().getMean();
        return new UnivariateGaussian( mean, variance );
    }

    @Override
    public BayesianRobustLinearRegression<InputType>.PredictiveDistribution createPredictiveDistribution(
        MultivariateGaussianInverseGammaDistribution posterior)
    {
        return new PredictiveDistribution(posterior);
    }

    /**
     * Predictive distribution of future data given the posterior of
     * the weights given the data.
     */
    public class PredictiveDistribution
        extends AbstractCloneableSerializable
        implements Evaluator<InputType,StudentTDistribution>
    {

        /**
         * Posterior distribution of the weights given the data.
         */
        private MultivariateGaussianInverseGammaDistribution posterior;

        /**
         * Creates a new instance of PredictiveDistribution
         * @param posterior
         * Posterior distribution of the weights given the data.
         */
        public PredictiveDistribution(
            MultivariateGaussianInverseGammaDistribution posterior)
        {
            this.posterior = posterior;
        }

        @Override
        public StudentTDistribution evaluate(
            InputType input)
        {
            Vector x = featureMap.evaluate(input);
            double mean = x.dotProduct( this.posterior.getMean() );
            double dofs = this.posterior.getInverseGamma().getShape() * 2.0;
            double v = x.times( this.posterior.getGaussian().getCovariance() ).dotProduct(x);
            double anbn = this.posterior.getInverseGamma().getShape() / this.posterior.getInverseGamma().getScale();
            double precision = anbn / (1.0 + v);
            return new StudentTDistribution( dofs, mean, precision );
        }

    }

    /**
     * Incremental estimator for BayesianRobustLinearRegression
     * @param <InputType>
     */
    public static class IncrementalEstimator<InputType>
        extends BayesianRobustLinearRegression<InputType>
        implements IncrementalLearner<InputOutputPair<? extends InputType,Double>, IncrementalEstimator<InputType>.SufficientStatistic>
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
         * Creates a new instance of IncrementalEstimator with the given
         * dimensionality and feature map.
         *
         * @param dimensionality
         * Sets up the parameters (except featureMap) for the given dimensionality
         * of objects in feature space.
         * @param featureMap
         * Function that maps the input space onto a Vector.
         */
        public IncrementalEstimator(
            int dimensionality,
            Evaluator<? super InputType, Vector> featureMap)
        {
            this(dimensionality);

            this.setFeatureMap(featureMap);
        }

        /**
         * Creates a new instance of IncrementalEstimator
         * @param featureMap
         * Function that maps the input space onto a Vector.
         * @param outputVariance
         * Distribution of the output (measurement) variance
         * @param weightPrior
         * Prior distribution of the weights, typically a zero-mean,
         * diagonal-variance distribution.
         */
        public IncrementalEstimator(
            Evaluator<? super InputType, Vector> featureMap,
            InverseGammaDistribution outputVariance,
            MultivariateGaussian weightPrior )
        {
            super( featureMap, outputVariance, weightPrior);
        }

        @Override
        public IncrementalEstimator<InputType>.SufficientStatistic createInitialLearnedObject()
        {
            return new SufficientStatistic(
                new MultivariateGaussianInverseGammaDistribution(
                    this.getWeightPrior(), this.getOutputVariance() ));
        }

        @Override
        public MultivariateGaussianInverseGammaDistribution learn(
            Collection<? extends InputOutputPair<? extends InputType, Double>> data)
        {
            IncrementalEstimator<InputType>.SufficientStatistic target = this.createInitialLearnedObject();
            this.update(target, data);
            return target.create();
        }

        @Override
        public void update(
            IncrementalEstimator<InputType>.SufficientStatistic target,
            InputOutputPair<? extends InputType, Double> data)
        {
            target.update(data);
        }

        @Override
        public void update(
            SufficientStatistic target,
            Iterable<? extends InputOutputPair<? extends InputType, Double>> data)
        {
            target.update(data);
        }

        /**
         * SufficientStatistic for incremental Bayesian linear regression
         */
        public class SufficientStatistic
            extends AbstractSufficientStatistic<InputOutputPair<? extends InputType, Double>, MultivariateGaussianInverseGammaDistribution>
        {

            /**
             * Sum of the output squared
             */
            private double outputSumSquared;

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
                MultivariateGaussianInverseGammaDistribution prior )
            {
                super();

                if( prior != null )
                {
                    Vector mean = prior.getMean();
                    this.covarianceInverse =
                        prior.getGaussian().getCovarianceInverse().clone();
                    this.z = this.covarianceInverse.times( mean );

                    double a0 = prior.getInverseGamma().getShape();
                    double b0 = prior.getInverseGamma().getScale();
                    this.count = (long) Math.ceil(2.0*a0);
                    this.outputSumSquared = 2.0*b0 + mean.dotProduct(this.z);
                }
                else
                {
                    this.covarianceInverse = null;
                    this.z = null;
                    this.count = 0;
                    this.outputSumSquared = 0.0;
                }
            }

            @Override
            public void update(
                InputOutputPair<? extends InputType, Double> value)
            {
                this.count++;
                Vector v = featureMap.evaluate(value.getInput());

                Vector x1 = v;
                Vector x2 = v.clone();
                final double y = value.getOutput();
                final double beta = DatasetUtil.getWeight(value);
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

                this.outputSumSquared += y*y;
            }

            @Override
            public MultivariateGaussianInverseGammaDistribution create()
            {
                MultivariateGaussianInverseGammaDistribution g =
                    new MultivariateGaussianInverseGammaDistribution( this.getDimensionality() );
                this.create(g);
                return g;
            }

            @Override
            public void create(
                MultivariateGaussianInverseGammaDistribution distribution)
            {
                distribution.getGaussian().setMean(this.getMean());
                distribution.getGaussian().setCovarianceInverse(this.getCovarianceInverse());

                distribution.getInverseGamma().setShape(this.getShape());
                distribution.getInverseGamma().setScale(this.getScale());
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

            /**
             * Getter for outputSumSquared
             * @return
             * Sum of the output squared
             */
            public double getOutputSumSquared()
            {
                return this.outputSumSquared;
            }

            /**
             * Computes the shape component for the inverse-gamma distribution
             * @return
             * Shape component for the inverse-gamma distribution
             */
            public double getShape()
            {
                return this.getCount()/2.0;
            }

            /**
             * Computes the scale component for the inverse-gamma distribution
             * @return
             * Scale component for the inverse-gamma distribution
             */
            public double getScale()
            {
                Vector mean = this.getMean();
                Matrix Ci = this.covarianceInverse;

                return 0.5 * (this.outputSumSquared - mean.times(Ci).dotProduct(mean));
            }

        }


    }

}
