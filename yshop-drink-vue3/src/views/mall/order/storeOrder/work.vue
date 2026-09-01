<template>
  <ContentWrap>
   <el-tabs v-model="activeName"  @tab-click="handleClick">
     <el-tab-pane label="全部订单" name="first"/>
     <el-tab-pane label="普通订单" name="second"/>
   </el-tabs>
 </ContentWrap>
 <ContentWrap>
   <!-- 搜索工作栏 -->
   <el-form
     class="-mb-15px"
     :model="queryParams"
     ref="queryFormRef"
     :inline="true"
     label-width="68px"
   >
     <el-form-item label="订单号" prop="orderId">
       <el-input
         v-model="queryParams.orderId"
         placeholder="请输入订单号"
         clearable
         @keyup.enter="handleQuery"
         class="!w-240px"
       />
     </el-form-item>
     <el-form-item label="取餐号" prop="numberId">
       <el-input
         v-model="queryParams.numberId"
         placeholder="请输入取餐号"
         clearable
         @keyup.enter="handleQuery"
         class="!w-240px"
       />
     </el-form-item>   
     <el-form-item>
       <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
       <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
       <el-button
         type="success"
         plain
         @click="handleExport"
         :loading="exportLoading"
         v-hasPermi="['order:store-order:export']"
       >
         <Icon icon="ep:download" class="mr-5px" /> 导出
       </el-button>
     </el-form-item>
   </el-form>
   
 </ContentWrap>

 <!-- 列表 -->
 <ContentWrap>

     <el-row :gutter="24">
       <el-col :span="6" v-for="(order,k) in list" :key="k"  style="margin-top: 10px;">
         <div >
           <el-card class="box-card">
             <template #header>
               <div class="card-header">
                 <span>{{ order.shopName }}</span>
                 
               </div>
             </template>
             <div>取餐号:{{ order.numberId }}
              <el-tag v-if="order.payType === 'JC'" type="warning" size="small" class="ml-8px">寄存领取</el-tag>
              <span v-if="order.refundStatus == 3" class="reRefundFormCls">退单拒绝</span>
             </div>
             <div>桌号:{{ order.deskNumber }}</div>
             <div>客户:{{ order.userRespVO?.nickname }}</div>
             <div>备注:{{ order.mark }}</div>
             <div v-if="order.refundStatus == 3">拒绝退单原因：{{order.refundReason}}</div>
             <div>下单时间:{{ formatDate(order.createTime) }}</div>
             <div>更新时间:{{ formatDate(order.updateTime) }}</div>
             <div>类型:{{ order.orderType == 'takeout' ? '外卖' : '送餐' }}</div>
             <div>  
               <el-button
                 type="primary"
                 @click="openForm('orderDetail', order.id)"
                 v-hasPermi="['order:store-order:update']"
               >详情</el-button>
               <el-button
                 v-if="order.refundStatus == 1"
                 type="danger"
                 @click="openForm('refundOrder', order.id)"
                 v-hasPermi="['order:store-order:update']"
               >退单</el-button>
               <el-button
                 v-if="order.refundStatus == 1"
                 style="margin-top: 10px;"
                 type="danger"
                 @click="openReRefundDialog(order.id)"
                 v-hasPermi="['order:store-order:update']"
               >拒绝退单</el-button>
               <el-button
                 v-if="(order.refundStatus == 0 || order.refundStatus == 3 )& order.status == 0"
                 type="primary"
                 @click="openForm('orderSend', order.id)"
                 v-hasPermi="['order:store-order:update']"
               >出单</el-button>
               
               <el-button
                v-if="(order.refundStatus == 0 || order.refundStatus == 3 ) & order.status == 1"
                 type="primary"
                 style="color: #001807;"
                @click="changeStatus(order)"
                 v-hasPermi="['order:store-order:update']"
               >送餐</el-button>
               <el-button
                 type="success"
                 v-if="(order.refundStatus == 0 || order.refundStatus == 3 ) & order.status == 2"
                 @click="changeStatus(order)"
                 v-hasPermi="['order:store-order:update']"
               >完成</el-button>
               <el-button
                 type="success"
                 v-if="(order.refundStatus == 0 || order.refundStatus == 3 ) & (order.status == 1 || order.status == 2)"
                 @click="printOrder(order.id)"
                 v-hasPermi="['order:store-order:update']"
               >打印</el-button>
               <!-- <el-button
                 type="danger"
                 @click="handleDelete(order.id)"
                 v-hasPermi="['order:store-order:update']"
               >删除</el-button> -->
             </div>
           </el-card>
        </div>
        </el-col>
        </el-row>
 </ContentWrap>

 <ContentWrap>
  <el-pagination
    v-model:current-page="queryParams.pageNo"
    v-model:page-size="queryParams.pageSize"
    :total="total"
    :page-sizes="[10, 20, 50, 100]"
    layout="total, sizes, prev, pager, next, jumper"
    @size-change="handleQuery"
    @current-change="getList"
  />
