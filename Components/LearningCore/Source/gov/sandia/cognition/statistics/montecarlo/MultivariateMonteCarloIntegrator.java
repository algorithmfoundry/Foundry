/*
 * File:                MultivariateMonteCarloIntegrator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 12, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Monte Carlo integrator for multivariate (vector) outputs.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class MultivariateMonteCarloIntegrator 
    extends AbstractCloneableSerializable
    implements MonteCarloIntegrator<Vector>
{

    /**
     * Default variance to add to the Gaussian, {@value}.
     */
    public static final double DEFAULT_VARIANCE = 0.0;

    /**
     * Default instance because this class has no state.
     */
    public static final MultivariateMonteCarloIntegrator INSTANCE =
        new MultivariateMonteCarloIntegrator();

    /** 
     * Creates a new instance of MultivariateMonteCarloIntegrator 
     */
    public MultivariateMonteCarloIntegrator()
    {
    }

    public <SampleType> MultivariateGaussian.PDF integrate(
        Collection<? extends SampleType> samples,
        Evaluator<? super SampleType, ? extends Vector> expectationFunction)
    {
        ArrayList<Vector> outputs = new ArrayList<Vector>( samples.size() );
        for( SampleType sample : samples )
        {
            outputs.add( expectationFunction.evaluate(sample).convertToVector() );
        }

        return MultivariateGaussian.MaximumLikelihoodEstimator.learn(
            outputs, DEFAULT_VARIANCE );
    }

    public <SampleType> MultivariateGaussian.PDF integrate(
        List<? extends WeightedValue<? extends SampleType>> samples,
        Evaluator<? super SampleType, ? extends Vector> expectationFunction)
    {
        ArrayList<DefaultWeightedValue<Vector>> outputs =
            new ArrayList<DefaultWeightedValue<Vector>>( samples.size() );
        for( WeightedValue<? extends SampleType> sample : samples )
        {
            Vector output =
                expectationFunction.evaluate(sample.getValue()).convertToVector();
            outputs.add( new DefaultWeightedValue<Vector>(output, sample.getWeight()) );
        }
        return MultivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(
            outputs, DEFAULT_VARIANCE);
    }

    public MultivariateGaussian.PDF getMean(
        Collection<? extends Vector> samples)
    {
        MultivariateGaussian.PDF pdf =
            MultivariateGaussian.MaximumLikelihoodEstimator.learn(
                samples,DEFAULT_VARIANCE);
        Matrix C = pdf.getCovariance().scale( 1.0/samples.size() );
        pdf.setCovariance(C);
        return pdf;
    }

    public MultivariateGaussian.PDF getMean(
        List<? extends WeightedValue<? extends Vector>> samples)
    {
        MultivariateGaussian.PDF pdf =
            MultivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(
                samples,DEFAULT_VARIANCE);
        double weightSum = 0.0;
        double sumSquared = 0.0;
        for( WeightedValue sample : samples )
        {
            final double w = sample.getWeight();
            weightSum += w;
            sumSquared += w*w;
        }
        double ws2 = weightSum*weightSum;
        Matrix C = pdf.getCovariance().scale( sumSquared / ws2 );
        pdf.setCovariance(C);
        return pdf;
    }

}
