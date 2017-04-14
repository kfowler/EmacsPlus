//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.ISearchHistory;


public class ISearchHistoryNext extends EmacsPlusAction {
  public ISearchHistoryNext() {
    super(new myHandler());
  }

  private static final class myHandler extends ISearchHistory {
    public void executeWriteAction(final Editor isEditor, final Caret isCaret, final DataContext dataContext) {
      final String[] vals = getHistory(isEditor);
      int index = getIndex();
      if (index < vals.length - 1) {
        setText(isEditor, vals, isReset() ? index : (++index));
      } else if (getText(isEditor).isEmpty()) {
        beep(isEditor);
      } else {
        setIndex(vals.length);
        setText(isEditor, "");
      }
    }
  }
}
