package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.WhiteSpaceHandler;


/**
 * From Emacs docs: Delete all spaces and tabs around point.
 *                  If BACKWARD-ONLY is non-nil, only delete them before point.
 */
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
