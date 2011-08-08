/*
 * File:                OnlineVotedPerceptron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright October 20, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.algorithm.ensemble.WeightedBinaryEnsemble;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.DefaultWeightedValue;

/**
 * An online version of the Voted-Perceptron algorithm. It is similar to the
 * typical Perceptron algorithm except that it creates multiple Perceptrons,
 * and combines them together in a weighted vote. Whenever a mistake is made,
 * a new Perceptron is created by modifying the previous one and given a weight
 * of 1. When it gets an example correct, it simply increments the weight on
 * the most recent one.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
@PublicationReference(
    title="Large Margin Classification Using the Perceptron Algorithm",
    author={"Yoav Freund", "Robert E. Schapire" },
    year=1999,
    type=PublicationType.Journal,
    publication="Machine Learning",
    pages={277, 296},
    url="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.48.8200")
public class OnlineVotedPerceptron
    extends AbstractBatchAndIncrementalLearner<InputOutputPair<? extends Vectorizable, Boolean>, WeightedBinaryEnsemble<Vectorizable, LinearBinaryCategorizer>>
    implements VectorFactoryContainer
{

    /** The factory to create weight vectors. */
    protected VectorFactory<?> vectorFactory;

    /**
     * Creates a new {@code OnlinePerceptron}.
     */
    public OnlineVotedPerceptron()
    {
        this(VectorFactory.getDenseDefault());
    }

    /**
     * Creates a new {@code OnlinePerceptron} with the given vector factory.
     *
     * @param   vectorFactory
     *      The vector factory to use to create the weight vectors.
     */
    public OnlineVotedPerceptron(
        final VectorFactory<?> vectorFactory)
    {
        super();

        this.setVectorFactory(vectorFactory);
    }

    @Override
    public WeightedBinaryEnsemble<Vectorizable, LinearBinaryCategorizer> createInitialLearnedObject()
    {
        return new WeightedBinaryEnsemble<Vectorizable, LinearBinaryCategorizer>();
    }

    @Override
    public void update(
        final WeightedBinaryEnsemble<Vectorizable, LinearBinaryCategorizer> target,
        final InputOutputPair<? extends Vectorizable, Boolean> example)
    {
        // Get the information about the example.
        final Vector input = example.getInput().convertToVector();
        final boolean actual = example.getOutput();

        // Predict the output as a double (negative values are false, positive
        // are true).
        final double prediction = target.evaluateAsDouble(input);

        // The computation that we do is based on using the last member in
        // the ensemble.
        final DefaultWeightedValue<LinearBinaryCategorizer> lastMember =
            getLastMember(target);

        // Make an update if there was an error.
        final boolean correct =
               (actual && prediction > 0.0)
            || (!actual && prediction < 0.0);
        if (correct)
        {
            // There was no error made, so increase the weight on the latest
            // member of the ensemble.
            // Note: It should never reach here when lastMember is null because
            // then the prediction has to be zero.
            lastMember.setWeight(lastMember.getWeight() + 1.0);
        }
        else
        {
            final LinearBinaryCategorizer next;
            if (lastMember == null)
            {
                // This is the very first data point we've seen, so we need
                // to create an initial categorizer.
                next = new LinearBinaryCategorizer(
                    this.getVectorFactory().createVector(
                        input.getDimensionality()), 0.0);
            }
            else
            {
                // Clone the previous member.
                next = lastMember.getValue().clone();
            }

            if (actual)
            {
                // An error with the true (positive) category.
                next.getWeights().plusEquals(input);
                next.setBias(next.getBias() + 1.0);
            }
            else
            {
                // An error with the false (negative) category.
                next.getWeights().minusEquals(input);
                next.setBias(next.getBias() - 1.0);
            }

            // Add the new member to the ensemble.
            target.add(next, 1.0);
        }
    }

    /**
     * Gets the last member in the ensemble. This is the one used by the
     * algorithm.
     *
     * @param   ensemble
     *      The ensemble to get the last member from.
     * @return
     *      The last member in the ensemble, or null if it is empty.
     */
    public static DefaultWeightedValue<LinearBinaryCategorizer> getLastMember(
        final WeightedBinaryEnsemble<Vectorizable, LinearBinaryCategorizer> ensemble)
{
        final int ensembleSize = ensemble.getMembers().size();
        if (ensembleSize <= 0)
        {
            return null;
        }
        else
        {
            return (DefaultWeightedValue<LinearBinaryCategorizer>)
                ensemble.getMembers().get(ensembleSize - 1);}
    }

    /**
     * Gets the VectorFactory used to create the weight vector.
     *
     * @return The VectorFactory used to create the weight vector.
     */
    public VectorFactory<?> getVectorFactory()
    {
        return this.vectorFactory;
    }

    /**
     * Sets the VectorFactory used to create the weight vector.
     *
     * @param  vectorFactory The VectorFactory used to create the weight vector.
     */
    public void setVectorFactory(
        final VectorFactory<?> vectorFactory)
    {
        this.vectorFactory = vectorFactory;
    }

}
