/*
 * File:                NearestNeighborKDTree.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.geometry.KDTree;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Collection;

/**
 * A KDTree-based implementation of the nearest neighbor algorithm.  This
 * algorithm has a O(n log(n)) construction time and a O(log(n)) evaluate time.
 * @param <InputType> Type of Vectorizable data upon which we determine
 * similarity.
 * @param <OutputType> Output of the evaluator, like Matrix, Double, String
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class NearestNeighborKDTree<InputType extends Vectorizable,OutputType>
    extends AbstractNearestNeighbor<InputType,OutputType>
{

    /**
     * KDTree that holds the data to search for neighbors.
     */
    private KDTree<InputType,OutputType,InputOutputPair<? extends InputType,OutputType>> data;

    /**
     * Creates a new instance of {@code NearestNeighborKDTree}.
     */
    public NearestNeighborKDTree()
    {
        this(null, null);
    }

    /**
     * Creates a new instance of NearestNeighborKDTree
     *
     * @param data
     * Underlying data for the classifier
     * @param divergenceFunction Divergence function that determines how "far" two objects are apart
     */
    public NearestNeighborKDTree(
        KDTree<InputType,OutputType,InputOutputPair<? extends InputType,OutputType>> data,
        DivergenceFunction<? super InputType, ? super InputType> divergenceFunction )
    {
        super( divergenceFunction );
        this.setData(data);
    }

    @Override
    public NearestNeighborKDTree<InputType,OutputType> clone()
    {
        @SuppressWarnings("unchecked")
        NearestNeighborKDTree<InputType,OutputType> clone =
            (NearestNeighborKDTree<InputType,OutputType>) super.clone();
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

    public OutputType evaluate(
        InputType input)
    {
        Collection<InputOutputPair<? extends InputType, OutputType>> neighbors =
            this.getData().findNearest(input, 1, this.getDivergenceFunction());

        InputOutputPair<?,OutputType> pair = CollectionUtil.getFirst(neighbors);
        if( pair != null )
        {
            return pair.getOutput();
        }
        else
        {
            return null;
        }
        
    }

    /**
     * This is a BatchLearner interface for creating a new NearestNeighbor
     * from a given dataset, simply a pass-through to the constructor of
     * NearestNeighbor
     * @param <InputType> Type of data upon which the NearestNeighbor operates,
     * something like Vector, Double, or String
     * @param <OutputType> Output of the evaluator, like Matrix, Double, String
     */
    public static class Learner<InputType extends Vectorizable, OutputType>
        extends NearestNeighborKDTree<InputType,OutputType>
        implements SupervisedBatchLearner<InputType,OutputType,NearestNeighborKDTree<InputType, OutputType>>
    {

        /**
         * Default constructor.
         */
        public Learner()
        {
            this( null );
        }

        /**
         * Creates a new instance of Learner
         * @param divergenceFunction
         * Divergence function that determines how "far" two objects are apart,
         * where lower values indicate two objects are more similar
         */
        public Learner(
            Metric<? super Vectorizable> divergenceFunction )
        {
            super( null, divergenceFunction );
        }

        /**
         * Creates a new NearestNeighbor from a Collection of InputType.
         * We build a balanced KDTree with the data, which is an O(n log(n))
         * operator for n data points.
         * @param data Dataset from which to create a new NearestNeighbor
         * @return
         * NearestNeighbor based on the given dataset with a balanced
         * KDTree.
         */
        public NearestNeighborKDTree<InputType, OutputType> learn(
            Collection<? extends InputOutputPair<? extends InputType,OutputType>> data )
        {
            @SuppressWarnings("unchecked")
            NearestNeighborKDTree<InputType, OutputType> clone = this.clone();
            KDTree<InputType,OutputType,InputOutputPair<? extends InputType,OutputType>> tree =
                KDTree.createBalanced(data);
            clone.setData( tree );
            return clone;
        }

    }

}
