import Scene from './Scene.js';
import SceneDirector from './SceneDirector.js';
import SoundStore from '../core/SoundStore.js';
import DaftMan from '../core/DaftMan.js';
import HighScoreDataCollector from '../util/HighScoreDataCollector.js';

export default class HighScoreView extends Scene {
    constructor(container) {
        super(container);
    }

    draw(ctx) {
        const w = this._width;
        const h = this._height;

        ctx.fillStyle = '#000';
        ctx.fillRect(0, 0, w, h);

        // Title
        ctx.fillStyle = '#fff';
        ctx.font = '72px ArcadeClassic, monospace';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'top';
        ctx.fillText(DaftMan.addExtraSpaces('High Scores'), w / 2, 25);

        // Score entries
        ctx.font = '26px ArcadeClassic, monospace';

        const scores = HighScoreDataCollector.getInstance().getRecordScores();
        const holders = HighScoreDataCollector.getInstance().getRecordHolders();
        const startY = 125;

        for (let i = 0; i < scores.length; i++) {
            if (scores[i] === 0) continue;
            const text = DaftMan.addExtraSpaces(`${holders[i]} ${scores[i]}`);
            ctx.fillText(text, w / 2, startY + i * 30);
        }
    }

    keyPressed(e) {
        switch (e.code) {
            case 'KeyH':
                SceneDirector.get().popScene();
                break;
            case 'KeyM':
                SoundStore.get().mute();
                break;
        }
    }
}
