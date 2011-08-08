/*
 * File:                DominanceGlobalTermWeighter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 22, 2009, Sandia Corporation.
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
import gov.sandia.cognition.math.matrix.VectorUtil;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Implements the dominance term gloal weighting scheme. It is based on the
 * entropy global weighting scheme, but instead the global weight favors terms
 * with high entropy instead of discounting them, which is called the term
 * dominance. The formula for weighting is given as:
 *
 * For term i, the global weight (D(i)) is:
 *    D(i) = exp(H(i)) / n
 *    H(i) = - sum_j { p_ij log(p_ij) }
 *    p_ij = tf_ij / gf_i
 *
 * where
 *    n = The total number of documents
 *    gf_i = The total number of times that term i appears
 *    tf_ij = The number of times that term i appears in document j
 *
 * This class uses an optimization for computing H(i):
 *     H(i) = - (sum_j (tf_ij log(tf_ij))) / fg_i + log(fg_i)
 * which allows sum_j (tf_ij log(tf_ij)) to be incrementally computed and then
 * divided by gf_i when needed, instead of needing to compute p_ij each time.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DominanceGlobalTermWeighter
    extends AbstractEntropyBasedGlobalTermWeighter
{

    /** A vector caching the global dominance weight of the document collection.
     *  It may be null. Use getDominance() to compute the proper value if it has
     *  not been updated yet.
     */
    protected Vector dominance;

    /**
     * Creates a new {@code DominanceGlobalTermWeighter}.
     */
    public DominanceGlobalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code DominanceGlobalTermWeighter}.
     *
     * @param   vectorFactory
     *      The vector factory.
     */
    public DominanceGlobalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);

        this.setDominance(null);
    }

    @Override
    public DominanceGlobalTermWeighter clone()
    {
        DominanceGlobalTermWeighter clone = (DominanceGlobalTermWeighter)
            super.clone();
        clone.dominance = ObjectUtil.cloneSafe(this.dominance);
        return clone;
    }

    @Override
    public void add(
        final Vector counts)
    {
        super.add(counts);
        this.setDominance(null);
    }

    @Override
    public boolean remove(
        final Vector counts)
    {
        final boolean result = super.remove(counts);

        if (result)
        {
            this.setDominance(null);
        }

        return result;
    }

    public Vector getGlobalWeights()
    {
        return this.getDominance();
    }

    public int getDimensionality()
    {
        return VectorUtil.safeGetDimensionality(this.getTermGlobalFrequencies());
    }

    /**
     * Gets the dominance weight (global weight) vector for all of the terms.
     *
     * @return
     *      The dominance weight (global weight) vector for all of the terms.
     */
    public Vector getDominance()
    {
        // We cache the dominance.
        if (this.dominance == null && this.termGlobalFrequencies != null)
        {
            // Need to update the dominance. Start by creating an empty vector to
            // hold it.
            final int dimensionality = this.getDimensionality();
            final Vector newDominance = this.getVectorFactory().createVector(
                dimensionality);
            for (VectorEntry entry : this.termGlobalFrequencies)
            {
                final int index = entry.getIndex();
                final double termEntropySum =
                    this.termEntropiesSum.getElement(index);
                final double termOccurrences = entry.getValue();

                // Calculate the actual dominance values.
                double value = 0.0;
                if (termOccurrences != 0.0)
                {
                    value =
                        Math.exp(-(termEntropySum / termOccurrences
                            - Math.log(termOccurrences)))
                        / this.documentCount;
                }

                newDominance.setElement(index, value);
            }

            this.setDominance(newDominance);
        }

        return this.dominance;
    }

    /**
     * Sets the cached dominance weight vector.
     *
     * @param   dominance
     *      The cached dominance weight vector.
     */
    protected void setDominance(
        final Vector dominance)
    {
        this.dominance = dominance;
    }

}
