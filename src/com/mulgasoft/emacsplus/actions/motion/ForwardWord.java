// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.motion;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.ExprHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;

public class ForwardWord extends EmacsPlusAction
{
    protected ForwardWord() {
        super(new myHandler());
    }
    
    private static final class myHandler extends ExprHandler
    {
        @Override
        protected void doXecute(final Editor editor, final Caret caret, final DataContext dataContext) {
            this.moveToWord(editor, caret, dataContext, 1);
        }
    }
}
