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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretState;
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


public class ISearchForward extends EditorAction implements EmacsPlusBA {
  private static final Logger LOG = Logger.getInstance(ISearchForward.class);

  private int myStartOffset = 0;
  private boolean myIsMulti = false;
  private EditorEx myEditor = null;
  private boolean isReplace = false;
  private theSelection oldSelection = null;
  private ISearchDelegate mySearcher = null;
  private final FindModel.FindModelObserver fmo;

  private String getNoActionMsg(final EditorAction action) {
    String GEN_MSG = "Emacs+ %s behavior not supported in this version of IDEA";
    return String.format(GEN_MSG, action.getTemplatePresentation().getText());
  }

  public ISearchForward(final boolean isReplace) {
    super(new IncrementalFindAction.Handler(isReplace));
    fmo = findModel -> {
      final boolean multi = findModel.isMultiline();
      if (multi != isMulti()) {
        myIsMulti = multi;
        if (mySearcher != null) {
          changeFieldActions(mySearcher, false);
        }
      }
    };
    EmacsPlusAction.addCommandListener(this, getName());
    this.isReplace = isReplace;
  }

  public ISearchForward() {
    this(false);
  }

  String getName() {
    return "isearch-forward";
  }

  ISearchDelegate getSearcher() {
    return mySearcher;
  }

  private boolean isMulti() {
    return myIsMulti;
  }

  public void before(final CommandEvent e) {
    myEditor = (EditorEx) ISHandler.getTextEditor(e.getProject());
    myStartOffset = myEditor.getCaretModel().getPrimaryCaret().getOffset();
    if (!isReplace) {
      oldSelection = new theSelection(myEditor.getSelectionModel(),
          (myEditor instanceof EditorEx) ? myEditor : null);
    }
  }

  public void after(final CommandEvent e) {
    mySearcher = ISearchFactory.getISearchObject(myEditor);
    if (mySearcher != null && mySearcher.getSearchField() != null) {
      final FindModel fm = mySearcher.getFindModel();
      fm.setRegularExpressions(false);
      myIsMulti = fm.isMultiline();
      changeFieldActions(mySearcher, false);
      setSwitchAction(mySearcher);
      watchModelChanges(fm);
      if (!isReplace && !oldSelection.isEmpty()) {
        myEditor.getSelectionModel()
            .setSelection(oldSelection.vpstart, oldSelection.start, oldSelection.vpend, oldSelection.end);
        if (oldSelection.isSticky) {
          myEditor.setStickySelection(true);
        }
      }
    } else {
      EmacsPlusAction.errorMessage(getNoActionMsg(this));
    }
  }

  void changeFieldActions(final ISearchDelegate searcher, final boolean isReplace) {
    final JTextComponent field = isReplace ? searcher.getReplaceField() : searcher.getSearchField();
    final KeyboardShortcut kbS = new KeyboardShortcut(KeyStroke.getKeyStroke(9, 512), null);
    if (!addToAction("com.intellij.find.editorHeaderActions.ShowHistoryAction", kbS, field)
        && findAction(field, InnerShowHistory.class) == null) {
      new InnerShowHistory(searcher, field, kbS);
    }
    final KeyStroke ksG = KeyStroke.getKeyStroke(71, 128);
    removeFromAction("com.intellij.find.editorHeaderActions.CloseOnESCAction", ksG, field);
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
    removeFromAction(NextOccurrenceAction.class, new KeyboardShortcut(ksE, null), field);
    if (isReplace) {
      replaceSpecifics(ksE, field);
    } else {
      searchSpecifics(searcher, ksE, field);
    }
  }

  private void replaceSpecifics(final KeyStroke ksE, final JComponent field) {
    if (!isMulti()) {
      removeFromAction("com.intellij.find.editorHeaderActions.ReplaceOnEnterAction", ksE, field);
      field.getActionMap().put("IS.Replace", new IReplaceReturn("IS.Replace"));
      field.getInputMap().put(ksE, "IS.Replace");
    }
  }

  private void searchSpecifics(final ISearchDelegate searcher, final KeyStroke ksE, final JComponent field) {
    final KeyboardShortcut kbC = new KeyboardShortcut(KeyStroke.getKeyStroke(9, 128), null);
    if (field instanceof JTextComponent) {
      addToAction(VariantsCompletionAction.class, kbC, (JTextComponent) field);
    }
    if (!isMulti() && !searcher.getFindModel().isReplaceState()) {
      field.getActionMap().put("IS.Enter", new ISearchReturn("IS.Enter"));
      field.getInputMap().put(ksE, "IS.Enter");
      if (findAction(field, InnerISearchReturn.class) == null) {
        new InnerISearchReturn(searcher, field, new KeyboardShortcut(KeyStroke.getKeyStroke(10, 0), null));
      }
    }
  }

  private void watchModelChanges(final FindModel model) {
    model.addObserver(fmo);
  }

