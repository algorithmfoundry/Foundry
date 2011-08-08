/*
 * File:                RootBracketExpanderTest.java
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
import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolatorTestHarness.CosineFunction;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class RootBracketExpanderTest
 * @author Kevin R. Dixon
 */
public class RootBracketExpanderTest
    extends TestCase
{

    public Random random = new Random( 1 );
    
    /**
     * Entry point for JUnit tests for class RootBracketExpanderTest
     * @param testName name of this test
     */
    public RootBracketExpanderTest(
        String testName)
    {
        super(testName);
    }

    
    /**
     * Test cosine
     */
    public void testCosine()
    {
        System.out.println( "Cosine" );
        
        RootBracketExpander instance = new RootBracketExpander();
        instance.setInitialGuess( 0.1 );
        
        CosineFunction f = new CosineFunction();
        LineBracket bracket = instance.learn( f );
        System.out.println( "cos Bracket: " + bracket );
        assertTrue( bracket.getLowerBound().getOutput() * bracket.getUpperBound().getOutput() < 0.0 );
    }
    
    /**
     * Atan()
     */
    public void testAtan()
    {
        System.out.println( "Atan" );
        
        RootBracketExpander instance = new RootBracketExpander();
        instance.setInitialGuess( random.nextGaussian() );
        AtanFunction f = new AtanFunction();
        
        LineBracket bracket = instance.learn( f );
        System.out.println( "atan Bracket: " + bracket );
        assertTrue( bracket.getLowerBound().getInput() < 0.0 );
        assertTrue( bracket.getUpperBound().getInput() > 0.0 );
        
    }
    

    /**
     * Test of getResult method, of class RootBracketExpander.
     */
    public void testGetResult()
    {
        System.out.println( "getResult" );
        RootBracketExpander instance = new RootBracketExpander();
        assertNull( instance.getResult() );
        AtanFunction f = new AtanFunction();
        LineBracket bracket = instance.learn( f );
        assertNotNull( instance.getResult() );
        assertSame( bracket, instance.getResult() );
    }

    /**
     * Test of getInitialGuess method, of class RootBracketExpander.
     */
    public void testGetInitialGuess()
    {
        System.out.println( "getInitialGuess" );
        RootBracketExpander instance = new RootBracketExpander();
        assertEquals( RootBracketExpander.DEFAULT_INITIAL_GUESS, instance.getInitialGuess() );
    }

    /**
     * Test of setInitialGuess method, of class RootBracketExpander.
     */
    public void testSetInitialGuess()
    {
        System.out.println( "setInitialGuess" );
        System.out.println( "getInitialGuess" );
        RootBracketExpander instance = new RootBracketExpander();
        assertEquals( RootBracketExpander.DEFAULT_INITIAL_GUESS, instance.getInitialGuess() );
        
        double r2 = random.nextGaussian();
        instance.setInitialGuess( r2 );
        assertEquals( r2, instance.getInitialGuess() );
        
        AtanFunction f = new AtanFunction();
        LineBracket bracket = instance.learn( f );
        assertEquals( r2, instance.getInitialGuess() );
        assertEquals( r2, bracket.getOtherPoint().getInput() );
    }

    /**
     * Test of getBracket method, of class RootBracketExpander.
     */
    public void testGetBracket()
    {
        System.out.println( "getBracket" );
        RootBracketExpander instance = new RootBracketExpander();
        double r2 = random.nextGaussian();
        instance.setInitialGuess( r2 );
        assertNull( instance.getBracket() );
        assertEquals( r2, instance.getInitialGuess() );
        AtanFunction f = new AtanFunction();
        LineBracket bracket = instance.learn( f );
        assertSame( bracket, instance.getBracket() );
        
    }

    /**
     * Test of setBracket method, of class RootBracketExpander.
     */
    public void testSetBracket()
    {
        System.out.println( "setBracket" );
        RootBracketExpander instance = new RootBracketExpander();
        double r2 = random.nextGaussian();
        instance.setInitialGuess( r2 );
        assertNull( instance.getBracket() );
        assertEquals( r2, instance.getInitialGuess() );
        AtanFunction f = new AtanFunction();
        LineBracket bracket = instance.learn( f );
        assertSame( bracket, instance.getBracket() );
        
        instance.setBracket( null );
        assertNull( instance.getBracket() );
        instance.setBracket( bracket );
        assertSame( bracket, instance.getBracket() );
    }

}
