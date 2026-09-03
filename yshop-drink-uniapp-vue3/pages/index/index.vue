<template>
  <layout>
	<uv-sticky
	  bg-color="transparent"
	  offset-top="0"
	  customNavHeight="0"
	>
	  <uv-navbar
		:fixed="false"
		:safeAreaInsetTop="true"
		height="0"
		bgColor="transparent"
		leftIcon=""
	  />
	  <!-- #ifndef MP -->
	  <blank size="10"></blank>
	  <!-- #endif -->

	  <blank size="5"></blank>
	</uv-sticky>

	<blank size="10"></blank>
	<view class="container index-page">
			<view class="swiper-content">
				<uv-swiper
					  v-if="listAds.length > 0"
					  height="220"
					  imgMode="aspectFill"
					  keyName="image"
					  :list="listAds"
					  indicatorMode="dot"
					  indicatorStyle="bottom"
					></uv-swiper>
			</view>
			<view class="index-content">
				<view class="ordering-box">
					<view class="user-info-box">
						<view class="user-image-info">
							
							<uv-avatar class="user-image-detail" size="60" :src="loginValueFlag ? member.avatar ? member.avatar : '/static/images/mine/default.png' : '/static/images/mine/default.png'"></uv-avatar>
						</view>
						<view class="user-detail-info">
							<view v-if="loginValueFlag">
								<view style="height: 40px; line-height: 40px; display: flex;margin-top: 30px;">
									<view style="font-weight: bolder;">Hi, {{ member.nickname }}</view>
									<view class="user-huiyuan-label">普通会员</view>
								</view>
								<view style="height: 40px; line-height: 40px; font-size: 12px; display: flex;"> 
									<view>积分: {{member.integral}}</view>
									<view style="margin-left: 10px;">余额: {{member.nowMoney}}</view>
									<view style="margin-left: 10px;" @tap="coupons">优惠券: {{member.couponNum ?? 0}}</view>
								</view>
							</view>
							<view v-else>
								<view style="height: 40px; line-height: 40px; display: flex;margin-top: 20px;">
									欢迎光临，请登录
								</view>
								<view style="height: 40px; line-height: 40px; font-size: 12px; display: flex;"> 
									成为会员，享受更多会员权益
								</view>
							</view>
						</view>
						<view v-if="!loginValueFlag" style="width: 180rpx;border-radius: 8rpx;display: flex;flex-direction: column;align-items: center;box-sizing: border-box;">
							<view @click="openLogin" style="background-color: #d4af37; height: 30px; line-height: 30px;color: #000; padding-left: 10px; padding-right: 10px; font-size: 14px; border-radius: 10px; margin-top: 40px;font-weight:bold;">
								立即登录
							</view>
							
						</view>
						
					</view>
					
					<view class="func-card">
						<view class="func-item" @click="takein">
						  <uv-icon name="empty-news" size="44" color="#ffd76e"></uv-icon>
						  <view class="func-text">立即点单</view>
						  <view class="func-en">ORDER</view>
						</view>
						  <view class="func-item" @click="goCoupon">
								<view class="icon-badge-wrap">
								  <uv-icon name="folder" size="44" color="#ffd76e"></uv-icon>
								  <uv-badge v-if="loginValueFlag && wineCount > 0" :value="wineCount" type="error"></uv-badge>
								</view>
								<view class="func-text">寄存</view>
								<view class="func-en">DEPOSIT</view>
						    </view>
					</view>
				</view>

				<view class="rank-enter-card" @tap="goPointsLeaderboard">
					  <view class="rank-enter-left">
						<text class="rank-enter-title">🏆积分排行榜 <text style="font-size: 13px; color: #ff9494;">线下活动才能获得积分</text></text>
						<text class="rank-enter-desc">查看全榜，比拼积分排名</text>
					  </view>
					  <view class="rank-enter-right">
						<uv-icon name="arrow-right" size="32" color="#ffd76e"></uv-icon>
					  </view>
				</view>

					<view class="vip-card" @tap="openVipCard">
					  <view class="vip-left">
						<text class="vip-title">VIP <text class="vip-sub">会员充值</text></text>
						<view class="vip-desc">福利多多</view>
					  </view>
					  <view class="vip-right">
						<view class="crown-box">
						  <svg width="100" height="80" viewBox="0 0 100 80">
							<path d="M10 75 L25 20 L40 45 L50 10 L60 45 L75 20 L90 75 Z" fill="#ffd76e"/>
							<circle cx="50" cy="10" r="6" fill="#ffd76e"/>
							<circle cx="25" cy="20" r="5" fill="#ffd76e"/>
							<circle cx="75" cy="20" r="5" fill="#ffd76e"/>
							<text x="38" y="62" font-size="22" fill="#222">VIP</text>
						  </svg>
						</view>
					  </view>
					</view>

					<view class="shop-card">
					  <view class="shop-name">{{shop.name??'德旺酒馆'}}</view>
					  <view class="shop-tag">主营商品：{{shop.businessContent??'暂无'}}</view>
					
					  <uv-image class="shop-img" :src="homeImage" mode="aspectFill"></uv-image>
					
					  <view class="info-row">
						<uv-icon name="clock" size="22" color="#ffd76e"></uv-icon>
						<text class="info-text">
							营业时间：{{shop.businessStartStr}} 
							<text v-if="shop.businessEndStr">- 次日 {{shop.businessEndStr}}</text>
							营业
						</text>
					  </view>
					  <view class="info-row">
						<uv-icon name="map-fill" size="22" color="#ffd76e"></uv-icon>
						<text class="info-text">{{shop.address??'暂无'}}</text>
					  </view>
					  <view class="info-row">
						<uv-icon name="phone" size="22" color="#ffd76e"></uv-icon>
						<text class="info-text">门店电话：{{shop.mobile??'暂无'}}</text>
					  </view>
					
					  <view class="btn-row">
						<view class="oper-btn" @click="openMap">
						  <uv-icon name="empty-address" size="26" color="#ffd76e"></uv-icon>
						  <text>一键导航</text>
						</view>
						<view class="oper-btn" @click="callPhone">
						  <uv-icon name="phone" size="26" color="#ffd76e"></uv-icon>
						  <text>联系电话</text>
						</view>
					  </view>
					</view>
			</view>
	</view>

	<uv-popup ref="wifiPopup" mode="center" closeIconPos="top-right" custom-style="height: auto;width:80%; border-radius: 15px;background:#2c1044;border:2rpx solid #d4af37;">
		<view style="margin-top: 20px; height: 40px; line-height: 40px; text-align: center; font-weight: bolder; color: #ffd76e;">
			wifi名：{{ shop.wifiInfo??'暂无' }}
			
		</view>
		<view style="height: 40px;line-height: 40px;text-align: center; font-weight: bolder; color: #ffd76e; ">
			密码：{{ shop.wifiPwd??'暂无' }}
		</view>
		<view style="height: 40px; line-height: 40px;text-align: center; margin-bottom: 20px;">
			<uv-button type="warning" @click="closeWifiPopup" text="关闭" style="width: 120rpx; height: 30px; margin-left: calc((100% - 120rpx)/2);"></uv-button>
		</view>
	</uv-popup>
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
  </layout>
