import Tile from './Tile.js';
import Wall from './Wall.js';
import ImageStore from '../core/ImageStore.js';

export default class Brick extends Wall {
    constructor(aRow, aCol, aPrize) {
        super(aRow, aCol);

        this._brickImage = ImageStore.get().getImage('BRICK');
        this._prize = aPrize;
    }

    draw(ctx) {
        ctx.drawImage(this._brickImage, this.getCol() * Tile.size.width, this.getRow() * Tile.size.height, this.getSize().width, this.getSize().height);
    }

    isDestructible() {
        return true;
    }

    getPrize() {
        return this._prize;
    }
}
