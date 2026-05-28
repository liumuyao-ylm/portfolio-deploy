<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>采购明细</span>
          <div class="header-tags" v-if="currentPurchaseId">
            <el-tag type="info">当前订单ID: {{ currentPurchaseId }}</el-tag>
            <el-tag :type="currentPurchaseOrder?.purchaseStatus === 1 ? 'success' : 'warning'">
              {{ currentPurchaseOrder?.purchaseStatus === 1 ? '已入库' : '待入库' }}
            </el-tag>
          </div>
        </div>
      </template>

      <div class="table-toolbar">
        <el-button type="primary" :disabled="isStockedIn" @click="handleAdd">新增</el-button>
        <el-button
          v-if="currentPurchaseId"
          type="success"
          :loading="stockInLoading"
          :disabled="isStockedIn || tableData.length === 0"
          @click="handleStockIn"
        >
          订单入库
        </el-button>
        <el-button type="danger" :disabled="multipleSelection.length === 0 || isStockedIn" @click="handleBatchDelete">批量删除</el-button>
        <el-button @click="handleBack" v-if="currentPurchaseId">返回订单列表</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="itemId" label="明细ID" width="100" />
        <el-table-column prop="purchaseId" label="订单ID" width="100" />
        <el-table-column prop="medName" label="药品名称" min-width="150" />
        <el-table-column prop="batchNo" label="批号" width="120" />
        <el-table-column prop="productionDate" label="生产日期" width="120" />
        <el-table-column prop="expireDate" label="有效期" width="120" />
        <el-table-column prop="purchaseNum" label="采购数量" width="100" />
        <el-table-column prop="purchasePrice" label="采购单价" width="120" />
        <el-table-column prop="totalPrice" label="总金额" width="120" />
        <el-table-column prop="cabinet" label="货柜" width="100" />
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" size="small" :disabled="isStockedIn" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" :disabled="isStockedIn" @click="handleDelete(row)">删除</el-button>
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
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单ID" prop="purchaseId">
              <el-input v-model="formData.purchaseId" placeholder="请输入订单ID" :disabled="!!currentPurchaseId" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="药品" prop="medId">
              <el-select v-model="formData.medId" placeholder="请选择药品" style="width: 100%">
                <el-option
                  v-for="item in medicineOptions"
                  :key="item.medId"
                  :label="item.medName"
                  :value="item.medId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="批号" prop="batchNo">
              <el-input v-model="formData.batchNo" placeholder="请输入批号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购数量" prop="purchaseNum">
              <el-input-number v-model="formData.purchaseNum" :min="1" placeholder="请输入采购数量" style="width: 100%" @change="calculateTotalPrice" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生产日期" prop="productionDate">
              <el-date-picker
                v-model="formData.productionDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择生产日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期" prop="expireDate">
              <el-date-picker
                v-model="formData.expireDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择有效期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购单价" prop="purchasePrice">
              <el-input-number v-model="formData.purchasePrice" :min="0" :precision="2" placeholder="请输入采购单价" style="width: 100%" @change="calculateTotalPrice" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总金额" prop="totalPrice">
              <el-input-number v-model="formData.totalPrice" :min="0" :precision="2" placeholder="请输入总金额" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="货柜" prop="cabinet">
              <el-input v-model="formData.cabinet" placeholder="请输入货柜" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPurchaseItemList, addPurchaseItem, updatePurchaseItem, deletePurchaseItem, stockInPurchaseItems } from '@/api/purchase-item'
import { getPurchaseOrderById } from '@/api/purchase-order'
import { getMedicineList } from '@/api/medicine'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const tableData = ref([])
const multipleSelection = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增采购明细')
const formRef = ref(null)
const currentPurchaseId = ref(null)
const currentPurchaseOrder = ref(null)
const medicineOptions = ref([])
const stockInLoading = ref(false)
const isStockedIn = computed(() => currentPurchaseOrder.value?.purchaseStatus === 1)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  itemId: null,
  purchaseId: null,
  medId: null,
  batchNo: '',
  productionDate: null,
  expireDate: null,
  purchaseNum: null,
  purchasePrice: null,
  totalPrice: null,
  cabinet: '',
  remark: ''
})

