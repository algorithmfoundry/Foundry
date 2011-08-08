/*
 * File:                StringEvaluatorSingleTermFilterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import gov.sandia.cognition.text.term.TermOccurrence;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class StringEvaluatorSingleTermFilter.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class StringEvaluatorSingleTermFilterTest
{
    /**
     * Creates a new test.
     */
    public StringEvaluatorSingleTermFilterTest()
    {
    }

    /**
     * Test of constructors of class StringEvaluatorSingleTermFilter.
     */
    @Test
    public void testConstructors()
    {
        Evaluator<String, String> evaluator = null;
        StringEvaluatorSingleTermFilter instance = new StringEvaluatorSingleTermFilter();
        assertSame(evaluator, instance.getEvaluator());

        evaluator = new AppendA();
        instance = new StringEvaluatorSingleTermFilter(evaluator);
        assertSame(evaluator, instance.getEvaluator());
    }

    /**
     * Test of filterTerm method, of class StringEvaluatorSingleTermFilter.
     */
    @Test
    public void testFilterTerm()
    {
        StringEvaluatorSingleTermFilter instance =
            new StringEvaluatorSingleTermFilter(new AppendA());

        TermOccurrence input = new DefaultTermOccurrence(
            new DefaultTerm("something"), 4, 7);
        TermOccurrence output = instance.filterTerm(input);
        assertEquals(new DefaultTerm("somethingA"), output.getTerm());
        assertEquals(4, output.getStart());
        assertEquals(7, output.getLength());
    }

    /**
     * Test of getEvaluator method, of class StringEvaluatorSingleTermFilter.
     */
    @Test
    public void testGetEvaluator()
    {
        this.testSetEvaluator();
    }

    /**
     * Test of setEvaluator method, of class StringEvaluatorSingleTermFilter.
     */
    @Test
    public void testSetEvaluator()
    {
        Evaluator<String, String> evaluator = null;
        StringEvaluatorSingleTermFilter instance = new StringEvaluatorSingleTermFilter();
        assertSame(evaluator, instance.getEvaluator());

        evaluator = new AppendA();
        instance.setEvaluator(evaluator);
        assertSame(evaluator, instance.getEvaluator());
        
        evaluator = new AppendA();
        instance.setEvaluator(evaluator);
        assertSame(evaluator, instance.getEvaluator());

        evaluator = null;
        instance.setEvaluator(evaluator);
        assertSame(evaluator, instance.getEvaluator());

        evaluator = new AppendA();
        instance.setEvaluator(evaluator);
        assertSame(evaluator, instance.getEvaluator());
    }

    public static class AppendA
        implements Evaluator<String, String>
    {

        public String evaluate(
            final String input)
        {
            return input + "A";
        }

    }

}