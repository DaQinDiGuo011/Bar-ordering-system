<template>
<view class="page">
	<uv-navbar
		:fixed="true"
	  	bgColor="#ffffff"
	  	title="交易明细"
	  	left-arrow
	  	:placeholder="true"
	  @leftClick="$onClickLeft"/>
    <view class="page-content">
		<view class="tab-header">
			<view 
			class="tab-item" 
			:class="{active:activeTab===0}"
			@click="activeTab=0"
			>支出</view>
			<view 
			  class="tab-item" 
			  :class="{active:activeTab===1}"
			  @click="activeTab=1"
			>收入</view>
		</view>

		<!-- <view class="stat-text">
			{{activeTab===0 ? `我的余额:¥${balance} 累计支出:¥${payTotal}` : `累计充值:¥${rechargeTotal} 累计赠送:¥${giftTotal}`}}
		</view> -->

		<view class="empty" v-if="list.length===0 && !loading">
			<view class="empty-text">暂无相关内容~</view>
		</view>
		<scroll-view scroll-y v-else class="scroll-wrap" 
			refresher-enabled
			:refresher-triggered="loading"
			lower-threshold="100"
			@refresherrefresh="onRefresh"
			@scrolltolower="onLoadMore">
			<view class="scroll-inner">
				<view v-for="item in list" :key="item.id" class="log-item">
					<view class="log-left">
						<view class="log-title">{{item.mark}}</view>
						<view class="log-time">{{formatTime(item.createTime)}}</view>
					</view>
					<view class="log-money" :class="{'money-out':item.pm===0,'money-in':item.pm===1}">
						{{ item.pm ===0 ? '-' : '+' }}¥{{item.number}}
					</view>
				</view>
				<view class="footer-tip">
					<view v-if="loading">加载中...</view>
					<view v-if="noMore && !loading">没有更多数据</view>
				</view>
			</view>
		</scroll-view>
	</view>
</view>
</template>

<script setup>
import {ref,onMounted,watch} from 'vue'
import { getWalletLogList } from '@/api/wallet'
import { balanceGetBillList } from '@/api/user.js'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'


const main = useMainStore()
const { member,loginValueFlag } = storeToRefs(main)

const activeTab = ref(0)
const balance = ref(0)
const payTotal = ref(0)
const rechargeTotal = ref(0)
const giftTotal = ref(0)
const list = ref([])

const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const noMore = ref(false)

function checkLogin() {
    if (!loginValueFlag.value) {
	  uni.navigateTo({url:'/pages/components/pages/login/login'})
	  return false
	}
    return true
}

const formatTime = (timestamp)=>{
	if(!timestamp) return ''
	const date = new Date(timestamp)
	const y = date.getFullYear()
	const m = String(date.getMonth()+1).padStart(2,'0')
	const d = String(date.getDate()).padStart(2,'0')
	const hh = String(date.getHours()).padStart(2,'0')
	const mm = String(date.getMinutes()).padStart(2,'0')
	return `${y}-${m}-${d} ${hh}:${mm}`
}

const loadLog = async (isRefresh = false)=>{
	if(!checkLogin()) return
  
	if(loading.value) return
  
	if(isRefresh){
		page.value = 1
		noMore.value = false
	}
	
	loading.value = true
	
	let queryType = activeTab.value === 0 ? 1 : 2
	
	try{
	    const wallRes = await balanceGetBillList({
			cate: 0,
			type: queryType,
			page: page.value,
			pagesize: pageSize.value
	    })
	    console.log("------wallRes----",wallRes)
	
	    const arr = Array.isArray(wallRes) ? wallRes : []
	    if(isRefresh){
			list.value = arr
	    }else{
			list.value.push(...arr)
	    }
	
	    // 判断是否无更多
	    if(arr.length < pageSize.value){
			noMore.value = true
	    }else{
			page.value +=1
	    }
	
	}catch(e){
	    console.error(e)
	}finally{
	    loading.value = false
	}
	  
	// const res = await getWalletLogList({type:activeTab.value})
	// console.log("--------------res=",res)
	// if(res.code===200){
	// 	list.value = res.data.list
	// 	balance.value = res.data.balance
	// 	payTotal.value = res.data.payTotal
	// 	rechargeTotal.value = res.data.rechargeTotal
	// 	giftTotal.value = res.data.giftTotal
	// }
}
const onRefresh = ()=>{
	loadLog(true)
}
const onLoadMore = ()=>{
	console.log('触发上拉加载')
	if(noMore.value || loading.value) return
	loadLog(false)
}

watch(activeTab,()=>loadLog(true))
onMounted(()=>{
	loadLog(true)
})

</script>

<style scoped>
page {
	background:#f7f7f7;
	height:100%;
}
.page {
	height:100vh;
	display:flex;
	flex-direction:column;
}
.page-content{
	padding-top: var(--uv-navbar-height);
	display:flex;
	flex-direction:column;
	flex:1;
	overflow:hidden;
}

.tab-header {
  display:flex;
  background:#fff;
  flex-shrink:0; /* 禁止被压缩，固定 */
}
.tab-item {
  flex:1;
  text-align:center;
  font-size:40rpx;
  padding:30rpx 0;
  position:relative;
}
.tab-item.active::after {
  content:"";
  width:120rpx;
  height:6rpx;
  background:#e494af;
  position:absolute;
  bottom:0;
  left:50%;
  transform:translateX(-50%);
}
.stat-text {
  text-align:center;
  font-size:34rpx;
  color:#999;
  padding:30rpx 0;
  background:#f7f7f7;
  flex-shrink:0; /* 固定，不压缩 */
}

.scroll-container{
	flex:1;
	overflow:hidden;
}
.scroll-wrap{
	height:100%; /* flex父级分配剩余高度，不需要calc */
}

.empty {
  padding-top:200rpx;
  text-align:center;
}
.empty-text {font-size:36rpx;color:#999;}

.log-item {
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:30rpx;
  background:#fff;
  margin-bottom:10rpx;
}
.log-left{
	display:flex;
	flex-direction:column;
	gap:10rpx;
}
.log-title{
	font-size:34rpx;
	color:#222;
}
.log-time{
	font-size:26rpx;
	color:#999;
}
.log-money{
	font-size:36rpx;
	font-weight:500;
}
.money-out{
	color:#333333;
}
.money-in{
	color:#e494af;
}
.footer-tip{
	text-align:center;
	padding:30rpx;
	font-size:28rpx;
	color:#999;
}
.scroll-inner{
	padding-bottom: calc(env(safe-area-inset-bottom) + 60rpx);
}
</style>