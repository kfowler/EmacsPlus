//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.actions.search;

import com.intellij.openapi.editor.Editor;
import com.intellij.util.ObjectUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;
import org.jetbrains.annotations.NonNls;

public class ISearchFactory
{
    @NonNls
    private static final String OBJ_14 = "com.intellij.find.EditorSearchComponent";
    @NonNls
    private static final String OBJ_15 = "com.intellij.find.EditorSearchSession";
    @NonNls
    private static final String GET_15 = "get";

    public static ISearchDelegate getISearchObject(final Editor editor) {
        ISearchDelegate result = null;
        Class<?> clazz = null;
        final JComponent hc;
        if (editor != null && (hc = editor.getHeaderComponent()) != null) {
            try {
                clazz = Class.forName("com.intellij.find.EditorSearchComponent");
                if (ObjectUtils.tryCast(hc, (Class)clazz) != null) {
                    result = new ISearch14(editor, hc);
                }
            }
            catch (ClassNotFoundException ex) {}
            if (clazz == null) {
                try {
                    clazz = Class.forName("com.intellij.find.EditorSearchSession");
                    final Method meth = clazz.getMethod("get", Editor.class);
                    final Object session = meth.invoke(clazz, editor);
                    if (session != null) {
                        result = new ISearch15(editor, session, hc);
                    }
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e2) {
                    e2.printStackTrace();
                }
                catch (NoSuchMethodException e3) {
                    e3.printStackTrace();
                }
                catch (IllegalAccessException e4) {
                    e4.printStackTrace();
                }
            }
        }
        return result;
    }
}
