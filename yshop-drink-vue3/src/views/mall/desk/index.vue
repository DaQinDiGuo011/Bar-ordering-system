<template>
  <div class="qr-code-wrap">
    <el-card header="生成小程序码">
      <el-form :model="form">
        <el-form-item label="scene参数(字母数字下划线，最大32位)">
          <el-input v-model="form.scene" placeholder="例如 A01"/>
        </el-form-item>
        <el-form-item label="跳转小程序页面">
          <el-input v-model="form.page" placeholder="pages/menu/menu"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getQrCode" :loading="loading">生成小程序码</el-button>
        </el-form-item>
      </el-form>

      <!-- 展示生成的小程序码 -->
      <div v-if="qrImgUrl" class="img-box">
        <p>生成结果：</p>
        <img :src="qrImgUrl" alt="小程序码图片" style="width:320px;height:320px;" />
        <div style="margin-top:12px;">
          <el-button @click="downloadImg">下载图片</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref,reactive } from 'vue'
import * as WineStoreApi from '@/api/mall/wineStore' //你的项目封装request
 
const loading = ref(false)
const qrImgUrl = ref('')

const form = reactive({
  scene:'A01',
  page:'pages/menu/menu'
})

// 调用后端接口获取小程序码二进制流
async function getQrCode(){
  loading.value = true
  // 释放旧blob，防止内存泄漏
  if(qrImgUrl.value){
    URL.revokeObjectURL(qrImgUrl.value)
    qrImgUrl.value = ''
  }
  try{
    /**
     responseType:'blob' 非常关键，告诉axios接收二进制图片流，不要解析json
     params传递scene、page参数
     */
    let params = {
        scene: form.scene,
        page: form.page
      }
    let res = await WineStoreApi.createDeskCode(params)
    // blob转浏览器可预览图片地址
    // qrImgUrl.value = URL.createObjectURL(res.data)
  }catch(err){
    console.error('生成小程序码失败',err)
  }finally {
    loading.value = false
  }
}

// 下载小程序码图片
function downloadImg(){
  if(!qrImgUrl.value) return
  const a = document.createElement('a')
  a.href = qrImgUrl.value
  a.download = "小程序码_"+form.scene+".png"
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
}

</script>

<style scoped>
.qr-code-wrap{
  padding:20px;
}
.img-box{
  margin-top:20px;
}
</style>