</template>

<script setup>
import {
  ref, nextTick
} from 'vue'
import { onLoad ,onPullDownRefresh} from '@dcloudio/uni-app'
import {
  menuAds
} from '@/api/market'
import { storeToRefs } from 'pinia'
import { useMainStore } from '@/store/store'
import login from '@/pages/login/login.vue'
import { getShopInfo } from '@/api/goods.js'
import {
  userGetUserInfo
} from '@/api/user'
import { getWineCount } from '@/api/wallet.js'
const main = useMainStore()
const { member,store, loginValueFlag} = storeToRefs(main)

const listAds = ref([])
const wineCount = ref(0)
const wifiPopup = ref(null)
const loginPopup = ref(null)
const shop = ref({})
const homeImage = ref('https://theonebar-dwjg.com/file/user/home.jpg')
	
const closeWifiPopup = ()=>{
	nextTick(()=>{
		wifiPopup.value.close()
	})
}
const handGetListAds = async () => {
	
	let data = await getShopInfo({})
	shop.value = data
	main.SET_STORE(data);
	if(data.images && data.images.length > 0){
		 homeImage.value = data.images[0]
		 
	}
	
	
	let adRaw = data.clientAdImages
	let tempAdList = []
	if(adRaw){
		if(typeof adRaw === 'string'){
			tempAdList = adRaw.split(',').filter(s=>!!s.trim())
		}else if(Array.isArray(adRaw)){
			tempAdList = adRaw
		}
	}
	listAds.value = tempAdList.map(imgUrl=>{
		return { image: imgUrl }
	})
	
}
const openVipCard = ()=>{
	if(!main.loginValueFlag) {
		openLogin()
		return
	}
	uni.navigateTo({url: '/pages/components/pages/recharge/recharge'})
}
const takein = () => {
	main.SET_ORDER_TYPE('takein')
	uni.switchTab({
		url: '/pages/menu/menu'
	})
}
const openLogin = ()=> {
	nextTick(()=>{
		loginPopup.value.open()
	})
}
const closeLogin = ()=> {
	nextTick(()=>{
		loginPopup.value.close()
	})
}
const clickVipFnt = ()=>{
	if(!main.loginValueFlag) {
		openLogin()
		return
	}
	uni.navigateTo({
		url: '/pages/components/pages/payQrcode/payQrcode'
	})
}
const takeout = () => {
	main.SET_ORDER_TYPE('takeout')
	uni.switchTab({
		url: '/pages/menu/menu'
	}) 
}

