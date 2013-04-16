/*
 * File:            IterationStartReporterTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2012 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.algorithm.event;

import gov.sandia.cognition.algorithm.AbstractIterativeAlgorithm;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class IterationStartReporter.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class IterationStartReporterTest
{

    /**
     * Creates a new test.
     */
    public IterationStartReporterTest()
    {
        super();
    }

    /**
     * Test of constructors of class IterationStartReporter.
     */
    @Test
    public void testConstructors()
    {
        PrintStream out = System.out;
        String format = IterationStartReporter.DEFAULT_FORMAT;
        IterationStartReporter instance = new IterationStartReporter();
        assertSame(out, instance.out);
        assertEquals(format, instance.format);

        out = new PrintStream(new ByteArrayOutputStream());
        instance = new IterationStartReporter(out);
        assertSame(out, instance.out);
        assertEquals(format, instance.format);

        out = System.out;
        format = "This is my custom message: %d is the iteration.";
        instance = new IterationStartReporter(format);
        assertSame(out, instance.out);
        assertEquals(format, instance.format);

        out = new PrintStream(new ByteArrayOutputStream());
        instance = new IterationStartReporter(out, format);
        assertSame(out, instance.out);
        assertEquals(format, instance.format);
    }

    /**
     * Test of stepStarted method, of class IterationStartReporter.
     */
    @Test
    public void testStepStarted()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream outPrintStream = new PrintStream(out);
        IterationStartReporter instance = new IterationStartReporter(outPrintStream);
        assertEquals("", out.toString());

        DummyAlgorithm algorithm = new DummyAlgorithm();

        instance.algorithmStarted(algorithm);
        assertEquals("", out.toString());

        algorithm.setIteration(1);
        instance.stepStarted(algorithm);
        assertEquals("Iteration 1\n", out.toString());
        instance.stepEnded(algorithm);

        algorithm.setIteration(2);
        instance.stepStarted(algorithm);
        assertEquals("Iteration 1\nIteration 2\n", out.toString());
        instance.stepEnded(algorithm);

        algorithm.setIteration(104);
        instance.stepStarted(algorithm);
        assertEquals("Iteration 1\nIteration 2\nIteration 104\n", out.toString());
        instance.stepEnded(algorithm);

        instance.algorithmEnded(algorithm);
        assertEquals("Iteration 1\nIteration 2\nIteration 104\n", out.toString());

        out.reset();
        algorithm = new DummyAlgorithm();


        instance.algorithmStarted(algorithm);
        instance.stepStarted(algorithm);
        assertEquals("Iteration 0\n", out.toString());
        instance.stepEnded(algorithm);

        algorithm.setIteration(-1);
        instance.stepStarted(algorithm);
        assertEquals("Iteration 0\nIteration -1\n", out.toString());
        instance.stepEnded(algorithm);

        instance.algorithmEnded(algorithm);
        assertEquals("Iteration 0\nIteration -1\n", out.toString());

        out.reset();
        instance = new IterationStartReporter(outPrintStream, "i=%d");

        algorithm.setIteration(321);
        instance.algorithmStarted(algorithm);
        instance.stepStarted(algorithm);
        assertEquals("i=321\n", out.toString());

        algorithm.setIteration(322);
        instance.stepStarted(algorithm);
        assertEquals("i=321\ni=322\n", out.toString());
        
        instance.algorithmEnded(algorithm);
        assertEquals("i=321\ni=322\n", out.toString());

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
    {
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
    }

}
