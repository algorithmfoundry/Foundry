/*
 * File:                DirichletProcessMixtureModel.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Apr 26, 2010, Sandia Corporation.
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
import gov.sandia.cognition.learning.algorithm.clustering.cluster.DefaultCluster;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.bayesian.conjugate.MultivariateGaussianMeanBayesianEstimator;
import gov.sandia.cognition.statistics.bayesian.conjugate.MultivariateGaussianMeanCovarianceBayesianEstimator;
import gov.sandia.cognition.statistics.distribution.BetaDistribution;
import gov.sandia.cognition.statistics.distribution.ChineseRestaurantProcess;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.statistics.distribution.MultivariateStudentTDistribution;
import gov.sandia.cognition.statistics.distribution.NormalInverseWishartDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

/**
 * An implementation of Dirichlet Process clustering, which estimates the
 * number of clusters and the centroids of the clusters from a set of
 * data.
 * @param <ObservationType>
 * Type of observations handled by the mixture model
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Radform M. Neal",
            title="Markov Chain Sampling Methods for Dirichlet Process Mixture Models",
            type=PublicationType.Journal,
            year=2000,
            publication="Journal of Computational and Graphical Statistics, Vol. 9, No. 2",
            pages={249,265},
            notes="Based in part on Algorithm 2 from Neal"
        )
        ,
        @PublicationReference(
            author={
                "Michael D. Escobar",
                "Mike West"
            },
            title="Bayesian Density Estimation and Inference Using Mixtures",
            type=PublicationType.Journal,
            publication="Journal of the American Statistical Association",
            year=1995
        )
    }
)
public class DirichletProcessMixtureModel<ObservationType>
    extends AbstractMarkovChainMonteCarlo<ObservationType,DirichletProcessMixtureModel.Sample<ObservationType>>
{

    /**
     * Default concentration parameter of the Dirichlet Process, {@value}.
     */
    public static final double DEFAULT_ALPHA = 1.0;

    /**
     * Default number of initial clusters
     */
    public static final int DEFAULT_NUM_INITIAL_CLUSTERS = 2;

    /**
     * 
     */
    public static final boolean DEFAULT_REESTIMATE_ALPHA = true;

    /**
     * Creates the clusters and predictive prior distributions
     */
    protected Updater<ObservationType> updater;

    /**
     * Number of clusters to initialize
     */
    private int numInitialClusters;

    /**
     * Flag to automatically re-estimate the alpha parameter
     */
    protected boolean reestimateAlpha;

    /**
     * Initial value of alpha, the concentration parameter of the
     * Dirichlet Process
     */
    protected double initialAlpha;

    /** 
     * Creates a new instance of DirichletProcessMixtureModel
     */
    public DirichletProcessMixtureModel()
    {
        this.setReestimateAlpha(DEFAULT_REESTIMATE_ALPHA);
        this.setInitialAlpha(DEFAULT_ALPHA);
        this.setNumInitialClusters( DEFAULT_NUM_INITIAL_CLUSTERS );
    }

    @Override
    public DirichletProcessMixtureModel<ObservationType> clone()
    {
        @SuppressWarnings("unchecked")
        DirichletProcessMixtureModel<ObservationType> clone =
            (DirichletProcessMixtureModel<ObservationType>) super.clone();
        clone.setUpdater( ObjectUtil.cloneSafe( this.getUpdater() ) );
        return clone;
    }

    /**
     * Base predictive distribution that determines the value of the
     * new cluster weighting during the Gibbs sampling.
     */
    transient protected ProbabilityFunction<ObservationType> conditionalPriorPredictive;

    /**
     * Holds the cluster weights so that we don't have to re-allocate them
     * each mcmcUpdate step.
     */
    transient protected double[] clusterWeights;

    @Override
    protected void mcmcUpdate()
    {

        // This is the "mother" or the "none of the above" distribution...
        // The omnipresent distribution hiding in the background that we
        // we will compute the weight of creating a new cluster.
        if( this.conditionalPriorPredictive == null )
        {
            this.conditionalPriorPredictive =
                this.updater.createPriorPredictive(this.data);
        }

        // This assigns observations to each of the K clusters, plus the
        // as-yet-uncreated new cluster
        final int K = this.currentParameter.getNumClusters();
        DPMMLogConditional logConditional = new DPMMLogConditional();
        ArrayList<Collection<ObservationType>> clusterAssignments =
            this.assignObservationsToClusters( K, logConditional );
        
        final int numObservations = CollectionUtil.size(this.data);

        // Through a bizarre quirk of the math, the log conditional is for
        // the sample we previously generated...
        if( (this.previousParameter != null) &&
            (this.previousParameter.posteriorLogLikelihood == null) )
        {
            this.previousParameter.posteriorLogLikelihood =
                this.previousParameter.computePosteriorLogLikelihood(
                    numObservations, logConditional.logConditional );
        }

        // Now, update each cluster according to the data assigned to it
        this.currentParameter.clusters = this.updateClusters(clusterAssignments);

        // Update the alpha parameter
        if( this.getReestimateAlpha() )
        {
            this.currentParameter.alpha =
                this.updateAlpha(this.currentParameter.alpha, numObservations);
        }

    }

    /**
     * Update each cluster according to the data assigned to it
     * @param clusterAssignments
     * Observations assigned to each cluster
     * @return
     * Cluster that contains an update parameter estimate and weighted by
     * the number of observations assigned to the cluster
     */
    protected ArrayList<DPMMCluster<ObservationType>> updateClusters(
        ArrayList<Collection<ObservationType>> clusterAssignments )
    {

        final int Kp1 = clusterAssignments.size();
        ArrayList<DPMMCluster<ObservationType>> clusters =
            new ArrayList<DPMMCluster<ObservationType>>( Kp1 );
        for( int k = 0; k < Kp1; k++ )
        {
            Collection<ObservationType> assignments = clusterAssignments.get(k);
            if( assignments.size() > 1 )
            {
                DPMMCluster<ObservationType> cluster =
                    this.createCluster( assignments, this.updater );
                if( cluster != null )
                {
                    clusters.add( cluster );
                }
            }
        }

        return clusters;

    }

    /**
     * Container for the log conditional likelihood
     */
    protected static class DPMMLogConditional
        extends AbstractCloneableSerializable
    {

        /**
         * log conditional likelihood
         */
        double logConditional;

        /**
         * Default constructor
         */
        public DPMMLogConditional()
        {
            this.logConditional = 0.0;
        }

    }

    /**
     * Assigns observations to each of the K clusters,
     * plus the as-yet-uncreated new cluster
     * @param K
     * Number of clusters
     * @return
     * Assignments from observations to clusters
     */
    protected ArrayList<Collection<ObservationType>> assignObservationsToClusters(
        int K,
        DPMMLogConditional logConditional )
    {

        // This is just a convenience to keep us from re-creating this
        // array every time.
        if( (this.clusterWeights == null) ||
            (this.clusterWeights.length != K+1) )
        {
            this.clusterWeights = new double[K+1];
        }

        // This assigns observations to each of the K clusters, plus the
        // as-yet-uncreated new cluster
        ArrayList<Collection<ObservationType>> clusterAssignments =
            new ArrayList<Collection<ObservationType>>( K+1 );
        for( int k = 0; k < K+1; k++ )
        {
            clusterAssignments.add( new LinkedList<ObservationType>() );
        }

        // Assign each observation to a cluster, including the possibility
        // of assigning the point to a new, as-yet-undefined new cluster
        for( ObservationType observation : this.data )
        {
            // Figure out which cluster this observation is assigned to.
            int clusterAssignment = this.assignObservationToCluster(
                observation, this.clusterWeights, logConditional );
            clusterAssignments.get(clusterAssignment).add(observation);
        }

        return clusterAssignments;
        
    }

    /**
     * Probabilistically assigns an observation to a cluster
     * @param observation
     * Observation that we're assigning
     * @param weights
     * Place holder for the weights that this method will create
     * @return
     * Index of the cluster to assign the observation to.  This will be
     * [0,K-1] for an existing cluster and "K" for an as-yet-undecided new
     * cluster.
     */
    protected int assignObservationToCluster(
        ObservationType observation,
        double[] weights,
        DPMMLogConditional logConditional )
    {

        final double alpha = this.currentParameter.alpha;
        final int K = this.currentParameter.getNumClusters();

        // Weight of assigning the data point to a brand-new cluster
        final double newClusterWeight =
            alpha*this.conditionalPriorPredictive.evaluate(observation);
        weights[K] = newClusterWeight;

        double weightSum = newClusterWeight;

        // The weight of each cluster is proporationate to the number of
        // points assigned to each cluster
        double conditional = 1e-100;
        for( int k = 0; k < K; k++ )
        {
            // This is an approximation.  We're really supposed to subtract
            // "1.0" from the weight of the cluster that the observation has
            // been assigned to.  However, that book-keeping gets really
            // expensive.  In any case, by subtracting "1.0" from all weights
            // we eliminate that nasty condition of assigning a cluster to
            // a single data point and getting infinite likelihood.
            DPMMCluster<ObservationType> cluster =
                this.currentParameter.clusters.get(k);
            int num = cluster.getMembers().size();
            if( num > 0 )
            {
                final double c = cluster.getProbabilityFunction().evaluate(observation);
                final double weight = (num-1)*c;
                weights[k] = weight;
                weightSum += weight;
                conditional += num*c;
            }
            else
            {
                weights[k] = 0.0;
            }
        }

        logConditional.logConditional += Math.log( conditional );

        // Choose a uniform number on [0,weightSum] to figure out which
        // cluster to assign this observation to
        double p = weightSum * this.random.nextDouble();
        for( int k = 0; k < K+1; k++ )
        {
            p -= weights[k];
            if( p <= 0.0 )
            {
                return k;
            }
        }

        // You should/will never get here.
        throw new IllegalArgumentException(
            "Did not select cluster: " + weightSum );

    }

    /**
     * Creates a cluster from the given cluster assignment
     * @param clusterAssignment
     * Observations assigned to a particular cluster
     * @param localUpdater
     * Updater that recomputes the cluster parameters, needed to ensure
     * thread safety in the parallel implementation
     * @return
     * Cluster that contains an update parameter estimate and weighted by
     * the number of observations assigned to the cluster
     */
    protected DPMMCluster<ObservationType> createCluster(
        Collection<ObservationType> clusterAssignment,
        Updater<ObservationType> localUpdater )
    {
        
        if( clusterAssignment == null )
        {
            return null;
        }
        
        double weight = clusterAssignment.size();
        if( weight <= 0.0 )
        {
            return null;
        }
        else
        {
            ProbabilityFunction<ObservationType> probabilityFunction =
                localUpdater.createClusterPosterior( clusterAssignment, this.random );
            return new DPMMCluster<ObservationType>( clusterAssignment, probabilityFunction );
        }
    }

    /**
     * Creates a new value of "eta" which, in turn, helps sample a new alpha.
     */
    transient protected BetaDistribution etaSampler;

    /**
     * Samples a new alpha-inverse.
     */
    transient protected GammaDistribution alphaInverseSampler;

    /**
     * Runs the Gibbs sampler for the concentration parameter, alpha, given
     * the data.
     * @param alpha
     * Current value of the concentration parameter
     * @param numObservations
     * Number of observations we're sampling over
     * @return
     * Updated estimate of alpha
     */
    protected double updateAlpha(
        double alpha,
        int numObservations )
    {

        // Gibbs Sampler for updating "alpha"
        // Escobar & West: Equation 14 on page 585.
        if( this.etaSampler == null )
        {
            this.etaSampler = new BetaDistribution();
        }
        this.etaSampler.setAlpha(alpha+1.0);
        this.etaSampler.setBeta(numObservations);
        final double eta = this.etaSampler.sample(this.random);
        final double logEta = Math.log(eta);

        // Parameterize the Gamma according to the mixture,
        // Escobar & West: Equation 13 on page 585.
        final double a = 1.0;
        final double b = 1.0;
        final int updatedK = this.currentParameter.getNumClusters();
        double etaWeight = (a+updatedK-1.0) / (numObservations*(b-logEta));
        double pEta = this.random.nextDouble();

        if( this.alphaInverseSampler == null )
        {
            this.alphaInverseSampler = new GammaDistribution();
        }
        
        if( pEta < etaWeight )
//        if( pEta < eta )
        {
            this.alphaInverseSampler.setShape( a + updatedK );
        }
        else
        {
            this.alphaInverseSampler.setShape( a + updatedK - 1.0 );
        }
        this.alphaInverseSampler.setScale( b - logEta );
        return 1.0/this.alphaInverseSampler.sample(this.random);

    }

    @Override
    public DirichletProcessMixtureModel.Sample<ObservationType> createInitialLearnedObject()
    {
        ArrayList<DPMMCluster<ObservationType>> clusters =
            new ArrayList<DPMMCluster<ObservationType>>(
                this.getNumInitialClusters() );
        ProbabilityFunction<ObservationType> probabilityFunction =
            this.updater.createClusterPosterior( this.data, this.random );
        ArrayList<? extends ObservationType> dataArray =
            CollectionUtil.asArrayList(this.data);
        for( int k = 0; k < this.getNumInitialClusters(); k++ )
        {
            clusters.add( new DPMMCluster<ObservationType>(
                dataArray, probabilityFunction ) );
        }
        return new Sample<ObservationType>(this.getInitialAlpha(),clusters);
    }

    /**
     * Getter for updater
     * @return
     * Creates the clusters and predictive prior distributions
     */
    public DirichletProcessMixtureModel.Updater<ObservationType> getUpdater()
    {
        return this.updater;
    }

    /**
     * Setter for updater
     * @param updater
     * Creates the clusters and predictive prior distributions
     */
    public void setUpdater(
        DirichletProcessMixtureModel.Updater<ObservationType> updater)
    {
        this.updater = updater;
    }

    /**
     * Getter for numInitialClusters
     * @return 
     * Number of clusters to initialize
     */
    public int getNumInitialClusters()
    {
        return this.numInitialClusters;
    }

    /**
     * Getter for numInitialClusters
     * @param numInitialClusters
     * Number of clusters to initialize
     */
    public void setNumInitialClusters(
        int numInitialClusters)
    {
        this.numInitialClusters = numInitialClusters;
    }

    /**
     * Getter for reestimateAlpha
     * @return
     * Flag to automatically re-estimate the alpha parameter
     */
    public boolean getReestimateAlpha()
    {
        return this.reestimateAlpha;
    }

    /**
     * Setter for reestimateAlpha
     * @param reestimateAlpha
     * Flag to automatically re-estimate the alpha parameter
     */
    public void setReestimateAlpha(
        boolean reestimateAlpha)
    {
        this.reestimateAlpha = reestimateAlpha;
    }

    /**
     * Getter for initialAlpha
     * @return
     * Initial value of alpha, the concentration parameter of the
     * Dirichlet Process
     */
    public double getInitialAlpha()
    {
        return this.initialAlpha;
    }

    /**
     * Setter for initialAlpha
     * @param initialAlpha
     * Initial value of alpha, the concentration parameter of the
     * Dirichlet Process
     */
    public void setInitialAlpha(
        double initialAlpha)
    {
        this.initialAlpha = initialAlpha;
    }

    /**
     * Cluster for a step in the DPMM
     * @param <ObservationType>
     * Types of observations of the DPMM
     */
    public static class DPMMCluster<ObservationType>
        extends DefaultCluster<ObservationType>
    {

        /**
         * Probability function describing the assigned data
         */
        private ProbabilityFunction<? super ObservationType> probabilityFunction;

        /**
         * Creates a new instance of DPMMCluster
         * @param assignedData
         * Data assigned to the cluster
         * @param probabilityFunction
         * Probability function describing the assigned data
         */
        public DPMMCluster(
            Collection<? extends ObservationType> assignedData,
            ProbabilityFunction<? super ObservationType> probabilityFunction )
        {
            super( assignedData );
            this.setProbabilityFunction(probabilityFunction);
        }

        @Override
        @SuppressWarnings("unchecked")
        public DPMMCluster<ObservationType> clone()
        {
            DPMMCluster<ObservationType> clone =
                (DPMMCluster<ObservationType>) super.clone();
            clone.setProbabilityFunction(
                ObjectUtil.cloneSafe( this.getProbabilityFunction() ) );
            return clone;
        }

        /**
         * Getter for probabilityFunction
         * @return 
         * Probability function describing the assigned data
         */
        public ProbabilityFunction<? super ObservationType> getProbabilityFunction()
        {
            return this.probabilityFunction;
        }

        /**
         * Setter for probabilityFunction
         * @param probabilityFunction
         * Probability function describing the assigned data
         */
        public void setProbabilityFunction(
            ProbabilityFunction<? super ObservationType> probabilityFunction)
        {
            this.probabilityFunction = probabilityFunction;
        }        

    }

    /**
     * A sample from the Dirichlet Process Mixture Model.
     * @param <ObservationType>
     * Type of observations handled by the mixture model
     */
    public static class Sample<ObservationType>
        extends AbstractCloneableSerializable
    {

        /**
         * Scaling parameter which defines the strength of the base distribution,
         * must be greater than zero.
         */
        protected double alpha;

        /**
         * Point mass realizations from the base distribution.
         */
        protected ArrayList<DPMMCluster<ObservationType>> clusters;

        /**
         * Posterior log likelihood of the sample
         */
        private Double posteriorLogLikelihood;

        /**
         * Creates a new instance of Sample
         * @param alpha
         * Scaling parameter which defines the strength of the base distribution,
         * must be greater than zero.
         * @param clusters
         * Point mass realizations from the base distribution.
         */
        public Sample(
            double alpha,
            ArrayList<DPMMCluster<ObservationType>> clusters )
        {
            this.setAlpha(alpha);
            this.setClusters(clusters);
            this.setPosteriorLogLikelihood(null);
        }

        @Override
        public Sample<ObservationType> clone()
        {
            @SuppressWarnings("unchecked")
            Sample<ObservationType> clone = (Sample<ObservationType>) super.clone();
            clone.setClusters(
                ObjectUtil.cloneSmartElementsAsArrayList( this.getClusters() ) );

            // The reason this is null is so that the we know to compute
            // the conditional on the next MCMC step
            clone.setPosteriorLogLikelihood( null );
            return clone;
        }

        /**
         * Computes the posterior log likelihood of the data given the clusters
         * and the prior probability of the clustering from a
         * Chinese Restaurant Process
         * @param data
         * Data to consider
         * @return
         * Posterior log likelihood of the data
         */
        public double computePosteriorLogLikelihood(
            Iterable<? extends ObservationType> data )
        {
            final int K = this.getNumClusters();
            final int numObservations = CollectionUtil.size(data);
            double logSum = 0.0;
            for( ObservationType value : data )
            {
                double p = 1e-100;
                for( int k = 0; k < K; k++ )
                {
                    DPMMCluster<ObservationType> cluster = this.clusters.get(k);
                    final int weight = cluster.getMembers().size();
                    final double likelihood =
                        cluster.getProbabilityFunction().evaluate(value);
                    p += weight * likelihood;
                }
                logSum += Math.log(p);
            }

            ChineseRestaurantProcess.PMF pmf = new ChineseRestaurantProcess.PMF(
                this.getAlpha(), numObservations );
            Vector counts = VectorFactory.getDefault().createVector(K);
            for( int k = 0; k < K; k++ )
            {
                counts.setElement(k, this.clusters.get(k).getMembers().size() );
            }
            logSum += pmf.logEvaluate( counts );

            return logSum;
        }

        /**
         * Computes the posterior log likelihood of the Sample
         * @param numObservations
         * Number of observations in the Sample
         * @param logConditional
         * Log conditional likelihood of the data given the sample
         * @return
         * Posterior log likelihood
         */
        public double computePosteriorLogLikelihood(
            int numObservations,
            double logConditional )
        {
            final int K = this.getNumClusters();
            ChineseRestaurantProcess.PMF pmf = new ChineseRestaurantProcess.PMF(
                this.getAlpha(), numObservations );
            Vector counts = VectorFactory.getDefault().createVector(K);
            for( int k = 0; k < K; k++ )
            {
                counts.setElement(k, this.clusters.get(k).getMembers().size() );
            }
            double logPrior = pmf.logEvaluate( counts );
            double logPosterior = logPrior + logConditional;
            return logPosterior;
        }

        /**
         * Removes the unused clusters from the Sample.
         */
        public void removeUnusedClusters()
        {
            for( int j = 0; j < this.getNumClusters(); j++ )
            {
                DPMMCluster<ObservationType> cluster = this.clusters.get(j);
                if( cluster.getMembers().size() <= 0 )
                {
                    this.clusters.remove(j);
                    j--;
                }
            }
        }

        /**
         * Getter for alpha
         * @return
         * Scaling parameter which defines the strength of the base distribution,
         * must be greater than zero.
         */
        public double getAlpha()
        {
            return this.alpha;
        }

        /**
         * Setter for alpha
         * @param alpha
         * Scaling parameter which defines the strength of the base distribution,
         * must be greater than zero.
         */
        protected void setAlpha(
            double alpha)
        {
            if( alpha <= 0.0 )
            {
                throw new IllegalArgumentException(
                    "Alpha must be > 0.0 " );
            }
            this.alpha = alpha;
        }

        /**
         * Gets the number of clusters in the Sample
         * @return
         * Number of clusters in the Sample.
         */
        public int getNumClusters()
        {
            return this.clusters.size();
        }

        /**
         * Getter for clusters
         * @return
         * Point mass realizations from the base distribution.
         */
        public ArrayList<DPMMCluster<ObservationType>> getClusters()
        {
            return this.clusters;
        }

        /**
         * Setter for clusters
         * @param clusters
         * Point mass realizations from the base distribution.
         */
        protected void setClusters(
            ArrayList<DPMMCluster<ObservationType>> clusters)
        {
            this.clusters = clusters;
        }

        public Double getPosteriorLogLikelihood()
        {
            return this.posteriorLogLikelihood;
        }

        public void setPosteriorLogLikelihood(
            Double posteriorLogLikelihood)
        {
            this.posteriorLogLikelihood = posteriorLogLikelihood;
        }

    }

    /**
     * Updater for the DPMM
     * @param <ObservationType>
     * Type of observations handled by the mixture model
     */
    public static interface Updater<ObservationType>
        extends CloneableSerializable
    {

        /**
         * Creates the prior predictive distribution from the data.
         * @param data
         * Data from which to create the prior predictive
         * @return
         * Prior predictive distribution from the data
         */
        public ProbabilityFunction<ObservationType> createPriorPredictive(
            Iterable<? extends ObservationType> data );

        /**
         * Updates the cluster from the values assigned to it
         * @param values
         * Values assigned to the cluster
         * @param random
         * Random number generator
         * @return
         * Updated cluster value
         */
        public ProbabilityFunction<ObservationType> createClusterPosterior(
            Iterable<? extends ObservationType> values,
            Random random );

    }


    /**
     * Updater that creates specified clusters with distinct means and covariances
     */
    public static class MultivariateMeanCovarianceUpdater
        extends AbstractCloneableSerializable
        implements Updater<Vector>
    {

        /**
         * Bayesian estimator for the parameters
         */
        private MultivariateGaussianMeanCovarianceBayesianEstimator estimator;

        /**
         * Default constructor
         */
        public MultivariateMeanCovarianceUpdater()
        {
            this( null );
        }

        /**
         * Creates a new instance of MultivariateMeanCovarianceUpdater
         * @param dimensionality
         * Dimensionality of the Vectors
         */
        public MultivariateMeanCovarianceUpdater(
            int dimensionality )
        {
            this( new MultivariateGaussianMeanCovarianceBayesianEstimator(dimensionality) );
        }

        /**
         * Creates a new instance of MultivariateMeanCovarianceUpdater
         * @param estimator
         * Bayesian estimator for the parameters
         */
        public MultivariateMeanCovarianceUpdater(
            MultivariateGaussianMeanCovarianceBayesianEstimator estimator)
        {
            this.estimator = estimator;
        }

        @Override
        public MultivariateMeanCovarianceUpdater clone()
        {
            MultivariateMeanCovarianceUpdater clone =
                (MultivariateMeanCovarianceUpdater) super.clone();
            clone.estimator = ObjectUtil.cloneSafe(this.estimator);
            return clone;
        }

        public MultivariateStudentTDistribution.PDF createPriorPredictive(
            Iterable<? extends Vector> data)
        {
            NormalInverseWishartDistribution posterior =
                this.estimator.learn(data);
            return this.estimator.createPredictiveDistribution(posterior).getProbabilityFunction();
        }

        public MultivariateGaussian.PDF createClusterPosterior(
            Iterable<? extends Vector> values,
            Random random )
        {
            NormalInverseWishartDistribution posterior =
                this.estimator.learn(values);
            Matrix parameters = posterior.sample(random);
            return this.estimator.createConditionalDistribution(parameters).getProbabilityFunction();
        }

    }

    /**
     * Updater that creates specified clusters with identical covariances
     */
    public static class MultivariateMeanUpdater
        extends AbstractCloneableSerializable
        implements Updater<Vector>
    {

        /**
         * Bayesian estimator for the parameters
         */
        protected MultivariateGaussianMeanBayesianEstimator estimator;

        /**
         * Default constructor
         */
        public MultivariateMeanUpdater()
        {
            this( null );
        }

        /**
         * Creates a new instance of MeanCovarianceUpdater
         * @param dimensionality
         * Dimensionality of the Vectors
         */
        public MultivariateMeanUpdater(
            int dimensionality )
        {
            this( new MultivariateGaussianMeanBayesianEstimator(dimensionality) );
        }

        /**
         * Creates a new instance of MeanUpdater
         * @param estimator
         * Bayesian estimator for the parameters
         */
        public MultivariateMeanUpdater(
            MultivariateGaussianMeanBayesianEstimator estimator)
        {
            this.estimator = estimator;
        }

        @Override
        public MultivariateMeanUpdater clone()
        {
            MultivariateMeanUpdater clone =
                (MultivariateMeanUpdater) super.clone();
            clone.estimator = ObjectUtil.cloneSafe(this.estimator);
            return clone;
        }

        public MultivariateGaussian.PDF createPriorPredictive(
            Iterable<? extends Vector> data)
        {
            MultivariateGaussian posterior = this.estimator.learn(data);
            return this.estimator.createPredictiveDistribution(posterior).getProbabilityFunction();
        }

        public MultivariateGaussian.PDF createClusterPosterior(
            Iterable<? extends Vector> values,
            Random random )
        {
            MultivariateGaussian posterior = this.estimator.learn(values);
            Vector parameters = posterior.sample(random);
            return this.estimator.createConditionalDistribution(parameters).getProbabilityFunction();
        }

    }


}
