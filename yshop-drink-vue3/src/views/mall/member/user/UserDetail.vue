<template>
 <el-drawer v-model="drawer" :title="dialogTitle" size="70%">
    <div>
      <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
        <el-tab-pane label="用户信息" name="first">
           <el-descriptions title="基本信息" :column="2">
            <el-descriptions-item label="用户头像"><el-image style="width: 50px; height: 50px" :src="DetailData.avatar ?? defaultAvatar" /></el-descriptions-item>
            <el-descriptions-item label="用户昵称">{{ DetailData.nickname }}</el-descriptions-item>
            <el-descriptions-item label="余额">{{ DetailData.nowMoney }}</el-descriptions-item>
            <el-descriptions-item label="生日">{{ DetailData.birthday }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ DetailData.mobile }}</el-descriptions-item>
            <el-descriptions-item label="可用优惠券数量">{{ DetailData.usableCouponNum ?? 0  }}</el-descriptions-item>

            <!-- <el-descriptions-item label="身份证号码">{{ DetailData.cardId }}</el-descriptions-item> -->
          </el-descriptions>
          <el-descriptions title="用户概况" :column="2">
            <el-descriptions-item label="绑定的会员卡">{{ DetailData.cardName ? DetailData.cardName : '无' }}</el-descriptions-item>
            <el-descriptions-item label="积分">{{ DetailData.integral }}</el-descriptions-item>
            <!-- <el-descriptions-item label="佣金金额">{{ DetailData.brokeragePrice }}</el-descriptions-item> -->
            <el-descriptions-item label="连续签到天数">{{ DetailData.signNum }}</el-descriptions-item>
            <el-descriptions-item label="登录ip">{{ DetailData.loginIp }}</el-descriptions-item>
            <!-- <el-descriptions-item label="等级">{{ DetailData.level }}</el-descriptions-item> -->
            <!-- <el-descriptions-item label="推广id">{{ DetailData.spreadUid }}</el-descriptions-item> -->
            <el-descriptions-item label="购买次数">{{ DetailData.payCount }}</el-descriptions-item>
            <!-- <el-descriptions-item label="下级人数">{{ DetailData.spreadCount }}</el-descriptions-item> -->
            <el-descriptions-item label="登陆类型">
              <span v-if="DetailData.loginType == 'routine'">小程序</span>
              <span v-else-if="DetailData.loginType == 'wechat'">公众号</span>
              <span v-else-if="DetailData.loginType == 'h5'">H5</span>
              <span v-else>未知</span>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatDate(DetailData.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="消费记录" name="second">
             <el-table  :data="list">
              <el-table-column label="支出/获得" align="center" prop="pm">
                  <template #default="scope">
                    <span v-if="scope.row.pm == 0">支出</span>
                    <span v-else-if="scope.row.category == 1">获得</span>
                    <span v-else>未知</span>
                  </template>
              </el-table-column>
              <el-table-column label="账单标题" align="center" prop="title" />
              <el-table-column label="明细种类" align="center" prop="category">
                 <template #default="scope">
                    <span v-if="scope.row.category == 'now_money'">余额</span>
                    <span v-else-if="scope.row.category == 'integral'">积分</span>
                    <span v-else>未知</span>
                  </template>
              </el-table-column>
              <el-table-column label="明细类型" align="center" prop="type">
                <template #default="scope">
                    <span v-if="scope.row.type == 'recharge'">充值</span>
                    <span v-else-if="scope.row.type == 'brokerage'">返佣</span>
                    <span v-else-if="scope.row.type == 'pay_product'">消费</span>
                    <span v-else-if="scope.row.type == 'extract'">提现</span>
                    <span v-else-if="scope.row.type == 'pay_product_refund'">退款</span>
                    <span v-else-if="scope.row.type == 'system_add'">系统添加</span>
                    <span v-else-if="scope.row.type == 'system_sub'">系统减少</span>
                    <span v-else-if="scope.row.type == 'deduction'">减去</span>
                    <span v-else-if="scope.row.type == 'gain'">奖励</span>
                    <span v-else-if="scope.row.type == 'sign'">签到</span>
                    <span v-else-if="scope.row.type == 'vip_card'">会员卡</span>
                    <span v-else>未知</span>
                  </template>
              </el-table-column>
              <el-table-column label="明细数字(元)" align="center" prop="number">
                    <template #default="scope">
                      <span v-if="scope.row.pm == 1">+</span>
                      <span v-else>-</span>
                      <span>{{ scope.row.number }}</span>
                    </template>
              </el-table-column>
              <el-table-column label="剩余(元)" align="center" prop="balance" />
              <el-table-column label="备注" align="center" prop="mark" width="200" />
              <el-table-column
                label="添加时间"
                align="center"
                prop="createTime"
                :formatter="dateFormatter"
                width="100"
              />
            </el-table>
            <!-- 分页 -->
            <Pagination
              :total="total"
              v-model:page="queryParams.pageNo"
              v-model:limit="queryParams.pageSize"
              @pagination="getList"
            />
        </el-tab-pane>
        <el-tab-pane label="优惠券信息" name="coupon">
          <el-form :model="couponQuery" inline class="mb-10px">
            <el-form-item label="优惠券状态">
              <el-select v-model="couponQuery.status" placeholder="全部状态" clearable style="width:160px">
                <el-option label="未使用" :value="0" />
                <el-option label="已使用" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="couponQuery.pageNo=1;getCouponList()">查询</el-button>
              <el-button @click="resetCouponQuery()">重置</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="couponList" border v-loading="couponLoading">
            <el-table-column label="领券id" align="center" prop="id" />
            <el-table-column label="优惠券名称" align="center" prop="title" />
            <el-table-column label="满减门槛" align="center" prop="least">
              <template #default="scope">
                <span>满{{ scope.row.least }}可用</span>
              </template>
            </el-table-column>
            <el-table-column label="优惠券金额" align="center" prop="value">
              <template #default="scope">
                <span style="color:#f56c6c">¥{{ scope.row.value }}</span>
              </template>
            </el-table-column>
            <el-table-column label="可用类型" align="center" prop="type">
              <template #default="scope">
                <span v-if="scope.row.type === 0">通用</span>
                <span v-else-if="scope.row.type === 1">自取</span>
                <span v-else-if="scope.row.type === 2">外卖</span>
                <span v-else-if="scope.row.type === 4">一人一券</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" align="center" prop="status">
              <template #default="scope">
                <el-tag v-if="scope.row.status ===0" type="success">未使用</el-tag>
                <el-tag v-else-if="scope.row.status ===1" type="info">已使用</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="有效期开始" align="center" prop="startTime" :formatter="dateFormatter" width="170"/>
            <el-table-column label="有效期结束" align="center" prop="endTime" :formatter="dateFormatter" width="170"/>
            <el-table-column label="兑换码" align="center" prop="exchangeCode"/>
            <el-table-column label="领取时间" align="center" prop="createTime" :formatter="dateFormatter" width="170"/>
          </el-table>
          <Pagination
            :total="couponTotal"
            v-model:page="couponQuery.pageNo"
            v-model:limit="couponQuery.pageSize"
            @pagination="getCouponList"
          />
        </el-tab-pane>
        <el-tab-pane label="寄存信息" name="wine">
          <el-form :model="wineQuery" inline class="mb-10px">
            <el-form-item label="寄存状态">
              <el-select v-model="wineQuery.storeStatus" placeholder="全部状态" clearable style="width:180px">
                <el-option label="审核中" :value="1" />
                <el-option label="存储中" :value="2" />
                <el-option label="已失效" :value="3" />
                <el-option label="领取中" :value="5" />
                <el-option label="已领取" :value="6" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="wineQuery.pageNo=1;getWineStoreList()">查询</el-button>
              <el-button @click="resetWineQuery()">重置</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="wineStoreList" border v-loading="wineLoading">
            <el-table-column label="寄存单号" align="center" prop="storeNo"/>
            <el-table-column label="寄存数量" align="center" prop="num"/>
            <el-table-column label="商品名称" align="center" prop="storeName"/>
            <el-table-column label="总金额" align="center" prop="totalPrice"/>
            <el-table-column label="优惠金额" align="center" prop="couponPrice"/>
            <el-table-column label="实付金额" align="center" prop="actualPayPrice"/>
            <el-table-column label="支付类型" align="center" prop="payType">
              <!-- <template #default="scope">
                <span v-if="scope.row.payType === '微信支付'">微信支付</span>
                <span v-else-if="scope.row.payType === '余额支付'">余额支付</span>
                <span v-else>-</span>
              </template> -->
            </el-table-column>
            <el-table-column label="领券ID" align="center" prop="couponIdList"/>
            <el-table-column label="寄存状态" align="center" prop="storeStatus">
              <template #default="scope">
                <el-tag v-if="scope.row.storeStatus ===1" type="info">审核中</el-tag>
                <el-tag v-else-if="scope.row.storeStatus ===2" type="primary">存储中</el-tag>
                <el-tag v-else-if="scope.row.storeStatus ===3" type="warning">已失效</el-tag>
                <el-tag v-else-if="scope.row.storeStatus ===4" type="success">领取中</el-tag>
                <el-tag v-else-if="scope.row.storeStatus ===5" type="success">已领取</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="寄存时间" align="center" prop="createTime" :formatter="dateFormatter"/>
            <el-table-column label="领取时间" align="center" prop="receiveTime" :formatter="dateFormatter"/>
            <el-table-column label="备注" align="center" prop="remark" width="180"/>
          </el-table>
          <Pagination
            :total="wineTotal"
            v-model:page="wineQuery.pageNo"
            v-model:limit="wineQuery.pageSize"
            @pagination="getWineStoreList"
          />
        </el-tab-pane>
      </el-tabs>
     
    </div>
  </el-drawer>
</template>
<script setup lang="ts">
import * as UserApi from '@/api/member/user'
import { formatDate } from '@/utils/formatTime'
import type { TabsPaneContext } from 'element-plus'
import * as UserBillApi from '@/api/member/userBill'
import { dateFormatter } from '@/utils/formatTime'
import * as WineStoreApi from '@/api/mall/wineStore'
import * as CouponUserApi from '@/api/mall/coupon/user/'
import defaultAvatar from '@/static/images/user-default.png'

const { t } = useI18n() // 国际化
// const message = useMessage() // 消息弹窗
const dialogTitle = ref('') // 弹窗的标题
const drawer = ref(false)
const DetailData = ref({})
const activeName = ref('first')
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  uid: null,
  linkId: null,
  pm: null,
  title: null,
  category: null,
  type: null,
  number: null,
  balance: null,
  mark: null,
  createTime: [],
  status: null
})
const loading = ref(false)


