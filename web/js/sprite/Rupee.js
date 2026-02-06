import Sprite from './Sprite.js';
import ImageStore from '../core/ImageStore.js';

export default class Rupee extends Sprite {
    constructor() {
        super();

        this._DEFAULT_VALUE = 20;
        this._value = this._DEFAULT_VALUE;

        this._yellowRupeeImage = ImageStore.get().getImage('YELLOW_RUPEE');
        this._blueRupeeImage = ImageStore.get().getImage('BLUE_RUPEE');
        this._rupeeImage = this._yellowRupeeImage;
    }

    getValue() {
        return this._value;
    }

    draw(ctx) {
        ctx.drawImage(this._rupeeImage, this.loc.x, this.loc.y, this.size.width, this.size.height);
    }
}
