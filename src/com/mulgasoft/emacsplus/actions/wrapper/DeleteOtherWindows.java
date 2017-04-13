//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.util.ActionUtil;

public class DeleteOtherWindows extends EmacsPlusAction
{
    protected DeleteOtherWindows() {
        super(new myHandler());
    }

    private static class myHandler extends EditorActionHandler
    {
        public void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
            final FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(editor.getProject());
            final DataContext d = DataManager.getInstance().getDataContext(editor.getComponent());
            ActionUtil.getInstance().dispatchLater(fileEditorManager.isInSplitter() ? "UnsplitAll" : "HideAllWindows", d);
        }
    }
}
