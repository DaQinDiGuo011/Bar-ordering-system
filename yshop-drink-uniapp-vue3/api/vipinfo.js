import api from './api'


export function getVipInfo(data){
  return api.get('/user/vip/getVipInfo', data, { login: true })
}

// 开通会员卡
export function openVipCard(data){
  return api.post('/user/vip/openVipCard', data, { login: true })
}