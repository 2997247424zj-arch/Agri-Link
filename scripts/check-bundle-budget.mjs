import { readdirSync, statSync } from 'node:fs'
import { join } from 'node:path'

const assetsDir = join(process.cwd(), 'dist', 'assets')
const KB = 1024

const budgets = {
  totalJsKb: 240,
  maxJsChunkKb: 120,
  totalCssKb: 40,
}

const files = readdirSync(assetsDir)
const assets = files.map((name) => ({
  name,
  sizeKb: statSync(join(assetsDir, name)).size / KB,
}))

const jsAssets = assets.filter((asset) => asset.name.endsWith('.js'))
const cssAssets = assets.filter((asset) => asset.name.endsWith('.css'))
const totalJsKb = jsAssets.reduce((sum, asset) => sum + asset.sizeKb, 0)
const totalCssKb = cssAssets.reduce((sum, asset) => sum + asset.sizeKb, 0)
const maxJsChunk = jsAssets.reduce((max, asset) => (asset.sizeKb > max.sizeKb ? asset : max), {
  name: '',
  sizeKb: 0,
})

const failures = [
  totalJsKb > budgets.totalJsKb && `JS total ${totalJsKb.toFixed(1)}KB > ${budgets.totalJsKb}KB`,
  maxJsChunk.sizeKb > budgets.maxJsChunkKb &&
    `Largest JS chunk ${maxJsChunk.name} ${maxJsChunk.sizeKb.toFixed(1)}KB > ${budgets.maxJsChunkKb}KB`,
  totalCssKb > budgets.totalCssKb && `CSS total ${totalCssKb.toFixed(1)}KB > ${budgets.totalCssKb}KB`,
].filter(Boolean)

console.log('Bundle budget:')
console.log(`- JS total: ${totalJsKb.toFixed(1)}KB / ${budgets.totalJsKb}KB`)
console.log(`- Largest JS chunk: ${maxJsChunk.name} ${maxJsChunk.sizeKb.toFixed(1)}KB / ${budgets.maxJsChunkKb}KB`)
console.log(`- CSS total: ${totalCssKb.toFixed(1)}KB / ${budgets.totalCssKb}KB`)

if (failures.length) {
  console.error('\nBudget failed:')
  for (const failure of failures) {
    console.error(`- ${failure}`)
  }
  process.exitCode = 1
}
