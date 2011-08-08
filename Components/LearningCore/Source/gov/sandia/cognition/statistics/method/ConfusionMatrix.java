/*
 * File:                ConfusionMatrix.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.learning.performance.AbstractSupervisedPerformanceEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * Returns a Matrix where the entries are,
 * [ True_Negatives     False_Positives ]
 * [ False_Negatives    True_Positives ],
 * In other words, the rows represent target classifications, and the columns
 * represent estimated classifications
 *
 * @author Kevin R. Dixon
 * @since  2.0
 */
public class ConfusionMatrix
    extends AbstractCloneableSerializable
{

    /**
     * False Positive: Target==false and Estimate==true
     */
    private double falsePositives;

    /**
     * False Negative: Target==true and Estimate==false
     */
    private double falseNegatives;

    /**
     * True Positive: Target==true and Estimate==true
     */
    private double truePositives;

    /**
     * True Negative: Target==false and Estimate==false
     */
    private double trueNegatives;

    /**
     * Creates a new instance of ConfusionMatrix
     * @param falsePositives
     * False Positive: Target==false and Estimate==true
     * @param falseNegatives
     * False Negative: Target==true and Estimate==false
     * @param truePositives
     * True Positive: Target==true and Estimate==true
     * @param trueNegatives
     * True Negative: Target==false and Estimate==false
     */
    public ConfusionMatrix(
        final double falsePositives,
        final double falseNegatives,
        final double truePositives,
        final double trueNegatives )
    {
        super();

        this.setFalsePositives( falsePositives );
        this.setFalseNegatives( falseNegatives );
        this.setTruePositives( truePositives );
        this.setTrueNegatives( trueNegatives );
    }

    @Override
    public ConfusionMatrix clone()
    {
        return (ConfusionMatrix) super.clone();
    }

    /**
     * Computes a ConfusionMatrix from classification pairs, where the first
     * element in the pair is the target and the second is the estimate,
     * uses weighting on the samples if available
     * @param input Collection of (target,estimate) pairs
     * @return ConfusionMatrix describing the classifier
     */
    static public ConfusionMatrix compute(
        Collection<? extends TargetEstimatePair<? extends Boolean, ? extends Boolean>> input )
    {
        return ConfusionMatrix.compute( input, true );
    }

    /**
     * Computes a ConfusionMatrix from classification pairs, where the first
     * element in the pair is the target and the second is the estimate
     * @param input Collection of (target,estimate) pairs
     * @return ConfusionMatrix describing the classifier
     * @param weightIfAvailable true to weight the samples if available, false
     * to disregard weights
     */
    static public ConfusionMatrix compute(
        Collection<? extends TargetEstimatePair<? extends Boolean, ? extends Boolean>> input,
        boolean weightIfAvailable )
    {
        double falsePositives = 0;
        double falseNegatives = 0;
        double truePositives = 0;
        double trueNegatives = 0;
        for (TargetEstimatePair<? extends Boolean, ? extends Boolean> pair : input)
        {
            double increment;
            if (weightIfAvailable)
            {
                increment = DatasetUtil.getWeight(pair);
            }
            else
            {
                increment = 1.0;
            }

            boolean target = pair.getTarget();
            boolean estimate = pair.getEstimate();
            if (target == true)
            {
                if (estimate == true)
                {
                    truePositives += increment;
                }
                else
                {
                    falseNegatives += increment;
                }
            }
            else
            {
                if (estimate == true)
                {
                    falsePositives += increment;
                }
                else
                {
                    trueNegatives += increment;
                }
            }

        }

        return new ConfusionMatrix(
            falsePositives, falseNegatives, truePositives, trueNegatives );
    }

    /**
     * Gets the number of true negatives in the ConfusionMatrix
     * @return 
     * True negatives, that is the target is false and the estimate is false
     */
    public double getTrueNegatives()
    {
        return this.trueNegatives;
    }

    /**
     * Gets the fraction of negative targets correctly classified as,
     * that is
     * TrueNegatives / TotalNegatives = TrueNegatives / (TrueNegatives + FalsePositives)
     * @return True negative fraction [0,1]
     */
    public double getTrueNegativesPct()
    {
        double denom = this.getTrueNegatives() + this.getFalsePositives();
        double retval;
        if (denom <= 0.0)
        {
            retval = 1.0;
        }
        else
        {
            retval = this.getTrueNegatives() / denom;
        }
        
        return retval;
        
    }

    /**
     * Gets the number of true positives in the ConfusionMatrix
     * @return 
     * True positives, that is, the target is true and the estimate is true
     */
    public double getTruePositives()
    {
        return this.truePositives;
    }

    /**
     * Gets the fraction of target trues that were correctly classified as true,
     * that is
     * TruePositives / TotalPositives = TruePositives / (TruePositives + FalseNegatives)
     * @return 
     * Fraction of true positives, [0,1]
     */
    public double getTruePositivesPct()
    {
        double denom = this.getTruePositives() + this.getFalseNegatives();
        double retval;
        if (denom <= 0.0)
        {
            retval = 1.0;
        }
        else
        {
            retval = this.getTruePositives() / denom;
        }
        
        return retval;
        
    }

    /**
     * Gets the number of false positives in the ConfusionMatrix
     * @return 
     * False positives, that is, the target is false and the estimate is true
     */
    public double getFalsePositives()
    {
        return this.falsePositives;
    }

    /**
     * Gets the fraction of false positives, that is, the fraction of 
     * target falses incorrectly classified as true, that is,
     * FalsePositives / TotalNegatives = FalsePositives / (TrueNegatives + FalsePositives)
     * 
     * @return 
     * Fraction of false positives, [0,1], 1-TrueNegativePct
     */
    public double getFalsePositivesPct()
    {
        return 1.0 - this.getTrueNegativesPct();
    }

    /**
     * Gets the number of false negatives in the ConfusionMatrix.
     *
     * @return 
     * False negatives, that is, the number of true targets incorrectly.
     * classified as false
     */
    public double getFalseNegatives()
    {
        return this.falseNegatives;
    }

    /**
     * Fraction of true targets incorrectly classified as false.
     *
     * @return 
     * Fraction of false negatives, [0,1], 1-TruePositivePct
     */
    public double getFalseNegativesPct()
    {
        return 1.0 - this.getTruePositivesPct();
    }

    @Override
    public String toString()
    {
        return "True Negatives: " + this.getTrueNegatives() + ", " + "False Positives: " + this.getFalsePositives() + ", " + "False Negatives: " + this.getFalseNegatives() + ", " + "True Positives: " + this.getTruePositives();
    }

    /**
     * Sets the number of false positives in the ConfusionMatrix.
     *
     * @param  falsePositives
     *     The number of false positives (target is false and the estimate 
     *     is true).
     */
    public void setFalsePositives(
        final double falsePositives )
    {
        this.falsePositives = falsePositives;
    }

    /**
     * Sets the number of false negatives in the ConfusionMatrix.
     *
     * @param  falseNegatives
     *     The number of false negatives (target is true and the estimate 
     *     is false).
     */
    public void setFalseNegatives(
        final double falseNegatives )
    {
        this.falseNegatives = falseNegatives;
    }

    /**
     * Sets the number of true positives in the ConfusionMatrix.
     *
     * @param  truePositives
     *     The number of true positives (target is true and the estimate 
     *     is true).
     */
    public void setTruePositives(
        final double truePositives )
    {
        this.truePositives = truePositives;
    }

    /**
     * Sets the number of true negatives in the ConfusionMatrix.
     *
     * @param  trueNegatives
     *     The number of true negatives (target is false and the estimate 
     *     is false).
     */
    public void setTrueNegatives(
        final double trueNegatives )
    {
        this.trueNegatives = trueNegatives;
    }

    /**
     * An implementation of the {@code SupervisedPerformanceEvaluator} interface
     * for creating a {@code ConfusionMatrix}.
     * @param <InputType> Input class to compute the error of
     */
    public static class PerformanceEvaluator<InputType>
        extends AbstractSupervisedPerformanceEvaluator<InputType, Boolean, Boolean, ConfusionMatrix>
    {

        /**
         * Creates a new PerformanceEvaluator.
         */
        public PerformanceEvaluator()
        {
            super();
        }

        public ConfusionMatrix evaluatePerformance(
            final Collection<? extends TargetEstimatePair<Boolean, Boolean>> data )
        {
            return ConfusionMatrix.compute( data );
        }

    }

}
