// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.info;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.EmacsPlusCaretHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorAction;

public class WhatLine extends EditorAction
{
    protected WhatLine() {
        super((EditorActionHandler)new myHandler());
    }
    
    private static final class myHandler extends EmacsPlusCaretHandler
    {
        @Override
        protected void doXecute(final Editor editor, final Caret caret, final DataContext dataContext) {
            final int cline = editor.getDocument().getLineNumber(caret.getOffset()) + 1;
            ApplicationManager.getApplication().invokeLater((Runnable)new Runnable() {
                @Override
                public void run() {
                    StatusBar.Info.set("Line " + cline, (Project)null);
                }
            });
        }
    }
}
