/*
 * File:                AbstractAnytimeBatchLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.algorithm.AbstractAnytimeAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;

/**
 * The {@code AbstractAnytimeBatchLearner} abstract class 
 * implements a standard method for conforming to the {@code BatchLearner} and
 * {@code AnytimeLearner} ({@code IterativeAlgorithm} and 
 * {@code StoppableAlgorithm}) interfaces. In doing so it implements 
 * the logic to handle the events that are fired for IterativeLearner and the 
 * method calls required for {@code StoppableAlgorithm} inside of the learn 
 * method for the {@code BatchLearner} interface. This means that classes that 
 * extend this abstract class can focus on the implementation of the logic of 
 * the learning algorithm and get conformance to these useful interfaces without 
 * any real effort.
 * <BR><BR>
 * Classes that extend this class must implement the following methods:
 * <UL>
 *     <LI>{@code initializeLearning} - Initializes the learning algorithm.</LI>
 *     <LI>{@code step} - Performs a single step of the learning algorithm.</LI>
 *     <LI>{@code cleanupLearning} - Clean up after learning has been completed.</LI>
 *     <LI>{@code getResult} - Get the result of learning.</LI>
 * </UL>
 * <BR><BR>
 * The general design pattern for this class that implementing classes should
 * follow is to store all of the main state information of the algorithm as
 * fields so that each of the different methods listed above will
 * modify the state of the algorithm.
 *
 * @param   <ResultType> The type of object created by the learning algorithm.
 *          For example, a {@code FeedforwardNeuralNetwork}.
 * @param   <DataType> The type of the data that the algorithm uses to perform
 *          the learning. For example, a 
 *          {@code Collection<InputOutputPair<Vector, Double>>} or 
 *          {@code String}.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments={
        "Added some HTML formatting to the javadoc, removed useless javadoc.",
        "Code looks fine."
    }
)
public abstract class AbstractAnytimeBatchLearner<DataType, ResultType>
    extends AbstractAnytimeAlgorithm<ResultType>
    implements AnytimeBatchLearner<DataType,ResultType>
{
    // Note: The fields in this class and some of its setter methods are 
    // exposed as protected so that high-performance implementations of 
    // sub-classes can be created.
    
    /** Indicates whether or not the learner should make another step. */
    protected boolean keepGoing;

    /** The data to learn from. */
    protected DataType data;

    // Note: The class has no default constructor so that each extending
    // algorithm must define their own default maximum number of iterations.
    
    /**
     * Creates a new instance of {@code AbstractAnytimeBatchLearner}.
     *
     * @param   maxIterations The maximum number of iterations, must be greater
     *          than zero.
     */
    protected AbstractAnytimeBatchLearner(
        int maxIterations)
    {
        super( maxIterations );
        this.setKeepGoing(false);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public AbstractAnytimeBatchLearner<DataType, ResultType> clone()
    {
        AbstractAnytimeBatchLearner<DataType, ResultType> result = 
            (AbstractAnytimeBatchLearner<DataType, ResultType>) 
            super.clone();
        result.keepGoing = false;
        result.data = null;
        return result;
    }

    public ResultType learn(
        DataType data)
    {
        // Set the data that is to be used in a field.
        this.setData(data);

        // Set up the looping fields. We do this before firing the learning
        // started event so that a listener can stop learning before it 
        // takes any steps.
        this.setIteration(0);
        this.setKeepGoing(true);

        // Attempt to initialize learning
        boolean initialized = this.initializeAlgorithm();

        if (!initialized)
        {
            // The data was not valid.
            return null;
        }

        // We are about to start learning.
        this.fireAlgorithmStarted();

        try
        {
            // This is the main learning loop. The keepGoing field determines the
            // stoping criteria.
            while (this.getKeepGoing())
            {
                // We are making a new iteration so increment the counter.
                this.setIteration(this.getIteration() + 1);

                // The step has started.
                this.fireStepStarted();

                boolean stepReturn = false;
                try
                {
                    // Take the step. The return value is true if the algorithm
                    // thinks it can keep going.
                    stepReturn = this.step();
                }
                finally
                {
                    // The step has ended.
                    this.fireStepEnded();
                }

                // Update the keepGoing field based on its current value,
                // if the algorithm's return value, and the iteration counter.
                this.setKeepGoing(
                       this.getKeepGoing()
                    && stepReturn
                    && this.getIteration() < this.getMaxIterations());
            }
        }
        finally
        {
            // Learning has finished.
            this.fireAlgorithmEnded();
        }
        
        // Clean up any extra data.
        this.cleanupAlgorithm();

        // The data field is no longer needed.
        this.setData(null);

        // Return the learned value.
        return this.getResult();
    }

    /**
     * Called to initialize the learning algorithm's state based on the
     * data that is stored in the data field. The return value indicates if the
     * algorithm can be run or not based on the initialization.
     *
     * @return  True if the learning algorithm can be run and false if it 
     *          cannot.
     */
    protected abstract boolean initializeAlgorithm();

    /**
     * Called to take a single step of the learning algorithm.
     *
     * @return  True if another step can be taken and false it the algorithm
     *          should halt.
     */
    protected abstract boolean step();

    /**
     * Called to clean up the learning algorithm's state after learning has
     * finished.
     */
    protected abstract void cleanupAlgorithm();

    public void stop()
    {
        this.setKeepGoing(false);
    }

    public boolean getKeepGoing()
    {
        return this.keepGoing;
    }

    /**
     * Sets the keep going value, which indicates if the algorithm should
     * continue on to another step.
     *
     * @param   keepGoing The keep going value.
     */
    public void setKeepGoing(
        boolean keepGoing)
    {
        this.keepGoing = keepGoing;
    }

    public DataType getData()
    {
        return this.data;
    }

    /**
     * Gets the data to use for learning. This is set when learning starts
     * and then cleared out once learning is finished.
     *
     * @param   data The data to use for learning.
     */
    protected void setData(
        DataType data)
    {
        this.data = data;
    }

}
