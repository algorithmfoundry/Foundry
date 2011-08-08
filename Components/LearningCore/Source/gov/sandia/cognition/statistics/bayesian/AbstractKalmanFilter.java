/*
 * File:                AbstractKalmanFilter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 13, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.learning.algorithm.AbstractBatchAndOnlineLearner;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Contains fields useful to both Kalman filters and extended Kalman filters.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractKalmanFilter
    extends AbstractBatchAndOnlineLearner<Vector,MultivariateGaussian>
    implements RecursiveBayesianEstimator<Vector,Vector,MultivariateGaussian>
{

    /**
     * Current input to the model.
     */
    protected Vector currentInput;

    /**
     * Covariance associated with the system's model.
     */
    protected Matrix modelCovariance;

    /**
     * Covariance associated with the measurements.
     */
    protected Matrix measurementCovariance;

    /** 
     * Creates a new instance of AbstractKalmanFilter 
     * @param currentInput
     * Current input to the model.
     * @param modelCovariance
     * Covariance associated with the system's model.
     * @param measurementCovariance
     * Covariance associated with the measurements.
     */
    public AbstractKalmanFilter(
        Vector currentInput,
        Matrix modelCovariance,
        Matrix measurementCovariance)
    {
        this.currentInput = currentInput;
        this.modelCovariance = modelCovariance;
        this.measurementCovariance = measurementCovariance;
    }

    @Override
    public AbstractKalmanFilter clone()
    {
        AbstractKalmanFilter clone = (AbstractKalmanFilter) super.clone();
        clone.setCurrentInput( ObjectUtil.cloneSafe( this.getCurrentInput() ) );
        clone.setMeasurementCovariance(
            ObjectUtil.cloneSafe( this.getMeasurementCovariance() ) );
        clone.setModelCovariance(
            ObjectUtil.cloneSafe( this.getModelCovariance() ) );
        return clone;
    }

    /**
     * Creates a prediction of the system's next state given the current
     * belief state
     * @param belief
     * Current belief state
     */
    public abstract void predict(
        MultivariateGaussian belief );

    /**
     * Computes the prediction covariance from the Jacobian and believe
     * covariance
     * @param A
     * System Jacobian, which is estimated in the case of the EKF.
     * @param beliefCovariance
     * Covariance of the current state belief.
     * @return
     * Covariance of the prediction.
     */
    public Matrix computePredictionCovariance(
        Matrix A,
        Matrix beliefCovariance )
    {
        // Calculate the covariance, which will increase due to the
        // inherent uncertainty of the model.
        Matrix P = beliefCovariance;
        P = A.times( P ).times( A.transpose() );
        P.plusEquals( this.modelCovariance );
        return P;
    }

    /**
     * Integrates a measurement into the system, refining the current
     * belief of the state of the system
     * @param belief
     * Current belief of the state of the system
     * @param observation
     * Measurement to integrate.
     */
    public abstract void measure(
        MultivariateGaussian belief,
        Vector observation );


    /**
     * Updates the measurement belief by computing the Kalman gain and
     * incorporating the innovation into the estimate
     * @param belief
     * Current belief of the state.
     * @param innovation
     * Innovation, which is the observation minus the predicted observation
     * @param C
     * Output-selector matrix, the partial derivative of the output with
     * respect to the current estimated state.
     */
    public void computeMeasurementBelief(
        MultivariateGaussian belief,
        Vector innovation,
        Matrix C )
    {

        Matrix Ct = C.transpose();

        // Figure out what the model says the observation should be
        Matrix P = belief.getCovariance();
        Vector xpred = belief.getMean();

        // Update step... compute the difference between the observation
        // and what the model says.
        // Then compute the Kalman gain, which essentially indicates
        // how much to believe the observation, and how much to believe model
        Matrix PCt = P.times(Ct);
        Matrix innovationCovariance = C.times(PCt);
        innovationCovariance.plusEquals( this.measurementCovariance );
        Matrix kalmanGain = PCt.times( innovationCovariance.inverse() );

        // These are the posterior distributions.
        xpred.plusEquals( kalmanGain.times( innovation ) );
        P.minusEquals( kalmanGain.times(C).times(P) );

        // Update the belief with the posterior distribution
        belief.setMean(xpred);
        belief.setCovariance(P);

    }

    public void update(
        MultivariateGaussian belief,
        Vector observation )
    {
        this.predict(belief);
        if( observation != null )
        {
            this.measure(belief, observation);
        }
    }

    /**
     * Getter for modelCovariance
     * @return
     * Covariance associated with the system's model.
     */
    public Matrix getModelCovariance()
    {
        return this.modelCovariance;
    }

    /**
     * Setter for modelCovariance
     * @param modelCovariance
     * Covariance associated with the system's model.
     */
    public void setModelCovariance(
        Matrix modelCovariance)
    {
        this.modelCovariance = modelCovariance;
    }

    /**
     * Getter for currentInput
     * @return
     * Current input to the model.
     */
    public Vector getCurrentInput()
    {
        return this.currentInput;
    }

    /**
     * Setter for currentInput
     * @param currentInput
     * Current input to the model.
     */
    public void setCurrentInput(
        Vector currentInput)
    {
        this.currentInput = currentInput;
    }

    /**
     * Getter for measurementCovariance
     * @return
     * Covariance associated with the measurements.
     */
    public Matrix getMeasurementCovariance()
    {
        return this.measurementCovariance;
    }

    /**
     * Setter for measurementCovariance
     * @param measurementCovariance
     * Covariance associated with the measurements.
     */
    public void setMeasurementCovariance(
        Matrix measurementCovariance)
    {
        this.measurementCovariance = measurementCovariance;
    }

}
