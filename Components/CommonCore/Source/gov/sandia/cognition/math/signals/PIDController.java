/*
 * File:                PIDController.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 4, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.signals;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.AbstractStatefulEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * This class defines a Proportional-plus-Integral-plus-Derivative set-point
 * controller.  The goal of this PID controller is to minimize the difference
 * between its target-value input and a given input by changing a control
 * variable to implicitly change its given input at the next time step.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Control Tutorial for MATLAB",
            title="PID Tutorial",
            type=PublicationType.WebPage,
            year=1997,
            url="http://www.engin.umich.edu/group/ctm/PID/PID.html"
        ),
        @PublicationReference(
            author="Wikipedia",
            title="PID Controller",
            type=PublicationType.WebPage,
            year=2009,
            url="http://en.wikipedia.org/wiki/PID_controller",
            notes="This article pretty much sucks"
        )
    }
)
public class PIDController 
    extends AbstractStatefulEvaluator<Double,Double,PIDController.State>
{

    /**
     * Set point target to achieve at steady-state.
     */
    private double targetInput;

    /**
     * Proportional-error gain.
     */
    private double proportionalGain;

    /**
     * Integral-error gain.
     */
    private double integralGain;

    /**
     * Derivative-error gain.
     */
    private double derivativeGain;

    /**
     * Default proportional-error gain, {@value}.
     */
    public static final double DEFAULT_PROPORTIONAL_GAIN = 0.5;

    /**
     * Default integral-error gain, {@value}.
     */
    public static final double DEFAULT_INTEGRAL_GAIN = 0.0;

    /**
     * Default derivative-error gain, {@value}.
     */
    public static final double DEFAULT_DERIVATIVE_GAIN = 0.25;


    /** 
     * Creates a new instance of PIDController 
     */
    public PIDController()
    {
        this( DEFAULT_PROPORTIONAL_GAIN, DEFAULT_INTEGRAL_GAIN, DEFAULT_DERIVATIVE_GAIN );
    }

    /**
     * Creates a new instance of PIDController.
     * @param proportionalGain
     * Proportional-error gain.
     * @param integralGain
     * Integral-error gain.
     * @param derivativeGain
     * Derivative-error gain.
     */
    public PIDController(
        double proportionalGain,
        double integralGain,
        double derivativeGain )
    {
        this( proportionalGain, integralGain, derivativeGain, 0.0 );
    }

    /**
     * Creates a new instance of PIDController.
     * @param proportionalGain
     * Proportional-error gain.
     * @param integralGain
     * Integral-error gain.
     * @param derivativeGain
     * Derivative-error gain.
     * @param targetInput
     * Set point target to achieve at steady-state.
     */
    public PIDController(
        double proportionalGain,
        double integralGain,
        double derivativeGain,
        double targetInput )
    {
        super();
        this.setProportionalGain( proportionalGain );
        this.setIntegralGain( integralGain );
        this.setDerivativeGain( derivativeGain );
        this.setTargetInput( targetInput );
    }

    /**
     * Getter for targetInput.
     * @return
     * Set point target to achieve at steady-state.
     */
    public double getTargetInput()
    {
        return this.targetInput;
    }

    /**
     * Setter for targetInput
     * @param targetInput
     * Set point target to achieve at steady-state.
     */
    public void setTargetInput(
        double targetInput )
    {
        this.targetInput = targetInput;
    }

    /**
     * Getter for proportionalGain
     * @return
     * Proportional-error gain.
     */
    public double getProportionalGain()
    {
        return this.proportionalGain;
    }

    /**
     * Setter for proportionalGain
     * @param proportionalGain
     * Proportional-error gain.
     */
    public void setProportionalGain(
        double proportionalGain )
    {
        this.proportionalGain = proportionalGain;
    }

    /**
     * Getter for integralGain.
     * @return
     * Integral-error gain.
     */
    public double getIntegralGain()
    {
        return this.integralGain;
    }

    /**
     * Setter for integralGain
     * @param integralGain
     * Integral-error gain.
     */
    public void setIntegralGain(
        double integralGain )
    {
        this.integralGain = integralGain;
    }

    /**
     * Getter for derivativeGain
     * @return
     * Derivative-error gain.
     */
    public double getDerivativeGain()
    {
        return this.derivativeGain;
    }

    /**
     * Setter for derivativeGain
     * @param derivativeGain
     * Derivative-error gain.
     */
    public void setDerivativeGain(
        double derivativeGain )
    {
        this.derivativeGain = derivativeGain;
    }

    public Double evaluate(
        Double input )
    {
        double err = this.targetInput - input;
        
        double errSum = err + this.getState().getErrSum();
        double diffErr = err - this.getState().getLastErr();

        this.getState().setErrSum( errSum );
        this.getState().setLastErr( err );
        
        double pid = this.proportionalGain * err
            + this.integralGain * errSum
            + this.derivativeGain * diffErr;
        
        return pid;
    }

    public State createDefaultState()
    {
        return new State();
    }

    /**
     * State of a PIDController
     */
    public static class State
        extends AbstractCloneableSerializable
    {

        /**
         * Last error.
         */
        private double lastErr;

        /**
         * Sum of all errors.
         */
        private double errSum;

        /**
         * Default constructor.
         */
        public State()
        {
            this( 0.0, 0.0 );
        }


        /**
         * Creates a new instance of State
         * @param lastErr
         * Last error.
         * @param errSum
         * Sum of all errors.
         */
        public State(
            double lastErr,
            double errSum )
        {
            this.setLastErr( lastErr );
            this.setErrSum( errSum );
        }

        /**
         * Getter for lastErr
         * @return
         * Last error.
         */
        public double getLastErr()
        {
            return this.lastErr;
        }

        /**
         * Setter for lastErr
         * @param lastErr
         * Last error.
         */
        public void setLastErr(
            double lastErr )
        {
            this.lastErr = lastErr;
        }

        /**
         * Getter for errSum
         * @return
         * Sum of all errors.
         */
        public double getErrSum()
        {
            return this.errSum;
        }

        /**
         * Setter for errSum
         * @param errSum
         * Sum of all errors.
         */
        public void setErrSum(
            double errSum )
        {
            this.errSum = errSum;
        }

    }

}
