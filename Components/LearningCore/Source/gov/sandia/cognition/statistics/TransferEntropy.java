/*
 * File:                TransferEntropy.java
 * Authors:             Tom Brounstein
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 2, 2014, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.bayesian.ConditionalProbability;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class for calculating the transfer entropy of two vectors.  Note that in the
 * current implementation only values included in a transfer entropy object are
 * used in calculating the conditional probability.  For example: assume the two
 * vectors were [0,0,0,0,1] and [1,1,1,1,1] with window size 1.  Then, when 
 * computing the conditional probabilities, the state [1,1] will not be included
 * in the calculations since there's no triad that includes [1,1] in the prior.
 * This generates more intuitive results (the above example has a transfer entropy value of 0
 * in this implementation), and is easier to code.
 * @author trbroun
 
 */
@PublicationReference(
    author="Thomas Schreiber",
    title="Measuring Information Transfer",
    type=PublicationType.Journal,
    year=2000,
    url="http://journals.aps.org/prl/pdf/10.1103/PhysRevLett.85.461"
)
public class TransferEntropy{
    private final DefaultDataDistribution<TransferEntropyDistributionObject> oneTermPriorDistribution;
    private final DefaultDataDistribution<TransferEntropyDistributionObject> oneTermPosteriorDistribution;
    private final DefaultDataDistribution<TransferEntropyDistributionObject> twoTermPriorDistribution;
    private final DefaultDataDistribution<TransferEntropyDistributionObject> twoTermPosteriorDistribution;

    /**
     * Creates a new transfer entropy object
     * @param destinationVector
     * @param sourceVector
     * @param windowSize  How many steps in the past will be used in the analysis. 
     */
    public TransferEntropy(Vector sourceVector, Vector destinationVector, 
        int windowSize) {
        
        this(sourceVector, destinationVector, windowSize, windowSize);
    }
    
    /**
     * Creates a new transfer entropy object.
     * @param destinationVector  
     * @param sourceVector
     * @param destinationWindowSize How many time steps into the past will be used with the vectorBeingActedOn
     * @param sourceWindowSize How many time steps into the past will be sued with the vectorActingOnOtherVector
     */
    public TransferEntropy(Vector sourceVector, Vector destinationVector, 
        int sourceWindowSize, int destinationWindowSize) {
        
        oneTermPriorDistribution = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        twoTermPriorDistribution = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        oneTermPosteriorDistribution = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        twoTermPosteriorDistribution = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        
        //Ensure window sizes are at least 1.
        destinationWindowSize = Math.max(destinationWindowSize, 1);
        sourceWindowSize = Math.max(sourceWindowSize, 1);
        
        int maxWindow = Math.max(destinationWindowSize, sourceWindowSize);
        
        //Generates the distributions by iterating over the vectors.
        for (int ii=0; ii < destinationVector.getDimensionality() - maxWindow; ii++) {
            
            ArrayList<Integer> destinationVectorPrior = new ArrayList<Integer>(destinationWindowSize);
            addVectorElements(destinationVector, ii + maxWindow - destinationWindowSize, ii + maxWindow, destinationVectorPrior);
            
            ArrayList<Integer> sourceVectorPrior = new ArrayList<Integer>(sourceWindowSize);
            addVectorElements(sourceVector, ii + maxWindow - sourceWindowSize, ii + maxWindow, sourceVectorPrior);
            
            Integer posterior = (int) destinationVector.getElement(ii + maxWindow);
            
            oneTermPriorDistribution.increment(new TransferEntropyDistributionObject(destinationVectorPrior, null, null));
            twoTermPriorDistribution.increment(new TransferEntropyDistributionObject(destinationVectorPrior, sourceVectorPrior, null));
            oneTermPosteriorDistribution.increment(new TransferEntropyDistributionObject(destinationVectorPrior, null, posterior));
            twoTermPosteriorDistribution.increment(new TransferEntropyDistributionObject(destinationVectorPrior, sourceVectorPrior, posterior));
        }
    }
    
    /**
     * Creates a new transfer entropy object using lists instead of vectors.
     * @param destinationList
     * @param sourceList
     * @param windowSize  How many steps in the past will be used in the analysis. 
     */
    public TransferEntropy(List<? extends Object> sourceList, List<? extends Object> destinationList, 
        int windowSize) {
        
        this(sourceList, destinationList, windowSize, windowSize);
    }
    