</ContentWrap>

 <el-dialog v-model="reRefundDialogVisible" title="拒绝退单" width="480px">
    <el-form ref="reRefundFormRef" :model="reRefundForm" :rules="reRefundRules" label-width="90px">
      <el-form-item label="操作密码" prop="pwd">
        <el-input
          v-model="reRefundForm.pwd"
          type="password"
          show-password
          placeholder="请输入操作密码"
        />
      </el-form-item>
      <el-form-item label="拒绝原因" prop="refuseReason">
        <el-input
          v-model="reRefundForm.refuseReason"
          type="textarea"
          :rows="4"
          placeholder="请输入拒绝退单原因"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="reRefundDialogVisible = false">取消</el-button>
      <el-button type="danger" :loading="reRefundSubmitLoading" @click="submitReRefund">确认拒绝退单</el-button>
    </template>
  </el-dialog>

 <!-- 表单弹窗：添加/修改 -->
 <StoreOrderForm ref="formRef" @success="getList" />
 <OrderSend ref="formRef1" @success="getList" />
 <OrderSendInfo ref="formRef2" @success="getList" />
 <StoreOrderRemark ref="formRef3" @success="getList" />
 <OrderDetail ref="formRef4" />
 <OrderRecord ref="formRef5" />
<StoreOrderRefund ref="formRef6" @success="getList" />
 <!-- 打印DOM：屏幕隐藏，仅打印生效 -->
    <div class="print-wrap" ref="printRef">
      <div class="ticket" style="margin-left: 15px;">
        <div class="titleCls" style="text-align: center;">{{orderData.shopName}}</div>
        <div class="line" style="border-bottom: 1px solid black;"></div>
        <div class="row">订单号：{{ orderData.numberId }}</div>
        <div class="row">桌号：{{ orderData.deskNumber }}</div>
        <div class="row">下单时间：{{ formatToDateTime(orderData.createTime) }}</div>
        <div class="row">客户：{{ orderData.userRespVO.nickname }}</div>
        <div class="row">备注：{{ orderData.mark }}</div>
        <div class="row" v-if="orderData.refundStatus == 3">拒绝退单原因：{{orderData .refundReason}}</div>
        <div class="line" style="border-bottom: 1px solid black;"></div>

        <!-- 商品列表 -->
        <div class="goods-header">
          <span style="display: inline-block; width: 80px; ">品名</span>
          <span style="display: inline-block; width: 40px;">数量</span>
          <span style="display: inline-block; width: 60px;">单价</span>
          <span style="display: inline-block; width: 60px;">小计</span>
        </div>
        <div v-for="(item,idx) in orderData.storeOrderCartInfoDOList" :key="idx" class="goods-row">
          <span style="display: inline-block; width: 80px; ">{{item.title}}</span> 
          <span style="display: inline-block; width: 40px;">{{item.number}}</span>
          <span style="display: inline-block; width: 60px;">{{item.price}}</span>
          <span style="display: inline-block; width: 60px;">{{item.number * item.price}}</span>
        </div>

        <div class="line" style="border-bottom: 1px solid black;"></div>
        <div class="total-row">合计：{{ orderData.totalPrice }} 元</div>
        <div class="total-row">优惠：{{ orderData.couponPrice }} 元</div>
        <div class="total-row">实付：{{ orderData.payPrice }} 元</div>
        <div class="tip">感谢惠顾，欢迎再次光临</div>
      </div>
    </div>
