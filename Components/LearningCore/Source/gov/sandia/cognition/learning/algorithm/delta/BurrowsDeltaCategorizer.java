/*
 * File:                BurrowsDeltaCategorizer.java
 * Authors:             Alex Killian
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 23, 2016, Sandia Corporation.
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
import gov.sandia.cognition.learning.data.DefaultWeightedValueDiscriminant;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.ValueDiscriminantPair;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The regular Burrows' Delta algorithm implementation.
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
            title="‘Delta’: a Measure of Stylistic Difference and a Guide to Likely Authorship",
            type=PublicationType.Journal,
            year=2002,
            pages={267,287}
        )
    }

)
public class BurrowsDeltaCategorizer<CategoryType>
    extends AbstractDeltaCategorizer<CategoryType>
{
   
    /**
     * Constructor that takes a learner and featureStddev.
     * 
     * @param learner
     * @param featureStddev 
     */
    protected BurrowsDeltaCategorizer(
        Learner<CategoryType> learner,
        ArrayList<Double> featureStddev) 
    {
        super(learner, featureStddev);
    }

    /**
     * This method implements the evaluation aspect of BurrowsDelta.
     * That is, given an unknownVector, this method should return
     * a discriminant value paired with the corresponding most likely category.
     * The discriminant value is the score.
     * 
     * @param unknownVector
     * @return 
     */
    @Override
    public ValueDiscriminantPair<CategoryType, Double> evaluateWithDiscriminant(
        Vector unknownVector)
    {
        
        double minDelta = Double.MAX_VALUE;
        CategoryType minCat = null;
        
        DefaultWeightedValueDiscriminant<CategoryType> result = 
            new DefaultWeightedValueDiscriminant<CategoryType>();

        for (InputOutputPair<? extends Vector, CategoryType> pair : 
            learner.trainingSet) 
        {
            Vector knownVector = pair.getInput();
            double delta = 0.0;
            for (int featureIndex = 0; 
                featureIndex < knownVector.getDimensionality();
                featureIndex++) 
            {
                
                delta += Math.abs((unknownVector.get(featureIndex) - 
                        knownVector.get(featureIndex)) /
                        (double) this.featureStddev.get(featureIndex));
            }
            
            if (delta < minDelta ) 
            {
                minDelta = delta;
                minCat = pair.getOutput();
            }
        }
        result.setValue(minCat);
        result.setWeight(minDelta);

        return result;
    }
    
    /**
     * Evaluates an unknown input, but does not return the discriminant value.
     * Only returns the category that the unknown input most likely corresponds 
     * to.
     * 
     * @param unknownInput
     * @return 
     */
    @Override
    public CategoryType evaluate(
        final Vector unknownInput)
    {
        return this.evaluateWithDiscriminant(unknownInput).getValue();
    }
    
    /**
     * Learner for a BurrowsDeltaCategorizer.
     * @param <CategoryType> Type of the categories of the categorizer.
     */
    public static class Learner<CategoryType>
        extends AbstractLearner<CategoryType>
    {
        
        /**
         * Default constructor.
         */
        public Learner()
        {
        }

        @Override
        public BurrowsDeltaCategorizer<CategoryType> learn(
            final Collection<? extends InputOutputPair<? extends Vector, CategoryType>> trainingSet)
        {
            // Get training set
            this.trainingSet = trainingSet;
            
            // Map for storing stddev for each feature for fast lookup
            ArrayList<Double> featureStddev = new ArrayList<Double>();

            // Get size of feature vectors (all feature vectors have the same size)
            int vectorSize = 0;
            for (InputOutputPair<? extends Vector, CategoryType> pair : trainingSet) 
            {
                    Vector vector = pair.getInput();
                    vectorSize = vector.getDimensionality();
                    break;
            }

            // Learn stats about the relative frequencies of each feature in the feature vector
            for (int featureIndex = 0; featureIndex < vectorSize; featureIndex++)
            {
                List<Double> observations = new ArrayList<Double>();
                for (InputOutputPair<? extends Vector, CategoryType> pair : trainingSet) 
                {
                    Vector vector = pair.getInput();
                    observations.add(vector.get(featureIndex));
                }
                Double stddev = UnivariateStatisticsUtil.computeStandardDeviation(observations);
                stddev = (stddev.equals(0.0)) ? 1E10 : stddev;
                featureStddev.add(stddev);
            }

            // Reaturn evaluator
            BurrowsDeltaCategorizer<CategoryType> bdc = 
                new BurrowsDeltaCategorizer<CategoryType>(this, featureStddev);
            return bdc;
        }
    }
}