    /**
     * Creates a new transfer entropy object using lists instead of vectors.
     * @param destinationList  
     * @param sourceList
     * @param destinationWindowSize How many time steps into the past will be used with the vectorBeingActedOn
     * @param sourceWindowSize How many time steps into the past will be sued with the vectorActingOnOtherVector
     */
    public TransferEntropy(List<? extends Object> sourceList, List<? extends Object> destinationList, 
        int sourceWindowSize, int destinationWindowSize) {
        
        oneTermPriorDistribution = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        twoTermPriorDistribution = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        oneTermPosteriorDistribution = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        twoTermPosteriorDistribution = new DefaultDataDistribution<TransferEntropyDistributionObject>();
        
        //Ensure window sizes are at least 1.
        destinationWindowSize = Math.max(destinationWindowSize, 1);
        sourceWindowSize = Math.max(sourceWindowSize, 1);
        
        int maxWindow = Math.max(destinationWindowSize, sourceWindowSize);
        
        //Generates the distributions by iterating over the vectors.
        for (int ii=0; ii < destinationList.size() - maxWindow; ii++) {
            
            List<? extends Object> destinationListPrior = destinationList.subList(
                ii + maxWindow - destinationWindowSize, ii + maxWindow);
            
            List<? extends Object> sourceListPrior = sourceList.subList(ii + maxWindow - sourceWindowSize,
                ii + maxWindow);
            
            Object posterior = destinationList.get(ii + maxWindow);
            
            oneTermPriorDistribution.increment(new TransferEntropyDistributionObject(destinationListPrior, null, null));
            twoTermPriorDistribution.increment(new TransferEntropyDistributionObject(destinationListPrior, sourceListPrior, null));
            oneTermPosteriorDistribution.increment(new TransferEntropyDistributionObject(destinationListPrior, null, posterior));
            twoTermPosteriorDistribution.increment(new TransferEntropyDistributionObject(destinationListPrior, sourceListPrior, posterior));
        }
    }
    
    /**
     * Creates a new transfer entropy object using distributions.
     * @param oneTermPriorDistribution  The prior distribution over one vector.  The prior one term prior should be filled in,
     * while the second vector prior and posterior should be null (or a consistent value over each element of the domain).
     * @param oneTermPosteriorDistribution  A distribution over the first term prior and the posterior.  Leave the second term vector prior
     * as null, or a consistent value across the domain space.
     * @param twoTermPriorDistribution  A distribution over TransferEntropyDistributionObjects where the prior for the first and second vector is supplied. 
     * @param twoTermPosteriorDistribution  A distribution over TransferEntropyDistributionObjects where all values are supplied.
     */
    public TransferEntropy(DefaultDataDistribution<TransferEntropyDistributionObject> oneTermPriorDistribution,
        DefaultDataDistribution<TransferEntropyDistributionObject> oneTermPosteriorDistribution,
        DefaultDataDistribution<TransferEntropyDistributionObject> twoTermPriorDistribution,
        DefaultDataDistribution<TransferEntropyDistributionObject> twoTermPosteriorDistribution) {
        
        this.oneTermPriorDistribution = oneTermPriorDistribution;
        this.oneTermPosteriorDistribution = oneTermPosteriorDistribution;
        this.twoTermPriorDistribution = twoTermPriorDistribution;
        this.twoTermPosteriorDistribution = twoTermPosteriorDistribution;
    }

    /**
     * Computes the transfer entropy value.
     * @return The transfer entropy from one vector to the other, or one distribution to the other.
     */
    public double compute() {
        
        double solution = 0.0;
        ConditionalProbability<TransferEntropyDistributionObject> instance = new ConditionalProbability<TransferEntropyDistributionObject>();
        
        for (TransferEntropyDistributionObject state : twoTermPosteriorDistribution.getDomain()) {
            
            double answer = partialSum(state, instance);
            solution += answer;
        }
        
        return solution;
    }
    
