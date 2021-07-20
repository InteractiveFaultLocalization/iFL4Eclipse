package org.eclipse.sed.ifl.ide.modifier.source;



import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.*;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public final class PomModifier {

	private final Xpp3Dom POM;
	private final String includeLine;
	private final String excludeLine;

	private Model ParsePOM(String POMPath) throws PomModificationException {
		File POMFile = new File(POMPath);
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
			Model model = reader.read(new FileReader(POMFile)); // parsing XML
			model.setPomFile(POMFile);
			return model;
		} catch (IOException e) {
			throw new PomModificationException("File is not found." + POMPath);
		} catch (XmlPullParserException e) {
			throw new PomModificationException(e.getMessage());
		}
	}
	
	private Xpp3Dom ParsePOMDom(String POMPath) throws PomModificationException {
		File POMFile = new File(POMPath); 
		
	    try {
	    	Xpp3Dom dom = Xpp3DomBuilder.build(new FileReader(POMFile));
	        return dom;
		} catch (IOException e) {
			throw new PomModificationException("File is not found." + POMPath);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public PomModifier(String POMPath) throws Exception {
		Xpp3Dom dom = ParsePOMDom(POMPath);
		Model model = ParsePOM(POMPath);
		this.POM = dom;
		//TODO replace with plexus utils
		this.includeLine = model.getBuild().getOutputDirectory();
		this.excludeLine = model.getBuild().getTestOutputDirectory();
	}
	
	public Xpp3Dom getPOM() {
		return POM;
	}

	public String getIncludeLine() {
		return includeLine;
	}

	public String getExcludeLine() {
		return excludeLine;
	}

	public void updateJUnitVersion(String newVersion) {
		Xpp3Dom dependencies = getElementsChild(this.POM, "dependencies");
		Xpp3Dom junit = getElementsChildByArtifactId(dependencies, "junit");
		if (junit == null) {
			junit = new Xpp3Dom("dependency");
			
			Xpp3Dom groupdId = new Xpp3Dom("groupId");
			Xpp3Dom artifactId = new Xpp3Dom("artifactId");
			Xpp3Dom version = new Xpp3Dom("version");
			Xpp3Dom scope = new Xpp3Dom("scope");
			
			groupdId.setValue("junit");
			artifactId.setValue("junit");
			version.setValue(newVersion);
			scope.setValue("test");
			
			junit.addChild(groupdId);
			junit.addChild(artifactId);
			junit.addChild(version);
			junit.addChild(scope);
			
			dependencies.addChild(junit);
			return;
		}
		
		Xpp3Dom version = getElementsChild(junit, "version");
		String oldVersion = version.getValue();
		String majorOldVersionString = oldVersion.split("\\.")[0];
		String minorOldVersionString = oldVersion.split("\\.")[1];
		String majorNewVersionString = newVersion.split("\\.")[0];
		String minorNewVersionString = newVersion.split("\\.")[1];
		int majorOldVersion = Integer.parseInt(majorOldVersionString);
		int minorOldVersion = Integer.parseInt(minorOldVersionString);
		int majorNewVersion = Integer.parseInt(majorNewVersionString);
		int minorNewVersion = Integer.parseInt(minorNewVersionString);
		if (majorOldVersion < majorNewVersion) { // checking if version is lower, than 4.13
			version.setValue(newVersion);
		} else if (majorOldVersion >= majorNewVersion && minorOldVersion < minorNewVersion) {
			version.setValue(newVersion);
		}
	}
		
	public void editSurefireConfig(String agentFilePath, String listener) {
		//String arglineValue = "-javaagent:" + agentFilePath + "=includes=" + (this.includeLine == null ? ""                : this.includeLine ) + ".*,excludes=" + (this.excludeLine == null ? "" : this.excludeLine + ".*");
		String arglineValue = "-javaagent:" + agentFilePath + "=includes=.*org.*";// + (this.includeLine == null ? ""                : this.includeLine ) + ".*org.joda.time.*";//,excludes=" + (this.excludeLine == null ? "" : this.excludeLine + ".*");
		//String arglineValue = "-javaagent:" + agentFilePath + "=includes=" + (this.includeLine == null ? ".*org.joda.time" : ".*org.joda.time") + ".*,excludes=" + (this.excludeLine == null ? "" : this.excludeLine + ".*");
		//ez az aslo mukodik, a kozepso a test, a felso meg, ahogy talan ki kene nezzen
		
		
		Xpp3Dom mBuild = getElementsChild(this.POM, "build");
		Xpp3Dom pManagement = getElementsChild(mBuild, "pluginManagement");
		Xpp3Dom plugins = getElementsChild(pManagement, "plugins");
		Xpp3Dom surefire = getElementsChildByArtifactId(plugins, "maven-surefire-plugin");
		
		if (surefire == null) {
			surefire = createNode(plugins, "plugin");
			createNode(surefire, "groupId", "org.apache.maven.plugins");
			createNode(surefire, "artifactId", "maven-surefire-plugin");
			createNode(surefire, "version", "3.0.0-M3");
		}
		
		Xpp3Dom configuration = getElementsChild(surefire, "configuration");

		Xpp3Dom argline = new Xpp3Dom("argLine");
		Xpp3Dom properties = new Xpp3Dom("properties");
		Xpp3Dom property = new Xpp3Dom("property");
		Xpp3Dom name = new Xpp3Dom("name");
		Xpp3Dom value = new Xpp3Dom("value");
		
		argline.setValue(arglineValue);
		properties.addChild(property);
		name.setValue("listener");
		property.addChild(name);
		value.setValue(listener);
		property.addChild(value);
		
		if (configuration.getChildCount() == 0) {
			configuration.addChild(argline);
			configuration.addChild(properties);
		} else {
			argline = getElementsChild(configuration, "argLine");
			argline.setValue(argline.getValue() + " " + arglineValue);
			//TODO multiagent support SoonTM
			
			properties = getElementsChild(configuration, "properties");
			if (properties.getChildCount() == 0) {
				properties.addChild(property);
			} else {
				//TODO Is it possible to add multiple nodes with <name>listener</name>?
				for (Xpp3Dom prop : properties.getChildren("property")) {
					if (prop.getChild("name").getValue().contentEquals("listener")) {
						if (prop.getChild("value") == null) {
							prop.addChild(value);
							return;
						}
						prop.getChild("value").setValue(listener);
						return;
					}
				}
				properties.addChild(property);	
			}
		}
	}
	
	public void savePOM(String newPOMPath) throws PomModificationException {
		try { 
			FileWriter writer = new FileWriter(newPOMPath);
			Xpp3DomWriter.write(writer, this.POM);
			writer.close();
		} catch (Exception e) {
			throw new PomModificationException("An error occurred while generating new pom file.");
		}
	}
	
	private Xpp3Dom getElementsChild(Xpp3Dom element, String name) {
		Xpp3Dom child = element.getChild(name);
		if (child == null) {
			child = createNode(element, name);
		}
		return child;
	}
	
	private Xpp3Dom getElementsChildByArtifactId(Xpp3Dom element, String name) {
		for (Xpp3Dom child : element.getChildren()) {
			if (child.getChild("artifactId").getValue().contentEquals(name)) {
				return child;
			}
		}
		return null;
	}
	
	private Xpp3Dom createNode(Xpp3Dom parentNode, String name) {
		Xpp3Dom newNode = new Xpp3Dom(name);
		parentNode.addChild(newNode);
		return newNode;
	}
	
	private void createNode(Xpp3Dom parentNode, String name, String value) {
		Xpp3Dom newNode = createNode(parentNode, name);
		newNode.setValue(value);
	}
}
