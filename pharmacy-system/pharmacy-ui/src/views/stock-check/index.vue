<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>盘点管理</span>
          <el-button type="primary" @click="handleAdd">新增盘点</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="checkId" label="盘点ID" width="80" />
        <el-table-column prop="checkNo" label="盘点单号" width="150" />
        <el-table-column prop="medId" label="药品ID" width="80" />
        <el-table-column prop="batchNo" label="批号" width="120" />
        <el-table-column prop="systemStock" label="账面库存" width="100" />
        <el-table-column prop="actualStock" label="实际库存" width="100" />
        <el-table-column prop="profitLoss" label="盈亏数量" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.profitLoss > 0 ? '#67c23a' : row.profitLoss < 0 ? '#f56c6c' : '' }">
              {{ row.profitLoss > 0 ? '+' + row.profitLoss : row.profitLoss }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="profitLossAmount" label="盈亏金额" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.profitLossAmount > 0 ? '#67c23a' : row.profitLossAmount < 0 ? '#f56c6c' : '' }">
              {{ row.profitLossAmount ? Number(row.profitLossAmount).toFixed(2) : '0.00' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="盈亏原因" min-width="150" />
        <el-table-column prop="checkTime" label="盘点时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
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

    <el-dialog
      v-model="dialogVisible"
      title="新增盘点"
      width="1200px"
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
            <el-form-item label="盘点单号" prop="checkNo">
              <el-input v-model="formData.checkNo" placeholder="请输入盘点单号，如：PD20250425001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="盘点时间" prop="checkTime">
              <el-date-picker
                v-model="formData.checkTime"
                type="datetime"
                placeholder="选择盘点时间"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注（可选）" />
        </el-form-item>
        
        <el-divider content-position="left">
          <span style="font-size: 16px; font-weight: bold;">盘点明细</span>
        </el-divider>
        
        <div v-for="(item, index) in formData.items" :key="index" style="margin-bottom: 20px;">
          <el-card :body-style="{ padding: '20px' }">
            <template #header>
              <div class="card-header">
                <span style="font-weight: bold;">明细 {{ index + 1 }}</span>
                <el-button type="danger" size="small" link @click="handleRemoveItem(index)">删除此明细</el-button>
              </div>
            </template>
            
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="药品ID" :required="true">
                  <el-select v-model="item.medId" placeholder="请选择药品" style="width: 100%" @change="handleMedIdChange(index)">
                    <el-option v-for="stock in stockList" :key="stock.stockId" :label="stock.medName + ' (' + stock.batchNo + ')'" :value="stock.medId" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="药品名称">
                  <el-input v-model="item.medName" disabled placeholder="选择药品后自动填充" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="批号">
                  <el-input v-model="item.batchNo" placeholder="请输入批号" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="6">
                <el-form-item label="账面库存">
                  <el-input-number v-model="item.systemStock" :min="0" style="width: 100%" placeholder="系统库存数量" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="实际库存" :required="true">
                  <el-input-number v-model="item.actualStock" :min="0" style="width: 100%" placeholder="请输入实际盘点数量" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="盈亏数量">
                  <div :style="{ 
                    padding: '8px 12px', 
                    borderRadius: '4px', 
                    backgroundColor: (item.actualStock - item.systemStock) === 0 ? '#f5f7fa' : (item.actualStock - item.systemStock) > 0 ? '#f0f9ff' : '#fef0f0',
                    color: (item.actualStock - item.systemStock) === 0 ? '#909399' : (item.actualStock - item.systemStock) > 0 ? '#67c23a' : '#f56c6c',
                    fontWeight: 'bold',
                    fontSize: '16px'
                  }">
                    {{ (item.actualStock - item.systemStock) > 0 ? '+' + (item.actualStock - item.systemStock) : (item.actualStock - item.systemStock) }}
                    <span v-if="(item.actualStock - item.systemStock) === 0" style="font-weight: normal; font-size: 14px; margin-left: 8px;">（无盈亏）</span>
                    <span v-else style="font-weight: normal; font-size: 14px; margin-left: 8px;">（{{ (item.actualStock - item.systemStock) > 0 ? '盘盈' : '盘亏' }}）</span>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="成本单价">
                  <el-input-number v-model="item.unitPrice" :min="0" :precision="2" :step="0.01" style="width: 100%" placeholder="成本价" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="盈亏金额">
                  <div :style="{ 
                    padding: '8px 12px', 
                    borderRadius: '4px', 
                    backgroundColor: (item.actualStock - item.systemStock) * item.unitPrice === 0 ? '#f5f7fa' : (item.actualStock - item.systemStock) * item.unitPrice > 0 ? '#f0f9ff' : '#fef0f0',
                    color: (item.actualStock - item.systemStock) * item.unitPrice === 0 ? '#909399' : (item.actualStock - item.systemStock) * item.unitPrice > 0 ? '#67c23a' : '#f56c6c',
                    fontWeight: 'bold',
                    fontSize: '16px'
                  }">
                    ¥ {{ Number((item.actualStock - item.systemStock) * (item.unitPrice || 0)).toFixed(2) }}
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item 
              label="盈亏原因" 
              :required="(item.actualStock - item.systemStock) !== 0"
              :style="{ marginBottom: 0 }"
            >
              <el-input 
                v-model="item.reason" 
                type="textarea" 
                :rows="2" 
                :placeholder="(item.actualStock - item.systemStock) === 0 ? '无盈亏，原因可选填' : '请填写盈亏原因，如：破损、过期、日常损耗等'"
                :style="{
                  borderColor: (item.actualStock - item.systemStock) !== 0 && !item.reason ? '#f56c6c' : ''
                }"
              />
              <div v-if="(item.actualStock - item.systemStock) !== 0" style="color: #f56c6c; font-size: 12px; margin-top: 4px;">
                ⚠️ 存在盈亏，请务必填写原因
              </div>
            </el-form-item>
            
          </el-card>
        </div>
        
        <div style="margin-top: 10px;">
          <el-button type="primary" @click="handleAddItem">+ 添加盘点明细</el-button>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getStockCheckList, createStockCheck } from '@/api/stock-check'
