/*
 * File:                LinearRegressionBuffer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 23, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.evaluator.AbstractStatefulEvaluator;
import gov.sandia.cognition.collection.FiniteCapacityBuffer;
import gov.sandia.cognition.math.RingAccumulator;

/**
 * Takes a sampled sequence of equal-dimension vectors as input and computes 
 * the linear regression coefficients for each dimension in the vectors. In
 * other words, it compute the best-fit equation:
 * <BR>
 *      y_i = m*x_i + b,
 * <BR>
 * where "m" and "b" are the slope and offset for that dimension in the buffer.
 * For each "i" vector dimension in the regression buffer. Thus, if one puts
 * vectors of dimension "n" into the buffer, at each timestep, one will get
 * one vector of slopes (m) and one vector of offsets (b), each of dimension n.
 * The output of the evaluate() method is a Matrix with first column of slopes,
 * and the next column of offsets.
 *
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2006-07-17",
    changesNeeded=false,
    comments={
        "Encapsulated members, added a comment to virtually every method and member.",
        "Added comment about the regression equation.",
        "No additional changes needed."
    }
)
public class LinearRegressionCoefficientExtractor
    extends AbstractStatefulEvaluator<Vector, Vector, FiniteCapacityBuffer<Vector>>
{

    /**
     * Default maximum buffer size, {@value}.
     */
    public static final int DEFAULT_MAX_BUFFER_SIZE = 20;

    /**
     * maximum number of vectors to hold in the buffer
     */
    private int maxBufferSize;


    /**
     * Default constructor.
     */
    public LinearRegressionCoefficientExtractor()
    {
        this( DEFAULT_MAX_BUFFER_SIZE );
    }

    /**
     * Creates new instance of LinearRegressionEvaluator
     * 
     * @param maxBufferSize
     * maximum number of vectors to hold in the buffer
     */
    public LinearRegressionCoefficientExtractor(
        int maxBufferSize )
    {
        super();
        this.setMaxBufferSize( maxBufferSize );        
    }

    @Override
    public LinearRegressionCoefficientExtractor clone()
    {
        LinearRegressionCoefficientExtractor clone =
            (LinearRegressionCoefficientExtractor) super.clone();
        if( this.getState() != null )
        {
            clone.setState( this.getState().clone() );
        }
        return clone;
    }
    
    public FiniteCapacityBuffer<Vector> createDefaultState()
    {
        return new FiniteCapacityBuffer<Vector>( this.getMaxBufferSize() );
    }
    
    public Vector evaluate(
        Vector input )
    {
        int M = input.getDimensionality();
        this.getState().addLast( input );
        
        // This is the mean of the arithmetic series from 0..(num-1):
        // 1->0;
        // 2->0.5;
        // 3->1;
        // 4->0+1+2+3->1.5;
        // 5->0+1+2+3+4->2;
        // Thus, meanx == (num-1) / 2.0;        
        int num = this.getState().size();
        
        Vector ms;
        Vector bs;
        RingAccumulator<Vector> sumy = new RingAccumulator<Vector>( this.getState() );
        Vector meany = sumy.getMean();
        if( num > 1 )
        {
            double meanx = -(num - 1) / 2.0;
            double sxx = 0.0;
            RingAccumulator<Vector> sumxy = new RingAccumulator<Vector>();
            int x = -num + 1;
            for (Vector y : this.getState())
            {
                double dx = x - meanx;
                sxx += dx * dx;
                sumxy.accumulate( y.minus( meany ).scale( dx ) );
                x++;
            }
            ms = sumxy.scaleSum( 1.0/sxx );
            bs = meany.minus( ms.scale(meanx) );
        }
        else
        {
            ms = VectorFactory.getDefault().createVector(M);
            bs = meany;
        }
        
        return bs.stack(ms);

    }

    /**
     * Getter for maxBufferSize
     * @return 
     * Maximum Buffer size
     */
    public int getMaxBufferSize()
    {
        return this.maxBufferSize;
    }

    /**
     * Setter for maxBufferSize
     * @param maxBufferSize 
     * Maximum buffer size, must be greater than or equal to 2
     */
    public void setMaxBufferSize(
        int maxBufferSize )
    {
        if (maxBufferSize < 2)
        {
            throw new IllegalArgumentException(
                "Must have at least 2 samples to fit two polynomials!!" );
        }
        this.maxBufferSize = maxBufferSize;

        this.setState(null);
    }

}

