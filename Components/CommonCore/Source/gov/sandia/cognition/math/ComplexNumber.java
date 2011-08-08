/*
 * File:                ComplexNumber.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 13, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import java.io.Serializable;

/**
 * Represents a complex number in a rectangular manner, explicitly storing
 * the real and imaginary portions: real + j*imaginary
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=false,
            comments={
                "Fixed one or two typos in comments.",
                "Should ComplexNumber be an interface, with implementations being rectangular and polar?",
                "Certainly doesn't need to be addressed now, but something to think about.",
                "Otherwise, looks fine."
            }
        ),
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-15",
            changesNeeded=false,
            comments="Looks good, but advise a second person go over this because there is a lot of math here."
        )
    }
)
@PublicationReference(
    author="Wikipedia",
    title="Complex number",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/Complex_number"
)
public class ComplexNumber
    extends AbstractRing<ComplexNumber>
    implements Serializable
{
    /** The real part of the ComplexNumber */
    private double realPart;
    
    /** The imaginary part of the ComplexNumber */
    private double imaginaryPart;
    
    /** 
     * Creates a new instance of ComplexNumber with zero magnitude.
     */
    public ComplexNumber()
    {
        this(0.0, 0.0);
    }
    
    /**
     * Creates a new instance of ComplexNumber using the specified complex parts 
     *
     * @param realPart
     *          real part of the complex number
     * @param imaginaryPart
     *          imaginary part of the complex number
     */
    public ComplexNumber(
        double realPart,
        double imaginaryPart)
    {
        this.setRealPart(realPart);
        this.setImaginaryPart(imaginaryPart);
    }

    /**
     * Copy constructor
     * @param other
     * ComplexNumber to copy
     */
    public ComplexNumber(
        ComplexNumber other )
    {
        this( other.getRealPart(), other.getImaginaryPart() );
    }

    /**
     * Returns a deep copy of this 
     *
     * @return deep copy of this, with the same complex parts
     */
    @Override
    public ComplexNumber clone()
    {
        return super.clone();
    }
    
    /**
     * Inline addition between this and the complex number
     *
     * @param number
     *          number to add this to
     */
    public void plusEquals(
        final ComplexNumber number)
    {
        double x = this.getRealPart() + number.getRealPart();
        double y = this.getImaginaryPart() + number.getImaginaryPart();
        
        this.setRealPart( x );
        this.setImaginaryPart( y );
    }
    
    /**
     * Computes the magnitude of the complex number, sometimes called the
     * length of the number.  So that if the number is stored as "x + j*y",
     * then the magnitude is sqrt( x*x + y*y )
     *
     * @return magnitude of the complex number
     */
    public double getMagnitude()
    {
        double x = this.getRealPart();
        double y = this.getImaginaryPart();
        
        return Math.sqrt( x*x + y*y );
    }
        
    
    /**
     * Computes the phase in radians of the complex number, sometimes called
     * the angle. If the number is stored as "x + j*y", then the phase is
     * atan2( y, x )
     *
     * @return phase, in radians, of the complex number
     */
    public double getPhase()
    {
        double x = this.getRealPart();
        double y = this.getImaginaryPart();
        
        return Math.atan2( y, x );
    }
    
    /**
     * Computes the natural-base exponent of the complex number, such that
     * this = log(exp(this)) = exp(log(this))
     *  
     * @return exp( x + j*y )
     */
    public ComplexNumber computeExponent()
    {
        // Here's my arithmetic:
        //  exp(x + j*y) = exp(x)*exp(j*y)
        //      = exp(x)*(cos(y) + j*sin(y))
        //      = exp(x)*cos(y) + j*exp(x)*sin(y)
        double r = Math.exp( this.getRealPart() );
        double phase = this.getImaginaryPart();
        
        double x = r * Math.cos(phase);
        double y = r * Math.sin(phase);
        
        return new ComplexNumber( x, y );
    }
    
    /**
     * Computes the natural-base logarithm of the complex number, such that
     * this = log(exp(this)) = exp(log(this))
     *
     * @return log( x + j*y )
     */
    public ComplexNumber computeNaturalLogarithm()
    {
        // Here's my arithmetic... let r = sqrt(x*x + y*y) and
        //  theta = atan2(y,x)
        //
        //  log( x + j*y ) = log(r*exp(j*theta))
        //      = log(r) + log(exp(j*theta))
        //      = log(r) + j*theta
        double r = this.getMagnitude();
        double phase = this.getPhase();
        
        double x = Math.log( r );
        double y = phase;
        
        return new ComplexNumber( x, y );
    }

    public void dotTimesEquals(
        final ComplexNumber other)
    {
        this.setRealPart( this.getRealPart() * other.getRealPart() );
        this.setImaginaryPart(
            this.getImaginaryPart() * other.getImaginaryPart() );
    }
    
    public void minusEquals(
        final ComplexNumber other)
    {
        this.setRealPart( this.getRealPart() - other.getRealPart() );
        this.setImaginaryPart(
            this.getImaginaryPart() - other.getImaginaryPart() );
    }

    public void scaleEquals(
        double scaleFactor)
    {
        this.setRealPart( this.getRealPart() * scaleFactor );
        this.setImaginaryPart( this.getImaginaryPart() * scaleFactor );
    }

    /**
     * Arithmetic multiplication of this and other using polar coordinates:
     *      magnitude = this.magnitude * other.magnitude
     *      phase = this.phase + other.phase
     *
     *      answer.realPart = magnitude * cos( phase )
     *      answer.imaginaryPart = magnitude * sin( phase )
     *
     * @param other
     *          complex number by which to multiply this
     * @return answer
     */
    public ComplexNumber times(
        final ComplexNumber other)
    {
        ComplexNumber copy = this.clone();
        copy.timesEquals( other );
        return copy;
    }
    
    /**
     * Inline arithmetic multiplication of this and other:
     *      this.magnitude *= other.magnitude
     *      this.phase += other.phase
     *
     * @param other
     *      complex number by which to multiple this
     */
    public void timesEquals(
        final ComplexNumber other)
    {
        double r = this.getMagnitude() * other.getMagnitude();
        double theta = this.getPhase() + other.getPhase();
        
        this.setRealPart( r * Math.cos( theta ) );
        this.setImaginaryPart( r * Math.sin( theta ) );
    }
    
    /**
     * Arithmetic division of this by other using polar coordinates:
     *      magnitude = this.magnitude / other.magnitude
     *      phase = this.phase - other.phase
     *
     *      answer.realPart = magnitude * cos( phase )
     *      answer.imaginaryPart = magnitude * sin( phase )
     *
     * @param other
     *          complex number by which to divide this
     * @return answer
     */
    public ComplexNumber dividedBy(
        final ComplexNumber other)
    {
        ComplexNumber copy = this.clone();
        copy.dividedByEquals( other );
        return copy;
    }
    
    /**
     * Inline arithmetic division of this by other:
     *      this.magnitude /= other.magnitude
     *      this.phase -= other.phase
     *
     * @param other
     *      complex number by which to divide this
     */
    public void dividedByEquals(
        final ComplexNumber other)
    {
        double r = this.getMagnitude() / other.getMagnitude();
        double theta = this.getPhase() - other.getPhase();
        
        this.setRealPart( r * Math.cos( theta ) );
        this.setImaginaryPart( r * Math.sin( theta ) );
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.realPart) ^ (Double.doubleToLongBits(this.realPart) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.imaginaryPart) ^ (Double.doubleToLongBits(this.imaginaryPart) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(
        Object other)
    {
        if ( other == null )
        {
            return false;
        }
        else if ( this == other )
        {
            return true;
        }
        else if ( other instanceof ComplexNumber )
        {
            return this.equals((ComplexNumber) other, 0.0 );
        }
        else
        {
            return false;
        }    
    }

    public boolean equals(
        ComplexNumber other,
        double effectiveZero)
    {
        double realDifference = this.getRealPart() - other.getRealPart();
        if( Math.abs( realDifference ) > effectiveZero )
        {
            return false;
        }
        
        double imaginaryDifference =
            this.getImaginaryPart() - other.getImaginaryPart();
        
        if( Math.abs( imaginaryDifference ) > effectiveZero )
        {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString()
    {
        return this.getRealPart() + " + j" + this.getImaginaryPart();
    }

    /**
     * Returns the real portion of the complex number, no computation needed 
     *
     * @return real part of the complex number
     */
    public double getRealPart()
    {
        return this.realPart;
    }

    /**
     * Sets the real part of the complex number
     *
     * @param realPart
     *          real portion of the complex number
     */
    public void setRealPart(
        double realPart)
    {
        this.realPart = realPart;
    }
    
    /**
     * Returns the imaginary portion of the complex number,
     * no computation needed 
     *
     * @return imaginary part of the complex number
     */
    public double getImaginaryPart()
    {
        return this.imaginaryPart;
    }

    /**
     * Sets the imaginary portion of the complex number 
     *
     * @param imaginaryPart
     *          imaginary portion of the complex number
     */
    public void setImaginaryPart(
        double imaginaryPart)
    {
        this.imaginaryPart = imaginaryPart;
    }


    /**
     * Switches the sign of the imaginary part of this complex number.  That is,
     * if y = a + jb, then its conjugate equals y_conjugate = a - jb.
     */
    public void conjugateEquals()
    {
        this.imaginaryPart *= -1;
    }

    /**
     * Switches the sign of the imaginary part of this complex number.  That is,
     * if y = a + jb, then its conjugate equals y_conjugate = a - jb.
     * @return
     * The conjugate of this complex number.
     */
    public ComplexNumber conjugate()
    {
        return new ComplexNumber( this.getRealPart(), -this.getImaginaryPart() );
    }

    public boolean isZero(
        final double effectiveZero)
    {
        return Math.abs(this.realPart) <= effectiveZero
            && Math.abs(this.imaginaryPart) <= effectiveZero;
    }

}
