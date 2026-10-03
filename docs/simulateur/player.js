// Simulator player: one app, full frame, black. Meant to be embedded with an iframe:
//
//   <iframe src="simulateur/player.html?app=snake" allow="clipboard-read; fullscreen"></iframe>
//
// URL parameters
//   app       id of an app in the catalog          catalog   catalog URL (default ../apps/apps.json)
//   jar, exe  URL of a .jar or of a native program (instead of app)
//   title     its name                             mode      window | native | terminal
//   ratio     auto | 4:3 | 16:9 | 16:10 | 1:1      keyboard  auto | azerty | qwerty
//   options   0 hides the options menu             debug     1 shows input and protocol traces
//
// The player posts {source: "simulateur", type, ...} messages to the parent page
// (progress, ready, started, window, terminal, title, closed, error) and accepts
// {simulateur: "run", app: {id, title, jar | exe, mode}} and {simulateur: "options", ratio, keyboard}.
import { Simulator, RATIOS, KEYBOARDS } from "./simulateur.js";

const $ = (id) => document.getElementById(id);
const params = new URLSearchParams(location.search);
const PREFS = "simulateur.options";

function loadPrefs() {
    try {
        return JSON.parse(localStorage.getItem(PREFS)) || {};
    } catch {
        return {};
    }
}

function savePrefs(prefs) {
    try {
        localStorage.setItem(PREFS, JSON.stringify(prefs));
    } catch {
        // storage unavailable (private mode, sandboxed iframe): options still apply
    }
}

const prefs = { ...loadPrefs() };
if (params.has("ratio")) prefs.ratio = params.get("ratio");
if (params.has("keyboard")) prefs.keyboard = params.get("keyboard");

const jw = new Simulator($("app"), { ...prefs, debug: params.get("debug") === "1" });
if (jw.debug) {
    // Diagnostic overlay (?debug=1): the last input events, commands and replies.
    const pre = document.createElement("pre");
    pre.style.cssText = "position:absolute;left:8px;bottom:8px;z-index:4;margin:0;padding:6px 8px;" +
        "max-width:60%;font:11px/1.35 ui-monospace,monospace;color:#9fe0a8;background:rgba(0,0,0,.75);" +
        "border-radius:6px;pointer-events:none;white-space:pre-wrap";
    document.body.append(pre);
    const lines = [];
    const t0 = performance.now();
    jw.addEventListener("debug", ({ detail }) => {
        lines.push(`${((performance.now() - t0) / 1000).toFixed(2).padStart(7)}  ${detail.text}`);
        pre.textContent = lines.slice(-14).join("\n");
    });
}
let current = null;
let started = false;
let queued = null; // app requested by the parent page before the machine was ready

function post(type, detail = {}) {
    if (parent !== window) parent.postMessage({ source: "simulateur", type, ...detail }, "*");
}

function overlay(label, { progress = null, detail = "", relaunch = false } = {}) {
    $("overlay").hidden = false;
    $("label").textContent = label;
    $("detail").textContent = detail;
    $("relaunch").hidden = !relaunch;
    $("bar").hidden = relaunch;
    $("bar").classList.toggle("busy", progress === null);
    $("fill").style.width = progress === null ? "" : `${Math.round(progress * 100)}%`;
}

// ---------------------------------------------------------------- engine events

jw.addEventListener("progress", ({ detail: { loaded, total } }) => {
    const mb = (n) => (n / 2 ** 20).toFixed(1);
    overlay("Chargement…", { progress: loaded / total, detail: `${mb(loaded)} / ${mb(total)} Mo` });
    post("progress", { loaded, total });
});
jw.addEventListener("started", ({ detail }) => post("started", detail));
jw.addEventListener("window", () => {
    $("overlay").hidden = true;
    post("window");
});
jw.addEventListener("terminal", () => {
    $("overlay").hidden = true;
    post("terminal");
});
jw.addEventListener("title", ({ detail }) => {
    document.title = detail.title || "Simulateur";
    post("title", detail);
});
jw.addEventListener("closed", () => {
    overlay(`${current?.title || "L'application"} est fermée.`, { relaunch: true });
    post("closed");
});
jw.addEventListener("error", ({ detail }) => post("error", detail));

