/*
 * File:                RelationNetwork.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.relation;

import java.util.Set;

/**
 * An interface for a network of relations between objects.
 *
 * @param   <ObjectType> The type of the objects that the relations are between.
 *      This is also the type of the nodes in the network.
 * @param   <RelationType> The type of the relations between objects in the
 *      network. This is also the type of the edges in the network.
 * @author  Justin Basilico
 * @since   3.0
 */
public interface RelationNetwork<ObjectType, RelationType>
{

    /**
     * Gets the number of objects that the relations are between. It is the
     * number of nodes in the network.
     *
     * @return
     *      The number of objects the relations are between.
     */
    public int getObjectCount();

    /**
     * Gets the set of objects that make up the nodes in the network.
     *
     * @return
     *      The set of objects that make up the nodes in the network.
     */
    public Set<ObjectType> getObjects();

    /**
     * Determines whether or not the given object is a node in the relation
     * network.
     *
     * @param   o
     *      An object.
     * @return
     *      True if the object is a node in the relation network. Otherwise,
     *      false.
     */
    public boolean isObject(
        final Object o);

    /**
     * Determines if the network has a relation between the two given objects.
     *
     * @param   source
     *      The source object.
     * @param target
     *      The target object.
     * @return
     *      True if the source and target are in the network and there exists
     *      is a relation from the source to the target. Otherwise, false.
     */
    public boolean hasRelation(
        final ObjectType source,
        final ObjectType target);

    /**
     * Gets a relation between the source and the target.
     *
     * @param   source
     *      The source object.
     * @param target
     *      The target object.
     * @return
     *      A relation between the source and target objects, if one exists.
     *      Otherwise, false.
     */
    public RelationType getRelation(
        final ObjectType source,
        final ObjectType target);

    /**
     * Gets all the relation between the source and the target.
     *
     * @param   source
     *      The source object.
     * @param target
     *      The target object.
     * @return
     *      All the relations between the source and target objects, if they
     *      exist. Otherwise, an empty set is returned.
     */
    public Set<RelationType> getAllRelations(
        final ObjectType source,
        final ObjectType target);

    /**
     * Gets the source of a given relation.
     *
     * @param   relation
     *      A relation from the network.
     * @return
     *      The source of the relation.
     */
    public ObjectType getRelationSource(
        final RelationType relation);

    /**
     * Gets the target of a given relation.
     *
     * @param   relation
     *      A relation from the network.
     * @return
     *      The target of the relation.
     */
    public ObjectType getRelationTarget(
        final RelationType relation);

    /**
     * Gets all of the relations that involve the given object. This includes
     * both the relations where the object is the source and the relations
     * where the object is the destination.
     *
     * @param   o
     *      An object (node) from the network.
     * @return
     *      The all of the relations involving that node.
     */
    public Set<RelationType> relationsOf(
        final ObjectType o);

    /**
     * Gets all of the relations where the given object is the source.
     *
     * @param   source
     *      An object (node) from the network.
     * @return
     *      A set of all the relations where the object is the source.
     */
    public Set<RelationType> relationsFrom(
        final ObjectType source);

    /**
     * Gets all of the relations where the given object is the target.
     *
     * @param   target
     *      An object (node) from the network.
     * @return
     *      A set of all the relations where the object is the target.
     */
    public Set<RelationType> relationsTo(
        final ObjectType target);

}
