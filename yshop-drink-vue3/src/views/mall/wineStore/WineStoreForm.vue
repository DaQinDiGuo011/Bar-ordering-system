<template>
  <el-dialog
    v-model="visible"
    :title="formType === 'create' ? '新增酒水寄存' : '编辑酒水寄存'"
    width="600px"
    destroy-on-close
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" label-width="100px" :rules="rules">
      
      <el-form-item label="寄存用户" prop="userId">
        <div class="flex items-center gap-2">
          <div v-if="selectedMemberName" class="flex items-center">
            <el-tag :closable="!lockUser" @close="clearMember">
              {{ selectedMemberName }}
            </el-tag>
          </div>
          <div v-else class="text-gray-400 text-sm">未选择寄存用户</div>
          <el-button v-if="!lockUser" type="primary" @click="openMemberDialog">选择用户</el-button>
          <!-- <el-checkbox v-model="bindMemberInfo" label="关联姓名手机号"/> -->
        </div>
      </el-form-item>
      <el-form-item label="酒水商品" prop="productId">
        <div class="flex items-center gap-2">
          <el-input v-model="selectedProductName" placeholder="请选择酒水商品" readonly class="flex-1"/>
          <el-button type="primary" @click="openProductDialog">选择商品</el-button>
        </div>
      </el-form-item>
      <el-form-item label="寄存人姓名" prop="realName">
        <el-input
          v-model="formData.realName"
          placeholder="请输入寄存人姓名"
          :disabled="formType=='update' || lockUser"
        />
      </el-form-item>
      <el-form-item label="寄存手机号" prop="phone">
        <el-input
          v-model="formData.phone"
          placeholder="请输入手机号"
          :disabled="formType=='update' || lockUser"
        />
      </el-form-item>
      <el-form-item label="权限密码" prop="pwd">
          <el-input v-model="formData.pwd" placeholder="密码" type="password" show-password/>
      </el-form-item>
      <el-form-item label="寄存数量" prop="num">
        <el-input-number v-model="formData.num" :min="1" class="w-full"/>
      </el-form-item>
      <el-form-item label="寄存单号" prop="storeNo">
        <el-input v-model="formData.storeNo" placeholder="系统自动生成" disabled />
      </el-form-item>
      <el-form-item label="寄存状态" prop="storeStatus">
        <el-select v-model="formData.storeStatus">
          <el-option label="待支付" :value="1"/>
          <el-option label="存储中" :value="2"/>
          <el-option label="已失效" :value="3"/>
          <el-option label="领取中" :value="4"/>
          <el-option label="已领取" :value="5"/>
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" rows="3" placeholder="备注信息"/>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="memberDialogVisible" title="选择寄存用户" width="750px" destroy-on-close @close="resetMemberSelect">
    <!-- 搜索栏：用户名/手机号搜索 -->
    <el-form :model="memberQuery" inline class="mb-3">
      <el-form-item label="用户名">
        <el-input v-model="memberQuery.nickname" placeholder="输入用户名搜索" clearable />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="memberQuery.phone" placeholder="输入手机号搜索" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getMemberPage">查询</el-button>
        <el-button @click="resetMemberQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="memberList" border highlight-current-row v-loading="memberLoading"
              @current-change="handleMemberCurrentChange" style="margin-bottom:10px">
      <el-table-column type="radio" width="55" />
      <el-table-column prop="id" label="用户ID" width="80"/>
      <el-table-column prop="nickname" label="用户姓名"/>
      <el-table-column prop="mobile" label="手机号"/>
    </el-table>

    <el-pagination
      v-model:current-page="memberQuery.pageNo"
      v-model:page-size="memberQuery.pageSize"
      :page-sizes="[5, 10, 20]"
      :total="memberTotal"
      layout="total, sizes, prev, pager, next"
      @update:current-page="()=>{ getMemberPage() }"
      @size-change="handleMemberSizeChange"
    />

    <template #footer>
      <el-button @click="memberDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="confirmSelectMember">确定选择</el-button>
    </template>
  </el-dialog>

  <!-- 商品选择弹窗：搜索+分页(5/10/20)+单选+确定按钮 -->
  <el-dialog v-model="productDialogVisible" title="选择酒水商品" width="750px" destroy-on-close @close="resetProductSelect">
    <!-- 搜索栏 -->
    <el-form :model="productQuery" inline class="mb-3">
      <el-form-item label="商品名称">
        <el-input v-model="productQuery.storeName" placeholder="输入商品名称搜索" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getProductPage">查询</el-button>
        <el-button @click="resetProductQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="productList" border highlight-current-row v-loading="productLoading"
              @current-change="handleCurrentChange" style="margin-bottom:10px">
      <el-table-column type="radio" width="55" />
      <el-table-column prop="id" label="商品ID" width="80"/>
      <el-table-column prop="storeName" label="商品名称"/>
      <el-table-column prop="price" label="价格"/>
    </el-table>

    <!-- 分页：page‑size改变触发@size‑change；页码改变触发@change -->
    <el-pagination
      v-model:current-page="productQuery.pageNo"
      v-model:page-size="productQuery.pageSize"
      :page-sizes="[5, 10, 20]"
      :total="productTotal"
      layout="total, sizes, prev, pager, next"
      @update:current-page="(val)=>{ productQuery.pageNo = val; getProductPage() }"
      @size-change="handleSizeChange"
    />

    <template #footer>
      <el-button @click="productDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="confirmSelectProduct">确定选择</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import * as WineStoreApi from '@/api/mall/wineStore'
