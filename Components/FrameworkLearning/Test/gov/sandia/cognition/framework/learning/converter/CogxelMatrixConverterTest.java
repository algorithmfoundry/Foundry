/*
 * CogxelMatrixConverterTest.java
 * JUnit based test
 *
 * Created on July 3, 2007, 10:31 AM
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.learning.converter.CogxelVectorConverter;
import gov.sandia.cognition.framework.learning.converter.CogxelMatrixConverter;
import gov.sandia.cognition.framework.DefaultCogxelFactory;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticIdentifier;
import junit.framework.*;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.lite.CogxelStateLite;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Kevin R. Dixon
 */
public class CogxelMatrixConverterTest extends TestCase
{
    
    /** The random number generator for the tests. */
    protected Random random = new Random();
    
    public CogxelMatrixConverterTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static CogxelMatrixConverter createInstance()
    {
        int M = (int) (Math.random() * 3) + 1;
        int N = (int) (Math.random() * 3) + 2;
        ArrayList<CogxelVectorConverter> columns = 
            new ArrayList<CogxelVectorConverter>( N );
        for( int j = 0; j < N; j++ )
        {
            ArrayList<SemanticLabel> labels = new ArrayList<SemanticLabel>( M );
            for( int i = 0; i < M; i++ )
            {
                labels.add( new DefaultSemanticLabel( "Label("+i+","+j+")" ) );
            }
            columns.add( new CogxelVectorConverter( labels ) );
        }
        
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        System.out.println( "Columns: " + columns );
        return new CogxelMatrixConverter( columns, map );
    }
    
    /**
     * Test of clone method, of class gov.sandia.cognition.framework.learning.CogxelMatrixConverter.
     */
    public void testClone()
    {
        System.out.println("clone");

        CogxelMatrixConverter instance = this.createInstance();
        CogxelMatrixConverter clone = instance.clone();
        assertEquals( instance.getColumnConverters(), clone.getColumnConverters() );     
        assertNotSame( instance.getColumnConverters(), clone.getColumnConverters() );
        assertSame( instance.getSemanticIdentifierMap(), clone.getSemanticIdentifierMap() );
        
    }

    /**
     * Test of getSemanticIdentifierMap method, of class gov.sandia.cognition.framework.learning.CogxelMatrixConverter.
     */
    public void testGetSemanticIdentifierMap()
    {
        System.out.println("getSemanticIdentifierMap");
        
        CogxelMatrixConverter instance = this.createInstance();
        assertNotNull( instance.getSemanticIdentifierMap() );
        
    }

    /**
     * Test of setSemanticIdentifierMap method, of class gov.sandia.cognition.framework.learning.CogxelMatrixConverter.
     */
    public void testSetSemanticIdentifierMap()
    {
        System.out.println("setSemanticIdentifierMap");
        
        CogxelMatrixConverter instance = this.createInstance();
        SemanticIdentifierMap map = instance.getSemanticIdentifierMap();
        assertNotNull( map );
        
        instance.setSemanticIdentifierMap( null );
        assertNull( instance.getSemanticIdentifierMap() );
        
        instance.setSemanticIdentifierMap( map );
        assertSame( map, instance.getSemanticIdentifierMap() );
        
    }

    /**
     * Test of fromCogxels method, of class gov.sandia.cognition.framework.learning.CogxelMatrixConverter.
     */
    public void testFromCogxels()
    {
        System.out.println("fromCogxels");
        
        CogxelMatrixConverter instance = this.createInstance();
        int M = instance.getColumnConverters().get(0).getDimensionality();
        int N = instance.getColumnConverters().size();
        
        try
        {
            instance.fromCogxels( null );
            fail( "Should have thrown NullPointerException" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        CogxelState cogxels = new CogxelStateLite();
        Matrix expected = MatrixFactory.getDefault().createMatrix( M, N );
        for( int j = 0; j < N; j++ )
        {
            CogxelVectorConverter column = instance.getColumnConverters().get(j);
            for( int i = 0; i < M; i++ )
            {
                SemanticIdentifier identifier = instance.getSemanticIdentifierMap().findIdentifier(
                    column.getLabels().get(i) );
                double value = Math.random();
                cogxels.getOrCreateCogxel( identifier, DefaultCogxelFactory.INSTANCE ).setActivation( value );
                expected.setElement(i,j, value );
            }
        }
        
        assertEquals( expected, instance.fromCogxels( cogxels ) );
    }

    /**
     * Test of toCogxels method, of class gov.sandia.cognition.framework.learning.CogxelMatrixConverter.
     */
    public void testToCogxels()
    {
        System.out.println("toCogxels");
        
        CogxelMatrixConverter instance = this.createInstance();
        int M = instance.getColumnConverters().get(0).getDimensionality();
        int N = instance.getColumnConverters().size();
        CogxelState cogxels = new CogxelStateLite();
        
        try
        {
            instance.toCogxels( null, cogxels );
            fail( "Should have thrown NullPointerException" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


        double r = 1.0;
        Matrix values = MatrixFactory.getDefault().createUniformRandom( M, N, -r, r, random );
        try
        {
            instance.toCogxels( values, null );
            fail( "Should have thrown NullPointerException" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        instance.toCogxels( values, cogxels );
        
        for( int j = 0; j < N; j++ )
        {
            CogxelVectorConverter column = instance.getColumnConverters().get(j);
            for( int i = 0; i < M; i++ )
            {
                double expected = values.getElement(i,j);
                double cogxelValue = cogxels.getCogxelActivation( column.getIdentifiers().get(i) );
                assertEquals( expected, cogxelValue );
            }
        }
        
    }

    /**
     * Test of getColumnConverters method, of class gov.sandia.cognition.framework.learning.CogxelMatrixConverter.
     */
    public void testGetColumnConverters()
    {
        System.out.println("getColumnConverters");
        
        CogxelMatrixConverter instance = this.createInstance();
        ArrayList<CogxelVectorConverter> c = instance.getColumnConverters();
        assertNotNull( c );
    }

    /**
     * Test of setColumnConverters method, of class gov.sandia.cognition.framework.learning.CogxelMatrixConverter.
     */
    @SuppressWarnings("unchecked")
    public void testSetColumnConverters()
    {
        System.out.println("setColumnConverters");
        
        CogxelMatrixConverter instance = this.createInstance();
        ArrayList<CogxelVectorConverter> c = instance.getColumnConverters();
        assertNotNull( c );
        
        ArrayList<CogxelVectorConverter> clone = (ArrayList<CogxelVectorConverter>) c.clone();
        instance.setColumnConverters( clone );
        assertNotSame( c, instance.getColumnConverters() );
        
        
        try
        {
            instance.setColumnConverters( null );
            fail( "Should have thrown NullPointerException" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        
    }
    
}
