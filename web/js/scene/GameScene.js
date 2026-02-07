import Scene from './Scene.js';
import SceneDirector from './SceneDirector.js';
import ScoreBoard from './ScoreBoard.js';
import EndScene from './EndScene.js';
import PauseScene from './PauseScene.js';
import DaftMan from '../core/DaftMan.js';
import SoundStore from '../core/SoundStore.js';
import Tile from '../tile/Tile.js';
import Wall from '../tile/Wall.js';
import Brick from '../tile/Brick.js';
import Grass from '../tile/Grass.js';
import Bro from '../sprite/Bro.js';
import Foe from '../sprite/Foe.js';
import Bomb from '../sprite/Bomb.js';
import Fire from '../sprite/Fire.js';
import Rupee from '../sprite/Rupee.js';
import Heart from '../sprite/Heart.js';
import Star from '../sprite/Star.js';
import { SpriteDirection, DIRECTIONS } from '../sprite/SpriteDirection.js';
import State from '../search/State.js';

const DEFAULT_SCORE = 0;
const DEFAULT_LEVEL = 1;
const DEFAULT_HEALTH = 3;
const HURT_PUNISHMENT_SCORE_VALUE = 0;
const HURT_FOE_PUNISHMENT_SCORE_VALUE = 5;
const TIME_TO_WIN = 120;
const LAST_STEPS = 20;

const BASE_NUMBER_OF_RUPEES = 10;
const ADD_NUMBER_OF_RUPEES_PER_LEVEL = 2;
const MAX_NUMBER_OF_RUPEES = 30;
const NUMBER_OF_HEARTS = 3;
const NUMBER_OF_STARS = 2;

const BASE_NUMBER_OF_FOES = 2;
const ADD_NUMBER_OF_FOES_PER_LEVEL = 1;

function rectsIntersect(r1, r2) {
    return r1.x < r2.x + r2.width &&
           r1.x + r1.width > r2.x &&
           r1.y < r2.y + r2.height &&
           r1.y + r1.height > r2.y;
}

function rectIntersection(r1, r2) {
    const x = Math.max(r1.x, r2.x);
    const y = Math.max(r1.y, r2.y);
    const w = Math.min(r1.x + r1.width, r2.x + r2.width) - x;
    const h = Math.min(r1.y + r1.height, r2.y + r2.height) - y;
    if (w <= 0 || h <= 0) {
        return { x: 0, y: 0, width: 0, height: 0 };
    }
    return { x, y, width: w, height: h };
}

export default class GameScene extends Scene {
    constructor(container, stringArray = null) {
        super(container);

        this._score = DEFAULT_SCORE;
        this._level = DEFAULT_LEVEL;

        this.SCOREBOARD_HEIGHT = 80;
        this.OFFSET_X = -16;
        this.OFFSET_Y = 0;
        this.MAX_BOMB_DISTANCE = 3;

        this._scoreBoard = new ScoreBoard(
            { x: -this.OFFSET_X, y: -this.SCOREBOARD_HEIGHT - this.OFFSET_Y },
            { width: container.getDimension().width + this.OFFSET_X, height: this.SCOREBOARD_HEIGHT }
        );

        this._tiles = [];
        for (let r = 0; r < 13; r++) {
            this._tiles[r] = [];
            for (let c = 0; c < 17; c++) {
                if (r === 0 || r === 12 || c === 0 || c === 16) {
                    this._tiles[r][c] = new Wall(r, c);
                } else {
                    this._tiles[r][c] = new Grass(r, c);
                }
            }
        }

        this._bro = new Bro(this);
        this._bro.setHealth(DEFAULT_HEALTH);

        this._foes = [];
        this._bomb = null;
        this._fires = [];
        this._rupees = [];
        this._hearts = [];
        this._stars = [];

        this._timeLeft = TIME_TO_WIN;
        this._lastStepsLeft = 0;
        this._gameOver = false;

        this._BASE_FOE_PERSISTENCE = SceneDirector.get().secondsToCycles(2);
        this._ADD_FOE_PERSISTENCE = SceneDirector.get().secondsToCycles(0.5);

        if (stringArray === null) {
            this.randomlyFill();
        } else {
            this.fillWithArray(stringArray);
        }

        this.update();
    }

