package com.leaf.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AnvilGUICompleteEvent extends Event {

	private Player p;	
    private final String input;

    public AnvilGUICompleteEvent(Player p, String text) {
        this.p = p;
        this.input = text;      
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public String getString() {
        return this.input;
    }
    public Player getPlayer() {
        return this.p;
    }
}