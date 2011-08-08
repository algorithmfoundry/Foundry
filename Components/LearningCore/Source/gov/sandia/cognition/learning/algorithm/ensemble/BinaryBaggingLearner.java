/*
 * File:                BinaryBaggingLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.util.Randomized;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * The {@code BinaryBaggingLearner} implements the Bagging learning algorithm.
 * At each step, the algorithm creates a "bag" of data by sampling from the
 * given data with replacement. It then passes the bag of data to the given
 * learner to learn a new binary categorizer, which it then adds to the 
 * ensemble. All learners are given an equal weight of 1.0.
 *
 * @param <InputType> Input class of the {@code Collection<InputOutputPairs>}
 * for the dataset, for example, something like Vector or String
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Cleaned up javadoc a little bit with code annotations.",
        "Otherwise, looks fine."
    }
)
public class BinaryBaggingLearner<InputType>
    extends AbstractAnytimeBatchLearner
        <Collection<? extends InputOutputPair<? extends InputType,Boolean>>, 
            WeightedBinaryEnsemble<InputType>>
    implements Randomized
{

    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /** The learner to use to create the categorizer for each iteration. */
    protected SupervisedBatchLearner<InputType, Boolean, ?> learner;

    /** The random number generator to use. */
    protected Random random;

    /** The ensemble being created by the learner. */
    private WeightedBinaryEnsemble<InputType> ensemble;

    /** The data stored for efficient random access. */
    private ArrayList<InputOutputPair<? extends InputType, Boolean>> dataList;

    /**
     * Creates a new instance of BinaryBaggingLearner.
     */
    public BinaryBaggingLearner()
    {
        this(null);
    }

    /**
     * Creates a new instance of BinaryBaggingLearner.
     *
     * @param  learner 
     *      The learner to use to create the categorizer on each iteration.
     */
    public BinaryBaggingLearner(
        final SupervisedBatchLearner<InputType, Boolean, ?> learner )
    {
        this(learner, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new instance of BinaryBaggingLearner.
     *
     * @param  learner 
     *      The learner to use to create the categorizer on each iteration.
     * @param  maxIterations 
     *      The maximum number of iterations to run for, which is also the 
     *      number of learners to create.
     */
    public BinaryBaggingLearner(
        final SupervisedBatchLearner<InputType, Boolean, ?> learner,
        final int maxIterations)
    {
        this(learner, maxIterations, new Random());
    }

    /**
     * Creates a new instance of BinaryBaggingLearner.
     *
     * @param  learner 
     *      The learner to use to create the categorizer on each iteration.
     * @param  maxIterations 
     *      The maximum number of iterations to run for, which is also the 
     *      number of learners to create.
     * @param  random 
     *      The random number generator to use.
     */
    public BinaryBaggingLearner(
        final SupervisedBatchLearner<InputType, Boolean, ?> learner,
        final int maxIterations,
        final Random random)
    {
        super(maxIterations);

        this.setLearner(learner);
        this.setRandom(random);

        this.setEnsemble(null);
        this.setDataList(null);
    }

    protected boolean initializeAlgorithm()
    {
        if (this.getData() == null || this.getData().size() <= 0)
        {
            // This is an invalid dataset.
            return false;
        }

        // Create the ensemble where we will be storing the output.
        WeightedBinaryEnsemble<InputType> localEmsemble = new WeightedBinaryEnsemble<InputType>(
            new ArrayList<WeightedValue<? extends Evaluator<? super InputType,? extends Boolean>>>(
                this.getMaxIterations()));
        this.setEnsemble(localEmsemble);

        // Create a random-access version of the data.
        this.setDataList(
            new ArrayList<InputOutputPair<? extends InputType,Boolean>>(
            this.getData()));

        return true;
    }

    protected boolean step()
    {
        // We are going to create a bag that is the same size as the data that
        // we have.
        int count = this.getDataList().size();
        ArrayList<InputOutputPair<? extends InputType, Boolean>> bag =
            new ArrayList<InputOutputPair<? extends InputType, Boolean>>(count);

        // Create the bag by sampling with replacement.
        for (int i = 0; i < count; i++)
        {
            int index = this.getRandom().nextInt(count);
            InputOutputPair<? extends InputType, Boolean> example =
                this.dataList.get(index);
            bag.add(example);
        }

        // Learn the categorizer on the new bag of data.
        Evaluator<? super InputType,? extends Boolean> learned = this.learner.learn(bag);

        // Add the categorizer to the ensemble and give it equal weight.
        this.ensemble.add(learned, 1.0);

        // We keep going until we've created the requested number of members,
        // which is checked by the super-class.
        return true;
    }

    protected void cleanupAlgorithm()
    {
        // To clean up we remove the reference to the copy of the data 
        // collection that we made.
        this.setDataList(null);
    }

    public WeightedBinaryEnsemble<InputType> getResult()
    {
        return this.getEnsemble();
    }

    /**
     * Gets the learner to use to create the categorizer for each iteration.
     *
     * @return The learner used by the algorithm.
     */
    public SupervisedBatchLearner<InputType, Boolean, ?> getLearner()
    {
        return this.learner;
    }

    /**
     * Sets the learner to use to create the categorizer for each iteration.
     *
     * @param   learner The learner for the algorithm to use.
     */
    public void setLearner(
        final SupervisedBatchLearner<InputType, Boolean, ?> learner)
    {
        this.learner = learner;
    }

    public Random getRandom()
    {
        return this.random;
    }

    public void setRandom(
        final Random random)
    {
        this.random = random;
    }

    /**
     * Gets the ensemble created by this learner.
     *
     * @return The ensemble created by this learner.
     */
    public WeightedBinaryEnsemble<InputType> getEnsemble()
    {
        return this.ensemble;
    }

    /**
     * Sets the ensemble created by this learner.
     *
     * @param  ensemble The ensemble created by this learner.
     */
    protected void setEnsemble(
        final WeightedBinaryEnsemble<InputType> ensemble)
    {
        this.ensemble = ensemble;
    }

    /**
     * Gets the data the learner is using as an array list.
     *
     * @return The data as an array list.
     */
    public ArrayList<InputOutputPair<? extends InputType, Boolean>> 
        getDataList()
    {
        return this.dataList;
    }

    /**
     * Sets the data the learner is using as an array list.
     *
     * @param  dataList The data as an array list.
     */
    protected void setDataList(
        final ArrayList<InputOutputPair<? extends InputType, Boolean>> dataList)
    {
        this.dataList = dataList;
    }

}
