package me.mancy.arandisdrops.parties;

import me.mancy.arandisdrops.data.Settings;
import me.mancy.arandisdrops.data.Strings;
import me.mancy.arandisdrops.main.Main;
import me.mancy.arandisdrops.utils.FormattedMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DropParty implements Listener {

    private Main plugin;

    private int tier;

    private List<Location> locations = new ArrayList<>(LocationManager.getLocations().size());
    private List<Block> beaconCapBlocks = new ArrayList<>(LocationManager.getLocations().size());

    public DropParty(int tier) { this.tier = tier; }

    public DropParty(Main main) {
        this.plugin = main;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    private void setLocationsToUse() {
        if (LocationManager.getLocations() != null) {
            int amtToUse = Math.round(LocationManager.getLocations().size() / 2f);
            Collections.shuffle(LocationManager.getLocations());
            for (int x = 1; x <= amtToUse; x++) {
                int index = new Random().nextInt(amtToUse);
                if (LocationManager.getLocations().get(index) != null)
                    locations.add(LocationManager.getLocations().get(index));
            }
            Collections.shuffle(locations);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(new FormattedMessage(ChatColor.RED + "Error: No locations found when starting drop party").toString());
        }
    }

    private List<ItemStack> getItemList(int tier) {

        int totalItemsToDrop = Bukkit.getServer().getOnlinePlayers().size() * 2;
        // Ex. 10 online = 20

        List<ItemStack> itemStackList = new ArrayList<>();

        int amtCommonItems = Math.round((Settings.getDropChances().get(tier)[0] / 100f) * totalItemsToDrop);
        int amtUnCommonItems = Math.round((Settings.getDropChances().get(tier)[1] / 100f) * totalItemsToDrop);
        int amtRareItems = Math.round((Settings.getDropChances().get(tier)[2] / 100f) * totalItemsToDrop);
        int amtEpicItems = Math.round((Settings.getDropChances().get(tier)[3] / 100f) * totalItemsToDrop);
        int amtLegendaryItems = Math.round((Settings.getDropChances().get(tier)[4] / 100f) * totalItemsToDrop);

        if (amtCommonItems > Settings.getItemLists().get(1).size())
            amtCommonItems = Settings.getItemLists().get(1).size();

        if (amtUnCommonItems > Settings.getItemLists().get(2).size())
            amtUnCommonItems = Settings.getItemLists().get(2).size();

        if (amtRareItems > Settings.getItemLists().get(3).size())
            amtRareItems = Settings.getItemLists().get(3).size();

        if (amtEpicItems > Settings.getItemLists().get(4).size())
            amtEpicItems = Settings.getItemLists().get(4).size();

        if (amtLegendaryItems > Settings.getItemLists().get(5).size())
            amtLegendaryItems = Settings.getItemLists().get(5).size();

        for (int x = 0; x <= amtCommonItems; x++)
            itemStackList.add(Settings.getItemLists().get(1).get(x));

        for (int x = 0; x <= amtUnCommonItems; x++)
            itemStackList.add(Settings.getItemLists().get(2).get(x));

        for (int x = 0; x <= amtRareItems; x++)
            itemStackList.add(Settings.getItemLists().get(3).get(x));

        for (int x = 0; x <= amtEpicItems; x++)
            itemStackList.add(Settings.getItemLists().get(4).get(x));

        for (int x = 0; x <= amtLegendaryItems; x++)
            itemStackList.add(Settings.getItemLists().get(5).get(x));
        Collections.shuffle(itemStackList, new Random());
        return itemStackList;
    }

    private void removeBeaconCaps() {

        for (Location loc : locations) {
            Block highest = loc.getWorld().getHighestBlockAt(loc);
            beaconCapBlocks.add(highest);
        }

        for (Location loc: locations) {
           loc.getWorld().getHighestBlockAt(loc).setType(Material.AIR);
        }

    }

    private void playDropFireworks(Location offsetLoc) {
        Firework f = offsetLoc.getWorld().spawn(offsetLoc.add(0, 2, 0), Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder()
                .flicker(false)
                .trail(true)
                .withColor(Color.LIME)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.ORANGE)
                .withFade(Color.ORANGE)
                .build());
        fm.setPower(3);
        f.setFireworkMeta(fm);
    }

    private void playParticleEffects(Location offsetLoc, ItemStack i) {
        Random random = new Random();
        float red;
        float green;
        float blue;

        if (Settings.getItemLists().get(1).contains(i)) {
            red = 255;
            green = 255;
            blue = 255;
        } else if (Settings.getItemLists().get(2).contains(i)) {
            red = 21;
            green = 255;
            blue = 0;
        } else if (Settings.getItemLists().get(3).contains(i)) {
            red = 0;
            green = 72;
            blue = 255;
        } else if (Settings.getItemLists().get(4).contains(i)) {
            red = 255;
            green = 225;
            blue = 0;
        } else if (Settings.getItemLists().get(5).contains(i)) {
            red = 255;
            green = 0;
            blue = 0;
        } else {
            red = 0;
            green = 0;
            blue = 0;
        }


        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            for (int amt = 0; amt < 40; amt++) {
                float randX = 0.1f + random.nextFloat() * (1);
                float randY = 0.4f + random.nextFloat() * (1);
                float randZ = 0.1f + random.nextFloat() * (1);
                online.spawnParticle(Particle.SPELL_MOB, new Location(offsetLoc.getWorld(), offsetLoc.getX() + randX, offsetLoc.getY() + randY, offsetLoc.getZ() + randZ),
                                    0, red, green, blue);
            }
        }
    }

    private void playStartFireworks() {
        for (Location location : locations) {
            Location launchLoc = location.add(0, 2, 0);
            Firework f = launchLoc.getWorld().spawn(launchLoc, Firework.class);
            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(FireworkEffect.builder()
                    .flicker(false)
                    .trail(true)
                    .withColor(Color.fromBGR(255, 0, 0))
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .withColor(Color.ORANGE)
                    .withFade(Color.BLUE)
                    .build());
            fm.setPower(3);
            f.setFireworkMeta(fm);
        }
    }

    private int itemsDropped = 0;
    public void start() {
        setLocationsToUse();
        removeBeaconCaps();
        if (!locations.isEmpty()) {
            List<ItemStack> itemsToDrop = getItemList(tier);
            for (int x = 0; x < itemsToDrop.size(); x++) {
                final ItemStack i = itemsToDrop.get(x);
                final Location locToDrop = locations.get(x);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                    double offsetX = -Settings.getDropRadius() + (Settings.getDropRadius() + Settings.getDropRadius()) * new Random().nextDouble();
                    double offsetZ = -Settings.getDropRadius() + (Settings.getDropRadius() + Settings.getDropRadius()) * new Random().nextDouble();
                    Location offsetLoc = new Location(locToDrop.getWorld(), locToDrop.getX() + offsetX, (locToDrop.getWorld().getHighestBlockYAt(locToDrop) + 1) + Settings.getDropHeight(), locToDrop.getZ() + offsetZ);

                    playDropFireworks(offsetLoc);
                    playParticleEffects(offsetLoc, i);

                    offsetLoc.getWorld().dropItemNaturally(offsetLoc, i);
                    itemsDropped++;
                    if (itemsDropped >= itemsToDrop.size()) {
                        replaceBeaconCaps();
                    }

                }, 40L * (x + 1));

            }
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(new FormattedMessage(ChatColor.RED + "Fatal Error: No locations set at time of drop party starting").toString());
        }
    }

    private void replaceBeaconCaps() {
        DropPartyManager.setIsActiveDropParty(false);
        for (Block b : beaconCapBlocks) {
            b.setType(Material.STONE_SLAB);
        }
        Bukkit.getServer().broadcastMessage(Strings.partyEnded);
    }

}
