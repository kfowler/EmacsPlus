package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindModel;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import org.jetbrains.annotations.NonNls;


public class ISearch15 implements ISearchDelegate {
  private static final Logger LOG = Logger.getInstance(ISearch15.class);

  @NonNls
  private final static String GET_SEARCH = "getSearchTextComponent";
  @NonNls
  private final static String GET_REPLACE = "getReplaceTextComponent";
  @NonNls
  private final static String GET_FIND = "getFindModel";
  private final JComponent searchComp;
  private final Object session;

  ISearch15(final Editor editor, final Object session, final JComponent component) {
    this.session = session;
    this.searchComp = component;
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

  private Object getAF(final Object o, final String fieldName) {
    Class<?> c = o.getClass();
    try {
      do {
        final Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
          if (fieldName.equals(field.getName())) {
            final boolean access = field.isAccessible();
            Object obj;
            try {
              if (!access) {
                field.setAccessible(true);
              }
              obj = field.get(o);
            } finally {
              if (!access) {
                field.setAccessible(false);
              }
            }
            return obj;
          }
        }
        c = c.getSuperclass();
      } while (c != null);
    } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
      LOG.error("Couldn't get field " + fieldName, e);
    }
    return null;
  }

  @Override
  public JComponent getComponent() {
    return this.searchComp;
  }

  @Override
  public JTextComponent getSearchField() {
    return (JTextComponent) this.invoke(this.searchComp, ISearch15.GET_SEARCH);
  }

  private JTextComponent getSearchWrapper() {
    JTextComponent result = null;
    final Object wrap = this.getAF(this.getComponent(), "mySearchTextComponent");
    if (wrap instanceof JTextComponent) {
      result = (JTextComponent) wrap;
    }
    return result;
  }

  @Override
  public JTextComponent getReplaceField() {
    return (JTextComponent) this.invoke(this.searchComp, ISearch15.GET_REPLACE);
  }

  @Override
  public FindModel getFindModel() {
    return (FindModel) this.invoke(this.session, ISearch15.GET_FIND);
  }

  @Override
  public boolean hasMatches() {
    return (boolean) this.invoke(this.session, "hasMatches");
  }

  @Override
  public void searchForward() {
    this.invoke(this.session, "searchForward");
  }

  @Override
  public void searchBackward() {
    this.invoke(this.session, "searchBackward");
  }

  @Override
  public void replaceCurrent() {
    this.invoke(this.session, "replaceCurrent");
  }

  @Override
  public void showHistory(final boolean byToolBar, final JTextComponent field) {
  }

  @Override
  public void requestFocus() {
    this.invoke(this.searchComp, "requstFocus");
  }

  @Override
  public void close() {
    this.invoke(this.session, "close");
  }
}
