export function aStarCompare(p1, p2) {
    return p1.getTotalWeight() - p2.getTotalWeight();
}

export class PriorityQueue {
    constructor(comparator) {
        this._heap = [];
        this._comparator = comparator;
    }

    add(item) {
        this._heap.push(item);
        this._bubbleUp(this._heap.length - 1);
    }

    remove() {
        const min = this._heap[0];
        const last = this._heap.pop();
        if (this._heap.length > 0) {
            this._heap[0] = last;
            this._sinkDown(0);
        }
        return min;
    }

    isEmpty() {
        return this._heap.length === 0;
    }

    _bubbleUp(i) {
        while (i > 0) {
            const parent = (i - 1) >> 1;
            if (this._comparator(this._heap[i], this._heap[parent]) < 0) {
                const tmp = this._heap[i];
                this._heap[i] = this._heap[parent];
                this._heap[parent] = tmp;
                i = parent;
            } else {
                break;
            }
        }
    }

    _sinkDown(i) {
        const length = this._heap.length;
        while (true) {
            let smallest = i;
            const left = 2 * i + 1;
            const right = 2 * i + 2;

            if (left < length && this._comparator(this._heap[left], this._heap[smallest]) < 0) {
                smallest = left;
            }
            if (right < length && this._comparator(this._heap[right], this._heap[smallest]) < 0) {
                smallest = right;
            }

            if (smallest !== i) {
                const tmp = this._heap[i];
                this._heap[i] = this._heap[smallest];
                this._heap[smallest] = tmp;
                i = smallest;
            } else {
                break;
            }
        }
    }
}
