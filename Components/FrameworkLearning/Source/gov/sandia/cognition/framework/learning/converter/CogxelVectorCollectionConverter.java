/*
 * File:                CogxelVectorCollectionConverter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Converts a Collection of Vectors to and from a CogxelState
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class CogxelVectorCollectionConverter
    implements CogxelConverter<Collection<Vector>>
{
    
    /**
     * Collection of CogxelVectorConverters that do the heavy lifting
     */
    private Collection<CogxelVectorConverter> cogxelVectorConverters;
    
    
    /**
     * Creates a new instance of CogxelVectorCollectionConverter
     * @param cogxelVectorConverters 
     * Collection of CogxelVectorConverters that do the heavy lifting
     */
    public CogxelVectorCollectionConverter(
        Collection<CogxelVectorConverter> cogxelVectorConverters )
    {
        this.setCogxelVectorConverters( cogxelVectorConverters );
    }
    
    
    /**
     * Creates a new instance of CogxelVectorCollectionConverter
     * 
     * @param vectorConverters 
     * Array of CogxelVectorConverters that do the heavy lifting
     */
    public CogxelVectorCollectionConverter(
        CogxelVectorConverter ... vectorConverters )
    {
        ArrayList<CogxelVectorConverter> c = 
            new ArrayList<CogxelVectorConverter>( vectorConverters.length );
        for( CogxelVectorConverter v : vectorConverters )
        {
            c.add( v );
        }
        
        this.setCogxelVectorConverters( c );
    }
    
    /**
     * Copy Constructor
     * @param other CogxelVectorCollectionConverter to copy
     */
    public CogxelVectorCollectionConverter(
        CogxelVectorCollectionConverter other )
    {
        this( other.getCogxelVectorConverters() );
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public CogxelVectorCollectionConverter clone()
    {
        return new CogxelVectorCollectionConverter( this );
    }
    
    /**
     * {@inheritDoc}
     * @param cogxels {@inheritDoc}
     * @return {@inheritDoc}
     */
    public ArrayList<Vector> fromCogxels(CogxelState cogxels)
    {
        ArrayList<Vector> retval = new ArrayList<Vector>( this.getNumVectors() );
        for( CogxelVectorConverter c : this.getCogxelVectorConverters() )
        {
            retval.add( c.fromCogxels( cogxels ) );
        }
        return retval;
        
    }

    /**
     * {@inheritDoc}
     * @param data {@inheritDoc}
     * @param cogxels {@inheritDoc}
     */
    public void toCogxels(
        Collection<Vector> data,
        CogxelState cogxels)
    {
        
        Iterator<CogxelVectorConverter> cvc = this.getCogxelVectorConverters().iterator();
        for( Vector v : data )
        {
            cvc.next().toCogxels( v, cogxels );
        }
        
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public SemanticIdentifierMap getSemanticIdentifierMap()
    {
        return this.getCogxelVectorConverters().iterator().next().getSemanticIdentifierMap();
    }

    /**
     * {@inheritDoc}
     * @param semanticIdentifierMap {@inheritDoc}
     */
    public void setSemanticIdentifierMap(
        SemanticIdentifierMap semanticIdentifierMap)
    {
        for( CogxelVectorConverter cvc : this.getCogxelVectorConverters() )
        {
            cvc.setSemanticIdentifierMap( semanticIdentifierMap );
        }
    }
    
    /**
     * Gets the number of Vectors that this class converts
     * @return number of Vectors that this class converts
     */
    public int getNumVectors()
    {
        return this.getCogxelVectorConverters().size();
    }

    /**
     * Getter for cogxelVectorConverters
     * @return 
     * Collection of CogxelVectorConverters that do the heavy lifting
     */
    public Collection<CogxelVectorConverter> getCogxelVectorConverters()
    {
        return this.cogxelVectorConverters;
    }

    /**
     * Setter for cogxelVectorConverters
     * @param cogxelVectorConverters 
     * Collection of CogxelVectorConverters that do the heavy lifting
     */
    protected void setCogxelVectorConverters(
        Collection<CogxelVectorConverter> cogxelVectorConverters)
    {
        this.cogxelVectorConverters = cogxelVectorConverters;
    }
    
}
