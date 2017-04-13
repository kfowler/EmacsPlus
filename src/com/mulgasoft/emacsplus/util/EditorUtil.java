//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.util;

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import java.awt.*;
import javax.swing.*;

public class EditorUtil
{
    public static Editor getCurrentEditor(final Project project) {
        return FileEditorManager.getInstance(project).getSelectedTextEditor();
    }

    public static void activateCurrentEditor(final Project project) {
        final Editor editor = getCurrentEditor(project);
        if (editor != null) {
            final JComponent focusedComponent = editor.getContentComponent();
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    IdeFocusManager.getInstance(project).requestFocus(focusedComponent, true);
                }
            });
        }
    }

    public static void closeEditorPopups() {
        if (JBPopupFactory.getInstance().isPopupActive()) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    IdeEventQueue.getInstance().getPopupManager().closeAllPopups();
                }
            });
        }
    }

    public static PsiFile getPsiFile(final Editor editor) {
        return getPsiFile(editor, editor.getCaretModel().getPrimaryCaret());
    }

    public static PsiFile getPsiFile(final Editor editor, final Caret caret) {
        return PsiUtilBase.getPsiFileInEditor(caret, editor.getProject());
    }

    public static boolean checkMarkSelection(final Editor editor, final Caret caret) {
        boolean result = false;
        if (editor instanceof EditorEx) {
            result = true;
            final EditorEx ex = (EditorEx)editor;
            if (caret.hasSelection() && !ex.isStickySelection()) {
                final int offset = caret.getOffset();
                int otherEnd = caret.getSelectionStart();
                if (otherEnd == caret.getSelectionEnd()) {
                    ex.setStickySelection(true);
                }
                else if (offset == otherEnd) {
                    otherEnd = caret.getSelectionEnd();
                    try {
                        caret.moveToOffset(otherEnd);
                        ex.setStickySelection(true);
                    }
                    finally {
                        caret.moveToOffset(offset);
                    }
                }
            }
        }
        return result;
    }

    public static boolean hasSelection(final Editor editor, final Caret caret) {
        checkMarkSelection(editor, caret);
        return caret.hasSelection() || (editor instanceof EditorEx && ((EditorEx)editor).isStickySelection());
    }

    public static TextRange getSelectionRange(final Editor editor, final Caret caret) {
        TextRange result = null;
        if (hasSelection(editor, caret)) {
            result = new TextRange(caret.getSelectionStart(), caret.getSelectionEnd());
        }
        return result;
    }
}
