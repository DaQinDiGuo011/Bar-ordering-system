<template>
	<uv-navbar
			:fixed="true"
		  	bgColor="#ffffff"
		  	title="联系客服"
		  	left-arrow
		  	:placeholder="true"
		  @leftClick="$onClickLeft"/>
<view class="page">
  <!-- Logo区域 -->
  <view class="logo-wrap">
    <uv-image class="logo-img" :src="store.logoImage" mode="aspectFill"></uv-image>
    <view class="shop-name">德旺酒馆</view>
  </view>

  <!-- 联系电话按钮 -->
  <view class="btn-wrap">
    <uv-button
      class="tel-btn"
      outline
      color="#e888a0"
      size="large"
      @click="callPhone"
    >
      <view class="btn-inner">
        <uv-icon name="phone" size="36"></uv-icon>
        <text class="btn-text">联系电话</text>
      </view>
    </uv-button>
  </view>
</view>
</template>

<script setup>
	import { storeToRefs } from 'pinia'
	import { useMainStore } from '@/store/store'
	import { onLoad} from '@dcloudio/uni-app'
	const main = useMainStore()
	const { store} = storeToRefs(main)
// 联系客服电话

// 拨打电话
const callPhone = () => {
  uni.makePhoneCall({
    phoneNumber: store.value.mobile,
    fail(){
      uni.showToast({title:"拨号失败",icon:"none"})
    }
  })
}
onLoad(()=>{
	console.log("----store-------",store)
})
</script>

<style scoped>
page{
  background: #f6f6f6;
}
.page{
  padding: 80rpx 40rpx;
}
.logo-wrap{
  margin-top: 120rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.logo-img{
  width: 320rpx;
  height: 320rpx;
  border-radius: 50%;
}
.shop-name{
  margin-top: 32rpx;
  font-size: 38rpx;
  color: #999;
}
.btn-wrap{
  margin-top: 120rpx;
}
.tel-btn{
  border-width: 2rpx;
  border-radius: 999rpx;
}
.btn-inner{
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.btn-text{
  font-size: 36rpx;
}
</style>