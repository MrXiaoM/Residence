package com.bekvon.bukkit.residence.commands;

import java.util.Arrays;
import java.util.Collections;

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

import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;

public class remove implements cmd {

	@Override
	@CommandAnnotation(simple = true, priority = 2300)
	public Boolean perform(Residence plugin, CommandSender sender, String[] args, boolean resadmin) {

		ClaimedResidence res = null;
		String senderName = sender.getName();
		if (args.length == 1) {
			res = plugin.getResidenceManager().getByName(args[0]);
		} else if (sender instanceof Player && args.length == 0) {
			res = plugin.getResidenceManager().getByLoc(((Player) sender).getLocation());
		}

		if (res == null) {
			plugin.msg(sender, lm.Invalid_Residence);
			return true;
		}

		if (res.isSubzone() && !resadmin && !ResPerm.delete_subzone.hasPermission(sender, lm.Subzone_CantDelete)) {
			return true;
		}

		Player player = null;
		if (sender instanceof Player)
			player = (Player) sender;

		// 服务器管理员可绕过限制
		if (!resadmin) {
			// 删除子领地时
			if (res.isSubzone()) {
				// 没有总权限不允许删除
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
		}

		plugin.deleteConfirm.remove(senderName);

		String resname = res.getName();

		if (!plugin.deleteConfirm.containsKey(senderName) || !resname.equalsIgnoreCase(plugin.deleteConfirm.get(senderName))) {
			String cmd = "res";
			if (resadmin)
				cmd = "resadmin";
			if (sender instanceof Player) {
				RawMessage rm = new RawMessage();
				if (res.isSubzone()) {
					rm.addText(plugin.msg(lm.Subzone_DeleteConfirm, res.getResidenceName())).addHover(plugin.msg(lm.info_clickToConfirm)).addCommand(cmd + " confirm");
				} else {
					rm.addText(plugin.msg(lm.Residence_DeleteConfirm, res.getResidenceName())).addHover(plugin.msg(lm.info_clickToConfirm)).addCommand(cmd + " confirm");
				}
				if (plugin.msg(lm.Subzone_DeleteConfirm, res.getResidenceName()).length() > 0)
					rm.show(sender);
			} else {
				if (res.isSubzone())
					plugin.msg(sender, lm.Subzone_DeleteConfirm, res.getResidenceName());
				else
					plugin.msg(sender, lm.Residence_DeleteConfirm, res.getResidenceName());
			}
			plugin.deleteConfirm.put(senderName, resname);
		} else {
			plugin.getResidenceManager().removeResidence(sender, resname, resadmin);
		}
		return true;
	}

	@Override
	public void getLocale() {
		ConfigReader c = Residence.getInstance().getLocaleManager().getLocaleConfig();
		// Main command
		c.get("Description", "Remove residences.");
		c.get("Info", Collections.singletonList("&eUsage: &6/res remove [residence_name]"));
		LocaleManager.addTabCompleteMain(this, "[residence]");
	}
}
