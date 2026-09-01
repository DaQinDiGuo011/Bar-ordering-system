const STORAGE_KEY = 'unhandled_order_list'
const localStorage = window.localStorage
/** 获取未处理订单列表 */
function getUnhandledOrders(): Array<{orderId:string}> {
  const str = localStorage.getItem(STORAGE_KEY)
  if(!str) return []
  try {
    return JSON.parse(str)
  }catch {
    return []
  }
}

function saveUnhandledOrders(list: Array<{orderId:string}>){
  localStorage.setItem(STORAGE_KEY, JSON.stringify(list))
}

/** ws收到推送：新增一条未处理订单，自动去重 */
export function addUnhandledOrder(orderId:string){
  const list = getUnhandledOrders()
  const exist = list.some(item=> item.orderId === orderId)
  if(!exist){
    list.push({orderId})
    saveUnhandledOrders(list)
  }
  return list.length
}

/**
 * ✅【只移除指定某一条订单，其他订单保留】
 * @param orderId 需要标记已处理的订单ID
 * @returns 返回处理后剩余未处理数量
 */
export function removeUnhandledOrder(orderId:string){
  const list = getUnhandledOrders()
  // filter过滤掉当前这条，其余全部保留
  const newList = list.filter(item=> item.orderId !== orderId)
  saveUnhandledOrders(newList)
  return newList.length
}

export function getUnhandledCount(){
  return getUnhandledOrders().length
}

