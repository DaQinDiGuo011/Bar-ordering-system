<template>
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="寄存单号" prop="storeNo">
        <el-input
          v-model="queryParams.storeNo"
          placeholder="请输入寄存单号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-220px"
        />
      </el-form-item>
      <el-form-item label="姓名" prop="realName">
        <el-input
          v-model="queryParams.realName"
          placeholder="请输入姓名"
          clearable
          @keyup.enter="handleQuery"
          class="!w-200px"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-200px"
        />
      </el-form-item>
      <el-form-item label="寄存状态" prop="storeStatus">
        <el-select v-model="queryParams.storeStatus" placeholder="全部" clearable class="!w‑180px">
          <el-option label="审核中" :value="1" />
          <el-option label="存储中" :value="2" />
          <el-option label="已领取" :value="3" />
          <el-option label="已失效" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="primary" @click="openForm('create')">
          <Icon icon="ep:plus" class="mr-5px" />新增寄存
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="寄存单号" align="center" prop="storeNo" width="180" />
      <el-table-column label="寄存人" align="center" prop="realName" width="100" />
      <el-table-column label="手机号" align="center" prop="phone" width="130" />
      <el-table-column label="酒水名称" align="center" prop="storeName" width="160" />
      <el-table-column label="寄存数量" align="center" prop="num" width="100" />
      <el-table-column label="寄存状态" align="center" prop="storeStatusDesc" width="110" />
      <el-table-column label="备注" align="center" prop="remark" width="200" />
      <el-table-column label="领取时间" align="center" prop="receiveTime" :formatter="dateFormatter" width="170" />
      <el-table-column label="提交时间" align="center" prop="createTime" :formatter="dateFormatter" width="170" />
      <el-table-column label="操作" align="center" fixed="right" width="120">
        <template #default="scope">
           <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >编辑</el-button>
          <!-- <el-button
            link
            type="success"
            @click="printStoreLabel(scope.row)"
          >打印标签</el-button>

          <el-button
            v-if="scope.row.storeStatus === 2"
            link
            type="warning"
            @click="handleReceive(scope.row)"
          >领取酒水</el-button>

          <el-button
            v-if="scope.row.storeStatus === 2"
            link
            type="info"
            @click="handleInvalid(scope.row)"
          >设为失效</el-button> -->
          <!-- <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button> -->
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="changePage"
    />
  </ContentWrap>
   <WineStoreForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts" name="WineStore">
import { dateFormatter } from '@/utils/formatTime'
import * as WineStoreApi from '@/api/mall/wineStore'
import WineStoreForm from './WineStoreForm.vue'
const message = useMessage()

const loading = ref(true)
const total = ref(0)
const list = ref<any[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  storeNo: null,
  realName: null,
  phone: null,
  storeStatus: null
})
const queryFormRef = ref()

const formRef = ref()

// 打印寄存标签
// const printStoreLabel = (row:any)=>{
//   const printHtml = `
// <!DOCTYPE html>
// <html>
// <head>
// <meta charset="GBK">
// <style>
// *{margin:0;padding:0;font-family:SimHei;}
// body{width:40mm;padding:2mm;font-size:12px;}
// .title{text-align:center;font-weight:bold;font-size:14px;margin-bottom:4px;}
// .line{border-bottom:1px dashed #333;margin:4px 0;}
// </style>
// </head>
// <body>
// <div class="title">酒水寄存标签</div>
// <div class="line"></div>
// <div>寄存编号：${row.storeNo}</div>
// <div>姓名：${row.realName}</div>
// <div>手机：${row.phone}</div>
// <div>酒水：${row.productName}</div>
// <div>数量：${row.num}</div>
// <div>状态：${row.storeStatusDesc}</div>
// <div>寄存时间：${row.createTime}</div>
// </body>
// </html>
// `
//   const win = window.open('','_blank')
//   if(!win) return
//   win.document.write(printHtml)
//   win.document.close()
//   setTimeout(()=>{
//     win.print()
//   },300)
// }

const changePage = (page)=>{
  console.log("---------changepage=",page)
  queryParams.pageNo = page.page
  queryParams.pageSize = page.limit
  getList()
}

// 领取操作
const handleReceive = async (row:any)=>{
  await message.confirm(`确认领取该寄存酒水？`)
  let data = {
    id: row.id,
    storeStatus: 3
  }
  await WineStoreApi.updateStatus(data)
  message.success('领取成功')
  getList()
}

// 设置失效
const handleInvalid = async (row:any)=>{
  await message.confirm(`确认设置该寄存记录为已失效？`)
  let data = {
    id: row.id,
    storeStatus: 4
  }
  await WineStoreApi.updateStatus(data)
  message.success('设置成功')
  getList()
}

const getList = async () => {
  loading.value = true
  try {
    const res = await WineStoreApi.getWineStorePage(queryParams)
    console.log("-------------res==",res)
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}
const openForm = (type:'create'|'update',id?:number)=>{
  formRef.value.open(type,id)
}
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await WineStoreApi.deleteWineStore(id)
    message.success('删除成功')
    getList()
  } catch {}
}

onMounted(()=>{
  getList()
})
</script>