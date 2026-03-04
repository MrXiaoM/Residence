package top.mrxiaom.residence.listeners;

import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntitySpawnEvent;

public class ResidenceEntityListener$Patch {

    /**
     * 在方法最前面插入代码
     * @see com.bekvon.bukkit.residence.listeners.ResidenceEntityListener#onEntitySpawnEvent(EntitySpawnEvent)
     */
    public static void onEntitySpawnEvent(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (Flags.itemhighlight.isGlobalyEnabled()) {
            if (entity instanceof Item) {
                FlagPermissions perms = FlagPermissions.getPerms(entity.getLocation());
                if (perms.has(Flags.itemhighlight, FlagPermissions.FlagCombo.OnlyTrue)) {
                    entity.setGlowing(true);
                }
            }
        }
    }

}
