package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;


public class JustOneSpaceTest extends JavaCodeInsightFixtureTestCase {

  public void testJustOneSpace() {
    myFixture.configureByText(PlainTextFileType.INSTANCE, "Just One \t <caret> \t space.");
    myFixture.testAction(new JustOneSpace());
    myFixture.checkResult("Just One space.");
  }
}
