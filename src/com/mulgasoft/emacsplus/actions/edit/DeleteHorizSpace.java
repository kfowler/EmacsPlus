//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.WhiteSpaceHandler;


public class DeleteHorizSpace extends EmacsPlusAction {
  public DeleteHorizSpace() {
    this(new myHandler());
  }

  protected DeleteHorizSpace(final EditorActionHandler defaultHandler) {
    super(defaultHandler);
  }

  private static class myHandler extends WhiteSpaceHandler {
    public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
      this.transformSpace(editor, caret, dataContext, "", false);
    }
  }
}
