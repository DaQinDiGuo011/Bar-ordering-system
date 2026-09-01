<template>
<view class="page">
  <uv-navbar
	  :fixed="true"
	  bgColor="#ffffff"
	  title="储值说明"
	  left-arrow
	  :placeholder="true"
  	  @leftClick="$onClickLeft"/>

  <view class="content">
    <view class="text">会员储值可享受优惠活动</view>
  </view>
</view>
</template>

<script setup>
// 纯静态页面，可后端接口动态拉取富文本
</script>

<style scoped>
page{background:#f7f7f7;}
.nav-bar {
  display:flex;
  align-items:center;
  justify-content:space-between;
  padding:30rpx;
  background:#fff;
}
.nav-title {font-size:38rpx;font-weight:500;}
.content {
  padding:40rpx 30rpx;
}
.text {font-size:38rpx;color:#222;line-height:1.8;}
</style>