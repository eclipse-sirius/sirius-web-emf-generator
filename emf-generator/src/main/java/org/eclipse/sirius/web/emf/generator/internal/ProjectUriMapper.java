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

import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Project URI mapper.
 * @author cbrun
 */
public class ProjectUriMapper {
    private static final String SLASH = "/";

    public void registerProject(ResourceSet resourceSet, String projectName, Path projectRoot) {
        URI platformProjectUri = URI.createURI("platform:/resource/" + projectName + SLASH);
        URI projectFileUri = URI.createFileURI(projectRoot.toString() + SLASH);
        resourceSet.getURIConverter().getURIMap().put(platformProjectUri, projectFileUri);
        System.out.println("Registered URI mapping: " + platformProjectUri + " -> " + projectFileUri);
    }

    public void registerGenmodel(ResourceSet resourceSet, ProjectLocation projectLocation, URI fileUri) {
        if (projectLocation == null) {
            return;
        }
        URI platformUri = URI.createURI("platform:/resource/" + projectLocation.projectName() + SLASH
                + projectLocation.projectRelativePath());
        resourceSet.getURIConverter().getURIMap().put(platformUri, fileUri);
        System.out.println("Registered URI mapping: " + platformUri + " -> " + fileUri);
        this.registerProject(resourceSet, projectLocation.projectName(), projectLocation.projectRoot());
    }
}
