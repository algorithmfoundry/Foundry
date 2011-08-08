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

package gov.sandia.cognition.statistics.method;

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
public class ConfusionMatrixConfidenceInterval
{
    
    /**
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definitely within the bound.
     */
    private double confidence;
    
    /**
     * The fraction of target falses incorrectly classified as false,
     * FalseNegatives / TotalPositives = 1 - TruePositivesPct
     */
    private ConfidenceInterval falseNegativesPct;
    
    /**
     * The fraction of target falses incorrectly classified as true,
     * FalsePositives / TotalNegatives = 1 - TrueNegativesPct
     */
    private ConfidenceInterval falsePositivesPct;
    
    /**
     * The fraction of target trues that were correctly classified as true,
     * TruePositives / TotalPositives = TruePositives / (TruePositives + FalseNegatives)
     */
    private ConfidenceInterval truePositivesPct;
    
    /**
     * The fraction of negative targets correctly classified as false,
     * TrueNegatives / TotalNegatives = TrueNegatives / (TrueNegatives + FalsePositives)
     */
    private ConfidenceInterval trueNegativesPct;
    
    
    /**
     * Creates a new instance of ConfusionMatrixConfidenceInterval
     * @param confidence
     * Confidence that the statistic is within the bound, or 1-alpha, on the
     * interval [0,1], where confidence=0 means definitely not within the bound
     * and confidence=1 means definitely within the bound.
     * @param falsePositivesPct
     * The fraction of target falses incorrectly classified as true,
     * FalsePositives / TotalNegatives = 1 - TrueNegativesPct
     * @param falseNegativesPct
     * The fraction of target falses incorrectly classified as false,
     * FalseNegatives / TotalPositives = 1 - TruePositivesPct
     * @param truePositivesPct
     * The fraction of target trues that were correctly classified as true,
     * TruePositives / TotalPositives = TruePositives / (TruePositives + FalseNegatives)
     * @param trueNegativesPct
     * The fraction of negative targets correctly classified as false,
     * TrueNegatives / TotalNegatives = TrueNegatives / (TrueNegatives + FalsePositives)
     */
    public ConfusionMatrixConfidenceInterval(
        final double confidence,
        final ConfidenceInterval falsePositivesPct,
        final ConfidenceInterval falseNegativesPct,
        final ConfidenceInterval truePositivesPct,
        final ConfidenceInterval trueNegativesPct )
    {
        this.setConfidence( confidence );
        this.setFalseNegativesPct( falseNegativesPct );
        this.setFalsePositivesPct( falsePositivesPct );
        this.setTruePositivesPct( truePositivesPct );
        this.setTrueNegativesPct( trueNegativesPct );
    }
    
    /**
     * Getter for falsePositivesPct
     * @return
     * The fraction of target falses incorrectly classified as true,
     * FalsePositives / TotalNegatives = 1 - TrueNegativesPct
     */
    public ConfidenceInterval getFalsePositivesPct()
    {
        return this.falsePositivesPct;
    }
    
    /**
     * Setter for falsePositivesPct
     * @param falsePositivesPct
     * The fraction of target falses incorrectly classified as true,
     * FalsePositives / TotalNegatives = 1 - TrueNegativesPct
     */
    protected void setFalsePositivesPct(
        ConfidenceInterval falsePositivesPct)
    {
        this.falsePositivesPct = falsePositivesPct;
    }
    
    /**
     * Getter for falseNegativesPct
     * @return
     * The fraction of target falses incorrectly classified as false,
     * FalseNegatives / TotalPositives = 1 - TruePositivesPct
     */
    public ConfidenceInterval getFalseNegativesPct()
    {
        return this.falseNegativesPct;
    }
    
    /**
     * Setter for falseNegativesPct
     * @param falseNegativesPct
     * The fraction of target falses incorrectly classified as false,
     * FalseNegatives / TotalPositives = 1 - TruePositivesPct
     */
    protected void setFalseNegativesPct(
        ConfidenceInterval falseNegativesPct)
    {
        this.falseNegativesPct = falseNegativesPct;
    }
    
