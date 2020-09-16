# iFL for Eclipse

## Requirements

+ Java 1.8
+ Eclipse 2018-12 or later

## Interactive Fault Localization for Eclipse

<img src="/media/dev.gif" alt="dev_mascot" height="150px"/><img src="/media/logo.png" alt="logo" height="150px"/><img src="/media/alg.gif" alt="alg_mascot" height="150px"/>

Fault localization is a debugging activity which is, by definition, part of a programmer's work in which she has to interact with the source code of the software being debugged.
It follows that this can be performed most effectively through the IDE itself; hence, the most logical form of supporting tools is when they are integrated into the IDE. 

With this in mind, we present iFL for Eclipse, which is an Eclipse plug-in for supporting iFL for Java projects developed in this environment. It is supporting the popular [Eclipse IDE](https://www.eclipse.org/), so it is part of the well-known workspace of developers.
The plug-in reads the tree of project elements (classes and functions) and lists them in a table showing detailed information about those elements.
This information includes, among others, the suspiciousness scores calculated using a traditional SBFL formula, which ranks code components (e.g., methods) according to their probability of having faults. For example, [Tarantula](http://spideruci.org/fault-localization/) is one of these algorithms.
This table also enables direct navigation towards the project tree and the contained source code elements.

Interactivity between the tool and the programmer is achieved by providing the capability to send feedback to the FL engine about the next element in the table based on user knowledge.
It involves the context of the investigated element, in our case, Java classes and methods.
This allows changing the order of elements in the table and hopefully arrive more quickly at the faulty item.

## Features

In this section, we will briefly elaborate on the key features of our plug-in.

**Please note that iFL for Eclipse only supports inspection of Java projects, i.e., projects associated with [Java Nature](https://www.vogella.com/tutorials/EclipseProjectNatures/article.html) and which do not contain any compilation error.**

You can also watch our [demo video](https://youtu.be/ADeK5ibnqSY) or our [promo poster](/media/poster.pdf), in which we showcase a previous version of our plug-in.

<img src="/media/screen_shot/menu.png" alt="menu" width="600px"/>

The fault localization (FL) process executed as a session in iFL for Eclipse. You could either start the session from the toolbar or the iFL menu after selecting the target project or any files of it.

### Showing suspiciousness scores for methods

<img src="/media/screen_shot/main.png" alt="main" width="600px"/>

The main view of the plug-in is a table, which displays the methods in the target project and their main properties. All common shorting and column rearranging features are available. 

### Loading scores from an external source

You can load scores from external sources by pressing the load score button. External score data has to be in CSV format with a header, using a semicolon (;) as separator and double quotes (" ") for string literals. It could contain the following columns (any other columns will be ignored).

<img src="/media/screen_shot/load.png" alt="load" width="600px"/>

- `name`: (mandatory string) the fully qualified name of the methods, e.g. `dgdg.bla.removeAll(Ljava/util/Collection<*>;)Z`
- `score`: (mandatory double) the suspiciousness values of the methods
- `interactive`: (optional string) if it is present and its value is `no` then the user will not be able to give feedback for this item
- `details`: (optional string) the URL to more information about the methods

### Providing user feedback about code elements

You could use the context menu of the table to provide various user feedback about the selected items. **Interactivity should not be disabled for any selected item to use this feature.**

<img src="/media/screen_shot/context_menu.png" alt="context_menu" width="600px"/>

Feedback will change the score for one or more items. The effect of the feedback categorized according to their target.

- **selection:** all items currently selected in the table
- **context:** all items, which are in the context of the selected items (context are highlighted with an orange hue)
- **other:** any items which do not fit into the above categories

For terminal choices (those which end the current iFL session) you have to reinforce your feedback.

### Navigation to inspected method

iFL for Eclipse supports quick navigation to inspected methods either by double-clicking on its line in the table or via the context menu. 

## Installation Guide

At first, you have to download the content of our update site and unzip it into the desired location. The official versions of iFL for Eclipse could be found under [Releases](https://github.com/sed-szeged/iFL4Eclipse/releases).

You can install our plug-in directly from Eclipse IDE. Please use the [Install New Software wizard](https://help.eclipse.org/2018-09/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftasks-124.htm) which allows you to add new software to your Eclipse installation.

To access all available features of iFL for Eclipse, please copy the `key` file next to your Eclipse executable, for further details see the description of the selected version.

## Contributor's guide

This section is a guideline for those developers who wish to contribute to the project.

### Developing environment

+ Java JDK 1.8 (https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
+ Eclipse for Java developers (2018-12 or later version) (https://www.eclipse.org/downloads/)
+ PDE plug-in for Eclipse (In the newer versions of Eclipse, this plug-in is included in the basic installation. If it is not, it can be easily added by using Eclipse's "Install new software..." feature from the "Help" menu.)

<img src="/media/screen_shot/install_pde.png" alt="install_pde" width="600px"/>

+ Maven (installation guide can be found here: https://maven.apache.org/install.html)

### Setting up project dependencies

After cloning the repository to a local machine, the dependencies of the project must be installed. To do this, navigate to the iFL4Eclipse\org.eclipse.sed.ifl.root\bundles\org.eclipse.sed.ifl.dependencies folder, open the command line and enter "mvn clean verify". This command will create a jar in the "target folder that includes the dependencies.
Another dependency that is not included in the jar is Swing2SWT (downloadable from here: https://mvnrepository.com/artifact/org.eclipse.wb.rcp/swing2swt/1.6.0). The downloaded jar must be decompressed with an archiver (like 7zip) and the content jar (swing2swt.jar) along with the previously created dependencies jar should be moved to the iFL4Eclipse\org.eclipse.sed.ifl.root\bundles\org.eclipse.sed.ifl\libs folder.

### Opening the project in Eclipse

In Eclipse, choose the "Open project from file system..." option in the "File" menu. Choose the iFL4Eclipse\org.eclipse.sed.ifl.root\bundles\org.eclipse.sed.ifl folder as import source, check the checkbox in front of the folder then click "Finish". The project is now imported.
Right click on the project and choose "Properties". Go to the "Java compiler" tab and set the compliance to 1.8.

<img src="/media/screen_shot/jdk_compliance.png" alt="jdk_compliance" width="600px"/>

Now go to the "Java build path" tab, choose "Libraries", then "Add JARs...". Select the dependencies jar and the swing2swt jar and click "Ok".

<img src="/media/screen_shot/build_path.png" alt="build_path" width="600px"/>

After this, navigate to the "Order and export" tab and check all of the checkboxes, then hit "Apply and close".
  
### How to run

Right click on the project, choose "Run as...", then hit "Eclipse application".

## Contact us

<img src="/media/szte.png" alt="szte" width="150px"/>

iFL for Eclipse is developed and maintained during the iFL research of the [University of Szeged, Department of Software Engineering](http://www.sed.inf.u-szeged.hu/).

If you have any question or suggestion feel free to [submit an issue](https://github.com/sed-szeged/iFL4Eclipse/issues/new/choose), or [write an e-mail](mailto:geryxyz@inf.u-szeged.hu?cc=beszedes@inf.u-szeged.hu&subject=%5BiFL4Eclipse%5D).
