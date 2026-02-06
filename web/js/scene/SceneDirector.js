import Container from '../core/Container.js';

export default class SceneDirector {
    static _instance = null;
    static REPAINT_DELAY = 30;
    static UPDATE_DELAY = 15;

    static get() {
        if (!SceneDirector._instance) {
            SceneDirector._instance = new SceneDirector();
        }
        return SceneDirector._instance;
    }

    constructor() {
        this._scenes = [];
        this._container = new Container({ width: 16 * 32, height: 100 + 12 * 32 });
        this._canvas = null;
        this._ctx = null;
        this._lastUpdateTime = 0;
        this._updateAccumulator = 0;
        this._running = false;
    }

    setCanvas(canvas) {
        this._canvas = canvas;
        this._ctx = canvas.getContext('2d');

        canvas.addEventListener('keydown', (e) => this._onKeyDown(e));
        canvas.addEventListener('keyup', (e) => this._onKeyUp(e));
        canvas.addEventListener('click', (e) => this._onClick(e));

        canvas.setAttribute('tabindex', '0');
        canvas.focus();
    }

    getCanvas() {
        return this._canvas;
    }

    getContext() {
        return this._ctx;
    }

    getContainer() {
        return this._container;
    }

    secondsToCycles(seconds) {
        return Math.ceil(seconds * 1000 / SceneDirector.UPDATE_DELAY);
    }

    pushScene(scene) {
        this._scenes.push(scene);
        scene.start();
    }

    popScene() {
        if (this._scenes.length > 0) {
            const oldScene = this._scenes.pop();
            if (this._scenes.length > 0) {
                this._scenes[this._scenes.length - 1].resume(oldScene);
            }
        }
    }

    popToRootScene() {
        if (this._scenes.length === 0) {
            return;
        }

        const root = this._scenes[0];
        root.resume(null);
        this._scenes = [root];
    }

    startLoop() {
        if (this._running) return;
        this._running = true;
        this._lastUpdateTime = performance.now();
        this._updateAccumulator = 0;
        this._loop(performance.now());
    }

    _loop(now) {
        if (!this._running) return;

        const delta = now - this._lastUpdateTime;
        this._lastUpdateTime = now;
        this._updateAccumulator += delta;

        while (this._updateAccumulator >= SceneDirector.UPDATE_DELAY) {
            this._updateAccumulator -= SceneDirector.UPDATE_DELAY;
            if (this._scenes.length > 0) {
                this._scenes[this._scenes.length - 1].update();
            }
        }

        this._repaint();
        requestAnimationFrame((t) => this._loop(t));
    }

    _repaint() {
        if (this._scenes.length > 0 && this._ctx) {
            const scene = this._scenes[this._scenes.length - 1];
            scene.draw(this._ctx);
        }
    }

    _onKeyDown(e) {
        // Prevent browser scrolling on arrow keys and spacebar
        if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'Space'].includes(e.code)) {
            e.preventDefault();
        }
        if (this._scenes.length > 0) {
            this._scenes[this._scenes.length - 1].keyPressed(e);
        }
    }

    _onKeyUp(e) {
        if (this._scenes.length > 0) {
            this._scenes[this._scenes.length - 1].keyReleased(e);
        }
    }

    _onClick(e) {
        this._canvas.focus();
        if (this._scenes.length > 0) {
            this._scenes[this._scenes.length - 1].mouseReleased(e);
        }
    }
}
