//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import org.jetbrains.annotations.NotNull;


public class ISDeleteChar extends EmacsPlusAction {
  public ISDeleteChar() {
    super(new myHandler());
  }

  private static final class myHandler extends ISHandler {
    public void executeWriteAction(final Editor isEditor, final Caret isCaret, final DataContext dataContext) {
      final Editor editor = FileEditorManager.getInstance(isEditor.getProject()).getSelectedTextEditor();
      final int offset = editor.getCaretModel().getOffset();
      final int isOffset = isCaret.getOffset();
      final Document isDoc = isEditor.getDocument();
      if (isOffset > 0) {
        isDoc.deleteString(isOffset - 1, isOffset);
        if (isDoc.getTextLength() == 0) {
          editor.getCaretModel().moveToOffset(offset - 1);
          editor.getSelectionModel().removeSelection();
        }
      }
    }

    @Override
    protected boolean isEnabledForCaret(@NotNull final Editor editor, @NotNull final Caret caret, final DataContext dataContext) {
      return ISHandler.isInISearch(editor);
    }
  }
}
