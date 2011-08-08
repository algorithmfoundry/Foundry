/*
 * File:                SimplePatternRecognizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 14, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.DefaultSemanticNetwork;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.SemanticNetwork;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * The SimplePatternRecognizer class implements a simple version of the
 * PatternRecognizerLite interface. All it does is use a matrix multiply to
 * come up with the next set of values.
 *
 * This implementation is for demonstration purposes only and is in no way
 * psychologically plausible.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class SimplePatternRecognizer
    extends AbstractCloneableSerializable
    implements MutablePatternRecognizerLite
{
    // Note: This class does not make use of the getters pattern because it
    // is expected to be high-performance and instead uses direct access to
    // the member variables.
    
    /** The nodes in the recognition network, sorted by how they are to appear
     *  in the vector. */
    private ArrayList<SemanticLabel> nodes = null;
    
    /** The mapping of semantic labels to their index in the vector. */
    private HashMap<SemanticLabel, Integer> nodeToIDMap = null;
    
    /** The matrix underlying the pattern recognizer. */
    private Matrix matrix = null;
    
    /** True if the set of nodes has changed since the last update. */
    private boolean nodesChangedSinceLastUpdate = false;
    
    /**
     * Creates a new, empty instance of SimplePatternRecognizer.
     */
    public SimplePatternRecognizer()
    {
        this(new DefaultSemanticNetwork());
    }
    
    /**
     * Creates a new instance of SimplePatternRecognizer.
     *
     * @param  network The network to create the pattern recognizer from.
     */
    public SimplePatternRecognizer(
        SemanticNetwork network)
    {
        super();
        
        // First get the nodes out of the given network.
        this.setNodes(new ArrayList<SemanticLabel>(network.getNodes()));
        this.buildNodeToIDMap();
        
        // Create the matrix corresponding to the network connections.
        int numNodes = this.nodes.size();
        Matrix weights = MatrixFactory.getDefault().createMatrix(numNodes, numNodes);
        for ( int i = 0; i < numNodes; i++)
        {
            SemanticLabel labelI = nodes.get(i);

            // Get the outgoing links for the i-th label.
            for ( SemanticLabel labelJ : network.getOutLinks(labelI) )
            {
                // Get the link to j.
                int j = nodeToIDMap.get(labelJ);
                
                double weight = network.getAssociation(labelI, labelJ);
                
                if ( weight == 0.0 )
                {
                    // The weight is zero so do not add it.
                    continue;
                }
                
                // Set the matrix entry.
                weights.setElement(j, i, weight);
            }
        }
        
        this.setMatrix(weights);
        this.setNodesChangedSinceLastUpdate(false);
    }
    
    /**
     * Creates a new instance of SimplePatternRecognizer.
     *
     * @param  other The SimplePatternRecognizer to copy.
     */
    public SimplePatternRecognizer(
        SimplePatternRecognizer other)
    {
        super();
        
        this.setNodes(new ArrayList<SemanticLabel>(other.nodes));
        this.setNodeToIDMap(
            new HashMap<SemanticLabel, Integer>(other.nodeToIDMap));
        this.setMatrix(other.matrix.clone());
        this.setNodesChangedSinceLastUpdate(other.nodesChangedSinceLastUpdate);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public SimplePatternRecognizer clone()
    {
        final SimplePatternRecognizer clone = (SimplePatternRecognizer)
            super.clone();

        clone.nodes = new ArrayList<SemanticLabel>(this.nodes);
        clone.nodeToIDMap = new HashMap<SemanticLabel, Integer>(this.nodeToIDMap);
        clone.matrix = ObjectUtil.cloneSafe(this.matrix);
        
        return clone;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public SimplePatternRecognizerState initialState()
    {
        return new SimplePatternRecognizerState(this.nodes);
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  state {@inheritDoc}
     * @param  inputs {@inheritDoc}
     * @return {@inheritDoc}
     */
    public Vector recognize(
        CognitiveModuleState state,
        Vector inputs)
    {
        if ( state == null || !(state instanceof SimplePatternRecognizerState) )
        {
            throw new IllegalArgumentException("Invalid state.");
        }
        
        SimplePatternRecognizerState recognizerState = 
            (SimplePatternRecognizerState) state;
        
        Vector stateVector = recognizerState.getStateVector();
        
        // If we've changed the underlying network, or the size of the
        // state vector has changed, then we need to rebuild the map
        // and create a new state vector.
        if ( this.nodesChangedSinceLastUpdate || 
             stateVector.getDimensionality() != this.getInputDimensionality() )
        {
            this.nodesChangedSinceLastUpdate = false;
            
            Vector oldState = stateVector;
            Vector newState = this.createEmptyInputVector();
            stateVector = newState;
            
            List<SemanticLabel> oldLabels = recognizerState.getLabels();
            int numOldLabels = oldLabels.size();
            for (int oldIndex = 0; oldIndex < numOldLabels; oldIndex++)
            {
                int newIndex = this.getIndex(oldLabels.get(oldIndex));
                
                if ( newIndex >= 0 )
                {
                    double value = oldState.getElement(oldIndex);
                    newState.setElement(newIndex, value);
                }
            }
            
            recognizerState.setLabels(this.nodes);
            recognizerState.setStateVector(newState);
        }
        
        // Let the recognition equation be:
        //      x(n+1) = A*x(n) + u(n)
        // Note that this is a dynamical system with an identity control-gain
        // matrix (feedforward), where the inputs are the same dimension are the
        // state.
        Vector result = this.matrix.times(stateVector);
        result.plusEquals(inputs);
        
        recognizerState.setStateVector(result);
        
        return result;
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  label {@inheritDoc}
     */
    public void addNode(
        SemanticLabel label)
    {
        int index = this.getIndex(label);
        
        if ( index >= 0 )
        {
            return;
        }
        
        index = this.nodes.size();
        this.nodes.add(label);
        this.nodeToIDMap.put(label, index);
        int numNodes = index + 1;
        
        Matrix newMatrix = MatrixFactory.getDefault().createMatrix(numNodes, numNodes);
        newMatrix.setSubMatrix(0, 0, this.matrix);
        this.setMatrix(newMatrix);
        this.setNodesChangedSinceLastUpdate(true);
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  label {@inheritDoc}
     */
    public void removeNode(
        SemanticLabel label)
    {
        // Get the index of the label.
        int index = this.getIndex(label);
        if ( index < 0 )
        {
            // The node is not in the recognizer, so there is nothing to do.
            return;
        }
        
        // Remove the node and rebuild the ID map.
        int oldNumNodes = this.nodes.size();
        this.nodes.remove(index);
        this.buildNodeToIDMap();
        
        // Remove the row and column for the node from the matrix.
        int newNumNodes = oldNumNodes - 1;
        Matrix oldMatrix = this.matrix;
        Matrix newMatrix =
            MatrixFactory.getDefault().createMatrix(newNumNodes, newNumNodes);
        this.matrix = newMatrix;
        
        // Copy over the matrix values.
        for (int oldI = 0; oldI < oldNumNodes; oldI++)
        {
            int newI = oldI;
            if ( oldI == index )
            {
                // This is the row we ignore.
                continue;
            }
            else if ( oldI > index )
            {
                newI = oldI - 1;
            }
            
            for (int oldJ = 0; oldJ < oldNumNodes; oldJ++)
            {
                int newJ = oldJ;
                
                 if ( oldJ == index )
                 {
                    // This is the column we ignore.
                    continue;
                 }
                 else if ( oldJ > index )
                 {
                    newJ = oldJ - 1;
                 }
                 
                 newMatrix.setElement(newI, newJ, 
                     oldMatrix.getElement(oldI, oldJ));
            }
        }
        
        this.setNodesChangedSinceLastUpdate(true);
    }
    
    /**
     * Gets the association between two nodes in the network.
     *
     * @param  from The label the association is from
     * @param  to The label the association is to.
     * @return The association weight between the two labels or zero if
     *         one of the labels is not in the recognizer.
     */
    public double getAssociation(
        SemanticLabel from,
        SemanticLabel to)
    {
        int fromIndex = this.getIndex(from);
        int toIndex   = this.getIndex(to);
        
        if ( fromIndex < 0 || toIndex < 0 )
        {
            // Error: Bad labels.
            return 0.0;
        }
        else
        {
            // Return the value.
            return this.matrix.getElement(toIndex, fromIndex);
        }
    }
    
     /**
      * {@inheritDoc}
      *
      * @param  from {@inheritDoc}
      * @param  to {@inheritDoc}
      * @param  weight {@inheritDoc}
      */
    public void setAssociation(
        SemanticLabel from,
        SemanticLabel to,
        double weight)
    {
        int fromIndex = this.getIndex(from);
        int toIndex   = this.getIndex(to);
        
        if ( fromIndex < 0 || toIndex < 0 )
        {
            // Error: Bad labels.
            return;
        }
        
        this.matrix.setElement(toIndex, fromIndex, weight);
    }
    
    /**
     * Gets the index in the vector of the given label.
     *
     * @param  label The label to get the index of.
     * @return The index assigned to the label or -1 if the label is
     *         not in the recognizer.
     */
    public int getIndex(
        SemanticLabel label)
    {
        Integer index = this.nodeToIDMap.get(label);
        
        if ( index == null )
        {
            return -1;
        }
        else
        {
            return index;
        }
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  label {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean isLabel(
        SemanticLabel label)
    {
        return this.nodeToIDMap.containsKey(label);
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  label {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean isInputLabel(
        SemanticLabel label)
    {
        return this.isLabel(label);
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  label {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean isOutputLabel(
        SemanticLabel label)
    {
        return this.isLabel(label);
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public Collection<SemanticLabel> getAllLabels()
    {
        return this.nodes;
    }

    /**
     * {@inheritDoc}
     *
     * @param  label {@inheritDoc}
     * @param  inputLabel {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean trySetInputLabel(
        SemanticLabel label, 
        boolean inputLabel)
    {
        // Labels must always be input labels.
        return inputLabel && this.isLabel(label);
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  label {@inheritDoc}
     * @param  outputLabel {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean trySetOutputLabel(
        SemanticLabel label, 
        boolean outputLabel)
    {
        // Labels must always be output labels.
        return outputLabel && this.isLabel(label);
    }
    
    /**
     * Builds the map of nodes to identifiers from the list of nodes.
     */
    protected void buildNodeToIDMap()
    {
        this.setNodeToIDMap(new HashMap<SemanticLabel, Integer>());
        
        int numNodes = this.nodes.size();
        // Map the nodes to their index into the vector.
        for ( int i = 0; i < numNodes; i++)
        {
            this.nodeToIDMap.put(this.nodes.get(i), i);
        }
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public Vector createEmptyInputVector()
    {
        return VectorFactory.getDefault().createVector(this.getInputDimensionality());
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public int getInputDimensionality()
    {
        return this.nodes.size();
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public int getOutputDimensionality()
    {
        return this.nodes.size();
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public Collection<SemanticLabel> getInputLabels()
    {
        return this.nodes;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public Collection<SemanticLabel> getOutputLabels()
    {
        return this.nodes;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public SemanticNetwork getNetwork()
    {
        return new SimplePatternRecognizer.Network();
    }

    /**
     * Sets the nodes in the recognizer.
     *
     * @param  nodes The new list of nodes.
     */
    protected void setNodes(
        ArrayList<SemanticLabel> nodes)
    {
        this.nodes = nodes;
    }

    /**
     * Sets the mapping of nodes to their vector indices.
     *
     * @param  nodeToIDMap The new mapping of nodes to vector indices.
     */
    protected void setNodeToIDMap(
        HashMap<SemanticLabel, Integer> nodeToIDMap)
    {
        this.nodeToIDMap = nodeToIDMap;
    }

    /**
     * Sets the underlying matrix.
     *
     * @param  matrix The new underlying matrix.
     */
    protected void setMatrix(
        Matrix matrix)
    {
        this.matrix = matrix;
    }

    /**
     * Sets whether or not the nodes have changed since the last update.
     *
     * @param  nodesChangedSinceLastUpdate Whether or not the nodes have
     *         changed since the last update.
     */
    private void setNodesChangedSinceLastUpdate(
        boolean nodesChangedSinceLastUpdate)
    {
        this.nodesChangedSinceLastUpdate = nodesChangedSinceLastUpdate;
    }
    
    /**
     * The <code>Network</code> inner class is an implementation of the
     * SemanticNetwork interface that pulls its data off of the
     * SimplePatternRecognizer.
     *
     * @author Justin Basilico
     * @since  1.0
     */
    private class Network
        extends java.lang.Object
        implements SemanticNetwork
    {
        /**
         * Creates a new instance of Network.
         */
        private Network()
        {
            super();
        }
        
        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        public int getNumNodes()
        {
            return nodes.size();
        }
        
        /**
         * {@inheritDoc}
         *
         * @param  label {@inheritDoc}
         * @return {@inheritDoc}
         */
        public boolean isNode(
            SemanticLabel label)
        {
            return nodes.contains(label);
        }
        
        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         */
        public Collection<SemanticLabel> getNodes()
        {
            return nodes;
        }
        
        /**
         * {@inheritDoc}
         *
         * @param  nodeLabel {@inheritDoc}
         * @return {@inheritDoc}
         */
        public Collection<SemanticLabel> getOutLinks(
            SemanticLabel nodeLabel)
        {
            // First see if the node is in the network at all.
            Integer nodeIDInteger = nodeToIDMap.get(nodeLabel);
            
            if ( nodeIDInteger == null )
            {
                // Not in the network.
                return null;
            }
            
            // We are going to return a list of the labels that the node is
            // connected to.
            ArrayList<SemanticLabel> result = new ArrayList<SemanticLabel>();
            
            // Convert the node ID to an integer.
            int nodeID = nodeIDInteger.intValue();
// TO DO: Change this to make use of sparseness once it is implemented.
            int numNodes = this.getNumNodes();
            for (int j = 0; j < numNodes; j++)
            {
                // Get the connection to the other node.
                double value = matrix.getElement(j, nodeID);
                if ( value != 0.0 )
                {
                    // The connection weight is 
                    result.add(nodes.get(j));
                }
            }
            
            return result;
        }
        
        /**
         * {@inheritDoc}
         *
         * @param  from {@inheritDoc}
         * @param  to {@inheritDoc}
         * @return {@inheritDoc}
         */
        public double getAssociation(
            SemanticLabel from,
            SemanticLabel to)
        {
            Integer fromID = nodeToIDMap.get(from);
            Integer toID   = nodeToIDMap.get(to);
            
            if ( fromID == null || toID == null )
            {
                return 0.0;
            }
            else
            {
                return matrix.getElement(toID, fromID);
            }
        }
    }
}