    /**
     * Computes the transfer entropy value and returns a map of the states with their corresponding partial sums.
     * Used to identify which states contribute the most or least to the transfer entropy.
     * @return A pair, where the first element is the transfer entropy value between the two vectors, and the second
     * element is a map from states to their corresponding partial sums.
     */
    public Pair<Double, HashMap<TransferEntropyDistributionObject, TransferEntropyPartialSumObject>> computeWithPartialSums() {
        
        double solution = 0.0;
        HashMap<TransferEntropyDistributionObject, TransferEntropyPartialSumObject> mapSolution = 
            new HashMap<TransferEntropyDistributionObject, TransferEntropyPartialSumObject>();
        ConditionalProbability<TransferEntropyDistributionObject> instance = 
            new ConditionalProbability<TransferEntropyDistributionObject>();
        
        for (TransferEntropyDistributionObject state : twoTermPosteriorDistribution.getDomain()) {
            
            TransferEntropyPartialSumObject answerWithWrapper = partialSumWithWrapper(state, instance);
            solution += answerWithWrapper.partialSum;
            mapSolution.put(state, answerWithWrapper);
        }
        
        Pair<Double, HashMap<TransferEntropyDistributionObject, TransferEntropyPartialSumObject>> finalAnswer = 
            new DefaultPair<Double, HashMap<TransferEntropyDistributionObject, TransferEntropyPartialSumObject>>(solution, mapSolution);
        return finalAnswer;
    }
    
    /**
     * A helper method to add values from the vector to an array list, and transform them to integers.
     * Gets all elements in range start to finish.  Is 0 indexed.
     * @param vector  The vector where elements are being taken from.
     * @param start  The start index for elements to be copied from the vector.
     * @param finish  The end index for elements to be copied from the vector.
     * @param temp The ArrayList that has elements being added to it.
     */
    private void addVectorElements(Vector vector, int start, int finish, ArrayList<Integer> temp) {
        for (int ii=start; ii < finish; ii++) {
            temp.add((int) vector.getElement(ii));
        }
    }
    
    /**
     * A helper method that computes an individual term in the summation.
     * @param state  The TransferEntropyDistributionObjects state being analyzed in this element of the sum.
     * @param instance  A conditional probability object, used to calculate elements of the transfer entropy.
     * @return The transfer entropy partial sum for this specific state.
     */
    private double partialSum(TransferEntropyDistributionObject state, ConditionalProbability<TransferEntropyDistributionObject> instance) {
        //Note that numerator and denominator can never be 0 (if they are, there's a bug in the code).
        //At this point in the code we're itereating over all observed two vector posterior states,
        //which take the form i_(n+1), i_n^k, j_n^l.  The numerator looks at probability of i_(n+1) 
        //given i_n^k and j_n^k; this probability is greater than 0 since we know there is at least
        //one case of i_(n+1), i_n^k, j_n^l is seen since it's included in the loop.  The denominator
        //follows similarly.  Thus, both of these values are non-0.
        double numerator = instance.computeConditionalProbabilityWhenDataTypeHasHistoricalData(
                new TransferEntropyDistributionObject(state.destinationVectorPrior, state.sourceVectorPrior), state, 
                twoTermPriorDistribution, twoTermPosteriorDistribution);
        double denominator = instance.computeConditionalProbabilityWhenDataTypeHasHistoricalData(
                new TransferEntropyDistributionObject(state.destinationVectorPrior), new TransferEntropyDistributionObject(state.destinationVectorPrior, state.posterior), 
                oneTermPriorDistribution, oneTermPosteriorDistribution);
        double multiple = twoTermPosteriorDistribution.getProbabilityFunction().evaluate(
            state);
        double answer = multiple * Math.log(numerator/denominator);
        //The return statement could have been in the previous statement, but this is to be explicit.
        return answer;
    }
    
    /**
     * A helper method that wraps a transfer entropy partial sum value with extra information.
     * @param state  The state being evaluated.
     * @param instance A conditional probability object, used to calculate elements of the transfer entropy.
     * @return An object which includes the state, the partial sum value, and how many times the state occurs.
     */
    private TransferEntropyPartialSumObject partialSumWithWrapper(TransferEntropyDistributionObject state, ConditionalProbability<TransferEntropyDistributionObject> instance) {
        double answer = partialSum(state, instance);
        double numberOfAppearances = twoTermPosteriorDistribution.get(state);
        return new TransferEntropyPartialSumObject(state, answer, numberOfAppearances);
    }
    
    /**
     * A helper class that define the objects used by the distributions in transfer entropy.
     */
    public static class TransferEntropyDistributionObject{
        public final List<? extends Object> destinationVectorPrior;
        public final List<? extends Object> sourceVectorPrior;
        public final Object posterior;
        
