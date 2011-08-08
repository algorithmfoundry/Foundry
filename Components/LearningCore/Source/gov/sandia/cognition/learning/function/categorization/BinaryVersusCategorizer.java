/*
 * File:                BinaryVersusCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 08, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractBatchLearnerWrapper;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * An adapter that allows binary categorizers to be adapted for multi-category
 * problems by applying a binary categorizer to each pair of categories.
 *
 * @param   <InputType>
 *      The type of the input to categorize.
 * @param   <CategoryType>
 *      The type of the output categories.
 * @author  Justin Basilico
 * @since   3.0
 */
public class BinaryVersusCategorizer<InputType, CategoryType>
    extends AbstractCategorizer<InputType, CategoryType>
{

    /** Maps false-true category pairs . */
    protected Map<Pair<CategoryType, CategoryType>, Evaluator<? super InputType, Boolean>>
        categoryPairsToEvaluatorMap;

    /**
     * Creates a new {@code BinaryVersusCategorizer}.
     */
    public BinaryVersusCategorizer()
    {
        this(new LinkedHashSet<CategoryType>(),
            new LinkedHashMap<Pair<CategoryType, CategoryType>, Evaluator<? super InputType, Boolean>>());
    }

    /**
     * Creates a new {@code BinaryVersusCategorizer} with the given
     * categories and an empty set of evaluators.
     *
     * @param   categories
     *      The possible output categories.
     */
    public BinaryVersusCategorizer(
        final Set<CategoryType> categories)
    {
        this(categories,
            new LinkedHashMap<Pair<CategoryType, CategoryType>, Evaluator<? super InputType, Boolean>>(
            (categories.size() * categories.size() / 2)));
    }

    /**
     * Creates a new {@code BinaryVersusCategorizer}.
     *
     * @param   categories
     *      The possible output categories.
     * @param   categoryPairsToEvaluatorMap
     *      The mapping of category pairs to their binary categorizer.
     */
    public BinaryVersusCategorizer(
        final Set<CategoryType> categories,
        final Map<Pair<CategoryType, CategoryType>, Evaluator<? super InputType, Boolean>> categoryPairsToEvaluatorMap)
    {
        super(categories);

        this.setCategoryPairsToEvaluatorMap(categoryPairsToEvaluatorMap);
    }

    @Override
    public BinaryVersusCategorizer<InputType, CategoryType> clone()
    {
        BinaryVersusCategorizer<InputType, CategoryType> result = (BinaryVersusCategorizer<InputType, CategoryType>)
            super.clone();

        result.categoryPairsToEvaluatorMap =
            new LinkedHashMap<Pair<CategoryType, CategoryType>, Evaluator<? super InputType, Boolean>>(
                this.categoryPairsToEvaluatorMap.size());
        for (Map.Entry<Pair<CategoryType, CategoryType>, Evaluator<? super InputType, Boolean>> entry
            : this.categoryPairsToEvaluatorMap.entrySet())
        {
            result.categoryPairsToEvaluatorMap.put(
                ObjectUtil.cloneSmart(entry.getKey()),
                ObjectUtil.cloneSmart(entry.getValue()));
        }

        return result;
    }



    public CategoryType evaluate(
        final InputType input)
    {
        final int categoryCount = this.categories.size();

        if (categoryCount <= 0)
        {
            // No categories.
            return null;
        }
        else if (categoryCount == 1)
        {
            // There is only one category.
            return CollectionUtil.getFirst(this.categories);
        }

        // We are going to count the number of votes for each category.
        final MapBasedDataHistogram<CategoryType> results =
            new MapBasedDataHistogram<CategoryType>(categoryCount);

        // Go through all the pairs of evaluators.
        for (Map.Entry<Pair<CategoryType, CategoryType>, Evaluator<? super InputType, Boolean>> entry
            : this.categoryPairsToEvaluatorMap.entrySet())
        {
            // Evaluate the binary categorizer for the two classes on the given
            // input.
            final Boolean category = entry.getValue().evaluate(input);

            if (category == null)
            {
                // Null values do not vote.
            }
            else if (!category)
            {
                // This belongs to the false (first) category.
                results.add(entry.getKey().getFirst());
            }
            else
            {
                // This belongs to the true (second) category.
                results.add(entry.getKey().getSecond());
            }
        }

        // The one with the most votes is the category we use.
        return results.getMaximumValue();
    }

    /**
     * Gets the mapping of false-true category pairs to the binary categorizer
     * that distinguishes them.
     *
     * @return
     *      The mapping of category pairs to their binary categorizer.
     */
    public Map<Pair<CategoryType, CategoryType>, Evaluator<? super InputType, Boolean>> getCategoryPairsToEvaluatorMap()
    {
        return this.categoryPairsToEvaluatorMap;
    }

    /**
     * Sets the mapping of false-true category pairs to the binary categorizer
     * that distinguishes them.
     *
     * @param   categoryPairsToEvaluatorMap
     *      The mapping of category pairs to their binary categorizer.
     */
    public void setCategoryPairsToEvaluatorMap(
        final Map<Pair<CategoryType, CategoryType>, Evaluator<? super InputType, Boolean>> categoryPairsToEvaluatorMap)
    {
        this.categoryPairsToEvaluatorMap = categoryPairsToEvaluatorMap;
    }

    /**
     * A learner for the {@code BinaryVersusCategorizer}. It learns a
     * binary categorizer for each pair of categories.
     *
     * @param   <InputType>
     *      The input to learn from and the input to the learned categorizer.
     * @param   <CategoryType>
     *      The type of categories to learn from.
     */
    public static class Learner<InputType, CategoryType>
        extends AbstractBatchLearnerWrapper<Collection<? extends InputOutputPair<? extends InputType, Boolean>>, Evaluator<? super InputType, Boolean>, BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, Boolean>>>
        implements SupervisedBatchLearner<InputType, CategoryType, BinaryVersusCategorizer<InputType, CategoryType>>
    {

        /**
         * Creates a new  {@code BinaryVersusCategorizer.Learner} with no
         * initial binary categorizer learner.
         */
        public Learner()
        {
            this(null);
        }

        /**
         * Creates a new {@code BinaryVersusCategorizer.Learner} with an
         * binary categorizer learner to learn category versus category.
         *
         * @param   learner
         *      The binary categorizer learner to use to learn decision
         *      functions between categories.
         */
        public Learner(
            BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, Boolean>> learner)
        {
            super(learner);
        }
                
        public BinaryVersusCategorizer<InputType, CategoryType> learn(
            final Collection<? extends InputOutputPair<? extends InputType, CategoryType>> data)
        {
            // Find the categories. We're going to look at pairs of categories
            // so we also make a list version of the set.
            final Set<CategoryType> categories =
                DatasetUtil.findUniqueOutputs(data);
            final int categoryCount = categories.size();
            final ArrayList<CategoryType> categoriesList =
                new ArrayList<CategoryType>(categories);

            // Create the object to hold the result.
            final BinaryVersusCategorizer<InputType, CategoryType> result =
                new BinaryVersusCategorizer<InputType, CategoryType>(categories);
            for (int i = 0; i < categoryCount; i++)
            {
                // This is the false category.
                final CategoryType falseCategory = categoriesList.get(i);
                for (int j = i + 1; j < categoryCount; j++)
                {
                    // This is the true category.
                    final CategoryType trueCategory = categoriesList.get(j);

                    final ArrayList<InputOutputPair<InputType, Boolean>> versusData =
                        new ArrayList<InputOutputPair<InputType, Boolean>>();

                    for (InputOutputPair<? extends InputType, CategoryType> example
                        : data)
                    {
                        // The category of the label.
                        final CategoryType category = example.getOutput();

                        if (falseCategory.equals(category))
                        {
                            // This is an example belonging to the false
                            // category.
                            versusData.add(new DefaultInputOutputPair<InputType, Boolean>(
                                example.getInput(), false));
                        }
                        else if (trueCategory.equals(category))
                        {
                            // This is an example belonging to the true
                            // category.
                            versusData.add(new DefaultInputOutputPair<InputType, Boolean>(
                                example.getInput(), true));
                        }
                        // else - The example did not belong to either category.
                    }

                    // Learn on the binary data.
                    final Evaluator<? super InputType, Boolean> learned =
                        this.getLearner().learn(versusData);

                    // Add the learned categorizer.
                    result.categoryPairsToEvaluatorMap.put(
                        new DefaultPair<CategoryType, CategoryType>(
                            falseCategory, trueCategory), learned);
                }
            }

            // Returns the created adapter.
            return result;
        }

    }

}
