/*
 * File:                DirectionalVectorToScalarFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Maps a vector function onto a scalar one by using a
 * directional vector and vector offset, and the parameter to the
 * function is a scalar value along the direction from the start-point
 * offset.  This class also approximates the derivative by a method of forward
 * differences.
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-06",
    changesNeeded=false,
    comments={
        "Fixed bug introduced into the numerical differentiation procedure.",
        "Made clone() call super.clone().",
        "Fixed the brittleness in the copy constructor."
    }
)
public class DirectionalVectorToScalarFunction
    extends AbstractDifferentiableUnivariateScalarFunction
{
    
    /**
     * Value used in the forward-difference derivative approximation
     */
    public static final double FORWARD_DIFFERENCE = 1e-5;

    /** Vector offset to scale in the specified direction */
    private Vector vectorOffset;

    /** Directional vector for optimization */
    private Vector direction;

    /**
     * Function that maps a Vector onto a Double
     */
    private Evaluator<? super Vector, ? extends Double> vectorScalarFunction;

    /**
     * Cache for the last input/output that was evaluated, so that we
     * can avoid using it again in the differentiate method.
     */
    private DefaultInputOutputPair<Double,Double> lastEvaluation;


    /**
     * Creates a new function that restricts the vectorFunction to a
     * particular vector direction
     *
     * @param vectorScalarFunction
     * Function that maps a Vector onto a Double
     * @param vectorOffset offset vector from which to scale along
     *        direction to evaluate vectorFunction
     * @param direction Direction to optimize along
     */
    public DirectionalVectorToScalarFunction(
        Evaluator<? super Vector, ? extends Double> vectorScalarFunction,
        Vector vectorOffset,
        Vector direction )
    {
        this.setVectorScalarFunction( vectorScalarFunction );
        this.setVectorOffset( vectorOffset );
        this.setDirection( direction );
        this.lastEvaluation = null;
    }
    
    /**
     * Copy constructor
     * @param other
     * DirectionalVectorToScalarFunction to copy
     */
    public DirectionalVectorToScalarFunction(
        DirectionalVectorToScalarFunction other )
    {
        this( ObjectUtil.cloneSmart( other.getVectorScalarFunction() ),
            ObjectUtil.cloneSafe( other.getVectorOffset() ),
            ObjectUtil.cloneSafe( other.getDirection() ) );
    }

    @Override
    public DirectionalVectorToScalarFunction clone()
    {
        DirectionalVectorToScalarFunction clone =
            (DirectionalVectorToScalarFunction) super.clone();
        clone.setVectorScalarFunction( ObjectUtil.cloneSmart( this.getVectorScalarFunction() ) );
        clone.setVectorOffset( ObjectUtil.cloneSafe( this.getVectorOffset() ) );
        clone.setDirection( ObjectUtil.cloneSafe( this.getDirection() ) );
        clone.lastEvaluation = null;
        return clone;
    }
    
    /**
     * Getter for direction
     *
     * @return Direction along vectorFunction
     */
    public Vector getDirection()
    {
        return this.direction;
    }

    /**
     * Setter for direction
     *
     * @param direction Direction to optimize along
     */
    public void setDirection(
        Vector direction )
    {
        this.lastEvaluation = null;
        this.direction = direction;
    }

    /**
     * Getter for vectorOffset
     *
     * @return Point to use as input to vectorFunction
     */
    public Vector getVectorOffset()
    {
        return this.vectorOffset;
    }

    /**
     * Point to use as input to vectorFunction
     *
     * @param vectorOffset Point to use as input to vectorFunction
     */
    public void setVectorOffset(
        Vector vectorOffset )
    {
        this.lastEvaluation = null;
        this.vectorOffset = vectorOffset;
    }

    /**
     * Transforms the scaleFactor into a multidimensional Vector using the
     * direction
     *
     * @param scaleFactor scale factor to move along the direction from
     *        vectorOffset
     * @return Multidimensional vector corresponding to the scale factor
     *         along the direction
     */
    public Vector computeVector(
        double scaleFactor )
    {
        return this.vectorOffset.plus(
            this.direction.scale( scaleFactor ) );
    }

    /**
     * Getter for vectorScalarFunction
     * @return
     * Function that maps a Vector onto a Double
     */
    public Evaluator<? super Vector, ? extends Double> getVectorScalarFunction()
    {
        return this.vectorScalarFunction;
    }

    /**
     * Setter for vectorScalarFunction
     * @param vectorScalarFunction
     * Function that maps a Vector onto a Double
     */
    public void setVectorScalarFunction(
        Evaluator<? super Vector, ? extends Double> vectorScalarFunction )
    {
        this.vectorScalarFunction = vectorScalarFunction;
    }

    /**
     * Evaluates the vector function along the direction using the scale
     * factor "input" and vectorOffset
     *
     * @param  input scale factor to move along direction from
     *         vectorOffset
     * @return vectorFunction evaluated at the Vector corresponding to the
     *         scale factor
     */
    public double evaluate(
        double input )
    {
        Double output;
        if( this.lastEvaluation == null )
        {
            output = this.vectorScalarFunction.evaluate(
                this.computeVector( input ) );
            this.lastEvaluation =
                new DefaultInputOutputPair<Double, Double>(input,output);
        }
        else if( this.lastEvaluation.getInput() == input )
        {
            return this.lastEvaluation.getOutput();
        }
        else
        {
            output = this.vectorScalarFunction.evaluate(
                this.computeVector( input ) );
            this.lastEvaluation.setInput(input);
            this.lastEvaluation.setOutput(output);
        }
        return this.vectorScalarFunction.evaluate(
            this.computeVector( input ) );
    }

    public double differentiate(
        double input )
    {
        double output;
        if( this.lastEvaluation == null )
        {
            output = this.evaluate(input);
            this.lastEvaluation =
                new DefaultInputOutputPair<Double, Double>(input,output);
        }
        else if( this.lastEvaluation.getInput() == input )
        {
            output = this.lastEvaluation.getOutput();
        }
        else
        {
            output = this.evaluate(input);
            this.lastEvaluation.setInput(input);
            this.lastEvaluation.setOutput(output);
        }
        
        double dx = FORWARD_DIFFERENCE;
        double dy = this.evaluate( input + dx ) - output;
        
        return dy / dx;
        
    }

}
