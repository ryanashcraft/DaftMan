import Tile from './Tile.js';
import ImageStore from '../core/ImageStore.js';

export default class Wall extends Tile {
    constructor(aRow, aCol) {
        super(aRow, aCol);

        this._wallImage = ImageStore.get().getImage('WALL');
    }

    draw(ctx) {
        ctx.drawImage(this._wallImage, this.getCol() * Tile.size.width, this.getRow() * Tile.size.height, this.getSize().width, this.getSize().height);
    }

    isImpassable() {
        return true;
    }
}
