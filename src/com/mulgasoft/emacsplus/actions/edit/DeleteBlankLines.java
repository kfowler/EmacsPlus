//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.WhiteSpaceHandler;
import org.jetbrains.annotations.NotNull;

public class DeleteBlankLines extends EmacsPlusAction
{
    public DeleteBlankLines() {
        this(new myHandler());
    }

    protected DeleteBlankLines(final EditorActionHandler defaultHandler) {
        super(defaultHandler);
    }

    private static class myHandler extends WhiteSpaceHandler
    {
        public boolean isEnabledForCaret(@NotNull final Editor editor, @NotNull final Caret caret, final DataContext dataContext) {
            if (editor == null) {
                throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "editor", "com/mulgasoft/emacsplus/actions/edit/DeleteBlankLines$myHandler", "isEnabledForCaret"));
            }
            if (caret == null) {
                throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "caret", "com/mulgasoft/emacsplus/actions/edit/DeleteBlankLines$myHandler", "isEnabledForCaret"));
            }
            return !editor.isOneLineMode();
        }

        public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
            final Document document = editor.getDocument();
            int offset = caret.getOffset();
            final int lineNum = document.getLineNumber(offset);
            final boolean nextBlank = lineNum < document.getLineCount() - 1 && this.isBlankLine(document, lineNum + 1);
            if (!this.isBlankLine(document, lineNum)) {
                if (nextBlank) {
                    offset = document.getLineStartOffset(document.getLineNumber(offset) + 1);
                    this.transformSpace(editor, offset, dataContext, "", true);
                }
            }
            else {
                if (nextBlank || (lineNum != 0 && this.isBlankLine(document, lineNum - 1))) {
                    offset = this.transformSpace(editor, offset, dataContext, "", true);
                    document.insertString(offset, "\n");
                }
                else {
                    offset = this.transformSpace(editor, caret, dataContext, "", true);
                }
                caret.moveToOffset(offset);
                EditorModificationUtil.scrollToCaret(editor);
            }
        }
    }
}
