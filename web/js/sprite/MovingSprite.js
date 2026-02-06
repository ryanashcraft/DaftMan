import Sprite from './Sprite.js';
import { SpriteDirection } from './SpriteDirection.js';
import SceneDirector from '../scene/SceneDirector.js';

export default class MovingSprite extends Sprite {
    constructor(aDelegate, health, moveDistance) {
        super();

        this.delegate = aDelegate;
        this._health = health;
        this._moveDistance = moveDistance;
        this._distanceToMove = { x: 0, y: 0 };
        this._direction = SpriteDirection.STOP;
        this._immunity = 0;
        this._pauseTime = 0;
    }

    moveUp() {
        this._distanceToMove = { x: 0, y: -this._moveDistance };
        this._direction = SpriteDirection.UP;
    }

    moveDown() {
        this._distanceToMove = { x: 0, y: this._moveDistance };
        this._direction = SpriteDirection.DOWN;
    }

    moveLeft() {
        this._distanceToMove = { x: -this._moveDistance, y: 0 };
        this._direction = SpriteDirection.LEFT;
    }

    moveRight() {
        this._distanceToMove = { x: this._moveDistance, y: 0 };
        this._direction = SpriteDirection.RIGHT;
    }

    stopMoving() {
        this._distanceToMove = { x: 0, y: 0 };
        this._direction = SpriteDirection.STOP;
    }

    isPaused() {
        return this._pauseTime !== 0;
    }

    setPauseTime(pauseTime) {
        this._pauseTime = pauseTime;
    }

    move(direction) {
        if (direction === SpriteDirection.DOWN) {
            this.moveDown();
        } else if (direction === SpriteDirection.UP) {
            this.moveUp();
        } else if (direction === SpriteDirection.RIGHT) {
            this.moveRight();
        } else if (direction === SpriteDirection.LEFT) {
            this.moveLeft();
        } else if (direction === SpriteDirection.STOP) {
            this.stopMoving();
        }
    }

    act() {
        if (this.isPaused()) {
            --this._pauseTime;
            return;
        }

        if (this._health <= 0) {
            return;
        }

        if (this.getImmunity() > 0) {
            --this._immunity;
        }

        const newPoint = {
            x: this.loc.x + this._distanceToMove.x,
            y: this.loc.y + this._distanceToMove.y
        };
        const autoCorrectedPoint = this.delegate.autoCorrectedPoint(newPoint, this);
        if (autoCorrectedPoint.x !== this.loc.x || autoCorrectedPoint.y !== this.loc.y || !this.delegate.isTileSafe(this.delegate.tileForPoint(autoCorrectedPoint))) {
            this.stepCount++;
        } else {
            this.stopMoving();
        }
        this.loc = autoCorrectedPoint;
    }

    stopMoveY() {
        this._distanceToMove = { x: this._distanceToMove.x, y: 0 };
    }

    stopMoveX() {
        this._distanceToMove = { x: 0, y: this._distanceToMove.y };
    }

    getDirection() {
        return this._direction;
    }

    getImmmunity() {
        return this._immunity;
    }

    getMoveDistance() {
        return this._moveDistance;
    }

    setMoveDistance(moveDistance) {
        this._moveDistance = moveDistance;
    }

    getHealth() {
        return this._health;
    }

    setHealth(aHealth) {
        this._health = aHealth;
    }

    hurt() {
        if (this._immunity <= 0) {
            this._health--;
            this._immunity = SceneDirector.get().secondsToCycles(1);
        }
    }

    heal(more) {
        this._health = this._health + more;
    }

    getImmunity() {
        return this._immunity;
    }

    setImmunity(immunity) {
        this._immunity = immunity;
    }

    getDistanceToMove() {
        return this._distanceToMove;
    }

    setDirection(direction) {
        this._direction = direction;
    }
}