    randomlyFill() {
        for (let r = 0; r < this._tiles.length; r++) {
            for (let c = 0; c < this._tiles[r].length; c++) {
                if (r % 2 === 0 && c % 2 === 0) {
                    this._tiles[r][c] = new Wall(r, c);
                }
            }
        }

        const rupeesToAdd = Math.min(BASE_NUMBER_OF_RUPEES + (this._level - 1) * ADD_NUMBER_OF_RUPEES_PER_LEVEL, MAX_NUMBER_OF_RUPEES);
        for (let i = 0; i < rupeesToAdd + NUMBER_OF_HEARTS + NUMBER_OF_STARS; i++) {
            let r = -1, c = -1;
            while ((r === -1 || c === -1) || this._tiles[r][c].isImpassable() || (r < 5 && c < 5)) {
                r = Math.floor(Math.random() * (this._tiles.length - 1));
                c = Math.floor(Math.random() * (this._tiles[this._tiles.length - 1].length - 1));
            }

            let aSprite;
            if (i < rupeesToAdd) {
                aSprite = new Rupee();
            } else if (i < rupeesToAdd + NUMBER_OF_HEARTS) {
                aSprite = new Heart();
            } else {
                aSprite = new Star();
            }

            this._tiles[r][c] = new Brick(r, c, aSprite);
        }

        this._bro.setLoc(this._tiles[1][1].getLoc());

        let foesToAdd = BASE_NUMBER_OF_FOES + (this._level - 1) * ADD_NUMBER_OF_FOES_PER_LEVEL;
        if (DaftMan.DEBUG) {
            foesToAdd = 0;
        }
        for (let i = 0; i < foesToAdd; i++) {
            const aFoe = new Foe(this, this);
            let aTile = null;

            const MAX_ATTEMPTS = 50;
            let attempts = 0;
            while ((attempts <= MAX_ATTEMPTS) && (aTile === null || aTile.isImpassable() || !this.adjacentTileMoveExists(aTile) || (aTile.getRow() < 5 && aTile.getCol() < 5))) {
                aTile = this._tiles[Math.floor(Math.random() * (this._tiles.length - 1))][Math.floor(Math.random() * (this._tiles[this._tiles.length - 1].length - 1))];
                attempts++;
            }
            aFoe.setLoc(aTile.getLoc());

            aFoe.setMaxPersistence(this._BASE_FOE_PERSISTENCE + (this._level - 1) * this._ADD_FOE_PERSISTENCE);

            this._foes.push(aFoe);
        }
    }

    fillWithArray(stringArray) {
        for (let r = 0; r < this._tiles.length - 1; r++) {
            for (let c = 0; c < this._tiles[r].length - 1; c++) {
                if (r === 0 || r === this._tiles.length - 1 || c === 0 || c === this._tiles[r].length - 1) {
                    // border walls already placed
                } else if (stringArray[r - 1].charAt(c - 1) === '1') {
                    this._bro.setLoc(this._tiles[r][c].getLoc());
                } else if (stringArray[r - 1].charAt(c - 1) === '2') {
                    const aFoe = new Foe(this, this);
                    aFoe.setLoc(this._tiles[r][c].getLoc());
                    this._foes.push(aFoe);
                } else if (stringArray[r - 1].charAt(c - 1) === 'w') {
                    // wall already placed
                } else if (stringArray[r - 1].charAt(c - 1) === 'g') {
                    this._tiles[r][c] = new Grass(r, c);
                } else if (stringArray[r - 1].charAt(c - 1) === 'h') {
                    this._tiles[r][c] = new Brick(r, c, new Heart());
                } else if (stringArray[r - 1].charAt(c - 1) === 'r') {
                    this._tiles[r][c] = new Brick(r, c, new Rupee());
                } else if (stringArray[r - 1].charAt(c - 1) === 's') {
                    this._tiles[r][c] = new Brick(r, c, new Star());
                }
            }
        }
    }

