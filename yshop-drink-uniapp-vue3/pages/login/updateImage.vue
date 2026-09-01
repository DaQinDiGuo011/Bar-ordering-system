<template>
	<view>
		<uv-popup
		      ref="showPrivacyPopupRef"
		      mode="bottom"
			  :round="20"
		      border-radius="24rpx"
		      :closeOnClickOverlay="false"
			  z-index="9990"
			  teleport
		    >
		      <view class="privacy-box">
		        <text class="popup-title">用户隐私保护提示</text>
		        <view class="popup-content">
		          <text>在你使用 TheOne Bar 服务之前，请仔细阅读</text>
		          <text 
		            class="link-text"
		            @click="goToPrivacyDoc"
		          >《德旺酒馆 小程序隐私保护指引》</text>
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
				          class="avatar-btn"
				          open-type="chooseAvatar"
				          @chooseavatar="onChooseAvatar"
				        >同意</uv-button>
		        </view>
		      </view>
		</uv-popup>
		
	</view>
</template>

<script setup>
	import {
	  ref,
	  computed, nextTick
	} from 'vue'
	import { VUE_APP_UPLOAD_URL } from '@/config';
	import { useMainStore } from '@/store/store'
	import { storeToRefs } from 'pinia'
	
	const emit = defineEmits(['close'])
	
	const main = useMainStore()
	const { member,openid, lang } = storeToRefs(main)
	const showPrivacyPopupRef = ref(null)
	// 头像底部菜单
	const showAvatarSheetRef = ref(null)
	
	
	// 点击头像打开弹窗
	const openPrivacyPopup = () => {
		const imagePrivateFlag = main.imagePrivateFlag
		console.log("imagePrivateFlag---===",imagePrivateFlag)
		if(imagePrivateFlag){
			nextTick(()=>{
				showAvatarSheetRef.value.open()
			})
		}else{
			nextTick(()=>{
				showPrivacyPopupRef.value.open()
			})
		}
	  
	}
	const onChooseAvatar = async (e) => {
	  console.log('拿到临时头像路径', e)
		main.SET_IMAGE_PRIVATE_FLAG(true)
		nextTick(()=>{
			showPrivacyPopupRef.value.close()
			setTimeout(()=>uni.showTabBar(),300)
		})
		uploadImage(e.avatarUrl)
	  // 此处执行uni.uploadFile上传到后端，拿到永久url再赋值
	}
	// 跳转隐私协议页面
	const goToPrivacyDoc = () => {
		
		  uni.navigateTo({
			url: '/pages/components/pages/privateProtectGuide/privateProtectGuide' // 你的隐私协议页面路径
		  })
	}
	
	// 拒绝隐私协议
	const handleRefuse = () => {
	  uni.showToast({
	    title: '您拒绝隐私协议，无法使用授权功能',
	    icon: 'none'
	  })
	  main.SET_IMAGE_PRIVATE_FLAG(false)
	  nextTick(()=>{
	  	showPrivacyPopupRef.value.close()
		setTimeout(()=>uni.showTabBar(),300)
	  })
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
				console.log('uploadFileResult:',uploadFileResult)
				if (uploadFileResult) {
					const upload = JSON.parse(uploadFileResult.data);
					console.log('upload:',upload.data)
					// member.value.avatar = upload.data;
					main.SET_AVATAR_URL_TEMP(upload.data)
					emit('updageImage', upload.data)
				}
				console.log("---------member==",member)
				uni.hideLoading()
			}, 
			fail(e){
				console.log(e)
				uni.showToast({title:'上传失败，请联系工作人员',icon:'none'})
				uni.hideLoading()
			}
		});
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
	
	defineExpose({
	  openPrivacyPopup
	})
</script>

<style>
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
   .avatar-btn {
		width: 100px;
		height: 30px;
   }
</style>
