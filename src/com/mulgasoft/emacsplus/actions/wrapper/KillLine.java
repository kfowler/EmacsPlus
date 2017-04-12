// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.wrapper;

public class KillLine extends KillWrapper
{
    public KillLine() {
        super(EmacsPlusWrapper.getWrappedHandler("EditorCutLineEnd"));
    }
    
    @Override
    protected String getName() {
        return "kill-line";
    }
}