        /**
         * Basic constructor for a TransferEntropyDistributionObjects.  FirstVectPrior is assigned in the obvious manner, all other values null.
         * @param destinationVectorPrior 
         */
        public TransferEntropyDistributionObject(List<? extends Object> destinationVectorPrior) {
            this(destinationVectorPrior, null, null);
        }
        
        /**
         * Basic constructor for a TransferEntropyDistributionObjects.  First and second vector priors are assigned in the obvious manner, posterior is null.
         * @param destinationVectorPrior
         * @param sourceVectorPrior 
         */
        public TransferEntropyDistributionObject(List<? extends Object> destinationVectorPrior, List<? extends Object> sourceVectorPrior) {
            this(destinationVectorPrior, sourceVectorPrior, null);
        }
        
        /**
         * Basic constructor for a TransferEntropyDistributionObjects.  First vector prior and posterior are assigned in the obvious manner, second vector prior is null.
         * @param destinationVectorPrior
         * @param posterior 
         */
        public TransferEntropyDistributionObject(List<? extends Object> destinationVectorPrior, Object posterior) {
            this(destinationVectorPrior, null, posterior);
        }
        
        /**
         * Basic constructor for a TransferEntropyDistributionObjects.  All values are assigned in the obvious manner.
         * @param destinationVectorPrior
         * @param sourceVectorPrior
         * @param posterior 
         */
        public TransferEntropyDistributionObject(List<? extends Object> destinationVectorPrior, List<? extends Object> sourceVectorPrior, Object posterior) {
            this.destinationVectorPrior = destinationVectorPrior;
            this.sourceVectorPrior = sourceVectorPrior;
            this.posterior = posterior;
        }
        
        @Override
        public boolean equals(Object other) {
            if (other instanceof TransferEntropyDistributionObject) {
                return equals((TransferEntropyDistributionObject) other);
            }
            return false;
        }
        
        /**
         * Equals function for comparing two TransferEntropyDistributionObjects.
         * @param other  The other TransferEntropyDistributionObjects being compared.
         * @return If the two objects are equal.  Compares each component individually.
         */
        private boolean equals(TransferEntropyDistributionObject other) {
            return compareComponent(destinationVectorPrior, other.destinationVectorPrior) &&
                compareComponent(sourceVectorPrior, other.sourceVectorPrior) &&
                compareComponent(posterior, other.posterior);
        }
        
        /**
         * Compares two objects to see if they're both null.  Otherwise, checks if they're equal.
         * @param first
         * @param second
         * @return If the two objects are both null or equal.
         */
        private boolean compareComponent(Object first, Object second) {
            if (first==null) {
                return second==null;
            }
            return first.equals(second);
        }
        
        @Override
        public int hashCode() {
            ArrayList<Object> temp = new ArrayList<Object>(destinationVectorPrior);
            if (sourceVectorPrior !=  null){
                temp.addAll(sourceVectorPrior);
            }
            if (posterior != null){
                temp.add(posterior);
            }
            
            return temp.hashCode();
        }
        
        @Override
        public String toString() {
            return "Source vector state: " + sourceVectorPrior + 
                "\nDestiation vector state: " + destinationVectorPrior + 
                "\nPosterior: " + posterior;
        }
        
    }
    
    /**
     * Helper class for holding information about the partial sums.
     */
    public static class TransferEntropyPartialSumObject {
        public final TransferEntropy.TransferEntropyDistributionObject state;
        public final double partialSum;
        public final double numberOfAppearances;
        
        /**
         * Basic constructor that assigns values in the intuitive way.
         * @param state The state represented by this partial sum;
         * @param partialSum The transfer entropy partial sum.
         * @param numberOfApperances The number of times 
         */
        public TransferEntropyPartialSumObject(TransferEntropy.TransferEntropyDistributionObject state, double partialSum, double numberOfApperances) {
            this.state = state;
            this.partialSum = partialSum;
            this.numberOfAppearances = numberOfApperances;
        }
        
        @Override
        public String toString() {
            return state.toString() + "\nPartial Sum: " + partialSum + "\nNumber of Appearances: " + numberOfAppearances;
        }
        
        public boolean equals(TransferEntropyPartialSumObject other) {
            return (state.equals(other.state)) && (Math.abs(partialSum - other.partialSum) < 0.00001)
                && (Math.abs(numberOfAppearances - other.numberOfAppearances) < 0.5);
        }
    }
}
