<template>
	<view>
    <view class="login-popup" @click.stop>
      <!-- 头像区域 -->
		<!-- <view class="avatar-box" @click="openAvatarSheet" v-if="!imagePrivateFlag">
			<view v-if="avatarUrl === ''">
				  <uv-icon name="account" size="90" color="#c8c7bb"></uv-icon>
				  <view class="edit-mark">
					<uv-icon name="edit-pen" size="26" color="#fff"></uv-icon>
				  </view>
			</view>
        
			<uv-avatar v-else size="90" :src="avatarUrl" style="border: 1px solid #cfcdc9;"></uv-avatar>
		</view> -->
		<view class="avatar-box">
			
			<button
			  class="avatar-bnt"
			  open-type="chooseAvatar"
			  @chooseavatar="onChooseAvatar"
			>
				<!-- <view v-if="avatarUrl === ''">
					  <uv-icon name="account" size="90" color="#cccccc"></uv-icon>
					  <view class="edit-mark">
						<uv-icon name="edit-pen" size="26" color="#fff"></uv-icon>
					  </view>
				</view> -->
			
				<uv-avatar size="90" :src="avatarUrl" :custom-style="imageStyle"></uv-avatar>
									
			</button>
		</view>
      <view class="title">欢迎来到TheOne德旺酒馆</view>
      <view class="desc">
        请完善个人信息完成会员注册
		<!-- ，登录即同意
        <text class="privacy-text" @click="goPrivacy">《隐私协议》</text>
        ,点击查看 -->
      </view>
		<view class="checkbox-wrap">
	      <!-- <checkbox :checked="isAgree" @change="onCheckChange" /> -->
		  <uv-checkbox-group v-model="checkboxValue" placement="row">
				<uv-checkbox name="agree" ></uv-checkbox>
		 </uv-checkbox-group>
	      <text>我已阅读并同意</text>
	      <!-- <text class="link" @click="openUserAgreement">《用户服务协议》</text>
	      <text>、</text> -->
	      <text class="privacy-text" @click="goPrivacy">《德旺酒馆 小程序隐私保护指引》</text>
	    </view>
      <!-- 昵称输入框 -->
      <view class="input-wrap">
        <view class="input-label">昵称 </view>
		<view class="star">*</view>
        <input class="input" v-model="nickname" placeholder="请输入昵称" />
      </view>

      <!-- 按钮 -->
      <!-- <uv-button
        block
        class="login-btn"
        type="primary"
        @click="authorizePhoneLogin"
        style="border:none;border-radius:99rpx"
      > -->
	  <button
		v-if="showPhoneFlag"
	    open-type="getPhoneNumber"
	    @getphonenumber="onGetPhoneNumber"
	    class="phone-box"
	  >
        授权手机号并登录
      </button>
	  
	  <button v-else
	    class="phone-box"
		@click="authorizePhoneLogin"
	  >
	    授权手机号并登录
	  </button>

      <view class="cancel-text" @click="closePopup">取消</view>
    </view>
	
	
	<updateImage ref="updateImageRef" @updageImage="updageImage"></updateImage>
	
	<userPhonePopup ref="userPhonePopupRef"></userPhonePopup>
	</view>
</template>

