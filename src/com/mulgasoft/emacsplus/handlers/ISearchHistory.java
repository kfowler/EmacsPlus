//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.handlers;

import com.intellij.find.FindSettings;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.ui.JBColor;
import com.intellij.ui.LightColors;
import com.mulgasoft.emacsplus.EmacsPlus;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.jetbrains.annotations.NotNull;


public abstract class ISearchHistory extends ISHandler {
  private static int ourSearchIndex;
  private static int ourReplaceIndex;
  private static int ourStartOffset;
  private boolean isSearchField;
  private boolean isReplaceField;
  private boolean isReset;
  private Color myReplaceBackground;
  private static List<String> keys;
  private static List<String> iskeys;
  private JTextComponent myReplaceField;
  private DocumentListener myReplaceListener;

  public ISearchHistory() {
    this.isSearchField = false;
    this.isReplaceField = false;
    this.isReset = false;
    this.myReplaceBackground = JBColor.WHITE;
    this.myReplaceField = null;
    this.myReplaceListener = new DocumentListener() {
      @Override
      public void insertUpdate(final DocumentEvent documentEvent) {
        ISearchHistory.this.restoreBackground();
      }

      @Override
      public void removeUpdate(final DocumentEvent documentEvent) {
        ISearchHistory.this.restoreBackground();
      }

      @Override
      public void changedUpdate(final DocumentEvent documentEvent) {
        ISearchHistory.this.restoreBackground();
      }
    };
  }

  private void checkReset(final Editor isEditor) {
    if (!this.wasPreviousHistory(isEditor) && !this.wasPreviousSearch(isEditor)) {
      this.isReset = true;
      ISearchHistory.ourSearchIndex = 0;
      ISearchHistory.ourReplaceIndex = 0;
      if (this.isReplaceField) {
        this.myReplaceBackground = new JTextField().getBackground();
      }
    } else {
      this.isReset = false;
    }
  }

  private boolean wasPreviousHistory(final Editor isEditor) {
    return ISearchHistory.keys.contains(EmacsPlus.getUltCommand());
  }

  private boolean wasPreviousSearch(final Editor isEditor) {
    boolean result = false;
    if (ISearchHistory.iskeys.contains(EmacsPlus.getUltCommand())) {
      ISearchHistory.ourStartOffset = ISHandler.getTextEditor(isEditor).getCaretModel().getPrimaryCaret().getOffset();
      final String lastSearch = this.getText(isEditor);
      if (!lastSearch.isEmpty()) {
        final String[] vals = this.getRecents();
        if (vals.length > 0 && lastSearch.equals(vals[vals.length - 1])) {
          result = true;
          if (this.isSearchField) {
            ISearchHistory.ourSearchIndex = vals.length - 1;
          } else {
            ISearchHistory.ourReplaceIndex = vals.length - 1;
          }
        }
      }
    }
    return result;
  }

  protected boolean isReset() {
    return this.isReset;
  }

  protected void setText(@NotNull final Editor isEditor, final String[] vals, final int index) {
    if (isEditor == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "isEditor",
              "com/mulgasoft/emacsplus/handlers/ISearchHistory", "setText"));
    }
    if (this.isSearchField && (this.isReset || ISearchHistory.ourSearchIndex != index)) {
      ISearchHistory.ourSearchIndex = index;
      this.setText(isEditor, vals[index]);
    } else if (this.isReplaceField && (this.isReset || ISearchHistory.ourReplaceIndex != index)) {
      ISearchHistory.ourReplaceIndex = index;
      this.setText(isEditor, vals[index]);
    } else {
      this.beep(isEditor);
    }
  }

  protected void setText(@NotNull final Editor isEditor, final String text) {
    if (isEditor == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "isEditor",
              "com/mulgasoft/emacsplus/handlers/ISearchHistory", "setText"));
    }
    ISHandler.getTextEditor(isEditor).getCaretModel().moveToOffset(ISearchHistory.ourStartOffset);
    final JComponent field = isEditor.getComponent();
    if (field instanceof JTextComponent) {
      ((JTextComponent) field).setText(text);
    } else {
      this.beep(isEditor);
    }
  }

  protected String getText(@NotNull final Editor isEditor) {
    if (isEditor == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "isEditor",
              "com/mulgasoft/emacsplus/handlers/ISearchHistory", "getText"));
    }
    String result = "";
    final JComponent field = isEditor.getComponent();
    if (field instanceof JTextComponent) {
      result = ((JTextComponent) field).getText();
    }
    return result;
  }

  protected int getIndex() {
    return this.isSearchField ? ISearchHistory.ourSearchIndex : ISearchHistory.ourReplaceIndex;
  }

  protected void setIndex(final int index) {
    if (this.isSearchField) {
      ISearchHistory.ourSearchIndex = index;
    } else {
      ISearchHistory.ourReplaceIndex = index;
    }
  }

  protected String[] getHistory(final Editor isEditor) {
    this.checkReset(isEditor);
    return this.getRecents();
  }

  private String[] getRecents() {
    final FindSettings settings = FindSettings.getInstance();
    return this.isSearchField ? settings.getRecentFindStrings() : settings.getRecentReplaceStrings();
  }

  protected void beep(final Editor isEditor) {
    final JComponent field = isEditor.getComponent();
    final Color back = field.getBackground();
    field.setBackground(LightColors.CYAN);
    EmacsPlus.beep();
  }

  @Override
  protected boolean isEnabledForCaret(@NotNull final Editor editor, @NotNull final Caret caret, final DataContext dataContext) {
    this.isSearchField = ISHandler.isISearchField(editor);
    this.isReplaceField = ISHandler.isISReplaceField(editor);
    if (this.isReplaceField && this.myReplaceField == null) {
      final JComponent j = editor.getComponent();
      if (j instanceof JTextComponent) {
        this.myReplaceField = (JTextComponent) j;
        this.myReplaceField.getDocument().addDocumentListener(this.myReplaceListener);
      }
    }
    return this.isSearchField || this.isReplaceField;
  }

  private void restoreBackground() {
    this.myReplaceField.setBackground(this.myReplaceBackground);
  }

  static {
    ISearchHistory.ourSearchIndex = 0;
    ISearchHistory.ourReplaceIndex = 0;
    ISearchHistory.ourStartOffset = 0;
    ISearchHistory.keys = Arrays.asList("isearch-history-previous", "isearch-history-next");
    ISearchHistory.iskeys =
        Arrays.asList("isearch-forward", "isearch-forward-regexp", "isearch-backward", "isearch-backward-regexp",
            "query-replace", "query-replace-regexp", IdeBundle.message("command.find.next"));
  }
}
