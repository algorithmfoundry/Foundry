/*
 * File:                AbstractPrincipalComponentsAnalysis.java
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
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Abstract implementation of PCA.
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
        "Minor change to javadoc.",
        "Looks fine."
    }
)
abstract public class AbstractPrincipalComponentsAnalysis
    extends AbstractCloneableSerializable
    implements PrincipalComponentsAnalysis
{

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
     * Creates a new instance of AbstractPrincipalComponentsAnalysis
     * @param numComponents 
     * Number of components to extract from the data, must be greater than zero
     * @param result 
     * Vector function that maps the input space onto a numComponents-dimension
     * Vector representing the directions of maximal variance (information
     * gain).  The i-th row in the matrix approximates the i-th column of the
     * "U" matrix of the Singular Value Decomposition.
     */
    public AbstractPrincipalComponentsAnalysis(
        int numComponents,
        PrincipalComponentsAnalysisFunction result )
    {
        this.setNumComponents( numComponents );
        this.setResult( result );
    }

    @Override
    public AbstractPrincipalComponentsAnalysis clone()
    {
        AbstractPrincipalComponentsAnalysis clone =
            (AbstractPrincipalComponentsAnalysis) super.clone();
        clone.setResult( ObjectUtil.cloneSafe( this.getResult() ) );
        return clone;
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
        int numComponents )
    {
        if (numComponents <= 0)
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
     * gain).  The i-th row in the matrix approximates the i-th column of the
     * "U" matrix of the Singular Value Decomposition.
     */
    protected void setResult(
        PrincipalComponentsAnalysisFunction result )
    {
        this.result = result;
    }

}
