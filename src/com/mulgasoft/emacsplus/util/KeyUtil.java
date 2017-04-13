//
// Decompiled by Procyon v0.5.30
//

package com.mulgasoft.emacsplus.util;

import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;

public class KeyUtil
{
    private Keymap getKeymap() {
        return KeymapManager.getInstance().getActiveKeymap();
    }
}
