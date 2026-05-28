<template>
  <div class="page home-page">
    <section class="home-hero">
      <div class="hero-copy">
        <h1>欢迎来到 <span>安心乡镇药房</span></h1>
        <p>专业服务 · 诚信经营 · 守护健康</p>
      </div>
      <div class="hero-status">
        <el-tag type="success" size="large">
          <span class="status-dot"></span>
          {{ storeInfo.status }}
        </el-tag>
      </div>
      <div class="hero-info">
        <el-icon><Clock /></el-icon>
        <span>营业时间</span>
        <strong>{{ storeInfo.hours }}</strong>
      </div>
      <div class="hero-info">
        <el-icon><Phone /></el-icon>
        <span>联系电话</span>
        <strong>{{ storeInfo.phone }}</strong>
      </div>
      <div class="hero-info wide">
        <el-icon><Location /></el-icon>
        <span>药店地址</span>
        <strong>{{ storeInfo.address }}</strong>
      </div>
    </section>

    <section class="section home-grid">
      <div class="panel search-panel">
        <h2 class="section-title"><span></span>查找药品<span></span></h2>
        <div class="search-box">
          <el-input v-model="keyword" placeholder="请输入药品名称" clearable @keyup.enter="goSearch">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button type="primary" @click="goSearch">搜索</el-button>
        </div>

        <h3 class="sub-title"><span></span>常用分类<span></span></h3>
        <div class="category-grid">
          <RouterLink v-for="item in quickFilters" :key="item.label" :to="{ path: '/medicines', query: item.query }">
            <span class="icon-bubble"><el-icon><component :is="item.icon" /></el-icon></span>
            <strong>{{ item.label }}</strong>
            <em>{{ item.desc }}</em>
          </RouterLink>
        </div>
      </div>

      <aside class="panel service-card">
        <div class="service-head">
          <span class="icon-bubble"><el-icon><Headset /></el-icon></span>
          <div>
            <h2>AI智能客服</h2>
            <p>有问题？随时为您解答</p>
          </div>
        </div>
        <div class="service-list">
          <div v-for="item in services" :key="item.title">
            <span class="icon-bubble small"><el-icon><component :is="item.icon" /></el-icon></span>
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc }}</p>
            </div>
          </div>
        </div>
        <el-button type="primary" size="large" @click="$router.push('/chat')">立即咨询</el-button>
        <p class="service-time">服务时间：{{ storeInfo.hours }}</p>
      </aside>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Bowl,
  Briefcase,
  Clock,
  FirstAidKit,
  Goods,
  Headset,
  HelpFilled,
  Location,
  Phone,
  Search,
  Service,
  Warning
} from '@element-plus/icons-vue'
import { storeInfo } from '@/data/mock'

const router = useRouter()
const keyword = ref('')
const quickFilters = [
  { label: '感冒发热', desc: '退烧止咳 缓解不适', query: { keyword: '感冒' }, icon: Warning },
  { label: '肠胃用药', desc: '健胃消食 呵护肠胃', query: { keyword: '胃' }, icon: Bowl },
  { label: '外伤消毒', desc: '伤口护理 消毒止血', query: { keyword: '消毒' }, icon: FirstAidKit },
  { label: '慢病常备', desc: '高血压 糖尿病用药', query: { keyword: '缓释' }, icon: Goods },
  { label: '医疗器械', desc: '家用护理 健康监测', query: { type: '器械' }, icon: Briefcase }
]
const services = [
  { title: '用药咨询', desc: '用药方法、副作用、注意事项', icon: Service },
  { title: '药品查找', desc: '药品功效、适应症、库存情况', icon: Search },
  { title: '健康建议', desc: '常见健康问题，提供参考建议', icon: HelpFilled }
]

const goSearch = () => {
  router.push({ path: '/medicines', query: keyword.value ? { keyword: keyword.value } : {} })
}
</script>

<style scoped>
.home-hero {
  display: grid;
  grid-template-columns: minmax(340px, 1.2fr) 150px repeat(2, minmax(170px, 0.6fr)) minmax(280px, 0.9fr);
  align-items: center;
  gap: 24px;
  min-height: 190px;
  padding: 38px;
  border-radius: 8px;
  background: linear-gradient(135deg, #f5f9ff 0%, #ffffff 100%);
  box-shadow: var(--client-shadow);
}

.hero-copy h1 {
  margin: 0 0 18px;
  font-size: 38px;
}

.hero-copy span,
.section-title,
.service-head h2 {
  color: var(--client-primary);
}

.hero-copy p,
.hero-info span,
.service-card p,
.service-time,
.category-grid em {
  color: var(--client-muted);
}

.status-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  margin-right: 8px;
  border-radius: 50%;
  background: #22c55e;
}

.hero-info {
  display: grid;
  grid-template-columns: 42px 1fr;
  gap: 4px 12px;
  align-items: center;
}

.hero-info .el-icon {
  grid-row: span 2;
  color: var(--client-primary);
  font-size: 34px;
}

.hero-info strong {
  font-size: 17px;
}

.home-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 390px;
  gap: 30px;
}

.search-panel {
  padding: 40px 62px;
  background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%);
}

.section-title,
.sub-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 30px;
  margin: 0;
  font-size: 34px;
}

.sub-title {
  margin: 44px 0 28px;
  color: var(--client-text);
  font-size: 24px;
}

.section-title span,
.sub-title span {
  width: 72px;
  height: 1px;
  background: #c8d7eb;
}

.search-box {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 140px;
  max-width: 880px;
  margin: 28px auto 0;
}

.search-box :deep(.el-input__wrapper) {
  height: 64px;
  border: 1px solid var(--client-primary);
  border-radius: 8px 0 0 8px;
  box-shadow: none;
}

.search-box :deep(.el-input__inner) {
  font-size: 22px;
}

.search-box :deep(.el-input__prefix) {
  color: var(--client-muted);
  font-size: 32px;
}

.search-box .el-button {
  height: 64px;
  border-radius: 0 8px 8px 0;
  font-size: 22px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}

.category-grid a {
  display: grid;
  justify-items: center;
  gap: 10px;
  min-height: 210px;
  padding: 26px 16px;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 10px 24px rgba(31, 74, 127, 0.06);
}

.category-grid strong {
  font-size: 20px;
}

.category-grid em {
  font-style: normal;
  text-align: center;
}

.service-card {
  display: grid;
  align-content: center;
  gap: 28px;
  padding: 34px;
}

.service-head {
  display: flex;
  align-items: center;
  gap: 18px;
}

.service-head h2 {
  margin: 0 0 8px;
  font-size: 28px;
}

.service-list {
  display: grid;
  gap: 10px;
  padding: 18px;
  border-radius: 8px;
  background: var(--client-surface-soft);
}

.service-list > div {
  display: flex;
  align-items: center;
  gap: 14px;
}

.icon-bubble.small {
  width: 42px;
  height: 42px;
  font-size: 22px;
}

.service-list strong {
  font-size: 18px;
}

.service-list p,
.service-time {
  margin: 4px 0 0;
}

@media (max-width: 1180px) {
  .home-hero,
  .home-grid {
    grid-template-columns: 1fr;
  }

  .category-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .search-panel {
    padding: 28px 20px;
  }

  .search-box {
    grid-template-columns: 1fr;
  }

  .search-box :deep(.el-input__wrapper),
  .search-box .el-button {
    border-radius: 8px;
  }
}
</style>
