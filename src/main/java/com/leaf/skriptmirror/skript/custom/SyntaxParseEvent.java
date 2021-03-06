package com.leaf.skriptmirror.skript.custom;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SimpleLiteral;

import org.bukkit.event.HandlerList;

import com.leaf.skriptmirror.util.SkriptUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SyntaxParseEvent extends CustomSyntaxEvent {
  private final static HandlerList handlers = new HandlerList();
  private final Class<?>[] eventClasses;
  private boolean markedContinue;

  public SyntaxParseEvent(Expression<?>[] expressions, int matchedPattern, SkriptParser.ParseResult parseResult,
                          Class<?>[] eventClasses) {
    super(null, wrapRawExpressions(expressions), matchedPattern, parseResult);
    this.eventClasses = eventClasses;
  }

  private static Expression<?>[] wrapRawExpressions(Expression<?>[] expressions) {
    return Arrays.stream(expressions)
        .map(expr -> expr == null ? null : new SimpleLiteral<>(expr, false))
        .toArray(Expression[]::new);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public Class<?>[] getEventClasses() {
    return eventClasses;
  }

  public boolean isMarkedContinue() {
    return markedContinue;
  }

  public void markContinue() {
    markedContinue = true;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  @SuppressWarnings("unchecked")
  public static <T extends CustomSyntaxSection.SyntaxData> void register(CustomSyntaxSection<T> section,
                                                                         SectionNode parseNode,
                                                                         List<T> whichInfo, Map<T, Trigger> parserHandlers) {
    ScriptLoader.setCurrentEvent("custom syntax parser", SyntaxParseEvent.class);
    List<TriggerItem> items = SkriptUtil.getItemsFromNode(parseNode);

    whichInfo.forEach(which ->
        parserHandlers.put(which,
            new Trigger(SkriptUtil.getCurrentScript(), "parse " + which.getPattern(), section, items)));
  }
}
