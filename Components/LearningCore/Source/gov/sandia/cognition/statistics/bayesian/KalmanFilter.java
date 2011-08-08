/*
 * File:                KalmanFilter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Oct 21, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.signals.LinearDynamicalSystem;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * A Kalman filter estimates the state of a dynamical system corrupted with
 * white Gaussian noise with observations that are corrupted with white
 * Gaussian noise.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="Kalman filter",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/Kalman_filter"
        )
    }
)
public class KalmanFilter
    extends AbstractKalmanFilter
{    

    /**
     * Default autonomous dimension, {@value}.
     */
    public static final int DEFAULT_DIMENSION = 1;

    /**
     * Motion model of the underlying system.
     */
    protected LinearDynamicalSystem model;

    /**
     * Creates a new instance of KalmanFilter
     */
    public KalmanFilter()
    {
        this( DEFAULT_DIMENSION );
    }

    /**
     * Creates an autonomous, fully observable linear dynamical system
     * with the given dimensionality
     * @param dim
     * Dimensionality of the LDS
     */
    public KalmanFilter(
        int dim )
    {
        // Autonomous Dynamical System:
        // xn+1 = A*xn
        // yn+1 = xn+1
        // Also, we're using an identity for the model covariance and
        // the measurement covariance
        this( new LinearDynamicalSystem(
            MatrixFactory.getDefault().createIdentity(dim,dim),
            MatrixFactory.getDefault().createMatrix(dim,dim),
            MatrixFactory.getDefault().createIdentity(dim, dim) ),
            MatrixFactory.getDefault().createIdentity(dim, dim),
            MatrixFactory.getDefault().createIdentity(dim, dim) );
    }

    /**
     * Creates a new instance of LinearUpdater
     * @param model
     * Motion model of the underlying system.
     * @param modelCovariance
     * Covariance associated with the system's model.
     * @param measurementCovariance
     * Covariance associated with the measurements.
     */
    public KalmanFilter(
        LinearDynamicalSystem model,
        Matrix modelCovariance,
        Matrix measurementCovariance )
    {
        super( VectorFactory.getDefault().createVector(
            model.getInputDimensionality() ),
            modelCovariance,
            measurementCovariance );
        this.setModel(model);
    }

    @Override
    public KalmanFilter clone()
    {
        KalmanFilter clone = (KalmanFilter) super.clone();
        clone.setModel( ObjectUtil.cloneSafe( this.getModel() ) );
        return clone;
    }

    public MultivariateGaussian createInitialLearnedObject()
    {
        return new MultivariateGaussian(
            this.model.getState(), this.getModelCovariance() );
    }

    /**
     * Getter for model
     * @return
     * Motion model of the underlying system.
     */
    public LinearDynamicalSystem getModel()
    {
        return this.model;
    }

    /**
     * Setter for model
     * @param model
     * Motion model of the underlying system.
     */
    public void setModel(
        LinearDynamicalSystem model)
    {
        this.model = model;
    }

    public void predict(
        MultivariateGaussian belief)
    {
        // Load the belief into the model and then predict the next state
        this.getModel().evaluate( this.currentInput, belief.getMean() );
        Vector xpred = this.model.getState();

        // Calculate the covariance, which will increase due to the
        // inherent uncertainty of the model.
        Matrix P = this.computePredictionCovariance(
            this.model.getA(), belief.getCovariance() );

        // Load the updated belief
        belief.setMean( xpred );
        belief.setCovariance( P );
    }

    public void measure(
        MultivariateGaussian belief,
        Vector observation)
    {
        final Matrix C = this.model.getC();

        // Figure out what the model says the observation should be
        Vector xpred = belief.getMean();
        Vector ypred = C.times( xpred );

        // Update step... compute the difference between the observation
        // and what the model says.
        // Then compute the Kalman gain, which essentially indicates
        // how much to believe the observation, and how much to believe model
        Vector innovation = observation.minus( ypred );
        this.computeMeasurementBelief(belief, innovation, C);
    }

}
