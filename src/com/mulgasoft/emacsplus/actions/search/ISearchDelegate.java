// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindModel;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public interface ISearchDelegate
{
    JTextComponent getSearchField();
    
    JTextComponent getReplaceField();
    
    JComponent getComponent();
    
    FindModel getFindModel();
    
    boolean hasMatches();
    
    void searchForward();
    
    void searchBackward();
    
    void replaceCurrent();
    
    void showHistory(final boolean p0, final JTextComponent p1);
    
    void close();
    
    void requestFocus();
}
