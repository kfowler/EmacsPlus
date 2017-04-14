//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.tool;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;


class TWAction extends AnAction {
  private static Map<String, KeyStroke> keys;

  private KeyStroke getKey(final AnActionEvent e) {
    return TWAction.keys.get(e.getActionManager().getId(this));
  }

  public void actionPerformed(final AnActionEvent e) {
    final JComponent component = getComponent(e.getDataContext());
    final ActionListener act = component.getActionForKeyStroke(getKey(e));
    if (act != null) {
      act.actionPerformed(new ActionEvent(getComponent(e.getDataContext()), 0, null));
    }
  }

  private Editor getEditor(final DataContext dc) {
    return TextComponentEditorAction.getEditorFromContext(dc);
  }

  public void update(final AnActionEvent e) {
    final Presentation presentation = e.getPresentation();
    final DataContext dataContext = e.getDataContext();
    final Editor editor = getEditor(dataContext);
    if (editor != null) {
      presentation.setEnabled(false);
    } else {
      presentation.setEnabled(isValid(e));
    }
  }

  JComponent getComponent(final DataContext dc) {
    JComponent result = null;
    final Object cc = PlatformDataKeys.CONTEXT_COMPONENT.getData(dc);
    if (cc instanceof JComponent) {
      result = (JComponent) cc;
    }
    return result;
  }

  boolean isValid(final AnActionEvent e) {
    final JComponent component = getComponent(e.getDataContext());
    final KeyStroke key = getKey(e);
    return component != null && key != null && component.getActionForKeyStroke(key) != null;
  }

  static {
    TWAction.keys = new HashMap<String, KeyStroke>() {
      {
        put("Emacs+.TWSelectPrevious", KeyStroke.getKeyStroke(224, 0));
        put("Emacs+.TWSelectNext", KeyStroke.getKeyStroke(225, 0));
        put("Emacs+.TWScrollUp", KeyStroke.getKeyStroke(34, 0));
        put("Emacs+.TWScrollDown", KeyStroke.getKeyStroke(33, 0));
        put("Emacs+.TWBegin", KeyStroke.getKeyStroke(36, 0));
        put("Emacs+.TWEnd", KeyStroke.getKeyStroke(35, 0));
      }
    };
  }
}
