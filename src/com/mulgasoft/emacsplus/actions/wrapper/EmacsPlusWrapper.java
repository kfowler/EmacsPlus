//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.wrapper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.util.ActionUtil;


public abstract class EmacsPlusWrapper extends EmacsPlusAction {
  protected EmacsPlusWrapper(final EditorActionHandler defaultHandler) {
    super(defaultHandler);
  }

  static EditorActionHandler getWrappedHandler(final String name) {
    EditorActionHandler handler = null;
    final AnAction action = ActionUtil.getInstance().getAction(name);
    if (action instanceof EditorAction) {
      handler = ((EditorAction) action).getHandler();
    }
    return handler;
  }
}
