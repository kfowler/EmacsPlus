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


public class TWAction extends AnAction {
  private static Map<String, KeyStroke> keys;

  protected KeyStroke getKey(final AnActionEvent e) {
    return TWAction.keys.get(e.getActionManager().getId(this));
  }

  public void actionPerformed(final AnActionEvent e) {
    final JComponent component = this.getComponent(e.getDataContext());
    final ActionListener act = component.getActionForKeyStroke(this.getKey(e));
    if (act != null) {
      act.actionPerformed(new ActionEvent(this.getComponent(e.getDataContext()), 0, null));
    }
  }

  protected Editor getEditor(final DataContext dc) {
    return TextComponentEditorAction.getEditorFromContext(dc);
  }

  public void update(final AnActionEvent e) {
    final Presentation presentation = e.getPresentation();
    final DataContext dataContext = e.getDataContext();
    final Editor editor = this.getEditor(dataContext);
    if (editor != null) {
      presentation.setEnabled(false);
    } else {
      presentation.setEnabled(this.isValid(e));
    }
  }

  protected JComponent getComponent(final DataContext dc) {
    JComponent result = null;
    final Object cc = PlatformDataKeys.CONTEXT_COMPONENT.getData(dc);
    if (cc instanceof JComponent) {
      result = (JComponent) cc;
    }
    return result;
  }

  protected boolean isValid(final AnActionEvent e) {
    final JComponent component = this.getComponent(e.getDataContext());
    final KeyStroke key = this.getKey(e);
    return component != null && key != null && component.getActionForKeyStroke(key) != null;
  }

  static {
    TWAction.keys = new HashMap<String, KeyStroke>() {
      {
        this.put("Emacs+.TWSelectPrevious", KeyStroke.getKeyStroke(224, 0));
        this.put("Emacs+.TWSelectNext", KeyStroke.getKeyStroke(225, 0));
        this.put("Emacs+.TWScrollUp", KeyStroke.getKeyStroke(34, 0));
        this.put("Emacs+.TWScrollDown", KeyStroke.getKeyStroke(33, 0));
        this.put("Emacs+.TWBegin", KeyStroke.getKeyStroke(36, 0));
        this.put("Emacs+.TWEnd", KeyStroke.getKeyStroke(35, 0));
      }
    };
  }
}
