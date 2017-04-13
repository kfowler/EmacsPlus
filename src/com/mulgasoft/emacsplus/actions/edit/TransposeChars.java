//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.EmacsPlusWriteHandler;

public class TransposeChars extends EmacsPlusAction
{
    public TransposeChars() {
        super(new myHandler());
    }

    private static final class myHandler extends EmacsPlusWriteHandler
    {
        public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
            final Document document = editor.getDocument();
            int coff = caret.getOffset();
            final int lineNo = document.getLineNumber(coff);
            final int boff = document.getLineStartOffset(lineNo);
            final int eoff = document.getLineEndOffset(lineNo);
            final int incr = 1;
            if (coff == boff && lineNo == 0) {
                return;
            }
            if (coff == eoff && --coff == 0) {
                return;
            }
            final int b = coff - incr;
            final int e = Math.min(eoff, coff + incr);
            final String sub = document.getText(new TextRange(b, e));
            document.replaceString(b, e, sub.substring(incr) + sub.substring(0, incr));
            caret.moveToOffset(e);
        }
    }
}
