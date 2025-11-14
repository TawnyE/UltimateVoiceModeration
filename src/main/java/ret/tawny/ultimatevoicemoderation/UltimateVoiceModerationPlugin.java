package ret.tawny.ultimatevoicemoderation;

import org.bukkit.plugin.java.JavaPlugin;
import ret.tawny.ultimatevoicemoderation.actions.ActionManager;
import ret.tawny.ultimatevoicemoderation.commands.UvmCommandHandler;
import ret.tawny.ultimatevoicemoderation.config.ConfigManager;
import ret.tawny.ultimatevoicemoderation.logging.AuditLogManager;
import ret.tawny.ultimatevoicemoderation.moderation.ModerationEngine;
import ret.tawny.ultimatevoicemoderation.moderation.PatternRepository;
import ret.tawny.ultimatevoicemoderation.scheduler.TaskScheduler;
import ret.tawny.ultimatevoicemoderation.scheduler.TaskSchedulerFactory;
import ret.tawny.ultimatevoicemoderation.text.TextChatListener;
import ret.tawny.ultimatevoicemoderation.transcription.LocalTranscriptionProvider;
import ret.tawny.ultimatevoicemoderation.transcription.VoiceTranscriptionProvider;
import ret.tawny.ultimatevoicemoderation.util.MessageManager;
import ret.tawny.ultimatevoicemoderation.voice.VoiceIntegrationManager;
import ret.tawny.ultimatevoicemoderation.voice.VoiceSessionListener;

public final class UltimateVoiceModerationPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private TaskScheduler taskScheduler;
    private PatternRepository patternRepository;
    private ModerationEngine moderationEngine;
    private VoiceIntegrationManager voiceIntegrationManager;
    private VoiceSessionListener voiceSessionListener;
    private VoiceTranscriptionProvider voiceTranscriptionProvider;
    private ActionManager actionManager;
    private AuditLogManager auditLogManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        messageManager = new MessageManager(this);
        messageManager.loadMessages();

        patternRepository = new PatternRepository(this);
        patternRepository.loadPatterns();

        moderationEngine = new ModerationEngine(this, patternRepository);

        taskScheduler = TaskSchedulerFactory.createScheduler(this);

        voiceTranscriptionProvider = new LocalTranscriptionProvider(this);
        voiceSessionListener = new VoiceSessionListener(this);
        voiceIntegrationManager = new VoiceIntegrationManager(this);
        voiceIntegrationManager.init();

        actionManager = new ActionManager(this);
        auditLogManager = new AuditLogManager(this);


        getCommand("uvm").setExecutor(new UvmCommandHandler(this));
        getServer().getPluginManager().registerEvents(new TextChatListener(this), this);

        getLogger().info("UltimateVoiceModeration has been enabled!");
    }

    @Override
    public void onDisable() {
        if (voiceIntegrationManager != null) {
            voiceIntegrationManager.close();
        }
        if (taskScheduler != null) {
            taskScheduler.cancelTasks();
        }
        getLogger().info("UltimateVoiceModeration has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public PatternRepository getPatternRepository(){
        return patternRepository;
    }

    public ModerationEngine getModerationEngine() {
        return moderationEngine;
    }

    public VoiceIntegrationManager getVoiceIntegrationManager() {
        return voiceIntegrationManager;
    }

    public VoiceTranscriptionProvider getVoiceTranscriptionProvider() {
        return voiceTranscriptionProvider;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public AuditLogManager getAuditLogManager() {
        return auditLogManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}
