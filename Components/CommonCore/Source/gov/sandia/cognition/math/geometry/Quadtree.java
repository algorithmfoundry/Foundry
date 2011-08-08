/*
 * File:                Quadtree.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 16, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.geometry;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.collection.DefaultMultiCollection;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Implements the quadtree region-partitioning algorithm and data structure. The 
 * quadtree works on two-dimensional data by building a tree over the data. 
 * Each node in the tree represents a square region of the data. Each interior 
 * node contains four children, corresponding to the four equal-sized quadrants 
 * of the node (hence the name quadtree). All of the data items are contained 
 * at the leaves of the tree. The algorithm maintains a threshold of the 
 * maximum number of items allowed in a leaf node. If a node exceeds that 
 * limit, then it is split into its four quadrants.
 * 
 * @param   <DataType> The type of data that is to be stored in the tree. It
 *      must be able to be converted to a two-dimensional vector via the
 *      {@code Vectorizable} interface.
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments={
        "Made Quadtree and Node extend AbstractCloneableSerializable",
        "Otherwise, class looks great!"
    }
)
public class Quadtree<DataType extends Vectorizable>
    extends AbstractCloneableSerializable
{
    /**
     * This is the default minimum number of items allowed in a leaf node,
     * {@value}.
     */
    public static final int DEFAULT_SPLIT_THRESHOLD = 10;
    
    /** The minimum number of items allowed in a leaf node. If there are more
     *  than this, then a node must be split. This number must be greater than
     *  zero. */
    protected int splitThreshold;
    
    /** All of the items in the tree. It should never be null. */
    protected LinkedList<DataType> items;
    
    /** The initial bounds for the tree. This may be null if they are not 
     *  specified. */
    protected Rectangle2D.Double initalBounds;
    
    /** The root node of the tree. It should never be null. */
    protected Node root;
    
    /**
     * Creates a new, empty {@code Quadtree}.
     */
    public Quadtree()
    {
        this(DEFAULT_SPLIT_THRESHOLD);
    }
    
    /**
     * Creates a new, empty {@code Quadtree} with the given split threshold.
     * 
     * @param   splitThreshold
     *      The maximum number of items allowed in a tree leaf node before
     *      it is split. Must be positive.
     */
    public Quadtree(
        final int splitThreshold)
    {
        this(splitThreshold, (Rectangle2D.Double) null);
    }
    
    /**
     * Creates a new, empty {@code Quadtree} with the given initial bounds.
     * 
     * @param   initialBounds The initial bounds for the quadtree.
     */
    public Quadtree(
        final Rectangle2D.Double initialBounds)
    {
        this(DEFAULT_SPLIT_THRESHOLD, initialBounds);
    }
    
    /**
     * Creates a new, empty {@code Quadtree} with the given split threshold.
     * 
     * @param   splitThreshold
     *      The maximum number of items allowed in a tree leaf node before
     *      it is split. Must be positive.
     * @param   initialBounds 
     *      The initial bounds for the quadtree.
     */
    public Quadtree(
        final int splitThreshold,
        final Rectangle2D.Double initialBounds)
    {
        super();
        
        this.items = new LinkedList<DataType>();
        
        this.root = new Node(null, null);
        
        this.setSplitThreshold(splitThreshold);
    }
    
    /**
     * Creates a new {@code Quadtree}, populating it with the given items.
     * 
     * @param  items The initial items to populate the tree with.
     */
    public Quadtree(
        final Collection<? extends DataType> items)
    {
        this(DEFAULT_SPLIT_THRESHOLD, items);
    }
    
    /**
     * Creates a new {@code Quadtree}, populating it with the given items.
     * 
     * @param   splitThreshold
     *      The maximum number of items allowed in a tree leaf node before
     *      it is split. Must be positive.
     * @param  items The initial items to populate the tree with.
     */
    public Quadtree(
        final int splitThreshold,
        final Collection<? extends DataType> items)
    {
        this(splitThreshold);
        
        this.items.addAll(items);
        this.rebuild();
    }
    
    /**
     * Adds an item to the tree. If the item is outside the current bounds of
     * the tree, it will rebuild the tree to fit the new item.
     * 
     * @param   item The item to add to the tree.
     */
    public void add(
        final DataType item)
    {
        final Vector2 point = this.convertTo2D(item);
        
        // Add the item to the total list of items.
        this.items.add(item);
        
        if (!this.root.boundsContain(point))
        {
            // Point is not in bounds, so the entire tree needs to be rebuilt
            // to handle the new bounds.
            this.rebuild();
        }
        else
        {
            // Find the closest matching node.
            final Node node = this.find(point);
            
            // Add the point to the node.
            node.getLocalItems().add(item);
            
            // Split the node if necessary.
            if (this.shouldSplit(node))
            {
                this.split(node);
            }
        }
    }
    
    /**
     * Adds all the items to the 
     * 
     * @param   newItems The new items to add to the tree.
     */
    public void addAll(
        final Collection<? extends DataType> newItems)
    {
        // See if all the items are contained in the current bounding box.
        boolean containsAll = true;
        for (DataType item : newItems)
        {
            // This item is contained.
            if (!this.root.isInBounds(item))
            {
                containsAll = false;
                break;
            }
        }
        
        if (!containsAll)
        {
            // The current tree does not contain all the items. Add them to
            // the list of items in the tree and rebuild it all.
            this.items.addAll(newItems);
            this.rebuild();
        }
        else
        {
            // They are all contained in the bounds of the current tree, so add
            // them one at a time.
            for (DataType item : newItems)
            {
                this.add(item);
            }
        }
    }
    
    /**
     * Rebuilds the entire quadtree. It destroys the current root node and then
     * repopulates the tree from the current list of items for the tree.
     */
    protected void rebuild()
    {
        this.root = null;
        
        // Compute the quadtree bounds.
        Rectangle2D.Double bounds = this.computeBounds(this.items);
        
        // Create the root node.
        this.root = new Node(null, bounds);
        this.root.getLocalItems().addAll(this.items);
        
        // Start the splitting of the root node, if required.
        if (this.shouldSplit(this.root))
        {
            this.split(this.root);
        }
    }
    
    /**
     * Computes the bounding rectangle of a given collection of points. This
     * takes into account the initial bounds of the quadtree, fi they are specified.
     * 
     * @param   items The items to compute the bounds for.
     * @return  The minimum bounding rectangle for the given items.
     */
    protected Rectangle2D.Double computeBounds(
        final Collection<? extends DataType> items)
    {
        // Start with the initial bounds.
        Rectangle2D.Double bounds = this.initalBounds;
        
        // Go through all the items and compute their bounds.
        for (DataType item : this.items)
        {
            final Vector2 point = this.convertTo2D(item);
            
            if (bounds == null)
            {
                // This is the first item, so initialize the bounds.
                bounds = new Rectangle2D.Double(point.getX(), point.getY(),
                    0.0, 0.0);
            }
            else
            {
                bounds.add(point.getX(), point.getY());
            }
        }
        
        if (bounds != null)
        {
            // Make the bounding box a bounding square by using the larger of
            // the width and the height.
            final double size = Math.max(bounds.getWidth(), bounds.getHeight());
            bounds.width = size;
            bounds.height = size;
        }
        
        return bounds;
    }
    
    /**
     * Determines if a given node should be split. This is done according to
     * the split threshold.
     * 
     * @param   node The node to check to see if it should be split.
     * @return  True if the node should be split and false otherwise.
     */
    protected boolean shouldSplit(
        final Node node)
    {
        return node.getLocalCount() > this.splitThreshold;
    }
    
    /** 
     * Splits the given node. This is the real meat of the algorithm.
     * 
     * @param   node The node to split into its two children.
     */
    protected void split(
        final Node node)
    {
        if (node == null)
        {
            return;
        }
        else if (!node.isLeaf())
        {
            throw new IllegalArgumentException(
                "Only leaf nodes can be split");
        }
        else if (node.areLocalItemsSame())
        {
            // All the local items are the same, so can't split this node.
            return;
        }
        
        // Figure out the splits.
        final Rectangle2D.Double bounds = node.getBounds();
        final double minX = bounds.getMinX();
        final double minY = bounds.getMinY();
        final double midX = bounds.getCenterX();
        final double midY = bounds.getCenterY();
        final double splitWidth = midX - minX;
        final double splitHeight = midY - minY;
        
        // Create the children.
        node.setLowerLeft(new Node(node, 
            new Rectangle2D.Double(minX, minY, splitWidth, splitHeight)));
        node.setLowerRight(new Node(node, 
            new Rectangle2D.Double(midX, minY, splitWidth, splitHeight)));
        node.setUpperLeft(new Node(node, 
            new Rectangle2D.Double(minX, midY, splitWidth, splitHeight)));
        node.setUpperRight(new Node(node, 
            new Rectangle2D.Double(midX, midY, splitWidth, splitHeight)));
        
        // Build the list of children.
        final ArrayList<Node> children = new ArrayList<Node>(4);
        children.add(node.getLowerLeft());
        children.add(node.getLowerRight());
        children.add(node.getUpperLeft());
        children.add(node.getUpperRight());
        node.setChildren(children);
        
        // Go through all the items and add to the children.
        for (DataType item : node.getLocalItems())
        {
            final Node child = node.findChild(item);
            child.localItems.add(item);
        }
        
        // Apply the changes to the node, transforming it from a leaf node to
        // an interior node.
        node.localItems.clear();

        // Now see if the children should be split.
        for (Node child : children)
        {
            if (this.shouldSplit(child))
            {
                this.split(child);
            }
        }
    }
    
    /**
     * Converts the given item into a two-dimensional vector. It throws an
     * illegal argument exception
     * 
     * @param   item The item to convert to a two-dimensional vector.
     * @return  The two-dimenaional vector version of the item.
     */
    public Vector2 convertTo2D(
        final DataType item)
    {
        final Vector vector = item.convertToVector();
        if (vector.getDimensionality() != 2)
        {
            throw new IllegalArgumentException(
                "Quadtree only accepts two-dimensional data");
        }
        return new Vector2(vector);
    }
    
    /**
     * Locates the node in the tree that has the smallest bounding box that
     * contains the item.
     * 
     * @param   item The item to find the node for.
     * @return  The node with the smallest bounding box that contains the item.
     */
    public Node find(
        final DataType item)
    {
        return this.find(this.convertTo2D(item));
    }
    
    /**
     * Locates the node in the tree that has the smallest bounding box that
     * contains the point.
     * 
     * @param   point The point to find the node for.
     * @return  The node with the smallest bounding box that contains the point.
     */
    public Node find(
        final Vector2 point)
    {
        return this.find(point.getX(), point.getY());
    }
    
    /**
     * Locates the node in the tree that has the smallest bounding box that
     * contains the point.
     * 
     * @param   x The x-coordinate of the point.
     * @param   y The y-coordinate of the point.
     * @return  The node with the smallest bounding box that contains the point.
     */
    public Node find(
        final double x,
        final double y)
    {
        if (!this.boundsContain(x, y))
        {
            // Not in the bounds of the tree.
            return null;
        }
        
        // Start at the root node and keep finding the proper child until
        // we hit a leaf.
        Node node = this.root;
        while (node != null && !node.isLeaf())
        {
            node = node.findChild(x, y);
        }
        
        return node;
    }
    
    /**
     * Finds all of the items in the quadtree that are contained in the given
     * rectangle.
     * 
     * @param   rectangle The rectangle to search for.
     * @return  The items in the quad tree that fit in the given rectangle.
     */
    public LinkedList<DataType> findItems(
        final Rectangle2D rectangle)
    {
        // Start searching at the root.
        LinkedList<DataType> result = new LinkedList<DataType>();
        this.root.findItems(rectangle, result);
        return result;
    }
    
    /**
     * Finds the list of nodes that overlap with the given rectangle, chosing 
     * the highest-level nodes in the tree that are contained in the rectangle.
     * 
     * @param   rectangle The rectangle to search for.
     * @return  The list of the highest-level nodes that are contained in the
     *      given rectangle plus the leaves that intersect the rectangle.
     */
    public LinkedList<Node> findNodes(
        final Rectangle2D rectangle)
    {
        final LinkedList<Node> result = new LinkedList<Node>();
        this.findNodes(rectangle, this.root, result);
        return result;
    }
    
    /**
     * Internal find nodes implementation that accumulates the result in a
     * given list.
     * 
     * @param   rectangle The rectangle to search for.
     * @param   node The node to search in.
     * @param   result The result to accumulate the results.
     */
    private void findNodes(
        final Rectangle2D rectangle,
        final Node node,
        final LinkedList<Node> result)
    {
        if (node.boundsOverlap(rectangle))
        {
            if (node.isLeaf() || rectangle.contains(node.bounds))
            {
                // This is a leaf node or the rectangle contains this entire
                // node, so add it to the result.
                result.add(node);
            }
            else
            {
                // The entire node isn't contained, but some part overlaps, so
                // go through the children and search them.
                for (Node child : node.children)
                {
                    findNodes(rectangle, child, result);
                }
            }
        }
    }
    
    /**
     * Determines if the given point is within the bounds of the quadtree.
     * 
     * @param   item The point to determine if it is the bounds.
     * @return  True if the given point is in the bounds of the quadtree; 
     *      otherwise, false.
     */
    public boolean boundsContain(
        final DataType item)
    {
        return this.boundsContain(this.convertTo2D(item));
    }
    
    /**
     * Determines if the given point is within the bounds of the quadtree.
     * 
     * @param   point The point to determine if it is the bounds.
     * @return  True if the given point is in the bounds of the quadtree; 
     *      otherwise, false.
     */
    public boolean boundsContain(
        final Vector2 point)
    {
        return this.boundsContain(point.getX(), point.getY());
    }
    
    
    /**
     * Determines if the given point is within the bounds of the quadtree.
     * 
     * @param   x The x-coordinate of the point.
     * @param   y The y-coordinate of the point.
     * @return  True if the given point is in the bounds of the quadtree; 
     *      otherwise, false.
     */
    public boolean boundsContain(
        final double x,
        final double y)
    {
        return this.root.boundsContain(x, y);
    }
    
    /**
     * Gets the split threshold for the tree. This is the maximum number of
     * items that are allowed in a leaf node.
     * 
     * @return  The split threshold for a node in the tree.
     */
    public int getSplitThreshold()
    {
        return this.splitThreshold;
    }
    
    /**
     * Sets the split threshold for the node. If this changes threshold, then
     * the tree is rebuilt.
     * 
     * @param   splitThreshold The new split threshold. Must be positive.
     */
    public void setSplitThreshold(
        final int splitThreshold)
    {
        if (splitThreshold <= 0)
        {
            throw new IllegalArgumentException(
                "splitThreshold must be positive.");
        }
        else if (splitThreshold == this.splitThreshold)
        {
            // Nothing actually changed.
            return;
        } 
        
        this.splitThreshold = splitThreshold;
        
        // The split threshold has changed, so we need to rebuild the entire
        // tree.
        this.rebuild();
    }
    
    /**
     * Gets the root node of the quadtree.
     * 
     * @return   The root node of the quadtree.
     */
    public Node getRoot()
    {
        return this.root;
    }
    
    /**
     * Represents a node in the quadtree.
     */
    public class Node
        extends AbstractCloneableSerializable
    {
        /** The parent of this node in the tree. Null only for the root node. 
         */
        protected Node parent;
        
        /** The two-dimensional bounds for this node. This is only null if it
         *  is the root node and has no elements and no default bounds. */
        protected Rectangle2D.Double bounds;
        
        /** The depth of this node in the tree. */
        protected int depth;
        
        /** The local items stored at this node. */
        protected LinkedList<DataType> localItems;
        
        /** The child for the lower-right quadrant of this node. */
        protected Node lowerRight;
        
        /** The child for the lower-left quadrant of this node. */
        protected Node lowerLeft;
        
        /** The child for the upper-left quadrant of this node. */
        protected Node upperLeft;
        
        /** The child for the upper-right quadrant of this node. */
        protected Node upperRight;
        
        /** The list of children for this node. Null to indicate that it has
         *  no children and thus is a leaf node. */
        protected ArrayList<Node> children;
        
        /**
         * Creates a new {@code Node} with the given parent and region bounds.
         * 
         * @param   parent
         *      The parent node. Null for the root node.
         * @param bounds
         *      The bounding rectangle for the region the node represents.
         */
        public Node(
            final Node parent,
            final Rectangle2D.Double bounds)
        {
            this.setParent(parent);
            this.setBounds(bounds);
            this.setDepth(parent == null ? 0 : parent.getDepth() + 1);
            this.setLocalItems(new LinkedList<DataType>());
            this.setUpperLeft(null);
            this.setUpperRight(null);
            this.setLowerLeft(null);
            this.setLowerRight(null);
            this.setChildren(null);
        }
        
        /**
         * Returns true if the given point is within the bounds of this node.
         * 
         * @param   item The point.
         * @return  True if-and-only-if the point is in the bounds of this node.
         */
        public boolean isInBounds(
            final DataType item)
        {
            return boundsContain(convertTo2D(item));
        }
        
        /**
         * Returns true if the given point is within the bounds of this node.
         * 
         * @param   point The point.
         * @return  True if-and-only-if the point is in the bounds of this node.
         */
        public boolean boundsContain(
            final Vector2 point)
        {
            return this.boundsContain(point.getX(), point.getY());
        }
        
        /**
         * Returns true if the given point is within the bounds of this node.
         * 
         * @param   point The point.
         * @return  True if-and-only-if the point is in the bounds of this node.
         */
        public boolean boundsContain(
            final Point2D point)
        {
            return this.boundsContain(point.getX(), point.getY());
        }
        
        /**
         * Returns true if the given point is within the bounds of this node.
         * 
         * @param   x The x-coordinate of the point.
         * @param   y The y-coordinate of the point.
         * @return  True if-and-only-if the point is in the bounds of this node.
         */
        public boolean boundsContain(
            final double x,
            final double y)
        {
            // Note: This does not call the method on the Rectangle2D since it
            // does not allow things at the bounds to return true for being
            // contained in the rectangle.
            return this.bounds != null
                && x >= this.bounds.getMinX() && y >= this.bounds.getMinY() 
                && x <= this.bounds.getMaxX() && y <= this.bounds.getMaxY();
        }
        
        /**
         * Returns true if the given rectangle is completely within the bounds 
         * of this node.
         * 
         * @param   rectangle The rectangle to test.
         * @return  True if-and-only-if the given rectangle is within the bounds
         *      of this rectangle.
         */
        public boolean boundsContain(
            final Rectangle2D rectangle)
        {
            // Note: This does not call the method on the Rectangle2D since it
            // does not allow things at the bounds to return true for being
            // contained in the rectangle.
            return this.bounds != null
                && rectangle.getMinX() >= this.bounds.getMinX()
                && rectangle.getMinY() >= this.bounds.getMinY()
                && rectangle.getMaxX() <= this.bounds.getMaxX()
                && rectangle.getMaxY() <= this.bounds.getMaxY();
        }
        
        /**
         * Returns true if the given rectangle intersects the bounds for this
         * node.
         * 
         * @param   rectangle The rectangle to test.
         * @return  True if-and-only-if the given rectangle intersects with
         *      the bounds of this node.
         */
        public boolean boundsOverlap(
            final Rectangle2D rectangle)
        {
            return this.bounds.intersects(rectangle);
        }
        
        /**
         * Finds the child corresponding to the given point. Note that if the
         * point is outide the bounds of the node, it will still return a child
         * node, because it just compares to the splitting planes in the node
         * for efficiency.
         * 
         * @param   item The item to query.
         * @return  The node that most closely matches the given point.
         */
        public Node findChild(
            final DataType item)
        {
            return this.findChild(convertTo2D(item));
        }
        
        /**
         * Finds the child corresponding to the given point. Note that if the
         * point is outide the bounds of the node, it will still return a child
         * node, because it just compares to the splitting planes in the node
         * for efficiency.
         * 
         * @param   point The point to query.
         * @return  The node that most closely matches the given point.
         */
        public Node findChild(
            final Vector2 point)
        {
            return this.findChild(point.getX(), point.getY());
        }
            
        /**
         * Finds the child corresponding to the given point. Note that if the
         * point is outide the bounds of the node, it will still return a child
         * node, because it just compares to the splitting planes in the node
         * for efficiency.
         * 
         * @param   x The x-coordinate to query.
         * @param   y The y-coordinate to query.
         * @return  The node that most closely matches the given point.
         */
        public Node findChild(
            final double x,
            final double y)
        {
            if (this.isLeaf())
            {
                // The node has no children.
                return null;
            }
            else if (x <= this.bounds.getCenterX())
            {
                if (y <= this.bounds.getCenterY())
                {
                    return this.lowerLeft;
                }
                else
                {
                    return this.upperLeft;
                }
            }
            else
            {
                if (y <= this.bounds.getCenterY())
                {
                    return this.lowerRight;
                }
                else
                {
                    return this.upperRight;
                }
            }
        }
        
        /**
         * Finds all of the items that fall within the region defined by this
         * node (and its children) and adds it to the given list.
         * 
         * @param   rectangle The rectangle to search in.
         * @param   result The result list to put the items in.
         */
        public void findItems(
            final Rectangle2D rectangle,
            final LinkedList<DataType> result)
        {
            // First see if the rectangle overlaps this node.
            if (this.boundsOverlap(rectangle))
            {
                if (rectangle.contains(this.bounds))
                {
                    // If the bounds are contained in the rectangle, add
                    // all the items in this node.
                    result.addAll(this.getItems());
                }
                else if (this.isLeaf())
                {
                    // This is a leaf node so test all of the items in the node
                    // to see if they fall in the rectangle.
                    for (DataType item : this.localItems)
                    {
                        final Vector2 point = convertTo2D(item);
                        if (rectangle.contains(point.getX(), point.getY()))
                        {
                            result.add(item);
                        }
                    }
                }
                else
                {
                    // Go through the child nodes.
                    for (Node child : this.children)
                    {
                        child.findItems(rectangle, result);
                    }
                }
            }
            // else - The bounds of this node do not overlap.
        }
        
        /**
         * Returns true if this is a leaf and all the local items are the same.
         * 
         * @return  True if the local items are the same.
         */
        public boolean areLocalItemsSame()
        {
            if (!this.isLeaf())
            {
                // No local items.
                return false;
            }
            else if (this.getLocalCount() <= 1)
            {
                // Only one local items, so they're all the same.
                return true;
            }
            
            // Get the first and then check to see if the rest are equal.
            final Vector2 first = convertTo2D(this.localItems.getFirst());
            for (DataType item : this.localItems)
            {
                final Vector2 vector = convertTo2D(item);
                if (!first.equals(vector))
                {
                    // Found one that is not equal.
                    return false;
                }
            }
            
            // They are all equal.
            return true;
        }
        
        
        /**
         * Gets the collection of items contained in this node and all subnodes.
         * 
         * @return  The collection of items contained in this node and all
         *      subnodes.
         */
        public Collection<DataType> getItems()
        {
            if (this.isLeaf())
            {
                return this.localItems;
            }
            else
            {
                // See if there is a more efficient way to do this. (jdbasil)
                // I couldn't think of one, so I removed the task.
                return new DefaultMultiCollection<DataType>(
                    new DefaultMultiCollection<DataType>(
                        this.lowerLeft.getItems(),
                        this.lowerRight.getItems()),
                    new DefaultMultiCollection<DataType>(
                        this.upperLeft.getItems(),
                        this.upperRight.getItems()));
            }
        }
        
        /**
         * Gets the children of this node.
         * 
         * @return  The children of this node.
         */
        public List<Node> getChildren()
        {
            if (this.children == null)
            {
                return Collections.emptyList();
            }
            else
            {
                return this.children;
            }
        }
        
        /**
         * Returns true if this is a leaf node and has no items in it.
         * 
         * @return  True if this is a leaf node and has no items in it.
         */
        public boolean isEmpty()
        {
            return this.isLeaf() && this.getLocalCount() <= 0;
        }
        
        /**
         * Returns true if this node is a leaf node, which means it has no
         * children.
         * 
         * @return  True if-and-only-if this is a leaf node.
         */
        public boolean isLeaf()
        {
            return this.children == null;
        }
        
        /**
         * Gets the number of items that are locally contained at the node. 
         * This does not count items at child nodes. For non-leaf nodes, this
         * should be zero.
         * 
         * @return  The number of items locally at the node.
         */
        public int getLocalCount()
        {
            return this.getLocalItems().size();
        }
        
        /**
         * The depth in the tree that this node exists at. The root starts at
         * depth 0.
         * 
         * @return  The depth in the tree of this node.
         */
        public int getDepth()
        {
            return this.depth;
        }

        /**
         * Sets the depth in the tree of this node.
         * 
         *  @param  depth   The new depth in the tree for the node.
         */
        protected void setDepth(
            final int depth)
        {
            this.depth = depth;
        }
        
        /**
         * Gets the parent node of this node. This is only null if it is a root
         * node.
         * 
         * @return The parent node of this node.
         */
        public Node getParent()
        {
            return this.parent;
        }

        /**
         * Sets the parent node of this node. It should only be null if this is
         * the root node.
         * 
         * @param   parent The parent node of this node.
         */
        protected void setParent(
            final Node parent)
        {
            this.parent = parent;
        }
        
        /**
         * Gets the bounding box of the region represented by this node.
         * 
         * @return  The bounding box of the region represented by this node.
         */
        public Rectangle2D.Double getBounds()
        {
            return this.bounds;
        }

        
        /**
         * Sets the bounding box of the region represented by this node.
         * 
         * @param bounds 
         *      The bounding box of the region represented by this node.
         */
        protected void setBounds(
            final Rectangle2D.Double bounds)
        {
            this.bounds = bounds;
        }
        
        /**
         * Gets the list of items stored locally at the node in the tree.
         * 
         * @return  The local items stored at the node.
         */
        public LinkedList<DataType> getLocalItems()
        {
            return this.localItems;
        }
        
        /**
         * Gets the list of items stored locally at the node in the tree.
         * 
         * @param   localItems The local items stored at the node.
         */
        protected void setLocalItems(
            final LinkedList<DataType> localItems)
        {
            this.localItems = localItems;
        }

        /**
         * Gets the child representing lower-left quadrant of the node, when
         * treating the 2D coordinate grid in a mathematical representation
         * (positive x values go left and positive y values go up).
         * 
         * @return  The child representing the lower-left quadrant of the node.
         */
        public Node getLowerLeft()
        {
            return this.lowerLeft;
        }
        
        /**
         * Sets the lower-left child.
         * 
         * @param   lowerLeft The lower-left child.
         */
        protected void setLowerLeft(
            final Node lowerLeft)
        {
            this.lowerLeft = lowerLeft;
        }

        /**
         * Gets the child representing lower-right quadrant of the node, when
         * treating the 2D coordinate grid in a mathematical representation
         * (positive x values go left and positive y values go up).
         * 
         * @return  The child representing the lower-right quadrant of the node.
         */
        public Node getLowerRight()
        {
            return this.lowerRight;
        }
        
        /**
         * Sets the lower-right child.
         * 
         * @param   lowerRight The lower-right child.
         */
        protected void setLowerRight(
            final Node lowerRight)
        {
            this.lowerRight = lowerRight;
        }
        
        /**
         * Gets the child representing upper-left quadrant of the node, when
         * treating the 2D coordinate grid in a mathematical representation
         * (positive x values go left and positive y values go up).
         * 
         * @return  The child representing the upper-left quadrant of the node.
         */
        public Node getUpperLeft()
        {
            return this.upperLeft;
        }

        /**
         * Sets the upper-left child.
         * 
         * @param   upperLeft The upper-left child.
         */
        protected void setUpperLeft(
            final Node upperLeft)
        {
            this.upperLeft = upperLeft;
        }

        /**
         * Gets the child representing upper-right quadrant of the node, when
         * treating the 2D coordinate grid in a mathematical representation
         * (positive x values go left and positive y values go up).
         * 
         * @return  The child representing the upper-right quadrant of the node.
         */
        public Node getUpperRight()
        {
            return this.upperRight;
        }

        /**
         * Sets the upper-right child.
         * 
         * @param   upperRight The upper-right child.
         */
        protected void setUpperRight(
            final Node upperRight)
        {
            this.upperRight = upperRight;
        }

        /**
         * Sets the list of child nodes of this node. It should only be the
         * four defined children that have pointers.
         * 
         * @param   children The list of child nodes of this node.
         */
        protected void setChildren(
            final ArrayList<Node> children)
        {
            this.children = children;
        }

        
    }
}
