import { Sequencer, WorkletSynthesizer } from '../lib/spessasynth_lib/index.js';

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

        this._midiFiles = new Map();
        this._midiFiles.set('AROUND_THE_WORLD', 'sounds/aroundtheworld.mid');
        this._midiFiles.set('DA_FUNK', 'sounds/dafunk.mid');
        this._midiFiles.set('STRONGER', 'sounds/stronger.mid');

        this._audioContext = null;
        this._synth = null;
        this._sequencer = null;
        this._currentSong = null;
        this._midiInitPromise = null;
        this._playRequestId = 0;
        this._pendingPlay = null;

        // On first user gesture, resume AudioContext and play any pending MIDI
        const gestureHandler = () => {
            if (this._audioContext?.state === 'suspended') {
                this._audioContext.resume();
            }
            if (this._pendingPlay) {
                const { name, loopCount, bpm, overlay } = this._pendingPlay;
                this._pendingPlay = null;
                this.playSound(name, loopCount, bpm, overlay);
            }
        };
        document.addEventListener('keydown', gestureHandler);
        document.addEventListener('click', gestureHandler);
    }

    _addSound(path, name) {
        this._sounds.set(name, path);
    }

    async _initMidi() {
        if (this._midiInitPromise) {
            return this._midiInitPromise;
        }
        this._midiInitPromise = this._doInitMidi();
        return this._midiInitPromise;
    }

    async _doInitMidi() {
        this._audioContext = new AudioContext();
        await this._audioContext.audioWorklet.addModule('js/lib/spessasynth_lib/spessasynth_processor.min.js');
        this._synth = new WorkletSynthesizer(this._audioContext);
        this._synth.connect(this._audioContext.destination);

        const sfResponse = await fetch('sounds/soundfont.sf3');
        const sfBuffer = await sfResponse.arrayBuffer();
        await this._synth.soundBankManager.addSoundBank(sfBuffer, 'main');
        await this._synth.isReady;

        this._sequencer = new Sequencer(this._synth);
    }

    async playSound(name, loopCount, _bpm, overlay) {
        if (this._muted) {
            return;
        }

        // WAV sound effect path (no extra args)
        if (loopCount === undefined) {
            const path = this._sounds.get(name);
            if (!path) {
                return;
            }
            const audio = new Audio(path);
            audio.play().catch(() => {});
            return;
        }

        // MIDI playback path
        const midiPath = this._midiFiles.get(name);
        if (!midiPath) {
            return;
        }

        // If AudioContext hasn't been created yet, or is suspended with no
        // user gesture yet, store this as a pending request to be triggered
        // on first user interaction.
        if (!this._audioContext || this._audioContext.state === 'suspended') {
            this._pendingPlay = { name, loopCount, bpm: _bpm, overlay };
            // Kick off init so it's ready when the user interacts
            this._initMidi().catch(() => {});
            // If the context already exists and is just suspended, try resuming
            // (will only work if called from a user gesture context)
            if (this._audioContext?.state === 'suspended') {
                try {
                    await this._audioContext.resume();
                } catch {
                    return;
                }
                // If resume succeeded (we're in a gesture context), clear pending and continue
                if (this._audioContext.state === 'running') {
                    this._pendingPlay = null;
                } else {
                    return;
                }
            } else {
                return;
            }
        }

        // Assign a request ID so stale async calls are discarded
        const requestId = ++this._playRequestId;

        try {
            await this._initMidi();
            if (requestId !== this._playRequestId) return;

            if (!overlay) {
                this._sequencer.pause();
            }

            const midiResponse = await fetch(midiPath);
            if (requestId !== this._playRequestId) return;
            const midiBuffer = await midiResponse.arrayBuffer();
            if (requestId !== this._playRequestId) return;

            this._sequencer.loadNewSongList([{ binary: midiBuffer }]);

            if (loopCount === -1) {
                this._sequencer.loopCount = Infinity;
            } else {
                this._sequencer.loopCount = loopCount;
            }

            // The original Java code had a bug where setTempoInBPM() was called
            // before setSequence() (which resets it), then startSequencer()
            // always hardcoded 120 BPM. The bpm parameter was effectively dead
            // code — all songs played at their native MIDI tempo. We match that.
            this._sequencer.playbackRate = 1;

            this._sequencer.play();
            this._currentSong = name;
        } catch (e) {
            console.warn('MIDI playback failed:', e);
        }
    }

    mute(toMute) {
        if (toMute !== undefined) {
            this._muted = toMute;
        } else {
            this._muted = !this._muted;
        }

        if (this._sequencer) {
            if (this._muted) {
                this._sequencer.pause();
            } else if (this._currentSong) {
                if (this._audioContext?.state === 'suspended') {
                    this._audioContext.resume();
                }
                this._sequencer.play();
            }
        }
    }

    isMuted() {
        return this._muted;
    }

    startSequencer() {
        // Pre-initialize the MIDI subsystem so it's ready when playSound is called
        this._initMidi().catch(() => {});
    }

    stopSequencer() {
        this._playRequestId++;
        this._pendingPlay = null;
        if (this._sequencer) {
            this._sequencer.pause();
        }
        this._currentSong = null;
    }

    stopSoundClips() {
        // No-op in browser
    }

    closeSequencer() {
        this._playRequestId++;
        this._pendingPlay = null;
        if (this._sequencer) {
            this._sequencer.pause();
            this._sequencer = null;
        }
        if (this._synth) {
            this._synth.destroy();
            this._synth = null;
        }
        if (this._audioContext) {
            this._audioContext.close();
            this._audioContext = null;
        }
        this._midiInitPromise = null;
        this._currentSong = null;
    }

    closeSoundClips() {
        // No-op in browser
    }

    getSound(name) {
        return this._sounds.get(name);
    }
}
