// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;

public class ISForwardDelete extends EmacsPlusAction
{
    public ISForwardDelete() {
        super((EditorActionHandler)new myHandler());
    }
    
    private static final class myHandler extends ISHandler
    {
        public void executeWriteAction(final Editor isEditor, final Caret isCaret, final DataContext dataContext) {
            final int isOffset = isCaret.getOffset();
            final Document isDoc = isEditor.getDocument();
            if (isOffset < isDoc.getTextLength()) {
                isDoc.deleteString(isOffset, isOffset + 1);
                if (isDoc.getTextLength() == 0) {
                    final Editor editor = FileEditorManager.getInstance(isEditor.getProject()).getSelectedTextEditor();
                    final int offset = editor.getCaretModel().getOffset();
                    editor.getCaretModel().moveToOffset(offset - 1);
                    editor.getSelectionModel().removeSelection();
                }
            }
        }
    }
}
