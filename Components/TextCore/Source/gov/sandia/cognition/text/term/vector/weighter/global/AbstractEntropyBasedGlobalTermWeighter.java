/*
 * File:                AbstractEntropyBasedGlobalWeighter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 20, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter.global;

import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * An abstract implementation of a global term weighting scheme that keeps track
 * of the sum of the entropy term (f_ij * log(f_ij)) over all documents. It is
 * used as a speed-up for global term weighting methods that are based on
 * entropy so that they can be computed incrementally.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractEntropyBasedGlobalTermWeighter
    extends AbstractFrequencyBasedGlobalTermWeighter
{

    /** A vector containing the sum of the entropy term (f_ij * log(f_ij)) over
     *  each document in the collection for each term.
     */
    protected Vector termEntropiesSum;

    /**
     * Creates a new {@code AbstractEntropyBasedGlobalTermWeighter}.
     */
    public AbstractEntropyBasedGlobalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code AbstractEntropyBasedGlobalTermWeighter}.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public AbstractEntropyBasedGlobalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);

        this.setTermEntropiesSum(null);
    }

    @Override
    public AbstractEntropyBasedGlobalTermWeighter clone()
    {
        final AbstractEntropyBasedGlobalTermWeighter clone = (AbstractEntropyBasedGlobalTermWeighter)
            super.clone();
        clone.termEntropiesSum = ObjectUtil.cloneSafe(this.termEntropiesSum);
        return clone;
    }

    @Override
    public void add(
        final Vector counts)
    {
        super.add(counts);
        
        // Update the term occurrence counts.
        for (VectorEntry entry : counts)
        {
            final int index = entry.getIndex();
            final double count = entry.getValue();

            if (count > 0.0)
            {
                final double termEntropySum = count * Math.log(count)
                    + this.termEntropiesSum.getElement(index);
                this.termEntropiesSum.setElement(index, termEntropySum);
            }
        }
    }

    @Override
    public boolean remove(
        final Vector counts)
    {
        final boolean result = super.remove(counts);

        if (result)
        {
            // Update the term entropies sum.
            for (VectorEntry entry : counts)
            {
                final int index = entry.getIndex();
                final double count = entry.getValue();

                if (count > 0.0)
                {
                    final double termEntropySum = count * Math.log(count)
                        - this.termEntropiesSum.getElement(index);
                    this.termEntropiesSum.setElement(index, termEntropySum);
                }
            }
        }

        return result;
    }

    @Override
    protected void initializeVectors(
        final int dimensionality)
    {
        super.initializeVectors(dimensionality);

        this.termEntropiesSum = this.getVectorFactory().createVector(
            dimensionality);
    }

    @Override
    protected void growVectors(
        final int newDimensionality)
    {
        super.growVectors(newDimensionality);

        this.termEntropiesSum = this.termEntropiesSum.stack(
            this.getVectorFactory().createVector(
                newDimensionality - this.termEntropiesSum.getDimensionality()));
    }



    /**
     * Gets the vector containing the sum of term the entropies.
     *
     * @return
     *      The term entropies sum.
     */
    public Vector getTermEntropiesSum()
    {
        return this.termEntropiesSum;
    }

    /**
     * Sets the vector containing the sum of the term entropies.
     *
     * @param   termEntropiesSum
     *      The term entropies sum.
     */
    protected void setTermEntropiesSum(
        final Vector termEntropiesSum)
    {
        this.termEntropiesSum = termEntropiesSum;
    }

}
