package com.mulgasoft.emacsplus;

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.junit.Test;


public class EmacsPlusTest extends JavaCodeInsightFixtureTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Test
  public void testPluginLoads() {
    assertTrue(true);
    assertNotNull(myFixture);
    assertNotNull(EmacsPlus.getVersion());
  }
}
