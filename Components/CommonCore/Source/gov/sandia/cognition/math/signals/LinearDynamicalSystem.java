/*
 * File:                LinearDynamicalSystem.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 1, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.signals;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.AbstractStatefulEvaluator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import gov.sandia.cognition.util.ObjectUtil;



/**
 * A generic Linear Dynamical System of the form
 * <BR>x_n = A*x_(n-1) + B*u_n
 * <BR>y_n = C*x_n,
 * <BR>
 * where x_(n-1) is the previous state, x_n is the current state, u_n is the
 * current input, y_n is the current output, A is the system matrix, B is the
 * input-gain matrix, and C is the output-selector matrix
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Norman S. Nise",
            title="Control Systems Engineering, Second Edition",
            type=PublicationType.Book,
            year=1995,
            pages={648,702},
            notes="Chapter 12"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Linear dynamical system",
            type=PublicationType.WebPage,
            year=2008,
            url="http://en.wikipedia.org/wiki/Linear_dynamical_system",
            notes="This Wikipedia page is simply horrible..."
        )
    }
)
public class LinearDynamicalSystem
    extends AbstractStatefulEvaluator<Vector, Vector, Vector>
    implements VectorizableVectorFunction,
    VectorInputEvaluator<Vector,Vector>,
    VectorOutputEvaluator<Vector,Vector>
{

    /**
     * System (Jacobian) matrix.  Must be square.
     */
    private Matrix A;

    /**
     * Input-gain matrix.  Columns must equal A's rows.
     */
    private Matrix B;

    /**
     * Output-selector matrix.  Columns must equal A's rows.
     */
    private Matrix C;

    /**
     * Default constructor.
     */
    public LinearDynamicalSystem()
    {
        this( 1, 1 );
    }

    /**
     * Creates a new instance of LinearDynamicalSystem.
     * @param inputDimensionality
     * Dimensionality of the input Vectors.
     * @param stateDimensionality
     * Dimensionality of the state Vectors.
     */
    public LinearDynamicalSystem(
        int inputDimensionality,
        int stateDimensionality )
    {
        this( inputDimensionality, stateDimensionality, stateDimensionality );
    }

    /**
     * Creates a new instance of LinearDynamicalSystem.
     * @param inputDimensionality
     * Dimensionality of the input Vectors.
     * @param stateDimensionality
     * Dimensionality of the state Vectors.
     * @param outputDimensionality
     * Dimensionality of the output Vectors.
     */
    public LinearDynamicalSystem(
        int inputDimensionality,
        int stateDimensionality,
        int outputDimensionality )
    {
        this( MatrixFactory.getDefault().createIdentity(stateDimensionality,stateDimensionality),
            MatrixFactory.getDefault().createMatrix(stateDimensionality,inputDimensionality),
            MatrixFactory.getDefault().createIdentity(outputDimensionality,stateDimensionality) );
    }

    /**
     * Creates a new instance of LinearDynamicalSystem
     * @param A
     * System (Jacobian) matrix.  Must be square.
     * @param B
     * Input-gain matrix.  Columns must equal A's rows.
     */
    public LinearDynamicalSystem(
        Matrix A,
        Matrix B )
    {
        this( A, B, MatrixFactory.getDefault().createIdentity(A.getNumRows(), A.getNumRows()) );
    }


    /**
     * Creates a new instance of LinearDynamicalSystem
     * @param A
     * System (Jacobian) matrix.  Must be square.
     * @param B
     * Input-gain matrix.  Columns must equal A's rows.
     * @param C
     * Output-selector matrix.  Columns must equal A's rows.
     */
    public LinearDynamicalSystem(
        Matrix A,
        Matrix B,
        Matrix C )
    {
        
        if( !A.isSquare() )
        {
            throw new IllegalArgumentException( "A must be square!" );
        }
        if( A.getNumRows() != B.getNumRows() )
        {
            throw new IllegalArgumentException(
                "A and B must have same number of rows!" );
        }
        if( A.getNumRows() != C.getNumColumns() )
        {
            throw new IllegalArgumentException(
                "Number of A rows must equal number of C columns!" );
        }

        this.setA(A);
        this.setB(B);
        this.setC(C);

    }

    @Override
    public LinearDynamicalSystem clone()
    {
        LinearDynamicalSystem clone = (LinearDynamicalSystem) super.clone();
        clone.setA( ObjectUtil.cloneSafe( this.getA() ) );
        clone.setB( ObjectUtil.cloneSafe( this.getB() ) );
        clone.setC( ObjectUtil.cloneSafe( this.getC() ) );
        return clone;
    }

    public Vector createDefaultState()
    {
        return VectorFactory.getDefault().createVector(
            this.getStateDimensionality());
    }

    public Vector evaluate(
        Vector input)
    {
        Vector xnm1 = this.getState();
        Vector xn = A.times(xnm1);
        xn.plusEquals( B.times(input) );
        this.setState(xn);
        return C.times(xn);
    }

    public Vector convertToVector()
    {
        return this.A.convertToVector().stack( this.B.convertToVector() );
    }

    public void convertFromVector(
        Vector parameters)
    {

        int Adim = this.A.getNumRows() * this.A.getNumColumns();
        int Bdim = this.B.getNumRows() * this.B.getNumColumns();
        if( Adim+Bdim != parameters.getDimensionality() )
        {
            throw new IllegalArgumentException(
                "Number of parameters doesn't equal A and B elements!" );
        }

        Vector av = parameters.subVector(0, Adim-1);
        Vector bv = parameters.subVector(Adim,parameters.getDimensionality()-1);

        this.A.convertFromVector(av);
        this.B.convertFromVector(bv);
        
    }

    public int getInputDimensionality()
    {
        return this.B.getNumColumns();
    }

    public int getOutputDimensionality()
    {
        return this.C.getNumRows();
    }

    /**
     * Gets the dimensionality of the state.
     * @return
     * Dimensionality of the state.
     */
    public int getStateDimensionality()
    {
        return this.A.getNumRows();
    }

    @Override
    public String toString()
    {
        StringBuffer retval = new StringBuffer( 1000 );
        retval.append( "x = " + this.getState() + "\n" );
        retval.append( "A =\n" + this.getA() + "\n" );
        retval.append( "B = \n" + this.getB() + "\n" );
        retval.append( "C = \n" + this.getC() + "\n" );
        return retval.toString();
    }

    /**
     * Getter for A.
     * @return
     * System (Jacobian) matrix.  Must be square.
     */
    public Matrix getA()
    {
        return this.A;
    }

    /**
     * Setter for A.
     * @param A
     * System (Jacobian) matrix.  Must be square.
     */
    public void setA(
        Matrix A)
    {
        this.A = A;
    }

    /**
     * Getter for B.
     * @return
     * Input-gain matrix.  Columns must equal A's rows.
     */
    public Matrix getB()
    {
        return this.B;
    }

    /**
     * Setter for B.
     * @param B
     * Input-gain matrix.  Columns must equal A's rows.
     */
    public void setB(
        Matrix B)
    {
        this.B = B;
    }

    /**
     * Getter for C.
     * @return
     * Output-selector matrix.  Columns must equal A's rows.
     */
    public Matrix getC()
    {
        return this.C;
    }

    /**
     * Setter for C.
     * @param C
     * Output-selector matrix.  Columns must equal A's rows.
     */
    public void setC(
        Matrix C)
    {
        this.C = C;
    }
    
}
