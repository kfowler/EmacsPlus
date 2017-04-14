//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindUtil;
import com.intellij.ide.actions.SearchBackAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.mulgasoft.emacsplus.util.ActionUtil;


class ISearchBackward extends SearchBackAction {
  public void actionPerformed(final AnActionEvent e) {
    delegateAction(e);
  }

  ISearchDelegate delegateAction(final AnActionEvent e) {
    final Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
    ISearchDelegate searcher = ISearchFactory.getISearchObject(editor);
    if (searcher == null) {
      ActionUtil.getInstance().dispatch("Emacs+.ISearchForward", e.getDataContext());
      searcher = ISearchFactory.getISearchObject(editor);
    } else {
      searcher.requestFocus();
      FindUtil.configureFindModel(false, editor, searcher.getFindModel(), false);
    }
    if (searcher != null) {
      searcher.searchBackward();
    }
    return searcher;
  }
}
