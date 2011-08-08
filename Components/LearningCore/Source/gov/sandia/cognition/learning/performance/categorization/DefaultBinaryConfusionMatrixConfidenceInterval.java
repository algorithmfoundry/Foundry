/*
 * File:                ConfusionMatrixConfidenceInterval.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.learning.performance.categorization.DefaultBinaryConfusionMatrix;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Summarizer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Puts Student-t ConfidenceIntervals on each entry of the ConfusionMatrix
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class DefaultBinaryConfusionMatrixConfidenceInterval
{
    
    /**
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definitely within the bound.
     */
    private double confidence;
    
    /**
     * The fraction of target falses incorrectly classified as false,
     * FalseNegatives / TotalPositives = 1 - TruePositivesRate
     */
    private ConfidenceInterval falseNegativesRate;
    
    /**
     * The fraction of target falses incorrectly classified as true,
     * FalsePositives / TotalNegatives = 1 - TrueNegativesRate
     */
    private ConfidenceInterval falsePositivesRate;
    
    /**
     * The fraction of target trues that were correctly classified as true,
     * TruePositives / TotalPositives = TruePositives / (TruePositives + FalseNegatives)
     */
    private ConfidenceInterval truePositivesRate;
    
    /**
     * The fraction of negative targets correctly classified as false,
     * TrueNegatives / TotalNegatives = TrueNegatives / (TrueNegatives + FalsePositives)
     */
    private ConfidenceInterval trueNegativesRate;
    
    
    /**
     * Creates a new instance of ConfusionMatrixConfidenceInterval
     * @param confidence
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definitely within the bound.
     * @param falsePositivesRate
     * The fraction of target falses incorrectly classified as true,
     * FalsePositives / TotalNegatives = 1 - TrueNegativesRate
     * @param falseNegativesRate
     * The fraction of target falses incorrectly classified as false,
     * FalseNegatives / TotalPositives = 1 - TruePositivesRate
     * @param truePositivesRate
     * The fraction of target trues that were correctly classified as true,
     * TruePositives / TotalPositives = TruePositives / (TruePositives + FalseNegatives)
     * @param trueNegativesRate
     * The fraction of negative targets correctly classified as false,
     * TrueNegatives / TotalNegatives = TrueNegatives / (TrueNegatives + FalsePositives)
     */
    public DefaultBinaryConfusionMatrixConfidenceInterval(
        final double confidence,
        final ConfidenceInterval falsePositivesRate,
        final ConfidenceInterval falseNegativesRate,
        final ConfidenceInterval truePositivesRate,
        final ConfidenceInterval trueNegativesRate )
    {
        this.setConfidence( confidence );
        this.setFalseNegativesRate( falseNegativesRate );
        this.setFalsePositivesRate( falsePositivesRate );
        this.setTruePositivesRate( truePositivesRate );
        this.setTrueNegativesRate( trueNegativesRate );
    }
    
    /**
     * Getter for falsePositivesRate
     * @return
     * The fraction of target falses incorrectly classified as true,
     * FalsePositives / TotalNegatives = 1 - TrueNegativesRate
     */
    public ConfidenceInterval getFalsePositivesRate()
    {
        return this.falsePositivesRate;
    }
    
    /**
     * Setter for falsePositivesRate
     * @param falsePositivesRate
     * The fraction of target falses incorrectly classified as true,
     * FalsePositives / TotalNegatives = 1 - TrueNegativesRate
     */
    protected void setFalsePositivesRate(
        ConfidenceInterval falsePositivesRate)
    {
        this.falsePositivesRate = falsePositivesRate;
    }
    
    /**
     * Getter for falseNegativesRate
     * @return
     * The fraction of target falses incorrectly classified as false,
     * FalseNegatives / TotalPositives = 1 - TruePositivesRate
     */
    public ConfidenceInterval getFalseNegativesRate()
    {
        return this.falseNegativesRate;
    }
    
    /**
     * Setter for falseNegativesRate
     * @param falseNegativesRate
     * The fraction of target falses incorrectly classified as false,
     * FalseNegatives / TotalPositives = 1 - TruePositivesRate
     */
    protected void setFalseNegativesRate(
        ConfidenceInterval falseNegativesRate)
    {
        this.falseNegativesRate = falseNegativesRate;
    }
    
    /**
     * Getter for truePositivesRate
     * @return
     * The fraction of target trues that were correctly classified as true,
     * TruePositives / TotalPositives = TruePositives / (TruePositives + FalseNegatives)
     */
    public ConfidenceInterval getTruePositivesRate()
    {
        return this.truePositivesRate;
    }
    
    /**
     * Setter for truePositivesRate
     * @param truePositivesRate
     * The fraction of target trues that were correctly classified as true,
     * TruePositives / TotalPositives = TruePositives / (TruePositives + FalseNegatives)
     */
    protected void setTruePositivesRate(
        ConfidenceInterval truePositivesRate)
    {
        this.truePositivesRate = truePositivesRate;
    }
    
    /**
     * Getter for trueNegativesRate
     * @return
     * The fraction of negative targets correctly classified as false,
     * TrueNegatives / TotalNegatives = TrueNegatives / (TrueNegatives + FalsePositives)
     */
    public ConfidenceInterval getTrueNegativesRate()
    {
        return this.trueNegativesRate;
    }
    
    /**
     * Setter for trueNegativesRate
     * @param trueNegativesRate
     * The fraction of negative targets correctly classified as false,
     * TrueNegatives / TotalNegatives = TrueNegatives / (TrueNegatives + FalsePositives)
     */
    protected void setTrueNegativesRate(
        ConfidenceInterval trueNegativesRate)
    {
        this.trueNegativesRate = trueNegativesRate;
    }
    
    
    /**
     * Getter for confidence
     * @return
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definitely within the bound.
     */
    public double getConfidence()
    {
        return this.confidence;
    }
    
    /**
     * Setter for confidence
     * @param confidence
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definitely within the bound.
     */
    protected void setConfidence(
        double confidence)
    {
        DefaultBinaryConfusionMatrixConfidenceInterval.checkConfidence(confidence);
        this.confidence = confidence;
    }
    
    /**
     * Checks to make sure that confidence is between 0.0 and 1.0.
     *
     * @param  confidence The confidence.
     */
    protected static void checkConfidence(
        final double confidence)
    {
        if ( confidence < 0.0 || confidence > 1.0 )
        {
            throw new IllegalArgumentException(
                "confidence must be between 0.0 and 1.0");
        }
    }
        
    @Override
    public String toString()
    {
        return "True Negatives: " + this.getTrueNegativesRate()  + "\n"
            + "False Positives: " + this.getFalsePositivesRate() + "\n"
            + "False Negatives: " + this.getFalseNegativesRate() + "\n"
            + "True Positives: "  + this.getTruePositivesRate();
    }
    
    /**
     * Computes the ConfidenceIntervals for the given Collection of
     * ConfusionMatrices
     * @param data
     * Collection of ConfusionMatrices from which to compute the
     * ConfidenceIntervals
     * @param confidence
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definitely within the bound.
     * @return
     * ConfusionMatrixConfidenceInterval that captures the ConfidenceIntervals
     * of the correct/incorrect classification rates
     */
    static public DefaultBinaryConfusionMatrixConfidenceInterval compute(
        Collection<? extends DefaultBinaryConfusionMatrix> data,
        double confidence )
    {
        
        ArrayList<Double> fpp = new ArrayList<Double>( data.size() );
        ArrayList<Double> fnp = new ArrayList<Double>( data.size() );
        ArrayList<Double> tpp = new ArrayList<Double>( data.size() );
        ArrayList<Double> tnp = new ArrayList<Double>( data.size() );
        
        for( DefaultBinaryConfusionMatrix cm : data )
        {
            double positives = cm.getTruePositivesCount() + cm.getFalseNegativesCount();
            double negatives = cm.getTrueNegativesCount() + cm.getFalsePositivesCount();
            
            // If there were no "positives" in the ConfusionMatrix, then
            // we should not use the rates into the confidence intervals
            if( positives != 0.0 )
            {
                tpp.add( cm.getTruePositivesRate() );
                fnp.add( cm.getFalseNegativesRate() );
            }
            
            // If there were no "positives" in the ConfusionMatrix, then
            // we should not use the rates into the confidence intervals            
            if( negatives != 0.0 )
            {
                tnp.add( cm.getTrueNegativesRate() );
                fpp.add( cm.getFalsePositivesRate() );
            }            
            
        }
        
        StudentTConfidence ttest = new StudentTConfidence();
        ConfidenceInterval fpci = ttest.computeConfidenceInterval( fpp, confidence );
        ConfidenceInterval fnci = ttest.computeConfidenceInterval( fnp, confidence );
        ConfidenceInterval tpci = ttest.computeConfidenceInterval( tpp, confidence );
        ConfidenceInterval tnci = ttest.computeConfidenceInterval( tnp, confidence );
        return new DefaultBinaryConfusionMatrixConfidenceInterval(
            confidence, fpci, fnci, tpci, tnci );
        
    }
    
    /**
     * An implementation of the {@code Summarizer} interface for creating a
     * {@code ConfusionMatrixInterval}
     */
    public static class Summary
        extends AbstractCloneableSerializable
        implements Summarizer<DefaultBinaryConfusionMatrix, DefaultBinaryConfusionMatrixConfidenceInterval>
    {
        /** The confidence for the created interval. */
        private double confidence;
        
        /**
         * Creates a new Summarizer.
         *
         * @param  confidence The confidence for the interval.
         */
        public Summary(
            final double confidence)
        {
            super();
            
            this.setConfidence(confidence);
        }
        
        public DefaultBinaryConfusionMatrixConfidenceInterval summarize(
            final Collection<? extends DefaultBinaryConfusionMatrix> data)
        {
            return DefaultBinaryConfusionMatrixConfidenceInterval.compute(data, confidence);
        }
        
        /**
         * Gets the confidence for created the interval.
         *
         * @return The confidence for the created interval.
         */
        public double getConfidence()
        {
            return this.confidence;
        }
        
        /**
         * Sets the confidence for created the interval.
         *
         * @param  confidence The confidence for the created interval.
         */
        public void setConfidence(
            final double confidence)
        {
            DefaultBinaryConfusionMatrixConfidenceInterval.checkConfidence(confidence);
            this.confidence = confidence;
        }
    }
}
