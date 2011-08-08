/*
 * File:                MatrixBasedTermSimilarityNetwork.java
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

import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.text.relation.RelationNetwork;
import gov.sandia.cognition.text.term.IndexedTerm;
import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.TermIndex;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A relation network between terms based on their similarity. The similarity
 * values are stored in an underlying matrix.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class MatrixBasedTermSimilarityNetwork
    implements RelationNetwork<IndexedTerm, IndexedTermSimilarityRelation>
{

    /** The index of terms. */
    protected TermIndex termIndex;

    /** The similarities between terms. */
    protected Matrix similarities;

    /**
     * Creates a new {@code MatrixBasedTermSimilarityNetwork}.
     *
     * @param   termIndex
     *      The index of terms that contains the nodes of the network.
     * @param   similarities
     *      The square matrix of similarities between terms. Must have a number
     *      of rows and columns equal to the number of terms in the term index.
     */
    public MatrixBasedTermSimilarityNetwork(
        final TermIndex termIndex,
        final Matrix similarities)
    {
        super();

        if (similarities.getNumRows() != termIndex.getTermCount() ||
            similarities.getNumColumns() != termIndex.getTermCount())
        {
            throw new DimensionalityMismatchException(
                  "the number of terms in the term index must match the "
                + "dimensions of the square similarities matrix");
        }

        this.setTermIndex(termIndex);
        this.setSimilarities(similarities);
    }

    /**
     * Gets the similarity between the two given terms.
     *
     * @param   sourceTerm
     *      The source term.
     * @param   targetTerm
     *      The target term.
     * @return
     *      The similarity between the two given terms if both exist in the
     *      term index. Otherwise, 0.0 is returned.
     */
    public double getSimilarity(
        final Term sourceTerm,
        final Term targetTerm)
    {
        final IndexedTerm source = this.termIndex.getIndexedTerm(sourceTerm);
        final IndexedTerm target = this.termIndex.getIndexedTerm(targetTerm);

        if (source == null || target == null)
        {
            return 0.0;
        }
        else
        {
            return this.getSimilarity(source, target);
        }
    }

    /**
     * Gets the similarity between the two given terms.
     *
     * @param   source
     *      The source term.
     * @param   target
     *      The target term.
     * @return
     *      The similarity between the two given terms if both exist in the
     *      term index. Otherwise, 0.0 is returned.
     */
    public double getSimilarity(
        final IndexedTerm source,
        final IndexedTerm target)
    {
        if (source == null || target == null)
        {
            return 0.0;
        }
        else
        {
// TODO: Should we enforce that the indexed terms are valid here? IE: That they
// actually match with the term index.
            return this.getSimilarity(source.getIndex(), target.getIndex());
        }
    }

    /**
     * Gets the similarity between the two given terms.
     *
     * @param   sourceIndex
     *      The index of the source term.
     * @param   targetIndex
     *      The index of the target term.
     * @return
     *      The similarity between the two given terms if both exist in the
     *      term index. Otherwise, 0.0 is returned.
     */
    public double getSimilarity(
        final int sourceIndex,
        final int targetIndex)
    {
// TODO: Should we enforce the indices here or just let the matrix barf on them?
        return this.similarities.getElement(sourceIndex, targetIndex);
    }

    public int getObjectCount()
    {
        return this.termIndex.getTermCount();
    }

    public Set<IndexedTerm> getObjects()
    {
        return new LinkedHashSet<IndexedTerm>(this.termIndex.getTerms());
    }

    public boolean isObject(
        final Object o)
    {
        return o != null && o instanceof IndexedTerm &&
            this.termIndex.hasIndexedTerm((IndexedTerm) o);
    }

    public boolean hasRelation(
        final IndexedTerm source,
        final IndexedTerm target)
    {
        return source != null && target != null && this.getSimilarity(source,
            target) != 0.0;
    }

    public IndexedTermSimilarityRelation getRelation(
        final IndexedTerm source,
        final IndexedTerm target)
    {
        if (source == null || target == null)
        {
            return null;
        }

        final int sourceIndex = source.getIndex();
        final int targetIndex = target.getIndex();

        final double similarity = this.similarities.getElement(
            sourceIndex, targetIndex);

        if (similarity != 0.0)
        {
            return new IndexedTermSimilarityRelation(
                source, target, similarity);
        }
        else
        {
            return null;
        }
    }

    public Set<IndexedTermSimilarityRelation> getAllRelations(
        final IndexedTerm source,
        final IndexedTerm target)
    {
        // This is a singleton relationship.
        final IndexedTermSimilarityRelation relation =
            this.getRelation(source, target);

        if (relation == null)
        {
            return Collections.emptySet();
        }
        else
        {
            return Collections.singleton(relation);
        }
    }

    public IndexedTerm getRelationSource(
        final IndexedTermSimilarityRelation relation)
    {
        if (relation == null)
        {
            return null;
        }
        else
        {
            return relation.getSource();
        }
    }

    public IndexedTerm getRelationTarget(
        final IndexedTermSimilarityRelation relation)
    {
        if (relation == null)
        {
            return null;
        }
        else
        {
            return relation.getTarget();
        }
    }

    public Set<IndexedTermSimilarityRelation> relationsOf(
        final IndexedTerm term)
    {
        final LinkedHashSet<IndexedTermSimilarityRelation> result =
            new LinkedHashSet<IndexedTermSimilarityRelation>();

        // Using the linked hash set will remove a redundant self-relation.
        result.addAll(this.relationsFrom(term));
        result.addAll(this.relationsTo(term));
        return result;
    }

    public Set<IndexedTermSimilarityRelation> relationsFrom(
        final IndexedTerm source)
    {
        final int sourceIndex = source.getIndex();

        final LinkedHashSet<IndexedTermSimilarityRelation> result =
            new LinkedHashSet<IndexedTermSimilarityRelation>();

        // Walk the rows of the matrix to get the relation.
        for (VectorEntry entry : this.similarities.getRow(sourceIndex))
        {
            final double similarity = entry.getValue();

            if (similarity != 0.0)
            {
                final IndexedTerm target = this.termIndex.getIndexedTerm(
                    entry.getIndex());
                result.add(new IndexedTermSimilarityRelation(source, target,
                    similarity));
            }
        // else - We ignore zero similarities.
        }
        return result;
    }

    public Set<IndexedTermSimilarityRelation> relationsTo(
        final IndexedTerm target)
    {
        final int targetIndex = target.getIndex();

        final LinkedHashSet<IndexedTermSimilarityRelation> result =
            new LinkedHashSet<IndexedTermSimilarityRelation>();

        // Walk the columns of the matrix to get the relation.
        for (VectorEntry entry : this.similarities.getColumn(targetIndex))
        {
            final double similarity = entry.getValue();

            if (similarity != 0.0)
            {
                final IndexedTerm source = this.termIndex.getIndexedTerm(
                    entry.getIndex());
                result.add(new IndexedTermSimilarityRelation(source, target,
                    similarity));
            }
        // else - We ignore zero similarities.
        }
        return result;
    }

    /**
     * Gets the index of terms.
     *
     * @return
     *      The index of terms.
     */
    public TermIndex getTermIndex()
    {
        return this.termIndex;
    }

    /**
     * Sets the index of terms.
     *
     * @param   termIndex
     *      The index of terms.
     */
    protected void setTermIndex(
        final TermIndex termIndex)
    {
        this.termIndex = termIndex;
    }

    /**
     * Gets the similarities between terms.
     *
     * @return
     *      The similarities between terms.
     */
    public Matrix getSimilarities()
    {
        return this.similarities;
    }

    /**
     * Gets the similarities between terms.
     *
     * @param   similarities
     *      The similarities between terms.
     */
    protected void setSimilarities(
        final Matrix similarities)
    {
        this.similarities = similarities;
    }

}
