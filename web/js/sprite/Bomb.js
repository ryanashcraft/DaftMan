import Sprite from './Sprite.js';
import ImageStore from '../core/ImageStore.js';
import SceneDirector from '../scene/SceneDirector.js';

export default class Bomb extends Sprite {
    constructor(aDelegate) {
        super();

        this.delegate = aDelegate;
        this.STEP_SPEED_MULTIPLIER = 0.05;
        this._bombImages = ImageStore.get().getAnimation('BOMB');
    }

    draw(ctx) {
        ctx.drawImage(
            this._bombImages[Math.floor(this.stepCount * this.STEP_SPEED_MULTIPLIER) % 2],
            this.loc.x,
            this.loc.y,
            this.size.width,
            this.size.height
        );
    }

    explode() {
        this.delegate.didExplode();
    }

    act() {
        this.stepCount++;

        if (SceneDirector.get().secondsToCycles(3) === this.stepCount) {
            this.explode();
        }
    }
}
