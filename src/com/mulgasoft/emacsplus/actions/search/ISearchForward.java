//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindSettings;
import com.intellij.find.FindUtil;
import com.intellij.find.editorHeaderActions.NextOccurrenceAction;
import com.intellij.find.editorHeaderActions.RestorePreviousSettingsAction;
import com.intellij.find.editorHeaderActions.VariantsCompletionAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actions.IncrementalFindAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;
import com.mulgasoft.emacsplus.actions.EmacsPlusBA;
import com.mulgasoft.emacsplus.handlers.ISHandler;
import com.mulgasoft.emacsplus.keys.Keymaps;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import org.jetbrains.annotations.NonNls;

public class ISearchForward extends EditorAction implements EmacsPlusBA
{
    String GEN_MSG;
    @NonNls
    private static final String REPLACE_CLASS = "com.intellij.find.editorHeaderActions.ReplaceOnEnterAction";
    @NonNls
    private static final String CLOSE_CLASS = "com.intellij.find.editorHeaderActions.CloseOnESCAction";
    @NonNls
    private static final String HISTORY_CLASS = "com.intellij.find.editorHeaderActions.ShowHistoryAction";
    @NonNls
    private static final String UP_ACTION = "IS.Up";
    @NonNls
    private static final String DOWN_ACTION = "IS.Down";
    @NonNls
    private static final String ENTER_ACTION = "IS.Enter";
    @NonNls
    private static final String REPLACE_ACTION = "IS.Replace";
    @NonNls
    private static final String INTERRUPT_ACTION = "IS.Interrupt";
    @NonNls
    private static final String UNDO_ACTION = "undoKeystroke";
    private int myStartOffset;
    private boolean myIsMulti;
    protected EditorEx myEditor;
    private boolean isReplace;
    private theSelection oldSelection;
    private ISearchDelegate mySearcher;
    private FindModel.FindModelObserver fmo;

    String getNoActionMsg(final EditorAction action) {
        return String.format(this.GEN_MSG, action.getTemplatePresentation().getText());
    }

    protected ISearchForward(final boolean isReplace) {
        super(new IncrementalFindAction.Handler(isReplace));
        this.GEN_MSG = "Emacs+ %s behavior not supported in this version of IDEA";
        this.myStartOffset = 0;
        this.myIsMulti = false;
        this.myEditor = null;
        this.isReplace = false;
        this.oldSelection = null;
        this.mySearcher = null;
        this.fmo = new FindModel.FindModelObserver() {
            public void findModelChanged(final FindModel findModel) {
                final boolean multi = findModel.isMultiline();
                if (multi != ISearchForward.this.isMulti()) {
                    ISearchForward.this.myIsMulti = multi;
                    if (ISearchForward.this.mySearcher != null) {
                        ISearchForward.this.changeFieldActions(ISearchForward.this.mySearcher, false);
                    }
                }
            }
        };
        EmacsPlusAction.addCommandListener(this, this.getName());
        this.isReplace = isReplace;
    }

    protected ISearchForward() {
        this(false);
    }

    protected String getName() {
        return "isearch-forward";
    }

    protected Editor getEditor() {
        return this.myEditor;
    }

    protected ISearchDelegate getSearcher() {
        return this.mySearcher;
    }

    private boolean isMulti() {
        return this.myIsMulti;
    }

    public void before(final CommandEvent e) {
        this.myEditor = (EditorEx) ISHandler.getTextEditor(e.getProject());
        this.myStartOffset = this.myEditor.getCaretModel().getPrimaryCaret().getOffset();
        if (!this.isReplace) {
            this.oldSelection = new theSelection(this.myEditor.getSelectionModel(), (this.myEditor instanceof EditorEx) ? this.myEditor : null);
        }
    }

