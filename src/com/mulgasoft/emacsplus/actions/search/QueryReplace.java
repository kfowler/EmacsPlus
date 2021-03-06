//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.mulgasoft.emacsplus.EmacsPlus;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


class QueryReplace extends ISearchForward implements FocusListener, KeyListener {
  public QueryReplace() {
    super(true);
  }

  @Override
  String getName() {
    return "query-replace";
  }

  @Override
  protected void changeFieldActions(final ISearchDelegate searcher, final boolean isReplace) {
    super.changeFieldActions(searcher, isReplace);
    super.changeFieldActions(searcher, true);
    searcher.getReplaceField().addFocusListener(this);
    searcher.getReplaceField().addKeyListener(this);
  }

  @Override
  public void focusGained(final FocusEvent e) {
    EmacsPlus.resetCommand(getName());
  }

  @Override
  public void focusLost(final FocusEvent e) {
  }

  @Override
  public void keyTyped(final KeyEvent e) {
    EmacsPlus.resetCommand(getName());
  }

  @Override
  public void keyPressed(final KeyEvent e) {
  }

  @Override
  public void keyReleased(final KeyEvent e) {
  }
}
