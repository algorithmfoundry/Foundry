/*
 * File:            BagBasedCategorizerEnsembleLearner.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2017 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AnytimeBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.Collection;

/**
 * Interface for a bag-based ensemble learner. Primarily used in conjunction 
 * with out-of-bag stopping criteria.
 * 
 * @param   <InputType>
 *      The type of the input for the categorizer to learn.
 * @param   <CategoryType>
 *      The type of the category that is the output for the categorizer to
 *      learn.
 * 
 * @author  Justin Basilico
 * @since   3.4.4
 */
public interface BagBasedCategorizerEnsembleLearner<InputType, CategoryType>
    extends AnytimeBatchLearner<Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>>
{
    
    /**
     * Gets the training example at the given index.
     * 
     * @param   index
     *      The 0-based index to lookup.
     * @return 
     *      The training example at that index.
     */
    public InputOutputPair<? extends InputType, CategoryType> getExample(
        final int index);
    
    /**
     * Gets the counter for each example indicating how many times it exists
     * in the current bag.
     * 
     * @return
     *      The counter per example of how many times it is in the current bag.
     *      Note that this may be an internal state array for the algorithm so
     *      it shouldn't be modified. It is provided for efficient access.
     */
    public int[] getDataInBag();
    
}
