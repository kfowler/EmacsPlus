// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.handlers;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.actions.EditorActionUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;

public abstract class EmacsPlusWriteHandler extends EditorWriteActionHandler
{
    protected EmacsPlusWriteHandler() {
        super(true);
    }
    
    protected String getNextWord(final Editor editor, final boolean isLine, final boolean isWord) {
        return editor.getDocument().getText(this.getNextWordRange(editor, isLine, isWord));
    }
    
    protected TextRange getNextWordRange(final Editor editor, final boolean isLine, final boolean isWord) {
        return this.getWordRange(editor, isLine, isWord, 1);
    }
    
    protected TextRange getPreviousWordRange(final Editor editor, final boolean isLine, final boolean isWord, final int dir) {
        return this.getWordRange(editor, isLine, isWord, (dir > 0) ? (-dir) : dir);
    }
    
    private TextRange getWordRange(final Editor editor, final boolean isLine, final boolean isWord, int dir) {
        final Document doc = editor.getDocument();
        int offset = editor.getCaretModel().getOffset();
        final CharSequence chars = editor.getDocument().getCharsSequence();
        final int maxOffset = isLine ? doc.getLineEndOffset(doc.getLineNumber(offset)) : chars.length();
        offset = Math.min(offset, maxOffset);
        int newOffset = offset + dir;
        dir = ((dir == 0) ? -1 : dir);
        int startOffset = (isWord && (dir < 0 || (offset < maxOffset && !Character.isJavaIdentifierStart(chars.charAt(offset))))) ? -1 : offset;
        if (newOffset == maxOffset && startOffset < 0) {
            startOffset = offset;
        }
        while (newOffset < maxOffset && newOffset > 0) {
            if (startOffset < 0) {
                final char c = chars.charAt(newOffset);
                if (Character.isJavaIdentifierStart(c)) {
                    startOffset = newOffset + ((dir < 0) ? 1 : 0);
                    if (dir < 0 && EditorActionUtil.isWordOrLexemeStart(editor, newOffset, false)) {
                        break;
                    }
                }
            }
            else if (dir < 0) {
                if (EditorActionUtil.isWordOrLexemeStart(editor, newOffset, false)) {
                    break;
                }
            }
            else if (EditorActionUtil.isWordOrLexemeEnd(editor, newOffset, false)) {
                break;
            }
            newOffset += dir;
        }
        if (dir < 0) {
            final int tmp = newOffset;
            newOffset = startOffset;
            startOffset = tmp;
        }
        return (newOffset > maxOffset || newOffset < 0) ? new TextRange(offset, offset) : new TextRange((startOffset < 0) ? newOffset : startOffset, newOffset);
    }
}
