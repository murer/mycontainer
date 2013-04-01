package com.googlecode.mycontainer.maven.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

/**
 * @goal start
 * @aggregator
 */
public class MycontainerStartMojo extends AbstractMojo {

	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The artifact repository to use.
	 * 
	 * @parameter expression="${localRepository}"
	 * @required
	 * @readonly
	 */
	private ArtifactRepository localRepository;

	/**
	 * The artifact factory to use.
	 * 
	 * @component
	 * @required
	 * @readonly
	 */
	private ArtifactFactory artifactFactory;

	/**
	 * The artifact metadata source to use.
	 * 
	 * @component
	 * @required
	 * @readonly
	 */
	private ArtifactMetadataSource artifactMetadataSource;

	/**
	 * The artifact collector to use.
	 * 
	 * @component
	 * @required
	 * @readonly
	 */
	private ArtifactCollector artifactCollector;

	/**
	 * The dependency tree builder to use.
	 * 
	 * @component
	 * @required
	 * @readonly
	 */
	private DependencyTreeBuilder dependencyTreeBuilder;

	/**
	 * @parameter
	 */
	private Set<String> packs;

	/**
	 * @parameter expression="false"
	 */
	private boolean includeTests = false;

	/**
	 * @parameter expression="${basedir}/src/mycontainer/mycontainer-start.bsh"
	 */
	private File script;

	/**
	 * Map of of plugin artifacts.
	 * 
	 * @parameter expression="${plugin.artifactMap}"
	 * @required
	 * @readonly
	 */
	private Map<String, Artifact> pluginArtifactMap;

	public MycontainerStartMojo() {
		this.packs = new TreeSet<String>(Arrays.asList("jar", "ejb", "war"));
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!script.exists()) {
			throw new MojoExecutionException("Beanshell not found: " + script);
		}
		PluginUtil.configureLogger(getLog());
		List<File> classpath = getClasspath();
		classpath.addAll(getMycontainerDependencies());
		exec(classpath);
	}

	private List<File> getMycontainerDependencies() {
		getLog().info("Getting plugin dependencies...");
		Collection<Artifact> artifacts = pluginArtifactMap.values();
		List<File> files = getArtifactsClasspath(artifacts);
		return files;
	}

	private void exec(List<File> classpath) throws MojoExecutionException {
		try {
			List<URL> urls = new ArrayList<URL>(classpath.size());
			for (File file : classpath) {
				if (!file.exists()) {
					getLog().warn("File not found to isolated classloader: " + file);
				} else {
					URL url = file.toURI().toURL();
					getLog().debug("Classpath: " + url);
					urls.add(url);
				}
			}
			getLog().info("Creating classloader for: " + urls);
			ClassLoader loader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]));
			MycontainerRunner runner = new MycontainerRunner(getLog(), loader, script);
			runner.start();
			runner.join();
		} catch (MalformedURLException e) {
			throw new MojoExecutionException("error", e);
		} catch (InterruptedException e) {
			throw new MojoExecutionException("error", e);
		}
	}

	@SuppressWarnings("unchecked")
	private List<File> getClasspath() throws MojoExecutionException {
		Set<Artifact> artifacts = new HashSet<Artifact>();
		Set<MavenProject> projects = new HashSet<MavenProject>();
		if (packs.contains(project.getPackaging())) {
			projects.add(project);
			mountClasspath(project, artifacts);
		}
		List<MavenProject> collectedProjects = project.getCollectedProjects();
		for (MavenProject module : collectedProjects) {
			if (packs.contains(module.getPackaging())) {
				projects.add(module);
				mountClasspath(module, artifacts);
			}
		}
		removeProjects(projects, artifacts);
		List<File> files = getArtifactsClasspath(artifacts);
		files.addAll(getProjectsClasspath(projects));

		return files;
	}

	@SuppressWarnings("unchecked")
	private List<File> getProjectsClasspath(Set<MavenProject> projects) throws MojoExecutionException {
		try {
			getLog().info("Including project classpath. includingTests: " + includeTests);
			List<File> ret = new ArrayList<File>(projects.size());
			for (MavenProject module : projects) {
				List<String> elements = module.getCompileClasspathElements();
				for (String element : elements) {
					getLog().debug("Path: " + element);
					ret.add(new File(element));
				}
				if (includeTests) {
					elements = module.getTestClasspathElements();
					for (String element : elements) {
						getLog().debug("Path: " + element);
						ret.add(new File(element));
					}
				}
			}
			return ret;
		} catch (DependencyResolutionRequiredException e) {
			throw new MojoExecutionException("error", e);
		}
	}

	private List<File> getArtifactsClasspath(Collection<Artifact> artifacts) {
		List<File> ret = new ArrayList<File>(artifacts.size());
		for (Artifact artifact : artifacts) {
			String path = localRepository.getBasedir() + "/" + localRepository.pathOf(artifact);
			ret.add(new File(path));
		}
		return ret;
	}

	private void removeProjects(Set<MavenProject> projects, Set<Artifact> artifacts) {
		for (MavenProject module : projects) {
			Artifact artifact = module.getArtifact();
			artifacts.remove(artifact);
		}
	}

	@SuppressWarnings("unchecked")
	private void mountClasspath(MavenProject module, Set<Artifact> artifacts) throws MojoExecutionException {
		try {
			Artifact artifact = module.getArtifact();
			getLog().info("Mounting classpath: " + artifact);
			artifacts.add(artifact);
			getLog().debug("Dependency: " + artifact);

			DependencyNode root = dependencyTreeBuilder.buildDependencyTree(module, localRepository, artifactFactory, artifactMetadataSource, null, artifactCollector);
			Iterator<DependencyNode> it = root.iterator();
			while (it.hasNext()) {
				DependencyNode node = it.next();
				Artifact dependency = node.getArtifact();
				String dependencyScope = dependency.getScope();
				if ((Artifact.SCOPE_COMPILE.equals(dependencyScope) || (includeTests && Artifact.SCOPE_TEST.equals(dependencyScope))) && packs.contains(dependency.getType())) {
					boolean add = artifacts.add(dependency);
					if (add) {
						getLog().debug("Dependency: " + artifact);
					}
				}
			}
		} catch (DependencyTreeBuilderException e) {
			throw new MojoExecutionException("error", e);
		}
	}

}
