//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.util.ObjectUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;


public final class ISearchFactory {
  private static final Logger LOG = Logger.getInstance(ISearch14.class);

  private ISearchFactory() {
  }

  public static ISearchDelegate getISearchObject(final Editor editor) {
    ISearchDelegate result = null;
    Class<?> clazz = null;
    final JComponent hc;
    if (editor != null && (hc = editor.getHeaderComponent()) != null) {
      try {
        clazz = Class.forName("com.intellij.find.EditorSearchComponent");
        if (ObjectUtils.tryCast(hc, clazz) != null) {
          result = new ISearch14(editor, hc);
        }
      } catch (ClassNotFoundException e) {
        LOG.error("Could not find EditorSearchComponent", e);
      }
      if (clazz == null) {
        try {
          clazz = Class.forName("com.intellij.find.EditorSearchSession");
          final Method meth = clazz.getMethod("get", Editor.class);
          final Object session = meth.invoke(clazz, editor);
          if (session != null) {
            result = new ISearch15(editor, session, hc);
          }
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
          LOG.error("Could not find EditorSearchSession", e);
        }
      }
    }
    return result;
  }
}
