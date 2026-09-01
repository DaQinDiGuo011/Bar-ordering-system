<template>
	<!-- <layout>
		<uv-navbar
		  :fixed="false"
		  :title="title"
		  left-arrow
		  @leftClick="$onClickLeft"
		/>
		<view class="container mine-page">
			<view class="mine-page__user-section">
				<view class="mine-user d-flex flex-column bg-white">

					<view class="mine-user__header d-flex align-items-center">
						<view class="mine-user__avatar rounded-circle">
							<image :src="isLogin ? member.avatar ? member.avatar : '/static/images/mine/default.png' : '/static/images/mine/default.png'"></image>
						</view>
						<view class="mine-user__info d-flex flex-column flex-fill overflow-hidden">
							<view v-if="isLogin"
								class="font-size-lg font-weight-bold d-flex justify-content-start align-items-center"
								@tap="serv({type:'pages',pages:'/pages/components/pages/mine/userinfo'})">
								<view class="text-truncate">{{ member.nickname }}</view>
								<view class="iconfont iconarrow-right line-height-100"></view>
							</view>
							<view v-else class="font-size-lg font-weight-bold" @tap="login">游客</view>
						</view>
					</view>
					<view class="mine-user__stats w-100 d-flex align-items-center just-content-center">
						<view class="mine-user__stat" @tap="serv({type:'pages',pages:'/pages/components/pages/coupons/coupons'})">
							<view class="value font-size-extra-lg font-weight-bold text-color-base">
								{{ isLogin ? member.couponCount : 0}}
							</view>
							<view class="font-size-sm text-color-assist">优惠券</view>
						</view>
						<view class="mine-user__stat" @tap="serv({type:'pages', pages: '/pages/components/pages/balance/bill?cate=1'})">
							<view class="value font-size-extra-lg font-weight-bold text-color-base">
								{{ isLogin ? member.integral : 0 }}
							</view>
							<view class="font-size-sm text-color-assist">积分</view>
						</view>
						<view class="mine-user__stat">
							<view class="value font-size-extra-lg font-weight-bold text-color-base">
								{{ isLogin ? member.nowMoney : 0 }}
							</view>
							<view class="font-size-sm text-color-assist">余额</view>
						</view>
						<view class="mine-user__stat" @tap="serv({type:'pages', pages: '/pages/components/pages/balance/bill?cate=0'})">
							<view class="value font-size-extra-lg font-weight-bold text-color-base">
								{{ isLogin ? member.sumMoney : 0 }}
							</view>
							<view class="font-size-sm text-color-assist">历史消费</view>
						</view>
					</view>
				</view>

			</view>
			<view class="mine-service">
				<view class="mine-service__title font-size-lg text-color-base font-weight-bold">我的服务</view>
				<view class="u-m-t-20">
					<uv-cell-group>
						<block v-for="(item, index) in services" :key='index'>
							<uv-cell :title="item.name" v-if="item.type == 'contact'" :isLink="true">
								<template #icon>
									<image :src="item.image" class="mine-service__icon mr-1"></image>
								</template>
							</uv-cell>
							<uv-cell :isLink="true" :title="item.name" v-else-if="item.type == 'call'" v-on:click="makePhoneCall(item.phone)">
								<template #icon>
									<image :src="item.image" class="mine-service__icon mr-1"></image>
								</template>
							</uv-cell>
							<uv-cell :isLink="true" :title="item.name" v-else @tap="serv(item)">
								<template #icon>
									<image :src="item.image" class="mine-service__icon mr-1"></image>
								</template>
							</uv-cell>
						</block>
					</uv-cell-group>
				</view>
			</view>
		</view>
	</layout> -->
	<uv-navbar
	  :fixed="true"
	  bgColor="#ffffff"
	  :title="title"
	  :is‑back="false"
	  :placeholder="true"/>
	<view class="page-mine">
	  <!-- 顶部标题区域 -->
		<view class="header-top">
			  <view @tap="openUserInfo" v-if="loginValueFlag" >
				<view class="welcome-text">欢迎回来</view>
				<view class="username" @tap="clickName">
					{{ loginValueFlag ? member.nickname : '暂无名字' }}
					<view style="display: inline-block;">
						<uv-icon name="arrow-right" size="28" bold="true"></uv-icon>
					</view>
					
				</view>  
				
			  </view>
			  <view v-else>
				  <view class="username"> 游客</view>  
				  <view class="onlogin-text" @click="loginFnt">立即登录</view>
			  </view>
			
			<uv-avatar class="avatar" size="100" :src="loginValueFlag ? member.avatar ? member.avatar : '/static/images/mine/default.png' : '/static/images/mine/default.png'"></uv-avatar>
		</view>
		
		  <!-- 余额 积分 优惠券 统计行 -->
		<view class="stat-row">
			<view class="stat-item" @tap="serv({type:'pages', pages: '/pages/components/pages/newbalance/newbalance'})">
			  <view class="stat-num">{{ loginValueFlag ? member.nowMoney : '*' }}</view>
			  <view class="stat-label">余额</view>
			</view>
			<view class="stat-item" >
			  <view class="stat-num">{{ loginValueFlag ? member.integral : '*'  }}</view>
			  <view class="stat-label">积分</view>
			</view>
			<view class="stat-item" @tap="serv({type:'pages', pages:'/pages/components/pages/coupons/coupons'})">
			  <view class="stat-num">{{ loginValueFlag ? member.couponNum ?? 0 : '*' }}</view>
			  <view class="stat-label">优惠券</view>
			</view>
		</view>
		
		<!-- 会员卡片 -->
		<view class="member-card">
			<view class="member-left">
			    <view class="member-level">普通会员</view>
			    <!-- <view class="member-tip">获取积分升级</view> -->
			</view>
			<!-- @tap="serv({type:'pages', pages: '/pages/components/pages/vipCard/vipCard'})" -->
			<view class="member-right" >
				<view class="card-btn">会员卡</view>
				<view class="card-link">会员权益</view>
			</view>
		</view>
		
		<!-- 功能菜单网格 -->
		<view class="menu-wrap">
			<view class="menu-grid">
				<view class="menu-item" v-for="(item, idx) in menuList" :key="idx" @tap="serv({type:item.type, pages: item.page, fnt: item.fnt})">
					<div v-if="item.showBadge && wineCount && wineCount > 0" class="uni-badge-left-margin">
						<uv-badge type="error" max="99" :value="wineCount">
						</uv-badge>
					</div>
					<uv-icon :name="item.icon" size="56rpx"></uv-icon>
					<view class="menu-text" >{{item.title}}</view>
				</view>
			</view>
		</view>
	    <uv-popup ref="wifiPopup" mode="center" closeIconPos="top-right" custom-style="height: auto;width:80%; border-radius: 15px;">
			<view style="margin-top: 20px; height: 40px; line-height: 40px; text-align: center; font-weight: bolder; color: #c8730f;">
				wifi名：{{ store.wifiInfo??'暂无' }}
				
			</view>
			<view style="height: 40px;line-height: 40px;text-align: center; font-weight: bolder; color: #c8730f; ">
				密码：{{ store.wifiPwd??'暂无' }}
			</view>
			<view style="height: 40px; line-height: 40px;text-align: center; margin-bottom: 20px;">
				<uv-button type="success" @click="closeWifiPopup" text="关闭" style="width: 120rpx; height: 30px; margin-left: calc((100% - 120rpx)/2);"></uv-button>
			</view>
		</uv-popup>
		
		<uv-popup
		      ref="showPrivacyPopupRef"
		      mode="bottom"
		      border-radius="24rpx"
		      width="86%"
		      :closeOnClickOverlay="false"
			  z-index="9999"
		    >
		    <view class="privacy-box">
				<text class="popup-title">用户隐私保护提示</text>
				<view class="popup-content">
					<text>在你使用 TheOne Bar618 服务之前，请仔细阅读</text>
					<text 
						class="link-text"
						@click="goToPrivacyDoc"
					  >《TheOne Bar618小程序隐私保护指引》</text>
					<text>如你同意该指引，请点击“同意”开始使用本小程序。</text>
				</view>
				<view class="btn-row">
					<uv-button
						class="btn-refuse"
						type="default"
						color="#cecece"
						@click="handleRefuse"
					>拒绝</uv-button>
					<uv-button
						class="btn-accept"
						type="primary"
						@click="handleAccept"
					>同意</uv-button>
				</view>
		    </view>
		</uv-popup>
		
		<!-- 2.底部选择菜单 uv-action-sheet -->
		<uv-action-sheet
		    ref="showAvatarSheetRef"
		    :actions="avatarActionList"
			:safeAreaInsetBottom="false"
			custom-style="bottom: 0 !important;"
		    cancel-text="取消"
		    @select="onSheetSelect"
			z-index="9999"
		  />
		<updateImage ref="updateImageRef" @updageImage="updageImage"></updateImage>
		
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
  computed, nextTick
} from 'vue'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'
import { onLoad,onShow,onPullDownRefresh } from '@dcloudio/uni-app'
import { formatDateTime,kmUnit } from '@/utils/util'
import {
  userGetUserInfo,
  userEdit
} from '@/api/user'
import { VUE_APP_UPLOAD_URL } from '@/config';
import updateImage from '@/pages/login/updateImage.vue'
import login from '@/pages/login/login.vue'
import { getWineCount } from '@/api/wallet.js'

