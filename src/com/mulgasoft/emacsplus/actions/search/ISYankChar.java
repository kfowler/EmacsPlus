// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;

public class ISYankChar extends EmacsPlusAction
{
    public ISYankChar() {
        super((EditorActionHandler)new myHandler());
    }
    
    private static final class myHandler extends ISHandler
    {
        public void executeWriteAction(final Editor isEditor, final Caret isCaret, final DataContext dataContext) {
            final Editor editor = FileEditorManager.getInstance(isEditor.getProject()).getSelectedTextEditor();
            final int offset = editor.getCaretModel().getOffset();
            final String text = editor.getDocument().getText(new TextRange(offset, offset + 1));
            if (text != null) {
                isEditor.getDocument().insertString(isCaret.getOffset(), (CharSequence)this.fixYank(isEditor, text));
            }
        }
    }
}
