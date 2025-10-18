# Bank Concurrency Demo

Demonstrates race conditions in concurrent bank account transfers and the evolution to thread-safe behavior.

## Development Roadmap
Track progress on the [Project Board](https://github.com/users/geopapala/projects/4/views/1).

### Milestones
- **v0.1 — Unsafe baseline**: non-thread-safe bank account simulation
- **v0.2 — Thread-safe fixes**: added synchronization and corrected behavior
- **v0.3 — Benchmark & demo**: performance comparison

## Overview
This project simulates a small banking system with accounts and transfers.  
The goal is to:
1. Show non-thread-safe implementation (race conditions visible)
2. Expose issues via stress-test harness
3. Apply synchronization techniques to correct behavior
4. Benchmark and document results

## How to reproduce the demo
1. Checkout `feature/unsafe-baseline` and run the test harness
    - Observe inconsistent balances or errors
2. Merge or checkout `feature/thread-safe-fixes`
    - Rerun harness and observe correct behavior
3. Logs, screenshots, and GIFs are in `/demo`

## Demo Artifacts
- `demo/unsafe-race.gif` — shows race condition
- `demo/unsafe-log.txt` — log of failed runs
- `demo/thread-safe-log.txt` — passing results after fixes

## Progress Timeline
- **v0.1 — 2025-10-xx**: Unsafe baseline + stress harness
- **v0.2 — 2025-10-xx**: Thread-safe fixes
- **v0.3 — 2025-10-xx**: Benchmarks and final demo

## Notes
- All work is done solo
- Branches and PRs document evolution from unsafe → safe
