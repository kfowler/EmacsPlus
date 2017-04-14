//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.featureStatistics.FeatureUsageTracker;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.Switcher;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ScrollingUtil;
import com.mulgasoft.emacsplus.keys.Keymaps;
import com.mulgasoft.emacsplus.util.ActionUtil;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.*;
import javax.swing.text.TextAction;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.*;


public class SwitchToBuffer extends DumbAwareAction {
  private static final Logger LOG = Logger.getInstance(SwitchToBuffer.class);

  private static Class[] args14;
  private static Class[] args15;
  @NonNls
  private static String SWITCH_CREATE;
  @NonNls
  private static String SWITCH_TITLE;

  public void actionPerformed(@NotNull final AnActionEvent event) {
    checkNotNull(event);
    FeatureUsageTracker.getInstance().triggerFeatureUsed("navigation.recent.files");
    final Switcher.SwitcherPanel switcher = this.getSwitcher(event);
    if (switcher != null) {
      this.keySetup(switcher);
    }
  }

  private Switcher.SwitcherPanel getSwitcher(final AnActionEvent e) {
    final Switcher s = new Switcher();
    Switcher.SwitcherPanel result = null;
    Method meth = null;
    if ((meth = ActionUtil.hasMethod(s, SwitchToBuffer.SWITCH_CREATE, (Class<?>[]) SwitchToBuffer.args14)) != null) {
      result = (Switcher.SwitcherPanel) ActionUtil.invokeMethod(s, meth, e, SwitchToBuffer.SWITCH_TITLE, true);
    } else if ((meth = ActionUtil.hasMethod(s, SwitchToBuffer.SWITCH_CREATE, (Class<?>[]) SwitchToBuffer.args15))
        != null) {
      result = (Switcher.SwitcherPanel) ActionUtil.invokeMethod(s, meth, e, SwitchToBuffer.SWITCH_TITLE, true, null);
    }
    return result;
  }

