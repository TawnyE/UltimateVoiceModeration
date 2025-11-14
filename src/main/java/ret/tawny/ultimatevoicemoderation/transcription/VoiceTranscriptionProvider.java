package ret.tawny.ultimatevoicemoderation.transcription;

import java.util.concurrent.CompletableFuture;

public interface VoiceTranscriptionProvider {

    CompletableFuture<String> transcribe(byte[] audioData);

}