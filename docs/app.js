const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');
const startBtn = document.getElementById('btn-start');
const terrainSelect = document.getElementById('terrain-select');
const modeSelect = document.getElementById('mode-select');
const overlay = document.getElementById('game-overlay');

let gameLoop;
let isRunning = false;
let gridSizeX = 20;
let gridSizeY = 20;
let tileSizeX = canvas.width / gridSizeX;
let tileSizeY = canvas.height / gridSizeY;

// Entities
let snakes = [];
let items = [];
let walls = [];
let spawns = [];

// Images (Resources)
const imgs = {
    apple: new Image(),
    sick: new Image(),
    invincible: new Image(),
    mystery: new Image(),
    wall: new Image(),
    snake_green: [new Image(), new Image(), new Image(), new Image(), new Image()],
    snake_red: [new Image(), new Image(), new Image(), new Image(), new Image()]
};
imgs.apple.src = 'images/apple.png';
imgs.sick.src = 'images/sickBall.png';
imgs.invincible.src = 'images/invicibleBall.png';
imgs.mystery.src = 'images/mysteryBox.png';
imgs.wall.src = 'images/wall.png';
for (let i = 0; i < 5; i++) {
    imgs.snake_green[i].src = `images/snake_green_${i}.png`;
    imgs.snake_red[i].src = `images/snake_red_${i}.png`;
}

const Direction = { UP: 0, RIGHT: 1, DOWN: 2, LEFT: 3 };
const ItemType = { APPLE: 0, SICK: 1, INVINCIBLE: 2, MYSTERY: 3 };
let score = 0;

class Snake {
    constructor(id, imgsArray, startX, startY) {
        this.id = id;
        this.imgs = imgsArray;
        this.body = [{ x: startX, y: startY }];
        this.direction = Direction.RIGHT;
        this.nextDirection = Direction.RIGHT;
        this.isBot = false; // Defini dans initGame
        this.growPending = 0;
        this.isSick = false;
        this.sickTimer = 0;
        this.isPoisoned = false; // poison par Boite Mystere
        this.isInvincible = false;
        this.invincibleTimer = 0;
        this.alive = true;
    }

    update() {
        if (!this.alive) return;
        this.direction = this.nextDirection;
        let head = { ...this.body[0] };

        if (this.isBot) this.computeBotMovement();

        switch (this.direction) {
            case Direction.UP: head.y -= 1; break;
            case Direction.DOWN: head.y += 1; break;
            case Direction.LEFT: head.x -= 1; break;
            case Direction.RIGHT: head.x += 1; break;
        }

        if (head.x < 0) head.x = gridSizeX - 1;
        if (head.x >= gridSizeX) head.x = 0;
        if (head.y < 0) head.y = gridSizeY - 1;
        if (head.y >= gridSizeY) head.y = 0;

        // Collision avec les murs
        let hitWall = walls.some(w => w.x === head.x && w.y === head.y);

        if (hitWall && this.isInvincible) {
            // Invincible : traverser les murs en continuant dans la meme direction
            let tries = 0;
            while (walls.some(w => w.x === head.x && w.y === head.y) && tries < gridSizeX + gridSizeY) {
                switch (this.direction) {
                    case Direction.UP: head.y = (head.y - 1 + gridSizeY) % gridSizeY; break;
                    case Direction.DOWN: head.y = (head.y + 1) % gridSizeY; break;
                    case Direction.LEFT: head.x = (head.x - 1 + gridSizeX) % gridSizeX; break;
                    case Direction.RIGHT: head.x = (head.x + 1) % gridSizeX; break;
                }
                tries++;
            }
            hitWall = false;
        }

        // Collision avec soi-meme (queue toujours libre)
        let hitSelf = this.body.some((s, idx) => {
            if (idx === this.body.length - 1) return false;
            return s.x === head.x && s.y === head.y;
        });
        if ((hitWall || hitSelf) && !this.isInvincible) this.alive = false;

        if (!this.alive) return;

        this.body.unshift(head);
        if (this.growPending > 0) this.growPending--;
        else this.body.pop();

        if (this.sickTimer > 0) {
            this.sickTimer--;
            this.isSick = true;
        } else {
            this.isSick = false;
            this.isPoisoned = false;
        }

        if (this.invincibleTimer > 0) {
            this.invincibleTimer--;
            this.isInvincible = true;
        } else {
            this.isInvincible = false;
        }
    }

