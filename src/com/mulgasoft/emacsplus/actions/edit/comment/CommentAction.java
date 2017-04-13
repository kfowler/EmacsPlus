//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit.comment;

import com.intellij.codeInsight.actions.MultiCaretCodeInsightActionHandler;
import com.intellij.lang.LanguageCommenters;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.mulgasoft.emacsplus.actions.ReversibleMultiCaretInsightAction;
import com.mulgasoft.emacsplus.handlers.CommentHandler;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import org.jetbrains.annotations.NotNull;


public abstract class CommentAction extends ReversibleMultiCaretInsightAction {
  protected abstract CommentHandler getMyHandler();

  @Override
  protected MultiCaretCodeInsightActionHandler getHandler(final ReversibleMultiCaretInsightAction action) {
    final CommentHandler mine = this.getMyHandler();
    mine.preInvoke(action);
    return mine;
  }

  protected boolean isValidFor(@NotNull final Project project, @NotNull final Editor editor, @NotNull final Caret caret,
      @NotNull final PsiFile file) {
    if (project == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "project",
              "com/mulgasoft/emacsplus/actions/edit/comment/CommentAction", "isValidFor"));
    }
    if (editor == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "editor",
              "com/mulgasoft/emacsplus/actions/edit/comment/CommentAction", "isValidFor"));
    }
    if (caret == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "caret",
              "com/mulgasoft/emacsplus/actions/edit/comment/CommentAction", "isValidFor"));
    }
    if (file == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "file",
              "com/mulgasoft/emacsplus/actions/edit/comment/CommentAction", "isValidFor"));
    }
    boolean result = false;
    if (!ISHandler.isInISearch(editor)) {
      final FileType fileType = file.getFileType();
      if (fileType instanceof AbstractFileType) {
        result = (((AbstractFileType) fileType).getCommenter() != null);
      } else if (LanguageCommenters.INSTANCE.forLanguage(file.getLanguage()) != null
          || LanguageCommenters.INSTANCE.forLanguage(file.getViewProvider().getBaseLanguage()) != null) {
        result = true;
      } else {
        final PsiElement host = InjectedLanguageManager.getInstance(project).getInjectionHost(file);
        result = (host != null && LanguageCommenters.INSTANCE.forLanguage(host.getLanguage()) != null);
      }
    }
    return result;
  }
}
