	<template>
	  <view class="score-rank-page">
		<uv-navbar
				  :fixed="true"
				  bgColor="#ffffff"
				  title="积分排行榜"
				  left-arrow
				  :placeholder="true"
				  @leftClick="$onClickLeft"/>
		
		<!-- 头部：我的排行 -->
		<view class="my-rank-card">
			<uv-notice-bar :text="text" mode="closable"></uv-notice-bar>
			<view class="my-rank-title">积分排行</view>
			<view class="my-rank-info">
				<view class="my-rank-num" v-if="myRank.rank > 0">第{{myRank.rank}}名</view>
				<view class="my-rank-num empty" v-else>未上榜</view>
				<view class="my-avatar">
				  <image :src="myRank.avatar?myRank.avatar:'/static/images/mine/default.png'" mode="aspectFill"></image>
				</view>
				<view class="my-name">{{myRank.nickname}}</view>
				<view class="my-score">{{myRank.integral}}积分</view>
			</view>
		</view>

		<!-- 排行榜列表 -->
		<scroll-view class="rank-scroll" :refresher-triggered="refresherTriggered" scroll-y @refresherrefresh="onRefresh" @scrolltolower="loadMore" refresher-enabled>
		  <view class="rank-list">
			<!-- 前3名特殊样式 -->
			<view class="top-three">
			  <view class="rank-item top-item rank-2" v-if="rankList[1]">
				<view class="crown">🥈</view>
				<image class="avatar" :src="rankList[1].avatar?rankList[1].avatar:'/static/images/mine/default.png'" mode="aspectFill"></image>
				<view class="nickname">{{rankList[1].nickname}}</view>
				<view class="score">{{rankList[1].integral}}积分</view>
			  </view>
			  <view class="rank-item top-item rank-1" v-if="rankList[0]">
				<view class="crown">🥇</view>
				<image class="avatar" :src="rankList[0].avatar?rankList[0].avatar:'/static/images/mine/default.png'" mode="aspectFill"></image>
				<view class="nickname">{{rankList[0].nickname}}</view>
				<view class="score">{{rankList[0].integral}}积分</view>
			  </view>
			  <view class="rank-item top-item rank-3" v-if="rankList[2]">
				<view class="crown">🥉</view>
				<image class="avatar" :src="rankList[2].avatar?rankList[2].avatar:'/static/images/mine/default.png'" mode="aspectFill"></image>
				<view class="nickname">{{rankList[2].nickname}}</view>
				<view class="score">{{rankList[2].integral}}积分</view>
			  </view>
			</view>

			<!-- 4‑50名普通列表 -->
			<view class="normal-list">
			  <view class="rank-item normal-item" v-for="(item, index) in rankList.slice(3)" :key="item.userId">
				<view class="rank-no">{{index + 4}}</view>
				<image class="avatar" :src="item.avatar?item.avatar:'/static/images/mine/default.png'" mode="aspectFill"></image>
				<view class="nickname">{{item.nickname}}</view>
				<view class="score">{{item.integral}}积分</view>
			  </view>
			</view>

			<!-- 没有更多（最多50条） -->
			<view class="no-more" v-if="rankList.length >= 50">已展示全部前50名</view>
		  </view>
		</scroll-view>
	  </view>
	</template>

	<script setup lang="ts">
	import { ref,nextTick } from 'vue'
	import * as API from '@/api/user.js'
	import { onLoad} from '@dcloudio/uni-app'
	import { useMainStore } from '@/store/store'
	const text = "注：积分为店里娱乐游戏才能获取，不涉及任何金钱交易"
	const main = useMainStore()
	interface RankItem {
	  userId: number
	  nickname: string
	  avatar: string
	  integral: number
	  rankNo: number
	  id: number
	}

	const myRank = ref<RankItem & {rank: number}>({
	  userId: 0,
	  nickname: '',
	  avatar: '',
	  integral: 0,
	  rank: 0,
	  rankNo: 0,
	  id: 0
	})

	const rankList = ref<RankItem[]>([])

	async function getRankData() {
	  try {
		const data = await API.getPointTop50()
		rankList.value = data
		
		data.forEach(info =>  {
			if(info.id === main.member.id){
				myRank.value.rank = info.rankNo
			}
		})
	  } catch(e) {
		console.error('获取排行榜失败', e)
	  }
	}
	const refresherTriggered = ref(false)

	async function onRefresh() {
		refresherTriggered.value = true
		await getRankData()
		refresherTriggered.value = false
	}

	function loadMore() {
	  if(rankList.value.length >= 50) return
	}

	onLoad(()=>{
		nextTick(()=>{
			myRank.value.avatar = main.member.avatar
			myRank.value.nickname = main.member.nickname
			myRank.value.integral = main.member.integral
			getRankData()
		})
	})
	</script>

	<style scoped>
	.score-rank-page {
		background: #f5f7fa;
		height: 100vh;
		display: flex;
		flex-direction: column;
		overflow: hidden;
	}

	.my-rank-card {
	  background: linear-gradient(135deg,#409eff,#7cb305);
	  color: #fff;
	  padding: 30rpx;
	  flex-shrink: 0;
	}
	.my-rank-title {
	  font-size: 28rpx;
	  margin-bottom: 20rpx;
	}
	.my-rank-info {
	  display: flex;
	  align-items: center;
	  gap:24rpx;
	}
	.my-rank-num {
	  font-size: 36rpx;
	  font-weight: bold;
	}
	.my-rank-num.empty {
	  font-size:28rpx;
	  opacity:0.8;
	}
	.my-avatar image {
	  width: 88rpx;
	  height: 88rpx;
	  border-radius: 50%;
	}
	.my-name {
	  flex:1;
	  font-size:30rpx;
	}
	.my-score {
	  font-size:30rpx;
	}

	.rank-scroll {
	  height: calc(100vh - 220rpx);
	}

	.top-three {
	  display: flex;
	  justify-content: center;
	  align-items: flex-end;
	  padding:40rpx 20rpx;
	  gap:20rpx;
	}
	.top-item {
	  display:flex;
	  flex-direction: column;
	  align-items:center;
	}
	.top-item.rank-1 {
	  transform: translateY(-30rpx);
	}
	.crown {
	  font-size:44rpx;
	  margin-bottom:12rpx;
	}
	.avatar {
	  width: 100rpx;
	  height:100rpx;
	  border-radius: 50%;
	  border:4rpx solid #fff;
	}
	.nickname {
	  margin-top:12rpx;
	  font-size:28rpx;
	  color:#333;
	}
	.score {
	  margin-top:8rpx;
	  font-size:26rpx;
	  color:#666;
	}

	.normal-list {
	  padding:0 30rpx;
	}
	.normal-item {
	  display:flex;
	  align-items:center;
	  background:#fff;
	  border-radius:16rpx;
	  padding:24rpx;
	  margin-bottom:16rpx;
	}
	.rank-no {
	  width:60rpx;
	  font-size:30rpx;
	  color:#999;
	}
	.normal-item .avatar {
	  width:80rpx;
	  height:80rpx;
	}
	.normal-item .nickname {
	  flex:1;
	  margin-left:20rpx;
	  margin-top:0;
	}
	.normal-item .score {
	  margin-top:0;
	}

	.no-more {
	  text-align:center;
	  padding:40rpx;
	  font-size:26rpx;
	  color:#999;
	}
	</style>