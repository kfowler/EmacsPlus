//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.textarea.TextComponentEditor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.util.TextRange;
import com.mulgasoft.emacsplus.EmacsPlus;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.handlers.YankHandler;
import com.mulgasoft.emacsplus.util.ActionUtil;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.List;

public class YankPop extends Yanking
{
    private static List<String> yanks;
    private static TextRange dest;
    private static boolean yanker;
    private static boolean dispatched;

    public YankPop() {
        super((EditorActionHandler)new myHandler());
        EmacsPlusAction.addCommandListener(this, "yank-pop");
    }

    @Override
    public void before(final CommandEvent e) {
        YankPop.yanker = YankPop.yanks.contains(EmacsPlus.getUltCommand());
    }

    @Override
    public void after(final CommandEvent e) {
        YankPop.dispatched = false;
        if (YankPop.yanker) {
            this.popped(YankPop.dest);
            YankPop.yanker = false;
        }
    }

    static {
        YankPop.yanks = Arrays.asList("yank", "yank-pop");
        YankPop.dest = null;
        YankPop.yanker = false;
        YankPop.dispatched = false;
    }

    private static final class myHandler extends YankHandler
    {
        public void executeWriteAction(final Editor editor, final Caret caret, final DataContext dataContext) {
            if (YankPop.yanker && (!(editor instanceof TextComponentEditor) || caret.getOffset() == Yanking.getOffset())) {
                int index = Yanking.getIndex();
                final Transferable[] contents = CopyPasteManager.getInstance().getAllContents();
                if (index >= contents.length) {
                    index = 0;
                    Yanking.setIndex(index);
                }
                final Transferable content = contents[index];
                YankPop.dest = this.paste(editor, caret, content, Yanking.getLength());
            }
            else if (!YankPop.dispatched) {
                YankPop.dispatched = true;
                YankPop.yanker = false;
                if (editor instanceof EditorEx) {
                    ActionUtil.getInstance().dispatchLater("PasteMultiple", dataContext);
                }
                else {
                    this.beep();
                }
            }
        }

        private void beep() {
            ApplicationManager.getApplication().invokeLater((Runnable)new Runnable() {
                @Override
                public void run() {
                    EmacsPlus.beep(true);
                }
            });
        }
    }
}
