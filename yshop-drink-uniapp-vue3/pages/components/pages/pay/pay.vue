<template>
	<uv-navbar
	  :fixed="true"
	  bgColor="#ffffff"
	  :title="title"
	  left-arrow
	  :placeholder="true"
	  @leftClick="onClickLeft"
	/>
	<view class="pay-page container position-relative">
		<view class="pay-page__content">
			<view class="pay-page__section">
				<!-- <template v-if="store.distance > 0">
					<list-cell class="pay-page__cell pay-page__cell--location">
						<view class="flex-fill d-flex justify-content-between align-items-center">
							<view class="store-name flex-fill">{{ orderType == 'takeout' ? '外卖配送' : '点餐自取' }}</view>
							<uv-switch activeColor="#09b4f1" v-model="active" @change="takout">
							</uv-switch>
						</view>
					</list-cell>
				</template> -->

				<template v-if="orderType == 'takeout'">
					<list-cell @click="chooseAddress">
						<view v-if="address.realName" class="w-100 d-flex flex-column">
							<view class="d-flex align-items-center justify-content-between mb-10">
								<view class="font-size-lg text-color-base">
									{{ address.address + ' ' + address.detail }}
								</view>
								<image src="/static/images/navigator-1.png" class="pay-page__arrow"></image>
							</view>
							<view class="d-flex text-color-assist font-size-sm align-items-center">
								<view class="mr-10">{{ address.realName }}</view>
								<view>{{ address.phone }}</view>
							</view>
						</view>
						<view v-else class="flex-fill d-flex justify-content-between align-items-center">
							<view class="store-name flex-fill">选择收货地址</view>
							<image src="/static/images/navigator-1.png" class="pay-page__arrow"></image>
						</view>
					</list-cell>
				</template>
			</view>

			<!-- <view class="pay-page__section">
				<template>
					<list-cell class="pay-page__cell pay-page__cell--location" @click="goToShop">
						<view class="flex-fill d-flex justify-content-between align-items-center">
							<view class="store-name flex-fill">{{ store.name }}</view>
							<image src="/static/images/navigator-1.png" class="pay-page__arrow"></image>
						</view>
					</list-cell>
				</template>

				<template>
					<list-cell arrow class="pay-page__cell pay-page__cell--meal-time" v-if="orderType == 'takein'">
						<view class="flex-fill d-flex justify-content-between align-items-center"
							@click="takeinTIme = !takeinTIme">
							<view class="title">取餐时间</view>
							<view class="time">
								{{ takeinRange[defaultSelector[0]].name }}
								<u-picker v-model="takeinTIme" :range="takeinRange" range-key="name" mode="selector"
									@cancel="takeinCancelTime" @confirm="takeinConfirmTime"
									:default-selector="defaultSelector"></u-picker>
							</view>
						</view>
					</list-cell>
					<list-cell class="pay-page__cell pay-page__cell--contact" last :hover="false" v-if="orderType == 'takein'">
						<view class="flex-fill d-flex justify-content-between align-items-center">
							<view class="title flex-fill">联系电话</view>
							<view class="time"><input class="text-right" placeholder="请输入手机号码" :value="member.mobile" />
							</view>
							<button class="pay-page__contact-tip font-size-sm">自动填写</button>
						</view>
					</list-cell>
				</template>
				<template v-if="orderType == 'takeout'">
					<list-cell>
						<view class="w-100 d-flex flex-column">
							<view class="d-flex align-items-center font-size-base text-color-base">
								<view class="flex-fill">预计送达时间</view>
								<view class="mr-10">
									{{ defaultTime }}
									<u-picker :default-time="defaultTime" v-model="takeoutTIme" :params="paramsTime"
										mode="time" @cancel="cancelTime" @confirm="choiceTime"></u-picker>
								</view>
							</view>
						</view>
					</list-cell>
				</template>
			</view> -->
			<!-- 桌号外层渐变背景容器 -->
			<view class="desk-header-bg">
				<view class="deskNumbCls">
					<view class="desk-icon">🪑</view>
					<text class="desk-text">桌号{{ deskNumber }}</text>
				</view>
			</view>
			
			<!-- 现点 / 寄存 选项 -->
			<view class="desk-type-wrap">
				<view class="desk-type-item" :class="{active: deskType === '1'}" @click="changeDeskType('1')">
					<text>现点</text>
				</view>
				<view class="desk-type-item" :class="{active: deskType === '2'}" @click="changeDeskType('2')">
					<text>寄存</text>
				</view>
			</view>
			<!-- 购物车列表 begin -->
			<view class="pay-page__section pay-page__section--cart">
				<view class="pay-page__cart d-flex flex-column">
					<list-cell last v-for="(item, index) in cart" :key="index">
						<view class="w-100 d-flex flex-column">
							<view class="d-flex align-items-center mb-10">
								<view
									class="d-flex flex-fill justify-content-between align-items-center text-color-base font-size-lg">
									<image class="pay-page__cart-thumb" mode="aspectFill" :src="item.image">
									</image>
								</view>
								<view class="pay-page__cart-name overflow-hidden">
									<view class="text-color-base font-size-lg">{{ item.name }}</view>
								</view>
								<view
									class="d-flex flex-fill justify-content-between align-items-center text-color-base font-size-lg">
									<view>x{{ item.number }}</view>
									<view>￥{{ item.price }}</view>
								</view>
							</view>
							<view class="text-truncate font-size-base text-color-assist">{{ item.valueStr }}</view>
						</view>
					</list-cell>
				</view>
				<list-cell arrow @click="goToPackages">
					<view class="flex-fill">
						
					
						<view class="flex-fill d-flex justify-content-between align-items-center">
							<view class="text-color-base">优惠券</view>
							<view v-if="coupons == 0 && couponList.length ==0" class="text-color-base">暂无可用</view>
							
							<view v-else-if="couponList.length > 0"></view>
							<view v-else class="text-color-primary">可用优惠券{{ coupons }}张</view>
						</view>
						<view v-if="couponList.length>0" class="mt-10">
							<view v-for="(info,i) in couponList" :key="i" class="text-color-danger">
								{{ info.title }}(满{{ info.least }}减{{ info.value }})
							</view>
						</view>
					</view>
				</list-cell>
				<list-cell last>
					<view class="flex-fill d-flex justify-content-end align-items-center">
						<view>
							总计￥{{ total.toFixed(2) }}
							<text v-if="orderType == 'takeout'">,配送费￥{{ store.deliveryPrice }}</text>
							<text v-if="totalCoupon">,￥-{{ totalCoupon.toFixed(2) }}</text>
							,实付
						</view>
						<view class="font-size-extra-lg font-weight-bold">￥{{ amount }}</view>
					</view>
				</list-cell>
			</view>
			<!-- 购物车列表 end -->
			<view class="pay-page__notice d-flex align-items-center justify-content-start font-size-sm text-color-warning">
			</view>
			<!-- 支付方式 begin -->
			<view class="pay-page__payment">
				<list-cell last :hover="false"><text>支付方式</text></list-cell>
				<list-cell v-if="continueOrderId == ''">
					<view class="pay-page__payment-row pay-page__payment-row--disabled d-flex align-items-center justify-content-between w-100"
						@click="setPayType('yue')">
						<view class="iconfont iconbalance line-height-100 pay-page__payment-icon"></view>
						<view class="flex-fill">余额支付（余额￥{{ member.nowMoney }}）</view>
						<view class="font-size-sm" v-if="member.nowMoney == 0">余额不足</view>
						<view class="iconfont line-height-100 pay-page__checkbox pay-page__checkbox--checked iconradio-button-on" v-if="payType == 'yue'">
						</view>
						<view class="iconfont line-height-100 pay-page__checkbox iconradio-button-off" v-else></view>
					</view>
				</list-cell>
				<list-cell last>
					<view class="pay-page__payment-row d-flex align-items-center justify-content-between w-100" @click="setPayType('weixin')">
						<view class="iconfont iconwxpay line-height-100 pay-page__payment-icon pay-page__payment-icon--wechat"></view>
						<view class="flex-fill">微信支付</view>
						<view class="iconfont line-height-100 pay-page__checkbox pay-page__checkbox--checked iconradio-button-on" v-if="payType == 'weixin'">
						</view>
						<view class="iconfont line-height-100 pay-page__checkbox iconradio-button-off" v-else></view>
					</view>
				</list-cell>
				<!-- #ifdef H5 -->
				<!-- <list-cell>
					<view class="pay-page__payment-row d-flex align-items-center justify-content-between w-100" @click="setPayType('alipay')">
						<view class="iconfont-yshop icon-alipay line-height-100 pay-page__payment-icon pay-page__payment-icon--alipay"></view>
						<view class="flex-fill">支付宝</view>
						<view class="iconfont line-height-100 pay-page__checkbox pay-page__checkbox--checked iconradio-button-on" v-if="payType == 'alipay'" ></view>
						<view class="iconfont line-height-100 pay-page__checkbox iconradio-button-off" v-else ></view>     
					</view>
				</list-cell> -->
				<!-- #endif -->
			</view>
			<!-- 支付方式 end -->
			<!-- 备注 begin -->
			<list-cell last @click="goToRemark">
				<view class="pay-page__remark d-flex flex-fill align-items-center justify-content-between overflow-hidden">
					<view class="flex-shrink-0 mr-20">备注</view>
					<view class="text-color-primary flex-fill text-truncate text-right">{{ showRemark }}
					</view>
				</view>
			</list-cell>
			<!-- 备注 end -->
		</view>
		<!-- 付款栏 begin -->
		<view class="pay-page__footer w-100 position-fixed fixed-bottom d-flex align-items-center justify-content-between bg-white">
			<view class="pay-page__footer-label font-size-sm">合计：</view>
			<view class="pay-page__footer-amount font-size-lg flex-fill">￥{{ amount }}</view>
			<view class="pay-page__footer-btn bg-primary h-100 d-flex align-items-center just-content-center text-color-white font-size-base"
				@tap="submit">付款</view>
		</view>
		<!-- 付款栏 end -->
		<modal :show="ensureAddressModalVisible" custom :mask-closable="false" :radius="'0rpx'" width="90%">
			<view class="pay-page__modal">
				<view class="d-flex justify-content-end">
					<image src="/static/images/pay/close.png" class="pay-page__modal-close"
						@tap="ensureAddressModalVisible = false"></image>
				</view>
				<view class="pay-page__modal-title d-flex just-content-center align-items-center">
					<view class="font-size-extra-lg text-color-base">请再次确认下单地址</view>
				</view>
				<view
					class="d-flex font-size-base text-color-base font-weight-bold align-items-center justify-content-between mb-20">
					<view>{{ address.realName }}</view>
					<view>{{ address.phone }}</view>
				</view>
				<view class="d-flex font-size-sm text-color-assist align-items-center justify-content-between mb-40">
					<view class="pay-page__modal-address">{{ address.address + address.detail }}</view>
					<button type="primary" size="mini" plain class="pay-page__modal-change-btn"
						@click="chooseAddress">修改地址</button>
				</view>
				<button type="primary" class="pay-page__modal-submit" @tap="pay">确认并付款</button>
			</view>
		</modal>
		<uv-toast ref="uToast"></uv-toast>
	</view>
