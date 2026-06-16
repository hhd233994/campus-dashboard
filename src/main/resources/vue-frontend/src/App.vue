<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* 清新典雅风格 - 校园消费数据管理平台 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background: #F7F8FC;
  color: #2C3E50;
  font-size: 14px;
  line-height: 1.6;
}

#app {
  height: 100vh;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #F0F1F5;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #C5CCD9;
  border-radius: 4px;
  transition: all 0.3s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: #8B9DC3;
}

/* Element Plus 全局样式覆盖 */
.el-card {
  border: none !important;
  border-radius: 12px !important;
}

.el-button {
  border-radius: 8px !important;
}

.el-input__wrapper {
  border-radius: 8px !important;
}
</style>
