/*
 * File:                AbstractDeltaCategorizer.java
 * Authors:             Alex Killian
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 24, 2016, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.algorithm.delta;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.ValueDiscriminantPair;
import gov.sandia.cognition.learning.function.categorization.DiscriminantCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Burrows Delta algorithm is primarily used for authorship attribution, but
 * can be used for other applications. This abstract class can be used to 
 * implement different variants of Burrows' Delta. The input type for this
 * algorithm is always Vector. Each element in the vectors should correspond
 * to a feature and all vectors should be of the same size
 * and their elements should correspond to the same features. Each element in
 * the vectors is expected to be the number of times the corresponding feature 
 * occurs in the text that the vector was generated from divided by the total number
 * of features in that text. This is referred to as relative feature frequency in
 * much of the literature. You may have to read a paper on Burrows' Delta to 
 * understand how to construct the vectors correctly.
 * 
 * If this algorithm is going to be used for other applications the most important
 * constraint to still obey is that all vectors should be of the same size
 * and their elements should correspond to the same thing.
 * 
 * @author alkilli
 * @param <CategoryType>
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author={
                "John Burrows"
            },
            title="'Delta': a Measure of Stylistic Difference and a Guide to Likely Authorship",
            type=PublicationType.Journal,
            year=2002,
            pages={267,287}
        )
    }

)
public abstract class AbstractDeltaCategorizer<CategoryType>
    extends AbstractCloneableSerializable
    implements DiscriminantCategorizer<Vector,CategoryType,Double>
{
    
    /**
     * The learner that was used to train this categorizer.
     */
    protected final AbstractLearner<CategoryType> learner;
    
    /**
     * The stddev of each feature.
     */
    protected final ArrayList<Double> featureStddev; // We want O(1) lookup
    
   
    /**
     * Constructor that takes a learner and featureStddev.
     * 
     * @param learner
     * @param featureStddev 
     */
    protected AbstractDeltaCategorizer(
        AbstractLearner<CategoryType> learner,
        ArrayList<Double> featureStddev) 
    {
        this.learner = learner;
        this.featureStddev = featureStddev;
    }
    
    /**
     * This abstract method should implement evaluation aspect of this general 
     * algorithm. That is, given an unknownVector, this method should return
     * a discriminant value paired with the corresponding most likely category.
     * The discriminant value should be the score.
     * 
     * @param unknownInput
     * @return 
     */
    @Override
    public abstract ValueDiscriminantPair<CategoryType, Double> evaluateWithDiscriminant(
        Vector unknownInput);

    /**
     * Returns a set of all the known categories.
     * 
     * @return 
     */
    @Override
    public Set<? extends CategoryType> getCategories()
    {
        Set<CategoryType> cats = new HashSet<CategoryType>();
        
        for (InputOutputPair<? extends Vector, CategoryType> pair :
            learner.trainingSet) 
        {
            cats.add(pair.getOutput());
        }
        
        return cats;
    }
    
    /**
     * Getter for featureStddev
     * 
     * @return 
     */
    public List<Double> getFeatureStddev() {
        return Collections.unmodifiableList(featureStddev);
    }
    
    /**
     * Abstract learner for delta algorithms. For each implementation of a delta 
     * algorithm, there should be a learner that extends AbstractLearner and
     * creates and returns a trained categorizer.
     * @param <CategoryType> Type of the categories of the categorizer.
     */
    public static abstract class AbstractLearner<CategoryType>
        extends AbstractCloneableSerializable
        implements SupervisedBatchLearner<Vector,CategoryType,
                AbstractDeltaCategorizer<CategoryType>>
    {
        /**
         * The training set.
         */
        protected Collection<? extends InputOutputPair<? extends Vector, CategoryType>> trainingSet;
        
        /**
         * Default constructor.
         */
        public AbstractLearner()
        {
        }

        /**
         * Method that does the training.
         * 
         * @param trainingSet
         * @return 
         */
        @Override
        public abstract AbstractDeltaCategorizer<CategoryType> learn(
            final Collection<? extends InputOutputPair<? extends Vector,
                CategoryType>> trainingSet);
    }
}
