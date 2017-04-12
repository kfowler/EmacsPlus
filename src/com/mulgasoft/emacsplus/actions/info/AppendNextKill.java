// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.info;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.EmacsPlusCaretHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusBA;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;

public class AppendNextKill extends EmacsPlusAction
{
    public AppendNextKill() {
        super(new myHandler(false));
        EmacsPlusAction.addCommandListener(this, "append-next-kill");
    }
    
    private static class myHandler extends EmacsPlusCaretHandler
    {
        public myHandler(final boolean runForEachCaret) {
            super(runForEachCaret);
        }
        
        @Override
        protected void doXecute(final Editor editor, final Caret caret, final DataContext dataContext) {
        }
    }
}
