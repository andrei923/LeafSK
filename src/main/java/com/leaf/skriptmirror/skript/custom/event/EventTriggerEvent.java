package com.leaf.skriptmirror.skript.custom.event;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.leaf.skriptmirror.skript.custom.CustomSyntaxEvent;

public class EventTriggerEvent extends CustomSyntaxEvent {

  private final static HandlerList handlers = new HandlerList();
  private final String which;
  private boolean markedContinue;

  public EventTriggerEvent(Event event, Expression<?>[] expressions, int matchedPattern,
                           SkriptParser.ParseResult parseResult, String which) {
    super(event, expressions, matchedPattern, parseResult);
    this.which = which;
  }

  public String getWhich() {
    return which;
  }

  public boolean isMarkedContinue() {
    return markedContinue;
  }

  public void markContinue() {
    markedContinue = true;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

}