// ==========优惠券变量==========
const couponLoading = ref(false)
const couponList = ref<any[]>([])
const couponTotal = ref(0)
const couponQuery = reactive({
  pageNo:1,
  pageSize:10,
  deleted: -1,
  userId:null as number | null,
  status: null as number | null 
})

// ==========寄存变量==========
const wineLoading = ref(false)
const wineStoreList = ref<any[]>([])
const wineTotal = ref(0)
const wineQuery = reactive({
  pageNo:1,
  pageSize:10,
  userId:null as number | null,
  storeStatus: null as number | null 
})

/** 重置优惠券筛选 */
const resetCouponQuery = () => {
  couponQuery.status = null
  couponQuery.pageNo = 1
  getCouponList()
}

/** 重置寄存筛选 */
const resetWineQuery = () => {
  wineQuery.storeStatus = null
  wineQuery.pageNo = 1
  getWineStoreList()
}

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  drawer.value = true
  dialogTitle.value = t('action.' + type)
  DetailData.value = await UserApi.getUser(id)
  queryParams.uid = id
  getList()

   // 重置账单
  queryParams.pageNo = 1
  list.value = []
  total.value = 0

  //重置优惠券
  couponQuery.userId = id ?? null
  couponQuery.pageNo = 1
  couponQuery.status = null 
  couponList.value = []
  couponTotal.value = 0

  //重置寄存
  wineQuery.userId = id ?? null
  wineQuery.pageNo = 1
  wineQuery.storeStatus = null
  wineStoreList.value = []
  wineTotal.value = 0

  activeName.value = 'first'
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const handleClick = (tab: TabsPaneContext, event: Event) => {
  console.log("--------tab---",tab.paneName)
  if(tab.paneName === 'second'){
    // queryParams.pageNo = 1
    // getList()
  }else if(tab.paneName === 'coupon'){
    couponQuery.pageNo = 1
    getCouponList()
  }else if(tab.paneName === 'wine'){
    wineQuery.pageNo = 1
    getWineStoreList()
  }
}
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await UserBillApi.getUserBillPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}
/** 查询优惠券 */
const getCouponList = async ()=>{
  couponLoading.value = true
  try {
    // await 后端接口 getUserCouponPage(couponQuery)
    const res = await CouponUserApi.getUserPage(couponQuery)
    couponList.value = res.list
    couponTotal.value = res.total
  }finally {
    couponLoading.value = false
  }
}

/** 查询寄存记录 */
const getWineStoreList = async ()=>{
  wineLoading.value = true
  try {
    const res = await WineStoreApi.getWineStorePage(wineQuery)
    console.log("-------------res==",res)
    wineStoreList.value = res.list
    wineTotal.value = res.total
    // await 后端接口 getUserWineStorePage(wineQuery)
    // const res = await getUserWineStorePage(wineQuery)
    // wineStoreList.value = res.list
    // wineTotal.value = res.total
  }finally {
    wineLoading.value = false
  }
}
</script>
<style scoped>
</style>