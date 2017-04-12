// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus;

import org.jetbrains.annotations.NotNull;
import java.awt.Toolkit;
import com.intellij.openapi.extensions.PluginId;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.command.CommandProcessor;
import com.mulgasoft.emacsplus.keys.Keymaps;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.components.ApplicationComponent;

public class EmacsPlus implements ApplicationComponent
{
    private static PluginDescriptor ourPlugin;
    private static String ultCommand;
    private static String penultCommand;
    private static boolean visualBeep;
    
    public static PluginDescriptor getPlugin() {
        return EmacsPlus.ourPlugin;
    }
    
    public void initComponent() {
        final PluginId pluginId = PluginManager.getPluginByClassName(EmacsPlus.class.getName());
        EmacsPlus.ourPlugin = (PluginDescriptor)PluginManager.getPlugin(pluginId);
        Keymaps.enableKeymaps();
        CommandProcessor.getInstance().addCommandListener((CommandListener)new CommandListener() {
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
        if (name == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "name", "com/mulgasoft/emacsplus/EmacsPlus", "resetCommand"));
        }
        setUltCommand(name);
    }
    
    public void disposeComponent() {
        System.out.println("disposeComponent");
    }
    
    @NotNull
    public String getComponentName() {
        final String s = "EmacsPlus";
        if (s == null) {
            throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "com/mulgasoft/emacsplus/EmacsPlus", "getComponentName"));
        }
        return s;
    }
    
    static {
        EmacsPlus.ourPlugin = null;
        EmacsPlus.ultCommand = null;
        EmacsPlus.penultCommand = null;
        EmacsPlus.visualBeep = false;
    }
}
