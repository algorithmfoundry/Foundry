/*
 * File:                BinaryConfusionMatrix.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 11, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.performance.categorization;

/**
 * An interface for a binary confusion matrix. It is defined as a
 * {@code ConfusionMatrix} over Boolean objects. It treats true as the positive
 * category and false as the negative category.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public interface BinaryConfusionMatrix
    extends ConfusionMatrix<Boolean>
{

    /**
     * Gets the number of true positives. This is the (true, true) entry.
     *
     * @return
     *      The number of true positives.
     */
    public double getTruePositivesCount();
    
    /**
     * Gets the number of false positives. This is the (false, true) entry.
     *
     * @return
     *      The number of false positives.
     */
    public double getFalsePositivesCount();

    /**
     * Gets the number of true negatives. This is the (false, false) entry.
     *
     * @return
     *      The number of true negatives.
     */
    public double getTrueNegativesCount();

    /**
     * Gets the number of false negatives. This is the (true, false) entry.
     *
     * @return
     *      The number of false negatives.
     */
    public double getFalseNegativesCount();

    /**
     * Gets the fraction of true positives. This is the (true, true) fraction.
     *
     * @return
     *      The fraction of true positives.
     */
    public double getTruePositivesFraction();

    /**
     * Gets the fraction of false positives. This is the (false, true) fraction.
     *
     * @return
     *      The fraction of false positives.
     */
    public double getFalsePositivesFraction();

    /**
     * Gets the fraction of true negatives. This is the (false, false) fraction.
     *
     * @return
     *      The fraction of true negatives.
     */
    public double getTrueNegativesFraction();

    /**
     * Gets the fraction of false negatives. This is the (true, false) fraction.
     *
     * @return
     *      The fraction of false negatives.
     */
    public double getFalseNegativesFraction();

    /**
     * The sensitivity value for the confusion matrix. The sensitivity is
     * the number of true positives divided by the number of true positives plus
     * the number of false negatives: TP / (TP + FN). It is equivalent to
     * recall.
     *
     * @return
     *      The sensitivity, which is between 0.0 and 1.0.
     */
    public double getSensitivity();

    /**
     * The specificity value for the confusion matrix. The specificity is
     * the number of true negatives divided by the number of true negatives plus
     * the number of false positives: TN / (TN + FP).
     *
     * @return
     *      The specificity value, which is between 0.0 and 1.0.
     */
    public double getSpecificity();

    /**
     * The precision value for the confusion matrix. The precision is the
     * number of true positives divided by the number of true positives plus
     * the number of false positives: TP / (TP + FP).
     *
     * @return
     *      The precision value, which is between 0.0 and 1.0.
     */
    public double getPrecision();

    /**
     * The recall value for the confusion matrix. The recall is the number of
     * true positives divided by the number of true positives plus the number
     * of false negatives: TP / (TP + FN). It is equivalent to sensitivity.
     *
     * @return
     *      The recall value, which is be between 0.0 and 1.0.
     */
    public double getRecall();

    /**
     * The F-score of the confusion matrix, which is also known as the
     * F1-score or F-measure. It is calculated as:
     * 2 * (precision * recall) / (precision + recall)
     * It is equivalent to the F-score with beta = 1.
     *
     * @return
     *      The F-score, which is between 0.0 and 1.0.
     */
    public double getFScore();

    /**
     * The F-score for the confusion matrix with the given trade-off parameter
     * (beta). It is calculated as:
     * (1 + beta^2) * (precision * recall) / ((beta^2 * precision) + recall)
     *
     * @param   beta
     *      The beta value of the score. It is the importance assigned to
     *      precision as compared to recall.
     * @return
     *      The F-score for the matrix, which is greater than zero.
     */
    public double getFScore(
        final double beta);
    
}
