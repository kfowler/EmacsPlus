// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.mulgasoft.emacsplus.handlers.EmacsPlusWriteHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;

public class TransposeLines extends EmacsPlusAction
{
    public TransposeLines() {
        super((EditorActionHandler)new myHandler());
    }
    
    private static final class myHandler extends EmacsPlusWriteHandler
    {
        public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
            final Document document = editor.getDocument();
            int cline = document.getLineNumber(caret.getOffset());
            if (cline == 0) {
                ++cline;
            }
            int line2 = cline;
            final int line3 = --cline;
            final int len = document.getLineCount();
            if (line2 < len) {
                this.swapLines(document, line3, line2);
                caret.moveToOffset(document.getLineStartOffset((++line2 < len) ? line2 : (len - 1)));
                EditorModificationUtil.scrollToCaret(editor);
            }
        }
        
        private void swapLines(final Document document, final int line1, final int line2) {
            final TextRange t1 = new TextRange(document.getLineStartOffset(line1), document.getLineEndOffset(line1));
            final TextRange t2 = new TextRange(document.getLineStartOffset(line2), document.getLineEndOffset(line2));
            final String line1Text = document.getText(t1);
            final String line2Text = document.getText(t2);
            document.replaceString(t2.getStartOffset(), t2.getEndOffset(), (CharSequence)line1Text);
            document.replaceString(t1.getStartOffset(), t1.getEndOffset(), (CharSequence)line2Text);
        }
    }
}
