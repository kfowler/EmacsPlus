//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindModel;
import com.intellij.openapi.editor.Editor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.swing.text.JTextComponent;

public class ISearch14 implements ISearchDelegate
{
    private Editor editor;
    private JComponent searchComp;

    public ISearch14(final Editor editor, final JComponent component) {
        this.editor = null;
        this.searchComp = null;
        this.editor = editor;
        this.searchComp = component;
    }

    protected Object invoke(final Object element, final String method) {
        Object result = null;
        try {
            final Method meth;
            if (element != null && (meth = element.getClass().getMethod(method, (Class<?>[])new Class[0])) != null) {
                result = meth.invoke(element, new Object[0]);
            }
        }
        catch (NoSuchMethodException e) {}
        catch (InvocationTargetException e2) {}
        catch (IllegalAccessException ex) {}
        return result;
    }

    @Override
    public JComponent getComponent() {
        return this.searchComp;
    }

    @Override
    public JTextComponent getSearchField() {
        return (JTextComponent)this.invoke(this.searchComp, "getSearchField");
    }

    @Override
    public JTextComponent getReplaceField() {
        return (JTextComponent)this.invoke(this.searchComp, "getReplaceField");
    }

    @Override
    public FindModel getFindModel() {
        return (FindModel)this.invoke(this.searchComp, "getFindModel");
    }

    @Override
    public boolean hasMatches() {
        return (boolean)this.invoke(this.searchComp, "hasMatches");
    }

    @Override
    public void searchForward() {
        this.invoke(this.searchComp, "searchForward");
    }

    @Override
    public void searchBackward() {
        this.invoke(this.searchComp, "searchBackward");
    }

    @Override
    public void replaceCurrent() {
        this.invoke(this.searchComp, "replaceCurrent");
    }

    @Override
    public void showHistory(final boolean byToolBar, final JTextComponent field) {
        try {
            final Method meth;
            if (this.searchComp != null && (meth = this.searchComp.getClass().getMethod("showHistory", Boolean.class, JTextComponent.class)) != null) {
                meth.invoke(this.searchComp, byToolBar, field);
            }
        }
        catch (NoSuchMethodException e) {}
        catch (InvocationTargetException e2) {}
        catch (IllegalAccessException ex) {}
    }

    @Override
    public void requestFocus() {
        this.invoke(this.searchComp, "requestFocus");
    }

    @Override
    public void close() {
        this.invoke(this.searchComp, "close");
    }
}
