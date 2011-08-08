/*
 * File:                KNearestNeighborKDTree.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 4, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.math.geometry.KDTree;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.Summarizer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A KDTree-based implementation of the k-nearest neighbor algorithm.  This
 * algorithm has a O(n log(n)) construction time and a O(log(n)) evaluate time.
 * @param <InputType> Type of Vectorizable data upon which we determine
 * similarity.
 * @param <OutputType> Output of the evaluator, like Matrix, Double, String
 * @see gov.sandia.cognition.math.geometry.KDTree
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="k-nearest neighbor algorithm",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/K-nearest_neighbor_algorithm"
)
public class KNearestNeighborKDTree<InputType extends Vectorizable,OutputType>
    extends AbstractKNearestNeighbor<InputType,OutputType>
    implements Evaluator<InputType, OutputType>
{

    /**
     * KDTree that holds the data to search for neighbors.
     */
    private KDTree<InputType,OutputType,InputOutputPair<? extends InputType,OutputType>> data;

    /** 
     * Creates a new instance of KNearestNeighborKDTree 
     */
    public KNearestNeighborKDTree()
    {
        this( DEFAULT_K, null, null, null );
    }

    /**
     * Creates a new instance of KNearestNeighborKDTree
     * @param k
     * Number of neighbors to consider, must be greater than zero
     * @param data
     * KDTree that holds the data to search for neighbors.
     * @param distanceFunction
     * Distance metric that determines how "far" two objects are apart,
     * where lower values indicate two objects are more similar
     * @param averager
     * KDTree that holds the data to search for neighbors.
     */
    public KNearestNeighborKDTree(
        int k,
        KDTree<InputType,OutputType,InputOutputPair<? extends InputType,OutputType>> data,
        Metric<? super InputType> distanceFunction,
        Summarizer<? super OutputType,? extends OutputType> averager )
    {
        super( k, distanceFunction, averager );
        this.setData(data);
    }

    @Override
    public KNearestNeighborKDTree<InputType,OutputType> clone()
    {
        KNearestNeighborKDTree<InputType,OutputType> clone =
            (KNearestNeighborKDTree<InputType,OutputType>) super.clone();
        clone.setData( ObjectUtil.cloneSafe( this.getData() ) );
        return clone;
    }

    /**
     * Setter for distanceFunction
     * @return
     * Distance metric that determines how "far" two objects are apart,
     * where lower values indicate two objects are more similar.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Metric<? super InputType> getDivergenceFunction()
    {
        return (Metric<? super InputType>) super.getDivergenceFunction();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDivergenceFunction(
        DivergenceFunction<? super InputType, ? super InputType> divergenceFunction)
    {
        this.setDivergenceFunction( (Metric<? super InputType>) divergenceFunction );
    }

    /**
     * Sets the Metric to use.
     * @param divergenceFunction
     * Metric that determines closeness.
     */
    public void setDivergenceFunction(
        Metric<? super InputType> divergenceFunction)
    {
        super.setDivergenceFunction(divergenceFunction);
    }

    /**
     * Getter for data
     * @return 
     * KDTree that holds the data to search for neighbors.
     */
    public KDTree<InputType, OutputType,InputOutputPair<? extends InputType,OutputType>> getData()
    {
        return this.data;
    }

    /**
     * Setter for data
     * @param data
     * KDTree that holds the data to search for neighbors.
     */
    public void setData(
        KDTree<InputType, OutputType,InputOutputPair<? extends InputType,OutputType>> data)
    {
        this.data = data;
    }

    @Override
    protected Collection<OutputType> computeNeighborhood(
        InputType key)
    {
        Collection<InputOutputPair<? extends InputType,OutputType>> neighbors =
            this.getData().findNearest(key, this.getK(), this.getDivergenceFunction());
        ArrayList<OutputType> outputs =
            new ArrayList<OutputType>( neighbors.size() );
        for( Pair<? extends InputType,OutputType> neighbor : neighbors )
        {
            outputs.add( neighbor.getSecond() );
        }

        return outputs;
    }

    /**
     * Rebalances the internal KDTree to make the search more efficient.  This
     * is an O(n log(n)) operation with n samples.
     */
    public void rebalance()
    {
        this.setData( this.getData().reblanace() );
    }

    /**
     * This is a BatchLearner interface for creating a new KNearestNeighbor
     * from a given dataset, simply a pass-through to the constructor of
     * KNearestNeighbor
     * @param <InputType> Type of data upon which the KNearestNeighbor operates,
     * something like Vector, Double, or String
     * @param <OutputType> Output of the evaluator, like Matrix, Double, String
     */
    public static class Learner<InputType extends Vectorizable, OutputType>
        extends KNearestNeighborKDTree<InputType,OutputType>
        implements SupervisedBatchLearner<InputType,OutputType,KNearestNeighborKDTree<InputType, OutputType>>
    {

        /**
         * Default constructor.
         */
        public Learner()
        {
            this( null );
        }

        /**
         * Creates a new instance of Learner.
         * @param averager
         * Creates a single object from a collection of data.
         */
        public Learner(
            Summarizer<? super OutputType,? extends OutputType> averager )
        {
            this( DEFAULT_K, EuclideanDistanceMetric.INSTANCE, averager );
        }

        /**
         * Creates a new instance of Learner
         * @param k
         * Number of neighbors to consider, must be greater than zero
         * @param divergenceFunction
         * Divergence function that determines how "far" two objects are apart,
         * where lower values indicate two objects are more similar
         * @param averager
         * Creates a single object from a collection of data
         */
        public Learner(
            int k,
            Metric<? super Vectorizable> divergenceFunction,
            Summarizer<? super OutputType,? extends OutputType> averager )
        {
            super( k, null, divergenceFunction, averager );
        }

        /**
         * Creates a new KNearestNeighbor from a Collection of InputType.
         * We build a balanced KDTree with the data, which is an O(n log(n))
         * operator for n data points.
         * @param data Dataset from which to create a new KNearestNeighbor
         * @return
         * KNearestNeighbor based on the given dataset with a balanced
         * KDTree.
         */
        public KNearestNeighborKDTree<InputType, OutputType> learn(
            Collection<? extends InputOutputPair<? extends InputType,OutputType>> data )
        {
            @SuppressWarnings("unchecked")
            KNearestNeighborKDTree<InputType, OutputType> clone = this.clone();
            KDTree<InputType,OutputType,InputOutputPair<? extends InputType,OutputType>> tree =
                KDTree.createBalanced(data);
            clone.setData( tree );
            return clone;
        }

    }
    
}
