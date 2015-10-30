/*
 * File:                TransferEntropy.java
 * Authors:             Tom Brounstein
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright July 1, 2014, Sandia Corporation.
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
import gov.sandia.cognition.statistics.distribution.ChiSquareDistribution;

/**
 * A class for computing the chi-squared similarity between two vectors.  A chi-
 * squared test requires frequency vectors, typically representing documents,
 * so all values in the vectors will be computed as non-negative values. The 
 * test assumes one vector represents a document in a given category, and
 * another vector which is being tested to see if it is (likely) from the same
 * distribution as the original vector.  Note that the test is symmetric,
 * so the choice of which vector is the categorized vector and which one is the
 * testing vector is somewhat arbitrary.
 * @author trbroun
 * @since  3.4.2
 */
@PublicationReference(
    author="Yao-Tsung Chen, Meng Chang Chen",
    title="Using chi-square statistics to measure similarities for text categorization",
    type=PublicationType.Journal,
    year=2011,
    url="http://www.sciencedirect.com/science/article/pii/S0957417410008961#"
)
public class ChiSquaredSimilarity
{
    private Vector categorizedVector;
    private Vector testingVector;
    
    /**
     * Basic constructor.  Sets the variables in the intuitive manner.
     * @param categorizedVector  The vector from a known category.
     * @param testingVector The vector which is being tested to see if it comes from the same category.
     */
    public ChiSquaredSimilarity(Vector categorizedVector, Vector testingVector) {
        this.categorizedVector = categorizedVector.clone();
        this.testingVector = testingVector.clone();
    }
    
    /**
     * Basic setter for the categorized vector.
     * @param newCategorizedVector 
     */
    public void setCategorizedVector(Vector newCategorizedVector) {
        categorizedVector = newCategorizedVector.clone();
    }
    
    /**
     * Basic setter for the test vector.
     * @param newTestVector 
     */
    public void setTestVector(Vector newTestVector) {
        testingVector = newTestVector.clone();
    }
    
    /**
     * Basic getter for the categorized vector.
     * @return The categorized vector.
     */
    public Vector getCategorizedVector() {
        return categorizedVector;
    }
    
    /**
     * Basic getter for the testing vector.
     * @return The test vector.
     */
    public Vector getTestVector() {
        return testingVector;
    }
    
    /**
     * Computes the chi-squared statistic of the two vectors.  This is a raw number
     * and needs to be fed into a chi-squared distribution to compute a probability.
     * Both vectors must be non-zero.
     * @return The chi-squared statistic.
     */
    public double compute() {
        if(categorizedVector.getDimensionality() != testingVector.getDimensionality()) {
            throw new RuntimeException("Dimension mismatch.");
        }
        
        double sumCat = categorizedVector.norm1();
        double sumTest = testingVector.norm1();
        double h = sumCat + sumTest;
        
        if(sumCat == 0 || sumTest == 0) {
            throw new RuntimeException("One of the vectors is the 0 vector.");
        }
        
        double total = 0.;
        
        for (int ii = 0; ii < categorizedVector.getDimensionality(); ii++) {
            double catii = Math.abs(categorizedVector.getElement(ii));
            double testii = Math.abs(testingVector.getElement(ii));
            double pairSum = catii + testii;
            
            if (pairSum == 0) {
                continue;
            }
            
            double firstTerm = (catii*catii)/(sumCat*pairSum);
            double secondTerm = (testii*testii)/(sumTest*pairSum);
            
            total += firstTerm + secondTerm;            
        }
        
        double chiSquaredStat = h*total - h;
        
        return chiSquaredStat;
    }
    
    /**
     * Computes the chi-squared similarity statistic, then uses that to compute 
     * a cumulative probability.  Returns the probability that a chi-squared  
     * statistic falls between 0 and the critical value (the computed chi-squared
     * statistic for the two supplied vectors).  Naturally, a large chi-squared 
     * value generates a large cumulative probability value.
     * @return The probability of a chi-squared statistic being lower than the value of the chi-squared similarity of the given vectors.
     */
    public double computeCumulativeProbabilityValue() {
        double chiSquaredStat = compute();
        
        ChiSquareDistribution dist = new ChiSquareDistribution(categorizedVector.getDimensionality()-1);
        
        return dist.getCDF().evaluate(chiSquaredStat);
    }
    
}
