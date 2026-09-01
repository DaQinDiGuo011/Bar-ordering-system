import request from '@/config/axios'

export interface wineStore {
  id: number
  userId: number
  productId: number
  realName: string
  phone: string
  num: number
  remark: string
  storeNo: string
  storeStatus: number
  receiveTime: Date
  updater: Date
  tenantId: number
}


export const getWineStorePage = async (params: wineStore) => {
  return await request.get({ url: `/system/winestore/page`, params })
}

export const saveInfo = async (data: wineStore) => {
  return await request.post({ url: `/system/winestore/save-info`, data })
}

export const getInfoById = async (id: number) => {
  return await request.get({ url: `/system/winestore/getInfo/${id}`})
}

export const updateStatus = async (data: wineStore) => {
  return await request.post({ url: `/system/winestore/update-status`, data})
}

//生成桌位二维码
export const createDeskCode = (params) => {
  return request.get({ url: '/system/auth/admin-api/wx/qrcode', params })
}
