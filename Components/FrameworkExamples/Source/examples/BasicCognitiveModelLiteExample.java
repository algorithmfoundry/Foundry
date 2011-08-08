/*
 * File:                BasicCognitiveModelLiteExample.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package examples;

import gov.sandia.cognition.framework.*;
import gov.sandia.cognition.framework.lite.*;

/**
 * This file contains example code for how to build and use a simple Cognitive
 * Model Lite programmatically. We use the simple example of driving a car where
 * we use brake, left blinker, and right blinker as inputs to determine if the
 * car is stopping or about to turn.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class BasicCognitiveModelLiteExample
{
    /**
     * This is the main method where execution begins.
     *
     * @param  arguments The command-line arguments.
     */
    public static void main(
        String[] arguments)
    {
        // Step 1: We need to create a semantic network that describes how our
        // labeled semantic entities are connected.

        // Step 1.1: Create a DefaultSemanticNetwork, which is a default
        // implementation of the SemanticNetwork interface that lets you build
        // up the network by adding labels to it.
        DefaultSemanticNetwork semanticNetwork = new DefaultSemanticNetwork();

        // Step 1.2: Create DefaultSemanticLabels for the semantic entities that
        // we want to put in our semantic network.
        SemanticLabel brake = new DefaultSemanticLabel("Brake");
        SemanticLabel leftBlinker = new DefaultSemanticLabel("Left Blinker");
        SemanticLabel rightBlinker = new DefaultSemanticLabel("Right Blinker");

        SemanticLabel turning = new DefaultSemanticLabel("Turning");
        SemanticLabel stopping = new DefaultSemanticLabel("Stopping");

        // Step 1.3: Add them to the network.
        semanticNetwork.addNode(brake);
        semanticNetwork.addNode(leftBlinker);
        semanticNetwork.addNode(rightBlinker);

        semanticNetwork.addNode(turning);
        semanticNetwork.addNode(stopping);

        // Step 1.4: Now connect the semantic labels together in the network
        // to show how they are associated.
        semanticNetwork.setAssociation(brake, turning, 0.2);
        semanticNetwork.setAssociation(brake, stopping, 1.0);
        semanticNetwork.setAssociation(leftBlinker, turning, 1.0);
        semanticNetwork.setAssociation(rightBlinker, turning, 1.0);


        // Step 2: With the SemanticNetwork created, we now need to build a
        // CognitiveModelFactory to create our CognitiveModel from. To do this
        // we need to build CognitiveModuleFactory objects for each of the
        // modules we want to install in the model.

        // Step 2.1: Create the CognitiveModelLiteFactory that we will add the
        // modules to.
        CognitiveModelLiteFactory modelFactory = new CognitiveModelLiteFactory();

        // Step 2.2: The first module installed in a model should be a
        // perception module, which handles the conversion of an input into
        // activations that the other modules can deal with. For this example
        // we use a simple ArrayBasedPerceptionModule since it allows us to
        // directly give the model the inputs we want. However, a custom
        // perception module can be written that takes any sort of external
        // perception data and converts it into activations that the model can
        // use.
        modelFactory.addModuleFactory(new ArrayBasedPerceptionModuleFactory());

        // Step 2.3: Now that we have a perception module we can instantiate
        // our SemanticNetwork in a SemanticMemory module. In order to do this
        // we first need to create a PatternRecognizerLite for our network,
        // which implements the core pattern recognition algorithms for the
        // SharedSemanticMemoryLite. We use the SimplePatternRecognizer
        // implementation as a stub. Once it is finished, a psychologically
        // plausible implementation should be used instead.
        PatternRecognizerLite recognizer =
            new SimplePatternRecognizer(semanticNetwork);
        modelFactory.addModuleFactory(
            new SharedSemanticMemoryLiteFactory(recognizer));

        // Step 3: Create the model from the factory.
        CognitiveModelLite model = modelFactory.createModel();

        // Step 4: Giving an input to the model.

        // Step 4.1: Since we are using the ArrayBasedPerceptionModule, in
        // order to drive the model we need to provide it with an
        // ArrayBasedCognitiveModelInput, which in turn requires us to
        // convert our SemanticLabels into SemanticIdentifiers.
        // SemanticIdentifiers provide the model with a faster way of
        // dealing with SemanticLables by assigning each one a unique
        // identifier. We convert SemanticLabels to SemanticIdentifiers using
        // the SemanticIdentifierMap that is on the model.
        SemanticIdentifierMap idMap = model.getSemanticIdentifierMap();

        SemanticIdentifier brakeID = idMap.addLabel(brake);
        SemanticIdentifier leftBlinkerID = idMap.addLabel(leftBlinker);
        SemanticIdentifier rightBlinkerID = idMap.addLabel(rightBlinker);

        SemanticIdentifier turningID = idMap.addLabel(turning);
        SemanticIdentifier stoppingID = idMap.addLabel(stopping);

        // Step 4.2: Now that we have the SemanticIdentifiers, we need to
        // create a ArrayBasedCognitiveModelInput with the activations that
        // we are going to give.

        // Here we are giving the brake a slight activation and the left
        // blinker an activation of 1.0 and the right blinker an activation
        // of 0.0. Note that we could leave out the right blinker and it would
        // get no activation.
        SemanticIdentifier[] identifiers =
            new SemanticIdentifier[] { brakeID, leftBlinkerID, rightBlinkerID };
        double[] inputValues = new double[] { 0.2, 1.0, 0.0 };
        ArrayBasedCognitiveModelInput input =
            new ArrayBasedCognitiveModelInput(identifiers, inputValues);

        // Step 4.3: With the input created we now pass it into the update
        // method on the model in order to update the state of the model.
        // Note that we need to update twice - the first time initializes
        // the model, while the second time performs the first actual state 
        // update. Note that the number of updates to propogate the input
        // all the way through the model depends on the number of layers
        // in the model.
        model.update(input);
        model.update(input);

        // Step 5: Getting output from the model.

        // Step 5.1: To get output from the model we look at the state of
        // the model.
        CognitiveModelState state = model.getCurrentState();

        // Step 5.2: The activations of the semantic entities in the state
        // are called Cogxels and are stored in the CogxelState in side the
        // CognitiveModelState.
        CogxelState cogxels = state.getCogxels();

        // Step 5.3: The activations for the semantic entities in the
        // CogxelState are stored in Cogxels. Cogxels can be retrieved directly
        // to get their activations (and any other information that you want
        // to associate with them through a CogxelFactory). However, we will
        // make use of a convenience method for getting the activation of a
        // Cogxel through the state.
        double turningActivation = cogxels.getCogxelActivation(turningID);
        double stoppingActivation = cogxels.getCogxelActivation(stoppingID);

        // Here we look at the activations to determine if we are turning or
        // stopping.
        if ( turningActivation <= 0.0 && stoppingActivation <= 0.0 )
        {
            System.out.println("We are neither turning nor stopping.");
        }
        else if ( turningActivation >= stoppingActivation )
        {
            System.out.println("We are turning.");
        }
        else
        {
            System.out.println("We are stopping.");
        }

        // That's all folks!
    }
}

