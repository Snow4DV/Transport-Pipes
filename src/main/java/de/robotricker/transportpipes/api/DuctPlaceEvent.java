package de.robotricker.transportpipes.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DuctPlaceEvent extends Event {

    final Player player;
    private static final HandlerList handlers = new HandlerList();

    /**
     * Called when a duct is placed
     * @param player The player that placed the duct
     */
    public DuctPlaceEvent(Player player) {
        super();
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the player that placed the duct
     * @return The player that placed the duct
     */
    public Player getPlayer() {
        return player;
    }
}