    draw(ctx) {
        ctx.save();
        ctx.translate(this.OFFSET_X, this.SCOREBOARD_HEIGHT + this.OFFSET_Y);

        this._scoreBoard.draw(ctx);

        for (let r = 0; r < this._tiles.length; r++) {
            for (let c = 0; c < this._tiles[r].length; c++) {
                this._tiles[r][c].draw(ctx);

                if (DaftMan.DEBUG && !this.isTileSafe(this._tiles[r][c])) {
                    ctx.fillStyle = '#FFB6C1';
                    ctx.fillRect(
                        this._tiles[r][c].getCol() * Tile.size.width,
                        this._tiles[r][c].getRow() * Tile.size.height,
                        Tile.size.width,
                        Tile.size.height
                    );
                }
            }
        }

        for (let i = this._rupees.length - 1; i >= 0; i--) {
            this._rupees[i].draw(ctx);
        }

        for (let i = this._hearts.length - 1; i >= 0; i--) {
            this._hearts[i].draw(ctx);
        }

        for (let i = this._stars.length - 1; i >= 0; i--) {
            this._stars[i].draw(ctx);
        }

        if (this._bomb !== null) {
            this._bomb.draw(ctx);
        }

        for (let i = this._foes.length - 1; i >= 0; i--) {
            this._foes[i].draw(ctx);
        }

        if (this._bro !== null) {
            this._bro.draw(ctx);
        }

        if (this._fires !== null) {
            for (let i = this._fires.length - 1; i >= 0; i--) {
                this._fires[i].draw(ctx);
            }
        }

        ctx.restore();
    }

    keyPressed(e) {
        switch (e.code) {
            case 'ArrowUp': this._bro.moveUp(); break;
            case 'ArrowDown': this._bro.moveDown(); break;
            case 'ArrowLeft': this._bro.moveLeft(); break;
            case 'ArrowRight': this._bro.moveRight(); break;
            case 'Space': this.placeBomb(); break;
            case 'KeyC': if (DaftMan.DEBUG) { this.cheat(); } break;
            case 'KeyM': SoundStore.get().mute(); break;
            case 'KeyQ': SceneDirector.get().popToRootScene(); break;
            case 'KeyP': SceneDirector.get().pushScene(new PauseScene(SceneDirector.get().getContainer())); break;
            case 'KeyH':
                if (!DaftMan.DEBUG) {
                    break;
                }
                this._bro.setHealth(10);
                break;
            case 'KeyD':
                if (e.shiftKey) {
                    DaftMan.DEBUG = !DaftMan.DEBUG;
                }
                break;
        }
    }

    keyReleased(e) {
        if (e.code === 'ArrowDown' || e.code === 'ArrowUp') {
            this._bro.stopMoveY();
        } else if (e.code === 'ArrowLeft' || e.code === 'ArrowRight') {
            this._bro.stopMoveX();
        }
    }

    mouseReleased(e) {
        if (DaftMan.DEBUG) {
            const rect = SceneDirector.get().getCanvas().getBoundingClientRect();
            const clickX = e.clientX - rect.left;
            const clickY = e.clientY - rect.top;
            const newFoe = new Foe(this, this);
            const tile = this.tileForPoint({ x: clickX - this.OFFSET_X, y: clickY - this.SCOREBOARD_HEIGHT - this.OFFSET_Y });
            if (tile) {
                newFoe.setLoc(tile.getLoc());
                newFoe.setMaxPersistence(this._BASE_FOE_PERSISTENCE + (this._level - 1) * this._ADD_FOE_PERSISTENCE);
                this._foes.push(newFoe);
            }
        }
    }

    start() {
        super.start();
        SoundStore.get().playSound('DA_FUNK', -1, 120.0, false);
    }