<script setup>
	import {ref, watch} from 'vue'
	import updateImage from '@/pages/login/updateImage.vue'
	import userPhonePopup from '@/pages/login/userPhonePopup.vue'
	import { VUE_APP_UPLOAD_URL } from '@/config';
	import { useMainStore } from '@/store/store'
	import { storeToRefs } from 'pinia'
	import { loginByPhone } from '@/api/auth.js'
	import { onLoad } from '@dcloudio/uni-app'
	
	const showPhoneFlag = ref(false)
	const emit = defineEmits(['close'])
	const agreePrivacy = ref(false) // 修复缺失变量
	const main = useMainStore()
	const { member,openid, lang,imagePrivateFlag } = storeToRefs(main)
	// 弹窗控制
	const nickname = ref('')
	const avatarUrl = ref('')
	const isAgree = ref(false)
	const updateImageRef = ref(null)
	const checkboxValue = ref([])
	const userPhonePopupRef = ref(null)
	const imageStyle = {
		'position': 'relative',
		'right': '12px',
		'top': '4px'
		}
		
	// =========新增监听========
	watch(
		[avatarUrl, nickname, checkboxValue],
		() => {
			if(nickname.value?.trim()){
				main.SET_NICKNAME_TEMP(nickname.value.trim())
			}
			if ( avatarUrl.value && nickname.value?.trim() && checkboxValue.value.length == 1) {
				showPhoneFlag.value = true
			} else {
				showPhoneFlag.value = false
			}
		},
		{ immediate:true }
	)
	// 关闭弹窗
	const closePopup = ()=>{
	  emit('close')
	}
	// 跳转隐私协议
	const goPrivacy = ()=>{
	  uni.navigateTo({url:'/pages/components/pages/privateAgreement/privateAgreement'})
	}

	// 打开头像菜单
	// const openAvatarSheet = ()=>{
	// 	updateImageRef.value.openPrivacyPopup()
	// }
	
	const onChooseAvatar = async (e) => {
		console.log("------------e--------",e)
		uploadImage(e.detail.avatarUrl)
	  // 此处执行uni.uploadFile上传到后端，拿到永久url再赋值
	}
	const uploadImage = (filePath) => {
		uni.showLoading({title:'上传中...'})
		uni.uploadFile({
			url: VUE_APP_UPLOAD_URL, 
			filePath: filePath,
			name: 'file',
			header: {
				Authorization: 'Bearer ' + member.value.accessToken,
				lang: lang.value,
				// 'content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
			},
			formData: {
			    path: 'user' // 这里传你要存储的子目录，例如 shop / avatar
		   },
			success(uploadFileResult){
				if (uploadFileResult) {
					const upload = JSON.parse(uploadFileResult.data);
					console.log('upload:',upload.data)
					// member.value.avatar = upload.data;
					avatarUrl.value = upload.data
					main.SET_AVATAR_URL_TEMP(avatarUrl.value)
				}
				
				uni.hideLoading()
			}, 
			fail(e){
				console.log(e)
				uni.showToast({title:'上传失败，请联系工作人员',icon:'none'})
				uni.hideLoading()
			}
		});
	}
	const onGetPhoneNumber = async (e) => {
		console.log('手机号授权回调',e)
		if(e.detail.errMsg !== 'getPhoneNumber:ok'){
			uni.showToast({title:'您拒绝获取手机号，无法快捷登录',icon:'none'})
			return
		}
		const { cloudID, encryptedData, iv,code } = e.detail;
	
		uni.showLoading({title:'登录中...'})
		try{
			const loginRes = await new Promise((resolve,reject)=>{
				uni.login({success:resolve,fail:reject})
			})
			const jsCode = loginRes.code
				
			let param = {
				jsCode: jsCode,
				phoneCode: e.detail.code,
				avatar: avatarUrl.value,
				nickname: nickname.value.trim()
			}
			const loginResq = await loginByPhone(param)
			
			uni.hideLoading()
				// console.log('login-ers ====',res)
			// uni.setStorage({
			// 	key: 'userinfo',
			// 	data: loginResq.userInfo
			// });
			// uni.setStorage({
			// 	key: 'accessToken',
			// 	data: loginResq.accessToken	
			// });
			main.SET_MEMBER(loginResq.userInfo);
			main.SET_LOGIN_VALUE_FLAG(true)
			main.SET_OPENID(loginResq.userInfo.openId)
			main.SET_TOKEN(loginResq.accessToken);
				
			// if(res.code === 200){
			uni.showToast({title:'登录成功'})
			closePopup()
			emit('loginSuccess')
			// }
		}catch(err){
			uni.hideLoading()
			uni.showToast({title:'登录失败',icon:'none'})
		}
	}
	// 授权手机号登录
	const authorizePhoneLogin = ()=>{
	   // 1.校验头像是否上传
	    if (!avatarUrl.value) {
	      uni.showToast({ title: '请点击上传头像', icon: 'none' })
	      return
	    }
	    // 2.校验昵称
	    if (!nickname.value.trim()) {
	      uni.showToast({ title: '请输入昵称', icon: 'none' })
	      return
	    }
		if(checkboxValue.value.length == 0){
			uni.showToast({ title: '请先阅读《隐私协议》，并勾选同意', icon: 'none' })
			return
		}
		
	}
	const updageImage = (imageUrl)=>{
		avatarUrl.value = imageUrl
	}
	onLoad(() => {
		if(main.avatarUrlTemp){
			avatarUrl.value = main.avatarUrlTemp
		}
		if(main.nicknameTemp){
			nickname.value = main.nicknameTemp
		}
	})
</script>

<style scoped>
.login-popup{
  width:100%;
  background:#f5f5f5;
  padding:60rpx 48rpx;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  
}
.avatar-bnt {
  width:6rem;
  height:6rem;
  border-radius:50%;
}
.avatar-box{
  width:6rem;
  height:6rem;
  border-radius:50%;
  background:#f5f5f5;
  /* margin:0 auto 40rpx; */
  display:flex;
  align-items:center;
  justify-content:center;
  position:relative;
  border: 1px solid #d5d5d5;
}

.edit-mark{
  position:absolute;
  right:0;
  bottom:0;
  width:56rpx;
  height:56rpx;
  background:#ff86a9;
  border-radius:50%;
  display:flex;
  align-items:center;
  justify-content:center;
}
.title{
  font-size:44rpx;
  font-weight:bold;
  color:#222;
  /* text-align:center; */
}
.desc{
  font-size:30rpx;
  color:#999;
  text-align:center;
  margin:24rpx 0 60rpx;
  line-height:1.6;
}
.privacy-text{
  color:#ff86a9;
}
.input-wrap{
  margin-bottom:30rpx;
  display: flex;
  height: 40px;
}
.input-label{
  font-size:36rpx;
  color:#222;
  margin-bottom:16rpx;
  width: 50px;
}
.star{
  color:#ff5555;
}
.input{
  width:100%;
  font-size:34rpx;
  padding-bottom:20rpx;
  border-bottom:1rpx solid #eee;
  margin-top: 5px;
  height: 30px;
  margin-left: 10px;
}
.login-btn{
  height:96rpx;
  font-size:34rpx;
}
.cancel-text{
  text-align:center;
  margin-top:40rpx;
  font-size:34rpx;
  color:#ff86a9;
}
</style>
