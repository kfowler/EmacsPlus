//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.YankHandler;


public class Yank extends Yanking {
  public Yank() {
    super(new myHandler());
    EmacsPlusAction.addCommandListener(this, "yank");
  }

  @Override
  public void before(final CommandEvent e) {
    this.reset();
  }

  private static final class myHandler extends YankHandler {
    public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
      Yanking.yanked(this.yankIt(editor, caret));
    }
  }
}
