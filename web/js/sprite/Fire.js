import Sprite from './Sprite.js';
import ImageStore from '../core/ImageStore.js';
import SceneDirector from '../scene/SceneDirector.js';

export default class Fire extends Sprite {
    constructor(aDelegate) {
        super();

        this.delegate = aDelegate;
        this.STEP_SPEED_MULTIPLIER = 0.1;
        this._fireImages = ImageStore.get().getAnimation('FIRE');
    }

    draw(ctx) {
        ctx.drawImage(
            this._fireImages[Math.floor(this.stepCount * this.STEP_SPEED_MULTIPLIER) % 2],
            this.loc.x,
            this.loc.y,
            this.size.width,
            this.size.height
        );
    }

    act() {
        this.stepCount++;

        if (SceneDirector.get().secondsToCycles(1) === this.stepCount) {
            this.delegate.stopFire(this);
        }
    }
}
