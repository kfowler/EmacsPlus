//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.info;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.EmacsPlusCaretHandler;


public class WhatCursorPos extends EditorAction {

  protected WhatCursorPos() {
    super(new myHandler());
  }

  private static final class myHandler extends EmacsPlusCaretHandler {
    @Override
    protected void doXecute(final Editor editor, final Caret caret, final DataContext dataContext) {
      final Document doc = editor.getDocument();
      final int offset = caret.getOffset();
      final int docLen = doc.getTextLength();
      final String msg = (offset >= docLen) ? this.getEob(offset, docLen) : this.getCurPos(offset, docLen, doc);
      EmacsPlusAction.infoMessage(msg);
    }

    private String getEob(final int offset, final int docLen) {
      return String.format("point=%d of %d (EOB)", offset, docLen);
    }

    private String getCurPos(final int offset, final int docLen, final Document doc) {
      final char curChar = doc.getCharsSequence().charAt(offset);
      final int percent = Math.round(offset * 100 / docLen + 0.5f);
      final String sChar = (curChar <= ' ') ? this.normalizeChar(curChar) : String.valueOf(curChar);
      return String.format("Char: %s  (%d, #o%o, #x%x)  point=%d of %d (%d%%)", sChar, curChar, curChar, curChar,
          offset, docLen, percent);
    }

    private String normalizeChar(final char cc) {
      String result = null;
      switch (cc) {
        case ' ': {
          result = "SPC";
          break;
        }
        case '\r': {
          result = "\\r";
          break;
        }
        case '\n': {
          result = "\\n";
          break;
        }
        case '\t': {
          result = "\\t";
          break;
        }
        case '\f': {
          result = "\\f";
          break;
        }
        case '\b': {
          result = "\\b";
          break;
        }
        default: {
          result = "\\c" + cc;
          break;
        }
      }
      return result;
    }
  }
}
