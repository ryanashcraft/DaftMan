import Tile from './Tile.js';

export default class Grass extends Tile {
    static bg = null;

    static _red = '#C83838';
    static _green = '#38C838';
    static _blue = '#38AEC8';
    static _yellow = '#C8AE38';

    constructor(aRow, aCol) {
        super(aRow, aCol);
    }

    static setBackgroundWithTime(seconds) {
        if (seconds % 4 === 0) {
            Grass.bg = Grass._red;
        } else if (seconds % 4 === 1) {
            Grass.bg = Grass._green;
        } else if (seconds % 4 === 2) {
            Grass.bg = Grass._blue;
        } else {
            Grass.bg = Grass._yellow;
        }
    }

    draw(ctx) {
        ctx.fillStyle = Grass.bg;
        ctx.fillRect(this.getCol() * Tile.size.width, this.getRow() * Tile.size.height, Tile.size.width, Tile.size.height);
    }
}
