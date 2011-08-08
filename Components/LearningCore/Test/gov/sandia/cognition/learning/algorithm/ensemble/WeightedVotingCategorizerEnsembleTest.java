/*
 * File:                WeightedVotingCategorizerEnsembleTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.categorization.ScalarThresholdBinaryCategorizer;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

/**
 * Unit tests for class WeightedVotingCategorizerEnsemble.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class WeightedVotingCategorizerEnsembleTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public WeightedVotingCategorizerEnsembleTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>> instance =
            new WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>>();
        assertNotNull(instance.getCategories());
        assertTrue(instance.getCategories().isEmpty());
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());

        Set<Boolean> categories = new HashSet<Boolean>();
        instance = new WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>>(
            categories);
        assertSame(categories, instance.getCategories());
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());

        List<WeightedValue<Evaluator<Double, Boolean>>> members = new ArrayList<WeightedValue<Evaluator<Double, Boolean>>>();
        instance = new WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>>(
            categories, members);
        assertSame(categories, instance.getCategories());
        assertSame(members, instance.getMembers());
    }

    /**
     * Test of add method, of class WeightedVotingCategorizerEnsemble.
     */
    public void testAdd()
    {
        WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>> instance =
            new WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>>();
        assertTrue(instance.getMembers().isEmpty());

        ScalarThresholdBinaryCategorizer member = new ScalarThresholdBinaryCategorizer(
            0.5);
        instance.add(member);
        assertEquals(1, instance.getMembers().size());
        assertEquals(1.0, instance.getMembers().get(0).getWeight());
        assertSame(member, instance.getMembers().get(0).getValue());

        member = new ScalarThresholdBinaryCategorizer(-0.6);
        instance.add(member, 1234.6);
        assertEquals(2, instance.getMembers().size());
        assertEquals(1234.6, instance.getMembers().get(1).getWeight());
        assertSame(member, instance.getMembers().get(1).getValue());

        boolean exceptionThrown = false;
        try
        {
            instance.add(null);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(2, instance.getMembers().size());

        exceptionThrown = false;
        try
        {
            instance.add(null, 1.0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(2, instance.getMembers().size());

        exceptionThrown = false;
        try
        {
            instance.add(member, -0.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(2, instance.getMembers().size());
    }


    /**
     * Test of evaluate method, of class WeightedVotingCategorizerEnsemble.
     */
    public void testEvaluate()
    {
        WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>> instance =
            new WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>>();
        instance.add(new ScalarThresholdBinaryCategorizer(0.0), 1.0);
        instance.add(new ScalarThresholdBinaryCategorizer(2.0), 1.0);
        instance.add(new ScalarThresholdBinaryCategorizer(-1.0), 1.0);

        assertTrue(instance.evaluate(3.0));
        assertTrue(instance.evaluate(1.0));
        assertTrue(instance.evaluate(0.5));
        assertTrue(instance.evaluate(0.0));
        assertFalse(instance.evaluate(-0.5));
        assertFalse(instance.evaluate(-3.0));
    }

    /**
     * Test of evaluateAsWeightedValue method, of class WeightedVotingCategorizerEnsemble.
     */
    public void testEvaluateWithDiscriminant()
    {
        double epsilon = 0.00001;
        WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>> instance =
            new WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>>();
        instance.add(new ScalarThresholdBinaryCategorizer(0.0), 2.0);
        instance.add(new ScalarThresholdBinaryCategorizer(2.0), 1.0);
        instance.add(new ScalarThresholdBinaryCategorizer(-1.0), 1.0);

        WeightedValue<Boolean> result = instance.evaluateWithDiscriminant(3.0);
        assertTrue(result.getValue());
        assertEquals(1.0, result.getWeight(), epsilon);

        assertTrue(instance.evaluate(1.0));
        result = instance.evaluateWithDiscriminant(1.0);
        assertTrue(result.getValue());
        assertEquals(0.75, result.getWeight(), epsilon);

        assertTrue(instance.evaluate(0.5));
        result = instance.evaluateWithDiscriminant(0.5);
        assertTrue(result.getValue());
        assertEquals(0.75, result.getWeight(), epsilon);

        assertTrue(instance.evaluate(0.0));
        result = instance.evaluateWithDiscriminant(0.0);
        assertTrue(result.getValue());
        assertEquals(0.75, result.getWeight(), epsilon);

        assertFalse(instance.evaluate(-0.5));
        result = instance.evaluateWithDiscriminant(-0.5);
        assertFalse(result.getValue());
        assertEquals(0.75, result.getWeight(), epsilon);

        assertFalse(instance.evaluate(-3.0));
        result = instance.evaluateWithDiscriminant(-3.0);
        assertFalse(result.getValue());
        assertEquals(1.0, result.getWeight(), epsilon);
    }

    /**
     * Test of getMembers method, of class WeightedVotingCategorizerEnsemble.
     */
    public void testGetMembers()
    {
        this.testSetMembers();
    }

    /**
     * Test of setMembers method, of class WeightedVotingCategorizerEnsemble.
     */
    public void testSetMembers()
    {
        WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>> instance =
            new WeightedVotingCategorizerEnsemble<Double, Boolean, Evaluator<Double, Boolean>>();
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());

        List<WeightedValue<Evaluator<Double, Boolean>>> members = new ArrayList<WeightedValue<Evaluator<Double, Boolean>>>();
        instance.setMembers(members);
        assertSame(members, instance.getMembers());

        members = null;
        instance.setMembers(null);
        assertSame(members, instance.getMembers());
    }

}
