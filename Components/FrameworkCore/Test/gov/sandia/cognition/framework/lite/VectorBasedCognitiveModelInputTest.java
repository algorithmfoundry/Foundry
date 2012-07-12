/*
 * File:                VectorBasedCognitiveModelInputTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 25, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.math.matrix.VectorFactory;
import junit.framework.*;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Kevin R. Dixon
 * @since 2.0
 */
public class VectorBasedCognitiveModelInputTest extends TestCase
{
    
    /** The random number generator for the tests. */
    protected Random random = new Random();
    
    public VectorBasedCognitiveModelInputTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static VectorBasedCognitiveModelInput createInstance(
        Random random)
    {
        int num = (int) (Math.random() * 10) + 1;
        return createInstance( num, random );
    }
    
    public static VectorBasedCognitiveModelInput createInstance( int num,
        Random random)
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier[] ids = new SemanticIdentifier[ num ];
        for( int i = 0; i < num; i++ )
        {
            ids[i] = map.addLabel( new DefaultSemanticLabel( String.valueOf( i ) ) );
        }      
        
        double r = 10.0;
        Vector values = VectorFactory.getDefault().createUniformRandom( ids.length, -r, r, random );
        
        return new VectorBasedCognitiveModelInput( ids, values );
    }
    
    /**
     * Test of clone method, of class gov.sandia.cognition.framework.lite.VectorBasedCognitiveModelInput.
     */
    public void testClone()
    {
        System.out.println("clone");
        
        VectorBasedCognitiveModelInput instance = createInstance(random);
        VectorBasedCognitiveModelInput clone = instance.clone();
        
        assertEquals( instance.getValues(), clone.getValues() );
        assertNotSame( instance.getValues(), clone.getValues() );

        assertTrue(Arrays.equals(instance.getIdentifiers(), clone.getIdentifiers()));
        assertNotSame( instance.getIdentifiers(), clone.getIdentifiers() );
        
    }

    /**
     * Test of getIdentifiers method, of class gov.sandia.cognition.framework.lite.VectorBasedCognitiveModelInput.
     */
    public void testGetIdentifiers()
    {
        System.out.println("getIdentifiers");

        VectorBasedCognitiveModelInput instance = createInstance(random);
        assertNotNull( instance.getIdentifiers() );
        assertEquals( instance.getIdentifiers().length, instance.getValues().getDimensionality() );
    }

    /**
     * Test of setIdentifiers method, of class gov.sandia.cognition.framework.lite.VectorBasedCognitiveModelInput.
     */
    public void testSetIdentifiers()
    {
        System.out.println("setIdentifiers");
        
        VectorBasedCognitiveModelInput instance = createInstance(random);
        SemanticIdentifier[] ids = instance.getIdentifiers();
        assertNotNull( ids );
        
        instance.setIdentifiers( null );
        assertNull( instance.getIdentifiers() );
        
        instance.setIdentifiers( ids );
        assertSame( ids, instance.getIdentifiers() );
    }

    /**
     * Test of getValues method, of class gov.sandia.cognition.framework.lite.VectorBasedCognitiveModelInput.
     */
    public void testGetValues()
    {
        System.out.println("getValues");

        VectorBasedCognitiveModelInput instance = createInstance(random);
        Vector v = instance.getValues();
        assertNotNull( v );
        assertEquals( v.getDimensionality(), instance.getIdentifiers().length );
    }

    /**
     * Test of setValues method, of class gov.sandia.cognition.framework.lite.VectorBasedCognitiveModelInput.
     */
    public void testSetValues()
    {
        System.out.println("setValues");
        
        VectorBasedCognitiveModelInput instance = createInstance(random);
        Vector v = instance.getValues();
        assertNotNull( v );
        
        instance.setValues( null );
        assertNull( instance.getValues() );
        
        instance.setValues( v );
        assertSame( v, instance.getValues() );
    }

    /**
     * Test of getIdentifier method, of class gov.sandia.cognition.framework.lite.VectorBasedCognitiveModelInput.
     */
    public void testGetIdentifier()
    {
        System.out.println("getIdentifier");
        
        VectorBasedCognitiveModelInput instance = createInstance(random);
        int N = instance.getNumInputs();
        for( int i = 0; i < N; i++ )
        {
            assertEquals( instance.getIdentifier( i ).getLabel().getName(), String.valueOf( i ) );
        }

    }

    /**
     * Test of getNumInputs method, of class gov.sandia.cognition.framework.lite.VectorBasedCognitiveModelInput.
     */
    public void testGetNumInputs()
    {
        System.out.println("getNumInputs");
        
        VectorBasedCognitiveModelInput instance = createInstance(random);
        assertEquals( instance.getIdentifiers().length, instance.getNumInputs() );
        assertEquals( instance.getValues().getDimensionality(), instance.getNumInputs() );
    }
    
}
