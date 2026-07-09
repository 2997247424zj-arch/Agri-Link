# Agri-Link Frontend Performance Governance

## Monitoring Metrics

- Page load: `page-load`, `first-contentful-paint`, `first-paint`.
- Route switch: `route-ready`.
- API latency: `api-request`.
- Main-thread jank: `long-task`.
- Bundle health: total JS, largest JS chunk, total CSS.

Development metrics are printed in the browser console with the `[perf]` prefix and emitted through the `agri-link:perf` window event.

## Issue Levels

- P0: page freeze, blank screen, or core flow unusable.
- P1: route switch is visibly slow, interaction is delayed, or long tasks block core pages.
- P2: non-core page is slow, route chunk grows, or repeated API requests are found.
- P3: potential performance debt, unused dependency, or minor animation inconsistency.

## Priority

Fix in this order:

1. Core routes and common layout.
2. Route switch responsiveness.
3. Initial bundle and heavy dependencies.
4. High-data pages such as tables, carts, admin views.
5. Motion polish and reduced-motion support.
6. Release regression checks.

## Development Rules

- Use route-level dynamic imports for pages.
- Preload route chunks from navigation hover/focus when the target is likely to be opened.
- Deduplicate concurrent GET requests in the shared API client.
- Keep page motion to `transform` and `opacity`.
- Respect `prefers-reduced-motion`.
- Lazy load heavy modules and non-critical images.
- Paginate or virtualize long lists before they can render large DOM trees.
- Use native rendering containment such as `content-visibility` for below-the-fold sections when layout allows it.
- Avoid complex calculations in templates.
- Debounce or throttle high-frequency input, scroll, and resize work.
- Clean timers, subscriptions, and request side effects on component unmount.

## Release Verification

Run before release:

```bash
npm run type-check
npm run perf:verify
```

Run browser regression when Playwright browsers are installed:

```bash
npm run test:e2e:perf
```

Check:

- No type errors.
- No bundle budget failures.
- No governance check failures.
- Core pages switch without blank screens.
- Browser console has no abnormal `long-task` bursts.
- API latency outliers are recorded and reviewed.

## Regression Check

Every iteration should review:

- Slowest route switches.
- Slowest API requests.
- Bundle size change.
- Largest route chunk.
- New large images or static assets.
- Any newly introduced dependency.
