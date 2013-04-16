/*
 * File:            IterationPerformanceReporter.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.algorithm.event;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.util.NamedValue;
import java.io.PrintStream;

/**
 * An iterative algorithm listeners for {@code MeasurablePerformanceAlgorithm}
 * objects that reports the performance of the algorithm at the end of each
 * iteration.
 *
 * @author  Justin Basilico
 * @since   3.3.3
 * @see     MeasurablePerformanceAlgorithm
 * @see     IterativeAlgorithm
 */
public class IterationMeasurablePerformanceReporter
    extends AbstractIterativeAlgorithmListener
{

    /** The default format for reporting performance is {@value}. */
    public static final String DEFAULT_FORMAT = "Iteration %d. %s: %s";

    /** The print stream to report performance to. */
    protected PrintStream out;

    /** The format for the performance report, passed to String.format.  */
    protected String format;

    /**
     * Creates a new {@code IterationMeasurablePerformanceReporter} that
     * reports to System.out using the default format.
     */
    public IterationMeasurablePerformanceReporter()
    {
        this(System.out);
    }

    /**
     * Creates a new {@code IterationMeasurablePerformanceReporter} that
     * reports to the given print stream using the default format.
     *
     * @param   out
     *      Print stream to report the status to.
     */
    public IterationMeasurablePerformanceReporter(
        final PrintStream out)
    {
        this(out, DEFAULT_FORMAT);
    }

    /**
     * Creates a new {@code IterationMeasurablePerformanceReporter} that
     * reports to System.out and the given format.
     *
     * @param   format
     *      Format string for the status messages, which passed to
     *      {@code String.format}.
     */
    public IterationMeasurablePerformanceReporter(
        final String format)
    {
        this(System.out, format);
    }

    /**
     * Creates a new {@code IterationMeasurablePerformanceReporter} that
     * reports to the given print stream and format.
     *
     * @param   out
     *      Print stream to report the status to.
     * @param   format
     *      Format string for the status messages, which passed to
     *      {@code String.format}.
     */
    public IterationMeasurablePerformanceReporter(
        final PrintStream out,
        final String format)
    {
        super();

        this.out = out;
        this.format = format;
    }

    @Override
    public void stepEnded(
        final IterativeAlgorithm algorithm)
    {
        final int iteration = algorithm.getIteration();
        final MeasurablePerformanceAlgorithm measurableAlgorithm =
            (MeasurablePerformanceAlgorithm) algorithm;

        // Be safe for handling null perfomance.
        final NamedValue<?> performance = measurableAlgorithm.getPerformance();
        String name = performance == null ? "" : performance.getName();
        if (name == null)
        {
            name = "";
        }
        final Object value = performance == null ? null : performance.getValue();
        this.out.println(String.format(format, iteration, name, value));
    }

}
