/*
 * File:                IndexedTermSimilarityRelation.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 18, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.relation;

import gov.sandia.cognition.text.relation.AbstractRelation;
import gov.sandia.cognition.text.term.IndexedTerm;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * A relationship between two indexed terms describing their term similarity.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class IndexedTermSimilarityRelation
    extends AbstractRelation<IndexedTerm, IndexedTerm>
{

    /** The similarity between the two terms. */
    protected double similarity;

    /**
     * Creates a new {@code IndexedTermSimilarityRelation}.
     *
     * @param   source
     *      The source term.
     * @param   target
     *      The target term.
     * @param   similarity
     *      The similarity between the terms.
     */
    public IndexedTermSimilarityRelation(
        final IndexedTerm source,
        final IndexedTerm target,
        final double similarity)
    {
        super(source, target);

        this.setSimilarity(similarity);
    }

    @Override
    public boolean equals(
        final Object other)
    {
        return other instanceof IndexedTermSimilarityRelation &&
            this.equals((IndexedTermSimilarityRelation) other);
    }

    /**
     * Determines if this object is equal to the given object. They are equal
     * if they have the same source, target, and similarity.
     *
     * @param   other
     *      Another object.
     * @return
     *      True if the two objects are equal; otherwise, false.
     */
    public boolean equals(
        final IndexedTermSimilarityRelation other)
    {
        return other != null && this.similarity == other.similarity &&
            ObjectUtil.equalsSafe(this.source, other.source) &&
            ObjectUtil.equalsSafe(this.target, other.target);
    }

    @Override
    public int hashCode()
    {
        // This is an auto-generated hash-code:
        int hash = 7;
        hash = 71 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 71 * hash + (this.target != null ? this.target.hashCode() : 0);
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.similarity) ^
            (Double.doubleToLongBits(this.similarity) >>> 32));
        return hash;
    }

    /**
     * Gets the similarity between the two terms.
     *
     * @return
     *      The similarity between the two terms.
     */
    public double getSimilarity()
    {
        return this.similarity;
    }

    /**
     * Sets the similarity between the two terms.
     *
     * @param   similarity
     *      The similarity between the two terms.
     */
    protected void setSimilarity(
        final double similarity)
    {
        this.similarity = similarity;
    }

}
