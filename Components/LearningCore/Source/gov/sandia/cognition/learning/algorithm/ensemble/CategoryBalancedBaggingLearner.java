/*
 * File:                CategoryBalancedBaggingLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 18, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

/**
 * An extension of the basic bagging learner that attempts to sample bags that
 * have equal numbers of examples from every category.
 *
 * @param   <InputType>
 *      The input type for supervised learning. Passed on to the internal
 *      learning algorithm. Also the input type for the learned ensemble.
 * @param   <CategoryType>
 *      The output type for supervised learning. Passed on to the internal
 *      learning algorithm. Also the output type of the learned ensemble.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class CategoryBalancedBaggingLearner<InputType, CategoryType>
    extends BaggingCategorizerLearner<InputType, CategoryType>
{
    
    /** The list of categories. */
    protected ArrayList<CategoryType> categoryList;

    /** The mapping of categories to indices of examples belonging to the category. */
    protected HashMap<CategoryType, ArrayList<Integer>> dataPerCategory;
    
    /**
     * Creates a new instance of CategoryBalancedBaggingLearner.
     */
    public CategoryBalancedBaggingLearner()
    {
        this(null);
    }

    /**
     * Creates a new instance of CategoryBalancedBaggingLearner.
     *
     * @param  learner
     *      The learner to use to create the categorizer on each iteration.
     */
    public CategoryBalancedBaggingLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner)
    {
        this(learner, DEFAULT_MAX_ITERATIONS, DEFAULT_PERCENT_TO_SAMPLE, new Random());
    }

    /**
     * Creates a new instance of CategoryBalancedBaggingLearner.
     *
     * @param  learner
     *      The learner to use to create the categorizer on each iteration.
     * @param  maxIterations
     *      The maximum number of iterations to run for, which is also the
     *      number of learners to create.
     * @param   percentToSample
     *      The percentage of the total size of the data to sample on each
     *      iteration. Must be positive.
     * @param  random
     *      The random number generator to use.
     */
    public CategoryBalancedBaggingLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner,
        final int maxIterations,
        final double percentToSample,
        final Random random)
    {
        super(learner, maxIterations, percentToSample, random);
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        boolean result = super.initializeAlgorithm();

        if (result)
        {
            // Map each category to a list of indices for it.
            final int dataSize = this.dataList.size();
            final Set<CategoryType> categories = DatasetUtil.findUniqueOutputs(
                this.dataList);
            this.categoryList = new ArrayList<CategoryType>(categories);
            this.dataPerCategory = new LinkedHashMap<CategoryType, ArrayList<Integer>>(
                categories.size());
            for (CategoryType category : categories)
            {
                this.dataPerCategory.put(category, new ArrayList<Integer>());
            }

            for (int i = 0; i < dataSize; i++)
            {
                final CategoryType category = this.dataList.get(i).getOutput();
                this.dataPerCategory.get(category).add(i);
            }
        }

        return result;
    }

    @Override
    protected void fillBag(
        final int sampleCount)
    {
        // Get the number of categories.
        final int categoryCount = this.categoryList.size();

        if ((sampleCount % categoryCount) != 0)
        {
            // Shuffle the category list to deal with uneven numbers of
            // examples per category.
            Collections.shuffle(this.categoryList, this.random);
        }
        
        int remainingSampleSize = sampleCount;
        for (int i = 0; i < categoryCount && remainingSampleSize > 0; i++)
        {
            final CategoryType category = this.categoryList.get(i);
            final ArrayList<Integer> indices =
                this.dataPerCategory.get(category);
            final int categorySize = indices.size();
            final int categorySampleSize =
                Math.max(1, remainingSampleSize / (categoryCount - i));
            for (int j = 0; j < categorySampleSize; j++)
            {
                final int index = indices.get(
                    this.random.nextInt(categorySize));
                final InputOutputPair<? extends InputType, CategoryType> example =
                    this.dataList.get(index);
                this.bag.add(example);
                this.dataInBag[index] += 1;
            }
            
            remainingSampleSize -= categorySampleSize;
        }
    }

    @Override
    protected void cleanupAlgorithm()
    {
        this.dataPerCategory = null;
        this.categoryList = null;
        super.cleanupAlgorithm();
    }

}
