//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import org.jetbrains.annotations.NotNull;


public class KillRegion extends KillWrapper {
  public KillRegion() {
    super(new myHandler());
  }

  @Override
  protected String getName() {
    return "kill-region";
  }

  public static class myHandler extends CutHandler {
    public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
      if (myCutHandler != null) {
        myCutHandler.executeWriteAction(editor, caret, dataContext);
      }
    }

    protected boolean isEnabledForCaret(@NotNull final Editor editor, @NotNull final Caret caret,
        final DataContext dataContext) {
      if (editor == null) {
        throw new IllegalArgumentException(
            String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "editor",
                "com/mulgasoft/emacsplus/actions/wrapper/KillRegion$myHandler", "isEnabledForCaret"));
      }
      if (caret == null) {
        throw new IllegalArgumentException(
            String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "caret",
                "com/mulgasoft/emacsplus/actions/wrapper/KillRegion$myHandler", "isEnabledForCaret"));
      }
      return caret.hasSelection() && !ISHandler.isInISearch(editor) && super.isEnabledForCaret(editor, caret,
          dataContext);
    }
  }
}
