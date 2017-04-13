//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.handlers.ISHandler;

public class SaveRegion extends KillWrapper
{
    public SaveRegion() {
        super(new myHandler());
    }

    @Override
    protected String getName() {
        return "kill-ring-save";
    }

    private static class myHandler extends EditorActionHandler
    {
        private EditorActionHandler mySaveHandler;

        private myHandler() {
            this.mySaveHandler = EmacsPlusWrapper.getWrappedHandler("EditorCopy");
        }

        public void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
            if (this.mySaveHandler != null) {
                this.mySaveHandler.execute(editor, caret, dataContext);
            }
        }

        protected boolean isEnabledForCaret(final Editor editor, final Caret caret, final DataContext dataContext) {
            return !ISHandler.isISearchField(editor) && super.isEnabledForCaret(editor, caret, dataContext);
        }
    }
}
