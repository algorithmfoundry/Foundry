/*
 * File:                FunctionMinimizerNelderMead.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.DirectionalVectorToScalarFunction;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;

/**
 * Implementation of the Downhill Simplex minimization algorithm, also known as
 * the Nelder-Mead method.  It finds the minimum of a nonlinear function 
 * without using derivative information.  In my experience, this method
 * "gives up" easily and the simplex breaks down and gets stuck on complicated
 * nonlinear manifolds.  I would recommend using Powell's method instead.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author={
                "William H. Press",
                "Saul A. Teukolsky",
                "William T. Vetterling",
                "Brian P. Flannery"
            },
            title="Numerical Recipes in C, Second Edition",
            type=PublicationType.Book,
            year=1992,
            pages={411,412},
            notes="Section 10.5",
            url="http://www.nrbook.com/a/bookcpdf.php"
        ),
        @PublicationReference(
            author="Wikipedia",
            title="Nelder-Mead method",
            type=PublicationType.WebPage,
            year=2008,
            url="http://en.wikipedia.org/wiki/Nelder-Mead_method"
        )
    }
)
public class FunctionMinimizerNelderMead 
    extends AbstractAnytimeFunctionMinimizer<Vector, Double, Evaluator<? super Vector, Double>>
{

    /**
     * Default tolerance, {@value}
     */
    public static final double DEFAULT_TOLERANCE = 1e-3;

    /**
     * Default max iterations, {@value}
     */
    public static final int DEFAULT_MAX_ITERATIONS = 4000;    
    
    /** 
     * Creates a new instance of FunctionMinimizerNelderMead 
     */
    public FunctionMinimizerNelderMead()
    {
        super( null, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS );
    }
    
    /**
     * InputOutputPairs that define the simplex
     */
    private ArrayList<DefaultInputOutputPair<Vector,Double>> simplex;

    /**
     * A running sum of the inputs of the simplex, used for reflection
     */
    private Vector simplexInputSum;
    
    /**
     * Small number to keep me from dividing by zero
     */
    private static final double TINY = 1e-10;    
    
    @Override
    protected boolean initializeAlgorithm()
    {
        Evaluator<? super Vector,Double> f = this.data;
        
        Vector x0 = this.getInitialGuess().clone();
        Double y0 = f.evaluate( x0 );
        InputOutputPair<Vector,Double> p0 = 
            new DefaultInputOutputPair<Vector, Double>( x0, y0 );

        // Create the simplex and keep a running average of its x-axis midpoint
        this.simplex = initializeSimplex( p0, 1.0 );        
        this.simplexInputSum = this.computeSimplexInputSum();
        
        this.result = null;
        
        this.lineFunction = new DirectionalVectorToScalarFunction(
            this.data, null, null );
        
        return true;
    }
    
    /**
     * Initializes the simplex about the initial point
     * @param initialPoint
     * Initial point about which to initialize the simplex
     * @param offsetValue
     * Value to use to spread out the vertices of the simplex
     * @return
     * Simplex
     */
    public ArrayList<DefaultInputOutputPair<Vector,Double>> initializeSimplex(
        InputOutputPair<Vector,Double> initialPoint,
        double offsetValue )
    {
        Vector x0 = initialPoint.getInput();
        int M = x0.getDimensionality();
        
        // Create the simplex and keep a running average of its x-axis midpoint
        ArrayList<DefaultInputOutputPair<Vector,Double>> localSimplex =
            new ArrayList<DefaultInputOutputPair<Vector,Double>>( M+1 );
        
        // Add the initial point to the simplex
        localSimplex.add( DefaultInputOutputPair.create(
            initialPoint.getInput(),
            initialPoint.getOutput()) );

        // Add the remaining M points to the simplex
        for( int i = 0; i < M; i++ )
        {
            Vector xi = x0.clone();
            xi.setElement( i, x0.getElement(i) + offsetValue );
            Double yi = this.data.evaluate( xi );
            localSimplex.add( new DefaultInputOutputPair<Vector, Double>( xi, yi ) );
        }
        
        return localSimplex;
        
    }
    
    @Override
    protected boolean step()
    {
        
        // Find the highest and lowest vertex of the simplex
        int indexLow = 0;
        DefaultInputOutputPair<Vector,Double> pLow = this.simplex.get(indexLow);
        DefaultInputOutputPair<Vector,Double> pHigh = this.simplex.get(0);
        DefaultInputOutputPair<Vector,Double> pSecondHighest = null;
        for( int i = 1; i < this.simplex.size(); i++ )
        {
            DefaultInputOutputPair<Vector,Double> pi = this.simplex.get( i );
            if( pHigh.getOutput() < pi.getOutput() )
            {
                pSecondHighest = pHigh;
                pHigh = pi;
            }
            else if( (pSecondHighest == null)
                || (pSecondHighest.getOutput() < pi.getOutput()) )
            {
                pSecondHighest = pi;
            }
            
            if( pLow.getOutput() > pi.getOutput() )
            {
                pLow = pi;
                indexLow = i;
            }
        }
                
        double fhigh = pHigh.getOutput();
        double flow = pLow.getOutput();
        
        // This is the termination criterion...
        // If the simplex has contracted sufficiently
        double rtol = 2.0 * Math.abs(fhigh - flow) /
            (Math.abs(fhigh) + Math.abs(flow) + TINY);
        if( rtol <= this.getTolerance() )
        {
            return false;
        }

        // Set the result as the lowest point in the simplex
        this.result = pLow;
        
        // Begin a new iteration. First extrapolate by a factor -1 through
        // the face of the simplex across from the high point, i.e., reflex the
        // simplex from the high point
        DefaultInputOutputPair<Vector,Double> pnew = this.pointTry( pHigh, -1.0 );
        
        // Gives a result better than the best point,
        // so try an additional extrapolation by a factor of 2
        if( pnew.getOutput() <= pLow.getOutput() )
        {
            pnew = this.pointTry( pHigh, 2.0 );

            // If the new point is still better than the lowest, then assign
            // it as the result
            if( pnew.getOutput() <= pLow.getOutput() )
            {
                this.result = pnew;
            }
        }
        
        // The reflected point is worse than the second-highest,
        // so look for an intermediate lower point, that is, 
        // we'll perform a one-dimensional contraction
        else if( pnew.getOutput() >= pSecondHighest.getOutput() )
        {
            double yHighSave = pHigh.getOutput();
            pnew = this.pointTry( pHigh, 0.5 );

            // Can't seem to get rid of that high point.
            // Better contract around the lowest (best) point.
            if( pnew.getOutput() > yHighSave )
            {
                Vector xlow = pLow.getInput();
                for( int i = 0; i < this.simplex.size(); i++ )
                {
                    DefaultInputOutputPair<Vector,Double> pi = this.simplex.get(i);
                    if( i != indexLow )
                    {
                        Vector xi = pi.getInput();
                        xi.plusEquals( xlow );
                        xi.scaleEquals( 0.5 );
                        Double yi = this.data.evaluate( xi );
                        
                        // I don't actually have to setInput here, since the
                        // scaleEquals() methods (etc) will automagically
                        // show up inside "pi", but I know I'll forget this
                        // when I look at the code in the future.
                        // So here it is.
                        pi.setInput( xi );
                        pi.setOutput( yi );
                    }
                    
                }
                
                // It is more efficient to recompute the simplexInputSum from
                // scratch here, instead of tracking the change in individual
                // point coordinates
                this.simplexInputSum = this.computeSimplexInputSum();
            }
            
        }
        
        return true;
    }

    @Override
    protected void cleanupAlgorithm()
    {
    }
    
    /**
     * Computes the sum of input values in the simplex
     * @return
     * Sum of input values in the simplex
     */
    protected Vector computeSimplexInputSum()
    {
        RingAccumulator<Vector> simplexInputAccumulator =
            new RingAccumulator<Vector>();
        for( InputOutputPair<Vector,Double> pi : this.simplex )
        {
            simplexInputAccumulator.accumulate( pi.getInput() );
        }
                
        return simplexInputAccumulator.getSum();
    }
    
    /**
     * Function that defines the simplex growth direction
     */
    private DirectionalVectorToScalarFunction lineFunction;

    /**
     * Function that attempts to find a better point using the given
     * scale factor
     * @param pold
     * Old point to improve upon
     * @param scaleFactor
     * Scale factor to reflect/stretch/contract the about the simplex
     * @return
     * New point in the simplex
     */
    private DefaultInputOutputPair<Vector,Double> pointTry(
        DefaultInputOutputPair<Vector,Double> pold,
        double scaleFactor )
    {
        
        int M = this.initialGuess.getDimensionality();

        Vector xold = pold.getInput();
        
        // This is the midpoint of the remaining points in the simplex,
        // as if xold didn't exist
        Vector xmid = this.simplexInputSum.minus( xold );
        xmid.scaleEquals( 1.0 / M );
        
        // Here is the direction of the operation
        Vector direction = xold.minus( xmid );
        this.lineFunction.setDirection( direction );
        this.lineFunction.setVectorOffset( xmid );
        
        // Now, compute the reflected, contracted, expanded, etc. point.
        // Oddly enough, line search seems to slow things down quite a bit
        // instead of just using a brain-dead computation here.
        // Don't know why, but it hurts.
        // So, dear person reading this in the future, please feel free
        // to use line minimization here but, as you'll soon find out,
        // future person, it really does hurt.
        Vector xnew = this.lineFunction.computeVector( scaleFactor );
        double ynew = this.lineFunction.evaluate( scaleFactor );
        DefaultInputOutputPair<Vector,Double> pnew =
            new DefaultInputOutputPair<Vector, Double>( xnew, ynew );
        
        // If the new point is better than the original point,
        // then replace the original point with the new point.
        if( ynew < pold.getOutput() )
        {
            this.simplexInputSum.minusEquals( pold.getInput() );
            this.simplexInputSum.plusEquals( xnew );
            pold.setInput( xnew );
            pold.setOutput( ynew );
        }
        
        return pnew;

    }    

}