    // Verifie si une case est libre (murs + corps + autres serpents)
    // invincible: si vrai, les murs ne bloquent pas
    isCellFree(x, y, body, invincible) {
        if (!invincible && walls.some(w => w.x === x && w.y === y)) return false;
        if (body.some(s => s.x === x && s.y === y)) return false;
        return true;
    }

    // Wrap-around des coordonnees
    wrap(x, y) {
        return {
            x: (x + gridSizeX) % gridSizeX,
            y: (y + gridSizeY) % gridSizeY
        };
    }

    // Retourne les voisins accessibles depuis (pos) etant donne un corps simule.
    // fromDir: direction d'ou l'on vient (pour interdire le demi-tour)
    getNeighbors(pos, body, fromDir, invincible) {
        const DIRS = [
            { dx: 0, dy: -1, dir: Direction.UP, opp: Direction.DOWN },
            { dx: 0, dy: 1, dir: Direction.DOWN, opp: Direction.UP },
            { dx: -1, dy: 0, dir: Direction.LEFT, opp: Direction.RIGHT },
            { dx: 1, dy: 0, dir: Direction.RIGHT, opp: Direction.LEFT },
        ];
        const results = [];
        for (const d of DIRS) {
            if (fromDir !== undefined && fromDir !== null && d.dir === fromDir) continue; // demi-tour interdit
            const n = this.wrap(pos.x + d.dx, pos.y + d.dy);
            n.dir = d.dir;
            if (!invincible && walls.some(w => w.x === n.x && w.y === n.y)) continue;
            // Corps : le dernier segment sera libere donc on l'ignore
            const blocked = body.some((s, idx) => idx < body.length - 1 && s.x === n.x && s.y === n.y);
            if (blocked) continue;
            results.push(n);
        }
        return results;
    }

    // BFS simple et robuste : cle de visite = "x,y" uniquement
    // On simule le glissement du corps a chaque etape
    bfs(startPos, targetPos, startBody, startGrow, oppDir) {
        // oppDir = direction OPPOSEE a la direction actuelle (a interdire au 1er pas)
        const MAX_STEPS = gridSizeX * gridSizeY * 2;
        const visited = new Set();
        visited.add(`${startPos.x},${startPos.y}`);
        const inv = this.isInvincible;

        const queue = [{ pos: startPos, body: startBody.map(s => ({ ...s })), grow: startGrow, path: [] }];

        while (queue.length > 0) {
            const curr = queue.shift();
            if (curr.pos.x === targetPos.x && curr.pos.y === targetPos.y) return curr.path;
            if (curr.path.length >= MAX_STEPS) continue;

            const blockDir = curr.path.length === 0 ? oppDir : null;
            const neighbors = this.getNeighbors(curr.pos, curr.body, blockDir, inv);

            for (const n of neighbors) {
                const key = `${n.x},${n.y}`;
                if (visited.has(key)) continue;
                visited.add(key);

                // Simuler le glissement du corps
                const newBody = [{ x: curr.pos.x, y: curr.pos.y }, ...curr.body];
                const newGrow = curr.grow;
                if (newGrow > 0) { /* grandit, ne pas pop */ } else newBody.pop();

                queue.push({
                    pos: n,
                    body: newBody,
                    grow: newGrow > 0 ? newGrow - 1 : 0,
                    path: [...curr.path, n.dir]
                });
            }
        }
        return null;
    }

