/*
 * File:            AnytimeBatchLearnerValidationPerformanceReporter.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2013 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.performance;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.algorithm.event.AbstractIterativeAlgorithmListener;
import gov.sandia.cognition.learning.algorithm.AnytimeBatchLearner;
import java.io.PrintStream;

/**
 * A performance reporter for a validation set. Can be attached to an anytime
 * algorithm to report results after each step. It reports the result of
 * evaluating performance on both the training and validation sets.
 * 
 * @param   <DataType>
 *      The type of data this reports validation performance for.
 * @param   <ObjectType>
 *      The type of object that is returned by the anytime learner that
 *      performance can be computed for.
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class AnytimeBatchLearnerValidationPerformanceReporter<DataType, ObjectType>
    extends AbstractIterativeAlgorithmListener
{
    /** The default format for reporting performance is {@value}. */
    public static final String DEFAULT_FORMAT = "Iteration %d. Train: %s. Validation: %s";
    
    /** The performance evaluator. Produces the metrics. */
    protected PerformanceEvaluator<? super ObjectType, ? super DataType, ?> performanceEvaluator;
    
    /** The validation dataset to use. */
    protected DataType validationData;
    
    /** The print stream to report performance to. */
    protected PrintStream out;
    
    /** The format for the performance report, passed to String.format.  */
    protected String format;

    /**
     * Creates a new {@code AnytimeBatchLearnerValidationPerformanceReporter} that
     * reports to the given print stream using the default format.
     *
     * @param   performanceEvaluator
     *      The performance evaluator. Produces the metrics.
     * @param   validationData
     *      The validation dataset to use.
     */
    public AnytimeBatchLearnerValidationPerformanceReporter(
        final PerformanceEvaluator<? super ObjectType, ? super DataType, ?> performanceEvaluator,
        final DataType validationData)
    {
        this(performanceEvaluator, validationData, System.out);
    }
    
    /**
     * Creates a new {@code AnytimeBatchLearnerValidationPerformanceReporter} that
     * reports to the given print stream using the default format.
     *
     * @param   performanceEvaluator
     *      The performance evaluator. Produces the metrics.
     * @param   validationData
     *      The validation dataset to use.
     * @param   out
     *      Print stream to report the status to.
     */
    public AnytimeBatchLearnerValidationPerformanceReporter(
        final PerformanceEvaluator<? super ObjectType, ? super DataType, ?> performanceEvaluator,
        final DataType validationData,
        final PrintStream out)
    {
        this(performanceEvaluator, validationData, out, DEFAULT_FORMAT);
    }

    /**
     * Creates a new {@code AnytimeBatchLearnerValidationPerformanceReporter} that
     * reports to System.out and the given format.
     *
     * @param   performanceEvaluator
     *      The performance evaluator. Produces the metrics.
     * @param   validationData
     *      The validation dataset to use.
     * @param   format
     *      Format string for the status messages, which passed to
     *      {@code String.format}.
     */
    public AnytimeBatchLearnerValidationPerformanceReporter(
        final PerformanceEvaluator<? super ObjectType, ? super DataType, ?> performanceEvaluator,
        final DataType validationData,
        final String format)
    {
        this(performanceEvaluator, validationData, System.out, format);
    }

    /**
     * Creates a new {@code AnytimeBatchLearnerValidationPerformanceReporter} that
     * reports to the given print stream and format.
     *
     * @param   performanceEvaluator
     *      The performance evaluator. Produces the metrics.
     * @param   validationData
     *      The validation dataset to use.
     * @param   out
     *      Print stream to report the status to.
     * @param   format
     *      Format string for the status messages, which passed to
     *      {@code String.format}.
     */
    public AnytimeBatchLearnerValidationPerformanceReporter(
        final PerformanceEvaluator<? super ObjectType, ? super DataType, ?> performanceEvaluator,
        final DataType validationData,
        final PrintStream out,
        final String format)
    {
        super();

        this.setPerformanceEvaluator(performanceEvaluator);
        this.setValidationData(validationData);
        this.out = out;
        this.format = format;
    }

    @Override
    public void stepEnded(
        final IterativeAlgorithm algorithm)
    {
        @SuppressWarnings("unchecked")
        final AnytimeBatchLearner<? extends DataType, ? extends ObjectType> anytimeAlgorithm =
            (AnytimeBatchLearner<? extends DataType, ? extends ObjectType>) algorithm;
        
        final Object trainPerformance =
            this.performanceEvaluator.evaluatePerformance(
                anytimeAlgorithm.getResult(), anytimeAlgorithm.getData());
        final Object validationPerformance =
            this.performanceEvaluator.evaluatePerformance(
                anytimeAlgorithm.getResult(), this.validationData);
        this.out.println(String.format(this.format, algorithm.getIteration(),
            trainPerformance, validationPerformance));
    }

    /**
     * Gets the performance evaluator. It produces the metrics.
     * 
     * @return 
     *      The performance evaluator.
     */
    public PerformanceEvaluator<? super ObjectType, ? super DataType, ?> getPerformanceEvaluator()
    {
        return this.performanceEvaluator;
    }

    /**
     * Sets the performance evaluator. It produces the metrics.
     * 
     * @param   performanceEvaluator  
     *      The performance evaluator.
     */
    public void setPerformanceEvaluator(
        final PerformanceEvaluator<? super ObjectType, ? super DataType, ?> performanceEvaluator)
    {
        this.performanceEvaluator = performanceEvaluator;
    }

    /**
     * Gets the validation dataset.
     * 
     * @return 
     *      The validation dataset.
     */
    public DataType getValidationData()
    {
        return this.validationData;
    }

    /**
     * Sets the validation dataset.
     * 
     * @param   validationData  
     *      The validation dataset.
     */
    public void setValidationData(
        final DataType validationData)
    {
        this.validationData = validationData;
    }
    
}
