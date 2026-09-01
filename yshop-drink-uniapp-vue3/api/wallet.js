import api from './api'


/**
 * 余额明细
 */
export function getWalletLogList(data) {
  return api.get('/wallet/log', data, { login: true })
}


/**
 * 获取钱包信息 
 */
export function getWalletInfo(data) {
  return api.get('/wallet/info', data, { login: true })
}


/**
 * 兑换储值码 
 */
export function getWalletExchange(data) {
  return api.post('/wallet/exchange', data, { login: true })
}


// 提交寄存
export function storeSubmitApi(data){
  return api.post('/user/winestore/submit',data, { login: true })
}
// 获取我的寄存列表
export function getMyStoreApi(params){
  return api.get('/user/winestore/storeRecord',params, { login: true })
}
//领取寄存
export function receiveStoreApi(params){
  return api.post('/user/winestore/receive',params, { login: true })
}
//取消寄存支付
export function cancelPay(params){
  return api.post('/user/winestore/cancelPay',params, { login: true })
}

export function getWineCount(params){
  return api.get('/user/winestore/getWineCount', { login: true })
}