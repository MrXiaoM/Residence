package top.mrxiaom.residence.listeners;

import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntitySpawnEvent;

public class ResidenceEntityListener$Patch {

    public static void onEntitySpawnEvent(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (Flags.itemhighlight.isGlobalyEnabled()) {
            if (entity instanceof Item) {
                FlagPermissions perms = plugin.getPermsByLoc(entity.getLocation());
                if (perms.has(Flags.itemhighlight, FlagPermissions.FlagCombo.OnlyTrue)) {
                    entity.setGlowing(true);
                }
            }
        }
    }
}
