package com.leaf.skriptmirror.skript.custom;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;

import org.bukkit.event.Event;

import com.leaf.skriptmirror.WrappedEvent;

import java.util.Arrays;

public abstract class CustomSyntaxEvent extends WrappedEvent {
  private final Expression<?>[] expressions;
  private final int matchedPattern;
  private final SkriptParser.ParseResult parseResult;

  protected CustomSyntaxEvent(Event event, Expression<?>[] expressions, int matchedPattern,
                              SkriptParser.ParseResult parseResult) {
    super(event);
    this.expressions = Arrays.stream(expressions)
        .map(expr -> CustomSyntaxExpression.wrap(expr, event))
        .toArray(Expression[]::new);
    this.matchedPattern = matchedPattern;
    this.parseResult = parseResult;
  }

  public Expression<?>[] getExpressions() {
    return expressions;
  }

  public int getMatchedPattern() {
    return matchedPattern;
  }

  public SkriptParser.ParseResult getParseResult() {
    return parseResult;
  }
}
