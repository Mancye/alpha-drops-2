package me.mancy.alphadrops.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {

    private final Material EXIT_BUTTON = Material.BARRIER;

    protected abstract Inventory getInventory();

    protected void setButton(int slot, ItemStack itemStack) {
        getInventory().setItem(slot, itemStack);
    }

    protected void setButton(int slot, Material type, String name, List<String> lore) {
        if (type != null && lore != null) {
            if (slot >= 0 && slot < getInventory().getSize()) {
                ItemStack itemStack = new ItemStack(type);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLore(lore);
                itemMeta.setDisplayName(name);
                itemStack.setItemMeta(itemMeta);
                getInventory().setItem(slot, itemStack);
            }
        }
    }

    protected void addButton(ItemStack itemStack) {
        getInventory().addItem(itemStack);
    }

    protected abstract void setUp();

    @EventHandler
    protected abstract void handleInput(InventoryClickEvent event);

    protected void fillEmptySlots(Inventory inv) {
        ItemStack emptySlot = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta emptyMeta = emptySlot.getItemMeta();
        emptyMeta.setDisplayName("");
        emptySlot.setItemMeta(emptyMeta);

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
                inv.setItem(i, emptySlot);
            }
        }
    }

    protected void setExitButton(int slot) {
        setButton(slot, EXIT_BUTTON, ChatColor.RED + "Exit", new ArrayList<>());
    }

}
