<template>
	<view class="page">
		<uv-navbar
			:fixed="true"
			bgColor="#ffffff"
			:title="title"
			:placeholder="true"
		/>
		<view class="page-content">
			<!-- 头部tabs区域 flex-shrink:0 固定住 bg-white，不会随列表滚动 -->
			<view class="bg-white tab-header-wrap">
				<uv-tabs :list="tabList" :current="current" @change="change" keyName="name" :scrollable="false"></uv-tabs>
			</view>

			<view class="empty-wrap" v-if="orders.length == 0 && !loading">
				<uv-empty mode="order"></uv-empty>
			</view>

			<!-- scroll‑view 负责下拉刷新、上拉加载，只有列表在这里滚动 -->
			<scroll-view
				scroll-y
				v-else
				class="scroll-wrap"
				refresher-enabled
				:refresher-triggered="loading"
				lower-threshold="100"
				@refresherrefresh="onRefresh"
				@scrolltolower="onLoadMore"
			>
				<view class="scroll-inner">
					<view class="order-item" v-for="(item, index) in orders" :key="index">
						<list-cell :hover="false">
							<view class="w-100 d-flex align-items-center">
								<view class="flex-fill d-flex flex-column">
									<view class="font-size-lg text-color-base order-item__shop">
										{{ item.shop.name }}
									</view>
									<view class="font-size-sm text-color-assist">取餐号：{{ item.numberId }}</view>
									<view class="font-size-sm text-color-assist">订单编号：{{ item.orderId }}</view>
								</view>
								<view class="font-size-lg text-color-primary">
									{{ item.statusDto.title }}
								</view>
							</view>
						</list-cell>
						<list-cell :hover="false" last>
							<view class="w-100 d-flex flex-column">
								<view class="w-100 text-truncate font-size-lg text-color-base order-item__goods">
									<view class="flex order-item__goods-row mb-2" v-for="(good,index) in item.cartInfo" :key="index">
										<image :src="good.image" mode="aspectFill" class="order-item__thumb"></image>
										<view class="flex flex-column">
											<view class="font-size-medium mt-1 text-color-base">{{ good.title }}</view>
											<view class="font-size-sm mt-1">{{ good.spec }}</view>
											<view class="font-size-sm mt-2">×{{ good.number }}  ¥{{ good.price }}</view>
										</view>
									</view>
								</view>
								<view class="d-flex justify-content-between align-items-center order-item__meta">
									<view class="font-size-sm text-color-assist">
										{{formatDateTime(item.createTime) }}
										<view v-if="item.paid === 0 && item.countdown > 0" class="countdown-text">
											{{formatCountdown(item.countdown)}} 后自动取消
										</view>
									</view>
									<view class="d-flex font-size-sm text-color-base align-items-center">
										<view class="order-item__summary">共{{ goodsNum(item.cartInfo) }}件商品，实付</view>
										<view class="font-size-lg">￥{{ item.payPrice }}</view>
									</view>
								</view>
								<view class="d-flex align-items-center justify-content-end order-item__actions">
									<button
										v-if="item.paid === 0"
										class="order-item__btn"
										plain
										size="mini"
										@tap.stop="cancelPayFnt(item.orderId)"
									>取消支付</button>
									<button
										v-if="item.paid === 0"
										class="order-item__btn"
										plain
										size="mini"
										@tap.stop="payOrder(item.orderId)"
									>继续支付</button>
									<button v-if="item.paid !== 0" class="order-item__btn" plain size="mini" @tap="detail(item.orderId)">订单详情</button>
								</view>
							</view>
						</list-cell>
					</view>

					<!-- 底部加载提示 -->
					<view class="footer-tip">
						<view v-if="loading">加载中...</view>
						<view v-if="noMore && !loading">没有更多数据</view>
					</view>
				</view>
			</scroll-view>
		</view>

		<uv-popup ref="loginPopup"
			mode="bottom"
			border-radius="24rpx"
			duration="600"
			mask-close-able
			:round="20"
			:closeOnClickOverlay="true"
			:safeAreaInsetBottom="false"
			z-index="9910"
			teleport>
			<login @close="closeLogin"></login>
		</uv-popup>
	</view>
