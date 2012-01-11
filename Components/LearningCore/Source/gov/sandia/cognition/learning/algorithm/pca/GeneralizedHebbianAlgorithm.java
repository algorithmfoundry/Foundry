/*
 * File:                GeneralizedHebbianAlgorithm.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 5, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.pca;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.function.vector.MultivariateDiscriminant;
import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of the Generalized Hebbian Algorithm, also known as
 * Sanger's Rule, which is a generalization of Oja's Rule.  This algorithm is
 * an iterative version of Principal Component Analysis.  GHA finds the "num"
 * Vectors corresponding to the "num" largest singular values of the covariance
 * matrix of the data.  The result is a VectorFunction that maps the input
 * space onto a reduced "num" dimensional space, which captures the directions
 * of maximal variance.  The ith row in the resulting matrix approximates the 
 * i-th column of the "U" matrix of the Singular Value Decomposition.
 * Amazingly, this implementation is faster than the time taken to perform
 * closed-form SVD on datasets, and is practical on datasets too large for
 * an SVD.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Added PublicationReference to Sanger's master's thesis.",
        "Minor changes to javadoc.",
        "Looks fine."
    }
)
@PublicationReference(
    author="Terrence D. Sanger",
    title="Optimal Unsupervised Learning in a Single-Layer Linear Feedforward Neural Network",
    type=PublicationType.Thesis,
    year=1989,
    url="http://ece-classweb.ucsd.edu/winter06/ece173/documents/Sanger%201989%20--%20Optimal%20Unsupervised%20Learning%20in%20a%20Single-layer%20Linear%20FeedforwardNN.pdf"
)
public class GeneralizedHebbianAlgorithm
    extends AbstractAnytimeBatchLearner<Collection<Vector>, PrincipalComponentsAnalysisFunction>
    implements PrincipalComponentsAnalysis, MeasurablePerformanceAlgorithm
{
    
    /** The performance name is {@value}. */
    public static final String PERFORMANCE_NAME = "Change";

    /**
     * Learning rate, or step size, (0,1], typically ~0.1
     */
    private double learningRate;
    
    /**
     * Number of components to extract from the data, must be greater than zero
     */
    private int numComponents;    
    
    /**
     * Vector function that maps the input space onto a numComponents-dimension
     * Vector representing the directions of maximal variance (information
     * gain).  The i-th row in the matrix approximates the i-th column of the
     * "U" matrix of the Singular Value Decomposition.
     */
    private PrincipalComponentsAnalysisFunction result;
    
    /**
     * Components that have been extracted from the input data, each
     * component has the same dimensions as the input data and the size of
     * the ArrayList is numComponents
     */
    private ArrayList<Vector> components;
    
    /**
     * Sample mean of the training data. This is subtracted from the training
     * data before GHA is executed.
     */
    private Vector mean;
    
    /**
     * Minimum change below which to stop iterating, greater than or equal to
     * zero, typically 1e-10
     */
    private double minChange;

    /** The change in the last iteration. */
    private transient double change;
    
    /**
     * Creates a new instance of GeneralizedHebbianAlgorithm
     * 
     * @param minChange 
     * Minimum change below which to stop iterating, greater than or equal to
     * zero, typically 1e-10
     * @param numComponents
     * Number of components to extract from the data, must be greater than zero
     * @param learningRate
     * Learning rate, or step size, (0,1], typically ~0.1
     * @param maxIterations
     * Maximum number of iterations before stopping
     */
    public GeneralizedHebbianAlgorithm(
        int numComponents,
        double learningRate,
        int maxIterations,
        double minChange )
    {
        super( maxIterations );
        
        this.setNumComponents( numComponents );
        this.setLearningRate( learningRate );
        this.setMinChange( minChange );
        this.setResult( null );
    }
    
    @Override
    public GeneralizedHebbianAlgorithm clone()
    {
        GeneralizedHebbianAlgorithm clone =
            (GeneralizedHebbianAlgorithm) super.clone();
        clone.setData( ObjectUtil.cloneSmartElementsAsArrayList(this.getData()) );
        clone.setResult( ObjectUtil.cloneSafe( this.getResult() ) );
        clone.mean = ObjectUtil.cloneSafe( this.mean );
        return clone;
    }
    
    protected boolean initializeAlgorithm()
    {
        boolean retval = true;
        this.setData(ObjectUtil.cloneSmartElementsAsArrayList(this.data));
        
        int M = this.getNumComponents();
        int N = this.getData().iterator().next().getDimensionality();
        if( M > N )
        {
            retval = false;
            throw new IllegalArgumentException(
                "Number of EigenVectors must be <= dimension of Vectors" );
        }
        
        // Subtract the mean from the data
        this.mean = MultivariateStatisticsUtil.computeMean( this.getData() );
        for( Vector x : this.getData() )
        {
            x.minusEquals( this.mean );
        }
        
        this.components = new ArrayList<Vector>( M );
        for( int i = 0; i < M; i++ )
        {
            // Make the ith vector be the identify for the ith direction
            Vector ui = VectorFactory.getDefault().createVector( N );
            ui.setElement( i, 1.0 );
            this.components.add( ui );
        }

        this.change = 0.0;
        
        return retval;
    }
    
    protected void cleanupAlgorithm()
    {
        int N = this.getData().iterator().next().getDimensionality();

        Matrix Umatrix = MatrixFactory.getDefault().createMatrix( 
            this.getNumComponents(), N );
        for( int i = 0; i < this.getNumComponents(); i++ )
        {
            // Normalize each Vector by its 2-norm (unit length)
            Vector ui = this.components.get(i);
            Umatrix.setRow( i, ui.unitVector() );
        }
     
        this.setResult( new PrincipalComponentsAnalysisFunction(
            this.mean, new MultivariateDiscriminant( Umatrix ) ) );
        
    }
    
    protected boolean step()
    {
        
        boolean retval = true;
        
        // Hold onto the previous components so that we can see the total change
        ArrayList<Vector> componentCopy =
            new ArrayList<Vector>( this.getNumComponents() );
        for( int i = 0; i < this.getNumComponents(); i++ )
        {
            componentCopy.add( this.components.get(i).clone() );
        }
        
        double alpha = this.getLearningRate();
        for( Vector x : this.getData() )
        {
            RingAccumulator<Vector> sum = new RingAccumulator<Vector>();
            for( int i = 0; i < this.getNumComponents(); i++ )
            {
                for( int j = 0; j <= i; j++ )
                {
                    Vector uj = this.components.get(j);
                    sum.accumulate( uj.scale( uj.dotProduct( x ) ) );
                }
                Vector delta = x.minus( sum.getSum() ).scale(
                    this.components.get(i).dotProduct( x ) * alpha );
                
                this.components.get(i).plusEquals( delta );
                
            }
            
        }
        
        double changeCurrent = 0;
        for( int i = 0; i < this.getNumComponents(); i++ )
        {
            changeCurrent += this.components.get(i).minus( componentCopy.get(i) ).norm2();
        }
        changeCurrent /= alpha;
  
        double delta = changeCurrent;
        
        if( (Math.abs( delta ) <= this.getMinChange()) ||
            (Double.isNaN( delta ) || Double.isInfinite( delta )) )
        {
            retval = false;
        }

        this.change = Math.abs(delta);
        
        return retval;
    }
        
    
    /**
     * Getter for learningRate
     * @return 
     * Learning rate, or step size, (0,1], typically ~0.1
     */
    public double getLearningRate()
    {
        return this.learningRate;
    }
    
    /**
     * Setter for learningRate
     * @param learningRate 
     * Learning rate, or step size, (0,1], typically ~0.1
     */
    public void setLearningRate(
        double learningRate)
    {
        if( (learningRate <= 0.0) ||
            (learningRate > 1.0) )
        {
            throw new IllegalArgumentException(
                "LearningRate must be (0,1]" );
        }
        this.learningRate = learningRate;
    }    
    

    /**
     * Getter for minChange
     * @return 
     * Minimum change below which to stop iterating, greater than or equal to
     * zero, typically 1e-10
     */
    public double getMinChange()
    {
        return this.minChange;
    }

    /**
     * Setter for minChange
     * @param minChange 
     * Minimum change below which to stop iterating, greater than or equal to
     * zero, typically 1e-10
     */
    public void setMinChange(
        double minChange)
    {
        if( minChange < 0.0 )
        {
            throw new IllegalArgumentException(
                "minChange must be greater than or equal to zero" );
        }
        this.minChange = minChange;
    }

    public int getNumComponents()
    {
        return this.numComponents;
    }
    
    /**
     * Setter for numComponents
     * @param numComponents 
     * Number of components to extract from the data, must be greater than zero
     */
    public void setNumComponents(
        int numComponents)
    {
        if( numComponents <= 0 )
        {
            throw new IllegalArgumentException(
                "Number of components must be > 0" );
        }
        this.numComponents = numComponents;
    }
        
    public PrincipalComponentsAnalysisFunction getResult()
    {
        return this.result;
    }
    
    /**
     * Setter for result
     * @param result 
     * Vector function that maps the input space onto a numComponents-dimension
     * Vector representing the directions of maximal variance (information
     * gain).  The ith row in the matrix approximates the i-th column of the
     * "U" matrix of the Singular Value Decomposition.
     */
    protected void setResult(
        PrincipalComponentsAnalysisFunction result)
    {
        this.result = result;
    }

    /**
     * Gets the change in in the last completed step of the algorithm.
     *
     * @return
     *      The change in the last completed step of the algorithm.
     */
    public double getChange()
    {
        return this.change;
    }

    @Override
    public NamedValue<Double> getPerformance()
    {
        return DefaultNamedValue.create(PERFORMANCE_NAME, this.getChange());
    }

}
