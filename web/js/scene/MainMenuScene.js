import Scene from './Scene.js';
import SceneDirector from './SceneDirector.js';
import SoundStore from '../core/SoundStore.js';
import DaftMan from '../core/DaftMan.js';
import GameScene from './GameScene.js';
import HighScoreView from './HighScoreView.js';

const RED = 'rgb(200, 56, 56)';
const GREEN = 'rgb(56, 200, 56)';
const BLUE = 'rgb(56, 174, 200)';
const YELLOW = 'rgb(200, 174, 56)';

export default class MainMenuScene extends Scene {
    constructor(container) {
        super(container);
        this._logoColor = RED;
    }

    start() {
        super.start();
    }

    update() {
        super.update();

        if (this.getCycleCount() <= 1) {
            // MIDI playSound is a no-op in browser; original played "AROUND_THE_WORLD"
            SoundStore.get().playSound('AROUND_THE_WORLD', -1, 120.0, false);
        }

        this._setLogoColor(this.getCycleCount());
    }

    _setLogoColor(stepCount) {
        const cycleDuration = SceneDirector.get().secondsToCycles(4);
        const phase = stepCount % cycleDuration;

        if (phase === 0) {
            this._logoColor = RED;
        } else if (phase === SceneDirector.get().secondsToCycles(1)) {
            this._logoColor = GREEN;
        } else if (phase === SceneDirector.get().secondsToCycles(2)) {
            this._logoColor = BLUE;
        } else if (phase === SceneDirector.get().secondsToCycles(3)) {
            this._logoColor = YELLOW;
        }
    }

    draw(ctx) {
        const w = this._width;
        const h = this._height;

        ctx.fillStyle = '#000';
        ctx.fillRect(0, 0, w, h);

        // Logo
        ctx.fillStyle = this._logoColor;
        ctx.font = '72px ArcadeClassic, monospace';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'top';
        ctx.fillText('DAFTMAN', w / 2, 25);

        // Menu items
        ctx.fillStyle = '#fff';
        ctx.font = '26px ArcadeClassic, monospace';

        ctx.fillText(DaftMan.addExtraSpaces('Press Enter To Play'), w / 2, 125);
        ctx.fillText(DaftMan.addExtraSpaces('Press H For High Scores'), w / 2, 155);

        // Credits at bottom
        ctx.textBaseline = 'bottom';
        ctx.fillText(DaftMan.addExtraSpaces('Tanner Smith'), w / 2, h - 25);
        ctx.fillText(DaftMan.addExtraSpaces('2010 2012 Ryan Ashcraft'), w / 2, h - 55);

        // Debug label
        if (DaftMan.DEBUG) {
            ctx.textAlign = 'left';
            ctx.textBaseline = 'top';
            ctx.fillText(DaftMan.addExtraSpaces('Debug'), 7, 0);
        }
    }

    keyPressed(e) {
        switch (e.code) {
            case 'Enter':
            case 'NumpadEnter':
                SceneDirector.get().pushScene(new GameScene(SceneDirector.get().getContainer()));
                break;
            case 'KeyH':
                SceneDirector.get().pushScene(new HighScoreView(SceneDirector.get().getContainer()));
                break;
            case 'KeyM':
                SoundStore.get().mute();
                break;
            case 'KeyB':
                if (!DaftMan.DEBUG) break;
                {
                    const level = new Array(11);
                    for (let r = 0; r < level.length; r++) {
                        if (r === 0 || r === level.length - 1) {
                            level[r] = 'rrrrrrrrrrrrrrr';
                        } else if (r === Math.floor(level.length / 2)) {
                            level[r] = 'rgggggg1ggggggr';
                        } else {
                            level[r] = 'rgggggggggggggr';
                        }
                    }
                    SceneDirector.get().pushScene(new GameScene(SceneDirector.get().getContainer(), level));
                }
                break;
            case 'KeyD':
                if (e.shiftKey) {
                    DaftMan.DEBUG = !DaftMan.DEBUG;
                }
                break;
        }
    }
}
