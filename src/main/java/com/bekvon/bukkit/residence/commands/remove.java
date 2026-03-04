package com.bekvon.bukkit.residence.commands;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bekvon.bukkit.residence.LocaleManager;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.CommandAnnotation;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.containers.cmd;
import com.bekvon.bukkit.residence.containers.lm;
import com.bekvon.bukkit.residence.permissions.PermissionManager.ResPerm;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions.FlagCombo;
import com.bekvon.bukkit.residence.protection.PlayerManager;

import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;

public class remove implements cmd {

    @Override
    @CommandAnnotation(simple = true, priority = 2300)
    public Boolean perform(Residence plugin, CommandSender sender, String[] args, boolean resadmin) {

        ClaimedResidence res = null;

        if (args.length == 1) {
            res = plugin.getResidenceManager().getByName(args[0]);
        } else if (sender instanceof Player && args.length == 0) {
            res = plugin.getResidenceManager().getByLoc(((Player) sender).getLocation());
        }

        if (res == null) {
            lm.Invalid_Residence.sendMessage(sender);
            return true;
        }

        if (res.isSubzone() && !resadmin && !ResPerm.delete_subzone.hasPermission(sender, lm.Subzone_CantDelete)) {
            return true;
        }
        Player player = null;
        if (sender instanceof Player)
            player = (Player) sender;

        // NeoWorld start - 明确删除命令的处理逻辑

        // 服务器管理员可绕过限制
        if (!resadmin) {
            // 删除子领地时
            if (res.isSubzone()) {
                if (!ResPerm.delete_subzone.hasPermission(sender, lm.Subzone_CantDelete)) {
                    return true;
                }

                if (player != null) {
                    // 如果开启了“不允许非父领地主人删除”
                    if (plugin.getConfigManager().isPreventSubZoneRemoval()) {
                        // 在不是父领地主人时不允许删除
                        if (!res.getParent().isOwner(sender)) {
                            Residence.msg(player, lm.Subzone_CantDeleteNotOwnerOfParent);
                            return true;
                        }
                    } else {
                        // 在非父领地主人、非子领地主人、没有 admin 权限时不允许删除
                        if (!res.getParent().isOwner(sender)
                                && !res.isOwner(sender)
                                && !res.getPermissions().playerHas(player, Flags.admin, FlagCombo.OnlyTrue)) {
                            Residence.msg(player, lm.Subzone_CantDelete);
                            return true;
                        }
                    }
                }
            }
        } else {
            // 没有总权限不允许删除
            if (!ResPerm.delete.hasPermission(sender, lm.Residence_CantDeleteResidence)) {
                return true;
            }
            // 不是领地主人不允许删除
            if (!res.isOwner(sender)) {
                Residence.msg(player, lm.Residence_CantDeleteResidence);
                return true;
            }
        }

        // NeoWorld end - 明确删除命令的处理逻辑

        if (res.getRaid().isRaidInitialized() && !resadmin) {
            lm.Raid_noRemoval.sendMessage(sender);
            return true;
        }

        UUID uuid = PlayerManager.getSenderUUID(sender);
        plugin.deleteConfirm.remove(uuid);

        if (!plugin.deleteConfirm.containsKey(uuid) || !res.equals(plugin.deleteConfirm.get(uuid))) {
            String cmd = "res";
            if (resadmin)
                cmd = "resadmin";
            if (sender instanceof Player) {
                RawMessage rm = new RawMessage();
                if (res.isSubzone()) {
                    rm.addText(lm.Subzone_DeleteConfirm.getMessage(res.getResidenceName())).addHover(lm.info_clickToConfirm.getMessage()).addCommand(cmd + " confirm");
                } else {
                    rm.addText(lm.Residence_DeleteConfirm.getMessage(res.getResidenceName())).addHover(lm.info_clickToConfirm.getMessage()).addCommand(cmd + " confirm");
                }
                if (lm.Subzone_DeleteConfirm.getMessage(res.getResidenceName()).length() > 0)
                    rm.show(sender);
            } else {
                if (res.isSubzone())
                    lm.Subzone_DeleteConfirm.sendMessage(sender, res.getResidenceName());
                else
                    lm.Residence_DeleteConfirm.sendMessage(sender, res.getResidenceName());
            }
            plugin.deleteConfirm.put(uuid, res);
        } else {
            plugin.getResidenceManager().removeResidence(sender, res, resadmin);
        }
        return true;
    }

    @Override
    public void getLocale() {
        ConfigReader c = Residence.getInstance().getLocaleManager().getLocaleConfig();
        // Main command
        c.get("Description", "Remove residences.");
        c.get("Info", Arrays.asList("&eUsage: &6/res remove [residence_name]"));
        LocaleManager.addTabCompleteMain(this, "[residence]");
    }
}
