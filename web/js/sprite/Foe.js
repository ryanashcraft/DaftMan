import MovingSprite from './MovingSprite.js';
import ImageStore from '../core/ImageStore.js';
import DaftMan from '../core/DaftMan.js';
import { SpriteDirection } from './SpriteDirection.js';
import { aStarCompare, PriorityQueue } from '../search/AStarComparator.js';
import Path from '../search/Path.js';
import State from '../search/State.js';

const STEP_SPEED_MULTIPLIER = 0.1;

export default class Foe extends MovingSprite {
    constructor(delegate, heuristicDelegate) {
        super(delegate, 1, 1);

        this._heuristicDelegate = heuristicDelegate;
        this._path = null;
        this._persistence = 0;
        this._maxPersistence = 0;
    }

    draw(ctx) {
        let imageArr;
        if (this._path === null) {
            if (this.getDirection() === SpriteDirection.UP) {
                imageArr = ImageStore.get().getAnimation('FOE_UP');
            } else if (this.getDirection() === SpriteDirection.DOWN) {
                imageArr = ImageStore.get().getAnimation('FOE_DOWN');
            } else if (this.getDirection() === SpriteDirection.LEFT) {
                imageArr = ImageStore.get().getAnimation('FOE_LEFT');
            } else {
                imageArr = ImageStore.get().getAnimation('FOE_RIGHT');
            }
        } else {
            if (this.getDirection() === SpriteDirection.UP) {
                imageArr = ImageStore.get().getAnimation('FOE_FOLLOW_UP');
            } else if (this.getDirection() === SpriteDirection.DOWN) {
                imageArr = ImageStore.get().getAnimation('FOE_FOLLOW_DOWN');
            } else if (this.getDirection() === SpriteDirection.LEFT) {
                imageArr = ImageStore.get().getAnimation('FOE_FOLLOW_LEFT');
            } else {
                imageArr = ImageStore.get().getAnimation('FOE_FOLLOW_RIGHT');
            }
        }

        if (DaftMan.DEBUG && this._path !== null) {
            const states = this._path.getPathway();
            let point = null;

            ctx.strokeStyle = 'red';
            ctx.lineWidth = 5;

            for (const state of states) {
                if (point === null) {
                    point = state.getTile().getCenter();
                    continue;
                }

                ctx.beginPath();
                ctx.moveTo(point.x, point.y);
                const nextPoint = state.getTile().getCenter();
                ctx.lineTo(nextPoint.x, nextPoint.y);
                ctx.stroke();

                point = nextPoint;
            }
        }

        if (imageArr != null) {
            ctx.drawImage(
                imageArr[Math.floor(this.stepCount * STEP_SPEED_MULTIPLIER) % 3],
                this.loc.x, this.loc.y, this.size.width, this.size.height
            );
        }
    }

    act() {
        if (this.isPaused()) {
            super.act();
            return;
        }

        const seesBro = this.delegate.canSeeBro(this);
        if (seesBro) {
            this._persistence = this._maxPersistence;
        }

        if (seesBro || this._persistence > 0) {
            this._path = this.aStarSearch();
        }

        if (this._path !== null && (seesBro || this._persistence-- > 0)) {
            this.move(this._path.getPathway()[1].getDirection());
        } else if (this._path !== null || this.delegate.shouldChangeDirection(this) && Math.floor(Math.random() * 100) < 5 || this.getDirection() === SpriteDirection.STOP) {
            this._path = null;

            const s = new State(this.delegate.tileForPoint(this.getCenter()));
            const successors = this.delegate.getSuccessors(s, this);
            let direction = SpriteDirection.STOP;
            let randomSuccessor = null;
            while (randomSuccessor === null) {
                randomSuccessor = successors[Math.floor(Math.random() * successors.length)];
                if (this.delegate.isTileSafe(randomSuccessor.getTile())) {
                    direction = randomSuccessor.getDirection();
                }
            }

            if (direction === SpriteDirection.STOP) {
                this.stopMoving();
            } else {
                this.move(direction);
            }
        }

        super.act();
    }

    aStarSearch() {
        const visited = [];

        const priorityQueue = new PriorityQueue(aStarCompare);

        priorityQueue.add(new Path(new State(this.delegate.tileForPoint(this.getCenter()))));

        while (!priorityQueue.isEmpty()) {
            const currentPath = priorityQueue.remove();
            const currentState = currentPath.getLastState();

            if (currentState !== null && visited.indexOf(currentState.getTile()) === -1) {
                visited.push(currentState.getTile());

                if (this._heuristicDelegate.isGoalState(currentState)) {
                    if (currentPath.getPathway().length > 1) {
                        return currentPath;
                    }
                }

                const successors = this.delegate.getSuccessors(currentState, this);
                for (const successor of successors) {
                    successor.setWeight(successor.getWeight() + this._heuristicDelegate.heuristicForTile(successor.getTile()));
                    priorityQueue.add(Path.extend(currentPath, successor));
                }
            }
        }

        return null;
    }

    setMaxPersistence(maxPersistence) {
        this._maxPersistence = maxPersistence;
    }
}
