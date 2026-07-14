import { describe, expect, it } from 'vitest'
import { translateInterfaceText } from '@/i18n/globalLocale'

describe('global interface translation', () => {
  it('translates complete page headings', () => {
    expect(translateInterfaceText('融资产品、意向匹配与审批')).toBe('Finance products, matching and approval')
  })

  it('translates shared labels without corrupting longer words', () => {
    expect(translateInterfaceText('共 3 条商品记录')).toBe('3 product records')
    expect(translateInterfaceText('采购订单')).toBe('Purchase orders')
    expect(translateInterfaceText('库存 420 箱')).toBe('Stock 420 箱')
  })
})
