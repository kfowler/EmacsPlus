// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;

public class KillWholeLine extends KillWrapper
{
    public KillWholeLine() {
        super((EditorActionHandler)new myHandler());
    }
    
    @Override
    protected String getName() {
        return "kill-whole-line";
    }
    
    private static class myHandler extends CutHandler
    {
        public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
            if (editor.getSelectionModel().hasSelection(true)) {
                editor.getSelectionModel().removeSelection(true);
            }
            this.myCutHandler.executeWriteAction(editor, caret, dataContext);
        }
    }
}
