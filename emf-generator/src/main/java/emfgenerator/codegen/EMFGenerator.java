/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package emfgenerator.codegen;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;

import emfgenerator.codegen.internal.EmfResourceSetFactory;
import emfgenerator.codegen.internal.GeneratorArguments;
import emfgenerator.codegen.internal.GeneratorArgumentsParser;
import emfgenerator.codegen.internal.GenmodelFinder;
import emfgenerator.codegen.internal.GenmodelGenerationService;
import emfgenerator.codegen.internal.GenmodelLoader;
import emfgenerator.codegen.internal.GenmodelValidator;
import emfgenerator.codegen.internal.GenmodelWrapper;
import emfgenerator.codegen.internal.ProjectUriMapper;

/**
 * EMF code generation entry point.
 *  @author cbrun
 *  @author lfasani
 */
public final class EMFGenerator {
    private final GeneratorArgumentsParser argumentsParser;
    private final GenmodelFinder genmodelFinder;
    private final EmfResourceSetFactory resourceSetFactory;
    private final GenmodelLoader genmodelLoader;
    private final GenmodelValidator genmodelValidator;
    private final GenmodelGenerationService generationService;

    public EMFGenerator() {
        ProjectUriMapper projectUriMapper = new ProjectUriMapper();
        this.argumentsParser = new GeneratorArgumentsParser();
        this.genmodelFinder = new GenmodelFinder();
        this.resourceSetFactory = new EmfResourceSetFactory();
        this.genmodelLoader = new GenmodelLoader();
        this.genmodelValidator = new GenmodelValidator();
        this.generationService = new GenmodelGenerationService();
    }

    public void run(String[] args) {
        GeneratorArguments arguments = this.argumentsParser.parse(args);
        List<Path> genmodels = this.genmodelFinder.find(arguments.repositoryRoot(), arguments.genmodelPattern());
        ResourceSet resourceSet = this.resourceSetFactory.create();
        List<GenmodelWrapper> loadedGenmodels = this.genmodelLoader.loadAll(resourceSet, arguments.repositoryRoot(), genmodels);
        this.printSummary(arguments, genmodels);
        System.out.printf("Genmodels loaded: %d%n", loadedGenmodels.size());
        this.generationService.generateAll(this.genmodelValidator.validateAll(resourceSet, loadedGenmodels));
    }

    private void printSummary(GeneratorArguments arguments, List<Path> genmodels) {
        System.out.printf("Repository root:  %s%n", arguments.repositoryRoot());
        System.out.printf("Genmodel filter:  %s%n", arguments.genmodelPattern());
        System.out.printf("Genmodels found:  %d%n", genmodels.size());
        genmodels.forEach(System.out::println);
    }
}