    // Flood-fill : compte les cases accessibles depuis une position
    floodFill(pos, body, invincible) {
        const visited = new Set();
        visited.add(`${pos.x},${pos.y}`);
        const queue = [pos];
        while (queue.length > 0) {
            const cur = queue.shift();
            for (const d of [{ dx: 0, dy: -1 }, { dx: 0, dy: 1 }, { dx: -1, dy: 0 }, { dx: 1, dy: 0 }]) {
                const n = this.wrap(cur.x + d.dx, cur.y + d.dy);
                const key = `${n.x},${n.y}`;
                if (visited.has(key)) continue;
                if (!invincible && walls.some(w => w.x === n.x && w.y === n.y)) continue;
                if (body.some(s => s.x === n.x && s.y === n.y)) continue;
                visited.add(key);
                queue.push(n);
            }
        }
        return visited.size;
    }

    computeBotMovement() {
        const head = this.body[0];
        const tail = this.body[this.body.length - 1];
        const inv = this.isInvincible;

        // Direction opposee a interdire au 1er pas
        const OPP = {
            [Direction.UP]: Direction.DOWN, [Direction.DOWN]: Direction.UP,
            [Direction.LEFT]: Direction.RIGHT, [Direction.RIGHT]: Direction.LEFT
        };
        const oppDir = OPP[this.direction];

        // Chercher la pomme la plus proche
        const apples = items.filter(i => i.type === ItemType.APPLE);
        if (apples.length === 0) return;
        const target = apples.reduce((best, a) => {
            const da = Math.abs(a.x - head.x) + Math.abs(a.y - head.y);
            const db = Math.abs(best.x - head.x) + Math.abs(best.y - head.y);
            return da < db ? a : best;
        }, apples[0]);

        // 1. Tenter le chemin vers la pomme
        const pathToApple = this.bfs(head, target, this.body, this.growPending, oppDir);

        if (pathToApple && pathToApple.length > 0) {
            // Simuler l'etat du corps apres avoir mange la pomme
            let futureBody = this.body.map(s => ({ ...s }));
            let futureGrow = this.growPending; // Croissance reelle initiale (sans +1 prématuré !)
            for (let i = 0; i < pathToApple.length; i++) {
                const dir = pathToApple[i];
                const delta = {
                    [Direction.UP]: { dx: 0, dy: -1 }, [Direction.DOWN]: { dx: 0, dy: 1 },
                    [Direction.LEFT]: { dx: -1, dy: 0 }, [Direction.RIGHT]: { dx: 1, dy: 0 }
                }[dir];
                const fh = this.wrap(futureBody[0].x + delta.dx, futureBody[0].y + delta.dy);
                futureBody.unshift(fh);

                // C'est UNIQUEMENT a l'arrivee sur la pomme qu'il grandit de +1
                if (i === pathToApple.length - 1) futureGrow += 1;

                if (futureGrow > 0) futureGrow--;
                else futureBody.pop();
            }
            const futureHead = futureBody[0];
            const futureTail = futureBody[futureBody.length - 1];
            const lastOpp = OPP[pathToApple[pathToApple.length - 1]];

            // Verifier qu'apres la pomme, on peut encore rejoindre sa queue
            const escape = this.bfs(futureHead, futureTail, futureBody, 0, lastOpp);
            if (futureBody.length <= 4 || escape !== null) {
                this.nextDirection = pathToApple[0];
                return;
            }
        }

        // 2. Chemin direct vers la pomme risqué : au lieu du chemin le plus court vers la queue qui fait tourner en petits cercles idiots,
        // on cherche les mouvements sûrs (qui conservent un accès à la queue) mais qui OUVRENT LE PLUS D'ESPACE (aller au large pour faire demi-tour plus loin).
        const free = this.getNeighbors(head, this.body, oppDir, inv);
        if (free.length === 0) return;

        const scoredNeighbors = free.map(n => {
            const nextBody = [{ x: n.x, y: n.y }, ...this.body];
            let nextGrow = this.growPending;
            if (nextGrow > 0) nextGrow--;
            else nextBody.pop();
            const nextTail = nextBody[nextBody.length - 1];

            // Vérifier si après cette action le serpent conserve une sortie de secours vers sa queue
            const canEscape = (nextBody.length <= 3) || (this.bfs(n, nextTail, nextBody, 0, OPP[n.dir]) !== null);
            const space = this.floodFill(n, nextBody, inv);

            // Eloignement de la pomme pour élargir le virage et éviter d'osciller stupidement autour d'elle
            const distToTarget = Math.abs(n.x - target.x) + Math.abs(n.y - target.y);

            return { dir: n.dir, canEscape, space, distToTarget };
        });

        const safeMoves = scoredNeighbors.filter(m => m.canEscape);

        if (safeMoves.length > 0) {
            // Trier les coups sûrs : 1. Max d'espace disponible (floodFill), 2. Eloignement pour faire son demi-tour au large
            safeMoves.sort((a, b) => {
                if (b.space !== a.space) return b.space - a.space;
                return b.distToTarget - a.distToTarget;
            });
            this.nextDirection = safeMoves[0].dir;
            return;
        }

        // 3. Dernier recours (aucune certitude de survie) : prendre la case qui donne le plus d'espace immédiat
        scoredNeighbors.sort((a, b) => b.space - a.space);
        this.nextDirection = scoredNeighbors[0].dir;
    }

