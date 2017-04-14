//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;


public class ISYankNextWord extends EmacsPlusAction {
  public ISYankNextWord() {
    super(new myHandler());
  }

  private static final class myHandler extends ISHandler {
    public void executeWriteAction(final Editor isEditor, final Caret isCaret, final DataContext dataContext) {
      final Document doc = isEditor.getDocument();
      final Editor editor = ISHandler.getTextEditor(isEditor);
      final String text = getNextWord(editor, isEditor.isOneLineMode() && !isRegexp(isEditor), false);
      if (text != null && !text.isEmpty()) {
        doc.insertString(isCaret.getOffset(), fixYank(isEditor, text));
      }
    }
  }
}