</template>

<script setup>
import {
  ref,
  computed,
  nextTick
} from 'vue'
import { useMainStore } from '@/store/store'
import { storeToRefs } from 'pinia'
import { onLoad,onShow ,onPullDownRefresh,onHide} from '@dcloudio/uni-app'
import { formatDateTime,isWeixin } from '@/utils/util'
import  debounce  from '@/uni_modules/uv-ui-tools/libs/function/debounce'

import {
  orderSubmit,
  orderDetail,
  payUnify,
  getWechatConfig,
  getPayInfo
} from '@/api/order'
import {
  couponCount,
  useListByIdList
} from '@/api/coupon'
// #ifdef H5
import * as jweixin from 'weixin-js-sdk'
// #endif
const main = useMainStore()
const { orderType,address, store,location,member,mycoupon,myCouponList } = storeToRefs(main)
const active = ref(false)
const title = ref('支付')
const jsStr = ref('')
const cart = ref([])
const form = ref({
	remark: ''
})
const deskNumber = ref('')
const  ensureAddressModalVisible = ref(false)
const  takeoutTIme = ref(false) // 外卖取餐时间picker
const paramsTime = ref({
	year: false,
	month: false,
	day: false,
	hour: true,
	minute: true,
	second: false
})
const defaultTime = ref('00:00')
const takeinTIme = ref(false) // 到店自取时间selector
// 新增：桌号类型 now现点 store寄存
const deskType = ref('1')
const takeinRange = ref([{
		name: '立即用餐',
		value: 0
	},
	{
		name: '10分钟后',
		value: 10
	},
	{
		name: '20分钟后',
		value: 20
	},
	{
		name: '30分钟后',
		value: 30
	},
	{
		name: '40分钟后',
		value: 40
	},
	{
		name: '50分钟后',
		value: 50
	}
])
const defaultSelector = ref([0])
const payType = ref('weixin') // 付款方式
const coupons = ref(0) // 可用优惠券数量
const couponList = ref(main.myCouponList)//选中的列表
const continueOrderId = ref("")
// const totalCoupon = ref(0)
const subscribeMss = ref({
	'takein': '',
	'takeout': '',
	'takein_made': '',
	'takeout_made': ''
})// 微信订阅信息
const uToast = ref()
const showRemark = ref('')