    draw(ctx) {
        if (!this.alive) return;
        this.body.forEach((seg, i) => {
            if (i === 0) {
                // Mapping correct Java (0:Haut, 1:Bas, 2:Droite, 3:Gauche)
                // En JS (0:Haut, 1:Droite, 2:Bas, 3:Gauche)
                const dirMap = { 0: 0, 1: 2, 2: 1, 3: 3 };
                ctx.drawImage(this.imgs[dirMap[this.direction]], seg.x * tileSizeX, seg.y * tileSizeY, tileSizeX, tileSizeY);
            } else {
                ctx.drawImage(this.imgs[4], seg.x * tileSizeX, seg.y * tileSizeY, tileSizeX, tileSizeY);
            }

            // Appliquer une teinte circulaire semi-transparente SUR l'image
            if (this.isInvincible || this.isSick || this.isPoisoned) {
                if (this.isInvincible) {
                    ctx.fillStyle = 'rgba(255, 215, 0, 0.6)'; // Teinte Jaune (Invincible)
                } else if (this.isPoisoned || this.isSick) {
                    ctx.fillStyle = 'rgba(155, 89, 182, 0.6)'; // Teinte Violette (Poison/Inversé)
                }
                ctx.beginPath();
                // Exactement la meme taille que l'image du corps du snake
                ctx.ellipse(seg.x * tileSizeX + tileSizeX / 2, seg.y * tileSizeY + tileSizeY / 2, tileSizeX / 2, tileSizeY / 2, 0, 0, Math.PI * 2);
                ctx.fill();
            }
        });
    }
}

