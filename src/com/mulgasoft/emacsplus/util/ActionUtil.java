package com.mulgasoft.emacsplus.util;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.*;


public class ActionUtil {
  private static final Logger LOG = Logger.getInstance(ActionUtil.class);

  private static ActionUtil ourInstance;

  public static ActionUtil getInstance() {
    if (ActionUtil.ourInstance == null) {
      ActionUtil.ourInstance = new ActionUtil();
    }
    return ActionUtil.ourInstance;
  }

  private List<KeyboardShortcut> getKBShortCuts(@NotNull final String actionId) {
    checkNotNull(actionId);
    return this.getKBShortCuts(this.getAction(actionId));
  }

  public List<KeyboardShortcut> getKBShortCuts(@NotNull final AnAction action) {
    checkNotNull(action);
    final List<KeyboardShortcut> kbs = new ArrayList<>();
    final Shortcut[] shortcuts = action.getShortcutSet().getShortcuts();
    for (final Shortcut shortcut : shortcuts) {
      if (shortcut instanceof KeyboardShortcut) {
        kbs.add((KeyboardShortcut) shortcut);
      }
    }
    return kbs;
  }

  public boolean hasKBShortCut(@NotNull final String actionId, @NotNull final KeyStroke key1,
      @Nullable final KeyStroke key2) {
    checkNotNull(actionId);
    checkNotNull(key1);
    return getShortcut(this.getKBShortCuts(actionId), key1, key2) != null;
  }

  public boolean hasKBShortCut(@NotNull final AnAction action, @NotNull final KeyStroke key1,
      @Nullable final KeyStroke key2) {
    checkNotNull(action);
    checkNotNull(key1);
    return getShortcut(this.getKBShortCuts(action), key1, key2) != null;
  }

  public KeyboardShortcut getShortcut(@NotNull final List<KeyboardShortcut> kbs, @NotNull final KeyStroke key1,
      @Nullable final KeyStroke key2) {
    checkNotNull(kbs);
    checkNotNull(key1);
    KeyboardShortcut result = null;
    if (!kbs.isEmpty()) {
      for (final KeyboardShortcut kb : kbs) {
        if (key1.equals(kb.getFirstKeyStroke())) {
          final KeyStroke k2 = kb.getSecondKeyStroke();
          if (key2 == null) {
            if (key2 != k2) {
              continue;
            }
          } else if (!key2.equals(k2)) {
            continue;
          }
          result = kb;
          break;
        }
      }
    }
    return result;
  }

  public boolean isOnceAction(@NotNull final String id) {
    checkNotNull(id);
    boolean result = true;
    final AnAction action = this.getAction(id);
    if (action instanceof EditorAction) {
      final EditorActionHandler handler = ((EditorAction) action).getHandler();
      result = !handler.runForAllCarets();
    }
    return result;
  }

  public AnAction getAction(final String id) {
    return ActionManager.getInstance().getAction(id);
  }

  public EditorAction getEditorAction(final String id) {
    EditorAction result = null;
    final AnAction a = this.getAction(id);
    if (a instanceof EditorAction) {
      result = (EditorAction) a;
    }
    return result;
  }

  public boolean dispatch(@NotNull final String id, @NotNull final DataContext context) {
    checkNotNull(id);
    checkNotNull(context);
    final AnAction dispatch = this.getAction(id);
    return dispatch != null && this.dispatch(dispatch, context);
  }

  public void dispatchLater(@NotNull final String id, @NotNull final DataContext context) {
    checkNotNull(id);
    checkNotNull(context);
    ApplicationManager.getApplication().invokeLater(() -> ActionUtil.this.dispatch(id, context));
  }

  public boolean dispatchLater(@NotNull final AnAction dispatch, @NotNull final DataContext context) {
    checkNotNull(dispatch);
    checkNotNull(context);
    ApplicationManager.getApplication().invokeLater(() -> ActionUtil.this.dispatch(dispatch, context));
    return true;
  }

  private boolean dispatch(@NotNull final AnAction dispatch, @NotNull final DataContext context) {
    checkNotNull(dispatch);
    checkNotNull(context);
    final AnActionEvent event = AnActionEvent.createFromAnAction(dispatch, null, "MainMenu", context);
    dispatch.update(event);
    if (event.getPresentation().isEnabled()) {
      dispatch.actionPerformed(event);
      return true;
    }
    return false;
  }

  public static Method hasMethod(final Object obj, final String method, final Class<?>... params) {
    try {
      return obj.getClass().getMethod(method, params);
    } catch (NoSuchMethodException e) {
      LOG.error("Unknown method " + method, e);
      return null;
    }
  }

  public static Object invokeMethod(final Object obj, final Method method, final Object... params) {
    try {
      if (method != null) {
        return method.invoke(obj, params);
      }
    } catch (Exception e) {
      LOG.error(e);
    }
    return null;
  }
}
