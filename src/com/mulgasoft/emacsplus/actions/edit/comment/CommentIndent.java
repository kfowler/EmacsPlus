// 
// Decompiled by Procyon v0.5.30
// 

package com.mulgasoft.emacsplus.actions.edit.comment;

import com.mulgasoft.emacsplus.handlers.CommentHandler;

public class CommentIndent extends CommentAction
{
    @Override
    protected CommentHandler getMyHandler() {
        return new CommentHandler();
    }
}
