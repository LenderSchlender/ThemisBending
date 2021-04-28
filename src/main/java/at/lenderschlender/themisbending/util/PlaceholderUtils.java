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
package at.lenderschlender.themisbending.util;

import at.lenderschlender.themisbending.ThemisBending;
import com.gmail.olexorus.themis.api.CheckType;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

import static at.lenderschlender.themisbending.util.ThemisUtils.*;

public class PlaceholderUtils {

    public static String setPlaceholders(Player player, String input) {
        input = input
                .replace("[SERVER_TPS]", new DecimalFormat("00.0#").format(LagUtils.getTPS()))
                .replace("[PLAYER_NAME]", player.getName())
                .replace("[PLAYER_PING]", String.valueOf(ThemisBending.getInstance().getPing(player.getUniqueId())))
                .replace("[TOTAL_VIOLATION_SCORE]", formatViolationScore(getTotalViolations(player)))
                .replace("[VERTICAL_MOVEMENT_SCORE]", getViolationScore(player, CheckType.VERTICAL_MOVEMENT))
                .replace("[HORIZONTAL_MOVEMENT_SCORE]", getViolationScore(player, CheckType.HORIZONTAL_MOVEMENT))
                .replace("[PACKET_SPOOF_SCORE]", getViolationScore(player, CheckType.PACKET_SPOOF))
                .replace("[ILLEGAL_PACKET_SCORES]", getViolationScore(player, CheckType.ILLEGAL_PACKETS))
                .replace("[TICKRATE_SCORE]", getViolationScore(player, CheckType.TICKRATE));

        if (player.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
            return PlaceholderAPI.setPlaceholders(player, input);
        return input;
    }

}
