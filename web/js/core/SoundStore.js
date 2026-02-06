export default class SoundStore {
    static _instance = null;

    static get() {
        if (!SoundStore._instance) {
            SoundStore._instance = new SoundStore();
        }
        return SoundStore._instance;
    }

    constructor() {
        this._sounds = new Map();
        this._muted = false;

        this._addSound('sounds/explode.wav', 'EXPLODE');
        this._addSound('sounds/fuse.wav', 'FUSE');
        this._addSound('sounds/hurt.wav', 'HURT');
        this._addSound('sounds/rupee-colected.wav', 'RUPEE_COLLECTED');
        this._addSound('sounds/heart.wav', 'HEART');
        this._addSound('sounds/speed-up.wav', 'SPEED_UP');
        this._addSound('sounds/kill.wav', 'KILL');
    }

    _addSound(path, name) {
        this._sounds.set(name, path);
    }

    playSound(name, loopCount, bpm, overlay) {
        if (this._muted) {
            return;
        }

        // If called with extra args, it was a MIDI play request - no-op in browser
        if (loopCount !== undefined) {
            return;
        }

        const path = this._sounds.get(name);
        if (!path) {
            return;
        }

        const audio = new Audio(path);
        audio.play().catch(() => {});
    }

    mute(toMute) {
        if (toMute !== undefined) {
            this._muted = toMute;
        } else {
            this._muted = !this._muted;
        }
    }

    isMuted() {
        return this._muted;
    }

    startSequencer() {
        // No-op: MIDI not supported in browser
    }

    stopSequencer() {
        // No-op: MIDI not supported in browser
    }

    stopSoundClips() {
        // No-op in browser
    }

    closeSequencer() {
        // No-op: MIDI not supported in browser
    }

    closeSoundClips() {
        // No-op in browser
    }

    getSound(name) {
        return this._sounds.get(name);
    }
}