const total = computed(() =>{
	return cart.value.reduce((acc, cur) => acc + cur.number * cur.price, 0);
})
const onClickLeft = ()=>{
	if(continueOrderId.value){
		if(deskType.value == '1'){
			uni.switchTab({
				url: '/pages/order/order'
			});
		}else{
			uni.navigateTo({
			  url: '/pages/components/pages/winestoreMylist/winestoreMylist?type=pay' // 你的隐私协议页面路径
			})
		}
	}else{
		uni.switchTab({
			url: '/pages/menu/menu'
		});
	}
	
}
const setRemark = (val)=>{
	if(val){
		form.value.remark = val
		showRemark.value = val
	}else if(continueOrderId.value){
		form.value.remark = ''
		showRemark.value = ''
	}else{
		form.value.remark = ''
		showRemark.value = '点击填写备注'
	}
}
const totalCoupon = computed(()=>{
	let sum = 0
	for(let i=0; i<couponList.value.length; i++){
		const couponItem = couponList.value[i]
		if (couponItem?.id && couponItem?.value) {
			sum += parseFloat(couponItem.value)
		}
	}
	return Number(sum.toFixed(2))
})
const amount = computed(() =>{
	let amount = cart.value.reduce((acc, cur) => acc + cur.number * cur.price, 0);
	// 加配送费
	if (store.value.distance > 0 && orderType.value == 'takeout') {
		amount += parseFloat(store.value.deliveryPrice);
	}

	
	// 减去优惠券
	amount -= totalCoupon.value
	
	if(amount < 0){
		amount = 0
	}
	return amount.toFixed(2);
})
onShow(() => {
	couponList.value = main.myCouponList
	let date = new Date(new Date().getTime() + 3600000); // 一个小时后
	let hour = date.getHours();
	let minute = date.getMinutes();
	if (hour < 10) {
		hour = '0' + hour;
	}
	if (minute < 10) {
		minute = '0' + minute;
	}
	defaultTime.value = hour + ':' + minute;

	
	if(orderType.value == 'takeout'){
		active.value = true
	}else{
		active.value = false
	}
	if(!continueOrderId.value){
		getCoupons();
	}
	
	
	let paytype = uni.getStorageSync('paytype');
	payType.value = paytype ? paytype : 'weixin';
	
})
onHide(() => {
	subscribeMss.value = [];
	coupons.value = 0;
})
onLoad((option) => {
	console.log('接收到桌号', option)
	if(option.orderId){
		continueOrderId.value = option.orderId
		getDetail(option.orderId);
		deskType.value = '1'
	}else{
		
		    // 赋值给页面变量
		deskNumber.value = option.localNumb
		cart.value = uni.getStorageSync('cart')
		
	}
	
	setRemark(option.remark)

})
const getDetail =  async(id) => {
	let data = await orderDetail(id);
	console.log("====",data,"-------************")
	if (data) {
		deskNumber.value = data.deskNumber
		cart.value = data.cartInfo
		cart.value.forEach(info => {
			info.name = info.title
			info.valueStr = info.spec
		})
		setRemark(data.mark)
		// amount.value = data.payPrice
		// totalCoupon = data.couponPrice
		if(data.couponIdList){
			let param = data.couponIdList.split(",")
			console.log(param,'-----param---------')
			// let couponData = await useListByIdList(param)
			couponList.value = await useListByIdList(param)
		}
	}
	
	
}

