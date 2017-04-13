//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.wrapper;

public class KillWord extends KillWrapper {
  protected KillWord() {
    super(EmacsPlusWrapper.getWrappedHandler("EditorKillToWordEnd"));
  }

  @Override
  protected String getName() {
    return "kill-word";
  }
}
