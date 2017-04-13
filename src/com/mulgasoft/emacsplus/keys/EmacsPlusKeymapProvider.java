package com.mulgasoft.emacsplus.keys;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.keymap.impl.BundledKeymapProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class EmacsPlusKeymapProvider implements BundledKeymapProvider {

  private static final Logger LOG = Logger.getInstance(EmacsPlusKeymapProvider.class);

  private final ImmutableList<String> keymapFileNames = ImmutableList.of(
      "Keymap_EmacsPlus.xml",
      "Keymap_EmacsPlusMac.xml",
      "de/Keymap_EmacsPlus.xml",
      "de/Keymap_EmacsPlusMac.xml",
      "es/Keymap_EmacsPlus.xml",
      "es/Keymap_EmacsPlusMac.xml",
      "fr/Keymap_EmacsPlus.xml",
      "fr/Keymap_EmacsPlusMac.xml"
  );

  @NotNull
  @Override
  public List<String> getKeymapFileNames() {
    return keymapFileNames;
  }

  @Override
  public <R> R load(@NotNull String key, @NotNull Function<InputStream, R> consumer) throws IOException {
    /*
     * Hack to work around: https://github.com/JetBrains/intellij-community/pull/493
     */
    try (InputStream stream = this.getClass().getResourceAsStream(key)) {
      return consumer.apply(stream);
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
  }
}
