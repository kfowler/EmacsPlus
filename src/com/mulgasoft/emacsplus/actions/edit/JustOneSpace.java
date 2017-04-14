package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.WhiteSpaceHandler;


/**
 * just-one-space is an interactive compiled Lisp function in ‘simple.el’.
 *
 * It is bound to M-SPC.
 *
 * (just-one-space &optional N)
 *
 * Delete all spaces and tabs around point, leaving one space (or N spaces).
 * If N is negative, delete newlines as well, leaving -N spaces.
 * See also ‘cycle-spacing’.
 *
 */
class JustOneSpace extends EmacsPlusAction {
  public JustOneSpace() {
    super(new myHandler());
  }

  private static class myHandler extends WhiteSpaceHandler {
    public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
      transformSpace(editor, caret, dataContext, " ", false);
    }
  }
}
