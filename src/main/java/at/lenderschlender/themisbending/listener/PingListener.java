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
package at.lenderschlender.themisbending.listener;

import at.lenderschlender.themisbending.ThemisBending;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import java.util.HashMap;
import java.util.UUID;

public class PingListener extends PacketAdapter {
    private final HashMap<UUID, Long> responseTimes = new HashMap<>();
    private final HashMap<UUID, Long> lastKeepAlivePackets = new HashMap<>();

    public PingListener(ThemisBending plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.KEEP_ALIVE, PacketType.Play.Client.KEEP_ALIVE);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        long currentTime = System.currentTimeMillis();
        if (event.getPacketType() != PacketType.Play.Server.KEEP_ALIVE) return;
        lastKeepAlivePackets.put(event.getPlayer().getUniqueId(), currentTime);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        long currentTime = System.currentTimeMillis();
        if (event.getPacketType() != PacketType.Play.Client.KEEP_ALIVE) return;
        UUID uuid = event.getPlayer().getUniqueId();
        Long lastKeepAliveSent = lastKeepAlivePackets.get(uuid);
        if (lastKeepAliveSent == null) return;
        long ping = currentTime - lastKeepAliveSent;
        responseTimes.put(uuid, ping);
    }

    public long getPing(UUID uuid) {
        if (responseTimes.get(uuid) == null)
            return 0;
        return responseTimes.get(uuid);
    }
}
