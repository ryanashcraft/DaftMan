export default class Path {
    constructor(state) {
        this._path = [state];
        this._totalWeight = 0;
        this._lastEdgeWeight = 0;
    }

    static extend(old, s) {
        const p = new Path(null);
        p._path = [];

        if (old === null) {
            return p;
        }

        const oldPathway = old.getPathway();
        for (const state of oldPathway) {
            p._path.push(state);
        }

        if (s !== null) {
            p._path.push(s);
        }

        if (p._path.length > 1) {
            p._totalWeight = old.getTotalWeight() + s.getWeight();
            p._lastEdgeWeight = s.getWeight();
        } else {
            p._totalWeight = 0;
            p._lastEdgeWeight = 0;
        }

        return p;
    }

    getTotalWeight() {
        return this._totalWeight;
    }

    getLastState() {
        if (this._path.length > 1) {
            return this._path[this._path.length - 1];
        } else if (this._path.length === 1) {
            return this._path[0];
        } else {
            return null;
        }
    }

    getPathway() {
        return this._path;
    }

    contains(state) {
        return this._path.indexOf(state) !== -1;
    }
}
