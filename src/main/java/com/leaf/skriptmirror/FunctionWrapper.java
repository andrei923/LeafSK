package com.leaf.skriptmirror;

import javax.annotation.Nullable;

import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;

public class FunctionWrapper {

  private final String name;
  private final Object[] arguments;

  public FunctionWrapper(String name, Object[] arguments) {
    this.name = name;
    this.arguments = arguments;
  }

  public String getName() {
    return name;
  }

  public Object[] getArguments() {
    return arguments;
  }

  @Nullable
  public Function<?> getFunction() {
    return Functions.getFunction(name);
  }

}

