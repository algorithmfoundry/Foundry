/*
 * File:            RandomSubspace.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.function.vector.SubVectorEvaluator;
import gov.sandia.cognition.math.Permutation;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractRandomized;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.Randomized;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * Selects a random subspace from the given vector, which is a random set of
 * indices. It is typically used in the context of ensemble learning to adapt
 * a base learner to increase the variance, similar to the method used in
 * bagging or random forests. Thus, it is also known as attribute bagging.
 *
 * @author  Justin Basilico
 * @version 3.4.0
 */
@PublicationReference(
    title="Random Subspace Method",
    author="Wikipedia",
    year=2011,
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/Random_subspace_method")
public class RandomSubspace
    extends AbstractRandomized
    implements BatchLearner<Collection<? extends Vectorizable>, SubVectorEvaluator>,
        Randomized, VectorFactoryContainer
{
    /** The default size is {@value}. */
    public static final int DEFAULT_SIZE = 10;

    /** The size of the random subspace to create, which is the number of
     *  dimensions that are chosen. */
    protected int size;

    /** The vector factory for the sub vector evaluator to use. */
    protected VectorFactory<?> vectorFactory;

    /**
     * Creates a new {@code RandomSubspace} with the default size.
     */
    public RandomSubspace()
    {
        this(DEFAULT_SIZE);
    }

    /**
     * Creates a new {@code RandomSubspace} with the given size.
     *
     * @param   size
     *      The size of the subspace to create. Must be positive.
     */
    public RandomSubspace(
        final int size)
    {
        this(size, new Random());
    }

    /**
     * Creates a new {@code RandomSubspace} with the given parameters.
     *
     * @param   size
     *      The size of the subspace to create. Must be positive.
     * @param   random
     *      The random number generator to use.
     */
    public RandomSubspace(
        final int size,
        final Random random)
    {
        this(size, random, VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code RandomSubspace} with the given parameters.
     *
     * @param   size
     *      The size of the subspace to create. Must be positive.
     * @param   random
     *      The random number generator to use.
     * @param   vectorFactory
     *      The vector factory to use in the result to create
     */
    public RandomSubspace(
        final int size,
        final Random random,
        final VectorFactory<?> vectorFactory)
    {
        super(random);

        this.setSize(size);
        this.setVectorFactory(vectorFactory);
    }

    @Override
    public SubVectorEvaluator learn(
        final Collection<? extends Vectorizable> data)
    {
        // Figure out the dimensionality of the data.
        final int dimensionality = DatasetUtil.getDimensionality(data);

        // Can't learn with a bad dimensionality.
        if (dimensionality < 0)
        {
            return null;
        }

// TODO: If we had a method for creating a partial permutation, we could avoid
// this code. -- jbasilico (2011-10-18)

        // Sample the indices we want to use without replacement.
        // We do this by creating a permutation and then only taking the first
        // part of it, containing the indices we will use.
        final int[] permutation = Permutation.createPermutation(
            dimensionality, this.getRandom());
        final int[] indices = new int[Math.min(size, dimensionality)];
        System.arraycopy(permutation, 0, indices, 0, indices.length);

        // We sort the indices just to make their interpretation easier.
        Arrays.sort(indices);

        // Create the result.
        return new SubVectorEvaluator(dimensionality, indices,
            this.getVectorFactory());
    }

    /**
     * Gets the size of the subspace that will be created.
     *
     * @return
     *      The size of the subspace that will be created. Must be positive.
     */
    public int getSize()
    {
        return this.size;
    }

    /**
     * Sets the size of the subspace that will be created.
     *
     * @param   size
     *      The size of the subspace that will be created. Must be positive.
     */
    public void setSize(
        final int size)
    {
        ArgumentChecker.assertIsPositive("size", size);
        this.size = size;
    }

    @Override
    public VectorFactory<? extends Vector> getVectorFactory()
    {
        return this.vectorFactory;
    }

    /**
     * Sets the vector factory to use.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public void setVectorFactory(
        final VectorFactory<?> vectorFactory)
    {
        this.vectorFactory = vectorFactory;
    }

}
