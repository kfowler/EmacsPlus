// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.codeInsight.editorActions.JoinLinesHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;

public class DeleteIndentation extends EmacsPlusWrapper
{
    public DeleteIndentation() {
        super((EditorActionHandler)new Handler());
    }
    
    private static class Handler extends EditorWriteActionHandler
    {
        EditorWriteActionHandler wrappedHandler;
        
        public Handler() {
            super(true);
            this.wrappedHandler = (EditorWriteActionHandler)new JoinLinesHandler(EmacsPlusWrapper.getWrappedHandler("EditorJoinLines"));
        }
        
        public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
            if (caret != null) {
                final Document document = editor.getDocument();
                final int cl = caret.getLogicalPosition().line;
                int currentLine = document.getLineNumber(caret.getOffset());
                if (currentLine > 0) {
                    caret.moveToOffset(document.getLineEndOffset(--currentLine));
                    this.wrappedHandler.executeWriteAction(editor, caret, dataContext);
                }
            }
        }
    }
}
