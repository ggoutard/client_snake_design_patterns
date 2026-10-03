#!/usr/bin/env bash
# Rebuilds docs/app/snake.jar (Java 8, for the simulator's JVM) from src/: sources + images + layouts.
set -euo pipefail
cd "$(dirname "$0")"
WORK=$(mktemp -d); trap 'rm -rf "$WORK"' EXIT
javac --release 8 -Xlint:-options -encoding UTF-8 -d "$WORK" $(find src -name '*.java')
(cd src && find . -type f ! -name '*.java' | tar -cf - -T -) | tar -C "$WORK" -xf -
rm -f ../docs/app/snake.jar && jar cfe ../docs/app/snake.jar tp1progreseau.SnakeWeb -C "$WORK" .
echo "docs/app/snake.jar : $(stat -c %s ../docs/app/snake.jar) octets"