  private void setSwitchAction(final ISearchDelegate searcher) {
    new SwitchToISearch(searcher);
    new SwitchToISearchBack(searcher);
  }

  private void cleanUp() {
    if (mySearcher != null) {
      final FindModel fm = mySearcher.getFindModel();
      if (fm.isRegularExpressions()) {
        fm.setRegularExpressions(false);
      }
      mySearcher = null;
    }
  }

  private void isearchReturn() {
    final ISearchDelegate searcher = getSearcher();
    if (searcher != null) {
      cleanUp();
      searcher.close();
    }
    myEditor.getSelectionModel().removeSelection();
    myEditor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
  }

  private AnAction findAction(final JComponent field, final Class actionClass) {
    final List<AnAction> actions = (List<AnAction>) field.getClientProperty("AnAction.shortcutSet");
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
    final AnAction action = findAction(field, actionClass);
    if (action != null) {
      result = true;
      final Shortcut[] cuts = action.getShortcutSet().getShortcuts();
      if (findCut(cuts, cut) == null) {
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
      result = addToAction(clazz, cut, field);
    } catch (ClassNotFoundException e) {
      LOG.error(e);
    }
    return result;
  }

  private boolean removeFromAction(final Class actionClass, final Shortcut cut, final JComponent field) {
    boolean result = false;
    final AnAction action = findAction(field, actionClass);
    if (action != null) {
      final Shortcut[] cuts = action.getShortcutSet().getShortcuts();
      final Shortcut oldCut = findCut(cuts, cut);
      if (oldCut != null) {
        final Shortcut[] newcuts = new Shortcut[cuts.length - 1];
        int diff = 0;
        for (int i = 0; i < cuts.length; ++i) {
          if (cuts[i] == oldCut) {
            diff = 1;
            result = true;
          } else {
            newcuts[i - diff] = cuts[i];
          }
        }
        action.unregisterCustomShortcutSet(field);
        action.registerCustomShortcutSet(new CustomShortcutSet(newcuts), field);
      }
    }
    return result;
  }

  private void removeFromAction(final String actionClass, final KeyStroke ks, final JComponent field) {
    if (!removeFromAction(actionClass, new KeyboardShortcut(ks, null), field)) {
      field.unregisterKeyboardAction(ks);
    }
  }

  private boolean removeFromAction(final String actionClass, final Shortcut cut, final JComponent field) {
    boolean result = false;
    try {
      final Class clazz = Class.forName(actionClass);
      result = removeFromAction(clazz, cut, field);
    } catch (ClassNotFoundException e) {
      LOG.error(e);
    }
    return result;
  }

  private class theSelection {
    VisualPosition vpstart;
    VisualPosition vpend;
    int start;
    int end;
    boolean isSticky;

    theSelection(final SelectionModel sm, final EditorEx editor) {
      isSticky = false;
      if (sm.hasSelection()) {
        vpstart = sm.getSelectionStartPosition();
        vpend = sm.getSelectionEndPosition();
        start = sm.getSelectionStart();
        end = sm.getSelectionEnd();
        if (editor != null) {
          isSticky = editor.isStickySelection();
          editor.setStickySelection(false);
        } else {
          sm.removeSelection(true);
        }
      }
    }

    boolean isEmpty() {
      return vpstart == null;
    }
  }

  private class ISearchInterrupt extends TextAction {
    ISearchInterrupt(final String name) {
      super(name);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      boolean hasMatches = true;
      if (mySearcher != null) {
        hasMatches = mySearcher.hasMatches();
        mySearcher.close();
        cleanUp();
      }
      if (hasMatches) {
        myEditor.getCaretModel().moveToOffset(myStartOffset);
        myEditor.getSelectionModel().removeSelection();
      }
      myEditor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
    }
  }

  private class InnerShowHistory extends AnAction implements DumbAware {
    JTextComponent field;

    InnerShowHistory(final ISearchDelegate searcher, final JTextComponent field, final KeyboardShortcut shortcut) {
      this.field = null;
      this.field = field;
      registerCustomShortcutSet(new CustomShortcutSet(shortcut), field);
    }

    public void actionPerformed(final AnActionEvent e) {
      getSearcher().showHistory(false, field);
    }

    public void update(final AnActionEvent e) {
      e.getPresentation().setEnabled(getSearcher() != null);
    }
  }

  private class InnerISearchReturn extends AnAction implements DumbAware {
    JComponent field;

    InnerISearchReturn(final ISearchDelegate searcher, final JComponent field, final KeyboardShortcut shortcut) {
      this.field = null;
      this.field = field;
      registerCustomShortcutSet(new CustomShortcutSet(shortcut), field);
    }

    public void actionPerformed(final AnActionEvent e) {
      isearchReturn();
    }

    public void update(final AnActionEvent e) {
      final ISearchDelegate searcher = getSearcher();
      e.getPresentation()
          .setEnabled(
              searcher != null && searcher.hasMatches() && !isMulti() && !StringUtil.isEmpty(
                  searcher.getSearchField().getText()));
    }
  }

  private class ISearchReturn extends TextAction {
    ISearchReturn(final String name) {
      super(name);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      isearchReturn();
    }
  }

  private class IReplaceReturn extends TextAction {
    private boolean once;

    IReplaceReturn(final String name) {
      super(name);
      once = true;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      final ISearchDelegate searcher = getSearcher();
      if (searcher != null) {
        final List<CaretState> state = makeState(searcher);
        searcher.replaceCurrent();
        if (state != null && !searcher.hasMatches()) {
          myEditor.getCaretModel().setCaretsAndSelections(state);
        }
      }
    }

    private List<CaretState> makeState(final ISearchDelegate searcher) {
      List<CaretState> result = null;
      if (once) {
        once = false;
        final SelectionModel sm = myEditor.getSelectionModel();
        if (sm.hasSelection() && !searcher.getFindModel().isGlobal()) {
          final int off = myEditor.getCaretModel().getOffset();
          final int[] starts = sm.getBlockSelectionStarts();
          final int[] ends = sm.getBlockSelectionEnds();
          for (int i = 0; i < starts.length; ++i) {
            if (starts[i] != ends[i]) {
              if (result == null) {
                result = new ArrayList<>();
              }
              result.add(new CaretState(myEditor.offsetToLogicalPosition(off), myEditor.offsetToLogicalPosition(starts[i]), myEditor.offsetToLogicalPosition(ends[i])));
            }
          }
        }
      }
      return result;
    }
  }

  private class IMoveDown extends TextAction {
    private boolean isReplace;

    IMoveDown(final String name, final boolean isReplace) {
      super(name);
      this.isReplace = false;
      this.isReplace = isReplace;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      final ISearchDelegate searcher = getSearcher();
      if (searcher != null) {
        final JTextComponent field = isReplace ? searcher.getReplaceField() : searcher.getSearchField();
        field.setCaretPosition(field.getText().length());
      }
    }
  }

  private class IMoveUp extends TextAction {
    private boolean isReplace;

    IMoveUp(final String name, final boolean isReplace) {
      super(name);
      this.isReplace = false;
      this.isReplace = isReplace;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      final ISearchDelegate searcher = getSearcher();
      if (searcher != null) {
        final JTextComponent field = isReplace ? searcher.getReplaceField() : searcher.getSearchField();
        field.setCaretPosition(0);
      }
    }
  }

  private class SwitchToISearch extends AnAction {
    SwitchToISearch(final ISearchDelegate searcher) {
      registerCustomShortcutSet(ISearchForward.this.getShortcutSet(), searcher.getComponent());
    }

    public void actionPerformed(final AnActionEvent e) {
      final JTextComponent field = getSearcher().getSearchField();
      if (field != null && field.getText().isEmpty()) {
        final AnAction action = findAction(field, RestorePreviousSettingsAction.class);
        if (action != null) {
          action.update(e);
          action.actionPerformed(e);
        } else {
          final FindModel model = FindManager.getInstance(e.getProject()).getPreviousFindModel();
          if (model != null) {
            getSearcher().getFindModel().copyFrom(model);
          }
        }
      }
      final AnAction action = ActionManager.getInstance().getAction("FindNext");
      action.update(e);
      action.actionPerformed(e);
    }
  }

  private class SwitchToISearchBack extends AnAction {
    SwitchToISearchBack(final ISearchDelegate searcher) {
      final Collection<Shortcut> shortcuts = new ArrayList<>();
      ContainerUtil.addAll(shortcuts,
          ActionManager.getInstance().getAction("Emacs+.ISearchBackward").getShortcutSet().getShortcuts());
      registerCustomShortcutSet(
          new CustomShortcutSet(shortcuts.toArray(new Shortcut[shortcuts.size()])),
          searcher.getComponent());
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
      final ISearchDelegate searcher = getSearcher();
      if (searcher != null) {
        searcher.searchBackward();
        final JTextComponent field = searcher.getSearchField();
        if (field.getText().isEmpty()) {
          final String[] vals = FindSettings.getInstance().getRecentFindStrings();
          if (vals.length > 0) {
            final int offset = myEditor.getCaretModel().getOffset();
            setInitialText(searcher, field, vals[vals.length - 1]);
            int adj;
            if ((adj = offset) < myEditor.getDocument().getTextLength()) {
              ++adj;
            }
            try {
              myEditor.getCaretModel().moveToOffset(adj);
              FindUtil.searchBack(e.getProject(), myEditor, null);
            } finally {
              if (myEditor.getCaretModel().getOffset() == adj) {
                myEditor.getCaretModel().moveToOffset(offset);
              }
            }
          }
        }
      }
    }
  }
}
