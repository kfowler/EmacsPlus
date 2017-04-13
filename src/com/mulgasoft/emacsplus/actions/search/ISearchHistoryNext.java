//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISearchHistory;

public class ISearchHistoryNext extends EmacsPlusAction
{
    public ISearchHistoryNext() {
        super(new myHandler());
    }

    private static final class myHandler extends ISearchHistory
    {
        public void executeWriteAction(final Editor isEditor, final Caret isCaret, final DataContext dataContext) {
            final String[] vals = this.getHistory(isEditor);
            int index = this.getIndex();
            if (index < vals.length - 1) {
                this.setText(isEditor, vals, this.isReset() ? index : (++index));
            }
            else if (this.getText(isEditor).isEmpty()) {
                this.beep(isEditor);
            }
            else {
                this.setIndex(vals.length);
                this.setText(isEditor, "");
            }
        }
    }
}
