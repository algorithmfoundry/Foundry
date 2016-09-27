/*
 * File:                CosineDeltaCategorizer.java
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
import java.util.Collections;
import java.util.List;

/**
 * The Cosine Delta algorithm implementation. The same as Burrows' Delta except 
 * it uses cosine similarity.
 * 
 * @author alkilli
 * @param <CategoryType>
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author={
                "Peter W. H. Smith  and  W. Aldridge "
            },
            title="Improving Authorship Attribution: Optimizing Burrows' Delta Method",
            type=PublicationType.Journal,
            year=2011,
            pages={63,88}
        )
    }
)
public class CosineDeltaCategorizer<CategoryType>
    extends AbstractDeltaCategorizer<CategoryType>
{
    /**
     * For Cosine Delta we also need to keep track of the overall mean
     * of the occurrences of each feature.
     */
    private final ArrayList<Double> featureMeans; // We want O(1) lookup
    
   
    /**
     * Constructor that takes learner, featureStddev, and featureMeans.
     * 
     * @param learner
     * @param featureStddev
     * @param featureMean 
     */
    protected CosineDeltaCategorizer(
        Learner<CategoryType> learner,
        ArrayList<Double> featureStddev,
        ArrayList<Double> featureMean) 
    {
        super(learner, featureStddev);
        this.featureMeans = featureMean;
    }
    
    /**
     * Getter for featureMeans.
     * 
     * @return 
     */
    public List<Double> getFeatureMeans() {
        return Collections.unmodifiableList(featureMeans);
    }

    /**
     * This method implements the evaluation aspect of CosineDelta. That is,
     * given an unknownVector, this method should return a discriminant 
     * value paired with the corresponding most likely category.
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
            double dotProduct = 0.0;
            double knownLength = 0.0;
            double unknownLength = 0.0;
            for (int featureIndex = 0; 
                featureIndex < knownVector.getDimensionality();
                featureIndex++) 
            {
                
                // Scale feature
                double known = (knownVector.get(featureIndex) -
                    featureMeans.get(featureIndex)) / 
                        featureStddev.get(featureIndex);
                double unknown = (unknownVector.get(featureIndex) -
                    featureMeans.get(featureIndex)) / 
                        featureStddev.get(featureIndex);

                // Dot product
                dotProduct += known * unknown;

                // Known length
                knownLength += Math.pow(known, 2);

                // Unkown length
                unknownLength += Math.pow(unknown, 2);
            }
            
            // Calculte cosine delta and record result
            double preDelta = dotProduct / (Math.sqrt(knownLength) * Math.sqrt(unknownLength));
            
            if (preDelta > 1.0) {
                preDelta = 1.0;
            }
            else if (preDelta < -1.0) {
                preDelta = -1.0;
            }
            
            delta = Math.acos(preDelta);
            if (delta < minDelta) 
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
     * Learner for a CosineDeltaCategorizer.
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
        public CosineDeltaCategorizer<CategoryType> learn(
            final Collection<? extends InputOutputPair<? extends Vector, CategoryType>> trainingSet)
        {
            // Get training set
            this.trainingSet = trainingSet;
            
            // Map for storing stddev for each feature for fast lookup
            ArrayList<Double> featureStddev = new ArrayList<>();
            ArrayList<Double> featureMean = new ArrayList<>();

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
                double mean = UnivariateStatisticsUtil.computeMean(observations);
                Double stddev = UnivariateStatisticsUtil.computeStandardDeviation(observations, mean);
                stddev = (stddev.equals(0.0)) ? 1E10 : stddev;
                featureStddev.add(stddev);
                featureMean.add(mean);
            }

            // Return evaluator
            CosineDeltaCategorizer<CategoryType> bdc = 
                new CosineDeltaCategorizer<CategoryType>(this, featureStddev, featureMean); 
            return bdc;
        }
    }
}
