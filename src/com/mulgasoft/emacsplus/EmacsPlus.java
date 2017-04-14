//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import java.awt.*;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.*;


public class EmacsPlus implements ApplicationComponent {
  private static final Logger LOG = Logger.getInstance(EmacsPlus.class);

  private static final String EMACS_PLUS = "Emacs+";

  private static String ultCommand = null;
  private static String penultCommand = null;
  private static final boolean visualBeep = false;

  public void initComponent() {
    CommandProcessor.getInstance().addCommandListener(new CommandListener() {
      public void beforeCommandFinished(final CommandEvent event) {
      }

      public void undoTransparentActionStarted() {
      }

      public void undoTransparentActionFinished() {
      }

      public void commandStarted(final CommandEvent event) {
      }

      public void commandFinished(final CommandEvent event) {
        setUltCommand(event.getCommandName());
      }
    });
    CommandProcessor.getInstance().addCommandListener(EmacsPlusAction.getCommandListener());
  }

  private static void setUltCommand(final String name) {
    EmacsPlus.penultCommand = EmacsPlus.ultCommand;
    EmacsPlus.ultCommand = name;
  }

  public static String getUltCommand() {
    return EmacsPlus.ultCommand;
  }

  public static String getPenultCommand() {
    return EmacsPlus.penultCommand;
  }

  public static void beep() {
    beep(false);
  }

  public static void beep(final boolean reset) {
    if (!EmacsPlus.visualBeep) {
      Toolkit.getDefaultToolkit().beep();
    }
    if (reset) {
      setUltCommand("");
    }
  }

  public static void resetCommand(@NotNull final String name) {
    checkNotNull(name);
    setUltCommand(name);
  }

  public void disposeComponent() {
    System.out.println("disposeComponent");
  }

  @NotNull
  public String getComponentName() {
    return EMACS_PLUS;
  }

  @NotNull
  public static PluginId getPluginId() {
    return PluginId.getId(EMACS_PLUS);
  }

  @NotNull
  public static String getVersion() {
    if (!ApplicationManager.getApplication().isInternal()) {
      final IdeaPluginDescriptor plugin = PluginManager.getPlugin(getPluginId());
      return plugin != null ? plugin.getVersion() : "SNAPSHOT";
    }
    else {
      return "INTERNAL";
    }
  }
}
