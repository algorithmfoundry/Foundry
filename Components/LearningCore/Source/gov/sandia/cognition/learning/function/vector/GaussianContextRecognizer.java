/*
 * File:                GaussianContextRecognizer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 3, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.algorithm.AnytimeAlgorithmWrapper;
import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.learning.algorithm.AnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.clustering.KMeansClusterer;
import gov.sandia.cognition.learning.algorithm.clustering.ParallelizedKMeansClusterer;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.GaussianClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.NeighborhoodGaussianClusterInitializer;
import gov.sandia.cognition.statistics.distribution.MixtureOfGaussians;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.NamedValue;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Uses a MixtureOfGaussians to compute the probability of the different
 * constituent MultivariateGaussians (that is, the contexts)
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class GaussianContextRecognizer
    extends AbstractCloneableSerializable
    implements VectorFunction,
    VectorInputEvaluator<Vector,Vector>,
    VectorOutputEvaluator<Vector,Vector>
{

    /**
     * Underlying MixtureOfGaussians that computes context probabilities
     */
    private MixtureOfGaussians gaussianMixture;

    /** Creates a new instance of GaussianContextRecognizer */
    public GaussianContextRecognizer()
    {
        this( (MixtureOfGaussians) null );
    }

    /**
     * Creates a new instance of GaussianContextRecognizer
     * @param gaussianMixture 
     * Underlying MixtureOfGaussians that computes context probabilities
     */
    public GaussianContextRecognizer(
        MixtureOfGaussians gaussianMixture )
    {
        this.setGaussianMixture( gaussianMixture );
    }

    /**
     * Creates a new instance of GaussianContextRecognizer
     * @param clusters 
     */
    public GaussianContextRecognizer(
        Collection<GaussianCluster> clusters )
    {
        this();
        this.consumeClusters( clusters );
    }

    /**
     * Copy constructor
     * @param other 
     * GaussianContextRecognizer to clone
     */
    public GaussianContextRecognizer(
        GaussianContextRecognizer other )
    {
        this( other.getGaussianMixture().clone() );
    }

    @Override
    public GaussianContextRecognizer clone()
    {
        GaussianContextRecognizer clone = (GaussianContextRecognizer) super.clone();
        clone.setGaussianMixture( this.getGaussianMixture().clone() );
        return clone;
    }

    public Vector evaluate(
        Vector input )
    {
        return this.getGaussianMixture().computeRandomVariableProbabilities( input );
    }

    /**
     * Uses the given clusters to populate the internal clusters of this
     * @param clusters Clusters from which to create the data for this
     */
    public void consumeClusters(
        Collection<GaussianCluster> clusters )
    {
        ArrayList<MultivariateGaussian.PDF> gaussians =
            new ArrayList<MultivariateGaussian.PDF>( clusters.size() );
        Vector prior = VectorFactory.getDefault().createVector( clusters.size() );
        int index = 0;
        for (GaussianCluster cluster : clusters)
        {
            gaussians.add( new MultivariateGaussian.PDF( cluster.getGaussian() ) );
            prior.setElement( index, cluster.getMembers().size() );
            index++;
        }

        this.setGaussianMixture(
            new MixtureOfGaussians( gaussians, prior ) );

    }

    /**
     * Getter for gaussianMixture
     * @return 
     * Underlying MixtureOfGaussians that computes context probabilities
     */
    public MixtureOfGaussians getGaussianMixture()
    {
        return this.gaussianMixture;
    }

    /**
     * Setter for gaussianMixture
     * @param gaussianMixture 
     * Underlying MixtureOfGaussians that computes context probabilities
     */
    public void setGaussianMixture(
        MixtureOfGaussians gaussianMixture )
    {
        this.gaussianMixture = gaussianMixture;
    }

    public int getInputDimensionality()
    {
        return this.getGaussianMixture().getDimensionality();
    }

    public int getOutputDimensionality()
    {
        return this.getGaussianMixture().getNumRandomVariables();
    }

    /**
     * Creates a GaussianContextRecognizer from a Dataset<Vector> using
     * a BatchClusterer
     */
    public static class Learner
        extends AnytimeAlgorithmWrapper<GaussianContextRecognizer,AnytimeBatchLearner<Collection<? extends Vector>, Collection<GaussianCluster>>>
        implements AnytimeBatchLearner<Collection<? extends Vector>,GaussianContextRecognizer>,
        MeasurablePerformanceAlgorithm
    {

        /**
         * Creates a new {@code Learner}.
         */
        public Learner()
        {
            super();

            // By default use a k-means cluster creator.
            final GaussianClusterCreator clusterCreator = new GaussianClusterCreator();
            final NeighborhoodGaussianClusterInitializer initializer =
                new NeighborhoodGaussianClusterInitializer();
            final ParallelizedKMeansClusterer<Vector, GaussianCluster> kMeans =
                new ParallelizedKMeansClusterer<Vector, GaussianCluster>();
            kMeans.setInitializer(initializer);
            kMeans.setDivergenceFunction(new GaussianClusterDivergenceFunction());
            kMeans.setCreator(clusterCreator);

            this.setAlgorithm(kMeans);
        }

        /**
         * Creates a new instance of Learner
         * @param algorithm
         * Clustering algorithm to create the GaussianContextRecognizer from
         */
        public Learner(
            KMeansClusterer<Vector, GaussianCluster> algorithm )
        {
            super( algorithm );
        }

        // SteeringWheelAngle(LW), BrakeForce(MBRE_ESP), AcceleratorPedalPct(PW), DistronicAccelerationRelative(DTR_ObjIntrstDist), DistronicDistance(DTR_ObjIntrstDist), DistronicPositionRelative(DTR_ObjIntrstPosn), DisctronicImpactTime, DistronicSpeedRelative(DTR_ObjIntrstRelSpd), GPSElevation(GPS_HEIGHT), Gear(GPS_SOG), LateralAccelerationDerived, WheelSpeedDerived

        public GaussianContextRecognizer learn(
            Collection<? extends Vector> data )
        {
            this.getAlgorithm().learn( data );
            return this.getResult();
        }

        public GaussianContextRecognizer getResult()
        {
            if( (this.getAlgorithm() == null) ||
                (this.getAlgorithm().getResult() == null) )
            {
                return null;
            }
            else
            {
                return new GaussianContextRecognizer( this.getAlgorithm().getResult() );
            }
        }

        public NamedValue<? extends Number> getPerformance()
        {

            if( this.getAlgorithm() instanceof MeasurablePerformanceAlgorithm )
            {
                return ((MeasurablePerformanceAlgorithm) this.getAlgorithm()).getPerformance();
            }
            else
            {
                return null;
            }
        }

        public boolean getKeepGoing()
        {
            return this.getAlgorithm().getKeepGoing();
        }

        public Collection<? extends Vector> getData()
        {
            return this.getAlgorithm().getData();
        }

    }

}