// 更改支付方式
const setPayType = (paytype) => {
	payType.value = 'weixin';
	payType.value= paytype;
	uni.setStorage({
		key: 'paytype',
		data: paytype
	})
}
const getCoupons = async() => {
	//0=通用,1=堂食,2=外卖
	let type = orderType.value == 'takein' ? 4 : 2;
	let data = await couponCount({
		shop_id: store.value.id ? store.value.id : 0,
		type: type
	});
	if (data) {
		coupons.value = data;
	}
}
// 选择时间
const choiceTime = (value) => {
	let hour = value.hour;
	let minute = value.minute;

	let date = new Date(new Date().getTime() + 3600000); // 一个小时后
	let nowhour = date.getHours();
	let nowminute = date.getMinutes();

	if ((hour * 60 * 60 + minute * 60) * 1000 - 3600000 < (nowhour * 60 * 60 + nowminute * 60) * 1000) {
		uToast.value.show({
			message: '请至少选择一个小时之后',
			type: 'error'
		});
		return
	}

	if (hour < 10) {
		hour = '0' + hour;
	}
	if (minute < 10) {
		minute = '0' + minute;
	}
	defaultTime.value = hour + ':' + minute;
	takeoutTIme.value = false;
}
const cancelTime = (value) => {
	takeoutTIme.value = false;
}
// 到店自取-取消选择取餐时间
const takeinCancelTime = (value) => {
	takeinTIme.value = false;
}
// 到店自取-选择取餐时间
const takeinConfirmTime = (value) => {
	defaultSelector.value = value;
}

