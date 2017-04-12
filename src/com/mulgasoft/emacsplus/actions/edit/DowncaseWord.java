// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.CaseHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;

public class DowncaseWord extends EmacsPlusAction
{
    public DowncaseWord() {
        super((EditorActionHandler)new myHandler());
    }
    
    private static class myHandler extends CaseHandler
    {
        public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
            this.caseAction(editor, caret, Cases.LOWER);
        }
    }
}
