package com.mulgasoft.emacsplus;

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;


public class EmacsPlusTest extends JavaCodeInsightFixtureTestCase {

  public void testPluginLoads() {
    assertTrue(true);
    assertNotNull(myFixture);
    assertNotNull(EmacsPlus.getVersion());
  }
}
