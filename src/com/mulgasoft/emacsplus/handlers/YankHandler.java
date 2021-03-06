//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.textarea.TextComponentEditor;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.util.KillCmdUtil;
import java.awt.datatransfer.Transferable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;


public abstract class YankHandler extends EmacsPlusWriteHandler {
  private static boolean ourYankReplace;

  private Transferable getData() {
    return EditorModificationUtil.getContentsToPasteToEditor(null);
  }

  private String getText(final Transferable data, final Editor editor) {
    return KillCmdUtil.getTransferableText(data, getSepr(editor));
  }

  @NonNls
  protected String getSepr(final Editor editor) {
    return "\n";
  }

  public TextRange paste(@NotNull final Editor editor, @NotNull final Caret caret, @NotNull final Transferable data,
      final int length) {
    if (editor == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "editor",
              "com/mulgasoft/emacsplus/handlers/YankHandler", "paste"));
    }
    if (caret == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "caret",
              "com/mulgasoft/emacsplus/handlers/YankHandler", "paste"));
    }
    if (data == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "data",
              "com/mulgasoft/emacsplus/handlers/YankHandler", "paste"));
    }
    final String text = getText(data, editor);
    final int caretOffset = caret.getOffset();
    final int newOff = caretOffset - length;
    TextRange result = null;
    if (newOff >= 0) {
      result = new TextRange(newOff, newOff + text.length());
      editor.getDocument().replaceString(caretOffset - length, caretOffset, text);
    }
    return result;
  }

  private TextRange paste(@NotNull final Editor editor, @NotNull final TextRange range,
      @NotNull final Transferable data) {
    if (editor == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "editor",
              "com/mulgasoft/emacsplus/handlers/YankHandler", "paste"));
    }
    if (range == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "range",
              "com/mulgasoft/emacsplus/handlers/YankHandler", "paste"));
    }
    if (data == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "data",
              "com/mulgasoft/emacsplus/handlers/YankHandler", "paste"));
    }
    final String text = getText(data, editor);
    final TextRange result = new TextRange(range.getStartOffset(), range.getStartOffset() + text.length());
    editor.getDocument().replaceString(range.getStartOffset(), range.getEndOffset(), text);
    return result;
  }

  private TextRange paste(@NotNull final Editor editor, @NotNull final Caret caret, @NotNull final Transferable data) {
    if (editor == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "editor",
              "com/mulgasoft/emacsplus/handlers/YankHandler", "paste"));
    }
    if (caret == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "caret",
              "com/mulgasoft/emacsplus/handlers/YankHandler", "paste"));
    }
    if (data == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "data",
              "com/mulgasoft/emacsplus/handlers/YankHandler", "paste"));
    }
    final String text = getText(data, editor);
    final int caretOffset = caret.getOffset();
    final TextRange result = new TextRange(caretOffset, caretOffset + text.length());
    if (editor instanceof TextComponentEditor) {
      final Document doc = editor.getDocument();
      doc.insertString(caret.getOffset(), text);
    } else {
      EditorModificationUtil.insertStringAtCaret(editor, text, false, true);
    }
    return result;
  }

  protected TextRange yankIt(final Editor editor, final Caret caret) {
    final Transferable data = getData();
    final boolean isReplace = YankHandler.ourYankReplace || editor.isOneLineMode();
    TextRange replace = null;
    final SelectionModel sm = editor.getSelectionModel();
    if (sm.hasSelection()) {
      replace = new TextRange(sm.getSelectionStart(), sm.getSelectionEnd());
      sm.removeSelection();
    }
    if (editor instanceof EditorEx) {
      final EditorEx editorEx = (EditorEx) editor;
      if (editorEx.isStickySelection()) {
        editorEx.setStickySelection(false);
      }
    }
    TextRange location;
    if (replace == null || !isReplace) {
      location = paste(editor, caret, data);
    } else {
      location = paste(editor, replace, data);
    }
    return location;
  }

  protected boolean isEnabledForCaret(@NotNull final Editor editor, @NotNull final Caret caret, final DataContext dataContext) {
    return !ISHandler.isISearchField(editor) && super.isEnabledForCaret(editor, caret, dataContext);
  }

  static {
    YankHandler.ourYankReplace = true;
  }
}
