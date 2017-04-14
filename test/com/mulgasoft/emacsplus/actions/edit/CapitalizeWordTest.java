package com.mulgasoft.emacsplus.actions.edit;

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;


public class CapitalizeWordTest extends JavaCodeInsightFixtureTestCase {

  public void testCapitalizeWord() {
    // examples --> Example
    myFixture.configureByText(PlainTextFileType.INSTANCE, "example");
    myFixture.performEditorAction(IdeActions.ACTION_SELECT_ALL);
    myFixture.testAction(new CapitalizeWord());
    myFixture.checkResult("Example");
    // Example --> Example
    myFixture.configureByText(PlainTextFileType.INSTANCE, "Example");
    myFixture.performEditorAction(IdeActions.ACTION_SELECT_ALL);
    myFixture.testAction(new CapitalizeWord());
    myFixture.checkResult("Example");
    // eXample --> Example
    myFixture.configureByText(PlainTextFileType.INSTANCE, "eXample");
    myFixture.performEditorAction(IdeActions.ACTION_SELECT_ALL);
    myFixture.testAction(new CapitalizeWord());
    myFixture.checkResult("Example");
    // "whole thing" --> "Whole thing"
    myFixture.configureByText(PlainTextFileType.INSTANCE, "whole thing");
    myFixture.performEditorAction(IdeActions.ACTION_SELECT_ALL);
    myFixture.testAction(new CapitalizeWord());
    myFixture.checkResult("Whole thing");
  }


}
