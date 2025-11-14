package ret.tawny.ultimatevoicemoderation.transcription;

import java.util.concurrent.CompletableFuture;

public class LocalTranscriptionProvider implements VoiceTranscriptionProvider {
    @Override
    public CompletableFuture<String> transcribe(byte[] audioData) {
        // In a real implementation, this would be a local STT engine.
        // For now, we'll just simulate a transcription.
        return CompletableFuture.completedFuture("this is a simulated transcription");
    }
}