/*
 * File:                AbstractOnlineLinearBinaryCategorizerLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Incremental Learning Core
 * 
 * Copyright January 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.algorithm.AbstractBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
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
    extends AbstractBatchAndIncrementalLearner<InputOutputPair<? extends Vectorizable, Boolean>, LinearBinaryCategorizer>
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
