/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.inspections;

import com.goide.psi.*;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

public class GoDuplicateFieldsOrMethodsInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull final ProblemsHolder problemsHolder) {
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitStructType(@NotNull final GoStructType type) {
        final List<GoNamedElement> fields = ContainerUtil.newArrayList();
        type.accept(new GoRecursiveVisitor() {
          @Override
          public void visitFieldDefinition(@NotNull GoFieldDefinition o) {
            fields.add(o);
          }

          @Override
          public void visitAnonymousFieldDefinition(@NotNull GoAnonymousFieldDefinition o) {
            fields.add(o);
          }

          @Override
          public void visitType(@NotNull GoType o) {
            if (o == type) super.visitType(o); 
          }
        });
        check(fields, problemsHolder, "field");
        super.visitStructType(type);
      }

      @Override
      public void visitInterfaceType(@NotNull GoInterfaceType o) {
        check(o.getMethodSpecList(), problemsHolder, "method");
        super.visitInterfaceType(o);
      }
    });
  }

  private static void check(@NotNull List<? extends GoNamedElement> fields, @NotNull ProblemsHolder problemsHolder, @NotNull String what) {
    Set<String> names = ContainerUtil.newHashSet();
    for (GoCompositeElement field : fields) {
      if (field instanceof GoNamedElement) {
        String name = ((GoNamedElement)field).getName();
        if (names.contains(name)) {
          PsiElement id = ((GoNamedElement)field).getIdentifier();
          problemsHolder.registerProblem(id != null ? id : field, "Duplicate " + what + " " + "'" + name + "'", GENERIC_ERROR_OR_WARNING);
        }
        else {
          ContainerUtil.addIfNotNull(names, name);
        }
      }
    }
  }
}
