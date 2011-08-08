/*
 * File:                AbstractKNearestNeighbor.java
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

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Summarizer;
import java.util.Collection;

/**
 * Partial implementation of KNearestNeighbor.
 * 
 * @param <InputType> Type of data upon which the KNearestNeighbor operates,
 * something like Vector, Double, or String
 * @param <OutputType> Output of the evaluator, like Matrix, Double, String
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractKNearestNeighbor<InputType,OutputType>
    extends AbstractNearestNeighbor<InputType,OutputType>
    implements KNearestNeighbor<InputType, OutputType>
{

    /**
     * Number of neighbors to consider, must be greater than zero
     */
    private int k;

    /**
     * Creates a single object from a collection of data
     */
    private Summarizer<? super OutputType,? extends OutputType> averager;

    /**
     * Creates a new instance of KNearestNeighbor
     * @param k
     * Number of neighbors to consider, must be greater than zero
     * @param divergenceFunction
     * Divergence function that determines how "far" two objects are apart
     * @param averager
     * Creates a single object from a collection of data
     */
    public AbstractKNearestNeighbor(
        int k,
        DivergenceFunction<? super InputType, ? super InputType> divergenceFunction,
        Summarizer<? super OutputType,? extends OutputType> averager )
    {
        super( divergenceFunction );
        this.setK(k);
        this.setAverager(averager);
    }

    @Override
    public AbstractKNearestNeighbor<InputType,OutputType> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractKNearestNeighbor<InputType,OutputType> clone =
            (AbstractKNearestNeighbor<InputType,OutputType>) super.clone();

        clone.setAverager( ObjectUtil.cloneSmart( this.getAverager() ) );
        return clone;
    }

    /**
     * Getter for k
     * @return
     * Number of neighbors to consider, must be greater than zero
     */
    public int getK()
    {
        return this.k;
    }

    /**
     * Setter for k
     * @param k
     * Number of neighbors to consider, must be greater than zero
     */
    public void setK(
        int k )
    {
        if (k <= 0)
        {
            throw new IllegalArgumentException(
                "Number of neighbors must be greater than zero" );
        }
        this.k = k;
    }


    /**
     * Getter for averager
     * @return
     * Creates a single object from a collection of data
     */
    public Summarizer<? super OutputType,? extends OutputType> getAverager()
    {
        return this.averager;
    }

    /**
     * Setter for averager
     * @param averager
     * Creates a single object from a collection of data
     */
    public void setAverager(
        Summarizer<? super OutputType,? extends OutputType> averager )
    {
        this.averager = averager;
    }

    /**
     * Computes the neighbors to the input key.
     * @param key
     * Input to find the nearest neighbors of.
     * @return
     * Collection of nearest neighbors.
     */
    protected abstract Collection<OutputType> computeNeighborhood(
        InputType key );

    public OutputType evaluate(
        InputType input)
    {

        Collection<OutputType> neighbors = this.computeNeighborhood(input);
        if( this.getK() == 1 )
        {
            return CollectionUtil.getFirst(neighbors);
        }
        else
        {
            return this.getAverager().summarize(neighbors);
        }
        
    }

}
