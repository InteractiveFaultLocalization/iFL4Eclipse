# iFL for Eclipse

## Requirements

+ Java 1.8
+ Eclipse 2018-12 or later

## Interactive Fault Localization for Eclipse

<!--<img src="https://git.sed.hu/geryxyz/iFL-eclipse-plugin/raw/dev/org.eclipse.sed.ifl/icons/logo.png" alt="logo" width="200px"/>-->

Fault localization is a debugging activity which is, by definition, part of a programmer's work in which she has to interact with the source code of the software being debugged.
It follows that this can be performed most effectively through the IDE itself; hence the most logical form of supporting tools is when they are integrated into the IDE. 

With this in mind, we present iFL for Eclipse, which is an Eclipse plug-in for supporting iFL for Java projects developed in this environment. It supporting the popular [Eclipse IDE](https://www.eclipse.org/), so it is part of the well-known workspace of developers.
The plug-in reads the tree of project elements (classes and functions) and lists them in a table showing detailed information about those elements.
This information includes, among others, the suspiciousness scores calculated using a traditional SBFL formula, which ranks code components (e.g., methods) according to their probability of having faults. For example [Tarantula](http://spideruci.org/fault-localization/) is one of these algorithms.
This table also enables direct navigation towards the project tree and the contained source code elements.

Interactivity between the tool and the programmer is achieved by providing the capability to send feedback to the FL engine about the next element in the table based on user knowledge.
It involves the context of the investigated element, in our case, Java classes and methods.
This gives an opportunity to change the order of elements in the table and hopefully arrive more quickly at the faulty element.

## Features

In this section we will briefly elaborate the key features of our plug-in.

**Please note that iFL for Eclipse only supports inspection of Java projects, i.e. projects associated with [Java Nature](https://www.vogella.com/tutorials/EclipseProjectNatures/article.html) and which do not contain any compilation error.**

You can also watch our [demo video](https://youtu.be/ADeK5ibnqSY) or our [promo poster](), in which we showcase a previous version of our plug-in.


### Showing suspiciousness scores for methods

The fault localization (FL) process executed as a session in iFL for Eclipse. You could either start the session from the toolbar or from the iFL menu after selecting the target project or any files of it.

The main view of the plug-in is a table, which display the methods in the target project and their main properties. All common shorting and column rearranging features are available. 

### Loading scores from external source

You are able to load scores from external sources by pressing the load score button. External score data has to be in CSV format with a header, using semicolon (;) as separator and double quotes (" ") for string literals. It could contain the following columns (any other columns will be ignored).

- `name`: (mandatory string) the fully qualified name of the methods, e.g. `dgdg.bla.removeAll(Ljava/util/Collection<*>;)Z`
- `score`: (mandatory double) the suspiciousness values of the methods
- `interactive`: (optional string) if it is present and its value is `no` then the user will not be able to give feedback for this item
- `details`: (optional string) the URL to more information about the methods

### Providing user feedback about code elements

You could use the context menu of the table to provide various user feedback about the selected items. **Interactivity should not be disabled for any selected item to use this feature.**

Feedback will change the score for one or more items. The effect of the feedback categorised according their target.

- **selection:** all items currently selected in the table
- **context:** all items, which are in the context of the selected items (context are highlighted with an orange hue)
- **other:** any items which do not fit into the above categories

For a terminal choices (those which end the current iFL session) you have to reinforce your feedback.

### Navigation to inspected method

iFL for Eclipse support quick navigation to inspected methods either by double clicking on its line in the table or via context menu. 

## Installation Guide

At first you have to download the content of our update site and unzip it into a desired location. The official versions of iFL for Eclipse could be found under [Releases](https://github.com/sed-szeged/iFL4Eclipse/releases).

You are able to install our plug-in directly from Eclipse IDE. Please use the [Install New Software wizard](https://help.eclipse.org/2018-09/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftasks-124.htm) which allows you to add new software to your Eclipse installation.

To access all aviable features of iFL for Eclipse please copy the `key` file next to your Eclipse executable. For further details see the description of selected version.
