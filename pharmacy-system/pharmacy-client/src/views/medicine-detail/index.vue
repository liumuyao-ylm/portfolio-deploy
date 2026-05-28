<template>
  <div class="page">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/medicines' }">药品查询</el-breadcrumb-item>
      <el-breadcrumb-item>{{ medicine?.name || '药品详情' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <section v-if="medicine" v-loading="loading" class="section grid-2">
      <div class="panel detail-main">
        <div class="title-line">
          <div>
            <h1>{{ medicine.name }}</h1>
            <p v-if="medicine.medAlias" class="muted">别名：{{ medicine.medAlias }}</p>
          </div>
          <el-tag :type="medicine.isRx === 1 ? 'warning' : 'success'" size="large">
            {{ medicine.isRx === 1 ? '处方药' : '非处方药' }}
          </el-tag>
        </div>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="药品类型">{{ medicine.medType || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="规格">{{ medicine.spec || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="单位">{{ medicine.unit || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="剂型">{{ medicine.dosageForm || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="零售价">¥{{ formatPrice(medicine.retailPrice) }}</el-descriptions-item>
          <el-descriptions-item label="生产厂家">{{ medicine.manufacturer || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="批准文号">{{ medicine.approvalNo || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="库存状态">
            <el-tag :type="stockTagType(medicine.stockStatus)">
              {{ medicine.stockStatus }}，约 {{ medicine.stockCount ?? 0 }} {{ medicine.unit || '件' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <aside class="panel">
        <h2 class="panel-title">到店购买提示</h2>
        <div class="info-list">
          <div class="info-row"><span>药店</span><strong>{{ storeInfo.name }}</strong></div>
          <div class="info-row"><span>电话</span><strong>{{ storeInfo.phone }}</strong></div>
          <div class="info-row"><span>地址</span><strong>{{ storeInfo.address }}</strong></div>
          <div class="info-row"><span>营业时间</span><strong>{{ storeInfo.hours }}</strong></div>
        </div>
        <p class="notice">用药请遵医嘱或在药师指导下购买，处方药需凭处方购买。</p>
        <div class="detail-actions">
          <el-button type="primary" :icon="Phone" @click="$router.push('/store')">查看药店电话</el-button>
          <el-button :icon="Back" @click="$router.push('/medicines')">返回查询</el-button>
        </div>
      </aside>
    </section>

    <el-empty v-else v-loading="loading" description="未找到该药品">
      <el-button type="primary" @click="$router.push('/medicines')">返回药品查询</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Back, Phone } from '@element-plus/icons-vue'
import { getClientMedicineDetail } from '@/api/client-medicine'
import { storeInfo } from '@/data/mock'

const route = useRoute()
const loading = ref(false)
const medicine = ref(null)

const loadMedicine = async () => {
  loading.value = true
  try {
    const { data } = await getClientMedicineDetail(route.params.id)
    if (data.code !== 200) {
      throw new Error(data.message || '药品详情加载失败')
    }
    medicine.value = data.data
  } catch (error) {
    ElMessage.error('药品详情加载失败，请稍后重试')
    medicine.value = null
  } finally {
    loading.value = false
  }
}

const stockTagType = (status) => {
  if (status === '有货') return 'success'
  if (status === '库存较少') return 'warning'
  return 'danger'
}

const formatPrice = (price) => Number(price || 0).toFixed(2)

onMounted(loadMedicine)
</script>

<style scoped>
.title-line {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.title-line h1 {
  margin: 0;
  font-size: 30px;
}

.title-line p {
  margin: 8px 0 0;
}

.detail-main {
  display: grid;
  gap: 18px;
}

.detail-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 18px;
}
</style>
