// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions;

import java.util.HashMap;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import java.util.Map;
import com.intellij.openapi.editor.actions.TextComponentEditorAction;

public abstract class EmacsPlusAction extends TextComponentEditorAction implements EmacsPlusBA
{
    private static Map<String, EmacsPlusBA> ourCommandMap;
    
    protected EmacsPlusAction(final EditorActionHandler defaultHandler) {
        super(defaultHandler);
    }
    
    public static final void addCommandListener(final EmacsPlusBA action, final String id) {
        EmacsPlusAction.ourCommandMap.put(id, action);
    }
    
    public void before(final CommandEvent e) {
    }
    
    public void after(final CommandEvent e) {
    }
    
    public static CommandListener getCommandListener() {
        return (CommandListener)StaticCommandListener.ourCommandListener;
    }
    
    public static void infoMessage(final String msg) {
        ApplicationManager.getApplication().invokeLater((Runnable)new Runnable() {
            @Override
            public void run() {
                StatusBar.Info.set(msg, (Project)null);
            }
        });
    }
    
    public static void errorMessage(final String msg) {
        infoMessage(msg);
    }
    
    static {
        EmacsPlusAction.ourCommandMap = new HashMap<String, EmacsPlusBA>();
    }
    
    private static class EmacsPlusCommandListener implements CommandListener
    {
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
        
        public void beforeCommandFinished(final CommandEvent event) {
        }
        
        public void undoTransparentActionStarted() {
        }
        
        public void undoTransparentActionFinished() {
        }
    }
    
    private static class StaticCommandListener
    {
        private static final EmacsPlusCommandListener ourCommandListener;
        
        static {
            ourCommandListener = new EmacsPlusCommandListener();
        }
    }
}
