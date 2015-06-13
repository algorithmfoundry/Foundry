/*
 * File:            RandomForestFactory.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.ensemble.BaggingCategorizerLearner;
import gov.sandia.cognition.learning.algorithm.ensemble.BaggingRegressionLearner;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Random;

/**
 * A factory class for creating Random Forest learners. A random forest is a
 * combination of using bagging to create an ensemble where the ensemble members
 * are decision trees where each split in the tree is created by only 
 * considering a random subset of the features (random subspace method).
 * 
 * @author  Justin Basilico
 * @since   3.4.2
 */
@PublicationReference(
    title="Bagging Predictors",
    author="Leo Breiman",
    year=1996,
    type=PublicationType.Journal,
    publication="Machine Learning",
    pages={123, 140},
    url="http://www.springerlink.com/index/L4780124W2874025.pdf")
public class RandomForestFactory
    extends AbstractCloneableSerializable
{
    
    /**
     * Creates a random forest learner for categorization outputs.
     * 
     * @param   <CategoryType>
     *      The type of categories.
     * @param   ensembleSize
     *      The size of the ensemble to learn. Must be non-negative.
     * @param   baggingFraction
     *      The percentage of the data to sample (with replacement) to train
     *      each ensemble member.
     * @param   dimensionsFraction
     *      The percentage of the dimensions to sample at each node in each
     *      tree when training in order to determine the best split point.
     * @param   maxTreeDepth
     *      The maximum allowed tree depth. Must be positive.
     * @param   minLeafSize
     *      The minimum allowed number of examples that are allowed to fall
     *      into a leaf.
     * @param   random
     *      The random number generator to use.
     * @return 
     *      A new algorithm object for learning a random forest.
     */
    public static <CategoryType> BaggingCategorizerLearner<Vector, CategoryType> createCategorizationLearner(
        final int ensembleSize,
        final double baggingFraction,
        final double dimensionsFraction,
        final int maxTreeDepth,
        final int minLeafSize,
        final Random random)
    {
        // The minimum size for a split has to be at least double the leaf
        // size.
        final int minSplitSize = 2 * minLeafSize;
        
        final CategorizationTreeLearner<Vector, CategoryType> treeLearner =
            new CategorizationTreeLearner<>(
                new RandomSubVectorThresholdLearner<>(
                    new VectorThresholdInformationGainLearner<CategoryType>(
                        minLeafSize),
                    dimensionsFraction, random),
                minSplitSize,
                maxTreeDepth);

        return new BaggingCategorizerLearner<>(treeLearner, ensembleSize,
            baggingFraction, random);
    }
    
    /**
     * Creates a random forest learner for categorization outputs.
     * 
     * @param   <CategoryType>
     *      The type of categories.
     * @param   ensembleSize
     *      The size of the ensemble to learn. Must be non-negative.
     * @param   baggingFraction
     *      The percentage of the data to sample (with replacement) to train
     *      each ensemble member.
     * @param   dimensionsFraction
     *      The percentage of the dimensions to sample at each node in each
     *      tree when training in order to determine the best split point.
     * @param   maxTreeDepth
     *      The maximum allowed tree depth. Must be positive.
     * @param   minLeafSize
     *      The minimum allowed number of examples that are allowed to fall
     *      into a leaf.
     * @param   random
     *      The random number generator to use.
     * @return 
     *      A new algorithm object for learning a random forest.
     */
    public static <CategoryType> BaggingRegressionLearner<Vector> createRegressionLearner(
        final int ensembleSize,
        final double baggingFraction,
        final double dimensionsFraction,
        final int maxTreeDepth,
        final int minLeafSize,
        final Random random)
    {
        final int minSplitSize = 2 * minLeafSize;
        final RegressionTreeLearner<Vector> treeLearner =
            new RegressionTreeLearner<>(
                new RandomSubVectorThresholdLearner<>(
                    new VectorThresholdVarianceLearner(minLeafSize),
                    dimensionsFraction, random),
                null,
                minSplitSize,
                maxTreeDepth);

        return new BaggingRegressionLearner<>(treeLearner, ensembleSize,
            baggingFraction, random);
    }
        
}
