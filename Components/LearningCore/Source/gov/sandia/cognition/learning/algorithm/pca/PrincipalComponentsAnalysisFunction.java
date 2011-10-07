/*
 * File:                PrincipalComponentAnalysisFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.pca;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.function.vector.MultivariateDiscriminant;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * This VectorFunction maps a high-dimension input space onto a (hopefully)
 * simple low-dimensional output space by subtracting the mean of the input
 * data, and passing the zero-mean input through a dimension-reducing matrix
 * multiplication function.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-09-02",
    changesNeeded=false,
    comments={
        "Added default constructor, minor changes to javadoc and clone() annotation",
        "Looks fine."
    }
)
public class PrincipalComponentsAnalysisFunction
    extends AbstractCloneableSerializable
    implements VectorFunction
{
   
    /**
     * Sample mean of the data, which is subtracted from input data to yield
     * zero-mean inputs
     */
    private Vector mean;
    
    /**
     * Function that maps a high-dimension input space onto a (hopefully)
     * simple low-dimensional output space capturing the directions of
     * maximum variance (information gain)
     */
    private MultivariateDiscriminant dimensionReducer;

    /**
     * Default constructor
     */
    public PrincipalComponentsAnalysisFunction()
    {
        this( null, null );
    }
    
    /**
     * Creates a new instance of PrincipalComponentAnalysisFunction
     * @param mean 
     * Sample mean of the data, which is subtracted from input data to yield
     * zero-mean inputs
     * @param dimensionReducer 
     * Function that maps a high-dimension input space onto a (hopefully)
     * simple low-dimensional output space capturing the directions of
     * maximum variance (information gain)
     */
    public PrincipalComponentsAnalysisFunction(
        Vector mean,
        MultivariateDiscriminant dimensionReducer )
    {
        this.setMean( mean );
        this.setDimensionReducer( dimensionReducer );
    }
    
    @Override
    public PrincipalComponentsAnalysisFunction clone()
    {
        PrincipalComponentsAnalysisFunction clone =
            (PrincipalComponentsAnalysisFunction) super.clone();
        clone.setMean( ObjectUtil.cloneSafe(this.getMean()) );
        clone.setDimensionReducer(
            ObjectUtil.cloneSafe(this.getDimensionReducer()) );
        return clone;
    }

    /**
     * Computes the reduced-dimension representation of the input by
     * subtracting the mean and mapping it through the dimension-reduction
     * matrix multiplication
     * @param input 
     * Input to reduce the dimensionality
     * @return 
     * Reduced-dimension output representation of the input
     */
    public Vector evaluate(
        Vector input)
    {
        Vector delta = input.minus( this.getMean() );
        return this.getDimensionReducer().evaluate( delta );
    }
    
    /**
     * Returns the expected dimension of input Vectors
     * @return 
     * Expected dimension of input Vectors
     */
    public int getInputDimensionality()
    {
        return this.getDimensionReducer().getDiscriminant().getNumColumns();
    }
    
    /**
     * Returns the expected dimension of output Vectors
     * @return 
     * Expected dimension of output Vectors
     */
    public int getOutputDimensionality()
    {
        return this.getDimensionReducer().getDiscriminant().getNumRows();
    }

    /**
     * Getter for mean
     * @return 
     * Sample mean of the data, which is subtracted from input data to yield
     * zero-mean inputs
     */
    public Vector getMean()
    {
        return this.mean;
    }

    /**
     * Setter for mean
     * @param mean 
     * Sample mean of the data, which is subtracted from input data to yield
     * zero-mean inputs
     */
    public void setMean(
        Vector mean)
    {
        this.mean = mean;
    }

    /**
     * Getter for dimensionReducer
     * @return 
     * Function that maps a high-dimension input space onto a (hopefully)
     * simple low-dimensional output space capturing the directions of
     * maximum variance (information gain)
     */
    public MultivariateDiscriminant getDimensionReducer()
    {
        return this.dimensionReducer;
    }

    /**
     * Setter for dimensionReducer
     * @param dimensionReducer 
     * Function that maps a high-dimension input space onto a (hopefully)
     * simple low-dimensional output space capturing the directions of
     * maximum variance (information gain)
     */
    public void setDimensionReducer(
        MultivariateDiscriminant dimensionReducer)
    {
        this.dimensionReducer = dimensionReducer;
    }
    
}
