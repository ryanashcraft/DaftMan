export default class Tile {
    static size = { width: 32, height: 32 };

    constructor(aRow, aCol) {
        this._row = aRow;
        this._col = aCol;
    }

    draw(ctx) {
        throw new Error('draw() must be implemented by subclass');
    }

    getSize() {
        return Tile.size;
    }

    getLoc() {
        return {
            x: this._col * Tile.size.width,
            y: this._row * Tile.size.height
        };
    }

    getCenter() {
        const loc = this.getLoc();
        return {
            x: loc.x + Tile.size.width / 2,
            y: loc.y + Tile.size.height / 2
        };
    }

    isImpassable() {
        return false;
    }

    isDestructible() {
        return false;
    }

    getRow() {
        return this._row;
    }

    getCol() {
        return this._col;
    }

    isAdjacent(t) {
        return Math.abs(this._row - t.getRow()) < 1 && Math.abs(this._col - t.getCol()) < 1;
    }

    equals(o) {
        if (o != null && o instanceof Tile) {
            const otherCenter = o.getCenter();
            const thisCenter = this.getCenter();
            return otherCenter.x === thisCenter.x && otherCenter.y === thisCenter.y;
        }
        return false;
    }

    toString() {
        return `Tile @ (${this._row}, ${this._col})`;
    }
}
