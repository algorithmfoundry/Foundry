/*
 * File:                AbstractMultipleHypothesisComparison.java
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

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Collection;

/**
 * Partial implementation of MultipleHypothesisComparison
 * @param <TreatmentData>
 * Data associated with each treatment, such as Double or Collection of Double
 * @param <StatisticType>
 * Type of statistic returned by the test
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
public abstract class AbstractMultipleHypothesisComparison<TreatmentData, StatisticType extends MultipleHypothesisComparison.Statistic>
    extends AbstractCloneableSerializable
    implements MultipleHypothesisComparison<TreatmentData>
{

    /**
     * Default constructor
     */
    public AbstractMultipleHypothesisComparison()
    {
        super();
    }

    @Override
    public StatisticType evaluateNullHypotheses(
        Collection<? extends TreatmentData> data)
    {
        return this.evaluateNullHypotheses(data, DEFAULT_UNCOMPENSATED_ALPHA);
    }

    @Override
    public abstract StatisticType evaluateNullHypotheses(
        Collection<? extends TreatmentData> data,
        double uncompensatedAlpha);

    /**
     * Partial implementation of MultipleHypothesisComparison.Statistic
     */
    public static abstract class Statistic
        extends AbstractCloneableSerializable
        implements MultipleHypothesisComparison.Statistic
    {

        /**
         * Number of treatments being compared
         */
        protected int treatmentCount;

        /**
         * Uncompensated alpha (p-value threshold) for the multiple comparison
         * test
         */
        protected double uncompensatedAlpha;

        /**
         * Test statistic associated with the (i,j) treatment comparison
         */
        protected Matrix testStatistics;

        /**
         * Null Hypothesis probability associated with the (i,j)
         * treatment comparison
         */
        protected Matrix nullHypothesisProbabilities;

        /**
         * Default constructor
         */
        public Statistic()
        {
        }

        @Override
        public AbstractMultipleHypothesisComparison.Statistic clone()
        {
            Statistic clone = (AbstractMultipleHypothesisComparison.Statistic) super.clone();
            clone.nullHypothesisProbabilities = ObjectUtil.cloneSafe( this.nullHypothesisProbabilities );
            clone.testStatistics = ObjectUtil.cloneSafe( this.testStatistics );
            return clone;
        }



        @Override
        public int getTreatmentCount()
        {
            return this.treatmentCount;
        }

        @Override
        public double getUncompensatedAlpha()
        {
            return this.uncompensatedAlpha;
        }

        @Override
        public double getTestStatistic(
            int i,
            int j)
        {
            return this.testStatistics.getElement(i, j);
        }

        @Override
        public double getNullHypothesisProbability(
            int i,
            int j)
        {
            return this.nullHypothesisProbabilities.getElement(i, j);
        }

        @Override
        public String toString()
        {
            return ObjectUtil.toString(this);
        }

    }

}
