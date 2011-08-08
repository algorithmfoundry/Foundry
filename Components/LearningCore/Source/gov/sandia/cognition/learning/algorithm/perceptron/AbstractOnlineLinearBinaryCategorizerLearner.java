/*
 * File:                AbstractOnlineLinearBinaryCategorizerLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.algorithm.AbstractSupervisedBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * An abstract class for online (incremental) learning algorithms that produce
 * an {@code LinearBinaryCategorizer}. It implements common functionality,
 * such as keeping track of the vector factory to use and creating the initial
 * linear binary categorizer object.
 * 
 * @author  Justin Basilico
 * @since   3.1.1
 */
public abstract class AbstractOnlineLinearBinaryCategorizerLearner
    extends AbstractSupervisedBatchAndIncrementalLearner<Vectorizable, Boolean, LinearBinaryCategorizer>
    implements VectorFactoryContainer
{

    /** The factory to create weight vectors. */
    protected VectorFactory<?> vectorFactory;

    /**
     * Creates a new {@code AbstractOnlineLinearBinaryCategorizerLearner} with
     * the default vector factory.
     */
    public AbstractOnlineLinearBinaryCategorizerLearner()
    {
        this(VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code AbstractOnlineLinearBinaryCategorizerLearner} with
     * the given vector factory.
     *
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public AbstractOnlineLinearBinaryCategorizerLearner(
        final VectorFactory<?> vectorFactory)
    {
        super();

        this.setVectorFactory(vectorFactory);
    }

    @Override
    public LinearBinaryCategorizer createInitialLearnedObject()
    {
        return new LinearBinaryCategorizer();
    }

    @Override
    public void update(
        final LinearBinaryCategorizer target,
        final Vectorizable input,
        final Boolean output)
    {
        if (input != null && output != null)
        {
            this.update(target, input.convertToVector(), (boolean) output);
        }
    }

    /**
     * The {@code update} method updates an object of {@code ResultType} using
     * the given a new supervised input-output pair, using some form of
     * "learning" algorithm.
     *
     * @param   target
     *      The object to update.
     * @param   input
     *      The supervised input vector to learn from.
     * @param   output
     *      The supervised output label to learn from.
     */
    public abstract void update(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean output);

    /**
     * Gets the VectorFactory used to create the weight vector.
     *
     * @return The VectorFactory used to create the weight vector.
     */
    @Override
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
