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

package com.goide.runconfig.testing;

import com.goide.GoFileType;
import com.goide.psi.GoFile;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.testIntegration.TestFinder;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class GoTestFinder implements TestFinder {

  private static final String TEST_SUFFIX = "_test.go";
  private static final String EXTENSION = "." + GoFileType.INSTANCE.getDefaultExtension();

  public static boolean isTestFile(@Nullable PsiFile file) {
    return file != null && file instanceof GoFile && file.getName().endsWith(TEST_SUFFIX);
  }

  @Nullable
  @Override
  public PsiElement findSourceElement(@NotNull PsiElement from) {
    return InjectedLanguageUtil.getTopLevelFile(from);
  }

  @NotNull
  @Override
  public Collection<PsiElement> findTestsForClass(@NotNull PsiElement element) {
    PsiFile file = InjectedLanguageUtil.getTopLevelFile(element);
    if (file instanceof GoFile) {
      PsiDirectory directory = file.getContainingDirectory();
      PsiFile testFile = directory.findFile(FileUtil.getNameWithoutExtension(file.getName()) + TEST_SUFFIX);
      if (testFile != null) {
        return new SmartList<PsiElement>(testFile);
      }
    }
    return Collections.emptyList();
  }

  @NotNull
  @Override
  public Collection<PsiElement> findClassesForTest(@NotNull PsiElement element) {
    PsiFile testFile = InjectedLanguageUtil.getTopLevelFile(element);
    if (testFile instanceof GoFile) {
      PsiDirectory directory = testFile.getContainingDirectory();
      PsiFile sourceFile = directory.findFile(StringUtil.trimEnd(testFile.getName(), TEST_SUFFIX) + EXTENSION);
      if (sourceFile != null) {
        return new SmartList<PsiElement>(sourceFile);
      }
    }
    return Collections.emptyList();
  }
  
  @Override
  public boolean isTest(@NotNull PsiElement element) {
    return isTestFile(InjectedLanguageUtil.getTopLevelFile(element));
  }
}
