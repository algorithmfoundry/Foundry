/*
 * File:            IterationMeasurablePerformanceReporterTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2012 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.algorithm.event;

import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.algorithm.AbstractIterativeAlgorithm;
import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.util.NamedValue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class IterationMeasurablePerformanceReporter.
 * 
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class IterationMeasurablePerformanceReporterTest
{

    /**
     * Creates a new test.
     */
    public IterationMeasurablePerformanceReporterTest()
    {
        super();
    }

    /**
     * Test of constructors of class IterationMeasurablePerformanceReporter.
     */
    @Test
    public void testConstructors()
    {
        PrintStream out = System.out;
        String format = IterationMeasurablePerformanceReporter.DEFAULT_FORMAT;
        IterationMeasurablePerformanceReporter instance =
            new IterationMeasurablePerformanceReporter();
        assertSame(out, instance.out);
        assertEquals(format, instance.format);

        out = new PrintStream(new ByteArrayOutputStream());
        instance = new IterationMeasurablePerformanceReporter(out);
        assertSame(out, instance.out);
        assertEquals(format, instance.format);

        out = System.out;
        format =
            "This is my custom message: %d is the iteration and the measurable performance of %s is %s.";
        instance = new IterationMeasurablePerformanceReporter(format);
        assertSame(out, instance.out);
        assertEquals(format, instance.format);

        out = new PrintStream(new ByteArrayOutputStream());
        instance = new IterationMeasurablePerformanceReporter(out, format);
        assertSame(out, instance.out);
        assertEquals(format, instance.format);
    }

    /**
     * Test of stepEnded method, of class IterationMeasurablePerformanceReporter.
     */
    @Test
    public void testStepEnded()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream outPrintStream = new PrintStream(out);
        IterationMeasurablePerformanceReporter instance =
            new IterationMeasurablePerformanceReporter(outPrintStream);
        assertEquals("", out.toString());

        DummyAlgorithm algorithm = new DummyAlgorithm();

        instance.algorithmStarted(algorithm);
        assertEquals("", out.toString());

        algorithm.setIteration(1);
        algorithm.setPerformance(DefaultNamedValue.create("n", 1.2));
        instance.stepStarted(algorithm);
        assertEquals("", out.toString());
        instance.stepEnded(algorithm);
        assertEquals("Iteration 1. n: 1.2\n", out.toString());

        algorithm.setIteration(2);
        algorithm.setPerformance(DefaultNamedValue.create("q", -3.4));
        instance.stepStarted(algorithm);
        assertEquals("Iteration 1. n: 1.2\n", out.toString());
        instance.stepEnded(algorithm);
        assertEquals("Iteration 1. n: 1.2\nIteration 2. q: -3.4\n", out.toString());

        algorithm.setIteration(104);
        instance.stepStarted(algorithm);
        assertEquals("Iteration 1. n: 1.2\nIteration 2. q: -3.4\n", out.toString());
        algorithm.setPerformance(DefaultNamedValue.create("r", 7));
        instance.stepEnded(algorithm);
        assertEquals("Iteration 1. n: 1.2\nIteration 2. q: -3.4\nIteration 104. r: 7\n", out.toString());

        instance.algorithmEnded(algorithm);
        assertEquals("Iteration 1. n: 1.2\nIteration 2. q: -3.4\nIteration 104. r: 7\n", out.toString());

        out.reset();
        algorithm = new DummyAlgorithm();


        instance.algorithmStarted(algorithm);
        instance.stepStarted(algorithm);
        assertEquals("", out.toString());
        instance.stepEnded(algorithm);
        assertEquals("Iteration 0. : null\n", out.toString());

        algorithm.setIteration(-1);
        algorithm.setPerformance(DefaultNamedValue.create("abc", (Integer) null));
        instance.stepStarted(algorithm);
        instance.stepEnded(algorithm);
        assertEquals("Iteration 0. : null\nIteration -1. abc: null\n", out.toString());


        algorithm.setIteration(2);
        algorithm.setPerformance(DefaultNamedValue.create(null, 3));
        instance.stepStarted(algorithm);
        instance.stepEnded(algorithm);
        assertEquals("Iteration 0. : null\nIteration -1. abc: null\nIteration 2. : 3\n", out.toString());

        instance.algorithmEnded(algorithm);
        assertEquals("Iteration 0. : null\nIteration -1. abc: null\nIteration 2. : 3\n", out.toString());

        out.reset();
        instance = new IterationMeasurablePerformanceReporter(outPrintStream, "i=%d,%s=%d");

        algorithm.setIteration(321);
        algorithm.setPerformance(DefaultNamedValue.create("q", 3));
        instance.algorithmStarted(algorithm);
        instance.stepStarted(algorithm);
        instance.stepEnded(algorithm);
        assertEquals("i=321,q=3\n", out.toString());

        algorithm.setIteration(322);
        algorithm.setPerformance(DefaultNamedValue.create("zzz", 777));
        instance.stepStarted(algorithm);
        instance.stepEnded(algorithm);
        assertEquals("i=321,q=3\ni=322,zzz=777\n", out.toString());

        instance.algorithmEnded(algorithm);
        assertEquals("i=321,q=3\ni=322,zzz=777\n", out.toString());

        out.reset();
        instance.algorithmStarted(algorithm);
        instance.algorithmEnded(algorithm);
        assertEquals("", out.toString());

    }

    /**
     * A support class for testing the status reporter.
     */
    protected static class DummyAlgorithm
        extends AbstractIterativeAlgorithm
        implements MeasurablePerformanceAlgorithm
    {

        protected NamedValue<? extends Number> performance;

        /**
         * Creates a new {@code DummyIterativeAlgorithm}.
         */
        protected DummyAlgorithm()
        {
            super();
        }

        @Override
        public void setIteration(
            final int iteration)
        {
            super.setIteration(iteration);
        }

        @Override
        public NamedValue<? extends Number> getPerformance()
        {
            return this.performance;
        }

        /**
         * Sets the performance.
         *
         * @param   performance
         *      The performance.
         */
        public void setPerformance(
            final NamedValue<? extends Number> performance)
        {
            this.performance = performance;
        }

    }

}
