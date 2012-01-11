/*
 * File:                FisherLinearDiscriminantBinaryCategorizer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.LinearDiscriminant;
import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.method.ReceiverOperatingCharacteristic;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * A Fisher Linear Discriminant classifier, which creates an optimal linear
 * separating plane between two Gaussian classes of different covariances.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Linear discriminant analysis",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Linear_discriminant_analysis#Fisher.27s_linear_discriminant"
)
public class FisherLinearDiscriminantBinaryCategorizer
    extends ScalarFunctionToBinaryCategorizerAdapter<Vector>
{

    /**
     * Default constructor
     */
    public FisherLinearDiscriminantBinaryCategorizer()
    {
        this( (Vector) null, DEFAULT_THRESHOLD );
    }

    /** 
     * Creates a new of {@code FisherLinearDiscriminantBinaryCategorizer}.
     * 
     * @param   weightVector The weight vector.
     * @param   threshold The threshold.
     */
    public FisherLinearDiscriminantBinaryCategorizer(
        final Vector weightVector,
        final double threshold)
    {
        this(new LinearDiscriminant(weightVector),threshold);
    }

    /**
     * Creates a new of {@code FisherLinearDiscriminantBinaryCategorizer}.
     * 
     * @param   discriminant The linear discriminant to use.
     * @param   threshold The threshold.
     */
    public FisherLinearDiscriminantBinaryCategorizer(
        final LinearDiscriminant discriminant,
        final double threshold )
    {
        super(discriminant, threshold);
    }

    @Override
    public FisherLinearDiscriminantBinaryCategorizer clone()
    {
        return (FisherLinearDiscriminantBinaryCategorizer) super.clone();
    }

    /**
     * This class implements a closed form solver for the Fisher linear
     * discriminant binary categorizer.
     */
    public static class ClosedFormSolver
        extends AbstractCloneableSerializable
        implements SupervisedBatchLearner<Vector, Boolean, FisherLinearDiscriminantBinaryCategorizer>
    {
        /** The default covariance. */
        private double defaultCovariance;

        /**
         * Default constructor.
         */
        public ClosedFormSolver()
        {
            this( MultivariateGaussian.MaximumLikelihoodEstimator.DEFAULT_COVARIANCE );
        }

        /**
         * Creates a new {@code ClosedFormSolver}.
         * 
         * @param   defaultCovariance The default covariance.
         */
        public ClosedFormSolver(
            double defaultCovariance)
        {
            this.defaultCovariance = defaultCovariance;
        }

        public FisherLinearDiscriminantBinaryCategorizer learn(
            Collection<? extends InputOutputPair<? extends Vector, Boolean>> data)
        {
            return ClosedFormSolver.learn(data, this.defaultCovariance);
        }
        
        /**
         * Closed-form learning algorithm for a Fisher Linear Discriminant.
         * 
         * @param   data The data to learn the discriminant categorizer from.
         * @param   defaultCovariance The default covariance.
         * @return  A discriminant categorizer learned from the data.
         */
        public static FisherLinearDiscriminantBinaryCategorizer learn(
            Collection<? extends InputOutputPair<? extends Vector, Boolean>> data,
            final double defaultCovariance)
        {
            // Split the data into two classes based on their
            DefaultPair<LinkedList<Vector>, LinkedList<Vector>> pair =
                DatasetUtil.splitDatasets(data);
            LinkedList<? extends Vector> d1 = pair.getFirst();
            LinkedList<? extends Vector> d0 = pair.getSecond();

            // This is faster than estimating a MultivariateGaussian as
            // the Gaussian will automatically invert the covariance matrix
            // and cache that
            Pair<Vector,Matrix> r1 =
                MultivariateStatisticsUtil.computeMeanAndCovariance(d1);
            Vector m1 = r1.getFirst();
            Matrix c1 = r1.getSecond();

            Pair<Vector,Matrix> r0 =
                MultivariateStatisticsUtil.computeMeanAndCovariance(d0);
            Vector m0 = r0.getFirst();
            Matrix c0 = r0.getSecond();

            Matrix cinverse;
            if (defaultCovariance != 0.0)
            {
                int M = m0.getDimensionality();
                Matrix ci = MatrixFactory.getDefault().createIdentity(M, M).scale(defaultCovariance);
                cinverse = c0.plus(c1.plus(ci)).inverse();
            }
            else
            {
                cinverse = c0.plus(c1).inverse();
            }

            Vector weightVector = cinverse.times(m1.minus(m0));

            // Technically, the threshold is supposed to be zero, but we might
            // try to do better
            LinearDiscriminant discriminant =
                new LinearDiscriminant(weightVector);
            ArrayList<InputOutputPair<Double, Boolean>> doubleData =
                new ArrayList<InputOutputPair<Double, Boolean>>(data.size());
            for (InputOutputPair<? extends Vector, Boolean> sample : data)
            {
                Double value = discriminant.evaluate(sample.getInput());
                doubleData.add(new DefaultInputOutputPair<Double, Boolean>(
                    value, sample.getOutput()));
            }


            ReceiverOperatingCharacteristic roc =
                ReceiverOperatingCharacteristic.create(doubleData);

            ReceiverOperatingCharacteristic.Statistic stats =
                roc.computeStatistics();
            return new FisherLinearDiscriminantBinaryCategorizer(
                discriminant, stats.getOptimalThreshold().getClassifier().getThreshold() );
        }

    }

}
