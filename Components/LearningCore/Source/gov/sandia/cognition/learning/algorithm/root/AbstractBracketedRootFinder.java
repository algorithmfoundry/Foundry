/*
 * File:                AbstractBracketedRootFinder.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 5, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.root;

import gov.sandia.cognition.learning.algorithm.minimization.line.LineBracket;
import gov.sandia.cognition.learning.data.InputOutputPair;

/**
 * Partial implementation of RootFinder that maintains a bracket on the root.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractBracketedRootFinder
    extends AbstractRootFinder
{

    /**
     * Default root-bracketing algorithm, RootBracketExpander.
     */
    public static final RootBracketer DEFAULT_ROOT_BRACKETER =
        new RootBracketExpander();
    
    /**
     * Root-bracketing algorithm.
     */
    private RootBracketer bracketer;
    
    /**
     * Gets the bracket on the root location.  That is, a range of input values
     * where a root is guaranteed to lay between.
     */
    private LineBracket rootBracket;
    
    /** 
     * Creates a new instance of AbstractBracketedRootFinder
     */
    public AbstractBracketedRootFinder()
    {
        super();
        this.setBracketer( DEFAULT_ROOT_BRACKETER );
        this.setRootBracket( null );
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        this.getBracketer().setInitialGuess( this.getInitialGuess() );
        this.setRootBracket( this.getBracketer().learn( this.data ) );
        
        return this.getBracketer().isResultValid();
    }
    
    @Override
    protected void cleanupAlgorithm()
    {
    }
    
    public InputOutputPair<Double, Double> getResult()
    {
        return (this.getRootBracket() != null) ? this.getRootBracket().getOtherPoint() : null;
    }    
    
    /**
     * Gets the root-bracketing algorithm that will be used to initially
     * bracket the root.
     * @return
     * Root-bracketing algorithm.
     */
    public RootBracketer getBracketer()
    {
        return this.bracketer;
    }

    /**
     * Gets the root-bracketing algorithm that will be used to initially
     * bracket the root.
     * @param bracketer
     * Root-bracketing algorithm.
     */
    public void setBracketer(
        RootBracketer bracketer )
    {
        this.bracketer = bracketer;
    }

    /**
     * Getter for rootBracket.
     * @return
     * Gets the bracket on the root location.  That is, a range of input values
     * where a root is guaranteed to lay between.
     */
    public LineBracket getRootBracket()
    {
        return this.rootBracket;
    }

    /**
     * Setter for rootBracket.
     * @param rootBracket
     * Gets the bracket on the root location.  That is, a range of input values
     * where a root is guaranteed to lay between.
     */
    public void setRootBracket(
        LineBracket rootBracket )
    {
        this.rootBracket = rootBracket;
    }    
    
}
