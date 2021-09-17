package org.eclipse.sed.ifl.ide.modifier.source;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.m2e.core.MavenPlugin;


public final class PomModifier {

	private final Xpp3Dom POM;
	private final Model model;
	private final MavenProject mavenProject;
	private final IJavaProject javaProject;
	private final String includeLine;
	private final String excludeLine;
	
	private Xpp3Dom ParsePOM(String POMPath) throws PomModificationException {
		File POMFile = new File(POMPath); 
		
	    try {
	    	Xpp3Dom dom = Xpp3DomBuilder.build(new FileReader(POMFile));
	        return dom;
		} catch (IOException e) {
			throw new PomModificationException("Project's pom not found", e);
	    } catch (Exception e) {
	        throw new PomModificationException("Unexpected error during pom parsing", e);
	    }
	}
	
	public PomModifier(IJavaProject javaProject) throws Exception {
		this.javaProject = javaProject;
		
		mavenProject = MavenPlugin.getMavenProjectRegistry().getProject(javaProject.getProject()).getMavenProject(new NullProgressMonitor());

		model = mavenProject.getModel();
		POM = ParsePOM(mavenProject.getFile().getPath());
		
		includeLine = new File(model.getBuild().getOutputDirectory()).toURI().toString() + ".*";
		excludeLine = new File(model.getBuild().getTestOutputDirectory()).toURI().toString() + ".*";
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

	public void updateJUnitVersion(String targetVersion) {
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
			version.setValue(targetVersion);
			scope.setValue("test");
			
			junit.addChild(groupdId);
			junit.addChild(artifactId);
			junit.addChild(version);
			junit.addChild(scope);
			
			dependencies.addChild(junit);
			return;
		}
		
		Xpp3Dom currentVersion = getElementsChild(junit, "version");
		if (isCurrentOlder(currentVersion.getValue(), targetVersion)) {
			currentVersion.setValue(targetVersion);
		}
	}

	private boolean isCurrentOlder(String current, String target) {
		// org.apache.maven.artifact.versioning.DefaultArtifactVersion is also a good candidate for comparison
		ComparableVersion currentVersion = new ComparableVersion(current);
		ComparableVersion targetVersion = new ComparableVersion(target);

		return currentVersion.compareTo(targetVersion) < 0;
	}

	public void editSurefireConfig(String agentFile, String listener) {
		String agentFilePath = javaProject.getProject().getRawLocation().removeLastSegments(1).append("java-instrumenter-master").append("target").append(agentFile).toOSString();		
		char separator = '=';
		StringBuilder arglineBuilder = new StringBuilder();
		arglineBuilder.append("-javaagent:").append(agentFilePath);

		if (includeLine != null && !includeLine.isEmpty()) {
			arglineBuilder.append(separator).append("includes=").append(includeLine);

			separator = ',';
		}

		if (excludeLine != null && !excludeLine.isEmpty()) {
			arglineBuilder.append(separator).append("excludes=").append(excludeLine);

			separator = ',';
		}
		
		String arglineValue = arglineBuilder.toString();

		Xpp3Dom mBuild = getElementsChild(this.POM, "build");
		Xpp3Dom pManagement = getElementsChild(mBuild, "pluginManagement");
		Xpp3Dom plugins = getElementsChild(pManagement, "plugins");
		Xpp3Dom surefire = getElementsChildByArtifactId(plugins, "maven-surefire-plugin");
		
		if (surefire == null) {
			surefire = createNode(plugins, "plugin");
			createNodeWithValue(surefire, "groupId", "org.apache.maven.plugins");
			createNodeWithValue(surefire, "artifactId", "maven-surefire-plugin");
			createNodeWithValue(surefire, "version", "3.0.0-M3");
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
			throw new PomModificationException("An error occurred while saving the new pom file.", e);
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
	
	private void createNodeWithValue(Xpp3Dom parentNode, String name, String value) {
		Xpp3Dom newNode = createNode(parentNode, name);
		newNode.setValue(value);
	}
}
