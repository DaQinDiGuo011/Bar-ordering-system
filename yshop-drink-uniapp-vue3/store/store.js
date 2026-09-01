import { defineStore } from 'pinia'

import cookie from '@/utils/cookie'
import { navigateTo } from '@/utils/router'

export const useMainStore = defineStore('main', {
  state: () => ({
	store: {},
	cart: [],
	orderType: 'takein',
	address: {},
	addresses: {},
	member: {

	},
	avatarUrlTemp: "",
	nicknameTemp:"",
	openid:"",
	imagePrivateFlag: false,
	loginValueFlag: false,
	token:"",
	lang: 'zh-cn',
	cookieKey:'YSESSID=yshop-e4dk4o2utr3c0n95tp42p745ai',
	// 默认地为你为北京地址
	location: {},
	myCouponList: [],
	mycoupon: {}
  }),
  getters: {
	  
    isLogin(state) {//是否登录
      return Object.keys(state.member).length > 0
	  //return cookie.get('accessToken') ? true : false
    }
	//isLogin: state => Object.keys(state.member).length > 0	//是否登录
  },
  actions: {
	DEL_COUPON() {
	    	this.mycoupon = {}
	},
	SET_AVATAR_URL_TEMP(url){
		this.avatarUrlTemp = url
	},
	SET_NICKNAME_TEMP(name){
		this.nicknameTemp = name
	},
	SET_COUPON(coupon) {
	  	this.mycoupon = coupon
	},
	SET_COUPON_LIST(list){
	  this.myCouponList = list
	},
	SET_ORDER_TYPE(type) {
	  	this.orderType = type
	},
	SET_MEMBER(member) {
		this.member = member
		cookie.set('userinfo', member)
	},
	SET_ADDRESS(address) {
		this.address = address
	},
	SET_ADDRESSES(addresses) {
		this.addresses = addresses
	},
	SET_STORE(store) {
		this.store = store
	},
	SET_CART(cart) {
		this.cart = cart
	},
	REMOVE_CART(state) {
		this.cart = []
	},
	setCookie(state, provider) {
		state.cookie = provider;
		uni.setStorage({
			key: 'cookieKey',
			data: provider
		});
	},
	SET_LOCATION(location) {
		this.location = location;
	},
	SET_OPENID(openid) {
		this.openid = openid;
	},
	SET_IMAGE_PRIVATE_FLAG(flag){
		this.imagePrivateFlag = flag;
	},
	SET_LOGIN_VALUE_FLAG(flag){
		this.loginValueFlag = flag;
	},
	SET_TOKEN(token) {
		this.token = token;
		cookie.set('accessToken', token)
	},
	  
    setAccessToken(user) {
      cookie.set('accessToken', user)
      // return getUserInfo()
    },
    setSelectAddress(id) {
      this.selectAddress = this.address.filter(item => item.id == id)[0]
    },
    init() {
      let accessToken = cookie.get('accessToken')
      if (accessToken) {
        //return getUserInfo()
      }
      return null
    },
    logout() {
      this.member = {}
      this.cart = []
      this.token = ''
      cookie.remove('accessToken')
      cookie.remove('userinfo')
      navigateTo('/pages/login/login')
    },
  },
})
