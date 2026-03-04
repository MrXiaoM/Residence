package top.mrxiaom.residence.listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceSubzoneRenameEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.command.CommandSender;

public class ClaimedResidence$Patch {

    /**
     * 在 <code>res.setName(newN);</code> 之前插入代码
     * @see ClaimedResidence#renameSubzone(CommandSender, String, String, boolean)
     */
    @SuppressWarnings("RedundantIfStatement")
    public static boolean onSubzoneRename(ClaimedResidence res, String newName, String oldName) {

        ResidenceSubzoneRenameEvent resevent = new ResidenceSubzoneRenameEvent(res, newName, oldName);
        Residence.getInstance().getServ().getPluginManager().callEvent(resevent);

        if (resevent.isCancelled())
            return false;

        return true;
    }

}
