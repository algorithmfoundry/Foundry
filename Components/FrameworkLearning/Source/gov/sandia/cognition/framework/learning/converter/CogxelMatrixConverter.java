/*
 * File:                CogxelMatrixConverter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 3, 2007, Sandia Corporation.  Under the terms of Contract
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
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The CogxelVectorConverter implements a converter to convert Cogxels to and
 * from Matrix objects.
 *
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class CogxelMatrixConverter
    implements CogxelConverter<Matrix>
{
 
    /**
     * Collection CogxelVectorConverters that convert the columns of the matrix
     */
    private ArrayList<CogxelVectorConverter> columnConverters;
    
    /**
     * SemanticIdentifierMap for the converter
     */
    private SemanticIdentifierMap semanticIdentifierMap;
    
    /**
     * Creates a new instance of CogxelMatrixConverter
     * @param columns 
     * Iterable of SemanticLabels to create CogxelVectorConverters from
     */
    public CogxelMatrixConverter(
        Collection<? extends Iterable<SemanticLabel>> columns )
    {
        ArrayList<CogxelVectorConverter> localColumnConverters =
            new ArrayList<CogxelVectorConverter>( columns.size() );
        for( Iterable<SemanticLabel> column : columns )
        {
            localColumnConverters.add( new CogxelVectorConverter( column ) );
        }
        this.setColumnConverters( localColumnConverters  );
        this.setSemanticIdentifierMap( null );
    }
    
    /**
     * Creates a new instance of CogxelMatrixConverter
     * @param columnConverters 
     * Collection CogxelVectorConverters that convert the columns of the matrix
     */
    public CogxelMatrixConverter(
        ArrayList<CogxelVectorConverter> columnConverters )
    {
        this( columnConverters, null );
    }
    
    /**
     * Creates a new instance of CogxelMatrixConverter
     * @param columnConverters
     * Collection CogxelVectorConverters that convert the columns of the matrix
     * @param semanticIdentifierMap 
     * SemanticIdentifierMap for the converter
     */
    public CogxelMatrixConverter(
        ArrayList<CogxelVectorConverter> columnConverters,
        SemanticIdentifierMap semanticIdentifierMap )
    {
        this.setColumnConverters( columnConverters );
        this.setSemanticIdentifierMap( semanticIdentifierMap );
    }
    
    /**
     * Copy constructor
     * @param other 
     * CogxelMatrixConverter to clone
     */
    @SuppressWarnings("unchecked")
    public CogxelMatrixConverter(
        CogxelMatrixConverter other )
    {
        this( (ArrayList<CogxelVectorConverter>) 
            other.getColumnConverters().clone(),
            other.getSemanticIdentifierMap() );
    }
    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public CogxelMatrixConverter clone()
    {
        return new CogxelMatrixConverter( this );
    }

    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public SemanticIdentifierMap getSemanticIdentifierMap()
    {
        return this.semanticIdentifierMap;
    }

    /**
     * {@inheritDoc}
     * @param semanticIdentifierMap {@inheritDoc}
     */
    public void setSemanticIdentifierMap(
        SemanticIdentifierMap semanticIdentifierMap)
    {
        this.semanticIdentifierMap = semanticIdentifierMap;
        for( CogxelVectorConverter column : this.getColumnConverters() )
        {
            column.setSemanticIdentifierMap( semanticIdentifierMap );
        }
    }

    /**
     * {@inheritDoc}
     * @param cogxels {@inheritDoc}
     * @return {@inheritDoc}
     */
    public Matrix fromCogxels(CogxelState cogxels)
    {
        Matrix retval = null;
        int N = this.getColumnConverters().size();
        for( int j = 0; j < N; j++ )
        {
            Vector column = 
                this.getColumnConverters().get(j).fromCogxels( cogxels );
            if( retval == null )
            {
                int M = column.getDimensionality();
                retval = MatrixFactory.getDefault().createMatrix( M, N );
            }
            retval.setColumn( j, column );            
        }
        
        return retval;
        
    }

    /**
     * {@inheritDoc}
     * @param data {@inheritDoc}
     * @param cogxels {@inheritDoc}
     */
    public void toCogxels(
        Matrix data,
        CogxelState cogxels)
    {
        for( int j = 0; j < this.getColumnConverters().size(); j++ )
        {
            Vector column = data.getColumn(j);
            this.getColumnConverters().get(j).toCogxels( column, cogxels );
        }
    }

    /**
     * Getter for columnConverters
     * @return 
     * Collection CogxelVectorConverters that convert the columns of the matrix
     */
    public ArrayList<CogxelVectorConverter> getColumnConverters()
    {
        return this.columnConverters;
    }

    /**
     * Setter for columnConverters
     * @param columnConverters 
     * Collection CogxelVectorConverters that convert the columns of the matrix
     */
    protected void setColumnConverters(
        ArrayList<CogxelVectorConverter> columnConverters)
    {
        if( columnConverters == null )
        {
            throw new NullPointerException( "columnConverters cannot be null!" );
        }
        this.columnConverters = columnConverters;
    }

    
}
