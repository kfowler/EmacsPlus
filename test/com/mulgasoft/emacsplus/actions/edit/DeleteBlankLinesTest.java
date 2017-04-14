package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;


public class DeleteBlankLinesTest extends JavaCodeInsightFixtureTestCase {

  public void testOnBlankLine() {
    // on blank line, delete all surrounding blank lines, leaving just one.
    myFixture.configureByText(PlainTextFileType.INSTANCE, "Keep\n\n\n\n<caret>\n\nit.\n");
    myFixture.testAction(new DeleteBlankLines());
    myFixture.checkResult("Keep\n\nit.\n");
    // on isolated blank line, delete that one.
    myFixture.configureByText(PlainTextFileType.INSTANCE, "Keep\n<caret>\nit.\n");
    myFixture.testAction(new DeleteBlankLines());
    myFixture.checkResult("Keep\nit.\n");
    // on nonblank line, delete any immediately following blank lines
    myFixture.configureByText(PlainTextFileType.INSTANCE, "Delete<caret>\n\n\n\nthem.\n");
    myFixture.testAction(new DeleteBlankLines());
    myFixture.checkResult("Delete\nthem.\n");

  }
}
