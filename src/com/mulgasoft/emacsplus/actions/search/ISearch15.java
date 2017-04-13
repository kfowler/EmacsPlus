//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.find.FindModel;
import com.intellij.openapi.editor.Editor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import org.jetbrains.annotations.NonNls;

public class ISearch15 implements ISearchDelegate
{
    @NonNls
    private static String GET_SEARCH;
    @NonNls
    private static String GET_REPLACE;
    @NonNls
    private static String GET_FIND;
    private Editor editor;
    private JComponent searchComp;
    private Object session;

    public ISearch15(final Editor editor, final Object session, final JComponent component) {
        this.editor = null;
        this.searchComp = null;
        this.session = null;
        this.editor = editor;
        this.session = session;
        this.searchComp = component;
    }

    protected Object invoke(final Object element, final String method) {
        Object result = null;
        try {
            final Method meth;
            if (element != null && (meth = element.getClass().getMethod(method, (Class<?>[])new Class[0])) != null) {
                result = meth.invoke(element);
            }
        }
        catch (NoSuchMethodException e) {}
        catch (InvocationTargetException e2) {}
        catch (IllegalAccessException ex) {}
        return result;
    }

    private Object getAF(final Object o, final String fieldName) {
        Class<?> c = o.getClass();
        try {
            do {
                final Field[] fields = c.getDeclaredFields();
                for (int i = 0; i < fields.length; ++i) {
                    if (fieldName.equals(fields[i].getName())) {
                        final boolean access = fields[i].isAccessible();
                        Object obj;
                        try {
                            if (!access) {
                                fields[i].setAccessible(true);
                            }
                            obj = fields[i].get(o);
                        }
                        finally {
                            if (!access) {
                                fields[i].setAccessible(false);
                            }
                        }
                        return obj;
                    }
                }
                c = c.getSuperclass();
            } while (c != null);
        }
        catch (SecurityException e) {}
        catch (IllegalArgumentException e2) {}
        catch (IllegalAccessException ex) {}
        return null;
    }

    @Override
    public JComponent getComponent() {
        return this.searchComp;
    }

    @Override
    public JTextComponent getSearchField() {
        return (JTextComponent)this.invoke(this.searchComp, ISearch15.GET_SEARCH);
    }

    private JTextComponent getSearchWrapper() {
        JTextComponent result = null;
        final Object wrap = this.getAF(this.getComponent(), "mySearchTextComponent");
        if (wrap instanceof JTextComponent) {
            result = (JTextComponent)wrap;
        }
        return result;
    }

    @Override
    public JTextComponent getReplaceField() {
        return (JTextComponent)this.invoke(this.searchComp, ISearch15.GET_REPLACE);
    }

    @Override
    public FindModel getFindModel() {
        return (FindModel)this.invoke(this.session, ISearch15.GET_FIND);
    }

    @Override
    public boolean hasMatches() {
        return (boolean)this.invoke(this.session, "hasMatches");
    }

    @Override
    public void searchForward() {
        this.invoke(this.session, "searchForward");
    }

    @Override
    public void searchBackward() {
        this.invoke(this.session, "searchBackward");
    }

    @Override
    public void replaceCurrent() {
        this.invoke(this.session, "replaceCurrent");
    }

    @Override
    public void showHistory(final boolean byToolBar, final JTextComponent field) {
    }

    @Override
    public void requestFocus() {
        this.invoke(this.searchComp, "requstFocus");
    }

    @Override
    public void close() {
        this.invoke(this.session, "close");
    }

    static {
        ISearch15.GET_SEARCH = "getSearchTextComponent";
        ISearch15.GET_REPLACE = "getReplaceTextComponent";
        ISearch15.GET_FIND = "getFindModel";
    }
}
