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
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenClassGeneratorAdapter;
import org.eclipse.emf.codegen.jet.JETEmitter;
import org.eclipse.emf.common.util.Monitor;

/**
 * Generates a customization class alongside each standard EMF item provider.
 *
 * @author mcharfadi
 */
public class SvgGenClassGeneratorAdapter extends GenClassGeneratorAdapter {
    public SvgGenClassGeneratorAdapter(GeneratorAdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    @Override
    protected void generateItemProvider(GenClass genClass, Monitor monitor) {
        super.generateItemProvider(genClass, monitor);
        String directory = genClass.getGenModel().getEditDirectory();
        String packageName = genClass.getGenPackage().getProviderPackageName() + ".spec";
        String className = genClass.getProviderClassName() + "Spec";
        // Specifications are customization points: never merge or overwrite an existing one.
        if (!this.exists(this.toURI(directory + '/' + packageName.replace('.', '/') + '/' + className + ".java"))) {
            JETEmitter emitter = new JETEmitter("edit/ItemProviderSpec.javajet");
            this.setStaticTemplateClass(emitter, ItemProviderSpecTemplate.class.getName());
            this.generateJava(directory, packageName, className, emitter, new Object[] { genClass }, monitor);
        }
    }
}
