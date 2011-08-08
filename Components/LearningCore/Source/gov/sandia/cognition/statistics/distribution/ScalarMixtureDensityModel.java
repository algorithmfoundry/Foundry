/*
 * File:                ScalarMixtureDensityModel.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 23, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.UnivariateProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.SmoothUnivariateDistribution;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * ScalarMixtureDensityModel (SMDM) implements just that: a scalar mixture density
 * model.  There are n distributions which can each be different.  There is
 * an n-dimensional vector of prior probabilities which are the probability of
 * selecting each particular distribution.  So these prior probabilities must
 * sum to 1.0.  To sample from a SMDM is to first select which distribution using
 * the prior probabilities, and then to sample from that distribution to return
 * a sample.
 * <BR><BR>
 * Each distribution must have a mean and variance defined.  A mean and variance
 * for the SMDM can be computed.  Given an input value, a weighted Z value can
 * be computed for the SMDM distribution.
 *
 * @author jdmorr
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-10-20",
    changesNeeded=true,
    comments={
        "Fixed some missing javadoc.",
        "General style fixes.",
        "Added task to figure out a way to avoid storing weights in matrix.",
        "Generally looks good.",
        "Some argument checks need to be more complete"
    },
    response=@CodeReviewResponse(
        date="2009-10-20",
        respondent="Dan Morrow",
        comments = {
            "added additional test coverage",
            "added more argument checks"
        },
        moreChangesNeeded=false
    )
)
@PublicationReference(
    author = "Wikipedia",
    title = "Mixture Model",
    type = PublicationType.WebPage,
    year = 2009,
    url = "http://en.wikipedia.org/wiki/Mixture_density"
)
public class ScalarMixtureDensityModel
    extends LinearMixtureModel<Double, SmoothUnivariateDistribution>
    implements SmoothUnivariateDistribution
{

    /** 
     * Creates a new instance of ScalarMixtureDensityModel
     */
    public ScalarMixtureDensityModel()
    {
        this( new UnivariateGaussian() );
    }

    /**
     * Creates a new instance of ScalarMixtureDensityModel
     * @param distributions
     * Distributions that comprise the SMDM with equal prior weight
     */
    public ScalarMixtureDensityModel(
        SmoothUnivariateDistribution ... distributions )
    {
        this( Arrays.asList(distributions) );
    }

    /**
     * Creates a new instance of ScalarMixtureDensityModel
     * @param distributions
     * Distributions that comprise the SMDM with equal prior weight
     */
    public ScalarMixtureDensityModel(
        final Collection<? extends SmoothUnivariateDistribution> distributions )
    {
        this( distributions, null );
    }

    /**
     * Creates a new instance of ScalarMixtureDensityModel
     * @param distributions
     * Distributions that comprise the SMDM
     * @param priorWeights
     * Weights proportionate by which the distributions are sampled
     */
    public ScalarMixtureDensityModel(
        final Collection<? extends SmoothUnivariateDistribution> distributions,
        final double[] priorWeights)
    {
        super( distributions, priorWeights );
    }

    /**
     * Copy constructor
     * @param other
     * SMDM to copy
     */
    public ScalarMixtureDensityModel(
        final ScalarMixtureDensityModel other )
    {
        this( ObjectUtil.cloneSmartElementsAsArrayList(other.getDistributions()),
            ObjectUtil.deepCopy(other.getPriorWeights()) );
    }

    @Override
    public ScalarMixtureDensityModel clone()
    {
        ScalarMixtureDensityModel clone =
            (ScalarMixtureDensityModel) super.clone();

        clone.setDistributions( ObjectUtil.cloneSmartElementsAsArrayList(
            this.getDistributions() ) );
        clone.setPriorWeights( ObjectUtil.cloneSmart( this.getPriorWeights() ) );
        return clone;
    }

    @Override
    public Vector convertToVector()
    {
        int dim = this.getDistributionCount();
        ArrayList<Vector> parameters = new ArrayList<Vector>( this.getDistributionCount() );
        for( SmoothUnivariateDistribution d : this.distributions )
        {
            Vector p = d.convertToVector();
            dim += p.getDimensionality();
            parameters.add( p );
        }

        Vector p = VectorFactory.getDefault().createVector(dim);
        int index = 0;
        for( int i = 0; i < this.getDistributionCount(); i++ )
        {
            p.setElement(index, this.priorWeights[i] );
            index++;
        }

        for( Vector parameter : parameters )
        {
            for( int i = 0; i < parameter.getDimensionality(); i++ )
            {
                p.setElement(index, parameter.getElement(i) );
                index++;
            }
        }
        return p;
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        int dim = this.getDistributionCount();
        ArrayList<Vector> ps =
            new ArrayList<Vector>( this.getDistributionCount() );
        for( SmoothUnivariateDistribution d : this.distributions )
        {
            Vector p = d.convertToVector();
            dim += p.getDimensionality();
            ps.add( p );
        }

        parameters.assertDimensionalityEquals(dim);

        int index = 0;
        for( int i = 0; i < this.getDistributionCount(); i++ )
        {
            this.priorWeights[i] = parameters.getElement(index);
            index++;
        }

        int d = 0;
        for( Vector p : ps )
        {
            for( int i = 0; i < p.getDimensionality(); i++ )
            {
                p.setElement(i, parameters.getElement(index) );
                index++;
            }
            this.distributions.get(d).convertFromVector(p);
            d++;
        }

    }

    @Override
    public Double getMinSupport()
    {
        double minMin = Double.POSITIVE_INFINITY;
        for( SmoothUnivariateDistribution d : this.getDistributions() )
        {
            final double min = d.getMinSupport();
            if( minMin > min )
            {
                minMin = min;

                // Nope, you can't get any more negative than negative infinity
                if( minMin == Double.NEGATIVE_INFINITY )
                {
                    break;
                }
            }
        }
        return minMin;
    }

    @Override
    public Double getMaxSupport()
    {
        double maxMax = Double.NEGATIVE_INFINITY;
        for( SmoothUnivariateDistribution d : this.getDistributions() )
        {
            final double max = d.getMaxSupport();
            if( maxMax < max )
            {
                maxMax = max;

                // Nope, you can't get any more positive than positive infinity
                if( maxMax == Double.POSITIVE_INFINITY )
                {
                    break;
                }
            }
        }
        return maxMax;
    }

    @Override
    public Double getMean()
    {
        double sum = 0.0;
        int i = 0;
        final double priorSum = this.getPriorWeightSum();

        for( SmoothUnivariateDistribution d : this.getDistributions() )
        {
            final double prior = this.getPriorWeights()[i];
            sum += prior * d.getMean();
            i++;
        }
        return sum / priorSum;
    }

    @PublicationReference(
        author = "Wikipedia",
        title = "Mixture Model",
        type = PublicationType.WebPage,
        year = 2009,
        url = "http://en.wikipedia.org/wiki/Mixture_density"
    )
    @Override
    public double getVariance()
    {
        final double mean = this.getMean();
        final double mean2 = mean*mean;

        double priorWeightSum = 0.0;
        for( int k = 0; k < priorWeights.length; k++ )
        {
            priorWeightSum += this.priorWeights[k];
        }
        if( priorWeightSum <= 0.0 )
        {
            priorWeightSum = 1.0;
        }

        
        double result = 0.0;
        int i = 0;
        for( SmoothUnivariateDistribution distribution : this.getDistributions() )
        {
            final double mi = distribution.getMean();
            final double prior = this.priorWeights[i] / priorWeightSum;
            result += prior*(mi*mi + distribution.getVariance()) - mean2;
            i++;
        }

        return result;
    }

    @Override
    public ScalarMixtureDensityModel.PDF getProbabilityFunction()
    {
        return new ScalarMixtureDensityModel.PDF( this );
    }

    @Override
    public ScalarMixtureDensityModel.CDF getCDF()
    {
        return new ScalarMixtureDensityModel.CDF( this );
    }

    /**
     * PDF of the SMDM
     */
    public static class PDF
        extends ScalarMixtureDensityModel
        implements UnivariateProbabilityDensityFunction
    {

        /**
         * Creates a new instance of ScalarMixtureDensityModel
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of ScalarMixtureDensityModel
         * @param distributions
         * Distributions that comprise the SMDM with equal prior weight
         */
        public PDF(
            SmoothUnivariateDistribution ... distributions )
        {
            super( distributions );
        }

        /**
         * Creates a new instance of ScalarMixtureDensityModel
         * @param distributions
         * Distributions that comprise the SMDM with equal prior weight
         */
        public PDF(
            final Collection<? extends SmoothUnivariateDistribution> distributions )
        {
            super( distributions );
        }

        /**
         * Creates a new instance of ScalarMixtureDensityModel
         * @param distributions
         * Distributions that comprise the SMDM
         * @param priorWeights
         * Weights proportionate by which the distributions are sampled
         */
        public PDF(
            final Collection<? extends SmoothUnivariateDistribution> distributions,
            final double[] priorWeights)
        {
            super( distributions, priorWeights );
        }

        /**
         * Copy constructor
         * @param other
         * SMDM to copy
         */
        public PDF(
            final ScalarMixtureDensityModel other )
        {
            super( other );
        }

        @Override
        public double logEvaluate(
            final Double input)
        {
            return this.logEvaluate(input.doubleValue());
        }

        @Override
        public Double evaluate(
            final Double input)
        {
            return this.evaluate( input.doubleValue() );
        }

        @Override
        public double evaluate(
            final double input)
        {
            final double weightSum = this.getPriorWeightSum();
            double sum = 0.0;
            int i = 0;
            for( SmoothUnivariateDistribution d : this.distributions )
            {
                UnivariateProbabilityDensityFunction pdf =
                    d.getProbabilityFunction();
                final double prior = this.priorWeights[i];
                sum += prior * pdf.evaluate(input);
                i++;
            }
            return sum / weightSum;
        }

        @Override
        public ScalarMixtureDensityModel.PDF getProbabilityFunction()
        {
            return this;
        }

        @Override
        public double logEvaluate(
            final double input)
        {
            return Math.log( this.evaluate(input) );
        }

    }

    /**
     * CDFof the SMDM
     */
    public static class CDF
        extends ScalarMixtureDensityModel
        implements SmoothCumulativeDistributionFunction
    {

        /**
         * Creates a new instance of ScalarMixtureDensityModel
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of ScalarMixtureDensityModel
         * @param distributions
         * Distributions that comprise the SMDM with equal prior weight
         */
        public CDF(
            SmoothUnivariateDistribution ... distributions )
        {
            super( distributions );
        }

        /**
         * Creates a new instance of ScalarMixtureDensityModel
         * @param distributions
         * Distributions that comprise the SMDM with equal prior weight
         */
        public CDF(
            final Collection<? extends SmoothUnivariateDistribution> distributions )
        {
            super( distributions );
        }

        /**
         * Creates a new instance of ScalarMixtureDensityModel
         * @param distributions
         * Distributions that comprise the SMDM
         * @param priorWeights
         * Weights proportionate by which the distributions are sampled
         */
        public CDF(
            final Collection<? extends SmoothUnivariateDistribution> distributions,
            final double[] priorWeights)
        {
            super( distributions, priorWeights );
        }

        /**
         * Copy constructor
         * @param other
         * SMDM to copy
         */
        public CDF(
            final ScalarMixtureDensityModel other )
        {
            super( other );
        }

        @Override
        public ScalarMixtureDensityModel.PDF getDerivative()
        {
            return this.getProbabilityFunction();
        }

        @Override
        public Double evaluate(
            final Double input)
        {
            return this.evaluate( input.doubleValue() );
        }

        @Override
        public double evaluate(
            final double input)
        {
            final double weightSum = this.getPriorWeightSum();
            double sum = 0.0;
            int i = 0;
            for( SmoothUnivariateDistribution d : this.distributions )
            {
                SmoothCumulativeDistributionFunction cdf = d.getCDF();
                final double prior = this.priorWeights[i];
                sum += prior * cdf.evaluate(input);
                i++;
            }
            return sum / weightSum;
        }

        @Override
        public Double differentiate(
            final Double input)
        {
            return this.getDerivative().evaluate(input.doubleValue());
        }

        @Override
        public ScalarMixtureDensityModel.CDF getCDF()
        {
            return this;
        }

    }


    /**
     * An EM learner that estimates a mixture model from data
     */
    public static class EMLearner
        extends AbstractAnytimeBatchLearner<Collection<? extends Double>, ScalarMixtureDensityModel>
        implements Randomized,
        DistributionEstimator<Double,ScalarMixtureDensityModel>,
        MeasurablePerformanceAlgorithm
    {

        /**
         * Name of the performance measurement, {@value}.
         */
        public static final String PERFORMANCE_NAME = "Assignment Change";

        /**
         * Default max iterations, {@value}.
         */
        public static final int DEFAULT_MAX_ITERATIONS = 100;

        /**
         * Default tolerance, {@value}.
         */
        public static final double DEFAULT_TOLERANCE = 1e-5;

        /**
         * Collection of learners used to create each component.
         */
        private Collection<? extends DistributionWeightedEstimator<Double,? extends SmoothUnivariateDistribution>> learners;

        /**
         * Random number generator.
         */
        protected Random random;

        /**
         * Tolerance before stopping, must be greater than or equal to 0
         */
        private double tolerance;

        /**
         * Weighted data used to reestimate the PDFs
         */
        private transient ArrayList<DefaultWeightedValue<Double>> weightedData;

        /**
         * Assignments get each data point onto each of the "k" PDFs.
         */
        private transient ArrayList<double[]> assignments;

        /**
         * Currently estimated set of distributions from the data
         */
        private transient ArrayList<UnivariateProbabilityDensityFunction> distributions;

        /**
         * Priors associated with the current estimates from the data
         */
        private transient double[] distributionPrior;

        /**
         * Amount that the assignments change between iterations
         */
        private transient double assignmentChanged;

        /**
         * Default constructor
         * @param random
         * Random number generator
         */
        public EMLearner(
            Random random )
        {
            this( 2, new UnivariateGaussian.WeightedMaximumLikelihoodEstimator(1.0), random );
        }

        /**
         * Creates a new instance of EMLearner
         * @param numClusters
         * Number of components to estimate
         * @param learner
         * Learner used for each component
         * @param random
         * Random number generator
         */
        public EMLearner(
            int numClusters,
            DistributionWeightedEstimator<Double,? extends SmoothUnivariateDistribution> learner,
            Random random )
        {

            super( DEFAULT_MAX_ITERATIONS );
            this.setTolerance(DEFAULT_TOLERANCE );
            this.setRandom( random );

            ArrayList<DistributionWeightedEstimator<Double, ? extends SmoothUnivariateDistribution>> ll =
                new ArrayList<DistributionWeightedEstimator<Double, ? extends SmoothUnivariateDistribution>>( numClusters );
            for( int k = 0; k < numClusters; k++ )
            {
                ll.add(learner);
            }
            this.setLearners(ll);

        }

        /**
         * Creates a new instance of EMLearner
         * @param learners
         * Learner used for each component
         * @param random
         * Random number generator
         */
        public EMLearner(
            Random random,
            DistributionWeightedEstimator<Double,? extends SmoothUnivariateDistribution> ... learners )
        {

            super( DEFAULT_MAX_ITERATIONS );

            this.setTolerance( DEFAULT_TOLERANCE );
            this.setRandom(random);
            this.setLearners( Arrays.asList(learners) );
        }

        @Override
        protected boolean initializeAlgorithm()
        {
            final int N = this.data.size();
            final int K = this.learners.size();

            // Assign the clusters "near" random data points.
            double[] x = new double[ K ];
            for( int k = 0; k < K; k++ )
            {
                int index = this.random.nextInt( N );
                x[k] = CollectionUtil.getElement(this.data,index) + this.random.nextGaussian();
            }

            this.weightedData = new ArrayList<DefaultWeightedValue<Double>>( N );
            this.assignments = new ArrayList<double[]>( N );
            this.distributionPrior = new double[K];
            this.assignmentChanged = N;
            for( Double value : this.data )
            {
                // Assign the values random weights to the learners initially
                this.weightedData.add( new DefaultWeightedValue<Double>(
                    value, 0.0 ) );

                double[] assignment = new double[ K ];
                double sum = 0.0;
                for( int k = 0; k < K; k++ )
                {
                    double delta = value - x[k];
                    final double ak = Math.exp( -Math.abs(delta) );
                    assignment[k] = ak;
                    sum += ak;
                }
                if( sum <= 0.0 )
                {
                    sum = 1.0;
                }
                for( int k = 0; k < K; k++ )
                {
                    assignment[k] /= sum;
                    this.distributionPrior[k] += assignment[k];
                }

                this.assignments.add( assignment );

            }

            // This is the initial distribution estimates
            this.distributions = new ArrayList<UnivariateProbabilityDensityFunction>( K );
            int k = 0;
            for( DistributionWeightedEstimator<Double,? extends SmoothUnivariateDistribution> learner : this.learners )
            {
                for( int n = 0; n < N; n++ )
                {
                    this.weightedData.get(n).setWeight( this.assignments.get(n)[k] );
                }
                this.distributions.add(
                    learner.learn( this.weightedData ).getProbabilityFunction() );
                k++;
            }

            return true;
        }


        @Override
        protected boolean step()
        {

            final int N = this.data.size();
            final int K = this.learners.size();

            // Reset the counters
            this.assignmentChanged = 0.0;
            Arrays.fill( this.distributionPrior, 0.0 );

            // Go through and set the assignments... the "E" step
            double[] anold = new double[ K ];
            for( int n = 0; n < N; n++ )
            {
                final double xn = this.weightedData.get(n).getValue();
                double[] an = this.assignments.get(n);
                System.arraycopy(an, 0, anold, 0, K);
                int k = 0;
                double sum = 0.0;
                for( UnivariateProbabilityDensityFunction pdf : this.distributions )
                {
                    final double ank = pdf.evaluate(xn);
                    an[k] = ank;
                    sum += ank;
                    k++;
                }
                if( sum <= 0.0 )
                {
                    sum = 1.0;
                }

                for( k = 0; k < K; k++ )
                {
                    final double ank = an[k] / sum;
                    an[k] = ank;
                    double delta = Math.abs(ank - anold[k]);
                    this.distributionPrior[k] += ank;
                    this.assignmentChanged += delta;
                }

            }

            if( this.assignmentChanged <= this.getTolerance() )
            {
                return false;
            }

            // Now update the distributions... the "M" step
            int k = 0;
            for( DistributionWeightedEstimator<Double,? extends SmoothUnivariateDistribution> learner : this.learners )
            {
                for( int n = 0; n < N; n++ )
                {
                    this.weightedData.get(n).setWeight(this.assignments.get(n)[k]);
                }
                SmoothUnivariateDistribution distribution = learner.learn( this.weightedData );
                this.distributions.set( k, distribution.getProbabilityFunction() );
                k++;
            }

            return true;

        }

        @Override
        protected void cleanupAlgorithm()
        {
            this.weightedData = null;
            this.assignments = null;
            this.data = null;
        }

        @Override
        public ScalarMixtureDensityModel getResult()
        {
            return new ScalarMixtureDensityModel(
                this.distributions, this.distributionPrior );
        }

        @Override
        public NamedValue<Double> getPerformance()
        {
            return new DefaultNamedValue<Double>(
                PERFORMANCE_NAME, this.assignmentChanged );
        }

        @Override
        public Random getRandom()
        {
            return this.random;
        }

        @Override
        public void setRandom(
            Random random)
        {
            this.random = random;
        }

        /**
         * Getter for learners
         * @return
         * Collection of learners used to create each component.
         */
        public Collection<? extends DistributionWeightedEstimator<Double, ? extends SmoothUnivariateDistribution>> getLearners()
        {
            return this.learners;
        }

        /**
         * Setter for learners
         * @param learners
         * Collection of learners used to create each component.
         */
        public void setLearners(
            Collection<? extends DistributionWeightedEstimator<Double, ? extends SmoothUnivariateDistribution>> learners)
        {
            this.learners = learners;
        }

        /**
         * Getter for tolerance
         * @return
         * Tolerance before stopping, must be greater than or equal to 0
         */
        public double getTolerance()
        {
            return tolerance;
        }

        /**
         * Setter for tolerance
         * @param tolerance
         * Tolerance before stopping, must be greater than or equal to 0
         */
        public void setTolerance(
            double tolerance)
        {
            ArgumentChecker.assertIsNonNegative("tolerance", tolerance);
            this.tolerance = tolerance;
        }

    }

}
