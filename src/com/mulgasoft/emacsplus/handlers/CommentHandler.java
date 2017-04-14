//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.handlers;

import com.intellij.codeInsight.actions.MultiCaretCodeInsightActionHandler;
import com.intellij.ide.DataManager;
import com.intellij.lang.Commenter;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageCommenters;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.actions.ReversibleMultiCaretInsightAction;
import com.mulgasoft.emacsplus.util.ActionUtil;
import com.mulgasoft.emacsplus.util.EditorUtil;
import org.jetbrains.annotations.NotNull;


public class CommentHandler extends MultiCaretCodeInsightActionHandler {
  private static Integer ourCommentColumn;
  private String myLangId;
  private String myLineC;
  private String myBlockStart;
  private String myBlockEnd;
  private ReversibleMultiCaretInsightAction myAction;

  public CommentHandler() {
    this.myLangId = null;
    this.myLineC = null;
    this.myBlockStart = null;
    this.myBlockEnd = null;
    this.myAction = null;
  }

  public void preInvoke(final ReversibleMultiCaretInsightAction action) {
    this.myAction = action;
  }

  public void postInvoke() {
    this.myAction = null;
  }

  public void invoke(@NotNull final Project project, @NotNull final Editor editor, @NotNull final Caret caret,
      @NotNull final PsiFile file) {
    if (project == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "project",
              "com/mulgasoft/emacsplus/handlers/CommentHandler", "invoke"));
    }
    if (editor == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "editor",
              "com/mulgasoft/emacsplus/handlers/CommentHandler", "invoke"));
    }
    if (caret == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "caret",
              "com/mulgasoft/emacsplus/handlers/CommentHandler", "invoke"));
    }
    if (file == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "file",
              "com/mulgasoft/emacsplus/handlers/CommentHandler", "invoke"));
    }
    if (this.checkLanguage(project, caret, file)) {
      this.invokeAction(editor, caret, this.myAction.getDataContext(), file);
    }
  }

  protected void invokeAction(final Editor editor, final Caret caret, final DataContext dataContext,
      final PsiFile file) {
    this.commentLine(editor, caret, dataContext);
  }

  private int getCommentColumn() {
    return CommentHandler.ourCommentColumn;
  }

  protected String getLineComment() {
    return this.myLineC;
  }

  protected String getBlockStart() {
    return this.myBlockStart;
  }

  protected boolean hasBlockComments() {
    return this.myBlockStart != null && this.myBlockEnd != null;
  }

  private String getEmptyBlockComment() {
    return " " + this.myBlockStart + " " + " " + this.myBlockEnd;
  }

  private String getEmptyLineComment() {
    return " " + this.myLineC + " ";
  }

  protected int getLineStartOffset(final Document document, final int offset) {
    return document.getLineStartOffset(document.getLineNumber(offset));
  }

  protected int getLineEndOffset(final Document document, final int offset) {
    return document.getLineEndOffset(document.getLineNumber(offset));
  }

  private boolean checkLanguage(final Project project, final Caret caret, final PsiFile file) {
    final FileType fileType = file.getFileType();
    if (fileType instanceof AbstractFileType) {
      final Commenter commenter = ((AbstractFileType) fileType).getCommenter();
      return checkFill(String.valueOf(commenter), commenter);
    }

    final Language language = file.getLanguage();
    if (language != null) {
      return checkFill(language.getID(), LanguageCommenters.INSTANCE.forLanguage(language));
    }
    return false;
  }

  private boolean checkFill(final String id, final Commenter commenter) {
    boolean result = true;
    if ((myLangId == null || !myLangId.equals(id)) && (result = (commenter != null))) {
      myLangId = id;
      myLineC = commenter.getLineCommentPrefix();
      myBlockStart = commenter.getBlockCommentPrefix();
      myBlockEnd = commenter.getBlockCommentSuffix();
    }
    return result;
  }

  protected CommentRange findCommentRange(final Editor editor, final Caret caret, final DataContext dataContext) {
    return this.findCommentRange(EditorUtil.getPsiFile(editor, caret), editor, caret, dataContext);
  }

  private CommentRange findCommentRange(final PsiFile psi, final Editor editor, final Caret caret,
      final DataContext dataContext) {
    final Document document = editor.getDocument();
    final int current = caret.getOffset();
    final int line = document.getLineNumber(current);
    final int bol = document.getLineStartOffset(line);
    final int eol = document.getLineEndOffset(line);
    final String text = document.getText(new TextRange(bol, eol));
    CommentRange result = this.findCommentRange(psi, bol, text, this.myLineC);
    if (result == null) {
      return findCommentRange(psi, bol, text, this.myBlockStart);
    }
    return result;
  }

  private CommentRange findCommentRange(final PsiFile psi, final int filePos, final String text, final String prefix) {
    CommentRange crange = null;
    int index = 0;
    if (prefix != null && psi != null) {
      while (index >= 0) {
        index = text.indexOf(prefix, index);
        if (index >= 0) {
          PsiElement ele = psi.findElementAt(filePos + index);
          if (ele instanceof PsiComment || (ele = ele.getParent()) instanceof PsiComment) {
            crange = this.commentRange(ele, prefix);
            break;
          }
          ++index;
        }
      }
      if (crange == null) {
        final PsiElement ele = this.inComment(psi, filePos);
        if (ele != null && ele.getText().startsWith(prefix)) {
          crange = this.commentRange(ele, prefix);
        }
      }
    }
    return crange;
  }

  private CommentRange commentRange(final PsiElement ele, final String prefix) {
    TextRange range = ele.getTextRange();
    if (range == null) {
      final int off = ele.getTextOffset();
      range = new TextRange(off, off + prefix.length());
    }
    return new CommentRange(range, prefix);
  }

  protected PsiElement inComment(final Editor editor, final Caret caret) {
    return this.inComment(EditorUtil.getPsiFile(editor, caret), caret.getOffset());
  }

  private PsiElement inComment(final PsiFile psi, final int offset) {
    PsiElement result = null;
    PsiElement ele = psi.findElementAt(offset);
    if (ele != null && (ele instanceof PsiComment || (ele = ele.getParent()) instanceof PsiComment)) {
      result = ele;
    }
    return result;
  }

  protected void commentLine(final Editor editor, final Caret caret, final DataContext dataContext) {
    boolean tabit = false;
    final Document document = editor.getDocument();
    final int line = document.getLineNumber(caret.getOffset());
    final int bol = document.getLineStartOffset(line);
    final int eol = document.getLineEndOffset(line);
    final PsiFile psi = EditorUtil.getPsiFile(editor, caret);
    final CommentRange range = this.findCommentRange(psi, editor, caret, dataContext);
    if (range != null) {
      int index = range.getStartOffset() + Math.min(range.getPrefix().length(), range.getLength());
      if (index != eol && index + 1 < document.getTextLength()) {
        final char next = document.getText(new TextRange(index, index + 1)).charAt(0);
        if (next == ' ' || next == '\t') {
          ++index;
        }
      }
      caret.moveToOffset(index);
    } else {
      tabit = this.addComment(editor, caret, bol, eol);
    }
    if (tabit) {
      ActionUtil.getInstance()
          .dispatchLater("EmacsStyleIndent", DataManager.getInstance().getDataContext(editor.getComponent()));
    }
    EditorModificationUtil.scrollToCaret(editor);
  }

  private boolean addComment(final Editor editor, final Caret caret, final int bol, final int eol) {
    final Document document = editor.getDocument();
    boolean tabit = false;
    String comment = null;
    int length = 0;
    if (this.myLineC != null) {
      comment = this.getEmptyLineComment();
      length = comment.length();
    } else if (this.myBlockStart != null) {
      comment = this.getEmptyBlockComment();
      length = this.myBlockStart.length() + 2;
    }
    if (comment != null) {
      String text;
      int end;
      for (text = document.getText(new TextRange(bol, eol)), end = text.length() - 1;
          end >= 0 && text.charAt(end) <= ' '; --end) {
      }
      if (++end == 0) {
        document.replaceString(bol, eol, comment);
        caret.moveToOffset(bol + length);
        tabit = true;
      } else {
        final int cc = this.getCommentColumn() - 1;
        final int newoff = bol + end;
        if (end < cc) {
          caret.moveToOffset(newoff);
          final String fill = EditorModificationUtil.calcStringToFillVirtualSpace(editor, cc - end);
          document.insertString(newoff, fill + comment);
          caret.moveToOffset(newoff + fill.length() + length);
        } else {
          document.replaceString(newoff, eol, comment);
          caret.moveToOffset(newoff + length);
        }
      }
    }
    return tabit;
  }

  static {
    CommentHandler.ourCommentColumn = 32;
  }

  public static class CommentRange {
    final TextRange range;
    final String prefix;

    private CommentRange(final TextRange range, final String prefix) {
      this.range = ((range != null) ? range : new TextRange(0, 0));
      this.prefix = prefix;
    }

    public String getPrefix() {
      return this.prefix;
    }

    public int getStartOffset() {
      return this.range.getStartOffset();
    }

    public int getEndOffset() {
      return this.range.getEndOffset();
    }

    public int getLength() {
      return this.range.getLength();
    }
  }
}
