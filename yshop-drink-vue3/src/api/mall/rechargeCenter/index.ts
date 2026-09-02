import request from '@/config/axios'

export interface RechargeOrderVO {
  id: number
  orderNo: string
  userId: number
  packageId: number
  rechargeAmount: number
  giftAmount: number
  giftGrowValue: number
  payStatus: number
  payTime: string
  createTime: string
  nickname: string
  mobile: string
}

export interface RechargePackageVO {
  id: number
  amount: number
  giftAmount: number
  growValue: number
  vipLevel: string
  sort: number
  status: number
  pwd?: string
}

// 查询充值订单分页
export const getRechargeOrderPage = async (params: any) => {
  return await request.get({ url: `/member/recharge-order/page`, params })
}

// 查询充值套餐分页
export const getRechargePackagePage = async (params: any) => {
  return await request.get({ url: `/member/recharge-package/page`, params })
}

// 新增充值套餐
export const createRechargePackage = async (data: RechargePackageVO) => {
  return await request.post({ url: `/member/recharge-package/create`, data })
}

// 修改充值套餐
export const updateRechargePackage = async (data: RechargePackageVO) => {
  return await request.put({ url: `/member/recharge-package/update`, data })
}
