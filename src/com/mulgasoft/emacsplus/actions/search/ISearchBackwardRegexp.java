//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.actionSystem.AnActionEvent;


public class ISearchBackwardRegexp extends ISearchBackward {
  @Override
  public void actionPerformed(final AnActionEvent e) {
    final ISearchDelegate searcher = this.delegateAction(e);
    if (searcher != null) {
      searcher.getFindModel().setRegularExpressions(true);
    }
  }
}