</template>

<script setup lang="ts" name="StoreOrder">
// import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as StoreOrderApi from '@/api/mall/order/storeOrder'
import StoreOrderForm from './StoreOrderForm.vue'
import OrderSend from './OrderSend.vue'
import OrderSendInfo from './OrderSendInfo.vue'
import StoreOrderRemark from './StoreOrderRemark.vue'
import OrderDetail from './OrderDetail.vue'
import OrderRecord from './OrderRecord.vue'
import type { TabsPaneContext, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/formatTime'
import { formatToDateTime } from '@/utils/dateUtil'
import StoreOrderRefund from './StoreOrderRefund.vue'
const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
 pageNo: 1,
 pageSize: 10,
 orderId: "",
 realName: "",
 userPhone: "",
 createTime: [],
 orderStatus: 1,
 payStatus: "",
 numberId: undefined

})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const formRef6 = ref()
const activeName = ref('first')
// ==========新增拒绝退单弹窗变量============
const reRefundDialogVisible = ref(false)
const reRefundSubmitLoading = ref(false)
const reRefundFormRef = ref()
const currentReRefundOrderId = ref<number | null>(null)
const reRefundForm = reactive({
  ped: '',
  refuseReason: ''
})
const reRefundRules = {
  pwd: [{ required: true, message: '请输入操作密码', trigger: 'blur' }],
  refuseReason: [{ required: true, message: '请输入拒绝退单原因', trigger: 'blur' }]
}

// 打开拒绝退单弹窗
const openReRefundDialog = (orderId: number) => {
  currentReRefundOrderId.value = orderId
  reRefundForm.pwd = ''
  reRefundForm.refuseReason = ''
  reRefundDialogVisible.value = true
}

// 提交拒绝退单
const submitReRefund = async () => {
  await reRefundFormRef.value.validate()
  reRefundSubmitLoading.value = true
  try {
    console.log("--data--------",{
      id: currentReRefundOrderId.value,
      pwd: reRefundForm.pwd,
      refuseReason: reRefundForm.refuseReason
    })
    // 这里调用你的拒绝退单api，参数：id、operatePwd、refuseReason
    await StoreOrderApi.reRefundUrl({
      id: currentReRefundOrderId.value,
      pwd: reRefundForm.pwd,
      refuseReason: reRefundForm.refuseReason
    })
    message.success('拒绝退单成功')
    reRefundDialogVisible.value = false
    getList()
  } catch (err) {
    console.error(err)
  } finally {
    reRefundSubmitLoading.value = false
  }
}

const changeStatus = async (order) =>{
  let statusId = order.status
  let msg = "";
  if(statusId == 1){
    msg = '确认送餐？'
  }else if(statusId == 2){
    msg = '确认送餐完成？'
  }

  ElMessageBox.confirm(msg, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async function () {
    if(statusId == 1){
      order.status = 2
    }else if(statusId == 2){
      order.status = 3
    }
    order.updateType = 'orderConfirm'
    await StoreOrderApi.updateStoreOrder(order)
    getList()
  })
}

const handleClick = (tab: TabsPaneContext, event: Event) => {
 console.log(tab, event)
 queryParams.pageNo = 1
 getList()
}


/** 查询列表 */
const getList = async () => {
 loading.value = true
 try {
   const data = await StoreOrderApi.getWorkStoreOrderPage(queryParams)
   list.value = data.list
   //console.log("aa:",list.value)
   total.value = data.total
 } finally {
   loading.value = false
 }
}
const printRef = ref<HTMLElement | null>(null)

  const orderData = ref({
  shopName: "",
  numberId: "",
  deskNumber: "",
  createTime: "",
  userRespVO: {
    nickname: ""
  },
  storeOrderCartInfoDOList: [
  ]
})

