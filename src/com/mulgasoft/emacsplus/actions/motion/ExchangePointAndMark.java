//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.motion;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.util.ActionUtil;


public class ExchangePointAndMark extends EmacsPlusAction {
  private static DataContext dc;

  public ExchangePointAndMark() {
    super(new myHandler());
    EmacsPlusAction.addCommandListener(this, getName());
  }

  private String getName() {
    return "exchange-point-and-mark";
  }

  @Override
  public void after(final CommandEvent e) {
    if (ExchangePointAndMark.dc != null) {
      ActionUtil.getInstance().dispatchLater("EditorSwapSelectionBoundaries", ExchangePointAndMark.dc);
      final Editor editor = getEditor(ExchangePointAndMark.dc);
      ExchangePointAndMark.dc = null;
      if (editor.getCaretModel().getCaretCount() == 1) {
        editor.getScrollingModel()
            .scrollTo(editor.getCaretModel().getPrimaryCaret().getLogicalPosition(), ScrollType.MAKE_VISIBLE);
      }
    }
  }

  static {
    ExchangePointAndMark.dc = null;
  }

  private static class myHandler extends EditorActionHandler {
    myHandler() {
      super(true);
    }

    protected void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
      final SelectionModel selectionModel = editor.getSelectionModel();
      if (selectionModel.hasSelection()) {
        final VisualPosition pos = caret.getVisualPosition();
        final VisualPosition vpos = selectionModel.getLeadSelectionPosition();
        final VisualPosition epos = selectionModel.getSelectionEndPosition();
        final VisualPosition spos = selectionModel.getSelectionStartPosition();
        if (!pos.equals(spos) && !pos.equals(epos)) {
          caret.moveToVisualPosition(vpos.equals(epos) ? spos : epos);
        }
        ExchangePointAndMark.dc = dataContext;
      }
    }
  }
}
