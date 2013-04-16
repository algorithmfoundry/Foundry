/*
 * File:            IterationStatusReporter.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.algorithm.event;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import java.io.PrintStream;

/**
 * An iterative algorithm listener that reports the start of each iteration
 * to the given print stream.
 *
 * @author  Justin Basilico
 * @version 3.4.0
 * @see     IterativeAlgorithm
 */
public class IterationStartReporter
    extends AbstractIterativeAlgorithmListener
{

    /** The default format is {@value}. */
    public static final String DEFAULT_FORMAT = "Iteration %d";

    /** The print stream to report performance to. */
    protected PrintStream out;

    /** The format for the performance report, passed to String.format.  */
    protected String format;

    /**
     * Creates a new {@code IterationStartReporter} that
     * reports to System.out using the default format.
     */
    public IterationStartReporter()
    {
        this(System.out);
    }

    /**
     * Creates a new {@code IterationStartReporter} that
     * reports to the given print stream using the default format.
     *
     * @param   out
     *      Print stream to report the status to.
     */
    public IterationStartReporter(
        final PrintStream out)
    {
        this(out, DEFAULT_FORMAT);
    }

    /**
     * Creates a new {@code IterationStartReporter} that
     * reports to System.out and the given format.
     *
     * @param   format
     *      Format string for the status messages, which passed to
     *      {@code String.format}.
     */
    public IterationStartReporter(
        final String format)
    {
        this(System.out, format);
    }

    /**
     * Creates a new {@code IterationStartReporter} that
     * reports to the given print stream and format.
     *
     * @param   out
     *      Print stream to report the status to.
     * @param   format
     *      Format string for the status messages, which passed to
     *      {@code String.format}.
     */
    public IterationStartReporter(
        final PrintStream out,
        final String format)
    {
        super();

        this.out = out;
        this.format = format;
    }

    @Override
    public void stepStarted(
        final IterativeAlgorithm algorithm)
    {
        this.out.println(String.format(this.format, algorithm.getIteration()));
    }

}
