/*
 * File:                ConfusionMatrixPerformanceEvaluatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 19, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.learning.data.DefaultTargetEstimatePair;
import java.util.ArrayList;
import gov.sandia.cognition.factory.DefaultFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.factory.Factory;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class ConfusionMatrixPerformanceEvaluator.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class ConfusionMatrixPerformanceEvaluatorTest
{
    /**
     * Creates a new test.
     */
    public ConfusionMatrixPerformanceEvaluatorTest()
    {
    }

    /**
     * Test of constructors of class ConfusionMatrixPerformanceEvaluator.
     */
    @Test
    public void testConstructors()
    {
        ConfusionMatrixPerformanceEvaluator<Vector, Boolean> instance =
            new ConfusionMatrixPerformanceEvaluator<Vector, Boolean>();
        assertTrue(instance.getFactory().create() instanceof DefaultConfusionMatrix<?>);

        Factory<? extends ConfusionMatrix<Boolean>> factory =
            DefaultFactory.get(DefaultBinaryConfusionMatrix.class);
        instance = new ConfusionMatrixPerformanceEvaluator<Vector, Boolean>(factory);
        assertSame(factory, instance.getFactory());
    }

    /**
     * Test of evaluatePerformance method, of class ConfusionMatrixPerformanceEvaluator.
     */
    @Test
    public void testEvaluatePerformance()
    {
        double epsilon = 1e-10;
        ConfusionMatrixPerformanceEvaluator<?, String> instance =
            new ConfusionMatrixPerformanceEvaluator<String, String>();

        Collection<TargetEstimatePair<String, String>> data =
            new ArrayList<TargetEstimatePair<String, String>>();

        ConfusionMatrix<String> confusion = instance.summarize(data);
        assertEquals(0.0, confusion.getTotalCount(), 0.0);

        data.add(new DefaultTargetEstimatePair<String, String>("yes", "no"));
        confusion = instance.summarize(data);
        assertEquals(1.0, confusion.getTotalCount(), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(1.0 / 1.0, confusion.getErrorRate(), 0.0);

        data.add(new DefaultTargetEstimatePair<String, String>("yes", "yes"));
        confusion = instance.summarize(data);
        assertEquals(2.0, confusion.getTotalCount(), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "yes"), 0.0);
        assertEquals(1.0 / 2.0, confusion.getErrorRate(), epsilon);

        data.add(new DefaultTargetEstimatePair<String, String>("no", "no"));
        confusion = instance.summarize(data);
        assertEquals(3.0, confusion.getTotalCount(), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "yes"), 0.0);
        assertEquals(1.0, confusion.getCount("no", "no"), 0.0);
        assertEquals(1.0 / 3.0, confusion.getErrorRate(), epsilon);

        data.add(new DefaultTargetEstimatePair<String, String>("something", "else"));
        confusion = instance.summarize(data);
        assertEquals(4.0, confusion.getTotalCount(), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "yes"), 0.0);
        assertEquals(1.0, confusion.getCount("no", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("something", "else"), 0.0);
        assertEquals(2.0 / 4.0, confusion.getErrorRate(), epsilon);

        data.add(new DefaultTargetEstimatePair<String, String>("same", "same"));
        confusion = instance.summarize(data);
        assertEquals(5.0, confusion.getTotalCount(), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "yes"), 0.0);
        assertEquals(1.0, confusion.getCount("no", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("something", "else"), 0.0);
        assertEquals(1.0, confusion.getCount("same", "same"), 0.0);
        assertEquals(2.0 / 5.0, confusion.getErrorRate(), epsilon);

        data.add(new DefaultTargetEstimatePair<String, String>("oh", "no"));
        confusion = instance.summarize(data);
        assertEquals(6.0, confusion.getTotalCount(), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "yes"), 0.0);
        assertEquals(1.0, confusion.getCount("no", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("something", "else"), 0.0);
        assertEquals(1.0, confusion.getCount("same", "same"), 0.0);
        assertEquals(1.0, confusion.getCount("oh", "no"), 0.0);
        assertEquals(3.0 / 6.0, confusion.getErrorRate(), epsilon);

        data.add(new DefaultTargetEstimatePair<String, String>("this", "bad"));
        confusion = instance.summarize(data);
        assertEquals(7.0, confusion.getTotalCount(), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "yes"), 0.0);
        assertEquals(1.0, confusion.getCount("no", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("something", "else"), 0.0);
        assertEquals(1.0, confusion.getCount("same", "same"), 0.0);
        assertEquals(1.0, confusion.getCount("oh", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("this", "bad"), 0.0);
        assertEquals(4.0 / 7.0, confusion.getErrorRate(), epsilon);

        data.add(new DefaultTargetEstimatePair<String, String>("not null", null));
        confusion = instance.summarize(data);
        assertEquals(8.0, confusion.getTotalCount(), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "yes"), 0.0);
        assertEquals(1.0, confusion.getCount("no", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("something", "else"), 0.0);
        assertEquals(1.0, confusion.getCount("same", "same"), 0.0);
        assertEquals(1.0, confusion.getCount("oh", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("this", "bad"), 0.0);
        assertEquals(1.0, confusion.getCount("not null", null), 0.0);
        assertEquals(5.0 / 8.0, confusion.getErrorRate(), epsilon);

        data.add(new DefaultTargetEstimatePair<String, String>(null, "not null"));
        confusion = instance.summarize(data);
        assertEquals(9.0, confusion.getTotalCount(), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "yes"), 0.0);
        assertEquals(1.0, confusion.getCount("no", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("something", "else"), 0.0);
        assertEquals(1.0, confusion.getCount("same", "same"), 0.0);
        assertEquals(1.0, confusion.getCount("oh", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("this", "bad"), 0.0);
        assertEquals(1.0, confusion.getCount("not null", null), 0.0);
        assertEquals(1.0, confusion.getCount(null, "not null"), 0.0);
        assertEquals(6.0 / 9.0, confusion.getErrorRate(), epsilon);

        data.add(new DefaultTargetEstimatePair<String, String>(null, null));
        confusion = instance.summarize(data);
        assertEquals(10.0, confusion.getTotalCount(), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("yes", "yes"), 0.0);
        assertEquals(1.0, confusion.getCount("no", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("something", "else"), 0.0);
        assertEquals(1.0, confusion.getCount("same", "same"), 0.0);
        assertEquals(1.0, confusion.getCount("oh", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("this", "bad"), 0.0);
        assertEquals(1.0, confusion.getCount("not null", null), 0.0);
        assertEquals(1.0, confusion.getCount(null, "not null"), 0.0);
        assertEquals(1.0, confusion.getCount(null, null), 0.0);
        assertEquals(6.0 / 10.0, confusion.getErrorRate(), epsilon);

        data.add(new DefaultTargetEstimatePair<String, String>("yes", "no"));
        data.add(new DefaultTargetEstimatePair<String, String>("yes", "no"));
        data.add(new DefaultTargetEstimatePair<String, String>("yes", "yes"));
        data.add(new DefaultTargetEstimatePair<String, String>("no", "no"));
        confusion = instance.summarize(data);
        assertEquals(14.0, confusion.getTotalCount(), 0.0);
        assertEquals(3.0, confusion.getCount("yes", "no"), 0.0);
        assertEquals(2.0, confusion.getCount("yes", "yes"), 0.0);
        assertEquals(2.0, confusion.getCount("no", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("something", "else"), 0.0);
        assertEquals(1.0, confusion.getCount("same", "same"), 0.0);
        assertEquals(1.0, confusion.getCount("oh", "no"), 0.0);
        assertEquals(1.0, confusion.getCount("this", "bad"), 0.0);
        assertEquals(1.0, confusion.getCount("not null", null), 0.0);
        assertEquals(1.0, confusion.getCount(null, "not null"), 0.0);
        assertEquals(1.0, confusion.getCount(null, null), 0.0);
        assertEquals(8.0 / 14.0, confusion.getErrorRate(), epsilon);
    }

    /**
     * Test of getFactory method, of class ConfusionMatrixPerformanceEvaluator.
     */
    @Test
    public void testGetFactory()
    {
        this.testSetFactory();
    }

    /**
     * Test of setFactory method, of class ConfusionMatrixPerformanceEvaluator.
     */
    @Test
    public void testSetFactory()
    {
        ConfusionMatrixPerformanceEvaluator<Vector, Boolean> instance =
            new ConfusionMatrixPerformanceEvaluator<Vector, Boolean>();
        assertTrue(instance.getFactory().create() instanceof DefaultConfusionMatrix<?>);

        Factory<? extends ConfusionMatrix<Boolean>> factory =
            DefaultFactory.get(DefaultBinaryConfusionMatrix.class);
        instance.setFactory(factory);
        assertSame(factory, instance.getFactory());

        factory = null;
        instance.setFactory(factory);
        assertSame(factory, instance.getFactory());

        factory = new DefaultFactory<ConfusionMatrix<Boolean>>(
            DefaultBinaryConfusionMatrix.class);
        instance.setFactory(factory);
        assertSame(factory, instance.getFactory());
    }

}