    public void after(final CommandEvent e) {
        this.mySearcher = ISearchFactory.getISearchObject(this.myEditor);
        if (this.mySearcher != null && this.mySearcher.getSearchField() != null) {
            final FindModel fm = this.mySearcher.getFindModel();
            fm.setRegularExpressions(false);
            this.myIsMulti = fm.isMultiline();
            this.changeFieldActions(this.mySearcher, false);
            this.setSwitchAction(this.mySearcher);
            this.watchModelChanges(fm);
            if (!this.isReplace && !this.oldSelection.isEmpty()) {
                this.myEditor.getSelectionModel().setSelection(this.oldSelection.vpstart, this.oldSelection.start, this.oldSelection.vpend, this.oldSelection.end);
                if (this.oldSelection.isSticky) {
                    this.myEditor.setStickySelection(true);
                }
            }
        }
        else {
            EmacsPlusAction.errorMessage(this.getNoActionMsg(this));
        }
    }

    protected void changeFieldActions(final ISearchDelegate searcher, final boolean isReplace) {
        final JTextComponent field = isReplace ? searcher.getReplaceField() : searcher.getSearchField();
        final KeyboardShortcut kbS = new KeyboardShortcut(KeyStroke.getKeyStroke(9, 512), null);
        if (!this.addToAction("com.intellij.find.editorHeaderActions.ShowHistoryAction", kbS, field) && this.findAction(field, InnerShowHistory.class) == null) {
            new InnerShowHistory(searcher, field, kbS);
        }
        final KeyStroke ksG = KeyStroke.getKeyStroke(71, 128);
        this.removeFromAction("com.intellij.find.editorHeaderActions.CloseOnESCAction", ksG, field);
        final InputMap im = field.getInputMap();
        final ActionMap am = field.getActionMap();
        am.put("IS.Interrupt", new ISearchInterrupt("IS.Interrupt"));
        im.put(ksG, "IS.Interrupt");
        im.put(KeyStroke.getKeyStroke(27, 0), "IS.Interrupt");
        am.put("IS.Down", new IMoveDown("IS.Down", isReplace));
        im.put(KeyStroke.getKeyStroke(86, 128), "IS.Down");
        am.put("IS.Up", new IMoveUp("IS.Up", isReplace));
        im.put(KeyStroke.getKeyStroke(86, Keymaps.getMeta()), "IS.Up");
        im.put(Keymaps.getIntlKeyStroke(47), "undoKeystroke");
        final KeyStroke ksE = KeyStroke.getKeyStroke(10, 0);
        this.removeFromAction(NextOccurrenceAction.class, new KeyboardShortcut(ksE, null), field);
        if (isReplace) {
            this.replaceSpecifics(ksE, field);
        }
        else {
            this.searchSpecifics(searcher, ksE, field);
        }
    }

    private void replaceSpecifics(final KeyStroke ksE, final JComponent field) {
        if (!this.isMulti()) {
            this.removeFromAction("com.intellij.find.editorHeaderActions.ReplaceOnEnterAction", ksE, field);
            field.getActionMap().put("IS.Replace", new IReplaceReturn("IS.Replace"));
            field.getInputMap().put(ksE, "IS.Replace");
        }
    }

    private void searchSpecifics(final ISearchDelegate searcher, final KeyStroke ksE, final JComponent field) {
        final KeyboardShortcut kbC = new KeyboardShortcut(KeyStroke.getKeyStroke(9, 128), null);
        if (field instanceof JTextComponent) {
            this.addToAction(VariantsCompletionAction.class, kbC, (JTextComponent)field);
        }
        if (!this.isMulti() && !searcher.getFindModel().isReplaceState()) {
            field.getActionMap().put("IS.Enter", new ISearchReturn("IS.Enter"));
            field.getInputMap().put(ksE, "IS.Enter");
            if (this.findAction(field, InnerISearchReturn.class) == null) {
                new InnerISearchReturn(searcher, field, new KeyboardShortcut(KeyStroke.getKeyStroke(10, 0), null));
            }
        }
    }

    private void watchModelChanges(final FindModel model) {
        model.addObserver(this.fmo);
    }

    protected void setSwitchAction(final ISearchDelegate searcher) {
        new SwitchToISearch(searcher);
        new SwitchToISearchBack(searcher);
    }

