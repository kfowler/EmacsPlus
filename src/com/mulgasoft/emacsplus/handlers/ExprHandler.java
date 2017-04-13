//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.handlers;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.actions.EditorActionUtil;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.util.EditorUtil;


public abstract class ExprHandler extends EmacsPlusCaretHandler {
  static boolean isVisual;

  protected boolean isVisual() {
    return ExprHandler.isVisual;
  }

  protected void moveToWord(final Editor editor, final Caret caret, final DataContext dataContext, final int dir) {
    EditorUtil.checkMarkSelection(editor, caret);
    final TextRange range = this.getWordRange(editor, caret, false, true, dir);
    int newpos =
        (dir > 0) ? range.getEndOffset() : ((range.getStartOffset() != caret.getOffset()) ? range.getStartOffset() : 0);
    VisualPosition pos = editor.offsetToVisualPosition(newpos);
    if (this.isVisual()) {
      final FoldRegion currentFoldRegion = editor.getFoldingModel().getCollapsedRegionAtOffset(range.getEndOffset());
      if (currentFoldRegion != null) {
        newpos = ((dir > 0) ? currentFoldRegion.getEndOffset() : currentFoldRegion.getStartOffset());
        pos = editor.offsetToVisualPosition(newpos);
      }
      caret.moveToVisualPosition(pos);
    } else {
      caret.moveToOffset(newpos);
    }
    EditorModificationUtil.scrollToCaret(editor);
  }

  protected void setSelection(final Editor editor, final Caret caret, final TextRange selection) {
    final int coffset = caret.getOffset();
    final int offset = (coffset <= selection.getStartOffset()) ? selection.getEndOffset() : selection.getStartOffset();
    this.setSelection(editor, caret, offset);
  }

  protected void setSelection(final Editor editor, final Caret caret, final int offset) {
    final SelectionModel selectionModel = editor.getSelectionModel();
    if (editor.isColumnMode() && !editor.getCaretModel().supportsMultipleCarets()) {
      selectionModel.setBlockSelection(editor.offsetToLogicalPosition(offset), caret.getLogicalPosition());
    } else {
      selectionModel.setSelection(offset, caret.getVisualPosition(), caret.getOffset());
    }
  }

  public TextRange getWordRange(final Editor editor, final Caret caret, final boolean isLine, final boolean isWord,
      int dir) {
    final Document doc = editor.getDocument();
    int offset = (caret == null) ? editor.getCaretModel().getOffset() : caret.getOffset();
    final CharSequence chars = editor.getDocument().getCharsSequence();
    final int maxOffset = isLine ? doc.getLineEndOffset(doc.getLineNumber(offset)) : chars.length();
    offset = Math.min(offset, maxOffset);
    int newOffset = offset + dir;
    dir = ((dir == 0) ? -1 : dir);
    int startOffset =
        (isWord && (dir < 0 || (offset < maxOffset && !Character.isJavaIdentifierStart(chars.charAt(offset))))) ? -1
            : offset;
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
      } else if (dir < 0) {
        if (EditorActionUtil.isWordOrLexemeStart(editor, newOffset, false)) {
          break;
        }
      } else if (EditorActionUtil.isWordOrLexemeEnd(editor, newOffset, false)) {
        break;
      }
      newOffset += dir;
    }
    if (dir < 0) {
      final int tmp = newOffset;
      newOffset = startOffset;
      startOffset = tmp;
    }
    return (newOffset > maxOffset || newOffset < 0) ? new TextRange(offset, offset)
        : new TextRange((startOffset < 0) ? newOffset : startOffset, newOffset);
  }

  static {
    ExprHandler.isVisual = true;
  }
}
