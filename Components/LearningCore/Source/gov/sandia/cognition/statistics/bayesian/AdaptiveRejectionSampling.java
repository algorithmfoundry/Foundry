/*
 * File:                AdaptiveRejectionSampling.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 5, 2010, Sandia Corporation.
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
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.math.AbstractUnivariateScalarFunction;
import gov.sandia.cognition.math.OperationNotConvergedException;
import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

/**
 * Samples form a univariate distribution using the method of adaptive
 * rejection sampling, which is a very efficient method that iteratively
 * improves the rejection and acceptance envelopes in response to additional
 * points.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author={
        "Christian P. Robert",
        "George Casella"
    },
    title="Monte Carlo Statistical Methods, Seconds Edition",
    type=PublicationType.Book,
    pages={56,58, 70,71},
    notes={
        "Algorithm A.7",
        "Algorithm A.17"
    },
    year=2004
)
public class AdaptiveRejectionSampling 
    extends AbstractCloneableSerializable
{

    /**
     * Default number of points, {@value}.
     */
    public static final int DEFAULT_MAX_NUM_POINTS = 50;

    /**
     * Logarithm of the function that we want to evaluate
     */
    LogEvaluator<?> logFunction;

    /**
     * Input-output point pairs, sorted in ascending order by their x-axis value
     */
    private ArrayList<Point> points;

    /**
     * Maximum number of points that will be stored
     */
    private int maxNumPoints;

    /**
     * Minimum support (x-value) of the logFunction
     */
    private double minSupport;

    /**
     * Maximum support (x-value) of the logFunction
     */
    private double maxSupport;

    /**
     * Upper envelope of the logFunction
     */
    UpperEnvelope upperEnvelope;

    /**
     * Lower envelope of the logFunction
     */
    LowerEnvelope lowerEnvelope;

    /** 
     * Creates a new instance of AdaptiveRejectionSampling 
     */
    public AdaptiveRejectionSampling()
    {
        this.maxNumPoints = DEFAULT_MAX_NUM_POINTS;
        this.points = new ArrayList<Point>( DEFAULT_MAX_NUM_POINTS );
        this.upperEnvelope = new UpperEnvelope();
        this.lowerEnvelope = new LowerEnvelope();
    }

    @Override
    public AdaptiveRejectionSampling clone()
    {
        AdaptiveRejectionSampling clone =
            (AdaptiveRejectionSampling) super.clone();
        clone.points = ObjectUtil.cloneSmartElementsAsArrayList(this.getPoints());
        clone.upperEnvelope = clone.new UpperEnvelope();
        clone.upperEnvelope.resetLines();

        clone.lowerEnvelope = clone.new LowerEnvelope();
        clone.lowerEnvelope.resetLines();
        clone.setLogFunction( ObjectUtil.cloneSafe( this.getLogFunction() ) );
        return clone;
    }

    /**
     * Initializes the Adaptive Rejection Sampling method
     * @param logFunction
     * Logarithm of the evaluator to consider
     * @param minSupport
     * Minimum support (x-axis) of the evaluator
     * @param maxSupport
     * Maximum support (x-axis) of the evaluator
     * @param leftPoint
     * Left point to initialize
     * @param midPoint
     * Mid point to initialize with
     * @param rightPoint
     * Right point to initialize with
     */
    public void initialize(
        AdaptiveRejectionSampling.LogEvaluator<?> logFunction,
        double minSupport,
        double maxSupport,
        double leftPoint,
        double midPoint,
        double rightPoint )
    {

        this.setLogFunction(logFunction);
        this.setMinSupport(minSupport);
        this.setMaxSupport(maxSupport);

        this.points = new ArrayList<Point>( DEFAULT_MAX_NUM_POINTS );
        this.upperEnvelope = new UpperEnvelope();
        this.lowerEnvelope = new LowerEnvelope();

        double y = this.logFunction.evaluate(leftPoint);
        this.addPoint(leftPoint, y);

        y = this.logFunction.evaluate(midPoint);
        this.addPoint(midPoint, y);

        y = this.logFunction.evaluate(rightPoint);
        this.addPoint(rightPoint, y);

    }

    /**
     * Adds a point to the set, which will adject the upper and lower envelopes
     * @param x
     * X-axis value
     * @param y
     * Y-axis value from the logFunction
     */
    public void addPoint(
        double x,
        double y )
    {
        // Only add points if we have enough space left.
        if( this.getNumPoints() < this.getMaxNumPoints() )
        {
            // Note... I've tried using SortedSet here to store points.
            // However, it appears to be MUCH more efficient to use ArrayList
            // and re-sort every time a point is added because the ability to
            // perform random-access into the ArrayList outweighs the nastiness
            // of having to re-sort each time we add a point.
            // In my unit-test batter, it appears about 50% faster to use
            // ArrayList than TreeSet -- krdixon, 2010-05-14.
            this.points.add( new Point( x, y ) );
            Collections.sort( this.points );
            this.upperEnvelope.resetLines();
            this.lowerEnvelope.resetLines();
        }
    }

    /**
     * Gets the number of points stored
     * @return
     * Number of points stored
     */
    public int getNumPoints()
    {
        return this.getPoints().size();
    }

    /**
     * Getter for points
     * @return
     * Input-output point pairs, sorted in ascending order by their x-axis value
     */
    protected Collection<Point> getPoints()
    {
        return this.points;
    }

    /**
     * Draws a single sample by the method of adaptive rejection sampling.
     * If a sample is rejected, the method will continue until a successful
     * sample is selected.
     * @param random
     * Random number generator
     * @return
     * Sample drawn according to the logFunction.
     */
    public double sample(
        Random random )
    {
        final int maxRejections = 100;
        for( int rejections = 0; rejections < maxRejections; rejections++ )
        {
            final double x = this.upperEnvelope.sampleAsDouble(random);
            final double u = random.nextDouble();
            final double logLower = this.lowerEnvelope.logEvaluate(x);
            final double logUpper = this.upperEnvelope.logEvaluate(x);
            final double envelopeRatio = Math.exp( logLower - logUpper );

            // If the probability of acceptance is between the upper and lower
            // envelopes, then we know we've got a winner, so just
            // accept this without evaluating the function itself,
            // which can be potentially costly.
            if( u <= envelopeRatio )
            {
                return x;
            }

            // The "squeeze" ratio wasn't conclusive, so compute the
            // acceptance ratio against the actual function
            else
            {
                // Update the envelopes so that we have a better estimate
                // of the function itself...
                final double logFx = this.logFunction.evaluate(x);
                if( this.getNumPoints() < this.getMaxNumPoints() )
                {
                    this.addPoint(x, logFx);
                }

                // This is the according-to-Hoyle rejection ratio...
                final double rejectionRatio = Math.exp( logFx - logUpper );
                if( u <= rejectionRatio )
                {
                    return x;
                }
            }
        }

        throw new OperationNotConvergedException(
            "Maximum number of rejections exceeded for a single sample: " + maxRejections );

    }

    /**
     * Draws samples by the adaptive rejection sampling method, which will
     * have the distribution of the logFunction
     * @param random
     * Random number generator
     * @param numSamples
     * Number of samples to draw
     * @return
     * Samples from the adaptive rejection sampling method, which will
     * have the distribution of the logFunction
     */
    public ArrayList<Double> sample(
        Random random,
        int numSamples)
    {
        ArrayList<Double> samples = new ArrayList<Double>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            samples.add( this.sample(random) );
        }
        return samples;
    }

    /**
     * Getter for logFunction
     * @return
     * Logarithm of the function that we want to evaluate
     */
    public AdaptiveRejectionSampling.LogEvaluator<?> getLogFunction()
    {
        return this.logFunction;
    }
    
    /**
     * Setter for logFunction
     * @param logFunction
     * Logarithm of the function that we want to evaluate
     */
    public void setLogFunction(
        AdaptiveRejectionSampling.LogEvaluator<?> logFunction)
    {
        this.logFunction = logFunction;
    }


    /**
     * Getter for maxNumPoints
     * @return
     * Maximum number of points that will be stored
     */
    public int getMaxNumPoints()
    {
        return this.maxNumPoints;
    }

    /**
     * Setter for maxNumPoints
     * @param maxNumPoints
     * Maximum number of points that will be stored
     */
    public void setMaxNumPoints(
        int maxNumPoints)
    {
        this.maxNumPoints = maxNumPoints;
    }

    /**
     * Getter for minSupport
     * @return
     * Minimum support (x-value) of the logFunction
     */
    public double getMinSupport()
    {
        return this.minSupport;
    }

    /**
     * Setter for minSupport
     * @param minSupport
     * Minimum support (x-value) of the logFunction
     */
    public void setMinSupport(
        double minSupport)
    {
        this.minSupport = minSupport;
    }

    /**
     * Getter for maxSupport
     * @return
     * Maximum support (x-value) of the logFunction
     */
    public double getMaxSupport()
    {
        return this.maxSupport;
    }

    /**
     * Setter for maxSupport
     * @param maxSupport
     * Maximum support (x-value) of the logFunction
     */
    public void setMaxSupport(
        double maxSupport)
    {
        this.maxSupport = maxSupport;
    }

    /**
     * Describes an enveloping function comprised of a sorted sequence of lines
     */
    public abstract class AbstractEnvelope
        extends AbstractUnivariateScalarFunction
    {

        /**
         * Line segments that comprise the envelope
         */
        protected ArrayList<LineSegment> lines;

        /**
         * Default constructor
         */
        public AbstractEnvelope()
        {
            this.lines = null;
        }

        @Override
        public AbstractEnvelope clone()
        {
            AbstractEnvelope clone = (AbstractEnvelope) super.clone();
            clone.lines = ObjectUtil.cloneSmartElementsAsArrayList(this.getLines());
            return clone;
        }

        /**
         * Getter for lines
         * @return
         * Line segments that comprise the envelope
         */
        protected ArrayList<LineSegment> getLines()
        {
            if( this.lines == null )
            {
                this.computeLines();
            }
            return this.lines;
        }

        /**
         * Resets the line segments
         */
        public void resetLines()
        {
            this.lines = null;
        }

        /**
         * Computes the line segments comprising this Envelope
         */
        abstract protected void computeLines();

        /**
         * Evaluates the logarithm of the Envelope
         * @param input
         * Input to consider
         * @return
         * Logarithm of the Envelope
         */
        public double logEvaluate(
            Double input)
        {
            return this.findLineSegment(input).evaluate(input);
        }

        public double evaluate(
            double input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

        /**
         * Finds the line segment that contains the input
         * @param input
         * Input to consider
         * @return
         * Line segment that contains the input
         */
        protected LineSegment findLineSegment(
            Double input )
        {
            ArrayList<LineSegment> ls = this.getLines();
            final int index = Collections.binarySearch(ls, input);
            return ls.get(index);
        }

    }

    /**
     * Constructs the upper envelope for sampling.
     */
    public class UpperEnvelope
        extends AbstractEnvelope
        implements ProbabilityFunction<Double>
    {


        /**
         * Cumulative sums of the normalized weights of the lines...
         * This is automatically computed by computeSegments method.
         */
        protected double[] segmentCDF;

        /**
         * Default constructor
         */
        public UpperEnvelope()
        {
            super();
            this.segmentCDF = null;
        }

        @Override
        public UpperEnvelope clone()
        {
            UpperEnvelope clone = (UpperEnvelope) super.clone();
            clone.segmentCDF = ObjectUtil.cloneSmart(this.segmentCDF);
            return clone;
        }

        public UpperEnvelope getProbabilityFunction()
        {
            return this;
        }

        /**
         * Gets the mean, which is not a supported operation. An exception is
         * thrown.
         * 
         * @return  Nothing.
         */
        public Double getMean()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * Samples from this distribution as a double.
         * 
         * @param   random
         *      The random number generator to use.
         * @return 
         *      A sample from this distribution.
         */
        public double sampleAsDouble(
            Random random)
        {
            // This is really just a trick to make sure we re-compute the lines
            // AND the segmentCDF!
            ArrayList<LineSegment> ls = this.getLines();
            final double p1 = random.nextDouble();
            int index = Arrays.binarySearch( this.segmentCDF, p1 );
            if( index < 0 )
            {
                int insertionPoint = -index - 1;
                index = insertionPoint;
            }
            LineSegment segment = ls.get(index);
            
            final double p2 = random.nextDouble();
            return segment.sampleExp(p2);
        }

        @Override
        public Double sample(
            final Random random)
        {
            return this.sampleAsDouble(random);
        }
        
        @Override
        public ArrayList<Double> sample(
            final Random random,
            final int numSamples)
        {
            final ArrayList<Double> result = new ArrayList<Double>(numSamples);
            this.sampleInto(random, numSamples, result);
            return result;
        }
        
        @Override
        public void sampleInto(
            final Random random,
            final int sampleCount,
            final Collection<? super Double> output)
        {
            for (int i = 0; i < sampleCount; i++)
            {
                output.add(this.sampleAsDouble(random));
            }
        }
        
        /**
         * Recomputes the line segments that comprise the upper envelope
         */
        protected void computeLines()
        {
            final int numLines = (points.size()-1) * 2;
            this.lines = new ArrayList<LineSegment>( numLines );
            this.segmentCDF = new double[ numLines ];

            double totalMass = 0.0;
            
            Iterator<Point> iterator = points.iterator();
            double left = getMinSupport();
            double right = iterator.next().getInput();
            PolynomialFunction.Linear leftLine = Point.line(0,points);
            LineSegment leftMost = new LineSegment(
                leftLine, left, right );
            double weight = leftMost.integrateExp();
            totalMass += weight;
            this.lines.add( leftMost );
            this.segmentCDF[this.lines.size()-1] = totalMass;
            PolynomialFunction.Linear rightLine = Point.line(1, points);
            left = right;
            right = iterator.next().getInput();
            LineSegment segment = new LineSegment(
                rightLine, left, right );
            weight = segment.integrateExp();
            totalMass += weight;
            this.lines.add( segment );
            this.segmentCDF[this.lines.size()-1] = totalMass;

            final int N = points.size();
            for( int index = 1; index < N-2; index++ )
            {
                left = right;
                leftLine = Point.line(index-1, points);
                rightLine = Point.line(index+1, points);
                right = Point.intercept(leftLine, rightLine);
                segment = new LineSegment(leftLine, left, right);
                weight = segment.integrateExp();
                totalMass += weight;
                this.lines.add( segment );
                this.segmentCDF[this.lines.size()-1] = totalMass;

                left = right;
                right = iterator.next().getInput();
                segment = new LineSegment(rightLine, left, right);
                weight = segment.integrateExp();
                totalMass += weight;
                lines.add( segment );
                this.segmentCDF[this.lines.size()-1] = totalMass;

            }
            left = right;
            right = iterator.next().getInput();
            segment = new LineSegment( Point.line(N-3,points), left, right);
            weight = segment.integrateExp();
            totalMass += weight;
            this.lines.add( segment );
            this.segmentCDF[this.lines.size()-1] = totalMass;

            left = right;
            right = getMaxSupport();
            LineSegment rightMost = new LineSegment(Point.line(N-2, points), left, right);
            weight = rightMost.integrateExp();
            totalMass += weight;
            this.lines.add( rightMost );
            this.segmentCDF[this.lines.size()-1] = totalMass;

            for( int i = 0; i < this.lines.size(); i++ )
            {
                this.segmentCDF[i] /= totalMass;
            }
        }

    }

    /**
     * Define the lower envelope for Adaptive Rejection Sampling
     */
    public class LowerEnvelope
        extends AbstractEnvelope
    {

        /**
         * Default constructor
         */
        public LowerEnvelope()
        {
            super();
        }

        /**
         * Recomputes the line segments that comprise the upper envelope
         */
        protected void computeLines()
        {
            final int numPoints = points.size();
            final int numLines = numPoints+1;
            this.lines = new ArrayList<LineSegment>( numLines );
            Iterator<Point> iterator = points.iterator();
            double left = minSupport;
            double right = iterator.next().getInput();
            PolynomialFunction.Linear line = new PolynomialFunction.Linear(
                0.0,Double.NEGATIVE_INFINITY);
            this.lines.add( new LineSegment(line, left, right) );
            for( int i = 0; i < numPoints-1; i++ )
            {
                left = right;
                right = iterator.next().getInput();
                line = Point.line(i, points);
                this.lines.add( new LineSegment(line, left, right) );
            }
            left = right;
            right = maxSupport;
            line = new PolynomialFunction.Linear( 0.0,Double.NEGATIVE_INFINITY);
            this.lines.add( new LineSegment(line, left, right) );
        }

    }

    /**
     * A line that has a minimum and maximum support (x-axis) value.
     */
    public static class LineSegment
        extends PolynomialFunction.Linear
        implements Comparable<Double>
    {

        /**
         * Left (minimum) x-axis value
         */
        double left;

        /**
         * Right (maximum) x-axis value
         */
        double right;

        /**
         * Creates a new instance of LineSegment
         * @param line
         * @param left
         * Left (minimum) x-axis value
         * @param right
         * Right (maximum) x-axis value
         */
        public LineSegment(
            PolynomialFunction.Linear line,
            double left,
            double right )
        {
            super( line.getQ0(), line.getQ1() );
            this.left = left;
            this.right = right;
        }

        /**
         * Sample from the exponent of the line segment
         * @param p
         * Probability into the line segment
         * @return
         * Sample (x-axis) value into the line segment
         */
        public double sampleExp(
            double p )
        {
            ProbabilityUtil.assertIsProbability(p);
            double q1 = this.getQ1();
            if( Math.abs(q1) >= COLLINEAR_TOLERANCE )
            {
                double l = Math.exp( q1*this.left );
                double r = Math.exp( q1*this.right );
                double delta = p*(r-l);
                double x = Math.log( l + delta ) / q1;
                return x;
            }
            else
            {
                // Straight line
                double l = this.left;
                double r = this.right;
                return l + p*(r-l);
            }

        }

        /**
         * Integrates the exponent of the line segment
         * @return
         * Integral of the exponent of the line segment
         */
        public double integrateExp()
        {
            // integral( exp( m*x + b ) )
            double q0 = this.getQ0();
            double q1 = this.getQ1();
            if( Math.abs(q1) >= COLLINEAR_TOLERANCE )
            {
                double l = Math.exp( q1*this.left );
                double r = Math.exp( q1*this.right );
                double coeff = Math.exp(q0) / q1;
                return coeff * (r-l);
            }
            else
            {
                // Slope is zero... this is a line segment
                double l = this.left;
                double r = this.right;
                double coeff = Math.exp(q0);
                return coeff * (r-l);
            }
        }

        public int compareTo(
            Double o)
        {
            final double x = o;
            if( x < this.left )
            {
                return 1;
            }
            else if( x > this.right )
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }

    }

    /**
     * An InputOutputPair that has a natural ordering according to their
     * input (x-axis) values.
     */
    public static class Point
        extends DefaultInputOutputPair<Double,Double>
        implements Comparable<Point>
    {

        /**
         * Creates a new instance of Point
         * @param x
         * Input (x-axis) value
         * @param y
         * Output (y-axis) value, most likely the natural logarithm of the
         * function output.
         */
        public Point(
            double x,
            double y )
        {
            super( x, y );
        }

        public int compareTo(
            Point o)
        {
            double x0 = this.getInput();
            double x1 = o.getInput();
            if( x0 < x1 )
            {
                return -1;
            }
            else if( x0 > x1 )
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }

        /**
         * Connects the points at index and index + 1 with a straight line.
         * If index is 0, then we connect
         * @param index
         * Lower index to connect to index + 1
         * @param points
         * Points sorted along the x-axis
         * @return
         * Linear fit between the two given points.
         */
        public static PolynomialFunction.Linear line(
            int index,
            ArrayList<Point> points )
        {
            Point pi = points.get(index);
            Point pip1 = points.get(index+1);
            return PolynomialFunction.Linear.fit(pi, pip1);
        }

        /**
         * Computes the x-axis value where the two lines intersect
         * @param line1
         * First line segment
         * @param line2
         * Second line segment
         * @return
         * X-axis value where the two lines intersect
         */
        public static double intercept(
            PolynomialFunction.Linear line1,
            PolynomialFunction.Linear line2 )
        {
            double a1 = line1.getQ1();
            double b1 = line1.getQ0();
            
            double a2 = line2.getQ1();
            double b2 = line2.getQ0();

            // The lines are collinear
            if( a1 == a2 )
            {
                // I suppose we could see if b1==b2, but then lines
                // intersect everywhere, so I'm not sure what the answer
                // would be
                throw new IllegalArgumentException( "Lines are collinear" );
            }
            else
            {
                // y = a1 * x + b1;
                // y = a2 * x + b2;
                // a1 * x + b1 = a2 * x + b2
                // (a1-a2)*x = b2-b1
                // x = (b2-b1)/(a1-a2)
                return (b2-b1)/(a1-a2);
            }

        }

    }

    /**
     * Wraps an Evaluator and takes the natural logarithm of the evaluate method
     * @param <EvaluatorType>
     * Type of Evaluator to wrap
     */
    public static abstract class LogEvaluator<EvaluatorType extends Evaluator<Double,Double>>
        extends AbstractUnivariateScalarFunction
    {

        /**
         * Evaluator to wrap and compute the natural logarithm of.
         */
        protected EvaluatorType function;

        /**
         * Creates a new instance of LogEvaluator
         * @param function
         * Evaluator to wrap and compute the natural logarithm of.
         */
        public LogEvaluator(
            EvaluatorType function)
        {
            this.setFunction(function);
        }

        @Override
        public LogEvaluator<EvaluatorType> clone()
        {
            @SuppressWarnings("unchecked")
            LogEvaluator<EvaluatorType> clone =
                (LogEvaluator<EvaluatorType>) super.clone();
            clone.setFunction( ObjectUtil.cloneSmart( this.getFunction() ) );
            return clone;
        }

        /**
         * Getter for function
         * @return
         * Evaluator to wrap and compute the natural logarithm of.
         */
        public EvaluatorType getFunction()
        {
            return this.function;
        }

        /**
         * Setter for function
         * @param function
         * Evaluator to wrap and compute the natural logarithm of.
         */
        public void setFunction(
            EvaluatorType function)
        {
            this.function = function;
        }

        public double evaluate(
            double input)
        {
            return Math.log(this.function.evaluate(input));
        }

    }

    /**
     * Wraps a PDF so that it returns the logEvaluate method.
     */
    public static class PDFLogEvaluator
        extends LogEvaluator<ProbabilityFunction<Double>>
    {

        /**
         * Creates a new instance of PDFLogEvaluator
         * @param function
         * PDF to wrap
         */
        public PDFLogEvaluator(
            ProbabilityFunction<Double> function )
        {
            super( function );
        }

        @Override
        public double evaluate(
            double input)
        {
            return this.function.logEvaluate(input);
        }

    }

}
