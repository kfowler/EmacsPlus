//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.util;

import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.CaretStateTransferableData;
import com.intellij.openapi.editor.ClipboardTextPerCaretSplitter;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ide.KillRingTransferable;
import com.intellij.openapi.util.text.StringUtil;
import com.mulgasoft.emacsplus.EmacsPlus;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import org.jetbrains.annotations.NotNull;


public final class KillCmdUtil {
  private KillCmdUtil() {
  }

  public static void killWrapper(final String cmdId, final DataContext data, final Document doc) {
    killWrapper(cmdId, data, doc, false);
  }

  private static boolean wasNextKill() {
    return "append-next-kill".equals(EmacsPlus.getPenultCommand());
  }

  private static boolean isNextKill() {
    return "append-next-kill".equals(EmacsPlus.getUltCommand());
  }

  public static KillRingInfo beforeKill() {
    int killLen = 0;
    Transferable prevContent = null;
    if (isNextKill()) {
      final Transferable[] contents = CopyPasteManager.getInstance().getAllContents();
      killLen = contents.length;
      if (killLen > 0) {
        prevContent = contents[0];
        if (prevContent instanceof KillRingTransferable) {
          ((KillRingTransferable) prevContent).setReadyToCombine(false);
        }
      }
    }
    return new KillRingInfo(killLen, prevContent);
  }

  public static void afterKill(final KillRingInfo info, final Document doc, final boolean isCut) {
    if (info != null && info.topContent != null) {
      Transferable[] contents = CopyPasteManager.getInstance().getAllContents();
      if (contents.length > 1) {
        final String oldD = getTransferableText(info.topContent);
        final String newD = getTransferableText(contents[0]);
        if (CopyPasteManager.getInstance() instanceof CopyPasteManagerEx) {
          int start = -1;
          int end = -1;
          if (contents[0] instanceof KillRingTransferable) {
            start = ((KillRingTransferable) contents[0]).getStartOffset();
            end = ((KillRingTransferable) contents[0]).getEndOffset();
          }
          final KillRingTransferable newT = new KillRingTransferable(oldD + newD, doc, start, end, isCut);
          contents = CopyPasteManager.getInstance().getAllContents();
          final CopyPasteManagerEx cpm = (CopyPasteManagerEx) CopyPasteManager.getInstance();
          cpm.removeContent(contents[1]);
          cpm.removeContent(contents[0]);
          cpm.setContents(newT);
          newT.setReadyToCombine(true);
        }
      }
    }
  }

  private static void killWrapper(final String cmdId, final DataContext data, final Document doc, final boolean isCut) {
    int killLen = 0;
    Transferable prevContent = null;
    KillRingInfo info = null;
    try {
      if (wasNextKill()) {
        final Transferable[] contents = CopyPasteManager.getInstance().getAllContents();
        killLen = contents.length;
        if (killLen > 0) {
          prevContent = contents[0];
          if (prevContent instanceof KillRingTransferable) {
            ((KillRingTransferable) prevContent).setReadyToCombine(false);
          }
        }
        info = new KillRingInfo(killLen, prevContent);
      }
      ActionUtil.getInstance().dispatch(cmdId, data);
    } finally {
      afterKill(info, doc, isCut);
    }
  }

  @NotNull
  private static String getTransferableText(@NotNull final Transferable data) {
    if (data == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "data",
              "com/mulgasoft/emacsplus/util/KillCmdUtil", "getTransferableText"));
    }
    final String transferableText = getTransferableText(data, "\n");
    if (transferableText == null) {
      throw new IllegalStateException(
          String.format("@NotNull method %s.%s must not return null", "com/mulgasoft/emacsplus/util/KillCmdUtil",
              "getTransferableText"));
    }
    return transferableText;
  }

  public static String getTransferableText(@NotNull final Transferable data, @NotNull final String lineSepr) {
    if (data == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "data",
              "com/mulgasoft/emacsplus/util/KillCmdUtil", "getTransferableText"));
    }
    if (lineSepr == null) {
      throw new IllegalArgumentException(
          String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "lineSepr",
              "com/mulgasoft/emacsplus/util/KillCmdUtil", "getTransferableText"));
    }
    String text = EditorModificationUtil.getStringContent(data);
    if (text != null) {
      try {
        CaretStateTransferableData caretData = null;
        caretData = (data.isDataFlavorSupported(CaretStateTransferableData.FLAVOR)
            ? ((CaretStateTransferableData) data.getTransferData(CaretStateTransferableData.FLAVOR)) : null);
        if (caretData != null) {
          final List<String> segments =
              new ClipboardTextPerCaretSplitter().split(text, caretData, caretData.startOffsets.length);
          final StringBuilder buf = new StringBuilder();
          for (final String s : segments) {
            buf.append(s);
          }
          text = buf.toString();
        } else {
          text = (String) data.getTransferData(DataFlavor.stringFlavor);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      text = StringUtil.convertLineSeparators(text, lineSepr);
    }
    return (text == null) ? "" : text;
  }

  public static class KillRingInfo {
    int killRingLen;
    Transferable topContent;

    KillRingInfo(final int killRingLen, final Transferable topContent) {
      this.killRingLen = 0;
      this.topContent = null;
      this.killRingLen = killRingLen;
      this.topContent = topContent;
    }
  }
}
