package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;


public class DowncaseWordTest extends JavaCodeInsightFixtureTestCase {

  public void testDowncaseWord() {
    // Convert to lower case from point to end of word, moving over.
    myFixture.configureByText(PlainTextFileType.INSTANCE, "<caret>DOWN. DOWN. DOWN.");
    myFixture.testAction(new DowncaseWord());
    myFixture.checkResult("down. DOWN. DOWN.");
    myFixture.testAction(new DowncaseWord());
    myFixture.checkResult("down. down. DOWN.");
    myFixture.configureByText(PlainTextFileType.INSTANCE, "DOWN<caret>DOWN. DOWN. DOWN.");
    myFixture.testAction(new DowncaseWord());
    myFixture.checkResult("DOWNdown. DOWN. DOWN.");
  }
}
