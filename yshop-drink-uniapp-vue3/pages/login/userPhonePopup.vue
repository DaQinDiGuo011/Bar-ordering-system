<template>
	<view>
		<uv-popup
		  ref="showPhonePopupRef"
		  mode="bottom"
		  border-radius="24rpx"
		  :closeOnClickOverlay="false"
		  z-index="9990"
		  teleport
		>
		  <view class="login-popup" @click.stop>
		    <view class="header-row">
		      <view class="logo"></view>
		      <text class="app-name">TheOne Bar618</text>
		      <uv-icon name="info-circle" size="38rpx" color="#999"></uv-icon>
		    </view>
		
		    <view class="main-title">申请获取并验证你的手机号</view>
		    <view class="sub-desc">手机号码用于门店联系</view>
		
		    <!-- ✅重点：直接使用原生button，不再隐藏！去除模拟点击逻辑 -->
		    <button
		      open-type="getPhoneNumber"
		      @getphonenumber="onGetPhoneNumber"
		      class="phone-box"
		    >
		      <!-- <text class="phone-num">147****9579</text> -->
		      <text class="phone-tip">微信绑定号码</text>
		    </button>
		
		    <view class="btn-refuse" @click="closePopup">
		      不允许
		    </view>
		
		    <view class="other-text" @click="showCodeLogin">
		      使用其它号码
		    </view>
		  </view>
		</uv-popup>
	</view>
  
</template>

<script setup>
import {ref, nextTick} from 'vue'
import { loginByPhone } from '@/api/auth.js'
// import { apiWxPhoneLogin } from '@/api/login.js'
const emit = defineEmits(['close','loginSuccess'])
const showPhonePopupRef = ref(null)

// 上层传入昵称、头像
const tempNickname = ref('')
const tempAvatar = ref('')

// 对外暴露，接收昵称头像
const openPhonePopup = (nick, avatar) => {
  tempNickname.value = nick
  tempAvatar.value = avatar
  nextTick(()=>{
    showPhonePopupRef.value.open()
  })
}

const closePopup = ()=>{
  nextTick(()=>{
    showPhonePopupRef.value.close()
  })
}

// =====【核心】微信手机号授权回调 =====
const onGetPhoneNumber = async (e) => {
  console.log('手机号授权回调',e)
  if(e.detail.errMsg !== 'getPhoneNumber:ok'){
    uni.showToast({title:'您拒绝获取手机号，无法快捷登录',icon:'none'})
    return
  }
  const { cloudID, encryptedData, iv } = e.detail;

  uni.showLoading({title:'登录中...'})
  try{
    const loginRes = await new Promise((resolve,reject)=>{
          uni.login({success:resolve,fail:reject})
        })
        const jsCode = loginRes.code
    
        const res = await apiWxPhoneLogin({
          jsCode,
          phoneCode: code,
          nickname: tempNickname.value,
          avatar: tempAvatar.value
        })
		uni.hideLoading()
		console.log('login-ers ====',res)
		// uni.setStorage({
		// 	key: 'userinfo',
		// 	data: data.userInfo
		// });
		// uni.setStorage({
		// 	key: 'accessToken',
		// 	data: data.accessToken
		// });
		// main.SET_MEMBER(data.userInfo);
		// main.SET_TOKEN(data.accessToken);
		
    // if(res.code === 200){
      uni.showToast({title:'登录成功'})
	  console.log("---===---res==",res)
      closePopup()
      emit('loginSuccess')
    // }
  }catch(err){
    uni.hideLoading()
    uni.showToast({title:'登录失败',icon:'none'})
  }
}

const showCodeLogin = ()=>{
  uni.showToast({title:'功能开发中',icon:'none'})
}

defineExpose({
  openPhonePopup,closePopup
})
</script>

<style scoped>
.login-popup{
  padding:60rpx 48rpx;
}
.header-row{
  display:flex;
  align-items:center;
  gap:20rpx;
}
.logo{
  width:64rpx;
  height:64rpx;
  border-radius:50%;
  background:#663333;
}
.app-name{
  font-size:38rpx;
  font-weight:500;
  flex:1;
}
.main-title{
  font-size:42rpx;
  font-weight:bold;
  margin-top:44rpx;
  color:#111;
}
.sub-desc{
  font-size:32rpx;
  color:#909090;
  margin:16rpx 0 50rpx;
}
/* 重置button默认样式，和原来view样式保持一致 */
.phone-box{
  background:#fff;
  border:1rpx solid #efefef;
  border-radius:16rpx;
  padding:40rpx 20rpx;
  text-align:center;
  display:block;
}
.phone-num{
  font-size:44rpx;
  color:#222;
  display:block;
}
.phone-tip{
  font-size:28rpx;
  color:#bbbbbb;
  margin-top:12rpx;
  display:block;
}
.btn-refuse{
  margin-top:32rpx;
  background:#ffffff;
  border:1rpx solid #efefef;
  text-align:center;
  padding:40rpx;
  border-radius:16rpx;
  font-size:40rpx;
  color:#111;
}
.other-text{
  text-align:center;
  margin-top:56rpx;
  font-size:34rpx;
  color:#4468b2;
}
</style>