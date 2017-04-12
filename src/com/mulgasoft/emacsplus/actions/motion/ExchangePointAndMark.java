// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.motion;

import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.mulgasoft.emacsplus.util.ActionUtil;
import com.intellij.openapi.command.CommandEvent;
import com.mulgasoft.emacsplus.actions.EmacsPlusBA;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;

public class ExchangePointAndMark extends EmacsPlusAction
{
    private static DataContext dc;
    
    public ExchangePointAndMark() {
        super(new myHandler());
        EmacsPlusAction.addCommandListener(this, this.getName());
    }
    
    protected String getName() {
        return "exchange-point-and-mark";
    }
    
    @Override
    public void after(final CommandEvent e) {
        if (ExchangePointAndMark.dc != null) {
            ActionUtil.getInstance().dispatchLater("EditorSwapSelectionBoundaries", ExchangePointAndMark.dc);
            final Editor editor = this.getEditor(ExchangePointAndMark.dc);
            ExchangePointAndMark.dc = null;
            if (editor.getCaretModel().getCaretCount() == 1) {
                editor.getScrollingModel().scrollTo(editor.getCaretModel().getPrimaryCaret().getLogicalPosition(), ScrollType.MAKE_VISIBLE);
            }
        }
    }
    
    static {
        ExchangePointAndMark.dc = null;
    }
    
    private static class myHandler extends EditorActionHandler
    {
        public myHandler() {
            super(true);
        }
        
        protected void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
            final SelectionModel selectionModel = editor.getSelectionModel();
            if (selectionModel.hasSelection()) {
                final VisualPosition pos = caret.getVisualPosition();
                final VisualPosition vpos = selectionModel.getLeadSelectionPosition();
                final VisualPosition epos = selectionModel.getSelectionEndPosition();
                final VisualPosition spos = selectionModel.getSelectionStartPosition();
                if (!pos.equals((Object)spos) && !pos.equals((Object)epos)) {
                    caret.moveToVisualPosition(vpos.equals((Object)epos) ? spos : epos);
                }
                ExchangePointAndMark.dc = dataContext;
            }
        }
    }
}
