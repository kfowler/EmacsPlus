//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.motion;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ExprHandler;


public class BackwardWord extends EmacsPlusAction {
  protected BackwardWord() {
    super(new myHandler());
  }

  private static final class myHandler extends ExprHandler {
    @Override
    protected void doXecute(final Editor editor, final Caret caret, final DataContext dataContext) {
      moveToWord(editor, caret, dataContext, -1);
    }
  }
}
