<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>库存管理</span>
        </div>
      </template>

      <div class="table-toolbar">
        <el-button type="success" @click="handleExport">导出Excel</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        :row-class-name="tableRowClassName"
      >
        <el-table-column prop="stockId" label="库存ID" width="80" />
        <el-table-column prop="medName" label="药品名称" min-width="150" />
        <el-table-column prop="supplierName" label="供应商" min-width="140">
          <template #default="{ row }">
            {{ row.supplierName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="spec" label="规格" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="batchNo" label="批号" width="120" />
        <el-table-column prop="stockNum" label="库存数量" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.isLowStock ? '#f56c6c' : '' }">{{ row.stockNum }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stockMin" label="预警下限" width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.stockMin" :min="0" size="small" controls-position="right" style="width: 110px" />
          </template>
        </el-table-column>
        <el-table-column prop="expireDate" label="有效期至" width="120" />
        <el-table-column prop="cabinet" label="存放位置" width="100" />
        <el-table-column label="状态" width="180">
          <template #default="{ row }">
            <el-tag v-if="row.isExpired" type="danger" effect="dark">已过期</el-tag>
            <el-tag v-if="row.isNearExpire && !row.isExpired" type="danger" effect="dark">近效期</el-tag>
            <el-tag v-if="row.isLowStock" type="warning" effect="dark">库存不足</el-tag>
            <el-tag v-if="!row.isExpired && !row.isNearExpire && !row.isLowStock" type="success">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="入库时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleSaveStockMin(row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStockListWithMedicine, exportStock, updateStock } from '@/api/stock'
import { downloadBlob } from '@/utils/download'

const loading = ref(false)
const tableData = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableRowClassName = ({ row }) => {
  if (row.isExpired || row.isNearExpire || row.isLowStock) {
    return 'warning-row'
  }
  return ''
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStockListWithMedicine({
      current: pagination.current,
      size: pagination.size
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleExport = async () => {
  try {
    const blob = await exportStock()
    downloadBlob(blob, '库存数据.xlsx')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handleSaveStockMin = async (row) => {
  try {
    const res = await updateStock({
      stockId: row.stockId,
      medId: row.medId,
      batchNo: row.batchNo,
      expireDate: row.expireDate,
      stockNum: row.stockNum,
      stockMin: row.stockMin || 0,
      purchasePrice: row.purchasePrice,
      cabinet: row.cabinet,
      productionDate: row.productionDate,
      supplierId: row.supplierId,
      status: row.status,
      remark: row.remark
    })
    if (res.code === 200) {
      ElMessage.success('预警下限已保存')
      fetchData()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '保存失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-container {
  width: 100%;
}

:deep(.warning-row) {
  background-color: #fef0f0 !important;
}

.table-toolbar {
  margin-bottom: 20px;
}
</style>
