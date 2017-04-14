//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import org.jetbrains.annotations.NotNull;


public abstract class EmacsPlusCaretHandler extends EditorActionHandler {
  protected EmacsPlusCaretHandler() {
    super(true);
  }

  public EmacsPlusCaretHandler(final boolean runForEachCaret) {
    super(runForEachCaret);
  }

  private Caret checkCaret(@NotNull final Editor editor, final Caret caret) {
    if (editor == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "editor",
              "com/mulgasoft/emacsplus/handlers/EmacsPlusCaretHandler", "checkCaret"));
    }
    return (caret == null) ? editor.getCaretModel().getCurrentCaret() : caret;
  }

  protected void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
    doXecute(editor, checkCaret(editor, caret), dataContext);
  }

  protected abstract void doXecute(final Editor p0, final Caret p1, final DataContext p2);
}
