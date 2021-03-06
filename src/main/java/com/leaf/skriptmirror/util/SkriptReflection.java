package com.leaf.skriptmirror.util;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.Option;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.DefaultExpression;
import ch.njol.skript.lang.SyntaxElementInfo;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.log.*;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.variables.Variables;

import org.bukkit.event.Event;

import com.leaf.skriptmirror.skript.custom.event.ExprReplacedEventValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkriptReflection {

  private static Field PATTERNS;
  private static Field PARAMETERS;
  private static Field HANDLERS;
  private static Field CURRENT_OPTIONS;
  private static Field LOCAL_VARIABLES;
  private static Field NODES;
  private static Field VARIABLES_MAP_HASHMAP;
  private static Field VARIABLES_MAP_TREEMAP;
  private static Constructor VARIABLES_MAP;
  private static Field DEFAULT_EXPRESSION;
  private static Field PARSED_VALUE;

  static {
    Field _FIELD;
    Constructor _CONSTRUCTOR;

    try {
      _FIELD = SyntaxElementInfo.class.getDeclaredField("patterns");
      _FIELD.setAccessible(true);
      PATTERNS = _FIELD;
    } catch (NoSuchFieldException e) {
      Skript.warning("Skript's pattern info field could not be resolved. " +
          "Custom syntax will not work.");
    }

    try {
      //noinspection JavaReflectionMemberAccess
      _FIELD = Function.class.getDeclaredField("parameters");
      _FIELD.setAccessible(true);
      PARAMETERS = _FIELD;
    } catch (NoSuchFieldException ignored) { }

    try {
      _FIELD = SkriptLogger.class.getDeclaredField("handlers");
      _FIELD.setAccessible(true);
      HANDLERS = _FIELD;
    } catch (NoSuchFieldException e) {
      Skript.warning("Skript's handlers field could not be resolved. Some Skript warnings may not be available.");
    }

    try {
      _FIELD = ScriptLoader.class.getDeclaredField("currentOptions");
      _FIELD.setAccessible(true);
      CURRENT_OPTIONS = _FIELD;
    } catch (NoSuchFieldException e) {
      Skript.warning("Skript's options field could not be resolved.");
    }

    try {
      _FIELD = Variables.class.getDeclaredField("localVariables");
      _FIELD.setAccessible(true);
      LOCAL_VARIABLES = _FIELD;
    } catch (NoSuchFieldException e) {
      Skript.warning("Skript's local variables field could not be resolved.");
    }

    try {
      _FIELD = SectionNode.class.getDeclaredField("nodes");
      _FIELD.setAccessible(true);
      NODES = _FIELD;
    } catch (NoSuchFieldException e) {
      Skript.warning("Skript's nodes field could not be resolved, therefore sections won't work.");
    }

    try {
      Class<?> variablesMap = Class.forName("ch.njol.skript.variables.VariablesMap");

      try {
        _FIELD = variablesMap.getDeclaredField("hashMap");
        _FIELD.setAccessible(true);
        VARIABLES_MAP_HASHMAP = _FIELD;
      } catch (NoSuchFieldException e) {
        Skript.warning("Skript's hash map field could not be resolved.");
      }

      try {
        _FIELD = variablesMap.getDeclaredField("treeMap");
        _FIELD.setAccessible(true);
        VARIABLES_MAP_TREEMAP = _FIELD;
      } catch (NoSuchFieldException e) {
        Skript.warning("Skript's tree map field could not be resolved.");
      }

      try {
        _CONSTRUCTOR = variablesMap.getDeclaredConstructor();
        _CONSTRUCTOR.setAccessible(true);
        VARIABLES_MAP = _CONSTRUCTOR;
      } catch (NoSuchMethodException e) {
        Skript.warning("Skript's variables map constructors could not be resolved.");
      }
    } catch (ClassNotFoundException e) {
      Skript.warning("Skript's variables map class could not be resolved.");
    }

    try {
      _FIELD = ClassInfo.class.getDeclaredField("defaultExpression");
      _FIELD.setAccessible(true);
      DEFAULT_EXPRESSION = _FIELD;
    } catch (NoSuchFieldException e) {
      Skript.warning("Skript's default expression field could not be resolved, " +
        "therefore event-values won't work in custom events");
    }

    try {
      _FIELD = Option.class.getDeclaredField("parsedValue");
      _FIELD.setAccessible(true);
      PARSED_VALUE = _FIELD;
    } catch (NoSuchFieldException e) {
      Skript.warning("Skript's parsed value field could not be resolved, " +
        "therefore and/or warnings won't be suppressed");
    }
  }

  public static void setPatterns(SyntaxElementInfo<?> info, String[] patterns) {
    try {
      PATTERNS.set(info, patterns);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public static Parameter<?>[] getParameters(Function<?> function) {
    if (PARAMETERS == null) {
      return function.getParameters();
    } else {
      try {
        return ((Parameter<?>[]) PARAMETERS.get(function));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      throw new IllegalStateException();
    }
  }

  public static void printLog(RetainingLogHandler logger) {
    logger.stop();
    HandlerList handler;
    try {
      handler = (HandlerList) HANDLERS.get(logger);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return;
    }

    Iterator<LogHandler> handlers = handler.iterator();
    LogHandler nextHandler;
    List<LogHandler> parseLogs = new ArrayList<>();

    while (handlers.hasNext()) {
      nextHandler = handlers.next();

      if (!(nextHandler instanceof ParseLogHandler)) {
        break;
      }
      parseLogs.add(nextHandler);
    }

    parseLogs.forEach(LogHandler::stop);
    SkriptLogger.logAll(logger.getLog());
  }

  @SuppressWarnings("unchecked")
  public static Map<String, String> getCurrentOptions() {
    try {
      return (Map<String, String>) CURRENT_OPTIONS.get(null);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    throw new IllegalStateException();
  }

  /**
   * Sets the local variables of an {@link Event} to the given local variables.
   */
  @SuppressWarnings("unchecked")
  public static void putLocals(Object originalVariablesMap, Event to) {
    if (originalVariablesMap == null)
      removeLocals(to);

    try {
      Map<Event, Object> localVariables = (Map<Event, Object>) LOCAL_VARIABLES.get(null);

      localVariables.put(to, originalVariablesMap);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes and returns the local variables from the given {@link Event}.
   */
  @SuppressWarnings("unchecked")
  public static Object removeLocals(Event event) {
    try {
      Map<Event, Object> localVariables = (Map<Event, Object>) LOCAL_VARIABLES.get(null);
      return localVariables.remove(event);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Retrieves the local variables from an {@link Event}.
   * @param event The {@link Event} to get the local variables from.
   * @return The local variables of the given {@link Event}.
   */
  @SuppressWarnings("unchecked")
  public static Object getLocals(Event event) {
    try {
      Map<Event, Object> localVariables = (Map<Event, Object>) LOCAL_VARIABLES.get(null);
      return localVariables.get(event);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Copies the VariablesMap contained in the given {@link Object}.
   * @param locals The local variables to copy.
   * @return The copied local variables.
   */
  @SuppressWarnings("unchecked")
  public static Object copyLocals(Object locals) {
    if (locals == null)
      return null;

    try {
      Object copiedLocals = VARIABLES_MAP.newInstance();

      ((Map<String, Object>) VARIABLES_MAP_HASHMAP.get(copiedLocals))
        .putAll((Map<String, Object>) VARIABLES_MAP_HASHMAP.get(locals));
      ((Map<String, Object>) VARIABLES_MAP_TREEMAP.get(copiedLocals))
        .putAll((Map<String, Object>) VARIABLES_MAP_TREEMAP.get(locals));
      return copiedLocals;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Retrieves the {@link Node}s of a {@link SectionNode}.
   * @param sectionNode The {@link SectionNode} to get the nodes from.
   * @return The {@link Node}s of the given {@link SectionNode}
   */
  @SuppressWarnings("unchecked")
  public static ArrayList<Node> getNodes(SectionNode sectionNode) {
    try {
      return (ArrayList<Node>) NODES.get(sectionNode);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

  /**
   * Replaces the event-values of a list of {@link ClassInfo}s with
   * {@link ExprReplacedEventValue}'s to make them work in custom events.
   *
   * @param classInfoList A list of {@link ClassInfo}s to replace
   */
  public static void replaceEventValues(List<ClassInfo<?>> classInfoList) {
    if (DEFAULT_EXPRESSION == null)
      return;

    try {
      List<ClassInfo<?>> replaceExtraList = new ArrayList<>();
      for (ClassInfo<?> classInfo : classInfoList) {
        DefaultExpression<?> defaultExpression = classInfo.getDefaultExpression();
        if (defaultExpression instanceof EventValueExpression && !(defaultExpression instanceof ExprReplacedEventValue)) {
          DEFAULT_EXPRESSION.set(classInfo,
            new ExprReplacedEventValue<>((EventValueExpression<?>) defaultExpression));

          replaceExtraList.add(classInfo);
        }
      }

      replaceExtraList.forEach(SkriptReflection::replaceExtra);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  /**
   * Replaces {@link ClassInfo}s related to the given {@link ClassInfo}.
   */
  public static void replaceExtra(ClassInfo<?> classInfo) {
    List<ClassInfo<?>> classInfoList = Classes.getClassInfos().stream()
      .filter(loopedClassInfo -> !(loopedClassInfo.getDefaultExpression() instanceof ExprReplacedEventValue))
      .filter(loopedClassInfo -> classInfo.getC().isAssignableFrom(loopedClassInfo.getC())
        || loopedClassInfo.getC().isAssignableFrom(classInfo.getC()))
      .collect(Collectors.toList());
    replaceEventValues(classInfoList);
  }

  /**
   * Disable Skript's missing and / or warnings.
   */
  public static void disableAndOrWarnings() {
    Option<Boolean> option = SkriptConfig.disableMissingAndOrWarnings;
    if (!option.value()) {
      try {
        PARSED_VALUE.set(option, true);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

}
