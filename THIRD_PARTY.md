# Composants tiers

Le simulateur en ligne (`docs/simulator.html`) exécute le vrai code Java du client Snake dans le
navigateur grâce au simulateur : un PC x86 émulé en WebAssembly, avec Linux et une JVM
OpenJDK 8. Ces composants sont publiés à côté du jeu,
sans modification, et gardent leur propre licence.

| Emplacement | Contenu | Licence |
|---|---|---|
| `docs/app/snake.jar` | le jeu, compilé depuis `web/src` (code du projet) | celle du projet |
| `docs/simulateur/player.html`, `player.js`, `simulateur.js`, `simulateur.css` | simulateur (lecteur) | MIT (`docs/simulateur/LICENSE`) |
| `docs/simulateur/v86/libv86.js`, `v86.wasm` | émulateur x86 [v86](https://github.com/copy/v86) | BSD-2-Clause |
| `docs/simulateur/v86/seabios.bin`, `vgabios.bin` | [SeaBIOS](https://www.seabios.org) 1.16.2 | LGPL-3.0 |
| `docs/simulateur/image/` | image de la machine virtuelle : Linux Tiny Core 11 (noyau, BusyBox, glibc), X11 (TinyX, bibliothèques X.Org), terminal rxvt-unicode, OpenJDK 8 JRE, environnement natif (Qt 5.11, libcob de GnuCOBOL 3.2, Python 3.7, Tcl/Tk 8.6 et leurs dépendances Debian 10), polices DejaVu | GPL-2.0, GPL-3.0, LGPL-2.1, LGPL-3.0, PSF-2.0, GPL-2.0 + Classpath Exception, X11/MIT, FTL… |

Le détail composant par composant (version, licence, origine) est dans
[`docs/licenses/NOTICE.txt`](docs/licenses/NOTICE.txt), les textes complets dans `docs/licenses/texts/`
et `docs/licenses/openjdk/`, et l'emplacement des sources correspondantes dans
[`docs/licenses/SOURCES.txt`](docs/licenses/SOURCES.txt).

**Sources des composants GPL/LGPL** : elles sont publiées, aux versions exactes des binaires
de l'image, dans la Release du simulateur indiquée dans `docs/licenses/SOURCES.txt` (archives
`jarwebox-sources-*.tar`), complétée par l'offre écrite du même fichier.

Le jeu n'utilise que les API publiques d'OpenJDK et relève de son exception Classpath : il
garde sa propre licence.

**Marques** : Java et OpenJDK sont des marques d'Oracle et/ou de ses filiales ; Linux, de
Linus Torvalds. Ce projet n'est ni affilié à ces titulaires, ni approuvé par eux.
