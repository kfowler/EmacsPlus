//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.textarea.TextComponentEditor;
import com.intellij.openapi.ui.DialogWrapper;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import com.mulgasoft.emacsplus.util.EditorUtil;
import java.awt.*;
import org.jetbrains.annotations.NotNull;


public class KeyboardQuit extends EmacsPlusWrapper {
  public KeyboardQuit() {
    super(new myHandler(EmacsPlusWrapper.getWrappedHandler("EditorEscape")));
    setInjectedContext(true);
  }

  private static class myHandler extends EditorActionHandler {
    private boolean isTextComponent;
    EditorActionHandler wrappedHandler;

    myHandler(@NotNull final EditorActionHandler wrappedHandler) {
      if (wrappedHandler == null) {
        throw new IllegalArgumentException(
            String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "wrappedHandler",
                "com/mulgasoft/emacsplus/actions/wrapper/KeyboardQuit$myHandler", "<init>"));
      }
      isTextComponent = false;
      this.wrappedHandler = null;
      this.wrappedHandler = wrappedHandler;
    }

    public void execute(final Editor editor, final DataContext dataContext) {
      if (isTextComponent) {
        isTextComponent = false;
        if (!cancelIfDialog(editor.getComponent())) {
          editor.getCaretModel().removeCaret(editor.getCaretModel().getPrimaryCaret());
        }
        EditorUtil.closeEditorPopups();
      } else {
        wrappedHandler.execute(editor, dataContext);
      }
      EditorUtil.closeEditorPopups();
      EditorUtil.activateCurrentEditor(CommonDataKeys.PROJECT.getData(dataContext));
    }

    public boolean isEnabled(final Editor editor, final DataContext dataContext) {
      if (editor instanceof TextComponentEditor) {
        if (ISHandler.isInISearch(editor)) {
          return false;
        }
        isTextComponent = true;
      }
      return isTextComponent || wrappedHandler.isEnabled(editor, dataContext) || (editor instanceof EditorEx
          && ((EditorEx) editor).isStickySelection());
    }

    private boolean cancelIfDialog(final Component component) {
      final DialogWrapper dw = DialogWrapper.findInstance(component);
      final boolean result = dw != null;
      if (result) {
        dw.doCancelAction();
      }
      return result;
    }
  }
}
