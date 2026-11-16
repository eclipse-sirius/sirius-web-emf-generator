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

package org.eclipse.sirius.web.emf.generator.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks that generated edit plugins declare image lookup methods.
 *
 * @author mcharfadi
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class SvgEditPluginGenerationTest {
    @TempDir
    private Path temporaryFolder;

    @Test
    public void generatesImageLookupMethods() throws Exception {
        GenmodelWrapper wrapper = this.createGenmodel();

        new GenmodelGenerationService().generateAll(List.of(wrapper), false);

        Path plugin = this.temporaryFolder.resolve("edit/src/main/java/example/provider/ExampleEditPlugin.java");
        assertThat(Files.readString(plugin))
                .contains("protected Object doGetImage(String key) throws java.io.IOException",
                        "protected static String extensionFor(String key)");
    }

    private GenmodelWrapper createGenmodel() throws Exception {
        Path modelProject = Files.createDirectory(this.temporaryFolder.resolve("model"));
        Files.createDirectory(this.temporaryFolder.resolve("edit"));
        ResourceSet resourceSet = new EmfResourceSetFactory().create();
        ProjectLocation location = new ProjectLocation("model", "model.genmodel", modelProject);
        new ProjectUriMapper().registerProject(resourceSet, "model", modelProject);
        EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
        ePackage.setName("example");
        ePackage.setNsPrefix("example");
        ePackage.setNsURI("https://example.org/model");
        EClass node = EcoreFactory.eINSTANCE.createEClass();
        node.setName("Node");
        ePackage.getEClassifiers().add(node);
        Resource ecore = resourceSet.createResource(URI.createFileURI(modelProject.resolve("model.ecore").toString()));
        ecore.getContents().add(ePackage);
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.setModelName("Example");
        genModel.setModelDirectory("/model/src/main/java");
        genModel.setEditDirectory("/edit/src/main/java");
        genModel.setModelPluginID("example");
        genModel.setEditPluginID("example.edit");
        genModel.setOSGiCompatible(false);
        genModel.initialize(List.of(ePackage));
        Resource genmodel = resourceSet.createResource(URI.createFileURI(modelProject.resolve("model.genmodel").toString()));
        genmodel.getContents().add(genModel);
        return new GenmodelWrapper(genmodel, location);
    }
}
