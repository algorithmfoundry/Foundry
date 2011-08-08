/*
 * File:                EvaluatorToCategorizerAdapter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 20, 2007, Sandia Corporation.
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
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * The {@code EvaluatorToCategorizerAdapter} class implements an adapter from a
 * general {@code Evaluator} to be a {@code Categorizer}. This is done by just
 * having a reference to the evaluator plus a field to store the list of 
 * possible output categories.
 * 
 * @param   <InputType> The type of the input to categorize.
 * @param   <CategoryType> The type of the output categories.
 * @author  Justin Basilico
 * @since   2.0
 */
public class EvaluatorToCategorizerAdapter<InputType, CategoryType>
    extends AbstractCategorizer<InputType, CategoryType>
{

    /** The evaluator that is being wrapped into a categorizer. */
    private Evaluator<? super InputType, ? extends CategoryType> evaluator;

    /**
     * Creates a new {@code EvaluatorToCategorizerAdapter}.
     */
    public EvaluatorToCategorizerAdapter()
    {
        this(null, new TreeSet<CategoryType>());
    }

    /**
     * Creates a new {@code EvaluatorToCategorizerAdapter}.
     * 
     * @param   evaluator The evaluator to turn into a categorizer.
     * @param   categories The list of possible output categories.
     */
    public EvaluatorToCategorizerAdapter(
        final Evaluator<? super InputType, ? extends CategoryType> evaluator,
        final Set<CategoryType> categories)
    {
        super(categories);

        this.setEvaluator(evaluator);
    }

    public CategoryType evaluate(
        final InputType input)
    {
        return this.evaluator.evaluate(input);
    }
    
    /**
     * Gets the evaluator that is to be adapted to be a categorizer.
     * 
     * @return  The evaluator to adapt.
     */
    public Evaluator<? super InputType, ? extends CategoryType> getEvaluator()
    {
        return evaluator;
    }
    
    /**
     * Sets the evaluator that is to be adapted to be a categorizer.
     * 
     * @param   evaluator The evaluator to adapt.
     */
    public void setEvaluator(
        final Evaluator<? super InputType, ? extends CategoryType> evaluator)
    {
        this.evaluator = evaluator;
    }

    @Override
    public void setCategories(
        final Set<CategoryType> categories)
    {
        super.setCategories(categories);
    }

    /**
     * The {@code EvaluatorToCategorizerAdapter.Learner} class implements a
     * simple supervised learner for a {@code EvaluatorToCategorizerAdapter}.
     * 
     * @param <InputType> The type of the input values.
     * @param <CategoryType> The type of the output categories.
     */
    public static class Learner<InputType, CategoryType>
        extends AbstractBatchLearnerContainer<BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>>>
        implements SupervisedBatchLearner<InputType,CategoryType,EvaluatorToCategorizerAdapter<InputType, CategoryType>>
    {
        /**
         * Creates a new {@code EvaluatorToCategorizerAdapter}.
         */
        public Learner()
        {
            this(null);
        }

        /**
         * Creates a new {@code EvaluatorToCategorizerAdapter}.
         * 
         * @param   learner The learner to adapt the output of to be a 
         *          categorizer.
         */
        public Learner(
            final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner)
        {
            super();
            this.setLearner(learner);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Learner<InputType,CategoryType> clone()
        {
            return (Learner<InputType,CategoryType>) super.clone();
        }
        
        @SuppressWarnings("unchecked")
        public EvaluatorToCategorizerAdapter<InputType, CategoryType> learn(
            Collection<? extends InputOutputPair<? extends InputType, CategoryType>> data)
        {
            final Evaluator<? super InputType, ? extends CategoryType> learned =
                (Evaluator<? super InputType, ? extends CategoryType>) this.getLearner().learn(data);

            // Go through the input data to determine the possible output
            // categories.
            final TreeSet<CategoryType> categories =
                new TreeSet<CategoryType>();
            for (InputOutputPair<? extends InputType, ? extends CategoryType> 
                 example : data)
            {
                categories.add(example.getOutput());
            }

            return new EvaluatorToCategorizerAdapter<InputType, CategoryType>(
                learned, categories);
        }

        /**
         * Gets the learner whose output is to be adapted to be a categorizer.
         * 
         * @return  The learner to adapt the output of.
         */
        public BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> getLearner()
        {
            return this.learner;
        }

        /**
         * Sets the learner whose output is to be adapted to be a categorizer.
         * 
         * @param   learner The leaner to adapt the output of.
         */
        public void setLearner(
            final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner)
        {
            this.learner = learner;
        }

    }

}
