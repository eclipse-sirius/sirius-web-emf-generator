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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.templates.edit.ItemProviderAdapterFactory;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

/**
 * Reuses EMF's factory template with specialized provider names.
 *
 * @author mcharfadi
 */
public class ItemProviderAdapterFactoryTemplate {
    // JETEmitter reflects on this field and create(String) to select the line delimiter.
    @SuppressWarnings({ "checkstyle:MemberName", "checkstyle:VisibilityModifier" })
    public final String NL;

    public ItemProviderAdapterFactoryTemplate() {
        this(System.lineSeparator());
    }

    private ItemProviderAdapterFactoryTemplate(String lineDelimiter) {
        this.NL = lineDelimiter;
    }

    public static ItemProviderAdapterFactoryTemplate create(String lineDelimiter) {
        return new ItemProviderAdapterFactoryTemplate(lineDelimiter);
    }

    public String generate(Object argument) {
        GenPackage genPackage = (GenPackage) argument;
        EList<GenClass> genClasses = new BasicEList<>();
        for (GenClass genClass : genPackage.getGenClasses()) {
            genClasses.add(this.withSpecializedProviderName(genClass));
        }
        // Only the class list is decorated. Every other package operation uses the original GenModel.
        GenPackage templatePackage = (GenPackage) Proxy.newProxyInstance(GenPackage.class.getClassLoader(), new Class<?>[] { GenPackage.class },
                (proxy, method, arguments) -> {
                    if ("getGenClasses".equals(method.getName())) {
                        return genClasses;
                    }
                    try {
                        return method.invoke(genPackage, arguments);
                    } catch (InvocationTargetException exception) {
                        throw exception.getCause();
                    }
                });
        return ItemProviderAdapterFactory.create(this.NL).generate(templatePackage);
    }

    private GenClass withSpecializedProviderName(GenClass genClass) {
        // EMF uses this name for both singleton fields and singleton/stateful construction.
        // Import registration stays on the original GenModel so the standard template emits it.
        return (GenClass) Proxy.newProxyInstance(GenClass.class.getClassLoader(), new Class<?>[] { GenClass.class },
                (proxy, method, arguments) -> {
                    if ("getProviderClassName".equals(method.getName())) {
                        String qualifiedName = genClass.getGenPackage().getProviderPackageName() + ".spec." + genClass.getProviderClassName() + "Spec";
                        return genClass.getGenModel().getImportedName(qualifiedName);
                    }
                    try {
                        return method.invoke(genClass, arguments);
                    } catch (InvocationTargetException exception) {
                        throw exception.getCause();
                    }
                });
    }
}
