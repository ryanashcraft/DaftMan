export default class State {
    constructor(tile, direction = null, weight = 0) {
        this._tile = tile;
        this._direction = direction;
        this._weight = weight;
    }

    getTile() {
        return this._tile;
    }

    getDirection() {
        return this._direction;
    }

    getWeight() {
        return this._weight;
    }

    setWeight(weight) {
        this._weight = weight;
    }
}
