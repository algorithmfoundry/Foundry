/*
 * File:                WinnerTakeAllCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 07, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractBatchLearnerContainer;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.DefaultWeightedValueDiscriminant;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryContainer;
import gov.sandia.cognition.math.matrix.Vectorizable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Adapts an evaluator that outputs a vector to be used as a categorizer.
 * @param  <InputType> The type of the input the categorizer can use.
 * @param  <CategoryType> The type of category output by the categorizer.
 * @author  Justin Basilico
 * @since   3.0
 */
public class WinnerTakeAllCategorizer<InputType, CategoryType>
    extends AbstractDiscriminantCategorizer<InputType, CategoryType, Double>
{
    
    /** The evaluator that outputs a vector to return. */
    protected Evaluator<? super InputType, ? extends Vectorizable> evaluator;

    /**
     * Creates a new {@code WinnerTakesAllCategorizer}.
     */
    public WinnerTakeAllCategorizer()
    {
        this(null, new LinkedHashSet<CategoryType>());
    }

    /**
     * Creates a new {@code WinnerTakesAllCategorizer}.
     *
     * @param   evaluator
     *      The evaluator that this class adapts.
     * @param   categories
     *      The set of categories.
     */
    public WinnerTakeAllCategorizer(
        final Evaluator<? super InputType, ? extends Vectorizable> evaluator,
        final Set<CategoryType> categories)
    {
        super(categories);

        this.setEvaluator(evaluator);
    }

    /**
     * Evaluates the categorizer and returns the category along with a weight.
     *
     * @param   input
     *      The input to evaluate.
     * @return
     *      The output plus its weight.
     */
    @Override
    public DefaultWeightedValueDiscriminant<CategoryType> evaluateWithDiscriminant(
        final InputType input)
    {
        final Vector output = this.evaluator.evaluate(input).convertToVector();
        return this.findBestCategory(output);
    }
    
    /**
     * Finds the best category (and its output value) from the given vector
     * of outputs from a vector evaluator. Ties are broken by taking the first
     * category with the highest value.
     *
     * @param   output
     *      The vector of outputs, one corresponding to each category.
     * @return
     *      The category with the highest output value. Ties are broken by
     *      taking the first category with the highest value.
     */
    public DefaultWeightedValueDiscriminant<CategoryType> findBestCategory(
        final Vector output)
    {
        output.assertDimensionalityEquals(this.categories.size());

        // Loop througn and find the best category.
        CategoryType best = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        int index = 0;
        for (CategoryType category : this.categories)
        {
            final double value = output.getElement(index);
            
            if (best == null || value > bestValue)
            {
                best = category;
                bestValue = value;
            }
            index++;
        }

        // Return the best category.
        return new DefaultWeightedValueDiscriminant<CategoryType>(best, bestValue);
    }

    /**
     * Gets the wrapped evaluator.
     *
     * @return
     *      The wrapped evaluator.
     */
    public Evaluator<? super InputType, ? extends Vectorizable> getEvaluator()
    {
        return this.evaluator;
    }

    /**
     * Sets the wrapped evaluator.
     *
     * @param   evaluator
     *      The wrapped evaluator.
     */
    public void setEvaluator(
        final Evaluator<? super InputType, ? extends Vectorizable> evaluator)
    {
        this.evaluator = evaluator;
    }

    @Override
    public void setCategories(
        final Set<CategoryType> categories)
    {
        // Just making this publicly visiable.
        super.setCategories(categories);
    }

    /**
     * A learner for the adapter. Makes it so that learning algorithms that
     * produce evaluators whose outputs are vectors to be used for
     * categorization.
     *
     * @param   <InputType>
     *      The type of the input data for the learner.
     * @param   <CategoryType>
     *      The type of the output categories.
     */
    public static class Learner<InputType, CategoryType>
        extends AbstractBatchLearnerContainer<BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Vector>>, ? extends Evaluator<? super InputType, ? extends Vectorizable>>>
        implements SupervisedBatchLearner<InputType, CategoryType, WinnerTakeAllCategorizer<InputType, CategoryType>>,
            VectorFactoryContainer
    {

        /** The vector factory used. */
        protected VectorFactory<?> vectorFactory;

        /**
         * Creates a new learner adapter.
         */
        public Learner()
        {
            this(null);
        }

        /**
         * Creates a new learner adapter with the given internal learner.
         *
         * @param   learner
         *      The learner to adapt.
         */
        public Learner(
            final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Vector>>, Evaluator<? super InputType, ? extends Vectorizable>> learner)
        {
            super(learner);

            this.setVectorFactory(VectorFactory.getDefault());
        }

        public WinnerTakeAllCategorizer<InputType, CategoryType> learn(
            final Collection<? extends InputOutputPair<? extends InputType, CategoryType>> data)
        {
            // First figure out all of the categories.
            final LinkedHashMap<CategoryType, Integer> categoryIndices =
                new LinkedHashMap<CategoryType, Integer>();
            for (InputOutputPair<?, CategoryType> example : data)
            {
                final CategoryType category = example.getOutput();

                if (!categoryIndices.containsKey(category))
                {
                    final int index = categoryIndices.size();
                    categoryIndices.put(category, index);
                }
            }

            // Now convert the dataset to have the output be vectors.
            final int categoryCount = categoryIndices.size();
            final ArrayList<InputOutputPair<InputType, Vector>> vectorData =
                new ArrayList<InputOutputPair<InputType, Vector>>(data.size());
            for (InputOutputPair<? extends InputType, CategoryType> example
                : data)
            {
                final CategoryType category = example.getOutput();
                final int index = categoryIndices.get(category);


// TODO: Make use of the boolean encoding code from the data package at some point.
                final Vector output = this.getVectorFactory().createVector(
                    categoryCount, -1.0);
                output.setElement(index, +1.0);
                vectorData.add(new DefaultInputOutputPair<InputType, Vector>(
                    example.getInput(), output));
            }

            // Do the learning.
            final Evaluator<? super InputType, ? extends Vectorizable> learned =
                this.getLearner().learn(vectorData);

            final LinkedHashSet<CategoryType> categories =
                new LinkedHashSet<CategoryType>(categoryIndices.keySet());
            return new WinnerTakeAllCategorizer<InputType, CategoryType>(
                learned, categories);
        }

        /**
         * Gets the vector factory.
         *
         * @return
         *      The vector factory.
         */
        public VectorFactory<?> getVectorFactory()
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
            final VectorFactory<?> vectorFactory)
        {
            this.vectorFactory = vectorFactory;
        }

    }

}