    /**
     * Getter for truePositivesPct
     * @return
     * The fraction of target trues that were correctly classified as true,
     * TruePositives / TotalPositives = TruePositives / (TruePositives + FalseNegatives)
     */
    public ConfidenceInterval getTruePositivesPct()
    {
        return this.truePositivesPct;
    }
    
    /**
     * Setter for truePositivesPct
     * @param truePositivesPct
     * The fraction of target trues that were correctly classified as true,
     * TruePositives / TotalPositives = TruePositives / (TruePositives + FalseNegatives)
     */
    protected void setTruePositivesPct(
        ConfidenceInterval truePositivesPct)
    {
        this.truePositivesPct = truePositivesPct;
    }
    
    /**
     * Getter for trueNegativesPct
     * @return
     * The fraction of negative targets correctly classified as false,
     * TrueNegatives / TotalNegatives = TrueNegatives / (TrueNegatives + FalsePositives)
     */
    public ConfidenceInterval getTrueNegativesPct()
    {
        return this.trueNegativesPct;
    }
    
    /**
     * Setter for trueNegativesPct
     * @param trueNegativesPct
     * The fraction of negative targets correctly classified as false,
     * TrueNegatives / TotalNegatives = TrueNegatives / (TrueNegatives + FalsePositives)
     */
    protected void setTrueNegativesPct(
        ConfidenceInterval trueNegativesPct)
    {
        this.trueNegativesPct = trueNegativesPct;
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
        ConfusionMatrixConfidenceInterval.checkConfidence(confidence);
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
        return "True Negatives: " + this.getTrueNegativesPct()  + "\n"
            + "False Positives: " + this.getFalsePositivesPct() + "\n"
            + "False Negatives: " + this.getFalseNegativesPct() + "\n"
            + "True Positives: "  + this.getTruePositivesPct();
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
    static public ConfusionMatrixConfidenceInterval compute(
        Collection<? extends ConfusionMatrix> data,
        double confidence )
    {
        
        ArrayList<Double> fpp = new ArrayList<Double>( data.size() );
        ArrayList<Double> fnp = new ArrayList<Double>( data.size() );
        ArrayList<Double> tpp = new ArrayList<Double>( data.size() );
        ArrayList<Double> tnp = new ArrayList<Double>( data.size() );
        
        for( ConfusionMatrix cm : data )
        {
            double positives = cm.getTruePositives() + cm.getFalseNegatives();
            double negatives = cm.getTrueNegatives() + cm.getFalsePositives();
            
            // If there were no "positives" in the ConfusionMatrix, then
            // we should not use the rates into the confidence intervals
            if( positives != 0.0 )
            {
                tpp.add( cm.getTruePositivesPct() );
                fnp.add( cm.getFalseNegativesPct() );
            }
            
            // If there were no "positives" in the ConfusionMatrix, then
            // we should not use the rates into the confidence intervals            
            if( negatives != 0.0 )
            {
                tnp.add( cm.getTrueNegativesPct() );
                fpp.add( cm.getFalsePositivesPct() );
            }            
            
        }
        
        StudentTConfidence ttest = new StudentTConfidence();
        ConfidenceInterval fpci = ttest.computeConfidenceInterval( fpp, confidence );
        ConfidenceInterval fnci = ttest.computeConfidenceInterval( fnp, confidence );
        ConfidenceInterval tpci = ttest.computeConfidenceInterval( tpp, confidence );
        ConfidenceInterval tnci = ttest.computeConfidenceInterval( tnp, confidence );
        return new ConfusionMatrixConfidenceInterval(
            confidence, fpci, fnci, tpci, tnci );
        
    }
    
    /**
     * An implementation of the {@code Summarizer} interface for creating a
     * {@code ConfusionMatrixInterval}
     */
    public static class Summary
        extends AbstractCloneableSerializable
        implements Summarizer<ConfusionMatrix, ConfusionMatrixConfidenceInterval>
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
        
        public ConfusionMatrixConfidenceInterval summarize(
            final Collection<? extends ConfusionMatrix> data)
        {
            return ConfusionMatrixConfidenceInterval.compute(data, confidence);
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
            ConfusionMatrixConfidenceInterval.checkConfidence(confidence);
            this.confidence = confidence;
        }
    }
}
