/*
 * File:            AbstractUnweightedEnsembleTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import gov.sandia.cognition.evaluator.Evaluator;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractUnweightedEnsemble.
 *
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class AbstractUnweightedEnsembleTest
    extends Object
{

    /**
     * Creates a new test.
     */
    public AbstractUnweightedEnsembleTest()
    {
    }

    /**
     * Test of constructors of class AbstractUnweightedEnsemble.
     */
    @Test
    public void testConstructors()
    {
        AbstractUnweightedEnsemble<Evaluator<Double, Double>> instance =
            new DummyUnweightedEnsemble<Evaluator<Double, Double>>();
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());

        List<Evaluator<Double, Double>> members =
            new ArrayList<Evaluator<Double, Double>>();
        instance =
            new DummyUnweightedEnsemble<Evaluator<Double, Double>>(
            members);
        assertSame(members, instance.getMembers());
    }

    /**
     * Test of clone method, of class AbstractUnweightedEnsemble.
     */
    @Test
    public void testClone()
    {
        AbstractUnweightedEnsemble<Evaluator<Double, Double>> instance =
            new DummyUnweightedEnsemble<Evaluator<Double, Double>>();

        AbstractUnweightedEnsemble<Evaluator<Double, Double>> clone =
            instance.clone();

        assertNotSame(instance, clone);
        assertEquals(instance.getMembers(), clone.getMembers());
        assertNotSame(instance.getMembers(), clone.getMembers());
        assertNotSame(instance.getMembers(), clone.getMembers());
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of add method, of class AbstractUnweightedEnsemble.
     */
    @Test
    public void testAdd()
    {
        AbstractUnweightedEnsemble<Evaluator<Double, Double>> instance =
            new DummyUnweightedEnsemble<Evaluator<Double, Double>>();
        assertTrue(instance.getMembers().isEmpty());

        LinearFunction member = new LinearFunction();
        instance.add(member);
        assertEquals(1, instance.getMembers().size());
        assertSame(member, instance.getMembers().get(0));

        member = new LinearFunction(2.0, 1.0);
        instance.add(member);
        assertEquals(2, instance.getMembers().size());
        assertSame(member, instance.getMembers().get(1));

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
    }

    /**
     * Test of getMembers method, of class AbstractUnweightedEnsemble.
     */
    @Test
    public void testGetMembers()
    {
        this.testSetMembers();
    }

    /**
     * Test of setMembers method, of class AbstractUnweightedEnsemble.
     */
    @Test
    public void testSetMembers()
    {
        AbstractUnweightedEnsemble<Evaluator<Double, Double>> instance =
            new DummyUnweightedEnsemble<Evaluator<Double, Double>>();
        assertNotNull(instance.getMembers());
        assertTrue(instance.getMembers().isEmpty());

        List<Evaluator<Double, Double>> members =
            new ArrayList<Evaluator<Double, Double>>();
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
    public static class DummyUnweightedEnsemble<MemberType>
        extends AbstractUnweightedEnsemble<MemberType>
    {

        public DummyUnweightedEnsemble()
        {
            super();
        }

        public DummyUnweightedEnsemble(
            final List<MemberType> members)
        {
            super(members);
        }

    }

}
