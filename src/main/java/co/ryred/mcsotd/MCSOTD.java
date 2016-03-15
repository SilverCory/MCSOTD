/*
 * http://ryred.co/
 * ace[at]ac3-servers.eu
 *
 * =================================================================
 *
 * Copyright (c) 2016, Cory Redmond
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of MCSOTD nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package co.ryred.mcsotd;

import lombok.Getter;
import lombok.Setter;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.ServerLoginHandler;
import org.spacehq.mc.protocol.data.message.TextMessage;
import org.spacehq.mc.protocol.data.status.PlayerInfo;
import org.spacehq.mc.protocol.data.status.ServerStatusInfo;
import org.spacehq.mc.protocol.data.status.VersionInfo;
import org.spacehq.mc.protocol.data.status.handler.ServerInfoBuilder;
import org.spacehq.packetlib.Server;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Proxy;
import java.util.Properties;

/**
 * Created by Cory Redmond on 14/03/2016.
 *
 * @author Cory Redmond <ace@ac3-servers.eu>
 */
public class MCSOTD {

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private int port;

    @Getter
    @Setter
    private String bindAddr;

    @Getter
    private Server server;

    public MCSOTD() {
        reload();
    }

    public static BufferedImage getFavicon() {

        try {

            File file = new File("server-icon.png");
            if (!file.exists()) return null;

            BufferedImage image = ImageIO.read(new FileInputStream(file));

            // check size
            if (image.getWidth() != 64 || image.getHeight() != 64) {
                throw new IllegalArgumentException("Server icon must be exactly 64x64 pixels");
            }

            return image;

        } catch (Exception e) {
            return null;
        }

    }

    public void init() {
        String addr = bindAddr;
        if (addr == null || addr.isEmpty()) addr = "0.0.0.0";
        Server server = new Server(addr, port, MinecraftProtocol.class, new TcpSessionFactory());
        server.setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session -> new ServerStatusInfo(new VersionInfo(ChatColor.translateAlternateColorCodes('&', "&4&lMaintenance"), MinecraftConstants.PROTOCOL_VERSION + 10), new PlayerInfo(1000, 100, new GameProfile[0]), new TextMessage(getColouredMessage()), getFavicon()));
        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session -> session.disconnect(getColouredMessage()));
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);

        this.server = server;
    }

    private String getColouredMessage() {
        return ChatColor.translateAlternateColorCodes('&', message).replace("\\r", "").replace("\\n", "\n");
    }

    public void start() {
        if (this.server == null) init();
        if (isListening()) throw new IllegalStateException("Server is already running!");
        server.bind();
    }

    public void stop() {
        if (this.server == null || !isListening()) return;
        this.server.close();
    }

    public boolean isListening() {
        try {
            return this.server.isListening();
        } catch (Exception ex) {
            return false;
        }
    }

    public void reload() {
        Properties properties = new Properties();
        try {
            File file = new File("server.properties");
            if (file.exists())
                properties.load(new FileReader(file));

            if (!properties.containsKey("hold-message"))
                properties.setProperty("hold-message", "&cWe're currently down for maintanance!\\n&aWe'll be back soon!");

            if (!properties.containsKey("server-port"))
                properties.setProperty("server-port", "25565");

            properties.store(new FileWriter(file), "Edited by MCSOTD!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.message = properties.getProperty("hold-message", "&cWe're currently down for maintanance!\\n&aWe'll be back soon!");
        this.port = Integer.parseInt(properties.getProperty("server-port", "25565"));
        this.bindAddr = properties.getProperty("server-ip");
    }

}
