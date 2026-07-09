import { readFileSync } from 'node:fs'

const checks = [
  {
    file: 'src/router/index.ts',
    label: 'route-level dynamic imports',
    patterns: ['const routeComponents', "import('@/views/", 'preloadRouteComponent'],
  },
  {
    file: 'src/api/client.ts',
    label: 'GET request dedupe and timing',
    patterns: ['pendingGets', 'measureApi'],
  },
  {
    file: 'src/utils/performance.ts',
    label: 'runtime performance metrics',
    patterns: ['route-ready', 'api-request', 'long-task'],
  },
  {
    file: 'src/styles/main.css',
    label: 'motion tokens and render containment',
    patterns: ['--motion-page', 'content-visibility: auto', 'prefers-reduced-motion'],
  },
  {
    file: 'PERFORMANCE_GOVERNANCE.md',
    label: 'performance governance documentation',
    patterns: ['Issue Levels', 'Release Verification', 'Regression Check'],
  },
]

const failures = []

for (const check of checks) {
  const content = readFileSync(check.file, 'utf8')
  const missing = check.patterns.filter((pattern) => !content.includes(pattern))
  if (missing.length) {
    failures.push(`${check.label}: ${check.file} missing ${missing.join(', ')}`)
  }
}

if (failures.length) {
  console.error('Performance governance check failed:')
  for (const failure of failures) {
    console.error(`- ${failure}`)
  }
  process.exitCode = 1
} else {
  console.log('Performance governance check passed.')
}
