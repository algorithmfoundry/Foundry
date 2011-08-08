/*
 * CogxelVectorCollectionConverterTest.java
 * JUnit based test
 *
 * Created on July 27, 2007, 9:41 AM
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.learning.*;
import gov.sandia.cognition.framework.learning.converter.CogxelVectorConverter;
import gov.sandia.cognition.framework.learning.converter.CogxelVectorCollectionConverter;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.math.matrix.VectorFactory;
import junit.framework.*;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultCogxel;
import gov.sandia.cognition.framework.lite.CogxelStateLite;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Kevin R. Dixon
 */
public class CogxelVectorCollectionConverterTest extends TestCase
{
    
    /** The random number generator for the tests. */
    protected Random random = new Random();
    
    public CogxelVectorCollectionConverterTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static CogxelVectorCollectionConverter createInstance()
    {
        
        int N = (int) (Math.random() * 10) + 2;
        ArrayList<CogxelVectorConverter> v = new ArrayList<CogxelVectorConverter>( N );
        for( int n = 0; n < N; n++ )
        {
            v.add( CogxelVectorConverterTest.createInstance() );
        }
        
        return new CogxelVectorCollectionConverter( v );
    }
    
    
    /**
     * Test of clone method, of class gov.sandia.cognition.framework.learning.CogxelVectorCollectionConverter.
     */
    public void testClone()
    {
        System.out.println("clone");
        
        CogxelVectorCollectionConverter instance = this.createInstance();
        CogxelVectorCollectionConverter clone = instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.getNumVectors(), clone.getNumVectors() );
        assertSame( instance.getCogxelVectorConverters(), clone.getCogxelVectorConverters() );
        
    }

    /**
     * Test of fromCogxels method, of class gov.sandia.cognition.framework.learning.CogxelVectorCollectionConverter.
     */
    public void testFromCogxels()
    {
        System.out.println("fromCogxels");
        
        int N = (int) (Math.random() * 10) + 10;
        double r = 1.0;
        Vector values = VectorFactory.getDefault().createUniformRandom( N, -r, r, random );
        ArrayList<SemanticLabel> labels = new ArrayList<SemanticLabel>( N );
        ArrayList<CogxelVectorConverter> c = new ArrayList<CogxelVectorConverter>( N );
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();        
        CogxelState cogxels = new CogxelStateLite();
        for( int i = 0; i < N; i++ )
        {
            SemanticLabel label = new DefaultSemanticLabel( "Label" + i );
            labels.add( label );
            c.add( new CogxelVectorConverter( label ) );
            SemanticIdentifier identifier = map.addLabel( label );
            cogxels.addCogxel(new DefaultCogxel(identifier, values.getElement(i)));       
        }
        
        CogxelVectorCollectionConverter instance =
            new CogxelVectorCollectionConverter( c );
        instance.setSemanticIdentifierMap( map );
        

        
        ArrayList<Vector> vest = instance.fromCogxels(cogxels);
        assertNotNull(vest);
        assertEquals(N, vest.size());
        for( int i = 0; i < N; i++ )
        {
            assertEquals( values.getElement(i), vest.get(i).getElement(0) );
        }

    }

    /**
     * Test of toCogxels method, of class gov.sandia.cognition.framework.learning.CogxelVectorCollectionConverter.
     */
    public void testToCogxels()
    {
        System.out.println("toCogxels");
        
        int N = (int) (Math.random() * 10) + 10;
        double r = 1.0;
        ArrayList<Vector> values = new ArrayList<Vector>( N );
        ArrayList<SemanticLabel> labels = new ArrayList<SemanticLabel>( N );
        ArrayList<CogxelVectorConverter> c = new ArrayList<CogxelVectorConverter>( N );
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();        
        CogxelState cogxels = new CogxelStateLite();
        for( int i = 0; i < N; i++ )
        {
            values.add( VectorFactory.getDefault().createUniformRandom(1,-r,r, random) );
            SemanticLabel label = new DefaultSemanticLabel( "Label" + i );
            labels.add( label );
            c.add( new CogxelVectorConverter( label ) );
            SemanticIdentifier identifier = map.addLabel( label );
            cogxels.addCogxel(new DefaultCogxel(identifier, values.get(i).getElement(0) + 1 ) );       
        }
        
        for( int i = 0; i < N; i++ )
        {
            assertFalse( values.get(i).getElement(0) == cogxels.getCogxelActivation( map.findIdentifier( labels.get(i) ) ) );
        }        
        
        CogxelVectorCollectionConverter instance =
            new CogxelVectorCollectionConverter( c );
        instance.setSemanticIdentifierMap( map );
        
        instance.toCogxels( values, cogxels );
        for( int i = 0; i < N; i++ )
        {
            assertEquals( values.get(i).getElement(0), cogxels.getCogxelActivation( map.findIdentifier( labels.get(i) ) ) );
        }

    }

    /**
     * Test of getSemanticIdentifierMap method, of class gov.sandia.cognition.framework.learning.CogxelVectorCollectionConverter.
     */
    public void testGetSemanticIdentifierMap()
    {
        System.out.println("getSemanticIdentifierMap");
        
        CogxelVectorCollectionConverter instance = this.createInstance();
        assertNull( instance.getSemanticIdentifierMap() );
    }

    /**
     * Test of setSemanticIdentifierMap method, of class gov.sandia.cognition.framework.learning.CogxelVectorCollectionConverter.
     */
    public void testSetSemanticIdentifierMap()
    {
        System.out.println("setSemanticIdentifierMap");
        
        CogxelVectorCollectionConverter instance = this.createInstance();
        assertNull( instance.getSemanticIdentifierMap() );
        
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap( map );
        assertSame( map, instance.getSemanticIdentifierMap() );
    }

    /**
     * Test of getNumVectors method, of class gov.sandia.cognition.framework.learning.CogxelVectorCollectionConverter.
     */
    public void testGetNumVectors()
    {
        System.out.println("getNumVectors");
        
        CogxelVectorCollectionConverter instance =
            new CogxelVectorCollectionConverter(
                CogxelVectorConverterTest.createInstance(),
                CogxelVectorConverterTest.createInstance() );
        assertEquals( 2, instance.getNumVectors() );
        
        int N = (int) (Math.random() * 10) + 2;
        LinkedList<CogxelVectorConverter> v = new LinkedList<CogxelVectorConverter>();
        for( int i = 0; i < N; i++ )
        {
            v.add( CogxelVectorConverterTest.createInstance() );
        }
        
        instance = new CogxelVectorCollectionConverter( v );
        assertEquals( N, instance.getNumVectors() );
        
    }

    /**
     * Test of getCogxelVectorConverters method, of class gov.sandia.cognition.framework.learning.CogxelVectorCollectionConverter.
     */
    public void testGetCogxelVectorConverters()
    {
        System.out.println("getCogxelVectorConverters");
        
        int N = (int) (Math.random() * 10) + 2;
        LinkedList<CogxelVectorConverter> v = new LinkedList<CogxelVectorConverter>();
        for( int i = 0; i < N; i++ )
        {
            v.add( CogxelVectorConverterTest.createInstance() );
        }
        
        CogxelVectorCollectionConverter instance = new CogxelVectorCollectionConverter( v );
        Collection<CogxelVectorConverter> vest = instance.getCogxelVectorConverters();
        assertSame( v, vest );

    }

    /**
     * Test of setCogxelVectorConverters method, of class gov.sandia.cognition.framework.learning.CogxelVectorCollectionConverter.
     */
    public void testSetCogxelVectorConverters()
    {
        System.out.println("setCogxelVectorConverters");
        
        int N = (int) (Math.random() * 10) + 2;
        LinkedList<CogxelVectorConverter> v = new LinkedList<CogxelVectorConverter>();
        for( int i = 0; i < N; i++ )
        {
            v.add( CogxelVectorConverterTest.createInstance() );
        }
        
        CogxelVectorCollectionConverter instance = new CogxelVectorCollectionConverter( v );
        Collection<CogxelVectorConverter> vest = instance.getCogxelVectorConverters();
        assertSame( v, vest );
        
        LinkedList<CogxelVectorConverter> v2 = new LinkedList<CogxelVectorConverter>();
        for( int i = 0; i < N; i++ )
        {
            v2.add( CogxelVectorConverterTest.createInstance() );
        }     
        instance.setCogxelVectorConverters( v2 );
        assertSame( v2, instance.getCogxelVectorConverters() );
    }
    
}
