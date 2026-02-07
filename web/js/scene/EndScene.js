import Scene from './Scene.js';
import SceneDirector from './SceneDirector.js';
import SoundStore from '../core/SoundStore.js';
import DaftMan from '../core/DaftMan.js';
import HighScoreDataCollector from '../util/HighScoreDataCollector.js';

const MAX_NAME_LENGTH = 20;

export default class EndScene extends Scene {
    constructor(container, gameScene, won) {
        super(container);

        this._won = won;
        this._score = gameScene.getScore();
        this._totalScore = this._score + gameScene.getTimeLeft();
        gameScene.setScore(this._totalScore);
        this._timeBonus = gameScene.getTimeLeft();
        this._lastLevelPlayed = gameScene.getLevel();
        this._name = '';

        SoundStore.get().startSequencer();
        SoundStore.get().playSound('STRONGER', 0, 160.0, false);
    }

    update() {
        super.update();

        if (this._won && this.getCycleCount() % SceneDirector.get().secondsToCycles(10) === 0) {
            SceneDirector.get().popScene();
        }
    }

    draw(ctx) {
        const w = this._width;
        const h = this._height;

        ctx.fillStyle = '#000';
        ctx.fillRect(0, 0, w, h);

        ctx.fillStyle = '#fff';
        ctx.font = '26px ArcadeClassic, monospace';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';

        const fontSize = 26;
        const startY = h / 2 - fontSize * 2;

        let headerText;
        if (this._won) {
            const levelStr = String(this._lastLevelPlayed).padStart(2, '0');
            headerText = DaftMan.addExtraSpaces(`Completed Level ${levelStr}`);
        } else {
            headerText = DaftMan.addExtraSpaces('Game Over!');
        }

        ctx.fillText(headerText, w / 2, startY);
        ctx.fillText('Score    ' + this._score, w / 2, startY + fontSize);

        if (this._won) {
            ctx.fillText(DaftMan.addExtraSpaces('Time Bonus ' + this._timeBonus), w / 2, startY + fontSize * 2);
            ctx.fillText(DaftMan.addExtraSpaces('Total ' + this._totalScore), w / 2, startY + fontSize * 3);
        } else {
            ctx.fillText(DaftMan.addExtraSpaces('Enter Name And Press Enter'), w / 2, startY + fontSize * 2);
            ctx.fillText(DaftMan.addExtraSpaces(this._name), w / 2, startY + fontSize * 3);
        }
    }

    keyPressed(e) {
        if (this._won) {
            return;
        }

        const key = e.key;

        if (this._name.length < MAX_NAME_LENGTH && key.length === 1 && /[a-zA-Z]/.test(key)) {
            this._name = this._name + key;
        } else if (this._name.length < MAX_NAME_LENGTH && this._name.length > 1 && e.code === 'Space') {
            this._name = this._name + ' ';
        } else if (this._name.length > 0 && e.code === 'Backspace') {
            this._name = this._name.substring(0, this._name.length - 1);
        } else if (this._name.length > 0 && (e.code === 'Enter' || e.code === 'NumpadEnter')) {
            HighScoreDataCollector.getInstance().recordScore(this._totalScore, this._name.trim());
            SceneDirector.get().popToRootScene();
        }
    }
}
