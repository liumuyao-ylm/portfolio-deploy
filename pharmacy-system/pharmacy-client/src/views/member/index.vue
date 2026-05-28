<template>
  <div class="page member-page">
    <div class="page-title">
      <h1>会员中心</h1>
      <span>欢迎使用安心乡镇药房会员服务</span>
    </div>

    <section v-loading="loading" class="member-profile">
      <template v-if="memberInfo">
        <div class="avatar-wrap">
          <el-icon><UserFilled /></el-icon>
        </div>
        <div class="profile-main">
          <div class="name-row">
            <span>会员姓名：</span>
            <strong>{{ memberInfo.name }}</strong>
          </div>
          <div class="phone-row">
            <span>手机号：</span>
            <strong>{{ memberInfo.phone }}</strong>
          </div>
        </div>
        <div class="profile-stat">
          <span>会员等级</span>
          <el-tag size="large">{{ memberInfo.memberLevel || '普通会员' }}</el-tag>
        </div>
        <div class="profile-stat">
          <span>累计消费金额</span>
          <strong class="amount">¥{{ formatPrice(memberInfo.totalConsume) }}</strong>
        </div>
        <div class="profile-stat">
          <span>账户状态</span>
          <el-tag type="success" size="large">{{ memberInfo.statusText || '正常' }}</el-tag>
        </div>
      </template>
      <el-empty v-else description="暂无会员信息" />
    </section>

    <section class="section content-grid">
      <div class="panel records-panel">
        <div class="table-heading">
          <h2 class="panel-title">消费记录</h2>
          <el-button :icon="Refresh" :disabled="!memberInfo" @click="loadOrders">刷新记录</el-button>
        </div>
        <el-table v-loading="ordersLoading" :data="orders" border empty-text="暂无消费记录">
          <el-table-column prop="orderId" label="订单号" min-width="150" />
          <el-table-column label="消费时间" min-width="170">
            <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="订单金额（元）" width="140">
            <template #default="{ row }">{{ formatPrice(row.totalPrice) }}</template>
          </el-table-column>
          <el-table-column prop="payType" label="支付方式" width="120" />
          <el-table-column label="订单状态" width="120">
            <template #default="{ row }">
              <el-tag type="success">{{ row.payStatusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text :icon="View" @click="openOrder(row)">查看明细</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="records-footer">
          <span>共 {{ orders.length }} 条记录</span>
          <el-pagination
            layout="prev, pager, next"
            :page-size="memberPage.size"
            :total="memberPage.total"
            v-model:current-page="memberPage.current"
            @current-change="loadMemberPage"
          />
        </div>
      </div>

      <aside class="side-panel">
        <div class="tip-card">
          <el-icon><InfoFilled /></el-icon>
          <div>
            <h2 class="panel-title">温馨提示</h2>
            <p>会员信息如需修改，请联系药店工作人员。</p>
            <div class="remark-block">
              <strong>会员备注</strong>
              <p>{{ memberInfo?.remark || '暂无特殊备注。' }}</p>
            </div>
          </div>
        </div>
      </aside>
    </section>

    <el-dialog v-model="orderDialogVisible" title="订单详情" width="680px">
      <template v-if="selectedOrder">
        <div class="order-summary">
          <span>单号：{{ selectedOrder.orderId }}</span>
          <span>时间：{{ formatDateTime(selectedOrder.createTime) }}</span>
          <span>合计：¥{{ formatPrice(selectedOrder.totalPrice) }}</span>
        </div>
        <el-table :data="selectedOrder.items || []" border>
          <el-table-column prop="medicineName" label="药品" min-width="180" />
          <el-table-column prop="quantity" label="数量" width="90" />
          <el-table-column label="单价" width="110">
            <template #default="{ row }">¥{{ formatPrice(row.unitPrice) }}</template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="{ row }">¥{{ formatPrice(row.totalPrice) }}</template>
          </el-table-column>
        </el-table>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { InfoFilled, Refresh, UserFilled, View } from '@element-plus/icons-vue'
import { getClientMemberOrders, getClientMemberPage } from '@/api/client-member'

const loading = ref(false)
const ordersLoading = ref(false)
const memberInfo = ref(null)
const orders = ref([])
const selectedOrder = ref(null)
const orderDialogVisible = ref(false)
const memberPage = reactive({
  current: 1,
  size: 1,
  total: 0
})

const loadMemberPage = async () => {
  loading.value = true
  try {
    const { data } = await getClientMemberPage({
      current: memberPage.current,
      size: memberPage.size
    })
    if (data.code !== 200) {
      throw new Error(data.message || '会员资料加载失败')
    }
    const result = data.data || {}
    memberPage.current = Number(result.current || memberPage.current)
    memberPage.total = Number(result.total || 0)
    memberInfo.value = result.records?.[0] || null
    orders.value = []
    if (memberInfo.value?.customerId) {
      await loadOrders()
    }
  } catch (error) {
    memberInfo.value = null
    orders.value = []
    ElMessage.error('会员资料加载失败，请确认后端服务已启动')
  } finally {
    loading.value = false
  }
}

const loadOrders = async () => {
  if (!memberInfo.value?.customerId) return
  ordersLoading.value = true
  try {
    const { data } = await getClientMemberOrders(memberInfo.value.customerId)
    if (data.code !== 200) {
      throw new Error(data.message || '消费记录查询失败')
    }
    orders.value = data.data || []
  } catch (error) {
    orders.value = []
    ElMessage.error('消费记录查询失败，请确认后端服务已启动')
  } finally {
    ordersLoading.value = false
  }
}

const openOrder = (order) => {
  selectedOrder.value = order
  orderDialogVisible.value = true
}

const formatPrice = (price) => Number(price || 0).toFixed(2)

const formatDateTime = (value) => {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

onMounted(loadMemberPage)
</script>

<style scoped>
.member-page {
  display: grid;
  gap: 0;
}

.page-title {
  display: flex;
  align-items: baseline;
  gap: 18px;
  margin-bottom: 28px;
}

.page-title h1 {
  margin: 0;
  font-size: 30px;
}

.page-title span {
  color: var(--client-muted);
}

.member-profile {
  display: grid;
  grid-template-columns: 136px minmax(300px, 1.25fr) repeat(3, minmax(170px, 1fr));
  align-items: center;
  min-height: 166px;
  padding: 34px 46px;
  border: 1px solid var(--client-border);
  border-radius: 8px;
  background: var(--client-surface);
  box-shadow: var(--client-shadow);
}

.avatar-wrap {
  display: grid;
  place-items: center;
  width: 96px;
  height: 96px;
  border-radius: 50%;
  color: #ffffff;
  background: linear-gradient(145deg, #2f7ded, #0f62fe);
  font-size: 58px;
}

.profile-main {
  display: grid;
  gap: 22px;
}

.name-row,
.phone-row {
  display: flex;
  align-items: center;
  gap: 22px;
  font-size: 20px;
}

.name-row span,
.phone-row span,
.profile-stat span {
  color: var(--client-muted);
}

.name-row strong {
  font-size: 24px;
}

.profile-stat {
  display: grid;
  justify-content: center;
  gap: 16px;
  min-height: 78px;
  padding-left: 48px;
  border-left: 1px solid var(--client-border);
}

.amount {
  color: var(--client-primary);
  font-size: 26px;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 330px;
  gap: 20px;
}

.records-panel {
  min-width: 0;
  padding: 24px 26px;
}

.records-panel :deep(.el-table__cell) {
  height: 52px;
}

.records-panel :deep(.el-table .cell) {
  padding: 0 26px;
}

.table-heading,
.order-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.side-panel {
  display: grid;
  align-content: start;
}

.tip-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  min-height: 594px;
  padding: 38px;
  border: 1px solid var(--client-border);
  border-radius: 8px;
  background: var(--client-surface);
  box-shadow: var(--client-shadow);
}

.tip-card > .el-icon {
  color: var(--client-primary);
  flex: 0 0 auto;
  font-size: 34px;
}

.tip-card .panel-title {
  color: var(--client-primary);
}

.tip-card p {
  margin: 0;
  color: var(--client-muted);
  line-height: 1.8;
  font-size: 18px;
}

.remark-block {
  margin-top: 30px;
  padding-top: 24px;
  border-top: 1px solid var(--client-border);
}

.remark-block strong {
  display: block;
  margin-bottom: 10px;
  color: var(--client-text);
  font-size: 18px;
}

.records-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-top: 14px;
  padding: 0 12px;
  color: var(--client-muted);
}

.order-summary {
  align-items: flex-start;
  justify-content: flex-start;
  flex-wrap: wrap;
  color: var(--client-muted);
}

@media (max-width: 1100px) {
  .member-profile {
    grid-template-columns: 100px minmax(0, 1fr);
    gap: 18px;
  }

  .profile-stat {
    justify-content: start;
    grid-column: span 2;
    padding: 16px 0 0;
    border-top: 1px solid var(--client-border);
    border-left: 0;
  }

  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .page-title {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
  }

  .member-profile {
    grid-template-columns: 1fr;
    padding: 24px;
  }

  .profile-stat {
    grid-column: span 1;
  }

  .table-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .records-footer {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