</template>


<script setup>
import {
  ref,
  computed,
  nextTick,
  onUnmounted
} from 'vue'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'
//❗删掉原生页面滚动钩子 onPullDownRefresh,onReachBottom，不再使用
import { onShow} from '@dcloudio/uni-app'
import { formatDateTime } from '@/utils/util'
import {
  orderGetOrders,
  orderReceive,
  cancelPay
} from '@/api/order'
import login from '@/pages/login/login.vue'

const main = useMainStore()
const { loginValueFlag,store } = storeToRefs(main)
const title = ref('我的订单')
const loginPopup = ref(null)

const page = ref(1)
const pageSize = ref(10)
const orders = ref([])
const loading = ref(false)
const noMore = ref(false)

const tabList = ref([{
			type: -1,
			name: '全部',
		}, {
			type: 0,
			name: '待支付',
		}, {
			type: 1,
			name: '进行中'
		}, {
			type: 4,
			name: '已完成'
		}, {
			type: -3,
			name: '退款单'
		}]
)
const current = ref(0)
const type = ref(-1)

/** 订单未支付超时时间 单位秒 10分钟=600秒 */
const ORDER_PAY_EXPIRE_SEC = ref(0)
// 存储每个订单定时器id
const timerMap = ref(new Map())

const formatCountdown = (seconds) => {
  const m = Math.floor(seconds / 60).toString().padStart(2,'0')
  const s = Math.floor(seconds % 60).toString().padStart(2,'0')
  return `${m}:${s}`
}

const startOrderCountDown = (orderItem) => {
	console.log("-------进入-startOrderCountDown-----")
  // 清除旧定时器
  if(timerMap.value.has(orderItem.orderId)){
    clearInterval(timerMap.value.get(orderItem.orderId))
    timerMap.value.delete(orderItem.orderId)
  }
  //计算已经过去多少秒
  const createTs = new Date(orderItem.createTime).getTime()
  const nowTs = Date.now()
  const pastSec = Math.floor((nowTs - createTs)/1000)
  let remainSec = ORDER_PAY_EXPIRE_SEC.value - pastSec
  if(remainSec < 0) {
	  remainSec = 0
  }
  orderItem.countdown = remainSec

  if(remainSec <=0){
    //已经超时，直接刷新列表
    // getOrders(true)
    return
  }

  const timer = setInterval(()=>{
    orderItem.countdown -=1
    if(orderItem.countdown <=0){
      clearInterval(timer)
      timerMap.value.delete(orderItem.orderId)
      //倒计时结束，刷新订单
      getOrders(true)
    }
  },1000)
  timerMap.value.set(orderItem.orderId,timer)
}

/** 清除全部订单定时器 */
const clearAllTimer = ()=>{
  for(let [id,timer] of timerMap.value){
    clearInterval(timer)
  }
  timerMap.value.clear()
}

//页面销毁清除定时器
onUnmounted(()=>{
  clearAllTimer()
})

const closeLogin = ()=> {
	nextTick(()=>{
		loginPopup.value.close()
	})
}
const goodsNum = computed(() => {
	return (goods) => {
		let num = 0;
		goods.forEach(good => num += parseInt(good.number))
		return num;
	}
})
const cancelPayFnt = async(orderId) => {
	const confirm = await uni.showModal({title:'提示',content:`确定取消订单吗`})
	if(confirm.confirm){
		await cancelPay({id: orderId})
		getOrders(true)
	}
}
const payOrder = (orderId) => {
	uni.navigateTo({
		url: '/pages/components/pages/pay/pay?orderId=' + orderId
	})
}

// scroll‑view下拉刷新触发
const onRefresh = ()=>{
	getOrders(true)
}
// scroll‑view上拉加载更多触发
const onLoadMore = ()=>{
	if(noMore.value || loading.value) return
	getOrders(false)
}

