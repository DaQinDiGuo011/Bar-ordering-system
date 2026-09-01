<template>
<view class="page">
	<uv-navbar
		:fixed="true"
		bgColor="#ffffff"
		title="券包购买记录"
		left-arrow
		:placeholder="true"
	  @leftClick="$onClickLeft"/>
  <view class="empty">
    <image class="empty-img" src="/static/empty_record.png"></image>
    <view class="empty-text">暂无相关内容~</view>
  </view>
</view>
</template>

<script setup>
// 可自行请求接口渲染列表
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
.empty {
  padding-top:200rpx;
  text-align:center;
}
.empty-img {width:320rpx;height:320rpx;}
.empty-text {font-size:36rpx;color:#999;margin-top:40rpx;}
</style>