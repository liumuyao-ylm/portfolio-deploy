<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统日志</span>
        </div>
      </template>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="操作模块">
          <el-input v-model="queryForm.operModule" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-input v-model="queryForm.operType" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe style="width: 100%; margin-top: 20px;">
        <el-table-column prop="logId" label="日志ID" width="80" />
        <el-table-column prop="realName" label="操作人" width="120" />
        <el-table-column prop="operModule" label="操作模块" width="150" />
        <el-table-column prop="operType" label="操作类型" width="120" />
        <el-table-column prop="operContent" label="操作内容" />
        <el-table-column prop="operIp" label="操作IP" width="140" />
        <el-table-column prop="operTime" label="操作时间" width="180" />
      </el-table>
      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getSysLogList } from '@/api/sys-log'

const tableData = ref([])
const total = ref(0)
const queryForm = ref({
  current: 1,
  size: 10,
  operModule: '',
  operType: ''
})

const fetchData = async () => {
  try {
    const res = await getSysLogList(queryForm.value)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

const handleQuery = () => {
  queryForm.value.current = 1
  fetchData()
}

const handleReset = () => {
  queryForm.value = {
    current: 1,
    size: 10,
    operModule: '',
    operType: ''
  }
  fetchData()
}

const handleSizeChange = (val) => {
  queryForm.value.size = val
  fetchData()
}

const handleCurrentChange = (val) => {
  queryForm.value.current = val
  fetchData()
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

.query-form {
  margin-bottom: 0;
}
</style>
