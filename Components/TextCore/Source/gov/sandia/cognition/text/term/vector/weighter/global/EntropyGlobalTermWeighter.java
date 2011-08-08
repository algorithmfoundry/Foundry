/*
 * File:                EntropyGlobalWeighter.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorUtil;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Implements the entropy global term weighting scheme. It has been seen that
 * this weighting scheme can work well with Latent Semantic Analysis
 * (Dumais, 1991).
 *
 * For a term i, the global weight (W(i)) is:
 *     W(i) = 1 - E(i) / log(n)
 *     E(i) = - sum_j (p_ij log(p_ij))
 *     p_ij = tf_ij / gf_i
 *
 * where
 *    n = The total number of documents
 *    gf_i = The total number of times that term i appears
 *    tf_ij = The number of times that term i appears in document j
 *
 * This class uses an optimization for computing E(i):
 *     E(i) = - (sum_j (tf_ij log(tf_ij))) / log(gf_i) + log(gf_i)
 * which allows sum_j (tf_ij log(tf_ij)) to be incrementally computed and then
 * divided by gf_i when needed, instead of needing to compute p_ij each time.
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference(
    author={"Susan T. Dumais"},
    title="Improving the retrieval of information from external sources",
    year=1991,
    type=PublicationType.Journal,
    publication="Behavior Research Methods, Instruments, and Computers",
    pages={229, 236},
    url="http://www.google.com/url?sa=t&source=web&ct=res&cd=1&url=http%3A%2F%2Fwww.psychonomic.org%2Fsearch%2Fview.cgi%3Fid%3D5145&ei=o7joSdGEHY-itgPLre3tAQ&usg=AFQjCNEvm6PZEL6_Hk3XThI6DQ-gGx9EnQ&sig2=-gjFzNroJQirwGtwjaJvgQ"
)
public class EntropyGlobalTermWeighter
    extends AbstractEntropyBasedGlobalTermWeighter
{
    
    /** A vector caching the global entropy weight of the document collection.
     *  It may be null. Use getEntropy() to compute the proper value if it has
     *  not been updated yet.
     */
    protected Vector entropy;

    /**
     * Creates a new {@code EntropyGlobalTermWeighter}.
     */
    public EntropyGlobalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code EntropyGlobalTermWeighter}.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public EntropyGlobalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);

        this.setEntropy(null);
    }

    @Override
    public EntropyGlobalTermWeighter clone()
    {
        final EntropyGlobalTermWeighter clone = (EntropyGlobalTermWeighter)
            super.clone();
        clone.entropy = ObjectUtil.cloneSafe(this.entropy);
        return clone;
    }

    @Override
    public void add(
        final Vector counts)
    {
        super.add(counts);
        this.setEntropy(null);
    }

    @Override
    public boolean remove(
        final Vector counts)
    {
        final boolean result = super.remove(counts);

        if (result)
        {
            this.setEntropy(null);
        }

        return result;
    }

    public int getDimensionality()
    {
        return VectorUtil.safeGetDimensionality(this.getTermGlobalFrequencies());
    }

    public Vector getGlobalWeights()
    {
        return this.getEntropy();
    }


    /**
     * Gets the entropy weight (global weight) vector for all of the terms.
     *
     * @return
     *      The entropy weight (global weight) vector for all of the terms.
     */
    public Vector getEntropy()
    {
        // We cache the entropy.
        if (this.entropy == null && this.termGlobalFrequencies != null)
        {
            // Need to update the entropy. Start by creating an empty vector to
            // hold it.
            final int dimensionality = this.getDimensionality();
            final Vector newEntropy = this.getVectorFactory().createVector(
                dimensionality);
            final double logDocumentCount = Math.log(this.documentCount);
            for (VectorEntry entry : this.termGlobalFrequencies)
            {
                final int index = entry.getIndex();
                final double termEntropySum =
                    this.termEntropiesSum.getElement(index);
                final double termOccurrences = entry.getValue();

                // Calculate the actual entropy values.
                double value = 1.0;
                if (termOccurrences != 0.0)
                {
                    value +=
                        (termEntropySum / termOccurrences
                            - Math.log(termOccurrences))
                        / logDocumentCount;
                }

                newEntropy.setElement(index, value);
            }

            this.setEntropy(newEntropy);
        }

        return this.entropy;
    }

    /**
     * Sets the cached entropy weight vector.
     *
     * @param   entropy
     *      The cached entropy weight vector.
     */
    protected void setEntropy(
        final Vector entropy)
    {
        this.entropy = entropy;
    }
    
}
