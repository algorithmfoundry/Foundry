/*
 * File:            AbstractWeightedEnsembleTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.util.WeightedValue;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractWeightedEnsemble.
 * 
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class AbstractWeightedEnsembleTest
{

    /**
     * Creates a new test.
     */
    public AbstractWeightedEnsembleTest()
    {
    }

    /**
     * Test of constructors of class AbstractWeightedEnsemble.
     */
    @Test
    public void testConstructors()
    {
        AbstractWeightedEnsemble<Evaluator<Double, Double>> instance =
            new DummyWeightedEnsemble<Evaluator<Double, Double>>();
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());

        List<WeightedValue<Evaluator<Double, Double>>> members =
            new ArrayList<WeightedValue<Evaluator<Double, Double>>>();
        instance =
            new DummyWeightedEnsemble<Evaluator<Double, Double>>(
            members);
        assertSame(members, instance.getMembers());
    }

    /**
     * Test of clone method, of class AbstractWeightedEnsemble.
     */
    @Test
    public void testClone()
    {
        AbstractWeightedEnsemble<Evaluator<Double, Double>> instance =
            new DummyWeightedEnsemble<Evaluator<Double, Double>>();

        AbstractWeightedEnsemble<Evaluator<Double, Double>> clone =
            instance.clone();

        assertNotSame(instance, clone);
        assertEquals(instance.getMembers(), clone.getMembers());
        assertNotSame(instance.getMembers(), clone.getMembers());
        assertNotSame(instance.getMembers(), clone.getMembers());
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of add method, of class AbstractWeightedEnsemble.
     */
    @Test
    public void testAdd()
    {
        AbstractWeightedEnsemble<Evaluator<Double, Double>> instance =
            new DummyWeightedEnsemble<Evaluator<Double, Double>>();
        assertTrue(instance.getMembers().isEmpty());

        LinearFunction member = new LinearFunction();
        instance.add(member);
        assertEquals(1, instance.getMembers().size());
        assertEquals(1.0, instance.getMembers().get(0).getWeight(), 0.0);
        assertSame(member, instance.getMembers().get(0).getValue());

        member = new LinearFunction(2.0, 1.0);
        double weight = 3.4;
        instance.add(member, weight);
        assertEquals(2, instance.getMembers().size());
        assertEquals(weight, instance.getMembers().get(1).getWeight(), 0.0);
        assertSame(member, instance.getMembers().get(1).getValue());

        member = new LinearFunction(-3.0, 3.0);
        weight = -1.1;
        instance.add(member, weight);
        assertEquals(3, instance.getMembers().size());
        assertEquals(weight, instance.getMembers().get(2).getWeight(), 0.0);
        assertSame(member, instance.getMembers().get(2).getValue());

        boolean exceptionThrown = false;
        exceptionThrown = false;
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
        assertEquals(3, instance.getMembers().size());
    }

    /**
     * Test of getWeightSum method, of class AbstractWeightedEnsemble.
     */
    @Test
    public void testGetWeightSum()
    {
        AbstractWeightedEnsemble<Evaluator<Double, Double>> instance =
            new DummyWeightedEnsemble<Evaluator<Double, Double>>();
        assertEquals(0.0, instance.getWeightSum(), 0.0);

        
    }

    /**
     * Test of getMembers method, of class AbstractWeightedEnsemble.
     */
    @Test
    public void testGetMembers()
    {
        this.testSetMembers();
    }

    /**
     * Test of setMembers method, of class AbstractWeightedEnsemble.
     */
    @Test
    public void testSetMembers()
    {
        AbstractWeightedEnsemble<Evaluator<Double, Double>> instance =
            new DummyWeightedEnsemble<Evaluator<Double, Double>>();
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());

        List<WeightedValue<Evaluator<Double, Double>>> members =
            new ArrayList<WeightedValue<Evaluator<Double, Double>>>();
        instance.setMembers(members);
        assertSame(members, instance.getMembers());

        members = null;
        instance.setMembers(members);
        assertSame(members, instance.getMembers());
    }

    /**
     * A dummy pass-through class.
     *
     * @param   <MemberType>
     *      The member type.
     */
    public static class DummyWeightedEnsemble<MemberType>
        extends AbstractWeightedEnsemble<MemberType>
    {

        public DummyWeightedEnsemble()
        {
            super();
        }

        public DummyWeightedEnsemble(
            final List<WeightedValue<MemberType>> members)
        {
            super(members);
        }

    }

}