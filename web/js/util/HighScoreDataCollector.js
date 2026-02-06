const HIGH_SCORE_RECORD_COUNT = 10;
const STORAGE_KEY = 'daftman_highscores';

export default class HighScoreDataCollector {
    static _instance = null;

    static getInstance() {
        if (!HighScoreDataCollector._instance) {
            HighScoreDataCollector._instance = new HighScoreDataCollector();
        }
        return HighScoreDataCollector._instance;
    }

    constructor() {
        this._recordScores = new Array(HIGH_SCORE_RECORD_COUNT).fill(0);
        this._recordHolders = new Array(HIGH_SCORE_RECORD_COUNT).fill('');
        this._loadFromStorage();
    }

    getRecordScores() {
        return this._recordScores;
    }

    getRecordHolders() {
        return this._recordHolders;
    }

    recordScore(score, name) {
        const entries = [];
        for (let i = 0; i < HIGH_SCORE_RECORD_COUNT; i++) {
            if (this._recordScores[i] > 0) {
                entries.push({ score: this._recordScores[i], name: this._recordHolders[i] });
            }
        }
        entries.push({ score, name });
        entries.sort((a, b) => b.score - a.score);

        this._recordScores = new Array(HIGH_SCORE_RECORD_COUNT).fill(0);
        this._recordHolders = new Array(HIGH_SCORE_RECORD_COUNT).fill('');

        for (let i = 0; i < Math.min(entries.length, HIGH_SCORE_RECORD_COUNT); i++) {
            this._recordScores[i] = entries[i].score;
            this._recordHolders[i] = entries[i].name;
        }

        this._saveToStorage();
    }

    _loadFromStorage() {
        try {
            const data = localStorage.getItem(STORAGE_KEY);
            if (data) {
                const parsed = JSON.parse(data);
                for (let i = 0; i < Math.min(parsed.length, HIGH_SCORE_RECORD_COUNT); i++) {
                    this._recordScores[i] = parsed[i].score || 0;
                    this._recordHolders[i] = parsed[i].name || '';
                }
            }
        } catch (e) {
            // localStorage unavailable or corrupt data - use defaults
        }
    }

    _saveToStorage() {
        try {
            const entries = [];
            for (let i = 0; i < HIGH_SCORE_RECORD_COUNT; i++) {
                if (this._recordScores[i] > 0) {
                    entries.push({ score: this._recordScores[i], name: this._recordHolders[i] });
                }
            }
            localStorage.setItem(STORAGE_KEY, JSON.stringify(entries));
        } catch (e) {
            // localStorage unavailable
        }
    }

    closeConnection() {
        // No-op: no database connection in browser
    }
}