const main = useMainStore()
const { member,loginValueFlag,openid, lang, store } = storeToRefs(main)
const showPrivacyPopupRef = ref(null)
// 头像底部菜单
const showAvatarSheetRef = ref(null)
const updateImageRef = ref(null)

const title = ref('个人中心')
const services = ref([])
const wifiPopup = ref(null)
const avatarUrl = ref('')
const loginPopup = ref(null)
const wineCount = ref(0)

// 底部菜单选项
const avatarActionList = ref([
  {
    name: '用微信头像',
	id: '1'
  },
  {
    name: '从相册选择',
	id: '2'
  },
  {
    name: '拍照',
	id: '3'
  }
])
onPullDownRefresh(async ()=>{
  await getUserInfo();
  await getWineInfo()
  uni.stopPullDownRefresh(); //停止下拉动画
})
const growthValue = computed(() => { 
	if (!loginValueFlag.value) return 0
	const {
		currentValue,
		needValue
	} = member.value
	return currentValue / (currentValue + needValue) * 100
})
const openWifiPupop = ()=>{
	nextTick(()=>{
		wifiPopup.value.open()
	})
}
const closeWifiPopup = ()=>{
	nextTick(()=>{
		wifiPopup.value.close()
	})
}
const closeLogin = ()=> {
	nextTick(()=>{
		loginPopup.value.close()
	})
}
const menuList = ref([
  // { type:'pages', icon: "map", title: "我的地址", page:'/pages/components/pages/address/address' },
  { type:'tab', icon: "order", title: "我的订单" , page:'/pages/order/order'},
  { type:'pages', icon: "red-packet", title: "充值中心" , page:'/pages/components/pages/recharge/recharge'},
  { type:'pages', icon: "coupon", title: "领券中心" , page:'/pages/components/pages/coupons/coupons'},
  { type:'pages', icon: "checkmark-circle", title: "寄存", page: '/pages/components/pages/winestoreMylist/winestoreMylist', showBadge:'true'},
  // { type:'pages', icon: "gift", title: "积分商城", page:'/pages/components/pages/pointShop/pointShop' },
  // { type:'pages', icon: "email", title: "会员中心", page: '/pages/components/pages/vipCard/vipCard'},
  { type:'fnt', icon: "empty-wifi-off", title: "wifi" , page:'/', fnt: openWifiPupop},
  { type:'pages', icon: "kefu-ermai", title: "联系客服" , page:'/pages/components/pages/contactService/contactService'},
])
	
