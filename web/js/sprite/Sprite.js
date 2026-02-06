export default class Sprite {
    constructor() {
        this.loc = null;
        this.size = { width: 32, height: 32 };
        this.stepCount = 0;
    }

    draw(ctx) {
        throw new Error('draw() must be implemented by subclass');
    }

    getLoc() {
        return this.loc;
    }

    setLoc(aPoint) {
        this.loc = aPoint;
    }

    getCenter() {
        return {
            x: this.loc.x + this.size.width / 2,
            y: this.loc.y + this.size.height / 2
        };
    }

    getSize() {
        return this.size;
    }
}
