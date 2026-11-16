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

import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.notify.Adapter;

/**
 * Extends EMF edit generation with SVG-aware item provider specializations.
 *
 * @author mcharfadi
 */
public class SvgGenModelGeneratorAdapterFactory extends GenModelGeneratorAdapterFactory {
    @Override
    public Adapter createGenModelAdapter() {
        if (this.genModelGeneratorAdapter == null) {
            this.genModelGeneratorAdapter = new SvgGenModelGeneratorAdapter(this);
        }
        return this.genModelGeneratorAdapter;
    }

    @Override
    public Adapter createGenClassAdapter() {
        if (this.genClassGeneratorAdapter == null) {
            this.genClassGeneratorAdapter = new SvgGenClassGeneratorAdapter(this);
        }
        return this.genClassGeneratorAdapter;
    }

    @Override
    public Adapter createGenPackageAdapter() {
        if (this.genPackageGeneratorAdapter == null) {
            this.genPackageGeneratorAdapter = new SvgGenPackageGeneratorAdapter(this);
        }
        return this.genPackageGeneratorAdapter;
    }
}