const formRules = {
  purchaseId: [{ required: true, message: '请输入订单ID', trigger: 'blur' }],
  medId: [{ required: true, message: '请选择药品', trigger: 'change' }],
  batchNo: [{ required: true, message: '请输入批号', trigger: 'blur' }],
  purchaseNum: [{ required: true, message: '请输入采购数量', trigger: 'change' }],
  purchasePrice: [{ required: true, message: '请输入采购单价', trigger: 'change' }],
  expireDate: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const fetchOptions = async () => {
  try {
    const res = await getMedicineList({ current: 1, size: 1000 })
    if (res.code === 200) {
      medicineOptions.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取药品列表失败')
  }
}

const fetchPurchaseOrder = async () => {
  if (!currentPurchaseId.value) {
    currentPurchaseOrder.value = null
    return
  }
  try {
    const res = await getPurchaseOrderById(currentPurchaseId.value)
    if (res.code === 200) {
      currentPurchaseOrder.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取采购订单状态失败')
  }
}

const calculateTotalPrice = () => {
  const num = Number(formData.purchaseNum) || 0
  const price = Number(formData.purchasePrice) || 0
  formData.totalPrice = Number((num * price).toFixed(2))
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size
    }
    if (currentPurchaseId.value) {
      params.purchaseId = currentPurchaseId.value
    }
    const res = await getPurchaseItemList(params)
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

const handleSelectionChange = (selection) => {
  multipleSelection.value = selection
}

const handleAdd = () => {
  dialogTitle.value = '新增采购明细'
  Object.assign(formData, {
    itemId: null,
    purchaseId: currentPurchaseId.value,
    medId: null,
    batchNo: '',
    productionDate: null,
    expireDate: null,
    purchaseNum: 1,
    purchasePrice: 0,
    totalPrice: 0,
    cabinet: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑采购明细'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (!formData.purchaseId) {
          ElMessage.warning('请先从采购订单进入明细页，或填写有效的订单ID')
          return
        }
        calculateTotalPrice()
        const res = formData.itemId ? await updatePurchaseItem(formData) : await addPurchaseItem(formData)
        if (res.code === 200) {
          ElMessage.success(formData.itemId ? '更新成功' : '添加成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error(error?.response?.data?.message || '操作失败')
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该采购明细吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deletePurchaseItem(row.itemId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 条记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      for (const item of multipleSelection.value) {
        await deletePurchaseItem(item.itemId)
      }
      ElMessage.success('批量删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  }).catch(() => {})
}

const handleStockIn = () => {
  if (!currentPurchaseId.value) return
  ElMessageBox.confirm('确定将当前采购订单的全部明细入库吗？入库后会增加库存并标记订单为已入库。', '入库确认', {
    confirmButtonText: '确定入库',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    stockInLoading.value = true
    try {
      const res = await stockInPurchaseItems(currentPurchaseId.value)
      if (res.code === 200) {
        ElMessage.success('入库成功')
        await fetchPurchaseOrder()
      } else {
        ElMessage.error(res.message || '入库失败')
      }
    } catch (error) {
      ElMessage.error(error?.response?.data?.message || '入库失败')
    } finally {
      stockInLoading.value = false
    }
  }).catch(() => {})
}

const handleBack = () => {
  router.push('/purchase-order')
}

onMounted(() => {
  fetchOptions()
  if (route.query.purchase_id) {
    currentPurchaseId.value = parseInt(route.query.purchase_id)
  }
  fetchPurchaseOrder()
  fetchData()
})
</script>

<style scoped>
.page-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-tags {
  display: flex;
  gap: 8px;
  align-items: center;
}

.table-toolbar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