    private void cleanUp() {
        if (this.mySearcher != null) {
            final FindModel fm = this.mySearcher.getFindModel();
            if (fm.isRegularExpressions()) {
                fm.setRegularExpressions(false);
            }
            this.mySearcher = null;
        }
    }

    private void isearchReturn() {
        final ISearchDelegate searcher = this.getSearcher();
        if (searcher != null) {
            this.cleanUp();
            searcher.close();
        }
        this.myEditor.getSelectionModel().removeSelection();
        this.myEditor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
    }

    private AnAction findAction(final JComponent field, final Class actionClass) {
        final List<AnAction> actions = (List<AnAction>)field.getClientProperty("AnAction.shortcutSet");
        AnAction action = null;
        for (final AnAction a : actions) {
            if (actionClass.isInstance(a)) {
                action = a;
                break;
            }
        }
        return action;
    }

    private Shortcut findCut(final Shortcut[] cuts, final Shortcut cut) {
        Shortcut result = null;
        for (final Shortcut sc : cuts) {
            if (sc.startsWith(cut)) {
                result = sc;
                break;
            }
        }
        return result;
    }

    private boolean addToAction(final Class actionClass, final Shortcut cut, final JTextComponent field) {
        boolean result = false;
        final AnAction action = this.findAction(field, actionClass);
        if (action != null) {
            result = true;
            final Shortcut[] cuts = action.getShortcutSet().getShortcuts();
            if (this.findCut(cuts, cut) == null) {
                final Shortcut[] newcuts = new Shortcut[cuts.length + 1];
                newcuts[0] = cut;
                System.arraycopy(cuts, 0, newcuts, 1, cuts.length);
                action.unregisterCustomShortcutSet(field);
                action.registerCustomShortcutSet(new CustomShortcutSet(newcuts), field);
            }
        }
        return result;
    }

    private boolean addToAction(final String actionClass, final Shortcut cut, final JTextComponent field) {
        boolean result = false;
        try {
            final Class clazz = Class.forName(actionClass);
            result = this.addToAction(clazz, cut, field);
        }
        catch (ClassNotFoundException ex) {}
        return result;
    }

    private boolean removeFromAction(final Class actionClass, final Shortcut cut, final JComponent field) {
        boolean result = false;
        final AnAction action = this.findAction(field, actionClass);
        if (action != null) {
            final Shortcut[] cuts = action.getShortcutSet().getShortcuts();
            final Shortcut oldCut = this.findCut(cuts, cut);
            if (oldCut != null) {
                final Shortcut[] newcuts = new Shortcut[cuts.length - 1];
                int diff = 0;
                for (int i = 0; i < cuts.length; ++i) {
                    if (cuts[i] == oldCut) {
                        diff = 1;
                        result = true;
                    }
                    else {
                        newcuts[i - diff] = cuts[i];
                    }
                }
                action.unregisterCustomShortcutSet(field);
                action.registerCustomShortcutSet(new CustomShortcutSet(newcuts), field);
            }
        }
        return result;
    }

    private boolean removeFromAction(final String actionClass, final KeyStroke ks, final JComponent field) {
        boolean result = this.removeFromAction(actionClass, new KeyboardShortcut(ks, null), field);
        if (!result) {
            field.unregisterKeyboardAction(ks);
            result = true;
        }
        return result;
    }

    private boolean removeFromAction(final String actionClass, final Shortcut cut, final JComponent field) {
        boolean result = false;
        try {
            final Class clazz = Class.forName(actionClass);
            result = this.removeFromAction(clazz, cut, field);
        }
        catch (ClassNotFoundException ex) {}
        return result;
    }

    private class theSelection
    {
        VisualPosition vpstart;
        VisualPosition vpend;
        int start;
        int end;
        boolean isSticky;

        theSelection(final SelectionModel sm, final EditorEx editor) {
            this.isSticky = false;
            if (sm.hasSelection()) {
                this.vpstart = sm.getSelectionStartPosition();
                this.vpend = sm.getSelectionEndPosition();
                this.start = sm.getSelectionStart();
                this.end = sm.getSelectionEnd();
                if (editor != null) {
                    this.isSticky = editor.isStickySelection();
                    editor.setStickySelection(false);
                }
                else {
                    sm.removeSelection(true);
                }
            }
        }

