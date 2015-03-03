/*
 * File:                CategorizationTree.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright October 22, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.function.categorization.Categorizer;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The {@code CategorizationTree} class extends the {@code DecisionTree} class
 * to implement a decision tree that does categorization.
 *
 * @param  <InputType> The input type to categorize.
 * @param  <OutputType> The output category type.
 * @author Justin Basilico
 * @since  2.0
 */
public class CategorizationTree<InputType, OutputType>
    extends DecisionTree<InputType, OutputType>
    implements Categorizer<InputType, OutputType>
{
    /** The list of possible output categories. */
    protected Set<OutputType> categories;
    
    /**
     * Creates a new instance of CategorizationTree.
     */
    public CategorizationTree()
    {
        this(null, null);
    }
    
    /**
     * Creates a new instance of CategorizationTree.
     *
     * @param  rootNode The root node of the tree.
     * @param  categories The possible output categories.
     */
    public CategorizationTree(
        final DecisionTreeNode<InputType, OutputType> rootNode,
        final Set<OutputType> categories)
    {
        super(rootNode);
        
        this.setCategories(categories);
    }

    @Override
    public CategorizationTree<InputType, OutputType> clone()
    {
        final CategorizationTree<InputType, OutputType> result = (CategorizationTree<InputType, OutputType>) super.clone();
        result.categories = this.categories == null ? null : new LinkedHashSet<>(this.categories);
        return result;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @return {@inheritDoc}
     */
    public Set<OutputType> getCategories()
    {
        return this.categories;
    }
    
    /**
     * Sets the possible output categories.
     * 
     * @param categories The collection of possible output categories.
     */
    public void setCategories(
        final Set<OutputType> categories)
    {
        this.categories = categories;
    }
}
