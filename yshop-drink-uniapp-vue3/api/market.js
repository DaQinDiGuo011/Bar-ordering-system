import api from './api'

/**
 * shopGetList 
 */
export function shopGetList(data) {
  return api.get('/store/list', data, { login: false })
}


export function menuAds(data) {
  return api.get('/ad/list', data, { login: false })
}

export function getReserveTableList(data){
	return api.get('/reserve/getTableList', data)
}

export function getMyReserveOrder(data){
	return api.get('/reserve/myOrder', data)
}

export function createMyReserveOrder(data){
	return api.post('/reserve/create', data)
}

export function cancelMyReserveOrder(data){
	return api.post('/reserve/cancel', data)
}