const goToRemark = () => {
	if(continueOrderId.value){
		return
	}
	uni.navigateTo({
		url: '/pages/components/pages/remark/remark?remark=' + form.value.remark + '&localNumb=' + deskNumber.value
	});
}
const chooseAddress = () => {
	uni.navigateTo({
		url: '/pages/components/pages/address/address?is_choose=true&scene=pay'
	});
}
const goToPackages = () => {
	if(continueOrderId.value){
		return
	}
	let newamount = total.value.toFixed(2);
	let coupon_id_list = couponList.value.map(item => item.id)
	let type = orderType.value == 'takein' ? 4 : 2;
	let shop_id = store.value.id;
	uni.navigateTo({
		url: '/pages/components/pages/packages/index?amount=' + newamount + '&selectedIds=' + coupon_id_list +
			'&shop_id=' + shop_id + '&type=' + type
	});
}
const goToShop = () => {
	uni.navigateTo({
		url: `/pages/components/pages/shop/shop`
	});
}
const submit = () => {
	if(continueOrderId.value){
		rePay()
		return
	}
	let shop = main.store
	if(shop.couponUseNumLimit > 0 && couponList.value.length > shop.couponUseNumLimit){
		uToast.value.show({
			message:`超过最大使用数量【${shop.couponUseNumLimit}张】`,
			type:'error'
		})
		return
	}
	console.log("----totalCoupon.value---",totalCoupon.value)
	
	if(shop.couponUseAmountLimit > 0 && totalCoupon.value > shop.couponUseAmountLimit){
		uToast.value.show({
			message:`超过最大使用读【限额${shop.couponUseAmountLimit}元】`,
			type:'error'
		})
		return
	}
	if (orderType.value == 'takeout') {
		// 外卖类型
		if (typeof address.value.id == 'undefined') {
			uToast.value.show({
				message: '请选择收货地址',
				type: 'error'
			});
			return
		}

		// 起送价钱
		if (store.value.min_price > total.value) {
			uToast.value.show({
				message: '本店外卖起送价为￥' + store.value.min_price,
				type: 'error'
			});
			return
		}

		pay();

	} else {
		pay();
	}
}
const rePay = async() => {
	uni.showLoading({
		title: '加载中'
	});
	let payInfo = await getPayInfo(continueOrderId.value)
	uni.hideLoading();
	if(!payInfo){
		uToast.value.show({message:'未找到支付信息，请联系工作人员',type: 'info'});
		return
	}
	uni.requestPayment({
		provider: 'wxpay',
		timeStamp: payInfo.timeStamp,
		nonceStr: payInfo.nonceStr,
		package: payInfo.packageVal,
		signType: 'MD5',
		paySign: payInfo.paySign,
		success: function(res) {
	
			if(deskType.value == '1'){
				uni.switchTab({
					url: '/pages/order/order'
				});
			}else{
				uni.navigateTo({
				  url: '/pages/components/pages/winestoreMylist/winestoreMylist?type=pay' // 你的隐私协议页面路径
				})
			}
			
		},
		fail: function(err) {
			console.log('fail:' + JSON.stringify(err));
			uni.switchTab({
				url: '/pages/order/order'
			});
		}
	});
}
const changeDeskType = (val) => {
	if(continueOrderId.value){
		return
	}
	deskType.value = val
}
const pay = async() => {
	let that = this;
	// // #ifdef MP-WEIXIN
	// await new Promise(function(revolve) {
	// 	//订阅号信息id
	// 	 let subscribeMss = ['KBtfY9G1IWCzC6q-ZKo-Q-MmdP7aaF79nx0XFcBf3h4'];

	// 	wx.showModal({
	// 		title: '温馨提示',
	// 		content: '为更好的促进您与商家的交流，小程序需要在您成交时向您发送消息',
	// 		confirmText: "同意",
	// 		cancelText: "拒绝",
	// 		success: function(res) {
	// 			if (res.confirm) {
	// 				uni.requestSubscribeMessage({
	// 					tmplIds: subscribeMss,
	// 					complete(res) {
	// 						console.log(res)
	// 						revolve(true)
	// 					}
	// 				});
	// 			} else {
	// 				revolve(true)
	// 			}
	// 		}
	// 	})
	// });
	

	// #endif
	if(amount.value == 0){
		payType.value = 'yue'
	}
	if(!deskNumber.value){
		//后面要开放
		uToast.value.show({message:'请先扫描桌号',type: 'info'});
		return
	}
	uni.showLoading({
		title: '加载中'
	});
	let couponIdList = couponList.value.map(info => info.id)
	
	let data = {
		orderType: orderType.value, // 购买类型:takein=自取,takeout=外卖
		addressId:orderType.value == 'takeout' ? address.value.id : 0, // 外卖配送地址
		shopId: store.value.id, // 店铺id
		mobile: member.value.mobile, // 联系电话
		deskNumber: deskNumber.value,
		gettime: takeinRange.value[defaultSelector.value[0]].value, // 取餐时间
		payType: payType.value, // 支付类型
		remark: form.value.remark, // 备注
		deskType: deskType.value, //新增桌号类型 now现点 / store寄存
		productId: [],
		spec: [],
		number: [],
		couponIdList: couponIdList,
		couponId: 0 // 优惠券id
	};

	cart.value.forEach((item, index) => {
		data.productId.push(item.id);
		data.spec.push(item.valueStr.replace(/,/g, '|'));
		//data.spec.push(item.valueStr);
		data.number.push(item.number);
	});

	//console.log(data);
	let order = await orderSubmit(data);
	if (!order) {
		uni.hideLoading();
		return;
	}
	
	main.DEL_COUPON()
    if(amount.value == 0){
		uToast.value.show({
			message: '订单金额为0自动走余额支付',
			type: 'success'
		});
		balancePay(order);
		uni.hideLoading()
		return
   }

	if (payType.value == 'weixin') {
		// 微信支付
		weixinPay(order);
	} else if (payType.value == 'yue') {
		// 余额支付
		balancePay(order);
	} else if (payType.value == 'alipay') {
		// 余额支付
		aliPay(order);
	} 
	uni.hideLoading()
	return
}
const balancePay = async(order) => {
	let from = 'routine'
	// #ifdef H5
	from = 'h5'
	// #endif
	let pay = await payUnify({
		uni: order.orderId,
		from: from,
		paytype: 'yue'
	});

	uni.hideLoading();
	if (!pay) {
		return;
	}

	member.value.money -= amount.value
	main.SET_MEMBER(member.value)
	uni.removeStorageSync('cart');
	main.SET_COUPON_LIST([])
	if(deskType.value == '1'){
		uni.switchTab({
			url: '/pages/order/order'
		});
	}else{
		uni.navigateTo({
		  url: '/pages/components/pages/winestoreMylist/winestoreMylist?type=pay&result=s' // 你的隐私协议页面路径
		})
	}
}
const weixinPay = async(order) => {
	let from = 'routine'
	// #ifdef H5
	from = 'h5'
	if(isWeixin()){
		from = 'wechat'
	}
	
	// #endif
	//let that = this;
	let data = await payUnify({
		uni: order.orderId,
		from: from,
		deskType: deskType.value, //新增桌号类型 now现点 / store寄存
		paytype: 'weixin'
	});
	console.log('param2:',data)
	if (!data) {
		uni.hideLoading();
		return;
	}
	if (data.trade_type == 'MWEB') {
		// #ifdef H5
		// 微信外的H5
		console.log('data:',data)
		location.href = data.data;
		// #endif
		console.log('data1:',data)
	} else if (data.trade_type == 'JSAPI') {
		console.log('param:',data)
		uni.removeStorageSync('cart');
		main.SET_COUPON_LIST([])
		// #ifdef MP-WEIXIN
		uni.requestPayment({
			provider: 'wxpay',
			timeStamp: data.data.timeStamp,
			nonceStr: data.data.nonceStr,
			package: data.data.package,
			signType: 'MD5',
			paySign: data.data.paySign,
			success: function(res) {

				
				if(deskType.value == '1'){
					uni.switchTab({
						url: '/pages/order/order'
					});
				}else{
					uni.navigateTo({
					  url: '/pages/components/pages/winestoreMylist/winestoreMylist?type=pay&result=s' // 你的隐私协议页面路径
					})
				}
				
			},
			fail: function(err) {
				console.log('fail:' + JSON.stringify(err));
				
				if(deskType.value == '1'){
					uni.switchTab({
						url: '/pages/order/order'
					});
				}else{
					uni.navigateTo({
					  url: '/pages/components/pages/winestoreMylist/winestoreMylist?type=pay&result=f' // 你的隐私协议页面路径
					})
				}
			}
		});
		// #endif
	} else if (data.trade_type == 'W-JSAPI'){
		//公众号支付
	
		
	}else if (data.trade_type == 'APP') {

	}
}
const aliPay = async(order) => {

	// #ifdef H5
	//let that = this;
	if(isWeixin()){
		uni.showToast({
			title: '请普通浏览器打开进行支付宝支付~',
			icon: 'none'
		})
		return
	}
	let data = await payUnify({
		uni: order.orderId,
		from: 'h5',
		paytype: 'alipay'
	});

	console.log('data:',data.data)
  // 支付宝支付，这里只要提交表单
	let form = data.data
	const div = document.createElement('formdiv');
	div.innerHTML = form;
	document.body.appendChild(div);      
	//document.forms[0].setAttribute('target', ' self');
	document.forms[0].submit();
	//div.remove();

// #endif


}




