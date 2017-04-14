package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.CaseHandler;


/**
 * DowncaseWord: Convert to lower case from point to end of word, moving over.
 *
 * If point is in the middle of a word, the part of that word before point
 * is ignored when moving forward.
 */
public class DowncaseWord extends EmacsPlusAction {
  public DowncaseWord() {
    super(new myHandler());
  }

  private static class myHandler extends CaseHandler {
    public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
      this.caseAction(editor, caret, Cases.LOWER);
    }
  }
}
