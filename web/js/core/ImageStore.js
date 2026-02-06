export default class ImageStore {
    static _instance = null;

    static get() {
        if (!ImageStore._instance) {
            ImageStore._instance = new ImageStore();
        }
        return ImageStore._instance;
    }

    constructor() {
        this._images = new Map();
        this._animations = new Map();
    }

    initialize() {
        const promises = [];

        const addImage = (path, name) => {
            const img = new Image();
            const p = new Promise((resolve, reject) => {
                img.onload = resolve;
                img.onerror = () => reject(new Error(`Failed to load image: ${path}`));
            });
            img.src = path;
            this._images.set(name, img);
            promises.push(p);
        };

        const addAnimation = (pathPattern, name, count) => {
            const frames = [];
            for (let i = 1; i <= count; i++) {
                const img = new Image();
                const path = pathPattern.replace('%d', i);
                const p = new Promise((resolve, reject) => {
                    img.onload = resolve;
                    img.onerror = () => reject(new Error(`Failed to load image: ${path}`));
                });
                img.src = path;
                frames.push(img);
                promises.push(p);
            }
            this._animations.set(name, frames);
        };

        addImage('images/wall.png', 'WALL');
        addImage('images/brick.png', 'BRICK');
        addImage('images/rupee-yellow.png', 'YELLOW_RUPEE');
        addImage('images/rupee-blue.png', 'BLUE_RUPEE');
        addImage('images/star.png', 'STAR');
        addImage('images/heart.png', 'HEART');
        addImage('images/heart-small.png', 'SMALL_HEART');

        addAnimation('images/bro-up-%d.png', 'BRO_UP', 3);
        addAnimation('images/bro-down-%d.png', 'BRO_DOWN', 3);
        addAnimation('images/bro-left-%d.png', 'BRO_LEFT', 3);
        addAnimation('images/bro-right-%d.png', 'BRO_RIGHT', 3);

        addAnimation('images/foe-up-%d.png', 'FOE_UP', 3);
        addAnimation('images/foe-down-%d.png', 'FOE_DOWN', 3);
        addAnimation('images/foe-left-%d.png', 'FOE_LEFT', 3);
        addAnimation('images/foe-right-%d.png', 'FOE_RIGHT', 3);

        // No separate foe-follow images exist; reuse regular foe images
        addAnimation('images/foe-up-%d.png', 'FOE_FOLLOW_UP', 3);
        addAnimation('images/foe-down-%d.png', 'FOE_FOLLOW_DOWN', 3);
        addAnimation('images/foe-left-%d.png', 'FOE_FOLLOW_LEFT', 3);
        addAnimation('images/foe-right-%d.png', 'FOE_FOLLOW_RIGHT', 3);

        addAnimation('images/bomb-%d.png', 'BOMB', 2);
        addAnimation('images/fire-%d.png', 'FIRE', 2);

        return Promise.all(promises);
    }

    getImage(name) {
        return this._images.get(name);
    }

    getAnimation(name) {
        return this._animations.get(name);
    }
}
