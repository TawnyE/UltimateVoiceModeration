package ret.tawny.ultimatevoicemoderation.voice.api;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;
import ret.tawny.ultimatevoicemoderation.voice.VoiceSessionListener;

import java.util.UUID;

public class VoiceApi implements VoicechatPlugin {

    private final UltimateVoiceModerationPlugin plugin;
    private final VoiceSessionListener voiceSessionListener;
    private VoicechatServerApi api;
    private Group mutedGroup;

    public VoiceApi(UltimateVoiceModerationPlugin plugin, VoiceSessionListener voiceSessionListener) {
        this.plugin = plugin;
        this.voiceSessionListener = voiceSessionListener;
    }

    public void init() {
        BukkitVoicechatService service = plugin.getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service != null) {
            service.registerPlugin(this);
        }
    }

    public void close() {
        if (api != null) {
            plugin.getServer().getServicesManager().unregister(api);
        }
    }

    @Override
    public String getPluginId() {
        return "ultimatevoicemoderation";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(MicrophonePacketEvent.class, voiceSessionListener::onPlayerVoicePacket);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        api = event.getVoicechat();
        mutedGroup = api.createGroup("UVM Muted", null);
    }

    public boolean isMuted(UUID playerUuid) {
        if (api == null) return false;
        VoicechatConnection connection = api.getConnectionOf(playerUuid);
        if (connection == null) return false;
        return connection.isInGroup() && connection.getGroup().equals(mutedGroup);
    }

    public void mutePlayer(UUID playerUuid) {
        if (api == null) return;
        VoicechatConnection connection = api.getConnectionOf(playerUuid);
        if (connection != null) {
            connection.setGroup(mutedGroup);
        }
    }

    public void unmutePlayer(UUID playerUuid) {
        if (api == null) return;
        VoicechatConnection connection = api.getConnectionOf(playerUuid);
        if (connection != null) {
            connection.setGroup(null);
        }
    }
}
