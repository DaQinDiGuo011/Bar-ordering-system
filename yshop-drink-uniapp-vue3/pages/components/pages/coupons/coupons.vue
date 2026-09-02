<template>
<view class="page">
	<uv-navbar
		:fixed="true"
		bgColor="#ffffff"
		title="优惠券"
		left-arrow
		:placeholder="true"
		@leftClick="$onClickLeft"/>
    <view class="page-content">
		<!-- tab-header 文档流，不fixed，flex-shrink:0禁止压缩 -->
		<view class="tab-header coupons-tabbar">
			<view 
				class="tab-item" 
				:class="{active:activeTab===0}"
				@click="activeTab=0"
			>未使用优惠券</view>
			<view 
			  class="tab-item" 
			  :class="{active:activeTab===1}"
			  @click="activeTab=1"
			>已使用优惠券</view>
		</view>

		<view class="empty" v-if="list.length===0 && !loading">
			<uv-empty mode="list"></uv-empty>
		</view>
		<scroll-view scroll-y v-else class="scroll-wrap" 
			refresher-enabled
			:refresher-triggered="loading"
			lower-threshold="100"
			@refresherrefresh="onRefresh"
			@scrolltolower="onLoadMore">
			<view class="scroll-inner">
				<view 
					v-for="item in list" 
					:key="item.id" 
					class="coupons-item"
					:class="[
						item.status === 0 ? 'coupons-item--unused' : '',
						item.status === 1 ? 'coupons-item--used' : '',
						item.status === 2 ? 'coupons-item--expired' : ''
					]"
					@tap="openDetailModal(item)"
				>
					<!-- 券卡片，带撕裂缺口 -->
					<view class="coupons-ticket">
						<!-- 过期水印 -->
						<view class="coupons-expired-watermark" v-if="item.status ===2">已失效</view>

						<view class="coupons-ticket__body">
							<view class="coupons-ticket__left">
								<image
									class="coupons-ticket__picture"
									src="/static/images/coupon.jpg"
									mode="aspectFill"
								></image>
								<view class="coupons-ticket__intro">
									<view class="coupons-ticket__value">
										￥
										<text class="coupons-ticket__amount">{{item.value}}</text>
										<view>满{{item.least}}减{{item.value}}</view>
									</view>
									<view class="coupons-ticket__type">{{ item.title }}</view>
									<view class="coupons-ticket__date u-line-1">
										{{formatDateTime(item.startTime, 'yyyy-MM-dd')}}‑{{formatDateTime(item.endTime, 'yyyy-MM-dd')}}
									</view>
								</view>
							</view>
							<view class="coupons-ticket__right" @click.stop>
								<view v-if="item.status ===0" class="coupons-ticket__btn coupons-ticket__btn--use" :round="true" @tap="useCouponWith(item)">立即使用</view>
								<view v-if="item.status ===1" class="coupons-ticket__btn coupons-ticket__btn--used">已使用</view>
								<view v-if="item.status ===2" class="coupons-ticket__btn coupons-ticket__btn--expired">已失效</view>
							</view>
						</view>
					</view>
				</view>
				<view class="footer-tip">
					<view v-if="loading">加载中...</view>
					<view v-if="noMore && !loading">没有更多数据</view>
				</view>
			</view>
		</scroll-view>
	</view>

	<uv-modal custom :show="detailModalVisible" @cancel="closeDetailModal" width="90%" title="优惠券详情">
		<view class="modal-content">
			<view class="d-flex font-size-extra-lg text-color-base justify-content-center mb-20">{{ coupon.title }}</view>
			<view class="d-flex font-size-sm text-color-base mb-20">
				有效期：{{formatDateTime(coupon.startTime, 'yyyy-MM-dd')}}‑{{formatDateTime(coupon.endTime, 'yyyy-MM-dd')}}
			</view>
			<view class="d-flex font-size-sm text-color-base mb-20">
				领取时间：{{formatDateTime(coupon.createTime)}}
			</view>
			<view class="d-flex font-size-sm text-color-base mb-20">
				券价值：满{{ coupon.least }}减{{ coupon.value }}
			</view>
			<view class="d-flex font-size-sm text-color-base mb-20" v-if="activeTab == 1">
				每人限领：{{ coupon.limit }} 张
			</view>
			<view class="d-flex font-size-sm text-color-base mb-20">
				适用范围：{{typeInfo(coupon.type)}}
			</view>
			<view class="d-flex font-size-sm text-color-base mb-20">
				适用店铺：{{coupon.shopName}}
			</view>
		</view>
	</uv-modal>
	<uv-toast ref="uToast"></uv-toast>
</view>
</template>

<script setup>
import {ref,onMounted,watch} from 'vue'
import { couponMine } from '@/api/coupon'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'

const main = useMainStore()
const { loginValueFlag } = storeToRefs(main)

const activeTab = ref(0) // 0未使用(包含未使用+已失效)，1已使用
const list = ref([])

const page = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const noMore = ref(false)

const detailModalVisible = ref(false)
const coupon = ref({})
const uToast = ref()

function checkLogin() {
    if (!loginValueFlag.value) {
	  uni.navigateTo({url:'/pages/components/pages/login/login'})
	  return false
	}
    return true
}

const formatDateTime = (timestamp,fmt)=>{
	if(!timestamp) return ''
	const date = new Date(timestamp)
	const y = date.getFullYear()
	const m = String(date.getMonth()+1).padStart(2,'0')
	const d = String(date.getDate()).padStart(2,'0')
	return `${y}-${m}-${d}`
}

