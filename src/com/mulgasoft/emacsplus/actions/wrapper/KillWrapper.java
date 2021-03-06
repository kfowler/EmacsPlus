//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.util.KillCmdUtil;


public abstract class KillWrapper extends EmacsPlusWrapper {
  private KillCmdUtil.KillRingInfo info;

  KillWrapper(final EditorActionHandler defaultHandler) {
    super(defaultHandler);
    info = null;
    EmacsPlusAction.addCommandListener(this, getName());
  }

  protected abstract String getName();

  @Override
  public void before(final CommandEvent e) {
    info = KillCmdUtil.beforeKill();
  }

  @Override
  public void after(final CommandEvent e) {
    try {
      KillCmdUtil.afterKill(info, e.getDocument(), true);
    } finally {
      info = null;
    }
  }

  public static class CutHandler extends EditorWriteActionHandler {
    final EditorWriteActionHandler myCutHandler;

    public CutHandler() {
      myCutHandler = getWrappedHandler("EditorCut");
    }

    private EditorWriteActionHandler getWrappedHandler(final String name) {
      EditorWriteActionHandler result = null;
      final EditorActionHandler handler = EmacsPlusWrapper.getWrappedHandler(name);
      if (handler instanceof EditorWriteActionHandler) {
        result = (EditorWriteActionHandler) handler;
      }
      return result;
    }
  }
}
