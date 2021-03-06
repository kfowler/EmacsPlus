//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandAdapter;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;
import com.intellij.openapi.wm.StatusBar;
import java.util.HashMap;
import java.util.Map;


public abstract class EmacsPlusAction extends TextComponentEditorAction implements EmacsPlusBA {
  private static Map<String, EmacsPlusBA> ourCommandMap;

  protected EmacsPlusAction(final EditorActionHandler defaultHandler) {
    super(defaultHandler);
  }

  public static void addCommandListener(final EmacsPlusBA action, final String id) {
    EmacsPlusAction.ourCommandMap.put(id, action);
  }

  public void before(final CommandEvent e) {}

  public void after(final CommandEvent e) {}

  public static CommandListener getCommandListener() {
    return StaticCommandListener.ourCommandListener;
  }

  public static void infoMessage(final String msg) {
    ApplicationManager.getApplication().invokeLater(() -> StatusBar.Info.set(msg, null));
  }

  public static void errorMessage(final String msg) {
    infoMessage(msg);
  }

  static {
    EmacsPlusAction.ourCommandMap = new HashMap<>();
  }

  private static class EmacsPlusCommandListener extends CommandAdapter {
    public void commandStarted(final CommandEvent event) {
      final EmacsPlusBA thisAction = EmacsPlusAction.ourCommandMap.get(event.getCommandName());
      if (thisAction != null) {
        thisAction.before(event);
      }
    }

    public void commandFinished(final CommandEvent event) {
      final EmacsPlusBA thisAction = EmacsPlusAction.ourCommandMap.get(event.getCommandName());
      if (thisAction != null) {
        thisAction.after(event);
      }
    }
  }

  private static final class StaticCommandListener {
    private static final EmacsPlusCommandListener ourCommandListener;

    static {
      ourCommandListener = new EmacsPlusCommandListener();
    }
  }
}