</script>


<style lang="scss" scoped>
// 支付页局部 token（与 uni.scss 全局变量配合）
$pay-page-padding: $spacing-row-lg;
$pay-page-section-gap: $spacing-row-lg;
$pay-page-content-offset: 130rpx;
$pay-arrow-size: 50rpx;
$pay-arrow-offset-right: -10rpx;
$pay-cart-name-width: 65%;
$pay-payment-icon-size: 44rpx;
$pay-checkbox-size: 36rpx;
$pay-checkbox-gap: 10rpx;
$pay-payment-gap: 10rpx;
$pay-footer-height: 100rpx;
$pay-footer-shadow: 0 0 20rpx rgba($color: #000, $alpha: 0.08);
$pay-footer-z-index: 1;
$pay-footer-label-margin: $spacing-row-base;
$pay-footer-btn-padding-x: 60rpx;
$pay-remark-margin-bottom: 110rpx;
$pay-notice-padding-y: $spacing-row-base;
$pay-wechat-color: #38a830;
$pay-alipay-color: #07b4fd;
$pay-modal-close-size: 40rpx;
$pay-modal-title-margin-bottom: 40px;
$pay-modal-address-max-width: 60%;
$pay-contact-tip-border-width: 2rpx;
$pay-contact-tip-padding-y: 6rpx;
$pay-contact-tip-padding-x: 10rpx;
$pay-contact-tip-margin-left: 10rpx;
$pay-modal-btn-radius: 50rem;
$pay-modal-btn-line-height: 3;
$pay-modal-change-btn-line-height: 2;

.desk-header-bg{
	margin: 0 -$pay-page-padding;
	padding: 40rpx $pay-page-padding 30rpx;
    // 顶部柔和紫蓝渐变，参考效果图
	background: linear-gradient(180deg,#4048e8,#d56bf8);
}

.deskNumbCls {
	display: inline-flex;
	align-items: center;
	gap: 14rpx;
	padding: 16rpx 36rpx;
    //毛玻璃半透胶囊效果
	background: rgba(255,255,255,0.22);
    backdrop-filter: blur(12rpx);
	color: #ffffff;
	border-radius: 100rpx;
	font-size: 34rpx;
	box-shadow: 0 4rpx 16rpx rgba(60, 80, 255, 0.25);
	border:1rpx solid rgba(255,255,255,0.35);

	.desk-icon {
		font-size: 38rpx;
		line-height: 1;
	}
	.desk-text {
		font-weight: 600;
		letter-spacing:2rpx;
	}
}
/*现点 / 寄存 tab 改造，参考效果图渐变+阴影*/
.desk-type-wrap{
	display: flex;
	gap:24rpx;
	padding:20rpx 15rpx;
	margin-bottom:20rpx;
}
.desk-type-item{
	flex:1;
	display:flex;
	align-items:center;
	justify-content:center;
	text-align:center;
	height:88rpx;
	border-radius:20rpx;
	border:none;
	background:#ffffff;
	font-size:30rpx;
	color:#666666;
    box-shadow:0 4rpx 14rpx rgba(0,0,0,0.07);
	transition: all 0.24s ease;
	&.active{
        //激活态绿色渐变
		background: linear-gradient(90deg,#52c748,#38a830);
		color:#ffffff;
		font-weight:500;
        box-shadow:0 6rpx 16rpx rgba(56,168,48,0.3);
	}
}
.pay-page {
	padding: $pay-page-padding;
	--pay-cart-thumb-size: #{$img-size-lg};
    background-color:#f7f7f9;

	&__content {
		margin-bottom: $pay-page-content-offset;
	}

	&__section {
		margin-bottom: $pay-page-section-gap;

		&--cart {
			margin-bottom: 0;
		}
	}

	&__arrow {
		position: relative;
		width: $pay-arrow-size;
		height: $pay-arrow-size;
		margin-right: $pay-arrow-offset-right;
	}

	&__cell--location {
		.store-name {
			font-size: $font-size-lg;
		}

		.iconfont {
			font-size: $pay-arrow-size;
			line-height: 100%;
			color: $color-primary;
		}
	}

	&__contact-tip {
		margin-left: $pay-contact-tip-margin-left;
		padding: $pay-contact-tip-padding-y $pay-contact-tip-padding-x;
		border: $pay-contact-tip-border-width solid $color-primary;
		color: $color-primary;
	}

	&__cart-name {
		width: $pay-cart-name-width;
	}

	&__cart-thumb {
		flex-shrink: 0;
		width: var(--pay-cart-thumb-size);
		height: var(--pay-cart-thumb-size);
        border-radius:12rpx;
	}

	&__notice {
		padding: $pay-notice-padding-y 0;
	}

	&__payment {
		margin-bottom: $pay-page-section-gap;
	}

	&__payment-row {
		&--disabled {
			color: $text-color-grey;
		}
	}

	&__payment-icon {
		margin-right: $pay-payment-gap;
		font-size: $pay-payment-icon-size;

		&--wechat {
			color: $pay-wechat-color;
		}

		&--alipay {
			color: $pay-alipay-color;
		}
	}

	&__checkbox {
		margin-left: $pay-checkbox-gap;
		font-size: $pay-checkbox-size;

		&--checked {
			color: $pay-wechat-color;
		}
	}

	&__remark {
		margin-bottom: $pay-remark-margin-bottom;
	}

	&__footer {
		z-index: $pay-footer-z-index;
		height: $pay-footer-height;
		box-shadow: $pay-footer-shadow;
        border-top:1rpx solid #eee;
	}

	&__footer-label {
		margin-left: $pay-footer-label-margin;
	}
    //底部付款按钮：大圆角
	&__footer-btn {
		padding: 0 $pay-footer-btn-padding-x;
        border-radius:100rpx;
        background:linear-gradient(90deg,#52c748,#38a830) !important;
	}

	&__modal-close {
		width: $pay-modal-close-size;
		height: $pay-modal-close-size;
	}

	&__modal-title {
		margin-bottom: $pay-modal-title-margin-bottom;
	}

	&__modal-address {
		max-width: $pay-modal-address-max-width;
	}

	&__modal-change-btn {
		line-height: $pay-modal-change-btn-line-height;
		padding: 0 1em;
		white-space: nowrap;
	}

	&__modal-submit {
		width: 100%;
		line-height: $pay-modal-btn-line-height;
		border-radius: $pay-modal-btn-radius !important;
	}
}

//覆盖list-cell全局样式，实现卡片圆角、柔和阴影
:deep(.uv-list-cell) {
    background:#ffffff;
    border-radius:16rpx;
    margin-bottom:16rpx;
    box-shadow:0 2rpx 12rpx rgba(0,0,0,0.04);
    border:none;
}
:deep(.uv-list-cell:last-child){
    margin-bottom:0;
}
</style>