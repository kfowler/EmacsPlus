//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.util;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionUtil
{
    @NonNls
    public static final String CR = "\n";
    @NonNls
    public static final String SPACE = " ";
    @NonNls
    public static final String EMPTY_STR = "";
    private static ActionUtil ourInstance;

    public static ActionUtil getInstance() {
        if (ActionUtil.ourInstance == null) {
            ActionUtil.ourInstance = new ActionUtil();
        }
        return ActionUtil.ourInstance;
    }

    public List<KeyboardShortcut> getKBShortCuts(@NotNull final String actionId) {
        if (actionId == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "actionId", "com/mulgasoft/emacsplus/util/ActionUtil", "getKBShortCuts"));
        }
        return this.getKBShortCuts(this.getAction(actionId));
    }

    public List<KeyboardShortcut> getKBShortCuts(@NotNull final AnAction action) {
        if (action == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "action", "com/mulgasoft/emacsplus/util/ActionUtil", "getKBShortCuts"));
        }
        final List<KeyboardShortcut> kbs = new ArrayList<>();
        if (action != null) {
            final Shortcut[] arr$;
            final Shortcut[] shortcuts = arr$ = action.getShortcutSet().getShortcuts();
            for (final Shortcut shortcut : arr$) {
                if (shortcut instanceof KeyboardShortcut) {
                    kbs.add((KeyboardShortcut)shortcut);
                }
            }
        }
        return kbs;
    }

    public boolean hasKBShortCut(@NotNull final String actionId, @NotNull final KeyStroke key1, @Nullable final KeyStroke key2) {
        if (actionId == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "actionId", "com/mulgasoft/emacsplus/util/ActionUtil", "hasKBShortCut"));
        }
        if (key1 == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "key1", "com/mulgasoft/emacsplus/util/ActionUtil", "hasKBShortCut"));
        }
        return this.getShortcut(this.getKBShortCuts(actionId), key1, key2) != null;
    }

    public boolean hasKBShortCut(@NotNull final AnAction action, @NotNull final KeyStroke key1, @Nullable final KeyStroke key2) {
        if (action == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "action", "com/mulgasoft/emacsplus/util/ActionUtil", "hasKBShortCut"));
        }
        if (key1 == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "key1", "com/mulgasoft/emacsplus/util/ActionUtil", "hasKBShortCut"));
        }
        return this.getShortcut(this.getKBShortCuts(action), key1, key2) != null;
    }

    public KeyboardShortcut getShortcut(@NotNull final List<KeyboardShortcut> kbs, @NotNull final KeyStroke key1, @Nullable final KeyStroke key2) {
        if (kbs == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "kbs", "com/mulgasoft/emacsplus/util/ActionUtil", "getShortcut"));
        }
        if (key1 == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "key1", "com/mulgasoft/emacsplus/util/ActionUtil", "getShortcut"));
        }
        KeyboardShortcut result = null;
        if (!kbs.isEmpty()) {
            for (final KeyboardShortcut kb : kbs) {
                if (key1.equals(kb.getFirstKeyStroke())) {
                    final KeyStroke k2 = kb.getSecondKeyStroke();
                    if (key2 == null) {
                        if (key2 != k2) {
                            continue;
                        }
                    }
                    else if (!key2.equals(k2)) {
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
        if (id == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "id", "com/mulgasoft/emacsplus/util/ActionUtil", "isOnceAction"));
        }
        boolean result = true;
        final AnAction action = this.getAction(id);
        if (action instanceof EditorAction) {
            final EditorActionHandler handler = ((EditorAction)action).getHandler();
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
            result = (EditorAction)a;
        }
        return result;
    }

    public boolean dispatch(@NotNull final String id, @NotNull final DataContext context) {
        if (id == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "id", "com/mulgasoft/emacsplus/util/ActionUtil", "dispatch"));
        }
        if (context == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "context", "com/mulgasoft/emacsplus/util/ActionUtil", "dispatch"));
        }
        final AnAction dispatch = this.getAction(id);
        return dispatch != null && this.dispatch(dispatch, context);
    }

    public boolean dispatchLater(@NotNull final String id, @NotNull final DataContext context) {
        if (id == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "id", "com/mulgasoft/emacsplus/util/ActionUtil", "dispatchLater"));
        }
        if (context == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "context", "com/mulgasoft/emacsplus/util/ActionUtil", "dispatchLater"));
        }
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ActionUtil.this.dispatch(id, context);
            }
        });
        return true;
    }

    public boolean dispatchLater(@NotNull final AnAction dispatch, @NotNull final DataContext context) {
        if (dispatch == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "dispatch", "com/mulgasoft/emacsplus/util/ActionUtil", "dispatchLater"));
        }
        if (context == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "context", "com/mulgasoft/emacsplus/util/ActionUtil", "dispatchLater"));
        }
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                ActionUtil.this.dispatch(dispatch, context);
            }
        });
        return true;
    }

    public boolean dispatch(@NotNull final AnAction dispatch, @NotNull final DataContext context) {
        if (dispatch == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "dispatch", "com/mulgasoft/emacsplus/util/ActionUtil", "dispatch"));
        }
        if (context == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "context", "com/mulgasoft/emacsplus/util/ActionUtil", "dispatch"));
        }
        final AnActionEvent event = AnActionEvent.createFromAnAction(dispatch, null, "MainMenu", context);
        dispatch.update(event);
        if (event.getPresentation().isEnabled()) {
            dispatch.actionPerformed(event);
            return true;
        }
        return false;
    }

    public static Method hasMethod(final Object obj, final String method, final Class<?>... params) {
        Method result = null;
        try {
            result = obj.getClass().getMethod(method, params);
        }
        catch (NoSuchMethodException ex) {}
        return result;
    }

    public static Method hasMethod(final Object obj, final String method) {
        return hasMethod(obj, method, (Class<?>[])null);
    }

    public static Object invokeMethod(final Object obj, final Method method, final Object... params) {
        Object result = null;
        try {
            if (method != null) {
                result = method.invoke(obj, params);
            }
        }
        catch (Exception ex) {}
        return result;
    }

    public static Object invokeMethod(final Object obj, final Method method) {
        return invokeMethod(obj, method, (Object[])null);
    }

    static {
        ActionUtil.ourInstance = null;
    }
}
