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
package org.eclipse.sirius.web.emf.generator;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.sirius.web.emf.generator.internal.EmfResourceSetFactory;
import org.eclipse.sirius.web.emf.generator.internal.GeneratorArguments;
import org.eclipse.sirius.web.emf.generator.internal.GeneratorArgumentsParser;
import org.eclipse.sirius.web.emf.generator.internal.GenmodelFinder;
import org.eclipse.sirius.web.emf.generator.internal.GenmodelGenerationService;
import org.eclipse.sirius.web.emf.generator.internal.GenmodelLoader;
import org.eclipse.sirius.web.emf.generator.internal.GenmodelValidator;
import org.eclipse.sirius.web.emf.generator.internal.GenmodelWrapper;

/**
 * EMF code generation entry point.
 * 
 * @author cbrun
 * @author lfasani
 */
public class EMFGenerator {
    private final GeneratorArgumentsParser argumentsParser = new GeneratorArgumentsParser();
    private final GenmodelFinder genmodelFinder = new GenmodelFinder();;
    private final EmfResourceSetFactory resourceSetFactory = new EmfResourceSetFactory();
    private final GenmodelLoader genmodelLoader = new GenmodelLoader();
    private final GenmodelValidator genmodelValidator = new GenmodelValidator();
    private final GenmodelGenerationService generationService = new GenmodelGenerationService();

    public void run(String[] args) {
        GeneratorArguments arguments = this.argumentsParser.parse(args);
        List<Path> genmodels = this.genmodelFinder.find(arguments.repositoryRoot(), arguments.genmodelPattern());
        ResourceSet resourceSet = this.resourceSetFactory.create();
        List<GenmodelWrapper> loadedGenmodels = this.genmodelLoader.loadAll(resourceSet, arguments.repositoryRoot(), genmodels);
        this.printSummary(arguments, genmodels);
        System.out.printf("Genmodels loaded: %d%n", loadedGenmodels.size());
        this.generationService.generateAll(this.genmodelValidator.validateAll(resourceSet, loadedGenmodels), arguments.format());
    }

    private void printSummary(GeneratorArguments arguments, List<Path> genmodels) {
        System.out.printf("Repository root:  %s%n", arguments.repositoryRoot());
        System.out.printf("Genmodel filter:  %s%n", arguments.genmodelPattern());
        System.out.printf("Genmodels found:  %d%n", genmodels.size());
        System.out.printf("Formatting:       %s%n", arguments.format());
        genmodels.forEach(System.out::println);
    }
}
