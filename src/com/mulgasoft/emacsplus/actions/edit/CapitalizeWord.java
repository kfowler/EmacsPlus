package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.CaseHandler;
import com.mulgasoft.emacsplus.handlers.ISHandler;

/**
 * Action to capitalize the first letter of a selected word.
 * E.g., "example" -> "Example"
 */
class CapitalizeWord extends EmacsPlusAction {
  public CapitalizeWord() {
    super(new myHandler());
  }

  private static class myHandler extends CaseHandler {
    public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
      this.caseAction(editor, caret, Cases.CAP);
    }

    protected boolean isEnabledForCaret(final Editor editor, final Caret caret, final DataContext dataContext) {
      return !ISHandler.isInISearch(editor) && super.isEnabledForCaret(editor, caret, dataContext);
    }
  }
}
