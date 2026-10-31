import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

class SoundManager {
    enum SoundEffect {
        START,
        PELLET,
        POWER_PELLET,
        BONUS,
        GHOST_EATEN,
        LIFE_LOST,
        GAME_OVER,
        LEVEL_COMPLETE
    }

    private static final float SAMPLE_RATE = 22050f;
    private static final AudioFormat AUDIO_FORMAT =
            new AudioFormat(SAMPLE_RATE, 8, 1, true, false);

    private final Map<SoundEffect, Clip> effectClips = new EnumMap<>(SoundEffect.class);
    private final boolean audioOutputEnabled;
    private boolean audioAvailable;
    private boolean muted = false;
    private boolean backgroundRequested = false;
    private Clip backgroundClip;

    SoundEffect lastRequestedEffect;
    int effectRequestCount = 0;

    SoundManager() {
        this(true);
    }

    SoundManager(boolean audioOutputEnabled) {
        this.audioOutputEnabled = audioOutputEnabled;
        this.audioAvailable = audioOutputEnabled;
    }

    void playEffect(SoundEffect effect) {
        lastRequestedEffect = effect;
        effectRequestCount += 1;
        if (muted || !audioAvailable) {
            return;
        }

        Clip clip = effectClips.get(effect);
        if (clip == null) {
            clip = createClip(createEffectData(effect));
            if (clip == null) {
                return;
            }
            effectClips.put(effect, clip);
        }
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    void setBackgroundPlaying(boolean playing) {
        backgroundRequested = playing;
        if (!playing || muted) {
            stopBackgroundOutput();
            return;
        }
        startBackgroundOutput();
    }

    void toggleMute() {
        muted = !muted;
        if (muted) {
            stopAllOutput();
        }
        else if (backgroundRequested) {
            startBackgroundOutput();
        }
    }

    boolean isMuted() {
        return muted;
    }

    boolean isBackgroundRequested() {
        return backgroundRequested;
    }

    boolean isAudioAvailable() {
        return audioAvailable;
    }

    private void startBackgroundOutput() {
        if (!audioAvailable) {
            return;
        }
        if (backgroundClip == null) {
            backgroundClip = createClip(createBackgroundData());
        }
        if (backgroundClip != null && !backgroundClip.isRunning()) {
            backgroundClip.setFramePosition(0);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void stopBackgroundOutput() {
        if (backgroundClip != null) {
            backgroundClip.stop();
        }
    }

    private void stopAllOutput() {
        stopBackgroundOutput();
        for (Clip clip : effectClips.values()) {
            clip.stop();
        }
    }

    private Clip createClip(byte[] audioData) {
        if (!audioOutputEnabled || !audioAvailable) {
            return null;
        }
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AUDIO_FORMAT, audioData, 0, audioData.length);
            return clip;
        }
        catch (LineUnavailableException | IllegalArgumentException exception) {
            audioAvailable = false;
            stopAllOutput();
            return null;
        }
    }

    byte[] createEffectData(SoundEffect effect) {
        switch (effect) {
            case START:
                return sequence(tone(392, 90, 0.45), tone(523, 90, 0.45), tone(659, 150, 0.45));
            case PELLET:
                return tone(880, 35, 0.25);
            case POWER_PELLET:
                return sequence(tone(220, 120, 0.45), tone(330, 160, 0.45));
            case BONUS:
                return sequence(tone(659, 70, 0.4), tone(784, 70, 0.4), tone(988, 130, 0.4));
            case GHOST_EATEN:
                return sequence(tone(988, 60, 0.4), tone(1319, 120, 0.4));
            case LIFE_LOST:
                return sequence(tone(440, 120, 0.4), tone(330, 120, 0.4), tone(220, 220, 0.4));
            case GAME_OVER:
                return sequence(tone(330, 180, 0.4), tone(247, 180, 0.4), tone(165, 300, 0.4));
            case LEVEL_COMPLETE:
                return sequence(tone(523, 100, 0.4), tone(659, 100, 0.4),
                        tone(784, 100, 0.4), tone(1047, 240, 0.4));
            default:
                throw new IllegalArgumentException("Unsupported sound effect: " + effect);
        }
    }

    byte[] createBackgroundData() {
        return sequence(
                tone(220, 160, 0.16), tone(262, 160, 0.16),
                tone(330, 160, 0.16), tone(262, 160, 0.16),
                tone(247, 160, 0.16), tone(294, 160, 0.16),
                tone(370, 160, 0.16), tone(294, 160, 0.16)
        );
    }

    byte[] tone(double frequency, int durationMilliseconds, double volume) {
        int sampleCount = Math.max(1, Math.round(SAMPLE_RATE*durationMilliseconds/1000f));
        byte[] samples = new byte[sampleCount];
        int fadeSamples = Math.min(sampleCount/2, Math.round(SAMPLE_RATE*0.01f));
        for (int sample = 0; sample < sampleCount; sample++) {
            double envelope = 1.0;
            if (fadeSamples > 0 && sample < fadeSamples) {
                envelope = sample/(double) fadeSamples;
            }
            else if (fadeSamples > 0 && sample >= sampleCount - fadeSamples) {
                envelope = (sampleCount - sample - 1)/(double) fadeSamples;
            }
            double wave = Math.sin(2.0*Math.PI*frequency*sample/SAMPLE_RATE);
            samples[sample] = (byte) Math.round(wave*volume*envelope*127.0);
        }
        return samples;
    }

    private byte[] sequence(byte[]... parts) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte[] part : parts) {
            output.write(part, 0, part.length);
        }
        return output.toByteArray();
    }
}
