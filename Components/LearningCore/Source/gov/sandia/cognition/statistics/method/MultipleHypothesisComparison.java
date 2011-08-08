/*
 * File:                MultipleHypothesisComparison.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 24, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.Collection;

/**
 * Describes the functionality of an algorithm for accepting or rejecting
 * multiple null hypothesis at the same time.  These are typically run as a
 * post-hoc test after an ANOVA or Friedman's test.  The multiple comparison
 * tests indicate which treatments are significantly different from each other
 * once an ANOVA or Friedman's test has indicated that there are significant
 * differences.
 * @param <TreatmentData>
 * Data associated with each treatment, such as Double or Collection of Double
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Juliet Popper Shaffer",
            title="Multiple Hypothesis Testing",
            type=PublicationType.Journal,
            year=1995,
            publication="Annual Review of Psychology",
            url="http://www.annualreviews.org/doi/pdf/10.1146/annurev.ps.46.020195.003021"
        )
        ,
        @PublicationReference(
            author="Janez Demsar",
            title="Statistical Comparisons of Classifiers over Multiple Data Sets",
            type=PublicationType.Journal,
            publication="Journal of Machine Learning Research",
            year=2006,
            url="http://www.jmlr.org/papers/volume7/demsar06a/demsar06a.pdf"
        )
        ,
        @PublicationReference(
            author={
                "Salvador Garcia",
                "Francisco Herrera"
            },
            title="An Extension on \"Statistical Comparisons of Classiﬁers over Multiple Data Sets\" for all Pairwise Comparisons",
            type=PublicationType.Journal,
            publication="Journal of Machine Learning Research",
            year=2008,
            url="http://150.214.190.154/publications/ficheros/2008-Garcia-JMLR.pdf"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Multiple comparisons",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Multiple_comparisons"
        )
    }
)
public interface MultipleHypothesisComparison<TreatmentData>
    extends CloneableSerializable
{

    /**
     * Default uncompensatedAlpha, {@value}.
     */
    public static final double DEFAULT_UNCOMPENSATED_ALPHA = 0.05;

    /**
     * Evaluates the null hypotheses associated with the given collection
     * of data.
     * @param data
     * Data from each treatment to consider
     * @return
     * Statistic that summarizes the multiple comparison test
     */
    public MultipleHypothesisComparison.Statistic evaluateNullHypotheses(
        Collection<? extends TreatmentData> data );

    /**
     * Evaluates the null hypotheses associated with the given collection
     * of data.
     * @param data
     * Data from each treatment to consider
     * @param uncompensatedAlpha
     * Uncompensated alpha (p-value threshold) for the multiple comparison
     * test, must be [0,1]
     * @return
     * Statistic that summarizes the multiple comparison test
     */
    public MultipleHypothesisComparison.Statistic evaluateNullHypotheses(
        Collection<? extends TreatmentData> data,
        double uncompensatedAlpha );

    /**
     * Statistic associated with the multiple hypothesis comparison
     */
    public interface Statistic
        extends CloneableSerializable
    {

        /**
         * Gets the number of treatments being compared
         * @return
         * Number of treatments being compared
         */
        public int getTreatmentCount();

        /**
         * Gets the uncompensated alpha (p-value threshold) for the multiple
         * comparison test
         * @return
         * Uncompensated alpha (p-value threshold) for the multiple comparison
         * test
         */
        public double getUncompensatedAlpha();

        /**
         * Gets the test statistic associated with the (i,j) treatment
         * comparison
         * @param i
         * First treatment index
         * @param j
         * Second treatment index
         * @return
         * Test statistic associated with the (i,j) treatment comparison
         */
        public double getTestStatistic(
            int i,
            int j );

        /**
         * Gets the Null Hypothesis probability associated with the (i,j)
         * treatment comparison
         * @param i
         * First treatment index
         * @param j
         * Second treatment index
         * @return
         * Null Hypothesis probability associated with the (i,j)
         * treatment comparison
         */
        public double getNullHypothesisProbability(
            int i,
            int j );

        /**
         * Determines if the (i,j) null hypothesis should be accepted (true) or
         * rejected (false) .  Rejecting a null hypothesis typically means that
         * there is a significant difference between the (i,j) treatment.
         * @param i
         * First treatment index
         * @param j
         * Second treatment index
         * @return
         * True if we accept the null hypothesis, false if we reject the
         * null hypothesis
         */
        public boolean acceptNullHypothesis(
            int i,
            int j );

    }
    
}
