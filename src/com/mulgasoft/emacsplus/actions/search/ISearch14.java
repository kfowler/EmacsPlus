//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindModel;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.swing.text.JTextComponent;


public class ISearch14 implements ISearchDelegate {
  private static final Logger LOG = Logger.getInstance(ISearch14.class);

  private final JComponent searchComp;

  ISearch14(final Editor editor, final JComponent component) {
    searchComp = component;
  }

  private Object invoke(final Object element, final String method) {
    try {
      final Method meth;
      if (element != null && (meth = element.getClass().getMethod(method, (Class<?>[]) new Class[0])) != null) {
        return meth.invoke(element);
      }
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      LOG.error("Failed to invoke " + method, e);
    }
    return null;
  }

  @Override
  public JComponent getComponent() {
    return searchComp;
  }

  @Override
  public JTextComponent getSearchField() {
    return (JTextComponent) invoke(searchComp, "getSearchField");
  }

  @Override
  public JTextComponent getReplaceField() {
    return (JTextComponent) invoke(searchComp, "getReplaceField");
  }

  @Override
  public FindModel getFindModel() {
    return (FindModel) invoke(searchComp, "getFindModel");
  }

  @Override
  public boolean hasMatches() {
    return (boolean) invoke(searchComp, "hasMatches");
  }

  @Override
  public void searchForward() {
    invoke(searchComp, "searchForward");
  }

  @Override
  public void searchBackward() {
    invoke(searchComp, "searchBackward");
  }

  @Override
  public void replaceCurrent() {
    invoke(searchComp, "replaceCurrent");
  }

  @Override
  public void showHistory(final boolean byToolBar, final JTextComponent field) {
    try {
      final Method meth;
      if (searchComp != null
          && (meth = searchComp.getClass().getMethod("showHistory", Boolean.class, JTextComponent.class))
          != null) {
        meth.invoke(searchComp, byToolBar, field);
      }
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      LOG.error("show history failed ", e);
    }
  }

  @Override
  public void requestFocus() {
    invoke(searchComp, "requestFocus");
  }

  @Override
  public void close() {
    invoke(searchComp, "close");
  }
}
