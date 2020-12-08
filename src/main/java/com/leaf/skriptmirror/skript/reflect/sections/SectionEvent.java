package com.leaf.skriptmirror.skript.reflect.sections;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.leaf.skriptmirror.WrappedEvent;

public class SectionEvent extends WrappedEvent {

  private final static HandlerList handlers = new HandlerList();
  private final Section section;

  public SectionEvent(Event event, Section section) {
    super(event);
    this.section = section;
  }

  public void setOutput(Object[] output) {
    section.setOutput(output);
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

}
