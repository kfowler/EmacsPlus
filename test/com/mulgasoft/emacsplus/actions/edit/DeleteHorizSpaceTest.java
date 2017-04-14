package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;

public class DeleteHorizSpaceTest extends JavaCodeInsightFixtureTestCase {

  public void testDeleteHorizSpace() {
    myFixture.configureByText(PlainTextFileType.INSTANCE, "Keep \t <caret> \tit.\n");
    myFixture.testAction(new DeleteHorizSpace());
    myFixture.checkResult("Keepit.\n");
  }
}
