/*
 * File:                FourierTransform.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 8, 2009, Sandia Corporation.
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
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Computes the Fast Fourier Transform, or brute-force discrete Fourier
 * transform, of a discrete input sequence.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Fast Fourier transform",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Fast_Fourier_transform"
)
public class FourierTransform
    extends AbstractCloneableSerializable
    implements Evaluator<Collection<Double>,Collection<ComplexNumber>>
{

    /** 
     * Creates a new instance of FourierTransform
     */
    public FourierTransform()
    {
    }

    /**
     * Converts the Collection of real data to complex numbers
     * @param data
     * Real data to convert
     * @return
     * ArrayList of ComplexNumbers with the real parts given by "data" and
     * zeros for imaginary parts.
     */
    protected static ArrayList<ComplexNumber> convertToComplex(
        Collection<Double> data )
    {
        ArrayList<ComplexNumber> complexData =
            new ArrayList<ComplexNumber>( data.size() );
        for( Double real : data )
        {
            complexData.add( new ComplexNumber( real, 0.0 ) );
        }
        return complexData;
    }

    /**
     * Computes the brute-force discrete Fourier transform of the input data.
     * This algorithm takes O(n^2) time to compute and is MUCH slower than
     * the Cooley-Tukey FFT.
     * @param data
     * Real data to compute the FFT of.
     * @return
     * Complex-number coefficients of the data, where the zeroth element is
     * the DC offset, the element at index 1 is the first component, etc.
     * There will be as many frequency components as the length of "data".
     */
    public static ComplexNumber[] discreteFourierTransform(
        ArrayList<Double> data )
    {
        return discreteFourierTransformComplex(
            convertToComplex(data) );
    }

    /**
     * Computes the brute-force discrete Fourier transform of the input data.
     * This algorithm takes O(n^2) time to compute and is MUCH slower than
     * the Cooley-Tukey FFT.
     * @param data
     * Data to compute the FFT of.
     * @return
     * Complex-number coefficients of the data, where the zeroth element is
     * the DC offset, the element at index 1 is the first component, etc.
     * There will be as many frequency components as the length of "data".
     */
    @PublicationReference(
        author="Wikipedia",
        title="Discrete Fourier transform",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Discrete_Fourier_transform"
    )
    protected static ComplexNumber[] discreteFourierTransformComplex(
        ArrayList<ComplexNumber> data )
    {

        int num = data.size();
        ComplexNumber[] coefficients = new ComplexNumber[num];
        for( int k = 0; k < num; k++ )
        {
            final ComplexNumber Xk = new ComplexNumber();
            final double phi0 = -2.0 * Math.PI * k / num;
            double phin;
            for( int n = 0; n < num; n++ )
            {
                phin = phi0 * n;
                ComplexNumber wn = new ComplexNumber( Math.cos( phin ), Math.sin( phin ) );
                wn.timesEquals(data.get(n));
                Xk.plusEquals(wn);
            }
            coefficients[k] = Xk;
        }

        return coefficients;

    }

    /**
     * Computes the Cooley-Tukey Radix-2 Fast Fourier Transform (FFT).
     * In its pure form, the Cooley-Tukey FFT requires the length of data
     * to transform be a power of 2 (1, 2, 4, 8, 16,  ... 1024, ... ).  If
     * given data that's a power of 2, then the algorithm computes the FFT in
     * O(n log(n)) time.  We have made a slight modification that allows the
     * FFT to fall back on brute-force O(n^2) discrete Fourier transform when
     * it encounters a subset with an odd number.  For example, if supplied
     * data of length 576, then the algorithm will divide and conquer as:
     * 576, 288, 144, 72, 36, 18, 9.  When the algorithm gets to FFTs of length
     * 9, then the algorithm falls back to using the brute-force DFT.  This
     * allows the FFT to retain its divide-and-conquer approach as much as
     * possible without using sophisticated bit-flipping FFTs.
     *
     * @param data
     * Data to compute the FFT of.
     * @return
     * Complex-number coefficients of the data, where the zeroth element is
     * the DC offset, the element at index 1 is the first component, etc.
     * There will be as many frequency components as the length of "data".
     */
    @PublicationReferences(
        references={
            @PublicationReference(
                author="Wikipedia",
                title="Cooley-Tukey FFT algorithm",
                type=PublicationType.WebPage,
                year=2009,
                url="http://en.wikipedia.org/wiki/Cooley-Tukey_FFT_algorithm"
            )
            ,
            @PublicationReference(
                author={
                    "Robert Sedgewick",
                    "Kevin Wayne"
                },
                title="FFT.java",
                type=PublicationType.WebPage,
                year=2007,
                url="http://www.cs.princeton.edu/introcs/97data/FFT.java.html"
            )
        }
    )
    protected static ComplexNumber[] cooleyTukeyFFT(
        ArrayList<ComplexNumber> data )
    {

        int num = data.size();

        if( num == 1 )
        {
            return new ComplexNumber[]{ new ComplexNumber( data.get(0) ) };
        }

        if( (num % 2) != 0 )
        {
            return discreteFourierTransformComplex(data);
        }

        // Compute the FFT of the even terms
        final int halfNum = num/2;
        ArrayList<ComplexNumber> subset = new ArrayList<ComplexNumber>( halfNum );
        for( int i = 0; i < num; i += 2 )
        {
            subset.add( data.get(i) );
        }
        ComplexNumber[] evenCoefficients = cooleyTukeyFFT(subset);

        // Compute the FFT of the odd terms
        subset.clear();
        for( int i = 1; i < num; i += 2 )
        {
            subset.add( data.get(i) );
        }
        ComplexNumber[] oddCoefficients = cooleyTukeyFFT(subset);
        subset = null;

        // Combine the butterflies
        ComplexNumber[] Y = new ComplexNumber[ num ];
        final double v0 = -2.0 * Math.PI / num;
        double vi = 0.0;
        for( int i = 0; i < halfNum; i++ )
        {
            final ComplexNumber wi =
                new ComplexNumber( Math.cos( vi ), Math.sin( vi ) );
            final ComplexNumber wioddi = wi.times( oddCoefficients[i] );
            final ComplexNumber eveni = evenCoefficients[i];
            Y[i] = eveni.plus( wioddi );
            Y[i+halfNum] = eveni.minus( wioddi );
            vi += v0;
        }

        return Y;

    }

    /**
     * Computes the Fast Fourier Transform of the given input data using
     * the Cooley-Tukey Radix-2 FFT, with brute-force DFT computation on
     * odd subsequence computation.
     * @param data
     * Input data to compute the FFT of.
     * @return
     * Complex-number coefficients of the data, where the zeroth element is
     * the DC offset, the element at index 1 is the first component, etc.
     * There will be as many frequency components as the length of "data".
     */
    public List<ComplexNumber> evaluate(
        Collection<Double> data )
    {

//        final int num = data.size();
//        long start = System.currentTimeMillis();
        ComplexNumber[] c = cooleyTukeyFFT( convertToComplex(data) );

        // Make sure we remove the useless coefficients
        List<ComplexNumber> coefficients = Arrays.asList( c );
//        long stop = System.currentTimeMillis();
//        System.out.println( "Computed FFT " + data.size() + " in " + (stop-start)/1000.0 );

        return coefficients;

    }

    /**
     * Static function that inverts a Fourier transform.
     * @param transformCoefficients
     * Transform coefficients to invert back into a scalar data set.
     * @return
     * Scalar data represented by the given Fourier coefficients.
     */
    public static ArrayList<Double> inverse(
        Collection<ComplexNumber> transformCoefficients )
    {

        final int num = transformCoefficients.size();
        
        ArrayList<ComplexNumber> transformCoefficientsConjugate =
            new ArrayList<ComplexNumber>( num );
        for( ComplexNumber value : transformCoefficients )
        {
            transformCoefficientsConjugate.add( value.conjugate() );
        }

        ComplexNumber[] complexData = cooleyTukeyFFT( transformCoefficientsConjugate );
        ArrayList<Double> data = new ArrayList<Double>( num );
        double scale = 1.0 / num;
        for( int i = 0; i < num; i++ )
        {
            data.add( complexData[i].getRealPart() * scale );
        }

        return data;

    }

    /**
     * Evaluator that inverts a Fourier transform.
     */
    public static class Inverse
        extends AbstractCloneableSerializable
        implements Evaluator<Collection<ComplexNumber>,Collection<Double>>
    {

        /**
         * Default constructor
         */
        public Inverse()
        {
        }

        public ArrayList<Double> evaluate(
            Collection<ComplexNumber> input)
        {
            return FourierTransform.inverse(input);
        }

    }
    
}