class Item {
    constructor(x, y, type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    draw(ctx) {
        let img = imgs.apple;
        // Inversion pour corriger le mismatch : 
        // L'image 'invincible' est violette -> c'est le POISON (SICK)
        // L'image 'sick' est jaune -> c'est l'INVINCIBILITÉ
        if (this.type === ItemType.SICK) img = imgs.invincible;
        if (this.type === ItemType.INVINCIBLE) img = imgs.sick;
        if (this.type === ItemType.MYSTERY) img = imgs.mystery;
        ctx.drawImage(img, this.x * tileSizeX, this.y * tileSizeY, tileSizeX, tileSizeY);
    }
}

async function fetchLayout(layoutName) {
    try {
        const response = await fetch('../layouts/' + layoutName + '.lay');
        if (!response.ok) throw new Error("Fichier introuvable");
        const text = await response.text();
        parseLayout(text);
    } catch (e) {
        console.error("Erreur de chargement du layout, fallback...", e);
        gridSizeX = 20; gridSizeY = 20;
        walls = []; spawns = [{ x: 5, y: 5 }, { x: 15, y: 15 }];
        tileSizeX = canvas.width / gridSizeX; tileSizeY = canvas.height / gridSizeY;
    }
}

function parseLayout(text) {
    const lines = text.split('\n').map(l => l.replace('\r', '')).filter(l => l.length > 0);
    gridSizeY = lines.length;
    gridSizeX = Math.max(...lines.map(l => l.length));
    tileSizeX = canvas.width / gridSizeX;
    tileSizeY = canvas.height / gridSizeY;

    walls = [];
    spawns = [];

    for (let y = 0; y < gridSizeY; y++) {
        for (let x = 0; x < lines[y].length; x++) {
            const char = lines[y][x];
            if (char === '%') walls.push({ x, y });
            if (char === 'S' || char === 'A') spawns.push({ x, y });
        }
    }

    if (spawns.length < 2) {
        spawns.push({ x: Math.floor(gridSizeX / 4), y: Math.floor(gridSizeY / 2) });
        spawns.push({ x: Math.floor(gridSizeX * 0.75), y: Math.floor(gridSizeY / 2) });
    }
}

function spawnApple() {
    let x, y, valid = false;
    while (!valid) {
        x = Math.floor(Math.random() * gridSizeX);
        y = Math.floor(Math.random() * gridSizeY);
        valid = !walls.some(w => w.x === x && w.y === y) &&
            !snakes.some(s => s.body.some(b => b.x === x && b.y === y)) &&
            !items.some(i => i.x === x && i.y === y);
    }
    items.push(new Item(x, y, ItemType.APPLE));

    // 1 chance sur 10 d'invoquer un item special
    if (Math.random() <= 0.10) {
        spawnSpecialItem();
    }
}

function spawnSpecialItem() {
    let x, y, valid = false;
    let attempts = 0;
    while (!valid && attempts < 100) {
        x = Math.floor(Math.random() * gridSizeX);
        y = Math.floor(Math.random() * gridSizeY);
        valid = !walls.some(w => w.x === x && w.y === y) &&
            !snakes.some(s => s.body.some(b => b.x === x && b.y === y)) &&
            !items.some(i => i.x === x && i.y === y);
        attempts++;
    }
    if (valid) {
        // Seulement INVINCIBLE et SICK (poison)
        const specialTypes = [ItemType.INVINCIBLE, ItemType.SICK];
        items.push(new Item(x, y, specialTypes[Math.floor(Math.random() * specialTypes.length)]));
    }
}

// Trouve la meilleure position de spawn (case la plus eloignee des murs)
function findBestSpawn() {
    let bestPos = { x: Math.floor(gridSizeX / 2), y: Math.floor(gridSizeY / 2) };
    let bestDist = -1;
    for (let y = 0; y < gridSizeY; y++) {
        for (let x = 0; x < gridSizeX; x++) {
            if (walls.some(w => w.x === x && w.y === y)) continue;
            let minDist = walls.reduce((min, w) => {
                return Math.min(min, Math.abs(w.x - x) + Math.abs(w.y - y));
            }, Infinity);
            if (minDist === Infinity) minDist = Math.min(x, gridSizeX - 1 - x, y, gridSizeY - 1 - y);
            if (minDist > bestDist) {
                bestDist = minDist;
                bestPos = { x, y };
            }
        }
    }
    return bestPos;
}

async function initGame() {
    overlay.classList.add('hidden');
    if (gameLoop) clearInterval(gameLoop);

    await fetchLayout(terrainSelect.value);

    snakes = [];
    items = [];
    score = 0;

    // Mode 1 joueur uniquement
    const spawnPos = findBestSpawn();
    snakes.push(new Snake(1, imgs.snake_green, spawnPos.x, spawnPos.y));
    snakes[0].isBot = false;

    spawnApple();
    isRunning = true;
    gameLoop = setInterval(update, 120);
}

function update() {
    if (!isRunning) return;

    snakes.forEach(s => s.update());

    // Check collisions
    snakes.forEach(snake => {
        if (!snake.alive) return;
        const head = snake.body[0];

        // Item collision
        for (let i = items.length - 1; i >= 0; i--) {
            if (items[i].x === head.x && items[i].y === head.y) {
                const t = items[i].type;
                items.splice(i, 1);
                if (t === ItemType.APPLE) {
                    snake.growPending += 1;
                    score += 10;
                    spawnApple();
                }
                else if (t === ItemType.INVINCIBLE) {
                    snake.invincibleTimer = 100;
                }
                else if (t === ItemType.SICK) {
                    // Poison = commandes inversées !
                    snake.sickTimer = 100;
                    snake.isPoisoned = true;
                }
            }
        }

        // Player collision
        if (!snake.isInvincible) {
            snakes.forEach(other => {
                if (snake.id !== other.id && other.alive) {
                    if (other.body.some(b => b.x === head.x && b.y === head.y)) {
                        const lenA = snake.body.length + snake.growPending;
                        const lenB = other.body.length + other.growPending;

                        if (lenA > lenB && !other.isInvincible) {
                            // Le plus grand MANGE le plus petit !
                            other.alive = false;
                            score += 50; // Bonus de kill
                            snake.growPending += Math.floor(lenB / 2); // Absorbe la moitie de la taille de l'autre
                        } else {
                            // Collision normale : le serpent meurt
                            snake.alive = false;
                        }
                    }
                }
            });
        }
    });

    draw();

    if (snakes.every(s => !s.alive)) {
        isRunning = false;
        clearInterval(gameLoop);
        overlay.querySelector('h2').textContent = `Score : ${score}`;
        overlay.classList.remove('hidden');
    }
}

function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    walls.forEach(w => {
        ctx.drawImage(imgs.wall, w.x * tileSizeX, w.y * tileSizeY, tileSizeX, tileSizeY);
    });

