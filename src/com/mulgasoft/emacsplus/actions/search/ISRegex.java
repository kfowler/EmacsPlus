// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindModel;
import javax.swing.JComponent;
import com.intellij.find.FindSettings;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;

public class ISRegex extends EmacsPlusAction
{
    public ISRegex() {
        super((EditorActionHandler)new myHandler());
    }
    
    private static final class myHandler extends ISHandler
    {
        public void executeWriteAction(final Editor isEditor, final Caret isCaret, final DataContext dataContext) {
            final Editor editor = FileEditorManager.getInstance(isEditor.getProject()).getSelectedTextEditor();
            final JComponent hc = editor.getHeaderComponent();
            final FindModel findModel = ISHandler.getFindModel(editor);
            if (findModel != null) {
                final boolean state = !findModel.isRegularExpressions();
                if (state) {
                    findModel.setWholeWordsOnly(false);
                }
                findModel.setRegularExpressions(state);
                FindSettings.getInstance().setLocalRegularExpressions(state);
            }
        }
        
        @Override
        protected boolean isEnabledForCaret(final Editor editor, final Caret caret, final DataContext dataContext) {
            return ISHandler.isInISearch(editor);
        }
    }
}
