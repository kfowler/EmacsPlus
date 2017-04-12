// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.handlers;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;

public class CaseHandler extends EmacsPlusWriteHandler
{
    protected void caseAction(final Editor editor, final Caret caret, final Cases cases) {
        final Document doc = editor.getDocument();
        final TextRange range = caret.hasSelection() ? this.getSelectionRange(caret) : this.getNextWordRange(editor, false, true);
        if (!range.isEmpty()) {
            doc.replaceString(range.getStartOffset(), range.getEndOffset(), (CharSequence)this.toCase(doc.getText(range), cases));
            caret.moveToVisualPosition(editor.offsetToVisualPosition(range.getEndOffset()));
        }
    }
    
    private TextRange getSelectionRange(final Caret caret) {
        final TextRange result = new TextRange(caret.getSelectionStart(), caret.getSelectionEnd());
        caret.removeSelection();
        return result;
    }
    
    private String toCase(final String text, final Cases cases) {
        final StringBuilder builder = new StringBuilder(text.length());
        boolean prevIsSlash = false;
        for (char c : text.toCharArray()) {
            if (!prevIsSlash) {
                c = ((cases == Cases.UPPER) ? Character.toUpperCase(c) : Character.toLowerCase(c));
            }
            prevIsSlash = (c == '\\');
            builder.append(c);
        }
        if (cases == Cases.CAP && builder.length() > 0) {
            builder.replace(0, 1, String.valueOf(Character.toUpperCase(builder.charAt(0))));
        }
        return builder.toString();
    }
    
    protected enum Cases
    {
        UPPER, 
        LOWER, 
        CAP;
    }
}
