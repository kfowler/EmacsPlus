//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.keys;

import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import com.intellij.openapi.util.JDOMUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import org.jdom.Document;
import org.jetbrains.annotations.NonNls;

public abstract class Keymaps
{
    @NonNls
    private static final String PREFIX = "lang/";
    @NonNls
    private static final String SUFFIX = ".xml";
    @NonNls
    private static final String DIR_SEPR = "/";
    @NonNls
    private static final String LOCALE_SEPR = "_";
    @NonNls
    private static final String STD_KEYMAP = "Keymap_EmacsPlus";
    @NonNls
    private static final String MAC_KEYMAP = "Keymap_EmacsPlusMac";
    @NonNls
    private static final String USER_KEYMAP;
    @NonNls
    private static final String EMACS = "Emacs+";
    @NonNls
    private static final String DE = "de";
    @NonNls
    private static final String ES = "es";
    @NonNls
    private static final String FR = "fr";
    private static List<String> mapNames;
    private static boolean isAlt;
    private static String isLocale;

    public static void enableKeymaps() {
        enableKeymap("Keymap_EmacsPlus.xml");
        enableKeymap("Keymap_EmacsPlusMac.xml");
        enableLocaleKeymap("Keymap_EmacsPlus");
        enableLocaleKeymap("Keymap_EmacsPlusMac");
        enableUserKeymap(Keymaps.USER_KEYMAP);
        setupKeymapListener();
    }

    private static void enableUserKeymap(final String name) {
        try {
            final File file = new File(name);
            if (file.exists() && loadKeymap(new FileInputStream(file))) {
                Keymaps.mapNames.remove(0);
                Keymaps.mapNames.add(0, "Mac Emacs+ de");
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void enableLocaleKeymap(final String name) {
        final Locale l = Locale.getDefault();
        if (!enableKeymap("lang/" + l.getLanguage() + "/" + name + "_" + l.getCountry() + ".xml")) {
            enableKeymap("lang/" + l.getLanguage() + "/" + name + ".xml");
        }
    }

    private static boolean enableKeymap(final String name) {
        return loadKeymap(Keymaps.class.getResourceAsStream(name));
    }

    private static boolean loadKeymap(final InputStream stream) {
        boolean result = false;
        try {
            if (stream != null) {
                final Document document = JDOMUtil.loadDocument(stream);
                if (document != null) {
                    /* This is broken
                    final KeymapManagerEx mgr = KeymapManagerEx.getInstanceEx();
                    final KeymapImpl emKeymap = new KeymapImpl();
                    emKeymap.readExternal(document.getRootElement(), mgr.getAllKeymaps());
                    mgr.getSchemesManager().addNewScheme((Scheme)emKeymap, true);
                    Keymaps.mapNames.add(0, emKeymap.getName());
                    */
                    result = true;
                }
                else {
                    System.out.println("Null Keymap Stream: " + stream);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isAlt() {
        return Keymaps.isAlt;
    }

    public static String isLocale() {
        return Keymaps.isLocale;
    }

    private static void setupKeymapListener() {
        final KeymapManagerEx mgr = KeymapManagerEx.getInstanceEx();
        mgr.addKeymapManagerListener(Keymaps::activate);
        activate(mgr.getActiveKeymap());
    }

    private static void activate(final Keymap keymap) {
        if (keymap != null) {
            Keymap map = keymap;
            do {
                Keymaps.isAlt = true;
                Keymaps.isLocale = null;
                final String name = map.getName();
                if (Keymaps.mapNames.contains(name)) {
                    if (!name.startsWith("Emacs+")) {
                        Keymaps.isAlt = false;
                    }
                    int ind = name.lastIndexOf("Emacs+") + "Emacs+".length();
                    if (++ind < name.length()) {
                        Keymaps.isLocale = name.substring(ind, name.length());
                        break;
                    }
                    break;
                }
            } while ((map = map.getParent()) != null);
        }
    }

    public static KeyStroke getIntlKeyStroke(final int key) {
        KeyStroke ks = null;
        switch (key) {
            case 153: {
                if (Keymaps.isLocale != null) {
                    ks = KeyStroke.getKeyStroke(153, getMeta());
                    break;
                }
                ks = KeyStroke.getKeyStroke(44, getMeta() | 0x40);
                break;
            }
            case 160: {
                if (Keymaps.isLocale != null) {
                    ks = KeyStroke.getKeyStroke(153, getMeta() | 0x40);
                    break;
                }
                ks = KeyStroke.getKeyStroke(46, getMeta() | 0x40);
                break;
            }
            case 47: {
                if (Keymaps.isLocale == null) {
                    ks = KeyStroke.getKeyStroke(47, 2);
                    break;
                }
                if ("fr".equals(Keymaps.isLocale)) {
                    ks = KeyStroke.getKeyStroke(513, 192);
                    break;
                }
                ks = KeyStroke.getKeyStroke(55, 192);
                break;
            }
        }
        return ks;
    }

    public static int getMeta() {
        return isAlt() ? 512 : 256;
    }

    static {
        USER_KEYMAP = System.getProperty("user.home") + "/.emacs+keymap" + ".xml";
        Keymaps.mapNames = new ArrayList<>();
        Keymaps.isAlt = true;
        Keymaps.isLocale = null;
    }
}
