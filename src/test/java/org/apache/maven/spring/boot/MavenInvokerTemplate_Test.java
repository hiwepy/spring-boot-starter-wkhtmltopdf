/*
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.maven.spring.boot;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.invoker.PrintStreamHandler;
import org.apache.maven.shared.invoker.SystemOutHandler;
import org.apache.maven.shared.invoker.SystemOutLogger;
import org.apache.maven.spring.boot.ext.MavenInvokerTemplate;
import org.junit.Test;
import org.springframework.util.StringUtils;

/**
 * https://www.sourcetrail.com/blog/how_to_integrate_maven_into_your_own_java_tool/
 */
public class MavenInvokerTemplate_Test {

	InvocationOutputHandler outputHandler = new SystemOutHandler();
	InvocationOutputHandler errorHandler = new PrintStreamHandler(System.err, false);
	InvokerLogger invokerLogger = new SystemOutLogger();

	public Invoker mavenInvoker(MavenInvokerProperties properties) {

		Invoker invoker = new DefaultInvoker();

		// Sets the handler used to capture the error output from the Maven build.
		invoker.setErrorHandler(errorHandler);
		// Sets the path to the base directory of the local repository to use for the
		// Maven invocation.
		if (StringUtils.hasText(properties.getLocalRepository())) {
			File localRepositoryDirectory = new File(properties.getLocalRepository());
			if (localRepositoryDirectory.exists() && localRepositoryDirectory.isDirectory()) {
				invoker.setLocalRepositoryDirectory(localRepositoryDirectory);
			} else {
				localRepositoryDirectory.mkdir();
				invoker.setLocalRepositoryDirectory(localRepositoryDirectory);
			}
		} else {
			File localRepositoryDirectory = new File(FileUtils.getUserDirectory(),
					".m2" + File.separator + "repository");
			if (!localRepositoryDirectory.exists()) {
				localRepositoryDirectory.mkdir();
			}
			invoker.setLocalRepositoryDirectory(localRepositoryDirectory);
		}
		// Sets the logger used by this invoker to output diagnostic messages.
		invoker.setLogger(invokerLogger);
		//
		if (StringUtils.hasText(properties.getMavenExecutable())) {
			invoker.setMavenExecutable(new File(properties.getMavenExecutable()));
		}
		// Sets the path to the base directory of the Maven installation used to invoke
		// Maven.
		if (StringUtils.hasText(properties.getMavenHome())) {
			invoker.setMavenHome(new File(properties.getMavenHome()));
		}
		// Sets the handler used to capture the standard output from the Maven build.
		invoker.setOutputHandler(outputHandler);

		return invoker;
	}

	@Test
	public void testInstall() throws MavenInvocationException {

		MavenInvokerProperties properties = new MavenInvokerProperties();
		properties.setNonPluginUpdates(true);
		properties.setUpdateSnapshots(false);
		properties.setMavenHome("D:\\Java\\maven\\apache-maven-3.5.3");
		properties.setLocalRepository("E:\\Java\\.m2\\repository");

		MavenInvokerTemplate template = new MavenInvokerTemplate(outputHandler, errorHandler, mavenInvoker(properties),
				properties);

		// InvocationResult result = template.install("D:\\", "p6spy-3.7.0.jar", "p6spy", "p6spy", "3.7.0-xx", "jar", true, true);
		InvocationResult result = template.install( "D:\\p6spy-3.7.0.jar", "p6spy", "p6spy", "3.7.0-xx", "jar", true, true);

		System.out.println("ExitCode:" + result.getExitCode());

	}

	// @Test
	public void testDeploy() throws MavenInvocationException {

		MavenInvokerProperties properties = new MavenInvokerProperties();

		properties.setMavenHome("D:\\Java\\maven\\apache-maven-3.5.3");

		MavenInvokerTemplate template = new MavenInvokerTemplate(outputHandler, errorHandler, mavenInvoker(properties),
				properties);
		
		InvocationResult result = template.deploy("D:\\", "D:\\p6spy-3.7.0.jar", "p6spy", "p6spy", "3.7.0-xx", "jar", 
				"-Durl=http://127.0.0.1:8082/nexus/content/repositories/thirdparty/", "-DrepositoryId=thirdparty");
		
		/*InvocationResult result = template.deploy("D:\\p6spy-3.7.0.jar", "p6spy", "p6spy", "D:\\3.7.0-xx", "jar", 
				"-Durl=http://127.0.0.1:8082/nexus/content/repositories/thirdparty/", "thirdparty");*/

		System.out.println("ExitCode:" + result.getExitCode());

	}

	public void testExecute() throws MavenInvocationException {

		MavenInvokerProperties properties = new MavenInvokerProperties();

		properties.setMavenHome("D:\\Java\\maven\\apache-maven-3.5.3");

		MavenInvokerTemplate template = new MavenInvokerTemplate(outputHandler, errorHandler, mavenInvoker(properties),
				properties);

		InvocationResult result = template.execute("D:\\", "deploy:deploy-file", "-DgroupId=p6spy",
				"-DartifactId=p6spy", "-Dversion=3.7.0-xx", "-Dpackaging=jar", "-Dfile=p6spy-3.7.0.jar",
				"-Durl=http://10.71.19.153:8081/nexus/content/repositories/thirdparty/",
				"-DrepositoryId=nexus-thirdparty");

		System.out.println("ExitCode:" + result.getExitCode());

	}

}
