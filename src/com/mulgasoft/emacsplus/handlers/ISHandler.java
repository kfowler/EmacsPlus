//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.handlers;

import com.intellij.find.FindModel;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.mulgasoft.emacsplus.actions.search.ISearchDelegate;
import com.mulgasoft.emacsplus.actions.search.ISearchFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import org.jetbrains.annotations.NonNls;


public abstract class ISHandler extends YankHandler {
  private static final Logger LOG = Logger.getInstance(ISHandler.class);

  private char[] regTokens;
  @NonNls
  private static String[] SearchMethods;
  @NonNls
  private static String[] ReplaceMethods;

  public ISHandler() {
    this.regTokens = new char[]{'{', '}', '(', ')', '[', ']', '\\', '^', '$', '.', '|', '?', '*', '+'};
  }

  public static FindModel getFindModel(final Editor editor) {
    FindModel findModel = null;
    final ISearchDelegate searcher = ISearchFactory.getISearchObject(editor);
    if (searcher != null) {
      findModel = searcher.getFindModel();
    }
    return findModel;
  }

  private static Object invokeOnHeader(final Editor editor, final String[] methods) {
    Object result = null;
    final JComponent hc = editor.getHeaderComponent();
    if (hc != null) {
      for (final String m : methods) {
        try {
          final Method meth;
          if ((meth = hc.getClass().getMethod(m, (Class<?>[]) new Class[0])) != null) {
            result = meth.invoke(hc);
            break;
          }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
          LOG.error("invoke on header failed", e);
        }
      }
    }
    return result;
  }

  private static JTextComponent getSearchField(final Editor editor) {
    return (JTextComponent) invokeOnHeader(getTextEditor(editor), ISHandler.SearchMethods);
  }

  private static JTextComponent getReplaceField(final Editor editor) {
    return (JTextComponent) invokeOnHeader(getTextEditor(editor), ISHandler.ReplaceMethods);
  }

  public static Editor getTextEditor(final Editor isEditor) {
    return FileEditorManager.getInstance(isEditor.getProject()).getSelectedTextEditor();
  }

  public static Editor getTextEditor(final Project project) {
    return FileEditorManager.getInstance(project).getSelectedTextEditor();
  }

  public static boolean isISearchField(final Editor isEditor) {
    final JTextComponent field = getSearchField(isEditor);
    return field != null && field == isEditor.getComponent();
  }

  static boolean isISReplaceField(final Editor isEditor) {
    final JTextComponent field = getReplaceField(isEditor);
    return field != null && field == isEditor.getComponent();
  }

  public static boolean isInISearch(final Editor isEditor) {
    return isISearchField(isEditor) || isISReplaceField(isEditor);
  }

  protected boolean isRegexp(final Editor isEditor) {
    final FindModel findModel = getFindModel(getTextEditor(isEditor));
    return findModel != null && findModel.isRegularExpressions();
  }

  protected String fixYank(final Editor isEditor, final String text) {
    String result = text;
    if (this.isRegexp(isEditor)) {
      final StringBuilder sb = new StringBuilder();
      for (final char c : text.toCharArray()) {
        if (c == '\n') {
          sb.append('\\');
          sb.append('n');
        } else {
          for (final char r : this.regTokens) {
            if (c == r) {
              sb.append('\\');
              break;
            }
          }
          sb.append(c);
        }
      }
      result = sb.toString();
    }
    return result;
  }

  @Override
  protected boolean isEnabledForCaret(final Editor editor, final Caret caret, final DataContext dataContext) {
    return isISearchField(editor);
  }

  static {
    ISHandler.SearchMethods = new String[]{"getSearchField", "getSearchTextComponent"};
    ISHandler.ReplaceMethods = new String[]{"getReplaceField", "getReplaceTextComponent"};
  }
}
