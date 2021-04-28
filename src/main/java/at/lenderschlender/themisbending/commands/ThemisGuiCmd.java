/*
MIT License

Copyright (c) 2021 LenderSchlender

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package at.lenderschlender.themisbending.commands;


import at.lenderschlender.themisbending.ThemisBending;
import at.lenderschlender.themisbending.util.PlaceholderUtils;
import at.lenderschlender.themisbending.util.ThemisUtils;
import me.mattstudios.mfgui.gui.components.ItemBuilder;
import me.mattstudios.mfgui.gui.guis.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ThemisGuiCmd implements CommandExecutor {
    private final ThemisBending plugin;

    public ThemisGuiCmd(ThemisBending plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly Players can execute this command!");
            return true;
        }
        Player senderPlayer = (Player) sender;
        FileConfiguration config = plugin.getConfig();

        if (!senderPlayer.hasPermission("themisbending.gui.open")) {
            String noPermMsg = config.getString("no_permission_message");
            if (noPermMsg == null)
                noPermMsg = ThemisBending.NO_PERMISSION_MESSAGE;
            sender.sendMessage(noPermMsg);
            return true;
        }


        // The inventory title
        String title = PlaceholderUtils.setPlaceholders(senderPlayer, config.getString("gui.title", ChatColor.RED +"Themis violation gui"));

        String fillerMaterialName = config.getString("gui.filler.item", "LIGHT_GRAY_STAINED_GLASS_PANE");
        // fillerMaterialName can't be null, but I wanted to get rid of the warning
        Material filler = Material.getMaterial(Objects.requireNonNull(fillerMaterialName));
        String fillerName = config.getString("gui.filler.name", ChatColor.RESET.toString());
        List<String> fillerLore = config.getStringList("gui.filler.lore");

        int rowAmount = config.getInt("gui.row_amount", 1) + 1;
        if (rowAmount < 2 || rowAmount > 6 )
            rowAmount = 2;

        // The name and lore for all the player heads (The placeholders are set in the forEach loop)
        String playerName = config.getString("gui.player_item.name", "[PLAYER_NAME]");
        List<String> playerLore = config.getStringList("gui.player_item.lore");

        // If the inventory should be closed when a playerhead is clicked
        boolean closeOnClick = config.getBoolean("gui.player_item.close_on_click", true);


        // The right button (next page)
        String rightName = PlaceholderUtils.setPlaceholders(senderPlayer, config.getString("gui.buttons.next.name", "Next Page"));
        String rightMaterialName = config.getString("gui.buttons.next.item", "PAPER");
        // rightMaterialName can't be null, but I wanted to get rid of the warning
        Material rightMaterial = Material.getMaterial(Objects.requireNonNull(rightMaterialName));

        List<String> rightLore = new ArrayList<>();
        config.getStringList("gui.buttons.next.lore").forEach(s ->
                rightLore.add(ChatColor.RESET + PlaceholderUtils.setPlaceholders(senderPlayer, s)));

        // The left button (previous page)
        String leftName = PlaceholderUtils.setPlaceholders(senderPlayer, config.getString("gui.buttons.previous.name"));
        String leftMaterialName = config.getString("gui.buttons.previous.item", "PAPER");
        // leftMaterialName can't be null, but I wanted to get rid of the warning
        Material leftMaterial = Material.getMaterial(Objects.requireNonNull(leftMaterialName));

        List<String> leftLore = new ArrayList<>();
        config.getStringList("gui.buttons.previous.lore").forEach(s ->
                leftLore.add(ChatColor.RESET + PlaceholderUtils.setPlaceholders(senderPlayer, s)));

        if (rightMaterial == null)
            rightMaterial = Material.ARROW;
        if (leftMaterial == null)
            leftMaterial = Material.ARROW;
        if (filler == null)
            filler = Material.LIGHT_GRAY_STAINED_GLASS_PANE;


        // playerName has added the format reset directly in the SkullMeta#setDisplayname() method,
        // because it is used in a lambda
        rightName = ChatColor.RESET + rightName;
        leftName = ChatColor.RESET + leftName;

        // Create the GUI
        PaginatedGui gui = new PaginatedGui(rowAmount, rowAmount * 9, title);
        // This is only used for the fillers
        gui.setDefaultClickAction(event -> event.setCancelled(true));
        // Add the fillers
        gui.getFiller().fillBottom(ItemBuilder.from(filler).setName(fillerName).setLore(fillerLore).asGuiItem());

        // right button (next page)
        gui.setItem(gui.getRows(), 9, ItemBuilder.from(rightMaterial)
                .setName(rightName)
                .setLore(rightLore)
                .asGuiItem(event ->  {
                    gui.next();
                    event.setCancelled(true);
                }));
        // left button (previous page)
        gui.setItem(gui.getRows(), 1, ItemBuilder.from(leftMaterial)
                .setName(leftName)
                .setLore(leftLore)
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.previous();
                }));

        // Sort the players by violation count
        List<Player> players = new ArrayList<>(plugin.getServer().getOnlinePlayers());
        players.sort(ThemisUtils.violationComparator);

        // Add all the playerheads to the GUI
        players.stream().filter(p -> ThemisUtils.getTotalViolations(p) > config.getDouble("gui.violation_threshold", 1.0))
                .forEachOrdered(player -> {

            List<String> lore = new ArrayList<>();
            playerLore.forEach(s -> lore.add(ChatColor.RESET + PlaceholderUtils.setPlaceholders(player, s)));

            ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta == null) return;

            meta.setOwningPlayer(player);
            meta.setDisplayName(PlaceholderUtils.setPlaceholders(player, ChatColor.RESET + playerName));
            meta.setLore(lore);
            item.setItemMeta(meta);
                gui.addItem(ItemBuilder.from(item).asGuiItem(event -> {
                    event.setCancelled(true);
                    if (!(event.getWhoClicked() instanceof Player))
                        return;
                    Player ePlayer = (Player) event.getWhoClicked();
                    if (!ePlayer.hasPermission("themisbending.gui.commands")) return;
                    ConsoleCommandSender cmdSender = plugin.getServer().getConsoleSender();
                    switch (event.getClick()) {
                        case LEFT:
                            config.getStringList("gui.player_item.commands.left_click.console")
                                    .forEach(cmd -> Bukkit.dispatchCommand(cmdSender, PlaceholderUtils.setPlaceholders(player, cmd)));
                            config.getStringList("gui.player_item.commands.left_click.player")
                                    .forEach(cmd -> ePlayer.performCommand(PlaceholderUtils.setPlaceholders(player, cmd)));
                            break;
                        case SHIFT_LEFT:
                            config.getStringList("gui.player_item.commands.shift_left_click.console")
                                    .forEach(cmd -> Bukkit.dispatchCommand(cmdSender, PlaceholderUtils.setPlaceholders(player, cmd)));
                            config.getStringList("gui.player_item.commands.shift_left_click.player")
                                    .forEach(cmd -> ePlayer.performCommand(PlaceholderUtils.setPlaceholders(player, cmd)));
                            break;
                        case RIGHT:
                            config.getStringList("gui.player_item.commands.right_click.console")
                                    .forEach(cmd -> Bukkit.dispatchCommand(cmdSender, PlaceholderUtils.setPlaceholders(player, cmd)));
                            config.getStringList("gui.player_item.commands.right_click.player")
                                    .forEach(cmd -> ePlayer.performCommand(PlaceholderUtils.setPlaceholders(player, cmd)));
                            break;
                        case SHIFT_RIGHT:
                            config.getStringList("gui.player_item.commands.shift_right_click.console")
                                    .forEach(cmd -> Bukkit.dispatchCommand(cmdSender, PlaceholderUtils.setPlaceholders(player, cmd)));
                            config.getStringList("gui.player_item.commands.shift_right_click.player")
                                    .forEach(cmd -> ePlayer.performCommand(PlaceholderUtils.setPlaceholders(player, cmd)));
                            break;
                        case MIDDLE:
                            config.getStringList("gui.player_item.commands.middle_click.console")
                                    .forEach(cmd -> Bukkit.dispatchCommand(cmdSender, PlaceholderUtils.setPlaceholders(player, cmd)));
                            config.getStringList("gui.player_item.commands.middle_click.player")
                                    .forEach(cmd -> ePlayer.performCommand(PlaceholderUtils.setPlaceholders(player, cmd)));
                            break;
                        case DROP:
                            config.getStringList("gui.player_item.commands.drop.console")
                                    .forEach(cmd -> Bukkit.dispatchCommand(cmdSender, PlaceholderUtils.setPlaceholders(player, cmd)));
                            config.getStringList("gui.player_item.commands.drop.player")
                                    .forEach(cmd -> ePlayer.performCommand(PlaceholderUtils.setPlaceholders(player, cmd)));
                            break;
                        case CONTROL_DROP:
                            config.getStringList("gui.player_item.commands.control_drop.console")
                                    .forEach(cmd -> Bukkit.dispatchCommand(cmdSender, PlaceholderUtils.setPlaceholders(player, cmd)));
                            config.getStringList("gui.player_item.commands.control_drop.player")
                                    .forEach(cmd -> ePlayer.performCommand(PlaceholderUtils.setPlaceholders(player, cmd)));
                    }
                    if (closeOnClick)
                        gui.close(ePlayer);
                }));
        });
        if (gui.getPageItems().size() == 0) {
            String msg = config.getString("gui.empty_gui_message");
            if (msg == null)
                msg = ChatColor.GREEN + "It seems like there is nobody to show!";
            senderPlayer.sendMessage(msg);
            return true;
        }

        gui.open(senderPlayer);


        return true;
    }
}
