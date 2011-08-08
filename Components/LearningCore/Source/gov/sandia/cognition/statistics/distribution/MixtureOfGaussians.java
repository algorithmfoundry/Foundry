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
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian.PDF;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;

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
    extends LinearMixtureModel<Vector, MultivariateGaussian>
    implements ProbabilityDensityFunction<Vector>,
    ClosedFormComputableDistribution<Vector>
{
    
    
    /**
     * Creates a new instance of MixtureOfGaussians
     * @param gaussians      
     * Underlying collection of MultivariateGaussian that compute the relative
     * likelihoods of a particular input. Assigns uniform prior probability
     * vector to these MultivariateGaussians
     */
    public MixtureOfGaussians(
        Collection<MultivariateGaussian.PDF> gaussians )
    {
        super( gaussians );
    }
    
    /**
     * Creates a new instance of MixtureOfGaussians
     * @param gaussians      
     * Underlying collection of MultivariateGaussian that compute the relative
     * likelihoods of a particular input, must all be same dimension
     * @param priorProbabilities
     * The prior probability distribution of the MultivariateGaussians, must
     * have 1-norm ~1.0 and dimensionality equal to the number of 
     * MultivariateGaussian
     */
    public MixtureOfGaussians(
        ArrayList<MultivariateGaussian.PDF> gaussians,
        Vector priorProbabilities )
    {
        super( gaussians, priorProbabilities );
    }

    @Override
    public MixtureOfGaussians clone()
    {
        return (MixtureOfGaussians) super.clone();
    }

    public MixtureOfGaussians getProbabilityFunction()
    {
        return this;
    }

    /**
     * Computes the likelihoods of the underlying RandomVariables in this
     * @param input Input to consider
     * @return Vector of likelihoods for the underlying RandomVariables
     */
    public Vector computeRandomVariableLikelihoods(
        Vector input )
    {
        
        int M = this.getNumRandomVariables();
        Vector likelihoods = VectorFactory.getDefault().createVector( M );
        for( int i = 0; i < M; i++ )
        {
            likelihoods.setElement( i,
                ((MultivariateGaussian.PDF) this.getRandomVariables().get(i)).evaluate( input ) );
        }
        
        return likelihoods;
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
        for( int i = 0; i < this.getNumRandomVariables(); i++ )
        {
            MultivariateGaussian Gaussian =
                (MultivariateGaussian) this.getRandomVariables().get(i);
            Vector meanDiff = Gaussian.getMean().minus( mean );
            covarianceAccumulator.accumulate( Gaussian.getCovariance().plus(
                meanDiff.outerProduct( meanDiff ) ).scale(
                    this.getPriorProbabilities().getElement(i) ) );
        }

        return new MultivariateGaussian.PDF( mean, covarianceAccumulator.getSum() );

    }

    /**
     * Computes the probability distribution that the input was generated by
     * the underlying RandomVariables
     * @param input Input to consider
     * @return probability distribution that the input was generated by
     * the underlying RandomVariables
     */
    public Vector computeRandomVariableProbabilities(
        Vector input )
    {
        int M = this.getNumRandomVariables();
        Vector likelihoods = this.computeRandomVariableLikelihoods( input );
        if( likelihoods.norm1() <= 0.0 )
        {
            for( int i = 0; i < likelihoods.getDimensionality(); i++ )
            {
                likelihoods.setElement( i, 1.0 / M );
            }
        }
        Vector priorLikelihoods = likelihoods.dotTimes( 
            this.getPriorProbabilities() );
        double denominator = priorLikelihoods.norm1();
        if( denominator <= 0.0 )
        {
            for( int i = 0; i < M; i++ )
            {
                priorLikelihoods.setElement( i, 1.0 );
            }
            denominator = priorLikelihoods.norm1();
        }
        return priorLikelihoods.scale( 1.0 / denominator );        
    }    
    
    /**
     * Gets the index of the most-likely RandomVariable, given the input. 
     * That is, find the RandomVariable that most likely generated the input
     * @param input input to consider
     * @return zero-based index of the most-likely RandomVariable
     */
    public int getMostLikelyRandomVariable(
        Vector input )
    {
        
        Vector probabilities = this.computeRandomVariableProbabilities( input );
        int bestIndex = 0;
        double bestProbability = probabilities.getElement(bestIndex);
        for( int i = 1; i < probabilities.getDimensionality(); i++ )
        {
            double prob = probabilities.getElement(i);
            if( bestProbability < prob )
            {
                bestProbability = prob;
                bestIndex = i;
            }
        }
        
        return bestIndex;        
        
    }    
    
    public Vector getMean()
    {
        RingAccumulator<Vector> mean =
            new RingAccumulator<Vector>();
        for( int i = 0; i < this.getNumRandomVariables(); i++ )
        {
            mean.accumulate( 
                this.getRandomVariables().get(i).getMean().scale( 
                    this.getPriorProbabilities().getElement(i) ) );
        }
        
        return mean.getSum();
        
    }
    
    /**
     * Gets the dimensionality of the MultivariateGaussian in the mixture
     * @return
     * Input dimensionality of the mixture
     */
    public int getDimensionality()
    {
        return CollectionUtil.getFirst( this.getRandomVariables() ).getMean().getDimensionality();
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

        Vector p = this.computeRandomVariableProbabilities(input);
        double weightedZSquared = 0.0;
        int index = 0;
        for( Distribution d : this.getRandomVariables() )
        {
            MultivariateGaussian g = (MultivariateGaussian) d;
            weightedZSquared += g.computeZSquared(input) * p.getElement(index);
            index++;
        }

        return weightedZSquared;

    }

    public Vector convertToVector()
    {
        return this.getPriorProbabilities().clone();
    }

    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertSameDimensionality(this.getPriorProbabilities());
        this.setPriorProbabilities(parameters);
    }

    public Double evaluate(
        Vector input)
    {

        double sum = 0.0;
        int index = 0;
        Vector prior = this.getPriorProbabilities();
        for( Distribution<? extends Vector>  d : this.getRandomVariables() )
        {
            MultivariateGaussian.PDF g = (MultivariateGaussian.PDF) d;
            sum += g.evaluate(input) * prior.getElement(index);
            index++;
        }

        return sum;
    }

    public double logEvaluate(
        Vector input)
    {
        return Math.log( this.evaluate(input) );
    }   

    /**
     * A hard-assignment learner for a MixtureOfGaussians
     */
    public static class Learner
        extends AnytimeAlgorithmWrapper<MixtureOfGaussians, KMeansClusterer<Vector,GaussianCluster>>
        implements DistributionEstimator<Vector,MixtureOfGaussians>,
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

        public MixtureOfGaussians getResult()
        {
            Collection<GaussianCluster> clusters = this.getAlgorithm().getResult();
            if( (clusters != null) && (clusters.size() > 0) )
            {
                ArrayList<MultivariateGaussian.PDF> gaussians =
                    new ArrayList<MultivariateGaussian.PDF>( clusters.size() );
                Vector priorProbabilities =
                    VectorFactory.getDefault().createVector( clusters.size() );
                int index = 0;
                int sum = 0;
                for( GaussianCluster cluster : clusters )
                {
                    gaussians.add( cluster.getGaussian() );
                    int num = cluster.getMembers().size();
                    sum += num;
                    priorProbabilities.setElement( index, num );
                    index++;
                }

                priorProbabilities.scaleEquals( 1.0/sum );

                return new MixtureOfGaussians( gaussians, priorProbabilities );
            }
            else
            {
                return null;
            }

        }

        public MixtureOfGaussians learn(
            Collection<? extends Vector> data)
        {
            this.getAlgorithm().learn(data);
            return this.getResult();
        }

        public NamedValue<? extends Number> getPerformance()
        {
            return this.getAlgorithm().getPerformance();
        }
    }

    /**
     * A "soft" learner for mixture of gaussians.  This learner does
     * not try to assign each data vector crisply to a gaussian.  Instead,
     * it uses a relative likelihood of the the vector coming from each
     * distribution to weight how much to use the vector in the learning of
     * each gaussian.
     * @author jdmorr
     */
     @PublicationReference(
        author="Jaakkola",
        title="Estimating mixtures: the EM-algorithm",
        type=PublicationType.Misc,
        year=2007,
        url="http://courses.csail.mit.edu/6.867/lectures/notes-em2.pdf"
     )
     public static class SoftLearner
            extends AbstractAnytimeBatchLearner<Collection<? extends Vector>, MixtureOfGaussians>
            implements DistributionEstimator<Vector,MixtureOfGaussians>,
            MeasurablePerformanceAlgorithm
         {
    // TODO: This needs to be cleaned up to match the foundry standards:
    //      - Use constants for default values of parameters.
    //      - Add getters/setters for parameters.
    //      - Remove DEBUG flag.
    //      - Make use of "this"
    //      - etc.
    // -- jdbasil (2009-09-11)
    // I tried to put the "this" pointer on all members... --krdixon, 2009-12-14
            protected int numDataVectors;
            protected int numGaussians = 2;
            protected double COVARIANCE_SCALE = 0.5;
            protected double normalizedCovarianceDistance = 0.0;
            protected double normalizedMeanDistance = 0.0;
            protected int iterationsToConverge = -1;
            protected double convergeTolerance = 1.e-4;
            protected Matrix m ;
            protected ArrayList<PDF> pdfList;
            protected Vector priorProbabilities;
            protected ArrayList<PDF> newPdfList;
            protected Vector newPriorProbabilities;
            protected MixtureOfGaussians mog = null;
            final static protected boolean DEBUG = false;

            public SoftLearner()
            {
                this(1000);
            }

            public SoftLearner(int maxIterations)
            {
                super(maxIterations);
            }

            @Override
            protected void setData( Collection<? extends Vector> data )
            {
                super.setData(data);
            }

            @Override
            /**
             * initializeAlgorithm should should generate the initial gaussians
             * and their prior probabilities from the data.
             */
            protected boolean initializeAlgorithm()
            {
                // initialize the distributions
                // compute mean and variance of overall data
                MultivariateGaussian.MaximumLikelihoodEstimator learner =
                    new MultivariateGaussian.MaximumLikelihoodEstimator();

                @SuppressWarnings("static-access")
                MultivariateGaussian.PDF avgpdf = learner.learn(this.data,1.e-5);

                Vector mean = avgpdf.getMean();
                Matrix covariance = avgpdf.getCovariance();

                this.numDataVectors = this.data.size();

                this.pdfList = new ArrayList<PDF>(this.numGaussians);

                double f = 1.0;
                for (int i = 0; i < numGaussians; i++)
                {
                    this.pdfList.add(
                            new MultivariateGaussian.PDF(
                            mean, covariance.scale(f)));

                    f *= this.COVARIANCE_SCALE;
                }

                // assume at outset that all distributions are equally likely
                this.priorProbabilities = VectorFactory.getDefault().createVector(
                    this.numGaussians, 1.0/this.numGaussians);

                return true;
            }


            @Override
            protected boolean step()
            {
                this.m = MatrixFactory.getDefault().createMatrix(
                    this.numGaussians,
                    this.numDataVectors);
                this.newPriorProbabilities =
                        VectorFactory.getDefault().createVector(this.numGaussians,1.0);

                // compute likelihood of data vector v in each gaussian
                int c = 0;
                for (Vector v : this.data)
                {
                    int r = 0;
                    for (MultivariateGaussian.PDF pdf : this.pdfList)
                    {
                        this.m.setElement(r, c, pdf.evaluate(v)*this.priorProbabilities.getElement(r));
                        r++;
                    }
                    c++;
                }

                // make each col vector sum to one
                for (c = 0; c < this.numDataVectors; c++)
                {
                    Vector col = this.m.getColumn(c);
                    this.m.setColumn(c, col.scale(1.0/col.sum()));
                }

                ArrayList<DefaultWeightedValue<? extends Vector>> wdata;
                int i = 0;
                MultivariateGaussian.WeightedMaximumLikelihoodEstimator learner =
                    new MultivariateGaussian.WeightedMaximumLikelihoodEstimator();

                this.newPdfList = new ArrayList<PDF>(this.numGaussians);

                for (i = 0; i < this.numGaussians; i++)
                {
                    // assign a weight vector according to the PDF
                    wdata = convertToWeightedData(this.m.getRow(i), this.data);

                    // learn a new PDF based on the weighted data
                    this.newPdfList.add(learner.learn(wdata));

                    // update the priorProbabilities
                    this.newPriorProbabilities.setElement(
                            i,
                            this.m.getRow(i).sum() / this.numDataVectors);
                }

                // determine whether termination condition is met
                // accesses newMOG and currentMOG to determine this...
                boolean terminateRun = checkTermination();

                this.priorProbabilities = this.newPriorProbabilities;
                this.pdfList = this.newPdfList;

                return !terminateRun;
            }

            @Override
            protected void cleanupAlgorithm()
            {

            }

            public MixtureOfGaussians getResult()
            {
                if (this.pdfList == null || this.priorProbabilities == null)
                {
                    // Bad getResult().
                    return null;
                }
                else
                {
                    return new MixtureOfGaussians(this.pdfList, this.priorProbabilities);
                }
            }

            public NamedValue<? extends Number> getPerformance()
            {
                // scales from 1 to 0, 0 being finished
                double result = 1.0;

                result = Math.max(
                        this.normalizedCovarianceDistance - this.getConvergeTolerance(),
                        this.normalizedMeanDistance = this.getConvergeTolerance());
                result = Math.min(result,1.0);

                return new DefaultNamedValue<Double>(
                    "Tolerance achieved", result );
            }

            /**
             * utility functions
             *
             *
             */
            protected ArrayList<DefaultWeightedValue<? extends Vector>> convertToWeightedData(
                Vector weights,
                Collection<? extends Vector> data)
            {
                int n = data.size();

                ArrayList<DefaultWeightedValue<? extends Vector>> weightedVectors =
                    new ArrayList<DefaultWeightedValue<? extends Vector>>(n);

                int i = 0;
                for (Vector v : data)
                {
                    weightedVectors.add(new DefaultWeightedValue<Vector>(v,
                        weights.getElement(i)));

                    // weightedVectors.set(i, new DefaultWeightedValue<Vector>(v,weights.getElement(i)));
                    i++;
                }
                return weightedVectors;
            }

            /**
             * returns true if we should terminate run, false to continue
             * note this is opposite of the return value for step.
             *
             * @return
             * True to terminate, false otherwise.
             */
            protected boolean checkTermination()
            {
                boolean result = true;

                // monitor the change in the covariance...
                for (int i = 0; i < this.numGaussians; i++)
                {
                    PDF oldPdf = this.pdfList.get(i);
                    PDF newPdf = this.newPdfList.get(i);

                    /**
                     * compute normalized distance in covariance change...
                     */
                    Vector vOld =
                        oldPdf.getCovariance().convertToVector();
                    Vector vNew =
                        newPdf.getCovariance().convertToVector();

                    this.normalizedCovarianceDistance =
                            vOld.euclideanDistance(vNew) / vOld.norm2();

                    if (this.normalizedCovarianceDistance > getConvergeTolerance())
                    {
                        result &= false;
                    }

                    /**
                     * compute normalizedDistance in mean change
                     */
                    Vector mOld = oldPdf.getMean();
                    Vector mNew = newPdf.getMean();
                    this.normalizedMeanDistance =
                            mOld.euclideanDistance(mNew) / mOld.norm2();
                    if (this.normalizedMeanDistance > getConvergeTolerance() )
                    {
                        result &= false;
                    }

                    if ( DEBUG )
                    {
                        System.out.printf(
                                "pdf %d Normalized distances: m=%.6f  cov=%.6f\n",
                                i,
                                this.normalizedMeanDistance,
                                this.normalizedCovarianceDistance);
                    }
                }

                // true indicates that it has converged...
                if ( result == true )
                {
                    this.iterationsToConverge = this.iteration;
                }

                return result;
            }

            /**
             * @return the convergeTolerance
             */
            public double getConvergeTolerance()
            {
                return convergeTolerance;
            }

            /**
             * @param convergeTolerance the convergeTolerance to set
             */
            public void setConvergeTolerance(double convergeTolerance)
            {
                this.convergeTolerance = convergeTolerance;
            }

            /**
             * @return the numPDFs
             */
            public int getNumGaussians()
            {
                return numGaussians;
            }

            /**
             * @param numPDFs the numPDFs to set
             */
            public void setNumGaussians(int numPDFs)
            {
                this.numGaussians = numPDFs;
            }
        }

}
