DaftMan 1.0
Ryan Ashcraft
December 02, 2010

DESCRIPTION
This application is a take on the classic video game "Bomberman" by Hudson Soft, with some design and theme inspirations from Daft Punk. Some homages to other games are included,including The Legend of Zelda, Super Mario Bros, Super Mario Kart, Sonic, and Minecraft.

You play by trying to destroy the bricks and collecting all the rupees in each level and avoiding
monsters and being hit by a bomb's explosion.

PLAY IT ONLINE
http://ryanashcraft.me/projects/daftman/

EXTRA FEATURES
- Pixel by pixel movements of sprites with collision detection
- Bombs/fires to block up blocks
- Health
- Stars that increase speed (Sonic/Mario Kart style)
- Fancy graphics
- Infinite levels with increased difficulty
- Time limit per limit
- Pause/Resume in-game
- MIDI background music sound clips
- WAV sound effects
- Read text files for creating board (test purposes only, not available when running in browser due to Java security permissions)
- Read/write to high scores database in MySQL server


OPENING TXT FILES TO FILL BOARD
Opening files is not possible when running in a browser, due to Java security permissions. If running the applet locally, press the 'o' key to launch a JFileChooser to load a test TXT file to fill the board.

Test TXT files must be made into 11 rows of strings with length 15. Each character in the each row define the position on the board.
	1 - 1st player
	2 - enemy
	g - grass tile
	w - wall tile
	h - brick tile with heart prize
	r - brick tile with rupee prize
	s - brick tile with star prize
If any extra characters or any characters not defined above are entered into the text file, then the game will not open the file.

Example from testboard.txt:
gggggggggggggg2
gggggggggggggg2
gggggggggggggg2
gggggggggggggg2
gggggggrgggggg2
1gggggswsggggg2
ggggggghgggggg2
gggggggggggggg2
gggggggggggggg2
gggggggggggggg2
gggggggggggggg2


MYSQL HIGH SCORE DATABASE
A MySQL connector jar is required to used to connect to a MySQL server to read/write high scores to a MySQL database. A manifest file is required in order to use this connector jar.

Connections to the MySQL database are not possible if using the appletviewer command (results in java.lang.ClassNotFoundException). Only way is to export a jar with a manifest file and the MySQL connector jar. The MySQL connector jar file must be in the same directory as the exported jar file, which must be in the same directory as the HTML file.

Go to http://ryanashcraft.me/projects/daftman/ to see high score list working as designed.