        boolean isEmpty() {
            return this.vpstart == null;
        }
    }

    private class ISearchInterrupt extends TextAction
    {
        public ISearchInterrupt(final String name) {
            super(name);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            boolean hasMatches = true;
            if (ISearchForward.this.mySearcher != null) {
                hasMatches = ISearchForward.this.mySearcher.hasMatches();
                ISearchForward.this.mySearcher.close();
                ISearchForward.this.cleanUp();
            }
            if (hasMatches) {
                ISearchForward.this.myEditor.getCaretModel().moveToOffset(ISearchForward.this.myStartOffset);
                ISearchForward.this.myEditor.getSelectionModel().removeSelection();
            }
            ISearchForward.this.myEditor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
        }
    }

    private class InnerShowHistory extends AnAction implements DumbAware
    {
        JTextComponent field;

        protected InnerShowHistory(final ISearchDelegate searcher, final JTextComponent field, final KeyboardShortcut shortcut) {
            this.field = null;
            this.field = field;
            this.registerCustomShortcutSet(new CustomShortcutSet(shortcut), field);
        }

        public void actionPerformed(final AnActionEvent e) {
            ISearchForward.this.getSearcher().showHistory(false, this.field);
        }

        public void update(final AnActionEvent e) {
            e.getPresentation().setEnabled(ISearchForward.this.getSearcher() != null);
        }
    }

    private class InnerISearchReturn extends AnAction implements DumbAware
    {
        JComponent field;

        protected InnerISearchReturn(final ISearchDelegate searcher, final JComponent field, final KeyboardShortcut shortcut) {
            this.field = null;
            this.field = field;
            this.registerCustomShortcutSet(new CustomShortcutSet(shortcut), field);
        }

        public void actionPerformed(final AnActionEvent e) {
            ISearchForward.this.isearchReturn();
        }

        public void update(final AnActionEvent e) {
            final ISearchDelegate searcher = ISearchForward.this.getSearcher();
            e.getPresentation().setEnabled(searcher != null && searcher.hasMatches() && !ISearchForward.this.isMulti() && !StringUtil.isEmpty(searcher.getSearchField().getText()));
        }
    }

    private class ISearchReturn extends TextAction
    {
        public ISearchReturn(final String name) {
            super(name);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            ISearchForward.this.isearchReturn();
        }
    }

    private class IReplaceReturn extends TextAction
    {
        private boolean once;

        public IReplaceReturn(final String name) {
            super(name);
            this.once = true;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            final ISearchDelegate searcher = ISearchForward.this.getSearcher();
            if (searcher != null) {
                final List<CaretState> state = this.makeState(searcher);
                searcher.replaceCurrent();
                if (state != null && !searcher.hasMatches()) {
                    ISearchForward.this.myEditor.getCaretModel().setCaretsAndSelections(state);
                }
            }
        }

        private List<CaretState> makeState(final ISearchDelegate searcher) {
            List<CaretState> result = null;
            if (this.once) {
                this.once = false;
                final SelectionModel sm = ISearchForward.this.myEditor.getSelectionModel();
                if (sm.hasSelection() && !searcher.getFindModel().isGlobal()) {
                    final int off = ISearchForward.this.myEditor.getCaretModel().getOffset();
                    final int[] starts = sm.getBlockSelectionStarts();
                    final int[] ends = sm.getBlockSelectionEnds();
                    for (int i = 0; i < starts.length; ++i) {
                        if (starts[i] != ends[i]) {
                            if (result == null) {
                                result = new ArrayList<CaretState>();
                            }
                            result.add(new CaretState(ISearchForward.this.myEditor.offsetToLogicalPosition(off), ISearchForward.this.myEditor.offsetToLogicalPosition(starts[i]), ISearchForward.this.myEditor.offsetToLogicalPosition(ends[i])));
                        }
                    }
                }
            }
            return result;
        }
    }

