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

import static com.google.common.base.Preconditions.*;


public abstract class CommentAction extends ReversibleMultiCaretInsightAction {
  protected abstract CommentHandler getMyHandler();

  @Override
  protected MultiCaretCodeInsightActionHandler getHandler(final ReversibleMultiCaretInsightAction action) {
    final CommentHandler mine = getMyHandler();
    mine.preInvoke(action);
    return mine;
  }

  protected boolean isValidFor(@NotNull final Project project, @NotNull final Editor editor, @NotNull final Caret caret,
      @NotNull final PsiFile file) {
    checkNotNull(project);
    checkNotNull(editor);
    checkNotNull(caret);
    checkNotNull(file);

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
