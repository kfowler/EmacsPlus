//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.wrapper;

public class KillWordBackward extends KillWrapper {
  protected KillWordBackward() {
    super(EmacsPlusWrapper.getWrappedHandler("EditorKillToWordStart"));
  }

  @Override
  protected String getName() {
    return "backward-kill-word";
  }
}