import * as ProductApi from '@/api/mall/product/product'
import * as UserApi from '@/api/member/user'

const emit = defineEmits(['success'])
const visible = ref(false)
const formRef = ref()
const formType = ref<'create'|'update'>('create')
const editId = ref<number|null>(null)
const lockUser = ref(false)

// ========= 用户选择弹窗 =========
const memberDialogVisible = ref(false)
const selectedMemberName = ref('')
const memberLoading = ref(false)
const memberList = ref<any[]>([])
const memberTotal = ref(0)
const currentSelectMemberRow = ref<any>(null)
const bindMemberInfo = ref(true)
const memberQuery = reactive({
  pageNo: 1,
  pageSize: 10,
  nickname: null,
  phone: null,
})

// 商品弹窗相关
const productDialogVisible = ref(false)
const selectedProductName = ref('')
const productLoading = ref(false)
const productList = ref<any[]>([])
const productTotal = ref(0)
// 当前表格选中行
const currentSelectRow = ref<any>(null)

// 商品分页查询参数，默认每页10条
const productQuery = reactive({
  pageNo: 1,
  pageSize: 10,
  storeName: '',
  isShow: 1
})

const formData = reactive({
  id:null,
  userId:null,
  productId:null,
  realName:'',
  phone:'',
  num:1,
  storeNo:'',
  storeStatus:2,
  pwd: '',
  remark:''
})
const clearMember = ()=>{
  formData.userId = null
  if(bindMemberInfo.value){
    formData.realName = ''
    formData.phone = ''
  }
  selectedMemberName.value = ''
  currentSelectMemberRow.value = null
}
const rules = {
  realName:[{required:true,message:'请填写寄存人姓名',trigger:'blur'}],
  phone:[{required:true,message:'请填写手机号',trigger:'blur'},
    {
      validator: (rule: any, value: string, callback: any) => {
        const reg = /^1[3-9]\d{9}$/
        if (!reg.test(value)) {
          callback(new Error('请输入正确的11位手机号'))
        } else {
          callback()
        }
      },
      trigger: ['blur','change']
    }],
  productId:[{required:true,message:'请选择酒水商品',trigger:'blur'}],
  pwd:[{required:true,message:'请输入密码',trigger:'blur'}]
}

/** 每页条数切换事件：切换size回到第一页再查询 */
const handleSizeChange = () => {
  productQuery.pageNo = 1
  getProductPage()
}

/** 打开商品弹窗 */
const openProductDialog = async ()=>{
  productDialogVisible.value = true
  resetProductSelect()
  productQuery.pageNo = 1
  await getProductPage()
}
/** 获取商品分页列表 */
const getProductPage = async ()=>{
  productLoading.value = true
  try {
    const res = await ProductApi.getStoreProductPage(productQuery)
    productList.value = res.list
    productTotal.value = res.total
  } finally {
    productLoading.value = false
  }
}