const printOrder = async(id)=>{
  if (!printRef.value) return

  orderData.value = await StoreOrderApi.getStoreOrder(id)
  console.log("-----formData---",orderData.value)

  await nextTick()

  
  const printDom = printRef.value.innerHTML
  const printWin = window.open('', '_blank')
  if (!printWin) {
    uni.showToast({ title: "请允许浏览器弹出新窗口", icon: "none" })
    return
  }

  printWin.document.write(`
    <!DOCTYPE html>
    <html lang="zh‑CN">
    <head>
    <meta charset="GBK">
    <title>点单小票</title>
    <style>
    *{margin:0;padding:0;font‑family:SimHei,Microsoft YaHei;box‑sizing:border‑box;}
    body{width:80mm;font‑size:13px;padding:2mm;}
    .titleCls{text‑align:center;font‑weight:bold;font‑size:16px;margin‑bottom:4px;display: block;}
    .line{border‑bottom:1px dashed #333;margin:4px 0;}
    .row{margin:2px 0;}
    .goods-header{display:flex;justify‑content:space‑between;margin:4px 0 2px;}
    .goods-row{display:flex;justify‑content:space‑between;margin:2px 0;}
    .total-row{text‑align:right;font‑weight:bold;margin‑top:4px;}
    .tip{text‑align:center;margin‑top:8px;font‑size:12px;}
    @media print{
      @page {
        size: 80mm auto;
        margin: 0;
      }
    }
    </style>
    </head>
    <body>
    ${printDom}
    </body>
    </html>
  `)
  printWin.document.close()

  // 等待渲染完成再调用打印
  setTimeout(() => {
    printWin.print()
    // printWin.close(); // 注释：部分浏览器关闭窗口会打断打印预览
  }, 300)

}

/** 搜索按钮操作 */
const handleQuery = () => {
 queryParams.pageNo = 1
 getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
 queryFormRef.value.resetFields()
 handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const formRef1 = ref()
const formRef2 = ref()
const formRef3 = ref()
const formRef4 = ref()
const formRef5 = ref()
const openForm = (type: string, id?: number) => {
 if (type == 'updateOrder') {
   formRef.value.open(type, id)
 } else if (type == 'orderSend') {
   formRef1.value.open(type, id)
 }else if (type == 'sendInfo') {
   formRef2.value.open(type, id)
 }else if (type == 'remark') {
   formRef3.value.open(type, id)
 }else if (type == 'orderDetail') {
   formRef4.value.open(type, id)
 }else if (type == 'orderRecord') {
   formRef5.value.open(type, id)
 }else if (type == 'refundOrder') {
    formRef6.value.open(type, id)
  }

 
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
 try {
   // 删除的二次确认
   await message.delConfirm()
   // 发起删除
   await StoreOrderApi.deleteStoreOrder(id)
   // 刷新列表
   getList()
 } catch {}
}


/** 导出按钮操作 */
const handleExport = async () => {
 try {
   // 导出的二次确认
   await message.exportConfirm()
   // 发起导出
   exportLoading.value = true
   const data = await StoreOrderApi.exportStoreOrder(queryParams)
   download.excel(data, '订单.xls')
 } catch {
 } finally {
   exportLoading.value = false
 }
}

/** 初始化 **/
onMounted(() => {
 getList()
})
</script>

<style scoped  >
img {
       height: 36px;
       display: block;
   }
   .reRefundFormCls{
    border: 1px solid red;
    margin-left: 40px;
    padding: 5px 10px;
    font-size: 12px;
    border-radius: 20px;
    background-color: #ff665c;
    color: #ffffff;
   }
.tabBox{
   width: 100%;
   height: 100%;
   display: flex;
   align-items: center
   }
.tabBox_img{
   width: 36px;
   height: 36px;
 }
.tabBox_img img{
 width: 100%;
 height: 100%;
}
.tabBox_tit{
   width :60%;
   font-size: 12px !important;
   margin: 0 2px 0 10px;
   letter-spacing: 1px;
   padding: 5px 0;
   box-sizing: border-box;
   text-align: left;
 }
 .tabBox_pice{
   width :30%;
   font-size: 12px !important;
   margin: 0 2px 0 10px;
   letter-spacing: 1px;
   padding: 5px 0;
   box-sizing: border-box;
   text-align: left;
 }
 .print-wrap {
    position: absolute;
    left: -9999rpx;
    top: -9999rpx;
    width: 0;
    height: 0;
    overflow: hidden;
  }
</style>