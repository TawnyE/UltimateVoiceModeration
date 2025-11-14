package ret.tawny.ultimatevoicemoderation.transcription;

import org.vosk.Model;
import org.vosk.Recognizer;
import ret.tawny.ultimatevoicemoderation.UltimateVoiceModerationPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class LocalTranscriptionProvider implements VoiceTranscriptionProvider {

    private final UltimateVoiceModerationPlugin plugin;
    private Model model;

    public LocalTranscriptionProvider(UltimateVoiceModerationPlugin plugin) {
        this.plugin = plugin;
        loadModel();
    }

    private void loadModel() {
        File modelDir = new File(plugin.getDataFolder(), "vosk-model");
        if (!modelDir.exists() || !modelDir.isDirectory()) {
            plugin.getLogger().warning("Vosk model not found. Please download a model and place it in the 'vosk-model' directory.");
            return;
        }
        try {
            model = new Model(modelDir.getAbsolutePath());
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load Vosk model: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<String> transcribe(byte[] audioData) {
        if (model == null) {
            return CompletableFuture.completedFuture("");
        }
        return CompletableFuture.supplyAsync(() -> {
            try (Recognizer recognizer = new Recognizer(model, 16000f)) {
                recognizer.acceptWaveForm(audioData, audioData.length);
                return recognizer.getFinalResult();
            } catch (Exception e) {
                plugin.getLogger().warning("Error during transcription: " + e.getMessage());
                return "";
            }
        });
    }
}
