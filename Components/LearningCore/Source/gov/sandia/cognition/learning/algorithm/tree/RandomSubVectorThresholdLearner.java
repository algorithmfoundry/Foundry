/*
 * File:                RandomSubVectorThresholdLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 06, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.Categorizer;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.Permutation;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractRandomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Learns a decision function by taking a randomly sampling a subspace from
 * a given set of input vectors and then learning a threshold function by 
 * passing the subspace vectors to a sublearner. This component is typically
 * used along with a decision tree learner to create random forests of decision
 * trees.
 *
 * @param   <OutputType>
 *      The output type for the decider.
 * @author  Justin Basilico
 * @since   3.0
 */
// TODO: Find a publication reference for random forests.
// -- jdbasil (2009-12-23)
public class RandomSubVectorThresholdLearner<OutputType>
    extends AbstractRandomized
    implements DeciderLearner<Vectorizable, OutputType, Boolean, Categorizer<? super Vectorizable, ? extends Boolean>>
{

    /** The default percent to sample is {@value}. */
    public static final double DEFAULT_PERCENT_TO_SAMPLE = 0.1;

    /** The decider learner for the subspace. */
    protected DeciderLearner<? super Vectorizable, OutputType, Boolean, VectorElementThresholdCategorizer> subLearner;

    /** The percentage of the dimensionality to sample. */
    protected double percentToSample;

    /** The vector factory to use. */
    protected VectorFactory<? extends Vector> vectorFactory;

    /**
     * Creates a new {@code RandomSubVectorThresholdLearner}.
     */
    public RandomSubVectorThresholdLearner()
    {
        this(null, DEFAULT_PERCENT_TO_SAMPLE, new Random());
    }

    /**
     * Creates a new {@code RandomSubVectorThresholdLearner}.
     *
     * @param   subLearner
     *      The threshold decision function learner to use over the subspace.
     * @param   percentToSample
     *      The percentage of the dimensionality to sample (must be between
     *      0.0 (exclusive) and 1.0 (inclusive).
     * @param   random
     *      The random number generator.
     */
    public RandomSubVectorThresholdLearner(
        final DeciderLearner<? super Vectorizable, OutputType, Boolean, VectorElementThresholdCategorizer> subLearner,
        final double percentToSample,
        final Random random)
    {
        this(subLearner, percentToSample, random, VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code RandomSubVectorThresholdLearner}.
     *
     * @param   subLearner
     *      The threshold decision function learner to use over the subspace.
     * @param   percentToSample
     *      The percentage of the dimensionality to sample (must be between
     *      0.0 and 1.0.
     * @param   random
     *      The random number generator.
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public RandomSubVectorThresholdLearner(
        final DeciderLearner<? super Vectorizable, OutputType, Boolean, VectorElementThresholdCategorizer> subLearner,
        final double percentToSample,
        final Random random,
        final VectorFactory<? extends Vector> vectorFactory)
    {
        super(random);

        this.setSubLearner(subLearner);
        this.setPercentToSample(percentToSample);
        this.setVectorFactory(vectorFactory);
    }

    public VectorElementThresholdCategorizer learn(
        final Collection<? extends InputOutputPair<? extends Vectorizable, OutputType>> data)
    {
        if (this.random == null)
        {
            this.random = new Random();
        }
        
        // Gets the dimensionality of the input.
        final int dimensionality = DatasetUtil.getInputDimensionality(data);

        // Get the dimensionality of the subspace.
        final int subDimensionality = this.getSubDimensionality(dimensionality);

        if (subDimensionality >= dimensionality)
        {
            // No point in subsampling if the requested dimensionality is as
            // big (or bigger) than the actual dimensionality.
            return this.subLearner.learn(data);
        }

        // Create a permutation of the indices of the dimensionality.
        final int[] permutation = Permutation.createPermutation(
            dimensionality, this.random);

        // Build up the dataset for the subspace.
        final ArrayList<InputOutputPair<Vector, OutputType>> subData =
            new ArrayList<InputOutputPair<Vector, OutputType>>(data.size());
        for (InputOutputPair<? extends Vectorizable, OutputType> example
            : data)
        {
            // Create the new subspace vector.
            final Vector subVector = this.vectorFactory.createVector(
                subDimensionality);

            // Copy over the values from the original vector.
            final Vector vector = example.getInput().convertToVector();
            for (int i = 0; i < subDimensionality; i++)
            {
                subVector.setElement(i, vector.getElement(permutation[i]));
            }

            // Add the new example.
            subData.add(new DefaultInputOutputPair<Vector, OutputType>(
                subVector, example.getOutput()));
        }

        // Learn on the subspace data.
        final VectorElementThresholdCategorizer subDecider =
            this.subLearner.learn(subData);

        if (subDecider != null)
        {
            // Change the index the threshold is applied to.
            final int subIndex = subDecider.getIndex();
            final int index = permutation[subIndex];
            subDecider.setIndex(index);
        }
        // else - Null just gets returned.
        
        // Return the learned function.
        return subDecider;
    }

    /**
     * Gets the dimensionality of the subspace based on the full dimensionality.
     *
     * @param   dimensionality
     *      The full dimensionality
     * @return
     *      The dimensionality of the subspace. Will always be greater than or
     *      equal to 1.
     */
    public int getSubDimensionality(
        final int dimensionality)
    {
        return Math.max(1, (int) (dimensionality * this.percentToSample));
    }

    /**
     * Gets the learner used to learn a threshold function over the subspace.
     *
     * @return
     *      The learner for the subspace.
     */
    public DeciderLearner<? super Vectorizable, OutputType, Boolean, VectorElementThresholdCategorizer>
        getSubLearner()
    {
        return this.subLearner;
    }

    /**
     * Sets the learner used to learn a threshold function over the subspace.
     *
     * @param   subLearner
     *      The learner for the subspace.
     */
    public void setSubLearner(
        final DeciderLearner<? super Vectorizable, OutputType, Boolean, VectorElementThresholdCategorizer> subLearner)
    {
        this.subLearner = subLearner;
    }

    /**
     * Gets the percent of the dimensionality to sample. Must be between 0.0
     * and 1.0.
     *
     * @return
     *      The percent of the dimensionality to sample.
     */
    public double getPercentToSample()
    {
        return this.percentToSample;
    }

    /**
     * Sets the percent of the dimensionality to sample. Must be between 0.0
     * and 1.0.
     *
     * @param   percentToSample
     *      The percent of the dimensionality to sample.
     */
    public void setPercentToSample(
        final double percentToSample)
    {
// Note: Technically, the percent to sample should be in the range (0.0, 1.0)
// not [0.0, 1.0] (in otherwords, where it is exclusive, not inclusive). 
// However, a value of 0.0 will mean that only 1 index is chosen and a value of
// 1.0 will mean that all indices are chosen (pass-through). Since these could 
// be useful values for testing various configurations, I decided to allow them.
// However, I'm not sure if that makes things more confusing or not.
// --jdbasil (2009-12-06)
        if (percentToSample < 0.0 || percentToSample > 1.0)
        {
            throw new IllegalArgumentException(
                "percentToSample must be between 0.0 and 1.0");
        }

        this.percentToSample = percentToSample;
    }

    /**
     * Gets the vector factory.
     *
     * @return
     *      The vector factory.
     */
    public VectorFactory<? extends Vector> getVectorFactory()
    {
        return this.vectorFactory;
    }

    /**
     * Sets the vector factory.
     *
     * @param   vectorFactory
     *      The vector factory.
     */
    public void setVectorFactory(
        final VectorFactory<? extends Vector> vectorFactory)
    {
        this.vectorFactory = vectorFactory;
    }

}