    update() {
        super.update();

        if (this._bro !== null) {
            this._bro.act();
        }

        for (let i = this._foes.length - 1; i >= 0; i--) {
            this._foes[i].act();
        }

        if (this._bomb !== null) {
            this._bomb.act();
        }

        for (let i = this._fires.length - 1; i >= 0; i--) {
            this._fires[i].act();
        }

        if (!DaftMan.DEBUG && this.getCycleCount() !== 0 && this.getCycleCount() % SceneDirector.get().secondsToCycles(1) === 0) {
            this._timeLeft--;
        }

        this._scoreBoard.setTime(this._timeLeft);
        this._scoreBoard.setHealth(this._bro.getHealth());
        this._scoreBoard.setScore(this._score);
        this._scoreBoard.setRupeesLeft(this.rupeesLeft());
        this._scoreBoard.setLevel(this._level);

        Grass.setBackgroundWithTime(Math.floor(this.getCycleCount() * SceneDirector.UPDATE_DELAY / 1000));

        if (this._bro !== null) {
            this.checkCollisions(this._bro);
        }

        for (let i = this._foes.length - 1; i >= 0; i--) {
            this.checkCollisions(this._foes[i]);
        }

        if (this.rupeesLeft() <= 0) {
            if (!this._gameOver) {
                this._gameOver = true;
                this._lastStepsLeft = LAST_STEPS;
            }

            if (this._gameOver) {
                if (this._lastStepsLeft <= 0) {
                    SceneDirector.get().pushScene(new EndScene(SceneDirector.get().getContainer(), this, true));
                    return;
                } else {
                    this._lastStepsLeft--;
                }
            }
        }

        if (this._timeLeft <= 0 || this._bro.getHealth() <= 0) {
            if (!this._gameOver) {
                this._gameOver = true;
                this._lastStepsLeft = LAST_STEPS;
            }

            if (this._gameOver) {
                if (this._lastStepsLeft <= 0) {
                    SceneDirector.get().pushScene(new EndScene(SceneDirector.get().getContainer(), this, false));
                    return;
                } else {
                    this._lastStepsLeft--;
                }
            }
        }
    }

    cheat() {
        SceneDirector.get().pushScene(new EndScene(SceneDirector.get().getContainer(), this, true));
    }

    resume(lastScene) {
        super.resume(lastScene);

        if (lastScene !== null && lastScene instanceof EndScene) {
            this._timeLeft = TIME_TO_WIN;
            this._level++;
            this._gameOver = false;

            this._foes = [];
            this._stars = [];
            this._hearts = [];
            this._fires = [];
            this._rupees = [];
            this._bomb = null;

            this._bro.setImmunity(0);
            this._bro.resetSpeed();
            this._bro.setDirection(SpriteDirection.STOP);

            this.randomlyFill();

            SoundStore.get().playSound('DA_FUNK', -1, 120.0, false);

            this.update();
        }
    }

    rupeesLeft() {
        let count = 0;

        for (let r = 0; r < this._tiles.length; r++) {
            for (let c = 0; c < this._tiles[r].length; c++) {
                if (this._tiles[r][c] instanceof Brick) {
                    const aBrick = this._tiles[r][c];
                    if (aBrick.getPrize() instanceof Rupee) {
                        count++;
                    }
                }
            }
        }

        return count + this._rupees.length;
    }

