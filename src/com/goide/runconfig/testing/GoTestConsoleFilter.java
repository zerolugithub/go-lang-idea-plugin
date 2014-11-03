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

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GoTestConsoleFilter implements Filter {
  private static final Pattern MESSAGE_PATTERN = Pattern.compile("[ \t]*(\\S+\\.\\w+):(\\d+)[:\\s].*\n");

  @NotNull
  private final Module myModule;
  @NotNull
  private final String myWorkingDirectory;

  public GoTestConsoleFilter(@NotNull Module module, @NotNull String workingDirectory) {
    myModule = module;
    myWorkingDirectory = workingDirectory;
  }

  @Override
  public Result applyFilter(@NotNull String line, int entireLength) {
    Matcher matcher = MESSAGE_PATTERN.matcher(line);
    if (!matcher.matches()) {
      return null;
    }
      
    String fileName = matcher.group(1);
    int lineNumber = StringUtil.parseInt(matcher.group(2), 0) - 1;
    if (lineNumber < 0) {
      return null;
    }

    VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(myWorkingDirectory + "/" + fileName);
    if (virtualFile == null) {
      VirtualFile moduleFile = myModule.getModuleFile();
      if (moduleFile != null) {
        VirtualFile moduleDirectory = moduleFile.getParent();
        if (moduleDirectory != null) {
          virtualFile = moduleDirectory.findFileByRelativePath(fileName);
        }
      }
    }
    if (virtualFile == null) {
      return null;
    }

    HyperlinkInfo hyperlinkInfo = new OpenFileHyperlinkInfo(myModule.getProject(), virtualFile, lineNumber);
    int lineStart = entireLength - line.length();
    return new Result(lineStart + matcher.start(1), lineStart + matcher.end(2), hyperlinkInfo);
  }
}
