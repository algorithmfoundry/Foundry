/*
 * File:                ScalarMixtureModel.java
 * Authors:             jdmorr
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

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.function.kernel.GeneralizedScalarRadialBasisKernel;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.KernelContainer;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormSmoothScalarDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.ScalarDistribution;
import gov.sandia.cognition.statistics.ScalarProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.SmoothScalarDistribution;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Randomized;
import gov.sandia.cognition.util.WeightedValue;
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
 *
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
    extends AbstractClosedFormSmoothScalarDistribution
{

    /**
     * default set of distributions
     */
    protected final static Collection<? extends SmoothScalarDistribution>
            DEFAULT_DISTRIBUTIONS =
            Arrays.asList(new UnivariateGaussian(0.0,1.0),
                          new UnivariateGaussian(5.0,1.0));


    /**
     * List of the distributions.
     */
    protected ArrayList<? extends SmoothScalarDistribution> distributions = null;

    /**
     * probability for each of the distributions
     */
    protected Vector priorProbabilities = null ;

    /**
     * Default constructor.
     * Builds two UnivariateGaussian, means 0 and 5 with variance 1.0
     * priorProbabilities is 0.5 and 0.5 for each gaussian.
     */
    public ScalarMixtureDensityModel()
    {
        this( DEFAULT_DISTRIBUTIONS );
    }

    /**
     * Creates a new instance of ScalarMixtureDensityModel
     * @param distributions
     * List of the distributions.
     */
    public ScalarMixtureDensityModel(
        Collection<? extends SmoothScalarDistribution> distributions)
    {
        this(new ArrayList<SmoothScalarDistribution>(distributions), null );
    }


    /**
     * Creates a new instance of ScalarMixtureDensityModel.
     * <BR><BR>
     * @param distributions
     * List of the distributions.
     * @param priorProbabilities
     * probability associated with each distribution; note if this is null
     * then the prior probabilities are computed as 1/n for each of n
     * distributions.
     */
    public ScalarMixtureDensityModel(
        ArrayList<? extends SmoothScalarDistribution> distributions,
        Vector priorProbabilities)
    {
        int n = distributions.size();

        if ( n <= 0 )
        {
            throw new IllegalArgumentException(
                "dimensionality problem in input args");
        }

        if ( priorProbabilities == null )
        {
            priorProbabilities = VectorFactory.getDefault().createVector(
                    n, 1.0/n);
        }

        if ( priorProbabilities.getDimensionality() != n )
        {
            throw new IllegalArgumentException(
                "probabilities size different from distributions");
        }

        if ( Math.abs(priorProbabilities.sum()-1.0) >1.e-4 )
        {
            throw new RuntimeException("probabilities do not sum to 1.0");
        }

        this.setDistributions(distributions);
        this.setPriorProbabilities(priorProbabilities);
    }

    /**
     * Copy constructor
     * @param mixtureModel
     * ScalarMixtureDensityModel to copy
     */
    public ScalarMixtureDensityModel(
        ScalarMixtureDensityModel mixtureModel)
    {
        ScalarMixtureDensityModel clonedModel = mixtureModel.clone();
        this.distributions = clonedModel.distributions;
        this.priorProbabilities = clonedModel.priorProbabilities;
    }


    /**
     * this clones an SMDM creating a new set of both prior probabilities and
     * distributions that exactly match the original parameter-wise.
     * @return a ScalarMixtureDensityModel
     */
    @Override
    public ScalarMixtureDensityModel clone()
    {
        ScalarMixtureDensityModel clone =
            (ScalarMixtureDensityModel) super.clone();

        clone.setDistributions(
            ObjectUtil.cloneSmartElementsAsArrayList(this.distributions));

        clone.setPriorProbabilities(
            ObjectUtil.cloneSafe(this.priorProbabilities));

        return clone;
    }

    /**
     * Computes the likelihoods of the underlying RandomVariables in this
     *
     * @param input Input to consider
     * @return Vector of likelihoods for the underlying RandomVariables
     */
    public Vector computeRandomVariableLikelihoods(
        Double input)
    {

        int M = this.getNumDistributions();
        Vector likelihoods = VectorFactory.getDefault().createVector(M);

        for (int i = 0; i < M; i++)
        {
            // remember that distributions are scalar PDFs
            likelihoods.setElement(i,
                this.getDistributions().get(i).getProbabilityFunction().evaluate(input));
        }

        return likelihoods;
    }

    public Double getMean()
    {
        return getMeanVector().dotProduct(this.getPriorProbabilities());
    }

    /**
     * getMeanVector returns a vector of the mean of each random variable
     * in the mixture.
     *
     * Use getMean() to get the weighted mean of the mixture.
     *
     * @return
     * a vector of the mean of each random variable in the mixture.
     */
    public Vector getMeanVector()
    {
        int N = this.getNumDistributions();
        Vector pp = VectorFactory.getDefault().createVector(N, 0.0);
        ArrayList<? extends SmoothScalarDistribution> randomVars =
            this.getDistributions();
        for (int i = 0; i < N; i++)
        {
            pp.setElement(i, randomVars.get(i).getMean().doubleValue());
        }

        return pp;
    }

    /**
     * computes the variance of a mixture density.
     * see Publication reference for algorithm.
     * 
     * @return
     * variance of a mixture density.
     */
    @PublicationReference(
        author = "Wikipedia",
        title = "Mixture Model",
        type = PublicationType.WebPage,
        year = 2009,
        url = "http://en.wikipedia.org/wiki/Mixture_density"
    )
    public Double computeVariance()
    {
        Double result = 0.0;
        Double mean = this.getMean();

        priorProbabilities.assertDimensionalityEquals(this.distributions.size());

        int i = 0;
        for (ScalarDistribution<Double> pdf : this.distributions)
        {
            result += this.priorProbabilities.getElement(i) *
                pdf.getVariance() *
                Math.pow((pdf.getMean() - mean + 1), 2.0);
            i++;
        }

        return result;
    }


    /**
     * computes and returns the square of the weighted Z value.
     * The square is of interest because the sum of a square of
     * normally distributed values yields a Chi-Squared distribution
     *
     * @param input
     * @return the square of weighted Z
     */
    public Double computeWeightedZsquared(
        Double input)
    {
        Double result = computeWeightedZ(input);

        return result*result;
    }

    /**
     * computes the weighted Z over all distributions
     * each distribution Z is computed as abs(input-mean)/stdDev
     * each is weighted by priorProbability
     *
     * @param input
     * @return the weighted Z of the input value
     */
    public Double computeWeightedZ(
        Double input)
    {
        Double result = 0.0;

        int i =0;
        for( SmoothScalarDistribution d : this.getDistributions() )
        {
            result += this.priorProbabilities.getElement(i) *
                    (input-d.getMean())/Math.sqrt(d.getVariance());
            
            i++;
        }
        return result;
    }

    /**
     * returns an ArrayList of numDraws samples from the
     * ScalarMixtureDensityModel distribution
     * <BR>
     * it randomly selects one of the distributions according to the
     * priorProbabilities, then it draws a sample from that distribution.
     * <BR>
     * both steps are repeated if numDraws is greater than 1
     *
     * @param random random number generator
     * @param numDraws number of samples to return
     * @return
     * ArrayList of numDraws samples from the ScalarMixtureDensityModel
     * distribution.
     */
    public ArrayList<Double> sample(
        Random random,
        int numDraws)
    {
        if ( this.getNumDistributions() <= 0 )
        {
            throw new IllegalArgumentException(
                    "Cannot sample from ScalarMixtureDensityModel with no" +
                    " distributions!");
        }
        
        ArrayList<Double> randomVectors =
            new ArrayList<Double>(numDraws);

        for (int n = 0; n < numDraws; n++)
        {
            // Make a single draw from the RandomVariable indicated by
            // the prior probability
            double prior = random.nextDouble();
            int index = 0;
            for (int i = 0; i < this.getNumDistributions(); i++)
            {
                prior -= this.getPriorProbabilities().getElement(i);
                if (prior <= 0.0)
                {
                    index = i;
                    break;
                }
            }
            randomVectors.add(this.getDistributions().get(index).sample(random));
        }

        return randomVectors;

    }

    /**
     * Getter for distributions
     * @return the distributions
     */
    public ArrayList<? extends SmoothScalarDistribution> getDistributions()
    {
        return this.distributions;
    }

    /**
     * Setter for distributions
     * @param distributions the distributions to set
     */
    public void setDistributions(
        ArrayList<? extends SmoothScalarDistribution> distributions)
    {
        this.distributions = distributions;
    }

    /**
     * Getter for priorProbabilities
     * @return the priorProbabilities
     */
    public Vector getPriorProbabilities()
    {
        return this.priorProbabilities;
    }

    /**
     * Setter for priorProbabilities
     * @param priorProbabilities the priorProbabilities to set
     */
    public void setPriorProbabilities(
        Vector priorProbabilities)
    {
        if (Math.abs(priorProbabilities.sum() - 1.0) > 1.e-4)
        {
            throw new IllegalArgumentException(
                "priorProbabilities must sum to 1.0 (" + priorProbabilities.sum() + ")");
        }

        this.priorProbabilities = priorProbabilities;
    }

    /**
     * Gets the number of distribution
     * @return the number of distributions in the mixture
     */
    public int getNumDistributions()
    {
        int n ;
        if ( this.distributions == null )
        {
            n = 0;
        }
        else
        {
            n = this.distributions.size();
        }

        return n;
    }

    /**
     * returns the variance of the distribution
     * @return variance
     * the variance of the distribution.
     */
    public double getVariance()
    {
        return this.computeVariance().doubleValue();
    }

    /**
     * converts distribution parameters into a vector
     * in this case, parameters are priorProbabilities
     * @return priorProbabilities as vector
     */
    public Vector convertToVector()
    {
        return this.getPriorProbabilities().clone();
    }

    /**
     * sets priorProbabilities according to incoming vector
     * note that vector must sum to 1.0
     *
     * @param parameters (priorProbabilities)
     */
    public void convertFromVector(
        Vector parameters)
    {
        parameters.assertDimensionalityEquals(
            this.priorProbabilities.getDimensionality());

        Vector pnorm = parameters.scale( 1.0/parameters.norm1() );
        setPriorProbabilities(pnorm);
    }

    /**
     * returns the CDF of ScalarMixtureDensityModel
     * @return the CDF
     */
    public ScalarMixtureDensityModel.CDF getCDF()
    {
        return new ScalarMixtureDensityModel.CDF(this);
    }

    /**
     * Gets the PDF.
     * @return returns PDF of ScalarMixtureDensityModel
     */
    public ScalarMixtureDensityModel.PDF getProbabilityFunction()
    {
        return new ScalarMixtureDensityModel.PDF(this);
    }

    /**
     * converts ScalarMixtureDensityModel to a string,
     * displays the priorProbabilities and each individual distribution
     * distributions are displayed according to their toString methods.
     */
    @Override
    public String toString()
    {
        String results;

        results = "priorProbabilities: " + this.priorProbabilities + "\n";

        results += "Distributions:\n";
        for (SmoothScalarDistribution d : getDistributions())
        {
            results += "\t" + d + "\n";
        }
        return results;
    }

    /**
     * builds a MixtureDensityModel from two gaussians whose parameters are
     * passed in.
     * @param mean  array of means of the gaussians
     * @param var   array of variances of the gaussians
     * @param priorProbabilities
     * @return a ScalarMixtureDensityModel build of these gaussians
     */
    public static ScalarMixtureDensityModel buildSMDMfromGaussians(
        double mean[],
        double var[],
        double priorProbabilities[])
    {

        if( (mean.length != var.length) ||
            (mean.length != priorProbabilities.length ))
        {
            throw new IllegalArgumentException(
                "Mismatch in argument lengths.");
        }
        int numberDistributions = mean.length;

        // build a mixture density model via construction
        ArrayList<SmoothScalarDistribution> distributionList =
            new ArrayList<SmoothScalarDistribution>(numberDistributions);
        for (int i = 0; i < numberDistributions; i++)
        {
            distributionList.add(new UnivariateGaussian(mean[i], var[i]));
        }

        ScalarMixtureDensityModel smdm = new ScalarMixtureDensityModel(
            distributionList);
        Vector pp = VectorFactory.getDefault().copyArray(priorProbabilities);

        smdm.setPriorProbabilities(pp);

        return smdm;
    }

    /**
     * builds a ScalarMixtureDensityModel from a Gaussian and a Laplace
     * distribution.
     * 
     * @param gaussian
     * UnivariateGaussian to use.
     * @param laplace
     * LaplaceDistribution to use.
     * @param priorProbabilities
     * Prior probabilities to assign to the gaussian and laplace.
     * @return
     * ScalarMixtureDensityModel represented by the parameters.
     */
    public static ScalarMixtureDensityModel buildSMDMfromGaussianAndLaplace(
        UnivariateGaussian gaussian,
        LaplaceDistribution laplace,
        double priorProbabilities[])
    {
        ArrayList<SmoothScalarDistribution> distributionList =
            new ArrayList<SmoothScalarDistribution>(2);
        distributionList.add(gaussian);
        distributionList.add(laplace);
        Vector pp = VectorFactory.getDefault().copyArray(priorProbabilities);
        return new ScalarMixtureDensityModel( distributionList, pp );
    }

    public Double getMinSupport()
    {
        return Double.NEGATIVE_INFINITY;
    }

    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * cumulative density function for the mixture density
     */
    public static class CDF
        extends ScalarMixtureDensityModel
        implements SmoothCumulativeDistributionFunction
    {

        /**
         * Default constructor.
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of CDF.
         * @param distributions
         * List of the distributions.
         */
        public CDF(
            Collection<? extends SmoothScalarDistribution> distributions)
        {
            super(distributions);
        }

        /**
         * Creates a new instance of CDF.
         * @param distributions
         * List of the distributions.
         * @param priorProbabilities
         * probability associated with each distribution.
         */
        public CDF(
            ArrayList<? extends SmoothScalarDistribution> distributions,
            Vector priorProbabilities)
        {
            super(distributions, priorProbabilities);
        }

        /**
         * Copy constructor.
         * @param mixtureModel
         * ScalarMixtureDensityModel to copy.
         */
        public CDF(
            ScalarMixtureDensityModel mixtureModel)
        {
            super(mixtureModel);
        }

        /**
         * getCDFvector returns a vector of CDF values for input
         * (one for each distribution)
         *
         * @param input
         * Value about which to get the CDF values for each of the components.
         * @return
         * vector of CDF values for input (one for each distribution).
         */
        public Vector getCDFvector(
            Double input)
        {
            int N = getNumDistributions();
            Vector results = VectorFactory.getDefault().createVector(N);

            for (int i = 0; i < N; i++)
            {
                results.setElement(i,
                    getDistributions().get(i).getCDF().evaluate(input));
            }

            return results;
        }

        public Double evaluate(
            Double input)
        {
            return this.evaluate(input.doubleValue());
        }

        public double evaluate(
            double input)
        {
            return getCDFvector(input).dotProduct(getPriorProbabilities());
        }

        @Override
        public ScalarMixtureDensityModel.CDF getCDF()
        {
            return this;
        }

        public ScalarMixtureDensityModel.PDF getDerivative()
        {
            return this.getProbabilityFunction();
        }

        public Double differentiate(
            Double input)
        {
            return this.getDerivative().evaluate(input);
        }

    }

    /**
     * PDF of the mixture density distribution
     */
    public static class PDF
        extends ScalarMixtureDensityModel
        implements ScalarProbabilityDensityFunction
    {

        /**
         * Default constructor.
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of PDF.
         * @param distributions
         * List of the distributions.
         */
        public PDF(
            Collection<? extends SmoothScalarDistribution> distributions)
        {
            super(distributions);
        }

        /**
         * Creates a new instance of PDF.
         * @param distributions
         * List of the distributions.
         * @param priorProbabilities
         * probability associated with each distribution.
         */
        public PDF(
            ArrayList<? extends SmoothScalarDistribution> distributions,
            Vector priorProbabilities)
        {
            super(distributions, priorProbabilities);
        }

        /**
         * Copy constructor.
         * @param mixtureModel
         * SMDM to copy.
         */
        public PDF(
            ScalarMixtureDensityModel mixtureModel)
        {
            super(mixtureModel);
        }

        public Double evaluate(
            Double input)
        {
            return this.evaluate(input.doubleValue());
        }

        public double evaluate(
            double input)
        {
            // get the likelihoods of input for each distribution
            Vector likelihoods = this.computeRandomVariableLikelihoods(input);

            return likelihoods.dotProduct(getPriorProbabilities());
        }

        public double logEvaluate(
            Double input)
        {
            return Math.log( this.evaluate(input) );
        }

        @Override
        public ScalarMixtureDensityModel.PDF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * a SoftLearner that can learn a ScalarMixtureDensityModel from data.
     * <BR><BR>
     * the basic approach is to use all data to learn each distribution, but
     * weight that data according to its distance from an anchor point assigned
     * to the distribution.  The anchor point is randomly assigned in the
     * learning initialization routine.
     * <BR><BR>
     * A SMDM learner is made up of a series of learners, one per distribution
     * in the SMDM.  The weighting associated with each data point varies
     * for each distribution according to an anchor point assigned to that
     * distribution.
     *
     */
    public static class SoftLearner
        extends AbstractAnytimeBatchLearner<Collection<? extends Double>, ScalarMixtureDensityModel>
        implements MeasurablePerformanceAlgorithm,
        Randomized,
        DistributionEstimator<Double,ScalarMixtureDensityModel>,
        KernelContainer<Double>
    {
        /**
         * Flag to print out debugging information.
         */
        protected static boolean DEBUG = false;

        /**
         * Default max iterations, {@value}.
         */
        protected final static int DEFAULT_MAX_ITERATIONS = 1000;

        /**
         * Default kernel, GeneralizedScalarRadialBasisKernel.
         */
        protected final static GeneralizedScalarRadialBasisKernel DEFAULT_KERNEL =
            new GeneralizedScalarRadialBasisKernel(1.0, 1.0);


        /**
         * Number of data points.
         */
        protected int numDataPoints;

        /**
         * Number of distributions.
         */
        protected int numDistributions = 2;

        /**
         * Tolerance for convergence.
         */
        protected double convergeTolerance = 1.e-6;

        /**
         * Weighted data for the learners.
         */
        protected ArrayList<? extends WeightedValue<? extends Double>> wdata;

        /**
         * Weighting matrix.
         */
        protected Matrix m = null;

        /** The performance of the algorithm. Calculated at the end of each iteration. */
        protected double performance;

        /**
         * the old performance data used in computePerformance
         */
        protected PerformanceData oldPerformanceData;

        /**
         * the latest performance data used in computePerformance
         */
        protected PerformanceData performanceData;

        /**
         * Distributions in the mixture.
         */
        protected ArrayList<SmoothScalarDistribution> distributionList;

        /**
         * Prior probabilities of the various distributions.
         */
        protected Vector priorProbabilities;

        /**
         * Random number generator.
         */
        protected Random random;

        /**
         * kernel function that takes two Doubles and computes a double
         * used to compute the weight on a point from an anchor point.
         */
        protected Kernel<Double> kernel;

        /**
         * Collection of learners used to create each component.
         */
        Collection<BatchLearner<Collection<? extends WeightedValue<? extends Double>>, ? extends SmoothScalarDistribution>> learners;

        /**
         * Default constructor.
         */
        public SoftLearner()
        {
            this(DEFAULT_MAX_ITERATIONS);
        }

        /**
         * Creates a new instance of SoftLearner.
         * @param learners
         * Collection of learners used to create each component.
         */
        public SoftLearner(
            Collection<BatchLearner<Collection<? extends WeightedValue<? extends Double>>, ? extends SmoothScalarDistribution>> learners)
        {
            this(DEFAULT_MAX_ITERATIONS);
            setLearners(learners);
        }

        /**
         * Creates a new instance of SoftLearner.
         * @param maxIterations
         * Maximum number of iterations before stopping.
         */
        public SoftLearner(
            int maxIterations)
        {
            super(maxIterations);
            setRandom(new Random());
            setKernel(DEFAULT_KERNEL);
        }

        /**
         * Sets the learners: one for each distribution.
         * Note that each learner must provide a Distribution which has a
         * getProbabilityFunction method!
         *
         * @param learners
         * Collection of learners used to create each component.
         */
        protected void setLearners(
            Collection<BatchLearner<Collection<? extends WeightedValue<? extends Double>>, ? extends SmoothScalarDistribution>> learners)
        {
            this.learners = learners;
            this.numDistributions = learners.size();
            this.distributionList =
                new ArrayList<SmoothScalarDistribution>(this.numDistributions);
        }

        /**
         * builds a ScalarMixtureDensityModel consisting of n Gaussians.
         * @param n number of Gaussians to base model on.
         * @return
         * SoftLearner with UnivariateGaussians.
         */
        public static ScalarMixtureDensityModel.SoftLearner buildSMDMmultiGaussianLearner(
            int n)
        {
            ArrayList<BatchLearner<Collection<? extends WeightedValue<? extends Double>>, ? extends SmoothScalarDistribution>> learners =
                new ArrayList<BatchLearner<Collection<? extends WeightedValue<? extends Double>>, ? extends SmoothScalarDistribution>>(n);

            for (int i = 0; i < n; i++)
            {
                learners.add(
                    new UnivariateGaussian.WeightedMaximumLikelihoodEstimator(0));
            }

            ScalarMixtureDensityModel.SoftLearner SMDMlearner =
                new ScalarMixtureDensityModel.SoftLearner(learners);


            return SMDMlearner;
        }

        /**
         * builds a ScalarMixtureDensityModel with two distribution learners:
         * one gaussian and one laplace.
         *
         * @return ScalarMixtureDensityModel with two distributions:
         *         1 gaussian, 1 laplace.
         */
        public static ScalarMixtureDensityModel.SoftLearner buildSmdmGaussianLaplaceLearner()
        {
            ArrayList<BatchLearner<Collection<? extends WeightedValue<? extends Double>>, ? extends SmoothScalarDistribution>> learners =
                new ArrayList<BatchLearner<Collection<? extends WeightedValue<? extends Double>>, ? extends SmoothScalarDistribution>>(2);

            learners.add(new UnivariateGaussian.WeightedMaximumLikelihoodEstimator(0));
            learners.add(
                new LaplaceDistribution.WeightedMaximumLikelihoodEstimator());

            ScalarMixtureDensityModel.SoftLearner smdmLearner =
                new ScalarMixtureDensityModel.SoftLearner();

            smdmLearner.setLearners(learners);

            return smdmLearner;
        }

        /**
         * selects numberLearners anchor points from the data
         * this is used during initialization to provide an anchor
         * for each distribution which is used with a scaling kernel
         * to compute initial weights.
         * <BR><BR>
         *  1) select k anchor points, where k is number of learners
         * each point MUST BE unique -- so this is sampling
         *          w/o replacement
         * <BR>
         * 2) compute the weight vector for each point according to
         *      a kernel function centered at this point
         * <BR>
         * 3) note that this set of weights will not have the convex
         *      property that later updates based on distributions will.
         *
         * @param numberLearners the number of learners (or distributions)
         * @return an array of anchor point values
         */
        protected Double[] getAnchorPoints(
            int numberLearners )
        {
            Double[] anchors = new Double[numberLearners];

            Integer[] anchorIndexes = new Integer[numberLearners];


            // selecting unique anchor points cannot be done if
            // there are more learners than datapoints.
            if ( numberLearners > data.size() )
            {
                throw new IllegalArgumentException(
                        "More learners than data points");

            }

            // use simple, brute-force algorithm since we expect WAY more
            // data points than distributions.  So we'll randomly pick an index
            // and then just check to make sure that index has not already been
            // picked.
            //
            // the index should be in range of 0 to n-1, where n is # of datapoints
            int i = 0;
            boolean notdone = true;
            boolean redo = false;
            if (DEBUG)
            {
                System.out.print("anchor indexes = ");
            }
            while (notdone)
            {
                anchorIndexes[i] = this.random.nextInt(data.size());

                // check for duplicate indexes so far...
                for (int k = 0; k < i; k++)
                {
                    if (anchorIndexes[i] == anchorIndexes[k])
                    {
                        redo = true;  // found a duplicate index!
                        break;
                    }
                }

                // if redo is true, then we generated a duplicate index for
                // slot i.  So re-do it!
                if (redo)
                {
                    redo = false;
                    continue;
                }

                if (DEBUG)
                {
                    System.out.print(" " + anchorIndexes[i]);
                }

                i++;
                if (i >= numberLearners)
                {
                    notdone = false;
                }

            }
            if (DEBUG)
            {
                System.out.println();
            }



            if (DEBUG)
            {
                System.out.print("anchor pts = ");
            }
            for (i = 0; i < numberLearners; i++)
            {
                anchors[i] = CollectionUtil.getElement(data, anchorIndexes[i]);
                if (DEBUG)
                {
                    System.out.println(" " + anchors[i]);
                }
            }
            if (DEBUG)
            {
                System.out.println();
            }

            return anchors;
        }


        /**
         * computeWeightsFromAnchors computes the weight matrix (m) from
         * anchor points assigned to each distribution (learner).  This is
         * only called once during initialization since we don't yet have
         * any distribution parameters defined.
         *
         * @param anchors  array of doubles, n long that are the anchor
         *                    points associated with each distribution.
         * @param data     n-dimensional array of doubles representing the
         *                    scalar data points.
         * @return         a matrix with the weights in it
         *                    # data pts columns, # distributions rows
         */
        protected Matrix computeWeightsfromAnchors(
            Double[] anchors,
            Collection<? extends Double> data )
        {
            Matrix weightMatrix;

            int numDist = anchors.length;
            int numPts = data.size();

            weightMatrix = MatrixFactory.getDefault().createMatrix(
                numDist,
                numPts);

            int c = 0;
            for (Double v : data)
            {
                for (int r = 0; r < numDist; r++)
                {
                    weightMatrix.setElement(r, c, this.kernel.evaluate(anchors[r], v));
                }
                c++;
            }

            // normalize each column's sum to 1
            for(c=0; c<weightMatrix.getNumColumns(); c++ )
            {
                Vector col = weightMatrix.getColumn(c);
                weightMatrix.setColumn(c, col.scale(1.0 / col.sum()));
            }

            int i;
            if (DEBUG)
            {
                System.out.println("weights");
                for (i = 0; i < numDist; i++)
                {
                    System.out.println("\twts " + i + " = " + weightMatrix.getRow(i));
                }
            }

            return weightMatrix;
        }


        /**
         * this computes the weight matrix (m) from the distribution PDFs
         * the weight matrix is # distributions (rows) x # datapoints (cols).
         * <BR><BR>
         * each element starts as the PDF value of the data point multiplied by
         * the probability of that distribution
         * <BR><BR>
         * then the matrix elements are scaled so that each col sums to 1.0
         *
         * @param distributionList ArrayList of SmoothScalarDistributions
         *        A SmoothScalarDistribution has a getProbabilityFunction() method
         * @param priorProbabilities vector of probabilities for each distribution
         *        this vector should sum to 1.0
         * @param data collection of Double values representing the data
         *        that the mixture density is trying to model
         * @return  a matrix of weight values
         */
        protected Matrix computeWeightsFromPDFs(
                ArrayList<SmoothScalarDistribution> distributionList,
                Vector priorProbabilities,
                Collection<? extends Double> data )
        {
            Matrix weightMatrix;

            int numDist = distributionList.size();
            int numPts = data.size();

            weightMatrix = MatrixFactory.getDefault().createMatrix(
                numDist,
                numPts);

            int c = 0;
            for (Double v : data)
            {
                int r = 0;
                for (SmoothScalarDistribution dist : distributionList)
                {
                    weightMatrix.setElement(r, c, dist.getProbabilityFunction().evaluate(v) *
                        priorProbabilities.getElement(r));
                    r++;
                }
                c++;
            }

            // make each col vector sum to one
            for (c = 0; c < numPts; c++)
            {
                Vector col = weightMatrix.getColumn(c);
                weightMatrix.setColumn(c, col.scale(1.0 / col.sum()));
            }


            return weightMatrix;
        }


        /**
         * initializeAlgorithm initializes both the distributionList and the
         * priorProbabilities from the data.
         *
         * @return true if it initialized properly, false if not
         */
        @Override
        protected boolean initializeAlgorithm()
        {
            int numberLearners = this.learners.size();
            this.numDistributions = numberLearners;
            this.numDataPoints = this.data.size();
            this.performance = 0.0;

            Double [] anchors = getAnchorPoints(numberLearners);

            
            // ok, so now we have an anchor point for each learner
            // (no guarantee it is a particularly good anchor point
            // for that learner)
            //
            // now we need to generate a weight vector based on
            // that anchor point for each distribution
            // (or compute the 'm' matrix)
            this.m = computeWeightsfromAnchors(anchors, this.data);

            this.priorProbabilities = VectorFactory.getDefault().createVector(
                numberLearners, 1.0 / numberLearners);

            updateDistributionsAndProbabilities();

            if (DEBUG)
            {
                System.out.println("initializeAlgorithm: ");
                System.out.println("priorProbabilities: " + this.priorProbabilities );
                for (SmoothScalarDistribution d : this.distributionList)
                {
                    System.out.println("\tdistribution is " + d);
                }
            }

            this.performanceData = new PerformanceData(
                    this.distributionList,
                    this.priorProbabilities);
            
            // note that this will be updated before the
            // first comparison is computed
            oldPerformanceData = new PerformanceData();

            return true;
        }


        /**
         *   this method updates the distributionList and priorProbabilities
         *   class variables on the basis of data and the content of the
         *   weight matrix, m.
         * <BR><BR>
         *   if the # of data points is N and the number of distributions is M
         * <BR><BR>
         *   then distributionList has M distributions,
         *   data has N points,
         *   m is M x N, where each column sums to 1.0,
         *   wdata is N points.
         *
         */
        protected void updateDistributionsAndProbabilities()
        {
            this.distributionList.clear();
            int i =0;
            for( BatchLearner<Collection<? extends WeightedValue<? extends Double>>, ? extends SmoothScalarDistribution> learner : this.learners )
            {
                this.wdata = convertToWeightedData(this.m.getRow(i), this.data);
                this.distributionList.add( learner.learn(this.wdata));
                this.priorProbabilities.setElement(i,
                        this.m.getRow(i).sum() / this.numDataPoints);
                ++i;
            }
        }

        /**
         * computes a single step of the learning algorithm.
         * <BR><BR>
         * It updates the distributionList and priorProbabilities variables
         * these are used by getResult to construct and return a
         * ScalarMixtureDensityModel
         * <BR><BR>
         * first, the weight matrix is computed from the current distributions
         * and probabilities then the distributions and probabilities are
         * updated based on the new weight matrix and the original data.
         *
         * @return true to continue to learn, false to stop learning
         */
        @Override
        protected boolean step()
        {
            oldPerformanceData.updateData(performanceData);

            this.m = computeWeightsFromPDFs(
                    this.distributionList,
                    this.priorProbabilities,
                    this.data);

            updateDistributionsAndProbabilities();

            performanceData.updateData(
                    this.distributionList,
                    this.priorProbabilities);

            // Compute the performance.
            this.performance =
                this.performanceData.computePerformanceMetric(oldPerformanceData);

            // determine whether termination condition is met
            final boolean continueLearning =
                (this.performance > this.convergeTolerance);

            if (DEBUG)
            {
                System.out.println("step #" + this.getIteration() + "\n" + this.getResult() );
                System.out.println("\tperformance=" + this.performance);
            }

            return continueLearning;
        }

        @Override
        protected void cleanupAlgorithm()
        {
        }

        /**
         * computes the learning algorithm performance.
         * <BR><BR>
         * this value that begins at 1.0 and decreases toward 0
         * right now it monitors the contents of the m matrix which consists
         * of relative weights of data vs. distribution
         * <BR><BR>
         * when this matrix stops changing, then the distributions and
         * probabilities will also stop changing.
         * <BR><BR>
         * the norm of the matrix difference divided by the norm of the matrix
         * is used to assess the change in the matrix
         *
         * @return  a double performance value to trends to 0 as the algorithm
         *          converges
         */
        public NamedValue<? extends Number> getPerformance()
        {
            if (this.performanceData == null)
            {
                return null;
            }
            else
            {
//            if ( this.old_m != null )
//            {
//                this.old_m.minusEquals(this.m);
//                result = this.old_m.normFrobenius() / this.m.normFrobenius();
//            }

                return new DefaultNamedValue<Double>(
                    "Normalized weight change", this.performance);
            }
        }

        public ScalarMixtureDensityModel getResult()
        {
            return new ScalarMixtureDensityModel(
                this.distributionList,
                this.priorProbabilities);
        }

        /*
         * utility functions
         * 
         */

        /**
         * converts a Collection of numbers to an ArrayList of weightedValues
         * @param weights Vector of weight values
         * @param data Collection of numbers
         * @return returns an ArrayList of WeightedValues
         */
        protected ArrayList<DefaultWeightedValue<Double>> convertToWeightedData(
            Vector weights,
            Collection<? extends Number> data)
        {
            int n = data.size();

            if( n != weights.getDimensionality() )
            {
                throw new IllegalArgumentException(
                    "Number of data must equal the number of weights.");
            }

            ArrayList<DefaultWeightedValue<Double>> weightedData =
                new ArrayList<DefaultWeightedValue<Double>>(n);

            int i = 0;
            for (Number num : data)
            {
                Double v = num.doubleValue();
                weightedData.add(new DefaultWeightedValue<Double>(v,
                    weights.getElement(i)));

                i++;
            }
            return weightedData;
        }


        public Random getRandom()
        {
            return this.random;
        }

        public void setRandom(
            Random random)
        {
            this.random = random;
        }

        public Kernel<Double> getKernel()
        {
            return this.kernel;
        }

        /**
         * Setter for kernel.
         * @param kernel the Kernel to set
         */
        public void setKernel(
            Kernel<Double> kernel)
        {
            this.kernel = kernel;
        }

        /**
         * expose setData method from BatchAnytimeLearner.
         * @param data
         * Collection of Double for SoftLearner
         */
        @Override
        protected void setData(
            Collection<? extends Double> data )
        {
            super.setData(data);

        }

    }

    /**
     * performanceInfo is a class designed to capture important
     * performance information to be used to compute the
     * performance metric.
     */
    protected static class PerformanceData
    {
        /**
         * Vector of mean values of the distributions.
         */
        protected Vector means=null;

        /**
         * Vector of variance value of the distributions.
         */
        protected Vector variances=null;

        /**
         * Vector prior probabilities of the distributions.
         */
        protected Vector priorProbabilities=null;

        /**
         * flag indicating whether the performance data is valid
         * to compute a metric with.  Set by updateData.
         */
        protected boolean validForMetricComputation = false;

        /**
         * performanceInfo constructor.
         *
         * @param dists list of SmoothScalarDistributions
         * @param pp vector of priorProbabilities
         */
        public  PerformanceData(
                ArrayList<SmoothScalarDistribution> dists,
                Vector pp )
        {
            updateData(dists,pp);
        }

        /**
         * default constructor
         */
        public PerformanceData() {};

        /**
         * whether valid for metric computation or not.  Is checked when
         * computePerformanceMetric is called.  Is set in updateData() which
         * is also called in a non-default constructor.
         *
         * @return true if ok to compute, false otherwise
         */
        public boolean isValid()
        {
            return this.validForMetricComputation;
        }

        /**
         * Computes a performance metric based on the data elements stored.
         * Compares this element's data to the oldData passed in.
         *
         * If they are identical, the performance metric should be 0.0
         * Closer to zero is closer to convergence for the learning
         * algorithm.
         *
         * @param oldData
         * @return a measure of convergence (0.0 being converged)
         */
        public double computePerformanceMetric(  PerformanceData oldData )
        {
            if ( !( this.isValid() && oldData.isValid())  )
            {
                throw new IllegalArgumentException(
                        "performance data is invalid");
            }

            double meanChg = this.getMeans().euclideanDistance(oldData.getMeans()) /
                    this.getMeans().norm2();

            double varianceChg = this.getVariances().euclideanDistance(
                    oldData.getVariances()) / this.getVariances().norm2();

            double probChg = this.getPriorProbabilities().euclideanDistance(
                oldData.getPriorProbabilities()) / this.getPriorProbabilities().norm2() ;

            return Math.max(probChg, Math.max( varianceChg, meanChg));
        }

        /**
         * @return the means
         */
        public Vector getMeans()
        {
            return means;
        }


        /**
         * updateData saves the parameter values in arguments in this object.
         *
         * @param dists SmoothScalarDistribution list
         * @param pp  vector of probabilities
         */
        public void updateData(
            ArrayList<SmoothScalarDistribution> dists,
            Vector pp )
        {
           /**
             * n is the number of distributions.
             */
            int n = dists.size();

            if ( n != pp.getDimensionality() )
            {
                throw new IllegalArgumentException(
                 "Mismatch in prior probability and distributions dimensions");
            }

            this.means = VectorFactory.getDefault().createVector(n);
            this.variances = VectorFactory.getDefault().createVector(n);
            this.priorProbabilities = pp.clone();

            for(int i = 0; i<n; i++ )
            {
                means.setElement(i,dists.get(i).getMean());
                variances.setElement(i, dists.get(i).getVariance());
            }

            // now check validity and set the flag
            boolean result = true ;

            result &= (means!=null);
            result &= (variances!=null);
            result &= (priorProbabilities!=null);
            result &= (means.getDimensionality() == variances.getDimensionality());
            result &= (means.getDimensionality() == priorProbabilities.getDimensionality() );

            validForMetricComputation = result;
        }

        /**
         * @return the variances
         */
        public Vector getVariances()
        {
            return variances;
        }

         /**
         * @return the priorProbabilities
         */
        public Vector getPriorProbabilities()
        {
            return priorProbabilities;
        }

        /**
         * update performance data from another performance data object.
         * (like the old one...)
         * 
         * @param performanceData
         * Performance data
         */
        public void updateData(PerformanceData performanceData)
        {
            if ( !performanceData.isValid() )
            {
                throw new IllegalArgumentException(
                        "cannot updateData from invalid data");
            }

            this.means = performanceData.getMeans().clone();
            this.variances = performanceData.getVariances().clone();
            this.priorProbabilities = performanceData.getPriorProbabilities().clone();
            this.validForMetricComputation = true;
        }


    }

}
