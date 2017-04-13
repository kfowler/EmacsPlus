//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;


public class ISYankLine extends EmacsPlusAction {
  public ISYankLine() {
    super(new myHandler());
  }

  private static final class myHandler extends ISHandler {
    public void executeWriteAction(final Editor isEditor, final Caret isCaret, final DataContext dataContext) {
      final Editor editor = FileEditorManager.getInstance(isEditor.getProject()).getSelectedTextEditor();
      final int offset = editor.getCaretModel().getOffset();
      final Document doc = editor.getDocument();
      final String text = doc.getText(new TextRange(offset, doc.getLineEndOffset(doc.getLineNumber(offset))));
      if (text != null) {
        isEditor.getDocument().insertString(isCaret.getOffset(), this.fixYank(isEditor, text));
      }
    }
  }
}
