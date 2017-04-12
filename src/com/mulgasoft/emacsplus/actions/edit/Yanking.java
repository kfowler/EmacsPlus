// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.mulgasoft.emacsplus.actions.EmacsPlusAction;

public abstract class Yanking extends EmacsPlusAction
{
    private static int ourLength;
    private static int ourIndex;
    private static int ourOffset;
    
    public Yanking(final EditorActionHandler defaultHandler) {
        super(defaultHandler);
    }
    
    private static void setLength(final int length) {
        Yanking.ourLength = length;
    }
    
    protected static int getLength() {
        return Yanking.ourLength;
    }
    
    protected static void setIndex(final int index) {
        Yanking.ourIndex = index;
    }
    
    protected static int getIndex() {
        return Yanking.ourIndex;
    }
    
    private static void setOffset(final int offset) {
        Yanking.ourOffset = offset;
    }
    
    protected static int getOffset() {
        return Yanking.ourOffset;
    }
    
    protected void reset() {
        setLength(0);
        setOffset(0);
        setIndex(1);
    }
    
    protected static void yanked(final TextRange location) {
        if (location != null) {
            setLength(location.getEndOffset() - location.getStartOffset());
            setOffset(location.getEndOffset());
        }
    }
    
    protected void popped(final TextRange location) {
        yanked(location);
        ++Yanking.ourIndex;
    }
    
    static {
        Yanking.ourLength = 0;
        Yanking.ourIndex = 0;
        Yanking.ourOffset = 0;
    }
}
