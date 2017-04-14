package com.mulgasoft.emacsplus.actions.motion;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.mulgasoft.emacsplus.EmacsPlus;
import com.mulgasoft.emacsplus.handlers.EmacsPlusCaretHandler;
import java.awt.*;


public class Recenter extends EditorAction {
  public Recenter() {
    super(new myHandler());
  }

  public void setScrollMargin(final int sm) {
    myHandler.CS.setScrollMargin(sm);
  }

  private static class myHandler extends EmacsPlusCaretHandler {
    private CS state;

    myHandler() {
      state = CS.C;
    }

    @Override
    protected void doXecute(final Editor editor, final Caret caret, final DataContext dataContext) {
      if (!"recenter-top-bottom".equals(EmacsPlus.getUltCommand())) {
        state = CS.C;
      }
      final int lineHeight = editor.getLineHeight();
      final int cpos = caret.getVisualPosition().getLine() * lineHeight;
      final Rectangle visibleArea = editor.getScrollingModel().getVisibleArea();
      editor.getScrollingModel().scrollVertically(state.getLine(this, cpos, visibleArea.height, lineHeight));
    }

    private enum CS {
      B, T, C;

      static int scrollMargin = 0;

      public static void setScrollMargin(final int sm) {
        CS.scrollMargin = sm;
      }

      public int getLine(final myHandler h, final int caretLine, final int areaHeight, final int lineHeight) {
        switch (h.state) {
          case B: {
            h.state = CS.C;
            return caretLine + lineHeight - areaHeight + CS.scrollMargin * lineHeight;
          }
          case T: {
            h.state = CS.B;
            return caretLine - CS.scrollMargin * lineHeight;
          }
          case C: {
            h.state = CS.T;
            break;
          }
        }
        return caretLine - areaHeight / 2;
      }

    }
  }
}
