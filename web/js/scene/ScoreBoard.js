import DaftMan from '../core/DaftMan.js';
import ImageStore from '../core/ImageStore.js';

const OUTER_MARGIN = 16;

export default class ScoreBoard {
    constructor(loc, size) {
        this._loc = loc;
        this._size = size;

        this._smallHeartImage = ImageStore.get().getImage('SMALL_HEART');

        this._seconds = 0;
        this._health = 0;
        this._score = 0;
        this._rupeesLeft = 0;
        this._level = 0;
    }

    draw(ctx) {
        ctx.fillStyle = '#000';
        ctx.fillRect(this._loc.x, this._loc.y, this._size.width, this._size.height);

        ctx.font = '26px ArcadeClassic, monospace';
        ctx.fillStyle = '#fff';

        const levelString = DaftMan.addExtraSpaces(
            `Level ${String(this._level).padStart(2, '0')}`
        );
        const scoreString = DaftMan.addExtraSpaces(
            `Score ${String(this._score).padStart(2, '0')}`
        );
        const timeString = DaftMan.addExtraSpaces(
            `Time ${String(this._seconds).padStart(2, '0')}`
        );
        const rupeesLeftString = DaftMan.addExtraSpaces(
            `Rupees ${String(this._rupeesLeft).padStart(2, '0')}`
        );

        // Measure text for right-alignment
        const levelWidth = ctx.measureText(levelString).width;
        const timeWidth = ctx.measureText(timeString).width;
        const rupeesWidth = ctx.measureText(rupeesLeftString).width;
        const fontHeight = 26;

        ctx.textAlign = 'left';
        ctx.textBaseline = 'top';

        // Level centered at top
        ctx.fillText(
            levelString,
            this._loc.x + (this._size.width - levelWidth) / 2,
            this._loc.y + OUTER_MARGIN * 2
        );

        // Score left-aligned
        ctx.fillText(
            scoreString,
            this._loc.x + OUTER_MARGIN,
            this._loc.y + OUTER_MARGIN * 2
        );

        // Time right-aligned
        ctx.fillText(
            timeString,
            this._size.width + this._loc.x * 2 - OUTER_MARGIN - timeWidth,
            this._loc.y + OUTER_MARGIN * 2
        );

        // Rupees right-aligned below time
        ctx.fillText(
            rupeesLeftString,
            this._size.width + this._loc.x * 2 - OUTER_MARGIN - rupeesWidth,
            this._loc.y + OUTER_MARGIN * 2 + fontHeight
        );

        // Heart images for health (bottom-left)
        if (this._smallHeartImage) {
            const heartW = this._smallHeartImage.width;
            const heartH = this._smallHeartImage.height;
            for (let i = 0; i < this._health; i++) {
                ctx.drawImage(
                    this._smallHeartImage,
                    this._loc.x + OUTER_MARGIN + (heartW + 1) * i,
                    this._loc.y + this._size.height - OUTER_MARGIN - heartH,
                    heartW,
                    heartH
                );
            }
        }
    }

    setTime(seconds) {
        this._seconds = seconds;
    }

    setHealth(health) {
        this._health = health;
    }

    setScore(score) {
        this._score = score;
    }

    setRupeesLeft(rupeeCount) {
        this._rupeesLeft = rupeeCount;
    }

    setLevel(level) {
        this._level = level;
    }
}