onShow(()=>{
	console.log("-------teime==",store.value.orderUnpayCancelSecond)
	if(store.value.orderUnpayCancelSecond){
		ORDER_PAY_EXPIRE_SEC.value = store.value.orderUnpayCancelSecond
	}
	if(!loginValueFlag.value) {
		nextTick(()=>{
			loginPopup.value.open()
	})
	}else{
		getOrders(true)
	}
})

// tab栏切换
const change = (e) => {
	type.value = e.type
	getOrders(true)
}

const getOrders = async(isRefresh = false) => {
	if(loading.value) return
	loading.value = true

	if(isRefresh) {
		clearAllTimer()
		orders.value = []
		page.value = 1
		noMore.value = false
	}
	try {
		let ordersData = await orderGetOrders({page:page.value, limit:pageSize.value,type:type.value});
		if(ordersData && ordersData.length > 0) {
			if(isRefresh){
				orders.value = ordersData
			}else{
				orders.value.push(...ordersData)
			}
			orders.value.forEach(item=>{
			    if(item.paid === 0){
					startOrderCountDown(item)
			    }
			})
			if(ordersData.length < pageSize.value){
				noMore.value = true
			}else{
				page.value += 1
			}
		}else{
			noMore.value = true
		}
	} catch(err) {
		console.error('获取订单异常', err)
	} finally {
		loading.value = false
	}
}

const detail = (id) => {
	uni.navigateTo({
		url: '/pages/components/pages/orders/detail?id=' + id
	})
}
// 确认收到货
const receive  = async(order) => {
	let data = await orderReceive({uni:order.orderId});
	if (data) {
		await getOrders(true)
	}
}
</script>

<style lang="scss" scoped>
$order-list-padding-x: $spacing-row-base;
$order-list-padding-bottom: 0;
$order-item-gap: $spacing-row-lg;
$order-section-gap: $spacing-row-base;
$order-btn-gap: $spacing-row-base;
$order-summary-gap: $spacing-row-base;
$order-thumb-size: 160rpx;
$order-thumb-radius: 8rpx;

/* 页面整体flex布局 */
.page {
	height: 100vh;
	display: flex;
	flex-direction: column;
}
.page-content{
	padding-top: var(--uv-navbar-height);
	display: flex;
	flex-direction: column;
	flex: 1;
	overflow: hidden;
}

/* tabs头部 bg-white 固定，flex‑shrink:0，不会被压缩滚动 */
.tab-header-wrap {
	background:#ffffff;
	flex-shrink: 0;
}

.empty-wrap{
	flex:1;
	display:flex;
	align-items:center;
	justify-content:center;
}

/* scroll‑view占满剩余高度 */
.scroll-wrap{
	height:100%;
}
.scroll-inner{
	padding: $order-list-padding-x;
	padding-bottom: calc(env(safe-area-inset-bottom) + 100rpx);
}

.order-item {
	margin-bottom: $order-item-gap;

	&__shop {
		margin-bottom: $order-section-gap;
	}

	&__goods {
		margin-bottom: $order-section-gap;
	}

	&__thumb {
		flex-shrink: 0;
		width: var(--order-thumb-size);
		height: var(--order-thumb-size);
		margin-right: $spacing-row-lg;
		border-radius: $order-thumb-radius;
	}

	&__meta {
		margin-bottom: $order-item-gap;
	}

	&__summary {
		margin-right: $order-summary-gap;
	}

	&__btn + &__btn {
		margin-left: $order-btn-gap;
	}
}
.order-item__thumb {
	flex-shrink: 0 !important;
	width: $order-thumb-size;
	height: $order-thumb-size;
	margin-right: $spacing-row-lg;
	border-radius: $order-thumb-radius;
	object-fit: cover;
	background:#f4f4f4; // 图片空白兜底底色
	overflow:hidden;
}
.countdown-text{
  margin-left:20rpx;
  color:#f56c6c;
  font-weight: bolder;
}

.footer-tip{
	text-align:center;
	padding:30rpx;
	font-size:28rpx;
	color:#999;
}
</style>