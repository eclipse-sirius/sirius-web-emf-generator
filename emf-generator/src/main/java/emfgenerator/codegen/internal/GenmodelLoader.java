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

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 *  @author cbrun
 *  @author lfasani
 */
final public class GenmodelLoader {
    private final ProjectLocationResolver projectLocationResolver = new ProjectLocationResolver();
    private final ProjectUriMapper projectUriMapper = new ProjectUriMapper();

    public List<GenmodelWrapper> loadAll(ResourceSet resourceSet, Path repositoryRoot, List<Path> genmodels) {
        return genmodels.stream()
                .flatMap(path -> this.load(resourceSet, repositoryRoot, path).stream())
                .toList();
    }

    private Optional<GenmodelWrapper> load(ResourceSet resourceSet, Path repositoryRoot, Path path) {
        Optional<GenmodelWrapper> optionalGenModel = Optional.empty();
        URI fileUri = URI.createFileURI(path.toAbsolutePath().toString());
        ProjectLocation projectLocation = this.projectLocationResolver.resolve(repositoryRoot, path);
        if (projectLocation != null) {
            this.projectUriMapper.registerGenmodel(resourceSet, projectLocation, fileUri);
            Resource resource = resourceSet.getResource(fileUri, true);
            if (resource != null) {
                optionalGenModel = Optional.of(new GenmodelWrapper(resource, projectLocation));
            } else {
                System.err.println("No GenModel resource found in " + path);
            }
        } else {
            System.err.println("No Project found in " + path);
        }
        return optionalGenModel;
    }
}
