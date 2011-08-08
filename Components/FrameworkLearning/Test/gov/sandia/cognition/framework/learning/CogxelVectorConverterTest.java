/*
 * File:                CogxelVectorConverterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.framework.learning;

import gov.sandia.cognition.framework.learning.converter.CogxelVectorConverter;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.lite.CogxelStateLite;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultCogxel;
import gov.sandia.cognition.framework.DefaultCogxelFactory;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: CogxelVectorConverter
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class CogxelVectorConverterTest
    extends TestCase
{
    public CogxelVectorConverterTest(
        String testName)
    {
        super(testName);
    }
    
    protected static int LABEL_COUNT = 0;
    
    public static CogxelVectorConverter createInstance()
    {
        
        int N = (int) (Math.random() * 10) + 2;
        
        ArrayList<SemanticLabel> labels = new ArrayList<SemanticLabel>( N );
        for( int n = 0; n < N; n++ )
        {
            LABEL_COUNT++;
            labels.add( new DefaultSemanticLabel( "Label" + LABEL_COUNT ) );
        }
        
        return new CogxelVectorConverter( labels );
        
    }
    
    public void testConstructors()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        
        SemanticLabel[] array = new SemanticLabel[] { a, b, c };
        
        ArrayList<SemanticLabel> list = new ArrayList<SemanticLabel>();
        list.add(a);
        list.add(b);
        list.add(c);
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        
        VectorFactory defaultVectorFactory = VectorFactory.getDefault();
        CogxelFactory defaultCogxelFactory = DefaultCogxelFactory.INSTANCE;
        
        VectorFactory vectorFactory = new DenseVectorFactoryMTJ();
        CogxelFactory cogxelFactory = new DefaultCogxelFactory();
        
        CogxelVectorConverter instance = new CogxelVectorConverter();
        assertNotNull(instance.getLabels());
        assertEquals(0, instance.getLabels().size());
        assertSame(instance.getSemanticIdentifierMap(), null);
        assertSame(instance.getVectorFactory(), defaultVectorFactory);
        assertSame(instance.getCogxelFactory(), defaultCogxelFactory);
        
        
        instance = new CogxelVectorConverter(array);
        assertNotNull(instance.getLabels());
        assertEquals(3, instance.getLabels().size());
        assertEquals(a, instance.getLabels().get(0));
        assertEquals(b, instance.getLabels().get(1));
        assertEquals(c, instance.getLabels().get(2));
        assertSame(instance.getSemanticIdentifierMap(), null);
        assertSame(instance.getVectorFactory(), defaultVectorFactory);
        assertSame(instance.getCogxelFactory(), defaultCogxelFactory);
        
        
        instance = new CogxelVectorConverter(list);
        assertNotNull(instance.getLabels());
        assertEquals(3, instance.getLabels().size());
        assertEquals(a, instance.getLabels().get(0));
        assertEquals(b, instance.getLabels().get(1));
        assertEquals(c, instance.getLabels().get(2));
        assertSame(instance.getSemanticIdentifierMap(), null);
        assertSame(instance.getVectorFactory(), defaultVectorFactory);
        assertSame(instance.getCogxelFactory(), defaultCogxelFactory);
        
        
        instance = new CogxelVectorConverter(array, map);
        assertNotNull(instance.getLabels());
        assertEquals(3, instance.getLabels().size());
        assertEquals(a, instance.getLabels().get(0));
        assertEquals(b, instance.getLabels().get(1));
        assertEquals(c, instance.getLabels().get(2));
        assertSame(instance.getSemanticIdentifierMap(), map);
        assertSame(instance.getVectorFactory(), defaultVectorFactory);
        assertSame(instance.getCogxelFactory(), defaultCogxelFactory);
        
        
        instance = new CogxelVectorConverter(list, map);
        assertNotNull(instance.getLabels());
        assertEquals(3, instance.getLabels().size());
        assertEquals(a, instance.getLabels().get(0));
        assertEquals(b, instance.getLabels().get(1));
        assertEquals(c, instance.getLabels().get(2));
        assertSame(instance.getSemanticIdentifierMap(), map);
        assertSame(instance.getVectorFactory(), defaultVectorFactory);
        assertSame(instance.getCogxelFactory(), defaultCogxelFactory);
        
        instance = new CogxelVectorConverter(array, map, vectorFactory, 
            cogxelFactory);
        assertNotNull(instance.getLabels());
        assertEquals(3, instance.getLabels().size());
        assertEquals(a, instance.getLabels().get(0));
        assertEquals(b, instance.getLabels().get(1));
        assertEquals(c, instance.getLabels().get(2));
        assertSame(instance.getSemanticIdentifierMap(), map);
        assertSame(instance.getVectorFactory(), vectorFactory);
        assertSame(instance.getCogxelFactory(), cogxelFactory);
        
        instance = new CogxelVectorConverter(list, map, vectorFactory, 
            cogxelFactory);
        assertNotNull(instance.getLabels());
        assertEquals(3, instance.getLabels().size());
        assertEquals(a, instance.getLabels().get(0));
        assertEquals(b, instance.getLabels().get(1));
        assertEquals(c, instance.getLabels().get(2));
        assertSame(instance.getSemanticIdentifierMap(), map);
        assertSame(instance.getVectorFactory(), vectorFactory);
        assertSame(instance.getCogxelFactory(), cogxelFactory);
        
        instance = new CogxelVectorConverter(instance);
        assertNotNull(instance.getLabels());
        assertEquals(3, instance.getLabels().size());
        assertEquals(a, instance.getLabels().get(0));
        assertEquals(b, instance.getLabels().get(1));
        assertEquals(c, instance.getLabels().get(2));
        assertSame(instance.getSemanticIdentifierMap(), map);
        assertSame(instance.getVectorFactory(), vectorFactory);
        assertSame(instance.getCogxelFactory(), cogxelFactory);
        
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testClone()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addLabel(a);
        instance.addLabel(b);
        instance.addLabel(c);
        
        assertEquals(3, instance.getLabels().size());
        assertEquals(a, instance.getLabels().get(0));
        assertEquals(b, instance.getLabels().get(1));
        assertEquals(c, instance.getLabels().get(2));
        assertEquals(null, instance.getSemanticIdentifierMap());
        assertSame(instance.getVectorFactory(), VectorFactory.getDefault());
        assertSame(instance.getCogxelFactory(), DefaultCogxelFactory.INSTANCE);
        
        CogxelVectorConverter clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone.getLabels(), instance.getLabels());
        
        assertEquals(3, clone.getLabels().size());
        assertEquals(a, clone.getLabels().get(0));
        assertEquals(b, clone.getLabels().get(1));
        assertEquals(c, clone.getLabels().get(2));
        assertEquals(null, clone.getSemanticIdentifierMap());
        assertSame(clone.getVectorFactory(), VectorFactory.getDefault());
        assertSame(clone.getCogxelFactory(), DefaultCogxelFactory.INSTANCE);
    }

    /**
     * Test of addLabel method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testAddLabel()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        
        assertEquals(0, instance.getLabels().size());
        instance.addLabel(a);
        assertEquals(1, instance.getLabels().size());
        instance.addLabel(b);
        assertEquals(2, instance.getLabels().size());
        instance.addLabel(c);
        assertEquals(3, instance.getLabels().size());
        instance.addLabel(a);
        assertEquals(4, instance.getLabels().size());
        
        assertEquals(a, instance.getLabels().get(0));
        assertEquals(b, instance.getLabels().get(1));
        assertEquals(c, instance.getLabels().get(2));
        assertEquals(a, instance.getLabels().get(3));
    }

    /**
     * Test of addLabels method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testAddLabels()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        
        ArrayList<SemanticLabel> labels = new ArrayList<SemanticLabel>();
        labels.add(b);
        labels.add(c);
        labels.add(a);
        
        
        assertEquals(0, instance.getLabels().size());
        instance.addLabel(a);
        assertEquals(1, instance.getLabels().size());
        instance.addLabels(labels);
        assertEquals(4, instance.getLabels().size());
        
        assertEquals(a, instance.getLabels().get(0));
        assertEquals(b, instance.getLabels().get(1));
        assertEquals(c, instance.getLabels().get(2));
        assertEquals(a, instance.getLabels().get(3));
    }

    /**
     * Test of fromCogxels method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testFromCogxels()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addLabel(a);
        instance.addLabel(b);
        instance.addLabel(c);
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);
        
        CogxelState cogxels = new CogxelStateLite();
        cogxels.addCogxel(new DefaultCogxel(map.findIdentifier(a),  0.0));
        cogxels.addCogxel(new DefaultCogxel(map.findIdentifier(b), -1.0));
        cogxels.addCogxel(new DefaultCogxel(map.findIdentifier(c),  2.0));
        
        Vector v = instance.fromCogxels(cogxels);
        assertNotNull(v);
        assertEquals(3, v.getDimensionality());
        assertEquals(new Vector3(0.0, -1.0, 2.0), v);
    }

    /**
     * Test of toCogxels method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testToCogxels()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addLabel(a);
        instance.addLabel(b);
        instance.addLabel(c);
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);
        
        CogxelState cogxels = new CogxelStateLite();
        
        Vector v = new Vector3(0.0, -1.0, 2.0);
        instance.toCogxels(v, cogxels);
        assertEquals(0.0, cogxels.getCogxelActivation(map.findIdentifier(a)));
        assertEquals(-1.0, cogxels.getCogxelActivation(map.findIdentifier(b)));
        assertEquals(2.0, cogxels.getCogxelActivation(map.findIdentifier(c)));
        
        
        instance.addLabel(a);
        boolean exceptionThrown = false;
        try
        {
            instance.toCogxels(v, cogxels);
        }
        catch ( DimensionalityMismatchException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of equals method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testEquals()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals(new CogxelVectorConverter()));
        assertFalse(instance.equals(null));
        
        instance.addLabel(new DefaultSemanticLabel("a"));
        
        CogxelVectorConverter instance2 = new CogxelVectorConverter();
        instance2.addLabel(new DefaultSemanticLabel("a"));
        
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals(instance2));
        assertTrue(instance2.equals(instance));
        
        instance.addLabel(new DefaultSemanticLabel("b"));
        assertTrue(instance.equals(instance));
        assertFalse(instance.equals(instance2));
        assertFalse(instance2.equals(instance));
        
        assertTrue(instance.equals(instance.clone()));
    }

    /**
     * Test of createEmptyVector method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testCreateEmptyVector()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        instance.addLabel(new DefaultSemanticLabel("a"));
        Vector v = instance.createEmptyVector();
        assertEquals(1, v.getDimensionality(), 1);
        assertEquals(0.0, v.norm2());
        assertNotSame(instance.createEmptyVector(), v);
        
        instance.addLabel(new DefaultSemanticLabel("b"));
        v = instance.createEmptyVector();
        assertEquals(2, v.getDimensionality());
        assertEquals(0.0, v.norm2());
    }

    /**
     * Test of getDimensionality method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testGetDimensionality()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        assertEquals(0, instance.getDimensionality());
        instance.addLabel(new DefaultSemanticLabel("a"));
        assertEquals(1, instance.getDimensionality());
        instance.addLabel(new DefaultSemanticLabel("b"));
        assertEquals(2, instance.getDimensionality());
        instance.addLabel(new DefaultSemanticLabel("c"));
        assertEquals(3, instance.getDimensionality());
        
        instance.setLabels(new ArrayList<SemanticLabel>());
        assertEquals(0, instance.getDimensionality());
    }

    /**
     * Test of getSemanticIdentifierMap method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testGetSemanticIdentifierMap()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        assertNull(instance.getSemanticIdentifierMap());
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);
        assertSame(instance.getSemanticIdentifierMap(), map);
    }

    /**
     * Test of setSemanticIdentifierMap method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testSetSemanticIdentifierMap()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        assertNull(instance.getSemanticIdentifierMap());
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);
        assertSame(instance.getSemanticIdentifierMap(), map);
        
        instance.setSemanticIdentifierMap(null);
        assertNull(instance.getSemanticIdentifierMap());
    }

    /**
     * Test of getLabels method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testGetLabels()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        assertNotNull(instance.getLabels());
        assertTrue(instance.getLabels().isEmpty());
        
        ArrayList<SemanticLabel> labels = new ArrayList<SemanticLabel>();
        labels.add(new DefaultSemanticLabel("a"));
        labels.add(new DefaultSemanticLabel("b"));
        labels.add(new DefaultSemanticLabel("c"));
        
        instance.setLabels(labels);
        assertSame(instance.getLabels(), labels);
    }

    /**
     * Test of setLabels method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testSetLabels()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        assertNotNull(instance.getLabels());
        assertTrue(instance.getLabels().isEmpty());
        
        ArrayList<SemanticLabel> labels = new ArrayList<SemanticLabel>();
        labels.add(new DefaultSemanticLabel("a"));
        labels.add(new DefaultSemanticLabel("b"));
        labels.add(new DefaultSemanticLabel("c"));
        
        instance.setLabels(labels);
        assertSame(instance.getLabels(), labels);
        
        labels = new ArrayList<SemanticLabel>();
        instance.setLabels(labels);
        assertSame(instance.getLabels(), labels);
        
        instance.setLabels(null);
        assertNull(instance.getLabels());
    }

    /**
     * Test of getVectorFactory method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testGetVectorFactory()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        assertSame(instance.getVectorFactory(), VectorFactory.getDefault());
        
        VectorFactory<?> factory = new DenseVectorFactoryMTJ();
        
        instance.setVectorFactory(factory);
        
        assertSame(instance.getVectorFactory(), factory);
    }

    /**
     * Test of setVectorFactory method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testSetVectorFactory()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        assertSame(instance.getVectorFactory(), VectorFactory.getDefault());
        
        VectorFactory<?> factory = new DenseVectorFactoryMTJ();
        instance.setVectorFactory(factory);
        assertSame(instance.getVectorFactory(), factory);
        
        instance.setVectorFactory(null);
        assertNull(instance.getVectorFactory());
    }

    /**
     * Test of getCogxelFactory method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testGetCogxelFactory()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        assertSame(instance.getCogxelFactory(), DefaultCogxelFactory.INSTANCE);
        
        CogxelFactory factory = new DefaultCogxelFactory();
        instance.setCogxelFactory(factory);
        assertSame(instance.getCogxelFactory(), factory);
    }

    /**
     * Test of setCogxelFactory method, of class gov.sandia.cognition.framework.learning.CogxelVectorConverter.
     */
    public void testSetCogxelFactory()
    {
        CogxelVectorConverter instance = new CogxelVectorConverter();
        assertSame(instance.getCogxelFactory(), DefaultCogxelFactory.INSTANCE);
        
        CogxelFactory factory = new DefaultCogxelFactory();
        instance.setCogxelFactory(factory);
        assertSame(instance.getCogxelFactory(), factory);
        
        instance.setCogxelFactory(null);
        assertNull(instance.getCogxelFactory());
    }
}
