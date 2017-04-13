//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions;

import com.intellij.codeInsight.actions.MultiCaretCodeInsightAction;
import com.intellij.codeInsight.actions.MultiCaretCodeInsightActionHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.actionSystem.DocCommandGroupId;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

public abstract class ReversibleMultiCaretInsightAction extends MultiCaretCodeInsightAction implements DumbAware
{
    boolean isReverse;
    private DataContext myDataContext;

    protected ReversibleMultiCaretInsightAction() {
        this(true);
    }

    protected ReversibleMultiCaretInsightAction(final boolean reverse) {
        this.isReverse = true;
        this.myDataContext = null;
        this.isReverse = reverse;
    }

    public DataContext getDataContext() {
        return this.myDataContext;
    }

    @NotNull
    protected MultiCaretCodeInsightActionHandler getHandler() {
        final MultiCaretCodeInsightActionHandler handler = this.getHandler(this);
        if (handler == null) {
            throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "com/mulgasoft/emacsplus/actions/ReversibleMultiCaretInsightAction", "getHandler"));
        }
        return handler;
    }

    protected abstract MultiCaretCodeInsightActionHandler getHandler(final ReversibleMultiCaretInsightAction p0);

    public void actionPerformed(@NotNull final AnActionEvent e) {
        if (e == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "e", "com/mulgasoft/emacsplus/actions/ReversibleMultiCaretInsightAction", "actionPerformed"));
        }
        final Project project = e.getProject();
        if (project != null) {
            this.myDataContext = e.getDataContext();
            final Editor hostEditor = CommonDataKeys.EDITOR.getData(this.myDataContext);
            if (hostEditor != null) {
                this.actionPerformedImpl(project, hostEditor);
            }
        }
    }

    public void actionPerformedImpl(final Project project, final Editor hostEditor) {
        CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
            final MultiCaretCodeInsightActionHandler handler = ReversibleMultiCaretInsightAction.this.getHandler();
            try {
                ReversibleMultiCaretInsightAction.this.iterateCarets(project, hostEditor, handler);
            }
            finally {
                handler.postInvoke();
                ReversibleMultiCaretInsightAction.this.myDataContext = null;
            }
        }), this.getCommandName(), DocCommandGroupId.noneGroupId(hostEditor.getDocument()));
        hostEditor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
    }

    private void iterateCarets(@NotNull final Project project, @NotNull final Editor hostEditor, @NotNull final MultiCaretCodeInsightActionHandler handler) {
        if (project == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "project", "com/mulgasoft/emacsplus/actions/ReversibleMultiCaretInsightAction", "iterateCarets"));
        }
        if (hostEditor == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "hostEditor", "com/mulgasoft/emacsplus/actions/ReversibleMultiCaretInsightAction", "iterateCarets"));
        }
        if (handler == null) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "handler", "com/mulgasoft/emacsplus/actions/ReversibleMultiCaretInsightAction", "iterateCarets"));
        }
        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        final PsiFile psiFile = documentManager.getCachedPsiFile(hostEditor.getDocument());
        documentManager.commitAllDocuments();
        hostEditor.getCaretModel().runForEachCaret(caret -> {
            Editor editor = hostEditor;
            if (psiFile != null) {
                final Caret injectedCaret = InjectedLanguageUtil.getCaretForInjectedLanguageNoCommit(caret, psiFile);
                if (injectedCaret != null) {
                    caret = injectedCaret;
                    editor = caret.getEditor();
                }
            }
            final PsiFile file = PsiUtilBase.getPsiFileInEditor(caret, project);
            if (file != null) {
                handler.invoke(project, editor, caret, file);
            }
        }, this.isReverse);
    }
}
