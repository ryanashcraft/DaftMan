import Sprite from './Sprite.js';
import ImageStore from '../core/ImageStore.js';

export default class Star extends Sprite {
    constructor() {
        super();

        this._starImage = ImageStore.get().getImage('STAR');
    }

    draw(ctx) {
        ctx.drawImage(this._starImage, this.loc.x, this.loc.y, this.size.width, this.size.height);
    }
}
