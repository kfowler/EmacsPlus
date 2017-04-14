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

import static com.google.common.base.Preconditions.*;


/**
 * On blank line, delete all surrounding blank lines, leaving just one.
 * On isolated blank line, delete that one.
 * On non-blank line, delete any immediately following blank lines.
 */
class DeleteBlankLines extends EmacsPlusAction {
  public DeleteBlankLines() {
    this(new myHandler());
  }

  private DeleteBlankLines(final EditorActionHandler defaultHandler) {
    super(defaultHandler);
  }

  private static class myHandler extends WhiteSpaceHandler {
    public boolean isEnabledForCaret(@NotNull final Editor editor, @NotNull final Caret caret,
        final DataContext dataContext) {
      checkNotNull(editor);
      checkNotNull(caret);
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
      } else {
        if (nextBlank || (lineNum != 0 && this.isBlankLine(document, lineNum - 1))) {
          offset = this.transformSpace(editor, offset, dataContext, "", true);
          document.insertString(offset, "\n");
        } else {
          offset = this.transformSpace(editor, caret, dataContext, "", true);
        }
        caret.moveToOffset(offset);
        EditorModificationUtil.scrollToCaret(editor);
      }
    }
  }
}
