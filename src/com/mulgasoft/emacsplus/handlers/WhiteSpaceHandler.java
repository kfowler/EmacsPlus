package com.mulgasoft.emacsplus.handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.*;


public class WhiteSpaceHandler extends EmacsPlusWriteHandler {
  protected int transformSpace(@NotNull final Editor editor, @NotNull final Caret caret, final DataContext dataContext,
      final String replace, final boolean ignoreCR) {
    checkNotNull(editor);
    checkNotNull(caret);
    return this.transformSpace(editor, caret.getOffset(), dataContext, replace, ignoreCR);
  }

  protected int transformSpace(@NotNull final Editor editor, final int offset, final DataContext dataContext,
      String replace, final boolean ignoreCR) {
    checkNotNull(editor);
    final Document document = editor.getDocument();
    final int lastOff = document.getTextLength();
    final int left = this.countWS(document, lastOff, offset - 1, -1, ignoreCR);
    final int right = this.countWS(document, lastOff, offset, 1, ignoreCR);
    if (ignoreCR) {
      replace = ((offset - left == 0 || offset + right == document.getTextLength()) ? replace : (replace + "\n"));
    }
    document.replaceString(offset - left, offset + right, replace);
    return offset - left + replace.length();
  }

  protected boolean isBlankLine(final Document document, final int line) {
    final int offset = document.getLineStartOffset(line);
    final int lastOff = document.getTextLength();
    return this.countWS(document, lastOff, offset, 1, false) == document.getLineEndOffset(line) - offset;
  }

  private int countWS(final Document document, final int lastOff, final int offset, final int dir,
      final boolean ignoreCR) {
    final CharSequence seq = document.getCharsSequence();
    final char eol = '\n';
    final char cr = '\r';
    int lineOff = offset;
    char c;
    int off = offset;
    for (; -1 < off && off < lastOff && (c = seq.charAt(off)) <= ' '; off += dir) {
      if (c == eol || c == cr) {
        if (!ignoreCR) {
          break;
        }
        lineOff = off + dir;
      }
    }
    return Math.abs(offset - (ignoreCR ? lineOff : off));
  }
}
