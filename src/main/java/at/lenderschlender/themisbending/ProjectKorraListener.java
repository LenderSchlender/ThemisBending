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
package at.lenderschlender.themisbending;

import com.gmail.olexorus.themis.api.CheckType;
import com.gmail.olexorus.themis.api.ViolationEvent;
import com.projectkorra.projectkorra.event.AbilityEndEvent;
import com.projectkorra.projectkorra.event.AbilityStartEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectKorraListener implements Listener {
    List<Player> bypassingPlayers = new ArrayList<>();

    public ProjectKorraListener() {}

    @EventHandler
    public void onViolation(ViolationEvent event) {
        Player player = event.getPlayer();
        CheckType type = event.getType();

        if (bypassingPlayers.contains(player)) {
            if (type.equals(CheckType.HORIZONTAL_MOVEMENT) ||
                    type.equals(CheckType.VERTICAL_MOVEMENT)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAbilityStart(AbilityStartEvent event) {
        Long joinTime = joinDates.get(event.getAbility().getPlayer());
        if (joinTime == null) return;
        long joinDiff = System.currentTimeMillis() - joinTime;
        if (joinDiff < 1000) return;

        if (!bypassingPlayers.contains(event.getAbility().getPlayer()))
            bypassingPlayers.add(event.getAbility().getPlayer());

    }

    @EventHandler
    public void onAbilityEnd(AbilityEndEvent event) {
        bypassingPlayers.remove(event.getAbility().getPlayer());
    }

    // For now we need to use the Player join dates to not bypass them instantly when joining with a passive ability.
    // I hope that when the new ProjectKorra API comes out, we can avoid that.
    HashMap<Player, Long> joinDates = new HashMap<>();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        joinDates.put(event.getPlayer(), System.currentTimeMillis());
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        joinDates.remove(event.getPlayer());
    }
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        joinDates.remove(event.getPlayer());
    }
}
