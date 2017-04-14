//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit.comment;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.handlers.CommentHandler;
import com.mulgasoft.emacsplus.util.ActionUtil;


public class CommentKill extends CommentAction {
  @Override
  protected CommentHandler getMyHandler() {
    return new myHandler();
  }

  private static class myHandler extends CommentHandler {
    @Override
    protected void invokeAction(final Editor editor, final Caret caret, final DataContext d, final PsiFile file) {
      final DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
      final CommentRange range = findCommentRange(editor, caret, dataContext);
      if (range != null) {
        caret.moveToOffset(range.getStartOffset());
        caret.setSelection(range.getStartOffset(), range.getEndOffset());
        ActionUtil.getInstance().dispatchLater("Emacs+.KillRegion", dataContext);
        ActionUtil.getInstance().dispatchLater("Emacs+.DeleteHorizSpace", dataContext);
        ActionUtil.getInstance().dispatchLater("EmacsStyleIndent", dataContext);
      }
    }
  }
}
