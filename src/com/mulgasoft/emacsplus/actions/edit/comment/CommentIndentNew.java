//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit.comment;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.handlers.CommentHandler;

public class CommentIndentNew extends CommentAction
{
    @Override
    protected CommentHandler getMyHandler() {
        return new myHandler();
    }

    private static class myHandler extends CommentHandler
    {
        @Override
        protected void invokeAction(final Editor editor, final Caret caret, final DataContext dataContext, final PsiFile file) {
            final Document document = editor.getDocument();
            final LogicalPosition pos = caret.getLogicalPosition();
            final int offset = document.getLineEndOffset(pos.line);
            document.insertString(offset, "\n");
            caret.moveToOffset(document.getLineStartOffset(pos.line + 1));
            super.invokeAction(editor, caret, dataContext, file);
        }
    }
}
