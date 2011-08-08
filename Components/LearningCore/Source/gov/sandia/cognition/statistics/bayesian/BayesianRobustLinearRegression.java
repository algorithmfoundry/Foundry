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
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.InverseGammaDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussianInverseGammaDistribution;
import gov.sandia.cognition.statistics.distribution.StudentTDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

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
     * Creates a new instance of BayesianLinearRegression
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
    public BayesianRobustLinearRegression clone()
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

    public MultivariateGaussianInverseGammaDistribution learn(
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

        Vector m0 = this.weightPrior.getMean();
        Matrix C0i = this.weightPrior.getCovarianceInverse();

        // Bishop's Equations 3.49 - 3.51
        Matrix Cni = C0i.plus( X.times( X.transpose() ) );
        Matrix Cn = Cni.inverse();
        Vector mn = Cn.times( C0i.times( m0 ).plus( X.times( y ) ) );

        double a0 = this.outputVariance.getShape();
        double b0 = this.outputVariance.getScale();
        double an = a0 + N/2.0;
        double bn = b0 + 0.5*(y.dotProduct(y) - mn.times( Cni ).dotProduct(mn));

        return new MultivariateGaussianInverseGammaDistribution(
            new MultivariateGaussian( mn, Cn ),
            new InverseGammaDistribution( an, bn ));
        
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
    public UnivariateGaussian createConditionalDistribution(
        InputType input,
        Vector weights )
    {
        double mean = this.featureMap.evaluate(input).dotProduct(weights);
        double variance = this.getOutputVariance().getMean();
        return new UnivariateGaussian( mean, variance );
    }

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

}
