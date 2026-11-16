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
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapter;
import org.eclipse.emf.codegen.jet.JETEmitter;
import org.eclipse.emf.common.util.Monitor;

/**
 * Generates edit plugins capable of locating SVG images in a standalone runtime.
 *
 * @author mcharfadi
 */
public class SvgGenModelGeneratorAdapter extends GenModelGeneratorAdapter {
    public SvgGenModelGeneratorAdapter(GeneratorAdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    @Override
    protected void generateEditPluginClass(GenModel genModel, Monitor monitor) {
        if (!genModel.sameEditEditorProject()) {
            JETEmitter emitter = new JETEmitter("edit/Plugin.javajet");
            this.setStaticTemplateClass(emitter, EditPluginTemplate.class.getName());
            this.generateJava(genModel.getEditPluginDirectory(), genModel.getEditPluginPackageName(),
                    genModel.getEditPluginClassName(), emitter, new Object[] { genModel }, monitor);
        }
    }
}