/** 表格单选变化 */
const handleCurrentChange = (row:any)=>{
  currentSelectRow.value = row
}

/** 确定选择商品按钮 */
const confirmSelectProduct = ()=>{
  if(!currentSelectRow.value){
    ElMessage.warning('请选择一条商品')
    return
  }
  formData.productId = currentSelectRow.value.id
  selectedProductName.value = currentSelectRow.value.storeName + '【' + currentSelectRow.value.id + '】'
  productDialogVisible.value = false
}

/** 重置商品选中状态 */
const resetProductSelect = ()=>{
  currentSelectRow.value = null
}

/** 重置搜索条件 */
const resetProductQuery = ()=>{
  productQuery.storeName = ''
  productQuery.pageNo = 1
  getProductPage()
}

/** 打开主弹窗 */
const open = async (
  type: 'create' | 'update',
  id?: number,
  userId?: number,
  realName?: string,
  phone?: string
) => {
  formType.value = type
  editId.value = id ?? null
  resetForm()
  if(type === 'update' && id){
    const res = await WineStoreApi.getInfoById(id)
    Object.assign(formData,res)
    selectedProductName.value = formData.storeName + '【' + formData.productId + '】'
    if(formData.userId){
      selectedMemberName.value = `${formData.realName}(${formData.phone})`
    }
    // 编辑时，需要根据productId回显商品名称，自行补接口
    // selectedProductName.value = res.storeName
  }
  if(userId){
    lockUser.value = true
    if(type === 'create'){
      formData.userId = userId
      formData.realName = realName || ''
      formData.phone = phone || ''
      selectedMemberName.value = `${realName}(${phone})`
    }
  }
  visible.value = true
}

const resetForm = ()=>{
  lockUser.value = false
  formRef.value?.resetFields()
  Object.assign(formData,{
    id:null,
    userId:null,
    productId:null,
    realName:'',
    phone:'',
    pwd: '',
    num:1,
    storeNo:'',
    storeStatus:2,
    remark:''
  })
  selectedProductName.value = ''
  // bindMemberInfo.value = false
  selectedMemberName.value = ''
  currentSelectMemberRow.value = null
}

const handleClose = ()=>{
  resetForm()
}

const submitForm = async ()=>{
  try {
    await formRef.value.validate()
    await WineStoreApi.saveInfo(formData)
    ElMessage.success('操作成功')
    visible.value = false
    emit('success')
  }catch(err){
    console.error('保存失败',err)
  }
}

// ---------------- 用户弹窗逻辑【新增】 ----------------
const openMemberDialog = async () => {
  memberDialogVisible.value = true
  resetMemberSelect()
  memberQuery.pageNo = 1
  await getMemberPage()
}

const getMemberPage = async () => {
  memberLoading.value = true
  try {
    const res = await UserApi.getUserPage(memberQuery)
    console.log("----------res=",res)
    memberList.value = res.list
    memberTotal.value = res.total
  } finally {
    memberLoading.value = false
  }
}

const handleMemberCurrentChange = (row:any) => {
  if(row){
    currentSelectMemberRow.value = row
  }
}

const handleMemberSizeChange = () => {
  memberQuery.pageNo = 1
  getMemberPage()
}

const confirmSelectMember = () => {
  if(!currentSelectMemberRow.value){
    ElMessage.warning('请选择一位寄存用户')
    return
  }
  // 回填到主表单
  const row = currentSelectMemberRow.value
  formData.userId = row.id
  if(bindMemberInfo.value){
    formData.realName = row.nickname
    formData.phone = row.mobile
  }
  selectedMemberName.value = `${row.nickname}(${row.mobile})`
  memberDialogVisible.value = false
}

const resetMemberSelect = () => {
  currentSelectMemberRow.value = null
  resetMemberQuery()
}

const resetMemberQuery = () => {
  memberQuery.nickname = null
  memberQuery.phone = null
  memberQuery.pageNo = 1
  getMemberPage()
}

defineExpose({open})
</script>

<style scoped>
.flex {
  display: flex;
}
.gap-2 {
  gap: 8px;
}
.items-center {
  align-items: center;
}
.flex-1 {
  flex:1;
}
.mb-3 {
  margin-bottom:12px;
}
</style>
