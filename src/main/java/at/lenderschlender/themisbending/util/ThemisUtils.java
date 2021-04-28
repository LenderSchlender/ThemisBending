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

import com.gmail.olexorus.themis.api.CheckType;
import com.gmail.olexorus.themis.api.ThemisApi;
import org.bukkit.entity.Player;

import java.util.Comparator;

public class ThemisUtils {
    public static final Comparator<Player> violationComparator = (o1, o2) -> {
        double violations1 = 0;
        double violations2 = 0;
        for (CheckType type : CheckType.values()) {
            violations1 += ThemisApi.getViolationScore(o1, type);
            violations2 += ThemisApi.getViolationScore(o2, type);
        }
        return Double.compare(violations1, violations2);
    };

    /**
     * A shortcut for {@link ThemisUtils#formatViolationScore(double)}
     *
     * @param player The Player to get the violation score from
     * @param checkType The CheckType to get the violation score from
     * @return A String with the formatted current violation score
     */
    public static String getViolationScore(Player player, CheckType checkType) {
        return formatViolationScore(ThemisApi.getViolationScore(player, checkType));
    }

    /**
     * Formats the violation score to one decimal place
     * @param score The violation score
     * @return A formatted String
     */
    public static String formatViolationScore(double score) {
        return String.format("%.1f", score);
    }

    /**
     * Gets the total violation score of a player
     * @param player The player to get the violation score from
     * @return All violation scores added up.
     */
    public static double getTotalViolations(Player player) {
        double totalScore = 0;
        for (CheckType type : CheckType.values()) {
            totalScore += ThemisApi.getViolationScore(player, type);
        }
        return totalScore;
    }
}
