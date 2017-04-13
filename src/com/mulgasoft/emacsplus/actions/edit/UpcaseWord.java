//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.CaseHandler;

public class UpcaseWord extends EmacsPlusAction
{
    public UpcaseWord() {
        super(new myHandler());
    }

    private static class myHandler extends CaseHandler
    {
        public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
            this.caseAction(editor, caret, Cases.UPPER);
        }
    }
}
