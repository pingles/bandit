# AGENTS.md

## Cursor Cloud specific instructions

### Overview

This is a Clojure multi-armed bandit library organized as a Leiningen monorepo with three sub-projects:
- `bandit-core` — Core library implementing bandit algorithms (Epsilon-Greedy, Softmax, UCB, Bayesian, EXP3)
- `bandit-simulate` — Monte Carlo simulation runner (outputs CSV)
- `bandit-ring` — Ring/Compojure web demo app (embedded Jetty, in-memory state)

### Prerequisites

- **JDK 11** (not JDK 14+ — `bandit-simulate` uses `-XX:+UseConcMarkSweepGC` which was removed in JDK 14)
- **Leiningen 2.x** (installed at `/usr/local/bin/lein`)
- `JAVA_HOME` must be set to `/usr/lib/jvm/java-11-openjdk-amd64`

### Key commands

| Task | Command | Working directory |
|------|---------|-------------------|
| Install all deps | `lein sub install` | `/workspace` |
| Run all tests | `lein sub do expectations, test` | `/workspace` |
| Run bandit-ring web app | `lein run 8080` | `/workspace/bandit-ring` |
| Run simulation | `lein run -a bayes -n 10 -t 1000` | `/workspace/bandit-simulate` |

### Gotchas

- The README says `lein run -m bandit.ring.example-app` but this namespace does not exist. The correct command is `lein run 8080` (from within `bandit-ring/`), which uses the `:main bandit.ring.app` declared in `project.clj`.
- The `bandit-ring` app's `-main` function requires a port number as an argument.
- `bandit-simulate` emits a JVM deprecation warning about `UseConcMarkSweepGC` on JDK 11 — this is harmless.
- All state in `bandit-ring` is in-memory (Clojure refs); no external databases or services are needed.
- The `bandit-ring` project depends on `bandit-core` being installed to the local Maven repo first (`lein sub install` handles this).