const openUserInfo = ()=>{
	uni.navigateTo({
		url: '/pages/components/pages/mine/userinfo', // 你的隐私协议页面路径
		animationType:"slide-in-right", // 新页面从右侧滑进来
		animationDuration:600 // 动画时长ms
	})
	
}
// 点击头像打开弹窗
// const openPrivacyPopup = () => {
//   if(!loginValueFlag.value){
// 	  return
//   }
//   nextTick(()=>{
//   	// showPrivacyPopupRef.value.open()
// 	updateImageRef.value.openPrivacyPopup()
//   })
  
// }

// 跳转隐私协议页面
const goToPrivacyDoc = () => {
  uni.navigateTo({
    url: '/pages/privacy/privacy' // 你的隐私协议页面路径
  })
}

// 拒绝隐私协议
const handleRefuse = () => {
  uni.showToast({
    title: '您拒绝隐私协议，无法使用授权功能',
    icon: 'none'
  })
  nextTick(()=>{
  	showPrivacyPopupRef.value.close()
	setTimeout(()=>uni.showTabBar(),300)
  })
}

// 同意隐私协议
const handleAccept = () => {
  nextTick(()=>{
  	showPrivacyPopupRef.value.close()
	setTimeout(()=>uni.showTabBar(),300)
  })
  // =========在这里写【微信头像昵称授权登录逻辑】=========
  setTimeout(()=>{
      showAvatarSheetRef.value.open()
    }, 300)
  // 调用你之前写好的 wxLogin() 登录函数
}
const getWineInfo = async()=>{
	wineCount.value = await getWineCount()
}
onLoad(() => {
	// getServices();
})	
onShow(() => {
	getUserInfo();
	getWineInfo()
})

