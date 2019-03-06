# Cognitive Foundry

## About


The Cognitive Foundry is an open-source Java library for building intelligent systems with a focus on machine learning. The Foundry's development is led by Sandia National Laboratories and is released under the open-source BSD license.

The Foundry contains four primary packages: Common, Learning, Text, and Framework. Common defines many of the basic interfaces and types in the Foundry. It also includes a linear algebra package and other generally useful Java utilities. Learning contains components and algorithms for machine learning and statistics. Text contains components and algorithms for text analysis and information retrieval such as topic modeling. Framework contains a framework for building multi-level models.

The Foundry requires Java 1.8 or later.

## License

See [License.txt](License.txt) for information about the license.

## Binaries

The latest version of the Cognitive Foundry can be downloaded from the [GitHub site](https://github.com/algorithmfoundry/Foundry/releases) or [official site](http://foundry.sandia.gov/download.html). You can also get the jars from Maven Central using Maven or Ivy. The organization/groupId is gov.sandia.foundry.

Example Maven dependencies:

```xml
<dependencies>
  <dependency>
    <groupId>gov.sandia.foundry</groupId>
    <artifactId>gov-sandia-cognition-common-core</artifactId>
    <version>4.0.1</version>
  </dependency>
  <dependency>
    <groupId>gov.sandia.foundry</groupId>
    <artifactId>gov-sandia-cognition-common-data</artifactId>
    <version>4.0.1</version>
  </dependency>
  <dependency>
    <groupId>gov.sandia.foundry</groupId>
    <artifactId>gov-sandia-cognition-learning-core</artifactId>
    <version>4.0.1</version>
  </dependency>
  <dependency>
    <groupId>gov.sandia.foundry</groupId>
    <artifactId>gov-sandia-cognition-text-core</artifactId>
    <version>4.0.1</version>
  </dependency>
  <dependency>
    <groupId>gov.sandia.foundry</groupId>
    <artifactId>gov-sandia-cognition-framework-core</artifactId>
    <version>4.0.1</version>
  </dependency>
  <dependency>
    <groupId>gov.sandia.foundry</groupId>
    <artifactId>gov-sandia-cognition-framework-learning</artifactId>
    <version>4.0.1</version>
  </dependency>
</dependencies>
```

Example Ivy dependencies
```xml
<dependencies>
    <dependency org="gov.sandia.foundry" name="gov-sandia-cognition-common-core"        rev="4.0.1"/>
    <dependency org="gov.sandia.foundry" name="gov-sandia-cognition-common-data"        rev="4.0.1"/>
    <dependency org="gov.sandia.foundry" name="gov-sandia-cognition-learning-core"      rev="4.0.1"/>
    <dependency org="gov.sandia.foundry" name="gov-sandia-cognition-text-core"          rev="4.0.1"/>
    <dependency org="gov.sandia.foundry" name="gov-sandia-cognition-framework-core"     rev="4.0.1"/>
    <dependency org="gov.sandia.foundry" name="gov-sandia-cognition-framework-learning" rev="4.0.1"/>
</dependencies>
```

## Building

To compile the Foundry from source, you can use either the [Ant](http://ant.apache.org/) or [Maven](http://maven.apache.org/) build scripts.

Using Ant you can simply invoke the main build.xml file in the root directory by doing:
```
ant clean build
```

Using Maven you can invoke the pom.xml in the root directory by doing:
```
mvn clean package
```

## Modules

The Foundry has four main packages some of which have multiple modules. Each module has a jar whos ename is prefixed with ```gov-sandia-cognition-```.

  * ```common-core``` - Contains base interfaces and types, including classes for linear algebra.
  * ```common-data``` - Contains utilities for handling data.
  * ```learning-core``` - Contains algorithms and components for machine learning and statistics.
  * ```text-core``` - Contains algorithms and components for text analysis and Information Retrieval.
  * ```graph-core``` - Contains algorithms and components for graphs.
  * ```framework-core``` - Contains a framework for multi-level models.
  * ```framework-learning``` - Contains adapters for Machine Learning in the framework.

## Example Code

Each of the four main packages has example code for how to get started with some of the basic components.

### Common Package Examples
  * [Evaluator Example](Components/CommonExamples/Source/examples/EvaluatorExample.java)
  * [Matrix and Vector Example](Components/CommonExamples/Source/examples/MatrixAndVectorExample.java)
  * [XStream Serialization Handler Example](Components/CommonExamples/Source/examples/XStreamSerializationHandlerExample.java)

### Learning Package Examples
  * [Learning Experiment Example](Components/LearningExamples/Source/examples/LearningExperimentExample.java)
  * [K-Means Example](Components/LearningExamples/Source/examples/SimpleKMeansExample.java)
  * [Custom Clustering Example](Components/LearningExamples/Source/examples/CustomClusteringExample.java)
  * [Mixture of Gaussians Example](Components/LearningExamples/Source/examples/MixtureOfGaussiansExample.java)

### Text Package Examples
  * [Text Pipeline Example](Components/TextExamples/Source/examples/TextPipelineExample.java)

### Framework Package Examples
  * [Cognitive Model Example](Components/FrameworkExamples/Source/examples/BasicCognitiveModelLiteExample.java)

For more information about a specific class you can also look at the unit test for examples of how to create and use it.


## Contacts

Here are some useful contact points for the Foundry.

 * Official site: http://foundry.sandia.gov
 * GitHub site: https://github.com/algorithmfoundry/Foundry
 * Community site: http://www.cognitivefoundry.org
 * Twitter: [@AlgoFoundry](http://www.twitter.com/AlgoFoundry)
 * Google Group: [cognitive-foundry@googlegroups.com](http://groups.google.com/d/forum/cognitive-foundry)

