	<template>
	  <layout>
		<uv-sticky
		  bg-color="#F5F5F5"
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
				<view class="index-banner">
					<!-- <uv-swiper class="index-banner__swiper" height="300" imgMode="aspectFill" keyName="image" :list="listAds" indicatorMode="dot" indicatorStyle="bottom"></uv-swiper> -->
					<uv-image src="https://theonebar-dwjg.com/file/user/barLogo.jpg" width="100%" height="300px"></uv-image>
					<!-- <view class="index-banner__intro">
						<view class="index-banner__greet">您好，{{ isLogin ? member.nickname : '游客' }}</view>
						<view class="index-banner__note">德旺酒馆点餐系统</view>
					</view> -->
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
									<!-- <view style="height: 40px; line-height: 40px;font-size: 12px;">
										获取积分升级
									</view> -->
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
								<!-- <view class="member-code-box">
									<svg width="70px" height="70px" viewBox="0 0 200 200" @tap="clickVipFnt">
										  
										<rect x="0" y="0" width="70" height="70" fill="none" stroke="#e2e2e2" stroke-width="12"/>
										<rect x="24" y="24" width="22" height="22" fill="#e2e2e2"/>
										
										<rect x="130" y="0" width="70" height="70" fill="none" stroke="#e2e2e2" stroke-width="12"/>
										<rect x="154" y="24" width="22" height="22" fill="#e2e2e2"/>
										
										<rect x="0" y="130" width="70" height="70" fill="none" stroke="#e2e2e2" stroke-width="12"/>
										<rect x="24" y="154" width="22" height="22" fill="#e2e2e2"/>
										
										<rect x="162" y="162" width="35" height="35" fill="#e2e2e2"/>
										<rect x="124" y="124" width="35" height="35" fill="#e2e2e2"/>
										<rect x="124" y="182" width="20" height="20" fill="#e2e2e2"/>
										<rect x="176" y="124" width="20" height="20" fill="#e2e2e2"/>
									</svg>
									
									<view style="font-size: 12px;text-align: center;">会员码</view>
								</view> -->
								<view @click="openLogin" style="background-color: black; height: 30px; line-height: 30px;color: #ffffff; padding-left: 10px; padding-right: 10px; font-size: 14px; border-radius: 10px; margin-top: 40px;">
									立即登录
								</view>
								
							</view>
							
						</view>
						
						<view class="func-card">
							<view class="func-item" @click="takein">
							  <uv-icon name="empty-news" size="44" color="#fff"></uv-icon>
							  <view class="func-text">立即点单</view>
							  <view class="func-en">ORDER</view>
							</view>
						  <!-- <view class="func-item" @click="goReserve">
							<uv-icon name="home" size="44" color="#fff"></uv-icon>
							<view class="func-text">房台预定</view>
							<view class="func-en">ROOM RESERVATION</view>
						  </view> -->
						    <view class="func-item" @click="goCoupon">
								<view class="icon-badge-wrap">
								  <uv-icon name="folder" size="44" color="#fff"></uv-icon>
								  <uv-badge v-if="loginValueFlag && wineCount > 0" :value="wineCount" type="error"></uv-badge>
								</view>
								<view class="func-text">寄存</view>
								<view class="func-en">DEPOSIT</view>
							
						    </view>
						  
						</view>
						<!-- <view style=" margin: 10px 0; color: #ffb7df; border: 1px solid #ffb7df;">
							<view class="flex flex-column align-center index-menu__item ordering-bnt-cls" @tap="takein">
								<view><image src="/static/images/ordering.jpg" mode="aspectFit" class="index-menu__icon"></image></view>
								<view>立即点单</view>
								<view>ORDER</view>
							</view>
								<view class="flex flex-column align-center index-menu__item store-jifen-cls" @tap="goScore">
									<view><image src="/static/images/store.jpg" mode="aspectFit" class="index-menu__icon"></image></view>
									<view>TheOne积分商城</view>
									<view>STORE</view>
								</view>
						</view> -->
					</view>
					<!-- <view class="flex justify-between index-menu">
						<view class="flex flex-column align-center index-menu__item" @tap="takein">
							<view><image src="/static/images/new003.jpg" mode="aspectFit" class="index-menu__icon"></image></view>
							<view>自取</view>
							<view class="font-small text-light-black">下单免排队</view>
						</view>
						<view class="flex flex-column align-center index-menu__item" @tap="takeout">
							<view><image src="/static/images/new002.jpg" mode="aspectFit" class="index-menu__icon"></image></view>
							<view>外卖</view>
							<view class="font-small text-light-black">美食送到家</view>
						</view>
					</view> -->
					 <!-- <view class="index-card">
						<view class="index-card__main">
							<view class="index-card__header">
								<text class="index-card__title">我的卡券</text>
								<text class="index-card__value">{{member.couponCount}}</text>
							</view>
							<view class="index-card__desc">
								可抵扣商品价格哦
							</view>
						</view>
						<view class="index-card__action" @tap="coupons">
							去领个券
						</view>
					</view> -->
					<view class="rank-enter-card" @tap="goPointsLeaderboard">
						  <view class="rank-enter-left">
							<text class="rank-enter-title">🏆积分排行榜 <text style="font-size: 13px; color: #b70000;">线下活动才能获得积分</text></text>
							<text class="rank-enter-desc">查看全榜，比拼积分排名</text>
						  </view>
						  <view class="rank-enter-right">
							<uv-icon name="arrow-right" size="32"></uv-icon>
						  </view>
					</view>
					<!-- 1.VIP会员充值模块 -->
						<view class="vip-card" @tap="openVipCard">
						  <view class="vip-left">
							<text class="vip-title">VIP <text class="vip-sub">会员充值</text></text>
							<view class="vip-desc">福利多多</view>
						  </view>
						  <view class="vip-right">
							<view class="crown-box">
							  <svg width="100" height="80" viewBox="0 0 100 80">
								<path d="M10 75 L25 20 L40 45 L50 10 L60 45 L75 20 L90 75 Z" fill="#000"/>
								<circle cx="50" cy="10" r="6" fill="#000"/>
								<circle cx="25" cy="20" r="5" fill="#000"/>
								<circle cx="75" cy="20" r="5" fill="#000"/>
								<text x="38" y="62" font-size="22" fill="#F8C8D8">VIP</text>
							  </svg>
							</view>
						  </view>
						</view>
					
						<!-- 2.功能按钮区域 -->
						
					
						<!-- 3.门店信息卡片 -->
						<view class="shop-card">
						  <view class="shop-name">{{shop.name??'德旺酒馆'}}</view>
						  <view class="shop-tag">主营商品：{{shop.businessContent??'暂无'}}</view>
					
						  <uv-image class="shop-img" :src="homeImage" mode="aspectFill"></uv-image>
					
						  <view class="info-row">
							<uv-icon name="clock" size="22"></uv-icon>
							<text class="info-text">
								营业时间：{{shop.businessStartStr}} 
								<text v-if="shop.businessEndStr">- 次日 {{shop.businessEndStr}}</text>
								营业
							</text>
						  </view>
						  <view class="info-row">
							<uv-icon name="map-fill" size="22"></uv-icon>
							<text class="info-text">{{shop.address??'暂无'}}</text>
						  </view>
						  <view class="info-row">
							<uv-icon name="phone" size="22"></uv-icon>
							<text class="info-text">门店电话：{{shop.mobile??'暂无'}}</text>
						  </view>
					
						  <view class="btn-row">
							<view class="oper-btn" @click="openMap">
							  <uv-icon name="empty-address" size="26"></uv-icon>
							  <text>一键导航</text>
							</view>
							<view class="oper-btn" @click="callPhone">
							  <uv-icon name="phone" size="26"></uv-icon>
							  <text>联系电话</text>
							</view>
						  </view>
						</view>
					<!-- <view class="index-card">
						<view class="index-card__main" @tap="goScore">
							<view class="index-card__header">
								<text class="index-card__title">积分商城</text>
							</view>
							<view class="index-card__desc">
								进入积分商城兑换奈雪券及周边好礼
							</view>
						</view>
						<view class="index-card__action index-card__action--with-icon" @tap="goScore">
							<image src="/static/images/jifen.png" class="index-card__action-icon"></image>
							<text>逛一逛</text>
						</view>
					</view> -->
				</view>
		</view>
		<!-- 功能菜单网格 -->
		<!-- <view class="menu-wrap">
		  <view class="menu-grid">
			<view class="menu-item" v-for="(item, idx) in menuList" :key="idx" @tap="serv({type:item.type, pages: item.page, fnt: item.fnt})">
			  <uv-icon :name="item.icon" size="56rpx"></uv-icon>
			  <view class="menu-text" >{{item.title}}</view>
			</view>
		  </view>
		</view> -->
		<uv-popup ref="wifiPopup" mode="center" closeIconPos="top-right" custom-style="height: auto;width:80%; border-radius: 15px;">
			<view style="margin-top: 20px; height: 40px; line-height: 40px; text-align: center; font-weight: bolder; color: #c8730f;">
				wifi名：{{ shop.wifiInfo??'暂无' }}
				
			</view>
			<view style="height: 40px;line-height: 40px;text-align: center; font-weight: bolder; color: #c8730f; ">
				密码：{{ shop.wifiPwd??'暂无' }}
			</view>
			<view style="height: 40px; line-height: 40px;text-align: center; margin-bottom: 20px;">
				<uv-button type="success" @click="closeWifiPopup" text="关闭" style="width: 120rpx; height: 30px; margin-left: calc((100% - 120rpx)/2);"></uv-button>
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

	//const store = ref(main.store)
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
		// let shop_id = store.id ? store.id : 0;
		// let data = await menuAds({
		// 	shop_id: shop_id
		// });
		// if (data) {
		// 	listAds.value = data.list;
		// 	console.log('listAds:',listAds.value)
		// 	uni.setStorage({
		// 			key: 'isActive',
		// 			data: data.isActive
		// 		});
		// 	if(data.list.length > 0){
		// 		uni.setStorage({
		// 				key: 'shopAd',
		// 				data: data.list[0].image
		// 		 });
		// 		}
		// 	}
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

	// 寄存
	const goCoupon = () => {
		if(!main.loginValueFlag) {
			openLogin()
			return
		}
		uni.navigateTo({ url: "/pages/components/pages/winestoreMylist/winestoreMylist" })
	}
	// 查看wifi
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
	// 一键导航
	const openMap = () => {
	  uni.openLocation({
		latitude: Number(shop.value.lat),  // 自行替换门店真实纬度
		longitude: Number(shop.value.lng), // 自行替换门店真实经度
		name: shop.value.name,
		address: shop.value.address
	  })
	}
	// 拨打电话
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
	 // main.init()
	 
	  handGetListAds()
	  await getWineInfo()
	})

	</script>

	<style lang="scss">
	// 首页局部尺寸 token（与 uni.scss 全局变量配合）
	$index-banner-height: 300rpx;
	$index-menu-item-width: 355rpx;
	$index-card-radius: 10rpx;
	$index-content-padding-x: 15rpx;
	$index-intro-offset-x: 40rpx;
	$index-intro-offset-y: 50rpx;
	$index-value-font-size: 44rpx;
	$index-gap-sm: 10rpx;

	/* #ifdef H5 */
	page {
		height: auto;
		min-height: 100%;
	}
	/* #endif */
	.member-code-box {
	  width: 180rpx;
	  height: 180rpx;
	  background-color: #ffb7df;
	  display: flex;
	  flex-direction: column;
	  align-items: center;
	  justify-content: center;
	  margin-right: 13px;
	  gap: 36rpx;
	  margin-top: 10px;
	}
	.index-page {
		margin-bottom: 10px;
		--index-banner-height: #{$index-banner-height};
		--index-menu-item-width: #{$index-menu-item-width};

		.index-banner {
			position: relative;
			width: 100%;
			background-color: black;
			&__swiper {
				width: 100%;
				height: var(--index-banner-height);
			}

			&__intro {
				position: absolute;
				top: calc(#{$index-intro-offset-y} + var(--status-bar-height));
				left: $index-intro-offset-x;
				color: $text-color-white;
				display: flex;
				flex-direction: column;
			}

			&__greet {
				font-size: $font-size-lg;
				margin-bottom: $index-gap-sm;
			}

			&__note {
				font-size: $font-size-sm;
			}
		}

		.index-content {
			padding: 0 $index-content-padding-x;
		}
		.ordering-box{
			min-height: 100px;
			background-color: #f8c8d8;
			border-radius: 10px;
			margin-top: 10px;
		}
		.user-info-box{
			height: 120px;
			display: flex;
		}
		.user-image-info{
			height: 120px;
			width: 80px;
			:deep(.user-image-detail) {
				width: 60px;
				height: 60px;
				margin-left: 10px;
				margin-top: 30px;
			  }
			  :deep(.uv-avatar) {
				  margin-top: 30px;
				  margin-left: 10px;
				}
		}
		.user-detail-info{
			height: 100%;
			width: calc(100% - 160px);
		}
		.user-huiyuan-label{
			height: 30px; 
			padding: 0 10px; 
			line-height: 30px; 
			background-color: #ffe163;
			border-radius: 5px;
			font-weight: bolder;
			color: #b38400;
			margin-left: 10px;
			margin-top: 5px;
		}
		.ordering-bnt-cls{
			margin-left: 10px;
			margin-right: 5px;
		}
		.ordering-bnt-cls,.store-jifen-cls{
			background-color: black !important;
			border-radius: 15px;
			margin-bottom: 10px;
		}
		.store-jifen-cls{
			margin-left: 5px;
			margin-right: 10px;
		}
		.index-menu {
			margin-top: $spacing-row-base;

			&__item {
				width: var(--index-menu-item-width);
				padding-bottom: $spacing-row-lg;
				background-color: $text-color-white;
			}

			&__icon {
				width: 100rpx;
				height: 100rpx;
				margin-top: $spacing-row-lg;
			}
		}

		.index-card {
			position: relative;
			display: flex;
			align-items: center;
			justify-content: center;
			margin: $index-gap-sm 0;
			padding: 25rpx;
			border-radius: $index-card-radius;
			background-color: $text-color-white;
			box-shadow: $box-shadow;

			&__main {
				flex: 1;
				display: flex;
				flex-direction: column;
				justify-content: center;
			}

			&__header {
				display: flex;
				align-items: center;
			}

			&__title {
				margin-right: $index-gap-sm;
				font-size: $font-size-base;
				color: $text-color-base;
			}

			&__value {
				font-size: $index-value-font-size;
				font-weight: bold;
			}

			&__desc {
				display: flex;
				align-items: center;
				font-size: $font-size-sm;
				color: $text-color-assist;
			}

			&__action {
				display: flex;
				align-items: center;
				justify-content: center;
				font-size: $font-size-sm;
				color: $color-primary;

				&--with-icon {
					flex-direction: column;
				}
			}

			&__action-icon {
				width: $img-size-sm;
				height: $img-size-sm;
				margin-bottom: $index-gap-sm;
			}
		}
		/* VIP卡片 */
		.vip-card {
		  background: #f8c8d8;
		  border-radius: 24rpx;
		  display: flex;
		  justify-content: space-between;
		  padding: 32rpx;
		  margin-bottom: 24rpx;
		}
		.vip-title {
		  font-size: 64rpx;
		  font-weight: bold;
		  color: #000;
		}
		.vip-sub {
		  font-size: 36rpx;
		}
		.vip-desc {
		  font-size: 28rpx;
		  margin-top: 12rpx;
		}
		.vip-right {
		  display: flex;
		  align-items: center;
		}
		
		/* 功能按钮区 */
		.func-card {
		  // background: #f8c8d8;
		  border-radius: 24rpx;
		  display: flex;
		  padding: 24rpx;
		  gap: 16rpx;
		  margin-bottom: 24rpx;
		}
		.func-item {
		  flex: 1;
		  background: #000;
		  border-radius: 16rpx;
		  display: flex;
		  flex-direction: column;
		  align-items: center;
		  padding: 30rpx 10rpx;
		}
		.func-text {
		  color: #fff;
		  font-size: 32rpx;
		  margin-top: 12rpx;
		}
		.func-en {
		  color: #aaa;
		  font-size: 22rpx;
		  margin-top: 6rpx;
		}
		
		/* 门店信息卡片 */
		.shop-card {
		  background: #f8c8d8;
		  border-radius: 24rpx;
		  padding: 32rpx;
		}
		.shop-name {
		  font-size: 44rpx;
		  font-weight: bold;
		  color: #000;
		}
		.shop-tag {
		  font-size: 30rpx;
		  margin: 12rpx 0;
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
		}
	}
	.rank-enter-card {
	  background: #f8c8d8;
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
	}
	.rank-enter-desc {
	  font-size: 26rpx;
	  color: #717171;
	  margin-top: 8rpx;
	}
	.rank-enter-right {
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
