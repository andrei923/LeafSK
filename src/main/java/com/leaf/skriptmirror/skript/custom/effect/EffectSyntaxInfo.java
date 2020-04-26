package com.leaf.skriptmirror.skript.custom.effect;

import com.leaf.skriptmirror.skript.custom.CustomSyntaxSection;
import com.leaf.skriptmirror.util.SkriptMirrorUtil;

import java.io.File;
import java.util.Objects;

class EffectSyntaxInfo extends CustomSyntaxSection.SyntaxData {
  private EffectSyntaxInfo(File script, String pattern, int matchedPattern) {
    super(script, pattern, matchedPattern);
  }

  public static EffectSyntaxInfo create(File script, String pattern, int matchedPattern) {
    return new EffectSyntaxInfo(script, SkriptMirrorUtil.preprocessPattern(pattern), matchedPattern);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EffectSyntaxInfo that = (EffectSyntaxInfo) o;
    return Objects.equals(getScript(), that.getScript()) &&
        Objects.equals(getPattern(), that.getPattern());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getScript(), getPattern());
  }
}
