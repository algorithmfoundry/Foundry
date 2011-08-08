/*
 * File:                ExtendedKalmanFilter.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.evaluator.StatefulEvaluator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.NumericalDifferentiator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Implements the Extended Kalman Filter (EKF), which is an extension of the
 * Kalman filter that allows nonlinear motion and observation models.  The
 * belief states are still Gaussian and the nonlinear models are approximated
 * using a first-order numerical differentiation approximation.  The EKF is
 * not guaranteed to be optimal or converge to the true state.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Extended Kalman filter",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Extended_Kalman_filter"
)
public class ExtendedKalmanFilter 
    extends AbstractKalmanFilter
{

    /**
     * Model that determines how inputs and the previous state are updated.
     */
    protected StatefulEvaluator<Vector,Vector, Vector> motionModel;

    /**
     * Model that determines how the state is observed.
     */
    protected Evaluator<Vector, Vector> observationModel;

    /** 
     * Creates a new instance of ExtendedKalmanFilter 
     */
    public ExtendedKalmanFilter()
    {
        this( null, null, null, null, null );
    }

    /**
     * Creates a new instance of ExtendedKalmanFilter
     * @param motionModel
     * Model that determines how inputs and the previous state are updated.
     * @param observationModel
     * Model that determines how the state is observed.
     * @param currentInput
     * Current input to the model.
     * @param modelCovariance
     * Covariance associated with the system's model.
     * @param measurementCovariance
     * Covariance associated with the measurements.
     */
    public ExtendedKalmanFilter(
        StatefulEvaluator<Vector, Vector, Vector> motionModel,
        Evaluator<Vector, Vector> observationModel,
        Vector currentInput,
        Matrix modelCovariance,
        Matrix measurementCovariance )
    {
        super( currentInput, modelCovariance, measurementCovariance );
        this.setMotionModel(motionModel);
        this.setObservationModel(observationModel);
    }

    @Override
    public ExtendedKalmanFilter clone()
    {
        ExtendedKalmanFilter clone = (ExtendedKalmanFilter) super.clone();
        clone.setMotionModel( ObjectUtil.cloneSmart( this.getMotionModel() ) );
        clone.setObservationModel(
            ObjectUtil.cloneSmart( this.getObservationModel() ) );
        return clone;
    }

    /**
     * Getter for motionModel
     * @return 
     * Model that determines how inputs and the previous state are updated.
     */
    public StatefulEvaluator<Vector, Vector, Vector> getMotionModel()
    {
        return this.motionModel;
    }

    /**
     * Setter for motionModel
     * @param motionModel
     * Model that determines how inputs and the previous state are updated.
     */
    public void setMotionModel(
        StatefulEvaluator<Vector, Vector, Vector> motionModel)
    {
        this.motionModel = motionModel;
    }

    /**
     * Getter for observationModel
     * @return 
     * Model that determines how the state is observed.
     */
    public Evaluator<Vector, Vector> getObservationModel()
    {
        return this.observationModel;
    }

    /**
     * Setter for observationModel
     * @param observationModel 
     * Model that determines how the state is observed.
     */
    public void setObservationModel(
        Evaluator<Vector, Vector> observationModel)
    {
        this.observationModel = observationModel;
    }

    public MultivariateGaussian createInitialLearnedObject()
    {
        return new MultivariateGaussian(
            this.getMotionModel().getState().clone(),
            this.getModelCovariance() );
    }

    @Override
    public void predict(
        MultivariateGaussian belief)
    {
        // The only difference between the KF and EKF is that
        // in EKF we have to estimate the Jacobian (A), whereas the KF just
        // accesses the Jacobian directly.
        Vector x = belief.getMean();
        Vector xpred = this.getMotionModel().evaluate(
            this.currentInput, x );
        Matrix A = NumericalDifferentiator.MatrixJacobian.differentiate(
            x, new ModelJacobianEvaluator() );
        Matrix P = this.computePredictionCovariance(A, belief.getCovariance());

        // Load the updated belief
        belief.setMean( xpred );
        belief.setCovariance( P );
    }

    @Override
    public void measure(
        MultivariateGaussian belief,
        Vector observation)
    {

        // Figure out what the model says the observation should be
        Vector xpred = belief.getMean();
        Vector ypred = this.observationModel.evaluate( xpred );

        // The only difference between the EKF and the KF is that we have
        // to estimate the output-Jacobian, the derivative of the estimated
        // output with respected to the current estimated state.
        Matrix C = NumericalDifferentiator.MatrixJacobian.differentiate(
            xpred, this.observationModel );

        // Update step... compute the difference between the observation
        // and what the model says.
        // Then compute the Kalman gain, which essentially indicates
        // how much to believe the observation, and how much to believe model
        Vector innovation = observation.minus( ypred );
        this.computeMeasurementBelief(belief, innovation, C);
    }

    /**
     * Holds the input constant while perturbing the state to estimate
     * the Jacobian (A) matrix
     */
    protected class ModelJacobianEvaluator
        implements Evaluator<Vector,Vector>
    {

        /**
         * Creates a new instance of ModelJacobianEvaluator
         */
        public ModelJacobianEvaluator()
        {
        }

        public Vector evaluate(
            Vector input)
        {
            motionModel.setState(input.clone());
            return motionModel.evaluate(currentInput);
        }

    }

}
