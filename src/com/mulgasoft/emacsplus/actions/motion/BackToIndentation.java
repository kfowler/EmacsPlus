//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.motion;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.actions.EditorActionUtil;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ExprHandler;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import com.mulgasoft.emacsplus.util.EditorUtil;


public class BackToIndentation extends EmacsPlusAction {
  public BackToIndentation() {
    super(new myHandler());
  }

  private static class myHandler extends ExprHandler {
    @Override
    protected void doXecute(final Editor editor, final Caret caret, final DataContext dataContext) {
      EditorUtil.checkMarkSelection(editor, caret);
      final int line = this.getCorrectLine(editor, caret);
      final int col = EditorActionUtil.findFirstNonSpaceColumnOnTheLine(editor, line);
      if (col >= 0) {
        caret.moveToVisualPosition(new VisualPosition(line, col));
        EditorModificationUtil.scrollToCaret(editor);
      }
    }

    private int getCorrectLine(final Editor editor, final Caret caret) {
      final int caretLine = caret.getLogicalPosition().line;
      final VisualPosition caretPos = caret.getVisualPosition();
      final VisualPosition caretLineStart =
          editor.offsetToVisualPosition(editor.getDocument().getLineStartOffset(caretLine));
      return Math.min(caretPos.line, caretLineStart.line);
    }

    protected boolean isEnabledForCaret(final Editor editor, final Caret caret, final DataContext dataContext) {
      return super.isEnabledForCaret(editor, caret, dataContext) && !ISHandler.isInISearch(editor);
    }
  }
}
