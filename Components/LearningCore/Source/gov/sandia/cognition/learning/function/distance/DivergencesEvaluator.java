/*
 * File:            ClusterDistanceEvaluator.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.data.convert.vector.AbstractToVectorEncoder;
import gov.sandia.cognition.learning.algorithm.AbstractBatchLearnerContainer;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Evaluates the divergence (distance) between an input and a list of values,
 * storing the resulting divergence values in a vector. This can be used as a
 * feature representation built from something like a clustering algorithm or
 * from a set of prototype/basis elements.
 *
 * @param   <InputType>
 *      The type of input value that the class evaluates. It is the second
 *      parameter passed to the divergence function. Typically a type like
 *      a Vector.
 * @param   <ValueType>
 *      The type of value that the divergence is computed from. It is the
 *      first parameter passed to the divergence function. It is typically a
 *      type like Vector or CenteroidCluster.
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class DivergencesEvaluator<InputType, ValueType>
    extends AbstractToVectorEncoder<InputType>
    implements VectorOutputEvaluator<InputType, Vector>,
        DivergenceFunctionContainer<ValueType, InputType>
{

    /** The divergence function to apply between the data and the input. */
    protected DivergenceFunction<? super ValueType, ? super InputType> divergenceFunction;

    /** The data to evaluate the divergence from. */
    protected Collection<ValueType> values;

    /**
     * Creates a new {@code DivergencesEvaluator} with a null divergence
     * function and an empty set of values.
     */
    public DivergencesEvaluator()
    {
        this(null, new ArrayList<ValueType>());
    }

    /**
     * Creates a new {@code DivergencesEvaluator} with the given divergence 
     * and values.
     *
     * @param   divergenceFunction
     *      The divergence function to use.
     * @param   values
     *      The values to calculate the divergence from.
     */
    public DivergencesEvaluator(
        final DivergenceFunction<? super ValueType, ? super InputType> divergenceFunction,
        final Collection<ValueType> values)
    {
        this(divergenceFunction, values, VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code DivergencesEvaluator} with the given divergence
     * and values.
     *
     * @param   divergenceFunction
     *      The divergence function to use.
     * @param   values
     *      The values to calculate the divergence from.
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public DivergencesEvaluator(
        final DivergenceFunction<? super ValueType, ? super InputType> divergenceFunction,
        final Collection<ValueType> values,
        final VectorFactory<?> vectorFactory)
    {
        super(vectorFactory);

        this.setDivergenceFunction(divergenceFunction);
        this.setValues(values);
    }

    @Override
    public DivergencesEvaluator<InputType, ValueType> clone()
    {
        @SuppressWarnings("unchecked")
        final DivergencesEvaluator<InputType, ValueType> clone = (DivergencesEvaluator<InputType, ValueType>)
            super.clone();
        clone.divergenceFunction = ObjectUtil.cloneSmart(this.divergenceFunction);
        clone.values = ObjectUtil.cloneSmartElementsAsArrayList(this.values);
        return clone;
    }
    
    @Override
    public void encode(
        final InputType input,
        final Vector result,
        final int startIndex)
    {
        // Go through the values and compute the divergence to each one.
        int index = startIndex;
        for (final ValueType cluster : this.getValues())
        {
            final double distance =
                this.divergenceFunction.evaluate(cluster, input);
            result.setElement(index, distance);
            index++;
        }
    }

    @Override
    public int getOutputDimensionality()
    {
        return this.getValues().size();
    }

    @Override
    public DivergenceFunction<? super ValueType, ? super InputType> getDivergenceFunction()
    {
        return this.divergenceFunction;
    }

    /**
     * Sets the divergence function to use from the values to the inputs.
     *
     * @param   divergenceFunction
     *      The divergence function to use.
     */
    public void setDivergenceFunction(
        final DivergenceFunction<? super ValueType, ? super InputType> divergenceFunction)
    {
        this.divergenceFunction = divergenceFunction;
    }

    /**
     * Gets the values that the divergence is computed from using the
     * divergence function to the input.
     * 
     * @return
     *      The values that the distance is computed from.
     */
    public Collection<ValueType> getValues()
    {
        return this.values;
    }

    /**
     * Sets the values that the divergence is computed from using the
     * divergence function to the input.
     *
     * @param   values
     *      The values that the distance is computed from.
     */
    public void setValues(
        final Collection<ValueType> values)
    {
        this.values = values;
    }

    /**
     * Convenience method for creation a {@code DivergeceEvaluator}.
     *
     * @param   <InputType>
     *      The type of input value that the class evaluates.
     * @param   <ValueType>
     *      The type of value that the divergence is computed from.
     * @param   divergenceFunction
     *      The divergence function to use.
     * @param   values
     *      The values to calculate the divergence from.
     * @return
     *      A new evaluator.
     */
    public static <InputType, ValueType> DivergencesEvaluator<InputType, ValueType>
        create(
        final DivergenceFunction<? super ValueType, ? super InputType> divergenceFunction,
        final Collection<ValueType> values)
    {
        return new DivergencesEvaluator<InputType, ValueType>(
            divergenceFunction, values);
    }


    /**
     * A learner adapter for the {@code DivergencesEvaluator}. It calls a
     * base learner and then wraps learned collection of values in an evaluator
     * that uses the given divergence function.
     *
     * @param   <DataType>
     *      The data type for learning. Passed to the wrapped learner.
     * @param   <InputType>
     *      The input type for the evaluator.
     * @param   <ValueType>
     *      The value type that is the output of learning and is used as the
     *      values in the learned evaluator.
     */
    public static class Learner<DataType, InputType, ValueType>
        extends AbstractBatchLearnerContainer<BatchLearner<? super DataType, ? extends Collection<ValueType>>>
        implements BatchLearner<DataType, DivergencesEvaluator<InputType, ValueType>>,
            DivergenceFunctionContainer<ValueType, InputType>,
            VectorFactoryContainer
    {

        /** The divergence function to apply between the data and the input. */
        protected DivergenceFunction<? super ValueType, ? super InputType> divergenceFunction;

        /** The vector factory to use. */
        protected VectorFactory<?> vectorFactory;

        /**
         * Creates a new {@code DivergenceFunction.Learner} with null base
         * learner and divergence functions.
         */
        public Learner()
        {
            this(null, null);
        }

        /**
         * Creates a new {@code DivergenceFunction.Learner} with the given
         * properties.
         *
         * @param   learner
         *      The base learner to use.
         * @param   divergenceFunction
         *      The divergence function to use.
         */
        public Learner(
            final BatchLearner<DataType, ? extends Collection<ValueType>> learner,
            final DivergenceFunction<? super ValueType, ? super InputType> divergenceFunction)
        {
            this(learner, divergenceFunction, VectorFactory.getDefault());
        }

        /**
         * Creates a new {@code DivergenceFunction.Learner} with the given
         * properties.
         *
         * @param   learner
         *      The base learner to use.
         * @param   divergenceFunction
         *      The divergence function to use.
         * @param   vectorFactory
         *      The vector factory to use.
         */
        public Learner(
            final BatchLearner<DataType, ? extends Collection<ValueType>> learner,
            final DivergenceFunction<? super ValueType, ? super InputType> divergenceFunction,
            final VectorFactory<?> vectorFactory)
        {
            super(learner);

            this.setDivergenceFunction(divergenceFunction);
            this.setVectorFactory(vectorFactory);
        }

        @Override
        public Learner<DataType, InputType, ValueType> clone()
        {
            @SuppressWarnings("unchecked")
            final Learner<DataType, InputType, ValueType> clone = (Learner<DataType, InputType, ValueType>)
                super.clone();
            clone.divergenceFunction = ObjectUtil.cloneSmart(this.divergenceFunction);
            return clone;
        }
        
        @Override
        public DivergencesEvaluator<InputType, ValueType> learn(
            final DataType data)
        {
            return new DivergencesEvaluator<InputType, ValueType>(
                this.getDivergenceFunction(), 
                this.getLearner().learn(data),
                this.getVectorFactory());
        }

        @Override
        public DivergenceFunction<? super ValueType, ? super InputType> getDivergenceFunction()
        {
            return this.divergenceFunction;
        }

        /**
         * Sets the divergence function to use from the values to the inputs.
         *
         * @param   divergenceFunction
         *      The divergence function to use.
         */
        public void setDivergenceFunction(
            final DivergenceFunction<? super ValueType, ? super InputType> divergenceFunction)
        {
            this.divergenceFunction = divergenceFunction;
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

        /**
         * Convenience method for creating a
         * {@code DivergencesEvaluator.Learner}.
         *
         * @param   <DataType>
         *      The data type for learning. Passed to the wrapped learner.
         * @param   <InputType>
         *      The input type for the evaluator.
         * @param   <ValueType>
         *      The value type that is the output of learning and is used as the
         *      values in the learned evaluator.
         * @param   learner
         *      The base learner to use.
         * @param   divergenceFunction
         *      The divergence function to use.
         * @return
         *      A new learner.
         */
        public static <DataType, InputType, ValueType> Learner<DataType, InputType, ValueType>
            create(
            final BatchLearner<DataType, ? extends Collection<ValueType>> learner,
            final DivergenceFunction<? super ValueType, ? super InputType> divergenceFunction)
        {
            return new Learner<DataType, InputType, ValueType>(
                learner, divergenceFunction);
        }

    }

}
