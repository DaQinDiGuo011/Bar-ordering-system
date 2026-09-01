import api from './api'

/**
 * 基本信息 
 */
export function userGetUserInfo(data) {
  return api.get('/member/user/get-info', data, { login: true })
}

/**
 * 获取菜单
 */
export function mineService(data) {
  return api.get('/service/list', data, { login: true })
}

/**
 * 获取内容 
 */
export function mineServiceContent(data) {
  return api.get('/service/content', data, { login: true })
}


/**
 * save 
 */
export function userEdit(data) {
  return api.post('/member/user/update-nickname', data, { login: true })
}


/**
 * balanceGetBillList 
 */
export function balanceGetBillList(data) {
  return api.get('/member/user/getBill', data, { login: true })
}




/**
 * 充值列表 
 */
export function balanceGetMoneyList(data) {
  return api.get('/member/user/package/list', data, { login: true })
}

/**
 * 充值 
 */
export function createPayOrder(data) {
  return api.post('/member/user/order/create', data, { login: true })
  return api.post('/member/user/order/create', data, { login: true })
}

/**
 * 积分明细记录 
 */
export function getPointLogList(data) {
  return api.get('/point/log', data, { login: true })
}

/**
 * 积分商城商品列表 
 */
export function getPointGoodsList(data) {
  return api.get('/point/goods', data, { login: true })
}

/**
 * 兑换记录列表 status:0未完成 1已完成 
 */
export function getPointExchangeList(data) {
  return api.get('/point/exchange/list', data, { login: true })
}

/**
 * 积分兑换商品 
 */
export function exchangePoint(data) {
  return api.post('/point/exchange', data, { login: true })
}

export function getUserCouponList(data) {
  return api.get('/user/coupon/getUserCouponList', data, { login: true })
}

export function getPointTop50() {
  return api.get('/member/user/points/rank', {}, { login: true })
}
