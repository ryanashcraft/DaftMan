import Sprite from './Sprite.js';
import ImageStore from '../core/ImageStore.js';

export default class Heart extends Sprite {
    constructor() {
        super();

        this._value = 1;
        this._heartImage = ImageStore.get().getImage('HEART');
    }

    getValue() {
        return this._value;
    }

    draw(ctx) {
        ctx.drawImage(this._heartImage, this.loc.x, this.loc.y, this.size.width, this.size.height);
    }
}
