<template>
  <div class="page store-page" v-loading="loading">
    <section class="panel store-info-panel">
      <h1 class="panel-title">药店基本信息</h1>
      <div class="store-info-grid">
        <div class="store-info-item">
          <span class="icon-bubble"><el-icon><House /></el-icon></span>
          <strong>药店名称</strong>
          <p>{{ storeInfo.name }}</p>
        </div>
        <div class="store-info-item">
          <span class="icon-bubble"><el-icon><Clock /></el-icon></span>
          <strong>营业时间</strong>
          <p>{{ storeInfo.hours }}（全年无休）</p>
        </div>
        <div class="store-info-item">
          <span class="icon-bubble"><el-icon><Phone /></el-icon></span>
          <strong>联系电话</strong>
          <p>{{ storeInfo.phone }}</p>
        </div>
        <div class="store-info-item">
          <span class="icon-bubble"><el-icon><Location /></el-icon></span>
          <strong>详细地址</strong>
          <p>{{ storeInfo.address }}</p>
        </div>
        <div class="store-info-item">
          <span class="icon-bubble"><el-icon><Memo /></el-icon></span>
          <strong>经营范围</strong>
          <p>{{ storeInfo.scope }}</p>
        </div>
        <div class="store-info-item">
          <span class="icon-bubble"><el-icon><Service /></el-icon></span>
          <strong>服务说明</strong>
          <p>提供药品咨询、用药指导、健康知识服务，为您的健康保驾护航。</p>
        </div>
      </div>
    </section>

    <section class="section store-layout">
      <div class="panel">
        <h2 class="panel-title">位置示意</h2>
        <div class="map-card">
          <div class="river"></div>
          <div class="road road-a"></div>
          <div class="road road-b"></div>
          <div class="road road-c"></div>
          <div class="park park-a"></div>
          <div class="park park-b"></div>
          <div class="pin">
            <el-icon><LocationFilled /></el-icon>
            <strong>{{ storeInfo.name }}</strong>
          </div>
        </div>
        <p class="traffic">
          <el-icon><LocationFilled /></el-icon>
          交通指引：{{ storeInfo.routeText }}
        </p>
      </div>

      <aside class="panel route-panel">
        <h2 class="panel-title">路线说明</h2>
        <div class="route-item" v-for="item in routeItems" :key="item.title">
          <span class="icon-bubble small"><el-icon><component :is="item.icon" /></el-icon></span>
          <div>
            <strong>{{ item.title }}</strong>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </aside>
    </section>

    <section class="section panel notice-panel">
      <h2 class="panel-title">药店公告 / 温馨提醒</h2>
      <div class="notice-grid">
        <div v-for="item in notices" :key="item.title">
          <el-icon><component :is="item.icon" /></el-icon>
          <div>
            <strong>{{ item.title }}</strong>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  BellFilled,
  Bicycle,
  Clock,
  House,
  Location,
  LocationFilled,
  Memo,
  Phone,
  Service,
  Van
} from '@element-plus/icons-vue'
import { getClientStoreInfo } from '@/api/client-store'
import { storeInfo as fallbackStoreInfo } from '@/data/mock'

const loading = ref(false)
const storeInfo = reactive({
  ...fallbackStoreInfo,
  locationText: '安心镇中心街',
  routeText: '可乘坐安化1路、2路公交车至梅城镇政府站下车，步行约300米即达。',
  services: []
})
const routeItems = [
  { title: '自驾导航', desc: '导航搜索“安心乡镇药房”即可到达', icon: Bicycle },
  { title: '公交路线', desc: '乘坐安化1路、2路至“梅城镇政府站”', icon: Van },
  { title: '步行路线', desc: '梅城镇政府向南步行约300米', icon: Location }
]
const notices = [
  { title: '处方药请凭处方购买', desc: '请携带有效处方到店购买处方药。', icon: BellFilled },
  { title: '如需咨询可联系药师', desc: '专业药师为您提供用药指导与解答。', icon: Service },
  { title: '请按说明书或药师建议使用', desc: '用药安全，健康为先。', icon: Memo },
  { title: '营业时间：08:00 - 20:00', desc: '全年无休，为您提供服务。', icon: Clock }
]