    private class IMoveDown extends TextAction
    {
        private boolean isReplace;

        public IMoveDown(final String name, final boolean isReplace) {
            super(name);
            this.isReplace = false;
            this.isReplace = isReplace;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            final ISearchDelegate searcher = ISearchForward.this.getSearcher();
            if (searcher != null) {
                final JTextComponent field = this.isReplace ? searcher.getReplaceField() : searcher.getSearchField();
                field.setCaretPosition(field.getText().length());
            }
        }
    }

    private class IMoveUp extends TextAction
    {
        private boolean isReplace;

        public IMoveUp(final String name, final boolean isReplace) {
            super(name);
            this.isReplace = false;
            this.isReplace = isReplace;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            final ISearchDelegate searcher = ISearchForward.this.getSearcher();
            if (searcher != null) {
                final JTextComponent field = this.isReplace ? searcher.getReplaceField() : searcher.getSearchField();
                field.setCaretPosition(0);
            }
        }
    }

    private class SwitchToISearch extends AnAction
    {
        SwitchToISearch(final ISearchDelegate searcher) {
            this.registerCustomShortcutSet(ISearchForward.this.getShortcutSet(), searcher.getComponent());
        }

        public void actionPerformed(final AnActionEvent e) {
            final JTextComponent field = ISearchForward.this.getSearcher().getSearchField();
            if (field != null && field.getText().isEmpty()) {
                final AnAction action = ISearchForward.this.findAction(field, RestorePreviousSettingsAction.class);
                if (action != null) {
                    action.update(e);
                    action.actionPerformed(e);
                }
                else {
                    final FindModel model = FindManager.getInstance(e.getProject()).getPreviousFindModel();
                    if (model != null) {
                        ISearchForward.this.getSearcher().getFindModel().copyFrom(model);
                    }
                }
            }
            final AnAction action = ActionManager.getInstance().getAction("FindNext");
            action.update(e);
            action.actionPerformed(e);
        }
    }

    private class SwitchToISearchBack extends AnAction
    {
        SwitchToISearchBack(final ISearchDelegate searcher) {
            final ArrayList<Shortcut> shortcuts = new ArrayList<Shortcut>();
            ContainerUtil.addAll((Collection)shortcuts, (Object[])ActionManager.getInstance().getAction("Emacs+.ISearchBackward").getShortcutSet().getShortcuts());
            this.registerCustomShortcutSet(
                new CustomShortcutSet((Shortcut[])shortcuts.toArray(new Shortcut[shortcuts.size()])), searcher.getComponent());
        }

        private void setInitialText(final ISearchDelegate seacher, final JTextComponent field, final String itext) {
            final String text = (itext != null) ? itext : "";
            if (text.contains("\n")) {
                seacher.getFindModel().setMultiline(true);
            }
            field.setText(text);
            seacher.getFindModel().setStringToFind(text);
            field.selectAll();
        }

        public void actionPerformed(final AnActionEvent e) {
            final ISearchDelegate searcher = ISearchForward.this.getSearcher();
            if (searcher != null) {
                searcher.searchBackward();
                final JTextComponent field = searcher.getSearchField();
                if (field.getText().isEmpty()) {
                    final String[] vals = FindSettings.getInstance().getRecentFindStrings();
                    if (vals.length > 0) {
                        final int offset = ISearchForward.this.myEditor.getCaretModel().getOffset();
                        this.setInitialText(searcher, field, vals[vals.length - 1]);
                        int adj;
                        if ((adj = offset) < ISearchForward.this.myEditor.getDocument().getTextLength()) {
                            ++adj;
                        }
                        try {
                            ISearchForward.this.myEditor.getCaretModel().moveToOffset(adj);
                            FindUtil.searchBack(e.getProject(), ISearchForward.this.myEditor, null);
                        }
                        finally {
                            if (ISearchForward.this.myEditor.getCaretModel().getOffset() == adj) {
                                ISearchForward.this.myEditor.getCaretModel().moveToOffset(offset);
                            }
                        }
                    }
                }
            }
        }
    }
}
