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

package emfgenerator.codegen.internal;

import java.nio.file.Files;
import java.util.List;

import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 *  @author cbrun
 */
public final class GenmodelGenerationService {
    private final ProjectUriMapper projectUriMapper = new ProjectUriMapper();
    private final EditProjectLocationResolver editProjectLocationResolver = new EditProjectLocationResolver();

    public void generateAll(List<GenmodelWrapper> genmodels) {
        for (GenmodelWrapper genModel : genmodels) {
            this.generate(genModel);
        }
    }

    private void generate(GenmodelWrapper genModelWrapper) {
        Resource resource = genModelWrapper.resource();
        GenModel genModel = this.findGenModel(resource);
        if (genModel == null) {
            System.err.println("No GenModel root found in " + resource.getURI());
            return;
        }
        Generator generator = new Generator();
        generator.getAdapterFactoryDescriptorRegistry()
                .addDescriptor(GenModelPackage.eNS_URI, GenModelGeneratorAdapterFactory.DESCRIPTOR);
        genModel.reconcile();
        genModel.setCanGenerate(true);
        genModel.setDynamicTemplates(false);
        genModel.setCodeFormatting(false);
        genModel.setCommentFormatting(false);
        genModel.setCleanup(true);
        generator.setInput(genModel);
        EditProjectLocation editProject = this.editProjectLocationResolver.resolve(genModelWrapper.projectLocation(), genModel);
        if (editProject != null && Files.isDirectory(editProject.projectRoot())) {
            this.projectUriMapper.registerProject(resource.getResourceSet(), editProject.projectName(), editProject.projectRoot());
        }
        System.out.println("Generating MODEL for " + resource.getURI());
        generator.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, new BasicMonitor.Printing(System.out));
        System.out.println("Generating EDIT for " + resource.getURI());
        generator.generate(genModel, GenBaseGeneratorAdapter.EDIT_PROJECT_TYPE, new BasicMonitor.Printing(System.out));
    }

    private GenModel findGenModel(Resource resource) {
        for (EObject root : resource.getContents()) {
            if (root instanceof GenModel genModel) {
                return genModel;
            }
        }
        return null;
    }
}