    items.forEach(i => i.draw(ctx));
    snakes.forEach(s => s.draw(ctx));

    // Mise à jour de l'affichage HTML du score (non étiré)
    const scoreEl = document.getElementById('score-display');
    if (scoreEl) {
        scoreEl.textContent = `SCORE : ${score}`;
    }
}

document.addEventListener('keydown', (e) => {
    // Menu Overlay on Escape
    if (e.key === 'Escape') {
        isRunning = !isRunning;
        if (!isRunning) {
            overlay.querySelector('h2').textContent = "--- PAUSE ---";
            overlay.classList.remove('hidden');
        } else {
            overlay.classList.add('hidden');
        }
        return;
    }

    if (!isRunning) return;
    const s1 = snakes[0];

    if (s1 && !s1.isBot) {
        let reqDir = null;
        if (e.key === 'ArrowUp') reqDir = s1.isPoisoned ? Direction.DOWN : Direction.UP;
        if (e.key === 'ArrowDown') reqDir = s1.isPoisoned ? Direction.UP : Direction.DOWN;
        if (e.key === 'ArrowLeft') reqDir = s1.isPoisoned ? Direction.RIGHT : Direction.LEFT;
        if (e.key === 'ArrowRight') reqDir = s1.isPoisoned ? Direction.LEFT : Direction.RIGHT;

        if (reqDir !== null) {
            const opp = {
                [Direction.UP]: Direction.DOWN, [Direction.DOWN]: Direction.UP,
                [Direction.LEFT]: Direction.RIGHT, [Direction.RIGHT]: Direction.LEFT
            };
            // On empêche le demi-tour mortel classique
            if (s1.direction !== opp[reqDir]) {
                s1.nextDirection = reqDir;
            }
        }
    }
});

startBtn.addEventListener('click', () => {
    initGame();
});

// Initial draw of empty board
ctx.fillStyle = "#000";
ctx.fillRect(0, 0, canvas.width, canvas.height);
