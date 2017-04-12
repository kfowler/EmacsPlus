// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.util;

import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.keymap.Keymap;

public class KeyUtil
{
    private Keymap getKeymap() {
        return KeymapManager.getInstance().getActiveKeymap();
    }
}
