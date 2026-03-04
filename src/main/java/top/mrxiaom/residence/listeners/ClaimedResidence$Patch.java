package top.mrxiaom.residence.listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceSubzoneRenameEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class ClaimedResidence$Patch {
    @SuppressWarnings("RedundantIfStatement")
    public static boolean onSubzoneRename(ClaimedResidence res, String newName, String oldName) {

        ResidenceSubzoneRenameEvent resevent = new ResidenceSubzoneRenameEvent(res, newName, oldName);
        Residence.getInstance().getServ().getPluginManager().callEvent(resevent);

        if (resevent.isCancelled())
            return false;

        return true;
    }
}
