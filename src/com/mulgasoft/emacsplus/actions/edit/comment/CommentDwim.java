//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit.comment;

import com.intellij.codeInsight.actions.MultiCaretCodeInsightActionHandler;
import com.intellij.codeInsight.generation.CommentByBlockCommentHandler;
import com.intellij.codeInsight.generation.CommentByLineCommentHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.handlers.CommentHandler;


public class CommentDwim extends CommentAction {
  @Override
  protected CommentHandler getMyHandler() {
    return new myHandler();
  }

  protected static final class myHandler extends CommentHandler {
    @Override
    protected void invokeAction(final Editor editor, final Caret caret, final DataContext dataContext,
        final PsiFile file) {
      if (caret.hasSelection()) {
        commentSelection(editor, caret, dataContext, file);
      } else {
        commentLine(editor, caret, dataContext);
      }
    }

    private void commentSelection(final Editor editor, final Caret caret, final DataContext d, final PsiFile file) {
      final Document document = editor.getDocument();
      final int start = caret.getSelectionStart();
      final int end = caret.getSelectionEnd();
      final String text = document.getText(new TextRange(start, end)).trim();
      String seq = getLineComment();
      final Project project = editor.getProject();
      if (seq != null && text.startsWith(seq)) {
        invoke(new CommentByLineCommentHandler(), project, editor, caret, file);
      } else if ((seq = getBlockStart()) != null && text.startsWith(seq)) {
        invoke(new CommentByBlockCommentHandler(), project, editor, caret, file);
      } else {
        final int ls = getLineStartOffset(document, start);
        final int es = getLineEndOffset(document, end);
        if ((start == ls && (end == es || end == getLineStartOffset(document, end))) || !hasBlockComments()) {
          invoke(new CommentByLineCommentHandler(), project, editor, caret, file);
        } else {
          invoke(new CommentByBlockCommentHandler(), project, editor, caret, file);
        }
      }
    }

    private void invoke(final MultiCaretCodeInsightActionHandler handler, final Project project, final Editor editor,
        final Caret caret, final PsiFile file) {
      handler.invoke(project, editor, caret, file);
      handler.postInvoke();
    }
  }
}
