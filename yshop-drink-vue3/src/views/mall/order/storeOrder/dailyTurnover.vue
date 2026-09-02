<template>
  <ContentWrap>
    <!--搜索栏-->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="80px"
      :rules="queryFormRules"
    >
      <el-form-item label="营业分割小时" prop="splitHour">
        <el-input-number
          v-model="queryParams.splitHour"
          :min="0"
          :max="23"
          size="default"
          class="!w-160px"
        />
        <span class="ml-10 text-gray-500">默认8点切分营业日</span>
      </el-form-item>

      <el-form-item label="统计时间" prop="statDateRange">
        <el-date-picker
          v-model="queryParams.statDateRange"
          value-format="YYYY-MM-DD"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          class="!w-260px"
        />
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" border style="width:100%">
      <el-table-column label="序号" align="center" type="index" width="60"/>
      <el-table-column label="营业统计日期" align="center" prop="bizDate" width="140"/>
      <el-table-column label="订单总数量" align="center" prop="totalOrderCount"/>
      <el-table-column label="有效支付订单" align="center" prop="payOrderCount"/>
      <el-table-column label="总营业额(元)" align="center" prop="totalTurnover"/>
      <el-table-column label="微信支付(元)" align="center" prop="wxAmount"/>
      <el-table-column label="余额支付(元)" align="center" prop="yueAmount"/>
      <el-table-column label="退款总金额(元)" align="center" prop="refundAmount"/>
      <el-table-column label="统计区间" align="center" prop="timeRangeStr" min-width="320"/>
    </el-table>

    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts" name="DailyTurnover">
import { ref, reactive, onMounted } from 'vue'
import * as StoreOrderApi from '@/api/mall/order/storeOrder'
import dayjs from 'dayjs'

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const total = ref(0)
const list = ref<any[]>([])

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  splitHour: 12, // ✅ 默认早上12点作为营业日分割点
  statDateRange: [] as string[]
})

// ==========新增表单校验规则：日期区间必输==========
const queryFormRules = {
  statDateRange: [
    {
      required: true,
      message: '请选择统计时间区间',
      trigger: 'change'
    }
  ]
}

const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    // 后端接收 startDate endDate
    if(params.statDateRange && params.statDateRange.length ===2){
      params.startDate = params.statDateRange[0]
      params.endDate = params.statDateRange[1]
    }
    const res = await StoreOrderApi.getDailyTurnoverPage(params)
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleQuery = async () => {
  // 表单校验
  await queryFormRef.value.validate()
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  queryParams.splitHour = 8
  // 重置默认还是最近一个月
  setDefaultDateRange()
  handleQuery()
}

// =========设置默认：最近一个月时间区间==========
const setDefaultDateRange = () => {
  const end = dayjs().format('YYYY-MM-DD')
  const start = dayjs().subtract(1, 'month').format('YYYY-MM-DD')
  queryParams.statDateRange = [start, end]
}

onMounted(()=>{
  // 页面加载先赋值默认一个月，再请求
  setDefaultDateRange()
  getList()
})
</script>

<style scoped>
</style>