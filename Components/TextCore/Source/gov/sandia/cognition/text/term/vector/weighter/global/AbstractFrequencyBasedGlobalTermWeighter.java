/*
 * File:                AbstractCountingGlobalWeighter.java
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

import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * An abstract {@code GlobalTermWeighter} that keeps track of term frequencies
 * in documents. For each term, it keeps track of both the document frequency
 * (the number of documents the term appears in) and the global frequency
 * (the total number of times the term appears). It also keeps track of the
 * total number of documents.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractFrequencyBasedGlobalTermWeighter
    extends AbstractGlobalTermWeighter
{

    /** The number of documents the weight is computed over. */
    protected int documentCount;

    /** The vector containing the number of documents that each term occurs in.
     */
    protected Vector termDocumentFrequencies;

    /** A vector containing the total number of times that each term occurred
     *  in the document set. */
    protected Vector termGlobalFrequencies;

    /**
     * Creates a new {@code AbstractCountingBasedGlobalTermWeighter}.
     */
    public AbstractFrequencyBasedGlobalTermWeighter()
    {
        this(SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code AbstractCountingBasedGlobalTermWeighter}.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public AbstractFrequencyBasedGlobalTermWeighter(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(vectorFactory);

        this.setDocumentCount(0);
        this.setTermDocumentFrequencies(null);
        this.setTermGlobalFrequencies(null);
    }

    @Override
    public AbstractFrequencyBasedGlobalTermWeighter clone()
    {
        final AbstractFrequencyBasedGlobalTermWeighter clone =
            (AbstractFrequencyBasedGlobalTermWeighter) super.clone();
        clone.termDocumentFrequencies = ObjectUtil.cloneSafe(
            this.termDocumentFrequencies);
        clone.termGlobalFrequencies = ObjectUtil.cloneSafe(
            this.termGlobalFrequencies);
        return clone;
    }

    public void add(
        final Vector counts)
    {
        final int dimensionality = counts.getDimensionality();
        if (this.termDocumentFrequencies == null)
        {
            // Initialize the internal vectors for the given dimensionality.
            this.initializeVectors(dimensionality);
        }
        else
        {
            int currentDimensionality =
                this.termDocumentFrequencies.getDimensionality();
            if (dimensionality < currentDimensionality)
            {
                throw new DimensionalityMismatchException(
                    "dimensionality must be at least "
                    + this.termDocumentFrequencies.getDimensionality());
            }
            else if (dimensionality > currentDimensionality)
            {
                // We need to grow the vectors to support the new dimensionality.
                this.growVectors(dimensionality);
                currentDimensionality = dimensionality;
            }
        }

        this.documentCount++;

        // Increment the global frequencies.
        this.termGlobalFrequencies.plusEquals(counts);

        // Increment the count of the number of documents a term occurrs in
        // for the nonzero entries of the counts.
        for (VectorEntry entry : counts)
        {
            if (entry.getValue() != 0.0)
            {
                final int index = entry.getIndex();
                final double count =
                    this.termDocumentFrequencies.getElement(index) + 1;
                this.termDocumentFrequencies.setElement(index, count);
            }
        }
    }

    public boolean remove(
        final Vector counts)
    {
// TODO: Should we first verify that the document was in the collection?
// For example, making sure that subtrating all the document counts make
// the count >= 0?

        // Make sure that the dimensionalities match.
        this.termDocumentFrequencies.assertSameDimensionality(counts);

        // We're removing the document, so decrease the total count.
        this.documentCount--;

        // Update the global frequencies.
        this.termGlobalFrequencies.minusEquals(counts);

        // Decrement the count of the number of documents a term occurrs in
        // for the nonzero entries of the counts.
        for (VectorEntry entry : counts)
        {
            if (entry.getValue() != 0.0)
            {
                final int index = entry.getIndex();
                final double count =
                    this.termDocumentFrequencies.getElement(index) - 1;
                this.termDocumentFrequencies.setElement(index, count);
            }
        }

        return true;
    }

    /**
     * Initializes internal vectors to the given dimensionality.
     * 
     * @param   dimensionality
     *      The dimensionality to initialize to.
     */
    protected void initializeVectors(
        final int dimensionality)
    {
        this.termDocumentFrequencies = this.getVectorFactory().createVector(
            dimensionality);
        this.termGlobalFrequencies = this.getVectorFactory().createVector(
            dimensionality);
    }

    /**
     * Called when the dimensionality of the term vector grows.
     *
     * @param   newDimensionality
     *      The new dimensionality;
     */
    protected void growVectors(
        final int newDimensionality)
    {
        // We need to grow the vector to hold more data.
        // TODO: This is an ugly way of growing a vector.
        final Vector difference = this.getVectorFactory().createVector(
            newDimensionality - this.termDocumentFrequencies.getDimensionality());
        this.termDocumentFrequencies = this.termDocumentFrequencies.stack(
            difference);
        this.termGlobalFrequencies = this.termGlobalFrequencies.stack(
            difference);
    }

    public int getDocumentCount()
    {
        return this.documentCount;
    }

    /**
     * Sets the document count.
     *
     * @param   documentCount
     *      The document count.
     */
    protected void setDocumentCount(
        final int documentCount)
    {
        this.documentCount = documentCount;
    }

    /**
     * Gets the vector containing the number of documents that each term
     * appears in.
     *
     * @return
     *      The term document frequencies.
     */
    public Vector getTermDocumentFrequencies()
    {
        return this.termDocumentFrequencies;
    }

    /**
     * Sets the vector containing the number of documents that each term
     * appears in.
     *
     * @param   termDocumentFrequencies
     *      The document frequencies.
     */
    protected void setTermDocumentFrequencies(
        final Vector termDocumentFrequencies)
    {
        this.termDocumentFrequencies = termDocumentFrequencies;
    }

    /**
     * Gets the vector containing the number of times that each term appears.
     *
     * @return
     *      The term global frequencies.
     */
    public Vector getTermGlobalFrequencies()
    {
        return this.termGlobalFrequencies;
    }

    /**
     * Gets the vector containing the number of times that each term appears.
     *
     * @param   termGlobalFrequencies
     *      The term global frequencies.
     */
    protected void setTermGlobalFrequencies(
        final Vector termGlobalFrequencies)
    {
        this.termGlobalFrequencies = termGlobalFrequencies;
    }

}
