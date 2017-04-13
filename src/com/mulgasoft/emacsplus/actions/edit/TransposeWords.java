//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.EmacsPlusWriteHandler;

public class TransposeWords extends EmacsPlusAction
{
    public TransposeWords() {
        super(new myHandler());
    }

    private static final class myHandler extends EmacsPlusWriteHandler
    {
        public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
            final Document doc = editor.getDocument();
            final CharSequence chars = doc.getCharsSequence();
            final int maxOffset = chars.length();
            int offset = caret.getOffset();
            if (offset == maxOffset) {
                caret.moveToOffset(--offset);
            }
            TextRange right = this.getNextWordRange(editor, false, true);
            final int leftdir = (offset != 0 && Character.isJavaIdentifierPart(chars.charAt(offset)) && !Character.isJavaIdentifierPart(chars.charAt(offset - 1))) ? 1 : 0;
            TextRange left = this.getPreviousWordRange(editor, false, true, leftdir);
            boolean eof = false;
            if (offset == 0 || left.isEmpty()) {
                left = right;
                caret.moveToOffset(right.getEndOffset() + 1);
                right = this.getNextWordRange(editor, false, true);
            }
            else if (offset == maxOffset || this.trueEnd(maxOffset, left, right, doc)) {
                eof = true;
            }
            else if (Character.isJavaIdentifierPart(chars.charAt(offset)) && Character.isJavaIdentifierPart(chars.charAt(offset - 1))) {
                left = new TextRange(left.getStartOffset(), right.getEndOffset());
                caret.moveToOffset(Math.min(maxOffset, right.getEndOffset() + 1));
                right = this.getNextWordRange(editor, false, true);
                if (right.getEndOffset() == maxOffset) {
                    eof = true;
                }
            }
            if (eof) {
                right = left;
                caret.moveToOffset(right.getStartOffset() - 1);
                left = this.getPreviousWordRange(editor, false, true, leftdir);
            }
            if (right != null && !right.isEmpty() && left != null && !left.isEmpty()) {
                final String rtext = doc.getText(right);
                final String ltext = doc.getText(left);
                doc.replaceString(right.getStartOffset(), right.getEndOffset(), ltext);
                doc.replaceString(left.getStartOffset(), left.getEndOffset(), rtext);
                caret.moveToOffset(right.getEndOffset());
            }
            else {
                caret.moveToOffset(offset);
            }
        }

        private boolean trueEnd(final int maxOffset, final TextRange left, final TextRange right, final Document doc) {
            boolean result = false;
            final String last = doc.getText(right);
            if (right.getEndOffset() == maxOffset && (left.getEndOffset() == maxOffset || last.isEmpty() || Character.isWhitespace(last.charAt(0)))) {
                result = true;
            }
            return result;
        }
    }
}
