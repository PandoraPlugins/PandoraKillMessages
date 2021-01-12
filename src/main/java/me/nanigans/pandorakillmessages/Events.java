package me.nanigans.pandorakillmessages;

import me.nanigans.pandorakillmessages.Util.JsonUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Events implements Listener {

    @EventHandler
    public void onKill(PlayerDeathEvent event){

        Map<String, Object> killMessages = (Map<String, Object>) JsonUtil.getData("killMessages");
        final EntityDamageEvent.DamageCause cause = event.getEntity().getLastDamageCause().getCause();
        assert killMessages != null;
        if(killMessages.containsKey(cause.toString())){
            final Map<String, Object> killMsges = ((Map<String, Object>) killMessages.get(cause.toString()));
            Map<String, Object> data = (Map<String, Object>) killMsges.get("data");
            final Player killer = event.getEntity().getKiller();

            if(data != null) {
                if (Boolean.parseBoolean(data.get("disable").toString())){
                    event.setDeathMessage(null);
                    return;
                }
                if (!Boolean.parseBoolean(data.get("useDefault").toString())) {
                    if (Boolean.parseBoolean(data.get("killedByPlayerOnly").toString()) && killer == null) {
                        event.setDeathMessage(null);
                        return;
                    }

                    final Player player = event.getEntity();

                    killMsges.remove("data");
                    final Object[] objects = killMsges.keySet().toArray();
                    final String msg = killMsges.get(objects[(int) (objects.length * Math.random())]).toString();
                    String deathMsg = ChatColor.translateAlternateColorCodes('&', msg)
                            .replaceAll("\\{player}", player.getName())
                            .replaceAll("\\{killer}", (killer != null ? killer.getName() : data.get("defaultKiller").toString()));
                    if(killer != null) {
                        final ItemStack itemInHand = killer.getInventory().getItemInHand();
                        final String itemName = (itemInHand != null && itemInHand.getType() != Material.AIR) ? itemInHand.getItemMeta().getDisplayName() : data.get("defaultWeapon").toString();
                        System.out.println("itemName = " + itemName);
                        deathMsg = deathMsg.replaceAll("\\{weapon}", (itemName == null ?
                                WordUtils.capitalizeFully(itemInHand.getType().name().toLowerCase().replaceAll("_", " "))
                                : itemName));
                    } else{
                        deathMsg = deathMsg.replaceAll("\\{weapon}", "Nothing");
                    }
                    event.setDeathMessage(deathMsg);


                }
            }

        }

    }

}
