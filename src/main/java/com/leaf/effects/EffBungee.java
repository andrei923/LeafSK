package com.leaf.effects;

import com.leaf.Leaf;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import ch.njol.skript.Skript;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;


public class EffBungee extends Effect {

	static {
		Skript.registerEffect(EffBungee.class, "[leaf] send %player% to bungeecord server %string%");
	}
	
	private Expression<Player> player;
	private Expression<String> srv;

	
@Override
protected void execute(Event event)
{
	for(Player p : player.getAll(event)){
		  if ((p == null) || (srv.getSingle(event) == null)) {
		    return;
		  }
		  connect(p, srv.getSingle(event));
	}
}

public static void connect(Player p, String srv)
{
  ByteArrayOutputStream b = new ByteArrayOutputStream();
  DataOutputStream out = new DataOutputStream(b);
  try
  {
    out.writeUTF("Connect");
    out.writeUTF(srv);
  }
  catch (IOException localIOException) {}
  p.sendPluginMessage(Leaf.getInstance(), "BungeeCord", b.toByteArray());
}

@Override
public String toString(Event event, boolean bool)
{
  return getClass().getName();
}

@Override
@SuppressWarnings("unchecked")
public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult)
{
  player = (Expression<Player>) expressions[0];
  srv = (Expression<String>) expressions[1];
  return true;
}
}