const updageImage = (iamgeUrl)=>{
		avatarUrl.value = iamgeUrl
	}
const getUserInfo = async() => {
	if (loginValueFlag.value) {
		let data = await userGetUserInfo();
		if (data) {
			data.couponNum = data.couponCount
			main.SET_MEMBER(data);
		}
	}
}
// const getServices = async() => {
// 	let data = await mineService();
// 	if (data) {
// 		services.value = data;
// 	}
// }
const makePhoneCall = (phoneNumber) => {
	uni.makePhoneCall({
		phoneNumber: phoneNumber,
	})
}
// 底部菜单选中回调
const onSheetSelect = async (item) => {
  switch(item.id){
    case '1':
      // 调用微信授权获取微信头像（你的wxLogin相关逻辑）
      getWechatAvatar()
      break
    case '2':
      chooseImageFromAlbum()
      break
    case '3':
      takePhoto()
      break
  }
}
// =========功能方法=========
// 使用微信头像
const getWechatAvatar = () => {
  // 这里调用 uni.getUserProfile 获取微信头像昵称
  uni.createSelectorQuery()
      .select('[open-type="chooseAvatar"]')
      .node()
      .exec((res) => {
        if(res[0]?.node){
          res[0].node.click()
        }
      })
}

// 相册选择
const chooseImageFromAlbum = () => {
  uni.chooseImage({
    count:1,
    sourceType:['album'],
    success: (res)=>{
      const tempPath = res.tempFilePaths[0]
      uploadImage(tempPath)
    }
  })
}

const uploadImage = (filePath) => {
	uni.uploadFile({
		url: VUE_APP_UPLOAD_URL, 
		filePath: filePath,
		name: 'file',
		header: {
			Authorization: 'Bearer ' + member.value.accessToken,
			lang: lang.value,
			'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
		},
		success(uploadFileResult){
			if (uploadFileResult) {
				const upload = JSON.parse(uploadFileResult.data);
				member.value.avatar = upload.data;
			}
		}, 
		fail(e){
			console.log(e)
		}
	});
}

