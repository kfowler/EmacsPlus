package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;

public class TransposeCharsTest extends JavaCodeInsightFixtureTestCase {

  public void testTransposeChars() {
    myFixture.configureByText(PlainTextFileType.INSTANCE, "ac<caret>b.");
    final int initialOffset = myFixture.getCaretOffset();
    myFixture.testAction(new TransposeChars());
    myFixture.checkResult("abc.");
    final int finalOffset = myFixture.getCaretOffset();
    assertEquals(1, finalOffset - initialOffset);
  }
}
