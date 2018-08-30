package me.mancy.arandisdrops.tokens;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Account implements ConfigurationSerializable {

    private final UUID playerUUID;

    //Tier, Balance
    private Map<Integer, Integer> balances = new HashMap<>();

    public Account(Player player, int startBalance) {
        this.playerUUID = player.getUniqueId();
        if (AccountManager.getPlayersAccount(player) != null)
            return;
        for (int x = 1; x <= 4; x++) {
            this.balances.putIfAbsent(x, startBalance);
        }
        AccountManager.registerAccount(this);

    }

    public void addTokens(int tier, int amount) {
        this.balances.put(tier, this.balances.get(tier) + amount);
        System.out.println(this.balances.get(tier));
    }

    public void removeTokens(int tier, int amount) {
        if (this.balances.get(tier) - amount < 0) {
            this.balances.put(tier, 0);
        } else {
            this.balances.put(tier, this.balances.get(tier) - amount);
        }
    }

    public void setBalance(int tier, int amount) {
        if (amount < 0) {
            this.balances.put(tier, 0);
        } else {
            this.balances.put(tier, amount);
        }
    }

    public Integer getBalance(int tier) {
        if (this.getPlayer() == null) return -1;
        switch (tier) {
            case 1: if (this.balances.containsKey(1))
                return this.balances.get(1);
            return -1;
            case 2: if (this.balances.containsKey(2))
                return this.balances.get(2);
            return -1;
            case 3: if (this.balances.containsKey(3))
                return this.balances.get(3);
            return -1;
            case 4: if (this.balances.containsKey(4))
                return this.balances.get(4);
            return -1;
        }
        return -1;
    }

    public void resetBalance(int tier) {
        this.balances.put(tier, 0);
    }

    public void resetAllBalances() {
        for (int x = 1; x <= 4; x++) {
            this.balances.put(x, 0);
        }
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.playerUUID);
    }


    @Override
    public boolean equals(Object object) {
        if(this == object)
            return true;

        if(object == null || object.getClass() != this.getClass())
            return false;

        Account account = (Account) object;

        return (account.playerUUID.equals(this.playerUUID));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("Player-UUID", playerUUID.toString());
        map.put("Tier 1 Balance", getBalance(1));
        map.put("Tier 2 Balance", getBalance(2));
        map.put("Tier 3 Balance", getBalance(3));
        map.put("Tier 4 Balance", getBalance(4));
        return map;
    }

    public static Account deserialize(Map<String, Object> map) {
        if (Bukkit.getPlayer(UUID.fromString((String)map.get("Player-UUID"))) != null) {
            Account account = new Account(Bukkit.getPlayer(UUID.fromString((String)map.get("Player-UUID"))), 0);
            account.setBalance(1, (int) map.get("Tier 1 Balance"));
            account.setBalance(2, (int) map.get("Tier 2 Balance"));
            account.setBalance(3, (int) map.get("Tier 3 Balance"));
            account.setBalance(4, (int) map.get("Tier 4 Balance"));
            return account;
        }
        return null;
    }

    @Override
    public int hashCode() {
        return Bukkit.getPlayer(playerUUID).getEntityId();
    }

}