const editImage = () => {
	userEdit
}

// 拍照
const takePhoto = () => {
  uni.chooseImage({
    count:1,
    sourceType:['camera'],
    success: (res)=>{
      const tempPath = res.tempFilePaths[0]
      // 上传图片逻辑
    }
  })
}

const clickName = () => {
	if (!loginValueFlag.value) {
		loginFnt()
	}else{
		
	}
}
const loginFnt = () => {
	nextTick(()=>{
		loginPopup.value.open()
	})
}
const packages = () => {
	if (!loginValueFlag.value) {
		loginFnt()
		return
	}
	uni.navigateTo({
		url: '/pages/components/pages/packages/index'
	})
}
const serv = (item) => {
	if(!item.pages){
		uni.showToast({type: 'default',title:"该功能暂未开放"})
		return
	}
	switch (item.type) {
		case 'pages':
			if (!loginValueFlag.value) {
				loginFnt()
				return
			}
			uni.navigateTo({
				url: item.pages
			})
			break;
		case 'miniprogram':
			uni.navigateToMiniProgram({
				appId: item.app_id
			})
			break;
		case 'menu':
			uni.navigateTo({
				url: '/pages/components/pages/mine/service?id=' + item.id + '&name=' + item.name
			})
			break;
		case 'content':
			// uni.navigateTo({
			// 	url: '/pages/components/pages/mine/content?id=' + item.id + '&name=' + item.name
			// })
			break;
		case 'tab':
			uni.switchTab({
				url: item.pages
			});
		case 'fnt':
			item.fnt()
	}
}



</script>

<style lang="scss" scoped>
// 个人中心页局部 token（与 uni.scss 全局变量配合）
// $mine-section-padding-x: $spacing-row-lg;
// $mine-user-box-radius: $border-radius-lg;
// $mine-user-box-margin-top: 70rpx;
// $mine-user-box-margin-bottom: $spacing-row-lg;
// $mine-avatar-size: 160rpx;
// $mine-avatar-image-size: 140rpx;
// $mine-avatar-offset-top: -35rpx;
// $mine-avatar-margin-x: 35rpx;
// $mine-avatar-radius: 20rpx;
// $mine-avatar-shadow: 0 0 20rpx rgba(0, 0, 0, 0.2);
// $mine-stat-width: 25%;
// $mine-stat-padding: $spacing-row-lg;
// $mine-stat-value-gap: $spacing-row-base;
// $mine-info-margin-top: $spacing-row-base;
// $mine-service-padding-y: 32rpx;
// $mine-service-padding-bottom: 10rpx;
// $mine-service-icon-size: $img-size-sm;
// $mine-service-title-gap: $spacing-row-base;

// /* #ifdef H5 */
// page {
// 	height: auto;
// 	min-height: 100%;
// }
// /* #endif */

// .mine-page {
// 	--mine-avatar-size: #{$mine-avatar-size};
// 	--mine-avatar-image-size: #{$mine-avatar-image-size};
// 	--mine-service-icon-size: #{$mine-service-icon-size};

// 	&__user-section {
// 		padding: 0 $mine-section-padding-x;
// 	}
// }

// .mine-user {
// 	position: relative;
// 	margin-top: $mine-user-box-margin-top;
// 	margin-bottom: $mine-user-box-margin-bottom;
// 	border-radius: $mine-user-box-radius;
// 	box-shadow: $box-shadow;

// 	&__info {
// 		margin-top: $mine-info-margin-top;
// 	}

// 	&__avatar {
// 		position: relative;
// 		display: flex;
// 		align-items: center;
// 		justify-content: center;
// 		flex-shrink: 0;
// 		width: var(--mine-avatar-size);
// 		height: var(--mine-avatar-size);
// 		margin-top: $mine-avatar-offset-top;
// 		margin-left: $mine-avatar-margin-x;
// 		margin-right: $mine-avatar-margin-x;
// 		border-radius: $mine-avatar-radius;
// 		background-color: $text-color-white;
// 		box-shadow: $mine-avatar-shadow;

