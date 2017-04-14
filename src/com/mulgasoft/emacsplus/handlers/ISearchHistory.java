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
  private static int ourSearchIndex = 0;
  private static int ourReplaceIndex = 0;
  private static int ourStartOffset = 0;
  private boolean isSearchField;
  private boolean isReplaceField;
  private boolean isReset;
  private Color myReplaceBackground;
  private static final List<String> keys = Arrays.asList("isearch-history-previous", "isearch-history-next");
  private static final List<String> iskeys =
      Arrays.asList("isearch-forward", "isearch-forward-regexp", "isearch-backward", "isearch-backward-regexp",
          "query-replace", "query-replace-regexp", IdeBundle.message("command.find.next"));

  private JTextComponent myReplaceField;
  private final DocumentListener myReplaceListener;

  public ISearchHistory() {
    isSearchField = false;
    isReplaceField = false;
    isReset = false;
    myReplaceBackground = JBColor.WHITE;
    myReplaceField = null;
    myReplaceListener = new DocumentListener() {
      @Override
      public void insertUpdate(final DocumentEvent documentEvent) {
        restoreBackground();
      }

      @Override
      public void removeUpdate(final DocumentEvent documentEvent) {
        restoreBackground();
      }

      @Override
      public void changedUpdate(final DocumentEvent documentEvent) {
        restoreBackground();
      }
    };
  }

  private void checkReset(final Editor isEditor) {
    if (!wasPreviousHistory(isEditor) && !wasPreviousSearch(isEditor)) {
      isReset = true;
      ISearchHistory.ourSearchIndex = 0;
      ISearchHistory.ourReplaceIndex = 0;
      if (isReplaceField) {
        myReplaceBackground = new JTextField().getBackground();
      }
    } else {
      isReset = false;
    }
  }

  private boolean wasPreviousHistory(final Editor isEditor) {
    return ISearchHistory.keys.contains(EmacsPlus.getUltCommand());
  }

  private boolean wasPreviousSearch(final Editor isEditor) {
    boolean result = false;
    if (ISearchHistory.iskeys.contains(EmacsPlus.getUltCommand())) {
      ISearchHistory.ourStartOffset = ISHandler.getTextEditor(isEditor).getCaretModel().getPrimaryCaret().getOffset();
      final String lastSearch = getText(isEditor);
      if (!lastSearch.isEmpty()) {
        final String[] vals = getRecents();
        if (vals.length > 0 && lastSearch.equals(vals[vals.length - 1])) {
          result = true;
          if (isSearchField) {
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
    return isReset;
  }

  protected void setText(@NotNull final Editor isEditor, final String[] vals, final int index) {
    if (isEditor == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "isEditor",
              "com/mulgasoft/emacsplus/handlers/ISearchHistory", "setText"));
    }
    if (isSearchField && (isReset || ISearchHistory.ourSearchIndex != index)) {
      ISearchHistory.ourSearchIndex = index;
      setText(isEditor, vals[index]);
    } else if (isReplaceField && (isReset || ISearchHistory.ourReplaceIndex != index)) {
      ISearchHistory.ourReplaceIndex = index;
      setText(isEditor, vals[index]);
    } else {
      beep(isEditor);
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
      beep(isEditor);
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
    return isSearchField ? ISearchHistory.ourSearchIndex : ISearchHistory.ourReplaceIndex;
  }

  protected void setIndex(final int index) {
    if (isSearchField) {
      ISearchHistory.ourSearchIndex = index;
    } else {
      ISearchHistory.ourReplaceIndex = index;
    }
  }

  protected String[] getHistory(final Editor isEditor) {
    checkReset(isEditor);
    return getRecents();
  }

  private String[] getRecents() {
    final FindSettings settings = FindSettings.getInstance();
    return isSearchField ? settings.getRecentFindStrings() : settings.getRecentReplaceStrings();
  }

  protected void beep(final Editor isEditor) {
    final JComponent field = isEditor.getComponent();
    final Color back = field.getBackground();
    field.setBackground(LightColors.CYAN);
    EmacsPlus.beep();
  }

  @Override
  protected boolean isEnabledForCaret(@NotNull final Editor editor, @NotNull final Caret caret,
      final DataContext dataContext) {
    isSearchField = ISHandler.isISearchField(editor);
    isReplaceField = ISHandler.isISReplaceField(editor);
    if (isReplaceField && myReplaceField == null) {
      final JComponent j = editor.getComponent();
      if (j instanceof JTextComponent) {
        myReplaceField = (JTextComponent) j;
        myReplaceField.getDocument().addDocumentListener(myReplaceListener);
      }
    }
    return isSearchField || isReplaceField;
  }

  private void restoreBackground() {
    myReplaceField.setBackground(myReplaceBackground);
  }
}
