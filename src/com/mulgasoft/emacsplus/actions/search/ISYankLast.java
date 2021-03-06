//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;


public class ISYankLast extends EmacsPlusAction {
  public ISYankLast() {
    super(new myHandler());
  }

  private static final class myHandler extends ISHandler {
    @Override
    protected String getSepr(final Editor editor) {
      return editor.isOneLineMode() ? " " : "\n";
    }

    public void executeWriteAction(final Editor isEditor, final Caret isCaret, final DataContext dataContext) {
      yankIt(isEditor, isCaret);
    }
  }
}