const loadStoreInfo = async () => {
  loading.value = true
  try {
    const { data } = await getClientStoreInfo()
    if (data.code !== 200) {
      throw new Error(data.message || '药店信息查询失败')
    }
    Object.assign(storeInfo, data.data || {})
  } catch (error) {
    ElMessage.error('药店信息查询失败，请确认后端服务已启动')
  } finally {
    loading.value = false
  }
}

onMounted(loadStoreInfo)
</script>

<style scoped>
.store-page {
  display: grid;
  gap: 20px;
}

.store-info-panel {
  padding: 22px;
}

.store-info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  border: 1px solid var(--client-border);
  border-radius: 8px;
  overflow: hidden;
}

.store-info-item {
  display: grid;
  grid-template-columns: 74px 130px 1fr;
  align-items: center;
  gap: 18px;
  min-height: 86px;
  padding: 18px 22px;
  border-bottom: 1px solid var(--client-border);
}

.store-info-item:nth-child(odd) {
  border-right: 1px solid var(--client-border);
}

.store-info-item:nth-last-child(-n + 2) {
  border-bottom: 0;
}

.store-info-item p {
  margin: 0;
  line-height: 1.7;
}

.store-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 490px;
  gap: 20px;
}

.map-card {
  position: relative;
  height: 215px;
  overflow: hidden;
  border-radius: 6px;
  background: #eef2f7;
}

.map-card::before,
.map-card::after,
.road {
  position: absolute;
  content: '';
  height: 16px;
  border-radius: 999px;
  background: #ffffff;
  transform-origin: left center;
}

.map-card::before {
  top: 40px;
  left: -30px;
  width: 760px;
  transform: rotate(12deg);
}

.map-card::after {
  top: 126px;
  left: -20px;
  width: 820px;
  transform: rotate(-18deg);
}

.road-a {
  top: 85px;
  left: -60px;
  width: 800px;
  transform: rotate(35deg);
}

.road-b {
  top: -20px;
  left: 240px;
  width: 16px;
  height: 360px;
  transform: rotate(26deg);
}

.road-c {
  top: 0;
  left: 530px;
  width: 16px;
  height: 340px;
  transform: rotate(48deg);
}

.river {
  position: absolute;
  right: 150px;
  bottom: -70px;
  width: 440px;
  height: 170px;
  border-radius: 50%;
  border: 28px solid rgba(110, 178, 242, 0.35);
}

.park {
  position: absolute;
  width: 160px;
  height: 80px;
  border-radius: 45%;
  background: rgba(149, 223, 172, 0.35);
}

.park-a {
  top: 0;
  left: 60px;
}

.park-b {
  right: 120px;
  top: 20px;
}

.pin {
  position: absolute;
  top: 84px;
  left: 44%;
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--client-text);
  font-size: 18px;
}

.pin .el-icon {
  color: var(--client-primary);
  font-size: 48px;
}

.traffic {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 16px 0 0;
  color: var(--client-muted);
  font-size: 17px;
}

.traffic .el-icon {
  color: var(--client-primary);
}

.route-panel {
  display: grid;
  align-content: start;
  gap: 12px;
}

.route-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px;
  border: 1px solid var(--client-border);
  border-radius: 8px;
  background: var(--client-surface-soft);
}

.route-item p {
  margin: 6px 0 0;
  color: var(--client-muted);
}

.notice-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 24px;
}

.notice-grid > div {
  display: flex;
  gap: 14px;
}

.notice-grid .el-icon {
  color: var(--client-primary);
  font-size: 28px;
}

.notice-grid p {
  margin: 6px 0 0;
  color: var(--client-muted);
}

@media (max-width: 1100px) {
  .store-info-grid,
  .store-layout,
  .notice-grid {
    grid-template-columns: 1fr;
  }

  .store-info-item,
  .store-info-item:nth-child(odd),
  .store-info-item:nth-last-child(-n + 2) {
    border-right: 0;
    border-bottom: 1px solid var(--client-border);
  }

  .store-info-item:last-child {
    border-bottom: 0;
  }
}

@media (max-width: 720px) {
  .store-info-item {
    grid-template-columns: 54px 1fr;
  }

  .store-info-item p {
    grid-column: 2;
  }
}
</style>
