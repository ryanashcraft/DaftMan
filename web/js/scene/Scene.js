export default class Scene {
    constructor(container) {
        this._width = container.getDimension().width;
        this._height = container.getDimension().height;
        this._cycleCount = 0;
    }

    getCycleCount() {
        return this._cycleCount;
    }

    update() {
        this._cycleCount++;
    }

    start() {}

    resume(lastScene) {}

    keyPressed(e) {}

    keyReleased(e) {}

    keyTyped(e) {}

    mouseReleased(e) {}

    draw(ctx) {}
}
