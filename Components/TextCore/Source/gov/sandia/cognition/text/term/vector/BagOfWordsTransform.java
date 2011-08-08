/*
 * File:                BagOfWordsTransform.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.DefaultVectorFactoryContainer;
import gov.sandia.cognition.text.term.DefaultTermIndex;
import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.TermIndex;
import gov.sandia.cognition.text.term.Termable;

/**
 * Transforms a list of term occurrences into a vector of counts.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class BagOfWordsTransform
    extends DefaultVectorFactoryContainer
    implements Evaluator<Iterable<? extends Termable>, Vector>
{

    /** Gets the term index used by the transform. Maps terms to indices in
     *  the vector. */
    protected TermIndex termIndex;
    
    /**
     * Creates a new {@code BagOfWordsTransform}. Starts with an empty term
     * index.
     */
    public BagOfWordsTransform()
    {
        this(new DefaultTermIndex());
    }

    /**
     * Creates a new {@code BagOfWordsTransform} with the given term index.
     *
     * @param   termIndex
     *      The term index to use to map terms to vector indices.
     */
    public BagOfWordsTransform(
        final TermIndex termIndex)
    {
        this(termIndex, SparseVectorFactory.getDefault());
    }

    /**
     * Creates a new {@code BagOfWordsTransform} with the given term index.
     *
     * @param   termIndex
     *      The term index to use to map terms to vector indices.
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public BagOfWordsTransform(
        final TermIndex termIndex,
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super();

        this.setTermIndex(termIndex);
        this.setVectorFactory(vectorFactory);
    }

    public Vector evaluate(
        final Iterable<? extends Termable> terms)
    {
        return this.convertToVector(terms);
    }

    /**
     * Converts a given list of terms to a vector by counting the occurrence of
     * each term.
     *
     * @param   terms
     *      The terms to count.
     * @return
     *      The bag-of-words vector representation of the terms, which is the
     *      count of how many times each term occurs in the document.
     */
    public Vector convertToVector(
        final Iterable<? extends Termable> terms)
    {
        return this.convertToVector(terms, this.getVectorFactory());
    }

    /**
     * Converts a given list of terms to a vector by counting the occurrence of
     * each term.
     *
     * @param   terms
     *      The terms to count.
     * @param   vectorFactory
     *      The vector factory to use to create the vector.
     * @return
     *      The bag-of-words vector representation of the terms, which is the
     *      count of how many times each term occurs in the document.
     */
    public Vector convertToVector(
        final Iterable<? extends Termable> terms,
        final VectorFactory<?> vectorFactory)
    {
        return convertToVector(terms, this.getTermIndex(), vectorFactory);
    }

    /**
     * Converts a given list of terms to a vector by counting the occurrence of
     * each term.
     *
     * @param   terms
     *      The terms to count.
     * @param   termIndex
     *      The term index to use to map terms to their vector indices.
     * @param   vectorFactory
     *      The vector factory to use to create the vector.
     * @return
     *      The bag-of-words vector representation of the terms, which is the
     *      count of how many times each term occurs in the document.
     */
    public static Vector convertToVector(
        final Iterable<? extends Termable> terms,
        final TermIndex termIndex,
        final VectorFactory<?> vectorFactory)
    {
        // Create the vector to store the result.
        final Vector result = vectorFactory.createVector(
            termIndex.getTermCount());

        for (Termable termable : terms)
        {
            final Term term = termable.asTerm();
            int index = termIndex.getIndex(term);

            if (index >= 0)
            {
                final double count = result.getElement(index);
                result.setElement(index, count + 1);
            }
            // TODO: Ideally we would somehow handle all of the "unknown"
            // elements also. Perhaps by using the first vector element for
            // unknowns.
        }

        return result;
    }

    /**
     * Gets the term index that the transform uses to map terms to their vector
     * indices.
     *
     * @return
     *      The term index used by the transform.
     */
    public TermIndex getTermIndex()
    {
        return this.termIndex;
    }

    /**
     * Sets the term index that the transform is to use to map terms to their
     * vector indices.
     *
     * @param   termIndex
     *      The term index for the transform to use.
     */
    public void setTermIndex(
        final TermIndex termIndex)
    {
        this.termIndex = termIndex;
    }
    
}
