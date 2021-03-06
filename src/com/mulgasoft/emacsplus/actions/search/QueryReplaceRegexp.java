//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.command.CommandEvent;


public class QueryReplaceRegexp extends QueryReplace {
  @Override
  protected String getName() {
    return "query-replace-regexp";
  }

  @Override
  public void after(final CommandEvent e) {
    super.after(e);
    final ISearchDelegate searcher = getSearcher();
    if (searcher != null) {
      searcher.getFindModel().setRegularExpressions(true);
    }
  }
}