const coupons = () => { 
	if(!main.loginValueFlag) {
		openLogin()
		return
	}
	uni.navigateTo({
		url: '/pages/components/pages/coupons/coupons'
	})
}

const goScore = () => { 
	if(!main.loginValueFlag) {
		openLogin()
		return
	}
	uni.navigateTo({
		url: '/pages/components/pages/pointShop/pointShop'
	})
}

const goCoupon = () => {
	if(!main.loginValueFlag) {
		openLogin()
		return
	}
	uni.navigateTo({ url: "/pages/components/pages/winestoreMylist/winestoreMylist" })
}
const goWifi = () => {
  nextTick(()=>{
	wifiPopup.value.open()
  })
}
const goPointsLeaderboard = () => {
  uni.navigateTo({
    url: '/pages/components/pages/pointsLeaderboard/pointsLeaderboard'
  })
}
const openMap = () => {
  uni.openLocation({
	latitude: Number(shop.value.lat),
	longitude: Number(shop.value.lng),
	name: shop.value.name,
	address: shop.value.address
  })
}
const callPhone = () => {
  uni.makePhoneCall({
	phoneNumber: shop.value.mobile
  })
}

const getWineInfo = async()=>{
	wineCount.value = await getWineCount()
}
const getUserInfo = async() => {
	if (main.loginValueFlag) {
		let data = await userGetUserInfo();
		if (data) {
			data.couponNum = data.couponCount
			main.SET_MEMBER(data);
		}
	}
}
onPullDownRefresh(async() => {
	handGetListAds()
	getUserInfo()
	await getWineInfo()
	uni.stopPullDownRefresh(); 
})
onLoad(async() => {
  handGetListAds()
  await getWineInfo()
})

</script>

<style lang="scss">
$gold-color: #ffd76e;
$gold-border: 2rpx solid #d4af37;
$bg-dark-start:#541f7e;
$bg-dark-end:#b6377e;

page{
	background-image: linear-gradient(180deg, $bg-dark-start 0%, $bg-dark-end 100%);
	min-height:100%;
}