// 		image {
// 			width: var(--mine-avatar-image-size);
// 			height: var(--mine-avatar-image-size);
// 			border-radius: $border-radius-circle;
// 		}
// 	}

// 	&__stat {
// 		display: flex;
// 		flex-direction: column;
// 		align-items: center;
// 		justify-content: center;
// 		width: $mine-stat-width;
// 		padding: $mine-stat-padding;

// 		.value {
// 			margin-bottom: $mine-stat-value-gap;
// 		}
// 	}
// }

// .mine-service {
// 	width: 100%;
// 	padding: $mine-service-padding-y $mine-section-padding-x $mine-service-padding-bottom;
// 	background-color: $text-color-white;
// 	box-shadow: $box-shadow;

// 	&__title {
// 		margin-bottom: $mine-service-title-gap;
// 	}

// 	&__icon {
// 		width: var(--mine-service-icon-size);
// 		height: var(--mine-service-icon-size);
// 		flex-shrink: 0;
// 	}
// }
page {
  background-color: #f8f8f8;
}
.page-mine {
  padding: 30rpx;
  background: #f8f8f8;
  min-height: 100vh;
  background-image: linear-gradient(135deg, #ffffff 0%, #f7f7f7 100%);
  // padding-top: var(--uv-navbar-height);
}

/* 顶部欢迎和头像 */
.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-top: 40rpx;
}
.welcome-text {
  font-size: 42rpx;
  color: #333;
  
}
.onlogin-text{
	font-size: 42rpx;
	color: #333;
	border: 1px solid #afa6ac;
	border-radius: 5px; 
	padding: 0 5px;
}
.username {
  font-size: 64rpx;
  font-weight: bold;
  color: #222;
  margin-top: 16rpx;
}
.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  margin-right: 40px;
}

/* 余额积分优惠券 */
.stat-row {
  display: flex;
  margin-top: 60rpx;
}
.stat-item {
  flex: 1;
  text-align: center;
}
.stat-num {
  font-size: 52rpx;
  font-weight: 500;
}
.stat-label {
  font-size: 30rpx;
  color: #666;
  margin-top: 12rpx;
}

/* 会员卡片 */
.member-card {
  background-color: #e494af;
  border-radius: 32rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 50rpx 40rpx;
  margin-top: 50rpx;
  color: #fff;
}
.member-level {
  font-size: 48rpx;
  font-weight: 500;
}
.member-tip {
  font-size: 34rpx;
  color: #f1d3dd;
  margin-top: 16rpx;
}
.member-right {
  text-align: right;
}
.card-btn {
  font-size: 40rpx;
}
.card-link {
  font-size: 34rpx;
  margin-top: 12rpx;
}

/* 菜单区域 */
.menu-wrap {
  background: #ffffff;
  border-radius: 32rpx;
  padding: 40rpx 20rpx;
  margin-top: 50rpx;
}
.menu-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40rpx 20rpx;
}
.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.menu-text {
  font-size: 32rpx;
  color: #333;
  margin-top: 16rpx;
}
/* 弹窗样式 */
.privacy-box {
  padding: 60rpx 40rpx;
}
.popup-title {
  font-size: 42rpx;
  font-weight: 500;
  display: block;
  margin-bottom: 30rpx;
}
.popup-content {
  font-size: 32rpx;
  color: #666;
  line-height: 1.7;
}
.link-text {
  color: #3678d8;
}
.btn-row {
  display: flex;
  margin-top: 60rpx;
  gap: 30rpx;
  justify-content: center;
}
.btn-refuse, .btn-accept {
  flex: 1;
  height: 88rpx;
  border-radius: 16rpx;
}
.uni-badge-left-margin {
	position: relative;
		:deep(.uv-badge) {
			position: absolute;
			top: -14rpx;
			right: -70rpx;
		}
}
</style>
