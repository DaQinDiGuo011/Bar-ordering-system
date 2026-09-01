import request from '@/config/axios'

export interface BusPerVO {
  id?: number
  passwordType: string
  passwordName: string
  passwordValue: string
  enabled: number
  remark: string  
  createTime: createTime
}



export const getListByPage = async (params: PageParam) => {
  return await request.get({ url: '/system/passwordConfig/page' },params)
}

export const createParam = async (data: BusPerVO) => {
  return await request.post({ url: '/system/passwordConfig/add', data: data })
}

export const updateParam = async (params: BusPerVO) => {
  return await request.post({ url: '/system/passwordConfig/update', data: params })
}