.index-page {
	margin-bottom: 10px;
	
	.swiper-content {
		margin: 40rpx 15rpx 20rpx 15rpx;
		border-radius: 16rpx;
		overflow: hidden;
		border:$gold-border;
	}

	.index-content {
		padding: 0 15rpx;
	}

	.ordering-box{
		margin-top: 10px;
	}
	.user-info-box{
		min-height: 140rpx;
		display: flex;
		background:rgba(255,255,255,0.12);
		border:$gold-border;
		border-radius:16rpx;
		padding:20rpx;
	}
	.user-image-info{
		width: 80px;
		  :deep(.uv-avatar) {
			  margin-top: 30px;
			  margin-left: 10px;
			}
	}
	.user-detail-info{
		height: 100%;
		flex:1;
		color:#fff;
	}
	.user-huiyuan-label{
		height: 30px; 
		padding: 0 10px; 
		line-height: 30px; 
		background-color: $gold-color;
		border-radius: 5px;
		font-weight: bolder;
		color: #222;
		margin-left: 10px;
		margin-top: 5px;
	}

	.func-card {
	  display: flex;
	  padding: 24rpx 0;
	  gap: 16rpx;
	}
	.func-item {
	  flex: 1;
	  background:rgba(0,0,0,0.35);
	  border:$gold-border;
	  border-radius: 16rpx;
	  display: flex;
	  flex-direction: column;
	  align-items: center;
	  padding: 30rpx 10rpx;
	}
	.func-text {
	  color: $gold-color;
	  font-size: 32rpx;
	  margin-top: 12rpx;
	}
	.func-en {
	  color: #ddbc70;
	  font-size: 22rpx;
	  margin-top: 6rpx;
	}

	.rank-enter-card {
	  background:rgba(0,0,0,0.35);
	  border:$gold-border;
	  border-radius: 24rpx;
	  padding: 32rpx;
	  margin-bottom: 24rpx;
	  display: flex;
	  justify-content: space-between;
	  align-items: center;
	}
	.rank-enter-left {
	  display: flex;
	  flex-direction: column;
	}
	.rank-enter-title {
	  font-size: 34rpx;
	  font-weight: bold;
	  color:$gold-color;
	}
	.rank-enter-desc {
	  font-size: 26rpx;
	  color: #e2c888;
	  margin-top: 8rpx;
	}

	/* VIP卡片 */
	.vip-card {
	  background:rgba(0,0,0,0.35);
	  border:$gold-border;
	  border-radius: 24rpx;
	  display: flex;
	  justify-content: space-between;
	  padding: 32rpx;
	  margin-bottom: 24rpx;
	}
	.vip-title {
	  font-size: 64rpx;
	  font-weight: bold;
	  color: $gold-color;
	}
	.vip-sub {
	  font-size: 36rpx;
	}
	.vip-desc {
	  font-size: 28rpx;
	  margin-top: 12rpx;
	  color:#e2c888;
	}
	.vip-right {
	  display: flex;
	  align-items: center;
	}
	
	/* 门店信息卡片 */
	.shop-card {
	  background:rgba(0,0,0,0.35);
	  border:$gold-border;
	  border-radius: 24rpx;
	  padding: 32rpx;
	}
	.shop-name {
	  font-size: 44rpx;
	  font-weight: bold;
	  color: $gold-color;
	}
	.shop-tag {
	  font-size: 30rpx;
	  margin: 12rpx 0;
	  color:#e2c888;
	}
	.shop-img {
	  width: 100%;
	  height: 280rpx;
	  border-radius: 16rpx;
	  margin: 20rpx 0;
	}
	.info-row {
	  display: flex;
	  align-items: flex-start;
	  margin: 16rpx 0;
	  gap: 12rpx;
	}
	.info-text {
	  font-size: 30rpx;
	  flex: 1;
	  color:#fff;
	}
	.btn-row {
	  display: flex;
	  justify-content: flex-end;
	  gap: 40rpx;
	  margin-top: 30rpx;
	}
	.oper-btn {
	  display: flex;
	  flex-direction: column;
	  align-items: center;
	  font-size: 26rpx;
	  color:$gold-color;
	}
}

.icon-badge-wrap {
	position: relative;
	:deep(.uv-badge) {
		position: absolute;
		top: -14rpx;
		right: -18rpx;
	}
}
</style>