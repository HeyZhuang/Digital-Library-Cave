// 浏览器缓存清理脚本（增强版）
// 专门解决单页应用路由跳转需要刷新的问题

(function() {
  'use strict';
  
  console.log('🧹 启动增强版缓存清理...');
  
  // 全面清理各种可能的缓存
  function clearAllCache() {
    try {
      // 1. 清理localStorage
      if (window.localStorage) {
        const keysToRemove = [];
        for (let i = 0; i < localStorage.length; i++) {
          const key = localStorage.key(i);
          if (key && (
            key.startsWith('vue-') || 
            key.startsWith('router-') ||
            key.startsWith('cache-') ||
            key.includes('route') ||
            key.includes('navigation') ||
            key.startsWith('vite-') ||
            key.includes('chunk-')
          )) {
            keysToRemove.push(key);
          }
        }
        keysToRemove.forEach(key => localStorage.removeItem(key));
        console.log('✅ localStorage 已清理', keysToRemove.length, '个项目');
      }
      
      // 2. 清理sessionStorage
      if (window.sessionStorage) {
        const sessionKeysToRemove = [];
        for (let i = 0; i < sessionStorage.length; i++) {
          const key = sessionStorage.key(i);
          if (key && (
            key.startsWith('vue-') || 
            key.startsWith('router-') ||
            key.startsWith('cache-') ||
            key.includes('route') ||
            key.includes('navigation') ||
            key.includes('scroll')
          )) {
            sessionKeysToRemove.push(key);
          }
        }
        sessionKeysToRemove.forEach(key => sessionStorage.removeItem(key));
        console.log('✅ sessionStorage 已清理', sessionKeysToRemove.length, '个项目');
      }
      
      // 3. 清理可能的 IndexedDB 缓存
      if ('indexedDB' in window) {
        try {
          const databasesToDelete = [
            'vue-router-cache',
            'navigation-cache',
            'component-cache',
            'vite-cache'
          ];
          
          databasesToDelete.forEach(dbName => {
            try {
              indexedDB.deleteDatabase(dbName);
            } catch (e) {
              console.warn(`删除数据库 ${dbName} 失败:`, e);
            }
          });
          
          console.log('✅ IndexedDB 缓存已清理');
        } catch (e) {
          console.warn('⚠️ IndexedDB 清理失败:', e);
        }
      }

      // 4. 清理 Service Worker 缓存
      if ('serviceWorker' in navigator && 'caches' in window) {
        caches.keys().then(cacheNames => {
          cacheNames.forEach(cacheName => {
            if (cacheName.includes('router') || cacheName.includes('navigation')) {
              caches.delete(cacheName);
            }
          });
        }).catch(e => {
          console.warn('⚠️ Service Worker 缓存清理失败:', e);
        });
      }

      // 5. 清理 Vue 组件缓存
      if (window.Vue) {
        try {
          // 清理可能的 Vue 缓存
          if (window.Vue.__v_cache) {
            window.Vue.__v_cache.clear();
          }
          
          // 清理 Vue Router 缓存
          if (window.Vue.$router && window.Vue.$router.options.scrollBehavior) {
            // 重置滚动行为
          }
        } catch (e) {
          console.warn('⚠️ Vue 缓存清理失败:', e);
        }
      }

      // 6. 清理可能阻塞导航的定时器
      try {
        // 清理可能的定时器（小心不要影响系统定时器）
        for (let i = 1; i < 1000; i++) {
          clearTimeout(i);
          clearInterval(i);
        }
      } catch (e) {
        console.warn('⚠️ 定时器清理失败:', e);
      }

      // 7. 重置滚动位置
      try {
        window.scrollTo(0, 0);
        document.body.scrollTop = 0;
        document.documentElement.scrollTop = 0;
      } catch (e) {
        console.warn('⚠️ 滚动重置失败:', e);
      }
      
      console.log('🎉 增强版缓存清理完成');
      
    } catch (error) {
      console.error('❌ 缓存清理失败:', error);
    }
  }

  // 强制清除路由相关的所有缓存
  function forceNavigationReset() {
    try {
      // 清除所有可能影响导航的缓存
      clearAllCache();
      
      // 重置 History API 状态
      if (window.history.replaceState) {
        try {
          const currentUrl = window.location.href;
          window.history.replaceState({}, '', currentUrl);
        } catch (e) {
          console.warn('⚠️ History API 重置失败:', e);
        }
      }
      
      // 确保 DOM 完全加载
      if (document.readyState !== 'complete') {
        document.addEventListener('DOMContentLoaded', clearAllCache);
      }
      
      console.log('🔄 导航重置完成');
      
    } catch (error) {
      console.error('❌ 导航重置失败:', error);
    }
  }

  // 监听页面加载
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', forceNavigationReset);
  } else {
    forceNavigationReset();
  }
  
  // 监听页面刷新
  window.addEventListener('beforeunload', function() {
    console.log('🔄 页面刷新，清理缓存...');
    clearAllCache();
  });
  
  // 监听历史记录变化
  window.addEventListener('popstate', function() {
    console.log('🔙 历史记录变化，清理缓存...');
    clearAllCache();
  });

  // 监听页面可见性变化
  document.addEventListener('visibilitychange', function() {
    if (document.visibilityState === 'visible') {
      console.log('👁️ 页面重新可见，清理缓存...');
      setTimeout(clearAllCache, 100);
    }
  });

  // 监听焦点变化
  window.addEventListener('focus', function() {
    console.log('🎯 窗口获得焦点，清理缓存...');
    setTimeout(clearAllCache, 100);
  });

  // 拦截可能导致问题的链接点击
  document.addEventListener('click', function(event) {
    const target = event.target;
    const link = target.closest('a');
    
    if (link && link.href) {
      try {
        const url = new URL(link.href);
        const currentOrigin = window.location.origin;
        
        // 如果是站内链接
        if (url.origin === currentOrigin) {
          // 在导航前清理缓存
          clearAllCache();
          
          // 如果是普通链接（非 RouterLink），转换为编程式导航
          if (!link.classList.contains('router-link') && 
              !link.hasAttribute('router-link') &&
              !link.closest('[router-link]')) {
            
            console.log('🔗 拦截普通链接，准备转换为路由导航:', link.href);
            
            // 延迟一下让 Vue Router 处理
            setTimeout(() => {
              if (window.Vue && window.Vue.$router) {
                event.preventDefault();
                window.Vue.$router.push(url.pathname + url.search + url.hash);
              }
            }, 10);
          }
        }
      } catch (e) {
        console.warn('⚠️ 链接处理失败:', e);
      }
    }
  }, true);
  
  // 提供全局方法
  window.clearAppCache = clearAllCache;
  window.forceNavigationReset = forceNavigationReset;
  
  // 定期清理缓存（每5分钟）
  setInterval(function() {
    console.log('⏰ 定期清理缓存...');
    clearAllCache();
  }, 5 * 60 * 1000);
  
  console.log('🚀 增强版缓存清理脚本已激活');
  
})(); 