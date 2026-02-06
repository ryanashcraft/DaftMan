import MovingSprite from './MovingSprite.js';
import ImageStore from '../core/ImageStore.js';
import SceneDirector from '../scene/SceneDirector.js';
import { SpriteDirection } from './SpriteDirection.js';

const STEP_SPEED_MULTIPLIER = 0.1;

export default class Bro extends MovingSprite {
    constructor(delegate) {
        super(delegate, 3, 1);
        this._boostSpeedStarStepCount = 0;
    }

    draw(ctx) {
        let imageArr;
        if (this.getDirection() === SpriteDirection.UP) {
            imageArr = ImageStore.get().getAnimation('BRO_UP');
        } else if (this.getDirection() === SpriteDirection.DOWN) {
            imageArr = ImageStore.get().getAnimation('BRO_DOWN');
        } else if (this.getDirection() === SpriteDirection.LEFT) {
            imageArr = ImageStore.get().getAnimation('BRO_LEFT');
        } else {
            imageArr = ImageStore.get().getAnimation('BRO_RIGHT');
        }

        if (this.getImmunity() === 0) {
            ctx.drawImage(
                imageArr[Math.floor(this.stepCount * STEP_SPEED_MULTIPLIER) % 3],
                this.loc.x, this.loc.y, this.size.width, this.size.height
            );
        } else {
            if (this.getImmunity() % 4 !== 0) {
                ctx.drawImage(
                    imageArr[Math.floor(this.stepCount * STEP_SPEED_MULTIPLIER) % 3],
                    this.loc.x, this.loc.y, this.size.width, this.size.height
                );
            }
        }
    }

    act() {
        super.act();

        if (this.getMoveDistance() > 1) {
            this._boostSpeedStarStepCount--;

            if (this._boostSpeedStarStepCount <= 0) {
                this.setMoveDistance(1);
            }
        }
    }

    boostSpeed() {
        this.setMoveDistance(2);
        this._boostSpeedStarStepCount = SceneDirector.get().secondsToCycles(14);
    }

    isSpedUp() {
        return this.getMoveDistance() > 1;
    }

    resetSpeed() {
        this.setMoveDistance(1);
    }
}
