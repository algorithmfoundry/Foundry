/*
 * File:                InverseDocumentFrequencyGlobalWeighter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 21, 2009, Sandia Corporation.
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
 * Implements the inverse-document-frequency (IDF) term global weighting scheme.
 * It is a commonly used term weighting approach that gives a higher weight to
 * terms that appear in a small number of documents in the collection. Its
 * formula is:
 *
 *     idf_i = log(n / df_i)
 *
 * where n is the total number of documents and df_i is the number of documents
 * that term i appears in.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference
(
    author="Wikipedia",
    title="tf-idf",
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/tf-idf",
    year=2009
)
public class InverseDocumentFrequencyGlobalTermWeighter
    extends AbstractFrequencyBasedGlobalTermWeighter
{

    /** The (cached) value of the inverse document frequency. The cached value
     *  is cleared out whenever a document is added or removed. It is recomputed
     *  from the other state values on request. */
    protected Vector inverseDocumentFrequency;

    /**
     * Creates a new {@code InverseDocumentFrequencyGlobalTermWeighter}.
     */
    public InverseDocumentFrequencyGlobalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code InverseDocumentFrequencyGlobalTermWeighter}.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public InverseDocumentFrequencyGlobalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);
    }

    @Override
    public InverseDocumentFrequencyGlobalTermWeighter clone()
    {
        final InverseDocumentFrequencyGlobalTermWeighter clone = (InverseDocumentFrequencyGlobalTermWeighter)
            super.clone();
        clone.inverseDocumentFrequency = ObjectUtil.cloneSafe(this.inverseDocumentFrequency);
        return clone;
    }



    @Override
    public void add(
        final Vector counts)
    {
        super.add(counts);
        this.setInverseDocumentFrequency(null);
    }

    @Override
    public boolean remove(
        final Vector counts)
    {
        final boolean result = super.remove(counts);

        if (result)
        {
            this.setInverseDocumentFrequency(null);
        }

        return result;
    }

    public int getDimensionality()
    {
        return VectorUtil.safeGetDimensionality(this.getTermDocumentFrequencies());
    }

    public Vector getGlobalWeights()
    {
        return this.getInverseDocumentFrequency();
    }

    /**
     * Gets the inverse-document-frequency (IDF) global weight values.
     *
     * @return
     *      The inverse-document-frequency (IDF) values.
     */
    public Vector getInverseDocumentFrequency()
    {
        // We cache the inverse document frequency.
        if (   this.inverseDocumentFrequency == null
            && this.termDocumentFrequencies != null)
        {
            // Need to update the IDF. Start by copying the term occurrence
            // counts vector since that is what we will use to compute the IDF.
            Vector newIDFs =
                this.getVectorFactory().copyVector(this.termDocumentFrequencies);

            for (VectorEntry entry : newIDFs)
            {
                // Get the number of documents this term occurrs in.
                final double count = entry.getValue();

                if (count > 0.0)
                {
                    // Compute the inverse-document frequency and use that as
                    // the value.
                    final double idf = Math.log(this.documentCount / count);
                    entry.setValue(idf);
                }
            }

            this.setInverseDocumentFrequency(newIDFs);
        }

        return this.inverseDocumentFrequency;
    }

    /**
     * Sets the cached inverse-document-frequency (IDF) global weight values.
     *
     * @param   inverseDocumentFrequency
     *      The cached inverse-document-frequency (IDF) global weight values.
     */
    protected void setInverseDocumentFrequency(
        final Vector inverseDocumentFrequency)
    {
        this.inverseDocumentFrequency = inverseDocumentFrequency;
    }
}
