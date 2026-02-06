import Scene from './Scene.js';
import SceneDirector from './SceneDirector.js';

export default class PauseScene extends Scene {
    constructor(container) {
        super(container);
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
        ctx.fillText('Paused', w / 2, h / 2);
    }

    keyPressed(e) {
        super.keyPressed(e);

        if (e.code === 'KeyP') {
            SceneDirector.get().popScene();
        }
    }
}