import { getStockListWithMedicine } from '@/api/stock'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  checkNo: '',
  checkUser: 1,
  checkTime: '',
  remark: '',
  items: []
})

const formRules = {
  checkNo: [{ required: true, message: '请输入盘点单号', trigger: 'blur' }],
  checkTime: [{ required: true, message: '请选择盘点时间', trigger: 'change' }]
}

const stockList = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getStockCheckList({
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

const fetchStockList = async () => {
  try {
    const res = await getStockListWithMedicine({ current: 1, size: 1000 })
    if (res.code === 200) {
      stockList.value = res.data.records
    }
  } catch (error) {
    console.error('获取库存列表失败', error)
  }
}

const handleAdd = () => {
  Object.assign(formData, {
    checkNo: '',
    checkUser: 1,
    checkTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
    remark: '',
    items: []
  })
  fetchStockList()
  dialogVisible.value = true
}

const handleAddItem = () => {
  formData.items.push({
    medId: null,
    medName: '',
    batchNo: '',
    systemStock: 0,
    actualStock: 0,
    unitPrice: 0,
    reason: '',
    remark: ''
  })
}

const handleRemoveItem = (index) => {
  formData.items.splice(index, 1)
}

const handleMedIdChange = (index) => {
  const item = formData.items[index]
  if (item.medId) {
    // 找到对应药品的库存信息
    const stock = stockList.value.find(s => s.medId === item.medId)
    if (stock) {
      item.medName = stock.medName
      item.batchNo = stock.batchNo
      item.systemStock = stock.stockNum
      item.unitPrice = stock.purchasePrice
    }
  }
}

const validateItems = () => {
  for (let i = 0; i < formData.items.length; i++) {
    const item = formData.items[i]
    if (!item.medId) {
      ElMessage.error(`第${i + 1}条明细：请选择药品ID`)
      return false
    }
    if (!item.batchNo) {
      ElMessage.error(`第${i + 1}条明细：请输入批号`)
      return false
    }
    if (item.actualStock === null || item.actualStock === undefined) {
      ElMessage.error(`第${i + 1}条明细：请输入实际库存`)
      return false
    }
    // 检查是否有盈亏且需要填写原因
    const profitLoss = item.actualStock - item.systemStock
    if (profitLoss !== 0 && !item.reason) {
      ElMessage.error(`第${i + 1}条明细：有盈亏请填写盈亏原因`)
      return false
    }
  }
  return true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (formData.items.length === 0) {
        ElMessage.warning('请至少添加一条盘点明细')
        return
      }
      if (!validateItems()) {
        return
      }
      try {
        const res = await createStockCheck(formData)
        if (res.code === 200) {
          ElMessage.success('新增盘点成功')
          dialogVisible.value = false
          fetchData()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

onMounted(() => {
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
</style>
