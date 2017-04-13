//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.tool;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.mulgasoft.emacsplus.util.EditorUtil;


public class TWInterrupt extends TWAction {
  @Override
  public void actionPerformed(final AnActionEvent e) {
    final Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
    this.checkTW(e, project);
    EditorUtil.closeEditorPopups();
    EditorUtil.activateCurrentEditor(project);
  }

  private void checkTW(final AnActionEvent e, final Project project) {
    final ToolWindow toolWindow = e.getData(PlatformDataKeys.TOOL_WINDOW);
    if (toolWindow != null) {
      final ToolWindowManager manager = ToolWindowManager.getInstance(project);
      if (manager.isMaximized(toolWindow)) {
        manager.setMaximized(toolWindow, false);
      }
    }
  }

  @Override
  protected boolean isValid(final AnActionEvent e) {
    return this.getComponent(e.getDataContext()) != null;
  }
}
