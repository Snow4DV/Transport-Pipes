package de.robotricker.transportpipes;

import de.robotricker.transportpipes.config.GeneralConf;
import de.robotricker.transportpipes.config.LangConf;
import de.robotricker.transportpipes.config.PlayerSettingsConf;
import de.robotricker.transportpipes.rendersystems.ModelledRenderSystem;
import de.robotricker.transportpipes.rendersystems.VanillaRenderSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

public class ResourcepackService implements Listener {

    private static final String URL = "https://raw.githubusercontent.com/BlackBeltPanda/Transport-Pipes/master/src/main/resources/wiki/resourcepack.zip";

    private final TransportPipes transportPipes;
    private final PlayerSettingsService playerSettingsService;

    private final ResourcepackMode resourcepackMode;
    private byte[] cachedHash;
    private final Set<Player> resourcepackPlayers;
    private final Set<Player> loadingResourcepackPlayers;
    private final Set<Player> waitingForAuthmeLogin;

    @Inject
    public ResourcepackService(TransportPipes transportPipes, PlayerSettingsService playerSettingsService, GeneralConf generalConf) {
        this.transportPipes = transportPipes;
        this.playerSettingsService = playerSettingsService;
        this.resourcepackMode = generalConf.getResourcepackMode();
        if (resourcepackMode == ResourcepackMode.DEFAULT) {
            cachedHash = calcSHA1Hash();
        }
        resourcepackPlayers = new HashSet<>();
        loadingResourcepackPlayers = new HashSet<>();
        waitingForAuthmeLogin = new HashSet<>();

        if (Bukkit.getPluginManager().isPluginEnabled("AuthMe")) {
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onAuthMeLogin(fr.xephi.authme.events.LoginEvent event) {
                    if (waitingForAuthmeLogin.remove(event.getPlayer())) {
                        loadResourcepackForPlayer(event.getPlayer());
                    }
                }
            }, transportPipes);
        }
    }

    public Set<Player> getResourcepackPlayers() {
        return resourcepackPlayers;
    }

    public ResourcepackMode getResourcepackMode() {
        return resourcepackMode;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (getResourcepackMode() == ResourcepackMode.NONE) {
            transportPipes.changeRenderSystem(event.getPlayer(), VanillaRenderSystem.getDisplayName());
        } else if (getResourcepackMode() == ResourcepackMode.DEFAULT) {
            PlayerSettingsConf conf = playerSettingsService.getOrCreateSettingsConf(event.getPlayer());
            if (conf.getRenderSystemName().equalsIgnoreCase(ModelledRenderSystem.getDisplayName())) {

                transportPipes.changeRenderSystem(event.getPlayer(), VanillaRenderSystem.getDisplayName());

                if (Bukkit.getPluginManager().isPluginEnabled("AuthMe") && !fr.xephi.authme.api.v3.AuthMeApi.getInstance().isAuthenticated(event.getPlayer())) {
                    waitingForAuthmeLogin.add(event.getPlayer());
                }
                else {
                    transportPipes.runTaskSync(() -> loadResourcepackForPlayer(event.getPlayer()));
                }
            }
        }
    }

    @EventHandler
    public void onResourcepackStatus(PlayerResourcePackStatusEvent e) {
        if (getResourcepackMode() != ResourcepackMode.DEFAULT) {
            return;
        }
        if (e.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || e.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            LangConf.Key.RESOURCEPACK_FAIL.sendMessage(e.getPlayer());
            if (loadingResourcepackPlayers.remove(e.getPlayer())) {
                transportPipes.changeRenderSystem(e.getPlayer(), VanillaRenderSystem.getDisplayName());
            }
        } else if (e.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            resourcepackPlayers.add(e.getPlayer());
            if (loadingResourcepackPlayers.remove(e.getPlayer())) {
                transportPipes.changeRenderSystem(e.getPlayer(), ModelledRenderSystem.getDisplayName());
            }
        }
    }

    public void loadResourcepackForPlayer(Player p) {
        if (getResourcepackMode() == ResourcepackMode.DEFAULT) {
            if (cachedHash == null) {
                p.setResourcePack(URL);
            } else {
                p.setResourcePack(URL, cachedHash);
            }
            loadingResourcepackPlayers.add(p);
        }
    }

    private byte[] calcSHA1Hash() {
        try {
            URL url = new URL(ResourcepackService.URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getContentLength() <= 0) {
                return null;
            }
            byte[] resourcePackBytes = new byte[connection.getContentLength()];
            InputStream in = connection.getInputStream();

            int b;
            int i = 0;
            while ((b = in.read()) != -1) {
                resourcePackBytes[i] = (byte) b;
                i++;
            }

            in.close();

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(resourcePackBytes);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } catch (Exception ignored) {
        }
        return null;
    }

    public enum ResourcepackMode {
        DEFAULT, NONE, SERVER
    }

}