// 使用范围
const typeInfo = (type) => {
	if (type == 0) return '通用'
	if (type == 1) return '自取'
	if (type == 2) return '外卖'
	return '通用'
}

const loadData = async (isRefresh = false)=>{
	if(!checkLogin()) return
	if(loading.value) return

	if(isRefresh){
		page.value = 1
		noMore.value = false
	}
	
	loading.value = true
	
	try{
	    const res = await couponMine({
			type: activeTab.value,
			page: page.value,
			pagesize: pageSize.value
	    })
	    const arr = Array.isArray(res) ? res : []
	    if(isRefresh){
			list.value = arr
	    }else{
			list.value.push(...arr)
	    }

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
}

const onRefresh = ()=>{
	loadData(true)
}
const onLoadMore = ()=>{
	if(noMore.value || loading.value) return
	loadData(false)
}

const openDetailModal = (item)=>{
	coupon.value = {...item}
	detailModalVisible.value = true
}
const closeDetailModal = ()=>{
	detailModalVisible.value = false
	coupon.value = {}
}
const useCouponWith = (item)=>{
	uni.switchTab({url:'/pages/menu/menu'})
}

watch(activeTab,()=>loadData(true))
onMounted(()=>{
	loadData(true)
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

.tab-header.coupons-tabbar {
  display:flex;
  background:#fff;
  flex-shrink:0;
}
.tab-item {
  flex:1;
  text-align:center;
  font-size:32rpx;
  padding:28rpx 0;
  position:relative;
}
.tab-item.active::after {
  content:"";
  width:100rpx;
  height:6rpx;
  background:#e494af;
  position:absolute;
  bottom:0;
  left:50%;
  transform:translateX(-50%);
}

.empty {
  padding-top:200rpx;
  text-align:center;
}

.scroll-wrap{
	height:100%;
}

.scroll-inner{
	padding:20rpx;
	padding-bottom: calc(env(safe-area-inset-bottom) + 170rpx);
}

/* 券卡片基础 */
.coupons-item {
	position:relative;
	background:#fff;
	border-radius:16rpx;
	margin-bottom:24rpx;
	overflow:hidden;
}
/* 券中间撕裂缺口 左右两个半圆缺口 */
.coupons-ticket{
	position:relative;
}
.coupons-ticket::before{
	content:"";
	width:20rpx;
	height:20rpx;
	border-radius:50%;
	background:#f7f7f7;
	position:absolute;
	left:0;
	top:50%;
	transform:translate(-50%,-50%);
	z-index:2;
}
.coupons-ticket::after{
	content:"";
	width:20rpx;
	height:20rpx;
	border-radius:50%;
	background:#f7f7f7;
	position:absolute;
	right:0;
	top:50%;
	transform:translate(50%,-50%);
	z-index:2;
}

.coupons-ticket__body {
	display:flex;
	flex-direction:row;
	padding:30rpx 30rpx 30rpx 40rpx;
	position:relative;
}
/* 中间分割虚线 */
.coupons-ticket__body::after{
	content:"";
	width:2rpx;
	background:#ddd;
	background-image: linear-gradient(to bottom,#ddd 50%,transparent 50%);
	background-size:2rpx 12rpx;
	height:80rpx;
	position:absolute;
	left:calc(100% - 140rpx);
	top:50%;
	transform:translateY(-50%);
}

.coupons-ticket__left {
	display:flex;
	flex:1;
	align-items:center;
	gap:24rpx;
}
.coupons-ticket__picture {
	width:120rpx;
	height:120rpx;
	border-radius:12rpx;
	flex-shrink:0;
}
.coupons-ticket__intro {
	display:flex;
	flex-direction:column;
	gap:12rpx;
}
.coupons-ticket__value {
	display:flex;
	align-items:baseline;
	gap:8rpx;
}
.coupons-ticket__amount {
	font-size:48rpx;
	font-weight:bold;
	color:#e494af;
}
.coupons-ticket__type {
	font-size:30rpx;
	color:#222;
}
.coupons-ticket__date {
	font-size:26rpx;
	color:#999;
}
.coupons-ticket__right {
	width:120rpx;
	display:flex;
	align-items:center;
	justify-content:center;
	padding-left:20rpx;
}
.coupons-ticket__btn {
	padding:12rpx 24rpx;
	border-radius:8rpx;
	font-size:28rpx;
}
.coupons-ticket__btn--use {
	background:#e494af;
	color:#fff;
}
.coupons-ticket__btn--used {
	background:#eeeeee;
	color:#999999;
}
.coupons-ticket__btn--expired {
	background:#f4f4f4;
	color:#bbbbbb;
}

/* -------- 三种状态样式 -------- */
/* 0 未使用 默认样式 */
.coupons-item--unused{

}
/* 1 已使用：整体置灰 */
.coupons-item--used {
	filter:grayscale(80%);
}
.coupons-item--used .coupons-ticket__amount{
	color:#999;
}
/* 2 已失效：置灰 + 斜水印 */
.coupons-item--expired {
	filter:grayscale(80%);
}
.coupons-item--expired .coupons-ticket__amount{
	color:#999;
}
/* 过期斜水印 */
.coupons-expired-watermark{
	position:absolute;
	right:-60rpx;
	top:20rpx;
	z-index:3;
	width:240rpx;
	text-align:center;
	font-size:32rpx;
	color:#cccccc;
	border:2rpx solid #dddddd;
	transform:rotate(30deg);
	padding:4rpx 0;
}

.footer-tip{
	text-align:center;
	padding:30rpx;
	font-size:28rpx;
	color:#999;
}
</style>