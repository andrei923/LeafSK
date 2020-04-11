package com.leaf.util;

import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import ch.njol.skript.Skript;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;

public class SkriptUtil {

  private static final Field STRING;
  private static final Field SIMPLE;
  private static final Field DELAYED;
  private static final Field EXPR;
  private static final Field VARIABLE_NAME;

  static {
    Field _FIELD = null;
    try {
      _FIELD = VariableString.class.getDeclaredField("string");
      _FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      Skript.error("Skript's 'string' field could not be resolved.");
      e.printStackTrace();
    }
    STRING = _FIELD;

    try {
      _FIELD = VariableString.class.getDeclaredField("simple");
      _FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      Skript.error("Skript's 'simple' field could not be resolved.");
      e.printStackTrace();
    }
    SIMPLE = _FIELD;

    try {
      _FIELD = Delay.class.getDeclaredField("delayed");
      _FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
      Skript.warning("Skript's 'delayed' method could not be resolved. Some Skript warnings may " +
          "not be available.");
    }
    DELAYED = _FIELD;

    try {
      Optional<Class<?>> expressionInfo = Arrays.stream(VariableString.class.getDeclaredClasses())
          .filter(cls -> cls.getSimpleName().equals("ExpressionInfo"))
          .findFirst();
      if (expressionInfo.isPresent()) {
        Class<?> expressionInfoClass = expressionInfo.get();
        _FIELD = expressionInfoClass.getDeclaredField("expr");
        _FIELD.setAccessible(true);
      } else {
        Skript.error("Skript's 'ExpressionInfo' class could not be resolved.");
      }
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
      Skript.error("Skript's 'expr' field could not be resolved.");
    }
    EXPR = _FIELD;

    try {
      _FIELD = Variable.class.getDeclaredField("name");
      _FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
      Skript.error("Skript's 'variable name' method could not be resolved.");
    }
    VARIABLE_NAME = _FIELD;
  }

  @SuppressWarnings("unchecked")
  public static void delay(Event e) {
    if (DELAYED != null) {
      try {
        ((Set<Event>) DELAYED.get(null)).add(e);
      } catch (IllegalAccessException ignored) {
      }
    }
  }

  public static String getSimpleString(VariableString vs) {
    try {
      return (String) SIMPLE.get(vs);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static Object[] getTemplateString(VariableString vs) {
    try {
      return (Object[]) STRING.get(vs);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static Expression<?> getExpressionFromInfo(Object o) {
    try {
      return (Expression<?>) EXPR.get(o);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static VariableString getVariableName(Variable<?> var) {
    try {
      return (VariableString) VARIABLE_NAME.get(var);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }
}
