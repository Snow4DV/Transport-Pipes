package de.robotricker.transportpipes.duct.pipe.extractionpipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.robotricker.transportpipes.config.LangConf;

public enum ExtractMode {

    DIRECT(LangConf.Key.EXTRACT_MODE_DIRECT.get(), Material.REPEATER),
    ROUND(LangConf.Key.EXTRACT_MODE_ROUND.get(), Material.COMPARATOR);

    private String displayName;
    private ItemStack displayItem;

    ExtractMode(String displayName, Material type) {
        this.displayName = displayName;
        this.displayItem = new ItemStack(type);
    }

    public String getDisplayName() {
        return displayName;
    }

    public ExtractMode next() {
        int ordinal = ordinal();
        ordinal++;
        ordinal %= values().length;
        return values()[ordinal];
    }

    public ItemStack getDisplayItem() {
        return displayItem.clone();
    }

}