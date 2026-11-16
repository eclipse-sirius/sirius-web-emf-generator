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

import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenPackageGeneratorAdapter;
import org.eclipse.emf.codegen.jet.JETEmitter;
import org.eclipse.emf.common.util.Monitor;

/**
 * Keeps EMF factory generation and merging while selecting specialized providers.
 *
 * @author mcharfadi
 */
public class SvgGenPackageGeneratorAdapter extends GenPackageGeneratorAdapter {
    public SvgGenPackageGeneratorAdapter(GeneratorAdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    @Override
    protected void generateItemProviderAdapterFactory(GenPackage genPackage, Monitor monitor) {
        if (!genPackage.getGenClasses().isEmpty()) {
            JETEmitter emitter = new JETEmitter("edit/ItemProviderAdapterFactory.javajet");
            this.setStaticTemplateClass(emitter, ItemProviderAdapterFactoryTemplate.class.getName());
            this.generateJava(genPackage.getGenModel().getEditDirectory(), genPackage.getProviderPackageName(),
                    genPackage.getItemProviderAdapterFactoryClassName(), emitter, new Object[] { genPackage }, monitor);
        }
    }
}
