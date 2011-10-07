/*
 * File:                DirichletProcessClustering.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 26, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.algorithm.AnytimeAlgorithmWrapper;
import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.AnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.bayesian.DirichletProcessMixtureModel;
import gov.sandia.cognition.statistics.bayesian.DirichletProcessMixtureModel.Sample;
import gov.sandia.cognition.statistics.bayesian.ParallelDirichletProcessMixtureModel;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Clustering algorithm that wraps Dirichlet Process Mixture Model.  DPMM
 * finds the number of clusters, means, and (optionally by default) covariance
 * of Vector data.  Gory details: The clustering algorithm begins by drawing
 * samples from the posterior of a Dirichlet process mixture model, given
 * the data, using the method of Gibbs sampling.  From the resulting samples
 * (the number of which is a parameter), we select the clustering that
 * has the highest Maximum A Posteriori likelihood using the Chinese Restaurant
 * Process as the prior on the clustering.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Michael I. Jordan",
            title="Dirichlet Processes, Chinese Restaurant Processes and All That",
            type=PublicationType.Conference,
            publication="NIPS",
            year=2005,
            url="http://www.cs.berkeley.edu/~jordan/nips-tutorial05.ps"
        )
        ,
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
public class DirichletProcessClustering
    extends AnytimeAlgorithmWrapper<Collection<GaussianCluster>,DirichletProcessMixtureModel<Vector>>
    implements BatchClusterer<Vector,GaussianCluster>,
    AnytimeBatchLearner<Collection<? extends Vector>,Collection<GaussianCluster>>,
    Randomized,
    MeasurablePerformanceAlgorithm
{

    /**
     * Description of the performance value returned, {@value}.
     */
    public static final String PERFORMANCE_DESCRIPTION = "Number of Clusters";

    /**
     * Default dimensionality, {@value}.
     */
    public static final int DEFAULT_DIMENSIONALITY = 2;

    /**
     * Default number of samples, {@value}.
     */
    public static final int DEFAULT_SAMPLES = 1000;

    /**
     * Clustering results
     */
    private transient ArrayList<GaussianCluster> result;

    /** 
     * Creates a new instance of DirichletProcessClustering 
     */
    public DirichletProcessClustering()
    {
        this( DEFAULT_DIMENSIONALITY );
    }

    /**
     * Creates a new instance of DirichletProcessClustering
     * @param dimensionality
     * Dimensionality of the observations
     */
    public DirichletProcessClustering(
        final int dimensionality )
    {
        this( new ParallelDirichletProcessMixtureModel<Vector>() );
        this.setMaxIterations( DEFAULT_SAMPLES );

        // Note: there's not a compelling reason to set the burn in or
        // throwing out samples to decorrelate the result, since we're just
        // looking for the maximum a posterior estimate... so let's just
        // look at all the samples that we generate and judge from there
        this.getAlgorithm().setBurnInIterations( 1 );
        this.getAlgorithm().setIterationsPerSample( 1 );
        this.getAlgorithm().setNumInitialClusters(2);
        this.setRandom( new Random() );
    }

    /**
     * Creates a new instance of DirichletProcessClustering
     * @param algorithm
     * Dirichlet Process Mixture model that is being wrapped
     */
    public DirichletProcessClustering(
        final DirichletProcessMixtureModel<Vector> algorithm )
    {
        super( algorithm );
        this.result = null;
    }

    @Override
    public DirichletProcessClustering clone()
    {
        return (DirichletProcessClustering) super.clone();
    }

    @Override
    public ArrayList<GaussianCluster> getResult()
    {
        return this.result;
    }

    @Override
    public ArrayList<GaussianCluster> learn(
        final Collection<? extends Vector> data)
    {
        this.result = null;

        if( this.getAlgorithm().getUpdater() == null )
        {
            final int dim = CollectionUtil.getFirst(data).getDimensionality();
            this.getAlgorithm().setUpdater(
                new DirichletProcessMixtureModel.MultivariateMeanCovarianceUpdater( dim ) );
        }

        DataDistribution<DirichletProcessMixtureModel.Sample<Vector>> dpmm =
            this.getAlgorithm().learn(data);

        int maxIndex = -1;
        double maxPosterior = Double.NEGATIVE_INFINITY;
        DirichletProcessMixtureModel.Sample<Vector> maxSample = null;
        int i = 0;
        for( DirichletProcessMixtureModel.Sample<Vector> sample : dpmm.getDomain() )
        {
            if( sample.getPosteriorLogLikelihood() != null )
            {
                double posterior = sample.getPosteriorLogLikelihood();
                if( maxPosterior < posterior )
                {
                    maxPosterior = posterior;
                    maxIndex = i;
                    maxSample = sample;
                }
            }
            i++;
        }

        final int K = maxSample.getNumClusters();
        System.out.println( "Max Index = " + maxIndex + ", K = " + K + ", Log-Posterior = " + maxPosterior );
        this.result = new ArrayList<GaussianCluster>( K );
        for( int k = 0; k < K; k++ )
        {
            this.result.add( new GaussianCluster( maxSample.getClusters().get(k).getMembers(),
                (MultivariateGaussian.PDF) maxSample.getClusters().get(k).getProbabilityFunction() ) );
        }

        return this.getResult();

    }

    @Override
    public Random getRandom()
    {
        return this.getAlgorithm().getRandom();
    }

    @Override
    public void setRandom(
        final Random random)
    {
        this.getAlgorithm().setRandom(random);
    }
   
    @Override
    public NamedValue<Integer> getPerformance()
    {

        int numClusters;
        if( (this.getAlgorithm() != null) &&
            (this.getAlgorithm().getCurrentParameter() != null) )
        {

            numClusters = this.getAlgorithm().getCurrentParameter().getNumClusters();
        }
        else
        {
            numClusters = 0;
        }
        return new DefaultNamedValue<Integer>( PERFORMANCE_DESCRIPTION, numClusters );
    }

    @Override
    public boolean getKeepGoing()
    {
        return (this.getAlgorithm() != null) ? this.getAlgorithm().getKeepGoing() : false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends Vector> getData()
    {
        return (this.getAlgorithm() != null) ? (Collection<? extends Vector>) this.getAlgorithm().getData() : null;
    }

}