// ---------------------------------------------------------------- apps

/** The app with its file (jar or exe) made absolute against `base`. */
function located(app, base) {
    const key = app.exe ? "exe" : "jar";
    return { ...app, [key]: new URL(app[key], base).href };
}

async function resolveApp() {
    const file = params.get("jar") || params.get("exe");
    if (file) {
        const exe = !params.has("jar");
        return located({
            id: (file.split("/").pop() || "app").replace(/\.jar$/, "").replace(/[^\w.-]/g, "_"),
            title: params.get("title") || (exe ? "Application" : "Java"),
            [exe ? "exe" : "jar"]: file,
            mode: params.get("mode") || (exe ? "native" : "window"),
        }, location.href);
    }
    const catalogUrl = new URL(params.get("catalog") || "../apps/apps.json", location.href);
    const catalog = await (await fetch(catalogUrl)).json();
    return located(catalog.find((a) => a.id === params.get("app")) || catalog[0], catalogUrl);
}

async function launch(app) {
    current = app;
    document.title = app.title;
    overlay(`Lancement de ${app.title}…`);
    try {
        await jw.run(app);
    } catch (e) {
        overlay("Le lancement a échoué.", { relaunch: true, detail: e.message });
        post("error", { message: e.message });
    }
}

$("relaunch").addEventListener("click", () => launch(current));

window.addEventListener("message", (e) => {
    const msg = e.data;
    if (!msg || typeof msg !== "object") return;
    if (msg.simulateur === "run" && (msg.app?.jar || msg.app?.exe)) {
        const defaults = msg.app.exe ? { mode: "native", title: "Application" } : { mode: "window", title: "Java" };
        const app = located({ ...defaults, ...msg.app }, document.referrer || location.href);
        if (started) launch(app);
        else queued = app;
    } else if (msg.simulateur === "options") {
        if (msg.ratio) setOption("ratio", msg.ratio);
        if (msg.keyboard) setOption("keyboard", msg.keyboard);
    }
});

// ---------------------------------------------------------------- options menu

const LABELS = {
    ratio: { auto: "Auto", "4:3": "4:3", "16:9": "16:9", "16:10": "16:10", "1:1": "1:1" },
    keyboard: { auto: "Auto", azerty: "AZERTY", qwerty: "QWERTY" },
};

function setOption(name, value) {
    if (name === "ratio") jw.setRatio(value);
    else jw.setKeyboard(value);
    prefs[name] = jw[name];
    savePrefs({ ratio: prefs.ratio, keyboard: prefs.keyboard });
    for (const b of $(name).children) b.setAttribute("aria-pressed", String(b.dataset.value === jw[name]));
}

for (const [name, values] of [["ratio", RATIOS], ["keyboard", KEYBOARDS]]) {
    for (const value of values) {
        const b = document.createElement("button");
        b.dataset.value = value;
        b.textContent = LABELS[name][value];
        b.setAttribute("aria-pressed", String(value === jw[name]));
        b.addEventListener("click", () => setOption(name, value));
        $(name).append(b);
    }
}

function toggleMenu(open = $("menu").hidden) {
    $("menu").hidden = !open;
    $("toggle").setAttribute("aria-expanded", String(open));
}
$("toggle").addEventListener("click", () => toggleMenu());
document.addEventListener("pointerdown", (e) => {
    if (!$("options").contains(e.target)) toggleMenu(false);
});
document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && !$("menu").hidden) toggleMenu(false);
});
$("options").hidden = params.get("options") === "0";

// ---------------------------------------------------------------- start

(async () => {
    try {
        const app = await resolveApp();
        current = app;
        document.title = app.title;
        await jw.start();
        started = true;
        post("ready");
        await launch(queued || app);
    } catch (e) {
        overlay("Impossible de démarrer la machine virtuelle.", { detail: e.message });
        post("error", { message: e.message });
    }
})();