  public void update(@NotNull final AnActionEvent e) {
    if (e == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "e",
              "com/mulgasoft/emacsplus/actions/wrapper/SwitchToBuffer", "update"));
    }
    e.getPresentation().setEnabled(e.getProject() != null);
  }

  private void keySetup(final JPanel field) {
    final KeyStroke ksG = KeyStroke.getKeyStroke(71, 128);
    final KeyStroke ksN = KeyStroke.getKeyStroke(78, 128);
    final KeyStroke ksP = KeyStroke.getKeyStroke(80, 128);
    final KeyStroke ksD = KeyStroke.getKeyStroke(86, 128);
    final KeyStroke ksU = KeyStroke.getKeyStroke(86, Keymaps.getMeta());
    final KeyStroke ksT = Keymaps.getIntlKeyStroke(153);
    final KeyStroke ksB = Keymaps.getIntlKeyStroke(160);
    this.removeFromActions(field, ksG, null);
    this.removeFromActions(field, ksN, null);
    this.removeFromActions(field, ksP, null);
    this.removeFromActions(field, ksD, null);
    final CustomShortcutSet cuts = CustomShortcutSet.fromString("ESCAPE", "control G");
    this.replaceOnActions(field, KeyStroke.getKeyStroke(27, 0), null, cuts);
    final InputMap im = field.getInputMap();
    final ActionMap am = field.getActionMap();
    am.put("Emacs+.Forward", new TextAction("Emacs+.Forward") {
      @Override
      public void actionPerformed(final ActionEvent e) {
        if (e.getSource() instanceof Switcher.SwitcherPanel) {
          ((Switcher.SwitcherPanel) e.getSource()).goForward();
        }
      }
    });
    am.put("Emacs+.Back", new TextAction("Emacs+.Back") {
      @Override
      public void actionPerformed(final ActionEvent e) {
        if (e.getSource() instanceof Switcher.SwitcherPanel) {
          ((Switcher.SwitcherPanel) e.getSource()).goBack();
        }
      }
    });
    am.put("Emacs+.Down", new TextAction("Emacs+.Down") {
      @Override
      public void actionPerformed(final ActionEvent e) {
        if (e.getSource() instanceof Switcher.SwitcherPanel) {
          ScrollingUtil.movePageDown(((Switcher.SwitcherPanel) e.getSource()).getSelectedList());
        }
      }
    });
    am.put("Emacs+.Up", new TextAction("Emacs+.Up") {
      @Override
      public void actionPerformed(final ActionEvent e) {
        if (e.getSource() instanceof Switcher.SwitcherPanel) {
          ScrollingUtil.movePageUp(((Switcher.SwitcherPanel) e.getSource()).getSelectedList());
        }
      }
    });
    am.put("Emacs+.Top", new TextAction("Emacs+.Top") {
      @Override
      public void actionPerformed(final ActionEvent e) {
        if (e.getSource() instanceof Switcher.SwitcherPanel) {
          ScrollingUtil.moveHome(((Switcher.SwitcherPanel) e.getSource()).getSelectedList());
        }
      }
    });
    am.put("Emacs+.Bottom", new TextAction("Emacs+.Bottom") {
      @Override
      public void actionPerformed(final ActionEvent e) {
        if (e.getSource() instanceof Switcher.SwitcherPanel) {
          ScrollingUtil.moveEnd(((Switcher.SwitcherPanel) e.getSource()).getSelectedList());
        }
      }
    });
    im.put(ksD, "Emacs+.Down");
    im.put(ksU, "Emacs+.Up");
    im.put(ksN, "Emacs+.Forward");
    im.put(ksP, "Emacs+.Back");
    im.put(ksT, "Emacs+.Top");
    im.put(ksB, "Emacs+.Bottom");
  }

  private void replaceOnActions(final JComponent field, final KeyStroke key1, final KeyStroke key2,
      final CustomShortcutSet customs) {
    final List<AnAction> actions = (List<AnAction>) field.getClientProperty("AnAction.shortcutSet");
    final ActionUtil au = ActionUtil.getInstance();
    for (final AnAction act : actions) {
      final List<KeyboardShortcut> kbs = au.getKBShortCuts(act);
      final KeyboardShortcut kb = au.getShortcut(kbs, key1, key2);
      if (kb != null) {
        act.unregisterCustomShortcutSet(field);
        act.registerCustomShortcutSet(customs, field);
        break;
      }
    }
  }

  private void removeFromActions(final JComponent field, final KeyStroke key1, final KeyStroke key2) {
    final List<AnAction> actions = (List<AnAction>) field.getClientProperty("AnAction.shortcutSet");
    if (actions == null) {
      LOG.error("Expected actions in " + field.getName());
      return;
    }
    final ActionUtil au = ActionUtil.getInstance();
    for (final AnAction act : actions) {
      final List<KeyboardShortcut> kbs = au.getKBShortCuts(act);
      final KeyboardShortcut kb = au.getShortcut(kbs, key1, key2);
      if (kb != null) {
        final Shortcut[] cuts = act.getShortcutSet().getShortcuts();
        final Shortcut[] newCuts = new Shortcut[cuts.length - 1];
        int diff = 0;
        for (int i = 0; i < cuts.length; ++i) {
          if (cuts[i] == kb) {
            diff = 1;
          } else {
            newCuts[i - diff] = cuts[i];
          }
        }
        act.unregisterCustomShortcutSet(field);
        act.registerCustomShortcutSet(new CustomShortcutSet(newCuts), field);
        break;
      }
    }
  }

  static {
    SwitchToBuffer.args14 = new Class[]{AnActionEvent.class, String.class, Boolean.TYPE};
    SwitchToBuffer.args15 = new Class[]{AnActionEvent.class, String.class, Boolean.TYPE, VirtualFile[].class};
    SwitchToBuffer.SWITCH_CREATE = "createAndShowSwitcher";
    SwitchToBuffer.SWITCH_TITLE = IdeBundle.message("title.popup.recent.files");
  }
}
