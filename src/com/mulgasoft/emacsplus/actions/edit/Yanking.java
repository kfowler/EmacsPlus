//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;


abstract class Yanking extends EmacsPlusAction {
  private static int ourLength;
  private static int ourIndex;
  private static int ourOffset;

  Yanking(final EditorActionHandler defaultHandler) {
    super(defaultHandler);
  }

  private static void setLength(final int length) {
    Yanking.ourLength = length;
  }

  static int getLength() {
    return Yanking.ourLength;
  }

  static void setIndex(final int index) {
    Yanking.ourIndex = index;
  }

  static int getIndex() {
    return Yanking.ourIndex;
  }

  private static void setOffset(final int offset) {
    Yanking.ourOffset = offset;
  }

  static int getOffset() {
    return Yanking.ourOffset;
  }

  void reset() {
    setLength(0);
    setOffset(0);
    setIndex(1);
  }

  static void yanked(final TextRange location) {
    if (location != null) {
      setLength(location.getEndOffset() - location.getStartOffset());
      setOffset(location.getEndOffset());
    }
  }

  void popped(final TextRange location) {
    yanked(location);
    ++Yanking.ourIndex;
  }

}
