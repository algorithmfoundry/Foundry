/*
 * File:                GaussianProcessRegression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 16, 2010, Sandia Corporation.
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
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.kernel.DefaultKernelContainer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Gaussian Process Regression, is also known as Kriging, is a nonparametric
 * method to interpolate and extrapolate using Bayesian regression, where
 * the expressiveness of the estimator can grow with the data.  However,
 * GPR does not scale well as it requires a N-cubed inversion, where N is the
 * number of data points, to compute the predictive distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 * @param <InputType>
 * Type of inputs to map through the kernel
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Christopher M. Bishop",
            title="Pattern Recognition and Machine Learning",
            type=PublicationType.Book,
            year=2006,
            pages={303,312}
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
            title="Kriging",
            type=PublicationType.WebPage,
            year=2010,
            url="http://en.wikipedia.org/wiki/Kriging"
        )
    }
)
public class GaussianProcessRegression<InputType>
    extends DefaultKernelContainer<InputType>
    implements BayesianEstimator<InputOutputPair<? extends InputType, Double>, Vector, MultivariateGaussian>
{

    /**
     * Default assumed variance of the measurements, {@value}.
     */
    public static final double DEFAULT_MEASUREMENT_VARIANCE = 1.0;

    /**
     * Assumed known variance of the outputs (measurements),
     * must be greater than or equal to zero.
     */
    private double outputVariance;

    /** 
     * Creates a new instance of GaussianProcessRegression 
     */
    public GaussianProcessRegression()
    {
        this( null, DEFAULT_MEASUREMENT_VARIANCE );
    }

    /**
     * Creates a new instance of GaussianProcessRegression
     * @param kernel
     * Kernel to map the InputType to the Vector space
     * @param outputVariance
     * Assumed known variance of the outputs (measurements),
     * must be greater than or equal to zero.
     */
    public GaussianProcessRegression(
        Kernel<InputType> kernel,
        double outputVariance )
    {
        super( kernel );
        this.setOutputVariance(outputVariance);
    }

    @Override
    public GaussianProcessRegression<InputType> clone()
    {
        return (GaussianProcessRegression<InputType>) super.clone();
    }

    public MultivariateGaussian learn(
        Collection<? extends InputOutputPair<? extends InputType, Double>> data)
    {

        ArrayList<? extends InputOutputPair<? extends InputType,Double>> dataArray =
            CollectionUtil.asArrayList(data);
        final int N = dataArray.size();
        Matrix C = MatrixFactory.getDefault().createMatrix(N, N);
        Vector mean = VectorFactory.getDefault().createVector(N);
        for( int i = 0; i < N; i++ )
        {
            InputOutputPair<? extends InputType,Double> pair = dataArray.get(i);
            final InputType xi = pair.getInput();
            mean.setElement(i, pair.getOutput());
            for( int j = 0; j < N; j++ )
            {
                final InputType xj = dataArray.get(j).getInput();
                double kv = this.kernel.evaluate(xi, xj);
                if( i == j )
                {
                    kv += this.getOutputVariance();
                }
                C.setElement(i, j, kv);
            }
        }

        return new MultivariateGaussian( mean, C );
    }

    /**
     * Getter for outputVariance
     * @return
     * Assumed known variance of the outputs (measurements),
     * must be greater than or equal to zero.
     */
    public double getOutputVariance()
    {
        return this.outputVariance;
    }

    /**
     * Getter for outputVariance
     * @param outputVariance
     * Assumed known variance of the outputs (measurements),
     * must be greater than or equal to zero.
     */
    public void setOutputVariance(
        double outputVariance)
    {
        if( outputVariance < 0.0 )
        {
            throw new IllegalArgumentException(
                "Output variance must be >= 0.0" );
        }
        this.outputVariance = outputVariance;
    }

    /**
     * Creates the predictive distribution for future points.
     * @param posterior
     * Posterior from the fitting of the training data.
     * @param inputs
     * Training data inputs
     * @return
     * Predictive distribution of the Gaussian Process Regression
     */
    public GaussianProcessRegression<InputType>.PredictiveDistribution createPredictiveDistribution(
        MultivariateGaussian posterior,
        ArrayList<InputType> inputs )
    {
        return new PredictiveDistribution(posterior, inputs);
    }

    /**
     * Predictive distribution for Gaussian Process Regression.
     */
    @PublicationReference(
        author="Christopher M. Bishop",
        title="Pattern Recognition and Machine Learning",
        type=PublicationType.Book,
        year=2006,
        pages=308,
        notes="Equations 6.66 and 6.67"
    )
    public class PredictiveDistribution
        extends AbstractCloneableSerializable
        implements Evaluator<InputType,UnivariateGaussian>
    {

        /**
         * Inputs that we've condition on.
         */
        private ArrayList<InputType> inputs;

        /**
         * Posterior distribution of the Gaussian process given the data.
         */
        private MultivariateGaussian posterior;

        /**
         * Creates a new instance of PredictiveDistribution
         * @param posterior
         * Posterior distribution of the Gaussian process given the data.
         * @param inputs
         * Inputs that we've condition on.
         */
        public PredictiveDistribution(
            MultivariateGaussian posterior,
            ArrayList<InputType> inputs )
        {
            this.posterior = posterior;
            this.inputs = inputs;
        }

        public UnivariateGaussian evaluate(
            InputType input)
        {

            final int N = this.posterior.getInputDimensionality();
            Vector k = VectorFactory.getDefault().createVector(N);
            final double c = kernel.evaluate(input, input) + getOutputVariance();
            for( int i = 0; i < N; i++ )
            {
                InputType xi = this.inputs.get(i);
                k.setElement(i, kernel.evaluate(xi,input) );
            }

            Matrix Ci = this.posterior.getCovarianceInverse();
            Vector ktCi = k.times( Ci );

            double mean = ktCi.dotProduct( this.posterior.getMean() );
            double variance = c - ktCi.dotProduct( k );
            return new UnivariateGaussian( mean, variance );
        }

    }

}
