/*
 * File:                BagOfWordsTransformTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector;

import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermIndex;
import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.TermIndex;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class BagOfWordsTransform.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class BagOfWordsTransformTest
{
    /**
     * Creates a new test.
     */
    public BagOfWordsTransformTest()
    {
    }

    /**
     * Test of constructors of class BagOfWordsTransform.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<?> vectorFactory = SparseVectorFactory.getDefault();
        BagOfWordsTransform instance = new BagOfWordsTransform();
        assertNotNull(instance.getTermIndex());
        assertTrue(instance.getTermIndex() instanceof DefaultTermIndex);
        assertEquals(0, instance.getTermIndex().getTermCount());
        assertSame(vectorFactory, instance.getVectorFactory());

        TermIndex termIndex = new DefaultTermIndex();
        instance = new BagOfWordsTransform(termIndex);
        assertSame(termIndex, instance.getTermIndex());
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = new DenseVectorFactoryMTJ();
        instance = new BagOfWordsTransform(termIndex, vectorFactory);
        assertSame(termIndex, instance.getTermIndex());
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of evaluate method, of class BagOfWordsTransform.
     */
    @Test
    public void testEvaluate()
    {
        DefaultTermIndex termIndex = new DefaultTermIndex();
        DefaultTerm term0 = new DefaultTerm("term0");
        DefaultTerm term1 = new DefaultTerm("term1");
        DefaultTerm term2 = new DefaultTerm("term2");
        DefaultTerm termBad = new DefaultTerm("termBad");


        BagOfWordsTransform instance = new BagOfWordsTransform(termIndex);

        Term[] input = new Term[] { term0, term1, term2, termBad };
        Vector expected = VectorFactory.getDefault().createVector(0);
        assertEquals(expected, instance.evaluate(Arrays.asList(input)));

        termIndex.add(term0);
        termIndex.add(term1);
        termIndex.add(term2);

        expected = new Vector3(1, 1, 1);
        assertEquals(expected, instance.evaluate(Arrays.asList(input)));

        input = new Term[] { term1, term2, term2, termBad, termBad, termBad };
        expected = new Vector3(0, 1, 2);
        assertEquals(expected, instance.evaluate(Arrays.asList(input)));
        
        input = new Term[] { };
        expected = new Vector3();
        assertEquals(expected, instance.evaluate(Arrays.asList(input)));
    }

    /**
     * Test of getTermIndex method, of class BagOfWordsTransform.
     */
    @Test
    public void testGetTermIndex()
    {
        this.testSetTermIndex();
    }

    /**
     * Test of setTermIndex method, of class BagOfWordsTransform.
     */
    @Test
    public void testSetTermIndex()
    {
        BagOfWordsTransform instance = new BagOfWordsTransform();
        assertNotNull(instance.getTermIndex());
        assertTrue(instance.getTermIndex() instanceof DefaultTermIndex);
        assertEquals(0, instance.getTermIndex().getTermCount());

        TermIndex termIndex = new DefaultTermIndex();
        instance.setTermIndex(termIndex);
        assertSame(termIndex, instance.getTermIndex());

        termIndex = new DefaultTermIndex();
        instance.setTermIndex(termIndex);
        assertSame(termIndex, instance.getTermIndex());

        termIndex = null;
        instance.setTermIndex(termIndex);
        assertSame(termIndex, instance.getTermIndex());
    }

}