    canMoveToPoint(aPoint, sprite) {
        const spriteRect = { x: aPoint.x, y: aPoint.y, width: sprite.getSize().width, height: sprite.getSize().height };

        for (let r = 0; r < this._tiles.length; r++) {
            for (let c = 0; c < this._tiles[r].length; c++) {
                if (this._tiles[r][c].isImpassable()) {
                    const tileRect = {
                        x: this._tiles[r][c].getLoc().x,
                        y: this._tiles[r][c].getLoc().y,
                        width: this._tiles[r][c].getSize().width,
                        height: this._tiles[r][c].getSize().height
                    };
                    if (rectsIntersect(spriteRect, tileRect)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    static euclidieanDistance(a, b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    tileForPoint(aPoint) {
        const row = Math.floor(aPoint.y / Tile.size.height);
        const col = Math.floor(aPoint.x / Tile.size.width);

        if (row < this._tiles.length - 1 && col < this._tiles[row].length - 1) {
            return this._tiles[row][col];
        }

        return null;
    }

    shouldChangeDirection(aFoe) {
        const foeCenter = aFoe.getCenter();
        const aTile = this.tileForPoint(foeCenter);

        if (!this.isTileSafe(aTile)) {
            return true;
        }

        const tileHeadedFor = this._tileHeadedFor(aFoe);
        if (tileHeadedFor !== null && !this.isTileSafe(tileHeadedFor)) {
            return true;
        }

        const tileCenter = aTile.getCenter();
        if (tileCenter.x === foeCenter.x && tileCenter.y === foeCenter.y) {
            return this.adjacentTileMoveExists(aTile);
        }

        return false;
    }

    _tileHeadedFor(sprite) {
        const currentTile = this.tileForPoint(sprite.getCenter());
        switch (sprite.getDirection()) {
            case SpriteDirection.UP:
                return this._tiles[currentTile.getRow() - 1][currentTile.getCol()];
            case SpriteDirection.RIGHT:
                return this._tiles[currentTile.getRow()][currentTile.getCol() + 1];
            case SpriteDirection.DOWN:
                return this._tiles[currentTile.getRow() + 1][currentTile.getCol()];
            case SpriteDirection.LEFT:
                return this._tiles[currentTile.getRow()][currentTile.getCol() - 1];
        }

        return null;
    }

    autoCorrectedPoint(aPoint, sprite) {
        const oldPoint = sprite.getLoc();
        const spriteRect = { x: aPoint.x, y: aPoint.y, width: sprite.getSize().width, height: sprite.getSize().height };
        let correctedPoint = aPoint;
        let wall = null;

        let found = false;
        for (let r = 0; r < this._tiles.length && !found; r++) {
            for (let c = 0; c < this._tiles[r].length && !found; c++) {
                if (this._tiles[r][c].isImpassable()) {
                    const tileRect = {
                        x: this._tiles[r][c].getLoc().x,
                        y: this._tiles[r][c].getLoc().y,
                        width: this._tiles[r][c].getSize().width,
                        height: this._tiles[r][c].getSize().height
                    };
                    if (rectsIntersect(spriteRect, tileRect)) {
                        wall = this._tiles[r][c];
                        found = true;
                    }
                }
            }
        }

        if (wall === null) {
            return aPoint;
        }

        const wallRect = { x: wall.getLoc().x, y: wall.getLoc().y, width: wall.getSize().width, height: wall.getSize().height };

        const intersectRect = rectIntersection(spriteRect, wallRect);

        const maxIntersect = 15;

        if (correctedPoint.x !== oldPoint.x) {
            if (intersectRect.height > maxIntersect) {
                return oldPoint;
            }

            let sign = 1;
            if (spriteRect.y < wallRect.y) {
                sign *= -1;
            }

            correctedPoint = { x: correctedPoint.x, y: correctedPoint.y + sign * intersectRect.height };
        } else if (correctedPoint.y !== oldPoint.y) {
            if (intersectRect.width > maxIntersect) {
                return oldPoint;
            }

            let sign = 1;
            if (spriteRect.x < wallRect.x) {
                sign *= -1;
            }

            correctedPoint = { x: correctedPoint.x + sign * intersectRect.width, y: correctedPoint.y };
        }

        return correctedPoint;
    }

    placeBomb() {
        if (this._bomb === null) {
            this._bomb = new Bomb(this);
            this._bomb.setLoc(this.tileForPoint(this._bro.getCenter()).getLoc());
            SoundStore.get().playSound('FUSE');
        }
    }

    didExplode() {
        SoundStore.get().playSound('EXPLODE');

        const tile = this.tileForPoint(this._bomb.getCenter());

        // up
        for (let r = tile.getRow(); r >= 0 && r >= tile.getRow() - this.MAX_BOMB_DISTANCE + 1; r--) {
            if (this._tiles[r][tile.getCol()].isDestructible()) {
                if (this._tiles[r][tile.getCol()] instanceof Brick) {
                    const aBrick = this._tiles[r][tile.getCol()];
                    this.placePrizeOnTile(aBrick.getPrize(), aBrick);
                }

                this._tiles[r][tile.getCol()] = new Grass(r, tile.getCol());

                const aCloud = new Fire(this);
                aCloud.setLoc(this._tiles[r][tile.getCol()].getLoc());
                this._fires.push(aCloud);

                break;
            } else if (this._tiles[r][tile.getCol()].isImpassable()) {
                break;
            } else {
                const aCloud = new Fire(this);
                aCloud.setLoc(this._tiles[r][tile.getCol()].getLoc());
                this._fires.push(aCloud);
            }
        }

        // down
        for (let r = tile.getRow(); r < this._tiles.length && r < tile.getRow() + this.MAX_BOMB_DISTANCE; r++) {
            if (this._tiles[r][tile.getCol()].isDestructible()) {
                if (this._tiles[r][tile.getCol()] instanceof Brick) {
                    const aBrick = this._tiles[r][tile.getCol()];
                    this.placePrizeOnTile(aBrick.getPrize(), aBrick);
                }

                this._tiles[r][tile.getCol()] = new Grass(r, tile.getCol());

                const aCloud = new Fire(this);
                aCloud.setLoc(this._tiles[r][tile.getCol()].getLoc());
                this._fires.push(aCloud);

                break;
            } else if (this._tiles[r][tile.getCol()].isImpassable()) {
                break;
            } else {
                const aCloud = new Fire(this);
                aCloud.setLoc(this._tiles[r][tile.getCol()].getLoc());
                this._fires.push(aCloud);
            }
        }

        // left
        for (let c = tile.getCol(); c >= 0 && c >= tile.getCol() - this.MAX_BOMB_DISTANCE + 1; c--) {
            if (this._tiles[tile.getRow()][c].isDestructible()) {
                if (this._tiles[tile.getRow()][c] instanceof Brick) {
                    const aBrick = this._tiles[tile.getRow()][c];
                    this.placePrizeOnTile(aBrick.getPrize(), aBrick);
                }

                this._tiles[tile.getRow()][c] = new Grass(tile.getRow(), c);

                const aCloud = new Fire(this);
                aCloud.setLoc(this._tiles[tile.getRow()][c].getLoc());
                this._fires.push(aCloud);

                break;
            } else if (this._tiles[tile.getRow()][c].isImpassable()) {
                break;
            } else {
                const aCloud = new Fire(this);
                aCloud.setLoc(this._tiles[tile.getRow()][c].getLoc());
                this._fires.push(aCloud);
            }
        }

        // right
        for (let c = tile.getCol(); c < this._tiles[tile.getRow()].length && c < tile.getCol() + this.MAX_BOMB_DISTANCE; c++) {
            if (this._tiles[tile.getRow()][c].isDestructible()) {
                if (this._tiles[tile.getRow()][c] instanceof Brick) {
                    const aBrick = this._tiles[tile.getRow()][c];
                    this.placePrizeOnTile(aBrick.getPrize(), aBrick);
                }

                this._tiles[tile.getRow()][c] = new Grass(tile.getRow(), c);

                const aCloud = new Fire(this);
                aCloud.setLoc(this._tiles[tile.getRow()][c].getLoc());
                this._fires.push(aCloud);

                break;
            } else if (this._tiles[tile.getRow()][c].isImpassable()) {
                break;
            } else {
                const aCloud = new Fire(this);
                aCloud.setLoc(this._tiles[tile.getRow()][c].getLoc());
                this._fires.push(aCloud);
            }
        }

        this._bomb = null;
    }

    stopFire(aFire) {
        const index = this._fires.indexOf(aFire);
        if (index !== -1) {
            this._fires.splice(index, 1);
        }
    }

    checkCollisions(sprite) {
        const spriteRect = { x: sprite.getLoc().x, y: sprite.getLoc().y, width: sprite.getSize().width, height: sprite.getSize().height };

        for (let i = this._fires.length - 1; i >= 0; i--) {
            const aFire = this._fires[i];
            const fireRect = { x: aFire.getLoc().x, y: aFire.getLoc().y, width: aFire.getSize().width, height: aFire.getSize().height };

            if (rectsIntersect(fireRect, spriteRect)) {
                this.hurt(sprite);
                return;
            }
        }

        if (sprite === this._bro) {
            for (let i = this._rupees.length - 1; i >= 0; i--) {
                const aRupee = this._rupees[i];
                const rupeeRect = { x: aRupee.getLoc().x, y: aRupee.getLoc().y, width: aRupee.getSize().width, height: aRupee.getSize().height };

                if (rectsIntersect(rupeeRect, spriteRect)) {
                    this.collectRupee(aRupee);
                }
            }

            for (let i = this._hearts.length - 1; i >= 0; i--) {
                const aHeart = this._hearts[i];
                const heartRect = { x: aHeart.getLoc().x, y: aHeart.getLoc().y, width: aHeart.getSize().width, height: aHeart.getSize().height };

                if (rectsIntersect(heartRect, spriteRect)) {
                    this.collectHeart(aHeart);
                }
            }

            for (let i = this._stars.length - 1; i >= 0; i--) {
                const aStar = this._stars[i];
                const starRect = { x: aStar.getLoc().x, y: aStar.getLoc().y, width: aStar.getSize().width, height: aStar.getSize().height };

                if (rectsIntersect(starRect, spriteRect)) {
                    this.collectStar(aStar);
                }
            }

            for (let i = this._foes.length - 1; i >= 0; i--) {
                const aFoe = this._foes[i];
                const foeRect = { x: aFoe.getLoc().x, y: aFoe.getLoc().y, width: aFoe.getSize().width, height: aFoe.getSize().height };

                if (rectsIntersect(foeRect, spriteRect)) {
                    this.hurt(sprite);
                    aFoe.setPauseTime(SceneDirector.get().secondsToCycles(1));
                    return;
                }
            }
        }
    }

    hurt(sprite) {
        if (sprite.getImmunity() > 0) {
            return;
        }

        sprite.hurt();

        if (sprite === this._bro) {
            SoundStore.get().playSound('HURT');
            this.addToScore(HURT_PUNISHMENT_SCORE_VALUE);
        }

        if (sprite.getHealth() === 0) {
            const foeIndex = this._foes.indexOf(sprite);
            if (foeIndex !== -1) {
                this._foes.splice(foeIndex, 1);
                this._score += HURT_FOE_PUNISHMENT_SCORE_VALUE;
                this.addToScore(HURT_PUNISHMENT_SCORE_VALUE);
            }
        }
    }

    collectRupee(aRupee) {
        this.addToScore(aRupee.getValue());
        const index = this._rupees.indexOf(aRupee);
        if (index !== -1) {
            this._rupees.splice(index, 1);
        }

        SoundStore.get().playSound('RUPEE_COLLECTED');
    }

    collectHeart(aHeart) {
        this.addToHealth(aHeart.getValue());
        const index = this._hearts.indexOf(aHeart);
        if (index !== -1) {
            this._hearts.splice(index, 1);
        }

        SoundStore.get().playSound('HEART');
    }

    collectStar(aStar) {
        if (!this._bro.isSpedUp()) {
            this._bro.boostSpeed();
            const index = this._stars.indexOf(aStar);
            if (index !== -1) {
                this._stars.splice(index, 1);
            }

            SoundStore.get().playSound('SPEED_UP');
        }
    }

    addToScore(more) {
        this._score = this._score + more;
    }

    addToHealth(more) {
        this._bro.heal(more);
    }

    placePrizeOnTile(aSprite, aTile) {
        aSprite.setLoc(aTile.getLoc());

        if (aSprite instanceof Rupee) {
            this._rupees.push(aSprite);
        } else if (aSprite instanceof Heart) {
            this._hearts.push(aSprite);
        } else if (aSprite instanceof Star) {
            this._stars.push(aSprite);
        }
    }

    heuristicForTile(t) {
        return this.distanceFromBro(t.getCenter());
    }

    distanceFromBro(point) {
        return Math.ceil((Math.abs(point.x - this._bro.getLoc().x) / Tile.size.width + Math.abs(point.y - this._bro.getLoc().y)) / Tile.size.height);
    }

    canSeeBro(sprite) {
        for (let x = Math.min(sprite.getCenter().x, this._bro.getCenter().x); x <= Math.max(sprite.getCenter().x, this._bro.getCenter().x); x++) {
            for (let y = Math.min(sprite.getCenter().y, this._bro.getCenter().y); y <= Math.max(sprite.getCenter().y, this._bro.getCenter().y); y++) {
                const tile = this.tileForPoint({ x, y });
                if (tile && tile.isImpassable()) {
                    return false;
                }
            }
        }

        return true;
    }

    getSuccessors(state, sprite) {
        const successors = [];
        const t = state.getTile();
        const loc = t.getLoc();

        for (let i = 0; i < DIRECTIONS.length; i++) {
            sprite.move(DIRECTIONS[i]);

            const newPoint = {
                x: loc.x + sprite.getDistanceToMove().x * sprite.getSize().width,
                y: loc.y + sprite.getDistanceToMove().y * sprite.getSize().height
            };
            if (this.canMoveToPoint(newPoint, sprite)) {
                const tile = this.tileForPoint(newPoint);
                if (tile !== null) {
                    let cost = 1;

                    if (!this.isTileSafe(tile)) {
                        cost = Number.MAX_SAFE_INTEGER;
                    }

                    successors.push(new State(tile, DIRECTIONS[i], cost));
                }
            }
        }

        return successors;
    }

    isGoalState(currentState) {
        if (this._bomb !== null) {
            const broTile = this.tileForPoint(this._bro.getCenter());
            if (!this.isTileSafe(broTile)) {
                const adjTiles = this._adjacentTilesArray(currentState.getTile());
                for (let i = 0; i < adjTiles.length; i++) {
                    if (!this.isTileSafe(adjTiles[i])) {
                        return true;
                    }
                }
            }
        }

        const broTile = this.tileForPoint(this._bro.getCenter());
        return broTile !== null && currentState.getTile().equals(broTile);
    }

    adjacentTileMoveExists(aTile) {
        const tileRow = aTile.getRow();
        const tileCol = aTile.getCol();

        for (let r = Math.max(0, tileRow - 1); r <= Math.min(this._tiles.length - 1, tileRow + 1); r++) {
            if (this._tiles[r][tileCol] !== aTile && !this._tiles[r][tileCol].isImpassable() && this.isTileSafe(this._tiles[r][tileCol])) {
                return true;
            }
        }

        for (let c = Math.max(0, tileCol - 1); c <= Math.min(this._tiles[this._tiles.length - 1].length, tileCol + 1); c++) {
            if (this._tiles[tileRow][c] !== aTile && !this._tiles[tileRow][c].isImpassable() && this.isTileSafe(this._tiles[tileRow][c])) {
                return true;
            }
        }

        return false;
    }

    _adjacentTilesArray(aTile) {
        const tileRow = aTile.getRow();
        const tileCol = aTile.getCol();
        const result = [];

        for (let r = Math.max(0, tileRow - 1); r <= Math.min(this._tiles.length - 1, tileRow + 1); r++) {
            if (this._tiles[r][tileCol] !== aTile && !this._tiles[r][tileCol].isImpassable()) {
                result.push(this._tiles[r][tileCol]);
            }
        }

        for (let c = Math.max(0, tileCol - 1); c <= Math.min(this._tiles[this._tiles.length - 1].length, tileCol + 1); c++) {
            if (this._tiles[tileRow][c] !== aTile && !this._tiles[tileRow][c].isImpassable()) {
                result.push(this._tiles[tileRow][c]);
            }
        }

        return result;
    }

    distanceToTileDistance(distance) {
        return distance / ((Tile.size.height + Tile.size.width) / 2);
    }

    isTileSafe(tile) {
        if (tile.isImpassable()) {
            return true;
        }

        for (let i = 0; i < this._fires.length; i++) {
            const fireTile = this.tileForPoint(this._fires[i].getCenter());
            if (fireTile === tile) {
                return false;
            }
        }

        if (this._bomb !== null) {
            const bombCenter = this._bomb.getCenter();
            const tileCenter = tile.getCenter();

            // in same column
            if (tileCenter.x === bombCenter.x) {
                const bombTile = this.tileForPoint(bombCenter);
                if (Math.abs(tile.getRow() - bombTile.getRow()) < this.MAX_BOMB_DISTANCE) {
                    const bombRow = bombTile.getRow();
                    if (tile.getRow() < bombRow) {
                        for (let r = bombRow; r > tile.getRow(); --r) {
                            if (this._tiles[r][tile.getCol()].isImpassable()) {
                                return true;
                            }
                        }
                    } else if (tile.getRow() > bombRow) {
                        for (let r = bombRow; r <= tile.getRow(); r++) {
                            if (this._tiles[r][tile.getCol()].isImpassable()) {
                                return true;
                            }
                        }
                    }

                    return false;
                }
            } else if (tileCenter.y === bombCenter.y) {
                const bombTile = this.tileForPoint(bombCenter);
                if (Math.abs(tile.getCol() - bombTile.getCol()) < this.MAX_BOMB_DISTANCE) {
                    const bombCol = bombTile.getCol();
                    if (tile.getCol() < bombCol) {
                        for (let c = bombCol; c > tile.getCol(); --c) {
                            if (this._tiles[tile.getRow()][c].isImpassable()) {
                                return true;
                            }
                        }
                    } else if (tile.getCol() > bombCol) {
                        for (let c = bombCol; c <= tile.getCol(); c++) {
                            if (this._tiles[tile.getRow()][c].isImpassable()) {
                                return true;
                            }
                        }
                    }

                    return false;
                }
            }
        }

        return true;
    }

    getScore() {
        return this._score;
    }

    getTimeLeft() {
        return this._timeLeft;
    }

    getLevel() {
        return this._level;
    }

    setScore(totalScore) {
        this._score = totalScore;
    }
}
