import ImageStore from './ImageStore.js';
import SoundStore from './SoundStore.js';
import SceneDirector from '../scene/SceneDirector.js';
import MainMenuScene from '../scene/MainMenuScene.js';

export default class DaftMan {
    static DEBUG = false;

    static addExtraSpaces(str) {
        let result = '';
        for (let i = 0; i < str.length; i++) {
            if (str[i] === ' ') {
                result += '    ';
            } else {
                result += str[i];
            }
        }
        return result;
    }

    static removeExtraSpaces(str) {
        let result = '';
        for (let i = 0; i < str.length; i++) {
            if (i > 0 && str[i] === ' ' && str[i - 1] === ' ') {
                continue;
            } else {
                result += str[i];
            }
        }
        return result;
    }

    static async init() {
        const canvas = document.getElementById('game');

        // Load custom font
        try {
            const arcadeFont = new FontFace('ArcadeClassic', 'url(fonts/ARCADECLASSIC.TTF)');
            await arcadeFont.load();
            document.fonts.add(arcadeFont);
        } catch (e) {
            console.warn('Could not load ArcadeClassic font, falling back to monospace');
        }

        // Initialize image store
        await ImageStore.get().initialize();

        // Initialize sound store (constructor loads sound paths)
        SoundStore.get();

        // Set up SceneDirector with canvas
        const director = SceneDirector.get();
        director.setCanvas(canvas);
        director.pushScene(new MainMenuScene(director.getContainer()));
        director.startLoop();
    }
}

// Auto-start when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => DaftMan.init());
} else {
    DaftMan.init();
}
