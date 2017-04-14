package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import org.jetbrains.annotations.NotNull;


public class SaveRegion extends KillWrapper {
  public SaveRegion() {
    super(new myHandler());
  }

  @Override
  protected String getName() {
    return "kill-ring-save";
  }

  private static class myHandler extends EditorActionHandler {
    private final EditorActionHandler mySaveHandler;

    private myHandler() {
      mySaveHandler = EmacsPlusWrapper.getWrappedHandler("EditorCopy");
    }

    public void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
      if (mySaveHandler != null) {
        mySaveHandler.execute(editor, caret, dataContext);
      }
    }

    protected boolean isEnabledForCaret(@NotNull final Editor editor, @NotNull final Caret caret, final DataContext dataContext) {
      return !ISHandler.isISearchField(editor) && super.isEnabledForCaret(editor, caret, dataContext);
    }
  }
}
