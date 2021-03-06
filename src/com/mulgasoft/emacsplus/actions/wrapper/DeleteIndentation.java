//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.codeInsight.editorActions.JoinLinesHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;


public class DeleteIndentation extends EmacsPlusWrapper {
  public DeleteIndentation() {
    super(new Handler());
  }

  private static class Handler extends EditorWriteActionHandler {
    final EditorActionHandler wrappedHandler;

    Handler() {
      super(true);
      wrappedHandler = new JoinLinesHandler(EmacsPlusWrapper.getWrappedHandler("EditorJoinLines"));
    }

    public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
      if (caret != null) {
        final Document document = editor.getDocument();
        int currentLine = document.getLineNumber(caret.getOffset());
        if (currentLine > 0) {
          caret.moveToOffset(document.getLineEndOffset(--currentLine));
          wrappedHandler.execute(editor, caret, dataContext);
        }
      }
    }
  }
}
