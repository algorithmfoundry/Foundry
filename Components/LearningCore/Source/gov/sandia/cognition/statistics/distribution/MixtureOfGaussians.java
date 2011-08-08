/*
 * File:                MixtureOfGaussians.java
 * Authors:             krdixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 7, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.algorithm.AnytimeAlgorithmWrapper;
import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.clustering.KMeansClusterer;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.DistributionEstimator;
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
 * Creates a probability density function (pdf) comprising of a collection of
 * MultivariateGaussian and corresponding prior probability distribution that
 * a particular MultivariateGaussian generates observations.  This is the
 * "typical" example of a multi-modal distribution
 *
 * @author krdixon
 * @since  1.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Mixture Model",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Mixture_model"
)
public class MixtureOfGaussians
{    

    /**
     * PDF of the MixtureOfGaussians
     */
    public static class PDF
        extends MultivariateMixtureDensityModel.PDF<MultivariateGaussian>
    {

        /**
         * Creates a new instance of MixtureOfGaussians
         * @param distributions
         * Underlying distributions from which we sample
         */
        public PDF(
            MultivariateGaussian ... distributions )
        {
            this( Arrays.asList( distributions ) );
        }


        /**
         * Creates a new instance of MixtureOfGaussians
         * @param distributions
         * Underlying distributions from which we sample
         */
        public PDF(
            Collection<? extends MultivariateGaussian> distributions )
        {
            this( distributions, null );
        }

        /**
         * Creates a new instance of LinearMixtureModel
         * @param distributions
         * Underlying distributions from which we sample
         * @param priorWeights
         * Weights proportionate by which the distributions are sampled
         */
        public PDF(
            Collection<? extends MultivariateGaussian> distributions,
            double[] priorWeights )
        {
            super( distributions, priorWeights );
        }

        /**
         * Copy Constructor
         * @param other
         * MixtureOfGaussians to copy
         */
        public PDF(
            MixtureOfGaussians.PDF other )
        {
            this( ObjectUtil.cloneSmartElementsAsArrayList(other.getDistributions()),
                ObjectUtil.deepCopy(other.getPriorWeights()) );
        }

        @Override
        public MixtureOfGaussians.PDF clone()
        {
            return (MixtureOfGaussians.PDF) super.clone();
        }

        @Override
        public MixtureOfGaussians.PDF getProbabilityFunction()
        {
            return this;
        }

        /**
         * Gets the dimensionality of the MultivariateGaussian in the mixture
         * @return
         * Input dimensionality of the mixture
         */
        public int getDimensionality()
        {
            return CollectionUtil.getFirst( this.getDistributions() ).getInputDimensionality();
        }


        /**
         * Fits a single MultivariateGaussian to the given MixtureOfGaussians
         * @return MultivariateGaussian that captures the mean and covariance of
         * the given MixtureOfGaussians
         */
        public MultivariateGaussian.PDF fitSingleGaussian()
        {

            Vector mean = this.getMean();

            RingAccumulator<Matrix> covarianceAccumulator =
                new RingAccumulator<Matrix>();
            double denom = this.getPriorWeightSum();
            for( int i = 0; i < this.getDistributionCount(); i++ )
            {
                MultivariateGaussian Gaussian =
                    (MultivariateGaussian) this.getDistributions().get(i);
                Vector meanDiff = Gaussian.getMean().minus( mean );
                covarianceAccumulator.accumulate( Gaussian.getCovariance().plus(
                    meanDiff.outerProduct( meanDiff ) ).scale(
                        this.priorWeights[i]/denom ) );
            }

            return new MultivariateGaussian.PDF( mean, covarianceAccumulator.getSum() );

        }

        /**
         * Computes the weighted z-value (deviate) of the given input.  This
         * is the multivariate equivalent of the "number of standard deviations
         * away from the mean."
         * @param input
         * Input about which to compute the z-value.
         * @return
         * Weighted z-value.
         */
        public double computeWeightedZSquared(
            Vector input )
        {

            double[] p = this.computeRandomVariableProbabilities(input);
            double weightedZSquared = 0.0;
            int index = 0;
            for( MultivariateGaussian g : this.getDistributions() )
            {
                weightedZSquared += g.computeZSquared(input) * p[index];
                index++;
            }

            return weightedZSquared;

        }

    }

    /**
     * A hard-assignment learner for a MixtureOfGaussians
     */
    public static class Learner
        extends AnytimeAlgorithmWrapper<MixtureOfGaussians.PDF, KMeansClusterer<Vector,GaussianCluster>>
        implements DistributionEstimator<Vector,MixtureOfGaussians.PDF>,
        MeasurablePerformanceAlgorithm
    {
// TODO: Rename this to HardLearner now this is a soft learner.
// TODO: Also, have this take any clustering algorithm that can take vectors and produce clusters rather than just KMeans.
// -- jdbasil (2009-09-11)
        /**
         * Creates a new Learner
         * @param algorithm
         * KMeansClusterer to wrap.
         */
        public Learner(
            KMeansClusterer<Vector,GaussianCluster> algorithm )
        {
            super( algorithm );
        }

        @Override
        public MixtureOfGaussians.PDF getResult()
        {
            Collection<GaussianCluster> clusters = this.getAlgorithm().getResult();
            if( (clusters != null) && (clusters.size() > 0) )
            {
                final int K = clusters.size();
                ArrayList<MultivariateGaussian.PDF> gaussians =
                    new ArrayList<MultivariateGaussian.PDF>( K );
                double[] priorProbabilities = new double[ K ];
//                Vector priorProbabilities =
//                    VectorFactory.getDefault().createVector( clusters.size() );
                int index = 0;
                for( GaussianCluster cluster : clusters )
                {
                    gaussians.add( cluster.getGaussian() );
                    int num = cluster.getMembers().size();
                    priorProbabilities[index] = num;
                    index++;
                }

                return new MixtureOfGaussians.PDF( gaussians, priorProbabilities );
            }
            else
            {
                return null;
            }

        }

        @Override
        public MixtureOfGaussians.PDF learn(
            Collection<? extends Vector> data)
        {
            this.getAlgorithm().learn(data);
            return this.getResult();
        }

        @Override
        public NamedValue<? extends Number> getPerformance()
        {
            return this.getAlgorithm().getPerformance();
        }
    }
    
    /**
     * An Expectation-Maximization based "soft" assignment learner.
     */
    @PublicationReference
    (
        author="Jaakkola",
        title="Estimating mixtures: the EM-algorithm",
        type=PublicationType.Misc,
        year=2007,
        url="http://courses.csail.mit.edu/6.867/lectures/notes-em2.pdf"
     )
     public static class EMLearner
        extends AbstractAnytimeBatchLearner<Collection<? extends Vector>, MixtureOfGaussians.PDF>
        implements Randomized,
        DistributionEstimator<Vector,MixtureOfGaussians.PDF>,
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
         * Learner used to estimate each state.
         */
        private MultivariateGaussian.WeightedMaximumLikelihoodEstimator learner;

        /**
         * Random number generator.
         */
        protected Random random;

        /**
         * Tolerance before stopping, must be greater than or equal to 0
         */
        private double tolerance;

        /**
         * Creates a new instance of EMLearner
         * @param random
         * Random number generator
         */
        public EMLearner(
            Random random )
        {
            this( 2, random );
        }

        /**
         * Creates a new instance of EMLearner
         * @param distributionCount
         * Number of distributions in the mixture
         * @param random
         * Random number generator
         */
        public EMLearner(
            int distributionCount,
            Random random )
        {
            this( distributionCount,
                new MultivariateGaussian.WeightedMaximumLikelihoodEstimator(), random );
        }

        /**
         * Creates a new instance of EMLearner
         * @param distributionCount
         * Number of distributions in the mixture
         * @param learner
         * Learner used to reestimate the components
         * @param random
         * Random number generator
         */
        public EMLearner(
            int distributionCount,
            MultivariateGaussian.WeightedMaximumLikelihoodEstimator learner,
            Random random )
        {
            super( DEFAULT_MAX_ITERATIONS );

            this.setRandom(random);
            this.setTolerance( DEFAULT_TOLERANCE );
            this.learner = learner;
            this.distributionPrior = new double[ distributionCount ];
            Arrays.fill( this.distributionPrior, 1.0 );
        }


        /**
         * Weighted data used to reestimate the PDFs
         */
        private transient ArrayList<DefaultWeightedValue<Vector>> weightedData;

        /**
         * Assignments get each data point onto each of the "k" PDFs.
         */
        private transient ArrayList<double[]> assignments;

        /**
         * Currently estimated set of distributions from the data
         */
        private transient ArrayList<MultivariateGaussian.PDF> distributions;

        /**
         * Priors associated with the current estimates from the data
         */
        private transient double[] distributionPrior;

        /**
         * Amount that the assignments change between iterations
         */
        private transient double assignmentChanged;

        @Override
        protected boolean initializeAlgorithm()
        {
            final int N = this.data.size();
            final int K = this.distributionPrior.length;
            final int dim = CollectionUtil.getFirst(this.data).getDimensionality();

            // Assign the clusters "near" random data points.
            Vector[] x = new Vector[ K ];
            for( int k = 0; k < K; k++ )
            {
                int index = this.random.nextInt( N );
                x[k] = VectorFactory.getDefault().createUniformRandom(
                    dim, -1.0, 1.0, this.getRandom() );
                x[k].plusEquals( CollectionUtil.getElement(this.data,index) );
            }

            this.weightedData = new ArrayList<DefaultWeightedValue<Vector>>( N );
            this.assignments = new ArrayList<double[]>( N );
            this.distributionPrior = new double[K];
            this.assignmentChanged = N;
            for( Vector value : this.data )
            {
                // Assign the values random weights to the learners initially
                this.weightedData.add( new DefaultWeightedValue<Vector>(
                    value, 0.0 ) );

                double[] assignment = new double[ K ];
                double sum = 0.0;
                for( int k = 0; k < K; k++ )
                {
                    Vector delta = value.minus( x[k] );
                    final double ak = Math.exp( -delta.norm1() );
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
            this.distributions = new ArrayList<MultivariateGaussian.PDF>( K );
            for( int k = 0; k < K; k++ )
            {
                for( int n = 0; n < N; n++ )
                {
                    this.weightedData.get(n).setWeight( this.assignments.get(n)[k] );
                }
                this.distributions.add( this.learner.learn( this.weightedData ) );
            }

            return true;
        }

        @Override
        protected boolean step()
        {
            final int N = this.data.size();
            final int K = this.distributionPrior.length;

            // Reset the counters
            this.assignmentChanged = 0.0;
            Arrays.fill( this.distributionPrior, 0.0 );

            // Go through and set the assignments... the "E" step
            double[] anold = new double[ K ];
            for( int n = 0; n < N; n++ )
            {
                final Vector xn = this.weightedData.get(n).getValue();
                double[] an = this.assignments.get(n);
                System.arraycopy(an, 0, anold, 0, K);
                int k = 0;
                double sum = 0.0;
                for( MultivariateGaussian.PDF pdf : this.distributions )
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

            System.out.println( this.getIteration() + ": " + this.assignmentChanged );
            if( this.assignmentChanged <= this.getTolerance() )
            {
                return false;
            }

            // Now update the distributions... the "M" step
            for( int k = 0; k < K; k++ )
            {
                for( int n = 0; n < N; n++ )
                {
                    this.weightedData.get(n).setWeight(this.assignments.get(n)[k]);
                }
                this.distributions.set( k, this.learner.learn( this.weightedData ) );

                System.out.println( "\t" + k + ": Prior = " + this.distributionPrior[k] + " Mean: " + this.distributions.get(k).getMean() );

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
        public MixtureOfGaussians.PDF getResult()
        {
            return new MixtureOfGaussians.PDF(
                this.distributions, this.distributionPrior );
        }

        @Override
        public NamedValue<Double> getPerformance()
        {
            return new DefaultNamedValue<Double>(
                PERFORMANCE_NAME, this.assignmentChanged );
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
        
     }

}
