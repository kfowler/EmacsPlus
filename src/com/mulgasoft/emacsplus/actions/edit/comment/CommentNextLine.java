// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.edit.comment;

import com.intellij.psi.PsiElement;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiFile;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.CommentHandler;

public class CommentNextLine extends CommentAction
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
            LogicalPosition pos = null;
            final PsiElement ele = this.inComment(editor, caret);
            if (ele != null && ele.getTextRange() != null) {
                pos = editor.offsetToLogicalPosition(ele.getTextRange().getEndOffset());
            }
            else {
                pos = caret.getLogicalPosition();
            }
            final int line = pos.line + 1;
            if (line < document.getLineCount()) {
                caret.moveToOffset(document.getLineStartOffset(line));
                super.invokeAction(editor, caret, dataContext, file);
            }
        }
    }
}
