//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISearchHistory;

public class ISearchHistoryPrevious extends EmacsPlusAction
{
    public ISearchHistoryPrevious() {
        super(new myHandler());
    }

    private static final class myHandler extends ISearchHistory
    {
        public void executeWriteAction(final Editor isEditor, final Caret isCaret, final DataContext dataContext) {
            final String[] vals = this.getHistory(isEditor);
            if (this.isReset()) {
                this.setIndex(vals.length);
            }
            final int index = this.getIndex();
            this.setText(isEditor, vals, index - ((index > 0) ? 1 : 0));
        }
    }
}
